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

package com.cyc.tool.subl.jrtl.nativeCode.subLisp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObject;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLProcess;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLString;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLProcess.TerminationRequest;
import com.cyc.tool.subl.jrtl.nativeCode.type.exception.SubLException;
import com.cyc.tool.subl.jrtl.nativeCode.type.symbol.SubLSymbol;
import com.cyc.tool.subl.jrtl.translatedCode.sublisp.streams_high;
import com.cyc.tool.subl.util.SubLErrorHistory;
import com.cyc.tool.subl.util.SubLFile;
import com.cyc.tool.subl.util.SubLFiles;
import com.cyc.tool.subl.util.SubLTrampolineFile;


//// Internal Imports

//// External Imports

public final class Errors extends SubLTrampolineFile {

	private static Logger log = Logger.getLogger(Errors.class.getCanonicalName());
	//// Constructors

	/** Creates a new instance of Errors. */
	public Errors() {
	}

	public static final SubLFile me = new Errors();

	//// Public Area

	public static SubLSymbol $break_on_errorP$;
	public static SubLSymbol $continue_cerrorP$;
	public static SubLSymbol $error_abort_handler$;
	public static SubLSymbol $error_handler$;
	public static SubLSymbol $error_message$;
	public static SubLSymbol $ignore_breaksP$;
	public static SubLSymbol $ignore_mustsP$;
	public static SubLSymbol $ignore_warnsP$;
	public static SubLSymbol $suspend_type_checkingP$;
	public static SubLSymbol $restarts$;


	public static final SubLObject list_of_all_errors() {
		return SubLObjectFactory.makeList(SubLErrorHistory.me.getAllErrors());
	}


	public static final SubLObject print_error_details(SubLObject error, SubLObject stream) {
		SubLException exception = (SubLException) error;
		SubLString details = SubLObjectFactory.makeString(exception.toDetailedString());
		streams_high.write_string(details, stream, UNPROVIDED, UNPROVIDED);
		return details;
	}


	public static final SubLObject handleMissingMethodError(String e) {
		log.severe(e);
		handleError(null, new Exception(e));
		return NIL;
	}


	public static final boolean boolHandleMissingMethodError(String e) {
		log.severe(e);
		handleError(null, new Exception(e));
		return false;
	}


	// helper for WITH-ERROR-HANDLER
	public static final void handleRuntimeException(RuntimeException rte) {
		if (rte instanceof TerminationRequest) {
			throw rte;
		}
		SubLObject handler = Errors.$error_handler$.getValue();
		// rte.printStackTrace();
		if (NIL != handler) {
			SubLObject oldValue = Errors.$error_message$.getDynamicValue();
			try {
				String message = rte.getMessage();
				if ((message == null) || (message.length() == 0)
												|| (APPEND_STACK_TRACES_TO_ERROR_MESSAGES.getValue() != NIL)) {
					// @todo make writing exceptions to strings a helper function
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					pw.print(message + "\n");
					rte.printStackTrace(pw);
					pw.flush();
					message = sw.toString();
				}
				Dynamic.bind(Errors.$error_message$, SubLObjectFactory.makeString(message));
				Functions.funcall(handler);
			} finally {
				Dynamic.rebind(Errors.$error_message$, oldValue);
			}
		} else {
			throw rte;
		}
	}


	public static final SubLObject warn(SubLObject formatString, SubLObject[] arguments) {
		if (NIL == Errors.$ignore_warnsP$.getValue()) {
			SubLObject warnString = PrintLow.format(NIL, formatString, arguments);
			SubLString warnStringTyped = (SubLString) warnString;
			showWarnMessage(warnStringTyped.getString());
		}
		return NIL;
	}


	public static final SubLObject warn(SubLObject formatString) {
		if (NIL == Errors.$ignore_warnsP$.getValue()) {
			SubLObject warnString = PrintLow.format(NIL, formatString);
			SubLString warnStringTyped = (SubLString) warnString;
			showWarnMessage(warnStringTyped.getString());
		}
		return NIL;
	}


	public static final SubLObject warn(String formatString) {
		return warn(SubLObjectFactory.makeString(formatString));
	}


	public static final SubLObject warn(SubLObject formatString, SubLObject arg1) {
		if (NIL == Errors.$ignore_warnsP$.getValue()) {
			SubLObject warnString = PrintLow.format(NIL, formatString, arg1);
			SubLString warnStringTyped = (SubLString) warnString;
			showWarnMessage(warnStringTyped.getString());
		}
		return NIL;
	}


	public static final SubLObject warn(SubLObject formatString, SubLObject arg1, SubLObject arg2) {
		if (NIL == Errors.$ignore_warnsP$.getValue()) {
			SubLObject warnString = PrintLow.format(NIL, formatString, arg1, arg2);
			SubLString warnStringTyped = (SubLString) warnString;
			showWarnMessage(warnStringTyped.getString());
		}
		return NIL;
	}


	public static final SubLObject warn(SubLObject formatString, SubLObject arg1, SubLObject arg2,
									SubLObject arg3) {
		// @todo add PrintLow.format up to arg3
		SubLObject[] args = null;
		Resourcer resourcer = Resourcer.getInstance();
		try {
			args = resourcer.acquireSubLObjectArray(3);
			args[0] = arg1;
			args[1] = arg2;
			args[2] = arg3;
			return warn(formatString, args);
		} finally {
			resourcer.releaseSubLObjectArray(args);
		}
	}


	public static final SubLObject error(SubLObject formatString, SubLObject[] arguments) {
		SubLObject errorString = PrintLow.format(NIL, formatString, arguments);
		SubLString errorStringTyped = (SubLString) errorString;
		throw new SubLException(errorStringTyped.getString());
	}


	public static final SubLObject error(SubLObject formatString) {
		SubLObject errorString = PrintLow.format(NIL, formatString);
		SubLString errorStringTyped = (SubLString) errorString;
		throw new SubLException(errorStringTyped.getString());
	}


	public static final SubLObject error(SubLObject formatString, Exception e) {
		SubLObject errorString = PrintLow.format(NIL, formatString);
		SubLString errorStringTyped = (SubLString) errorString;
		throw new SubLException(errorStringTyped.getString(), e);
	}


	public static final SubLObject error(String formatString) {
		return error(SubLObjectFactory.makeString(formatString));
	}


	public static final SubLObject error(String formatString, Exception e) {
		//@todo print stack for passed in exception when appropriate
		return error(SubLObjectFactory.makeString(formatString + "\n" + e.getMessage()), e);
	}


	public static final SubLObject error(SubLObject formatString, SubLObject arg1) {
		String errorString = PrintLow.format(NIL, formatString, arg1).getString();
		throw new SubLException(errorString);
	}


	public static final SubLObject error(SubLObject formatString, SubLObject arg1, SubLObject arg2) {
		String errorString = PrintLow.format(NIL, formatString, arg1, arg2).getString();
		throw new SubLException(errorString);
	}


	public static final SubLObject error(SubLObject formatString, SubLObject arg1, SubLObject arg2,
									SubLObject arg3) {
		// @todo add PrintLow.format up to arg3
		SubLObject[] args = null;
		Resourcer resourcer = Resourcer.getInstance();
		try {
			args = resourcer.acquireSubLObjectArray(3);
			args[0] = arg1;
			args[1] = arg2;
			args[2] = arg3;
			return error(formatString, args);
		} finally {
			resourcer.releaseSubLObjectArray(args);
		}
	}


	public static final SubLObject unimplementedMethod(String methodName) {
		SubLException exp = new SubLException("unimplemented method : " + methodName);
		exp.printStackTrace();
		throw exp;
	}


	public static final void handleError(Exception e) {
		handleError(null, e);
	}


	public static final void handleError(String description, Exception e) {
		if (!(e instanceof SubLException)) {
			e = new SubLException(e);
		}
		SubLException se = (SubLException) e;
		se.setDescription(description);
		SubLErrorHistory.me.add(se);
		if ((!SubLMain.isInitialized())
										|| (CommonSymbols.SHOW_STACK_TRACES.getValue() != CommonSymbols.NIL)) {
			e.printStackTrace();
		} else {
			log.severe(String.format("Error: {}",  e.getMessage() == null ? "RuntimeException of type "
					+ e.getClass().getName() + " without much detail."
					: e.getMessage()));
		}
	}


	public static final SubLObject print_stack_trace(SubLObject outStream) {
		Exception e = new Exception();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		SubLString stackTraceText = SubLObjectFactory.makeString(sw.toString());
		streams_high.write_string(stackTraceText, outStream, UNPROVIDED, UNPROVIDED);
		return NIL;
	}


	public static final SubLObject sublisp_break(SubLObject format_string, SubLObject[] arguments) {
		// @hack until we get a real recursive REPL
		if ((format_string == UNPROVIDED)) {
			format_string = SubLObjectFactory.makeString("break");
		}
		PrintLow.format(StreamsLow.$error_output$.getDynamicValue(SubLProcess.currentSubLThread()),
							 format_string,
							 arguments);
		PrintLow.format(StreamsLow.$error_output$.getDynamicValue(SubLProcess.currentSubLThread()),
							 SubLObjectFactory.makeString("~%"));
		print_stack_trace(StreamsLow.$error_output$);
		StreamsLow.$error_output$.getDynamicValue(SubLProcess.currentSubLThread())
										 .toOutputStream()
										 .flush();
		return NIL;
	}


	public static final SubLObject sublisp_break(SubLObject format_string) {
		return sublisp_break(format_string, Resourcer.EMPTY_SUBL_OBJECT_ARRAY);
	}


	public static final SubLObject cerror(SubLObject continue_string, SubLObject formatString,
									SubLObject[] arguments) {
		if (NIL == $continue_cerrorP$.getDynamicValue()) {
			SubLObject errorString = PrintLow.format(NIL, formatString, arguments);
			SubLString errorStringTyped = (SubLString) errorString;
			throw new SubLException(errorStringTyped.getString());
		}
		return NIL;
	}


	public static final SubLObject cerror(SubLObject continue_string, SubLObject formatString) {
		return cerror(continue_string, formatString, Resourcer.EMPTY_SUBL_OBJECT_ARRAY);
	}


	public static final SubLObject cerror(SubLObject continue_string, SubLObject formatString,
									SubLObject arg1) {
		SubLObject[] args = null;
		Resourcer resourcer = Resourcer.getInstance();
		try {
			args = resourcer.acquireSubLObjectArray(1);
			args[0] = arg1;
			return cerror(continue_string, formatString, args);
		} finally {
			resourcer.releaseSubLObjectArray(args);
		}
	}


	public static final SubLObject cerror(SubLObject continue_string, SubLObject formatString,
									SubLObject arg1, SubLObject arg2) {
		SubLObject[] args = null;
		Resourcer resourcer = Resourcer.getInstance();
		try {
			args = resourcer.acquireSubLObjectArray(2);
			args[0] = arg1;
			args[1] = arg2;
			return cerror(continue_string, formatString, args);
		} finally {
			resourcer.releaseSubLObjectArray(args);
		}
	}


	public static final SubLObject cerror(SubLObject continue_string, SubLObject formatString,
									SubLObject arg1, SubLObject arg2, SubLObject arg3) {
		SubLObject[] args = null;
		Resourcer resourcer = Resourcer.getInstance();
		try {
			args = resourcer.acquireSubLObjectArray(3);
			args[0] = arg1;
			args[1] = arg2;
			args[2] = arg3;
			return cerror(continue_string, formatString, args);
		} finally {
			resourcer.releaseSubLObjectArray(args);
		}
	}


	public static final SubLObject debug() {
		return Errors.unimplementedMethod("Errors.debug()");
	}


	//// Initializers

	public void declareFunctions() {
		SubLFiles.declareFunction(me, "sublisp_break", "BREAK", 0, 1, true);
		SubLFiles.declareFunction(me, "cerror", "CERROR", 2, 0, true);
		SubLFiles.declareFunction(me, "debug", "DEBUG", 0, 0, false);
		SubLFiles.declareFunction(me, "error", "ERROR", 1, 0, true);
		SubLFiles.declareFunction(me, "warn", "WARN", 1, 0, true);
		SubLFiles.declareFunction(me, "print_stack_trace", "PRINT-STACK-TRACE", 0, 1, false);
		SubLFiles.declareFunction(me, "list_of_all_errors", "LIST-OF-ALL-ERRORS", 0, 0, false);
		SubLFiles.declareFunction(me, "print_error_details", "PRINT-ERROR-DETAILS", 1, 1, false);
	}


	public void initializeVariables() {
		$suspend_type_checkingP$ = SubLFiles.defvar(me, "*SUSPEND-TYPE-CHECKING?*", NIL);
		$break_on_errorP$ = SubLFiles.defvar(me, "*BREAK-ON-ERROR?*", NIL);
		$continue_cerrorP$ = SubLFiles.defvar(me, "*CONTINUE-CERROR?*", NIL);
		$error_abort_handler$ = SubLFiles.defvar(me, "*ERROR-ABORT-HANDLER*", NIL);
		$error_handler$ = SubLFiles.defvar(me, "*ERROR-HANDLER*", NIL);
		$error_message$ = SubLFiles.defvar(me, "*ERROR-MESSAGE*", NIL);
		$ignore_breaksP$ = SubLFiles.defvar(me, "*IGNORE-BREAKS?*", NIL);
		$ignore_mustsP$ = SubLFiles.defvar(me, "*IGNORE-MUSTS?*", NIL);
		$ignore_warnsP$ = SubLFiles.defvar(me, "*IGNORE-WARNS?*", NIL);
		$restarts$ = SubLFiles.defvar(me, "*RESTARTS*", NIL);
		SubLFiles.defvar(me, SHOW_STACK_TRACES.getName(), T);
		SubLFiles.defvar(me, APPEND_STACK_TRACES_TO_ERROR_MESSAGES.getName(), NIL);
	}


	public void runTopLevelForms() {
	}


	//// Protected Area

	//// Private Area

	private static final void showWarnMessage(String str) {
		if (SHOW_WARNING_STACK_TRACES) {
			System.err.println(new SubLException("WARNING: " + str).toDetailedString());
		} else {
			System.err.println("WARNING: " + str);
		}
	}

	//// Internal Rep
	private static final boolean SHOW_WARNING_STACK_TRACES = false;

	//// Main

}
