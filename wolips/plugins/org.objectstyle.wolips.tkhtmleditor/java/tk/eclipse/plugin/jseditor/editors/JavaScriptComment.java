package tk.eclipse.plugin.jseditor.editors;

/**
 * The model for the JavaScript comment.
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptComment implements JavaScriptElement {
	
	private int offset;
	private int endOffset;
	private String text;
	
	public JavaScriptComment(int offset, int endOffset, String text){
		this.offset = offset;
		this.endOffset = endOffset;
		this.text = text;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public int getOffset() {
		return offset;
	}

	public String getText() {
		return text;
	}
	
	public String getName(){
		return "Comment";
	}
	
}
