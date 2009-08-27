/*
 * LispObject.java
 *
 * Copyright (C) 2002-2007 Peter Graves
 * $Id: LispObject.java 12111 2009-08-23 09:26:13Z ehuelsmann $
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
import static org.armedbear.lisp.Lisp.*;

import java.math.BigInteger;

//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLCharacter;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLCons;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLEnvironment;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLGuid;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLHashtable;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLHashtableIterator;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLKeyhash;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLKeyhashIterator;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLList;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLLock;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObject;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLProcess;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLReadWriteLock;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLRegexPattern;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLSemaphore;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLSequence;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLString;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLStruct;
//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLVector;
//import com.cyc.tool.subl.jrtl.nativeCode.type.exception.InvalidSubLExpressionException;
//import com.cyc.tool.subl.jrtl.nativeCode.type.exception.SubLException;
//import com.cyc.tool.subl.jrtl.nativeCode.type.number.SubLDoubleFloat;
//import com.cyc.tool.subl.jrtl.nativeCode.type.number.SubLFixnum;
//import com.cyc.tool.subl.jrtl.nativeCode.type.number.SubLInteger;
//import com.cyc.tool.subl.jrtl.nativeCode.type.number.SubLNumber;
//import com.cyc.tool.subl.jrtl.nativeCode.type.operator.SubLFunction;
//import com.cyc.tool.subl.jrtl.nativeCode.type.operator.SubLMacro;
//import com.cyc.tool.subl.jrtl.nativeCode.type.stream.SubLInputBinaryStream;
//import com.cyc.tool.subl.jrtl.nativeCode.type.stream.SubLInputStream;
//import com.cyc.tool.subl.jrtl.nativeCode.type.stream.SubLInputTextStream;
//import com.cyc.tool.subl.jrtl.nativeCode.type.stream.SubLOutputBinaryStream;
//import com.cyc.tool.subl.jrtl.nativeCode.type.stream.SubLOutputStream;
//import com.cyc.tool.subl.jrtl.nativeCode.type.stream.SubLOutputTextStream;
//import com.cyc.tool.subl.jrtl.nativeCode.type.stream.SubLStream;
//import com.cyc.tool.subl.jrtl.nativeCode.type.symbol.SubLPackage;
//import com.cyc.tool.subl.jrtl.nativeCode.type.symbol.SubLSymbol;


abstract public class LispObject implements ILispObject
{	
	
   abstract public boolean isSubL();	
// final methods from AbstractSubLObject
	
//	public SubLObject fifth() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject fourth() {
//		unimplemeted();
//		return null;
//	}
//	public SubLObject seventh() {
//		unimplemeted();
//		return null;
//	}
//
//
//	public SubLObject sixth() {
//		unimplemeted();
//		return null;
//	}
//
//	public int superHash() {
//		unimplemeted();
//		return 0;
//	}
//
//	public SubLObject tenth() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject ninth() {
//		unimplemeted();
//		return null;
//	}
//	public SubLObject eighth() {
//		unimplemeted();
//		return null;
//	}
//
//	public void enforceType(SubLSymbol predicate) throws SubLException {
//		unimplemeted();
//		
//	}
//	public void enforceTypeInternal(SubLSymbol predicate) throws SubLException {
//		unimplemeted();
//		
//	}
//	public void checkType(SubLSymbol predicate) throws SubLException {
//		unimplemeted();
//		
//	}
//
//	public void checkTypeInternal(SubLSymbol predicate) throws SubLException {
//		unimplemeted();
//		
//	}
//
//	public SubLProcess toProcess() {
//		unimplemeted();
//		return null;
//	}
//	public SubLNumber toNumber() {
//		unimplemeted();
//		return null;
//	}
    public int hashCode() {
    	return clHash();
	}
    
    public int clHash() {
    	return super.hashCode();
	}
	
	private void unimplemeted() {
	    throw new RuntimeException("unimpented SubLObject method!");		
	}
	

	private void abclMethod() {
	    throw new RuntimeException("unimpented LispObject method!");		
	}
	
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			unimplemeted();
			e.printStackTrace();
			return this;
		}
	}

	public boolean canFastHash() {
		unimplemeted();
		return false;
	}
	

	public int hashCode(int currentDepth) {
		unimplemeted();
		return 0;
	}

	public boolean isAtom() {
		unimplemeted();
		return false;
	}

	public boolean isBigIntegerBignum() {
		unimplemeted();
		return false;
	}

	public boolean isBignum() {
		unimplemeted();
		return false;
	}

	public boolean isBoolean() {
		unimplemeted();
		return false;
	}

	public boolean isChar() {
		unimplemeted();
		return false;
	}

	public boolean isCons() {
		unimplemeted();
		return false;
	}

	public boolean isDouble() {
		unimplemeted();
		return false;
	}

	public boolean isEnvironment() {
		unimplemeted();
		return false;
	}

	public boolean isError() {
		unimplemeted();
		return false;
	}

	public boolean isFixnum() {
		unimplemeted();
		return false;
	}

	public boolean isFunction() {
		unimplemeted();
		return false;
	}

	public boolean isFunctionSpec() {
		unimplemeted();
		return false;
	}

	public boolean isGuid() {
		unimplemeted();
		return false;
	}

	public boolean isHashtable() {
		unimplemeted();
		return false;
	}

	public boolean isHashtableIterator() {
		unimplemeted();
		return false;
	}

	public boolean isIntBignum() {
		unimplemeted();
		return false;
	}

	public boolean isInteger() {
		unimplemeted();
		return false;
	}

	public boolean isKeyhash() {
		unimplemeted();
		return false;
	}

	public boolean isKeyhashIterator() {
		unimplemeted();
		return false;
	}

	public boolean isKeyword() {
		unimplemeted();
		return false;
	}

	public boolean isList() {
		unimplemeted();
		return false;
	}

	public boolean isLock() {
		unimplemeted();
		return false;
	}

	public boolean isLongBignum() {
		unimplemeted();
		return false;
	}

	public boolean isMacroOperator() {
		unimplemeted();
		return false;
	}

	public boolean isNil() {
		unimplemeted();
		return false;
	}

	public boolean isNumber() {
		unimplemeted();
		return false;
	}

	public boolean isPackage() {
		unimplemeted();
		return false;
	}

	public boolean isProcess() {
		unimplemeted();
		return false;
	}

	public boolean isReadWriteLock() {
		unimplemeted();
		return false;
	}

	public boolean isRegexPattern() {
		unimplemeted();
		return false;
	}

	public boolean isSemaphore() {
		unimplemeted();
		return false;
	}

	public boolean isSequence() {
		unimplemeted();
		return false;
	}

	public boolean isStream() {
		unimplemeted();
		return false;
	}

	public boolean isString() {
		unimplemeted();
		return false;
	}

	public boolean isStructure() {
		unimplemeted();
		return false;
	}

	public boolean isSymbol() {
		unimplemeted();
		return false;
	}

	public boolean isVector() {
		unimplemeted();
		return false;
	}
	
	public boolean isArrayBased() {
		unimplemeted();
		return false;
	}

	public int getNumSize() {
		unimplemeted();
		return 0;
	}
	
	public char charValue() {
		unimplemeted();
		return 0;
	}

	public String getFileDesignator() {
		unimplemeted();
		return null;
	}

	public String getString() {
		unimplemeted();
		return null;
	}


	public String toTypeName() {
		unimplemeted();
		return null;
	}

	public void setHotCount(int value) {
		unimplemeted();		
	}
	
	public void incrementHotCount() {
		unimplemeted();		
	}

	public int getHotCount() {
		unimplemeted();
		return 0;
	}

	public LispObject ABS() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject AREF(int index) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject AREF(LispObject index) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject ATOM() {
		abclMethod();
		return null;
	}

	public LispObject CHAR(int index) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject CHARACTERP() {
		abclMethod();
		return null;
	}

	public LispObject COMPLEXP() {
		abclMethod();
		return null;
	}

	public LispObject CONSTANTP() {
		abclMethod();
		return null;
	}

	public LispObject DENOMINATOR() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject ENDP() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject EQ(LispObject obj) {
		abclMethod();
		return null;
	}

	public LispObject EQL(LispObject obj) {
		abclMethod();
		return null;
	}

	public LispObject EQUAL(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject EVENP() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject FLOATP() {
		abclMethod();
		return null;
	}

	public LispObject INTEGERP() {
		abclMethod();
		return null;
	}

	public LispObject IS_E(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject IS_GE(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject IS_GT(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject IS_LE(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject IS_LT(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject IS_NE(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject LDB(int size, int position) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject LENGTH() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject LISTP() {
		abclMethod();
		return null;
	}

	public LispObject LOGAND(int n) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject LOGAND(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject LOGIOR(int n) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject LOGIOR(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject LOGNOT() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject LOGXOR(int n) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject LOGXOR(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject MINUSP() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject MOD(LispObject divisor) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject MOD(int divisor) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject NOT() {
		abclMethod();
		return null;
	}

	public LispObject NTH(int index) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject NTH(LispObject arg) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject NUMBERP() {
		abclMethod();
		return null;
	}

	public LispObject NUMERATOR() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject ODDP() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject PLUSP() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject RATIONALP() {
		abclMethod();
		return null;
	}

	public LispObject REALP() {
		abclMethod();
		return null;
	}

	public LispObject RPLACA(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject RPLACD(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject SCHAR(int index) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject SIMPLE_STRING_P() {
		abclMethod();
		return null;
	}

	public LispObject SLOT_VALUE(LispObject slotName) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject STRING() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject STRINGP() {
		abclMethod();
		return null;
	}

	public LispObject SVREF(int index) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject SYMBOLP() {
		abclMethod();
		return null;
	}

	public LispObject VECTORP() {
		abclMethod();
		return null;
	}

	public LispObject VECTOR_PUSH_EXTEND(LispObject element)
			throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject VECTOR_PUSH_EXTEND(LispObject element,
			LispObject extension) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject ZEROP() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject add(int n) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject add(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public int aref(int index) throws ConditionThrowable {
		abclMethod();
		return 0;
	}

	public long aref_long(int index) throws ConditionThrowable {
		abclMethod();
		return 0;
	}

	public void aset(int index, int n) throws ConditionThrowable {
		abclMethod();
		
	}

	public void aset(int index, LispObject newValue) throws ConditionThrowable {
		abclMethod();
		
	}

	public void aset(LispObject index, LispObject newValue)
			throws ConditionThrowable {
		abclMethod();
		
	}

	public LispObject ash(int shift) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject ash(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public boolean atom() {
		abclMethod();
		return false;
	}

	public LispObject CDDR() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public char[] chars() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject classOf() {
		abclMethod();
		return null;
	}

	public boolean constantp() {
		abclMethod();
		return false;
	}

	public LispObject[] copyToArray() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject decr() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject dispatch(LispObject[] args) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject divideBy(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public double doubleValue() throws ConditionThrowable {
		abclMethod();
		return 0;
	}

	public LispObject elt(int index) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public boolean endp() throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean eql(char c) {
		abclMethod();
		return false;
	}

	public boolean eql(int n) {
		abclMethod();
		return false;
	}

	public boolean eql(LispObject obj) {
		abclMethod();
		return false;
	}

	public boolean equal(int n) {
		abclMethod();
		return false;
	}

	public boolean equal(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean equalp(int n) {
		abclMethod();
		return false;
	}

	public boolean equalp(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public LispObject execute(LispObject args, Environment env)
			throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject execute() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject execute(LispObject arg) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject execute(LispObject first, LispObject second)
			throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject execute(LispObject first, LispObject second,
			LispObject third) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject execute(LispObject first, LispObject second,
			LispObject third, LispObject fourth) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject execute(LispObject first, LispObject second,
			LispObject third, LispObject fourth, LispObject fifth)
			throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject execute(LispObject first, LispObject second,
			LispObject third, LispObject fourth, LispObject fifth,
			LispObject sixth) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject execute(LispObject first, LispObject second,
			LispObject third, LispObject fourth, LispObject fifth,
			LispObject sixth, LispObject seventh) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject execute(LispObject first, LispObject second,
			LispObject third, LispObject fourth, LispObject fifth,
			LispObject sixth, LispObject seventh, LispObject eighth)
			throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject execute(LispObject[] args) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject CAR() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public float floatValue() throws ConditionThrowable {
		abclMethod();
		return 0;
	}

	public boolean floatp() {
		abclMethod();
		return false;
	}

	public boolean getBooleanValue() {
		abclMethod();
		return false;
	}

	public int getCallCount() {
		abclMethod();
		return 0;
	}

	public LispObject getDescription() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject getDocumentation(LispObject docType)
			throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public int getFixnumSlotValue(int index) throws ConditionThrowable {
		abclMethod();
		return 0;
	}

	public LispObject getParts() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject getPropertyList() {
		abclMethod();
		return null;
	}

	public LispObject getSlotValue(int index) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public boolean getSlotValueAsBoolean(int index) throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public LispObject getSlotValue_0() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject getSlotValue_1() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject getSlotValue_2() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject getSlotValue_3() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public char[] getStringChars() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public String getStringValue() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject getSymbolFunction() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject getSymbolFunctionOrDie() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject getSymbolValue() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject incr() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public void incrementCallCount() {
		abclMethod();
		
	}

	public int intValue() throws ConditionThrowable {
		abclMethod();
		return 0;
	}

	public boolean isEqualTo(int n) throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isEqualTo(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isEven() throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isGreaterThan(int n) throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isGreaterThan(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isGreaterThanOrEqualTo(int n) throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isGreaterThanOrEqualTo(LispObject obj)
			throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isLessThan(int n) throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isLessThan(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isLessThanOrEqualTo(int n) throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isLessThanOrEqualTo(LispObject obj)
			throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isNegative() throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isNotEqualTo(int n) throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isNotEqualTo(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isOdd() throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isPositive() throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isSpecialOperator() throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public boolean isSpecialVariable() {
		abclMethod();
		return false;
	}

	public boolean isZero() throws ConditionThrowable {
		abclMethod();
		return false;
	}

	public Object javaInstance() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public Object javaInstance(Class<?> c) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public Object lockableInstance() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public long longValue() throws ConditionThrowable {
		abclMethod();
		return 0;
	}

	public LispObject multiplyBy(int n) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject multiplyBy(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject negate() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject noFillPointer() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject nreverse() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject nthcdr(int n) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public int psxhash() {
		abclMethod();
		return 0;
	}

	public int psxhash(int depth) {
		abclMethod();
		return 0;
	}

	public LispObject push(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public boolean rationalp() {
		abclMethod();
		return false;
	}

	public boolean realp() {
		abclMethod();
		return false;
	}

	public LispObject CDR() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject reverse() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject CADR() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public void setCallCount(int n) {
		abclMethod();
		
	}

	public void setCar(LispObject obj) throws ConditionThrowable {
		abclMethod();
		
	}

	public void setCdr(LispObject obj) throws ConditionThrowable {
		abclMethod();
		
	}

	public void setDocumentation(LispObject docType, LispObject documentation)
			throws ConditionThrowable {
		abclMethod();
		
	}

	public void setPropertyList(LispObject obj) {
		abclMethod();
		
	}

	public void setSlotValue(int index, LispObject value)
			throws ConditionThrowable {
		abclMethod();
		
	}

	public void setSlotValue(LispObject slotName, LispObject newValue)
			throws ConditionThrowable {
		abclMethod();
		
	}

	public void setSlotValue_0(LispObject value) throws ConditionThrowable {
		abclMethod();
		
	}

	public void setSlotValue_1(LispObject value) throws ConditionThrowable {
		abclMethod();
		
	}

	public void setSlotValue_2(LispObject value) throws ConditionThrowable {
		abclMethod();
		
	}

	public void setSlotValue_3(LispObject value) throws ConditionThrowable {
		abclMethod();
		
	}

	public int size() throws ConditionThrowable {
		abclMethod();
		return 0;
	}

	public LispObject subtract(int n) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject subtract(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public void svset(int index, LispObject newValue) throws ConditionThrowable {
		abclMethod();
		
	}

	public int sxhash() {
		abclMethod();
		return 0;
	}

	public LispObject CADDR() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject truncate(LispObject obj) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public LispObject typeOf() {
		abclMethod();
		return null;
	}

	public LispObject typep(LispObject typeSpecifier) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public String unreadableString(String s) {
		abclMethod();
		return null;
	}

	public String unreadableString(Symbol symbol) throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public void vectorPushExtend(LispObject element) throws ConditionThrowable {
		abclMethod();
		
	}

	public String writeToString() throws ConditionThrowable {
		abclMethod();
		return null;
	}

	public BigInteger bigIntegerValue() {
		unimplemeted();
		return null;
	}

//	public SubLObject add(SubLObject num) {
//		unimplemeted();
//		return null;
//	}
//
//	public void addKey(SubLObject key) {
//		unimplemeted();
//		
//	}
//
//	public SubLList asArrayList() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLList asConsList() {
//		unimplemeted();
//		return null;
//	}
//
//	public void bind(SubLObject newValue, SubLObject[] bindings) {
//		unimplemeted();
//		
//	}
//
//	public void checkType(SubLSymbol predicate) throws SubLException {
//		unimplemeted();
//		
//	}
//
//	public void checkTypeInternal(SubLSymbol predicate) throws SubLException {
//		unimplemeted();
//		
//	}
//
//	public SubLObject currentBinding(SubLObject[] bindings) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject dec() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject eighth() {
//		unimplemeted();
//		return null;
//	}
//
//	public void enforceType(SubLSymbol predicate) throws SubLException {
//		unimplemeted();
//		
//	}
//
//	public void enforceTypeInternal(SubLSymbol predicate) throws SubLException {
//		unimplemeted();
//		
//	}
//
//	public boolean eql(SubLObject obj) {
//		unimplemeted();
//		return false;
//	}
//
//	public boolean equal(SubLObject obj) {
//		unimplemeted();
//		return false;
//	}
//
//	public boolean equalp(SubLObject obj) {
//		unimplemeted();
//		return false;
//	}
//
//	public SubLObject eval(SubLEnvironment env)
//			throws InvalidSubLExpressionException {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject fifth() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject first() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject fourth() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject get(SubLObject obj) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject get(int index) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLCharacter getCharacter(int index) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField(int fieldNum) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField0() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField1() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField10() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField11() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField12() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField13() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField14() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField15() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField16() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField17() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField18() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField19() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField2() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField20() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField3() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField4() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField5() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField6() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField7() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField8() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject getField9() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLFunction getFunc() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLStream getStream(boolean followSynonymStream) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLSymbol getType() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLFixnum getTypeCode() {
//		unimplemeted();
//		return null;
//	}
//
//	public boolean hasKey(SubLObject obj) {
//		unimplemeted();
//		return false;
//	}
//
//	public SubLObject inc() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject last(int i) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject makeCopy() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject makeDeepCopy() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject mult(SubLObject num) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject ninth() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject nthCdr(int index) {
//		unimplemeted();
//		return null;
//	}
//
//	public boolean numE(SubLObject x) {
//		unimplemeted();
//		return false;
//	}
//
//	public boolean numG(SubLObject x) {
//		unimplemeted();
//		return false;
//	}
//
//	public boolean numGE(SubLObject x) {
//		unimplemeted();
//		return false;
//	}
//
//	public boolean numL(SubLObject x) {
//		unimplemeted();
//		return false;
//	}
//
//	public boolean numLE(SubLObject x) {
//		unimplemeted();
//		return false;
//	}
//
//	public SubLObject put(SubLObject key, SubLObject value) {
//		unimplemeted();
//		return null;
//	}
//
//	public void rebind(SubLObject oldValue, SubLObject[] bindings) {
//		unimplemeted();
//		
//	}
//
//	public boolean remKey(SubLObject obj) {
//		unimplemeted();
//		return false;
//	}
//
//	public SubLObject remove(SubLObject obj) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject rest() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLSequence reverse(boolean isDestructive) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject second() {
//		unimplemeted();
//		return null;
//	}
//
//	public void set(int index, SubLObject val) {
//		unimplemeted();
//		
//	}
//
//	public void setField(int fieldNum, SubLObject value) {
//		unimplemeted();
//		
//	}
//
//	public SubLObject setField0(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField1(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField10(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField11(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField12(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField13(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField14(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField15(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField16(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField17(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField18(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField19(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField2(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField20(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField3(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField4(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField5(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField6(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField7(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField8(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject setField9(SubLObject newVal) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLCons setFirst(SubLObject first) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLCons setRest(SubLObject rest) {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject seventh() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject sixth() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject sub(SubLObject num) {
//		unimplemeted();
//		return null;
//	}
//
//	public int superHash() {
//		unimplemeted();
//		return 0;
//	}
//
//	public SubLObject tenth() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLObject third() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLCharacter toChar() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLCons toCons() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLDoubleFloat toDouble() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLEnvironment toEnv() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLFixnum toFixnum() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLGuid toGuid() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLHashtable toHashtable() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLHashtableIterator toHashtableIterator() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLInputBinaryStream toInputBinaryStream() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLInputStream toInputStream() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLInputTextStream toInputTextStream() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLInteger toInteger() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLKeyhash toKeyhash() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLKeyhashIterator toKeyhashIterator() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLList toList() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLLock toLock() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLMacro toMacro() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLNumber toNumber() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLOutputBinaryStream toOutputBinaryStream() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLOutputStream toOutputStream() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLOutputTextStream toOutputTextStream() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLPackage toPackage() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLProcess toProcess() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLReadWriteLock toReadWriteLock() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLRegexPattern toRegexPattern() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLSemaphore toSemaphore() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLSequence toSeq() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLString toStr() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLStruct toStruct() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLSymbol toSymbol() {
//		unimplemeted();
//		return null;
//	}
//
//	public SubLVector toVect() {
//		unimplemeted();
//		return null;
//	}
}
