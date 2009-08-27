/*
 * Layout.java
 *
 * Copyright (C) 2003-2006 Peter Graves
 * $Id: Layout.java 11754 2009-04-12 10:53:39Z vvoutilainen $
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

public final class Layout extends AbstractLispObject
{
  public final LispClass lispClass;
  public final EqHashTable slotTable;

  /*private*/ final LispObject[] slotNames;
  /*private*/ final LispObject sharedSlots;

  private boolean invalid;

  public Layout(LispClass lispClass, LispObject instanceSlots, LispObject sharedSlots)
  {
    this.lispClass = lispClass;
    Debug.assertTrue(instanceSlots.isList());
    int length = 0;
    try
      {
        length = instanceSlots.seqLength();
      }
    catch (Throwable t)
      {
        // Shouldn't happen.
        Debug.trace(t);
      }
    slotNames = new LispObject[length];
    int i = 0;
    try
      {
        while (instanceSlots != NIL)
          {
            slotNames[i++] = instanceSlots.first();
            instanceSlots = instanceSlots.rest();
          }
      }
    catch (Throwable t)
      {
        // Shouldn't happen.
        Debug.trace(t);
      }
    Debug.assertTrue(i == length);
    this.sharedSlots = sharedSlots;
    slotTable = initializeSlotTable(slotNames);
  }

  public Layout(LispClass lispClass, LispObject[] instanceSlotNames,
                LispObject sharedSlots)
  {
    this.lispClass = lispClass;
    this.slotNames = instanceSlotNames;
    this.sharedSlots = sharedSlots;
    slotTable = initializeSlotTable(slotNames);
  }

  // Copy constructor.
  /*private*/ Layout(Layout oldLayout)
  {
    lispClass = oldLayout.lispClass;
    slotNames = oldLayout.slotNames;
    sharedSlots = oldLayout.sharedSlots;
    slotTable = initializeSlotTable(slotNames);
  }

  private EqHashTable initializeSlotTable(LispObject[] slotNames)
  {
    EqHashTable ht = new EqHashTable(slotNames.length, NIL, NIL);
    for (int i = slotNames.length; i-- > 0;)
      ht.putVoid(slotNames[i], Fixnum.getInstance(i));
    return ht;
  }

  @Override
  public LispObject getParts() throws ConditionThrowable
  {
    LispObject result = NIL;
    result = result.push(new Cons("class", lispClass));
    for (int i = 0; i < slotNames.length; i++)
      {
        result = result.push(new Cons("slot " + i, slotNames[i]));
      }
    result = result.push(new Cons("shared slots", sharedSlots));
    return result.nreverse();
  }

  public boolean isInvalid()
  {
    return invalid;
  }

  public void invalidate()
  {
    invalid = true;
  }

  public LispObject[] getSlotNames()
  {
    return slotNames;
  }

  public int getLength()
  {
    return slotNames.length;
  }

  public LispObject getSharedSlots()
  {
    return sharedSlots;
  }

  @Override
  public String writeToString() throws ConditionThrowable
  {
    return unreadableString(SymbolConstants.LAYOUT);
  }

  // Generates a list of slot definitions for the slot names in this layout.
  protected LispObject generateSlotDefinitions()
  {
    LispObject list = NIL;
    try
      {
        for (int i = slotNames.length; i-- > 0;)
          list = list.push(new SlotDefinition(slotNames[i], NIL));
      }
    catch (Throwable t)
      {
        // Shouldn't happen.
        Debug.trace(t);
      }
    return list;
  }

  // ### make-layout
  private static final Primitive MAKE_LAYOUT =
    new Primitive("make-layout", PACKAGE_SYS, true,
                  "class instance-slots class-slots")
    {
      @Override
      public LispObject execute(LispObject first, LispObject second,
                                LispObject third)
        throws ConditionThrowable
      {
          return new Layout(checkClass(first), checkList(second),
                              checkList(third));
      }

    };

  // ### layout-class
  private static final Primitive LAYOUT_CLASS =
    new Primitive("layout-class", PACKAGE_SYS, true, "layout")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return checkLayout(arg).lispClass;
      }
    };

  // ### layout-length
  private static final Primitive LAYOUT_LENGTH =
    new Primitive("layout-length", PACKAGE_SYS, true, "layout")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return Fixnum.getInstance(checkLayout(arg).slotNames.length);
      }
    };

  public int getSlotIndex(LispObject slotName)
  {
    LispObject index = slotTable.get(slotName);
    if (index != null)
      return ((Fixnum)index).value;
    return -1;
  }

  public LispObject getSharedSlotLocation(LispObject slotName)
    throws ConditionThrowable
  {
    LispObject rest = sharedSlots;
    while (rest != NIL)
      {
        LispObject location = rest.first();
        if (location.first() == slotName)
          return location;
        rest = rest.rest();
      }
    return null;
  }

  // ### layout-slot-index layout slot-name => index
  private static final Primitive LAYOUT_SLOT_INDEX =
    new Primitive("layout-slot-index", PACKAGE_SYS, true)
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
          final LispObject slotNames[] = checkLayout(first).slotNames;
          for (int i = slotNames.length; i-- > 0;)
            {
              if (slotNames[i] == second)
                return Fixnum.getInstance(i);
            }
          return NIL;
      }
    };

  // ### layout-slot-location layout slot-name => location
  private static final Primitive LAYOUT_SLOT_LOCATION =
    new Primitive("layout-slot-location", PACKAGE_SYS, true, "layout slot-name")
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
                final Layout layOutFirst = checkLayout(first);
            final LispObject slotNames[] = layOutFirst.slotNames;
            final int limit = slotNames.length;
            for (int i = 0; i < limit; i++)
              {
                if (slotNames[i] == second)
                  return Fixnum.getInstance(i);
              }
            // Reaching here, it's not an instance slot.
            LispObject rest = layOutFirst.sharedSlots;
            while (rest != NIL)
              {
                LispObject location = rest.first();
                if (location.first() == second)
                  return location;
                rest = rest.rest();
              }
            return NIL;
          }
    };

  // ### %make-instances-obsolete class => class
  private static final Primitive _MAKE_INSTANCES_OBSOLETE =
    new Primitive("%make-instances-obsolete", PACKAGE_SYS, true, "class")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        final LispClass lispClass = checkClass(arg);
        Layout oldLayout = lispClass.getClassLayout();
        Layout newLayout = new Layout(oldLayout);
        lispClass.setClassLayout(newLayout);
        oldLayout.invalidate();
        return arg;
      }
    };
}
