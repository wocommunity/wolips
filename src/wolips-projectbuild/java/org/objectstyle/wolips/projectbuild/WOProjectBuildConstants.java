/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
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
 
package org.objectstyle.wolips.projectbuild;

//import org.eclipse.core.resources.IMarker;
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;

/*
 * Created on 16.02.2003
 *
 */

/**
 * @author Harald Niesche
 *
 * constants used throughout the project build fragment
 */


public interface WOProjectBuildConstants extends IWOLipsPluginConstants {
  static final String RES_EXCLUDES = "res.exclude.patterns";
  static final String RES_EXCLUDES_DEFAULT = "*/Thumbs.db,*/.*,*/CVS/*,CustomInfo.plist";
  
  static final String RES_INCLUDES = "res.include.patterns";
  static final String RES_INCLUDES_DEFAULT = "Properties,*.api,*.d2wmodel,*.xml,*.plist,*.strings,/Resources/*,*/*.wo/*,*/*.eomodeld/*";
  
  static final String WSRES_EXCLUDES = "webserver.res.exclude.patterns";
  static final String WSRES_EXCLUDES_DEFAULT = "*/Thumbs.db,*/.*,*/CVS/*,CustomInfo.plist,*/*.wo/*,*/*.eomodeld/*";

  static final String WSRES_INCLUDES = "webserver.res.include.patterns";
  static final String WSRES_INCLUDES_DEFAULT = "*.js,*.css,*.jpg,*.jpeg,*.png,*.gif,*.html,/WebResources/*";

  static final String MARKER_BUILD_GENERIC   = "org.objectstyle.wolips.projectbuild.marker";
  static final String MARKER_BUILD_PROBLEM   = "org.objectstyle.wolips.projectbuild.problem";
  static final String MARKER_BUILD_DUPLICATE = "org.objectstyle.wolips.projectbuild.duplicate";
}



//Resources:
//
//*/Thumbs.db
//*/.*
//*/CVS/*
//CustomInfo.plist
//
//Properties
//*.api
//*.d2wmodel
//*.xml
//*.plist
//*.strings
//*/*.wo/*
//*/*.eomodeld/*
///Resources/*
//
//
//WebResources:
//
//*/Thumbs.db
//*/.*
//*/CVS/*
//*/*.wo/*
//*/*.eomodeld/*
//
//*.js
//*.css
//*.jpg
//*.jpeg
//*.png
//*.gif
//*.html
///WebResources/*
