/*
 * StandardObject.java
 *
 * Copyright (C) 2003-2006 Peter Graves
 * $Id: StandardObject.java 11754 2009-04-12 10:53:39Z vvoutilainen $
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

abstract public class AbstractStandardObject extends AbstractLispObject implements StandardObject
{  
	
	  protected LispObject invalidInstanceSlot(StandardObject obj, LispObject slotName, Symbol fun) {	  
	      return SymbolConstants.SLOT_MISSING.execute(obj.getLispClass(), obj, slotName,
	              fun);
	  }

	@Override
		public String toString() {
		   return Lisp.safeWriteToString(this);
		}
  protected void ensureLayoutValid() {
	    Debug.assertTrue(layout != null);
	    if (layout.isInvalid())
	      {
	    	layout = updateLayout();
	        // Update instance.
		    Debug.assertTrue(layout != null);
	      }	
	   }
  public int getInstanceSlotLength() throws ConditionThrowable {
	  Debug.traceStep("AbstractStandardObject: " + this);
	  return -1;
  }
  
  public void setSlots(LispObject[] lispObjects) {
	  Debug.traceStep("AbstractStandardObject: " + this);
  }
  
  public LispObject[] getSlots() {
	  Debug.traceStep("AbstractStandardObject: " + this);
	  return null;
  }
  
  public LispObject getSlot(int index) {
	  Debug.traceStep("AbstractStandardObject: " + this);
	  return null;
	}
  public void setSlot(int intValue, LispObject third) {
	  Debug.traceStep("AbstractStandardObject: " + this);
  }
  
  protected Layout layout;
  public Layout getLayout() {	
    return layout;
  }
  public void setLayout(Layout checkLayout) {
	Debug.traceStep("AbstractStandardObject: " + this);
    layout = checkLayout;
  }
	  
  
  protected AbstractStandardObject(Layout l)
  {
	layout = l;  
	//  setLayout(new Layout(StandardClass.STANDARD_OBJECT, NIL, NIL));
  }

  @Override
  public LispObject getParts() throws ConditionThrowable
  {
    LispObject parts = NIL;
    if (layout != null)
      {
      	ensureLayoutValid();
      }
    parts = parts.push(makeCons("LAYOUT", getLayout()));
    if (getLayout() != null)
      {
        LispObject[] slotNames = getLayout().getSlotNames();
        if (slotNames != null)
          {
            for (int i = 0; i < slotNames.length; i++)
              {
                parts = parts.push(makeCons(slotNames[i], getSlot(i)));
              }
          }
      }
    return parts.nreverse();
  }

  public LispClass getLispClass()
  {
    return getLayout().lispClass;
  }

  @Override
  public LispObject typeOf()
  {
    // "For objects of metaclass STRUCTURE-CLASS or STANDARD-CLASS, and for
    // conditions, TYPE-OF returns the proper name of the class returned by
    // CLASS-OF if it has a proper name, and otherwise returns the class
    // itself."
    final LispClass c1 = getLayout().lispClass;
    // The proper name of a class is "a symbol that names the class whose
    // name is that symbol".
    final Symbol symbol = c1.getSymbol();
    if (symbol != NIL)
      {
        // TYPE-OF.9
        final LispObject c2 = findLispClass(symbol);
        if (c2 == c1)
          return symbol;
      }
    return c1;
  }

  @Override
  public LispObject classOf()
  {
    return getLayout().lispClass;
  }

  @Override
  public LispObject typep(LispObject type) throws ConditionThrowable
  {
    if (type == SymbolConstants.STANDARD_OBJECT)
      return T;
    if (type == StandardClass.STANDARD_OBJECT)
      return T;
    LispClass cls = layout != null ? layout.lispClass : null;
    if (cls != null)
      {
        if (type == cls)
          return T;
        if (type == cls.getSymbol())
          return T;
        LispObject cpl = cls.getCPL();
        while (cpl != NIL)
          {
            if (type == cpl.CAR())
              return T;
            if (type == ((LispClass)cpl.CAR()).getSymbol())
              return T;
            cpl = cpl.CDR();
          }
      }
    return super.typep(type);
  }

  @Override
  public String writeToString() throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    int maxLevel = Integer.MAX_VALUE;
    LispObject printLevel = SymbolConstants.PRINT_LEVEL.symbolValue(thread);
    if (printLevel  instanceof Fixnum)
      maxLevel = printLevel.intValue();
    LispObject currentPrintLevel =
      _CURRENT_PRINT_LEVEL_.symbolValue(thread);
    int currentLevel = currentPrintLevel.intValue();
    if (currentLevel >= maxLevel)
      return "#";
    if (typep(SymbolConstants.CONDITION) != NIL)
      {
        StringOutputStream stream = new StringOutputStream();
        SymbolConstants.PRINT_OBJECT.execute(this, stream);
        return stream.getStringOutputString().getStringValue();
      }
    return unreadableString(typeOf().writeToString());
  }

  public Layout updateLayout() throws ConditionThrowable
  {
    Debug.assertTrue(getLayout().isInvalid());
    Layout oldLayout = getLayout();
    LispClass cls = oldLayout.lispClass;
    Layout newLayout = cls.getClassLayout();
    Debug.assertTrue(!newLayout.isInvalid());
    StandardObjectImpl newInstance = new StandardObjectImpl(cls);
    Debug.assertTrue(newInstance.layout == newLayout);
    LispObject added = NIL;
    LispObject discarded = NIL;
    LispObject plist = NIL;
    // Old local slots.
    LispObject[] oldSlotNames = oldLayout.getSlotNames();
    for (int i = 0; i < oldSlotNames.length; i++)
      {
        LispObject slotName = oldSlotNames[i];
        int j = newLayout.getSlotIndex(slotName);
        if (j >= 0)
          newInstance.setSlot(j, getSlot(i));
        else
          {
            discarded = discarded.push(slotName);
            LispObject getSlotI = getSlot(i);
            if (getSlotI != UNBOUND_VALUE)
              {
                plist = plist.push(slotName);
                plist = plist.push(getSlotI);
              }
          }
      }
    // Old shared slots.
    LispObject rest = oldLayout.getSharedSlots(); // A list.
    if (rest != null)
      {
        while (rest != NIL)
          {
            LispObject location = rest.CAR();
            LispObject slotName = location.CAR();
            int i = newLayout.getSlotIndex(slotName);
            if (i >= 0)
              newInstance.setSlot(i, location.CDR());
            rest = rest.CDR();
          }
      }
    // Go through all the new local slots to compute the added slots.
    LispObject[] newSlotNames = newLayout.getSlotNames();
    for (int i = 0; i < newSlotNames.length; i++)
      {
        LispObject slotName = newSlotNames[i];
        int j = oldLayout.getSlotIndex(slotName);
        if (j >= 0)
          continue;
        LispObject location = oldLayout.getSharedSlotLocation(slotName);
        if (location != null)
          continue;
        // Not found.
        added = added.push(slotName);
      }
    // Swap slots.
    LispObject[] tempSlots = getSlots();
    setSlots(newInstance.getSlots());
    newInstance.setSlots(tempSlots);
    // Swap layouts.
    Layout tempLayout = getLayout();
    layout = (newInstance.layout);
    newInstance.layout = (tempLayout);
    Debug.assertTrue(!layout.isInvalid());
    // Call UPDATE-INSTANCE-FOR-REDEFINED-CLASS.
    SymbolConstants.UPDATE_INSTANCE_FOR_REDEFINED_CLASS.execute(this, added,
                                                       discarded, plist);
    return newLayout;
  }

  // Only handles instance slots (not shared slots).
  public LispObject getInstanceSlotValue(LispObject slotName)
    throws ConditionThrowable
  {
	ensureLayoutValid();
    int index = getLayout().getSlotIndex(slotName);
    Debug.assertTrue(index >= 0);
    return getSlot(index);
  }

  // Only handles instance slots (not shared slots).
  public void setInstanceSlotValue(LispObject slotName, LispObject newValue)
    throws ConditionThrowable
  {
    ensureLayoutValid();
    int index = getLayout().getSlotIndex(slotName);
    Debug.assertTrue(index >= 0);
    setSlot(index, newValue);
  }

  @Override
  public LispObject SLOT_VALUE(LispObject slotName) throws ConditionThrowable
  {
	ensureLayoutValid();
    LispObject value;
    final LispObject index = getLayout().slotTable.get(slotName);
    if (index != null)
      {
        // Found instance slot.
        value = getSlot(index.intValue());
      }
    else
      {
        // Check for shared slot.
        LispObject location = getLayout().getSharedSlotLocation(slotName);
        if (location == null)
            return invalidInstanceSlot(this,slotName,SymbolConstants.SLOT_VALUE);
        value = location.CDR();
      }
    if (value == UNBOUND_VALUE)
      {
        value = SymbolConstants.SLOT_UNBOUND.execute(getLispClass(), this, slotName);
        LispThread.currentThread()._values = null;
      }
    return value;
  }

  @Override
  public void setSlotValue(LispObject slotName, LispObject newValue)
    throws ConditionThrowable
  {
    if (getLayout().isInvalid())
      {
        // Update instance.
        setLayout(updateLayout());
      }
    final LispObject index = getLayout().slotTable.get(slotName);
    if (index != null)
      {
        // Found instance slot.
        setSlot(index.intValue(), newValue);
        return;
      }
    // Check for shared slot.
    LispObject location = getLayout().getSharedSlotLocation(slotName);
    if (location != null)
      {
        location.setCdr(newValue);
        return;
      }
    LispObject[] args = new LispObject[5];
    args[0] = getLispClass();
    args[1] = this;
    args[2] = slotName;
    args[3] = SymbolConstants.SETF;
    args[4] = newValue;
    SymbolConstants.SLOT_MISSING.execute(args);
  }
  
}
