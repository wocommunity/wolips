package tk.eclipse.plugin.htmleditor.assist;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextStyle;

/**
 * Represents a completion proposal for the HTML editor that is deprecated. This should have been a
 * subclass of {@link CompletionProposal} but unfortunately that class is final.
 * 
 * @author jw
 */
public class HTMLDeprecatedCompletionProposal implements ICompletionProposal, ICompletionProposalExtension6 {
  /** The string to be displayed in the completion proposal popup. */
  private String fDisplayString;
  /** The replacement string. */
  private String fReplacementString;
  /** The replacement offset. */
  private int fReplacementOffset;
  /** The replacement length. */
  private int fReplacementLength;
  /** The cursor position after this proposal has been applied. */
  private int fCursorPosition;
  /** The image to be displayed in the completion proposal popup. */
  private Image fImage;
  /** The context information of this proposal. */
  private IContextInformation fContextInformation;
  /** The additional info of this proposal. */
  private String fAdditionalProposalInfo;

  private static final StrikeThroughStyler _strikeThroughStyler = new StrikeThroughStyler();

  public HTMLDeprecatedCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition) {
    this(replacementString, replacementOffset, replacementLength, cursorPosition, null, null, null, null);
  }

  public HTMLDeprecatedCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition, Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo) {
    fReplacementString= replacementString;
    fReplacementOffset= replacementOffset;
    fReplacementLength= replacementLength;
    fCursorPosition= cursorPosition;
    fImage= image;
    fDisplayString= displayString;
    fContextInformation= contextInformation;
    fAdditionalProposalInfo= additionalProposalInfo;
  }

  public StyledString getStyledDisplayString() {
    return new StyledString(getDisplayString(), _strikeThroughStyler);
  }

  /*
   * @see ICompletionProposal#apply(IDocument)
   */
  public void apply(IDocument document) {
    try {
      document.replace(fReplacementOffset, fReplacementLength, fReplacementString);
    } catch (BadLocationException x) {
      // ignore
    }
  }

  /*
   * @see ICompletionProposal#getSelection(IDocument)
   */
  public Point getSelection(IDocument document) {
    return new Point(fReplacementOffset + fCursorPosition, 0);
  }

  /*
   * @see ICompletionProposal#getContextInformation()
   */
  public IContextInformation getContextInformation() {
    return fContextInformation;
  }

  /*
   * @see ICompletionProposal#getImage()
   */
  public Image getImage() {
    return fImage;
  }

  /*
   * @see ICompletionProposal#getDisplayString()
   */
  public String getDisplayString() {
    if (fDisplayString != null)
      return fDisplayString;
    return fReplacementString;
  }

  /*
   * @see ICompletionProposal#getAdditionalProposalInfo()
   */
  public String getAdditionalProposalInfo() {
    return fAdditionalProposalInfo;
  }

  private static class StrikeThroughStyler extends Styler {
    @Override
    public void applyStyles(TextStyle textStyle) {
      textStyle.strikeout = true;
    }
  }
}
