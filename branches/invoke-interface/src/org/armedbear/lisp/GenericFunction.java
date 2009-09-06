/*
 * GenericFunction.java
 *
 * Copyright (C) 2003-2005 Peter Graves
 * $Id: GenericFunction.java 11488 2008-12-27 10:50:33Z ehuelsmann $
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

public abstract class GenericFunction extends AbstractStandardObject
{
	@Override
	public LispObject[] getSlots() {
		// TODO Auto-generated method stub
		return slots;
	}
	@Override
	void setSlots(LispObject[] lispObjects) {
		slots = lispObjects;
	}
	
	  private Layout layout;
	  private LispObject[] slots;
	  
	  public int getInstanceSlotLength() throws ConditionThrowable {
			// TODO Auto-generated method stub
			return slots.length;
		}
	  public Layout getLayout() {
	    return layout;
	  }
	  public void setLayout(Layout checkLayout) {
		  layout = checkLayout;
	  }
	  public LispObject getSlot(int index) {
	      try
	      {
	        return slots[index];
	      }
	    catch (ArrayIndexOutOfBoundsException e)
	      {
	        return type_error(Fixnum.makeFixnum(index),
	                               list(SymbolConstants.INTEGER, Fixnum.ZERO,
	                                     Fixnum.makeFixnum(getInstanceSlotLength())));
	      }
	  }
	  public void setSlot(int index, LispObject value) {
	      try
	      {
	        slots[index] = value;
	      }
	    catch (ArrayIndexOutOfBoundsException e)
	      {
	        type_error(Fixnum.makeFixnum(index),
	                               list(SymbolConstants.INTEGER, Fixnum.ZERO,
	                                     Fixnum.makeFixnum(getInstanceSlotLength())));
	      }
	  }

    protected GenericFunction(LispClass cls, int length)
    {
        layout = cls.getClassLayout();
        slots = new LispObject[length];
        for (int i = slots.length; i-- > 0;)
          slots[i] = UNBOUND_VALUE;
    }

    @Override
    public LispObject typep(LispObject type) throws ConditionThrowable
    {
        if (type == SymbolConstants.GENERIC_FUNCTION)
            return T;
        if (type == StandardClass.GENERIC_FUNCTION)
            return T;
        if (type == SymbolConstants.FUNCTION)
            return T;
        if (type == BuiltInClass.FUNCTION)
            return T;
        return super.typep(type);
    }
}
