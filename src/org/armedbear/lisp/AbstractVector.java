/*
 * AbstractVector.java
 *
 * Copyright (C) 2003-2006 Peter Graves
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

public abstract class AbstractVector extends AbstractArray
{
  @Override
  public LispObject typep(LispObject type) throws ConditionThrowable
  {
    if (type == SymbolConstants.VECTOR)
      return T;
    if (type == BuiltInClass.VECTOR)
      return T;
    if (type == SymbolConstants.SEQUENCE)
      return T;
    if (type == BuiltInClass.SEQUENCE)
      return T;
    return super.typep(type);
  }

  @Override
  public final LispObject VECTORP()
  {
    return T;
  }

  @Override
  public final boolean isVector()
  {
    return true;
  }

  @Override
  public boolean equalp(LispObject obj) throws ConditionThrowable
  {
    if (obj instanceof AbstractVector)
      {
        if (seqLength() != obj.seqLength())
          return false;
        AbstractVector v = (AbstractVector) obj;
        for (int i = seqLength(); i-- > 0;)
          if (!AREF(i).equalp(v.AREF(i)))
            return false;
        return true;
      }
    return false;
  }

  @Override
  public final int getRank()
  {
    return 1;
  }

  @Override
  public final LispObject getDimensions()
  {
    return new Cons(Fixnum.getInstance(capacity()));
  }

  @Override
  public final int getDimension(int n) throws ConditionThrowable
  {
    if (n != 0)
      {
        error(new TypeError("bad dimension for vector"));
        // Not reached.
        return 0;
      }
    return capacity();
  }

  @Override
  public final int getTotalSize()
  {
    return capacity();
  }

  public abstract int capacity();

  public abstract LispObject subseq(int start, int end) throws ConditionThrowable;

  public LispObject deleteEq(LispObject item) throws ConditionThrowable
  {
    final int limit = seqLength();
    int i = 0;
    int j = 0;
    while (i < limit)
      {
        LispObject obj = AREF(i++);
        if (obj != item)
          aset(j++, obj);
      }
    final int newLength = j;
    if (newLength < capacity())
      shrink(newLength);
    return this;
  }

  public LispObject deleteEql(LispObject item) throws ConditionThrowable
  {
    final int limit = seqLength();
    int i = 0;
    int j = 0;
    while (i < limit)
      {
        LispObject obj = AREF(i++);
        if (!obj.eql(item))
          aset(j++, obj);
      }
    final int newLength = j;
    if (newLength < capacity())
      shrink(newLength);
    return this;
  }

  public abstract void shrink(int n) throws ConditionThrowable;

  public int checkIndex(int index) throws ConditionThrowable
  {
    if (index < 0 || index >= capacity())
      badIndex(index, capacity());
    return index;
  }

  protected void badIndex(int index, int limit) throws ConditionThrowable
  {
    FastStringBuffer sb = new FastStringBuffer("Invalid array index ");
    sb.append(index);
    sb.append(" for ");
    sb.append(writeToString());
    if (limit > 0)
      {
        sb.append(" (should be >= 0 and < ");
        sb.append(limit);
        sb.append(").");
      }
    error(new TypeError(sb.toString(),
                         Fixnum.getInstance(index),
                         list(SymbolConstants.INTEGER,
                               Fixnum.ZERO,
                               Fixnum.getInstance(limit - 1))));

  }

  public void setFillPointer(int n) throws ConditionThrowable
  {
    noFillPointer();
  }

  public void setFillPointer(LispObject obj) throws ConditionThrowable
  {
    noFillPointer();
  }

  public boolean isSimpleVector()
  {
    return false;
  }

  @Override
  public abstract LispObject reverse() throws ConditionThrowable;

  @Override
  public LispObject nreverse() throws ConditionThrowable
  {
    int i = 0;
    int j = seqLength() - 1;
    while (i < j)
      {
        LispObject temp = AREF(i);
        aset(i, AREF(j));
        aset(j, temp);
        ++i;
        --j;
      }
    return this;
  }

  @Override
  public String writeToString() throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    if (SymbolConstants.PRINT_READABLY.symbolValue(thread) != NIL)
      {
        FastStringBuffer sb = new FastStringBuffer("#(");
        final int limit = seqLength();
        for (int i = 0; i < limit; i++)
          {
            if (i > 0)
              sb.append(' ');
            sb.append(AREF(i).writeToString());
          }
        sb.append(')');
        return sb.toString();
      }
    else if (SymbolConstants.PRINT_ARRAY.symbolValue(thread) != NIL)
      {
        int maxLevel = Integer.MAX_VALUE;
        final LispObject printLevel =
          SymbolConstants.PRINT_LEVEL.symbolValue(thread);
        if (printLevel instanceof Fixnum)
          maxLevel = ((Fixnum)printLevel).value;
        LispObject currentPrintLevel =
          _CURRENT_PRINT_LEVEL_.symbolValue(thread);
        int currentLevel = Fixnum.getValue(currentPrintLevel);
        if (currentLevel < maxLevel)
          {
            StringBuffer sb = new StringBuffer("#(");
            int maxLength = Integer.MAX_VALUE;
            final LispObject printLength =
              SymbolConstants.PRINT_LENGTH.symbolValue(thread);
            if (printLength instanceof Fixnum)
              maxLength = ((Fixnum)printLength).value;
            final int length = seqLength();
            final int limit = Math.min(length, maxLength);
            SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
            thread.bindSpecial(_CURRENT_PRINT_LEVEL_, currentPrintLevel.incr());
            try
              {
                for (int i = 0; i < limit; i++)
                  {
                    if (i > 0)
                      sb.append(' ');
                    sb.append(AREF(i).writeToString());
                  }
              }
            finally
              {
                thread.lastSpecialBinding = lastSpecialBinding;
              }
            if (limit < length)
              sb.append(limit > 0 ? " ..." : "...");
            sb.append(')');
            return sb.toString();
          }
        else
          return "#";
      }
    else
      {
        StringBuffer sb = new StringBuffer();
        sb.append(isSimpleVector() ? "SIMPLE-VECTOR " : "VECTOR ");
        sb.append(capacity());
        return unreadableString(sb.toString());
      }
  }

  // For EQUALP hash tables.
  @Override
  public int psxhash()
  {
    try
      {
        final int length = seqLength();
        final int limit = length < 4 ? length : 4;
        long result = 48920713; // Chosen at random.
        for (int i = 0; i < limit; i++)
          result = mix(result, AREF(i).psxhash());
        return (int) (result & 0x7fffffff);
      }
    catch (Throwable t)
      {
        // Shouldn't happen.
        Debug.trace(t);
        return 0;
      }
  }

  public abstract AbstractArray adjustArray(int size,
                                              LispObject initialElement,
                                              LispObject initialContents)
    throws ConditionThrowable;
  public abstract AbstractArray adjustArray(int size,
                                              AbstractArray displacedTo,
                                              int displacement)
    throws ConditionThrowable;


  public AbstractArray adjustArray(int[] dims,
                                              LispObject initialElement,
                                              LispObject initialContents)
    throws ConditionThrowable {
      return adjustArray(dims[0], initialElement, initialContents);
  }

  public AbstractArray adjustArray(int[] dims,
                                              AbstractArray displacedTo,
                                              int displacement)
    throws ConditionThrowable {
      return adjustArray(dims[0], displacedTo, displacement);
  }
}
