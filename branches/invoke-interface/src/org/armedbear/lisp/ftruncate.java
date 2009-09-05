/*
 * ftruncate.java
 *
 * Copyright (C) 2004-2005 Peter Graves
 * $Id: ftruncate.java 11488 2008-12-27 10:50:33Z ehuelsmann $
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

// ### ftruncate number &optional divisor => quotient, remainder
// (defun ftruncate (number &optional (divisor 1))
//  (multiple-value-bind (tru rem) (truncate number divisor)
//   (values (float tru) rem)))

// "FFLOOR, FCEILING, FTRUNCATE, and FROUND handle arguments of different types
// in the following way: If number is a float, and divisor is not a float of
// longer format, then the first result is a float of the same type as number.
// Otherwise, the first result is of the type determined by contagion rules."
public final class ftruncate extends Primitive
{
    private ftruncate()
    {
        super("ftruncate", "number &optional divisor");
    }

    @Override
    public LispObject execute(LispObject arg) throws ConditionThrowable
    {
        final LispThread thread = LispThread.currentThread();
        if (arg.isZero()) {
            LispObject q = arg;
            LispObject r;
            if (arg .isDoubleFloat())
                r = DoubleFloat.ZERO;
            else
                r = SingleFloat.ZERO;
            return thread.setValues(q, r);
        }
        if (arg .isDoubleFloat()) {
            double d = arg.doubleValue();
            if (Double.isInfinite(d) || Double.isNaN(d))
                return thread.setValues(arg, NumericLispObject.createDoubleFloat(Double.NaN));
        } else if (arg .isSingleFloat()) {
            float f = arg.floatValue();
            if (Float.isInfinite(f) || Float.isNaN(f))
                return thread.setValues(arg, NumericLispObject.createSingleFloat(Float.NaN));
        }
        LispObject q = arg.truncate(Fixnum.ONE); // an integer
        if (arg .isDoubleFloat()) {
            if (q.isZero()) {
                if (arg.isNegative())
                    q = NumericLispObject.createDoubleFloat(-0.0);
                else
                    q = NumericLispObject.createDoubleFloat(0.0);
            } else if (q .isFixnum())
                q = NumericLispObject.createDoubleFloat((double)q.intValue());
            else
                q = NumericLispObject.createDoubleFloat(q.doubleValue());
        } else {
            if (q.isZero()) {
                if (arg.isNegative())
                    q = NumericLispObject.createSingleFloat(-0.0f);
                else
                    q = NumericLispObject.createSingleFloat(0.0f);
            } else if (q .isFixnum())
                q = NumericLispObject.createSingleFloat((float)q.intValue());
            else
                q = NumericLispObject.createSingleFloat(q.floatValue());
        }
        thread._values[0] = q;
        return q;
    }

    @Override
    public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
    {
        final LispThread thread = LispThread.currentThread();
        if (first.isZero()) {
            LispObject q = first;
            LispObject r;
            if (first .isDoubleFloat())
                r = DoubleFloat.ZERO;
            else
                r = SingleFloat.ZERO;
            return thread.setValues(q, r);
        }
        if (first .isDoubleFloat()) {
            double d1 = first.doubleValue();
            if (Double.isInfinite(d1) || Double.isNaN(d1))
                return thread.setValues(first, NumericLispObject.createDoubleFloat(Double.NaN));
        } else if (first .isSingleFloat()) {
            float f1 = first.floatValue();
            if (Float.isInfinite(f1) || Float.isNaN(f1))
                return thread.setValues(first, NumericLispObject.createSingleFloat(Float.NaN));
        }
        LispObject q = first.truncate(second); // an integer
        if (first .isDoubleFloat() || second .isDoubleFloat()) {
            if (q.isZero()) {
                if (first.isNegative()) {
                    if (second.isNegative())
                        q = NumericLispObject.createDoubleFloat(0.0);
                    else
                        q = NumericLispObject.createDoubleFloat(-0.0);
                } else if (second.isNegative())
                    q = NumericLispObject.createDoubleFloat(-0.0);
                else
                    q = NumericLispObject.createDoubleFloat(0.0);
            } else if (q .isFixnum())
                q = NumericLispObject.createDoubleFloat((double)q.intValue());
            else
                q = NumericLispObject.createDoubleFloat(q.doubleValue());
        } else {
            if (q.isZero()) {
                if (first.isNegative()) {
                    if (second.isNegative())
                        q = NumericLispObject.createSingleFloat(0.0f);
                    else
                        q = NumericLispObject.createSingleFloat(-0.0f);
                } else if (second.isNegative())
                    q = NumericLispObject.createSingleFloat(-0.0f);
                else
                    q = NumericLispObject.createSingleFloat(0.0f);
            } else if (q .isFixnum())
                q = NumericLispObject.createSingleFloat((float)q.intValue());
            else
                q = NumericLispObject.createSingleFloat(q.floatValue());
        }
        thread._values[0] = q;
        return q;
    }

    private static final Primitive FTRUNCATE = new ftruncate();
}
