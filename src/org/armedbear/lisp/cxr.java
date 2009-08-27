/*
 * cxr.java
 *
 * Copyright (C) 2003-2005 Peter Graves
 * $Id: cxr.java 11488 2008-12-27 10:50:33Z ehuelsmann $
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

public final class cxr extends LispFile
{
  // ### set-car
  private static final Primitive SET_CAR =
    new Primitive("set-car", PACKAGE_SYS, true)
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
        first.setCar(second);
        return second;
      }
    };

  // ### set-cdr
  private static final Primitive SET_CDR =
    new Primitive("set-cdr", PACKAGE_SYS, true)
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
        first.setCdr(second);
        return second;
      }
    };

  // ### car
  private static final Primitive CAR = new Primitive(SymbolConstants.CAR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.first();
      }
    };

  // ### cdr
  private static final Primitive CDR = new Primitive(SymbolConstants.CDR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.rest();
      }
    };

  // ### caar
  private static final Primitive CAAR = new Primitive(SymbolConstants.CAAR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.first().first();
      }
    };

  // ### cadr
  private static final Primitive CADR = new Primitive(SymbolConstants.CADR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.cadr();
      }
    };

  // ### cdar
  private static final Primitive CDAR = new Primitive(SymbolConstants.CDAR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.first().rest();
      }
    };

  // ### cddr
  private static final Primitive CDDR = new Primitive(SymbolConstants.CDDR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.rest().rest();
      }
    };

  // ### caddr
  private static final Primitive CADDR = new Primitive(SymbolConstants.CADDR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.caddr();
      }
    };

  // ### caadr
  private static final Primitive CAADR = new Primitive(SymbolConstants.CAADR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.rest().first().first();
      }
    };

  // ### caaar
  private static final Primitive CAAAR = new Primitive(SymbolConstants.CAAAR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.first().first().first();
      }
    };

  // ### cdaar
  private static final Primitive CDAAR = new Primitive(SymbolConstants.CDAAR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.first().first().rest();
      }
    };

  // ### cddar
  private static final Primitive CDDAR = new Primitive(SymbolConstants.CDDAR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.first().rest().rest();
      }
    };

  // ### cdddr
  private static final Primitive CDDDR = new Primitive(SymbolConstants.CDDDR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.rest().rest().rest();
      }
    };

  // ### cadar
  private static final Primitive CADAR = new Primitive(SymbolConstants.CADAR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.first().rest().first();
      }
    };

  // ### cdadr
  private static final Primitive CDADR = new Primitive(SymbolConstants.CDADR, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.rest().first().rest();
      }
    };

  // ### first
  private static final Primitive FIRST = new Primitive(SymbolConstants.FIRST, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.first();
      }
    };

  // ### second
  private static final Primitive SECOND = new Primitive(SymbolConstants.SECOND, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.cadr();
      }
    };

  // ### third
  private static final Primitive THIRD = new Primitive(SymbolConstants.THIRD, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.caddr();
      }
    };

  // ### fourth
  private static final Primitive FOURTH = new Primitive(SymbolConstants.FOURTH, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.rest().rest().cadr();
      }
    };

  // ### rest
  private static final Primitive REST = new Primitive(SymbolConstants.REST, "list")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg.rest();
      }
    };
}
