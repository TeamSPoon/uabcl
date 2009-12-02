/*
 * Extensions.java
 *
 * Copyright (C) 2002-2007 Peter Graves
 * $Id: Extensions.java 12290 2009-11-30 22:28:50Z vvoutilainen $
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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

import static org.armedbear.lisp.InlinedPrimitiveRegistry.getCurrentOperator;
import java.io.File;
import java.io.IOException;
import static org.armedbear.lisp.Lisp.*;

/**
 * Description of the Class
 * 
 * @author Administrator
 */
public final class Extensions {
  // ### *ed-functions*
  /** Description of the Field */
  public static final Symbol _ED_FUNCTIONS_ = exportSpecial("*ED-FUNCTIONS*", PACKAGE_EXT, list(intern(
      "DEFAULT-ED-FUNCTION", PACKAGE_SYS)));

  // ### source-pathname
  /** Description of the Field */
  public static final Primitive SOURCE_PATHNAME = new Primitive("source-pathname", PACKAGE_EXT, true) {

    public LispObject execute(LispObject arg) {
      return SOURCE_PATHNAME_execute(arg);
    }
  };

  static final public LispObject SOURCE_PATHNAME_execute(LispObject arg) {
    LispObject obj = get(arg, Symbol._SOURCE, NIL);
    if (obj instanceof Cons) {
      return obj.car();
    }
    return obj;
  }

  // ### truly-the value-type form => result*
  private static final SpecialOperator TRULY_THE = new SpecialOperator("truly-the", PACKAGE_EXT, true, "type value") {

    public LispObject execute(LispObject args, Environment env) {
      return TRULY_THE_execute(args, env);
    }
  };

  static final public LispObject TRULY_THE_execute(LispObject args, Environment env) {
    if (args.length() != 2) {
      return error(new WrongNumberOfArgumentsException(getCurrentOperator()));
    }
    return eval(args.cadr(), env, LispThread.currentThread());
  }

  // ### neq
  private static final Primitive NEQ = new Primitive(Symbol.NEQ, "obj1 obj2") {

    public LispObject execute(LispObject first, LispObject second) {
      return NEQ_execute(first, second);
    }
  };

  static final public LispObject NEQ_execute(LispObject first, LispObject second) {
    return first != second ? T : NIL;
  }

  // ### memq item list => tail
  private static final Primitive MEMQ = new Primitive(Symbol.MEMQ, "item list") {

    public LispObject execute(LispObject item, LispObject list) {
      return MEMQ_execute(item, list);
    }
  };

  static final public LispObject MEMQ_execute(LispObject item, LispObject list) {
    while (list instanceof Cons) {
      if (item == ((Cons) list).car) {
        return list;
      }
      list = ((Cons) list).cdr;
    }
    if (list != NIL) {
      type_error(list, Symbol.LIST);
    }
    return NIL;
  }

  // ### memql item list => tail
  private static final Primitive MEMQL = new Primitive(Symbol.MEMQL, "item list") {

    public LispObject execute(LispObject item, LispObject list) {
      return MEMQL_execute(item, list);
    }
  };

  static final public LispObject MEMQL_execute(LispObject item, LispObject list) {
    while (list instanceof Cons) {
      if (item.eql(((Cons) list).car)) {
        return list;
      }
      list = ((Cons) list).cdr;
    }
    if (list != NIL) {
      type_error(list, Symbol.LIST);
    }
    return NIL;
  }

  // ### adjoin-eql item list => new-list
  private static final Primitive ADJOIN_EQL = new Primitive(Symbol.ADJOIN_EQL, "item list") {

    public LispObject execute(LispObject item, LispObject list) {
      return ADJOIN_EQL_execute(item, list);
    }
  };

  static final public LispObject ADJOIN_EQL_execute(LispObject item, LispObject list) {
    return memql(item, list) ? list : new Cons(item, list);
  }

  // ### special-variable-p
  private static final Primitive SPECIAL_VARIABLE_P = new Primitive("special-variable-p", PACKAGE_EXT, true) {

    public LispObject execute(LispObject arg) {
      return SPECIAL_VARIABLE_P_execute(arg);
    }
  };

  static final public LispObject SPECIAL_VARIABLE_P_execute(LispObject arg) {
    return arg.isSpecialVariable() ? T : NIL;
  }

  // ### source
  private static final Primitive SOURCE = new Primitive("source", PACKAGE_EXT, true) {

    public LispObject execute(LispObject arg) {
      return SOURCE_execute(arg);
    }
  };

  static final public LispObject SOURCE_execute(LispObject arg) {
    return get(arg, Symbol._SOURCE, NIL);
  }

  // ### source-file-position
  private static final Primitive SOURCE_FILE_POSITION = new Primitive("source-file-position", PACKAGE_EXT, true) {

    public LispObject execute(LispObject arg) {
      return SOURCE_FILE_POSITION_execute(arg);
    }
  };

  static final public LispObject SOURCE_FILE_POSITION_execute(LispObject arg) {
    LispObject obj = get(arg, Symbol._SOURCE, NIL);
    if (obj instanceof Cons) {
      return obj.cdr();
    }
    return NIL;
  }

  // ### exit
  private static final Primitive EXIT = new Primitive("exit", PACKAGE_EXT, true, "&key status") {

    public LispObject execute() {
      return EXIT_execute();
    }

    public LispObject execute(LispObject first, LispObject second) {
      return EXIT_execute(first, second);
    }
  };

  static final public LispObject EXIT_execute() {
    exit(0);
    return LispThread.currentThread().nothing();
  }

  static final public LispObject EXIT_execute(LispObject first, LispObject second) {
    int status = 0;
    if (first == Keyword.STATUS) {
      if (second instanceof Fixnum) {
        status = ((Fixnum) second).value;
      }
    }
    exit(status);
    return LispThread.currentThread().nothing();
  }

  // ### quit
  private static final Primitive QUIT = new Primitive("quit", PACKAGE_EXT, true, "&key status") {

    public LispObject execute() {
      return QUIT_execute();
    }

    public LispObject execute(LispObject first, LispObject second) {
      return QUIT_execute(first, second);
    }
  };

  static final public LispObject QUIT_execute() {
    exit(0);
    return LispThread.currentThread().nothing();
  }

  static final public LispObject QUIT_execute(LispObject first, LispObject second) {
    int status = 0;
    if (first == Keyword.STATUS) {
      if (second instanceof Fixnum) {
        status = ((Fixnum) second).value;
      }
    }
    exit(status);
    return LispThread.currentThread().nothing();
  }

  // ### dump-java-stack
  private static final Primitive DUMP_JAVA_STACK = new Primitive("dump-java-stack", PACKAGE_EXT, true) {

    public LispObject execute() {
      return DUMP_JAVA_STACK_execute();
    }
  };

  static final public LispObject DUMP_JAVA_STACK_execute() {
    Thread.dumpStack();
    return LispThread.currentThread().nothing();
  }

  // ### make-temp-file => namestring
  private static final Primitive MAKE_TEMP_FILE = new Primitive("make-temp-file", PACKAGE_EXT, true, "") {

    public LispObject execute() {
      return MAKE_TEMP_FILE_execute();
    }
  };

  static final public LispObject MAKE_TEMP_FILE_execute() {
    try {
      File file = File.createTempFile("abcl", null, null);
      if (file != null) {
        return new Pathname(file.getPath());
      }
    } catch (IOException e) {
      Debug.trace(e);
    }
    return NIL;
  }

  // ### interrupt-lisp
  private static final Primitive INTERRUPT_LISP = new Primitive("interrupt-lisp", PACKAGE_EXT, true, "") {

    public LispObject execute() {
      return INTERRUPT_LISP_execute();
    }
  };

  static final public LispObject INTERRUPT_LISP_execute() {
    setInterrupted(true);
    return T;
  }

  // ### getenv
  private static final Primitive GETENV = new Primitive("getenv", PACKAGE_EXT, true) {

    public LispObject execute(LispObject arg) {
      return GETENV_execute(arg);
    }
  };

  static final public LispObject GETENV_execute(LispObject arg) {
    AbstractString string;
    if (arg instanceof AbstractString) {
      string = (AbstractString) arg;
    } else {
      return type_error(arg, Symbol.STRING);
    }
    String result = System.getenv(string.getStringValue());
    if (result != null) {
      return new SimpleString(result);
    } else {
      return NIL;
    }
  }
  static {
    InlinedPrimitiveRegistry.inlineStaticsNow(Extensions.class);
  }
}
