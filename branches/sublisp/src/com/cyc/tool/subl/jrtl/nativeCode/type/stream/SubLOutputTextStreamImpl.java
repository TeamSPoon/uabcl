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
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

//// External Imports

public final class SubLOutputTextStreamImpl extends AbstractSubLTextStream implements SubLOutputTextStream, CommonSymbols {
  
  //// Constructors
  
  /**
   * Creates a new instance of SubLInputBinaryStreamImpl.
   */
  SubLOutputTextStreamImpl(OutputStream outStream) {
    super(TEXT_KEYWORD, OUTPUT_KEYWORD, ERROR_KEYWORD, ERROR_KEYWORD);
    this.outStream = outStream;
    outWriter = new OutputStreamWriter(outStream, DEFAULT_CHARSET);
    bufferedWriter = outWriter;// = new BufferedWriter(outWriter);
  }
  
  /**
   * Creates a new instance of SubLInputBinaryStreamImpl.
   */
  SubLOutputTextStreamImpl(String filename,
      SubLSymbol ifExists, SubLSymbol ifNotExists) {
    super(filename, TEXT_KEYWORD, OUTPUT_KEYWORD, ifExists, ifNotExists);
  }
  
  /**
   * Creates a new instance of SubLInputBinaryStreamImpl as a text output stream.
   */
  SubLOutputTextStreamImpl(int initialByteSizeForString) {
    super(TEXT_KEYWORD, OUTPUT_KEYWORD, APPEND_KEYWORD, CREATE_KEYWORD);
    this.outStream = new ByteArrayOutputStream(initialByteSizeForString);
    outWriter = new OutputStreamWriter(outStream, DEFAULT_CHARSET);
    bufferedWriter = outWriter;//new BufferedWriter(outWriter); //@note this is probably inefficient in this case -APB
  }
  
  /**
   * Creates a new instance of SubLOutputBinaryStreamImpl.
   */
  /*SubLOutputTextStreamImpl(FileDescriptor fileDesc, SubLSymbol elementType, 
      SubLSymbol direction, SubLSymbol ifExists, SubLSymbol ifNotExists) {
    super(elementType, direction, ifExists, ifNotExists);
    this.outStream = new FileOutputStream(fileDesc);
    outWriter = new OutputStreamWriter(outStream, DEFAULT_CHARSET);
    bufferedWriter = outWriter;//= new BufferedWriter(outWriter);
  }*/

  //// Public Area
  
  public final SubLStream getStream(boolean followSynonymStream) {
    return this;
  }
  
  /** Method created to avoid casting */
  public final SubLOutputStream toOutputStream() {
    return this;
  }
  /** Method created to avoid casting */
  public final SubLOutputTextStream toOutputTextStream() {
    return this;
  }
  
//  /** Method created to avoid casting */
//  public final SubLOutputBinaryStream toOutputBinaryStream() {
//    return this;
//  }
  
  public boolean isStringOutputStream() {
    return (outStream != null) && (outStream instanceof ByteArrayOutputStream);
  }
  
  public String getStringOutput() {
    if (!isStringOutputStream()) {
      throw new SubLException("Can't get the stream string output for non-string streams.");
    }
    ensureOpen("GET-STRING-OUTPUT");
    ByteArrayOutputStream byteStream = (ByteArrayOutputStream)outStream;
    try {
      flush();
      String result = byteStream.toString(DEFAULT_CHARSET.name());
      byteStream.reset();
      return result;
    } catch (Exception e) {
      throw new SubLException("Unsupported charset: " + DEFAULT_CHARSET, e);
    }
  }
  
  public synchronized void close() {
    if (isClosed()) { return; }
    super.close();
    try {
      if (bufferedWriter != null) {
        try { bufferedWriter.flush(); } catch (Exception e) {} // ignore
        bufferedWriter.close();
        outStream = null;
        outWriter = null;
        bufferedWriter = null;
      }
    } catch (Exception e) {
      throw new SubLException("Unable to close stream.", e);
    }
  }
  
  public void flush() {
    ensureOpen("FLUSH");
    if (shouldParentDoWork()) {
      super.flush();
      return;
    }
    try {
      bufferedWriter.flush();
    } catch (Exception e) {
      //throw new SubLException("Error flushing stream.", e);
    }
  }
  
  public void writeChar(char b) {
    ensureOpen("WRITE-CHAR");
    if (shouldParentDoWork()) {
      super.write(b);
      return;
    }
    try {
      bufferedWriter.write(b);
    } catch (Exception e) {
      throw new SubLException("Error writing stream.", e);
    }
  }
  
  public void writeChar(char[] b) {
    ensureOpen("WRITE-CHAR");
    if (shouldParentDoWork()) {
      super.writeChar(b);
      return;
    }
    try {
      bufferedWriter.write(b);
    } catch (Exception e) {
      throw new SubLException("Error writing stream.", e);
    }
  }
  
  public void writeChar(char[] b, int off, int len) {
    ensureOpen("WRITE-CHAR");
    if (shouldParentDoWork()) {
      super.writeChar(b, off, len);
      return;
    }
    try {
      bufferedWriter.write(b, off, len);
    } catch (Exception e) {
      throw new SubLException("Error writing stream.", e);
    }
  }
  
  public void writeString(String str) {
    ensureOpen("WRITE-STRING");
    if (shouldParentDoWork()) {
      super.write(str);
      return;
    }
    try {
      bufferedWriter.write(str);
    } catch (Exception e) {
      throw new SubLException("Error writing stream.", e);
    }
  }
  
  public void writeString(String str, int off, int len) {
    ensureOpen("WRITE-STRING");
    if (shouldParentDoWork()) {
      super.write(str, off, len);
      return;
    }
    try {
      bufferedWriter.write(str, off, len);
    } catch (Exception e) {
      throw new SubLException("Error writing stream.", e);
    }
  }
  
    public void write(int b) {
    ensureOpen("WRITE");
    if (shouldParentDoWork()) {
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
    if (shouldParentDoWork()) {
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
    if (shouldParentDoWork()) {
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
  
  //// Internal Rep
  
  private OutputStream outStream;
  private OutputStreamWriter outWriter;
  private Writer bufferedWriter;
  
  //// Main
  
}
