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

//// Internal Imports

//// External Imports
import com.cyc.tool.subl.jrtl.nativeCode.subLisp.Resourcer;
import com.cyc.tool.subl.jrtl.nativeCode.subLisp.SubLListListIterator;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLList;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLObjectFactory;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.SubLString;
import java.io.File;
import java.util.ArrayList;

public class FileSystem {
  
  //// Constructors
  
  /** Creates a new instance of FileSystem. */
  private FileSystem() {
  }
  
  //// Public Area
  static public String fileExists( final String filename) {
    final File file = new File(filename);
    // @stub this should actually do a PROBE open, since that influences
    // the error code returned
    final boolean accessible = file.exists();
    if (accessible) {
      return file.getAbsolutePath();
    } else {
      return null;
    }
  }
  
  
  static public long fileModificationTime( final String filename) {
    final File file = new File(filename);
    return file.lastModified();
  }
  
  
  static public String constructFileName( SubLList directoryList, String pathname,
          String extension, boolean isRelative) {
    final StringBuilder parentName = new StringBuilder();
    if (!isRelative) {
      // @note this is as wrong for Win32 as the current (1.38) filesys.c is for Win32
      parentName.append( File.separator);
    }
    // build up the directory part
    final Resourcer resourcer = Resourcer.getInstance();
    SubLListListIterator iter = null;
    try {
      iter = resourcer.acquireSubLListListIterator(directoryList);
      while (iter.hasNext()) {
        SubLString directoryName = iter.nextSubLObject().toStr();
        parentName.append(directoryName.getString());
        parentName.append(File.separator);
      }
    } finally {
      resourcer.releaseSubLListListIterator(iter);
    }
    // build up the filename
    if (extension != null) {
      pathname = pathname + "." + extension;
    }
    // now we can make a true filename
    String parentStr = parentName.toString();
    if (isRelative) {
      String result = parentStr + ((pathname == null) ? "" : pathname);
      return result;
    }
    return new File("".equals(parentStr) ? null : parentStr, pathname).getAbsolutePath();
  }
  
  
  static public boolean deleteFile( String filename) {
    final File file = new File(filename);
    return file.delete();
  }
  
  
  static public Object[] renameFile( String oldName, String newName) {
    final File oldFile = new File(oldName);
    final File newFile = new File(newName);
    final boolean success = oldFile.renameTo(newFile);
    if (success) {
      return new Object[] { Boolean.TRUE, oldFile.getAbsolutePath(), newFile.getAbsolutePath() };
    } else {
      return new Object[] { Boolean.FALSE, oldFile.getAbsolutePath(), newFile.getAbsolutePath() };
    }
  }
 
  static public boolean isDirectory(final String directoryName) {
    final File directory = new File(directoryName);
    return directory.isDirectory();
  }
  
  public static final String fileSeparator = System.getProperty("file.separator");
  
  static public boolean makeDirectory(String parentName, String directoryName) {
    if (directoryName.endsWith(fileSeparator)) {
      directoryName = directoryName.substring(0, directoryName.length() - fileSeparator.length());
    }
    File directory = new File(directoryName);
    if ((!directory.getAbsolutePath().startsWith(directoryName)) && (parentName != null)) {
      directory = new File(parentName, directoryName);
    }
    return directory.mkdir();
  }
  
  static public boolean deleteDirectory( String directoryName, boolean deleteRecursively) {
    // @note recursive delete is currently not implemented in (1.38) filesys.c
    final File directory = new File(directoryName);
    if (directory.isDirectory()) {
      return directory.delete();
    }
    return false;
  }
  
  static public ArrayList getDirectoryContents( String directoryName, boolean includeDirectory) {
    final File directory = new File(directoryName);
    final ArrayList contents = new ArrayList();
    final File[] directoryContents = directory.listFiles();
    if (directoryContents != null) {
      for (int i = 0; i < directoryContents.length; i++) {
        File current = directoryContents[i];
        String fileName = null;
        if (includeDirectory) {
          fileName = current.getAbsolutePath();
        } else {
          fileName = current.getName();
        }
        // @note this is a bit weird, to make SubL strings here, but
        // having the calling code do the repackaging seems silly
        contents.add( SubLObjectFactory.makeString(fileName));
      }
    }
    return contents;
  }
  
  //// Protected Area
  
  //// Private Area
  
  //// Internal Rep
  
  //// Main
  
}
