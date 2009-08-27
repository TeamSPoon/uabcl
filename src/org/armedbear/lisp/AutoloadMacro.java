/*
 * AutoloadMacro.java
 *
 * Copyright (C) 2003-2004 Peter Graves
 * $Id: AutoloadMacro.java 11488 2008-12-27 10:50:33Z ehuelsmann $
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

public final class AutoloadMacro extends Autoload
{
    private AutoloadMacro(Symbol symbol)
    {
        super(symbol);
    }

    private AutoloadMacro(Symbol symbol, String fileName)
    {
        super(symbol, fileName, null);
    }

  /*private*/ static void installAutoloadMacro(Symbol symbol, String fileName)
        throws ConditionThrowable
    {
        AutoloadMacro am = new AutoloadMacro(symbol, fileName);
        if (symbol.getSymbolFunction() instanceof SpecialOperator)
            put(symbol, SymbolConstants.MACROEXPAND_MACRO, am);
        else
            symbol.setSymbolFunction(am);
    }

    @Override
    public void load() throws ConditionThrowable
    {
        Load.loadSystemFile(getFileName(), true);
    }

    @Override
    public String writeToString() throws ConditionThrowable
    {
        StringBuffer sb = new StringBuffer("#<AUTOLOAD-MACRO ");
        sb.append(getSymbol().writeToString());
        sb.append(" \"");
        sb.append(getFileName());
        sb.append("\">");
        return sb.toString();
    }

    // ### autoload-macro
    private static final Primitive AUTOLOAD_MACRO =
        new Primitive("autoload-macro", PACKAGE_EXT, true)
    {
        @Override
        public LispObject execute(LispObject first) throws ConditionThrowable
        {
            if (first instanceof Symbol) {
                Symbol symbol = (Symbol) first;
                installAutoloadMacro(symbol, null);
                return T;
            }
            if (first instanceof Cons) {
                for (LispObject list = first; list != NIL; list = list.rest()) {
                    Symbol symbol = checkSymbol(list.first());
                    installAutoloadMacro(symbol, null);
                }
                return T;
            }
            return error(new TypeError(first));
        }
        @Override
        public LispObject execute(LispObject first, LispObject second)
            throws ConditionThrowable
        {
            final String fileName = second.getStringValue();
            if (first instanceof Symbol) {
                Symbol symbol = (Symbol) first;
                installAutoloadMacro(symbol, fileName);
                return T;
            }
            if (first instanceof Cons) {
                for (LispObject list = first; list != NIL; list = list.rest()) {
                    Symbol symbol = checkSymbol(list.first());
                    installAutoloadMacro(symbol, fileName);
                }
                return T;
            }
            return error(new TypeError(first));
        }
    };
}
