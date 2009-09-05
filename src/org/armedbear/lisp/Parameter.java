/**
 * 
 */
package org.armedbear.lisp;

import static org.armedbear.lisp.Lisp.PACKAGE_KEYWORD;
import static org.armedbear.lisp.Lisp.checkSymbol;
import static org.armedbear.lisp.Nil.NIL;

class Parameter
  {
	  /*private*/ final Symbol var;
	  /*private*/ final LispObject initForm;
	  /*private*/ final LispObject initVal;
	  /*private*/ final LispObject svar;
	  /*private*/ final int type;
  /*private*/ final Symbol keyword;

    public Parameter(Symbol var)
    {
      this.var = var;
      this.initForm = null;
      this.initVal = null;
      this.svar = NIL;
      this.type = Closure.REQUIRED;
      this.keyword = null;
    }

    public Parameter(Symbol var, LispObject initForm, int type)
      throws ConditionThrowable
    {
      this.var = var;
      this.initForm = initForm;
      this.initVal = processInitForm(initForm);
      this.svar = NIL;
      this.type = type;
      keyword =
        type == Closure.KEYWORD ? PACKAGE_KEYWORD.intern(var.getSymbolName()) : null;
    }

    public Parameter(Symbol var, LispObject initForm, LispObject svar,
                     int type)
      throws ConditionThrowable
    {
      this.var = var;
      this.initForm = initForm;
      this.initVal = processInitForm(initForm);
      this.svar = (svar != NIL) ? checkSymbol(svar) : NIL;
      this.type = type;
      keyword =
        type == Closure.KEYWORD ? PACKAGE_KEYWORD.intern(var.getSymbolName()) : null;
    }

    public Parameter(Symbol keyword, Symbol var, LispObject initForm,
                     LispObject svar)
      throws ConditionThrowable
    {
      this.var = var;
      this.initForm = initForm;
      this.initVal = processInitForm(initForm);
      this.svar = (svar != NIL) ? checkSymbol(svar) : NIL;
      type = Closure.KEYWORD;
      this.keyword = keyword;
    }

    @Override
    public String toString()
    {
      if (type == Closure.REQUIRED)
        return var.toString();
      StringBuffer sb = new StringBuffer();
      if (keyword != null)
        {
          sb.append(keyword);
          sb.append(' ');
        }
      sb.append(var.toString());
      sb.append(' ');
      sb.append(initForm);
      sb.append(' ');
      sb.append(type);
      return sb.toString();
    }

    private static final LispObject processInitForm(LispObject initForm)
      throws ConditionThrowable
    {
      if (initForm.constantp())
        {
          if (initForm instanceof Symbol)
            return initForm.getSymbolValue();
          if (initForm instanceof Cons)
            {
              Debug.assertTrue(initForm.CAR() == SymbolConstants.QUOTE);
              return initForm.CADR();
            }
          return initForm;
        }
      return null;
    }
  }