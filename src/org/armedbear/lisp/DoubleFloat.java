/*
 * DoubleFloat.java
 *
 * Copyright (C) 2003-2007 Peter Graves
 * $Id: DoubleFloat.java 12018 2009-06-16 20:46:06Z ehuelsmann $
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
import static org.armedbear.lisp.Lisp.*;

import java.math.BigInteger;

public final class DoubleFloat extends NumericLispObject
{
    public static final DoubleFloat ZERO       = new DoubleFloat(0);
    public static final DoubleFloat MINUS_ZERO = new DoubleFloat(-0.0d);
    public static final DoubleFloat ONE        = new DoubleFloat(1);
    public static final DoubleFloat MINUS_ONE  = new DoubleFloat(-1);

    public static final DoubleFloat DOUBLE_FLOAT_POSITIVE_INFINITY =
        new DoubleFloat(Double.POSITIVE_INFINITY);

    public static final DoubleFloat DOUBLE_FLOAT_NEGATIVE_INFINITY =
        new DoubleFloat(Double.NEGATIVE_INFINITY);

    static {
        SymbolConstants.DOUBLE_FLOAT_POSITIVE_INFINITY.initializeConstant(DOUBLE_FLOAT_POSITIVE_INFINITY);
        SymbolConstants.DOUBLE_FLOAT_NEGATIVE_INFINITY.initializeConstant(DOUBLE_FLOAT_NEGATIVE_INFINITY);
    }

    public static DoubleFloat getInstance(double d) {
        if (d == 0)
            return ZERO;
        else if (d == -0.0d )
            return MINUS_ZERO;
        else if (d == 1)
            return ONE;
        else if (d == -1)
            return MINUS_ONE;
        else
            return new DoubleFloat(d);
    }

    public final double value;

    public DoubleFloat(double value)
    {
        this.value = value;
    }

    @Override
    public LispObject typeOf()
    {
        return SymbolConstants.DOUBLE_FLOAT;
    }

    @Override
    public LispObject classOf()
    {
        return BuiltInClass.DOUBLE_FLOAT;
    }

    @Override
    public LispObject typep(LispObject typeSpecifier) throws ConditionThrowable
    {
        if (typeSpecifier == SymbolConstants.FLOAT)
            return T;
        if (typeSpecifier == SymbolConstants.REAL)
            return T;
        if (typeSpecifier == SymbolConstants.NUMBER)
            return T;
        if (typeSpecifier == SymbolConstants.DOUBLE_FLOAT)
            return T;
        if (typeSpecifier == SymbolConstants.LONG_FLOAT)
            return T;
        if (typeSpecifier == BuiltInClass.FLOAT)
            return T;
        if (typeSpecifier == BuiltInClass.DOUBLE_FLOAT)
            return T;
        return super.typep(typeSpecifier);
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
    public boolean realp()
    {
        return true;
    }

    @Override
    public boolean eql(LispObject obj)
    {
        if (this == obj)
            return true;
        if (obj instanceof DoubleFloat) {
            if (value == 0) {
                // "If an implementation supports positive and negative zeros
                // as distinct values, then (EQL 0.0 -0.0) returns false."
                double d = ((DoubleFloat)obj).value;
                long bits = Double.doubleToRawLongBits(d);
                return bits == Double.doubleToRawLongBits(value);
            }
            if (value == ((DoubleFloat)obj).value)
                return true;
        }
        return false;
    }

    @Override
    public boolean equal(LispObject obj)
    {
        if (this == obj)
            return true;
        if (obj instanceof DoubleFloat) {
            if (value == 0) {
                // same as EQL
                double d = ((DoubleFloat)obj).value;
                long bits = Double.doubleToRawLongBits(d);
                return bits == Double.doubleToRawLongBits(value);
            }
            if (value == ((DoubleFloat)obj).value)
                return true;
        }
        return false;
    }

    @Override
    public boolean equalp(int n)
    {
        // "If two numbers are the same under =."
        return value == n;
    }

    @Override
    public boolean equalp(LispObject obj) throws ConditionThrowable
    {
        if (obj instanceof SingleFloat)
            return value == ((SingleFloat)obj).value;
        if (obj instanceof DoubleFloat)
            return value == ((DoubleFloat)obj).value;
        if (obj .isFixnum())
            return value == obj.intValue();
        if (obj .isBignum())
            return value == ((Bignum)obj).doubleValue();
        if (obj instanceof Ratio)
            return value == ((Ratio)obj).doubleValue();
        return false;
    }

    @Override
    public LispObject ABS()
    {
        if (value > 0)
            return this;
        if (value == 0) // 0.0 or -0.0
            return ZERO;
        return new DoubleFloat(- value);
    }

    @Override
    public boolean isPositive()
    {
        return value > 0;
    }

    @Override
    public boolean isNegative()
    {
        return value < 0;
    }

    @Override
    public boolean isZero()
    {
        return value == 0;
    }

    @Override
    public LispObject FLOATP()
    {
        return T;
    }

    @Override
    public boolean floatp()
    {
        return true;
    }

    public static double getValue(LispObject obj) throws ConditionThrowable
    {
        if (obj instanceof DoubleFloat) 
            return ((DoubleFloat)obj).value;
            type_error(obj, SymbolConstants.FLOAT);
            // Not reached.
            return 0;
    }

    public final double getValue()
    {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public Object javaInstance()
    {
        return Double.valueOf(value);
    }

    @Override
    public Object javaInstance(Class c)
    {
        String cn = c.getName();
        if (cn.equals("java.lang.Float") || cn.equals("float"))
            return Float.valueOf((float)value);
        return javaInstance();
    }

    @Override
    public final LispObject incr()
    {
        return new DoubleFloat(value + 1);
    }

    @Override
    public final LispObject decr()
    {
        return new DoubleFloat(value - 1);
    }

    @Override
    public LispObject negate()
    {
        if (value == 0) {
            long bits = Double.doubleToRawLongBits(value);
            return (bits < 0) ? ZERO : MINUS_ZERO;
        }
        return new DoubleFloat(-value);
    }

    @Override
    public LispObject add(LispObject obj) throws ConditionThrowable
    {
        if (obj .isFixnum())
            return new DoubleFloat(value + obj.intValue());
        if (obj instanceof SingleFloat)
            return new DoubleFloat(value + ((SingleFloat)obj).value);
        if (obj instanceof DoubleFloat)
            return new DoubleFloat(value + ((DoubleFloat)obj).value);
        if (obj .isBignum())
            return new DoubleFloat(value + ((Bignum)obj).doubleValue());
        if (obj instanceof Ratio)
            return new DoubleFloat(value + ((Ratio)obj).doubleValue());
        if (obj instanceof Complex) {
            Complex c = (Complex) obj;
            return Complex.getInstance(add(c.getRealPart()), c.getImaginaryPart());
        }
        return type_error(obj, SymbolConstants.NUMBER);
    }

    @Override
    public LispObject subtract(LispObject obj) throws ConditionThrowable
    {
        if (obj .isFixnum())
            return new DoubleFloat(value - obj.intValue());
        if (obj instanceof SingleFloat)
            return new DoubleFloat(value - ((SingleFloat)obj).value);
        if (obj instanceof DoubleFloat)
            return new DoubleFloat(value - ((DoubleFloat)obj).value);
        if (obj .isBignum())
            return new DoubleFloat(value - ((Bignum)obj).doubleValue());
        if (obj instanceof Ratio)
            return new DoubleFloat(value - ((Ratio)obj).doubleValue());
        if (obj instanceof Complex) {
            Complex c = (Complex) obj;
            return Complex.getInstance(subtract(c.getRealPart()),
                                       ZERO.subtract(c.getImaginaryPart()));
        }
        return type_error(obj, SymbolConstants.NUMBER);
    }

    @Override
    public LispObject multiplyBy(LispObject obj) throws ConditionThrowable
    {
        if (obj .isFixnum())
            return new DoubleFloat(value * obj.intValue());
        if (obj instanceof SingleFloat)
            return new DoubleFloat(value * ((SingleFloat)obj).value);
        if (obj instanceof DoubleFloat)
            return new DoubleFloat(value * ((DoubleFloat)obj).value);
        if (obj .isBignum())
            return new DoubleFloat(value * ((Bignum)obj).doubleValue());
        if (obj instanceof Ratio)
            return new DoubleFloat(value * ((Ratio)obj).doubleValue());
        if (obj instanceof Complex) {
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
            return new DoubleFloat(value / obj.intValue());
        if (obj instanceof SingleFloat)
            return new DoubleFloat(value / ((SingleFloat)obj).value);
        if (obj instanceof DoubleFloat)
            return new DoubleFloat(value / ((DoubleFloat)obj).value);
        if (obj .isBignum())
            return new DoubleFloat(value / ((Bignum)obj).doubleValue());
        if (obj instanceof Ratio)
            return new DoubleFloat(value / ((Ratio)obj).doubleValue());
        if (obj instanceof Complex) {
            Complex c = (Complex) obj;
            LispObject re = c.getRealPart();
            LispObject im = c.getImaginaryPart();
            LispObject denom = re.multiplyBy(re).add(im.multiplyBy(im));
            LispObject resX = multiplyBy(re).divideBy(denom);
            LispObject resY =
                multiplyBy(Fixnum.MINUS_ONE).multiplyBy(im).divideBy(denom);
            return Complex.getInstance(resX, resY);
        }
        return type_error(obj, SymbolConstants.NUMBER);
    }

    @Override
    public boolean isEqualTo(LispObject obj) throws ConditionThrowable
    {
        if (obj .isFixnum())
            return value == obj.intValue();
        if (obj instanceof SingleFloat)
            return value == ((SingleFloat)obj).value;
        if (obj instanceof DoubleFloat)
            return value == ((DoubleFloat)obj).value;
        if (obj .isBignum())
            return rational().isEqualTo(obj);
        if (obj instanceof Ratio)
            return rational().isEqualTo(obj);
        if (obj instanceof Complex)
            return obj.isEqualTo(this);
        type_error(obj, SymbolConstants.NUMBER);
        // Not reached.
        return false;
    }

    @Override
    public boolean isNotEqualTo(LispObject obj) throws ConditionThrowable
    {
        return !isEqualTo(obj);
    }

    @Override
    public boolean isLessThan(LispObject obj) throws ConditionThrowable
    {
        if (obj .isFixnum())
            return value < obj.intValue();
        if (obj instanceof SingleFloat)
            return value < ((SingleFloat)obj).value;
        if (obj instanceof DoubleFloat)
            return value < ((DoubleFloat)obj).value;
        if (obj .isBignum())
            return rational().isLessThan(obj);
        if (obj instanceof Ratio)
            return rational().isLessThan(obj);
        type_error(obj, SymbolConstants.REAL);
        // Not reached.
        return false;
    }

    @Override
    public boolean isGreaterThan(LispObject obj) throws ConditionThrowable
    {
        if (obj .isFixnum())
            return value > obj.intValue();
        if (obj instanceof SingleFloat)
            return value > ((SingleFloat)obj).value;
        if (obj instanceof DoubleFloat)
            return value > ((DoubleFloat)obj).value;
        if (obj .isBignum())
            return rational().isGreaterThan(obj);
        if (obj instanceof Ratio)
            return rational().isGreaterThan(obj);
        type_error(obj, SymbolConstants.REAL);
        // Not reached.
        return false;
    }

    @Override
    public boolean isLessThanOrEqualTo(LispObject obj) throws ConditionThrowable
    {
        if (obj .isFixnum())
            return value <= obj.intValue();
        if (obj instanceof SingleFloat)
            return value <= ((SingleFloat)obj).value;
        if (obj instanceof DoubleFloat)
            return value <= ((DoubleFloat)obj).value;
        if (obj .isBignum())
            return rational().isLessThanOrEqualTo(obj);
        if (obj instanceof Ratio)
            return rational().isLessThanOrEqualTo(obj);
        type_error(obj, SymbolConstants.REAL);
        // Not reached.
        return false;
    }

    @Override
    public boolean isGreaterThanOrEqualTo(LispObject obj) throws ConditionThrowable
    {
        if (obj .isFixnum())
            return value >= obj.intValue();
        if (obj instanceof SingleFloat)
            return value >= ((SingleFloat)obj).value;
        if (obj instanceof DoubleFloat)
            return value >= ((DoubleFloat)obj).value;
        if (obj .isBignum())
            return rational().isGreaterThanOrEqualTo(obj);
        if (obj instanceof Ratio)
            return rational().isGreaterThanOrEqualTo(obj);
        type_error(obj, SymbolConstants.REAL);
        // Not reached.
        return false;
    }

    @Override
    public LispObject truncate(LispObject obj) throws ConditionThrowable
    {
        // "When rationals and floats are combined by a numerical function,
        // the rational is first converted to a float of the same format."
        // 12.1.4.1
        if (obj .isFixnum()) {
            return truncate(new DoubleFloat(obj.intValue()));
        }
        if (obj .isBignum()) {
            return truncate(new DoubleFloat(((Bignum)obj).doubleValue()));
        }
        if (obj instanceof Ratio) {
            return truncate(new DoubleFloat(((Ratio)obj).doubleValue()));
        }
        if (obj instanceof SingleFloat) {
            final LispThread thread = LispThread.currentThread();
            double divisor = ((SingleFloat)obj).value;
            double quotient = value / divisor;
            if (value != 0)
                MathFunctions.OverUnderFlowCheck(quotient);
            if (quotient >= Integer.MIN_VALUE && quotient <= Integer.MAX_VALUE) {
                int q = (int) quotient;
                return thread.setValues(Fixnum.getInstance(q),
                                        new DoubleFloat(value - q * divisor));
            }
            // We need to convert the quotient to a bignum.
            long bits = Double.doubleToRawLongBits((double)quotient);
            int s = ((bits >> 63) == 0) ? 1 : -1;
            int e = (int) ((bits >> 52) & 0x7ffL);
            long m;
            if (e == 0)
                m = (bits & 0xfffffffffffffL) << 1;
            else
                m = (bits & 0xfffffffffffffL) | 0x10000000000000L;
            LispObject significand = number(m);
            Fixnum exponent = Fixnum.getInstance(e - 1075);
            Fixnum sign = Fixnum.getInstance(s);
            LispObject result = significand;
            result =
                result.multiplyBy(MathFunctions.EXPT.execute(Fixnum.TWO, exponent));
            result = result.multiplyBy(sign);
            // Calculate remainder.
            LispObject product = result.multiplyBy(obj);
            LispObject remainder = subtract(product);
            return thread.setValues(result, remainder);
        }
        if (obj instanceof DoubleFloat) {
//             Debug.trace("value = " + value);
            final LispThread thread = LispThread.currentThread();
            double divisor = ((DoubleFloat)obj).value;
//             Debug.trace("divisor = " + divisor);
            double quotient = value / divisor;
            if (value != 0)
                MathFunctions.OverUnderFlowCheck(quotient);
//             Debug.trace("quotient = " + quotient);
            if (quotient >= Integer.MIN_VALUE && quotient <= Integer.MAX_VALUE) {
                int q = (int) quotient;
                return thread.setValues(Fixnum.getInstance(q),
                                        new DoubleFloat(value - q * divisor));
            }
            // We need to convert the quotient to a bignum.
            long bits = Double.doubleToRawLongBits((double)quotient);
            int s = ((bits >> 63) == 0) ? 1 : -1;
            int e = (int) ((bits >> 52) & 0x7ffL);
            long m;
            if (e == 0)
                m = (bits & 0xfffffffffffffL) << 1;
            else
                m = (bits & 0xfffffffffffffL) | 0x10000000000000L;
            LispObject significand = number(m);
//             Debug.trace("significand = " + significand.writeToString());
            Fixnum exponent = Fixnum.getInstance(e - 1075);
//             Debug.trace("exponent = " + exponent.writeToString());
            Fixnum sign = Fixnum.getInstance(s);
//             Debug.trace("sign = " + sign.writeToString());
            LispObject result = significand;
//             Debug.trace("result = " + result.writeToString());
            result =
                result.multiplyBy(MathFunctions.EXPT.execute(Fixnum.TWO, exponent));
//             Debug.trace("result = " + result.writeToString());


            result = result.truncate(Fixnum.ONE);
            LispObject remainder = coerceToFloat(thread._values[1]);

            result = result.multiplyBy(sign);
//             Debug.trace("result = " + result.writeToString());
//             // Calculate remainder.
//             LispObject product = result.multiplyBy(obj);
//             Debug.trace("product = " + product.writeToString());
//             LispObject remainder = subtract(product);
            return thread.setValues(result, remainder);
        }
        return type_error(obj, SymbolConstants.REAL);
    }

    @Override
    public int clHash()
    {
        long bits = Double.doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
    }

    @Override
    public int psxhash()
    {
        if ((value % 1) == 0)
            return (((int)value) & 0x7fffffff);
        else
            return (clHash() & 0x7fffffff);
    }

    @Override
    public String writeToString() throws ConditionThrowable
    {
        if (value == Double.POSITIVE_INFINITY) {
            FastStringBuffer sb = new FastStringBuffer("#.");
            sb.append(SymbolConstants.DOUBLE_FLOAT_POSITIVE_INFINITY.writeToString());
            return sb.toString();
        }
        if (value == Double.NEGATIVE_INFINITY) {
            FastStringBuffer sb = new FastStringBuffer("#.");
            sb.append(SymbolConstants.DOUBLE_FLOAT_NEGATIVE_INFINITY.writeToString());
            return sb.toString();
        }

        LispThread thread = LispThread.currentThread();
        boolean printReadably = SymbolConstants.PRINT_READABLY.symbolValue(thread) != NIL;

        if (value != value) {
            if (printReadably)
                return "#.(progn \"Comment: create a NaN.\" (/ 0.0d0 0.0d0))";
            else
                return "#<DOUBLE-FLOAT NaN>";
        }
        String s1 = String.valueOf(value);
        if (printReadably ||
            !memq(SymbolConstants.READ_DEFAULT_FLOAT_FORMAT.symbolValue(thread),
                  list(SymbolConstants.DOUBLE_FLOAT, SymbolConstants.LONG_FLOAT)))
        {
            if (s1.indexOf('E') >= 0)
                return s1.replace('E', 'd');
            else
                return s1.concat("d0");
        } else
            return s1;
    }

    public LispObject rational() throws ConditionThrowable
    {
        final long bits = Double.doubleToRawLongBits(value);
        int sign = ((bits >> 63) == 0) ? 1 : -1;
        int storedExponent = (int) ((bits >> 52) & 0x7ffL);
        long mantissa;
        if (storedExponent == 0)
            mantissa = (bits & 0xfffffffffffffL) << 1;
        else
            mantissa = (bits & 0xfffffffffffffL) | 0x10000000000000L;
        if (mantissa == 0)
            return Fixnum.ZERO;
        if (sign < 0)
            mantissa = -mantissa;
        // Subtract bias.
        final int exponent = storedExponent - 1023;
        BigInteger numerator, denominator;
        if (exponent < 0) {
            numerator = BigInteger.valueOf(mantissa);
            denominator = BigInteger.valueOf(1).shiftLeft(52 - exponent);
        } else {
            numerator = BigInteger.valueOf(mantissa).shiftLeft(exponent);
            denominator = BigInteger.valueOf(0x10000000000000L); // (ash 1 52)
        }
        return number(numerator, denominator);
    }

    public static DoubleFloat coerceToFloat(LispObject obj) throws ConditionThrowable
    {
        if (obj instanceof DoubleFloat)
            return (DoubleFloat) obj;
        if (obj .isFixnum())
            return new DoubleFloat(obj.intValue());
        if (obj .isBignum())
            return new DoubleFloat(((Bignum)obj).doubleValue());
        if (obj instanceof SingleFloat)
            return new DoubleFloat(((SingleFloat)obj).value);
        if (obj instanceof Ratio)
            return new DoubleFloat(((Ratio)obj).doubleValue());
        error(new TypeError("The value " + obj.writeToString() +
                             " cannot be converted to type DOUBLE-FLOAT."));
        // Not reached.
        return null;
    }
}
