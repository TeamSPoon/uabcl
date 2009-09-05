/*
 * Bignum.java
 *
 * Copyright (C) 2003-2007 Peter Graves
 * $Id: Bignum.java 11754 2009-04-12 10:53:39Z vvoutilainen $
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

import java.math.BigInteger;

public final class Bignum extends LispInteger
{
  private final BigInteger value;


  public final BigInteger bigIntegerValue()
  {
    return value;
  }
  
  public boolean isBignum() {
	return true;
  }
  
  private static BigInteger MOST_NEGATIVE_FIXNUM =
          BigInteger.valueOf(Integer.MIN_VALUE);
  private static BigInteger MOST_POSITIVE_FIXNUM =
          BigInteger.valueOf(Integer.MAX_VALUE);

  public static LispInteger getInstance(long l) {
      if (Integer.MIN_VALUE <= l && l <= Integer.MAX_VALUE)
          return getInstance(l);
      else
          return new Bignum(l);
  }

  public static LispInteger getInstance(BigInteger n) {
      if (MOST_NEGATIVE_FIXNUM.compareTo(n) < 0 ||
              MOST_POSITIVE_FIXNUM.compareTo(n) > 0)
          return new Bignum(n);
      else
          return Fixnum.getInstance(n.intValue());
  }

  public static LispInteger getInstance(String s, int radix) {
      BigInteger value = new BigInteger(s, radix);

      return Bignum.getInstance(value);
  }

  private Bignum(long l)
  {
    value = BigInteger.valueOf(l);
  }

  private Bignum(BigInteger n)
  {
    value = n;
  }

  @Override
  public Object javaInstance()
  {
    return bigIntegerValue();
  }

  @Override
  public Object javaInstance(Class c) {
    String cn = c.getName();
    if (cn.equals("java.lang.Byte") || cn.equals("byte"))
      return Byte.valueOf((byte)bigIntegerValue().intValue());
    if (cn.equals("java.lang.Short") || cn.equals("short"))
      return Short.valueOf((short)bigIntegerValue().intValue());
    if (cn.equals("java.lang.Integer") || cn.equals("int"))
      return Integer.valueOf(bigIntegerValue().intValue());
    if (cn.equals("java.lang.Long") || cn.equals("long"))
      return Long.valueOf((long)bigIntegerValue().longValue());
    return javaInstance();
  }


  @Override
  public LispObject typeOf()
  {
    if (bigIntegerValue().signum() > 0)
      return list(SymbolConstants.INTEGER,
                   new Bignum((long)Integer.MAX_VALUE + 1));
    return SymbolConstants.BIGNUM;
  }

  @Override
  public LispObject classOf()
  {
    return BuiltInClass.BIGNUM;
  }

  @Override
  public LispObject typep(LispObject type) throws ConditionThrowable
  {
    if (type instanceof Symbol)
      {
        if (type == SymbolConstants.BIGNUM)
          return T;
        if (type == SymbolConstants.INTEGER)
          return T;
        if (type == SymbolConstants.RATIONAL)
          return T;
        if (type == SymbolConstants.REAL)
          return T;
        if (type == SymbolConstants.NUMBER)
          return T;
        if (type == SymbolConstants.SIGNED_BYTE)
          return T;
        if (type == SymbolConstants.UNSIGNED_BYTE)
          return bigIntegerValue().signum() >= 0 ? T : NIL;
      }
    else if (type instanceof LispClass)
      {
        if (type == BuiltInClass.BIGNUM)
          return T;
        if (type == BuiltInClass.INTEGER)
          return T;
        if (type == BuiltInClass.RATIONAL)
          return T;
        if (type == BuiltInClass.REAL)
          return T;
        if (type == BuiltInClass.NUMBER)
          return T;
      }
    else if (type instanceof Cons)
      {
        if (type.equal(UNSIGNED_BYTE_8))
          return NIL;
        if (type.equal(UNSIGNED_BYTE_32))
          {
            if (isNegative())
              return NIL;
            return isLessThan(UNSIGNED_BYTE_32_MAX_VALUE) ? T : NIL;
          }
      }
    return super.typep(type);
  }

  @Override
  public LispObject NUMBERP()
  {
    return T;
  }

  @Override
  public boolean isNumber()
  {
    return true;
  }

  @Override
  public LispObject INTEGERP()
  {
    return T;
  }

  @Override
  public boolean isInteger()
  {
    return true;
  }

  @Override
  public boolean rationalp()
  {
    return true;
  }

  @Override
  public boolean realp()
  {
    return true;
  }

  @Override
  public boolean eql(LispObject obj)
  {
    if (this == obj)
      return true;
    if (obj .isBignum())
      {
        if (bigIntegerValue().equals(obj.bigIntegerValue()))
          return true;
      }
    return false;
  }

  @Override
  public boolean equal(LispObject obj)
  {
    if (this == obj)
      return true;
    if (obj .isBignum())
      {
        if (bigIntegerValue().equals(obj.bigIntegerValue()))
          return true;
      }
    return false;
  }

  @Override
  public boolean equalp(LispObject obj) throws ConditionThrowable
  {
    if (obj .isBignum())
      return bigIntegerValue().equals(obj.bigIntegerValue());
    if (obj instanceof SingleFloat)
      return floatValue() == ((SingleFloat)obj).floatValue();
    if (obj instanceof DoubleFloat)
      return doubleValue() == ((DoubleFloat)obj).doubleValue();
    return false;
  }

  @Override
  public LispObject ABS()
  {
    if (bigIntegerValue().signum() >= 0)
      return this;
    return new Bignum(bigIntegerValue().negate());
  }

  @Override
  public LispObject NUMERATOR()
  {
    return this;
  }

  @Override
  public LispObject DENOMINATOR()
  {
    return Fixnum.ONE;
  }

  @Override
  public boolean isEven() throws ConditionThrowable
  {
    return !bigIntegerValue().testBit(0);
  }

  @Override
  public boolean isOdd() throws ConditionThrowable
  {
    return bigIntegerValue().testBit(0);
  }

  @Override
  public boolean isPositive()
  {
    return bigIntegerValue().signum() > 0;
  }

  @Override
  public boolean isNegative()
  {
    return bigIntegerValue().signum() < 0;
  }

  @Override
  public boolean isZero()
  {
    return false;
  }

  @Override
  public int intValue()
  {
	  BigInteger bi =  bigIntegerValue();
    return bi.intValue();
  }

  @Override
  public long longValue()
  {
    return bigIntegerValue().longValue();
  }

  @Override
  public float floatValue() throws ConditionThrowable
  {
    float f = bigIntegerValue().floatValue();
    if (Float.isInfinite(f))
      error(new TypeError("The value " + writeToString() +
                           " is too large to be converted to a single float."));
    return f;
  }

  @Override
  public double doubleValue() throws ConditionThrowable
  {
    double d = bigIntegerValue().doubleValue();
    if (Double.isInfinite(d))
      error(new TypeError("The value " + writeToString() +
                           " is too large to be converted to a double float."));
    return d;
  }

  public static BigInteger getValue(LispObject obj) throws ConditionThrowable
  {
      return obj.bigIntegerValue();
          
//    if (obj .isBignum())
//      {
//        return obj.bigIntegerValue();
//      }
//        type_error(obj, SymbolConstants.BIGNUM);
//        // Not reached.
//        return null;
  }

  @Override
  public final LispObject incr()
  {
    return number(bigIntegerValue().add(BigInteger.ONE));
  }

  @Override
  public final LispObject decr()
  {
    return number(bigIntegerValue().subtract(BigInteger.ONE));
  }

  @Override
  public LispObject add(int n) throws ConditionThrowable
  {
    return number(bigIntegerValue().add(BigInteger.valueOf(n)));
  }

  @Override
  public LispObject add(LispObject obj) throws ConditionThrowable
  {
    if (obj .isFixnum())
      return number(bigIntegerValue().add(obj.bigIntegerValue()));
    if (obj .isBignum())
      return number(bigIntegerValue().add(obj.bigIntegerValue()));
    if (obj instanceof Ratio)
      {
        BigInteger numerator = ((Ratio)obj).numerator();
        BigInteger denominator = ((Ratio)obj).denominator();
        return number(bigIntegerValue().multiply(denominator).add(numerator),
                      denominator);
      }
    if (obj instanceof SingleFloat)
      return SingleFloat.createSingleFloat(floatValue() + ((SingleFloat)obj).floatValue());
    if (obj instanceof DoubleFloat)
      return DoubleFloat.createDoubleFloat(doubleValue() + ((DoubleFloat)obj).doubleValue());
    if (obj instanceof Complex)
      {
        Complex c = (Complex) obj;
        return Complex.getInstance(add(c.getRealPart()), c.getImaginaryPart());
      }
    return type_error(obj, SymbolConstants.NUMBER);
  }

  @Override
  public LispObject subtract(LispObject obj) throws ConditionThrowable
  {
    if (obj .isFixnum())
      return number(bigIntegerValue().subtract(obj.bigIntegerValue()));
    if (obj .isBignum())
      return number(bigIntegerValue().subtract(obj.bigIntegerValue()));
    if (obj instanceof Ratio)
      {
        BigInteger numerator = ((Ratio)obj).numerator();
        BigInteger denominator = ((Ratio)obj).denominator();
        return number(bigIntegerValue().multiply(denominator).subtract(numerator),
                      denominator);
      }
    if (obj instanceof SingleFloat)
      return SingleFloat.createSingleFloat(floatValue() - ((SingleFloat)obj).floatValue());
    if (obj instanceof DoubleFloat)
      return DoubleFloat.createDoubleFloat(doubleValue() - ((DoubleFloat)obj).doubleValue());
    if (obj instanceof Complex)
      {
        Complex c = (Complex) obj;
        return Complex.getInstance(subtract(c.getRealPart()),
                                   Fixnum.ZERO.subtract(c.getImaginaryPart()));
      }
    return type_error(obj, SymbolConstants.NUMBER);
  }

  @Override
  public LispObject multiplyBy(int n) throws ConditionThrowable
  {
    if (n == 0)
      return Fixnum.ZERO;
    if (n == 1)
      return this;
    return new Bignum(bigIntegerValue().multiply(BigInteger.valueOf(n)));
  }

  @Override
  public LispObject multiplyBy(LispObject obj) throws ConditionThrowable
  {
    if (obj .isFixnum())
      {
        int n = obj.intValue();
        if (n == 0)
          return Fixnum.ZERO;
        if (n == 1)
          return this;
        return new Bignum(bigIntegerValue().multiply(BigInteger.valueOf(n)));
      }
    if (obj .isBignum())
      return new Bignum(bigIntegerValue().multiply(obj.bigIntegerValue()));
    if (obj instanceof Ratio)
      {
        BigInteger n = ((Ratio)obj).numerator();
        return number(n.multiply(bigIntegerValue()), ((Ratio)obj).denominator());
      }
    if (obj instanceof SingleFloat)
      return SingleFloat.createSingleFloat(floatValue() * ((SingleFloat)obj).floatValue());
    if (obj instanceof DoubleFloat)
      return DoubleFloat.createDoubleFloat(doubleValue() * ((DoubleFloat)obj).doubleValue());
    if (obj instanceof Complex)
      {
        Complex c = (Complex) obj;
        return Complex.getInstance(multiplyBy(c.getRealPart()),
                                   multiplyBy(c.getImaginaryPart()));
      }
    return type_error(obj, SymbolConstants.NUMBER);
  }

  @Override
  public LispObject divideBy(LispObject obj) throws ConditionThrowable
  {
    if (obj .isFixnum())
      return number(bigIntegerValue(), obj.bigIntegerValue());
    if (obj .isBignum())
      return number(bigIntegerValue(), obj.bigIntegerValue());
    if (obj instanceof Ratio)
      {
        BigInteger d = ((Ratio)obj).denominator();
        return number(d.multiply(bigIntegerValue()), ((Ratio)obj).numerator());
      }
    if (obj instanceof SingleFloat)
      return SingleFloat.createSingleFloat(floatValue() / ((SingleFloat)obj).floatValue());
    if (obj instanceof DoubleFloat)
      return DoubleFloat.createDoubleFloat(doubleValue() / ((DoubleFloat)obj).doubleValue());
    if (obj instanceof Complex)
      {
        Complex c = (Complex) obj;
        LispObject realPart = c.getRealPart();
        LispObject imagPart = c.getImaginaryPart();
        LispObject denominator =
          realPart.multiplyBy(realPart).add(imagPart.multiplyBy(imagPart));
        return Complex.getInstance(multiplyBy(realPart).divideBy(denominator),
                                   Fixnum.ZERO.subtract(multiplyBy(imagPart).divideBy(denominator)));
      }
    return type_error(obj, SymbolConstants.NUMBER);
  }

  @Override
  public boolean isEqualTo(LispObject obj) throws ConditionThrowable
  {
    if (obj .isBignum())
      return bigIntegerValue().equals(obj.bigIntegerValue());
    if (obj instanceof SingleFloat)
      return isEqualTo(((SingleFloat)obj).rational());
    if (obj instanceof DoubleFloat)
      return isEqualTo(((DoubleFloat)obj).rational());
    if (obj.isNumber())
      return false;
    type_error(obj, SymbolConstants.NUMBER);
    // Not reached.
    return false;
  }

  @Override
  public boolean isNotEqualTo(LispObject obj) throws ConditionThrowable
  {
    if (obj .isBignum())
      return !bigIntegerValue().equals(obj.bigIntegerValue());
    if (obj instanceof SingleFloat)
      return isNotEqualTo(((SingleFloat)obj).rational());
    if (obj instanceof DoubleFloat)
      return isNotEqualTo(((DoubleFloat)obj).rational());
    if (obj.isNumber())
      return true;
    type_error(obj, SymbolConstants.NUMBER);
    // Not reached.
    return false;
  }

  @Override
  public boolean isLessThan(LispObject obj) throws ConditionThrowable
  {
    if (obj .isFixnum())
      return bigIntegerValue().compareTo(obj.bigIntegerValue()) < 0;
    if (obj .isBignum())
      return bigIntegerValue().compareTo(obj.bigIntegerValue()) < 0;
    if (obj instanceof Ratio)
      {
        BigInteger n = bigIntegerValue().multiply(((Ratio)obj).denominator());
        return n.compareTo(((Ratio)obj).numerator()) < 0;
      }
    if (obj instanceof SingleFloat)
      return isLessThan(((SingleFloat)obj).rational());
    if (obj instanceof DoubleFloat)
      return isLessThan(((DoubleFloat)obj).rational());
    type_error(obj, SymbolConstants.REAL);
    // Not reached.
    return false;
  }

  @Override
  public boolean isGreaterThan(LispObject obj) throws ConditionThrowable
  {
    if (obj .isFixnum())
      return bigIntegerValue().compareTo(obj.bigIntegerValue()) > 0;
    if (obj .isBignum())
      return bigIntegerValue().compareTo(obj.bigIntegerValue()) > 0;
    if (obj instanceof Ratio)
      {
        BigInteger n = bigIntegerValue().multiply(((Ratio)obj).denominator());
        return n.compareTo(((Ratio)obj).numerator()) > 0;
      }
    if (obj instanceof SingleFloat)
      return isGreaterThan(((SingleFloat)obj).rational());
    if (obj instanceof DoubleFloat)
      return isGreaterThan(((DoubleFloat)obj).rational());
    type_error(obj, SymbolConstants.REAL);
    // Not reached.
    return false;
  }

  @Override
  public boolean isLessThanOrEqualTo(LispObject obj) throws ConditionThrowable
  {
    if (obj .isFixnum())
      return bigIntegerValue().compareTo(obj.bigIntegerValue()) <= 0;
    if (obj .isBignum())
      return bigIntegerValue().compareTo(obj.bigIntegerValue()) <= 0;
    if (obj instanceof Ratio)
      {
        BigInteger n = bigIntegerValue().multiply(((Ratio)obj).denominator());
        return n.compareTo(((Ratio)obj).numerator()) <= 0;
      }
    if (obj instanceof SingleFloat)
      return isLessThanOrEqualTo(((SingleFloat)obj).rational());
    if (obj instanceof DoubleFloat)
      return isLessThanOrEqualTo(((DoubleFloat)obj).rational());
    type_error(obj, SymbolConstants.REAL);
    // Not reached.
    return false;
  }

  @Override
  public boolean isGreaterThanOrEqualTo(LispObject obj) throws ConditionThrowable
  {
    if (obj .isFixnum())
      return bigIntegerValue().compareTo(obj.bigIntegerValue()) >= 0;
    if (obj .isBignum())
      return bigIntegerValue().compareTo(obj.bigIntegerValue()) >= 0;
    if (obj instanceof Ratio)
      {
        BigInteger n = bigIntegerValue().multiply(((Ratio)obj).denominator());
        return n.compareTo(((Ratio)obj).numerator()) >= 0;
      }
    if (obj instanceof SingleFloat)
      return isGreaterThanOrEqualTo(((SingleFloat)obj).rational());
    if (obj instanceof DoubleFloat)
      return isGreaterThanOrEqualTo(((DoubleFloat)obj).rational());
    type_error(obj, SymbolConstants.REAL);
    // Not reached.
    return false;
  }

  @Override
  public LispObject truncate(LispObject obj) throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    LispObject value1, value2;
    try
      {
        if (obj .isFixnum())
          {
            BigInteger divisor = obj.bigIntegerValue();
            BigInteger[] results = bigIntegerValue().divideAndRemainder(divisor);
            BigInteger quotient = results[0];
            BigInteger remainder = results[1];
            value1 = number(quotient);
            value2 = (remainder.signum() == 0) ? Fixnum.ZERO : number(remainder);
          }
        else if (obj .isBignum())
          {
            BigInteger divisor = obj.bigIntegerValue();
            BigInteger[] results = bigIntegerValue().divideAndRemainder(divisor);
            BigInteger quotient = results[0];
            BigInteger remainder = results[1];
            value1 = number(quotient);
            value2 = (remainder.signum() == 0) ? Fixnum.ZERO : number(remainder);
          }
        else if (obj instanceof Ratio)
          {
            Ratio divisor = (Ratio) obj;
            LispObject quotient =
              multiplyBy(divisor.DENOMINATOR()).truncate(divisor.NUMERATOR());
            LispObject remainder =
              subtract(quotient.multiplyBy(divisor));
            value1 = quotient;
            value2 = remainder;
          }
        else if (obj instanceof SingleFloat)
          {
            // "When rationals and floats are combined by a numerical
            // function, the rational is first converted to a float of the
            // same format." 12.1.4.1
            return SingleFloat.createSingleFloat(floatValue()).truncate(obj);
          }
        else if (obj instanceof DoubleFloat)
          {
            // "When rationals and floats are combined by a numerical
            // function, the rational is first converted to a float of the
            // same format." 12.1.4.1
            return DoubleFloat.createDoubleFloat(doubleValue()).truncate(obj);
          }
        else
          return type_error(obj, SymbolConstants.REAL);
      }
    catch (ArithmeticException e)
      {
        if (obj.isZero())
          return error(new DivisionByZero());
        else
          return error(new ArithmeticError(e.getMessage()));
      }
    return thread.setValues(value1, value2);
  }

  @Override
  public LispObject ash(LispObject obj) throws ConditionThrowable
  {
    BigInteger n = bigIntegerValue();
    if (obj .isFixnum())
      {
        int count = obj.intValue();
        if (count == 0)
          return this;
        // BigInteger.shiftLeft() succumbs to a stack overflow if count
        // is Integer.MIN_VALUE, so...
        if (count == Integer.MIN_VALUE)
          return n.signum() >= 0 ? Fixnum.ZERO : Fixnum.MINUS_ONE;
        return number(n.shiftLeft(count));
      }
    if (obj .isBignum())
      {
        BigInteger count = obj.bigIntegerValue();
        if (count.signum() > 0)
          return error(new LispError("Can't represent result of left shift."));
        if (count.signum() < 0)
          return n.signum() >= 0 ? Fixnum.ZERO : Fixnum.MINUS_ONE;
        Debug.bug(); // Shouldn't happen.
      }
    return type_error(obj, SymbolConstants.INTEGER);
  }

  @Override
  public LispObject LOGNOT()
  {
    return number(bigIntegerValue().not());
  }

  @Override
  public LispObject LOGAND(int n) throws ConditionThrowable
  {
    if (n >= 0)
      return Fixnum.getInstance(bigIntegerValue().intValue() & n);
    else
      return number(bigIntegerValue().and(BigInteger.valueOf(n)));
  }

  @Override
  public LispObject LOGAND(LispObject obj) throws ConditionThrowable
  {
    if (obj .isFixnum())
      {
        int n = obj.intValue();
        if (n >= 0)
          return Fixnum.getInstance(bigIntegerValue().intValue() & n);
        else
          return number(bigIntegerValue().and(BigInteger.valueOf(n)));
      }
    else if (obj .isBignum())
      {
        final BigInteger n = obj.bigIntegerValue();
        return number(bigIntegerValue().and(n));
      }
    else
      return type_error(obj, SymbolConstants.INTEGER);
  }

  @Override
  public LispObject LOGIOR(int n) throws ConditionThrowable
  {
    return number(bigIntegerValue().or(BigInteger.valueOf(n)));
  }

  @Override
  public LispObject LOGIOR(LispObject obj) throws ConditionThrowable
  {
    if (obj .isFixnum())
      {
        final BigInteger n = obj.bigIntegerValue();
        return number(bigIntegerValue().or(n));
      }
    else if (obj .isBignum())
      {
        final BigInteger n = obj.bigIntegerValue();
        return number(bigIntegerValue().or(n));
      }
    else
      return type_error(obj, SymbolConstants.INTEGER);
  }

  @Override
  public LispObject LOGXOR(int n) throws ConditionThrowable
  {
    return number(bigIntegerValue().xor(BigInteger.valueOf(n)));
  }

  @Override
  public LispObject LOGXOR(LispObject obj) throws ConditionThrowable
  {
    final BigInteger n;
    if (obj .isInteger())
      n = obj.bigIntegerValue();
    else
      return type_error(obj, SymbolConstants.INTEGER);
    return number(bigIntegerValue().xor(n));
  }

  @Override
  public LispObject LDB(int size, int position)
  {
    BigInteger n = bigIntegerValue().shiftRight(position);
    BigInteger mask = BigInteger.ONE.shiftLeft(size).subtract(BigInteger.ONE);
    return number(n.and(mask));
  }

  @Override
  public int clHash()
  {
    return bigIntegerValue().hashCode();
  }

  @Override
  public String writeToString() throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    final int base = SymbolConstants.PRINT_BASE.symbolValue(thread).intValue();
    String s = bigIntegerValue().toString(base).toUpperCase();
    if (SymbolConstants.PRINT_RADIX.symbolValue(thread) != NIL)
      {
        StringBuffer sb = new StringBuffer();
        switch (base)
          {
          case 2:
            sb.append("#b");
            sb.append(s);
            break;
          case 8:
            sb.append("#o");
            sb.append(s);
            break;
          case 10:
            sb.append(s);
            sb.append('.');
            break;
          case 16:
            sb.append("#x");
            sb.append(s);
            break;
          default:
            sb.append('#');
            sb.append(String.valueOf(base));
            sb.append('r');
            sb.append(s);
            break;
          }
        s = sb.toString();
      }
    return s;
  }
}
