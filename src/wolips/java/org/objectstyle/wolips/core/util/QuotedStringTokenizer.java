/*
 * Created on 28.07.2003
 *
 * QuotedStringTokenizer -- tokenize string ignoring quoted separators
 *
 */

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

package org.objectstyle.wolips.core.util;

import java.util.Iterator;

/**
 * @author Harald Niesche
 * @version 1.0
 */
public class QuotedStringTokenizer implements Iterator {

  /**
   * construct a string tokenizer for String <i>string</i>,
   * using ' ' (blank) as separator
   */
  public QuotedStringTokenizer (String string) {
    _s = string;
    _sep = ' ';
    _nextStart = 0;
  }

  /**
   * construct a string tokenizer for String <i>string</i>,
   * using sep as the separator
   */
  public QuotedStringTokenizer (String string, int sep) {
    _s = string;
    _sep = sep;
    _nextStart = 0;
  }

  /**
   * checks whether more tokens are available
   */

  public boolean hasNext () {
    return (_s.length() >= _nextStart);
  }

  public boolean hasMoreTokens() {
    return (hasNext());
  }

  /**
   * Iterator interface: obtains the next token as Object
   */
  public Object next () {
    return nextToken (_sep);
  }

  /**
   * Iterator interface: remove current token (skips it, actually)
   */
  public void remove () {
    nextToken (_sep);
  }

  /**
   * obtain current token as String
   */
  public String nextToken () {
    return (nextToken (_sep));
  }

  /**
   * obtain current token as String, using sep as separator
   */
  public String nextToken (int sep) {
    if (_s.length () < _nextStart) {
      return (null);
    }

    if (_s.length () == _nextStart) {
      ++_nextStart;
      return ("");
    }

    StringBuffer buffer = new StringBuffer ();

    int end;
    int tmp;
    int restStart = _nextStart;
    if ('"' == _s.charAt (_nextStart)) {
      ++_nextStart;
      restStart = _nextStart;
      tmp = _s.indexOf ('"', _nextStart);

      while (
        (tmp != -1) 
        && (tmp+1 < _s.length()) 
        && (_s.charAt(tmp+1) == '"')
      ) {
        buffer.append (_s.substring(restStart, tmp+1));
        restStart = tmp+2;
        tmp = _s.indexOf ('"', tmp+2);
      }

      if (tmp == -1) {
        tmp = _s.length();
      }
    } else {
      tmp = _nextStart;
    }
    int end1 = _s.indexOf (sep, tmp);
    int end2 = _s.length ();
    if (-1 == end1) {
      end1 = _s.length();
    }

    if (end1 < end2) {
      end = end1;
    } else {
      end = end2;
    }

    if (restStart < end) {
      if (_s.charAt (end-1) == '"') {
        buffer.append (_s.substring(restStart, end-1));
      } else {
        buffer.append (_s.substring(restStart, end));
      }
    }

    _nextStart = end+1; // skip the separator

    String result = buffer.toString();
    if (result == null) {
      result = "";
    }

    return (result);
  }

  private String _s;
  private int _sep;
  private int _nextStart;
}
