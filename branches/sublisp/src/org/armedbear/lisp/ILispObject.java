package org.armedbear.lisp;

public interface ILispObject {

	public abstract LispObject typeOf();

	public abstract LispObject classOf();

	public abstract LispObject getDescription() throws ConditionThrowable;

	public abstract LispObject getParts() throws ConditionThrowable;

	public abstract boolean getBooleanValue();

	public abstract LispObject typep(LispObject typeSpecifier)
			throws ConditionThrowable;

	public abstract boolean constantp();

	public abstract LispObject CONSTANTP();

	public abstract LispObject ATOM();

	public abstract boolean atom();

	public abstract Object javaInstance() throws ConditionThrowable;

	public abstract Object javaInstance(Class<?> c) throws ConditionThrowable;

	/** This method returns 'this' by default, but allows
	 * objects to return different values to increase Java
	 * interoperability
	 * 
	 * @return An object to be used with synchronized, wait, notify, etc
	 * @throws org.armedbear.lisp.ConditionThrowable
	 */
	public abstract Object lockableInstance() throws ConditionThrowable;

	public abstract LispObject first() throws ConditionThrowable;

	public abstract void setCar(LispObject obj) throws ConditionThrowable;

	public abstract LispObject RPLACA(LispObject obj) throws ConditionThrowable;

	public abstract LispObject rest() throws ConditionThrowable;

	public abstract void setCdr(LispObject obj) throws ConditionThrowable;

	public abstract LispObject RPLACD(LispObject obj) throws ConditionThrowable;

	public abstract LispObject cadr() throws ConditionThrowable;

	public abstract LispObject cddr() throws ConditionThrowable;

	public abstract LispObject caddr() throws ConditionThrowable;

	public abstract LispObject nthcdr(int n) throws ConditionThrowable;

	public abstract LispObject push(LispObject obj) throws ConditionThrowable;

	public abstract LispObject EQ(LispObject obj);

	public abstract boolean eql(char c);

	public abstract boolean eql(int n);

	public abstract boolean eql(LispObject obj);

	public abstract LispObject EQL(LispObject obj);

	public abstract LispObject EQUAL(LispObject obj) throws ConditionThrowable;

	public abstract boolean equal(int n);

	public abstract boolean equal(LispObject obj) throws ConditionThrowable;

	public abstract boolean equalp(int n);

	public abstract boolean equalp(LispObject obj) throws ConditionThrowable;

	public abstract LispObject ABS() throws ConditionThrowable;

	public abstract LispObject NUMERATOR() throws ConditionThrowable;

	public abstract LispObject DENOMINATOR() throws ConditionThrowable;

	public abstract LispObject EVENP() throws ConditionThrowable;

	public abstract boolean isEven() throws ConditionThrowable;

	public abstract LispObject ODDP() throws ConditionThrowable;

	public abstract boolean isOdd() throws ConditionThrowable;

	public abstract LispObject PLUSP() throws ConditionThrowable;

	public abstract boolean isPositive() throws ConditionThrowable;

	public abstract LispObject MINUSP() throws ConditionThrowable;

	public abstract boolean isNegative() throws ConditionThrowable;

	public abstract LispObject NUMBERP();

	public abstract boolean isNumber();

	public abstract LispObject ZEROP() throws ConditionThrowable;

	public abstract boolean isZero() throws ConditionThrowable;

	public abstract LispObject COMPLEXP();

	public abstract LispObject FLOATP();

	public abstract boolean floatp();

	public abstract LispObject INTEGERP();

	public abstract boolean isInteger();

	public abstract LispObject RATIONALP();

	public abstract boolean rationalp();

	public abstract LispObject REALP();

	public abstract boolean realp();

	public abstract LispObject STRINGP();

	public abstract boolean isString();

	public abstract LispObject SIMPLE_STRING_P();

	public abstract LispObject VECTORP();

	public abstract boolean isVector();

	public abstract LispObject CHARACTERP();

	public abstract boolean isChar();

	public abstract int seqLength() throws ConditionThrowable;

	public abstract LispObject LENGTH() throws ConditionThrowable;

	public abstract LispObject CHAR(int index) throws ConditionThrowable;

	public abstract LispObject SCHAR(int index) throws ConditionThrowable;

	public abstract LispObject NTH(int index) throws ConditionThrowable;

	public abstract LispObject NTH(LispObject arg) throws ConditionThrowable;

	public abstract LispObject elt(int index) throws ConditionThrowable;

	public abstract LispObject reverse() throws ConditionThrowable;

	public abstract LispObject nreverse() throws ConditionThrowable;

	public abstract long aref_long(int index) throws ConditionThrowable;

	public abstract int aref(int index) throws ConditionThrowable;

	public abstract LispObject AREF(int index) throws ConditionThrowable;

	public abstract LispObject AREF(LispObject index) throws ConditionThrowable;

	public abstract void aset(int index, int n) throws ConditionThrowable;

	public abstract void aset(int index, LispObject newValue)
			throws ConditionThrowable;

	public abstract void aset(LispObject index, LispObject newValue)
			throws ConditionThrowable;

	public abstract LispObject SVREF(int index) throws ConditionThrowable;

	public abstract void svset(int index, LispObject newValue)
			throws ConditionThrowable;

	public abstract void vectorPushExtend(LispObject element)
			throws ConditionThrowable;

	public abstract LispObject VECTOR_PUSH_EXTEND(LispObject element)
			throws ConditionThrowable;

	public abstract LispObject VECTOR_PUSH_EXTEND(LispObject element,
			LispObject extension) throws ConditionThrowable;

	public abstract LispObject noFillPointer() throws ConditionThrowable;

	public abstract LispObject[] copyToArray() throws ConditionThrowable;

	public abstract LispObject SYMBOLP();

	public abstract boolean isList();

	public abstract LispObject LISTP();

	public abstract boolean endp() throws ConditionThrowable;

	public abstract LispObject ENDP() throws ConditionThrowable;

	public abstract LispObject NOT();

	public abstract boolean isSpecialOperator() throws ConditionThrowable;

	public abstract boolean isSpecialVariable();

	public abstract LispObject getDocumentation(LispObject docType)
			throws ConditionThrowable;

	public abstract void setDocumentation(LispObject docType,
			LispObject documentation) throws ConditionThrowable;

	public abstract LispObject getPropertyList();

	public abstract void setPropertyList(LispObject obj);

	public abstract LispObject getSymbolValue() throws ConditionThrowable;

	public abstract LispObject getSymbolFunction() throws ConditionThrowable;

	public abstract LispObject getSymbolFunctionOrDie()
			throws ConditionThrowable;

	public abstract String writeToString() throws ConditionThrowable;

	public abstract String unreadableString(String s);

	public abstract String unreadableString(Symbol symbol)
			throws ConditionThrowable;

	// Special operator
	public abstract LispObject execute(LispObject args, Environment env)
			throws ConditionThrowable;

	public abstract LispObject execute() throws ConditionThrowable;

	public abstract LispObject execute(LispObject arg)
			throws ConditionThrowable;

	public abstract LispObject execute(LispObject first, LispObject second)
			throws ConditionThrowable;

	public abstract LispObject execute(LispObject first, LispObject second,
			LispObject third) throws ConditionThrowable;

	public abstract LispObject execute(LispObject first, LispObject second,
			LispObject third, LispObject fourth) throws ConditionThrowable;

	public abstract LispObject execute(LispObject first, LispObject second,
			LispObject third, LispObject fourth, LispObject fifth)
			throws ConditionThrowable;

	public abstract LispObject execute(LispObject first, LispObject second,
			LispObject third, LispObject fourth, LispObject fifth,
			LispObject sixth) throws ConditionThrowable;

	public abstract LispObject execute(LispObject first, LispObject second,
			LispObject third, LispObject fourth, LispObject fifth,
			LispObject sixth, LispObject seventh) throws ConditionThrowable;

	public abstract LispObject execute(LispObject first, LispObject second,
			LispObject third, LispObject fourth, LispObject fifth,
			LispObject sixth, LispObject seventh, LispObject eighth)
			throws ConditionThrowable;

	public abstract LispObject execute(LispObject[] args)
			throws ConditionThrowable;

	// Used by COMPILE-MULTIPLE-VALUE-CALL.
	public abstract LispObject dispatch(LispObject[] args)
			throws ConditionThrowable;

	public abstract int intValue() throws ConditionThrowable;

	public abstract long longValue() throws ConditionThrowable;

	public abstract float floatValue() throws ConditionThrowable;

	public abstract double doubleValue() throws ConditionThrowable;

	public abstract LispObject incr() throws ConditionThrowable;

	public abstract LispObject decr() throws ConditionThrowable;

	public abstract LispObject negate() throws ConditionThrowable;

	public abstract LispObject add(int n) throws ConditionThrowable;

	public abstract LispObject add(LispObject obj) throws ConditionThrowable;

	public abstract LispObject subtract(int n) throws ConditionThrowable;

	public abstract LispObject subtract(LispObject obj)
			throws ConditionThrowable;

	public abstract LispObject multiplyBy(int n) throws ConditionThrowable;

	public abstract LispObject multiplyBy(LispObject obj)
			throws ConditionThrowable;

	public abstract LispObject divideBy(LispObject obj)
			throws ConditionThrowable;

	public abstract boolean isEqualTo(int n) throws ConditionThrowable;

	public abstract boolean isEqualTo(LispObject obj) throws ConditionThrowable;

	public abstract LispObject IS_E(LispObject obj) throws ConditionThrowable;

	public abstract boolean isNotEqualTo(int n) throws ConditionThrowable;

	public abstract boolean isNotEqualTo(LispObject obj)
			throws ConditionThrowable;

	public abstract LispObject IS_NE(LispObject obj) throws ConditionThrowable;

	public abstract boolean isLessThan(int n) throws ConditionThrowable;

	public abstract boolean isLessThan(LispObject obj)
			throws ConditionThrowable;

	public abstract LispObject IS_LT(LispObject obj) throws ConditionThrowable;

	public abstract boolean isGreaterThan(int n) throws ConditionThrowable;

	public abstract boolean isGreaterThan(LispObject obj)
			throws ConditionThrowable;

	public abstract LispObject IS_GT(LispObject obj) throws ConditionThrowable;

	public abstract boolean isLessThanOrEqualTo(int n)
			throws ConditionThrowable;

	public abstract boolean isLessThanOrEqualTo(LispObject obj)
			throws ConditionThrowable;

	public abstract LispObject IS_LE(LispObject obj) throws ConditionThrowable;

	public abstract boolean isGreaterThanOrEqualTo(int n)
			throws ConditionThrowable;

	public abstract boolean isGreaterThanOrEqualTo(LispObject obj)
			throws ConditionThrowable;

	public abstract LispObject IS_GE(LispObject obj) throws ConditionThrowable;

	public abstract LispObject truncate(LispObject obj)
			throws ConditionThrowable;

	public abstract LispObject MOD(LispObject divisor)
			throws ConditionThrowable;

	public abstract LispObject MOD(int divisor) throws ConditionThrowable;

	public abstract LispObject ash(int shift) throws ConditionThrowable;

	public abstract LispObject ash(LispObject obj) throws ConditionThrowable;

	public abstract LispObject LOGNOT() throws ConditionThrowable;

	public abstract LispObject LOGAND(int n) throws ConditionThrowable;

	public abstract LispObject LOGAND(LispObject obj) throws ConditionThrowable;

	public abstract LispObject LOGIOR(int n) throws ConditionThrowable;

	public abstract LispObject LOGIOR(LispObject obj) throws ConditionThrowable;

	public abstract LispObject LOGXOR(int n) throws ConditionThrowable;

	public abstract LispObject LOGXOR(LispObject obj) throws ConditionThrowable;

	public abstract LispObject LDB(int size, int position)
			throws ConditionThrowable;

	public abstract int sxhash();

	// For EQUALP hash tables.
	public abstract int psxhash();

	public abstract int psxhash(int depth);

	public abstract LispObject STRING() throws ConditionThrowable;

	public abstract char[] chars() throws ConditionThrowable;

	public abstract char[] getStringChars() throws ConditionThrowable;

	public abstract String getStringValue() throws ConditionThrowable;

	public abstract LispObject getSlotValue_0() throws ConditionThrowable;

	public abstract LispObject getSlotValue_1() throws ConditionThrowable;

	public abstract LispObject getSlotValue_2() throws ConditionThrowable;

	public abstract LispObject getSlotValue_3() throws ConditionThrowable;

	public abstract LispObject getSlotValue(int index)
			throws ConditionThrowable;

	public abstract int getFixnumSlotValue(int index) throws ConditionThrowable;

	public abstract boolean getSlotValueAsBoolean(int index)
			throws ConditionThrowable;

	public abstract void setSlotValue_0(LispObject value)
			throws ConditionThrowable;

	public abstract void setSlotValue_1(LispObject value)
			throws ConditionThrowable;

	public abstract void setSlotValue_2(LispObject value)
			throws ConditionThrowable;

	public abstract void setSlotValue_3(LispObject value)
			throws ConditionThrowable;

	public abstract void setSlotValue(int index, LispObject value)
			throws ConditionThrowable;

	public abstract LispObject SLOT_VALUE(LispObject slotName)
			throws ConditionThrowable;

	public abstract void setSlotValue(LispObject slotName, LispObject newValue)
			throws ConditionThrowable;

	// Profiling.
	public abstract int getCallCount();

	public abstract void setCallCount(int n);

	public abstract void incrementCallCount();

	public abstract String toString();

}