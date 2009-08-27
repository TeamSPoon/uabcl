/***
 *   Copyright (c) 1995-2008 Cycorp Inc.
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *   
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  Substantial portions of this code were developed by the Cyc project
 *  and by Cycorp Inc, whose contribution is gratefully acknowledged.
*/

package com.cyc.tool.subl.jrtl.translatedCode.sublisp;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.ArrayList;
import com.cyc.tool.subl.jrtl.nativeCode.subLisp.*;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.*;
import com.cyc.tool.subl.jrtl.nativeCode.type.symbol.*;
import com.cyc.tool.subl.jrtl.nativeCode.type.number.*;
import com.cyc.tool.subl.jrtl.translatedCode.sublisp.*;
import com.cyc.tool.subl.util.*;
import static com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory.makeBoolean;
import static com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory.makeInteger;
import static com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory.makeDouble;
import static com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory.makeChar;
import static com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory.makeString;
import static com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory.makeSymbol;
import static com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory.makeKeyword;
import static com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory.makeUninternedSymbol;
import static com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory.makeGuid;
import static com.cyc.tool.subl.jrtl.nativeCode.subLisp.ConsesLow.cons;
import static com.cyc.tool.subl.jrtl.nativeCode.subLisp.ConsesLow.list;
import static com.cyc.tool.subl.jrtl.nativeCode.subLisp.ConsesLow.listS;
import static com.cyc.tool.subl.util.SubLFiles.defconstant;
import static com.cyc.tool.subl.util.SubLFiles.deflexical;
import static com.cyc.tool.subl.util.SubLFiles.defparameter;
import static com.cyc.tool.subl.util.SubLFiles.defvar;
import static com.cyc.tool.subl.util.SubLFiles.declareFunction;
import static com.cyc.tool.subl.util.SubLFiles.declareMacro;

public final class complex_special_forms extends SubLTranslatedFile {

  //// Constructor

  private complex_special_forms() {}
  public static final SubLFile me = new complex_special_forms();
  public static final String myName = "com.cyc.tool.subl.jrtl.translatedCode.sublisp.complex_special_forms";

  //// Definitions

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 1040) 
  public static final SubLObject intern_accessor(SubLObject string) {
    return Packages.intern(string, UNPROVIDED);
  }

  /** Defines a new polymorphic function that dispatches on the type of its first argument.  <body> defines a default method. The function define-method can be used to define additional methods. For example:

  (defpolymorphic test (a b)
    (list a b))
      
  (define-method test ((a cons) b)
    (cons b a))
      
  (define-method test ((a fixnum) b)
    (+ a b))
     
  The defpolymorphic form defines the function TEST with a default method.
  The two define-method forms specialize the behavior of TEST for lists and fixnums.
sin
  (test 'foo 'bar) => (FOO BAR)
  (test '(foo) 'bar) => (BAR FOO)
  (test 2 3) => 5
   */
  @SubL(source = "sublisp/complex-special-forms.lisp", position = 1236) 
  public static final SubLObject defpolymorphic(SubLObject macroform, SubLObject v_environment) {
    {
      SubLObject datum = macroform.rest();
      SubLObject current = datum;
      SubLObject name = NIL;
      SubLObject lambda_list = NIL;
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list0);
      name = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list0);
      lambda_list = current.first();
      current = current.rest();
      {
        SubLObject body = current;
        return defpolymorphic_internal(name, lambda_list, body);
      }
    }
  }

  /** Defines a method for the polymorphic function <name> that runs if
<dispatch-arg> is of type <type>. The lambda list must have the same
structure as in the DEFPOLYMORPHIC call. */
  @SubL(source = "sublisp/complex-special-forms.lisp", position = 2890) 
  public static final SubLObject define_method(SubLObject macroform, SubLObject v_environment) {
    {
      SubLObject datum = macroform.rest();
      SubLObject current = datum;
      SubLObject name = NIL;
      SubLObject lambda_list = NIL;
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list0);
      name = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list0);
      lambda_list = current.first();
      current = current.rest();
      {
        SubLObject body = current;
        return define_method_internal(name, lambda_list, body);
      }
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 4827) 
  public static final SubLObject defpolymorphic_internal(SubLObject name, SubLObject arglist, SubLObject body) {
    {
      SubLObject method_table_var = method_table_var(name);
      SubLObject method_function_var = Symbols.make_symbol($str1$METHOD_FUNCTION);
      SubLObject doc_string = NIL;
      if (body.first().isString()) {
        doc_string = list(body.first());
        body = body.rest();
      }
      return list($sym2$PROGN, listS($sym3$DEFLEXICAL, method_table_var, $list4), listS($sym5$DEFINE, name, arglist, ConsesLow.append(doc_string, list(listS($sym6$CLET, list(list(method_function_var, list($sym7$_METHOD, arglist.first(), method_table_var))), list($sym8$PWHEN, method_function_var, list($sym9$RET, listS($sym10$FUNCALL, method_function_var, ConsesLow.append(full_formal_parameter_list(arglist), NIL)))), ConsesLow.append(body, NIL))))));
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 5615) 
  public static final SubLObject define_method_internal(SubLObject name, SubLObject arglist, SubLObject body) {
    {
      SubLObject type = conses_high.second(arglist.first());
      SubLObject plain_args = reader.bq_cons(arglist.first().first(), ConsesLow.append(arglist.rest(), NIL));
      SubLObject method_table_var = method_table_var(name);
      SubLObject method_function = method_function_var(name, type);
      SubLObject regmethod_list = build_regmethod_list(type, method_table_var, method_function);
      return listS($sym2$PROGN, list($sym11$DECLAIM, list($sym12$OPTIMIZE_FUNCALL, method_function)), listS($sym5$DEFINE, method_function, plain_args, ConsesLow.append(body, NIL)), ConsesLow.append(regmethod_list, NIL));
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 6097) 
  public static SubLSymbol $polymorphic_type_hierarchy$ = null;

  /** Given one type, this method builds the right call to register-method. */
  @SubL(source = "sublisp/complex-special-forms.lisp", position = 6461) 
  public static final SubLObject build_regmethod_call(SubLObject type, SubLObject method_table, SubLObject method_function) {
    {
      SubLObject dtp_var = dtp_var(type);
      return list($sym14$_REGISTER_METHOD, method_table, dtp_var, list($sym15$FUNCTION, method_function));
    }
  }

  /** This function builds a list of _register-method calls, as appropriate for the type. */
  @SubL(source = "sublisp/complex-special-forms.lisp", position = 6715) 
  public static final SubLObject build_regmethod_list(SubLObject type, SubLObject method_table, SubLObject method_function) {
    {
      final SubLThread thread = SubLProcess.currentSubLThread();
      {
        SubLObject reglist = NIL;
        SubLObject alias_list = conses_high.second(conses_high.assoc(Symbols.symbol_name(type), $polymorphic_type_hierarchy$.getDynamicValue(thread), Symbols.symbol_function($sym16$STRING_), UNPROVIDED));
        if ((NIL != alias_list)) {
          {
            SubLObject cdolist_list_var = alias_list;
            SubLObject curr = NIL;
            for (curr = cdolist_list_var.first(); (NIL != cdolist_list_var); cdolist_list_var = cdolist_list_var.rest(), curr = cdolist_list_var.first()) {
              reglist = cons(build_regmethod_call(curr, method_table, method_function), reglist);
            }
          }
        } else {
          reglist = list(build_regmethod_call(type, method_table, method_function));
        }
        return reglist;
      }
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 7241) 
  public static final SubLObject method_table_var(SubLObject name) {
    {
      final SubLThread thread = SubLProcess.currentSubLThread();
      {
        SubLObject symbol = NIL;
        {
          SubLObject _prev_bind_0 = Packages.$package$.currentBinding(thread);
          try {
            Packages.$package$.bind(accessor_package(name), thread);
            symbol = intern_accessor(Sequences.cconcatenate($str17$_, new SubLObject[] {Symbols.symbol_name(name), $str18$_METHOD_TABLE_}));
          } finally {
            Packages.$package$.rebind(_prev_bind_0, thread);
          }
        }
        return symbol;
      }
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 7501) 
  public static final SubLObject method_function_var(SubLObject name, SubLObject type) {
    return intern_accessor(Sequences.cconcatenate(Symbols.symbol_name(name), new SubLObject[] {$str19$_, Symbols.symbol_name(type), $str20$_METHOD}));
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 7638) 
  public static final SubLObject dtp_var(SubLObject name) {
    {
      SubLObject symbol = intern_accessor(Sequences.cconcatenate($str21$_DTP_, new SubLObject[] {Symbols.symbol_name(name), $str17$_}));
      return symbol;
    }
  }

  /** Given a function ARGLIST, return a list of all the formal parameters */
  @SubL(source = "sublisp/complex-special-forms.lisp", position = 7831) 
  public static final SubLObject full_formal_parameter_list(SubLObject arglist) {
    {
      SubLObject answer = NIL;
      SubLObject cdolist_list_var = arglist;
      SubLObject arg = NIL;
      for (arg = cdolist_list_var.first(); (NIL != cdolist_list_var); cdolist_list_var = cdolist_list_var.rest(), arg = cdolist_list_var.first()) {
        if (arg.isCons()) {
          answer = cons(arg.first(), answer);
        } else if (((arg == $sym22$_OPTIONAL)
            || (arg == $sym23$_REST))) {
        } else {
          answer = cons(arg, answer);
        }
      }
      return Sequences.nreverse(answer);
    }
  }

  /** Like Common Lisp defstruct except:
 (1) slot initializations are not allowed.

 (2) there is an option: (:c-structure-tag val), where
 val is an integer between 128-255.  If you add a new defstruct, and you want efficient 
 C code support,
 you must have the person who maintains the Sublisp Run-Time Library
 (RTL) add the tag by hand.

 (3) the only other options: 
 (:conc-name whatever)
 (:print-function #'whatever) 

 the default structure print function is
 default-struct-print-function : object stream depth

 (4) The make constructor takes an argument list, which is expected to have the keyword/value pairs, 
 i.e. (make-foo :a 1 :b 2) in common lisp is (make-foo '(:a 1 :b 2)) for us.

 (5) Reading of structures is not supported by the reader. */
  @SubL(source = "sublisp/complex-special-forms.lisp", position = 8162) 
  public static final SubLObject defstruct(SubLObject macroform, SubLObject v_environment) {
    {
      SubLObject datum = macroform.rest();
      SubLObject current = datum;
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list24);
      {
        SubLObject temp = current.rest();
        current = current.first();
        {
          SubLObject name = NIL;
          cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list24);
          name = current.first();
          current = current.rest();
          {
            SubLObject options = current;
            current = temp;
            {
              SubLObject slots = current;
              SubLObject analysis = defstruct_analyze(name, options, slots);
              return defstruct_expand(analysis);
            }
          }
        }
      }
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 9628) 
  public static final SubLObject defstruct_analyze(SubLObject name, SubLObject options, SubLObject slots) {
    checkType(name, $sym25$SYMBOLP);
    {
      SubLObject conc_name = conses_high.second(conses_high.assoc($kw26$CONC_NAME, options, UNPROVIDED, UNPROVIDED));
      SubLObject print_function = conses_high.second(conses_high.assoc($kw27$PRINT_FUNCTION, options, UNPROVIDED, UNPROVIDED));
      SubLObject c_structure_tag = conses_high.second(conses_high.assoc($kw28$C_STRUCTURE_TAG, options, UNPROVIDED, UNPROVIDED));
      conc_name = defstruct_conc_name(name, conc_name);
      {
        SubLObject predicate = defstruct_predicate(name);
        SubLObject constructor = defstruct_constructor(name);
        SubLObject type_var = defstruct_type_var(name);
        SubLObject slot_keywords = defstruct_slot_keywords(slots);
        SubLObject getters = defstruct_getters(conc_name, slots);
        SubLObject setters = defstruct_setters(getters);
        return listS($kw29$NAME, name, ConsesLow.append(((NIL != conc_name) ? ((SubLObject) list($kw26$CONC_NAME, conc_name)) : NIL), ((NIL != print_function) ? ((SubLObject) list($kw27$PRINT_FUNCTION, print_function)) : NIL), ((NIL != c_structure_tag) ? ((SubLObject) list($kw28$C_STRUCTURE_TAG, c_structure_tag)) : NIL), list(new SubLObject[] {$kw30$PREDICATE, predicate, $kw31$CONSTRUCTOR, constructor, $kw32$TYPE_VAR, type_var, $kw33$SLOTS, slots, $kw34$SLOT_KEYWORDS, slot_keywords, $kw35$GETTERS, getters, $kw36$SETTERS, setters})));
      }
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 10730) 
  public static final SubLObject defstruct_conc_name(SubLObject name, SubLObject conc_name) {
    if ((NIL != conc_name)) {
      return conc_name;
    }
    return intern_accessor(Sequences.cconcatenate(Symbols.symbol_name(name), $str19$_));
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 10877) 
  public static final SubLObject defstruct_predicate(SubLObject name) {
    return intern_accessor(Sequences.cconcatenate(Symbols.symbol_name(name), $str37$_P));
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 10979) 
  public static final SubLObject defstruct_constructor(SubLObject name) {
    return intern_accessor(Sequences.cconcatenate($str38$MAKE_, Symbols.symbol_name(name)));
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 11086) 
  public static final SubLObject defstruct_type_var(SubLObject name) {
    return intern_accessor(Sequences.cconcatenate($str21$_DTP_, new SubLObject[] {Symbols.symbol_name(name), $str17$_}));
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 11194) 
  public static final SubLObject defstruct_slot_keywords(SubLObject slots) {
    {
      SubLObject slot_keywords = NIL;
      SubLObject cdolist_list_var = slots;
      SubLObject slot = NIL;
      for (slot = cdolist_list_var.first(); (NIL != cdolist_list_var); cdolist_list_var = cdolist_list_var.rest(), slot = cdolist_list_var.first()) {
        {
          SubLObject slot_keyword = Symbols.make_keyword(Symbols.symbol_name(slot));
          slot_keywords = cons(slot_keyword, slot_keywords);
        }
      }
      return Sequences.nreverse(slot_keywords);
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 11428) 
  public static final SubLObject defstruct_getters(SubLObject conc_name, SubLObject slots) {
    {
      SubLObject getters = NIL;
      SubLObject cdolist_list_var = slots;
      SubLObject slot = NIL;
      for (slot = cdolist_list_var.first(); (NIL != cdolist_list_var); cdolist_list_var = cdolist_list_var.rest(), slot = cdolist_list_var.first()) {
        {
          SubLObject getter = defstruct_getter(conc_name, slot);
          getters = cons(getter, getters);
        }
      }
      return Sequences.nreverse(getters);
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 11639) 
  public static final SubLObject defstruct_getter(SubLObject conc_name, SubLObject slot) {
    return intern_accessor(Strings.string_upcase(Sequences.cconcatenate(Symbols.symbol_name(conc_name), Symbols.symbol_name(slot)), UNPROVIDED, UNPROVIDED));
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 11783) 
  public static final SubLObject defstruct_setters(SubLObject getters) {
    {
      SubLObject setters = NIL;
      SubLObject cdolist_list_var = getters;
      SubLObject getter = NIL;
      for (getter = cdolist_list_var.first(); (NIL != cdolist_list_var); cdolist_list_var = cdolist_list_var.rest(), getter = cdolist_list_var.first()) {
        {
          SubLObject setter = defstruct_setter(getter);
          setters = cons(setter, setters);
        }
      }
      return Sequences.nreverse(setters);
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 11982) 
  public static final SubLObject defstruct_setter(SubLObject getter) {
    {
      SubLObject getter_name = Symbols.symbol_name(getter);
      SubLObject getter_package = getter_package(getter);
      SubLObject setter_name = Sequences.cconcatenate($str39$_CSETF_, getter_name);
      SubLObject setter_package = getter_package;
      return Packages.intern(setter_name, setter_package);
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 12249) 
  public static final SubLObject getter_package(SubLObject getter) {
    {
      SubLObject v_package = Symbols.symbol_package(getter);
      if ((!(Packages.package_name(v_package).equal($str40$CYC)))) {
        v_package = Packages.find_package($str41$SUBLISP);
      }
      return v_package;
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 12444) 
  public static final SubLObject accessor_package(SubLObject accessor) {
    return getter_package(accessor);
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 12549) 
  public static final SubLObject setter_from_accessor(SubLObject accessor) {
    return defstruct_setter(accessor);
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 12673) 
  public static final SubLObject defstruct_lisp_constructor(SubLObject constructor) {
    return intern_accessor(Sequences.cconcatenate(Symbols.symbol_name(constructor), $str42$_1));
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 14074) 
  public static final SubLObject defstruct_expand(SubLObject analysis) {
    {
      final SubLThread thread = SubLProcess.currentSubLThread();
      {
        SubLObject datum = analysis;
        SubLObject current = datum;
        SubLObject allow_other_keys_p = NIL;
        SubLObject rest = current;
        SubLObject bad = NIL;
        SubLObject current_1 = NIL;
        for (; (NIL != rest); ) {
          cdestructuring_bind.destructuring_bind_must_consp(rest, datum, $list43);
          current_1 = rest.first();
          rest = rest.rest();
          cdestructuring_bind.destructuring_bind_must_consp(rest, datum, $list43);
          if ((NIL == conses_high.member(current_1, $list44, UNPROVIDED, UNPROVIDED))) {
            bad = T;
          }
          if ((current_1 == $kw45$ALLOW_OTHER_KEYS)) {
            allow_other_keys_p = rest.first();
          }
          rest = rest.rest();
        }
        if (((NIL != bad)
             && (NIL == allow_other_keys_p))) {
          cdestructuring_bind.cdestructuring_bind_error(datum, $list43);
        }
        {
          SubLObject name_tail = cdestructuring_bind.property_list_member($kw29$NAME, current);
          SubLObject name = ((NIL != name_tail) ? ((SubLObject) conses_high.cadr(name_tail)) : NIL);
          SubLObject conc_name_tail = cdestructuring_bind.property_list_member($kw26$CONC_NAME, current);
          SubLObject conc_name = ((NIL != conc_name_tail) ? ((SubLObject) conses_high.cadr(conc_name_tail)) : NIL);
          SubLObject print_function_tail = cdestructuring_bind.property_list_member($kw27$PRINT_FUNCTION, current);
          SubLObject print_function = ((NIL != print_function_tail) ? ((SubLObject) conses_high.cadr(print_function_tail)) : NIL);
          SubLObject c_structure_tag_tail = cdestructuring_bind.property_list_member($kw28$C_STRUCTURE_TAG, current);
          SubLObject c_structure_tag = ((NIL != c_structure_tag_tail) ? ((SubLObject) conses_high.cadr(c_structure_tag_tail)) : NIL);
          SubLObject predicate_tail = cdestructuring_bind.property_list_member($kw30$PREDICATE, current);
          SubLObject predicate = ((NIL != predicate_tail) ? ((SubLObject) conses_high.cadr(predicate_tail)) : NIL);
          SubLObject constructor_tail = cdestructuring_bind.property_list_member($kw31$CONSTRUCTOR, current);
          SubLObject constructor = ((NIL != constructor_tail) ? ((SubLObject) conses_high.cadr(constructor_tail)) : NIL);
          SubLObject type_var_tail = cdestructuring_bind.property_list_member($kw32$TYPE_VAR, current);
          SubLObject type_var = ((NIL != type_var_tail) ? ((SubLObject) conses_high.cadr(type_var_tail)) : NIL);
          SubLObject slots_tail = cdestructuring_bind.property_list_member($kw33$SLOTS, current);
          SubLObject slots = ((NIL != slots_tail) ? ((SubLObject) conses_high.cadr(slots_tail)) : NIL);
          SubLObject slot_keywords_tail = cdestructuring_bind.property_list_member($kw34$SLOT_KEYWORDS, current);
          SubLObject slot_keywords = ((NIL != slot_keywords_tail) ? ((SubLObject) conses_high.cadr(slot_keywords_tail)) : NIL);
          SubLObject getters_tail = cdestructuring_bind.property_list_member($kw35$GETTERS, current);
          SubLObject getters = ((NIL != getters_tail) ? ((SubLObject) conses_high.cadr(getters_tail)) : NIL);
          SubLObject setters_tail = cdestructuring_bind.property_list_member($kw36$SETTERS, current);
          SubLObject setters = ((NIL != setters_tail) ? ((SubLObject) conses_high.cadr(setters_tail)) : NIL);
          if ((NIL != Sequences.find($kw46$SL2JAVA, reader.$features$.getDynamicValue(thread), UNPROVIDED, UNPROVIDED, UNPROVIDED, UNPROVIDED))) {
            c_structure_tag = NIL;
          }
          {
            SubLObject type_var_form = defstruct_type_var_form(type_var, name, c_structure_tag);
            SubLObject print_method = defstruct_print_method(name, print_function);
            SubLObject print_forms = defstruct_print_forms(c_structure_tag, type_var, print_function, print_method);
            SubLObject register_form = defstruct_register_form(name, c_structure_tag, slots, print_method);
            SubLObject predicate_form = defstruct_predicate_form(name, predicate, c_structure_tag, type_var);
            SubLObject getter_forms = defstruct_getter_forms(name, c_structure_tag, predicate, slots, getters);
            SubLObject setter_forms = defstruct_setter_forms(name, c_structure_tag, predicate, slots, setters);
            SubLObject def_csetf_forms = defstruct_def_csetf_forms(getters, setters);
            SubLObject constructor_form = defstruct_constructor_form(name, constructor, c_structure_tag, type_var, slot_keywords, setters);
            return listS($sym2$PROGN, type_var_form, ConsesLow.append(print_forms, ((NIL != register_form) ? ((SubLObject) list(register_form)) : NIL), listS(list($sym11$DECLAIM, list($sym12$OPTIMIZE_FUNCALL, predicate)), predicate_form, ConsesLow.append(getter_forms, setter_forms, def_csetf_forms, list(constructor_form, list(IDENTITY, list($sym47$QUOTE, name)))))));
          }
        }
      }
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 15775) 
  public static final SubLObject _defstruct_object_p(SubLObject macroform, SubLObject v_environment) {
    {
      SubLObject datum = macroform.rest();
      SubLObject current = datum;
      SubLObject object = NIL;
      SubLObject c_structure_tag = NIL;
      SubLObject type_var = NIL;
      SubLObject type = NIL;
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list48);
      object = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list48);
      c_structure_tag = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list48);
      type_var = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list48);
      type = current.first();
      current = current.rest();
      if ((NIL == current)) {
        if ((NIL != c_structure_tag)) {
          return list($sym49$_STRUCTURE_TYPE, object, c_structure_tag);
        } else {
          return list($sym50$CAND, list($sym51$_STRUCTURES_BAG_P, object), list(EQ, listS($sym52$_STRUCTURE_SLOT, object, $list53), type_var));
        }
      } else {
        cdestructuring_bind.cdestructuring_bind_error(datum, $list48);
      }
    }
    return NIL;
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 16085) 
  public static final SubLObject _defstruct_get_slot(SubLObject macroform, SubLObject v_environment) {
    {
      SubLObject datum = macroform.rest();
      SubLObject current = datum;
      SubLObject object = NIL;
      SubLObject index = NIL;
      SubLObject type = NIL;
      SubLObject slot = NIL;
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list54);
      object = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list54);
      index = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list54);
      type = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list54);
      slot = current.first();
      current = current.rest();
      if ((NIL == current)) {
        return list($sym52$_STRUCTURE_SLOT, object, index);
      } else {
        cdestructuring_bind.cdestructuring_bind_error(datum, $list54);
      }
    }
    return NIL;
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 16232) 
  public static final SubLObject _defstruct_set_slot(SubLObject macroform, SubLObject v_environment) {
    {
      SubLObject datum = macroform.rest();
      SubLObject current = datum;
      SubLObject object = NIL;
      SubLObject index = NIL;
      SubLObject value = NIL;
      SubLObject type = NIL;
      SubLObject slot = NIL;
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list55);
      object = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list55);
      index = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list55);
      value = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list55);
      type = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list55);
      slot = current.first();
      current = current.rest();
      if ((NIL == current)) {
        return list($sym56$_SET_STRUCTURE_SLOT, object, index, value);
      } else {
        cdestructuring_bind.cdestructuring_bind_error(datum, $list55);
      }
    }
    return NIL;
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 16396) 
  public static final SubLObject _defstruct_construct(SubLObject macroform, SubLObject v_environment) {
    {
      SubLObject datum = macroform.rest();
      SubLObject current = datum;
      SubLObject c_structure_tag = NIL;
      SubLObject size = NIL;
      SubLObject type_var = NIL;
      SubLObject type = NIL;
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list57);
      c_structure_tag = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list57);
      size = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list57);
      type_var = current.first();
      current = current.rest();
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list57);
      type = current.first();
      current = current.rest();
      if ((NIL == current)) {
        if ((NIL != c_structure_tag)) {
          return list($sym58$_CLEAR_STRUCTURE, list($sym59$_NEW_STRUCTURE, c_structure_tag, size), size);
        } else {
          return list($sym60$_CLEAR_SUB_STRUCTURE, list($sym59$_NEW_STRUCTURE, $sym61$_DTP_STRUCTURES_BAG_, size), size, type_var);
        }
      } else {
        cdestructuring_bind.cdestructuring_bind_error(datum, $list57);
      }
    }
    return NIL;
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 16717) 
  public static final SubLObject defstruct_type_var_form(SubLObject type_var, SubLObject name, SubLObject c_structure_tag) {
    return list($sym62$DEFCONSTANT, type_var, ((NIL != c_structure_tag) ? ((SubLObject) c_structure_tag) : list($sym47$QUOTE, name)));
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 17137) 
  public static final SubLObject defstruct_print_method(SubLObject name, SubLObject print_function) {
    return intern_accessor(Sequences.cconcatenate(Symbols.symbol_name(name), $str63$_PRINT_FUNCTION_TRAMPOLINE));
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 17307) 
  public static final SubLObject defstruct_print_forms(SubLObject c_structure_tag, SubLObject tag_var, SubLObject print_function, SubLObject print_method) {
    if ((NIL == print_function)) {
      print_function = $sym64$DEFAULT_STRUCT_PRINT_FUNCTION;
    }
    return listS(list($sym11$DECLAIM, list($sym65$FACCESS, $sym66$PRIVATE, print_method)), list($sym5$DEFINE, print_method, $list67, reader.bq_cons(print_function, $list68)), ConsesLow.append(((NIL != c_structure_tag) ? ((SubLObject) NIL) : list(list($sym14$_REGISTER_METHOD, $sym69$_PRINT_OBJECT_METHOD_TABLE_, tag_var, list($sym15$FUNCTION, print_method)))), NIL));
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 17790) 
  public static final SubLObject defstruct_register_form(SubLObject name, SubLObject c_structure_tag, SubLObject slots, SubLObject print_method) {
    if ((NIL != c_structure_tag)) {
      return list($sym70$_REGISTER_DEFSTRUCT, Symbols.symbol_name(name), c_structure_tag, Sequences.length(slots), list($sym47$QUOTE, print_method), list($sym47$QUOTE, slots));
    }
    return NIL;
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 18046) 
  public static final SubLObject defstruct_predicate_form(SubLObject type, SubLObject predicate, SubLObject c_structure_tag, SubLObject type_var) {
    {
      SubLObject arglist = defstruct_predicate_arglist(predicate);
      SubLObject predicate_var = arglist.first();
      return list($sym5$DEFINE, predicate, arglist, list($sym9$RET, list($sym71$_DEFSTRUCT_OBJECT_P, predicate_var, c_structure_tag, type_var, type)));
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 18346) 
  public static final SubLObject defstruct_predicate_arglist(SubLObject predicate) {
    return list(defstruct_predicate_variable(predicate));
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 18451) 
  public static final SubLObject defstruct_predicate_variable(SubLObject predicate) {
    return defstruct_first_function_variable(predicate, $sym72$OBJECT);
  }

  /** return a symbol to use as the first variable in the arglist of FUNCTION */
  @SubL(source = "sublisp/complex-special-forms.lisp", position = 18564) 
  public static final SubLObject defstruct_first_function_variable(SubLObject function, SubLObject v_default) {
    return v_default;
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 18876) 
  public static final SubLObject defstruct_getter_forms(SubLObject type, SubLObject c_structure_tag, SubLObject predicate, SubLObject slots, SubLObject getters) {
    {
      SubLObject forms = NIL;
      SubLObject cdotimes_end_var = Sequences.length(getters);
      SubLObject i = NIL;
      for (i = ZERO_INTEGER; i.numL(cdotimes_end_var); i = Numbers.add(i, ONE_INTEGER)) {
        {
          SubLObject slot = ConsesLow.nth(i, slots);
          SubLObject getter = ConsesLow.nth(i, getters);
          SubLObject index = ((NIL != c_structure_tag) ? ((SubLObject) i) : Numbers.add(i, TWO_INTEGER));
          forms = cons(defstruct_getter_form(getter, index, predicate, type, slot), forms);
        }
      }
      return Sequences.nreverse(forms);
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 19289) 
  public static final SubLObject defstruct_getter_form(SubLObject getter, SubLObject index, SubLObject predicate, SubLObject type, SubLObject slot) {
    {
      SubLObject arglist = defstruct_getter_arglist(getter);
      SubLObject getter_var = arglist.first();
      return list($sym5$DEFINE, getter, arglist, list($sym73$CHECK_TYPE, getter_var, predicate), list($sym9$RET, list($sym74$_DEFSTRUCT_GET_SLOT, getter_var, index, type, slot)));
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 19587) 
  public static final SubLObject defstruct_getter_arglist(SubLObject getter) {
    return list(defstruct_getter_variable(getter));
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 19678) 
  public static final SubLObject defstruct_getter_variable(SubLObject getter) {
    return defstruct_first_function_variable(getter, $sym72$OBJECT);
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 19782) 
  public static final SubLObject defstruct_setter_forms(SubLObject type, SubLObject c_structure_tag, SubLObject predicate, SubLObject slots, SubLObject setters) {
    {
      SubLObject forms = NIL;
      SubLObject cdotimes_end_var = Sequences.length(setters);
      SubLObject i = NIL;
      for (i = ZERO_INTEGER; i.numL(cdotimes_end_var); i = Numbers.add(i, ONE_INTEGER)) {
        {
          SubLObject slot = ConsesLow.nth(i, slots);
          SubLObject setter = ConsesLow.nth(i, setters);
          SubLObject index = ((NIL != c_structure_tag) ? ((SubLObject) i) : Numbers.add(i, TWO_INTEGER));
          forms = cons(defstruct_setter_form(setter, index, predicate, type, slot), forms);
        }
      }
      return Sequences.nreverse(forms);
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 20194) 
  public static final SubLObject defstruct_setter_form(SubLObject setter, SubLObject index, SubLObject predicate, SubLObject type, SubLObject slot) {
    {
      SubLObject arglist = defstruct_setter_arglist(setter);
      SubLObject object_var = arglist.first();
      SubLObject value_var = conses_high.second(arglist);
      return list($sym5$DEFINE, setter, arglist, list($sym73$CHECK_TYPE, object_var, predicate), list($sym9$RET, list($sym75$_DEFSTRUCT_SET_SLOT, object_var, index, value_var, type, slot)));
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 20536) 
  public static final SubLObject defstruct_setter_arglist(SubLObject setter) {
    return $list76;
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 20622) 
  public static final SubLObject defstruct_def_csetf_forms(SubLObject getters, SubLObject setters) {
    {
      SubLObject forms = NIL;
      SubLObject rest_getters = NIL;
      SubLObject rest_setters = NIL;
      SubLObject getter = NIL;
      SubLObject setter = NIL;
      for (rest_getters = getters, rest_setters = setters, getter = rest_getters.first(), setter = rest_setters.first(); (NIL != rest_getters); rest_getters = rest_getters.rest(), rest_setters = rest_setters.rest(), getter = rest_getters.first(), setter = rest_setters.first()) {
        forms = cons(list($sym77$_DEF_CSETF, list($sym47$QUOTE, getter), list($sym47$QUOTE, setter)), forms);
      }
      return Sequences.nreverse(forms);
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 21008) 
  public static final SubLObject defstruct_constructor_form(SubLObject type, SubLObject constructor, SubLObject c_structure_tag, SubLObject type_var, SubLObject slot_keywords, SubLObject setters) {
    {
      SubLObject arglist = defstruct_constructor_arglist(constructor);
      SubLObject arglist_var = conses_high.second(arglist);
      SubLObject new_var = $sym78$NEW;
      SubLObject size = Sequences.length(slot_keywords);
      SubLObject arglist_handler = defstruct_constructor_arglist_handler(arglist_var, new_var, slot_keywords, setters);
      return list($sym5$DEFINE, constructor, arglist, list($sym6$CLET, list(list(new_var, list($sym79$_DEFSTRUCT_CONSTRUCT, c_structure_tag, size, type_var, type))), arglist_handler, list($sym9$RET, new_var)));
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 21639) 
  public static final SubLObject defstruct_constructor_arglist(SubLObject constructor) {
    return $list80;
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 21745) 
  public static final SubLObject defstruct_constructor_arglist_handler(SubLObject arglist_var, SubLObject new_var, SubLObject slot_keywords, SubLObject setters) {
    {
      SubLObject current_value = Symbols.make_symbol($str81$CURRENT_VALUE);
      SubLObject current_arg = Symbols.make_symbol($str82$CURRENT_ARG);
      SubLObject next = Symbols.make_symbol($str83$NEXT);
      SubLObject gencaseslots = NIL;
      SubLObject cdotimes_end_var = Sequences.length(slot_keywords);
      SubLObject n = NIL;
      for (n = ZERO_INTEGER; n.numL(cdotimes_end_var); n = Numbers.add(n, ONE_INTEGER)) {
        {
          SubLObject slot_keyword = ConsesLow.nth(n, slot_keywords);
          SubLObject setter = ConsesLow.nth(n, setters);
          gencaseslots = cons(list(slot_keyword, list(setter, new_var, current_value)), gencaseslots);
        }
      }
      gencaseslots = cons(list($sym84$OTHERWISE, list($sym85$ERROR, $str86$Invalid_slot__S_for_construction_, current_arg)), gencaseslots);
      return list($sym87$CDO, list(list(next, arglist_var, list($sym88$CDDR, next))), list(list($sym89$NULL, next)), list($sym6$CLET, list(list(current_arg, list($sym90$CAR, next)), list(current_value, list($sym91$CADR, next))), listS($sym92$PCASE, current_arg, ConsesLow.append(Sequences.nreverse(gencaseslots), NIL))));
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 22555) 
  public static SubLSymbol $call_profiling_enabledP$ = null;

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 22744) 
  public static SubLSymbol $call_profiling_table$ = null;

  /** Execute BODY with call profiling enabled.
   On exit PLACE will be set to an alist of (FUNCTION . COUNT) pairs. */
  @SubL(source = "sublisp/complex-special-forms.lisp", position = 22853) 
  public static final SubLObject with_call_profiling(SubLObject macroform, SubLObject v_environment) {
    {
      SubLObject datum = macroform.rest();
      SubLObject current = datum;
      SubLObject place = NIL;
      cdestructuring_bind.destructuring_bind_must_consp(current, datum, $list93);
      place = current.first();
      current = current.rest();
      {
        SubLObject body = current;
        return list($sym6$CLET, $list94, list($sym95$CUNWIND_PROTECT, reader.bq_cons($sym2$PROGN, ConsesLow.append(body, NIL)), listS($sym96$CSETF, place, $list97)));
      }
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 23258) 
  public static final SubLObject possibly_note_function_entry(SubLObject name) {
    {
      final SubLThread thread = SubLProcess.currentSubLThread();
      if (((NIL != $call_profiling_enabledP$.getDynamicValue(thread))
           && (NIL != $call_profiling_table$.getDynamicValue(thread)))) {
        Hashtables.sethash(name, $call_profiling_table$.getDynamicValue(thread), Numbers.add(Hashtables.gethash_without_values(name, $call_profiling_table$.getDynamicValue(thread), ZERO_INTEGER), ONE_INTEGER));
      }
      return NIL;
    }
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 23456) 
  public static final SubLObject initialize_call_profiling_table() {
    return Hashtables.make_hash_table($int98$1000, Symbols.symbol_function(EQ), UNPROVIDED);
  }

  @SubL(source = "sublisp/complex-special-forms.lisp", position = 23551) 
  public static final SubLObject finalize_call_profiling_table(SubLObject table) {
    {
      SubLObject pairs = NIL;
      SubLObject function = NIL;
      SubLObject count = NIL;
      {
        final Iterator cdohash_iterator = Hashtables.getEntrySetIterator(table);
        try {
          while (Hashtables.iteratorHasNext(cdohash_iterator)) {
            final Entry cdohash_entry = Hashtables.iteratorNextEntry(cdohash_iterator);
            function = Hashtables.getEntryKey(cdohash_entry);
            count = Hashtables.getEntryValue(cdohash_entry);
            pairs = cons(cons(function, count), pairs);
          }
        } finally {
          Hashtables.releaseEntrySetIterator(cdohash_iterator);
        }
      }
      pairs = Sort.sort(pairs, Symbols.symbol_function($sym99$_), Symbols.symbol_function($sym100$CDR));
      return pairs;
    }
  }

  public static final SubLObject declare_complex_special_forms_file() {
    declareFunction(myName, "intern_accessor", "INTERN-ACCESSOR", 1, 0, false);
    declareMacro(myName, "defpolymorphic", "DEFPOLYMORPHIC");
    declareMacro(myName, "define_method", "DEFINE-METHOD");
    declareFunction(myName, "defpolymorphic_internal", "DEFPOLYMORPHIC-INTERNAL", 3, 0, false);
    declareFunction(myName, "define_method_internal", "DEFINE-METHOD-INTERNAL", 3, 0, false);
    declareFunction(myName, "build_regmethod_call", "BUILD-REGMETHOD-CALL", 3, 0, false);
    declareFunction(myName, "build_regmethod_list", "BUILD-REGMETHOD-LIST", 3, 0, false);
    declareFunction(myName, "method_table_var", "METHOD-TABLE-VAR", 1, 0, false);
    declareFunction(myName, "method_function_var", "METHOD-FUNCTION-VAR", 2, 0, false);
    declareFunction(myName, "dtp_var", "DTP-VAR", 1, 0, false);
    declareFunction(myName, "full_formal_parameter_list", "FULL-FORMAL-PARAMETER-LIST", 1, 0, false);
    declareMacro(myName, "defstruct", "DEFSTRUCT");
    declareFunction(myName, "defstruct_analyze", "DEFSTRUCT-ANALYZE", 3, 0, false);
    declareFunction(myName, "defstruct_conc_name", "DEFSTRUCT-CONC-NAME", 2, 0, false);
    declareFunction(myName, "defstruct_predicate", "DEFSTRUCT-PREDICATE", 1, 0, false);
    declareFunction(myName, "defstruct_constructor", "DEFSTRUCT-CONSTRUCTOR", 1, 0, false);
    declareFunction(myName, "defstruct_type_var", "DEFSTRUCT-TYPE-VAR", 1, 0, false);
    declareFunction(myName, "defstruct_slot_keywords", "DEFSTRUCT-SLOT-KEYWORDS", 1, 0, false);
    declareFunction(myName, "defstruct_getters", "DEFSTRUCT-GETTERS", 2, 0, false);
    declareFunction(myName, "defstruct_getter", "DEFSTRUCT-GETTER", 2, 0, false);
    declareFunction(myName, "defstruct_setters", "DEFSTRUCT-SETTERS", 1, 0, false);
    declareFunction(myName, "defstruct_setter", "DEFSTRUCT-SETTER", 1, 0, false);
    declareFunction(myName, "getter_package", "GETTER-PACKAGE", 1, 0, false);
    declareFunction(myName, "accessor_package", "ACCESSOR-PACKAGE", 1, 0, false);
    declareFunction(myName, "setter_from_accessor", "SETTER-FROM-ACCESSOR", 1, 0, false);
    declareFunction(myName, "defstruct_lisp_constructor", "DEFSTRUCT-LISP-CONSTRUCTOR", 1, 0, false);
    declareFunction(myName, "defstruct_expand", "DEFSTRUCT-EXPAND", 1, 0, false);
    declareMacro(myName, "_defstruct_object_p", "_DEFSTRUCT-OBJECT-P");
    declareMacro(myName, "_defstruct_get_slot", "_DEFSTRUCT-GET-SLOT");
    declareMacro(myName, "_defstruct_set_slot", "_DEFSTRUCT-SET-SLOT");
    declareMacro(myName, "_defstruct_construct", "_DEFSTRUCT-CONSTRUCT");
    declareFunction(myName, "defstruct_type_var_form", "DEFSTRUCT-TYPE-VAR-FORM", 3, 0, false);
    declareFunction(myName, "defstruct_print_method", "DEFSTRUCT-PRINT-METHOD", 2, 0, false);
    declareFunction(myName, "defstruct_print_forms", "DEFSTRUCT-PRINT-FORMS", 4, 0, false);
    declareFunction(myName, "defstruct_register_form", "DEFSTRUCT-REGISTER-FORM", 4, 0, false);
    declareFunction(myName, "defstruct_predicate_form", "DEFSTRUCT-PREDICATE-FORM", 4, 0, false);
    declareFunction(myName, "defstruct_predicate_arglist", "DEFSTRUCT-PREDICATE-ARGLIST", 1, 0, false);
    declareFunction(myName, "defstruct_predicate_variable", "DEFSTRUCT-PREDICATE-VARIABLE", 1, 0, false);
    declareFunction(myName, "defstruct_first_function_variable", "DEFSTRUCT-FIRST-FUNCTION-VARIABLE", 2, 0, false);
    declareFunction(myName, "defstruct_getter_forms", "DEFSTRUCT-GETTER-FORMS", 5, 0, false);
    declareFunction(myName, "defstruct_getter_form", "DEFSTRUCT-GETTER-FORM", 5, 0, false);
    declareFunction(myName, "defstruct_getter_arglist", "DEFSTRUCT-GETTER-ARGLIST", 1, 0, false);
    declareFunction(myName, "defstruct_getter_variable", "DEFSTRUCT-GETTER-VARIABLE", 1, 0, false);
    declareFunction(myName, "defstruct_setter_forms", "DEFSTRUCT-SETTER-FORMS", 5, 0, false);
    declareFunction(myName, "defstruct_setter_form", "DEFSTRUCT-SETTER-FORM", 5, 0, false);
    declareFunction(myName, "defstruct_setter_arglist", "DEFSTRUCT-SETTER-ARGLIST", 1, 0, false);
    declareFunction(myName, "defstruct_def_csetf_forms", "DEFSTRUCT-DEF-CSETF-FORMS", 2, 0, false);
    declareFunction(myName, "defstruct_constructor_form", "DEFSTRUCT-CONSTRUCTOR-FORM", 6, 0, false);
    declareFunction(myName, "defstruct_constructor_arglist", "DEFSTRUCT-CONSTRUCTOR-ARGLIST", 1, 0, false);
    declareFunction(myName, "defstruct_constructor_arglist_handler", "DEFSTRUCT-CONSTRUCTOR-ARGLIST-HANDLER", 4, 0, false);
    declareMacro(myName, "with_call_profiling", "WITH-CALL-PROFILING");
    declareFunction(myName, "possibly_note_function_entry", "POSSIBLY-NOTE-FUNCTION-ENTRY", 1, 0, false);
    declareFunction(myName, "initialize_call_profiling_table", "INITIALIZE-CALL-PROFILING-TABLE", 0, 0, false);
    declareFunction(myName, "finalize_call_profiling_table", "FINALIZE-CALL-PROFILING-TABLE", 1, 0, false);
    return NIL;
  }

  public static final SubLObject init_complex_special_forms_file() {
    $polymorphic_type_hierarchy$ = defparameter("*POLYMORPHIC-TYPE-HIERARCHY*", $list13);
    $call_profiling_enabledP$ = defvar("*CALL-PROFILING-ENABLED?*", NIL);
    $call_profiling_table$ = defvar("*CALL-PROFILING-TABLE*", NIL);
    return NIL;
  }

  public static final SubLObject setup_complex_special_forms_file() {
    // CVS_ID("Id: complex-special-forms.lisp,v 1.50 2008/03/24 22:21:10 goolsbey Exp ");
    return NIL;
  }

  //// Internal Constants

  public static final SubLList $list0 = list(makeSymbol("NAME", "SUBLISP"), makeSymbol("LAMBDA-LIST", "SUBLISP"), makeSymbol("&BODY"), makeSymbol("BODY", "SUBLISP"));
  public static final SubLString $str1$METHOD_FUNCTION = makeString("METHOD-FUNCTION");
  public static final SubLSymbol $sym2$PROGN = makeSymbol("PROGN");
  public static final SubLSymbol $sym3$DEFLEXICAL = makeSymbol("DEFLEXICAL");
  public static final SubLList $list4 = list(list(makeSymbol("MAKE-VECTOR"), makeInteger(256), NIL));
  public static final SubLSymbol $sym5$DEFINE = makeSymbol("DEFINE");
  public static final SubLSymbol $sym6$CLET = makeSymbol("CLET");
  public static final SubLSymbol $sym7$_METHOD = makeSymbol("_METHOD", "SUBLISP");
  public static final SubLSymbol $sym8$PWHEN = makeSymbol("PWHEN");
  public static final SubLSymbol $sym9$RET = makeSymbol("RET");
  public static final SubLSymbol $sym10$FUNCALL = makeSymbol("FUNCALL");
  public static final SubLSymbol $sym11$DECLAIM = makeSymbol("DECLAIM");
  public static final SubLSymbol $sym12$OPTIMIZE_FUNCALL = makeSymbol("OPTIMIZE-FUNCALL");
  public static final SubLList $list13 = list(list(makeString("INTEGER"), list(makeSymbol("FIXNUM"), makeSymbol("BIGNUM"))), list(makeString("NUMBER"), list(makeSymbol("FIXNUM"), makeSymbol("BIGNUM"), makeSymbol("FLOAT"))), list(makeString("SEQUENCE"), list(makeSymbol("CONS"), makeSymbol("VECTOR"), makeSymbol("STRING"))));
  public static final SubLSymbol $sym14$_REGISTER_METHOD = makeSymbol("_REGISTER-METHOD", "SUBLISP");
  public static final SubLSymbol $sym15$FUNCTION = makeSymbol("FUNCTION");
  public static final SubLSymbol $sym16$STRING_ = makeSymbol("STRING=");
  public static final SubLString $str17$_ = makeString("*");
  public static final SubLString $str18$_METHOD_TABLE_ = makeString("-METHOD-TABLE*");
  public static final SubLString $str19$_ = makeString("-");
  public static final SubLString $str20$_METHOD = makeString("-METHOD");
  public static final SubLString $str21$_DTP_ = makeString("*DTP-");
  public static final SubLSymbol $sym22$_OPTIONAL = makeSymbol("&OPTIONAL");
  public static final SubLSymbol $sym23$_REST = makeSymbol("&REST");
  public static final SubLList $list24 = list(list(makeSymbol("NAME", "SUBLISP"), makeSymbol("&REST"), makeSymbol("OPTIONS", "SUBLISP")), makeSymbol("&BODY"), makeSymbol("SLOTS", "SUBLISP"));
  public static final SubLSymbol $sym25$SYMBOLP = makeSymbol("SYMBOLP");
  public static final SubLSymbol $kw26$CONC_NAME = makeKeyword("CONC-NAME");
  public static final SubLSymbol $kw27$PRINT_FUNCTION = makeKeyword("PRINT-FUNCTION");
  public static final SubLSymbol $kw28$C_STRUCTURE_TAG = makeKeyword("C-STRUCTURE-TAG");
  public static final SubLSymbol $kw29$NAME = makeKeyword("NAME");
  public static final SubLSymbol $kw30$PREDICATE = makeKeyword("PREDICATE");
  public static final SubLSymbol $kw31$CONSTRUCTOR = makeKeyword("CONSTRUCTOR");
  public static final SubLSymbol $kw32$TYPE_VAR = makeKeyword("TYPE-VAR");
  public static final SubLSymbol $kw33$SLOTS = makeKeyword("SLOTS");
  public static final SubLSymbol $kw34$SLOT_KEYWORDS = makeKeyword("SLOT-KEYWORDS");
  public static final SubLSymbol $kw35$GETTERS = makeKeyword("GETTERS");
  public static final SubLSymbol $kw36$SETTERS = makeKeyword("SETTERS");
  public static final SubLString $str37$_P = makeString("-P");
  public static final SubLString $str38$MAKE_ = makeString("MAKE-");
  public static final SubLString $str39$_CSETF_ = makeString("_CSETF-");
  public static final SubLString $str40$CYC = makeString("CYC");
  public static final SubLString $str41$SUBLISP = makeString("SUBLISP");
  public static final SubLString $str42$_1 = makeString("-1");
  public static final SubLList $list43 = list(new SubLObject[] {makeSymbol("&KEY"), makeSymbol("NAME", "SUBLISP"), makeSymbol("CONC-NAME", "SUBLISP"), makeSymbol("PRINT-FUNCTION", "SUBLISP"), makeSymbol("C-STRUCTURE-TAG", "SUBLISP"), makeSymbol("PREDICATE", "SUBLISP"), makeSymbol("CONSTRUCTOR", "SUBLISP"), makeSymbol("TYPE-VAR", "SUBLISP"), makeSymbol("SLOTS", "SUBLISP"), makeSymbol("SLOT-KEYWORDS", "SUBLISP"), makeSymbol("GETTERS", "SUBLISP"), makeSymbol("SETTERS", "SUBLISP")});
  public static final SubLList $list44 = list(new SubLObject[] {makeKeyword("NAME"), makeKeyword("CONC-NAME"), makeKeyword("PRINT-FUNCTION"), makeKeyword("C-STRUCTURE-TAG"), makeKeyword("PREDICATE"), makeKeyword("CONSTRUCTOR"), makeKeyword("TYPE-VAR"), makeKeyword("SLOTS"), makeKeyword("SLOT-KEYWORDS"), makeKeyword("GETTERS"), makeKeyword("SETTERS")});
  public static final SubLSymbol $kw45$ALLOW_OTHER_KEYS = makeKeyword("ALLOW-OTHER-KEYS");
  public static final SubLSymbol $kw46$SL2JAVA = makeKeyword("SL2JAVA");
  public static final SubLSymbol $sym47$QUOTE = makeSymbol("QUOTE");
  public static final SubLList $list48 = list(makeSymbol("OBJECT", "SUBLISP"), makeSymbol("C-STRUCTURE-TAG", "SUBLISP"), makeSymbol("TYPE-VAR", "SUBLISP"), makeSymbol("TYPE"));
  public static final SubLSymbol $sym49$_STRUCTURE_TYPE = makeSymbol("_STRUCTURE-TYPE", "SUBLISP");
  public static final SubLSymbol $sym50$CAND = makeSymbol("CAND");
  public static final SubLSymbol $sym51$_STRUCTURES_BAG_P = makeSymbol("_STRUCTURES-BAG-P", "SUBLISP");
  public static final SubLSymbol $sym52$_STRUCTURE_SLOT = makeSymbol("_STRUCTURE-SLOT", "SUBLISP");
  public static final SubLList $list53 = list(ONE_INTEGER);
  public static final SubLList $list54 = list(makeSymbol("OBJECT", "SUBLISP"), makeSymbol("INDEX", "SUBLISP"), makeSymbol("TYPE"), makeSymbol("SLOT", "SUBLISP"));
  public static final SubLList $list55 = list(makeSymbol("OBJECT", "SUBLISP"), makeSymbol("INDEX", "SUBLISP"), makeSymbol("VALUE", "SUBLISP"), makeSymbol("TYPE"), makeSymbol("SLOT", "SUBLISP"));
  public static final SubLSymbol $sym56$_SET_STRUCTURE_SLOT = makeSymbol("_SET-STRUCTURE-SLOT", "SUBLISP");
  public static final SubLList $list57 = list(makeSymbol("C-STRUCTURE-TAG", "SUBLISP"), makeSymbol("SIZE", "SUBLISP"), makeSymbol("TYPE-VAR", "SUBLISP"), makeSymbol("TYPE"));
  public static final SubLSymbol $sym58$_CLEAR_STRUCTURE = makeSymbol("_CLEAR-STRUCTURE", "SUBLISP");
  public static final SubLSymbol $sym59$_NEW_STRUCTURE = makeSymbol("_NEW-STRUCTURE", "SUBLISP");
  public static final SubLSymbol $sym60$_CLEAR_SUB_STRUCTURE = makeSymbol("_CLEAR-SUB-STRUCTURE", "SUBLISP");
  public static final SubLSymbol $sym61$_DTP_STRUCTURES_BAG_ = makeSymbol("*DTP-STRUCTURES-BAG*");
  public static final SubLSymbol $sym62$DEFCONSTANT = makeSymbol("DEFCONSTANT");
  public static final SubLString $str63$_PRINT_FUNCTION_TRAMPOLINE = makeString("-PRINT-FUNCTION-TRAMPOLINE");
  public static final SubLSymbol $sym64$DEFAULT_STRUCT_PRINT_FUNCTION = makeSymbol("DEFAULT-STRUCT-PRINT-FUNCTION");
  public static final SubLSymbol $sym65$FACCESS = makeSymbol("FACCESS");
  public static final SubLSymbol $sym66$PRIVATE = makeSymbol("PRIVATE");
  public static final SubLList $list67 = list(makeSymbol("OBJECT", "SUBLISP"), makeSymbol("STREAM"));
  public static final SubLList $list68 = list(makeSymbol("OBJECT", "SUBLISP"), makeSymbol("STREAM"), ZERO_INTEGER);
  public static final SubLSymbol $sym69$_PRINT_OBJECT_METHOD_TABLE_ = makeSymbol("*PRINT-OBJECT-METHOD-TABLE*", "SUBLISP");
  public static final SubLSymbol $sym70$_REGISTER_DEFSTRUCT = makeSymbol("_REGISTER-DEFSTRUCT", "SUBLISP");
  public static final SubLSymbol $sym71$_DEFSTRUCT_OBJECT_P = makeSymbol("_DEFSTRUCT-OBJECT-P", "SUBLISP");
  public static final SubLSymbol $sym72$OBJECT = makeSymbol("OBJECT", "SUBLISP");
  public static final SubLSymbol $sym73$CHECK_TYPE = makeSymbol("CHECK-TYPE");
  public static final SubLSymbol $sym74$_DEFSTRUCT_GET_SLOT = makeSymbol("_DEFSTRUCT-GET-SLOT", "SUBLISP");
  public static final SubLSymbol $sym75$_DEFSTRUCT_SET_SLOT = makeSymbol("_DEFSTRUCT-SET-SLOT", "SUBLISP");
  public static final SubLList $list76 = list(makeSymbol("OBJECT", "SUBLISP"), makeSymbol("VALUE", "SUBLISP"));
  public static final SubLSymbol $sym77$_DEF_CSETF = makeSymbol("_DEF-CSETF", "SUBLISP");
  public static final SubLSymbol $sym78$NEW = makeSymbol("NEW", "SUBLISP");
  public static final SubLSymbol $sym79$_DEFSTRUCT_CONSTRUCT = makeSymbol("_DEFSTRUCT-CONSTRUCT", "SUBLISP");
  public static final SubLList $list80 = list(makeSymbol("&OPTIONAL"), makeSymbol("ARGLIST", "SUBLISP"));
  public static final SubLString $str81$CURRENT_VALUE = makeString("CURRENT-VALUE");
  public static final SubLString $str82$CURRENT_ARG = makeString("CURRENT-ARG");
  public static final SubLString $str83$NEXT = makeString("NEXT");
  public static final SubLSymbol $sym84$OTHERWISE = makeSymbol("OTHERWISE");
  public static final SubLSymbol $sym85$ERROR = makeSymbol("ERROR");
  public static final SubLString $str86$Invalid_slot__S_for_construction_ = makeString("Invalid slot ~S for construction function");
  public static final SubLSymbol $sym87$CDO = makeSymbol("CDO");
  public static final SubLSymbol $sym88$CDDR = makeSymbol("CDDR");
  public static final SubLSymbol $sym89$NULL = makeSymbol("NULL");
  public static final SubLSymbol $sym90$CAR = makeSymbol("CAR");
  public static final SubLSymbol $sym91$CADR = makeSymbol("CADR");
  public static final SubLSymbol $sym92$PCASE = makeSymbol("PCASE");
  public static final SubLList $list93 = list(makeSymbol("PLACE", "SUBLISP"), makeSymbol("&BODY"), makeSymbol("BODY", "SUBLISP"));
  public static final SubLList $list94 = list(list(makeSymbol("*CALL-PROFILING-TABLE*", "SUBLISP"), list(makeSymbol("INITIALIZE-CALL-PROFILING-TABLE", "SUBLISP"))), list(makeSymbol("*CALL-PROFILING-ENABLED?*", "SUBLISP"), T));
  public static final SubLSymbol $sym95$CUNWIND_PROTECT = makeSymbol("CUNWIND-PROTECT");
  public static final SubLSymbol $sym96$CSETF = makeSymbol("CSETF");
  public static final SubLList $list97 = list(list(makeSymbol("FINALIZE-CALL-PROFILING-TABLE", "SUBLISP"), makeSymbol("*CALL-PROFILING-TABLE*", "SUBLISP")));
  public static final SubLInteger $int98$1000 = makeInteger(1000);
  public static final SubLSymbol $sym99$_ = makeSymbol(">");
  public static final SubLSymbol $sym100$CDR = makeSymbol("CDR");

  //// Initializers

  public void declareFunctions() {
    declare_complex_special_forms_file();
  }

  public void initializeVariables() {
    init_complex_special_forms_file();
  }

  public void runTopLevelForms() {
    setup_complex_special_forms_file();
  }

}
