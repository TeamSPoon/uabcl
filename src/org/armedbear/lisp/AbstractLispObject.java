package org.armedbear.lisp;

import java.math.BigInteger;
import static org.armedbear.lisp.Nil.NIL;
import static org.armedbear.lisp.Lisp.*;

abstract public class AbstractLispObject implements LispObject {
	
	public LispObject rational() {
		return type_error(this, SymbolConstants.REAL).rational();
	}
	// @Override
	public boolean isSubL() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isFixnum() {
		return false;
	}
	
	public boolean isBignum() {
		return false;
	}
	
	public BigInteger bigIntegerValue() {
		// TODO Auto-generated method stub
		return  type_error(this, SymbolConstants.INTEGER).bigIntegerValue();
	}
	
	//@Override
	public int clHash() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}	
	  
	  public LispObject typeOf()
	  {
	    return T;
	  }

	  public LispObject classOf()
	  {
	    return BuiltInClass.CLASS_T;
	  }

	  public LispObject getDescription() throws ConditionThrowable
	  {
	    FastStringBuffer sb = new FastStringBuffer("An object of type ");
	    sb.append(typeOf().writeToString());
	    sb.append(" at #x");
	    sb.append(Integer.toHexString(System.identityHashCode(this)).toUpperCase());
	    return new SimpleString(sb);
	  }

	  public LispObject getParts() throws ConditionThrowable
	  {
	    return NIL;
	  }

	  public boolean getBooleanValue()
	  {
	    return true;
	  }

	  public LispObject typep(LispObject typeSpecifier) throws ConditionThrowable
	  {
	    if (typeSpecifier == T)
	      return T;
	    if (typeSpecifier == BuiltInClass.CLASS_T)
	      return T;
	    if (typeSpecifier == SymbolConstants.ATOM)
	      return T;
	    return NIL;
	  }

	  public boolean constantp()
	  {
	    return true;
	  }

	  public LispObject CONSTANTP()
	  {
	    return constantp() ? T : NIL;
	  }

	  public LispObject ATOM()
	  {
	    return T;
	  }

	  public boolean atom()
	  {
	    return true;
	  }

	  public Object javaInstance() throws ConditionThrowable
	  {
	        return this;
	  }

	  public Object javaInstance(Class<?> c) throws ConditionThrowable
	  {
	      if (c.isAssignableFrom(getClass()))
		  return this;
	      return error(new LispError("The value " + writeToString() +
					 " is not of class " + c.getName()));
	  }

	  /** This method returns 'this' by default, but allows
	   * objects to return different values to increase Java
	   * interoperability
	   * 
	   * @return An object to be used with synchronized, wait, notify, etc
	   * @throws org.armedbear.lisp.ConditionThrowable
	   */
	  public Object lockableInstance() throws ConditionThrowable
	  {
	      return this;
	  }


	  public LispObject CAR() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.LIST).CAR();
	  }

	  public void setCar(LispObject obj) throws ConditionThrowable
	  {
	    type_error(this, SymbolConstants.CONS).setCar(obj);
	  }

	  public LispObject RPLACA(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.CONS).RPLACA(obj);
	  }

	public LispObject CDR() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.LIST).CDR();
	  }

	  public void setCdr(LispObject obj) throws ConditionThrowable
	  {
	    type_error(this, SymbolConstants.CONS).setCdr(obj);
	  }

	  public LispObject RPLACD(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.CONS).RPLACD(obj);
	  }

	  public LispObject CADR() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.LIST).CADR();
	  }

	  public LispObject CDDR() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.LIST).CDDR();
	  }

	  public LispObject CADDR() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.LIST).CADDR();
	  }

	  public LispObject nthcdr(int n) throws ConditionThrowable
	  {
	    if (n < 0)
	      return type_error(Fixnum.getInstance(n),
	                             list(SymbolConstants.INTEGER, Fixnum.ZERO)).nthcdr(n);
	    return type_error(this, SymbolConstants.LIST).nthcdr(n);
	  }

	  public LispObject push(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.LIST).push(obj);
	  }

	  public LispObject EQ(LispObject obj)
	  {
	    return this == obj ? T : NIL;
	  }

	  public boolean eql(char c)
	  {
	    return false;
	  }

	  public boolean eql(int n)
	  {
	    return false;
	  }

	  public boolean eql(LispObject obj)
	  {
	    return this == obj;
	  }

	  public final LispObject EQL(LispObject obj)
	  {
	    return eql(obj) ? T : NIL;
	  }

	  public final LispObject EQUAL(LispObject obj) throws ConditionThrowable
	  {
	    return equal(obj) ? T : NIL;
	  }

	  public boolean equal(int n)
	  {
	    return false;
	  }

	  public boolean equal(LispObject obj) throws ConditionThrowable
	  {
	    return this == obj;
	  }

	  public boolean equalp(int n)
	  {
	    return false;
	  }

	  public boolean equalp(LispObject obj) throws ConditionThrowable
	  {
	    return this == obj;
	  }

	  public LispObject ABS() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.NUMBER);
	  }

	  public LispObject NUMERATOR() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.RATIONAL).NUMERATOR();
	  }

	  public LispObject DENOMINATOR() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.RATIONAL).DENOMINATOR();
	  }

	  public LispObject EVENP() throws ConditionThrowable
	  {
	    return isEven() ? T : NIL;
	  }

	  public boolean isEven() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.INTEGER).isEven();
	  }

	  public LispObject ODDP() throws ConditionThrowable
	  {
	    return isOdd() ? T : NIL;
	  }

	  public boolean isOdd() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.INTEGER).isOdd();
	  }

	  public LispObject PLUSP() throws ConditionThrowable
	  {
	    return isPositive() ? T : NIL;
	  }

	  public boolean isPositive() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.REAL).isPositive();
	  }

	  public LispObject MINUSP() throws ConditionThrowable
	  {
	    return isNegative() ? T : NIL;
	  }

	  public boolean isNegative() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.REAL).isNegative();
	  }

	  public LispObject NUMBERP()
	  {
		    return isNumber()?T:NIL;
	  }

	  public boolean isNumber()
	  {
	    return false;
	  }

	  public LispObject ZEROP() throws ConditionThrowable
	  {
	    return isZero() ? T : NIL;
	  }

	  public boolean isZero() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.NUMBER).isZero();
	  }

	  public LispObject COMPLEXP()
	  {
	    return NIL;
	  }

	  public LispObject FLOATP()
	  {
	    return NIL;
	  }

	  public boolean floatp()
	  {
	    return false;
	  }

	  public LispObject INTEGERP()
	  {
	    return NIL;
	  }

	  public boolean isInteger()
	  {
	    return false;
	  }

	  public LispObject RATIONALP()
	  {
	    return rationalp() ? T : NIL;
	  } 

	  public boolean rationalp()
	  {
	    return false;
	  }

	  public LispObject REALP()
	  {
	    return realp() ? T : NIL;
	  }

	  public boolean realp()
	  {
	    return false;
	  }

	  public LispObject STRINGP()
	  {
		  return isString() ? T : NIL;
	  }

	  public boolean isString()
	  {
	    return false;
	  }

	  public LispObject SIMPLE_STRING_P()
	  {
	    return NIL;
	  }

	  public LispObject VECTORP()
	  {
		  return isVector() ? T : NIL;
	  }

	  public boolean isVector()
	  {
	    return false;
	  }

	  public LispObject CHARACTERP()
	  {
		  return isChar() ? T : NIL;
	  }

	  public boolean isChar()
	  {
	    return false;
	  }

	  public int size() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.SEQUENCE).size();
	  }

	  public final LispObject LENGTH() throws ConditionThrowable
	  {
	    return Fixnum.getInstance(size());
	  }

	  public LispObject CHAR(int index) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.STRING).CHAR(index);
	  }

	  public LispObject SCHAR(int index) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.SIMPLE_STRING).SCHAR(index);
	  }

	  public LispObject NTH(int index) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.LIST).NTH(index);
	  }

	  public LispObject NTH(LispObject arg) throws ConditionThrowable
	  {
	    return Lisp.type_error(this, SymbolConstants.LIST).NTH(arg);
	  }

	  public LispObject elt(int index) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.SEQUENCE).elt(index);
	  }

	  public LispObject reverse() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.SEQUENCE).reverse();
	  }

	  public LispObject nreverse() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.SEQUENCE).nreverse();
	  }

	  public long aref_long(int index) throws ConditionThrowable
	  {
	    return AREF(index).longValue();
	  }

	  public int aref(int index) throws ConditionThrowable
	  {
	    return AREF(index).intValue();
	  }

	  public LispObject AREF(int index) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.ARRAY).AREF(index);
	  }

	  public LispObject AREF(LispObject index) throws ConditionThrowable
	  {
	      return AREF(index.intValue());
	  }

	  public void aset(int index, int n)
	    throws ConditionThrowable
	  {    
	          aset(index, Fixnum.getInstance(n));
	  }

	  public void aset(int index, LispObject newValue)
	    throws ConditionThrowable
	  {
	    type_error(this, SymbolConstants.ARRAY).aset(index, newValue);
	  }

	  public void aset(LispObject index, LispObject newValue)
	    throws ConditionThrowable
	  {
	      aset(index.intValue(), newValue);
	  }

	  public LispObject SVREF(int index) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.SIMPLE_VECTOR).SVREF(index);
	  }

	  public void svset(int index, LispObject newValue) throws ConditionThrowable
	  {
	    type_error(this, SymbolConstants.SIMPLE_VECTOR).svset(index, newValue);
	  }

	  public void vectorPushExtend(LispObject element)
	    throws ConditionThrowable
	  {
	    noFillPointer().vectorPushExtend(element);
	  }

	  public LispObject VECTOR_PUSH_EXTEND(LispObject element)
	    throws ConditionThrowable
	  {
	    return noFillPointer().VECTOR_PUSH_EXTEND(element);
	  }

	  public LispObject VECTOR_PUSH_EXTEND(LispObject element, LispObject extension)
	    throws ConditionThrowable
	  {
	    return noFillPointer().VECTOR_PUSH_EXTEND(element, extension);
	  }

	  public final LispObject noFillPointer() throws ConditionThrowable
	  {
	    return type_error((LispObject)this, (LispObject)list(SymbolConstants.AND, SymbolConstants.VECTOR,
	                                       list(SymbolConstants.SATISFIES,
	                                             SymbolConstants.ARRAY_HAS_FILL_POINTER_P)));
	  }

	  public LispObject[] copyToArray() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.LIST).copyToArray();
	  }

	  public LispObject SYMBOLP()
	  {
	    return NIL;
	  }

	  public boolean isList()
	  {
	    return false;
	  }

	  public LispObject LISTP()
	  {
	    return isList()?T:NIL;
	  }

	  public boolean endp() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.LIST).endp();
	  }

	  public LispObject ENDP() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.LIST).ENDP();
	  }

	  public LispObject NOT()
	  {
	    return NIL;
	  }

	  public boolean isSpecialOperator() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.SYMBOL).isSpecialOperator();
	  }

	  public boolean isSpecialVariable()
	  {
	    return false;
	  }

	  public LispObject getDocumentation(LispObject docType)
	    throws ConditionThrowable
	  {
	    LispObject alist = documentationHashTable.get(this);
	    if (alist != null)
	      {
	        LispObject entry = assq(docType, alist);
	        if (entry instanceof Cons)
	          return ((Cons)entry).cdr;
	      }
	    return NIL;
	  }

	  public void setDocumentation(LispObject docType, LispObject documentation)
	    throws ConditionThrowable
	  {
	    LispObject alist = documentationHashTable.get(this);
	    if (alist == null)
	      alist = NIL;
	    LispObject entry = assq(docType, alist);
	    if (entry instanceof Cons)
	      {
	        ((Cons)entry).cdr = documentation;
	      }
	    else
	      {
	        alist = alist.push(new Cons(docType, documentation));
	        documentationHashTable.putVoid(this, alist);
	      }
	  }

	  public LispObject getPropertyList()
	  {
	    return null;
	  }

	  public void setPropertyList(LispObject obj)
	  {
	  }

	  public LispObject getSymbolValue() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.SYMBOL).getSymbolValue();
	  }

	  public LispObject getSymbolFunction() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.SYMBOL).getSymbolFunction();
	  }

	  public LispObject getSymbolFunctionOrDie() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.SYMBOL).getSymbolFunctionOrDie();
	  }

	  public String writeToString() throws ConditionThrowable
	  {
	    return toString();
	  }

	  public String unreadableString(String s)
	  {
	    FastStringBuffer sb = new FastStringBuffer("#<");
	    sb.append(s);
	    sb.append(" {");
	    sb.append(Integer.toHexString(System.identityHashCode(this)).toUpperCase());
	    sb.append("}>");
	    return sb.toString();
	  }

	  public String unreadableString(Symbol symbol) throws ConditionThrowable
	  {
	    FastStringBuffer sb = new FastStringBuffer("#<");
	    sb.append(symbol.writeToString());
	    sb.append(" {");
	    sb.append(Integer.toHexString(System.identityHashCode(this)).toUpperCase());
	    sb.append("}>");
	    return sb.toString();
	  }

	  // Special operator
	  public LispObject execute(LispObject args, Environment env)
	    throws ConditionThrowable
	  {
	    return error(new LispError());
	  }

	  public LispObject execute() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.FUNCTION).execute();
	  }

	  public LispObject execute(LispObject arg) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.FUNCTION);
	  }

	  public LispObject execute(LispObject first, LispObject second)
	    throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.FUNCTION);
	  }

	  public LispObject execute(LispObject first, LispObject second,
	                            LispObject third)
	    throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.FUNCTION);
	  }

	  public LispObject execute(LispObject first, LispObject second,
	                            LispObject third, LispObject fourth)
	    throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.FUNCTION);
	  }

	  public LispObject execute(LispObject first, LispObject second,
	                            LispObject third, LispObject fourth,
	                            LispObject fifth)
	    throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.FUNCTION);
	  }

	  public LispObject execute(LispObject first, LispObject second,
	                            LispObject third, LispObject fourth,
	                            LispObject fifth, LispObject sixth)
	    throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.FUNCTION);
	  }

	  public LispObject execute(LispObject first, LispObject second,
	                            LispObject third, LispObject fourth,
	                            LispObject fifth, LispObject sixth,
	                            LispObject seventh)
	    throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.FUNCTION);
	  }

	  public LispObject execute(LispObject first, LispObject second,
	                            LispObject third, LispObject fourth,
	                            LispObject fifth, LispObject sixth,
	                            LispObject seventh, LispObject eighth)
	    throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.FUNCTION);
	  }

	  public LispObject execute(LispObject[] args) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.FUNCTION);
	  }

	  // Used by COMPILE-MULTIPLE-VALUE-CALL.
	  public LispObject dispatch(LispObject[] args) throws ConditionThrowable
	  {
	    switch (args.length)
	      {
	      case 0:
	        return execute();
	      case 1:
	        return execute(args[0]);
	      case 2:
	        return execute(args[0], args[1]);
	      case 3:
	        return execute(args[0], args[1], args[2]);
	      case 4:
	        return execute(args[0], args[1], args[2], args[3]);
	      case 5:
	        return execute(args[0], args[1], args[2], args[3], args[4]);
	      case 6:
	        return execute(args[0], args[1], args[2], args[3], args[4],
	                       args[5]);
	      case 7:
	        return execute(args[0], args[1], args[2], args[3], args[4],
	                       args[5], args[6]);
	      case 8:
	        return execute(args[0], args[1], args[2], args[3], args[4],
	                       args[5], args[6], args[7]);
	      default:
	        return type_error(this, SymbolConstants.FUNCTION);
	      }
	  }

	  public int intValue() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.INTEGER).intValue();
	  }

	  public long longValue() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.INTEGER).longValue();
	  }

	  public float floatValue() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.SINGLE_FLOAT).floatValue();
	  }

	  public double doubleValue() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.DOUBLE_FLOAT).doubleValue();
	  }

	  public LispObject incr() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.NUMBER);
	  }

	  public LispObject decr() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.NUMBER);
	  }

	  public LispObject negate() throws ConditionThrowable
	  {
	    return Fixnum.ZERO.subtract(this);
	  }

	  public LispObject add(int n) throws ConditionThrowable
	  {
	    return add(Fixnum.getInstance(n));
	  }

	  public LispObject add(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.NUMBER);
	  }

	  public LispObject subtract(int n) throws ConditionThrowable
	  {
	    return subtract(Fixnum.getInstance(n));
	  }

	  public LispObject subtract(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.NUMBER);
	  }

	  public LispObject multiplyBy(int n) throws ConditionThrowable
	  {
	    return multiplyBy(Fixnum.getInstance(n));
	  }

	  public LispObject multiplyBy(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.NUMBER);
	  }

	  public LispObject divideBy(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.NUMBER);
	  }

	  public boolean isEqualTo(int n) throws ConditionThrowable
	  {
	    return isEqualTo(Fixnum.getInstance(n));
	  }

	  public boolean isEqualTo(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.NUMBER).isEqualTo(obj);
	  }

	  public LispObject IS_E(LispObject obj) throws ConditionThrowable
	  {
	    return isEqualTo(obj) ? T : NIL;
	  }

	  public boolean isNotEqualTo(int n) throws ConditionThrowable
	  {
	    return isNotEqualTo(Fixnum.getInstance(n));
	  }

	  public boolean isNotEqualTo(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.NUMBER).isNotEqualTo(obj);
	  }

	  public LispObject IS_NE(LispObject obj) throws ConditionThrowable
	  {
	    return isNotEqualTo(obj) ? T : NIL;
	  }

	  public boolean isLessThan(int n) throws ConditionThrowable
	  {
	    return isLessThan(Fixnum.getInstance(n));
	  }

	  public boolean isLessThan(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.REAL).isLessThan(obj);
	  }

	  public LispObject IS_LT(LispObject obj) throws ConditionThrowable
	  {
	    return isLessThan(obj) ? T : NIL;
	  }

	  public boolean isGreaterThan(int n) throws ConditionThrowable
	  {
	    return isGreaterThan(Fixnum.getInstance(n));
	  }

	  public boolean isGreaterThan(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.REAL).isGreaterThan(obj);
	  }

	  public LispObject IS_GT(LispObject obj) throws ConditionThrowable
	  {
	    return isGreaterThan(obj) ? T : NIL;
	  }

	  public boolean isLessThanOrEqualTo(int n) throws ConditionThrowable
	  {
	    return isLessThanOrEqualTo(Fixnum.getInstance(n));
	  }

	  public boolean isLessThanOrEqualTo(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.REAL).isLessThanOrEqualTo(obj);
	  }

	  public LispObject IS_LE(LispObject obj) throws ConditionThrowable
	  {
	    return isLessThanOrEqualTo(obj) ? T : NIL;
	  }

	  public boolean isGreaterThanOrEqualTo(int n) throws ConditionThrowable
	  {
	    return isGreaterThanOrEqualTo(Fixnum.getInstance(n));
	  }

	  public boolean isGreaterThanOrEqualTo(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.REAL).isGreaterThanOrEqualTo(obj);
	  }

	  public LispObject IS_GE(LispObject obj) throws ConditionThrowable
	  {
	    return isGreaterThanOrEqualTo(obj) ? T : NIL;
	  }

	  public LispObject truncate(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.REAL);
	  }

	  public LispObject MOD(LispObject divisor) throws ConditionThrowable
	  {
	    truncate(divisor);
	    final LispThread thread = LispThread.currentThread();
	    LispObject remainder = thread._values[1];
	    thread.clearValues();
	    if (!remainder.isZero())
	      {
	        if (divisor.isNegative())
	          {
	            if (isPositive())
	              return remainder.add((LispObject)divisor);
	          }
	        else
	          {
	            if (isNegative())
	              return remainder.add(divisor);
	          }
	      }
	    return remainder;
	  }

	  public LispObject MOD(int divisor) throws ConditionThrowable
	  {
	    return MOD(Fixnum.getInstance(divisor));
	  }

	  public LispObject ash(int shift) throws ConditionThrowable
	  {
	    return ash(Fixnum.getInstance(shift));
	  }

	  public LispObject ash(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.INTEGER);
	  }

	  public LispObject LOGNOT() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.INTEGER);
	  }

	  public LispObject LOGAND(int n) throws ConditionThrowable
	  {
	    return LOGAND(Fixnum.getInstance(n));
	  }

	  public LispObject LOGAND(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.INTEGER);
	  }

	  public LispObject LOGIOR(int n) throws ConditionThrowable
	  {
	    return LOGIOR(Fixnum.getInstance(n));
	  }

	  public LispObject LOGIOR(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.INTEGER);
	  }

	  public LispObject LOGXOR(int n) throws ConditionThrowable
	  {
	    return LOGXOR(Fixnum.getInstance(n));
	  }

	  public LispObject LOGXOR(LispObject obj) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.INTEGER);
	  }

	  public LispObject LDB(int size, int position) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.INTEGER);
	  }

	  public int sxhash()
	  {
	    return clHash() & 0x7fffffff;
	  }

	  // For EQUALP hash tables.
	  public int psxhash()
	  {
	    return sxhash();
	  }

	  public int psxhash(int depth)
	  {
	    return psxhash();
	  }

	  public LispObject STRING() throws ConditionThrowable
	  {
	    return error(new TypeError(writeToString() + " cannot be coerced to a string."));
	  }

	  public char[] chars() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.STRING).chars();
	  }

	  public char[] getStringChars() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.STRING).getStringChars();
	  }

	  public String getStringValue() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.STRING).getStringValue();
	  }

	  public LispObject getSlotValue_0() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.STRUCTURE_OBJECT);
	  }

	  public LispObject getSlotValue_1() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.STRUCTURE_OBJECT);
	  }

	  public LispObject getSlotValue_2() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.STRUCTURE_OBJECT);
	  }

	  public LispObject getSlotValue_3() throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.STRUCTURE_OBJECT);
	  }

	  public LispObject getSlotValue(int index) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.STRUCTURE_OBJECT);
	  }

	  public int getFixnumSlotValue(int index) throws ConditionThrowable
	  {
	    type_error(this, SymbolConstants.STRUCTURE_OBJECT);
	    // Not reached.
	    return 0;
	  }

	  public boolean getSlotValueAsBoolean(int index) throws ConditionThrowable
	  {
	    type_error(this, SymbolConstants.STRUCTURE_OBJECT);
	    // Not reached.
	    return false;
	  }

	  public void setSlotValue_0(LispObject value)
	    throws ConditionThrowable
	  {
	    type_error(this, SymbolConstants.STRUCTURE_OBJECT);
	  }

	  public void setSlotValue_1(LispObject value)
	    throws ConditionThrowable
	  {
	    type_error(this, SymbolConstants.STRUCTURE_OBJECT);
	  }

	  public void setSlotValue_2(LispObject value)
	    throws ConditionThrowable
	  {
	    type_error(this, SymbolConstants.STRUCTURE_OBJECT);
	  }

	  public void setSlotValue_3(LispObject value)
	    throws ConditionThrowable
	  {
	    type_error(this, SymbolConstants.STRUCTURE_OBJECT);
	  }

	  public void setSlotValue(int index, LispObject value)
	    throws ConditionThrowable
	  {
	    type_error(this, SymbolConstants.STRUCTURE_OBJECT);
	  }

	  public LispObject SLOT_VALUE(LispObject slotName) throws ConditionThrowable
	  {
	    return type_error(this, SymbolConstants.STANDARD_OBJECT);
	  }

	  public void setSlotValue(LispObject slotName, LispObject newValue)
	    throws ConditionThrowable
	  {
	    type_error(this, SymbolConstants.STANDARD_OBJECT);
	  }

	  // Profiling.
	  public int getCallCount()
	  {
	    return 0;
	  }

	  public void setCallCount(int n)
	  {
	  }

	  public void incrementCallCount()
	  {
	  }

	  public int getHotCount()
	  {
	      return 0;
	  }

	  public void setHotCount(int n)
	  {
	  }

	  public void incrementHotCount()
	  {
	  }
}
