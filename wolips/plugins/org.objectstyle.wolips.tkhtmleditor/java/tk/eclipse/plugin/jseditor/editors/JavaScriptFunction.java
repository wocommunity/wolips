package tk.eclipse.plugin.jseditor.editors;

import java.util.ArrayList;
import java.util.List;

/**
 * The model for the JavaScript function.
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptFunction implements JavaScriptElement, JavaScriptContext {
	
	private String name;
	private String arguments;
	private int offset;
	private int end;
	private List children = new ArrayList();
	private JavaScriptContext parent;
	
	public JavaScriptFunction(String name, String arguments, int offset){
		this.name = name;
		this.arguments = arguments;
		this.offset = offset;
	}
	
	public String getArguments() {
		return arguments;
	}
	
	public String getName() {
		return name;
	}
	
	public int getOffset(){
		return offset;
	}
	
	public int getStartOffset(){
		return getOffset();
	}
	
	public void setEndOffset(int end){
		this.end = end;
	}
	
	public int getEndOffset(){
		return end;
	}
	
	public void add(JavaScriptFunction func){
		this.children.add(func);
	}
	
	public void add(JavaScriptVariable var){
		this.children.add(var);
	}
	
	public JavaScriptElement[] getChildren(){
		return (JavaScriptElement[])this.children.toArray(new JavaScriptElement[this.children.size()]);
	}
	
	public JavaScriptElement[] getVisibleElements(){
		List list = new ArrayList();
		JavaScriptContext context = this;
		while(true){
			JavaScriptElement[] children = context.getChildren();
			for(int i=0;i<children.length;i++){
				list.add(children[i]);
			}
			
			if(context.getParent()==null){
				break;
			} else {
				context = context.getParent();
			}
		}
		return (JavaScriptElement[])list.toArray(new JavaScriptElement[list.size()]);
	}
	
	public void setParent(JavaScriptContext context){
		this.parent = context;
	}
	
	public JavaScriptContext getParent(){
		return parent;
	}

	
	public String toString(){
		return name + "(" + arguments + ")";
	}
}
