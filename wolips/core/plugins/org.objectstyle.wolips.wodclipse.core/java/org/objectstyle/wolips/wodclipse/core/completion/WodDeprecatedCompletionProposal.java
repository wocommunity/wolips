package org.objectstyle.wolips.wodclipse.core.completion;

import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

/**
 * A subtype of the normal completion proposal item that will style its displayed string to
 * have a strikeout to show that this points to something deprecated.
 * 
 * @author jw
 */
public class WodDeprecatedCompletionProposal extends WodCompletionProposal implements ICompletionProposalExtension6 {
  private static final StrikeThroughStyler _strikeThroughStyler = new StrikeThroughStyler();

  public WodDeprecatedCompletionProposal(String token, int replacementOffset, int offset, String replacementString) {
    super(token, replacementOffset, offset, replacementString);
  }

  public WodDeprecatedCompletionProposal(String token, int replacementOffset, int offset, String replacementString, String displayString, int cursorPosition) {
    super(token, replacementOffset, offset, replacementString, displayString, cursorPosition);
  }

  public WodDeprecatedCompletionProposal(String token, int replacementOffset, int replacementLength, int offset, String replacementString, String displayString, int cursorPosition, Image image) {
      super(token, replacementOffset, replacementLength, offset, replacementString, displayString, cursorPosition, image);
  }

  /*
   * @see ICompletionProposalExtension6#getStyledDisplayString()
   */
  public StyledString getStyledDisplayString() {
    return new StyledString(getDisplayString(), _strikeThroughStyler);
  }

  private static class StrikeThroughStyler extends Styler {
    public void applyStyles(TextStyle textStyle) {
      textStyle.strikeout = true;
    }
  }
}
