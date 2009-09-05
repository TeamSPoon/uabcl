/*
 * java
 *
 * Copyright (C) 2002-2007 Peter Graves
 * $Id: java 12105 2009-08-19 14:51:56Z mevenson $
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
import static org.armedbear.lisp.Nil.NIL;
import static org.armedbear.lisp.SymbolConstants.T;
import static org.armedbear.lisp.SymbolConstants.*;
import static org.armedbear.lisp.Lisp.*;

import java.math.BigInteger;

import com.cyc.tool.subl.jrtl.nativeCode.type.symbol.SubLSymbolImpl;

public class LispSymbol extends AbstractLispObject implements Symbol
{
  // Bit flags.
  private static final int FLAG_SPECIAL           = 0x0001;
  private static final int FLAG_CONSTANT          = 0x0002;
  private static final int FLAG_BUILT_IN_FUNCTION = 0x0004;

  public final SimpleString name;
  private int hash = -1;
  private LispObject pkg; // Either a package object or NIL.
  private LispObject value;
  LispObject function;
  private LispObject propertyList;
  private int flags;

//  // Construct an uninterned symbol.
//  public Symbol(String s)
//  {
//    name = new SimpleString(s);
//    pkg = NIL;
//  }

  public LispSymbol(SimpleString string)
  {
    name = string;
    pkg = NIL;
  }
//
//  public Symbol(String s, LispPackage pkg)
//  {
//    name = new SimpleString(s);
//    this.pkg = pkg;
//  }

  public LispSymbol(SimpleString string, LispPackage pkg)
  {
    name = string;
    this.pkg = pkg;
  }

  public LispSymbol(SimpleString string, int hash, LispPackage pkg)
  {
    name = string;
    this.hash = hash;
    this.pkg = pkg;
  }

  @Override
  public LispObject typeOf()
  {
    if (pkg == PACKAGE_KEYWORD)
      return SymbolConstants.KEYWORD;
    if (this == T)
      return BOOLEAN;
    return SYMBOL;
  }

  @Override
  public LispObject classOf()
  {
    return BuiltInClass.SYMBOL;
  }

  @Override
  public LispObject getDescription() throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
    thread.bindSpecial(SymbolConstants.PRINT_ESCAPE, NIL);
    try
      {
        FastStringBuffer sb = new FastStringBuffer("The symbol ");
        sb.append(name.writeToString());
        sb.append(" at #x");
        sb.append(Integer.toHexString(System.identityHashCode(this)).toUpperCase());
        if (pkg instanceof LispPackage)
          {
            sb.append(", an ");
            Symbol sym = ((LispPackage)pkg).findExternalSymbol(name);
            sb.append(sym == this ? "external" : "internal");
            sb.append(" symbol in the ");
            sb.append(((LispPackage)pkg).getName());
            sb.append(" package");
          }
        return new SimpleString(sb);
      }
    finally
      {
        thread.lastSpecialBinding = lastSpecialBinding;
      }
  }

  @Override
  public LispObject getParts() throws ConditionThrowable
  {
    LispObject parts = NIL;
    parts = parts.push(makeCons("name", name));
    parts = parts.push(makeCons("package", pkg));
    parts = parts.push(makeCons("value", value));
    parts = parts.push(makeCons("function", function));
    parts = parts.push(makeCons("plist", propertyList));
    parts = parts.push(makeCons("flags", Fixnum.makeFixnum(flags)));
    parts = parts.push(makeCons("hash", Fixnum.makeFixnum(hash)));
    return parts.nreverse();
  }

  @Override
  public LispObject typep(LispObject type) throws ConditionThrowable
  {
    if (type == SYMBOL)
      return T;
    if (type == BuiltInClass.SYMBOL)
      return T;
    if (type == KEYWORD)
      return pkg == PACKAGE_KEYWORD ? T : NIL;
    if (type == BOOLEAN)
      return this == T ? T : NIL;
    return super.typep(type);
  }

  @Override
  public final LispObject SYMBOLP()
  {
    return T;
  }

  @Override
  public boolean constantp()
  {
    return (flags & FLAG_CONSTANT) != 0;
  }

  @Override
  public final LispObject STRING()
  {
    return name;
  }

  public final LispObject getLispPackage()
  {
    return pkg;
  }

  public final void setPackage(LispObject obj)
  {
    pkg = obj;
  }

  @Override
  public final boolean isSpecialOperator()
  {
    return (function instanceof SpecialOperator);
  }

  @Override
  public final boolean isSpecialVariable()
  {
    return (flags & FLAG_SPECIAL) != 0;
  }

  public final void setSpecial(boolean b)
  {
    if (b)
      flags |= FLAG_SPECIAL;
    else
      flags &= ~FLAG_SPECIAL;
  }

  public final void initializeSpecial(LispObject value)
  {
    flags |= FLAG_SPECIAL;
    this.value = value;
  }

  public final boolean isConstant()
  {
    return (flags & FLAG_CONSTANT) != 0;
  }

  public final void initializeConstant(LispObject value)
  {
    flags |= (FLAG_SPECIAL | FLAG_CONSTANT);
    this.value = value;
  }

  public final boolean isBuiltInFunction()
  {
    return (flags & FLAG_BUILT_IN_FUNCTION) != 0;
  }

  public final void setBuiltInFunction(boolean b)
  {
    if (b)
      flags |= FLAG_BUILT_IN_FUNCTION;
    else
      flags &= ~FLAG_BUILT_IN_FUNCTION;
  }

  public final String getName()
  {
    try
      {
        return name.getStringValue();
      }
    catch (Throwable t)
      {
        Debug.trace(t);
        return null;
      }
  }

  public final String getQualifiedName()
  {
    try
      {
        final String n = name.getStringValue();
        if (pkg == NIL)
          return("#:".concat(n));
        if (pkg == PACKAGE_KEYWORD)
          return ":".concat(n);
        FastStringBuffer sb = new FastStringBuffer(((LispPackage)pkg).getName());
        if (((LispPackage)pkg).findExternalSymbol(name) != null)
          sb.append(':');
        else
          sb.append("::");
        sb.append(n);
        return sb.toString();
      }
    catch (Throwable t)
      {
        Debug.trace(t);
        return null;
      }
  }

  /** Gets the value associated with the symbol
   * as set by SYMBOL-VALUE.
   *
   * @return The associated value, or null if unbound.
   *
   * @see Symbol#symbolValue
   */
  @Override
  public LispObject getSymbolValue()
  {
    return value;
  }

  /** Sets the value associated with the symbol
   * as if set by SYMBOL-VALUE.
   *
   * @return The associated value, or null if unbound.
   *
   * @see Symbol#symbolValue
   */
  public final void setSymbolValue(LispObject value)
  {
    this.value = value;
  }

  /** Returns the value associated with this symbol in the current
   * thread context when it is treated as a special variable.
   *
   * A lisp error is thrown if the symbol is unbound.
   *
   * @return The associated value
   * @throws org.armedbear.lisp.ConditionThrowable
   *
   * @see LispThread#lookupSpecial
   * @see Symbol#getSymbolValue()
   *
   */
  public final LispObject symbolValue() throws ConditionThrowable
  {
    return symbolValue(LispThread.currentThread());
  }

  /** Returns the value associated with this symbol in the specified
   * thread context when it is treated as a special variable.
   *
   * A lisp error is thrown if the symbol is unbound.
   *
   * @return The associated value
   * @throws org.armedbear.lisp.ConditionThrowable
   *
   * @see LispThread#lookupSpecial
   * @see Symbol#getSymbolValue()
   *
   */
  public final LispObject symbolValue(LispThread thread) throws ConditionThrowable
  {
    LispObject val = thread.lookupSpecial(this);
    if (val != null)
      return val;
    if (value != null)
      return value;
    return error(new UnboundVariable(this));
  }

  /** Returns the value of the symbol in the current thread context;
   * if the symbol has been declared special, the value of the innermost
   * binding is returned. Otherwise, the SYMBOL-VALUE is returned, or
   * null if unbound.
   *
   * @return A lisp object, or null if unbound
   *
   * @see LispThread#lookupSpecial
   * @see Symbol#getSymbolValue()
   *
   */
  public final LispObject symbolValueNoThrow()
  {
    return symbolValueNoThrow(LispThread.currentThread());
  }

  /** Returns the value of the symbol in the current thread context;
   * if the symbol has been declared special, the value of the innermost
   * binding is returned. Otherwise, the SYMBOL-VALUE is returned, or
   * null if unbound.
   *
   * @return A lisp object, or null if unbound
   *
   * @see LispThread#lookupSpecial
   * @see Symbol#getSymbolValue()
   *
   */
  public final LispObject symbolValueNoThrow(LispThread thread)
  {
    if ((flags & FLAG_SPECIAL) != 0)
      {
        LispObject val = thread.lookupSpecial(this);
        if (val != null)
          return val;
      }
    return value;
  }

  @Override
  public LispObject getSymbolFunction()
  {
    return function;
  }

  @Override
  public final LispObject getSymbolFunctionOrDie() throws ConditionThrowable
  {
    if (function == null)
      return error(new UndefinedFunction(this));
    if (function instanceof Autoload)
      {
        Autoload autoload = (Autoload) function;
        autoload.load();
      }
    return function;
  }

  public final LispObject getSymbolSetfFunctionOrDie()
    throws ConditionThrowable
  {
    LispObject obj = Lisp.get(this, SETF_FUNCTION, null);
    if (obj == null)
      error(new UndefinedFunction(list(Keyword.NAME,
                                         list(SymbolConstants.SETF,
                                               this))));
    return obj;
  }

  public final void setSymbolFunction(LispObject obj)
  {
    this.function = obj;
  }

  @Override
  public final LispObject getPropertyList()
  {
    if (propertyList == null)
      propertyList = NIL;
    return propertyList;
  }

  @Override
  public final void setPropertyList(LispObject obj)
  {
    if (obj == null)
      throw new NullPointerException();
    propertyList = obj;
  }

  @Override
  public String writeToString() throws ConditionThrowable
  {
    final String n = name.getStringValue();
    final LispThread thread = LispThread.currentThread();
    boolean printEscape = (PRINT_ESCAPE.symbolValue(thread) != NIL);
    LispObject printCase = PRINT_CASE.symbolValue(thread);
    final LispObject readtableCase =
      ((Readtable)CURRENT_READTABLE.symbolValue(thread)).getReadtableCase();
    boolean printReadably = (PRINT_READABLY.symbolValue(thread) != NIL);
    if (printReadably)
      {
        if (readtableCase != Keyword.UPCASE ||
            printCase != Keyword.UPCASE)
          {
            FastStringBuffer sb = new FastStringBuffer();
            if (pkg == PACKAGE_KEYWORD)
              {
                sb.append(':');
              }
            else if (pkg instanceof LispPackage)
              {
                sb.append(multipleEscape(((LispPackage)pkg).getName()));
                sb.append("::");
              }
            else
              {
                sb.append("#:");
              }
            sb.append(multipleEscape(n));
            return sb.toString();
          }
        else
          printEscape = true;
      }
    if (!printEscape)
      {
        if (pkg == PACKAGE_KEYWORD)
          {
            if (printCase == Keyword.DOWNCASE)
              return n.toLowerCase();
            if (printCase == Keyword.CAPITALIZE)
              return capitalize(n, readtableCase);
            return n;
          }
        // Printer escaping is disabled.
        if (readtableCase == Keyword.UPCASE)
          {
            if (printCase == Keyword.DOWNCASE)
              return n.toLowerCase();
            if (printCase == Keyword.CAPITALIZE)
              return capitalize(n, readtableCase);
            return n;
          }
        else if (readtableCase == Keyword.DOWNCASE)
          {
            // "When the readtable case is :DOWNCASE, uppercase characters
            // are printed in their own case, and lowercase characters are
            // printed in the case specified by *PRINT-CASE*." (22.1.3.3.2)
            if (printCase == Keyword.DOWNCASE)
              return n;
            if (printCase == Keyword.UPCASE)
              return n.toUpperCase();
            if (printCase == Keyword.CAPITALIZE)
              return capitalize(n, readtableCase);
            return n;
          }
        else if (readtableCase == Keyword.PRESERVE)
          {
            return n;
          }
        else // INVERT
          return invert(n);
      }
    // Printer escaping is enabled.
    final boolean escapeSymbolName = needsEscape(n, readtableCase, thread);
    String symbolName = escapeSymbolName ? multipleEscape(n) : n;
    if (!escapeSymbolName)
      {
        if (readtableCase == Keyword.PRESERVE) { }
        else if (readtableCase == Keyword.INVERT)
          symbolName = invert(symbolName);
        else if (printCase == Keyword.DOWNCASE)
          symbolName = symbolName.toLowerCase();
        else if (printCase == Keyword.UPCASE)
          symbolName = symbolName.toUpperCase();
        else if (printCase == Keyword.CAPITALIZE)
          symbolName = capitalize(symbolName, readtableCase);
      }
    if (pkg == NIL)
      {
        if (printReadably || PRINT_GENSYM.symbolValue(thread) != NIL)
          return "#:".concat(symbolName);
        else
          return symbolName;
      }
    if (pkg == PACKAGE_KEYWORD)
      return ":".concat(symbolName);
    // "Package prefixes are printed if necessary." (22.1.3.3.1)
    final LispPackage currentPackage = (LispPackage) _PACKAGE_.symbolValue(thread);
    if (pkg == currentPackage)
      return symbolName;
    if (currentPackage != null && currentPackage.uses(pkg))
      {
        // Check for name conflict in current package.
        if (currentPackage.findExternalSymbol(name) == null)
          if (currentPackage.findInternalSymbol(name) == null)
            if (((LispPackage)pkg).findExternalSymbol(name) != null)
              return symbolName;
      }
    // Has this symbol been imported into the current package?
    if (currentPackage.findExternalSymbol(name) == this)
      return symbolName;
    if (currentPackage.findInternalSymbol(name) == this)
      return symbolName;
    // Package prefix is necessary.
    String packageName = ((LispPackage)pkg).getName();
    final boolean escapePackageName = needsEscape(packageName, readtableCase, thread);
    if (escapePackageName)
      {
        packageName = multipleEscape(packageName);
      }
    else
      {
        if (readtableCase == Keyword.UPCASE)
          {
            if (printCase == Keyword.DOWNCASE)
              packageName = packageName.toLowerCase();
            else if (printCase == Keyword.CAPITALIZE)
              packageName = capitalize(packageName, readtableCase);
          }
        else if (readtableCase == Keyword.DOWNCASE)
          {
            if (printCase == Keyword.UPCASE)
              packageName = packageName.toUpperCase();
            else if (printCase == Keyword.CAPITALIZE)
              packageName = capitalize(packageName, readtableCase);
          }
        else if (readtableCase == Keyword.INVERT)
          {
            packageName = invert(packageName);
          }
      }
    FastStringBuffer sb = new FastStringBuffer(packageName);
    if (((LispPackage)pkg).findExternalSymbol(name) != null)
      sb.append(':');
    else
      sb.append("::");
    sb.append(symbolName);
    return sb.toString();
  }

  private static final String invert(String s)
  {
    // "When the readtable case is :INVERT, the case of all alphabetic
    // characters in single case symbol names is inverted. Mixed-case
    // symbol names are printed as is." (22.1.3.3.2)
    final int limit = s.length();
    final int LOWER = 1;
    final int UPPER = 2;
    int state = 0;
    for (int i = 0; i < limit; i++)
      {
        char c = s.charAt(i);
        if (Character.isUpperCase(c))
          {
            if (state == LOWER)
              return s; // Mixed case.
            state = UPPER;
          }
        if (Character.isLowerCase(c))
          {
            if (state == UPPER)
              return s; // Mixed case.
            state = LOWER;
          }
      }
    FastStringBuffer sb = new FastStringBuffer(limit);
    for (int i = 0; i < limit; i++)
      {
        char c = s.charAt(i);
        if (Character.isUpperCase(c))
          sb.append(Character.toLowerCase(c));
        else if (Character.isLowerCase(c))
          sb.append(Character.toUpperCase(c));
        else
          sb.append(c);
      }
    return sb.toString();
  }

  private static final boolean needsEscape(String s,
                                           LispObject readtableCase,
                                           LispThread thread)
    throws ConditionThrowable
  {
    boolean escape = false;
    final int length = s.length();
    if (length == 0)
      return true;
    if (s.charAt(0) == '#')
      return true;
    int radix;
    LispObject printBaseBinding = PRINT_BASE.symbolValue(thread); 
    if (printBaseBinding  instanceof Fixnum)
      {
        radix = printBaseBinding.intValue();
      }
    else
      {
        error(new TypeError("The value of *PRINT-BASE* is not of type (INTEGER 2 36)."));
        // Not reached.
        return false;
      }
    if (radix < 2 || radix > 36)
      {
        error(new TypeError("The value of *PRINT-BASE* is not of type (INTEGER 2 36)."));
        // Not reached.
        return false;
      }
    boolean seenNonDigit = false;
    for (int i = length; i-- > 0;)
      {
        char c = s.charAt(i);
        if ("(),|\\`'\";:".indexOf(c) >= 0)
          return true;
        if (Character.isWhitespace(c))
          return true;
        if (readtableCase == Keyword.UPCASE)
          {
            if (Character.isLowerCase(c))
              return true;
          }
        else if (readtableCase == Keyword.DOWNCASE)
          {
            if (Character.isUpperCase(c))
              return true;
          }
        if (!escape && !seenNonDigit)
          {
            if (Character.digit(c, radix) < 0)
              seenNonDigit = true;
          }
      }
    if (!seenNonDigit)
      return true;
    if (s.charAt(0) == '.')
      {
        boolean allDots = true;
        for (int i = length; i-- > 1;)
          {
            if (s.charAt(i) != '.')
              {
                allDots = false;
                break;
              }
          }
        if (allDots)
          return true;
      }
    return false;
  }

  private static final String multipleEscape(String s)
  {
    FastStringBuffer sb = new FastStringBuffer("|");
    final int limit = s.length();
    for (int i = 0; i < limit; i++)
      {
        char c = s.charAt(i);
        if (c == '|' || c == '\\')
          sb.append('\\');
        sb.append(c);
      }
    sb.append('|');
    return sb.toString();
  }

  private static final String capitalize(String s, LispObject readtableCase)
  {
    if (readtableCase == Keyword.INVERT || readtableCase == Keyword.PRESERVE)
      return s;
    final int limit = s.length();
    FastStringBuffer sb = new FastStringBuffer(limit);
    boolean lastCharWasAlphanumeric = false;
    for (int i = 0; i < limit; i++)
      {
        char c = s.charAt(i);
        if (Character.isLowerCase(c))
          {
            if (readtableCase == Keyword.UPCASE)
              sb.append(c);
            else // DOWNCASE
              sb.append(lastCharWasAlphanumeric ? c : LispCharacter.toUpperCase(c));
            lastCharWasAlphanumeric = true;
          }
        else if (Character.isUpperCase(c))
          {
            if (readtableCase == Keyword.UPCASE)
              sb.append(lastCharWasAlphanumeric ? LispCharacter.toLowerCase(c) : c);
            else // DOWNCASE
              sb.append(c);
            lastCharWasAlphanumeric = true;
          }
        else
          {
            sb.append(c);
            lastCharWasAlphanumeric = Character.isDigit(c);
          }
      }
    return sb.toString();
  }

  @Override
  public final int sxhash()
  {
    int h = hash;
    if (h < 0)
      {
        h = name.sxhash();
        hash = h;
      }
    return h;
  }

  @Override
  final public LispObject execute() throws ConditionThrowable
  {
    LispObject fun;
    if ((fun = function) == null)
        return undefinedFunction(NIL);

    return fun.execute();
  }

  @Override
  final public LispObject execute(LispObject arg) throws ConditionThrowable
  {
    LispObject fun;
    if ((fun = function) == null)
        return undefinedFunction(list(arg));

    return fun.execute(arg);
  }

  @Override
  final public LispObject execute(LispObject first, LispObject second)
    throws ConditionThrowable
  {
    LispObject fun;
    if ((fun = function) == null)
        return undefinedFunction(list(first, second));

    return fun.execute(first, second);
  }

  @Override
  final public LispObject execute(LispObject first, LispObject second,
                            LispObject third)
    throws ConditionThrowable
  {
    LispObject fun;
    if ((fun = function) == null)
        return undefinedFunction(list(first, second, third));

    return fun.execute(first, second, third);
  }

  @Override
  final public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth)
    throws ConditionThrowable
  {
    LispObject fun;
    if ((fun = function) == null)
        return undefinedFunction(list(first, second, third, fourth));

    return fun.execute(first, second, third, fourth);
  }

  @Override
  final public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth,
                            LispObject fifth)
    throws ConditionThrowable
  {
    LispObject fun;
    if ((fun = function) == null)
        return undefinedFunction(list(first, second, third, fourth,
                                      fifth));

    return fun.execute(first, second, third, fourth,
                       fifth);
  }

  @Override
  final public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth,
                            LispObject fifth, LispObject sixth)
    throws ConditionThrowable
  {
    LispObject fun;
    if ((fun = function) == null)
        return undefinedFunction(list(first, second, third, fourth,
                                      fifth, sixth));

    return fun.execute(first, second, third, fourth,
                       fifth, sixth);
  }

  @Override
  final public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth,
                            LispObject fifth, LispObject sixth,
                            LispObject seventh)
    throws ConditionThrowable
  {
    LispObject fun;
    if ((fun = function) == null)
        return undefinedFunction(list(first, second, third, fourth,
                                      fifth, sixth, seventh));

    return fun.execute(first, second, third, fourth,
                       fifth, sixth, seventh);
  }

  @Override
  final public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth,
                            LispObject fifth, LispObject sixth,
                            LispObject seventh, LispObject eighth)
    throws ConditionThrowable
  {
    LispObject fun;
    if ((fun = function) == null)
        return undefinedFunction(list(first, second, third, fourth,
                                      fifth, sixth, seventh, eighth));

    return fun.execute(first, second, third, fourth,
                       fifth, sixth, seventh, eighth);
  }

  @Override
  final public LispObject execute(LispObject[] args) throws ConditionThrowable
  {
    LispObject fun;
    if ((fun = function) == null) {
        LispObject list = NIL;
        for (int i = args.length; i-- > 0;)
          list = makeCons(args[i], list);
        return undefinedFunction(list);
    }

    return fun.execute(args);
  }

  private final LispObject undefinedFunction(LispObject args)
    throws ConditionThrowable
  {
    return LispThread.currentThread().execute(SymbolConstants.UNDEFINED_FUNCTION_CALLED,
                                              this, args);
  }

  @Override
  public void incrementCallCount()
  {
    if (function != null)
      function.incrementCallCount();
  }

  @Override
  public void incrementHotCount()
  {
    if (function != null)
      function.incrementHotCount();
  }

public SimpleString getSymbolName() {
	// TODO Auto-generated method stub
	return name;
}
}
