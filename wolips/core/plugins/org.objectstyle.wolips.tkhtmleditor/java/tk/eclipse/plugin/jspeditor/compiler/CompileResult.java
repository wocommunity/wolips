package tk.eclipse.plugin.jspeditor.compiler;

/**
 * 
 * 
 * @author Naoki Takezoe
 * @since 2.0.3
 * @see JSPCompiler
 */
public class CompileResult {
	
	private String header;
	private String body;
	private String footer;
	
	public CompileResult(String header, String body, String footer){
		this.header = header;
		this.body = body;
		this.footer = footer;
	}
	
	public String getHeader(){
		return this.header;
	}
	
	public String getBody(){
		return this.body;
	}
	
	public String getFooter(){
		return this.footer;
	}
	
	public String toString(){
		return header + body + footer;
	}
	
}
