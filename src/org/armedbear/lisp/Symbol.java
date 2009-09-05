/*
 * java
 *
 * Copyright (C) 2002-2007 Peter Graves
 * $Id: java 12105 2009-08-19 14:51:56Z mevenson $
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
import static org.armedbear.lisp.SymbolConstants.T;
import static org.armedbear.lisp.SymbolConstants.*;
import static org.armedbear.lisp.Lisp.*;

import java.math.BigInteger;

import com.cyc.tool.subl.jrtl.nativeCode.type.symbol.SubLSymbolImpl;

public interface Symbol extends LispObject
{
  // Bit flags.
  static final int FLAG_SPECIAL           = 0x0001;
  static final int FLAG_CONSTANT          = 0x0002;
  static final int FLAG_BUILT_IN_FUNCTION = 0x0004;
  LispObject symbolValue(LispThread thread);
  void setSymbolFunction(LispObject second);
  LispObject getSymbolSetfFunctionOrDie()  throws ConditionThrowable;
  LispObject symbolValueNoThrow();
  LispObject symbolValue();
  void setSymbolValue(LispObject t);
void setSpecial(boolean b);
void setPackage(LispObject nil);
void setBuiltInFunction(boolean b);
boolean isConstant();
void initializeSpecial(LispObject t);
void initializeConstant(LispObject t);
boolean isBuiltInFunction();
String getQualifiedName();
String getName();
LispObject getLispPackage();
SimpleString getSymbolName();

}
