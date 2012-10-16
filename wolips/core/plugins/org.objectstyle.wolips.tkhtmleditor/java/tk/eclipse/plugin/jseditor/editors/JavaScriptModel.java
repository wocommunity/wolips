package tk.eclipse.plugin.jseditor.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptModel implements JavaScriptContext {
	
	private List<JavaScriptElement> _children = new ArrayList<JavaScriptElement>();
	private List<JavaScriptComment> _comments = new ArrayList<JavaScriptComment>();
	private JavaScriptContext _context;
	private int _endOffset;
	
	public JavaScriptModel(String source){
		update(source);
	}
	
	public int getStartOffset(){
		return 0;
	}
	
	public int getEndOffset(){
		return _endOffset;
	}
	
	public JavaScriptContext getContextFromOffset(int offset){
		return getContextFromOffset(this, offset);
	}
	
	private JavaScriptContext getContextFromOffset(JavaScriptContext context, int offset){
		if(context.getStartOffset() < offset && context.getEndOffset() > offset){
			JavaScriptElement[] children = context.getChildren();
			for(int i=0;i<children.length;i++){
				if(children[i] instanceof JavaScriptContext){
					JavaScriptContext result = getContextFromOffset((JavaScriptContext)children[i], offset);
					if(result!=null){
						return result;
					}
				}
			}
			return context;
		}
		return this;
	}
	
	/**
	 * Updates model structure by the specified source code.
	 * 
	 * @param source JavaScript
	 */
	public void update(String source){
		this._children.clear();
		this._comments.clear();
		this._endOffset = source.length();
		_context = this;
		
		boolean whitespace = true;
		char quote = 0;
		boolean escape = false;
		
		for(int i=0;i<source.length();i++){
			char c = source.charAt(i);
			// String literal
			if(c=='"' || c=='\''){
				if(!escape){
					quote = (quote == c ? 0 : c);
				}
				escape = false;
				continue;
			}
			if(quote != 0){
				escape = (c=='\\');
				continue;
			}
			// skip comment
			if(c=='/' && source.length() > i+1){
				char nc = source.charAt(i+1);
				if(nc == '/'){
					int start = i;
					while(nc!='\r' && nc!='\n' && source.length() > i){
						i++;
						nc = source.charAt(i+1);
					}
					_comments.add(new JavaScriptComment(start, i+1,
							source.substring(start, i+1)));
				}
				if(nc == '*'){
					int start = i;
					i = source.indexOf("*/", i);
					if(i==-1){
						break;
					}
					_comments.add(new JavaScriptComment(start, i+2,
							source.substring(start, i+2)));
				}
			}
			// var
			if(whitespace && c=='v'){
				int result = parseVariable(source, i, _context);
				if(result!=0){
					whitespace = true;
					i += result;
					continue;
				}
			}
			// function
			if(whitespace && c=='f'){
				Object[] result = parseFunction(source, i, _context);
				if(result != null){
					whitespace = true;
					i += ((Integer)result[0]).intValue();
					_context = (JavaScriptFunction)result[1];
					continue;
				}
			}
			// end function
			if(c=='}'){
				if(_context.getParent()!=null){
					if(_context instanceof JavaScriptFunction){
						((JavaScriptFunction)_context).setEndOffset(i);
					}
					_context = _context.getParent();
				}
			}
			// whitespace
			if(c==' ' || c=='\t' || c=='\r' || c=='\n'){
				whitespace = true;
			} else {
				whitespace = false;
			}
		}
	}
	
	private static int parseVariable(String source, int position, JavaScriptContext context){
		Pattern pattern = Pattern.compile("var[\\s\r\n]+(.+?)[\\s\r\n]*?[;=]");
		if(source.indexOf("var", position) == position){
			Matcher matcher = pattern.matcher(source.substring(position));
			if(matcher.find() && matcher.start()==0){
				JavaScriptVariable var = new JavaScriptVariable(matcher.group(1), position);
				context.add(var);
				return matcher.end();
			}
		}
		return 0;
	}
	
	private static Object[] parseFunction(String source, int position, JavaScriptContext context){
		Pattern pattern = Pattern.compile("function[\\s\r\n]+?(.+?)[\\s\r\n]*?\\((.*?)\\)[\\s\r\n]*?\\{", Pattern.DOTALL);
		if(source.indexOf("function", position) == position){
			Matcher matcher = pattern.matcher(source.substring(position));
			if(matcher.find() && matcher.start()==0){
				String args = matcher.group(2).replaceAll("[\\s\r\n]*,[\\s\r\n]*",", ").trim();
				JavaScriptFunction func = new JavaScriptFunction(matcher.group(1), args, position);
				func.setParent(context);
				context.add(func);
				return new Object[]{Integer.valueOf(matcher.end()), func};
			}
		}
		return null;
	}

	
	public void add(JavaScriptFunction func) {
		this._children.add(func);
	}
	
	public void add(JavaScriptVariable var) {
		this._children.add(var);
	}
	
	public JavaScriptElement[] getChildren(){
		return this._children.toArray(new JavaScriptElement[this._children.size()]);
	}
	
	public JavaScriptElement[] getVisibleElements(){
		return getChildren();
	}
	
	public JavaScriptContext getParent(){
		return null;
	}
	
	public JavaScriptComment[] getComments(){
		return this._comments.toArray(new JavaScriptComment[this._comments.size()]);
	}
	
}
