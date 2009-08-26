/*
 * JavaObject.java
 *
 * Copyright (C) 2002-2005 Peter Graves
 * $Id: JavaObject.java 12037 2009-07-11 12:46:04Z ehuelsmann $
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
import java.lang.reflect.*;

import java.math.BigInteger;

import java.util.*;

public interface IJavaObject 
{
    public LispObject typeOf();
    public LispObject classOf();
    public LispObject typep(LispObject type) throws ConditionThrowable;
    /** Encapsulates obj, if required.
     * If obj is a {@link  LispObject}, it's returned as-is.
     * 
     * @param obj Any java object
     * @return obj or a new JavaObject encapsulating obj
     */
    public Object getObject();
    public Object javaInstance();

    public Object javaInstance(Class c) throws ConditionThrowable;

    /** Returns the encapsulated Java object for
     * interoperability with wait, notify, synchronized, etc.
     *
     * @return The encapsulated object
     */

    public Object lockableInstance();

    public boolean equal(LispObject other);

    public  boolean equalp(LispObject other);
    public int sxhash();
    public String writeToString() throws ConditionThrowable;

}
