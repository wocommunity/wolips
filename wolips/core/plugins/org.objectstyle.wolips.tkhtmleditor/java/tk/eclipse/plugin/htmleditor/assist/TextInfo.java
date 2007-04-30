package tk.eclipse.plugin.htmleditor.assist;

/**
 * You can use this class instead of TagInfo if you want to provide code-completion for
 * not tags in the HTML, JSP and XML editor.
 * 
 * @author Naoki Takezoe
 */
public class TextInfo extends TagInfo {
	
	private String display;
	private String text;
	private int position;
	
	public TextInfo(String text, int position){
		this(text, text, position);
	}
	
	public TextInfo(String display, String text, int position){
		super(null, false);
		this.display = display;
		this.text = text;
		this.position = position;
	}
	
	public String getDisplayString(){
		return this.display;
	}
	
	public String getText(){
		return this.text;
	}
	
	public int getPosition(){
		return this.position;
	}
	
}
