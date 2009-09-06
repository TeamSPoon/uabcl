/*
 * LispClass.java
 *
 * Copyright (C) 2003-2005 Peter Graves
 * $Id: LispClass.java 11754 2009-04-12 10:53:39Z vvoutilainen $
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

public abstract class LispClass extends AbstractStandardObject
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
  private final int sxhash;

  protected Symbol symbol;
  private LispObject propertyList;
  private Layout classLayout;
  private LispObject directSuperclasses = NIL;
  private LispObject directSubclasses = NIL;
  public LispObject classPrecedenceList = NIL; // FIXME! Should be private!
  public LispObject directMethods = NIL; // FIXME! Should be private!
  public LispObject documentation = NIL; // FIXME! Should be private!
  private boolean finalized;

  protected LispClass()
  {
    sxhash = clHash() & 0x7fffffff;
  }

  protected LispClass(Symbol symbol)
  {
    sxhash = clHash() & 0x7fffffff;
    this.symbol = symbol;
    this.directSuperclasses = NIL;
  }

  protected LispClass(Symbol symbol, LispObject directSuperclasses)
  {
    sxhash = clHash() & 0x7fffffff;
    this.symbol = symbol;
    this.directSuperclasses = directSuperclasses;
  }

  @Override
  public LispObject getParts() throws ConditionThrowable
  {
    LispObject result = NIL;
    result = result.push(makeCons("NAME", symbol != null ? symbol : NIL));
    result = result.push(makeCons("LAYOUT", classLayout != null ? classLayout : NIL));
    result = result.push(makeCons("DIRECT-SUPERCLASSES", directSuperclasses));
    result = result.push(makeCons("DIRECT-SUBCLASSES", directSubclasses));
    result = result.push(makeCons("CLASS-PRECEDENCE-LIST", classPrecedenceList));
    result = result.push(makeCons("DIRECT-METHODS", directMethods));
    result = result.push(makeCons("DOCUMENTATION", documentation));
    return result.nreverse();
  }

  @Override
  public final int sxhash()
  {
    return sxhash;
  }

  public final Symbol getSymbol()
  {
    return symbol;
  }

  @Override
  public final LispObject getPropertyList()
  {
    if (propertyList == null)
      propertyList = NIL;
    return propertyList;
  }

  @Override
  public final void setPropertyList(LispObject obj)
  {
    if (obj == null)
      throw new NullPointerException();
    propertyList = obj;
  }

  public final Layout getClassLayout()
  {
    return classLayout;
  }

  public final void setClassLayout(Layout layout)
  {
    classLayout = layout;
  }

  public final int getLayoutLength()
  {
	final Layout layout = getLayout();
    if (layout == null)
      return 0;
    return layout.getLength();
  }

  public final LispObject getDirectSuperclasses()
  {
    return directSuperclasses;
  }

  public final void setDirectSuperclasses(LispObject directSuperclasses)
  {
    this.directSuperclasses = directSuperclasses;
  }

  public final boolean isFinalized()
  {
    return finalized;
  }

  public final void setFinalized(boolean b)
  {
    finalized = b;
  }

  // When there's only one direct superclass...
  public final void setDirectSuperclass(LispObject superclass)
  {
    directSuperclasses = makeCons(superclass);
  }

  public final LispObject getDirectSubclasses()
  {
    return directSubclasses;
  }

  public final void setDirectSubclasses(LispObject directSubclasses)
  {
    this.directSubclasses = directSubclasses;
  }

  public final LispObject getCPL()
  {
    return classPrecedenceList;
  }

  public final void setCPL(LispObject obj1)
  {
    if (obj1 instanceof Cons)
      classPrecedenceList = obj1;
    else
      {
        Debug.assertTrue(obj1 == this);
        classPrecedenceList = makeCons(obj1);
      }
  }

  public final void setCPL(LispObject obj1, LispObject obj2)
  {
    Debug.assertTrue(obj1 == this);
    classPrecedenceList = list(obj1, obj2);
  }

  public final void setCPL(LispObject obj1, LispObject obj2, LispObject obj3)
  {
    Debug.assertTrue(obj1 == this);
    classPrecedenceList = list(obj1, obj2, obj3);
  }

  public final void setCPL(LispObject obj1, LispObject obj2, LispObject obj3,
                           LispObject obj4)
  {
    Debug.assertTrue(obj1 == this);
    classPrecedenceList = list(obj1, obj2, obj3, obj4);
  }

  public final void setCPL(LispObject obj1, LispObject obj2, LispObject obj3,
                           LispObject obj4, LispObject obj5)
  {
    Debug.assertTrue(obj1 == this);
    classPrecedenceList = list(obj1, obj2, obj3, obj4, obj5);
  }

  public final void setCPL(LispObject obj1, LispObject obj2, LispObject obj3,
                           LispObject obj4, LispObject obj5, LispObject obj6)
  {
    Debug.assertTrue(obj1 == this);
    classPrecedenceList = list(obj1, obj2, obj3, obj4, obj5, obj6);
  }

  public final void setCPL(LispObject obj1, LispObject obj2, LispObject obj3,
                           LispObject obj4, LispObject obj5, LispObject obj6,
                           LispObject obj7)
  {
    Debug.assertTrue(obj1 == this);
    classPrecedenceList = list(obj1, obj2, obj3, obj4, obj5, obj6, obj7);
  }

  public final void setCPL(LispObject obj1, LispObject obj2, LispObject obj3,
                           LispObject obj4, LispObject obj5, LispObject obj6,
                           LispObject obj7, LispObject obj8)
  {
    Debug.assertTrue(obj1 == this);
    classPrecedenceList =
      list(obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8);
  }

  public final void setCPL(LispObject obj1, LispObject obj2, LispObject obj3,
                           LispObject obj4, LispObject obj5, LispObject obj6,
                           LispObject obj7, LispObject obj8, LispObject obj9)
  {
    Debug.assertTrue(obj1 == this);
    classPrecedenceList =
      list(obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9);
  }

  public String getName()
  {
    return symbol.getName();
  }

  @Override
  public LispObject typeOf()
  {
    return SymbolConstants.CLASS;
  }

  @Override
  public LispObject classOf()
  {
    return StandardClass.CLASS;
  }

  @Override
  public LispObject typep(LispObject type) throws ConditionThrowable
  {
    if (type == SymbolConstants.CLASS)
      return T;
    if (type == StandardClass.CLASS)
      return T;
    return super.typep(type);
  }

  public boolean subclassp(LispObject obj) throws ConditionThrowable
  {
    LispObject cpl = classPrecedenceList;
    while (cpl != NIL)
      {
        if (cpl.CAR() == obj)
          return true;
        cpl = ((Cons)cpl).CDR();
      }
    return false;
  }

  // ### find-class symbol &optional errorp environment => class
  private static final Primitive FIND_CLASS =
    new Primitive(SymbolConstants.FIND_CLASS, "symbol &optional errorp environment")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return findLispClass(arg, true);
      }
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
        return findLispClass(first, second != NIL);
      }
      @Override
      public LispObject execute(LispObject first, LispObject second,
                                LispObject third)
        throws ConditionThrowable
      {
        // FIXME Use environment!
        return findLispClass(first, second != NIL);
      }
    };

  // ### %set-find-class
  private static final Primitive _SET_FIND_CLASS =
    new Primitive("%set-find-class", PACKAGE_SYS, true)
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
        final Symbol name = checkSymbol(first);
        if (second == NIL)
          {
            removeLispClass(name);
            return second;
          }
        final LispClass c = checkClass(second);
        addLispClass(name, c);
        return second;
      }
    };

  // ### subclassp
  private static final Primitive SUBCLASSP =
    new Primitive(SymbolConstants.SUBCLASSP, "class")
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
        final LispClass c = checkClass(first);
        return c.subclassp(second) ? T : NIL;
      }
    };
}
