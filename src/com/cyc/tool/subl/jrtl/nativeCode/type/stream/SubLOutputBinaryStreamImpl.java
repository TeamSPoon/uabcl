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

package com.cyc.tool.subl.jrtl.nativeCode.type.stream;

import com.cyc.tool.subl.jrtl.nativeCode.subLisp.CommonSymbols;
import com.cyc.tool.subl.jrtl.nativeCode.type.exception.SubLException;
import com.cyc.tool.subl.jrtl.nativeCode.type.symbol.SubLSymbol;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;

//// Internal Imports

//// External Imports

public final class SubLOutputBinaryStreamImpl extends AbstractSubLBinaryStream implements SubLOutputBinaryStream, CommonSymbols {
  
  //// Constructors
  
  /**
   * Creates a new instance of SubLInputBinaryStreamImpl.
   */
  SubLOutputBinaryStreamImpl(OutputStream outStream) {
    super(BINARY_KEYWORD, OUTPUT_KEYWORD, ERROR_KEYWORD, ERROR_KEYWORD);
    this.outStream = outStream;
    this.bufferedOutStream = new BufferedOutputStream(outStream);
    init();
  }
  
  /**
   * Creates a new instance of SubLInputBinaryStreamImpl.
   */
  SubLOutputBinaryStreamImpl(String filename, 
      SubLSymbol ifExists, SubLSymbol ifNotExists) {
    super(filename, BINARY_KEYWORD, OUTPUT_KEYWORD, ifExists, ifNotExists);
    init();
  }
  
  /**
   * Creates a new instance of SubLInputBinaryStreamImpl.
   */
   SubLOutputBinaryStreamImpl(FileDescriptor fileDesc, SubLSymbol elementType, 
      SubLSymbol direction, SubLSymbol ifExists, SubLSymbol ifNotExists) {
    super(elementType, direction, ifExists, ifNotExists);
    this.outStream = new FileOutputStream(fileDesc);
    this.bufferedOutStream = new BufferedOutputStream(outStream);
    init();
  }

  //// Public Area
   
  public final SubLStream getStream(boolean followSynonymStream) {
    return this;
  }
  
  /** Method created to avoid casting */
  public final SubLOutputStream toOutputStream() {
    return this;
  }
  /** Method created to avoid casting */
  public final SubLOutputBinaryStream toOutputBinaryStream() {
    return this;
  }
  
  public synchronized void close() {
    if (isClosed()) { return; }
    try {
      if (bufferedOutStream != null) {
        try { bufferedOutStream.flush(); } catch (Exception e) {} //ignore
        bufferedOutStream.close();
        outStream = null;
        bufferedOutStream = null;
      }
    } catch (Exception e) {
      throw new SubLException("Unable to close stream.", e);
    } finally {
      super.close();
    }
  }
  
  public void flush() {
    ensureOpen("FLUSH");
    if (isRandomAccess()) {
      super.flush();
      return;
    }
    try {
      outStream.flush();
    } catch (Exception e) {
      throw new SubLException("Error flushing stream.", e);
    }
  }
  
  public void write(int b) {
    ensureOpen("WRITE");
    if (isRandomAccess()) {
      super.write(b);
      return;
    }
    try {
      outStream.write(b);
    } catch (Exception e) {
      throw new SubLException("Error writing stream.", e);
    }
  }
  
  public void write(byte[] b) {
    ensureOpen("WRITE");
    if (isRandomAccess()) {
      super.write(b);
      return;
    }
    try {
      outStream.write(b);
    } catch (Exception e) {
      throw new SubLException("Error writing stream.", e);
    }
  }
  
  public void write(byte[] b, int off, int len) {
    ensureOpen("WRITE");
    if (isRandomAccess()) {
      super.write(b, off, len);
      return;
    }
    try {
      outStream.write(b, off, len);
    } catch (Exception e) {
      throw new SubLException("Error writing stream.", e);
    }
  }
  
  //// Protected Area
  
  //// Private Area
  
  private void init() {
    if (isRandomAccess()) {
      return; 
    }
    
  }
  
  //// Internal Rep
  
  private OutputStream outStream;
  private BufferedOutputStream bufferedOutStream;
  
  //// Main
  
}
