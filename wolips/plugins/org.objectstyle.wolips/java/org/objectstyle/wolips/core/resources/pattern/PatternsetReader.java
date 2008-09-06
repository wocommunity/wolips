/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 The ObjectStyle Group 
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

package org.objectstyle.wolips.core.resources.pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.objectstyle.wolips.core.CorePlugin;

/**
 * @author ulrich
 */
public class PatternsetReader {
	private String[] pattern = new String[0];

	/*
	 * private long lastReadAt = 0; private IFile theFile = null;
	 */

	public PatternsetReader(IFile patternset) {

		ArrayList<String> patternList = new ArrayList<String>();
		BufferedReader patternReader = null;
		try {
			patternReader = new BufferedReader(new FileReader(new File(patternset.getLocation().toOSString())));
			String line = patternReader.readLine();
			while (line != null) {
				if (line.length() > 0) {
					patternList.add(line);
				}
				line = patternReader.readLine();
			}
		} catch (IOException ioe) {
			CorePlugin.getDefault().log(ioe);
			// String msg = "An error occured while reading from pattern file: "
			// + patternset.getLocation().toOSString();
			// throw new InvocationException(msg, ioe);
		} finally {
			if (null != patternReader) {
				try {
					patternReader.close();
				} catch (IOException ioe) {
					// Ignore exception
				}
			}
		}
		setPattern(patternList.toArray(new String[patternList.size()]));
	}

	/**
	 * @param pattern
	 */
	public PatternsetReader(String[] pattern) {
		super();
		setPattern(pattern);
	}
	
	public void setPattern(String[] pattern) {
		this.pattern = pattern;
		if (this.pattern != null) {
			for (int patternNum = 0; patternNum < this.pattern.length; patternNum ++) {
				this.pattern[patternNum] = this.pattern[patternNum].replace("/", File.separator);
			}
		}
	}

	/**
	 * @return Returns the pattern.
	 */
	public String[] getPattern() {
		return pattern;
	}
}
