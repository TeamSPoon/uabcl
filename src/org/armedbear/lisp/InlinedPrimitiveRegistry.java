package org.armedbear.lisp;

import static org.armedbear.lisp.Lisp.NIL;
import static org.armedbear.lisp.Lisp.PACKAGE_SYS;
import static org.armedbear.lisp.Lisp.T;
import static org.armedbear.lisp.Lisp.error;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InlinedPrimitiveRegistry {
  final static HashSet<Class> staticsDone = new HashSet<Class>();
  final static HashSet<Class> staticsToDo = new HashSet<Class>();
  final static HashSet<Symbol> neverInlineSymbol = new HashSet<Symbol>();
  final static HashSet<Symbol> alwaysInlineSymbol = new HashSet<Symbol>();
  static int inlinedCount = 0;
  static int missingCount = 0;
  static int wishfullCount = 0;
  static boolean showInlineDebug = false;

  public static LispObject wrongNumberOfArguments() throws InlineThrows {
    return error(new WrongNumberOfArgumentsException(InlinedPrimitiveRegistry.getCurrentOperator()));
  }

  public class InlineThrows extends RuntimeException {
  }

  public @interface WrongNumberOfArguments {
  }
  
  // ### call-count-arities
  public static final Primitive CALL_COUNT_ARITIES =
      new Primitive("call-count-arities", Lisp.PACKAGE_PROF, true)
  {
      @Override
      public LispObject execute(LispObject obj)
      {
        return obj.getCallCounts();
      }
  };
  
  private static final Primitive INLINED_PRIMITIVE_METHOD = new Primitive("INLINED-PRIMITIVE-METHOD", PACKAGE_SYS) {
    public final LispObject execute(LispObject primitive, LispObject nargs) {
      final int i = nargs.intValue();
      final Symbol sym = extractSymbol(primitive);
      final Method m = getMethodForSymbol(sym, i);
      if (m == null)
        return NIL;
      if (m.getParameterTypes().length != i) {
        error(new WrongNumberOfArgumentsException((Operator) primitive));
        return NIL;
      }
      inlinedCount++;
      return new JavaObject(m);

    }
  };

  protected static Symbol extractSymbol(LispObject primitive) {
    if (primitive instanceof Symbol) {
      return (Symbol) primitive;
    } else {
      return (Symbol) ((Operator) primitive).getLambdaName();
    }
  }

  private static final Primitive INLINED_PRIMITIVE_P = new Primitive("INLINED-PRIMITIVE-P", PACKAGE_SYS) {

    public LispObject execute(LispObject primitive, LispObject nargs) {
      final int i = nargs.intValue();
      final Symbol sym = extractSymbol(primitive);
      if (neverInline(sym))
        return NIL;
      final Method m = getMethodForSymbol(sym, i);
      if (m == null)
        return NIL;
      if (m.getParameterTypes().length != i) {
        wishfullCount++;
        System.err.println(";; Wishfull " + sym.getName() + "/" + i);
        return NIL;
      }
      return T;
    }
  };

  //
  // private static final Primitive INLINED_PRIMITIVE_METHOD =
  // InlinedPrimitive.get(InlinedPrimitive.class, "inlinedPrimitiveMethod",
  // "INLINED-PRIMITIVE-METHOD", PACKAGE_SYS);
  //
  // public static final LispObject inlinedPrimitiveP(LispObject obj) {
  // return (obj instanceof InlinedPrimitive) ? T : NIL;
  // }

  final static String MISSING = "#:MISSING";

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface InlinableMethod {
    public String name() default MISSING;

    public String pkg() default MISSING;

    public String parms() default MISSING;
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target( { ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
  public @interface NoInline {
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target( { ElementType.FIELD })
  public @interface InlineSymbol {
  }

  // / This is a registry of static methods that may be used by the compiler
  static Set<MethodDescr> inlineDecls = new HashSet<MethodDescr>();
  final static HashMap<String, Object> inlinedMethods = new HashMap<String, Object>();
  final static Object NOSUCHMETHOD = new Object();

  public static Method getMethodForSymbol(Symbol name, int arity) {
    createRegistry();
    String symname = name.getName();
    LispObject pname = name.getPackage();
    String key = null;
    if (pname instanceof Package) {
      String pkname = ((Package) pname).getName();
      key = symname + ":" + arity + "-" + pkname;
      Object m = inlinedMethods.get(key);
      if (m != null) {
        if (NOSUCHMETHOD == m || m instanceof Integer)
          return null;
        return (Method) m;
      }
    }
    MethodDescr inexact = null;
    for (MethodDescr desc : inlineDecls) {
      if (desc.matches(name, arity)) {
        if (desc.allowsRest || desc.optionsArgs > 0) {
          inexact = desc;
          continue;
        }
        System.out.println("Inline: " + desc);
        inlineReport();
        if (key != null) {
          inlinedMethods.put(key, desc.method);
        }
        return desc.method;
      }
    }
    if (inexact == null) {
      if (key != null) {
        missingCount++;
        // System.out.println(";;;;; Cannot inline = " + key);
        inlinedMethods.put(key, NOSUCHMETHOD);
      }
      return null;
    }
    // right now only inline exact matches
    if (key != null) {
      inlinedMethods.put(key, NOSUCHMETHOD);
    }
    if (true)
      return null;
    return inexact == null ? null : inexact.method;
  }

  protected static boolean neverInline(Symbol sym) {
    // just test code right now .. nothing solid
    if (sym == Symbol.APPLY)
      return true;
    return neverInlineSymbol.contains(sym);
  }

  public static class MethodDescr {

    public boolean equals(Object obj) {
      if (!(obj instanceof MethodDescr))
        return false;
      MethodDescr md = (MethodDescr) obj;
      if (md.method != method)
        return false;
      if (md.requiredArgs != requiredArgs)
        return false;
      if (md.allowsRest != allowsRest)
        return false;

      if (md.symbol != symbol)
        return false;

      if (name == MISSING)
        return false;

      if (!md.name.equals(name))
        return false;

      return true;
    }

    public boolean matches(Symbol sym) {
      if (symbol != null)
        return sym == symbol;

      String named = sym.getName();
      if (!named.equals(name))
        return false;

      if (pkg != MISSING) {
        LispObject pk = sym.getPackage();
        if (pk == NIL)
          return false;
        if (!((Package) pk).getName().equals(pkg))
          return false;
      }
      return true;
    }

    public boolean matches(Symbol name2, int arity) {
      if (!matches(name2))
        return false;

      int overflow = arity - requiredArgs;
      // underflow
      if (overflow < 0)
        return false;
      // good enough
      if (overflow == 0) {
        return true;
      }
      // consider optional on an overflow
      overflow = overflow - optionsArgs;
      if (overflow <= 0) {
        return true;
      }
      // overflow ok?
      return allowsRest;
    }

    public String toString() {
      return symbol.getName() + "/" + requiredArgs + (allowsRest ? "&rest" : "") + " " + method;
    }

    Method method;
    Symbol symbol;
    String name = MISSING;
    String pkg = MISSING;
    String parms = MISSING;
    int requiredArgs = 0;
    int optionsArgs = 0;
    boolean allowsRest = false;
  }

  private static Operator getOperator(Method method) {
    String mname = method.getName();
    if (mname.endsWith("_execute")) {
      mname = mname.substring(0, mname.length() - 8);
    }
    try {
      Field f = method.getDeclaringClass().getDeclaredField(mname);
      if (!f.isAccessible())
        f.setAccessible(true);
      return (Operator) f.get(null);
    } catch (Throwable e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    return null;
  }

  private static String guessName(Method method) {
    String mname = method.getName();
    if (mname.endsWith("_execute")) {
      mname = mname.substring(0, mname.length() - 8);
    }
    try {
      Operator op = getOperator(method);
      return op.getLambdaName().STRING().getStringValue();
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return mname;
  }

  private static Symbol extractSymbol(Method method) {
    String mname = method.getName();
    if (mname.endsWith("_execute")) {
      mname = mname.substring(0, mname.length() - 8);
    }
    Class dc = method.getDeclaringClass();
    for (Field f : dc.getDeclaredFields()) {
      if (Operator.class.isAssignableFrom(f.getType())) {
        String fname = f.getName();
        if (fname.equals(mname)) {
          try {
            if (!f.isAccessible()) {
              f.setAccessible(true);
            }
            Operator op = (Operator) f.get(null);
            return (Symbol) op.getLambdaName();
          } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    }
    return null;
  }

  public static void inlineStatics(Class tramp) {
    if (tramp == null)
      return;
    if (staticsDone.contains(tramp))
      return;
    staticsToDo.add(tramp);
    registryUpToDate = false;
  }

  static boolean registryUpToDate = false;

  public static void createRegistry() {
    if (registryUpToDate)
      return;
    registryUpToDate = true;
    for (Class c : staticsToDo.toArray(new Class[staticsToDo.size()])) {
      inlineStaticsNow(c);
    }
    inlineReport();
  }

  private static void inlineReport() {
    System.err.println(";; INLINE REPORT total=" + inlineDecls.size() + " cannot=" + missingCount + " soon="
        + wishfullCount + " inlined-calls=" + inlinedCount);

  }

  public synchronized static void inlineStaticsNow(Class tramp) {
    inlineStaticsNow(tramp, true);
  }

  public static void inlineStaticsNow(Class tramp, boolean doStatics) {
    if (tramp == null)
      return;

    staticsToDo.remove(tramp);
    boolean needsDoing = staticsDone.add(tramp);
    // if (!needsDoing) return;

    for (Field f : tramp.getDeclaredFields()) {
      if (!Operator.class.isAssignableFrom(f.getType()))
        continue;
      if (!f.isAccessible())
        f.setAccessible(true);
      try {
        Operator p = (Operator) f.get(null);
        Symbol sym = (Symbol) p.getLambdaName();
        if (f.getAnnotation(NoInline.class) != null) {
          neverInlineSymbol.add(sym);
          debug("Never inlining " + sym.writeToString());
        }
        if (f.getAnnotation(InlineSymbol.class) != null) {
          alwaysInlineSymbol.add(sym);
          debug("Always inlining " + sym.writeToString());
        }
      } catch (Throwable e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    Class inlineMethodClass = InlinableMethod.class;

    for (Method method : tramp.getDeclaredMethods()) {

      if (method.getAnnotation(NoInline.class) != null)
        continue;

      final MethodDescr md;

      InlinableMethod annotation = (InlinableMethod) method.getAnnotation(inlineMethodClass);
      final int mods = method.getModifiers();

      Symbol sym;
      if (annotation != null) {
        md = new MethodDescr();
        md.name = annotation.name();
        if (md.name == MISSING)
          md.name = guessName(method);
        md.pkg = annotation.pkg();
        md.parms = annotation.parms();
        sym = extractSymbol(method);
      } else {
        if (Modifier.isPrivate(mods) || !Modifier.isStatic(mods))
          continue;
        if (method.getReturnType() != LispObject.class)
          continue;
        Operator op = getOperator(method);
        sym = (Symbol) op.getLambdaName();
        boolean mustInline = alwaysInlineSymbol.contains(sym);
        if (!mustInline && !doStatics)
          continue;
        md = new MethodDescr();
        String guessName = guessName(method);
        md.name = guessName;
      }

      md.method = method;
      md.symbol = sym;
      if (md.symbol == null) {
        return;
      }
      md.pkg = ((Package) md.symbol.getPackage()).getName();
      boolean skip = false;
      Class[] argTypes = method.getParameterTypes();
      for (Class class1 : argTypes) {
        if (class1 == LispObject.class) {
          md.requiredArgs++;
        } else if (class1 == LispObject[].class) {
          md.allowsRest = true;
        } else {
          skip = true;
          break;
        }
      }
      if (skip)
        continue;
      if (!inlineDecls.contains(md)) {
        if (!method.isAccessible())
          method.setAccessible(true);
        inlineDecls.add(md);
        // System.out.println(";; INLINABLE: " + md);
      }
    }
  }

  private static void debug(String string) {
    if (showInlineDebug)
      System.err.println(";; INLINE DEBUG: " + string);
  }

  public static Operator getCurrentOperator() {
    // just a workarr9ound for testing
    return INLINED_PRIMITIVE_P;
  }

  public static void registerSubClass(Operator operator) {
    inlineStatics(operator.getClass());
    inlineStatics(operator.getClass().getDeclaringClass());
  }

}
