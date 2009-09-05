/*
 * logbitp.java
 *
 * Copyright (C) 2003-2005 Peter Graves
 * $Id: logbitp.java 11488 2008-12-27 10:50:33Z ehuelsmann $
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

// ### logbitp index integer => generalized-boolean
public final class logbitp extends Primitive
{
    private logbitp()
    {
        super("logbitp", "index integer");
    }

    @Override
    public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
    {
        int index = -1;
        if (first .isFixnum()) {
            index = first.intValue();
        } else if (first .isBignum()) {
            // FIXME If the number is really big, we're not checking the right
            // bit...
            if (first.bigIntegerValue().signum() > 0)
                index = Integer.MAX_VALUE;
        }
        if (index < 0)
            return type_error(first, SymbolConstants.UNSIGNED_BYTE);
        BigInteger n;
        if (second .isInteger())
            n = second.bigIntegerValue();
        else
            return type_error(second, SymbolConstants.INTEGER);
        // FIXME See above.
        if (index == Integer.MAX_VALUE)
            return n.signum() < 0 ? T : NIL;
        return n.testBit(index) ? T : NIL;
    }

    private static final Primitive LOGBITP = new logbitp();
}
