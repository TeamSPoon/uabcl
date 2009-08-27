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

package com.cyc.tool.subl.jrtl.nativeCode.type.core;

import com.cyc.tool.subl.jrtl.nativeCode.subLisp.CommonSymbols;
import com.cyc.tool.subl.jrtl.nativeCode.subLisp.Threads;
import com.cyc.tool.subl.jrtl.nativeCode.subLisp.Types;
import com.cyc.tool.subl.jrtl.nativeCode.type.exception.SubLException;
import com.cyc.tool.subl.jrtl.nativeCode.type.number.SubLFixnum;
import com.cyc.tool.subl.jrtl.nativeCode.type.symbol.SubLSymbol;

//// Internal Imports

//// External Imports

public final class SubLLock extends AbstractSubLObject implements SubLObject {
  
  //// Constructors
  
  /** Creates a new instance of SubLLock. */
  SubLLock(SubLString name) {
    this.name = name;
    this.locker = null;
    this.recursionDepth = 0;
  }
  
  //// Public Area
  
  /** Method created to avoid casting */
  public final SubLLock toLock() { // SubLLock
    return this;
  }
  
  public int hashCode(int currentDepth) {
    if (currentDepth < MAX_HASH_DEPTH) {
      return superHash();
    } else {
      return DEFAULT_EXCEEDED_HASH_VALUE;
    }
  }
  
  public final boolean canFastHash() {
    return true;
  }
  
  public String toString() {
    return "#<" + LOCK_TYPE_NAME + " " + getName() + " @ " + hashCode(0) + ">";
  }
  
  public static final String LOCK_TYPE_NAME = "LOCK";
  
  public String toTypeName() {
    return LOCK_TYPE_NAME;
  }
  
  public SubLString getName() {
    return name;
  }
  
  public synchronized boolean isIdle() {
    return locker == null;
  }
  
  public synchronized SubLProcess getLocker() {
    return locker;
  }
  
  public synchronized void seizeLock() {
    while (!tryToSeizeLock()) {
      while (true) {
        try {
          wait();
          break;
        } catch (InterruptedException ie) {
          Threads.possiblyHandleInterrupts(false);
        }
      }
    }
  }
  
  public synchronized void releaseLock() {
    if (DO_LOCKING_CHECKS) {
      if (locker == null) {
        throw new SubLException("Lock " + this +
            " is not held but" +
            " is being released by " + SubLProcess.currentProcess());
        
      } else if (locker != SubLProcess.currentProcess()) {
        throw new SubLException("Lock " + this +
            " held by " + locker +
            " is being released by " + SubLProcess.currentProcess());
      }
    }
    recursionDepth--;
    if (recursionDepth <= 0) {
      locker = null;
      notify();
    }
  }
  
  public final SubLSymbol getType(){
    return Types.$dtp_lock$;
  }
  
  public final SubLFixnum getTypeCode() {
    return CommonSymbols.FIFTEEN_INTEGER;
  }
  
  public final boolean isNil() { return false; }
  public final boolean isBoolean() { return false; }
  public final boolean isSymbol() { return false; }
  public final boolean isKeyword() { return false; }
  public final boolean isAtom() { return true; }
  public final boolean isCons() { return false; }
  public final boolean isList() { return false; }
  public final boolean isSequence() { return false; }
  public final boolean isNumber() { return false; }
  public final boolean isFixnum() { return false; }
  public final boolean isBignum() { return false; }
  public final boolean isIntBignum() { return false; }
  public final boolean isLongBignum() { return false; }
  public final boolean isBigIntegerBignum() { return false; }
  public final boolean isInteger() { return false; }
  public final boolean isDouble() { return false; }
  public final boolean isChar() { return false; }
  public final boolean isString() { return false; }
  public final boolean isVector() { return false; }
  public final boolean isFunction() { return false; }
  public final boolean isFunctionSpec() { return false; }
  public final boolean isMacroOperator() { return false; }
  public final boolean isHashtable() { return false; }
  public final boolean isProcess() { return false; }
  public final boolean isLock() { return true; }
  public final boolean isReadWriteLock() { return false; }
  public final boolean isStructure() { return false; }
  public final boolean isStream() { return false; }
  public final boolean isPackage() { return false; }
  public final boolean isError() { return false; }
  public final boolean isGuid() { return false; }
  public final boolean isSemaphore() { return false; }
  public final boolean isEnvironment() { return false; }
  public final boolean isHashtableIterator() { return false; }
  public final boolean isRegexPattern() { return false; }
  public final boolean isKeyhash() { return false; }
  public final boolean isKeyhashIterator() { return false; }
  
  //// Protected Area
  
  //// Private Area
  
  private synchronized boolean tryToSeizeLock() {
    SubLProcess currentProcess = SubLProcess.currentProcess();
    if (locker == null) {
      locker = currentProcess;
      recursionDepth++;
      return true;
    } else if (locker == currentProcess) {
      recursionDepth++;
      return true;
    } else {
      return false;
    }
  }
  
  //// Internal Rep
  
  private SubLString name;
  private SubLProcess locker;
  private int recursionDepth;
  
  private static final boolean DO_LOCKING_CHECKS = false;
  
}
