/*
 * Lisp.java
 *
 * Copyright (C) 2002-2007 Peter Graves <peter@armedbear.org>
 * $Id: Lisp.java 12063 2009-07-26 20:33:16Z ehuelsmann $
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */

package org.armedbear.lisp;

import static org.armedbear.lisp.Lisp.checkSymbol;
import static org.armedbear.lisp.Lisp.error;
import static org.armedbear.lisp.Nil.NIL;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class Lisp
{	
  public static final boolean debug = true;

  public static boolean cold = true;

  public static boolean initialized;

  // Packages.
  public static final Package PACKAGE_CL =
    Packages.createPackage("COMMON-LISP", 1024);
  public static final Package PACKAGE_CL_USER =
    Packages.createPackage("COMMON-LISP-USER", 1024);
  public static final Package PACKAGE_KEYWORD =
    Packages.createPackage("KEYWORD", 1024);
  public static final Package PACKAGE_SYS =
    Packages.createPackage("SYSTEM");
  public static final Package PACKAGE_MOP =
    Packages.createPackage("MOP");
  public static final Package PACKAGE_TPL =
    Packages.createPackage("TOP-LEVEL");
  public static final Package PACKAGE_EXT =
    Packages.createPackage("EXTENSIONS");
  public static final Package PACKAGE_JVM =
    Packages.createPackage("JVM");
  public static final Package PACKAGE_LOOP =
    Packages.createPackage("LOOP");
  public static final Package PACKAGE_PROF =
    Packages.createPackage("PROFILER");
  public static final Package PACKAGE_JAVA =
    Packages.createPackage("JAVA");
  public static final Package PACKAGE_LISP =
    Packages.createPackage("LISP");
  public static final Package PACKAGE_THREADS =
    Packages.createPackage("THREADS");


  public static final Symbol addFunction(String name, LispObject obj)
  {
    try
      {
        Symbol symbol = PACKAGE_CL.internAndExport(name);
        symbol.function = obj;
        return symbol;
      }
    catch (ConditionThrowable t)
      {
        Debug.trace(t); // Shouldn't happen.
        return null;
      }
    catch (RuntimeException t)
    {
      Debug.trace(t); // Shouldn't happen.
      return null;
    }
  }
  
  // ### nil
  public static final LispObject NIL = Nil.NIL;

  // We need NIL before we can call usePackage().
  static
  {
    try
      {
        PACKAGE_CL.addNickname("CL");
        PACKAGE_CL_USER.addNickname("CL-USER");
        PACKAGE_CL_USER.usePackage(PACKAGE_CL);
        PACKAGE_CL_USER.usePackage(PACKAGE_EXT);
        PACKAGE_CL_USER.usePackage(PACKAGE_JAVA);
        PACKAGE_SYS.addNickname("SYS");
        PACKAGE_SYS.usePackage(PACKAGE_CL);
        PACKAGE_SYS.usePackage(PACKAGE_EXT);
        PACKAGE_MOP.usePackage(PACKAGE_CL);
        PACKAGE_MOP.usePackage(PACKAGE_EXT);
        PACKAGE_MOP.usePackage(PACKAGE_SYS);
        PACKAGE_TPL.addNickname("TPL");
        PACKAGE_TPL.usePackage(PACKAGE_CL);
        PACKAGE_TPL.usePackage(PACKAGE_EXT);
        PACKAGE_EXT.addNickname("EXT");
        PACKAGE_EXT.usePackage(PACKAGE_CL);
        PACKAGE_EXT.usePackage(PACKAGE_THREADS);
        PACKAGE_JVM.usePackage(PACKAGE_CL);
        PACKAGE_JVM.usePackage(PACKAGE_EXT);
        PACKAGE_JVM.usePackage(PACKAGE_SYS);
        PACKAGE_LOOP.usePackage(PACKAGE_CL);
        PACKAGE_PROF.addNickname("PROF");
        PACKAGE_PROF.usePackage(PACKAGE_CL);
        PACKAGE_PROF.usePackage(PACKAGE_EXT);
        PACKAGE_JAVA.usePackage(PACKAGE_CL);
        PACKAGE_JAVA.usePackage(PACKAGE_EXT);
        PACKAGE_LISP.usePackage(PACKAGE_CL);
        PACKAGE_LISP.usePackage(PACKAGE_EXT);
        PACKAGE_LISP.usePackage(PACKAGE_SYS);
  PACKAGE_THREADS.usePackage(PACKAGE_CL);
      }
    catch (Throwable t)
      {
        t.printStackTrace();
      }
  }

  // End-of-file marker.
  public static final LispObject EOF = new SingletonLispObject() {
    public String writeToString() {
      return "end-of-file marker";
    }
  };

  public static boolean profiling;

  public static boolean sampling;

  public static volatile boolean sampleNow;

  // args must not be null!
  public static final LispObject funcall(LispObject fun, LispObject[] args,
                                         LispThread thread)
    throws ConditionThrowable
  {
    thread._values = null;

    // 26-07-2009: For some reason we cannot "just" call the array version;
    // it causes an error (Wrong number of arguments for LOOP-FOR-IN)
    // which is probably a sign of an issue in our design?
    switch (args.length)
      {
      case 0:
        return thread.execute(fun);
      case 1:
        return thread.execute(fun, args[0]);
      case 2:
        return thread.execute(fun, args[0], args[1]);
      case 3:
        return thread.execute(fun, args[0], args[1], args[2]);
      case 4:
        return thread.execute(fun, args[0], args[1], args[2], args[3]);
      case 5:
        return thread.execute(fun, args[0], args[1], args[2], args[3],
                              args[4]);
      case 6:
        return thread.execute(fun, args[0], args[1], args[2], args[3],
                              args[4], args[5]);
      case 7:
        return thread.execute(fun, args[0], args[1], args[2], args[3],
                              args[4], args[5], args[6]);
      case 8:
        return thread.execute(fun, args[0], args[1], args[2], args[3],
                              args[4], args[5], args[6], args[7]);
      default:
        return thread.execute(fun, args);
    }
  }

  public static final LispObject macroexpand(LispObject form,
                                             final Environment env,
                                             final LispThread thread)
    throws ConditionThrowable
  {
    LispObject expanded = NIL;
    while (true)
      {
        form = macroexpand_1(form, env, thread);
        LispObject[] values = thread._values;
        if (values[1] == NIL)
          {
            values[1] = expanded;
            return form;
          }
        expanded = T;
      }
  }

  public static final LispObject macroexpand_1(final LispObject form,
                                               final Environment env,
                                               final LispThread thread)
    throws ConditionThrowable
  {
    if (form instanceof Cons)
      {
        LispObject car = ((Cons)form).car;
        if (car instanceof Symbol)
          {
            LispObject obj = env.lookupFunction(car);
            if (obj instanceof Autoload)
              {
                Autoload autoload = (Autoload) obj;
                autoload.load();
                obj = car.getSymbolFunction();
              }
            if (obj instanceof SpecialOperator)
              {
                obj = get(car, SymbolConstants.MACROEXPAND_MACRO, null);
                if (obj instanceof Autoload)
                  {
                    Autoload autoload = (Autoload) obj;
                    autoload.load();
                    obj = get(car, SymbolConstants.MACROEXPAND_MACRO, null);
                  }
              }
            if (obj instanceof MacroObject)
              {
                LispObject expander = ((MacroObject)obj).expander;
                if (profiling)
                  if (!sampling)
                    expander.incrementCallCount();
                LispObject hook =
                  coerceToFunction(SymbolConstants.MACROEXPAND_HOOK.symbolValue(thread));
                return thread.setValues(hook.execute(expander, form, env),
                                        T);
              }
          }
      }
    else if (form instanceof Symbol)
      {
        Symbol symbol = (Symbol) form;
        LispObject obj = null;
        if (symbol.isSpecialVariable())
          obj = thread.lookupSpecial(symbol);
        else
          obj = env.lookup(symbol);
        if (obj == null)
          obj = symbol.getSymbolValue();
        if (obj instanceof SymbolMacro)
          return thread.setValues(((SymbolMacro)obj).getExpansion(), T);
      }
    // Not a macro.
    return thread.setValues(form, NIL);
  }

  // ### interactive-eval
  private static final Primitive INTERACTIVE_EVAL =
    new Primitive("interactive-eval", PACKAGE_SYS, true)
    {
      @Override
      public LispObject execute(LispObject object) throws ConditionThrowable
      {
        final LispThread thread = LispThread.currentThread();
        thread.setSpecialVariable(SymbolConstants.MINUS, object);
        LispObject result;
        try
          {
            result = thread.execute(SymbolConstants.EVAL.getSymbolFunction(), object);
          }
        catch (OutOfMemoryError e)
          {
            Debug.trace(e);
            return error(new LispError("Out of memory."));
          }
        catch (StackOverflowError e)
          {
            thread.setSpecialVariable(_SAVED_BACKTRACE_,
                                      thread.backtrace(0));
            return error(new StorageCondition("Stack overflow."));
          }
        catch (Go go)
        {
          throw go;
        }
        catch (Throw t)
          {
            return error(new ControlError("Attempt to throw to the nonexistent tag " +
                    t.tag.writeToString() 
                    + " ret="+safeWriteToString(t.getResult(thread))+ "."));
          }
        catch (Return r)
        {
          Debug.trace(r);
          String str = "Attempt to return to the nonexistent tag " +
          r.tag.writeToString() //+ " block=" + safeWriteToString(r.block) 
          + " ret="+safeWriteToString(r.result)+ ".";
          Debug.trace(str);
          return error(new ControlError(str));
        }        
        catch (Throwable t)
          {
            Debug.trace(t);
            thread.setSpecialVariable(_SAVED_BACKTRACE_,
                                      thread.backtrace(0));
            return error(new LispError("Caught " + t + "."));
          }
        Debug.assertTrue(result != null);
        thread.setSpecialVariable(SymbolConstants.STAR_STAR_STAR,
                                  thread.safeSymbolValue(SymbolConstants.STAR_STAR));
        thread.setSpecialVariable(SymbolConstants.STAR_STAR,
                                  thread.safeSymbolValue(SymbolConstants.STAR));
        thread.setSpecialVariable(SymbolConstants.STAR, result);
        thread.setSpecialVariable(SymbolConstants.PLUS_PLUS_PLUS,
                                  thread.safeSymbolValue(SymbolConstants.PLUS_PLUS));
        thread.setSpecialVariable(SymbolConstants.PLUS_PLUS,
                                  thread.safeSymbolValue(SymbolConstants.PLUS));
        thread.setSpecialVariable(SymbolConstants.PLUS,
                                  thread.safeSymbolValue(SymbolConstants.MINUS));
        LispObject[] values = thread._values;
        thread.setSpecialVariable(SymbolConstants.SLASH_SLASH_SLASH,
                                  thread.safeSymbolValue(SymbolConstants.SLASH_SLASH));
        thread.setSpecialVariable(SymbolConstants.SLASH_SLASH,
                                  thread.safeSymbolValue(SymbolConstants.SLASH));
        if (values != null)
          {
            LispObject slash = NIL;
            for (int i = values.length; i-- > 0;)
              slash = makeCons(values[i], slash);
            thread.setSpecialVariable(SymbolConstants.SLASH, slash);
          }
        else
          thread.setSpecialVariable(SymbolConstants.SLASH, makeCons(result));
        return result;
      }
    };

  private static final void pushJavaStackFrames() throws ConditionThrowable
  {
      final LispThread thread = LispThread.currentThread();
      final StackTraceElement[] frames = thread.getJavaStackTrace();

      // Search for last Primitive in the StackTrace; that was the
      // last entry point from Lisp.
      int last = frames.length - 1;
      for (int i = 0; i<= last; i++) {
          if (frames[i].getClassName().startsWith("org.armedbear.lisp.Primitive"))
	    last = i;
      }
      // Do not include the first three frames:
      //   Thread.getStackTrace, LispThread.getJavaStackTrace,
      //   Lisp.pushJavaStackFrames.
      while (last > 2) {
        thread.pushStackFrame(new JavaStackFrame(frames[last]));
        last--;
      }
  }


  public static final LispObject error(LispObject condition)
    throws ConditionThrowable
  {
    pushJavaStackFrames();
    return SymbolConstants.ERROR.execute(condition);
  }

  public static final LispObject error(LispObject condition, LispObject message)
    throws ConditionThrowable
  {
    pushJavaStackFrames();
    return SymbolConstants.ERROR.execute(condition, Keyword.FORMAT_CONTROL, message);
  }

  public static final LispObject type_error(LispObject datum,
                                            LispObject expectedType)
    throws ConditionThrowable
  {
    return error(new TypeError(datum, expectedType));
  }

  public static volatile boolean interrupted;

  public static synchronized final void setInterrupted(boolean b)
  {
    interrupted = b;
  }

  public static final void handleInterrupt() throws ConditionThrowable
  {
    setInterrupted(false);
    SymbolConstants.BREAK.getSymbolFunction().execute();
    setInterrupted(false);
  }

  // Used by the compiler.
  public static final LispObject loadTimeValue(LispObject obj)
    throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    if (SymbolConstants.LOAD_TRUENAME.symbolValue(thread) != NIL)
      return eval(obj, new Environment(), thread);
    else
      return NIL;
  }

  public static final LispObject eval(LispObject obj)
    throws ConditionThrowable
  {
    return eval(obj, new Environment(), LispThread.currentThread());
  }

  public static final LispObject eval(final LispObject obj,
                                      final Environment env,
                                      final LispThread thread)
    throws ConditionThrowable
  {
    thread._values = null;
    if (interrupted)
      handleInterrupt();
    if (thread.isDestroyed())
      throw new ThreadDestroyed();
    if (obj instanceof Symbol)
      {
        LispObject result;
        if (obj.isSpecialVariable())
          {
            if (obj.constantp())
              return obj.getSymbolValue();
            else
              result = thread.lookupSpecial(obj);
          }
        else if (env.isDeclaredSpecial(obj))
          result = thread.lookupSpecial(obj);
        else
          result = env.lookup(obj);
        if (result == null)
          {
            result = obj.getSymbolValue();
            if (result == null)
              return error(new UnboundVariable(obj));
          }
        if (result instanceof SymbolMacro)
          return eval(((SymbolMacro)result).getExpansion(), env, thread);
        return result;
      }
    else if (obj instanceof Cons)
      {
        LispObject first = ((Cons)obj).car;
        if (first instanceof Symbol)
          {
            LispObject fun = env.lookupFunction(first);
            if (fun instanceof SpecialOperator)
              {
                if (profiling)
                  if (!sampling)
                    fun.incrementCallCount();
                // Don't eval args!
                return fun.execute(((Cons)obj).cdr, env);
              }
            if (fun instanceof MacroObject)
              return eval(macroexpand(obj, env, thread), env, thread);
            if (fun instanceof Autoload)
              {
                Autoload autoload = (Autoload) fun;
                autoload.load();
                return eval(obj, env, thread);
              }
            return evalCall(fun != null ? fun : first,
                            ((Cons)obj).cdr, env, thread);
          }
        else
          {
            if (first.CAR() == SymbolConstants.LAMBDA)
              {
                Closure closure = new Closure(first, env);
                return evalCall(closure, ((Cons)obj).cdr, env, thread);
              }
            else
              return error(new ProgramError("Illegal function object: " +
                                             first.writeToString()));
          }
      }
    else
      return obj;
  }

  public static final int CALL_REGISTERS_MAX = 8;

  // Also used in JProxy.java.
  protected static final LispObject evalCall(LispObject function,
                                             LispObject args,
                                             Environment env,
                                             LispThread thread)
    throws ConditionThrowable
  {
    if (args == NIL)
      return thread.execute(function);
    LispObject first = eval(args.CAR(), env, thread);
    args = ((Cons)args).cdr;
    if (args == NIL)
      {
        thread._values = null;
        return thread.execute(function, first);
      }
    LispObject second = eval(args.CAR(), env, thread);
    args = ((Cons)args).cdr;
    if (args == NIL)
      {
        thread._values = null;
        return thread.execute(function, first, second);
      }
    LispObject third = eval(args.CAR(), env, thread);
    args = ((Cons)args).cdr;
    if (args == NIL)
      {
        thread._values = null;
        return thread.execute(function, first, second, third);
      }
    LispObject fourth = eval(args.CAR(), env, thread);
    args = ((Cons)args).cdr;
    if (args == NIL)
      {
        thread._values = null;
        return thread.execute(function, first, second, third, fourth);
      }
    LispObject fifth = eval(args.CAR(), env, thread);
    args = ((Cons)args).cdr;
    if (args == NIL)
      {
        thread._values = null;
        return thread.execute(function, first, second, third, fourth, fifth);
      }
    LispObject sixth = eval(args.CAR(), env, thread);
    args = ((Cons)args).cdr;
    if (args == NIL)
      {
        thread._values = null;
        return thread.execute(function, first, second, third, fourth, fifth,
                              sixth);
      }
    LispObject seventh = eval(args.CAR(), env, thread);
    args = ((Cons)args).cdr;
    if (args == NIL)
      {
        thread._values = null;
        return thread.execute(function, first, second, third, fourth, fifth,
                              sixth, seventh);
      }
    LispObject eighth = eval(args.CAR(), env, thread);
    args = ((Cons)args).cdr;
    if (args == NIL)
      {
        thread._values = null;
        return thread.execute(function, first, second, third, fourth, fifth,
                              sixth, seventh, eighth);
      }
    // More than CALL_REGISTERS_MAX arguments.
    final int length = args.size() + CALL_REGISTERS_MAX;
    LispObject[] array = new LispObject[length];
    array[0] = first;
    array[1] = second;
    array[2] = third;
    array[3] = fourth;
    array[4] = fifth;
    array[5] = sixth;
    array[6] = seventh;
    array[7] = eighth;
    for (int i = CALL_REGISTERS_MAX; i < length; i++)
      {
        array[i] = eval(args.CAR(), env, thread);
        args = args.CDR();
      }
    thread._values = null;
    return thread.execute(function, array);
  }

  public static final LispObject parseBody(LispObject body,
                                           boolean documentationAllowed)
    throws ConditionThrowable
  {
      LispObject decls = NIL;
      LispObject doc = NIL;

      while (body != NIL) {
        LispObject form = body.CAR();
        if (documentationAllowed && form instanceof AbstractString
            && body.CDR() != NIL) {
          doc = body.CAR();
          documentationAllowed = false;
        } else if (form instanceof Cons && form.CAR() == SymbolConstants.DECLARE)
          decls = makeCons(form, decls);
        else
          break;

        body = body.CDR();
      }
      return list(body, decls.nreverse(), doc);
  }

  public static final LispObject parseSpecials(LispObject forms)
    throws ConditionThrowable
  {
    LispObject specials = NIL;
    while (forms != NIL) {
      LispObject decls = forms.CAR();

      Debug.assertTrue(decls instanceof Cons);
      Debug.assertTrue(decls.CAR() == SymbolConstants.DECLARE);
      decls = decls.CDR();
      while (decls != NIL) {
        LispObject decl = decls.CAR();

        if (decl instanceof Cons && decl.CAR() == SymbolConstants.SPECIAL) {
            decl = decl.CDR();
            while (decl != NIL) {
              specials = makeCons(checkSymbol(decl.CAR()), specials);
              decl = decl.CDR();
            }
        }

        decls = decls.CDR();
      }

      forms = forms.CDR();
    }

    return specials;
  }

  public static final LispObject progn(LispObject body, Environment env,
                                       LispThread thread)
    throws ConditionThrowable
  {
    LispObject result = NIL;
    while (body != NIL)
      {
        result = eval(body.CAR(), env, thread);
        body = ((Cons)body).cdr;
      }
    return result;
  }

  // Environment wrappers.
  private static final boolean isSpecial(Symbol sym, LispObject ownSpecials,
                                         Environment env)
    throws ConditionThrowable
  {
    if (ownSpecials != null)
      {
        if (sym.isSpecialVariable())
          return true;
        for (; ownSpecials != NIL; ownSpecials = ownSpecials.CDR())
          {
            if (sym == ownSpecials.CAR())
              return true;
          }
      }
    return false;
  }

  protected static final void bindArg(LispObject ownSpecials,
                                      Symbol sym, LispObject value,
                                      Environment env, LispThread thread)
    throws ConditionThrowable
  {
    if (isSpecial(sym, ownSpecials, env)) {
      env.declareSpecial(sym);
      thread.bindSpecial(sym, value);
    }
    else
      env.bindLispSymbol(sym, value);
  }
  
    public static Cons makeCons(LispObject a, LispObject d) {
		// TODO Auto-generated method stub
		return new Cons(a,d);
	}
	public static Cons makeCons(String a, LispObject d) {
		// TODO Auto-generated method stub
		return new Cons(a,d);
	}
	public static Cons makeCons(LispObject a) {
		// TODO Auto-generated method stub
		return new Cons(a);
	}

  public static final Cons list(LispObject obj1, LispObject... remaining)
  {
    Cons theList = null;
    if (remaining.length > 0) {
      theList = makeCons(remaining[remaining.length-1]);
      for (int i = remaining.length - 2; i >= 0; i--)
        theList = makeCons(remaining[i], theList);
    }
    return (theList == null) ? makeCons(obj1) : makeCons(obj1, theList);
  }

  @Deprecated
  public static final Cons list1(LispObject obj1)
  {
    return makeCons(obj1);
  }

  @Deprecated
  public static final Cons list2(LispObject obj1, LispObject obj2)
  {
    return makeCons(obj1, makeCons(obj2));
  }

  @Deprecated
  public static final Cons list3(LispObject obj1, LispObject obj2,
                                 LispObject obj3)
  {
    return makeCons(obj1, makeCons(obj2, makeCons(obj3)));
  }

  @Deprecated
  public static final Cons list4(LispObject obj1, LispObject obj2,
                                 LispObject obj3, LispObject obj4)
  {
    return makeCons(obj1,
                    makeCons(obj2,
                             makeCons(obj3,
                                      makeCons(obj4))));
  }

  @Deprecated
  public static final Cons list5(LispObject obj1, LispObject obj2,
                                 LispObject obj3, LispObject obj4,
                                 LispObject obj5)
  {
    return makeCons(obj1,
                    makeCons(obj2,
                             makeCons(obj3,
                                      makeCons(obj4,
                                               makeCons(obj5)))));
  }

  @Deprecated
  public static final Cons list6(LispObject obj1, LispObject obj2,
                                 LispObject obj3, LispObject obj4,
                                 LispObject obj5, LispObject obj6)
  {
    return makeCons(obj1,
                    makeCons(obj2,
                             makeCons(obj3,
                                      makeCons(obj4,
                                               makeCons(obj5,
                                                        makeCons(obj6))))));
  }

  @Deprecated
  public static final Cons list7(LispObject obj1, LispObject obj2,
                                 LispObject obj3, LispObject obj4,
                                 LispObject obj5, LispObject obj6,
                                 LispObject obj7)
  {
    return makeCons(obj1,
                    makeCons(obj2,
                             makeCons(obj3,
                                      makeCons(obj4,
                                               makeCons(obj5,
                                                        makeCons(obj6,
                                                                 makeCons(obj7)))))));
  }

  @Deprecated
  public static final Cons list8(LispObject obj1, LispObject obj2,
                                 LispObject obj3, LispObject obj4,
                                 LispObject obj5, LispObject obj6,
                                 LispObject obj7, LispObject obj8)
  {
    return makeCons(obj1,
                    makeCons(obj2,
                             makeCons(obj3,
                                      makeCons(obj4,
                                               makeCons(obj5,
                                                        makeCons(obj6,
                                                                 makeCons(obj7,
                                                                          makeCons(obj8))))))));
  }

  @Deprecated
  public static final Cons list9(LispObject obj1, LispObject obj2,
                                 LispObject obj3, LispObject obj4,
                                 LispObject obj5, LispObject obj6,
                                 LispObject obj7, LispObject obj8,
                                 LispObject obj9)
  {
    return makeCons(obj1,
                    makeCons(obj2,
                             makeCons(obj3,
                                      makeCons(obj4,
                                               makeCons(obj5,
                                                        makeCons(obj6,
                                                                 makeCons(obj7,
                                                                          makeCons(obj8,
                                                                                   makeCons(obj9)))))))));
  }

  // Used by the compiler.
  public static final LispObject multipleValueList(LispObject result)
    throws ConditionThrowable
  {
    LispThread thread = LispThread.currentThread();
    LispObject[] values = thread._values;
    if (values == null)
      return makeCons(result);
    thread._values = null;
    LispObject list = NIL;
    for (int i = values.length; i-- > 0;)
      list = makeCons(values[i], list);
    return list;
  }

  // Used by the compiler for MULTIPLE-VALUE-CALLs with a single values form.
  public static final LispObject multipleValueCall1(LispObject result,
                                                    LispObject function,
                                                    LispThread thread)
    throws ConditionThrowable
  {
    LispObject[] values = thread._values;
    thread._values = null;
    if (values == null)
      return thread.execute(coerceToFunction(function), result);
    else
      return funcall(coerceToFunction(function), values, thread);
  }

  public static final void progvBindVars(LispObject symbols,
                                         LispObject values,
                                         LispThread thread)
    throws ConditionThrowable
  {
    for (LispObject list = symbols; list != NIL; list = list.CDR())
      {
        Symbol symbol = checkSymbol(list.CAR());
        LispObject value;
        if (values != NIL)
          {
            value = values.CAR();
            values = values.CDR();
          }
        else
          {
            // "If too few values are supplied, the remaining symbols are
            // bound and then made to have no value."
            value = null;
          }
        thread.bindSpecial(symbol, value);
      }
  }

  public static Symbol checkSymbol(LispObject obj) throws ConditionThrowable
  {             
          if (obj instanceof Symbol)      
                  return (Symbol) obj;         
          return (Symbol)// Not reached.       
              type_error(obj, SymbolConstants.SYMBOL);
  }

   public static final LispInteger checkInt(LispObject obj)
   throws ConditionThrowable {
    if (obj instanceof LispInteger)
      return (LispInteger)obj;
    return (LispInteger)type_error(obj, SymbolConstants.INTEGER);
  }
   
   public static final Fixnum checkFixnum(LispObject obj)
   throws ConditionThrowable {
    if (obj instanceof LispInteger)
    return (Fixnum)obj;
    return (Fixnum)type_error(obj, SymbolConstants.FIXNUM);
  }
   
  public static final LispObject checkList(LispObject obj)
    throws ConditionThrowable
  {
    if (obj.isList())
      return obj;
    return type_error(obj, SymbolConstants.LIST);
  }

  public static final AbstractArray checkArray(LispObject obj)
    throws ConditionThrowable
  {
          if (obj instanceof AbstractArray)       
                  return (AbstractArray) obj;         
          return (AbstractArray)// Not reached.       
        type_error(obj, SymbolConstants.ARRAY);
  }

  public static final AbstractVector checkVector(LispObject obj)
    throws ConditionThrowable
  {
          if (obj instanceof AbstractVector)      
                  return (AbstractVector) obj;         
          return (AbstractVector)// Not reached.       
        type_error(obj, SymbolConstants.VECTOR);
  }

  public static final DoubleFloat checkDoubleFloat(LispObject obj)
    throws ConditionThrowable
  {
          if (obj .isDoubleFloat())
                  return (DoubleFloat) obj;
          return (DoubleFloat)// Not reached.
            type_error(obj, SymbolConstants.DOUBLE_FLOAT);
  }

  public static final SingleFloat checkSingleFloat(LispObject obj)
    throws ConditionThrowable
  {
          if (obj .isSingleFloat())
                  return (SingleFloat) obj;
          return (SingleFloat)// Not reached.
            type_error(obj, SymbolConstants.SINGLE_FLOAT);
  }

  public static final StackFrame checkStackFrame(LispObject obj)
    throws ConditionThrowable
  {
          if (obj instanceof StackFrame)      
                  return (StackFrame) obj;         
          return (StackFrame)// Not reached.       
	    type_error(obj, SymbolConstants.STACK_FRAME);
  }

  static
  {
    // ### *gensym-counter*
    SymbolConstants.GENSYM_COUNTER.initializeSpecial(Fixnum.ZERO);
  }

  public static final Symbol gensym(LispThread thread)
    throws ConditionThrowable
  {
    return gensym("G", thread);
  }

  public static final Symbol gensym(String prefix, LispThread thread)
    throws ConditionThrowable
  {
    FastStringBuffer sb = new FastStringBuffer(prefix);
    SpecialBinding binding = thread.getSpecialBinding(SymbolConstants.GENSYM_COUNTER);
    final LispObject oldValue;
    if (binding != null) {
        oldValue = binding.value;
        if (oldValue .isFixnum()
                || oldValue .isBignum())
          binding.value = oldValue.incr();
        else {
           SymbolConstants.GENSYM_COUNTER.setSymbolValue(Fixnum.ZERO);
           error(new TypeError("The value of *GENSYM-COUNTER* was not a nonnegative integer. Old value: " +
                                oldValue.writeToString() + " New value: 0"));
        }
    } else {
        // we're manipulating a global resource
        // make sure we operate thread-safely
        synchronized (SymbolConstants.GENSYM_COUNTER) {
            oldValue = SymbolConstants.GENSYM_COUNTER.getSymbolValue();
            if (oldValue .isFixnum()
                    || oldValue .isBignum())
                SymbolConstants.GENSYM_COUNTER.setSymbolValue(oldValue.incr());
            else {
               SymbolConstants.GENSYM_COUNTER.setSymbolValue(Fixnum.ZERO);
               error(new TypeError("The value of *GENSYM-COUNTER* was not a nonnegative integer. Old value: " +
                                    oldValue.writeToString() + " New value: 0"));
            }
        }
    }
      
    // Decimal representation.
    if (oldValue .isFixnum())
      sb.append(oldValue.intValue());
    else if (oldValue .isBignum())
      sb.append(oldValue.bigIntegerValue().toString());

    return new Symbol(new SimpleString(sb));
  }

  public static final String javaString(LispObject arg)
    throws ConditionThrowable
  {
    if (arg instanceof AbstractString)
      return arg.getStringValue();
    if (arg instanceof Symbol)
      return ((Symbol)arg).getName();
    if (arg instanceof LispCharacter)
      return String.valueOf(new char[] {((LispCharacter)arg).value});
    type_error(arg, list(SymbolConstants.OR, SymbolConstants.STRING, SymbolConstants.SYMBOL,
                               SymbolConstants.CHARACTER));
    // Not reached.
    return null;
  }

  public static final LispObject number(long n)
  {
    if (n >= Integer.MIN_VALUE && n <= Integer.MAX_VALUE)
      return Fixnum.makeFixnum((int)n);
    else
      return Bignum.getInteger(n);
  }

  private static final BigInteger INT_MIN = BigInteger.valueOf(Integer.MIN_VALUE);
  private static final BigInteger INT_MAX = BigInteger.valueOf(Integer.MAX_VALUE);

  public static final LispObject number(BigInteger numerator,
                                        BigInteger denominator)
    throws ConditionThrowable
  {
    if (denominator.signum() == 0)
      error(new DivisionByZero());
    if (denominator.signum() < 0)
      {
        numerator = numerator.negate();
        denominator = denominator.negate();
      }
    BigInteger gcd = numerator.gcd(denominator);
    if (!gcd.equals(BigInteger.ONE))
      {
        numerator = numerator.divide(gcd);
        denominator = denominator.divide(gcd);
      }
    if (denominator.equals(BigInteger.ONE))
      return number(numerator);
    else
      return new Ratio(numerator, denominator);
  }

  public static final LispObject number(BigInteger n)
  {
    if (n.compareTo(INT_MIN) >= 0 && n.compareTo(INT_MAX) <= 0)
      return Fixnum.makeFixnum(n.intValue());
    else
      return Bignum.getInteger(n);
  }

  public static final int mod(int number, int divisor)
    throws ConditionThrowable
  {
    final int r;
    try
      {
        r = number % divisor;
      }
    catch (ArithmeticException e)
      {
        error(new ArithmeticError("Division by zero."));
        // Not reached.
        return 0;
      }
    if (r == 0)
      return r;
    if (divisor < 0)
      {
        if (number > 0)
          return r + divisor;
      }
    else
      {
        if (number < 0)
          return r + divisor;
      }
    return r;
  }

  // Adapted from SBCL.
  public static final int mix(long x, long y)
  {
    long xy = x * 3 + y;
    return (int) (536870911L & (441516657L ^ xy ^ (xy >> 5)));
  }

  // Used by the compiler.
  public static final LispObject readObjectFromString(String s)
  {
    try
      {
        return new StringInputStream(s).faslRead(true, NIL, false,
                                                 LispThread.currentThread());
      }
    catch (Throwable t)
      {
        return null;
      }
  }

  public static final LispObject loadCompiledFunction(final String namestring)
    throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    final boolean absolute = Utilities.isFilenameAbsolute(namestring);
    LispObject device = NIL;
    final Pathname defaultPathname;
    if (absolute)
      {
        defaultPathname =
          coerceToPathname(SymbolConstants.DEFAULT_PATHNAME_DEFAULTS.symbolValue(thread));
      }
    else
      {
        LispObject loadTruename = SymbolConstants.LOAD_TRUENAME.symbolValue(thread);
        if (loadTruename instanceof Pathname)
          {
            defaultPathname = (Pathname) loadTruename;
            // We're loading a file.
            device = ((Pathname)loadTruename).getDevice();
          }
        else
          {
            defaultPathname =
              coerceToPathname(SymbolConstants.DEFAULT_PATHNAME_DEFAULTS.symbolValue(thread));
          }
      }
    if (device instanceof Pathname)
      {
        // We're loading a fasl from j.jar.
        URL url = Lisp.class.getResource(namestring);
        if (url != null)
          {
            try
              {
                String s = url.toString();
                String zipFileName;
                String entryName;
                if (s.startsWith("jar:file:"))
                  {
                    s = s.substring(9);
                    int index = s.lastIndexOf('!');
                    if (index >= 0)
                      {
                        zipFileName = s.substring(0, index);
                        entryName = s.substring(index + 1);
                        if (entryName.length() > 0 && entryName.charAt(0) == '/')
                          entryName = entryName.substring(1);
                        if (Utilities.isPlatformWindows)
                          {
                            // "/C:/Documents%20and%20Settings/peter/Desktop/j.jar"
                            if (zipFileName.length() > 0 && zipFileName.charAt(0) == '/')
                              zipFileName = zipFileName.substring(1);
                          }
                        zipFileName = URLDecoder.decode(zipFileName, "UTF-8");
                        ZipFile zipFile = ZipCache.getZip(zipFileName);
                        try
                          {
                            ZipEntry entry = zipFile.getEntry(entryName);
                            if (entry != null)
                              {
                                long size = entry.getSize();
                                InputStream in = zipFile.getInputStream(entry);
                                LispObject obj = loadCompiledFunction(in, (int) size);
                                return obj != null ? obj : NIL;
                              }
                          }
                        finally
                          {
                            ZipCache.removeZip(zipFile.getName());
                          }
                      }
                  }
            	if (s.startsWith("ikvmres:")) {
                    InputStream in = url.openStream();
                    int bytesAvailable = in.available();
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    while (bytesAvailable>0) {
                    	byte[] b = new byte[bytesAvailable];
                    	in.read(b);
                    	bytesAvailable = in.available();
                    	buf.write(b);
                    }
                    LispObject obj = loadCompiledFunction(buf.toByteArray());
                    return obj != null ? obj : NIL;
            	}
              }
            catch (VerifyError e)
              {
                return error(new LispError("Class verification failed: " +
                                            e.getMessage()));
              }
            catch (IOException e)
              {
                Debug.trace(e);
              }
            catch (Throwable t)
              {
                Debug.trace(t);
              }
          }
        try {
        	if (IkvmSite.isIKVMDll() && namestring.endsWith(".class")) {
        		String className = namestring.substring(0,namestring.length()-6);
                Class c = Class.forName(Lisp.class.getPackage().getName()+"."+className.replace("-", "_"));
                LispObject obj = loadCompiledFunction(c);
                return obj != null ? obj : NIL;
        	}
        } catch (Throwable cnf) {
        	cnf.printStackTrace();
        }
        return error(new LispError("Unable to load " + namestring));
      }
    Pathname pathname = new Pathname(namestring);
    File file = Utilities.getFile(pathname, defaultPathname);
    if (file != null && !file.isFile()) {
    	 // maybe IKVM?
    	 file = IkvmSite.ikvmFileSafe(file);
    }
    if (file != null && file.isFile())
      {
        // The .cls file exists.
        try
          {
            LispObject obj = loadCompiledFunction(new FileInputStream(file),
                                                  (int) file.length());
            // FIXME close stream!
            if (obj != null)
              return obj;
          }
        catch (VerifyError e)
          {
            return error(new LispError("Class verification failed: " +
                                        e.getMessage()));
          }
        catch (Throwable t)
          {
            Debug.trace(t);
          }
        return error(new LispError("Unable to load " +
                                    pathname.writeToString()));
      }
    try
      {
        LispObject loadTruename = SymbolConstants.LOAD_TRUENAME.symbolValue(thread);
        String zipFileName = ((Pathname)loadTruename).getNamestring();
        ZipFile zipFile = ZipCache.getZip(zipFileName);
        try
          {
            ZipEntry entry = zipFile.getEntry(namestring);
            if (entry != null)
              {
                LispObject obj = loadCompiledFunction(zipFile.getInputStream(entry),
                                                      (int) entry.getSize());
                if (obj != null)
                  return obj;
                Debug.trace("Unable to load " + namestring);
                return error(new LispError("Unable to load " + namestring));
              }
          }
        finally
          {
            ZipCache.removeZip(zipFile.getName());
          }
      }
    catch (Throwable t)
      {
        Debug.trace(t);
      }
    return error(new FileError("File not found: " + namestring,
                                new Pathname(namestring)));
  }

  private static final LispObject loadCompiledFunction(InputStream in, int size)
  {
    try
      {
        byte[] bytes = new byte[size];
        int bytesRemaining = size;
        int bytesRead = 0;
        while (bytesRemaining > 0)
          {
            int n = in.read(bytes, bytesRead, bytesRemaining);
            if (n < 0)
              break;
            bytesRead += n;
            bytesRemaining -= n;
          }
        in.close();
        if (bytesRemaining > 0)
          Debug.trace("bytesRemaining = " + bytesRemaining);

        return loadCompiledFunction(bytes);
      }
    catch (Throwable t)
      {
        Debug.trace(t);
      }
    return null;
  }

    public static final LispObject loadCompiledFunction(byte[] bytes) throws Throwable {
        Class<?> c = (new JavaClassLoader())
            .loadClassFromByteArray(null, bytes, 0, bytes.length);
        if (c != null) {
            Constructor constructor = c.getConstructor((Class[])null);
            LispObject obj = (LispObject)constructor
                .newInstance((Object[])null);
            if (obj instanceof Function) {
              ((Function)obj).setClassBytes(bytes);
            }
            return obj;
        } else {
            return null;
        }
    }
    public static final LispObject loadCompiledFunction(Class c) throws Throwable {
        if (c != null) {
            Constructor constructor = c.getConstructor((Class[])null);
            LispObject obj = (LispObject) constructor.newInstance((Object[])null);
            return obj;
        } else {
            return null;
        }
    }
  public static final LispObject makeCompiledClosure(LispObject template,
                                                     ClosureBinding[] context)
    throws ConditionThrowable
  {
    return ((CompiledClosure)template).dup().setContext(context);
  }

  public static final String safeWriteToString(LispObject obj)
  {
    try
      {
        return obj.writeToString();
      }
    catch (ConditionThrowable t)
      {
        return obj.toString();
      }
    catch (NullPointerException e)
      {
        Debug.trace(e);
        return "null";
      }
  }

  public static final boolean isValidSetfFunctionName(LispObject obj)
  {
    if (obj instanceof Cons)
      {
        Cons cons = (Cons) obj;
        if (cons.car == SymbolConstants.SETF && cons.cdr instanceof Cons)
          {
            Cons cdr = (Cons) cons.cdr;
            return (cdr.car instanceof Symbol && cdr.cdr == NIL);
          }
      }
    return false;
  }

  public static final LispObject FUNCTION_NAME =
    list(SymbolConstants.OR,
          SymbolConstants.SYMBOL,
          list(SymbolConstants.CONS,
                list(SymbolConstants.EQL, SymbolConstants.SETF),
                list(SymbolConstants.CONS, SymbolConstants.SYMBOL, SymbolConstants.NULL)));

  public static final LispObject UNSIGNED_BYTE_8 =
    list(SymbolConstants.UNSIGNED_BYTE, Fixnum.constants[8]);

  public static final LispObject UNSIGNED_BYTE_16 =
    list(SymbolConstants.UNSIGNED_BYTE, Fixnum.constants[16]);

  public static final LispObject UNSIGNED_BYTE_32 =
    list(SymbolConstants.UNSIGNED_BYTE, Fixnum.constants[32]);

  public static final LispObject UNSIGNED_BYTE_32_MAX_VALUE =
    Bignum.getInteger(4294967296L);

  public static final LispObject getUpgradedArrayElementType(LispObject type)
    throws ConditionThrowable
  {
    if (type instanceof Symbol)
      {
        if (type == SymbolConstants.CHARACTER || type == SymbolConstants.BASE_CHAR ||
            type == SymbolConstants.STANDARD_CHAR)
          return SymbolConstants.CHARACTER;
        if (type == SymbolConstants.BIT)
          return SymbolConstants.BIT;
        if (type == NIL)
          return NIL;
      }
    if (type == BuiltInClass.CHARACTER)
      return SymbolConstants.CHARACTER;
    if (type instanceof Cons)
      {
        if (type.equal(UNSIGNED_BYTE_8))
          return type;
        if (type.equal(UNSIGNED_BYTE_16))
          return type;
        if (type.equal(UNSIGNED_BYTE_32))
          return type;
        LispObject car = type.CAR();
        if (car == SymbolConstants.INTEGER)
          {
            LispObject lower = type.CADR();
            LispObject upper = type.CDR().CADR();
            // Convert to inclusive bounds.
            if (lower instanceof Cons)
              lower = lower.CAR().incr();
            if (upper instanceof Cons)
              upper = upper.CAR().decr();
            if (lower.isInteger() && upper.isInteger())
              {
                if (lower .isFixnum() && upper .isFixnum())
                  {
                    int l = lower.intValue();
                    if (l >= 0)
                      {
                        int u = upper.intValue();
                        if (u <= 1)
                          return SymbolConstants.BIT;
                        if (u <= 255)
                          return UNSIGNED_BYTE_8;
                        if (u <= 65535)
                          return UNSIGNED_BYTE_16;
                        return UNSIGNED_BYTE_32;
                      }
                  }
                if (lower.isGreaterThanOrEqualTo(Fixnum.ZERO))
                  {
                    if (lower.isLessThan(UNSIGNED_BYTE_32_MAX_VALUE))
                      {
                        if (upper.isLessThan(UNSIGNED_BYTE_32_MAX_VALUE))
                          return UNSIGNED_BYTE_32;
                      }
                  }
              }
          }
        else if (car == SymbolConstants.EQL)
          {
            LispObject obj = type.CADR();
            if (obj .isFixnum())
              {
                int val = obj.intValue();
                if (val >= 0)
                  {
                    if (val <= 1)
                      return SymbolConstants.BIT;
                    if (val <= 255)
                      return UNSIGNED_BYTE_8;
                    if (val <= 65535)
                      return UNSIGNED_BYTE_16;
                    return UNSIGNED_BYTE_32;
                  }
              }
            else if (obj .isBignum())
              {
                if (obj.isGreaterThanOrEqualTo(Fixnum.ZERO))
                  {
                    if (obj.isLessThan(UNSIGNED_BYTE_32_MAX_VALUE))
                      return UNSIGNED_BYTE_32;
                  }
              }
          }
        else if (car == SymbolConstants.MEMBER)
          {
            LispObject rest = type.CDR();
            while (rest != NIL)
              {
                LispObject obj = rest.CAR();
                if (obj instanceof LispCharacter)
                  rest = rest.CDR();
                else
                  return T;
              }
            return SymbolConstants.CHARACTER;
          }
      }
    return T;
  }

  public static final byte coerceLispObjectToJavaByte(LispObject obj)
    throws ConditionThrowable
  {
          return (byte)obj.intValue();
  }

  public static final LispObject coerceJavaByteToLispObject(byte b)
  {
    return Fixnum.constants[((int)b) & 0xff];
  }

  public static final LispCharacter checkCharacter(LispObject obj)
    throws ConditionThrowable
  {
          if (obj instanceof LispCharacter) 
                  return (LispCharacter) obj;         
          return (LispCharacter) // Not reached.       
        type_error(obj, SymbolConstants.CHARACTER);
  }

  public static final Package checkPackage(LispObject obj)
    throws ConditionThrowable
  {
          if (obj instanceof Package)     
                  return (Package) obj;         
          return (Package) // Not reached.       
        type_error(obj, SymbolConstants.PACKAGE);
  }

  public static final Function checkFunction(LispObject obj)
    throws ConditionThrowable
  {
          if (obj instanceof Function)    
                  return (Function) obj;         
          return (Function) // Not reached.       
        type_error(obj, SymbolConstants.FUNCTION);
  }

  public static final Stream checkStream(LispObject obj)
    throws ConditionThrowable
  {
          if (obj instanceof Stream)      
                  return (Stream) obj;         
          return (Stream) // Not reached.       
        type_error(obj, SymbolConstants.STREAM);
  }

  public static final Stream checkCharacterInputStream(LispObject obj)
    throws ConditionThrowable
  {
          final Stream stream = checkStream(obj);
          if (stream.isCharacterInputStream())      
                  return stream;                        
          return (Stream) // Not reached.                      
          error(new TypeError("The value " + obj.writeToString() +
                        " is not a character input stream."));
  }

  public static final Stream checkCharacterOutputStream(LispObject obj)
    throws ConditionThrowable
  {
          final Stream stream = checkStream(obj);
          if (stream.isCharacterOutputStream())      
                  return stream;                        
        return (Stream) // Not reached.
        error(new TypeError("The value " + obj.writeToString() +
                            " is not a character output stream."));
  }

  public static final Stream checkBinaryInputStream(LispObject obj)
    throws ConditionThrowable
  {
          final Stream stream = checkStream(obj);
          if (stream.isBinaryInputStream())      
                  return stream;                        
        return (Stream) // Not reached.
        error(new TypeError("The value " + obj.writeToString() +
                             " is not a binary input stream."));
  }
  
  public static final Stream outSynonymOf(LispObject obj)
  throws ConditionThrowable
  {       
          if (obj instanceof Stream)
            return (Stream) obj;
          if (obj == T)
            return checkCharacterOutputStream(SymbolConstants.TERMINAL_IO.symbolValue());
          if (obj == NIL)
            return checkCharacterOutputStream(SymbolConstants.STANDARD_OUTPUT.symbolValue());
          return (Stream)         // Not reached.
          type_error(obj, SymbolConstants.STREAM);
  }

  public static final Stream inSynonymOf(LispObject obj)
    throws ConditionThrowable
  {
    if (obj instanceof Stream)
      return (Stream) obj;
    if (obj == T)
      return checkCharacterInputStream(SymbolConstants.TERMINAL_IO.symbolValue());
    if (obj == NIL)
      return checkCharacterInputStream(SymbolConstants.STANDARD_INPUT.symbolValue());
          return (Stream)         // Not reached.
          type_error(obj, SymbolConstants.STREAM);
  }

  public static final void writeByte(int n, LispObject obj)
    throws ConditionThrowable
  {
    if (n < 0 || n > 255)
      type_error(Fixnum.makeFixnum(n), UNSIGNED_BYTE_8);
    checkStream(obj)._writeByte(n);
  }

  public static final Readtable checkReadtable(LispObject obj)
    throws ConditionThrowable
  {
          if (obj instanceof Readtable)   
                  return (Readtable) obj;         
          return (Readtable)// Not reached.       
          type_error(obj, SymbolConstants.READTABLE);
  }
  
  public final static AbstractString checkString(LispObject obj) 
   throws ConditionThrowable 
  {
          if (obj instanceof AbstractString)            
                  return (AbstractString) obj;                    
          return (AbstractString)// Not reached.               
              type_error(obj, SymbolConstants.STRING);
  }
  
  public final static LispClass checkClass(LispObject obj) 
   throws ConditionThrowable 
   {
          if (obj instanceof LispClass)         
                  return (LispClass) obj;                         
          return (LispClass)// Not reached.                    
                type_error(obj, SymbolConstants.CLASS);
   }   

  public final static Layout checkLayout(LispObject obj) 
   throws ConditionThrowable 
  {
          if (obj instanceof Layout)            
                  return (Layout) obj;                    
          return (Layout)// Not reached.               
                type_error(obj, SymbolConstants.LAYOUT);
  }

  public static final Readtable designator_readtable(LispObject obj)
    throws ConditionThrowable
  {
    if (obj == NIL)
      obj = STANDARD_READTABLE.symbolValue();
    if (obj == null)
        throw new NullPointerException();
    return checkReadtable(obj);
  }

  public static final Environment checkEnvironment(LispObject obj)
    throws ConditionThrowable
  {
          if (obj instanceof Environment)         
                  return (Environment) obj;         
          return (Environment)// Not reached.       
        type_error(obj, SymbolConstants.ENVIRONMENT);
  }

  public static final void checkBounds(int start, int end, int length)
    throws ConditionThrowable
  {
    if (start < 0 || end < 0 || start > end || end > length)
      {
        FastStringBuffer sb = new FastStringBuffer("The bounding indices ");
        sb.append(start);
        sb.append(" and ");
        sb.append(end);
        sb.append(" are bad for a sequence of length ");
        sb.append(length);
        sb.append('.');
        error(new TypeError(sb.toString()));
      }
  }

  public static final LispObject coerceToFunction(LispObject obj)
    throws ConditionThrowable
  {
    if (obj instanceof Function)
      return obj;
    if (obj instanceof StandardGenericFunction)
      return obj;
    if (obj instanceof Symbol)
      {
        LispObject fun = obj.getSymbolFunction();
        if (fun instanceof Function)
          return (Function) fun;
      }
    else if (obj instanceof Cons && obj.CAR() == SymbolConstants.LAMBDA)
      return new Closure(obj, new Environment());
    error(new UndefinedFunction(obj));
    // Not reached.
    return null;
  }

  // Returns package or throws exception.
  public static final Package coerceToPackage(LispObject obj)
    throws ConditionThrowable
  {
    if (obj instanceof Package)
      return (Package) obj;
    Package pkg = Packages.findPackage(javaString(obj));
    if (pkg != null)
      return pkg;
    error(new PackageError(obj.writeToString() + " is not the name of a package."));
    // Not reached.
    return null;
  }

  public static Pathname coerceToPathname(LispObject arg)
    throws ConditionThrowable
  {
    if (arg instanceof Pathname)
      return (Pathname) arg;
    if (arg instanceof AbstractString)
      return Pathname.parseNamestring((AbstractString)arg);
    if (arg instanceof FileStream)
      return ((FileStream)arg).getPathname();
    type_error(arg, list(SymbolConstants.OR, SymbolConstants.PATHNAME,
                               SymbolConstants.STRING, SymbolConstants.FILE_STREAM));
    // Not reached.
    return null;
  }

  public static LispObject assq(LispObject item, LispObject alist)
    throws ConditionThrowable
  {
    while (alist instanceof Cons)
      {
        LispObject entry = ((Cons)alist).car;
        if (entry instanceof Cons)
          {
            if (((Cons)entry).car == item)
              return entry;
          }
        else if (entry != NIL)
          return type_error(entry, SymbolConstants.LIST);
        alist = ((Cons)alist).cdr;
      }
    if (alist != NIL)
      return type_error(alist, SymbolConstants.LIST);
    return NIL;
  }

  public static final boolean memq(LispObject item, LispObject list)
    throws ConditionThrowable
  {
    while (list instanceof Cons)
      {
        if (item == ((Cons)list).car)
          return true;
        list = ((Cons)list).cdr;
      }
    if (list != NIL)
      type_error(list, SymbolConstants.LIST);
    return false;
  }

  public static final boolean memql(LispObject item, LispObject list)
    throws ConditionThrowable
  {
    while (list instanceof Cons)
      {
        if (item.eql(((Cons)list).car))
          return true;
        list = ((Cons)list).cdr;
      }
    if (list != NIL)
      type_error(list, SymbolConstants.LIST);
    return false;
  }

  // Property lists.
  public static final LispObject getf(LispObject plist, LispObject indicator,
                                      LispObject defaultValue)
    throws ConditionThrowable
  {
    LispObject list = plist;
    while (list != NIL)
      {
        if (list.CAR() == indicator)
          return list.CADR();
        if (list.CDR() instanceof Cons)
          list = list.CDDR();
        else
          return error(new TypeError("Malformed property list: " +
                                      plist.writeToString()));
      }
    return defaultValue;
  }

  public static final LispObject get(LispObject symbol, LispObject indicator)
    throws ConditionThrowable
  {
    LispObject list = checkSymbol(symbol).getPropertyList();
    while (list != NIL)
      {
        if (list.CAR() == indicator)
          return list.CADR();
        list = list.CDDR();
      }
    return NIL;
  }

  public static final LispObject get(LispObject symbol, LispObject indicator,
                                     LispObject defaultValue)
    throws ConditionThrowable
  {
    LispObject list = checkSymbol(symbol).getPropertyList();
    while (list != NIL)
      {
        if (list.CAR() == indicator)
          return list.CADR();
        list = list.CDDR();
      }
    return defaultValue;
  }

  public static final LispObject put(Symbol symbol, LispObject indicator,
                                     LispObject value)
    throws ConditionThrowable
  {
    LispObject list = symbol.getPropertyList();
    while (list != NIL)
      {
        if (list.CAR() == indicator)
          {
            // Found it!
            LispObject rest = list.CDR();
            rest.setCar(value);
            return value;
          }
        list = list.CDDR();
      }
    // Not found.
    symbol.setPropertyList(makeCons(indicator,
                                    makeCons(value,
                                             symbol.getPropertyList())));
    return value;
  }

  public static final LispObject putf(LispObject plist, LispObject indicator,
                                      LispObject value)
    throws ConditionThrowable
  {
    LispObject list = plist;
    while (list != NIL)
      {
        if (list.CAR() == indicator)
          {
            // Found it!
            LispObject rest = list.CDR();
            rest.setCar(value);
            return plist;
          }
        list = list.CDDR();
      }
    // Not found.
    return makeCons(indicator, makeCons(value, plist));
  }

  public static final LispObject remprop(Symbol symbol, LispObject indicator)
    throws ConditionThrowable
  {
    LispObject list = checkList(symbol.getPropertyList());
    LispObject prev = null;
    while (list != NIL)
      {
        if (!(list.CDR() instanceof Cons))
          error(new ProgramError("The symbol " + symbol.writeToString() +
                                  " has an odd number of items in its property list."));
        if (list.CAR() == indicator)
          {
            // Found it!
            if (prev != null)
              prev.setCdr(list.CDDR());
            else
              symbol.setPropertyList(list.CDDR());
            return T;
          }
        prev = list.CDR();
        list = list.CDDR();
      }
    // Not found.
    return NIL;
  }

  public static final String format(LispObject formatControl,
                                    LispObject formatArguments)
    throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    String control = formatControl.getStringValue();
    LispObject[] args = formatArguments.copyToArray();
    StringBuffer sb = new StringBuffer();
    if (control != null)
      {
        final int limit = control.length();
        int j = 0;
        final int NEUTRAL = 0;
        final int TILDE = 1;
        int state = NEUTRAL;
        for (int i = 0; i < limit; i++)
          {
            char c = control.charAt(i);
            if (state == NEUTRAL)
              {
                if (c == '~')
                  state = TILDE;
                else
                  sb.append(c);
              }
            else if (state == TILDE)
              {
                if (c == 'A' || c == 'a')
                  {
                    if (j < args.length)
                      {
                        LispObject obj = args[j++];
                        SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
                        thread.bindSpecial(SymbolConstants.PRINT_ESCAPE, NIL);
                        thread.bindSpecial(SymbolConstants.PRINT_READABLY, NIL);
                        sb.append(obj.writeToString());
                        thread.lastSpecialBinding = lastSpecialBinding;
                      }
                  }
                else if (c == 'S' || c == 's')
                  {
                    if (j < args.length)
                      {
                        LispObject obj = args[j++];
                        SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
                        thread.bindSpecial(SymbolConstants.PRINT_ESCAPE, T);
                        try {
                            sb.append(obj.writeToString());
                        }
                        finally {
                            thread.lastSpecialBinding = lastSpecialBinding;
                        }
                      }
                  }
                else if (c == 'D' || c == 'd')
                  {
                    if (j < args.length)
                      {
                        LispObject obj = args[j++];
                        SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
                        thread.bindSpecial(SymbolConstants.PRINT_ESCAPE, NIL);
                        thread.bindSpecial(SymbolConstants.PRINT_RADIX, NIL);
                        thread.bindSpecial(SymbolConstants.PRINT_BASE, Fixnum.constants[10]);
                        try {
                            sb.append(obj.writeToString());
                        }
                        finally {
                            thread.lastSpecialBinding = lastSpecialBinding;
                        }
                      }
                  }
                else if (c == 'X' || c == 'x')
                  {
                    if (j < args.length)
                      {
                        LispObject obj = args[j++];
                        SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
                        thread.bindSpecial(SymbolConstants.PRINT_ESCAPE, NIL);
                        thread.bindSpecial(SymbolConstants.PRINT_RADIX, NIL);
                        thread.bindSpecial(SymbolConstants.PRINT_BASE, Fixnum.constants[16]);
                        try {
                            sb.append(obj.writeToString());
                        }
                        finally {
                            thread.lastSpecialBinding = lastSpecialBinding;
                        }
                      }
                  }
                else if (c == '%')
                  {
                    sb.append('\n');
                  }
                state = NEUTRAL;
              }
            else
              {
                // There are no other valid states.
                Debug.assertTrue(false);
              }
          }
      }
    return sb.toString();
  }

  public static final Symbol intern(String name, Package pkg)
  {
    return pkg.intern(name);
  }

  // Used by the compiler.
  public static final Symbol internInPackage(String name, String packageName)
    throws ConditionThrowable
  {
    Package pkg = Packages.findPackage(packageName);
    if (pkg == null)
      pkg = (Package) error(new LispError(packageName + " is not the name of a package."));
    return pkg.intern(name);
  }

  public static final Symbol internKeyword(String s)
  {
    return PACKAGE_KEYWORD.intern(s);
  }

  // The compiler's object table.
  /*private*/ static final Hashtable<String,LispObject> objectTable =
          new Hashtable<String,LispObject>();

  public static final LispObject recall(SimpleString key)
  {
    return (LispObject) objectTable.remove(key.getStringValue());
  }

  // ### remember
  public static final Primitive REMEMBER =
    new Primitive("remember", PACKAGE_SYS, true)
    {
      @Override
      public LispObject execute(LispObject key, LispObject value)
        throws ConditionThrowable
      {
        objectTable.put(key.getStringValue(), value);
        return NIL;
      }
    };

  public static final Symbol internSpecial(String name, Package pkg,
                                           LispObject value)
  {
    Symbol symbol = pkg.intern(name);
    symbol.setSpecial(true);
    symbol.setSymbolValue(value);
    return symbol;
  }

  public static final Symbol internConstant(String name, Package pkg,
                                            LispObject value)
  {
    Symbol symbol = pkg.intern(name);
    symbol.initializeConstant(value);
    return symbol;
  }

  public static final Symbol exportSpecial(String name, Package pkg,
                                           LispObject value)
  {
    Symbol symbol = pkg.intern(name);
    try
      {
        pkg.export(symbol); // FIXME Inefficient!
      }
    catch (ConditionThrowable t)
      {
        Debug.trace(t);
      }
    symbol.setSpecial(true);
    symbol.setSymbolValue(value);
    return symbol;
  }

  public static final Symbol exportConstant(String name, Package pkg,
                                            LispObject value)
  {
    Symbol symbol = pkg.intern(name);
    try
      {
        pkg.export(symbol); // FIXME Inefficient!
      }
    catch (ConditionThrowable t)
      {
        Debug.trace(t);
      }
    symbol.initializeConstant(value);
    return symbol;
  }

  static
  {
    String userDir = System.getProperty("user.dir");
    if (userDir != null && userDir.length() > 0)
      {
        if (userDir.charAt(userDir.length() - 1) != File.separatorChar)
          userDir = userDir.concat(File.separator);
      }
    // This string will be converted to a pathname when Pathname.java is loaded.
    SymbolConstants.DEFAULT_PATHNAME_DEFAULTS.initializeSpecial(new SimpleString(userDir));
  }

  static
  {
    SymbolConstants._PACKAGE_.initializeSpecial(PACKAGE_CL_USER);
  }

  public static final Package getCurrentPackage()
  {
    return (Package) SymbolConstants._PACKAGE_.symbolValueNoThrow();
  }

  private static Stream stdin = new Stream(System.in, SymbolConstants.CHARACTER, true);

  private static Stream stdout = new Stream(System.out, SymbolConstants.CHARACTER, true);

  static
  {
    SymbolConstants.STANDARD_INPUT.initializeSpecial(stdin);
    SymbolConstants.STANDARD_OUTPUT.initializeSpecial(stdout);
    SymbolConstants.ERROR_OUTPUT.initializeSpecial(stdout);
    SymbolConstants.TRACE_OUTPUT.initializeSpecial(stdout);
    SymbolConstants.TERMINAL_IO.initializeSpecial(new TwoWayStream(stdin, stdout, true));
    SymbolConstants.QUERY_IO.initializeSpecial(new TwoWayStream(stdin, stdout, true));
    SymbolConstants.DEBUG_IO.initializeSpecial(new TwoWayStream(stdin, stdout, true));
  }

  public static final void resetIO(Stream in, Stream out)
  {
    stdin = in;
    stdout = out;
    SymbolConstants.STANDARD_INPUT.setSymbolValue(stdin);
    SymbolConstants.STANDARD_OUTPUT.setSymbolValue(stdout);
    SymbolConstants.ERROR_OUTPUT.setSymbolValue(stdout);
    SymbolConstants.TRACE_OUTPUT.setSymbolValue(stdout);
    SymbolConstants.TERMINAL_IO.setSymbolValue(new TwoWayStream(stdin, stdout, true));
    SymbolConstants.QUERY_IO.setSymbolValue(new TwoWayStream(stdin, stdout, true));
    SymbolConstants.DEBUG_IO.setSymbolValue(new TwoWayStream(stdin, stdout, true));
  }

  // Used in org/armedbear/j/JLisp.java.
  public static final void resetIO()
  {
    resetIO(new Stream(System.in, SymbolConstants.CHARACTER, true),
            new Stream(System.out, SymbolConstants.CHARACTER, true));
  }

  public static final TwoWayStream getTerminalIO()
  {
    return (TwoWayStream) SymbolConstants.TERMINAL_IO.symbolValueNoThrow();
  }

  public static final Stream getStandardInput()
  {
    return (Stream) SymbolConstants.STANDARD_INPUT.symbolValueNoThrow();
  }

  public static final Stream getStandardOutput() throws ConditionThrowable
  {
    return checkCharacterOutputStream(SymbolConstants.STANDARD_OUTPUT.symbolValue());
  }

  static
  {
    SymbolConstants.CURRENT_READTABLE.initializeSpecial(new Readtable());
  }

  // ### +standard-readtable+
  // internal symbol
  public static final Symbol STANDARD_READTABLE =
    internConstant("+STANDARD-READTABLE+", PACKAGE_SYS, new Readtable());

  public static final Readtable currentReadtable() throws ConditionThrowable
  {
    return (Readtable) SymbolConstants.CURRENT_READTABLE.symbolValue();
  }

  static
  {
    SymbolConstants.READ_SUPPRESS.initializeSpecial(NIL);
    SymbolConstants.DEBUGGER_HOOK.initializeSpecial(NIL);
  }

  static
  {
    SymbolConstants.MOST_POSITIVE_FIXNUM.initializeConstant(Fixnum.makeFixnum(Integer.MAX_VALUE));
    SymbolConstants.MOST_NEGATIVE_FIXNUM.initializeConstant(Fixnum.makeFixnum(Integer.MIN_VALUE));
    SymbolConstants.MOST_POSITIVE_JAVA_LONG.initializeConstant(Bignum.getInteger(Long.MAX_VALUE));
    SymbolConstants.MOST_NEGATIVE_JAVA_LONG.initializeConstant(Bignum.getInteger(Long.MIN_VALUE));
  }

  public static void exit(int status)
  {
    Interpreter interpreter = Interpreter.getInstance();
    if (interpreter != null)
      interpreter.kill(status);
  }

  // ### t
  public static final Symbol T = SymbolConstants.T;
  static
  {
    T.initializeConstant(T);
  }

  static
  {
    SymbolConstants.READ_EVAL.initializeSpecial(T);
  }

  // ### *features*
  static
  {
    SymbolConstants.FEATURES.initializeSpecial(NIL);
    String osName = System.getProperty("os.name");
    if (osName.startsWith("Linux"))
      {
        SymbolConstants.FEATURES.setSymbolValue(list(Keyword.ARMEDBEAR,
                                             Keyword.ABCL,
                                             Keyword.COMMON_LISP,
                                             Keyword.ANSI_CL,
                                             Keyword.UNIX,
                                             Keyword.LINUX,
                                             Keyword.CDR6));
      }
    else if (osName.startsWith("SunOS"))
      {
        SymbolConstants.FEATURES.setSymbolValue(list(Keyword.ARMEDBEAR,
                                             Keyword.ABCL,
                                             Keyword.COMMON_LISP,
                                             Keyword.ANSI_CL,
                                             Keyword.UNIX,
                                             Keyword.SUNOS,
                                             Keyword.CDR6));
      }
    else if (osName.startsWith("Mac OS X"))
      {
        SymbolConstants.FEATURES.setSymbolValue(list(Keyword.ARMEDBEAR,
                                             Keyword.ABCL,
                                             Keyword.COMMON_LISP,
                                             Keyword.ANSI_CL,
                                             Keyword.UNIX,
                                             Keyword.DARWIN,
                                             Keyword.CDR6));
      }
    else if (osName.startsWith("FreeBSD"))
      {
        SymbolConstants.FEATURES.setSymbolValue(list(Keyword.ARMEDBEAR,
                                             Keyword.ABCL,
                                             Keyword.COMMON_LISP,
                                             Keyword.ANSI_CL,
                                             Keyword.UNIX,
                                             Keyword.FREEBSD,
                                             Keyword.CDR6));
      }
    else if (osName.startsWith("OpenBSD"))
      {
        SymbolConstants.FEATURES.setSymbolValue(list(Keyword.ARMEDBEAR,
                                             Keyword.ABCL,
                                             Keyword.COMMON_LISP,
                                             Keyword.ANSI_CL,
                                             Keyword.UNIX,
                                             Keyword.OPENBSD,
                                             Keyword.CDR6));
      }
    else if (osName.startsWith("NetBSD"))
      {
        SymbolConstants.FEATURES.setSymbolValue(list(Keyword.ARMEDBEAR,
                                             Keyword.ABCL,
                                             Keyword.COMMON_LISP,
                                             Keyword.ANSI_CL,
                                             Keyword.UNIX,
                                             Keyword.NETBSD,
                                             Keyword.CDR6));
      }
    else if (osName.startsWith("Windows"))
      {
        SymbolConstants.FEATURES.setSymbolValue(list(Keyword.ARMEDBEAR,
                                             Keyword.ABCL,
                                             Keyword.COMMON_LISP,
                                             Keyword.ANSI_CL,
                                             Keyword.WINDOWS,
                                             Keyword.CDR6));
      }
    else
      {
        SymbolConstants.FEATURES.setSymbolValue(list(Keyword.ARMEDBEAR,
                                             Keyword.ABCL,
                                             Keyword.COMMON_LISP,
                                             Keyword.ANSI_CL,
                                             Keyword.CDR6));
      }
  }
  static
  {
    final String version = System.getProperty("java.version");
    if (version.startsWith("1.5"))
      {
        SymbolConstants.FEATURES.setSymbolValue(makeCons(Keyword.JAVA_1_5,
                                                SymbolConstants.FEATURES.getSymbolValue()));
      }
    else if (version.startsWith("1.6"))
      {
        SymbolConstants.FEATURES.setSymbolValue(makeCons(Keyword.JAVA_1_6,
                                                SymbolConstants.FEATURES.getSymbolValue()));
      }
    else if (version.startsWith("1.7"))
      {
        SymbolConstants.FEATURES.setSymbolValue(makeCons(Keyword.JAVA_1_7,
                                                SymbolConstants.FEATURES.getSymbolValue()));
      }
  }
  static
  {
    String os_arch = System.getProperty("os.arch");
    if(os_arch != null) {
      if (os_arch.equals("amd64"))
        SymbolConstants.FEATURES.setSymbolValue(makeCons(Keyword.X86_64,
                                                SymbolConstants.FEATURES.getSymbolValue()));
      else if (os_arch.equals("x86"))
        SymbolConstants.FEATURES.setSymbolValue(makeCons(Keyword.X86,
                                                SymbolConstants.FEATURES.getSymbolValue()));
    }
  }

  static
  {
    SymbolConstants.MODULES.initializeSpecial(NIL);
  }

  static
  {
    SymbolConstants.LOAD_VERBOSE.initializeSpecial(NIL);
    SymbolConstants.LOAD_PRINT.initializeSpecial(NIL);
    SymbolConstants.LOAD_PATHNAME.initializeSpecial(NIL);
    SymbolConstants.LOAD_TRUENAME.initializeSpecial(NIL);
    SymbolConstants.COMPILE_VERBOSE.initializeSpecial(T);
    SymbolConstants.COMPILE_PRINT.initializeSpecial(T);
    SymbolConstants._COMPILE_FILE_PATHNAME_.initializeSpecial(NIL);
    SymbolConstants.COMPILE_FILE_TRUENAME.initializeSpecial(NIL);
  }

  // ### *load-depth*
  // internal symbol
  public static final Symbol _LOAD_DEPTH_ =
    internSpecial("*LOAD-DEPTH*", PACKAGE_SYS, Fixnum.ZERO);

  // ### *load-stream*
  // internal symbol
  public static final Symbol _LOAD_STREAM_ =
    internSpecial("*LOAD-STREAM*", PACKAGE_SYS, NIL);

  // ### *source*
  // internal symbol
  public static final Symbol _SOURCE_ =
    exportSpecial("*SOURCE*", PACKAGE_SYS, NIL);

  // ### *source-position*
  // internal symbol
  public static final Symbol _SOURCE_POSITION_ =
    exportSpecial("*SOURCE-POSITION*", PACKAGE_SYS, NIL);

  // ### *autoload-verbose*
  // internal symbol
  public static final Symbol _AUTOLOAD_VERBOSE_ =
    exportSpecial("*AUTOLOAD-VERBOSE*", PACKAGE_EXT, NIL);

  // ### *compile-file-type*
  public static final String COMPILE_FILE_TYPE = "abcl";
  public static final Symbol _COMPILE_FILE_TYPE_ =
    internConstant("*COMPILE-FILE-TYPE*", PACKAGE_SYS,
                   new SimpleString(COMPILE_FILE_TYPE));

  // ### *compile-file-zip*
  public static final Symbol _COMPILE_FILE_ZIP_ =
    exportSpecial("*COMPILE-FILE-ZIP*", PACKAGE_SYS, T);

  static
  {
    SymbolConstants.MACROEXPAND_HOOK.initializeSpecial(SymbolConstants.FUNCALL);
  }

  public static final int ARRAY_DIMENSION_MAX = Integer.MAX_VALUE;
  static
  {
    // ### array-dimension-limit
    SymbolConstants.ARRAY_DIMENSION_LIMIT.initializeConstant(Fixnum.makeFixnum(ARRAY_DIMENSION_MAX));
  }

  // ### char-code-limit
  // "The upper exclusive bound on the value returned by the function CHAR-CODE."
  public static final int CHAR_MAX = 256;
  static
  {
    SymbolConstants.CHAR_CODE_LIMIT.initializeConstant(Fixnum.makeFixnum(CHAR_MAX));
  }

  static
  {
    SymbolConstants.READ_BASE.initializeSpecial(Fixnum.constants[10]);
  }

  static
  {
    SymbolConstants.READ_DEFAULT_FLOAT_FORMAT.initializeSpecial(SymbolConstants.SINGLE_FLOAT);
  }

  // Printer control variables.
  static
  {
    SymbolConstants.PRINT_ARRAY.initializeSpecial(T);
    SymbolConstants.PRINT_BASE.initializeSpecial(Fixnum.constants[10]);
    SymbolConstants.PRINT_CASE.initializeSpecial(Keyword.UPCASE);
    SymbolConstants.PRINT_CIRCLE.initializeSpecial(NIL);
    SymbolConstants.PRINT_ESCAPE.initializeSpecial(T);
    SymbolConstants.PRINT_GENSYM.initializeSpecial(T);
    SymbolConstants.PRINT_LENGTH.initializeSpecial(NIL);
    SymbolConstants.PRINT_LEVEL.initializeSpecial(NIL);
    SymbolConstants.PRINT_LINES.initializeSpecial(NIL);
    SymbolConstants.PRINT_MISER_WIDTH.initializeSpecial(NIL);
    SymbolConstants.PRINT_PPRINT_DISPATCH.initializeSpecial(NIL);
    SymbolConstants.PRINT_PRETTY.initializeSpecial(NIL);
    SymbolConstants.PRINT_RADIX.initializeSpecial(NIL);
    SymbolConstants.PRINT_READABLY.initializeSpecial(NIL);
    SymbolConstants.PRINT_RIGHT_MARGIN.initializeSpecial(NIL);
  }

  public static final Symbol _PRINT_STRUCTURE_ =
    exportSpecial("*PRINT-STRUCTURE*", PACKAGE_EXT, T);

  // ### *current-print-length*
  public static final Symbol _CURRENT_PRINT_LENGTH_ =
    exportSpecial("*CURRENT-PRINT-LENGTH*", PACKAGE_SYS, Fixnum.ZERO);

  // ### *current-print-level*
  public static final Symbol _CURRENT_PRINT_LEVEL_ =
    exportSpecial("*CURRENT-PRINT-LEVEL*", PACKAGE_SYS, Fixnum.ZERO);

  public static final Symbol _PRINT_FASL_ =
    internSpecial("*PRINT-FASL*", PACKAGE_SYS, NIL);

  static
  {
    SymbolConstants._RANDOM_STATE_.initializeSpecial(new RandomState());
  }

  static
  {
    SymbolConstants.STAR.initializeSpecial(NIL);
    SymbolConstants.STAR_STAR.initializeSpecial(NIL);
    SymbolConstants.STAR_STAR_STAR.initializeSpecial(NIL);
    SymbolConstants.MINUS.initializeSpecial(NIL);
    SymbolConstants.PLUS.initializeSpecial(NIL);
    SymbolConstants.PLUS_PLUS.initializeSpecial(NIL);
    SymbolConstants.PLUS_PLUS_PLUS.initializeSpecial(NIL);
    SymbolConstants.SLASH.initializeSpecial(NIL);
    SymbolConstants.SLASH_SLASH.initializeSpecial(NIL);
    SymbolConstants.SLASH_SLASH_SLASH.initializeSpecial(NIL);
  }

  // Floating point constants.
  static
  {
    SymbolConstants.PI.initializeConstant(NumericLispObject.createDoubleFloat(Math.PI));
    SymbolConstants.SHORT_FLOAT_EPSILON.initializeConstant(NumericLispObject.createSingleFloat((float)5.960465E-8));
    SymbolConstants.SINGLE_FLOAT_EPSILON.initializeConstant(NumericLispObject.createSingleFloat((float)5.960465E-8));
    SymbolConstants.DOUBLE_FLOAT_EPSILON.initializeConstant(NumericLispObject.createDoubleFloat((double)1.1102230246251568E-16));
    SymbolConstants.LONG_FLOAT_EPSILON.initializeConstant(NumericLispObject.createDoubleFloat((double)1.1102230246251568E-16));
    SymbolConstants.SHORT_FLOAT_NEGATIVE_EPSILON.initializeConstant(NumericLispObject.createSingleFloat(2.9802326e-8f));
    SymbolConstants.SINGLE_FLOAT_NEGATIVE_EPSILON.initializeConstant(NumericLispObject.createSingleFloat(2.9802326e-8f));
    SymbolConstants.DOUBLE_FLOAT_NEGATIVE_EPSILON.initializeConstant(NumericLispObject.createDoubleFloat((double)5.551115123125784E-17));
    SymbolConstants.LONG_FLOAT_NEGATIVE_EPSILON.initializeConstant(NumericLispObject.createDoubleFloat((double)5.551115123125784E-17));
    SymbolConstants.MOST_POSITIVE_SHORT_FLOAT.initializeConstant(NumericLispObject.createSingleFloat(Float.MAX_VALUE));
    SymbolConstants.MOST_POSITIVE_SINGLE_FLOAT.initializeConstant(NumericLispObject.createSingleFloat(Float.MAX_VALUE));
    SymbolConstants.MOST_POSITIVE_DOUBLE_FLOAT.initializeConstant(NumericLispObject.createDoubleFloat(Double.MAX_VALUE));
    SymbolConstants.MOST_POSITIVE_LONG_FLOAT.initializeConstant(NumericLispObject.createDoubleFloat(Double.MAX_VALUE));
    SymbolConstants.LEAST_POSITIVE_SHORT_FLOAT.initializeConstant(NumericLispObject.createSingleFloat(Float.MIN_VALUE));
    SymbolConstants.LEAST_POSITIVE_SINGLE_FLOAT.initializeConstant(NumericLispObject.createSingleFloat(Float.MIN_VALUE));
    SymbolConstants.LEAST_POSITIVE_DOUBLE_FLOAT.initializeConstant(NumericLispObject.createDoubleFloat(Double.MIN_VALUE));
    SymbolConstants.LEAST_POSITIVE_LONG_FLOAT.initializeConstant(NumericLispObject.createDoubleFloat(Double.MIN_VALUE));
    SymbolConstants.LEAST_POSITIVE_NORMALIZED_SHORT_FLOAT.initializeConstant(NumericLispObject.createSingleFloat(1.17549435e-38f));
    SymbolConstants.LEAST_POSITIVE_NORMALIZED_SINGLE_FLOAT.initializeConstant(NumericLispObject.createSingleFloat(1.17549435e-38f));
    SymbolConstants.LEAST_POSITIVE_NORMALIZED_DOUBLE_FLOAT.initializeConstant(NumericLispObject.createDoubleFloat(2.2250738585072014e-308d));
    SymbolConstants.LEAST_POSITIVE_NORMALIZED_LONG_FLOAT.initializeConstant(NumericLispObject.createDoubleFloat(2.2250738585072014e-308d));
    SymbolConstants.MOST_NEGATIVE_SHORT_FLOAT.initializeConstant(NumericLispObject.createSingleFloat(- Float.MAX_VALUE));
    SymbolConstants.MOST_NEGATIVE_SINGLE_FLOAT.initializeConstant(NumericLispObject.createSingleFloat(- Float.MAX_VALUE));
    SymbolConstants.MOST_NEGATIVE_DOUBLE_FLOAT.initializeConstant(NumericLispObject.createDoubleFloat(- Double.MAX_VALUE));
    SymbolConstants.MOST_NEGATIVE_LONG_FLOAT.initializeConstant(NumericLispObject.createDoubleFloat(- Double.MAX_VALUE));
    SymbolConstants.LEAST_NEGATIVE_SHORT_FLOAT.initializeConstant(NumericLispObject.createSingleFloat(- Float.MIN_VALUE));
    SymbolConstants.LEAST_NEGATIVE_SINGLE_FLOAT.initializeConstant(NumericLispObject.createSingleFloat(- Float.MIN_VALUE));
    SymbolConstants.LEAST_NEGATIVE_DOUBLE_FLOAT.initializeConstant(NumericLispObject.createDoubleFloat(- Double.MIN_VALUE));
    SymbolConstants.LEAST_NEGATIVE_LONG_FLOAT.initializeConstant(NumericLispObject.createDoubleFloat(- Double.MIN_VALUE));
    SymbolConstants.LEAST_NEGATIVE_NORMALIZED_SHORT_FLOAT.initializeConstant(NumericLispObject.createSingleFloat(-1.17549435e-38f));
    SymbolConstants.LEAST_NEGATIVE_NORMALIZED_SINGLE_FLOAT.initializeConstant(NumericLispObject.createSingleFloat(-1.17549435e-38f));
    SymbolConstants.LEAST_NEGATIVE_NORMALIZED_DOUBLE_FLOAT.initializeConstant(NumericLispObject.createDoubleFloat(-2.2250738585072014e-308d));
    SymbolConstants.LEAST_NEGATIVE_NORMALIZED_LONG_FLOAT.initializeConstant(NumericLispObject.createDoubleFloat(-2.2250738585072014e-308d));
  }

  static
  {
    SymbolConstants.BOOLE_CLR.initializeConstant(Fixnum.ZERO);
    SymbolConstants.BOOLE_SET.initializeConstant(Fixnum.ONE);
    SymbolConstants.BOOLE_1.initializeConstant(Fixnum.TWO);
    SymbolConstants.BOOLE_2.initializeConstant(Fixnum.constants[3]);
    SymbolConstants.BOOLE_C1.initializeConstant(Fixnum.constants[4]);
    SymbolConstants.BOOLE_C2.initializeConstant(Fixnum.constants[5]);
    SymbolConstants.BOOLE_AND.initializeConstant(Fixnum.constants[6]);
    SymbolConstants.BOOLE_IOR.initializeConstant(Fixnum.constants[7]);
    SymbolConstants.BOOLE_XOR.initializeConstant(Fixnum.constants[8]);
    SymbolConstants.BOOLE_EQV.initializeConstant(Fixnum.constants[9]);
    SymbolConstants.BOOLE_NAND.initializeConstant(Fixnum.constants[10]);
    SymbolConstants.BOOLE_NOR.initializeConstant(Fixnum.constants[11]);
    SymbolConstants.BOOLE_ANDC1.initializeConstant(Fixnum.constants[12]);
    SymbolConstants.BOOLE_ANDC2.initializeConstant(Fixnum.constants[13]);
    SymbolConstants.BOOLE_ORC1.initializeConstant(Fixnum.constants[14]);
    SymbolConstants.BOOLE_ORC2.initializeConstant(Fixnum.constants[15]);
  }

  static
  {
    // ### call-arguments-limit
    SymbolConstants.CALL_ARGUMENTS_LIMIT.initializeConstant(Fixnum.constants[50]);
  }

  static
  {
    // ### lambda-parameters-limit
    SymbolConstants.LAMBDA_PARAMETERS_LIMIT.initializeConstant(Fixnum.constants[50]);
  }

  static
  {
    // ### multiple-values-limit
    SymbolConstants.MULTIPLE_VALUES_LIMIT.initializeConstant(Fixnum.constants[20]);
  }

  static
  {
    // ### internal-time-units-per-second
    SymbolConstants.INTERNAL_TIME_UNITS_PER_SECOND.initializeConstant(Fixnum.makeFixnum(1000));
  }

  // ### call-registers-limit
  public static final Symbol CALL_REGISTERS_LIMIT =
    exportConstant("CALL-REGISTERS-LIMIT", PACKAGE_SYS,
                   Fixnum.constants[CALL_REGISTERS_MAX]);

  // ### *warn-on-redefinition*
  public static final Symbol _WARN_ON_REDEFINITION_ =
    exportSpecial("*WARN-ON-REDEFINITION*", PACKAGE_EXT, T);

  // ### *saved-backtrace*
  public static final Symbol _SAVED_BACKTRACE_ =
    exportSpecial("*SAVED-BACKTRACE*", PACKAGE_EXT, NIL);

  // ### *batch-mode*
  public static final Symbol _BATCH_MODE_ =
    exportSpecial("*BATCH-MODE*", PACKAGE_EXT, NIL);

  // ### *noinform*
  public static final Symbol _NOINFORM_ =
    exportSpecial("*NOINFORM*", PACKAGE_SYS, NIL);

  // ### *disassembler*
  public static final Symbol _DISASSEMBLER_ =
    exportSpecial("*DISASSEMBLER*", PACKAGE_EXT,
                  new SimpleString("jad -a -p")); // or "jad -dis -p"

  // ### *speed* compiler policy
  public static final Symbol _SPEED_ =
    exportSpecial("*SPEED*", PACKAGE_SYS, Fixnum.ONE);

  // ### *space* compiler policy
  public static final Symbol _SPACE_ =
    exportSpecial("*SPACE*", PACKAGE_SYS, Fixnum.ONE);

  // ### *safety* compiler policy
  public static final Symbol _SAFETY_ =
    exportSpecial("*SAFETY*", PACKAGE_SYS, Fixnum.ONE);

  // ### *debug* compiler policy
  public static final Symbol _DEBUG_ =
    exportSpecial("*DEBUG*", PACKAGE_SYS, Fixnum.ONE);

  // ### *explain* compiler policy
  public static final Symbol _EXPLAIN_ =
    exportSpecial("*EXPLAIN*", PACKAGE_SYS, NIL);

  // ### *enable-inline-expansion*
  public static final Symbol _ENABLE_INLINE_EXPANSION_ =
    exportSpecial("*ENABLE-INLINE-EXPANSION*", PACKAGE_EXT, T);

  // ### *require-stack-frame*
  public static final Symbol _REQUIRE_STACK_FRAME_ =
    exportSpecial("*REQUIRE-STACK-FRAME*", PACKAGE_EXT, NIL);

  static
  {
    SymbolConstants.SUPPRESS_COMPILER_WARNINGS.initializeSpecial(NIL);
  }

  public static final Symbol _COMPILE_FILE_ENVIRONMENT_ =
    exportSpecial("*COMPILE-FILE-ENVIRONMENT*", PACKAGE_SYS, NIL);

  public static final LispObject UNBOUND_VALUE = new SingletonLispObject()
    {
      @Override
      public String writeToString()
      {
        return "#<UNBOUND>";
      }
    };

  public static final LispObject NULL_VALUE = new SingletonLispObject()
    {
      @Override
      public String writeToString()
      {
        return "null";
      }
    };

  public static final Symbol _SLOT_UNBOUND_ =
    exportConstant("+SLOT-UNBOUND+", PACKAGE_SYS, UNBOUND_VALUE);

  public static final Symbol _CL_PACKAGE_ =
    exportConstant("+CL-PACKAGE+", PACKAGE_SYS, PACKAGE_CL);

  public static final Symbol _KEYWORD_PACKAGE_ =
    exportConstant("+KEYWORD-PACKAGE+", PACKAGE_SYS, PACKAGE_KEYWORD);

  // ### *backquote-count*
  public static final Symbol _BACKQUOTE_COUNT_ =
    internSpecial("*BACKQUOTE-COUNT*", PACKAGE_SYS, Fixnum.ZERO);

  // ### *bq-vector-flag*
  public static final Symbol _BQ_VECTOR_FLAG_ =
    internSpecial("*BQ-VECTOR-FLAG*", PACKAGE_SYS, list(new Symbol("bqv")));

  // ### *traced-names*
  public static final Symbol _TRACED_NAMES_ =
    exportSpecial("*TRACED-NAMES*", PACKAGE_SYS, NIL);

  // Floating point traps.
  protected static boolean TRAP_OVERFLOW  = true;
  protected static boolean TRAP_UNDERFLOW = true;


  // Extentions
  static {
    SymbolConstants._INSPECTOR_HOOK_.initializeSpecial(NIL);
  }
  public static final EqHashTable documentationHashTable =
      new EqHashTable(11, NIL, NIL);

  public static final LispObject[] ZERO_LISPOBJECTS = new LispObject[0];


  private static final void loadClass(String className)
  {
    try
      {
        Class.forName(className);
      }
    catch (ClassNotFoundException e)
      {
        e.printStackTrace();
      }
  }

  public static JavaObject makeNewJavaObject(Object obj) {
    return new JavaObject(obj);
  }
  
  public static LispObject getInstance(Object obj) {
	    return JavaObject.getInstance(obj);
	  }
  public static LispObject getBoolean(boolean obj) {
	    return obj?T:NIL;
	  }
  public static LispObject getInstance(Object obj,boolean translate)  throws ConditionThrowable {
    return JavaObject.getInstance(obj,translate);
  }
  private static final EqHashTable lispClassMap = new EqHashTable(256, NIL, NIL);

  public static void addLispClass(Symbol symbol, LispClass c)
  {
    synchronized (lispClassMap)
      {
        lispClassMap.putVoid(symbol, c);
      }
  }

  public static void removeLispClass(Symbol symbol)
  {
    synchronized (lispClassMap)
      {
        lispClassMap.remove(symbol);
      }
  }

  public static LispClass findLispClass(Symbol symbol)
  {
    synchronized (lispClassMap)
      {
        return (LispClass) lispClassMap.get(symbol);
      }
  }

  public static LispObject findLispClass(LispObject name, boolean errorp)
    throws ConditionThrowable
  {
    final Symbol symbol = checkSymbol(name);
    final LispClass c;
    synchronized (lispClassMap)
      {
        c = (LispClass) lispClassMap.get(symbol);
      }
    if (c != null)
      return c;
    if (errorp)
      {
        FastStringBuffer sb =
          new FastStringBuffer("There is no class named ");
        sb.append(name.writeToString());
        sb.append('.');
        return error(new LispError(sb.toString()));
      }
    return NIL;
  }



static
  {
    loadClass("org.armedbear.lisp.Primitives");
    loadClass("org.armedbear.lisp.SpecialOperators");
    loadClass("org.armedbear.lisp.Extensions");
    loadClass("org.armedbear.lisp.CompiledClosure");
    loadClass("org.armedbear.lisp.Autoload");
    loadClass("org.armedbear.lisp.AutoloadMacro");
    loadClass("org.armedbear.lisp.cxr");
    loadClass("org.armedbear.lisp.Do");
    loadClass("org.armedbear.lisp.dolist");
    loadClass("org.armedbear.lisp.dotimes");
    loadClass("org.armedbear.lisp.Pathname");
    loadClass("org.armedbear.lisp.LispClass");
    loadClass("org.armedbear.lisp.BuiltInClass");
    loadClass("org.armedbear.lisp.StructureObjectImpl");
    loadClass("org.armedbear.lisp.ash");
    loadClass("org.armedbear.lisp.Java");
    cold = false;
  }
static {
	//force load
	IkvmSite.isIKVM();
}
}
