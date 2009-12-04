/*
 * Primitive.java
 *
 * Copyright (C) 2002-2005 Peter Graves
 * $Id: Primitive.java 12254 2009-11-06 20:07:54Z ehuelsmann $
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

public class Primitive extends Function
{
    public Primitive(LispObject name)
    {
        super(name);
    }

    public Primitive(String name)
    {
        super(name);
    }

    public Primitive(Symbol symbol, String arglist)
    {
        super(symbol, arglist);
    }

    public Primitive(Symbol symbol, String arglist, String docstring)
    {
        super(symbol, arglist, docstring);
    }

    public Primitive(String name, String arglist)
    {
        super(name, arglist);
    }

    public Primitive(LispObject name, LispObject lambdaList)
    {
        super(name, lambdaList);
    }

    public Primitive(String name, Package pkg)
    {
        super(name, pkg);
    }

    public Primitive(String name, Package pkg, boolean exported)
    {
        super(name, pkg, exported);
    }

    public Primitive(String name, Package pkg, boolean exported,
                     String arglist)
    {
        super(name, pkg, exported, arglist);
    }

    public Primitive(String name, Package pkg, boolean exported,
                     String arglist, String docstring)
    {
        super(name, pkg, exported, arglist, docstring);
    }

    @Override
    public LispObject typeOf()
    {
        return Symbol.COMPILED_FUNCTION;
    }
    
    final static LispObject[] ZERO_ARGS = new LispObject[0];

    @Override
    public LispObject execute()
    {
        return execute(ZERO_ARGS);
    }

    @Override
    public LispObject execute(LispObject arg)
    {
      return execute(new LispObject[] { arg });
    }

    @Override
    public LispObject execute(LispObject first, LispObject second)

    {
      return execute(new LispObject[] { first, second});

    }

    @Override
    public LispObject execute(LispObject first, LispObject second,
                              LispObject third)

    {
        return execute(new LispObject[] { first, second, third });
    }

    @Override
    public LispObject execute(LispObject first, LispObject second,
                              LispObject third, LispObject fourth)

    {
      return execute(new LispObject[] { first, second, third, fourth });
    }

    @Override
    public LispObject execute(LispObject first, LispObject second,
                              LispObject third, LispObject fourth,
                              LispObject fifth)

    {
      return execute(new LispObject[] { first, second, third, fourth, fifth});

    }

    @Override
    public LispObject execute(LispObject first, LispObject second,
                              LispObject third, LispObject fourth,
                              LispObject fifth, LispObject sixth)

    {
      return execute(new LispObject[] { first, second, third, fourth, fifth, sixth });
    }

    @Override
    public LispObject execute(LispObject first, LispObject second,
                              LispObject third, LispObject fourth,
                              LispObject fifth, LispObject sixth,
                              LispObject seventh)

    {
      return execute(new LispObject[] { first, second, third, fourth, fifth, sixth, seventh });
    }

    @Override
    public LispObject execute(LispObject first, LispObject second,
                              LispObject third, LispObject fourth,
                              LispObject fifth, LispObject sixth,
                              LispObject seventh, LispObject eighth)

    {
      return execute(new LispObject[] { first, second, third, fourth, fifth, sixth, seventh, eighth });
    }
}
