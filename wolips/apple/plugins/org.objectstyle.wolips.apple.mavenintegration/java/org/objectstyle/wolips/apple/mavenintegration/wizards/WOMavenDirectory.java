/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 The ObjectStyle Group,
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
/*Portions of this code are Copyright Apple Inc. 2008 and licensed under the
ObjectStyle Group Software License, version 1.0.  This license from Apple
applies solely to the actual code contributed by Apple and to no other code.
No other license or rights are granted by Apple, explicitly, by implication,
by estoppel, or otherwise.  All rights reserved.*/
package org.objectstyle.wolips.apple.mavenintegration.wizards;

/*
 * Licensed to the Codehaus Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Simple class representing a directory of the default Maven2 directory
 * structure.
 *
 * Each Maven2 directory is mainly characterized by its path and its output path
 * in which any potential output created during the processing of the directory
 * is placed.
 */
final class WOMavenDirectory {

  /** The path of this Maven2 directory. */
  private String path = null;

  /** The output path of this Maven2 directory. */
  private String outputPath = null;

  /** Default marker */
  private final boolean isDefault;

  /**
   * Constructor.
   *
   * Creates a Maven2 directory for the given <code>path</code> having the
   * given <code>outputPath</code>.
   *
   * @param path           The relative path of the Maven2 directory.
   * @param outputPath     The relative output path of this Maven2 directory
   *                       or <code>null</code> if processing this directory
   *                       does not produce any output.
   * @param isSourceEntry  Whether this Maven2 directory is a source or source
   *                       related resource directory. Directories having this
   *                       flag set will have an appropriate classpath entry.
   *                       The following Maven2 directories should have this
   *                       flag set:
   *                       <ul>
   *                         <li>src/main/java</li>
   *                         <li>src/main/resources</li>
   *                         <li>src/test/java</li>
   *                         <li>src/test/resources</li>
   *                       </ul>
   */
  WOMavenDirectory( String path, String outputPath, boolean isDefault ) {
    this.path = path;
    this.outputPath = outputPath;
    this.isDefault = isDefault;
  }

  /**
   * Returns the relative path of the Maven2 directory as a <code>String</code>.
   *
   * @return  The relative path of the Maven2 directory.
   *          Is never <code>null</code>.
   */
  String getPath() {
    return path;
  }

  /**
   * Returns the relative output path in which resources resulting from the
   * processing of this Maven2 directory, if any, should be placed.
   *
   * @return  The relative output path of this Maven2 directory or
   *          <code>null</code> if processing this directory does not produce
   *          any output.
   */
  String getOutputPath() {
    return outputPath;
  }

  /**
   * Returns whether this Maven2 directory is a source or source related
   * resource directory.
   *
   * @return  Whether this directory is a source or source related resource
   *          directory.
   */
  boolean isSourceEntry() {
    return this.getOutputPath()!=null;
  }

  boolean isDefault() {
    return this.isDefault;
  }

}

