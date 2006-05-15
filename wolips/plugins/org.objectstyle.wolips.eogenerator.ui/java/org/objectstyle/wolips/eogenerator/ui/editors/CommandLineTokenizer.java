package org.objectstyle.wolips.eogenerator.ui.editors;

import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.StringCharacterIterator;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class CommandLineTokenizer implements Enumeration {
  private StringCharacterIterator myIterator;
  private int myState;
  private boolean myWasQuoted;

  public CommandLineTokenizer(String _line) {
    myIterator = new StringCharacterIterator(_line);
    reset();
  }

  public void reset() {
    myState = 1;
    myIterator.first();
  }

  public boolean hasMoreElements() {
    return hasMoreTokens();
  }

  public boolean hasMoreTokens() {
    return (myIterator.current() != CharacterIterator.DONE);
  }

  public Object nextElement() {
    String token;
    try {
      token = nextToken();
    }
    catch (ParseException e) {
      e.printStackTrace();
      token = null;
    }
    return token;
  }

  public String nextToken() throws ParseException {
    boolean escapeNext = false;
    boolean wasQuoted = myWasQuoted;
    // 1 = Whitespace, 2 = Text, 3 = Quoted;
    StringBuffer token = new StringBuffer();
    char c = myIterator.current();
    boolean done = false;
    while (!done && c != CharacterIterator.DONE) {
      if (escapeNext) {
        switch (c) {
        case '\n':
          throw new ParseException("Unexception escape '\\' at end of string.", myIterator.getIndex());

        default:
          token.append(c);
          c = myIterator.next();
          break;
        }
        escapeNext = false;
      }
      else {
        switch (myState) {
        case 1:
          switch (c) {
          case '\n':
          case ' ':
          case '\t':
            c = myIterator.next();
            break;

          case '\"':
            myState = 3;
            c = myIterator.next();
            if (token.length() > 0 || myWasQuoted) {
              done = true;
              myWasQuoted = false;
            }
            myWasQuoted = true;
            break;

          case '\\':
            escapeNext = true;
            c = myIterator.next();
            break;

          default:
            myState = 2;
            if (token.length() > 0 || myWasQuoted) {
              done = true;
              myWasQuoted = false;
            }
            break;
          }
          break;

        case 2:
          switch (c) {
          case ' ':
          case '\t':
          case '\n':
            myState = 1;
            break;

          // case '\"':
          // throw new ParseException("Unexpected quote '\"' in string.",
          // myIterator.getIndex());

          case '\\':
            escapeNext = true;
            c = myIterator.next();
            break;

          default:
            token.append(c);
            c = myIterator.next();
            break;
          }
          break;

        case 3:
          switch (c) {
          case '\"':
            myState = 1;
            c = myIterator.next();
            break;

          case '\\':
            escapeNext = true;
            c = myIterator.next();
            break;

          default:
            token.append(c);
            c = myIterator.next();
            break;
          }
          break;
        }
      }
    }

    if (token.length() <= 0 && !wasQuoted) {
      throw new NoSuchElementException("There are no more tokens on this line.");
    }

    return token.toString();
  }
}
