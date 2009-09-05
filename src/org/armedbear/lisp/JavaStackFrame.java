/*
 * JavaStackFrame.java
 *
 * Copyright (C) 2009 Mark Evenson
 * $Id: JavaStackFrame.java 12105 2009-08-19 14:51:56Z mevenson $
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
public class JavaStackFrame 
  extends StackFrame
{
  public final StackTraceElement javaFrame;

  public JavaStackFrame(StackTraceElement javaFrame)
  {
    this.javaFrame = javaFrame;
  }

  @Override
  public LispObject typeOf() { 
    return SymbolConstants.JAVA_STACK_FRAME; 
  }

  @Override
  public LispObject classOf()   { return BuiltInClass.JAVA_STACK_FRAME; }

  @Override
  public String writeToString() { 
    String result = null;
    final String JAVA_STACK_FRAME = "JAVA-STACK-FRAME";
    try {
      result = unreadableString(JAVA_STACK_FRAME + " " 
				+ toLispString().toString()); 
    } catch (ConditionThrowable t) {
      Debug.trace("Implementation error: ");
      Debug.trace(t);
      result = unreadableString(JAVA_STACK_FRAME);
    }
    return result;
  }

  @Override
  public LispObject typep(LispObject typeSpecifier) 
     throws ConditionThrowable
  {
     if (typeSpecifier == SymbolConstants.JAVA_STACK_FRAME)
       return T;
     if (typeSpecifier == BuiltInClass.JAVA_STACK_FRAME)
       return T;
     return super.typep(typeSpecifier);
   }

  static final Symbol CLASS = internKeyword("CLASS");
  static final Symbol METHOD = internKeyword("METHOD");
  static final Symbol FILE = internKeyword("FILE");
  static final Symbol LINE = internKeyword("LINE");
  static final Symbol NATIVE_METHOD = internKeyword("NATIVE-METHOD");

  public LispObject toLispList() throws ConditionThrowable
  {
    LispObject result = Lisp.NIL;
    
    if ( javaFrame == null) 
      return result;

    result = result.push(CLASS);
    result = result.push(new SimpleString(javaFrame.getClassName()));
    result = result.push(METHOD);
    result = result.push(new SimpleString(javaFrame.getMethodName()));
    result = result.push(FILE);
    result = result.push(new SimpleString(javaFrame.getFileName()));
    result = result.push(LINE);
    result = result.push(Fixnum.makeFixnum(javaFrame.getLineNumber()));
    if (javaFrame.isNativeMethod()) {
      result = result.push(NATIVE_METHOD);
      result = result.push(SymbolConstants.T);
    }

    return result.nreverse();
  }

  @Override
  public SimpleString toLispString() 
    throws ConditionThrowable 
  {
    return new SimpleString(javaFrame.toString());
  }

  @Override
  public LispObject getParts() 
    throws ConditionThrowable
  { 
    LispObject result = NIL;
    result = result.push(makeCons("CLASS", 
				  new SimpleString(javaFrame.getClassName())));
    result = result.push(makeCons("METHOD", 
				  new SimpleString(javaFrame.getMethodName())));
    result = result.push(makeCons("FILE", 
				  new SimpleString(javaFrame.getFileName())));
    result = result.push(makeCons("LINE",
				  Fixnum.makeFixnum(javaFrame.getLineNumber())));
    result = result.push(makeCons("NATIVE-METHOD",
				  javaFrame.isNativeMethod()?T:NIL));
    return result.nreverse();
  }
}
