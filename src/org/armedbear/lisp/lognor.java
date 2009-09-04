/*
 * lognor.java
 *
 * Copyright (C) 2003-2005 Peter Graves
 * $Id: lognor.java 11714 2009-03-23 20:05:37Z ehuelsmann $
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

public final class lognor extends Primitive
{
    private lognor()
    {
        super("lognor", "integer-1 integer-2");
    }

    @Override
    public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
    {
        if (first .isFixnum()) {
            if (second .isFixnum())
                return Fixnum.getInstance(~(first.intValue() |
                                    second.intValue()));
            if (second instanceof Bignum) {
                BigInteger n1 = ((Fixnum)first).bigIntegerValue();
                BigInteger n2 = ((Bignum)second).bigIntegerValue();
                return number(n1.or(n2).not());
            }
            return type_error(second, SymbolConstants.INTEGER);
        }
        if (first instanceof Bignum) {
            BigInteger n1 = ((Bignum)first).bigIntegerValue();
            if (second .isFixnum()) {
                BigInteger n2 = ((Fixnum)second).bigIntegerValue();
                return number(n1.or(n2).not());
            }
            if (second instanceof Bignum) {
                BigInteger n2 = ((Bignum)second).bigIntegerValue();
                return number(n1.or(n2).not());
            }
            return type_error(second, SymbolConstants.INTEGER);
        }
        return type_error(first, SymbolConstants.INTEGER);
    }

    private static final Primitive LOGNOR = new lognor();
}
