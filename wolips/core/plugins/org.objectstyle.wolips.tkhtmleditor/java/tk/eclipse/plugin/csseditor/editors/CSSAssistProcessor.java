package tk.eclipse.plugin.csseditor.editors;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ContextInformationValidator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLUtil;

/**
 * The implementaion of IContentAssistProcessor for the CSS Editor.
 * 
 * @author Naoki Takezoe
 */
public class CSSAssistProcessor implements IContentAssistProcessor {

  public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
    List<ICompletionProposal> proposals = new LinkedList<ICompletionProposal>();
    try {
      IDocument document = viewer.getDocument();
      
      // state 0 = selector
      // state 1 = property name
      // state 2 = property value
      int colonIndex = -1;
      boolean done = false;
      int state = 0;
      for (int currentOffset = offset - 1; !done && currentOffset > 0; currentOffset--) {
        char ch = document.getChar(currentOffset);
        if (ch == '}') {
          state = 0;
          done = true;
        }
        else if (ch == ':') {
          colonIndex = currentOffset;
          state = 2;
          done = true;
        }
        else if (ch == ';') {
          state = 1;
          done = true;
        }
        else if (ch == '{') {
          state = 1;
          done = true;
        }
      }

      if (state == 0) {
        // we can't do anything right now for selector completion
      }
      else if (state == 1) {
        int lineNumber = document.getLineOfOffset(offset);
        int lineOffset = document.getLineOffset(lineNumber);
        String propertyName = document.get(lineOffset, offset - lineOffset);
        int replacementOffset;
        for (replacementOffset = 0; replacementOffset < propertyName.length(); replacementOffset++) {
          if (!Character.isWhitespace(propertyName.charAt(replacementOffset))) {
            break;
          }
        }
        String trimmedPropertyName = propertyName.trim();
        if (trimmedPropertyName.length() == 0 || !propertyName.endsWith(" ")) {
          char lastChar = document.getChar(offset - 1);
          String lowercasePropertyName = propertyName.trim().toLowerCase();
          for (CSSProperty property : CSSDefinition.PROPERTIES) {
            String displayPropertyName = property.getName();
            String replacementPropertyName = displayPropertyName + ": ";
            if (displayPropertyName.startsWith(lowercasePropertyName)) {
              proposals.add(new CompletionProposal(replacementPropertyName, lineOffset + replacementOffset, trimmedPropertyName.length(), replacementPropertyName.length(), null, displayPropertyName, null, null));
            }
          }
        }
      }
      else if (state == 2) {
        int lineNumber = document.getLineOfOffset(offset);
        int lineOffset = document.getLineOffset(lineNumber);
        String propertyName = document.get(lineOffset, colonIndex - lineOffset).trim();
        CSSProperty matchingProperty = null;
        for (CSSProperty property : CSSDefinition.PROPERTIES) {
          if (property.getName().equalsIgnoreCase(propertyName)) {
            matchingProperty = property;
          }
        }
        if (matchingProperty != null) {
          String propertyValue = document.get(colonIndex + 1, offset - colonIndex - 1);
          int replacementOffset;
          for (replacementOffset = 0; replacementOffset < propertyValue.length(); replacementOffset++) {
            if (!Character.isWhitespace(propertyValue.charAt(replacementOffset))) {
              break;
            }
          }
          String trimmedPropertyValue = propertyValue.substring(replacementOffset);
          Set<String> valueProposals = new HashSet<String>();
          matchingProperty.fillInProposals(trimmedPropertyValue, valueProposals);
          for (String valueProposal : valueProposals) {
            String displayValueProposal = valueProposal;
            String replacementValueProposal = valueProposal;
            proposals.add(new CompletionProposal(replacementValueProposal, colonIndex + replacementOffset + 1, trimmedPropertyValue.length(), replacementValueProposal.length(), null, displayValueProposal, null, null));
          }
        }
      }
      //    String text = getSource(viewer).substring(0, offset);
      //    String word = getLastWord(text);
      //
      //    ArrayList list = new ArrayList();
      //    if (word != null) {
      //      for (int i = 0; i < CSSDefinition.CSS_KEYWORDS.length; i++) {
      //        if (CSSDefinition.CSS_KEYWORDS[i].getReplaceString().startsWith(word)) {
      //          list.add(new CompletionProposal(CSSDefinition.CSS_KEYWORDS[i].getReplaceString(), offset - word.length(), word.length(), CSSDefinition.CSS_KEYWORDS[i].getReplaceString().length(), HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_CSS_PROP), CSSDefinition.CSS_KEYWORDS[i].getDisplayString(), null, null));
      //        }
      //      }
      //    }

      // sort
      HTMLUtil.sortCompilationProposal(proposals);
      //    ICompletionProposal[] prop = (ICompletionProposal[]) list.toArray(new ICompletionProposal[list.size()]);
    }
    catch (BadLocationException e) {
      HTMLPlugin.logException(e);
    }
    return proposals.toArray(new ICompletionProposal[proposals.size()]);
  }

  protected String getSource(ITextViewer viewer) {
    return viewer.getDocument().get();
  }

  private String getLastWord(String text) {

    text = HTMLUtil.cssComment2space(text);

    int index1 = text.lastIndexOf(';');
    int index2 = text.lastIndexOf('{');

    if (index1 >= 0 && index1 > index2) {
      return text.substring(index1 + 1).trim();
    }
    else if (index2 >= 0) {
      return text.substring(index2 + 1).trim();
    }

    return null;
  }

  public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
    ContextInformation[] info = new ContextInformation[0];
    return info;
  }

  public char[] getCompletionProposalAutoActivationCharacters() {
    return new char[0];
  }

  public char[] getContextInformationAutoActivationCharacters() {
    return new char[0];
  }

  public String getErrorMessage() {
    return "error";
  }

  public IContextInformationValidator getContextInformationValidator() {
    return new ContextInformationValidator(this);
  }

}
