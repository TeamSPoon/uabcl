/*
 * LispCharacter.java
 *
 * Copyright (C) 2002-2007 Peter Graves
 * $Id: LispCharacter.java 11836 2009-05-06 18:52:17Z ehuelsmann $
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

import java.util.HashMap;
import java.util.Map;

//import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLCharacter;

public class LispCharacter extends AbstractLispObject
{
  public static final LispCharacter[] constants = new LispCharacter[CHAR_MAX];

  static
  {
    for (int i = constants.length; i-- > 0;)
      constants[i] = new LispCharacter((char)i);
  }

  public final char value;

  protected String mainName;

  public static LispCharacter getLispCharacter(char c)
  {
    try
      {
        return constants[c];
      }
    catch (ArrayIndexOutOfBoundsException e)
      {
        return new LispCharacter(c);
      }
  }

  // This needs to be public for the compiler.
  public LispCharacter(char c)
  {
    this.value = c;
  }

  @Override
  public LispObject typeOf()
  {
    if (isStandardChar())
      return SymbolConstants.STANDARD_CHAR;
    return SymbolConstants.CHARACTER;
  }

  @Override
  public LispObject classOf()
  {
    return BuiltInClass.CHARACTER;
  }

  @Override
  public LispObject getDescription()
  {
    FastStringBuffer sb = new FastStringBuffer("character #\\");
    sb.append(value);
    sb.append(" char-code #x");
    sb.append(Integer.toHexString(value));
    return new SimpleString(sb);
  }

  @Override
  public LispObject typep(LispObject type) throws ConditionThrowable
  {
    if (type == SymbolConstants.CHARACTER)
      return T;
    if (type == BuiltInClass.CHARACTER)
      return T;
    if (type == SymbolConstants.BASE_CHAR)
      return T;
    if (type == SymbolConstants.STANDARD_CHAR)
      return isStandardChar() ? T : NIL;
    return super.typep(type);
  }

  @Override
  public LispObject CHARACTERP()
  {
    return T;
  }

  @Override
  public boolean isChar()
  {
    return true;
  }

  @Override
  public LispObject STRING()
  {
    return new SimpleString(value);
  }

  /*private*/ boolean isStandardChar()
  {
    if (value >= ' ' && value < 127)
      return true;
    if (value == '\n')
      return true;
    return false;
  }

  @Override
  public boolean eql(char c)
  {
    return value == c;
  }

  @Override
  public boolean eql(LispObject obj)
  {
    if (this == obj)
      return true;
    if (obj instanceof LispCharacter)
      {
        if (value == ((LispCharacter)obj).value)
          return true;
      }
    return false;
  }

  @Override
  public boolean equal(LispObject obj)
  {
    if (this == obj)
      return true;
    if (obj instanceof LispCharacter)
      {
        if (value == ((LispCharacter)obj).value)
          return true;
      }
    return false;
  }

  @Override
  public boolean equalp(LispObject obj)
  {
    if (this == obj)
      return true;
    if (obj instanceof LispCharacter)
      {
        if (value == ((LispCharacter)obj).value)
          return true;
        return LispCharacter.toLowerCase(value) == LispCharacter.toLowerCase(((LispCharacter)obj).value);
      }
    return false;
  }

//  public static char getValue(LispObject obj) throws ConditionThrowable
//  {       
//	  return obj.charValue();
////          if (obj instanceof LispCharacter)
////        return ((LispCharacter)obj).value;
////      type_error(obj, SymbolConstants.CHARACTER);
////        // Not reached.
////      return 0;
//  }

//  public final char getValue()
//  {
//    return value;
//  }

  @Override
  public Object javaInstance()
  {
    return Character.valueOf(value);
  }

  @Override
  public Object javaInstance(Class c)
  {
    return javaInstance();
  }

  @Override
  public int sxhash()
  {
    return value;
  }

  @Override
  public int psxhash()
  {
    return Character.toUpperCase(value);
  }

  @Override
  public final String writeToString() throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    boolean printReadably = (SymbolConstants.PRINT_READABLY.symbolValue(thread) != NIL);
    // "Specifically, if *PRINT-READABLY* is true, printing proceeds as if
    // *PRINT-ESCAPE*, *PRINT-ARRAY*, and *PRINT-GENSYM* were also true,
    // and as if *PRINT-LENGTH*, *PRINT-LEVEL*, and *PRINT-LINES* were
    // false."
    boolean printEscape =
      printReadably || (SymbolConstants.PRINT_ESCAPE.symbolValue(thread) != NIL);
    FastStringBuffer sb = new FastStringBuffer();
    if (printEscape)
      {
        sb.append("#\\");
        switch (value)
          {
          case 0:
              sb.append("Null");
              break;
          case 1:
              sb.append("Soh");
              break;
          case 7:
            sb.append("Bell");
            break;
          case '\b':
            sb.append("Backspace");
            break;
          case '\t':
            sb.append("Tab");
            break;
          case '\n':
            sb.append("Newline");
            break;
          case '\f':
            sb.append("Page");
            break;
          case '\r':
            sb.append("Return");
            break;
          case 27:
            sb.append("Escape");
            break;
          case 127:
            sb.append("Rubout");
            break;
          default:
        	if (mainName!=null) sb.append(mainName);
        	else
            sb.append(value);
            break;
          }
      }
    else
      {
        sb.append(value);
      }
    return sb.toString();
  }

  // ### character
  private static final Primitive CHARACTER =
    new Primitive(SymbolConstants.CHARACTER, "character")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        if (arg instanceof LispCharacter)
          return arg;
        if (arg instanceof AbstractString)
          {
            if (arg.size() == 1)
              return ((AbstractString)arg).AREF(0);
          }
        else if (arg instanceof Symbol)
          {
            String name = ((Symbol)arg).getName();
            if (name.length() == 1)
              return LispCharacter.getLispCharacter(name.charAt(0));
          }
        return type_error(arg, SymbolConstants.CHARACTER_DESIGNATOR);
      }
    };

  // ### whitespacep
  private static final Primitive WHITESPACEP =
    new Primitive("whitespacep", PACKAGE_SYS, true)
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return Character.isWhitespace(arg.charValue()) ? T : NIL;
      }
    };

  // ### char-code
  private static final Primitive CHAR_CODE =
    new Primitive(SymbolConstants.CHAR_CODE, "character")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          int n = arg.charValue();
          return Fixnum.makeFixnum(n);
      }
    };

  // ### char-int
  private static final Primitive CHAR_INT =
    new Primitive(SymbolConstants.CHAR_INT, "character")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          int n = arg.charValue();
          return Fixnum.makeFixnum(n);
      }
    };

  // ### code-char
  private static final Primitive CODE_CHAR =
    new Primitive(SymbolConstants.CODE_CHAR, "code")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          int n = arg.intValue();
          if (n < CHAR_MAX)
            return constants[n];
          else if (n <= Character.MAX_VALUE)
            return new LispCharacter((char)n);
              // SBCL signals a type-error here: "not of type (UNSIGNED-BYTE 8)"
        return NIL;
      }
    };

  // ### characterp
  private static final Primitive CHARACTERP =
    new Primitive(SymbolConstants.CHARACTERP, "object")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return arg instanceof LispCharacter ? T : NIL;
      }
    };

  // ### both-case-p
  private static final Primitive BOTH_CASE_P =
    new Primitive(SymbolConstants.BOTH_CASE_P, "character")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        char c = arg.charValue();
        if (Character.isLowerCase(c) || Character.isUpperCase(c))
          return T;
        return NIL;
      }
    };

  // ### lower-case-p
  private static final Primitive LOWER_CASE_P =
    new Primitive(SymbolConstants.LOWER_CASE_P, "character")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return Character.isLowerCase(arg.charValue()) ? T : NIL;
      }
    };

  // ### upper-case-p
  private static final Primitive UPPER_CASE_P =
    new Primitive(SymbolConstants.UPPER_CASE_P, "character")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        return Character.isUpperCase(arg.charValue()) ? T : NIL;
      }
    };

  // ### char-downcase
  private static final Primitive CHAR_DOWNCASE =
    new Primitive(SymbolConstants.CHAR_DOWNCASE, "character")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          final char c = arg.charValue();
          if (c < 128)
           return constants[LOWER_CASE_CHARS[c]];
        return LispCharacter.getLispCharacter(toLowerCase(c));
      }
    };

  // ### char-upcase
  private static final Primitive CHAR_UPCASE =
    new Primitive(SymbolConstants.CHAR_UPCASE, "character")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        final char c;
        c = arg.charValue();
        if (c < 128)
          return constants[UPPER_CASE_CHARS[c]];
        return LispCharacter.getLispCharacter(toUpperCase(c));
      }
    };

  // ### digit-char
  private static final Primitive DIGIT_CHAR =
    new Primitive(SymbolConstants.DIGIT_CHAR, "weight &optional radix")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          if (arg  instanceof Bignum)
              return NIL;

          int weight = arg.intValue();
        if (weight < 10)
          return constants['0'+weight];
        return NIL;
      }
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
        int radix;
        if (second  instanceof Fixnum)
            radix = second.intValue();
        else
            radix = -1;
        
        if (radix < 2 || radix > 36)
          return type_error(second,
                                 list(SymbolConstants.INTEGER, Fixnum.TWO,
                                       Fixnum.constants[36]));
        if (first  instanceof Bignum)
            return NIL;
        int weight = first.intValue();
        if (weight >= radix)
          return NIL;
        if (weight < 10)
          return constants['0' + weight];
        return constants['A' + weight - 10];
      }
    };

  // ### digit-char-p char &optional radix => weight
  private static final Primitive DIGIT_CHAR_P =
    new Primitive(SymbolConstants.DIGIT_CHAR_P, "char &optional radix")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          final int n = Character.digit(arg.charValue(), 10);
          return n < 0 ? NIL : Fixnum.makeFixnum(n);
      }
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
        char c;
            c = first.charValue();
        if (second  instanceof Fixnum)
          {
            int radix = second.intValue();
            if (radix >= 2 && radix <= 36)
              {
                int n = Character.digit(c, radix);
                return n < 0 ? NIL : Fixnum.constants[n];
              }
          }
        return type_error(second,
                               list(SymbolConstants.INTEGER, Fixnum.TWO,
                                     Fixnum.constants[36]));
      }
    };

  // ### standard-char-p
  private static final Primitive STANDARD_CHAR_P =
    new Primitive(SymbolConstants.STANDARD_CHAR_P, "character")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return checkCharacter(arg).isStandardChar() ? T : NIL;
      }
    };

  // ### graphic-char-p
  private static final Primitive GRAPHIC_CHAR_P =
    new Primitive(SymbolConstants.GRAPHIC_CHAR_P, "char")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          char c = arg.charValue();
          if (c >= ' ' && c < 127)
            return T;
          return Character.isISOControl(c) ? NIL : T;
      }
    };

  // ### alpha-char-p
  private static final Primitive ALPHA_CHAR_P =
    new Primitive(SymbolConstants.ALPHA_CHAR_P, "character")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return Character.isLetter(arg.charValue()) ? T : NIL;
      }
    };

  // ### alphanumericp
  private static final Primitive ALPHANUMERICP =
    new Primitive(SymbolConstants.ALPHANUMERICP, "character")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return Character.isLetterOrDigit(arg.charValue()) ? T : NIL;
      }
    };

  public static final int nameToChar(String s)
  {
    String lower = s.toLowerCase();
    LispCharacter lc = namedToChar.get(lower);
    if (lc!=null) return lc.value;
    if (lower.equals("null"))
      return 0;
    if (lower.equals("soh"))
        return 1;
    if (lower.equals("bell"))
        return 7;
    if (lower.equals("backspace"))
      return '\b';
    if (lower.equals("tab"))
      return '\t';
    if (lower.equals("linefeed"))
      return '\n';
    if (lower.equals("newline"))
      return '\n';
    if (lower.equals("page"))
      return '\f';
    if (lower.equals("return"))
      return '\r';
    if (lower.equals("escape"))
        return 27;
    if (lower.equals("space"))
      return ' ';
    if (lower.equals("rubout"))
      return 127;
    // Unknown.
    return -1;
  }

  // ### name-char
  private static final Primitive NAME_CHAR =
    new Primitive(SymbolConstants.NAME_CHAR, "name")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        String s = arg.STRING().getStringValue();
        int n = nameToChar(s);
        return n >= 0 ? LispCharacter.getLispCharacter((char)n) : NIL;
      }
    };

  public static final String charToName(char c)
  {
    switch (c)
      {
      case 0:
        return "Null";
      case 1:
          return "Soh";
      case 7:
          return "Bell";
      case '\b':
        return "Backspace";
      case '\t':
        return "Tab";
      case '\n':
        return "Newline";
      case '\f':
        return "Page";
      case '\r':
        return "Return";
      case 27:
        return "Escape";
      case ' ':
        return "Space";
      case 127:
        return "Rubout";
      }
     if(c<CHAR_MAX)return constants[c].mainName;
    return getLispCharacter(c).mainName;
  }

  // ### char-name
  private static final Primitive CHAR_NAME =
    new Primitive(SymbolConstants.CHAR_NAME, "character")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        String name = charToName(arg.charValue());
        return name != null ? new SimpleString(name) : NIL;
      }
    };

  public static final char toUpperCase(char c)
  {
    if (c < 128)
      return UPPER_CASE_CHARS[c];
    return Character.toUpperCase(c);
  }

  /*private*/ static final char[] UPPER_CASE_CHARS = new char[128];

  static
  {
    for (int i = UPPER_CASE_CHARS.length; i-- > 0;)
      UPPER_CASE_CHARS[i] = Character.toUpperCase((char)i);
  }

  public static final char toLowerCase(char c)
  {
    if (c < 128)
      return LOWER_CASE_CHARS[c];
    return Character.toLowerCase(c);
  }

  /*private*/ static final char[] LOWER_CASE_CHARS = new char[128];

  static
  {
    for (int i = LOWER_CASE_CHARS.length; i-- > 0;)
      LOWER_CASE_CHARS[i] = Character.toLowerCase((char)i);
  }

  public static boolean isBaseChar(char value) {
	if (value >= ' ' && value < 127)
	    return true;
	  if (value == '\n')
	    return true;
	  return false;
	}

  static int maxNamedChar = 0;
  static Map<String, LispCharacter> namedToChar = new HashMap<String, LispCharacter>();

  static void setCharNames(int i, String[] string) {
    int settingChar = i;
    int index = 0;
    int stringLen = string.length;
    while(stringLen-->0) {
      setCharName(settingChar,string[index]);
      index++;
      settingChar++;
    }
    if (maxNamedChar<settingChar) maxNamedChar = settingChar; 
  }

  static void setCharName(int settingChar, String string) {
    LispCharacter c = getLispCharacter((char)settingChar);
    String pc = toProperCase( string);
    c.mainName = pc;
    namedToChar.put(pc.toLowerCase(), c);
  }
 
  private static String toProperCase(String string) {
	  if (!string.toUpperCase().equals(string)) return string;
		char[] chars = string.toLowerCase().toCharArray();
		boolean virgin = true;
		for (int i = 0; i < chars.length; i++) {
			char c = string.charAt(i);
			if (virgin) {
				if (Character.isLetterOrDigit(c)) {
					chars[i] = LispCharacter.toUpperCase(c);
					virgin = false;
				} else {
					virgin = true;
				}
			} else {
				if (!Character.isLetterOrDigit(c)) {
					virgin = true;					
				}
			}
				//else				
					//chars[i] = LispCharacter.toLowerCase(c);		
		}
		if (chars[0]=='U') return new String(chars);
		return new String(chars);
	}

static {
   new CharNameMaker0();
  }
  /*
   * 
   *  // (defun print-names (start) ( let ((n start)) (loop (when (> n (+ start 256)) (return))(princ (format nil "~S, " (char-name (code-char n)))) (incf n))))
  static class CharNameMaker0{
    {
      //setCharNames(0,new String[]{"Null", "Soh", "Stx", "Etx", "Eot", "Enq", "Ack", "Bell", "Backspace", "Tab", "Newline", "Vt", "Page", "Return", "So", "Si", "Dle", "Dc1", "Dc2", "Dc3", "Dc4", "Nak", "Syn", "Etb", "Can", "Em", "Sub", "Escape", "Fs", "Gs", "Rs", "Us"});
    	setCharNames(0, new String[]{"Null", "Soh", "Stx", "Etx", "Eot", "Enq", "Ack", "Bell", "Backspace", "Tab", "Newline", "Vt", "Page", "Return", "So", "Si", "Dle", "Dc1", "Dc2", "Dc3", "Dc4", "Nak", "Syn", "Etb", "Can", "Em", "Sub", "Escape", "Fs", "Gs", "Rs", "Us"});
      setCharNames(128,new String[]{"C80", "C81", "Break-Permitted", "No-Break-Permitted", "C84", "Next-Line", "Start-Selected-Area", "End-Selected-Area", "Character-Tabulation-Set", "Character-Tabulation-With-Justification", "Line-Tabulation-Set", "Partial-Line-Forward", "Partial-Line-Backward", "Reverse-Linefeed", "Single-Shift-Two", "Single-Shift-Three", "Device-Control-String", "Private-Use-One", "Private-Use-Two", "Set-Transmit-State", "Cancel-Character", "Message-Waiting", "Start-Guarded-Area", "End-Guarded-Area", "Start-String", "C99", "Single-Character-Introducer", "Control-Sequence-Introducer", "String-Terminator", "Operating-System-Command", "Privacy-Message", "Application-Program-Command", "NO-BREAK_SPACE", "INVERTED_EXCLAMATION_MARK", "CENT_SIGN", "POUND_SIGN", "CURRENCY_SIGN", "YEN_SIGN", "BROKEN_BAR", "SECTION_SIGN", "DIAERESIS", "COPYRIGHT_SIGN", "FEMININE_ORDINAL_INDICATOR", "LEFT-POINTING_DOUBLE_ANGLE_QUOTATION_MARK", "NOT_SIGN", "SOFT_HYPHEN", "REGISTERED_SIGN", "MACRON", "DEGREE_SIGN", "PLUS-MINUS_SIGN", "SUPERSCRIPT_TWO", "SUPERSCRIPT_THREE", "ACUTE_ACCENT", "MICRO_SIGN", "PILCROW_SIGN", "MIDDLE_DOT", "CEDILLA", "SUPERSCRIPT_ONE", "MASCULINE_ORDINAL_INDICATOR", "RIGHT-POINTING_DOUBLE_ANGLE_QUOTATION_MARK", "VULGAR_FRACTION_ONE_QUARTER", "VULGAR_FRACTION_ONE_HALF", "VULGAR_FRACTION_THREE_QUARTERS", "INVERTED_QUESTION_MARK", "LATIN_CAPITAL_LETTER_A_WITH_GRAVE", "LATIN_CAPITAL_LETTER_A_WITH_ACUTE", "LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_A_WITH_TILDE", "LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS", "LATIN_CAPITAL_LETTER_A_WITH_RING_ABOVE", "LATIN_CAPITAL_LETTER_AE", "LATIN_CAPITAL_LETTER_C_WITH_CEDILLA", "LATIN_CAPITAL_LETTER_E_WITH_GRAVE", "LATIN_CAPITAL_LETTER_E_WITH_ACUTE", "LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS", "LATIN_CAPITAL_LETTER_I_WITH_GRAVE", "LATIN_CAPITAL_LETTER_I_WITH_ACUTE", "LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS", "LATIN_CAPITAL_LETTER_ETH", "LATIN_CAPITAL_LETTER_N_WITH_TILDE", "LATIN_CAPITAL_LETTER_O_WITH_GRAVE", "LATIN_CAPITAL_LETTER_O_WITH_ACUTE", "LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_O_WITH_TILDE", "LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS", "MULTIPLICATION_SIGN", "LATIN_CAPITAL_LETTER_O_WITH_STROKE", "LATIN_CAPITAL_LETTER_U_WITH_GRAVE", "LATIN_CAPITAL_LETTER_U_WITH_ACUTE", "LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS", "LATIN_CAPITAL_LETTER_Y_WITH_ACUTE", "LATIN_CAPITAL_LETTER_THORN", "LATIN_SMALL_LETTER_SHARP_S", "LATIN_SMALL_LETTER_A_WITH_GRAVE", "LATIN_SMALL_LETTER_A_WITH_ACUTE", "LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_A_WITH_TILDE", "LATIN_SMALL_LETTER_A_WITH_DIAERESIS", "LATIN_SMALL_LETTER_A_WITH_RING_ABOVE", "LATIN_SMALL_LETTER_AE", "LATIN_SMALL_LETTER_C_WITH_CEDILLA", "LATIN_SMALL_LETTER_E_WITH_GRAVE", "LATIN_SMALL_LETTER_E_WITH_ACUTE", "LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_E_WITH_DIAERESIS", "LATIN_SMALL_LETTER_I_WITH_GRAVE", "LATIN_SMALL_LETTER_I_WITH_ACUTE", "LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_I_WITH_DIAERESIS", "LATIN_SMALL_LETTER_ETH", "LATIN_SMALL_LETTER_N_WITH_TILDE", "LATIN_SMALL_LETTER_O_WITH_GRAVE", "LATIN_SMALL_LETTER_O_WITH_ACUTE", "LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_O_WITH_TILDE", "LATIN_SMALL_LETTER_O_WITH_DIAERESIS", "DIVISION_SIGN", "LATIN_SMALL_LETTER_O_WITH_STROKE", "LATIN_SMALL_LETTER_U_WITH_GRAVE", "LATIN_SMALL_LETTER_U_WITH_ACUTE", "LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_U_WITH_DIAERESIS", "LATIN_SMALL_LETTER_Y_WITH_ACUTE", "LATIN_SMALL_LETTER_THORN", "LATIN_SMALL_LETTER_Y_WITH_DIAERESIS"});
    }
  }
*/
  static class CharNameMaker0{
    {
      setCharNames(0,new String[]{"Null", "Soh", "Stx", "Etx", "Eot", "Enq", "Ack", "Bell", "Backspace", "Tab", "Newline", "Vt", "Page", "Return", "So", "Si", "Dle", "Dc1", "Dc2", "Dc3", "Dc4", "Nak", "Syn", "Etb", "Can", "Em", "Sub", "Escape", "Fs", "Gs", "Rs", "Us"});
      //this will fail tests
  	// setCharNames(33, new String[]{"EXCLAMATION_MARK", "QUOTATION_MARK", "NUMBER_SIGN", "DOLLAR_SIGN", "PERCENT_SIGN", "AMPERSAND", "APOSTROPHE", "LEFT_PARENTHESIS", "RIGHT_PARENTHESIS", "ASTERISK", "PLUS_SIGN", "COMMA", "HYPHEN-MINUS", "FULL_STOP", "SOLIDUS", "DIGIT_ZERO", "DIGIT_ONE", "DIGIT_TWO", "DIGIT_THREE", "DIGIT_FOUR", "DIGIT_FIVE", "DIGIT_SIX", "DIGIT_SEVEN", "DIGIT_EIGHT", "DIGIT_NINE", "COLON", "SEMICOLON", "LESS-THAN_SIGN", "EQUALS_SIGN", "GREATER-THAN_SIGN", "QUESTION_MARK", "COMMERCIAL_AT", "LATIN_CAPITAL_LETTER_A", "LATIN_CAPITAL_LETTER_B", "LATIN_CAPITAL_LETTER_C", "LATIN_CAPITAL_LETTER_D", "LATIN_CAPITAL_LETTER_E", "LATIN_CAPITAL_LETTER_F", "LATIN_CAPITAL_LETTER_G", "LATIN_CAPITAL_LETTER_H", "LATIN_CAPITAL_LETTER_I", "LATIN_CAPITAL_LETTER_J", "LATIN_CAPITAL_LETTER_K", "LATIN_CAPITAL_LETTER_L", "LATIN_CAPITAL_LETTER_M", "LATIN_CAPITAL_LETTER_N", "LATIN_CAPITAL_LETTER_O", "LATIN_CAPITAL_LETTER_P", "LATIN_CAPITAL_LETTER_Q", "LATIN_CAPITAL_LETTER_R", "LATIN_CAPITAL_LETTER_S", "LATIN_CAPITAL_LETTER_T", "LATIN_CAPITAL_LETTER_U", "LATIN_CAPITAL_LETTER_V", "LATIN_CAPITAL_LETTER_W", "LATIN_CAPITAL_LETTER_X", "LATIN_CAPITAL_LETTER_Y", "LATIN_CAPITAL_LETTER_Z", "LEFT_SQUARE_BRACKET", "REVERSE_SOLIDUS", "RIGHT_SQUARE_BRACKET", "CIRCUMFLEX_ACCENT", "LOW_LINE", "GRAVE_ACCENT", "LATIN_SMALL_LETTER_A", "LATIN_SMALL_LETTER_B", "LATIN_SMALL_LETTER_C", "LATIN_SMALL_LETTER_D", "LATIN_SMALL_LETTER_E", "LATIN_SMALL_LETTER_F", "LATIN_SMALL_LETTER_G", "LATIN_SMALL_LETTER_H", "LATIN_SMALL_LETTER_I", "LATIN_SMALL_LETTER_J", "LATIN_SMALL_LETTER_K", "LATIN_SMALL_LETTER_L", "LATIN_SMALL_LETTER_M", "LATIN_SMALL_LETTER_N", "LATIN_SMALL_LETTER_O", "LATIN_SMALL_LETTER_P", "LATIN_SMALL_LETTER_Q", "LATIN_SMALL_LETTER_R", "LATIN_SMALL_LETTER_S", "LATIN_SMALL_LETTER_T", "LATIN_SMALL_LETTER_U", "LATIN_SMALL_LETTER_V", "LATIN_SMALL_LETTER_W", "LATIN_SMALL_LETTER_X", "LATIN_SMALL_LETTER_Y", "LATIN_SMALL_LETTER_Z", "LEFT_CURLY_BRACKET", "VERTICAL_LINE", "RIGHT_CURLY_BRACKET", "TILDE", "Rubout"});
      setCharNames(128,new String[]{"U0080", "U0081", "U0082", "U0083", "U0084", "U0085", "U0086", "U0087", "U0088", "U0089", "U008a", "U008b", "U008c", "U008d", "U008e", "U008f", "U0090", "U0091", "U0092", "U0093", "U0094", "U0095", "U0096", "U0097", "U0098", "U0099", "U009a", "U009b", "U009c", "U009d", "U009e", "U009f"});
      // secondary overlay      
      setCharNames(128,new String[]{"C80", "C81", "Break-Permitted", "No-Break-Permitted", "C84", "Next-Line", "Start-Selected-Area", "End-Selected-Area", "Character-Tabulation-Set", "Character-Tabulation-With-Justification", "Line-Tabulation-Set", "Partial-Line-Forward", "Partial-Line-Backward", "Reverse-Linefeed", "Single-Shift-Two", "Single-Shift-Three", "Device-Control-String", "Private-Use-One", "Private-Use-Two", "Set-Transmit-State", "Cancel-Character", "Message-Waiting", "Start-Guarded-Area", "End-Guarded-Area", "Start-String", "C99", "Single-Character-Introducer", "Control-Sequence-Introducer", "String-Terminator", "Operating-System-Command", "Privacy-Message", "Application-Program-Command"
    	
    		  // tests will fail with these
    		 //,"NO-BREAK_SPACE", "INVERTED_EXCLAMATION_MARK", "CENT_SIGN", "POUND_SIGN", "CURRENCY_SIGN", "YEN_SIGN", "BROKEN_BAR", "SECTION_SIGN", "DIAERESIS", "COPYRIGHT_SIGN", "FEMININE_ORDINAL_INDICATOR", "LEFT-POINTING_DOUBLE_ANGLE_QUOTATION_MARK", "NOT_SIGN", "SOFT_HYPHEN", "REGISTERED_SIGN", "MACRON", "DEGREE_SIGN", "PLUS-MINUS_SIGN", "SUPERSCRIPT_TWO", "SUPERSCRIPT_THREE", "ACUTE_ACCENT", "MICRO_SIGN", "PILCROW_SIGN", "MIDDLE_DOT", "CEDILLA", "SUPERSCRIPT_ONE", "MASCULINE_ORDINAL_INDICATOR", "RIGHT-POINTING_DOUBLE_ANGLE_QUOTATION_MARK", "VULGAR_FRACTION_ONE_QUARTER", "VULGAR_FRACTION_ONE_HALF", "VULGAR_FRACTION_THREE_QUARTERS", "INVERTED_QUESTION_MARK", "LATIN_CAPITAL_LETTER_A_WITH_GRAVE", "LATIN_CAPITAL_LETTER_A_WITH_ACUTE", "LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_A_WITH_TILDE", "LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS", "LATIN_CAPITAL_LETTER_A_WITH_RING_ABOVE", "LATIN_CAPITAL_LETTER_AE", "LATIN_CAPITAL_LETTER_C_WITH_CEDILLA", "LATIN_CAPITAL_LETTER_E_WITH_GRAVE", "LATIN_CAPITAL_LETTER_E_WITH_ACUTE", "LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS", "LATIN_CAPITAL_LETTER_I_WITH_GRAVE", "LATIN_CAPITAL_LETTER_I_WITH_ACUTE", "LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS", "LATIN_CAPITAL_LETTER_ETH", "LATIN_CAPITAL_LETTER_N_WITH_TILDE", "LATIN_CAPITAL_LETTER_O_WITH_GRAVE", "LATIN_CAPITAL_LETTER_O_WITH_ACUTE", "LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_O_WITH_TILDE", "LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS", "MULTIPLICATION_SIGN", "LATIN_CAPITAL_LETTER_O_WITH_STROKE", "LATIN_CAPITAL_LETTER_U_WITH_GRAVE", "LATIN_CAPITAL_LETTER_U_WITH_ACUTE", "LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS", "LATIN_CAPITAL_LETTER_Y_WITH_ACUTE", "LATIN_CAPITAL_LETTER_THORN", "LATIN_SMALL_LETTER_SHARP_S", "LATIN_SMALL_LETTER_A_WITH_GRAVE", "LATIN_SMALL_LETTER_A_WITH_ACUTE", "LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_A_WITH_TILDE", "LATIN_SMALL_LETTER_A_WITH_DIAERESIS", "LATIN_SMALL_LETTER_A_WITH_RING_ABOVE", "LATIN_SMALL_LETTER_AE", "LATIN_SMALL_LETTER_C_WITH_CEDILLA", "LATIN_SMALL_LETTER_E_WITH_GRAVE", "LATIN_SMALL_LETTER_E_WITH_ACUTE", "LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_E_WITH_DIAERESIS", "LATIN_SMALL_LETTER_I_WITH_GRAVE", "LATIN_SMALL_LETTER_I_WITH_ACUTE", "LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_I_WITH_DIAERESIS", "LATIN_SMALL_LETTER_ETH", "LATIN_SMALL_LETTER_N_WITH_TILDE", "LATIN_SMALL_LETTER_O_WITH_GRAVE", "LATIN_SMALL_LETTER_O_WITH_ACUTE", "LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_O_WITH_TILDE", "LATIN_SMALL_LETTER_O_WITH_DIAERESIS", "DIVISION_SIGN", "LATIN_SMALL_LETTER_O_WITH_STROKE", "LATIN_SMALL_LETTER_U_WITH_GRAVE", "LATIN_SMALL_LETTER_U_WITH_ACUTE", "LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_U_WITH_DIAERESIS", "LATIN_SMALL_LETTER_Y_WITH_ACUTE", "LATIN_SMALL_LETTER_THORN", "LATIN_SMALL_LETTER_Y_WITH_DIAERESIS"
    		  });
    }
  }
  static class CharNameMaker1{
	    {
	    	setCharNames(256, new String[]{"LATIN_CAPITAL_LETTER_A_WITH_MACRON", "LATIN_SMALL_LETTER_A_WITH_MACRON", "LATIN_CAPITAL_LETTER_A_WITH_BREVE", "LATIN_SMALL_LETTER_A_WITH_BREVE", "LATIN_CAPITAL_LETTER_A_WITH_OGONEK", "LATIN_SMALL_LETTER_A_WITH_OGONEK", "LATIN_CAPITAL_LETTER_C_WITH_ACUTE", "LATIN_SMALL_LETTER_C_WITH_ACUTE", "LATIN_CAPITAL_LETTER_C_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_C_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_C_WITH_DOT_ABOVE", "LATIN_SMALL_LETTER_C_WITH_DOT_ABOVE", "LATIN_CAPITAL_LETTER_C_WITH_CARON", "LATIN_SMALL_LETTER_C_WITH_CARON", "LATIN_CAPITAL_LETTER_D_WITH_CARON", "LATIN_SMALL_LETTER_D_WITH_CARON", "LATIN_CAPITAL_LETTER_D_WITH_STROKE", "LATIN_SMALL_LETTER_D_WITH_STROKE", "LATIN_CAPITAL_LETTER_E_WITH_MACRON", "LATIN_SMALL_LETTER_E_WITH_MACRON", "LATIN_CAPITAL_LETTER_E_WITH_BREVE", "LATIN_SMALL_LETTER_E_WITH_BREVE", "LATIN_CAPITAL_LETTER_E_WITH_DOT_ABOVE", "LATIN_SMALL_LETTER_E_WITH_DOT_ABOVE", "LATIN_CAPITAL_LETTER_E_WITH_OGONEK", "LATIN_SMALL_LETTER_E_WITH_OGONEK", "LATIN_CAPITAL_LETTER_E_WITH_CARON", "LATIN_SMALL_LETTER_E_WITH_CARON", "LATIN_CAPITAL_LETTER_G_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_G_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_G_WITH_BREVE", "LATIN_SMALL_LETTER_G_WITH_BREVE", "LATIN_CAPITAL_LETTER_G_WITH_DOT_ABOVE", "LATIN_SMALL_LETTER_G_WITH_DOT_ABOVE", "LATIN_CAPITAL_LETTER_G_WITH_CEDILLA", "LATIN_SMALL_LETTER_G_WITH_CEDILLA", "LATIN_CAPITAL_LETTER_H_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_H_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_H_WITH_STROKE", "LATIN_SMALL_LETTER_H_WITH_STROKE", "LATIN_CAPITAL_LETTER_I_WITH_TILDE", "LATIN_SMALL_LETTER_I_WITH_TILDE", "LATIN_CAPITAL_LETTER_I_WITH_MACRON", "LATIN_SMALL_LETTER_I_WITH_MACRON", "LATIN_CAPITAL_LETTER_I_WITH_BREVE", "LATIN_SMALL_LETTER_I_WITH_BREVE", "LATIN_CAPITAL_LETTER_I_WITH_OGONEK", "LATIN_SMALL_LETTER_I_WITH_OGONEK", "LATIN_CAPITAL_LETTER_I_WITH_DOT_ABOVE", "LATIN_SMALL_LETTER_DOTLESS_I", "LATIN_CAPITAL_LIGATURE_IJ", "LATIN_SMALL_LIGATURE_IJ", "LATIN_CAPITAL_LETTER_J_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_J_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_K_WITH_CEDILLA", "LATIN_SMALL_LETTER_K_WITH_CEDILLA", "LATIN_SMALL_LETTER_KRA", "LATIN_CAPITAL_LETTER_L_WITH_ACUTE", "LATIN_SMALL_LETTER_L_WITH_ACUTE", "LATIN_CAPITAL_LETTER_L_WITH_CEDILLA", "LATIN_SMALL_LETTER_L_WITH_CEDILLA", "LATIN_CAPITAL_LETTER_L_WITH_CARON", "LATIN_SMALL_LETTER_L_WITH_CARON", "LATIN_CAPITAL_LETTER_L_WITH_MIDDLE_DOT", "LATIN_SMALL_LETTER_L_WITH_MIDDLE_DOT", "LATIN_CAPITAL_LETTER_L_WITH_STROKE", "LATIN_SMALL_LETTER_L_WITH_STROKE", "LATIN_CAPITAL_LETTER_N_WITH_ACUTE", "LATIN_SMALL_LETTER_N_WITH_ACUTE", "LATIN_CAPITAL_LETTER_N_WITH_CEDILLA", "LATIN_SMALL_LETTER_N_WITH_CEDILLA", "LATIN_CAPITAL_LETTER_N_WITH_CARON", "LATIN_SMALL_LETTER_N_WITH_CARON", "LATIN_SMALL_LETTER_N_PRECEDED_BY_APOSTROPHE", "LATIN_CAPITAL_LETTER_ENG", "LATIN_SMALL_LETTER_ENG", "LATIN_CAPITAL_LETTER_O_WITH_MACRON", "LATIN_SMALL_LETTER_O_WITH_MACRON", "LATIN_CAPITAL_LETTER_O_WITH_BREVE", "LATIN_SMALL_LETTER_O_WITH_BREVE", "LATIN_CAPITAL_LETTER_O_WITH_DOUBLE_ACUTE", "LATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE", "LATIN_CAPITAL_LIGATURE_OE", "LATIN_SMALL_LIGATURE_OE", "LATIN_CAPITAL_LETTER_R_WITH_ACUTE", "LATIN_SMALL_LETTER_R_WITH_ACUTE", "LATIN_CAPITAL_LETTER_R_WITH_CEDILLA", "LATIN_SMALL_LETTER_R_WITH_CEDILLA", "LATIN_CAPITAL_LETTER_R_WITH_CARON", "LATIN_SMALL_LETTER_R_WITH_CARON", "LATIN_CAPITAL_LETTER_S_WITH_ACUTE", "LATIN_SMALL_LETTER_S_WITH_ACUTE", "LATIN_CAPITAL_LETTER_S_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_S_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_S_WITH_CEDILLA", "LATIN_SMALL_LETTER_S_WITH_CEDILLA", "LATIN_CAPITAL_LETTER_S_WITH_CARON", "LATIN_SMALL_LETTER_S_WITH_CARON", "LATIN_CAPITAL_LETTER_T_WITH_CEDILLA", "LATIN_SMALL_LETTER_T_WITH_CEDILLA", "LATIN_CAPITAL_LETTER_T_WITH_CARON", "LATIN_SMALL_LETTER_T_WITH_CARON", "LATIN_CAPITAL_LETTER_T_WITH_STROKE", "LATIN_SMALL_LETTER_T_WITH_STROKE", "LATIN_CAPITAL_LETTER_U_WITH_TILDE", "LATIN_SMALL_LETTER_U_WITH_TILDE", "LATIN_CAPITAL_LETTER_U_WITH_MACRON", "LATIN_SMALL_LETTER_U_WITH_MACRON", "LATIN_CAPITAL_LETTER_U_WITH_BREVE", "LATIN_SMALL_LETTER_U_WITH_BREVE", "LATIN_CAPITAL_LETTER_U_WITH_RING_ABOVE", "LATIN_SMALL_LETTER_U_WITH_RING_ABOVE", "LATIN_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE", "LATIN_SMALL_LETTER_U_WITH_DOUBLE_ACUTE", "LATIN_CAPITAL_LETTER_U_WITH_OGONEK", "LATIN_SMALL_LETTER_U_WITH_OGONEK", "LATIN_CAPITAL_LETTER_W_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_W_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_Y_WITH_CIRCUMFLEX", "LATIN_SMALL_LETTER_Y_WITH_CIRCUMFLEX", "LATIN_CAPITAL_LETTER_Y_WITH_DIAERESIS", "LATIN_CAPITAL_LETTER_Z_WITH_ACUTE", "LATIN_SMALL_LETTER_Z_WITH_ACUTE", "LATIN_CAPITAL_LETTER_Z_WITH_DOT_ABOVE", "LATIN_SMALL_LETTER_Z_WITH_DOT_ABOVE", "LATIN_CAPITAL_LETTER_Z_WITH_CARON", "LATIN_SMALL_LETTER_Z_WITH_CARON", "LATIN_SMALL_LETTER_LONG_S", "LATIN_SMALL_LETTER_B_WITH_STROKE"});
	      setCharNames(384,new String[]{"LATIN_SMALL_LETTER_B_WITH_STROKE", "LATIN_CAPITAL_LETTER_B_WITH_HOOK", "LATIN_CAPITAL_LETTER_B_WITH_TOPBAR", "LATIN_SMALL_LETTER_B_WITH_TOPBAR", "LATIN_CAPITAL_LETTER_TONE_SIX", "LATIN_SMALL_LETTER_TONE_SIX", "LATIN_CAPITAL_LETTER_OPEN_O", "LATIN_CAPITAL_LETTER_C_WITH_HOOK", "LATIN_SMALL_LETTER_C_WITH_HOOK", "LATIN_CAPITAL_LETTER_AFRICAN_D", "LATIN_CAPITAL_LETTER_D_WITH_HOOK", "LATIN_CAPITAL_LETTER_D_WITH_TOPBAR", "LATIN_SMALL_LETTER_D_WITH_TOPBAR", "LATIN_SMALL_LETTER_TURNED_DELTA", "LATIN_CAPITAL_LETTER_REVERSED_E", "LATIN_CAPITAL_LETTER_SCHWA", "LATIN_CAPITAL_LETTER_OPEN_E", "LATIN_CAPITAL_LETTER_F_WITH_HOOK", "LATIN_SMALL_LETTER_F_WITH_HOOK", "LATIN_CAPITAL_LETTER_G_WITH_HOOK", "LATIN_CAPITAL_LETTER_GAMMA", "LATIN_SMALL_LETTER_HV", "LATIN_CAPITAL_LETTER_IOTA", "LATIN_CAPITAL_LETTER_I_WITH_STROKE", "LATIN_CAPITAL_LETTER_K_WITH_HOOK", "LATIN_SMALL_LETTER_K_WITH_HOOK", "LATIN_SMALL_LETTER_L_WITH_BAR", "LATIN_SMALL_LETTER_LAMBDA_WITH_STROKE", "LATIN_CAPITAL_LETTER_TURNED_M", "LATIN_CAPITAL_LETTER_N_WITH_LEFT_HOOK", "LATIN_SMALL_LETTER_N_WITH_LONG_RIGHT_LEG", "LATIN_CAPITAL_LETTER_O_WITH_MIDDLE_TILDE", "LATIN_CAPITAL_LETTER_O_WITH_HORN", "LATIN_SMALL_LETTER_O_WITH_HORN", "LATIN_CAPITAL_LETTER_OI", "LATIN_SMALL_LETTER_OI", "LATIN_CAPITAL_LETTER_P_WITH_HOOK", "LATIN_SMALL_LETTER_P_WITH_HOOK", "LATIN_LETTER_YR", "LATIN_CAPITAL_LETTER_TONE_TWO", "LATIN_SMALL_LETTER_TONE_TWO", "LATIN_CAPITAL_LETTER_ESH", "LATIN_LETTER_REVERSED_ESH_LOOP", "LATIN_SMALL_LETTER_T_WITH_PALATAL_HOOK", "LATIN_CAPITAL_LETTER_T_WITH_HOOK", "LATIN_SMALL_LETTER_T_WITH_HOOK", "LATIN_CAPITAL_LETTER_T_WITH_RETROFLEX_HOOK", "LATIN_CAPITAL_LETTER_U_WITH_HORN", "LATIN_SMALL_LETTER_U_WITH_HORN", "LATIN_CAPITAL_LETTER_UPSILON", "LATIN_CAPITAL_LETTER_V_WITH_HOOK", "LATIN_CAPITAL_LETTER_Y_WITH_HOOK", "LATIN_SMALL_LETTER_Y_WITH_HOOK", "LATIN_CAPITAL_LETTER_Z_WITH_STROKE", "LATIN_SMALL_LETTER_Z_WITH_STROKE", "LATIN_CAPITAL_LETTER_EZH", "LATIN_CAPITAL_LETTER_EZH_REVERSED", "LATIN_SMALL_LETTER_EZH_REVERSED", "LATIN_SMALL_LETTER_EZH_WITH_TAIL", "LATIN_LETTER_TWO_WITH_STROKE", "LATIN_CAPITAL_LETTER_TONE_FIVE", "LATIN_SMALL_LETTER_TONE_FIVE", "LATIN_LETTER_INVERTED_GLOTTAL_STOP_WITH_STROKE", "LATIN_LETTER_WYNN", "LATIN_LETTER_DENTAL_CLICK", "LATIN_LETTER_LATERAL_CLICK", "LATIN_LETTER_ALVEOLAR_CLICK", "LATIN_LETTER_RETROFLEX_CLICK", "LATIN_CAPITAL_LETTER_DZ_WITH_CARON", "LATIN_CAPITAL_LETTER_D_WITH_SMALL_LETTER_Z_WITH_CARON", "LATIN_SMALL_LETTER_DZ_WITH_CARON", "LATIN_CAPITAL_LETTER_LJ", "LATIN_CAPITAL_LETTER_L_WITH_SMALL_LETTER_J", "LATIN_SMALL_LETTER_LJ", "LATIN_CAPITAL_LETTER_NJ", "LATIN_CAPITAL_LETTER_N_WITH_SMALL_LETTER_J", "LATIN_SMALL_LETTER_NJ", "LATIN_CAPITAL_LETTER_A_WITH_CARON", "LATIN_SMALL_LETTER_A_WITH_CARON", "LATIN_CAPITAL_LETTER_I_WITH_CARON", "LATIN_SMALL_LETTER_I_WITH_CARON", "LATIN_CAPITAL_LETTER_O_WITH_CARON", "LATIN_SMALL_LETTER_O_WITH_CARON", "LATIN_CAPITAL_LETTER_U_WITH_CARON", "LATIN_SMALL_LETTER_U_WITH_CARON", "LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS_AND_MACRON", "LATIN_SMALL_LETTER_U_WITH_DIAERESIS_AND_MACRON", "LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS_AND_ACUTE", "LATIN_SMALL_LETTER_U_WITH_DIAERESIS_AND_ACUTE", "LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS_AND_CARON", "LATIN_SMALL_LETTER_U_WITH_DIAERESIS_AND_CARON", "LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS_AND_GRAVE", "LATIN_SMALL_LETTER_U_WITH_DIAERESIS_AND_GRAVE", "LATIN_SMALL_LETTER_TURNED_E", "LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS_AND_MACRON", "LATIN_SMALL_LETTER_A_WITH_DIAERESIS_AND_MACRON", "LATIN_CAPITAL_LETTER_A_WITH_DOT_ABOVE_AND_MACRON", "LATIN_SMALL_LETTER_A_WITH_DOT_ABOVE_AND_MACRON", "LATIN_CAPITAL_LETTER_AE_WITH_MACRON", "LATIN_SMALL_LETTER_AE_WITH_MACRON", "LATIN_CAPITAL_LETTER_G_WITH_STROKE", "LATIN_SMALL_LETTER_G_WITH_STROKE", "LATIN_CAPITAL_LETTER_G_WITH_CARON", "LATIN_SMALL_LETTER_G_WITH_CARON", "LATIN_CAPITAL_LETTER_K_WITH_CARON", "LATIN_SMALL_LETTER_K_WITH_CARON", "LATIN_CAPITAL_LETTER_O_WITH_OGONEK", "LATIN_SMALL_LETTER_O_WITH_OGONEK", "LATIN_CAPITAL_LETTER_O_WITH_OGONEK_AND_MACRON", "LATIN_SMALL_LETTER_O_WITH_OGONEK_AND_MACRON", "LATIN_CAPITAL_LETTER_EZH_WITH_CARON", "LATIN_SMALL_LETTER_EZH_WITH_CARON", "LATIN_SMALL_LETTER_J_WITH_CARON", "LATIN_CAPITAL_LETTER_DZ", "LATIN_CAPITAL_LETTER_D_WITH_SMALL_LETTER_Z", "LATIN_SMALL_LETTER_DZ", "LATIN_CAPITAL_LETTER_G_WITH_ACUTE", "LATIN_SMALL_LETTER_G_WITH_ACUTE", "LATIN_CAPITAL_LETTER_HWAIR", "LATIN_CAPITAL_LETTER_WYNN", "LATIN_CAPITAL_LETTER_N_WITH_GRAVE", "LATIN_SMALL_LETTER_N_WITH_GRAVE", "LATIN_CAPITAL_LETTER_A_WITH_RING_ABOVE_AND_ACUTE", "LATIN_SMALL_LETTER_A_WITH_RING_ABOVE_AND_ACUTE", "LATIN_CAPITAL_LETTER_AE_WITH_ACUTE", "LATIN_SMALL_LETTER_AE_WITH_ACUTE", "LATIN_CAPITAL_LETTER_O_WITH_STROKE_AND_ACUTE", "LATIN_SMALL_LETTER_O_WITH_STROKE_AND_ACUTE", "LATIN_CAPITAL_LETTER_A_WITH_DOUBLE_GRAVE"});
	    }
	  }
static class CharNameMaker2{
	    {
	    	setCharNames(512, new String[]{
	    			"LATIN_CAPITAL_LETTER_A_WITH_DOUBLE_GRAVE", "LATIN_SMALL_LETTER_A_WITH_DOUBLE_GRAVE", "LATIN_CAPITAL_LETTER_A_WITH_INVERTED_BREVE", "LATIN_SMALL_LETTER_A_WITH_INVERTED_BREVE", "LATIN_CAPITAL_LETTER_E_WITH_DOUBLE_GRAVE", "LATIN_SMALL_LETTER_E_WITH_DOUBLE_GRAVE", "LATIN_CAPITAL_LETTER_E_WITH_INVERTED_BREVE", "LATIN_SMALL_LETTER_E_WITH_INVERTED_BREVE", "LATIN_CAPITAL_LETTER_I_WITH_DOUBLE_GRAVE", "LATIN_SMALL_LETTER_I_WITH_DOUBLE_GRAVE", "LATIN_CAPITAL_LETTER_I_WITH_INVERTED_BREVE", "LATIN_SMALL_LETTER_I_WITH_INVERTED_BREVE", "LATIN_CAPITAL_LETTER_O_WITH_DOUBLE_GRAVE", "LATIN_SMALL_LETTER_O_WITH_DOUBLE_GRAVE", "LATIN_CAPITAL_LETTER_O_WITH_INVERTED_BREVE", "LATIN_SMALL_LETTER_O_WITH_INVERTED_BREVE", "LATIN_CAPITAL_LETTER_R_WITH_DOUBLE_GRAVE", "LATIN_SMALL_LETTER_R_WITH_DOUBLE_GRAVE", "LATIN_CAPITAL_LETTER_R_WITH_INVERTED_BREVE", "LATIN_SMALL_LETTER_R_WITH_INVERTED_BREVE", "LATIN_CAPITAL_LETTER_U_WITH_DOUBLE_GRAVE", "LATIN_SMALL_LETTER_U_WITH_DOUBLE_GRAVE", "LATIN_CAPITAL_LETTER_U_WITH_INVERTED_BREVE", "LATIN_SMALL_LETTER_U_WITH_INVERTED_BREVE", "LATIN_CAPITAL_LETTER_S_WITH_COMMA_BELOW", "LATIN_SMALL_LETTER_S_WITH_COMMA_BELOW", "LATIN_CAPITAL_LETTER_T_WITH_COMMA_BELOW", "LATIN_SMALL_LETTER_T_WITH_COMMA_BELOW", "LATIN_CAPITAL_LETTER_YOGH", "LATIN_SMALL_LETTER_YOGH", "LATIN_CAPITAL_LETTER_H_WITH_CARON", "LATIN_SMALL_LETTER_H_WITH_CARON", "LATIN_CAPITAL_LETTER_N_WITH_LONG_RIGHT_LEG", "LATIN_SMALL_LETTER_D_WITH_CURL", "LATIN_CAPITAL_LETTER_OU", "LATIN_SMALL_LETTER_OU", "LATIN_CAPITAL_LETTER_Z_WITH_HOOK", "LATIN_SMALL_LETTER_Z_WITH_HOOK", "LATIN_CAPITAL_LETTER_A_WITH_DOT_ABOVE", "LATIN_SMALL_LETTER_A_WITH_DOT_ABOVE", "LATIN_CAPITAL_LETTER_E_WITH_CEDILLA", "LATIN_SMALL_LETTER_E_WITH_CEDILLA", "LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS_AND_MACRON", "LATIN_SMALL_LETTER_O_WITH_DIAERESIS_AND_MACRON", "LATIN_CAPITAL_LETTER_O_WITH_TILDE_AND_MACRON", "LATIN_SMALL_LETTER_O_WITH_TILDE_AND_MACRON", "LATIN_CAPITAL_LETTER_O_WITH_DOT_ABOVE", "LATIN_SMALL_LETTER_O_WITH_DOT_ABOVE", "LATIN_CAPITAL_LETTER_O_WITH_DOT_ABOVE_AND_MACRON", "LATIN_SMALL_LETTER_O_WITH_DOT_ABOVE_AND_MACRON", "LATIN_CAPITAL_LETTER_Y_WITH_MACRON", "LATIN_SMALL_LETTER_Y_WITH_MACRON", "LATIN_SMALL_LETTER_L_WITH_CURL", "LATIN_SMALL_LETTER_N_WITH_CURL", "LATIN_SMALL_LETTER_T_WITH_CURL", "U0237", "U0238", "U0239", "U023A", "U023B", "U023C", "U023D", "U023E", "U023F", "U0240", "U0241", "U0242", "U0243", "U0244", "U0245", "U0246", "U0247", "U0248", "U0249", "U024A", "U024B", "U024C", "U024D", "U024E", "U024F", "LATIN_SMALL_LETTER_TURNED_A", "LATIN_SMALL_LETTER_ALPHA", "LATIN_SMALL_LETTER_TURNED_ALPHA", "LATIN_SMALL_LETTER_B_WITH_HOOK", "LATIN_SMALL_LETTER_OPEN_O", "LATIN_SMALL_LETTER_C_WITH_CURL", "LATIN_SMALL_LETTER_D_WITH_TAIL", "LATIN_SMALL_LETTER_D_WITH_HOOK", "LATIN_SMALL_LETTER_REVERSED_E", "LATIN_SMALL_LETTER_SCHWA", "LATIN_SMALL_LETTER_SCHWA_WITH_HOOK", "LATIN_SMALL_LETTER_OPEN_E", "LATIN_SMALL_LETTER_REVERSED_OPEN_E", "LATIN_SMALL_LETTER_REVERSED_OPEN_E_WITH_HOOK", "LATIN_SMALL_LETTER_CLOSED_REVERSED_OPEN_E", "LATIN_SMALL_LETTER_DOTLESS_J_WITH_STROKE", "LATIN_SMALL_LETTER_G_WITH_HOOK", "LATIN_SMALL_LETTER_SCRIPT_G", "LATIN_LETTER_SMALL_CAPITAL_G", "LATIN_SMALL_LETTER_GAMMA", "LATIN_SMALL_LETTER_RAMS_HORN", "LATIN_SMALL_LETTER_TURNED_H", "LATIN_SMALL_LETTER_H_WITH_HOOK", "LATIN_SMALL_LETTER_HENG_WITH_HOOK", "LATIN_SMALL_LETTER_I_WITH_STROKE", "LATIN_SMALL_LETTER_IOTA", "LATIN_LETTER_SMALL_CAPITAL_I", "LATIN_SMALL_LETTER_L_WITH_MIDDLE_TILDE", "LATIN_SMALL_LETTER_L_WITH_BELT", "LATIN_SMALL_LETTER_L_WITH_RETROFLEX_HOOK", "LATIN_SMALL_LETTER_LEZH", "LATIN_SMALL_LETTER_TURNED_M", "LATIN_SMALL_LETTER_TURNED_M_WITH_LONG_LEG", "LATIN_SMALL_LETTER_M_WITH_HOOK", "LATIN_SMALL_LETTER_N_WITH_LEFT_HOOK", "LATIN_SMALL_LETTER_N_WITH_RETROFLEX_HOOK", "LATIN_LETTER_SMALL_CAPITAL_N", "LATIN_SMALL_LETTER_BARRED_O", "LATIN_LETTER_SMALL_CAPITAL_OE", "LATIN_SMALL_LETTER_CLOSED_OMEGA", "LATIN_SMALL_LETTER_PHI", "LATIN_SMALL_LETTER_TURNED_R", "LATIN_SMALL_LETTER_TURNED_R_WITH_LONG_LEG", "LATIN_SMALL_LETTER_TURNED_R_WITH_HOOK", "LATIN_SMALL_LETTER_R_WITH_LONG_LEG", "LATIN_SMALL_LETTER_R_WITH_TAIL", "LATIN_SMALL_LETTER_R_WITH_FISHHOOK", "LATIN_SMALL_LETTER_REVERSED_R_WITH_FISHHOOK", "LATIN_LETTER_SMALL_CAPITAL_R", "LATIN_LETTER_SMALL_CAPITAL_INVERTED_R", "LATIN_SMALL_LETTER_S_WITH_HOOK", "LATIN_SMALL_LETTER_ESH", "LATIN_SMALL_LETTER_DOTLESS_J_WITH_STROKE_AND_HOOK", "LATIN_SMALL_LETTER_SQUAT_REVERSED_ESH", "LATIN_SMALL_LETTER_ESH_WITH_CURL", "LATIN_SMALL_LETTER_TURNED_T", "LATIN_SMALL_LETTER_T_WITH_RETROFLEX_HOOK", "LATIN_SMALL_LETTER_U_BAR", "LATIN_SMALL_LETTER_UPSILON", "LATIN_SMALL_LETTER_V_WITH_HOOK", "LATIN_SMALL_LETTER_TURNED_V", "LATIN_SMALL_LETTER_TURNED_W", "LATIN_SMALL_LETTER_TURNED_Y", "LATIN_LETTER_SMALL_CAPITAL_Y", "LATIN_SMALL_LETTER_Z_WITH_RETROFLEX_HOOK", "LATIN_SMALL_LETTER_Z_WITH_CURL", 
	    			"LATIN_SMALL_LETTER_EZH", "LATIN_SMALL_LETTER_EZH_WITH_CURL", "LATIN_LETTER_GLOTTAL_STOP", "LATIN_LETTER_PHARYNGEAL_VOICED_FRICATIVE", "LATIN_LETTER_INVERTED_GLOTTAL_STOP", "LATIN_LETTER_STRETCHED_C", "LATIN_LETTER_BILABIAL_CLICK", "LATIN_LETTER_SMALL_CAPITAL_B", "LATIN_SMALL_LETTER_CLOSED_OPEN_E", "LATIN_LETTER_SMALL_CAPITAL_G_WITH_HOOK", "LATIN_LETTER_SMALL_CAPITAL_H", "LATIN_SMALL_LETTER_J_WITH_CROSSED-TAIL", "LATIN_SMALL_LETTER_TURNED_K", "LATIN_LETTER_SMALL_CAPITAL_L", "LATIN_SMALL_LETTER_Q_WITH_HOOK", "LATIN_LETTER_GLOTTAL_STOP_WITH_STROKE", "LATIN_LETTER_REVERSED_GLOTTAL_STOP_WITH_STROKE", "LATIN_SMALL_LETTER_DZ_DIGRAPH", "LATIN_SMALL_LETTER_DEZH_DIGRAPH", "LATIN_SMALL_LETTER_DZ_DIGRAPH_WITH_CURL", "LATIN_SMALL_LETTER_TS_DIGRAPH", "LATIN_SMALL_LETTER_TESH_DIGRAPH", "LATIN_SMALL_LETTER_TC_DIGRAPH_WITH_CURL", "LATIN_SMALL_LETTER_FENG_DIGRAPH", "LATIN_SMALL_LETTER_LS_DIGRAPH", "LATIN_SMALL_LETTER_LZ_DIGRAPH", "LATIN_LETTER_BILABIAL_PERCUSSIVE", "LATIN_LETTER_BIDENTAL_PERCUSSIVE", "LATIN_SMALL_LETTER_TURNED_H_WITH_FISHHOOK", "LATIN_SMALL_LETTER_TURNED_H_WITH_FISHHOOK_AND_TAIL", "MODIFIER_LETTER_SMALL_H", "MODIFIER_LETTER_SMALL_H_WITH_HOOK", "MODIFIER_LETTER_SMALL_J", "MODIFIER_LETTER_SMALL_R", "MODIFIER_LETTER_SMALL_TURNED_R", "MODIFIER_LETTER_SMALL_TURNED_R_WITH_HOOK", "MODIFIER_LETTER_SMALL_CAPITAL_INVERTED_R", "MODIFIER_LETTER_SMALL_W", "MODIFIER_LETTER_SMALL_Y", "MODIFIER_LETTER_PRIME", "MODIFIER_LETTER_DOUBLE_PRIME", "MODIFIER_LETTER_TURNED_COMMA", "MODIFIER_LETTER_APOSTROPHE", "MODIFIER_LETTER_REVERSED_COMMA", "MODIFIER_LETTER_RIGHT_HALF_RING", "MODIFIER_LETTER_LEFT_HALF_RING", "MODIFIER_LETTER_GLOTTAL_STOP", "MODIFIER_LETTER_REVERSED_GLOTTAL_STOP", "MODIFIER_LETTER_LEFT_ARROWHEAD", "MODIFIER_LETTER_RIGHT_ARROWHEAD", "MODIFIER_LETTER_UP_ARROWHEAD", "MODIFIER_LETTER_DOWN_ARROWHEAD", "MODIFIER_LETTER_CIRCUMFLEX_ACCENT", "CARON", "MODIFIER_LETTER_VERTICAL_LINE", "MODIFIER_LETTER_MACRON", "MODIFIER_LETTER_ACUTE_ACCENT", "MODIFIER_LETTER_GRAVE_ACCENT", "MODIFIER_LETTER_LOW_VERTICAL_LINE", "MODIFIER_LETTER_LOW_MACRON", "MODIFIER_LETTER_LOW_GRAVE_ACCENT", "MODIFIER_LETTER_LOW_ACUTE_ACCENT", "MODIFIER_LETTER_TRIANGULAR_COLON", "MODIFIER_LETTER_HALF_TRIANGULAR_COLON", "MODIFIER_LETTER_CENTRED_RIGHT_HALF_RING", "MODIFIER_LETTER_CENTRED_LEFT_HALF_RING", "MODIFIER_LETTER_UP_TACK", "MODIFIER_LETTER_DOWN_TACK", "MODIFIER_LETTER_PLUS_SIGN", "MODIFIER_LETTER_MINUS_SIGN", "BREVE", "DOT_ABOVE", "RING_ABOVE", "OGONEK", "SMALL_TILDE", "DOUBLE_ACUTE_ACCENT", "MODIFIER_LETTER_RHOTIC_HOOK", "MODIFIER_LETTER_CROSS_ACCENT", "MODIFIER_LETTER_SMALL_GAMMA", "MODIFIER_LETTER_SMALL_L", "MODIFIER_LETTER_SMALL_S", "MODIFIER_LETTER_SMALL_X", "MODIFIER_LETTER_SMALL_REVERSED_GLOTTAL_STOP", "MODIFIER_LETTER_EXTRA-HIGH_TONE_BAR", "MODIFIER_LETTER_HIGH_TONE_BAR", "MODIFIER_LETTER_MID_TONE_BAR", "MODIFIER_LETTER_LOW_TONE_BAR", "MODIFIER_LETTER_EXTRA-LOW_TONE_BAR", "MODIFIER_LETTER_YIN_DEPARTING_TONE_MARK", "MODIFIER_LETTER_YANG_DEPARTING_TONE_MARK", "MODIFIER_LETTER_VOICING", "MODIFIER_LETTER_UNASPIRATED", "MODIFIER_LETTER_DOUBLE_APOSTROPHE", "MODIFIER_LETTER_LOW_DOWN_ARROWHEAD", "MODIFIER_LETTER_LOW_UP_ARROWHEAD", "MODIFIER_LETTER_LOW_LEFT_ARROWHEAD", "MODIFIER_LETTER_LOW_RIGHT_ARROWHEAD", "MODIFIER_LETTER_LOW_RING", "MODIFIER_LETTER_MIDDLE_GRAVE_ACCENT", "MODIFIER_LETTER_MIDDLE_DOUBLE_GRAVE_ACCENT", "MODIFIER_LETTER_MIDDLE_DOUBLE_ACUTE_ACCENT", "MODIFIER_LETTER_LOW_TILDE", "MODIFIER_LETTER_RAISED_COLON", "MODIFIER_LETTER_BEGIN_HIGH_TONE", "MODIFIER_LETTER_END_HIGH_TONE", "MODIFIER_LETTER_BEGIN_LOW_TONE", "MODIFIER_LETTER_END_LOW_TONE", "MODIFIER_LETTER_SHELF", "MODIFIER_LETTER_OPEN_SHELF", "MODIFIER_LETTER_LOW_LEFT_ARROW", "COMBINING_GRAVE_ACCENT"});
	    }
}
static class CharNameMaker3{
	    {
	    	setCharNames(768, new String[]{"COMBINING_GRAVE_ACCENT", "COMBINING_ACUTE_ACCENT", "COMBINING_CIRCUMFLEX_ACCENT", "COMBINING_TILDE", "COMBINING_MACRON", "COMBINING_OVERLINE", "COMBINING_BREVE", "COMBINING_DOT_ABOVE", "COMBINING_DIAERESIS", "COMBINING_HOOK_ABOVE", "COMBINING_RING_ABOVE", "COMBINING_DOUBLE_ACUTE_ACCENT", "COMBINING_CARON", "COMBINING_VERTICAL_LINE_ABOVE", "COMBINING_DOUBLE_VERTICAL_LINE_ABOVE", "COMBINING_DOUBLE_GRAVE_ACCENT", "COMBINING_CANDRABINDU", "COMBINING_INVERTED_BREVE", "COMBINING_TURNED_COMMA_ABOVE", "COMBINING_COMMA_ABOVE", "COMBINING_REVERSED_COMMA_ABOVE", "COMBINING_COMMA_ABOVE_RIGHT", "COMBINING_GRAVE_ACCENT_BELOW", "COMBINING_ACUTE_ACCENT_BELOW", "COMBINING_LEFT_TACK_BELOW", "COMBINING_RIGHT_TACK_BELOW", "COMBINING_LEFT_ANGLE_ABOVE", "COMBINING_HORN", "COMBINING_LEFT_HALF_RING_BELOW", "COMBINING_UP_TACK_BELOW", "COMBINING_DOWN_TACK_BELOW", "COMBINING_PLUS_SIGN_BELOW", "COMBINING_MINUS_SIGN_BELOW", "COMBINING_PALATALIZED_HOOK_BELOW", "COMBINING_RETROFLEX_HOOK_BELOW", "COMBINING_DOT_BELOW", "COMBINING_DIAERESIS_BELOW", "COMBINING_RING_BELOW", "COMBINING_COMMA_BELOW", "COMBINING_CEDILLA", "COMBINING_OGONEK", "COMBINING_VERTICAL_LINE_BELOW", "COMBINING_BRIDGE_BELOW", "COMBINING_INVERTED_DOUBLE_ARCH_BELOW", "COMBINING_CARON_BELOW", "COMBINING_CIRCUMFLEX_ACCENT_BELOW", "COMBINING_BREVE_BELOW", "COMBINING_INVERTED_BREVE_BELOW", "COMBINING_TILDE_BELOW", "COMBINING_MACRON_BELOW", "COMBINING_LOW_LINE", "COMBINING_DOUBLE_LOW_LINE", "COMBINING_TILDE_OVERLAY", "COMBINING_SHORT_STROKE_OVERLAY", "COMBINING_LONG_STROKE_OVERLAY", "COMBINING_SHORT_SOLIDUS_OVERLAY", "COMBINING_LONG_SOLIDUS_OVERLAY", "COMBINING_RIGHT_HALF_RING_BELOW", "COMBINING_INVERTED_BRIDGE_BELOW", "COMBINING_SQUARE_BELOW", "COMBINING_SEAGULL_BELOW", "COMBINING_X_ABOVE", "COMBINING_VERTICAL_TILDE", "COMBINING_DOUBLE_OVERLINE", "COMBINING_GRAVE_TONE_MARK", "COMBINING_ACUTE_TONE_MARK", "COMBINING_GREEK_PERISPOMENI", "COMBINING_GREEK_KORONIS", "COMBINING_GREEK_DIALYTIKA_TONOS", "COMBINING_GREEK_YPOGEGRAMMENI", "COMBINING_BRIDGE_ABOVE", "COMBINING_EQUALS_SIGN_BELOW", "COMBINING_DOUBLE_VERTICAL_LINE_BELOW", "COMBINING_LEFT_ANGLE_BELOW", "COMBINING_NOT_TILDE_ABOVE", "COMBINING_HOMOTHETIC_ABOVE", "COMBINING_ALMOST_EQUAL_TO_ABOVE", "COMBINING_LEFT_RIGHT_ARROW_BELOW", "COMBINING_UPWARDS_ARROW_BELOW", "COMBINING_GRAPHEME_JOINER", "COMBINING_RIGHT_ARROWHEAD_ABOVE", "COMBINING_LEFT_HALF_RING_ABOVE", "COMBINING_FERMATA", "COMBINING_X_BELOW", "COMBINING_LEFT_ARROWHEAD_BELOW", "COMBINING_RIGHT_ARROWHEAD_BELOW", "COMBINING_RIGHT_ARROWHEAD_AND_UP_ARROWHEAD_BELOW", "COMBINING_RIGHT_HALF_RING_ABOVE", "U0358", "U0359", "U035A", "U035B", "U035C", "COMBINING_DOUBLE_BREVE", "COMBINING_DOUBLE_MACRON", "COMBINING_DOUBLE_MACRON_BELOW", "COMBINING_DOUBLE_TILDE", "COMBINING_DOUBLE_INVERTED_BREVE", "COMBINING_DOUBLE_RIGHTWARDS_ARROW_BELOW", "COMBINING_LATIN_SMALL_LETTER_A", "COMBINING_LATIN_SMALL_LETTER_E", "COMBINING_LATIN_SMALL_LETTER_I", "COMBINING_LATIN_SMALL_LETTER_O", "COMBINING_LATIN_SMALL_LETTER_U", "COMBINING_LATIN_SMALL_LETTER_C", "COMBINING_LATIN_SMALL_LETTER_D", "COMBINING_LATIN_SMALL_LETTER_H", "COMBINING_LATIN_SMALL_LETTER_M", "COMBINING_LATIN_SMALL_LETTER_R", "COMBINING_LATIN_SMALL_LETTER_T", "COMBINING_LATIN_SMALL_LETTER_V", "COMBINING_LATIN_SMALL_LETTER_X", "U0370", "U0371", "U0372", "U0373", "GREEK_NUMERAL_SIGN", "GREEK_LOWER_NUMERAL_SIGN", "U0376", "U0377", "U0378", "U0379", "GREEK_YPOGEGRAMMENI", "U037B", "U037C", "U037D", "GREEK_QUESTION_MARK", "U037F", "U0380", "U0381", "U0382", "U0383", "GREEK_TONOS", "GREEK_DIALYTIKA_TONOS", "GREEK_CAPITAL_LETTER_ALPHA_WITH_TONOS", "GREEK_ANO_TELEIA", "GREEK_CAPITAL_LETTER_EPSILON_WITH_TONOS", "GREEK_CAPITAL_LETTER_ETA_WITH_TONOS", "GREEK_CAPITAL_LETTER_IOTA_WITH_TONOS", "U038B", "GREEK_CAPITAL_LETTER_OMICRON_WITH_TONOS", "U038D", "GREEK_CAPITAL_LETTER_UPSILON_WITH_TONOS", "GREEK_CAPITAL_LETTER_OMEGA_WITH_TONOS", "GREEK_SMALL_LETTER_IOTA_WITH_DIALYTIKA_AND_TONOS", "GREEK_CAPITAL_LETTER_ALPHA", "GREEK_CAPITAL_LETTER_BETA", "GREEK_CAPITAL_LETTER_GAMMA", "GREEK_CAPITAL_LETTER_DELTA", "GREEK_CAPITAL_LETTER_EPSILON", "GREEK_CAPITAL_LETTER_ZETA", "GREEK_CAPITAL_LETTER_ETA", "GREEK_CAPITAL_LETTER_THETA", "GREEK_CAPITAL_LETTER_IOTA", "GREEK_CAPITAL_LETTER_KAPPA", "GREEK_CAPITAL_LETTER_LAMDA", "GREEK_CAPITAL_LETTER_MU", "GREEK_CAPITAL_LETTER_NU", "GREEK_CAPITAL_LETTER_XI", "GREEK_CAPITAL_LETTER_OMICRON", "GREEK_CAPITAL_LETTER_PI", "GREEK_CAPITAL_LETTER_RHO", "U03A2", "GREEK_CAPITAL_LETTER_SIGMA", "GREEK_CAPITAL_LETTER_TAU", "GREEK_CAPITAL_LETTER_UPSILON", "GREEK_CAPITAL_LETTER_PHI", "GREEK_CAPITAL_LETTER_CHI", "GREEK_CAPITAL_LETTER_PSI", "GREEK_CAPITAL_LETTER_OMEGA", "GREEK_CAPITAL_LETTER_IOTA_WITH_DIALYTIKA", "GREEK_CAPITAL_LETTER_UPSILON_WITH_DIALYTIKA", "GREEK_SMALL_LETTER_ALPHA_WITH_TONOS", "GREEK_SMALL_LETTER_EPSILON_WITH_TONOS", 
	    			"GREEK_SMALL_LETTER_ETA_WITH_TONOS", "GREEK_SMALL_LETTER_IOTA_WITH_TONOS", "GREEK_SMALL_LETTER_UPSILON_WITH_DIALYTIKA_AND_TONOS", "GREEK_SMALL_LETTER_ALPHA", "GREEK_SMALL_LETTER_BETA", "GREEK_SMALL_LETTER_GAMMA", "GREEK_SMALL_LETTER_DELTA", "GREEK_SMALL_LETTER_EPSILON", "GREEK_SMALL_LETTER_ZETA", "GREEK_SMALL_LETTER_ETA", "GREEK_SMALL_LETTER_THETA", "GREEK_SMALL_LETTER_IOTA", "GREEK_SMALL_LETTER_KAPPA", "GREEK_SMALL_LETTER_LAMDA", "GREEK_SMALL_LETTER_MU", "GREEK_SMALL_LETTER_NU", "GREEK_SMALL_LETTER_XI", "GREEK_SMALL_LETTER_OMICRON", "GREEK_SMALL_LETTER_PI", "GREEK_SMALL_LETTER_RHO", "GREEK_SMALL_LETTER_FINAL_SIGMA", "GREEK_SMALL_LETTER_SIGMA", "GREEK_SMALL_LETTER_TAU", "GREEK_SMALL_LETTER_UPSILON", "GREEK_SMALL_LETTER_PHI", "GREEK_SMALL_LETTER_CHI", "GREEK_SMALL_LETTER_PSI", "GREEK_SMALL_LETTER_OMEGA", "GREEK_SMALL_LETTER_IOTA_WITH_DIALYTIKA", "GREEK_SMALL_LETTER_UPSILON_WITH_DIALYTIKA", "GREEK_SMALL_LETTER_OMICRON_WITH_TONOS", "GREEK_SMALL_LETTER_UPSILON_WITH_TONOS", "GREEK_SMALL_LETTER_OMEGA_WITH_TONOS", "U03CF", "GREEK_BETA_SYMBOL", "GREEK_THETA_SYMBOL", "GREEK_UPSILON_WITH_HOOK_SYMBOL", "GREEK_UPSILON_WITH_ACUTE_AND_HOOK_SYMBOL", "GREEK_UPSILON_WITH_DIAERESIS_AND_HOOK_SYMBOL", "GREEK_PHI_SYMBOL", "GREEK_PI_SYMBOL", "GREEK_KAI_SYMBOL", "GREEK_LETTER_ARCHAIC_KOPPA", "GREEK_SMALL_LETTER_ARCHAIC_KOPPA", "GREEK_LETTER_STIGMA", "GREEK_SMALL_LETTER_STIGMA", "GREEK_LETTER_DIGAMMA", "GREEK_SMALL_LETTER_DIGAMMA", "GREEK_LETTER_KOPPA", "GREEK_SMALL_LETTER_KOPPA", "GREEK_LETTER_SAMPI", "GREEK_SMALL_LETTER_SAMPI", "COPTIC_CAPITAL_LETTER_SHEI", "COPTIC_SMALL_LETTER_SHEI", "COPTIC_CAPITAL_LETTER_FEI", "COPTIC_SMALL_LETTER_FEI", "COPTIC_CAPITAL_LETTER_KHEI", "COPTIC_SMALL_LETTER_KHEI", "COPTIC_CAPITAL_LETTER_HORI", "COPTIC_SMALL_LETTER_HORI", "COPTIC_CAPITAL_LETTER_GANGIA", "COPTIC_SMALL_LETTER_GANGIA", "COPTIC_CAPITAL_LETTER_SHIMA", "COPTIC_SMALL_LETTER_SHIMA", "COPTIC_CAPITAL_LETTER_DEI", "COPTIC_SMALL_LETTER_DEI", "GREEK_KAPPA_SYMBOL", "GREEK_RHO_SYMBOL", "GREEK_LUNATE_SIGMA_SYMBOL", "GREEK_LETTER_YOT", "GREEK_CAPITAL_THETA_SYMBOL", "GREEK_LUNATE_EPSILON_SYMBOL", "GREEK_REVERSED_LUNATE_EPSILON_SYMBOL", "GREEK_CAPITAL_LETTER_SHO", "GREEK_SMALL_LETTER_SHO", "GREEK_CAPITAL_LUNATE_SIGMA_SYMBOL", "GREEK_CAPITAL_LETTER_SAN", "GREEK_SMALL_LETTER_SAN", "U03FC", "U03FD", "U03FE", "U03FF", "CYRILLIC_CAPITAL_LETTER_IE_WITH_GRAVE"});
	    }
}
static class CharNameMaker4{
	    {
	    	setCharNames(1024, new String[]{"CYRILLIC_CAPITAL_LETTER_IE_WITH_GRAVE", "CYRILLIC_CAPITAL_LETTER_IO", "CYRILLIC_CAPITAL_LETTER_DJE", "CYRILLIC_CAPITAL_LETTER_GJE", "CYRILLIC_CAPITAL_LETTER_UKRAINIAN_IE", "CYRILLIC_CAPITAL_LETTER_DZE", "CYRILLIC_CAPITAL_LETTER_BYELORUSSIAN-UKRAINIAN_I", "CYRILLIC_CAPITAL_LETTER_YI", "CYRILLIC_CAPITAL_LETTER_JE", "CYRILLIC_CAPITAL_LETTER_LJE", "CYRILLIC_CAPITAL_LETTER_NJE", "CYRILLIC_CAPITAL_LETTER_TSHE", "CYRILLIC_CAPITAL_LETTER_KJE", "CYRILLIC_CAPITAL_LETTER_I_WITH_GRAVE", "CYRILLIC_CAPITAL_LETTER_SHORT_U", "CYRILLIC_CAPITAL_LETTER_DZHE", "CYRILLIC_CAPITAL_LETTER_A", "CYRILLIC_CAPITAL_LETTER_BE", "CYRILLIC_CAPITAL_LETTER_VE", "CYRILLIC_CAPITAL_LETTER_GHE", "CYRILLIC_CAPITAL_LETTER_DE", "CYRILLIC_CAPITAL_LETTER_IE", "CYRILLIC_CAPITAL_LETTER_ZHE", "CYRILLIC_CAPITAL_LETTER_ZE", "CYRILLIC_CAPITAL_LETTER_I", "CYRILLIC_CAPITAL_LETTER_SHORT_I", "CYRILLIC_CAPITAL_LETTER_KA", "CYRILLIC_CAPITAL_LETTER_EL", "CYRILLIC_CAPITAL_LETTER_EM", "CYRILLIC_CAPITAL_LETTER_EN", "CYRILLIC_CAPITAL_LETTER_O", "CYRILLIC_CAPITAL_LETTER_PE", "CYRILLIC_CAPITAL_LETTER_ER", "CYRILLIC_CAPITAL_LETTER_ES", "CYRILLIC_CAPITAL_LETTER_TE", "CYRILLIC_CAPITAL_LETTER_U", "CYRILLIC_CAPITAL_LETTER_EF", "CYRILLIC_CAPITAL_LETTER_HA", "CYRILLIC_CAPITAL_LETTER_TSE", "CYRILLIC_CAPITAL_LETTER_CHE", "CYRILLIC_CAPITAL_LETTER_SHA", "CYRILLIC_CAPITAL_LETTER_SHCHA", "CYRILLIC_CAPITAL_LETTER_HARD_SIGN", "CYRILLIC_CAPITAL_LETTER_YERU", "CYRILLIC_CAPITAL_LETTER_SOFT_SIGN", "CYRILLIC_CAPITAL_LETTER_E", "CYRILLIC_CAPITAL_LETTER_YU", "CYRILLIC_CAPITAL_LETTER_YA", "CYRILLIC_SMALL_LETTER_A", "CYRILLIC_SMALL_LETTER_BE", "CYRILLIC_SMALL_LETTER_VE", "CYRILLIC_SMALL_LETTER_GHE", "CYRILLIC_SMALL_LETTER_DE", "CYRILLIC_SMALL_LETTER_IE", "CYRILLIC_SMALL_LETTER_ZHE", "CYRILLIC_SMALL_LETTER_ZE", "CYRILLIC_SMALL_LETTER_I", "CYRILLIC_SMALL_LETTER_SHORT_I", "CYRILLIC_SMALL_LETTER_KA", "CYRILLIC_SMALL_LETTER_EL", "CYRILLIC_SMALL_LETTER_EM", "CYRILLIC_SMALL_LETTER_EN", "CYRILLIC_SMALL_LETTER_O", "CYRILLIC_SMALL_LETTER_PE", "CYRILLIC_SMALL_LETTER_ER", "CYRILLIC_SMALL_LETTER_ES", "CYRILLIC_SMALL_LETTER_TE", "CYRILLIC_SMALL_LETTER_U", "CYRILLIC_SMALL_LETTER_EF", "CYRILLIC_SMALL_LETTER_HA", "CYRILLIC_SMALL_LETTER_TSE", "CYRILLIC_SMALL_LETTER_CHE", "CYRILLIC_SMALL_LETTER_SHA", "CYRILLIC_SMALL_LETTER_SHCHA", "CYRILLIC_SMALL_LETTER_HARD_SIGN", "CYRILLIC_SMALL_LETTER_YERU", "CYRILLIC_SMALL_LETTER_SOFT_SIGN", "CYRILLIC_SMALL_LETTER_E", "CYRILLIC_SMALL_LETTER_YU", "CYRILLIC_SMALL_LETTER_YA", "CYRILLIC_SMALL_LETTER_IE_WITH_GRAVE", "CYRILLIC_SMALL_LETTER_IO", "CYRILLIC_SMALL_LETTER_DJE", "CYRILLIC_SMALL_LETTER_GJE", "CYRILLIC_SMALL_LETTER_UKRAINIAN_IE", "CYRILLIC_SMALL_LETTER_DZE", "CYRILLIC_SMALL_LETTER_BYELORUSSIAN-UKRAINIAN_I", "CYRILLIC_SMALL_LETTER_YI", "CYRILLIC_SMALL_LETTER_JE", "CYRILLIC_SMALL_LETTER_LJE", "CYRILLIC_SMALL_LETTER_NJE", "CYRILLIC_SMALL_LETTER_TSHE", "CYRILLIC_SMALL_LETTER_KJE", "CYRILLIC_SMALL_LETTER_I_WITH_GRAVE", "CYRILLIC_SMALL_LETTER_SHORT_U", "CYRILLIC_SMALL_LETTER_DZHE", "CYRILLIC_CAPITAL_LETTER_OMEGA", "CYRILLIC_SMALL_LETTER_OMEGA", "CYRILLIC_CAPITAL_LETTER_YAT", "CYRILLIC_SMALL_LETTER_YAT", "CYRILLIC_CAPITAL_LETTER_IOTIFIED_E", "CYRILLIC_SMALL_LETTER_IOTIFIED_E", "CYRILLIC_CAPITAL_LETTER_LITTLE_YUS", "CYRILLIC_SMALL_LETTER_LITTLE_YUS", "CYRILLIC_CAPITAL_LETTER_IOTIFIED_LITTLE_YUS", "CYRILLIC_SMALL_LETTER_IOTIFIED_LITTLE_YUS", "CYRILLIC_CAPITAL_LETTER_BIG_YUS", "CYRILLIC_SMALL_LETTER_BIG_YUS", "CYRILLIC_CAPITAL_LETTER_IOTIFIED_BIG_YUS", "CYRILLIC_SMALL_LETTER_IOTIFIED_BIG_YUS", "CYRILLIC_CAPITAL_LETTER_KSI", "CYRILLIC_SMALL_LETTER_KSI", "CYRILLIC_CAPITAL_LETTER_PSI", "CYRILLIC_SMALL_LETTER_PSI", "CYRILLIC_CAPITAL_LETTER_FITA", "CYRILLIC_SMALL_LETTER_FITA", "CYRILLIC_CAPITAL_LETTER_IZHITSA", "CYRILLIC_SMALL_LETTER_IZHITSA", "CYRILLIC_CAPITAL_LETTER_IZHITSA_WITH_DOUBLE_GRAVE_ACCENT", "CYRILLIC_SMALL_LETTER_IZHITSA_WITH_DOUBLE_GRAVE_ACCENT", "CYRILLIC_CAPITAL_LETTER_UK", "CYRILLIC_SMALL_LETTER_UK", "CYRILLIC_CAPITAL_LETTER_ROUND_OMEGA", "CYRILLIC_SMALL_LETTER_ROUND_OMEGA", "CYRILLIC_CAPITAL_LETTER_OMEGA_WITH_TITLO", "CYRILLIC_SMALL_LETTER_OMEGA_WITH_TITLO", "CYRILLIC_CAPITAL_LETTER_OT", "CYRILLIC_SMALL_LETTER_OT", "CYRILLIC_CAPITAL_LETTER_KOPPA", "CYRILLIC_SMALL_LETTER_KOPPA", "CYRILLIC_THOUSANDS_SIGN", "COMBINING_CYRILLIC_TITLO", "COMBINING_CYRILLIC_PALATALIZATION", "COMBINING_CYRILLIC_DASIA_PNEUMATA", "COMBINING_CYRILLIC_PSILI_PNEUMATA", "U0487", "COMBINING_CYRILLIC_HUNDRED_THOUSANDS_SIGN", "COMBINING_CYRILLIC_MILLIONS_SIGN", "CYRILLIC_CAPITAL_LETTER_SHORT_I_WITH_TAIL", "CYRILLIC_SMALL_LETTER_SHORT_I_WITH_TAIL", "CYRILLIC_CAPITAL_LETTER_SEMISOFT_SIGN", "CYRILLIC_SMALL_LETTER_SEMISOFT_SIGN", "CYRILLIC_CAPITAL_LETTER_ER_WITH_TICK", "CYRILLIC_SMALL_LETTER_ER_WITH_TICK", "CYRILLIC_CAPITAL_LETTER_GHE_WITH_UPTURN", "CYRILLIC_SMALL_LETTER_GHE_WITH_UPTURN", 
	    			"CYRILLIC_CAPITAL_LETTER_GHE_WITH_STROKE", "CYRILLIC_SMALL_LETTER_GHE_WITH_STROKE", "CYRILLIC_CAPITAL_LETTER_GHE_WITH_MIDDLE_HOOK", "CYRILLIC_SMALL_LETTER_GHE_WITH_MIDDLE_HOOK", "CYRILLIC_CAPITAL_LETTER_ZHE_WITH_DESCENDER", "CYRILLIC_SMALL_LETTER_ZHE_WITH_DESCENDER", "CYRILLIC_CAPITAL_LETTER_ZE_WITH_DESCENDER", "CYRILLIC_SMALL_LETTER_ZE_WITH_DESCENDER", "CYRILLIC_CAPITAL_LETTER_KA_WITH_DESCENDER", "CYRILLIC_SMALL_LETTER_KA_WITH_DESCENDER", "CYRILLIC_CAPITAL_LETTER_KA_WITH_VERTICAL_STROKE", "CYRILLIC_SMALL_LETTER_KA_WITH_VERTICAL_STROKE", "CYRILLIC_CAPITAL_LETTER_KA_WITH_STROKE", "CYRILLIC_SMALL_LETTER_KA_WITH_STROKE", "CYRILLIC_CAPITAL_LETTER_BASHKIR_KA", "CYRILLIC_SMALL_LETTER_BASHKIR_KA", "CYRILLIC_CAPITAL_LETTER_EN_WITH_DESCENDER", "CYRILLIC_SMALL_LETTER_EN_WITH_DESCENDER", "CYRILLIC_CAPITAL_LIGATURE_EN_GHE", "CYRILLIC_SMALL_LIGATURE_EN_GHE", "CYRILLIC_CAPITAL_LETTER_PE_WITH_MIDDLE_HOOK", "CYRILLIC_SMALL_LETTER_PE_WITH_MIDDLE_HOOK", "CYRILLIC_CAPITAL_LETTER_ABKHASIAN_HA", "CYRILLIC_SMALL_LETTER_ABKHASIAN_HA", "CYRILLIC_CAPITAL_LETTER_ES_WITH_DESCENDER", "CYRILLIC_SMALL_LETTER_ES_WITH_DESCENDER", "CYRILLIC_CAPITAL_LETTER_TE_WITH_DESCENDER", "CYRILLIC_SMALL_LETTER_TE_WITH_DESCENDER", "CYRILLIC_CAPITAL_LETTER_STRAIGHT_U", "CYRILLIC_SMALL_LETTER_STRAIGHT_U", "CYRILLIC_CAPITAL_LETTER_STRAIGHT_U_WITH_STROKE", "CYRILLIC_SMALL_LETTER_STRAIGHT_U_WITH_STROKE", "CYRILLIC_CAPITAL_LETTER_HA_WITH_DESCENDER", "CYRILLIC_SMALL_LETTER_HA_WITH_DESCENDER", "CYRILLIC_CAPITAL_LIGATURE_TE_TSE", "CYRILLIC_SMALL_LIGATURE_TE_TSE", "CYRILLIC_CAPITAL_LETTER_CHE_WITH_DESCENDER", "CYRILLIC_SMALL_LETTER_CHE_WITH_DESCENDER", "CYRILLIC_CAPITAL_LETTER_CHE_WITH_VERTICAL_STROKE", "CYRILLIC_SMALL_LETTER_CHE_WITH_VERTICAL_STROKE", "CYRILLIC_CAPITAL_LETTER_SHHA", "CYRILLIC_SMALL_LETTER_SHHA", "CYRILLIC_CAPITAL_LETTER_ABKHASIAN_CHE", "CYRILLIC_SMALL_LETTER_ABKHASIAN_CHE", "CYRILLIC_CAPITAL_LETTER_ABKHASIAN_CHE_WITH_DESCENDER", "CYRILLIC_SMALL_LETTER_ABKHASIAN_CHE_WITH_DESCENDER", "CYRILLIC_LETTER_PALOCHKA", "CYRILLIC_CAPITAL_LETTER_ZHE_WITH_BREVE", "CYRILLIC_SMALL_LETTER_ZHE_WITH_BREVE", "CYRILLIC_CAPITAL_LETTER_KA_WITH_HOOK", "CYRILLIC_SMALL_LETTER_KA_WITH_HOOK", "CYRILLIC_CAPITAL_LETTER_EL_WITH_TAIL", "CYRILLIC_SMALL_LETTER_EL_WITH_TAIL", "CYRILLIC_CAPITAL_LETTER_EN_WITH_HOOK", "CYRILLIC_SMALL_LETTER_EN_WITH_HOOK", "CYRILLIC_CAPITAL_LETTER_EN_WITH_TAIL", "CYRILLIC_SMALL_LETTER_EN_WITH_TAIL", "CYRILLIC_CAPITAL_LETTER_KHAKASSIAN_CHE", "CYRILLIC_SMALL_LETTER_KHAKASSIAN_CHE", "CYRILLIC_CAPITAL_LETTER_EM_WITH_TAIL", "CYRILLIC_SMALL_LETTER_EM_WITH_TAIL", "U04CF", "CYRILLIC_CAPITAL_LETTER_A_WITH_BREVE", "CYRILLIC_SMALL_LETTER_A_WITH_BREVE", "CYRILLIC_CAPITAL_LETTER_A_WITH_DIAERESIS", "CYRILLIC_SMALL_LETTER_A_WITH_DIAERESIS", "CYRILLIC_CAPITAL_LIGATURE_A_IE", "CYRILLIC_SMALL_LIGATURE_A_IE", "CYRILLIC_CAPITAL_LETTER_IE_WITH_BREVE", "CYRILLIC_SMALL_LETTER_IE_WITH_BREVE", "CYRILLIC_CAPITAL_LETTER_SCHWA", "CYRILLIC_SMALL_LETTER_SCHWA", "CYRILLIC_CAPITAL_LETTER_SCHWA_WITH_DIAERESIS", "CYRILLIC_SMALL_LETTER_SCHWA_WITH_DIAERESIS", "CYRILLIC_CAPITAL_LETTER_ZHE_WITH_DIAERESIS", "CYRILLIC_SMALL_LETTER_ZHE_WITH_DIAERESIS", "CYRILLIC_CAPITAL_LETTER_ZE_WITH_DIAERESIS", "CYRILLIC_SMALL_LETTER_ZE_WITH_DIAERESIS", "CYRILLIC_CAPITAL_LETTER_ABKHASIAN_DZE", "CYRILLIC_SMALL_LETTER_ABKHASIAN_DZE", "CYRILLIC_CAPITAL_LETTER_I_WITH_MACRON", "CYRILLIC_SMALL_LETTER_I_WITH_MACRON", "CYRILLIC_CAPITAL_LETTER_I_WITH_DIAERESIS", "CYRILLIC_SMALL_LETTER_I_WITH_DIAERESIS", "CYRILLIC_CAPITAL_LETTER_O_WITH_DIAERESIS", "CYRILLIC_SMALL_LETTER_O_WITH_DIAERESIS", "CYRILLIC_CAPITAL_LETTER_BARRED_O", "CYRILLIC_SMALL_LETTER_BARRED_O", "CYRILLIC_CAPITAL_LETTER_BARRED_O_WITH_DIAERESIS", "CYRILLIC_SMALL_LETTER_BARRED_O_WITH_DIAERESIS", "CYRILLIC_CAPITAL_LETTER_E_WITH_DIAERESIS", "CYRILLIC_SMALL_LETTER_E_WITH_DIAERESIS", "CYRILLIC_CAPITAL_LETTER_U_WITH_MACRON", "CYRILLIC_SMALL_LETTER_U_WITH_MACRON", "CYRILLIC_CAPITAL_LETTER_U_WITH_DIAERESIS", "CYRILLIC_SMALL_LETTER_U_WITH_DIAERESIS", "CYRILLIC_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE", "CYRILLIC_SMALL_LETTER_U_WITH_DOUBLE_ACUTE", "CYRILLIC_CAPITAL_LETTER_CHE_WITH_DIAERESIS", "CYRILLIC_SMALL_LETTER_CHE_WITH_DIAERESIS", "U04F6", "U04F7", "CYRILLIC_CAPITAL_LETTER_YERU_WITH_DIAERESIS", "CYRILLIC_SMALL_LETTER_YERU_WITH_DIAERESIS", "U04FA", "U04FB", "U04FC", "U04FD", "U04FE", "U04FF", "CYRILLIC_CAPITAL_LETTER_KOMI_DE"});
	    }
}

/*
 * (non-Javadoc)
 * @see org.armedbear.lisp.AbstractLispObject#charValue()
 */
 public char charValue() {
	// TODO Auto-generated method stub
	return value;
 }
}
