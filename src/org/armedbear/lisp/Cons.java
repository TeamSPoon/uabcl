/*
 * Cons.java
 *
 * Copyright (C) 2002-2005 Peter Graves
 * $Id: Cons.java 11754 2009-04-12 10:53:39Z vvoutilainen $
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
import static org.armedbear.lisp.Lisp.*;

public final class Cons extends AbstractLispObject
{
  // TODO -make private bytecode should use accessors 
  public LispObject car;
  public LispObject cdr;


  // TODO -make protected bytecode should use Lisp.makeCons 
 public Cons(LispObject car, LispObject cdr)
  {
    this.car = car;
    this.setCdr(cdr);
    ++count;
  }
 // TODO -make protected bytecode should use Lisp.makeCons 
  public Cons(LispObject car)
  {
    this.car = car;
    this.setCdr(NIL);
    ++count;
  }
  // TODO -make protected bytecode should use accessors 
  public Cons(String name, LispObject value)
  {
    this.car = new SimpleString(name);
    this.setCdr(value != null ? value : NULL_VALUE);
    ++count;
  }

  @Override
  public LispObject typeOf()
  {
    return SymbolConstants.CONS;
  }

  @Override
  public LispObject classOf()
  {
    return BuiltInClass.CONS;
  }

  @Override
  public LispObject typep(LispObject typeSpecifier) throws ConditionThrowable
  {
    if (typeSpecifier instanceof Symbol)
      {
        if (typeSpecifier == SymbolConstants.LIST)
          return T;
        if (typeSpecifier == SymbolConstants.CONS)
          return T;
        if (typeSpecifier == SymbolConstants.SEQUENCE)
          return T;
        if (typeSpecifier == T)
          return T;
      }
    else if (typeSpecifier instanceof BuiltInClass)
      {
        if (typeSpecifier == BuiltInClass.LIST)
          return T;
        if (typeSpecifier == BuiltInClass.CONS)
          return T;
        if (typeSpecifier == BuiltInClass.SEQUENCE)
          return T;
        if (typeSpecifier == BuiltInClass.CLASS_T)
          return T;
      }
    return NIL;
  }

  @Override
  public final boolean constantp()
  {
    if (car == SymbolConstants.QUOTE)
      {
        if (cdr instanceof Cons)
          if (((Cons)CDR()).CDR() == NIL)
            return true;
      }
    return false;
  }

  @Override
  public LispObject ATOM()
  {
    return NIL;
  }

  @Override
  public boolean atom()
  {
    return false;
  }

  @Override
  public final LispObject CAR()
  {
    return car;
  }

  @Override
  public final LispObject CDR()
  {
    return cdr;
  }

  @Override
  public final void setCar(LispObject obj)
  {
    car = obj;
  }

  @Override
  public LispObject RPLACA(LispObject obj) throws ConditionThrowable
  {
    car = obj;
    return this;
  }

  @Override
  public final void setCdr(LispObject obj)
  {
    cdr = obj;
  }

  @Override
  public LispObject RPLACD(LispObject obj) throws ConditionThrowable
  {
    setCdr(obj);
    return this;
  }

  @Override
  public final LispObject CADR() throws ConditionThrowable
  {
    return cdr.CAR();
  }

  @Override
  public final LispObject CDDR() throws ConditionThrowable
  {
    return cdr.CDR();
  }

  @Override
  public final LispObject CADDR() throws ConditionThrowable
  {
    return cdr.CADR();
  }

  @Override
  public LispObject nthcdr(int n) throws ConditionThrowable
  {
    if (n < 0)
      return type_error(Fixnum.makeFixnum(n),
                             list(SymbolConstants.INTEGER, Fixnum.ZERO));
    LispObject result = this;
    for (int i = n; i-- > 0;)
      {
        result = result.CDR();
        if (result == NIL)
          break;
      }
    return result;
  }

  @Override
  public final LispObject push(LispObject obj)
  {
    return makeCons(obj, this);
  }

  @Override
  public final int sxhash()
  {
    return computeHash(this, 4);
  }

  private static final int computeHash(LispObject obj, int depth)
  {
    if (obj instanceof Cons)
      {
        if (depth > 0)
          {
            int n1 = computeHash(((Cons)obj).car, depth - 1);
            int n2 = computeHash(((Cons)obj).CDR(), depth - 1);
            return n1 ^ n2;
          }
        else
          {
            // This number comes from SBCL, but since we're not really
            // using SBCL's SXHASH algorithm, it's probably not optimal.
            // But who knows?
            return 261835505;
          }
      }
    else
      return obj.sxhash();
  }

  @Override
  public final int psxhash() //throws ConditionThrowable
  {
    return computeEqualpHash(this, 4);
  }

  private static final int computeEqualpHash(LispObject obj, int depth)
  {
    if (obj instanceof Cons)
      {
        if (depth > 0)
          {
            int n1 = computeEqualpHash(((Cons)obj).car, depth - 1);
            int n2 = computeEqualpHash(((Cons)obj).CDR(), depth - 1);
            return n1 ^ n2;
          }
        else
          return 261835505; // See above.
      }
    else
      return obj.psxhash();
  }

  @Override
  public final boolean equal(LispObject obj) throws ConditionThrowable
  {
    if (this == obj)
      return true;
    if (obj instanceof Cons)
      {
        if (car.equal(((Cons)obj).car) && cdr.equal(((Cons)obj).CDR()))
          return true;
      }
    return false;
  }

  @Override
  public final boolean equalp(LispObject obj) throws ConditionThrowable
  {
    if (this == obj)
      return true;
    if (obj instanceof Cons)
      {
        if (car.equalp(((Cons)obj).car) && cdr.equalp(((Cons)obj).CDR()))
          return true;
      }
    return false;
  }

  @Override
  public final int size() throws ConditionThrowable
  {
    int length = 1;
    LispObject obj = cdr;
        while (obj != NIL)
          {
            ++length;
            if (obj instanceof Cons) {
                obj = ((Cons)obj).CDR();
            } else  type_error(obj, SymbolConstants.LIST);
          }      
    return length;
  }

  @Override
  public LispObject NTH(int index) throws ConditionThrowable
  {
    if (index < 0)
      type_error(Fixnum.makeFixnum(index), SymbolConstants.UNSIGNED_BYTE);
    int i = 0;
    LispObject obj = this;
    while (true)
      {
        if (i == index)
          return obj.CAR();
        obj = obj.CDR();
        if (obj == NIL)
          return NIL;
        ++i;
      }
  }

  @Override
  public LispObject NTH(LispObject arg) throws ConditionThrowable
  {
    int index;
    if (arg  instanceof Fixnum)
      {
        index = arg.intValue();
      }
    else
        {
        if (arg  instanceof Bignum)
          {
            // FIXME (when machines have enough memory for it to matter)
            if (arg.isNegative())
              return type_error(arg, SymbolConstants.UNSIGNED_BYTE);
            return NIL;
          }
        return type_error(arg, SymbolConstants.UNSIGNED_BYTE);
      }
    if (index < 0)
      type_error(arg, SymbolConstants.UNSIGNED_BYTE);
    int i = 0;
    LispObject obj = this;
    while (true)
      {
        if (i == index)
          return obj.CAR();
        obj = obj.CDR();
        if (obj == NIL)
          return NIL;
        ++i;
      }
  }

  @Override
  public LispObject elt(int index) throws ConditionThrowable
  {
    if (index < 0)
      type_error(Fixnum.makeFixnum(index), SymbolConstants.UNSIGNED_BYTE);
    int i = 0;
    Cons cons = this;
    while (true)
      {
        if (i == index)
          return cons.car;
        LispObject conscdr = cons.CDR();
        if (conscdr instanceof Cons)
          {
            cons = (Cons) conscdr;
          }
        else
          {
            if (conscdr == NIL)
              {
                // Index too large.
                type_error(Fixnum.makeFixnum(index),
                                list(SymbolConstants.INTEGER, Fixnum.ZERO,
                                      Fixnum.makeFixnum(size() - 1)));
              }
            else
              {
                // Dotted list.
                type_error(conscdr, SymbolConstants.LIST);
              }
            // Not reached.
            return NIL;
          }
        ++i;
      }
  }

  @Override
  public LispObject reverse() throws ConditionThrowable
  {
    Cons cons = this;
    LispObject result = makeCons(cons.car);
    while (cons.CDR() instanceof Cons)
      {
        cons = (Cons) cons.CDR();
        result = makeCons(cons.car, result);
      }
    if (cons.CDR() != NIL)
      return type_error(cons.CDR(), SymbolConstants.LIST);
    return result;
  }

  @Override
  public final LispObject nreverse() throws ConditionThrowable
  {
    if (cdr instanceof Cons)
      {
        Cons cons = (Cons) cdr;
        if (cons.CDR() instanceof Cons)
          {
            Cons cons1 = cons;
            LispObject list = NIL;
            do
              {
                Cons temp = (Cons) cons.CDR();
                cons.setCdr(list);
                list = cons;
                cons = temp;
              }
            while (cons.CDR() instanceof Cons);
            if (cons.CDR() != NIL)
              return type_error(cons.CDR(), SymbolConstants.LIST);
            setCdr(list);
            cons1.setCdr(cons);
          }
        else if (cons.CDR() != NIL)
          return type_error(cons.CDR(), SymbolConstants.LIST);
        LispObject temp = car;
        car = cons.car;
        cons.car = temp;
      }
    else if (cdr != NIL)
      return type_error(cdr, SymbolConstants.LIST);
    return this;
  }

  @Override
  public final boolean isList()
  {
    return true;
  }

  @Override
  public final LispObject LISTP()
  {
    return T;
  }

  @Override
  public final boolean endp()
  {
    return false;
  }

  @Override
  public final LispObject ENDP()
  {
    return NIL;
  }

  @Override
  public final LispObject[] copyToArray() throws ConditionThrowable
  {
    final int length = size();
    LispObject[] array = new LispObject[length];
    LispObject rest = this;
    for (int i = 0; i < length; i++)
      {
        array[i] = rest.CAR();
        rest = rest.CDR();
      }
    return array;
  }

  @Override
  public LispObject execute() throws ConditionThrowable
  {
    if (car == SymbolConstants.LAMBDA)
      {
        Closure closure = new Closure(this, new Environment());
        return closure.execute();
      }
    return signalExecutionError();
  }

  @Override
  public LispObject execute(LispObject arg) throws ConditionThrowable
  {
    if (car == SymbolConstants.LAMBDA)
      {
        Closure closure = new Closure(this, new Environment());
        return closure.execute(arg);
      }
    return signalExecutionError();
  }

  @Override
  public LispObject execute(LispObject first, LispObject second)
    throws ConditionThrowable
  {
    if (car == SymbolConstants.LAMBDA)
      {
        Closure closure = new Closure(this, new Environment());
        return closure.execute(first, second);
      }
    return signalExecutionError();
  }

  @Override
  public LispObject execute(LispObject first, LispObject second,
                            LispObject third)
    throws ConditionThrowable
  {
    if (car == SymbolConstants.LAMBDA)
      {
        Closure closure = new Closure(this, new Environment());
        return closure.execute(first, second, third);
      }
    return signalExecutionError();
  }

  @Override
  public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth)
    throws ConditionThrowable
  {
    if (car == SymbolConstants.LAMBDA)
      {
        Closure closure = new Closure(this, new Environment());
        return closure.execute(first, second, third, fourth);
      }
    return signalExecutionError();
  }

  @Override
  public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth,
                            LispObject fifth)
    throws ConditionThrowable
  {
    if (car == SymbolConstants.LAMBDA)
      {
        Closure closure = new Closure(this, new Environment());
        return closure.execute(first, second, third, fourth, fifth);
      }
    return signalExecutionError();
  }

  @Override
  public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth,
                            LispObject fifth, LispObject sixth)
    throws ConditionThrowable
  {
    if (car == SymbolConstants.LAMBDA)
      {
        Closure closure = new Closure(this, new Environment());
        return closure.execute(first, second, third, fourth, fifth, sixth);
      }
    return signalExecutionError();
  }

  @Override
  public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth,
                            LispObject fifth, LispObject sixth,
                            LispObject seventh)
    throws ConditionThrowable
  {
    if (car == SymbolConstants.LAMBDA)
      {
        Closure closure = new Closure(this, new Environment());
        return closure.execute(first, second, third, fourth, fifth, sixth,
                               seventh);
      }
    return signalExecutionError();
  }

  @Override
  public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth,
                            LispObject fifth, LispObject sixth,
                            LispObject seventh, LispObject eighth)
    throws ConditionThrowable
  {
    if (car == SymbolConstants.LAMBDA)
      {
        Closure closure = new Closure(this, new Environment());
        return closure.execute(first, second, third, fourth, fifth, sixth,
                               seventh, eighth);
      }
    return signalExecutionError();
  }

  @Override
  public LispObject execute(LispObject[] args) throws ConditionThrowable
  {
    if (car == SymbolConstants.LAMBDA)
      {
        Closure closure = new Closure(this, new Environment());
        return closure.execute(args);
      }
    return signalExecutionError();
  }

  private final LispObject signalExecutionError() throws ConditionThrowable
  {
    return type_error(this, list(SymbolConstants.OR, SymbolConstants.FUNCTION,
                                       SymbolConstants.SYMBOL));
  }

  @Override
  public String writeToString() throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    final LispObject printLength = SymbolConstants.PRINT_LENGTH.symbolValue(thread);
    final int maxLength;
    if (printLength  instanceof Fixnum)
      maxLength = printLength.intValue();
    else
      maxLength = Integer.MAX_VALUE;
    final LispObject printLevel = SymbolConstants.PRINT_LEVEL.symbolValue(thread);
    final int maxLevel;
    if (printLevel  instanceof Fixnum)
      maxLevel = printLevel.intValue();
    else
      maxLevel = Integer.MAX_VALUE;
    FastStringBuffer sb = new FastStringBuffer();
    if (car == SymbolConstants.QUOTE)
      {
        if (cdr instanceof Cons)
          {
            // Not a dotted list.
            if (cdr.CDR() == NIL)
              {
                sb.append('\'');
                sb.append(cdr.CAR().writeToString());
                return sb.toString();
              }
          }
      }
    if (car == SymbolConstants.FUNCTION)
      {
        if (cdr instanceof Cons)
          {
            // Not a dotted list.
            if (cdr.CDR() == NIL)
              {
                sb.append("#'");
                sb.append(cdr.CAR().writeToString());
                return sb.toString();
              }
          }
      }
    LispObject currentPrintLevel =
      _CURRENT_PRINT_LEVEL_.symbolValue(thread);
    int currentLevel = currentPrintLevel.intValue();
    if (currentLevel < maxLevel)
      {
        SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
        thread.bindSpecial(_CURRENT_PRINT_LEVEL_, currentPrintLevel.incr());
        try
          {
            int count = 0;
            boolean truncated = false;
            sb.append('(');
            if (count < maxLength)
              {
                LispObject p = this;
                sb.append(p.CAR().writeToString());
                ++count;
                while ((p = p.CDR()) instanceof Cons)
                  {
                    sb.append(' ');
                    if (count < maxLength)
                      {
                        sb.append(p.CAR().writeToString());
                        ++count;
                      }
                    else
                      {
                        truncated = true;
                        break;
                      }
                  }
                if (!truncated && p != NIL)
                  {
                    sb.append(" . ");
                    sb.append(p.writeToString());
                  }
              }
            else
              truncated = true;
            if (truncated)
              sb.append("...");
            sb.append(')');
          }
        finally
          {
            thread.lastSpecialBinding = lastSpecialBinding;
          }
      }
    else
      sb.append('#');
    return sb.toString();
  }

  // Statistics for TIME.
  private static long count;

  /*package*/ static long getCount()
  {
    return count;
  }

  /*package*/ static void setCount(long n)
  {
    count = n;
  }
}
