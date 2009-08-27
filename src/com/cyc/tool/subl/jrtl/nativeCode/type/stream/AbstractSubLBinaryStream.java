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

import com.cyc.tool.subl.jrtl.nativeCode.type.exception.SubLException;
import com.cyc.tool.subl.jrtl.nativeCode.type.exception.SubLStreamNilException;
import com.cyc.tool.subl.jrtl.nativeCode.type.symbol.SubLSymbol;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.channels.FileChannel;

//// Internal Imports

//// External Imports

public abstract class AbstractSubLBinaryStream extends AbstractRandomAccessSubLStream {
  
  //// Constructors
  
  AbstractSubLBinaryStream(SubLSymbol elementType, SubLSymbol direction,
          SubLSymbol ifExists, SubLSymbol ifNotExists) {
    super(elementType, direction, ifExists, ifNotExists);
    if (elementType != BINARY_KEYWORD) {
      throw new SubLException("Got bad stream element type: " + elementType);
    }
  }
  
  AbstractSubLBinaryStream(String fileName, SubLSymbol elementType,
          SubLSymbol direction, SubLSymbol ifExists, SubLSymbol ifNotExists) {
    super(fileName, elementType, direction, ifExists, ifNotExists);
    if (elementType != BINARY_KEYWORD) {
      throw new SubLException("Got bad stream element type: " + elementType);
    }
  }
  
  //// Public Area
  
  //// Protected Area
  
  /*protected final FileChannel getFileInChannel() {
    if (!isRandomAccess()) {
      throw new SubLException("Illegal operation on a non-random access stream.");
    }
    return fileInChannel;
  }
  
  protected final InputStream getInputStream() {
    if (!isRandomAccess()) {
      throw new SubLException("Illegal operation on a non-random access stream.");
    }
    return actualInStream;
  }
  
  protected final FileChannel getFileOutChannel() {
    if (!isRandomAccess()) {
      throw new SubLException("Illegal operation on a non-random access stream.");
    }
    return fileOutChannel;
  }
  
  protected final OutputStream getOutputStream() {
    if (!isRandomAccess()) {
      throw new SubLException("Illegal operation on a non-random access stream.");
    }
    return actualOutStream;
  }*/
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
