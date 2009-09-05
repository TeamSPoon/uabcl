/*
 * make_condition.java
 *
 * Copyright (C) 2003-2005 Peter Graves
 * $Id: make_condition.java 11488 2008-12-27 10:50:33Z ehuelsmann $
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

public final class make_condition extends Primitive
{
    private make_condition()
    {
        super("%make-condition", PACKAGE_SYS, true);
    }

    // ### %make-condition
    // %make-condition type slot-initializations => condition
    @Override
    public LispObject execute(LispObject type, LispObject initArgs)
        throws ConditionThrowable
    {
        final Symbol symbol;
        if (type instanceof Symbol)
            symbol = (Symbol) type;
        else if (type instanceof LispClass)
            symbol = ((LispClass)type).getSymbol();
        else {
            // This function only works on symbols and classes.
            return NIL;
        }

        if (symbol == SymbolConstants.ARITHMETIC_ERROR)
            return new ArithmeticError(initArgs);
        if (symbol == SymbolConstants.CELL_ERROR)
            return new CellError(initArgs);
        if (symbol == SymbolConstants.CONDITION)
            return new Condition(initArgs);
        if (symbol == SymbolConstants.CONTROL_ERROR)
            return new ControlError(initArgs);
        if (symbol == SymbolConstants.DIVISION_BY_ZERO)
            return new DivisionByZero(initArgs);
        if (symbol == SymbolConstants.END_OF_FILE)
            return new EndOfFile(initArgs);
        if (symbol == SymbolConstants.ERROR)
            return new LispError(initArgs);
        if (symbol == SymbolConstants.FILE_ERROR)
            return new FileError(initArgs);
        if (symbol == SymbolConstants.FLOATING_POINT_INEXACT)
            return new FloatingPointInexact(initArgs);
        if (symbol == SymbolConstants.FLOATING_POINT_INVALID_OPERATION)
            return new FloatingPointInvalidOperation(initArgs);
        if (symbol == SymbolConstants.FLOATING_POINT_OVERFLOW)
            return new FloatingPointOverflow(initArgs);
        if (symbol == SymbolConstants.FLOATING_POINT_UNDERFLOW)
            return new FloatingPointUnderflow(initArgs);
        if (symbol == SymbolConstants.PACKAGE_ERROR)
            return new PackageError(initArgs);
        if (symbol == SymbolConstants.PARSE_ERROR)
            return new ParseError(initArgs);
        if (symbol == SymbolConstants.PRINT_NOT_READABLE)
            return new PrintNotReadable(initArgs);
        if (symbol == SymbolConstants.PROGRAM_ERROR)
            return new ProgramError(initArgs);
        if (symbol == SymbolConstants.READER_ERROR)
            return new ReaderError(initArgs);
        if (symbol == SymbolConstants.SERIOUS_CONDITION)
            return new SeriousCondition(initArgs);
        if (symbol == SymbolConstants.SIMPLE_CONDITION)
            return new SimpleCondition(initArgs);
        if (symbol == SymbolConstants.SIMPLE_ERROR)
            return new SimpleError(initArgs);
        if (symbol == SymbolConstants.SIMPLE_TYPE_ERROR)
            return new SimpleTypeError(initArgs);
        if (symbol == SymbolConstants.SIMPLE_WARNING)
            return new SimpleWarning(initArgs);
        if (symbol == SymbolConstants.STORAGE_CONDITION)
            return new StorageCondition(initArgs);
        if (symbol == SymbolConstants.STREAM_ERROR)
            return new StreamError(initArgs);
        if (symbol == SymbolConstants.STYLE_WARNING)
            return new StyleWarning(initArgs);
        if (symbol == SymbolConstants.TYPE_ERROR)
            return new TypeError(initArgs);
        if (symbol == SymbolConstants.UNBOUND_SLOT)
            return new UnboundSlot(initArgs);
        if (symbol == SymbolConstants.UNBOUND_VARIABLE)
            return new UnboundVariable(initArgs);
        if (symbol == SymbolConstants.UNDEFINED_FUNCTION)
            return new UndefinedFunction(initArgs);
        if (symbol == SymbolConstants.WARNING)
            return new Warning(initArgs);

        if (symbol == SymbolConstants.COMPILER_ERROR)
            return new CompilerError(initArgs);
        if (symbol == SymbolConstants.COMPILER_UNSUPPORTED_FEATURE_ERROR)
            return new CompilerUnsupportedFeatureError(initArgs);

        return NIL;
    }

    private static final Primitive MAKE_CONDITION = new make_condition();
}
