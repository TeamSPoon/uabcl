/*
 * AbstractArray.java
 *
 * Copyright (C) 2003-2005 Peter Graves
 * $Id: AbstractArray.java 11711 2009-03-15 15:51:40Z ehuelsmann $
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

public interface LispArray extends LispObject
{
	abstract public LispObject AREF(int index) throws ConditionThrowable;
	 
    //@Override
    public LispObject typep(LispObject type) throws ConditionThrowable;

    //@Override
    public boolean equalp(LispObject obj) throws ConditionThrowable;

    public boolean isDisplaced();

    public LispObject arrayDisplacement() throws ConditionThrowable;
    
    public boolean hasFillPointer();

    public int getFillPointer() throws ConditionThrowable;

    public void setFillPointer(LispObject fillPointer) throws ConditionThrowable;

    public void setFillPointer(int fillPointer) throws ConditionThrowable;

    public boolean isAdjustable();

    public abstract int getRank();

    public abstract LispObject getDimensions();

    public abstract int getDimension(int n) throws ConditionThrowable;

    public abstract LispObject getElementType();

    public abstract int getTotalSize();

    //@Override
    public abstract void aset(int index, LispObject newValue)
        throws ConditionThrowable;


    public int getRowMajorIndex(LispObject[] subscripts)
        throws ConditionThrowable;

    public int getRowMajorIndex(int[] subscripts) throws ConditionThrowable;
    public LispObject get(int[] subscripts) throws ConditionThrowable;
    public void set(int[] subscripts, LispObject newValue)
        throws ConditionThrowable;
    public abstract void fillVoid(LispObject obj) throws ConditionThrowable;

    public String writeToString(int[] dimv) throws ConditionThrowable;
    // Helper for writeToString().

    // For EQUALP hash tables.
    //@Override
    public int psxhash();

    /** Returns a newly allocated array or the current array with
     * adjusted dimensions.
     *
     * @param dims
     * @param initialElement @c null if none
     * @param initialContents @c null if none
     * @return @c this or a new array
     * @throws org.armedbear.lisp.ConditionThrowable
     */
    public abstract LispArray adjustArray(int[] dims,
                                              LispObject initialElement,
                                              LispObject initialContents)
        throws ConditionThrowable;

    /**
     *
     * @param dims
     * @param displacedTo
     * @param displacement
     * @return
     * @throws org.armedbear.lisp.ConditionThrowable
     */
    public abstract LispArray adjustArray(int[] dims,
                                              LispArray displacedTo,
                                              int displacement)
        throws ConditionThrowable;
}
