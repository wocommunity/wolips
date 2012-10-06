package tk.eclipse.plugin.htmleditor.assist;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

public class AssistInfo {
	
	private String displayString;
	private String replaceString;
	private Image image;
  private int _offset;
	private boolean _deprecated;
	
	public AssistInfo(String displayString){
		this.displayString = displayString;
		this.replaceString = displayString;
	}

	public AssistInfo(String displayString,Image image){
		this.displayString = displayString;
		this.replaceString = displayString;
		this.image = image;
	}
	
	public AssistInfo(String replaceString,String displayString){
		this.displayString = displayString;
		this.replaceString = replaceString;
	}
	
	public AssistInfo(String replaceString,String displayString,Image image){
		this.displayString = displayString;
		this.replaceString = replaceString;
		this.image = image;
	}
	
	public String getDisplayString() {
		return displayString;
	}
	
	public String getReplaceString() {
		return replaceString;
	}
	
	public Image getImage(){
		return this.image;
	}
  
  public void setOffset(int offset) {
    _offset = offset;
  }
  
  public int getOffset() {
    return _offset;
  }

  	public void setDeprecated(boolean deprecated) {
  		_deprecated = deprecated;
  	}
  	
  	public boolean getDeprecated() {
  		return _deprecated;
  	}
	
	public ICompletionProposal toCompletionProposal(int offset, String matchString, Image defaultImage){
		if (_deprecated) {
			return new HTMLDeprecatedCompletionProposal(
				getReplaceString(),
				_offset + offset - matchString.length(), matchString.length() - _offset,
				getReplaceString().length(),
				getImage()==null ? defaultImage : getImage(),
				getDisplayString(), null, null);
		}
		return new CompletionProposal(
				getReplaceString(),
				_offset + offset - matchString.length(), matchString.length() - _offset,
				getReplaceString().length(),
				getImage()==null ? defaultImage : getImage(),
				getDisplayString(), null, null);
	}
}
