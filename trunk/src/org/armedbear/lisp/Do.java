/*
 * Do.java
 *
 * Copyright (C) 2003-2006 Peter Graves
 * $Id: Do.java 12165 2009-09-29 19:08:59Z ehuelsmann $
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
import static org.armedbear.lisp.Lisp.*;

public final class Do extends LispFile
{
  // ### do
  private static final SpecialOperator DO =
    new SpecialOperator(SymbolConstants.DO, "varlist endlist &body body")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        return _do(args, env, false);
      }
    };

  // ### do*
  private static final SpecialOperator DO_STAR =
    new SpecialOperator(SymbolConstants.DO_STAR, "varlist endlist &body body")
    {
      @Override
      public LispObject execute(LispObject args, Environment env)
        throws ConditionThrowable
      {
        return _do(args, env, true);
      }
    };

  /*private*/ static final LispObject _do(LispObject args, Environment env,
                                      boolean sequential)
    throws ConditionThrowable
  {
    LispObject varlist = args.CAR();
    LispObject second = args.CADR();
    LispObject end_test_form = second.CAR();
    LispObject result_forms = second.CDR();
    LispObject body = args.CDDR();
    // Process variable specifications.
    final int numvars = varlist.size();
    Symbol[] vars = new Symbol[numvars];
    LispObject[] initforms = new LispObject[numvars];
    LispObject[] stepforms = new LispObject[numvars];
    for (int i = 0; i < numvars; i++)
      {
        final LispObject varspec = varlist.CAR();
        if (varspec instanceof Cons)
          {
            vars[i] = checkSymbol(varspec.CAR());
            initforms[i] = varspec.CADR();
            // Is there a step form?
            if (varspec.CDDR() != NIL)
              stepforms[i] = varspec.CADDR();
          }
        else
          {
            // Not a cons, must be a symbol.
            vars[i] = checkSymbol(varspec);
            initforms[i] = NIL;
          }
        varlist = varlist.CDR();
      }
    final LispThread thread = LispThread.currentThread();
    final SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
    // Process declarations.

    final LispObject bodyAndDecls = parseBody(body, false);
    LispObject specials = parseSpecials(bodyAndDecls.NTH(1));
    body = bodyAndDecls.CAR();

    Environment ext = new Environment(env);
    for (int i = 0; i < numvars; i++)
      {
        Symbol var = vars[i];
        LispObject value = eval(initforms[i], (sequential ? ext : env), thread);
	ext = new Environment(ext);
        if (specials != NIL && memq(var, specials))
            thread.bindSpecial(var, value);
        else if (var.isSpecialVariable())
          thread.bindSpecial(var, value);
        else
          ext.bindLispSymbol(var, value);
      }
    LispObject list = specials;
    while (list != NIL)
      {
        ext.declareSpecial(checkSymbol(list.CAR()));
        list = list.CDR();
      }
    // Look for tags.
    LispObject localTags = Lisp.preprocessTagBody(body, ext);
    LispObject blockId = new BlockLispObject();
    try
      {
        // Implicit block.
        ext.addBlock(NIL, blockId);
        while (true)
          {
            // Execute body.
            // Test for termination.
            if (eval(end_test_form, ext, thread) != NIL)
              break;

            Lisp.processTagBody(body, localTags, ext);

            // Update variables.
            if (sequential)
              {
                for (int i = 0; i < numvars; i++)
                  {
                    LispObject step = stepforms[i];
                    if (step != null)
                      {
                        Symbol symbol = vars[i];
                        LispObject value = eval(step, ext, thread);
                        if (symbol.isSpecialVariable()
                            || ext.isDeclaredSpecial(symbol))
                          thread.rebindSpecial(symbol, value);
                        else
                          ext.rebindLispSymbol(symbol, value);
                      }
                  }
              }
            else
              {
                // Evaluate step forms.
                LispObject results[] = new LispObject[numvars];
                for (int i = 0; i < numvars; i++)
                  {
                    LispObject step = stepforms[i];
                    if (step != null)
                      {
                        LispObject result = eval(step, ext, thread);
                        results[i] = result;
                      }
                  }
                // Update variables.
                for (int i = 0; i < numvars; i++)
                  {
                    if (results[i] != null)
                      {
                        Symbol symbol = vars[i];
                        LispObject value = results[i];
                        if (symbol.isSpecialVariable()
                            || ext.isDeclaredSpecial(symbol))
                          thread.rebindSpecial(symbol, value);
                        else
                          ext.rebindLispSymbol(symbol, value);
                      }
                  }
              }
            if (interrupted)
              handleInterrupt();
          }
        LispObject result = progn(result_forms, ext, thread);
        return result;
      }
    catch (Return ret)
      {
        if (ret.getBlock() == blockId)
          {
            return ret.getResult();
          }
        throw ret;
      }
    finally
      {
        thread.lastSpecialBinding = lastSpecialBinding;
      }
  }
}
