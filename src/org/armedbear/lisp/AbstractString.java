/*
 * AbstractString.java
 *
 * Copyright (C) 2004 Peter Graves
 * $Id: AbstractString.java 11488 2008-12-27 10:50:33Z ehuelsmann $
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

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

public abstract class AbstractString extends AbstractVector
{
    @Override
    public LispObject typep(LispObject type) throws ConditionThrowable
    {
        if (type instanceof Symbol) {
            if (type == SymbolConstants.STRING)
                return T;
            if (type == SymbolConstants.BASE_STRING)
                return isBaseString()?T:NIL;
        }
        if (type == BuiltInClass.STRING)
            return T;
        if (type == BuiltInClass.BASE_STRING)
            return isBaseString()?T:NIL;
        return super.typep(type);
    }
    
   
    public boolean isBaseString() {
    	char[] chars;
		try {
			chars = chars();
		} catch (ConditionThrowable e) {
			return false;
		}
    	for (int i=chars.length-1;i>=0;i--) {
    		char c = chars[i];
    		if (!LispCharacter.isBaseChar(c)) {
    			return false;
    		}
    	}
		return true;
	}
    
    @Override
    public final LispObject STRINGP()
    {
        return T;
    }

    @Override
    public final boolean isString()
    {
        return true;
    }

    @Override
    public LispObject getElementType()
    {
        return SymbolConstants.CHARACTER;
    }

    @Override
    public final boolean isSimpleVector()
    {
        return false;
    }

    @Override
    public final LispObject STRING()
    {
        return this;
    }

    public abstract void fill(char c) throws ConditionThrowable;

    public abstract char charAt(int index) throws ConditionThrowable;

    public abstract void setCharAt(int index, char c) throws ConditionThrowable;

    public final String writeToString(int beginIndex, int endIndex)
        throws ConditionThrowable
    {
        if (beginIndex < 0)
            beginIndex = 0;
        final int limit;
        limit = seqLength();
        if (endIndex > limit)
            endIndex = limit;
        final LispThread thread = LispThread.currentThread();
        if (SymbolConstants.PRINT_ESCAPE.symbolValue(thread) != NIL ||
        		SymbolConstants.PRINT_READABLY.symbolValue(thread) != NIL)
        {
            FastStringBuffer sb = new FastStringBuffer('"');
            for (int i = beginIndex; i < endIndex; i++) {
                char c = charAt(i);
                if (c == '\"' || c == '\\')
                    sb.append('\\');
                sb.append(c);
            }
            sb.append('"');
            return sb.toString();
        } else
            return getStringValue().substring(beginIndex, endIndex);
    }

    @Override
    public String writeToString() throws ConditionThrowable
    {
        return writeToString(0, seqLength());
    }    
}
