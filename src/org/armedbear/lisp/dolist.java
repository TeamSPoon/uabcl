/*
 * dolist.java
 *
 * Copyright (C) 2003-2006 Peter Graves
 * $Id: dolist.java 12055 2009-07-24 19:24:12Z ehuelsmann $
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

// ### dolist
public final class dolist extends SpecialOperator
{
  private dolist()
  {
    super(SymbolConstants.DOLIST);
  }

  @Override
  public LispObject execute(LispObject args, Environment env)
    throws ConditionThrowable
  {
    LispObject bodyForm = args.CDR();
    args = args.CAR();
    Symbol var = checkSymbol(args.CAR());
    LispObject listForm = args.CADR();
    final LispThread thread = LispThread.currentThread();
    LispObject resultForm = args.CDR().CDR().CAR();
    SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
    // Process declarations.
    LispObject bodyAndDecls = parseBody(bodyForm, false);
    LispObject specials = parseSpecials(bodyAndDecls.NTH(1));
    bodyForm = bodyAndDecls.CAR();

    try
      {
        final Environment ext = new Environment(env);
        // Implicit block.
        ext.addBlock(NIL, new BlockLispObject());
        // Evaluate the list form.
        LispObject list = checkList(Lisp.eval(listForm, ext, thread));
        // Look for tags.
        LispObject remaining = bodyForm;
        while (remaining != NIL)
          {
            LispObject current = remaining.CAR();
            remaining = remaining.CDR();
            if (current instanceof Cons)
              continue;
            // It's a tag.
            ext.addTagBinding(current, remaining);
          }
        // Establish a reusable binding.
        final Object binding;
        if (specials != NIL && memq(var, specials))
          {
            thread.bindSpecial(var, null);
            binding = thread.getSpecialBinding(var);
            ext.declareSpecial(var);
          }
        else if (var.isSpecialVariable())
          {
            thread.bindSpecial(var, null);
            binding = thread.getSpecialBinding(var);
          }
        else
          {
            ext.bindLispSymbol(var, null);
            binding = ext.getBinding(var);
          }
        while (specials != NIL)
          {
            ext.declareSpecial(checkSymbol(specials.CAR()));
            specials = specials.CDR();
          }
        while (list != NIL)
          {
            if (binding instanceof SpecialBinding)
              ((SpecialBinding)binding).value = list.CAR();
            else
              ((Binding)binding).value = list.CAR();
            LispObject body = bodyForm;
            while (body != NIL)
              {
                LispObject current = body.CAR();
                if (current instanceof Cons)
                  {
                    try
                      {
                        // Handle GO inline if possible.
                        if (current.CAR() == SymbolConstants.GO)
                          {
                            LispObject tag = current.CADR();
                            Binding b = ext.getTagBinding(tag);
                            if (b != null && b.value != null)
                              {
                                body = b.value;
                                continue;
                              }
                            throw new Go(tag);
                          }
                        Lisp.eval(current, ext, thread);
                      }
                    catch (Go go)
                      {
                        LispObject tag = go.getTag();
                        Binding b = ext.getTagBinding(tag);
                        if (b != null && b.value != null)
                          {
                            body = b.value;
                            continue;
                          }
                        throw go;
                      }
                  }
                body = body.CDR();
              }
            list = list.CDR();
            if (interrupted)
              handleInterrupt();
          }
        if (binding instanceof SpecialBinding)
          ((SpecialBinding)binding).value = NIL;
        else
          ((Binding)binding).value = NIL;
        LispObject result = Lisp.eval(resultForm, ext, thread);
        return result;
      }
    catch (Return ret)
      {
        if (ret.getTag() == NIL)
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

  private static final dolist DOLIST = new dolist();
}
