/*
 * Complex.java
 *
 * Copyright (C) 2003-2006 Peter Graves
 * $Id: Complex.java 11954 2009-05-26 18:34:23Z ehuelsmann $
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

public final class Complex extends AbstractLispObject
{
  public final LispObject realpart;
  public final LispObject imagpart;

  private Complex(LispObject realpart, LispObject imagpart)
  {
    this.realpart = realpart;
    this.imagpart = imagpart;
  }

  public static LispObject getInstance(LispObject realpart,
                                       LispObject imagpart)
    throws ConditionThrowable
  {
    if (!realpart.realp())
      return type_error(realpart, SymbolConstants.REAL);
    if (!imagpart.realp())
      return type_error(imagpart, SymbolConstants.REAL);
    if (realpart instanceof DoubleFloat)
      imagpart = DoubleFloat.coerceToFloat(imagpart);
    else if (imagpart instanceof DoubleFloat)
      realpart = DoubleFloat.coerceToFloat(realpart);
    else if (realpart instanceof SingleFloat)
      imagpart = SingleFloat.coerceToFloat(imagpart);
    else if (imagpart instanceof SingleFloat)
      realpart = SingleFloat.coerceToFloat(realpart);
    if (imagpart .isFixnum())
      {
        if (imagpart.intValue() == 0)
          return realpart;
      }
    return new Complex(realpart, imagpart);
  }

  public LispObject getRealPart()
  {
    return realpart;
  }

  public LispObject getImaginaryPart()
  {
    return imagpart;
  }

  @Override
  public LispObject typeOf()
  {
    return SymbolConstants.COMPLEX;
  }

  @Override
  public LispObject classOf()
  {
    return BuiltInClass.COMPLEX;
  }

  @Override
  public LispObject typep(LispObject type) throws ConditionThrowable
  {
    if (type == SymbolConstants.COMPLEX)
      return T;
    if (type == SymbolConstants.NUMBER)
      return T;
    if (type == BuiltInClass.COMPLEX)
      return T;
    if (type == BuiltInClass.NUMBER)
      return T;
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
  public boolean eql(LispObject obj)
  {
    if (this == obj)
      return true;
    if (obj instanceof Complex)
      {
        Complex c = (Complex) obj;
        return realpart.eql(c.realpart) && imagpart.eql(c.imagpart);
      }
    return false;
  }

  @Override
  public boolean equal(LispObject obj)
  {
    return eql(obj);
  }

  @Override
  public boolean equalp(LispObject obj) throws ConditionThrowable
  {
    if (this == obj)
      return true;
    if (obj instanceof Complex)
      {
        Complex c = (Complex) obj;
        return (realpart.isEqualTo(c.realpart) &&
                imagpart.isEqualTo(c.imagpart));
      }
    if (obj.isNumber())
      {
        // obj is a number, but not complex.
        if (imagpart instanceof SingleFloat)
          {
            if (((SingleFloat)imagpart).floatValue() == 0)
              {
                if (obj .isFixnum())
                  return obj.intValue() == ((SingleFloat)realpart).floatValue();
                if (obj instanceof SingleFloat)
                  return ((SingleFloat)obj).floatValue() == ((SingleFloat)realpart).floatValue();
              }
          }
        if (imagpart instanceof DoubleFloat)
          {
            if (((DoubleFloat)imagpart).doubleValue() == 0)
              {
                if (obj .isFixnum())
                  return obj.intValue() == ((DoubleFloat)realpart).doubleValue();
                if (obj instanceof DoubleFloat)
                  return ((DoubleFloat)obj).doubleValue() == ((DoubleFloat)realpart).doubleValue();
              }
          }
      }
    return false;
  }

  @Override
  public final LispObject incr() throws ConditionThrowable
  {
    return new Complex(realpart.add(Fixnum.ONE), imagpart);
  }

  @Override
  public final LispObject decr() throws ConditionThrowable
  {
    return new Complex(realpart.subtract(Fixnum.ONE), imagpart);
  }

  @Override
  public LispObject add(LispObject obj) throws ConditionThrowable
  {
    if (obj instanceof Complex)
      {
        Complex c = (Complex) obj;
        return getInstance(realpart.add(c.realpart), imagpart.add(c.imagpart));
      }
    return getInstance(realpart.add(obj), imagpart);
  }

  @Override
  public LispObject subtract(LispObject obj) throws ConditionThrowable
  {
    if (obj instanceof Complex)
      {
        Complex c = (Complex) obj;
        return getInstance(realpart.subtract(c.realpart),
                           imagpart.subtract(c.imagpart));
      }
    return getInstance(realpart.subtract(obj), imagpart);
  }

  @Override
  public LispObject multiplyBy(LispObject obj) throws ConditionThrowable
  {
    if (obj instanceof Complex)
      {
        LispObject a = realpart;
        LispObject b = imagpart;
        LispObject c = ((Complex)obj).getRealPart();
        LispObject d = ((Complex)obj).getImaginaryPart();
        // xy = (ac - bd) + i(ad + bc)
        // real part = ac - bd
        // imag part = ad + bc
        LispObject ac = a.multiplyBy(c);
        LispObject bd = b.multiplyBy(d);
        LispObject ad = a.multiplyBy(d);
        LispObject bc = b.multiplyBy(c);
        return Complex.getInstance(ac.subtract(bd), ad.add(bc));
      }
    return Complex.getInstance(realpart.multiplyBy(obj),
                               imagpart.multiplyBy(obj));
  }

  @Override
  public LispObject divideBy(LispObject obj) throws ConditionThrowable
  {
    if (obj instanceof Complex)
      {
        LispObject a = realpart;
        LispObject b = imagpart;
        LispObject c = ((Complex)obj).getRealPart();
        LispObject d = ((Complex)obj).getImaginaryPart();
        LispObject ac = a.multiplyBy(c);
        LispObject bd = b.multiplyBy(d);
        LispObject bc = b.multiplyBy(c);
        LispObject ad = a.multiplyBy(d);
        LispObject denominator = c.multiplyBy(c).add(d.multiplyBy(d));
        return Complex.getInstance(ac.add(bd).divideBy(denominator),
                                   bc.subtract(ad).divideBy(denominator));
      }
    return Complex.getInstance(realpart.divideBy(obj),
                               imagpart.divideBy(obj));
  }

  @Override
  public boolean isEqualTo(LispObject obj) throws ConditionThrowable
  {
    if (obj instanceof Complex)
      {
        Complex c = (Complex) obj;
        return (realpart.isEqualTo(c.realpart) &&
                imagpart.isEqualTo(c.imagpart));
      }
    if (obj.isNumber())
      {
        // obj is a number, but not complex.
        if (imagpart instanceof SingleFloat)
          {
            if (((SingleFloat)imagpart).floatValue() == 0)
              {
                if (obj .isFixnum())
                  return obj.intValue() == ((SingleFloat)realpart).floatValue();
                if (obj instanceof SingleFloat)
                  return ((SingleFloat)obj).floatValue() == ((SingleFloat)realpart).floatValue();
                if (obj instanceof DoubleFloat)
                  return ((DoubleFloat)obj).doubleValue() == ((SingleFloat)realpart).floatValue();
              }
          }
        if (imagpart instanceof DoubleFloat)
          {
            if (((DoubleFloat)imagpart).doubleValue() == 0)
              {
                if (obj .isFixnum())
                  return obj.intValue() == ((DoubleFloat)realpart).doubleValue();
                if (obj instanceof SingleFloat)
                  return ((SingleFloat)obj).floatValue() == ((DoubleFloat)realpart).doubleValue();
                if (obj instanceof DoubleFloat)
                  return ((DoubleFloat)obj).doubleValue() == ((DoubleFloat)realpart).doubleValue();
              }
          }
        return false;
      }
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
  public LispObject ABS() throws ConditionThrowable
  {
    if (realpart.isZero())
      return imagpart.ABS();
    double real = DoubleFloat.coerceToFloat(realpart).doubleValue();
    double imag = DoubleFloat.coerceToFloat(imagpart).doubleValue();
    if (realpart instanceof DoubleFloat)
      return DoubleFloat.createDoubleFloat(hypot(real, imag));
    else
      return new SingleFloat((float)hypot(real, imag));
  }

  private double hypot(double real, double imag) {
	  return Math.sqrt(real * real + imag * imag);
  }

@Override
  public boolean isZero() throws ConditionThrowable
  {
    return realpart.isZero() && imagpart.isZero();
  }

  @Override
  public LispObject COMPLEXP()
  {
    return T;
  }

  @Override
  public int sxhash()
  {
    return (mix(realpart.sxhash(), imagpart.sxhash()) & 0x7fffffff);
  }

  @Override
  public int psxhash()
  {
    return (mix(realpart.psxhash(), imagpart.psxhash()) & 0x7fffffff);
  }

  @Override
  public String writeToString() throws ConditionThrowable
  {
    FastStringBuffer sb = new FastStringBuffer("#C(");
    sb.append(realpart.writeToString());
    sb.append(' ');
    sb.append(imagpart.writeToString());
    sb.append(')');
    return sb.toString();
  }
}
