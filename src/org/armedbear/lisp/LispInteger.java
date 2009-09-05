/*
 * LispInteger.java
 *
 * Copyright (C) 2003-2007 Peter Graves
 * $Id: Bignum.java 11573 2009-01-21 22:14:47Z ehuelsmann $
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

/** This class merely serves as the super class for
 * Fixnum and Bignum
 */
abstract public class LispInteger extends NumericLispObject
{
	
  abstract public BigInteger bigIntegerValue();
  abstract public int intValue();
  abstract public long longValue();
  
  private static BigInteger MOST_NEGATIVE_FIXNUM =
          BigInteger.valueOf(Integer.MIN_VALUE);
  private static BigInteger MOST_POSITIVE_FIXNUM =
          BigInteger.valueOf(Integer.MAX_VALUE);

//  public static LispInteger getInstance(long l) {
//      if (Integer.MIN_VALUE <= l && l <= Integer.MAX_VALUE)
//          return LispInteger.getInstance(l);
//      else
//          return new Bignum(l);
//  }
  public static LispInteger getInteger(String s, int radix) {
      BigInteger value = new BigInteger(s, radix);

      return LispInteger.getInteger(value);
  }
  
  public static LispInteger getInteger(BigInteger n) {
      if (MOST_NEGATIVE_FIXNUM.compareTo(n) < 0 ||
              MOST_POSITIVE_FIXNUM.compareTo(n) > 0)
          return new Bignum(n);
      else
          return Fixnum.makeFixnum(n.intValue());
  }

  public static LispInteger getInteger(long l) {
      if (Integer.MIN_VALUE <= l && l <= Integer.MAX_VALUE)
          return Fixnum.makeFixnum((int)l);
      else
          return new Bignum(l);
  }


}
