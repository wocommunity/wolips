package org.objectstyle.wolips.wodclipse.editors;

import org.eclipse.jface.text.contentassist.CompletionProposal;

public class WODCompletionProposal implements Comparable {
  private String myToken;
  private int myTokenOffset;
  private int myOffset;
  private String myProposal;

  public WODCompletionProposal(String _token, int _tokenOffset, int _offset, String _proposal) {
    myToken = _token;
    myTokenOffset = _tokenOffset;
    myOffset = _offset;
    myProposal = _proposal;
  }

  public CompletionProposal toCompletionProposal() {
    CompletionProposal completionProposal = new CompletionProposal(myProposal, myTokenOffset, myToken.length(), myProposal.length());
    return completionProposal;
  }

  public boolean equals(Object _obj) {
    return (_obj instanceof WODCompletionProposal && ((WODCompletionProposal) _obj).myProposal.equals(myProposal));
  }

  public int hashCode() {
    return myProposal.hashCode();
  }

  public int compareTo(Object _obj) {
    int comparison;
    if (_obj instanceof WODCompletionProposal) {
      comparison = myProposal.compareTo(((WODCompletionProposal) _obj).myProposal);
    }
    else {
      comparison = -1;
    }
    return comparison;
  }
}
