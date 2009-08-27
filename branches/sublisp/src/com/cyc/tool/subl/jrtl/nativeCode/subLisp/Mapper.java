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

import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObject;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory;
import com.cyc.tool.subl.util.SubLFile;
import com.cyc.tool.subl.util.SubLFiles;

//// Internal Imports

//// External Imports

public class Mapper implements SubLFile {
  
  //// Constructors
  
  /** Creates a new instance of Mapper. */
  public Mapper() {}
  public static final SubLFile me = new Mapper();
  
   
  //// Public Area
  
  public static final SubLObject write_image(SubLObject filename, SubLObject do_full_gc) {
    if ((do_full_gc == UNPROVIDED)) {
      do_full_gc = NIL;
    }
    return Errors.unimplementedMethod("Mapper.write_image()");
  }
 
  //// Initializers
  
  public void declareFunctions() {
    SubLFiles.declareFunction(me, "write_image", "WRITE-IMAGE", 1, 1, false);
  }
  
  public void initializeVariables() {
  }
  
  public void runTopLevelForms() {
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
