/*
 * SpecialOperators.java
 *
 * Copyright (C) 2003-2007 Peter Graves
 * $Id: SpecialOperators.java 12114 2009-08-23 19:08:04Z ehuelsmann $
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
import java.util.ArrayList;
import java.util.LinkedList;
public final class SpecialOperators extends LispFile
{
  // ### quote
  public static final SpecialOperator QUOTE =
    new SpecialOperator(SymbolConstants.QUOTE, "thing")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        if (args.CDR() != NIL)
          return error(new WrongNumberOfArgumentsException(this));
        return ((Cons)args).CAR();
      }
    };

  // ### if
  public static final SpecialOperator IF =
    new SpecialOperator(SymbolConstants.IF, "test then &optional else")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        final LispThread thread = LispThread.currentThread();
        switch (args.size())
          {
          case 2:
            {
              if (Lisp.eval(((Cons)args).CAR(), env, thread) != NIL)
                return Lisp.eval(args.CADR(), env, thread);
              thread.clearValues();
              return NIL;
            }
          case 3:
            {
              if (Lisp.eval(((Cons)args).CAR(), env, thread) != NIL)
                return Lisp.eval(args.CADR(), env, thread);
              return Lisp.eval((((Cons)args).CDR()).CADR(), env, thread);
            }
          default:
            return error(new WrongNumberOfArgumentsException(this));
          }
      }
    };

  // ### let
  public static final SpecialOperator LET =
    new SpecialOperator(SymbolConstants.LET, "bindings &body body")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        if (args == NIL)
          return error(new WrongNumberOfArgumentsException(this));
        return _let(args, env, false);
      }
    };

  // ### let*
  public static final SpecialOperator LET_STAR =
    new SpecialOperator(SymbolConstants.LET_STAR, "bindings &body body")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        if (args == NIL)
          return error(new WrongNumberOfArgumentsException(this));
        return _let(args, env, true);
      }
    };

  public static final LispObject _let(LispObject args, Environment env,
                                       boolean sequential)
    throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    final SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
    try
      {
        LispObject varList = checkList(args.CAR());
        LispObject bodyAndDecls = parseBody(args.CDR(), false);
        LispObject specials = parseSpecials(bodyAndDecls.NTH(1));
        LispObject body = bodyAndDecls.CAR();

        Environment ext = new Environment(env);
        LinkedList<Cons> nonSequentialVars = new LinkedList<Cons>();
        while (varList != NIL)
          {
            final Symbol symbol;
            LispObject value;
            LispObject obj = varList.CAR();
            if (obj instanceof Cons)
              {
                if (obj.size() > 2)
                  return error(new LispError("The " + (sequential ? "LET*" : "LET")
                          + " binding specification " +
                          obj.writeToString() + " is invalid."));
                symbol = checkSymbol(((Cons)obj).CAR());
                value = eval(obj.CADR(), sequential ? ext : env, thread);
              }
            else
              {
                symbol = checkSymbol(obj);
                value = NIL;
              }
            if (sequential) {
	      ext = new Environment(ext);
              bindArg(specials, symbol, value, ext, thread);
	    }
            else
                nonSequentialVars.add(makeCons(symbol, value));
            varList = ((Cons)varList).CDR();
          }
        if (!sequential)
          for (Cons x : nonSequentialVars)
            bindArg(specials, (Symbol)x.CAR(), x.CDR(), ext, thread);

        // Make sure free special declarations are visible in the body.
        // "The scope of free declarations specifically does not include
        // initialization forms for bindings established by the form
        // containing the declarations." (3.3.4)
        for (; specials != NIL; specials = specials.CDR())
          ext.declareSpecial((Symbol)specials.CAR());

        return progn(body, ext, thread);
      }
    finally
      {
        thread.lastSpecialBinding = lastSpecialBinding;
      }
  }

  // ### symbol-macrolet
  public static final SpecialOperator SYMBOL_MACROLET =
    new SpecialOperator(SymbolConstants.SYMBOL_MACROLET, "macrobindings &body body")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        LispObject varList = checkList(args.CAR());
        final LispThread thread = LispThread.currentThread();
        SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
        Environment ext = new Environment(env);
        try
         {
             // Declare our free specials, this will correctly raise
             LispObject body = ext.processDeclarations(args.CDR());

             for (int i = varList.size(); i-- > 0;)
               {
                 LispObject obj = varList.CAR();
                 varList = varList.CDR();
                 if (obj instanceof Cons && obj.size() == 2)
                   {
                     Symbol symbol = checkSymbol(obj.CAR());
                     if (symbol.isSpecialVariable()
                         || ext.isDeclaredSpecial(symbol))
                       {
                          return error(new ProgramError(
                              "Attempt to bind the special variable " +
                              symbol.writeToString() +
                              " with SYMBOL-MACROLET."));
                       }
                     bindArg(null, symbol, new SymbolMacro(obj.CADR()), ext, thread);
                   }
                 else
                   {
                     return error(new ProgramError(
                       "Malformed symbol-expansion pair in SYMBOL-MACROLET: " +
                       obj.writeToString()));
                   }
                }
             return progn(body, ext, thread);
              }
        finally
            {
                thread.lastSpecialBinding = lastSpecialBinding;
            }
      }
    };

  // ### load-time-value form &optional read-only-p => object
  public static final SpecialOperator LOAD_TIME_VALUE =
    new SpecialOperator(SymbolConstants.LOAD_TIME_VALUE,
                        "form &optional read-only-p")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        switch (args.size())
          {
          case 1:
          case 2:
            return Lisp.eval(args.CAR(), new Environment(),
                        LispThread.currentThread());
          default:
            return error(new WrongNumberOfArgumentsException(this));
          }
      }
    };

  // ### locally
  public static final SpecialOperator LOCALLY =
    new SpecialOperator(SymbolConstants.LOCALLY, "&body body")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        final LispThread thread = LispThread.currentThread();
        final Environment ext = new Environment(env);
        args = ext.processDeclarations(args);
        return progn(args, ext, thread);
      }
    };

  // ### progn
  public static final SpecialOperator PROGN =
    new SpecialOperator(SymbolConstants.PROGN, "&rest forms")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        LispThread thread = LispThread.currentThread();
        return progn(args, env, thread);
      }
    };

  // ### flet
  public static final SpecialOperator FLET =
    new SpecialOperator(SymbolConstants.FLET, "definitions &body body")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        return _flet(args, env, false);
      }
    };

  // ### labels
  public static final SpecialOperator LABELS =
    new SpecialOperator(SymbolConstants.LABELS, "definitions &body body")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        return _flet(args, env, true);
      }
    };

  public static final LispObject _flet(LispObject args, Environment env,
                                        boolean recursive)
    throws ConditionThrowable
  {
    // First argument is a list of local function definitions.
    LispObject defs = checkList(args.CAR());
    final LispThread thread = LispThread.currentThread();
    final SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
    final Environment funEnv = new Environment(env);
    while (defs != NIL)
      {
        final LispObject def = checkList(defs.CAR());
        final LispObject name = def.CAR();
        final Symbol symbol;
        if (name instanceof Symbol)
          {
            symbol = checkSymbol(name);
            if (symbol.getSymbolFunction() instanceof SpecialOperator)
              {
                String message =
                  symbol.getName() + " is a special operator and may not be redefined";
                return error(new ProgramError(message));
              }
          }
        else if (isValidSetfFunctionName(name))
          symbol = checkSymbol(name.CADR());
        else
          return type_error(name, FUNCTION_NAME);
        LispObject rest = def.CDR();
        LispObject parameters = rest.CAR();
        LispObject body = rest.CDR();
        LispObject decls = NIL;
        while (body.CAR() instanceof Cons && body.CAR().CAR() == SymbolConstants.DECLARE)
          {
            decls = makeCons(body.CAR(), decls);
            body = body.CDR();
          }
        body = makeCons(symbol, body);
        body = makeCons(SymbolConstants.BLOCK, body);
        body = makeCons(body, NIL);
        while (decls != NIL)
          {
            body = makeCons(decls.CAR(), body);
            decls = decls.CDR();
          }
        LispObject lambda_expression =
          makeCons(SymbolConstants.LAMBDA, makeCons(parameters, body));
        LispObject lambda_name =
          list(recursive ? SymbolConstants.LABELS : SymbolConstants.FLET, name);
        Closure closure =
          new Closure(lambda_name, lambda_expression,
                      recursive ? funEnv : env);
        funEnv.addFunctionBinding(name, closure);
        defs = defs.CDR();
      }
    try
      {
        final Environment ext = new Environment(funEnv);
        LispObject body = args.CDR();
        body = ext.processDeclarations(body);
        return progn(body, ext, thread);
      }
    finally
      {
        thread.lastSpecialBinding = lastSpecialBinding;
      }
  }

  // ### the value-type form => result*
  // ### the value-type form => result*
  private static final SpecialOperator THE =
    new SpecialOperator(SymbolConstants.THE, "type value")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        if (args.size() != 2)
          return error(new WrongNumberOfArgumentsException(this));
        LispObject rv = eval(args.CADR(), env, LispThread.currentThread());

        // check only the most simple types: single symbols
        // (class type specifiers/primitive types)
        // DEFTYPE-d types need expansion;
        // doing so would slow down our execution too much

        // An implementation is allowed not to check the type,
        // the fact that we do so here is mainly driven by the
        // requirement to verify argument types in structure-slot
        // accessors (defstruct.lisp)

        // The policy below is in line with the level of verification
        // in the compiler at *safety* levels below 3
        LispObject type = args.CAR();
        if ((type instanceof Symbol
             && get(type, SymbolConstants.DEFTYPE_DEFINITION) == NIL)
            || type instanceof BuiltInClass)
	  if (rv.typep(type) == NIL)
	      type_error(rv, type);

        return rv;
      }
    };

  // ### progv
  public static final SpecialOperator PROGV =
    new SpecialOperator(SymbolConstants.PROGV, "symbols values &body body")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        if (args.size() < 2)
          return error(new WrongNumberOfArgumentsException(this));
        final LispThread thread = LispThread.currentThread();
        final LispObject symbols = checkList(Lisp.eval(args.CAR(), env, thread));
        LispObject values = checkList(Lisp.eval(args.CADR(), env, thread));
        SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
        try
          {
            // Set up the new bindings.
            progvBindVars(symbols, values, thread);
            // Implicit PROGN.
            return progn(args.CDR().CDR(), env, thread);
          }
        finally
          {
            thread.lastSpecialBinding = lastSpecialBinding;
          }
      }
    };

  // ### declare
  public static final SpecialOperator DECLARE =
    new SpecialOperator(SymbolConstants.DECLARE, "&rest declaration-specifiers")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        return NIL;
      }
    };

  // ### function
  public static final SpecialOperator FUNCTION =
    new SpecialOperator(SymbolConstants.FUNCTION, "thing")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        final LispObject arg = args.CAR();
        if (arg instanceof Symbol)
          {
            LispObject operator = env.lookupFunction(arg);
            if (operator instanceof Autoload)
              {
                Autoload autoload = (Autoload) operator;
                autoload.load();
                operator = autoload.getSymbol().getSymbolFunction();
              }
            if (operator instanceof Function)
              return operator;
            if (operator instanceof StandardGenericFunction)
              return operator;
            return error(new UndefinedFunction(arg));
          }
        if (arg instanceof Cons)
          {
            LispObject car = ((Cons)arg).CAR();
            if (car == SymbolConstants.SETF)
              {
                LispObject f = env.lookupFunction(arg);
                if (f != null)
                  return f;
                Symbol symbol = checkSymbol(arg.CADR());
                f = Lisp.get(symbol, SymbolConstants.SETF_FUNCTION, null);
                if (f != null)
                  return f;
                f = Lisp.get(symbol, SymbolConstants.SETF_INVERSE, null);
                if (f != null)
                  return f;
              }
            if (car == SymbolConstants.LAMBDA)
              return new Closure(arg, env);
            if (car == SymbolConstants.NAMED_LAMBDA)
              {
                LispObject name = arg.CADR();
                if (name instanceof Symbol || isValidSetfFunctionName(name))
                  {
                    return new Closure(name,
                                       makeCons(SymbolConstants.LAMBDA, arg.CDDR()),
                                       env);
                  }
                return type_error(name, FUNCTION_NAME);
              }
          }
        return error(new UndefinedFunction(list(Keyword.NAME, arg)));
      }
    };

  // ### setq
  public static final SpecialOperator SETQ =
    new SpecialOperator(SymbolConstants.SETQ, "&rest vars-and-values")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        LispObject value = NIL;
        final LispThread thread = LispThread.currentThread();
        while (args != NIL)
          {
            Symbol symbol = checkSymbol(args.CAR());
            if (symbol.isConstant())
              {
                return error(new ProgramError(symbol.writeToString() +
                                               " is a constant and thus cannot be set."));
              }
            args = args.CDR();
            if (symbol.isSpecialVariable() || env.isDeclaredSpecial(symbol))
              {
                SpecialBinding binding = thread.getSpecialBinding(symbol);
                if (binding != null)
                  {
                    if (binding.value instanceof SymbolMacro)
                      {
                        LispObject expansion =
                          ((SymbolMacro)binding.value).getExpansion();
                        LispObject form = list(SymbolConstants.SETF, expansion, args.CAR());
                        value = Lisp.eval(form, env, thread);
                      }
                    else
                      {
                        value = Lisp.eval(args.CAR(), env, thread);
                        binding.value = value;
                      }
                  }
                else
                  {
                    if (symbol.getSymbolValue() instanceof SymbolMacro)
                      {
                        LispObject expansion =
                          ((SymbolMacro)symbol.getSymbolValue()).getExpansion();
                        LispObject form = list(SymbolConstants.SETF, expansion, args.CAR());
                        value = Lisp.eval(form, env, thread);
                      }
                    else
                      {
                        value = Lisp.eval(args.CAR(), env, thread);
                        symbol.setSymbolValue(value);
                      }
                  }
              }
            else
              {
                // Not special.
                Binding binding = env.getBinding(symbol);
                if (binding != null)
                  {
                    if (binding.value instanceof SymbolMacro)
                      {
                        LispObject expansion =
                          ((SymbolMacro)binding.value).getExpansion();
                        LispObject form = list(SymbolConstants.SETF, expansion, args.CAR());
                        value = Lisp.eval(form, env, thread);
                      }
                    else
                      {
                        value = Lisp.eval(args.CAR(), env, thread);
                        binding.value = value;
                      }
                  }
                else
                  {
                    if (symbol.getSymbolValue() instanceof SymbolMacro)
                      {
                        LispObject expansion =
                          ((SymbolMacro)symbol.getSymbolValue()).getExpansion();
                        LispObject form = list(SymbolConstants.SETF, expansion, args.CAR());
                        value = Lisp.eval(form, env, thread);
                      }
                    else
                      {
                        value = Lisp.eval(args.CAR(), env, thread);
                        symbol.setSymbolValue(value);
                      }
                  }
              }
            args = args.CDR();
          }
        // Return primary value only!
        thread._values = null;
        return value;
      }
    };
}
