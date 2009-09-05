/*
 * MathFunctions.java
 *
 * Copyright (C) 2004-2006 Peter Graves
 * $Id: MathFunctions.java 11955 2009-05-26 18:59:27Z ehuelsmann $
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

public final class MathFunctions extends LispFile
{
    // ### sin
    private static final Primitive SIN = new Primitive("sin", "radians")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return sin(arg);
        }
    };

  /*private*/ static LispObject sin(LispObject arg) throws ConditionThrowable
    {
        if (arg .isDoubleFloat())
            return DoubleFloat.createDoubleFloat(Math.sin(arg.doubleValue()));
        if (arg.realp())
            return SingleFloat.createSingleFloat((float)Math.sin(SingleFloat.coerceToFloat(arg).floatValue()));
        if (arg instanceof Complex) {
            LispObject n = arg.multiplyBy(Complex.getInstance(Fixnum.ZERO,
                                                              Fixnum.ONE));
            LispObject result = exp(n);
            result = result.subtract(exp(n.multiplyBy(Fixnum.MINUS_ONE)));
            return result.divideBy(Fixnum.TWO.multiplyBy(Complex.getInstance(Fixnum.ZERO,
                                                                             Fixnum.ONE)));
        }
        return type_error(arg, SymbolConstants.NUMBER);
    }

    // ### cos
    private static final Primitive COS = new Primitive("cos", "radians")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return cos(arg);
        }
    };

  /*private*/ static LispObject cos(LispObject arg) throws ConditionThrowable
    {
        if (arg .isDoubleFloat())
            return DoubleFloat.createDoubleFloat(Math.cos(arg.doubleValue()));
        if (arg.realp())
            return SingleFloat.createSingleFloat((float)Math.cos(SingleFloat.coerceToFloat(arg).floatValue()));
        if (arg instanceof Complex) {
            LispObject n = arg.multiplyBy(Complex.getInstance(Fixnum.ZERO,
                                                              Fixnum.ONE));
            LispObject result = exp(n);
            result = result.add(exp(n.multiplyBy(Fixnum.MINUS_ONE)));
            return result.divideBy(Fixnum.TWO);
        }
        return type_error(arg, SymbolConstants.NUMBER);
    }

    // ### tan
    private static final Primitive TAN = new Primitive("tan", "radians")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            if (arg .isDoubleFloat())
                return DoubleFloat.createDoubleFloat(Math.tan(arg.doubleValue()));
            if (arg.realp())
                return SingleFloat.createSingleFloat((float)Math.tan(SingleFloat.coerceToFloat(arg).floatValue()));
            return sin(arg).divideBy(cos(arg));
        }
    };

    // ### asin
    private static final Primitive ASIN = new Primitive("asin", "number")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return asin(arg);
        }
    };

  /*private*/ static LispObject asin(LispObject arg) throws ConditionThrowable
    {
        if (arg .isSingleFloat()) {
            float f = arg.floatValue();
            if (Math.abs(f) <= 1)
                return SingleFloat.createSingleFloat((float)Math.asin(f));
        }
        if (arg .isDoubleFloat()) {
            double d = arg.doubleValue();
            if (Math.abs(d) <= 1)
                return DoubleFloat.createDoubleFloat(Math.asin(d));
        }
        LispObject result = arg.multiplyBy(arg);
        result = Fixnum.ONE.subtract(result);
        result = sqrt(result);
        LispObject n = Complex.getInstance(Fixnum.ZERO, Fixnum.ONE);
        n = n.multiplyBy(arg);
        result = n.add(result);
        result = log(result);
        result = result.multiplyBy(Complex.getInstance(Fixnum.ZERO,
                                                       Fixnum.MINUS_ONE));
        if (result instanceof Complex) {
            if (arg instanceof Complex)
                return result;
            LispObject im = ((Complex)result).getImaginaryPart();
            if (im.isZero())
                return ((Complex)result).getRealPart();
        }
        return result;
    }

    // ### acos
    private static final Primitive ACOS = new Primitive("acos", "number")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return acos(arg);
        }
    };

  /*private*/ static LispObject acos(LispObject arg) throws ConditionThrowable
    {
        if (arg .isDoubleFloat()) {
            double d = arg.doubleValue();
            if (Math.abs(d) <= 1)
                return DoubleFloat.createDoubleFloat(Math.acos(d));
        }
        if (arg .isSingleFloat()) {
            float f = arg.floatValue();
            if (Math.abs(f) <= 1)
                return SingleFloat.createSingleFloat((float)Math.acos(f));
        }
        LispObject result = DoubleFloat.createDoubleFloat(Math.PI/2);
        if (!(arg .isDoubleFloat())) {
            if (arg instanceof Complex &&
                    ((Complex)arg).getRealPart() .isDoubleFloat()) {
                    // do nothing; we want to keep the double float value
            }
            else
                result = SingleFloat.createSingleFloat((float)result.doubleValue());
        }
        result = result.subtract(asin(arg));
        if (result instanceof Complex) {
            if (arg instanceof Complex)
                return result;
            LispObject im = ((Complex)result).getImaginaryPart();
            if (im.isZero())
                return ((Complex)result).getRealPart();
        }
        return result;
    }

    // ### atan
    private static final Primitive ATAN =
        new Primitive("atan", "number1 &optional number2")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            if (arg.isNumber())
                return atan(arg);
            return type_error(arg, SymbolConstants.NUMBER);
        }

        // "If both number1 and number2 are supplied for atan, the result is
        // the arc tangent of number1/number2."

        // y = +0     x = +0       +0
        // y = -0     x = +0       -0
        // y = +0     x = -0       +<PI>
        // y = -0     x = -0       -<PI>
        @Override
        public LispObject execute(LispObject y, LispObject x)
            throws ConditionThrowable
        {
            if (!y.realp())
                return type_error(y, SymbolConstants.REAL);
            if (!x.realp())
                return type_error(x, SymbolConstants.REAL);
            double d1, d2;
            d1 = DoubleFloat.coerceToFloat(y).doubleValue();
            d2 = DoubleFloat.coerceToFloat(x).doubleValue();
            double result = Math.atan2(d1, d2);
            if (y .isDoubleFloat() || x .isDoubleFloat())
                return DoubleFloat.createDoubleFloat(result);
            else
                return SingleFloat.createSingleFloat((float)result);
        }
    };

  /*private*/ static LispObject atan(LispObject arg) throws ConditionThrowable
    {
        if (arg instanceof Complex) {
            LispObject im = ((Complex)arg).imagpart;
            if (im.isZero())
                return Complex.getInstance(atan(((Complex)arg).realpart),
                                           im);
            LispObject result = arg.multiplyBy(arg);
            result = result.add(Fixnum.ONE);
            result = Fixnum.ONE.divideBy(result);
            result = sqrt(result);
            LispObject n = Complex.getInstance(Fixnum.ZERO, Fixnum.ONE);
            n = n.multiplyBy(arg);
            n = n.add(Fixnum.ONE);
            result = n.multiplyBy(result);
            result = log(result);
            result = result.multiplyBy(Complex.getInstance(Fixnum.ZERO, Fixnum.MINUS_ONE));
            return result;
        }
        if (arg .isDoubleFloat())
            return DoubleFloat.createDoubleFloat(Math.atan(arg.doubleValue()));
        return SingleFloat.createSingleFloat((float)Math.atan(SingleFloat.coerceToFloat(arg).floatValue()));
    }

    // ### sinh
    private static final Primitive SINH = new Primitive("sinh", "number")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return sinh(arg);
        }
    };

  /*private*/ static LispObject sinh(LispObject arg) throws ConditionThrowable
    {
        if (arg instanceof Complex) {
            LispObject im = ((Complex)arg).getImaginaryPart();
            if (im.isZero())
                return Complex.getInstance(sinh(((Complex)arg).getRealPart()),
                                           im);
        }
        if (arg .isSingleFloat()) {
            try {
                double d = Math.sinh(arg.floatValue());
                return SingleFloat.createSingleFloat((float)d);
            }
            catch (Throwable t) {
                Debug.trace(t);
                // Fall through...
            }
        } else if (arg .isDoubleFloat()) {
            try {
                double d = Math.sinh(arg.doubleValue());
                return DoubleFloat.createDoubleFloat(d);
            }
            catch (Throwable t) {
                Debug.trace(t);
                // Fall through...
            }
        }
        LispObject result = exp(arg);
        result = result.subtract(exp(arg.multiplyBy(Fixnum.MINUS_ONE)));
        result = result.divideBy(Fixnum.TWO);
        if (result instanceof Complex) {
            if (arg instanceof Complex)
                return result;
            LispObject im = ((Complex)result).getImaginaryPart();
            if (im.isZero())
                return ((Complex)result).getRealPart();
        }
        return result;
    }

    // ### cosh
    private static final Primitive COSH = new Primitive("cosh", "number")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return cosh(arg);
        }
    };

  /*private*/ static LispObject cosh(LispObject arg) throws ConditionThrowable
    {
        if (arg instanceof Complex) {
            LispObject im = ((Complex)arg).getImaginaryPart();
            if (im.isZero())
                return Complex.getInstance(cosh(((Complex)arg).getRealPart()),
                                           im);
        }
        if (arg .isSingleFloat()) {
            try {
                double d = Math.cosh(arg.floatValue());
                return SingleFloat.createSingleFloat((float)d);
            }
            catch (Throwable t) {
                Debug.trace(t);
                // Fall through...
            }
        } else if (arg .isDoubleFloat()) {
            try {
                double d = Math.cosh(arg.doubleValue());
                return DoubleFloat.createDoubleFloat(d);
            }
            catch (Throwable t) {
                Debug.trace(t);
                // Fall through...
            }
        }
        LispObject result = exp(arg);
        result = result.add(exp(arg.multiplyBy(Fixnum.MINUS_ONE)));
        result = result.divideBy(Fixnum.TWO);
        if (result instanceof Complex) {
            if (arg instanceof Complex)
                return result;
            LispObject im = ((Complex)result).getImaginaryPart();
            if (im.isZero())
                return ((Complex)result).getRealPart();
        }
        return result;
    }

    // ### tanh
    private static final Primitive TANH = new Primitive("tanh", "number")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            if (arg .isSingleFloat()) {
                try {
                    double d = Math.tanh(arg.floatValue());
                    return SingleFloat.createSingleFloat((float)d);
                }
                catch (Throwable t) {
                    Debug.trace(t);
                    // Fall through...
                }
            } else if (arg .isDoubleFloat()) {
                try {
                    double d = Math.tanh(arg.doubleValue());
                    return DoubleFloat.createDoubleFloat(d);
                }
                catch (Throwable t) {
                    Debug.trace(t);
                    // Fall through...
                }
            }
            return sinh(arg).divideBy(cosh(arg));
        }
    };

    // ### asinh
    private static final Primitive ASINH = new Primitive("asinh", "number")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return asinh(arg);
        }
    };

  /*private*/ static LispObject asinh(LispObject arg) throws ConditionThrowable
    {
        if (arg instanceof Complex) {
            LispObject im = ((Complex)arg).getImaginaryPart();
            if (im.isZero())
                return Complex.getInstance(asinh(((Complex)arg).getRealPart()),
                                           im);
        }
        LispObject result = arg.multiplyBy(arg);
        result = Fixnum.ONE.add(result);
        result = sqrt(result);
        result = result.add(arg);
        result = log(result);
        if (result instanceof Complex) {
            if (arg instanceof Complex)
                return result;
            LispObject im = ((Complex)result).getImaginaryPart();
            if (im.isZero())
                return ((Complex)result).getRealPart();
        }
        return result;
    }

    // ### acosh
    private static final Primitive ACOSH = new Primitive("acosh", "number")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return acosh(arg);
        }
    };

  /*private*/ static LispObject acosh(LispObject arg) throws ConditionThrowable
    {
        if (arg instanceof Complex) {
            LispObject im = ((Complex)arg).getImaginaryPart();
            if (im.isZero())
                return Complex.getInstance(acosh(((Complex)arg).getRealPart()),
                                           im);
        }
        LispObject n1 = arg.add(Fixnum.ONE);
        n1 = n1.divideBy(Fixnum.TWO);
        n1 = sqrt(n1);
        LispObject n2 = arg.subtract(Fixnum.ONE);
        n2 = n2.divideBy(Fixnum.TWO);
        n2 = sqrt(n2);
        LispObject result = n1.add(n2);
        result = log(result);
        result = result.multiplyBy(Fixnum.TWO);
        if (result instanceof Complex) {
            if (arg instanceof Complex)
                return result;
            LispObject im = ((Complex)result).getImaginaryPart();
            if (im.isZero())
                return ((Complex)result).getRealPart();
        }
        return result;
    }

    // ### atanh
    private static final Primitive ATANH = new Primitive("atanh", "number")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return atanh(arg);
        }
    };

  /*private*/ static LispObject atanh(LispObject arg) throws ConditionThrowable
    {
        if (arg instanceof Complex) {
            LispObject im = ((Complex)arg).getImaginaryPart();
            if (im.isZero())
                return Complex.getInstance(atanh(((Complex)arg).getRealPart()),
                                           im);
        }
        LispObject n1 = log(Fixnum.ONE.add(arg));
        LispObject n2 = log(Fixnum.ONE.subtract(arg));
        LispObject result = n1.subtract(n2);
        result = result.divideBy(Fixnum.TWO);
        if (result instanceof Complex) {
            if (arg instanceof Complex)
                return result;
            LispObject im = ((Complex)result).getImaginaryPart();
            if (im.isZero())
                return ((Complex)result).getRealPart();
        }
        return result;
    }

    // ### cis
    private static final Primitive CIS = new Primitive("cis", "radians")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return cis(arg);
        }
    };

  /*private*/ static LispObject cis(LispObject arg) throws ConditionThrowable
    {
        if (arg.realp())
            return Complex.getInstance(cos(arg), sin(arg));
        return type_error(arg, SymbolConstants.REAL);
    }

    // ### exp
    private static final Primitive EXP = new Primitive("exp", "number")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return exp(arg);
        }
    };

  /*private*/ static LispObject exp(LispObject arg) throws ConditionThrowable
    {
        if (arg.realp()) {
            if (arg .isDoubleFloat()) {
                double d = Math.pow(Math.E, arg.doubleValue());
                return OverUnderFlowCheck(DoubleFloat.createDoubleFloat(d));
            } else {
                float f = (float) Math.pow(Math.E, SingleFloat.coerceToFloat(arg).floatValue());
                return OverUnderFlowCheck(SingleFloat.createSingleFloat(f));
            }
        }
        if (arg instanceof Complex) {
            Complex c = (Complex) arg;
            return exp(c.getRealPart()).multiplyBy(cis(c.getImaginaryPart()));
        }
        return type_error(arg, SymbolConstants.NUMBER);
    }

    // ### sqrt
    private static final Primitive SQRT = new Primitive("sqrt", "number")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return sqrt(arg);
        }
    };

  /*private*/ static final LispObject sqrt(LispObject obj) throws ConditionThrowable
    {
        if (obj .isDoubleFloat()) {
            if (obj.isNegative())
                return Complex.getInstance(DoubleFloat.createDoubleFloat((double)0), sqrt(obj.negate()));
            return DoubleFloat.createDoubleFloat(Math.sqrt(DoubleFloat.coerceToFloat(obj).doubleValue()));
        }
        if (obj.realp()) {
            if (obj.isNegative())
                return Complex.getInstance(SingleFloat.createSingleFloat(0f), sqrt(obj.negate()));
            return SingleFloat.createSingleFloat((float)Math.sqrt(SingleFloat.coerceToFloat(obj).floatValue()));
        }
        if (obj instanceof Complex) {
            LispObject imagpart = ((Complex)obj).imagpart;
            if (imagpart.isZero()) {
                LispObject realpart = ((Complex)obj).realpart;
                if (realpart.isNegative())
                    return Complex.getInstance(imagpart, sqrt(realpart.negate()));
                else
                    return Complex.getInstance(sqrt(realpart), imagpart);
            }
            return exp(log(obj).divideBy(Fixnum.TWO));
        }
        return type_error(obj, SymbolConstants.NUMBER);
    }

    // ### log
    private static final Primitive LOG =
        new Primitive("log", "number &optional base")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return log(arg);
        }
        @Override
        public LispObject execute(LispObject number, LispObject base)
            throws ConditionThrowable
        {
            if (number.realp() && !number.isNegative()
                && base.isEqualTo(Fixnum.getInstance(10))) {
                try {
                    double d =
                        Math.log10(DoubleFloat.coerceToFloat(number).doubleValue());
                    if (number .isDoubleFloat()
                        || base .isDoubleFloat())
                        return DoubleFloat.createDoubleFloat(d);
                    else
                        return SingleFloat.createSingleFloat((float)d);
                }
                catch (Throwable t) {
                    Debug.trace(t);
                    // Fall through...
                }
            }
            return log(number).divideBy(log(base));
        }
    };

  /*private*/ static final LispObject log(LispObject obj) throws ConditionThrowable
    {
        if (obj.realp() && !obj.isNegative()) {
            // Result is real.
            if (obj .isFixnum())
                return SingleFloat.createSingleFloat((float)Math.log(obj.intValue()));
            if (obj .isBignum())
                return SingleFloat.createSingleFloat((float)Math.log(obj.doubleValue()));
            if (obj instanceof Ratio)
                return SingleFloat.createSingleFloat((float)Math.log(obj.doubleValue()));
            if (obj .isSingleFloat())
                return SingleFloat.createSingleFloat((float)Math.log(obj.floatValue()));
            if (obj .isDoubleFloat())
                return DoubleFloat.createDoubleFloat(Math.log(obj.doubleValue()));
        } else {
            // Result is complex.
            if (obj.realp() && obj.isNegative()) {
                if (obj .isDoubleFloat()) {
                    DoubleFloat re = DoubleFloat.coerceToFloat(obj);
                    DoubleFloat abs = DoubleFloat.createDoubleFloat(Math.abs(re.doubleValue()));
                    DoubleFloat phase = DoubleFloat.createDoubleFloat(Math.PI);
                    return Complex.getInstance(DoubleFloat.createDoubleFloat(Math.log(abs.getValue())), phase);
                } else {
                    SingleFloat re = SingleFloat.coerceToFloat(obj);
                    SingleFloat abs = SingleFloat.createSingleFloat(Math.abs(re.floatValue()));
                    SingleFloat phase = SingleFloat.createSingleFloat((float)Math.PI);
                    return Complex.getInstance(SingleFloat.createSingleFloat((float)Math.log(abs.floatValue())), phase);
                }
            } else if (obj instanceof Complex) {
                if (((Complex)obj).getRealPart() .isDoubleFloat()) {
                    DoubleFloat re = DoubleFloat.coerceToFloat(((Complex)obj).getRealPart());
                    DoubleFloat im = DoubleFloat.coerceToFloat(((Complex)obj).getImaginaryPart());
                    DoubleFloat phase =
                        DoubleFloat.createDoubleFloat(Math.atan2(im.getValue(), re.getValue()));  // atan(y/x)
                    DoubleFloat abs = DoubleFloat.coerceToFloat(obj.ABS());
                    return Complex.getInstance(DoubleFloat.createDoubleFloat(Math.log(abs.getValue())), phase);
                } else {
                    SingleFloat re = SingleFloat.coerceToFloat(((Complex)obj).getRealPart());
                    SingleFloat im = SingleFloat.coerceToFloat(((Complex)obj).getImaginaryPart());
                    SingleFloat phase =
                        SingleFloat.createSingleFloat((float)Math.atan2(im.floatValue(), re.floatValue()));  // atan(y/x)
                    SingleFloat abs = SingleFloat.coerceToFloat(obj.ABS());
                    return Complex.getInstance(SingleFloat.createSingleFloat((float)Math.log(abs.floatValue())), phase);
                }
            }
        }
        type_error(obj, SymbolConstants.NUMBER);
        return NIL;
    }

    // ### expt base-number power-number => result
    public static final Primitive EXPT =
        new Primitive("expt", "base-number power-number")
    {
        @Override
        public LispObject execute(LispObject base, LispObject power)
            throws ConditionThrowable
        {
            if (power.isZero()) {
                if (power .isFixnum()) {
                    if (base .isSingleFloat())
                        return SingleFloat.ONE;
                    if (base .isDoubleFloat())
                        return DoubleFloat.ONE;
                    if (base instanceof Complex) {
                        if (((Complex)base).realpart .isSingleFloat())
                            return Complex.getInstance(SingleFloat.ONE,
                                                       SingleFloat.ZERO);
                        if (((Complex)base).realpart .isDoubleFloat())
                            return Complex.getInstance(DoubleFloat.ONE,
                                                       DoubleFloat.ZERO);
                    }
                    return Fixnum.ONE;
                }
                if (power .isDoubleFloat())
                    return DoubleFloat.ONE;
                if (base .isDoubleFloat())
                    return DoubleFloat.ONE;
                return SingleFloat.ONE;
            }
            if (base.isZero())
                return base;
            if (base.isEqualTo(1))
                return base;
            
            if ((power .isFixnum()
                 || power .isBignum())
                 && (base.rationalp()
                     || (base instanceof Complex
                         && ((Complex)base).realpart.rationalp()))) {
                // exact math version
                return intexp(base, power);
            }
            // for anything not a rational or complex rational, use
            // float approximation.
            if (base instanceof Complex || power instanceof Complex)
                return exp(power.multiplyBy(log(base)));
            final double x; // base
            final double y; // power
            if (base .isFixnum())
                x = base.intValue();
            else if (base .isBignum())
                x = base.doubleValue();
            else if (base instanceof Ratio)
                x = base.doubleValue();
            else if (base .isSingleFloat())
                x = base.floatValue();
            else if (base .isDoubleFloat())
                x = base.doubleValue();
            else
                return error(new LispError("EXPT: unsupported case: base is of type " +
                                            base.typeOf().writeToString()));

            if (power .isFixnum())
                y = power.intValue();
            else if (power .isBignum())
                y = power.doubleValue();
            else if (power instanceof Ratio)
                y = power.doubleValue();
            else if (power .isSingleFloat())
                y = power.floatValue();
            else if (power .isDoubleFloat())
                y = power.doubleValue();
            else
                return error(new LispError("EXPT: unsupported case: power is of type " +
                                            power.typeOf().writeToString()));
            double r = Math.pow(x, y);
            if (Double.isNaN(r)) {
                if (x < 0) {
                    r = Math.pow(-x, y);
                    double realPart = r * Math.cos(y * Math.PI);
                    double imagPart = r * Math.sin(y * Math.PI);
                    if (base .isDoubleFloat() || power .isDoubleFloat())
                        return Complex
                            .getInstance(OverUnderFlowCheck(DoubleFloat.createDoubleFloat(realPart)),
                                         OverUnderFlowCheck(DoubleFloat.createDoubleFloat(imagPart)));
                    else
                        return Complex
                            .getInstance(OverUnderFlowCheck(SingleFloat.createSingleFloat((float)realPart)),
                                         OverUnderFlowCheck(SingleFloat
												.createSingleFloat((float)imagPart)));
                }
            }
            if (base .isDoubleFloat() || power .isDoubleFloat())
                return OverUnderFlowCheck(DoubleFloat.createDoubleFloat(r));
            else
                return OverUnderFlowCheck(SingleFloat.createSingleFloat((float)r));
        }
    };

    /** Checks number for over- or underflow values.
     *
     * @param number
     * @return number or signals an appropriate error
     * @throws org.armedbear.lisp.ConditionThrowable
     */
  /*private*/ final static LispObject OverUnderFlowCheck(LispObject number)
            throws ConditionThrowable
    {
        if (number instanceof Complex) {
            OverUnderFlowCheck(((Complex)number).realpart);
            OverUnderFlowCheck(((Complex)number).imagpart);
            return number;
        }

        if (TRAP_OVERFLOW) {
            if (number .isSingleFloat())
                if (Float.isInfinite(number.floatValue()))
                    return error(new FloatingPointOverflow(NIL));
            if (number .isDoubleFloat())
                if (Double.isInfinite(number.doubleValue()))
                    return error(new FloatingPointOverflow(NIL));
        }
        if (TRAP_UNDERFLOW) {
            if (number.isZero())
                return error(new FloatingPointUnderflow(NIL));
        }
        return number;
    }

    /** Checks number for over- or underflow values.
     *
     * @param number
     * @return number or signals an appropriate error
     * @throws org.armedbear.lisp.ConditionThrowable
     */
    final static float OverUnderFlowCheck(float number)
            throws ConditionThrowable
    {
        if (TRAP_OVERFLOW) {
            if (Float.isInfinite(number))
                error(new FloatingPointOverflow(NIL));
        }
        if (TRAP_UNDERFLOW) {
            if (number == 0)
                error(new FloatingPointUnderflow(NIL));
        }
        return number;
    }

    /** Checks number for over- or underflow values.
     *
     * @param number
     * @return number or signals an appropriate error
     * @throws org.armedbear.lisp.ConditionThrowable
     */
    public final static double OverUnderFlowCheck(double number)
            throws ConditionThrowable
    {
        if (TRAP_OVERFLOW) {
            if (Double.isInfinite(number))
                error(new FloatingPointOverflow(NIL));
        }
        if (TRAP_UNDERFLOW) {
            if (number == 0)
                error(new FloatingPointUnderflow(NIL));
        }
        return number;
    }
    // Adapted from SBCL.
    /** Return the exponent of base taken to the integer exponent power
     *
     * @param base A value of any type
     * @param power An integer (fixnum or bignum) value
     * @throws org.armedbear.lisp.ConditionThrowable
     */
  /*private*/ static final LispObject intexp(LispObject base, LispObject power)
        throws ConditionThrowable
    {
        if (power.isEqualTo(0))
            return Fixnum.ONE;
        if (base.isEqualTo(1))
            return base;
        if (base.isEqualTo(0))
            return base;

        if (power.isNegative()) {
            power = Fixnum.ZERO.subtract(power);
            return Fixnum.ONE.divideBy(intexp(base, power));
        }
        if (base.eql(Fixnum.TWO))
            return Fixnum.ONE.ash(power);

        LispObject nextn = power.ash(Fixnum.MINUS_ONE);
        LispObject total;
        if (power.isOdd())
            total = base;
        else
            total = Fixnum.ONE;
        while (true) {
            if (nextn.isZero())
                return total;
            base = base.multiplyBy(base);

            if (nextn.isOdd())
                total = base.multiplyBy(total);
            nextn = nextn.ash(Fixnum.MINUS_ONE);
        }
    }
}
