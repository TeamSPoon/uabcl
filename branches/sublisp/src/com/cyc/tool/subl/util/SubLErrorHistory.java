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

package com.cyc.tool.subl.util;

//// Internal Imports

//// External Imports
import com.cyc.tool.subl.jrtl.nativeCode.type.exception.SubLException;
import java.util.*;

public class SubLErrorHistory {
  
  //// Constructors
  
  /** Creates a new instance of SubLCommandHistory. */
  SubLErrorHistory() {
  }
  
  //// Public Area
  
  public synchronized void clearHistory() {
    history.clear();
  }
  
  public synchronized ArrayList getAllErrors() {
    ArrayList result = new ArrayList(history.size());
    for (int i = 0, size = history.size(); i < size; i++) {
      result.add(history.get(i));
    }
    return result;
  }
  
  public synchronized int size() {
    return history.size();
  }
  
  public synchronized void add(SubLException item) {
    history.add(item);
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  public static SubLErrorHistory me = new SubLErrorHistory();
  
  private ArrayList history = new ArrayList();
  
  //// Main
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
  }
  
}
