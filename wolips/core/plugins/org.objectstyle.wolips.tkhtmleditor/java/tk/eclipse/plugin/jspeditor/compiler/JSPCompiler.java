package tk.eclipse.plugin.jspeditor.compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This compiler converts the JSP source code to the Java code
 * to make code completion in the scriptlet / expression.
 * <p>
 * <strong>Note:</strong> <code>compile()</code> is not thread safe.
 * 
 * @author Naoki Takezoe
 * @since 2.0.3
 */
public class JSPCompiler {
	
	private static final int NONE    = 0;
	private static final int LT      = 1;
	private static final int PARCENT = 2;
	private static final int SCRIPT  = 3;
	private static final int END     = 4;
	
	private static final Pattern IMPORT_PATTERN 
		= Pattern.compile("<%@\\s*page.+?import\\s*=\\s*\"(.+?)\"");
	
	private static final Pattern USEBEAN_PATTERN 
		= Pattern.compile("<jsp:useBean.+?>", Pattern.DOTALL);
	
	/**
	 * Do compile. This method is not thread safe.
	 * 
	 * @param source the raw JSP source code
	 * @return the compile result
	 */
	public static CompileResult compile(String source){
		
		StringBuffer header = new StringBuffer();
		Matcher matcher = IMPORT_PATTERN.matcher(source);
		while(matcher.find()){
			String[] imports = matcher.group(1).split(",");
			for(int i=0;i<imports.length;i++){
				String trimedImport = imports[i].trim();
				if(trimedImport.length()!=0){
					header.append("import ").append(trimedImport).append(";\n");
				}
			}
		}
		header.append("public class _xxx {\n");
		header.append("public static void doService() throws Exception {\n");
		// implicit objects
		header.append("javax.servlet.http.HttpServletRequest request = null;\n");
		header.append("javax.servlet.http.HttpServletResponse response = null;\n");
		header.append("javax.servlet.http.HttpServletSession session = null;\n");
		header.append("javax.servlet.jsp.JspWriter out = null;\n");
		header.append("javax.servlet.ServletContext application = null;\n");
		header.append("javax.servlet.ServletConfig config = null;\n");
		header.append("javax.servlet.jsp.HttpJspPage page = null;\n");
		header.append("javax.servlet.jsp.PageContext pageContext = null;\n");
		header.append("java.lang.Exception exception = null;\n");
		// jsp:useBean
		matcher = USEBEAN_PATTERN.matcher(source);
		while(matcher.find()){
			String useBean = matcher.group();
			String id   = getAttibuteValue(useBean, "id");
			String type = getAttibuteValue(useBean, "class");
			if(id!=null && type!=null){
				header.append(type + " " + id + " = null;\n");
			}
		}
		
		StringBuffer sb = new StringBuffer();
		int flag = NONE;
		for(int i=0;i<source.length();i++){
			char c = source.charAt(i);
			if(flag==NONE && c=='<'){
				flag = LT;
				sb.append(' ');
			} else if(flag==LT){
				if(c=='%'){
					flag = PARCENT;
				} else {
					flag = NONE;
				}
				sb.append(' ');
			} else if(flag==PARCENT){
				if(c=='@'){
					flag = NONE;
					sb.append(' ');
				} else if(c=='='){
					flag = SCRIPT;
					sb.append(' ');
				} else {
					flag = SCRIPT;
					sb.append(c);
				}
			} else if(flag==SCRIPT){
				if(c=='%'){
					flag = END;
				} else {
					sb.append(c);
				}
			} else if(flag==END){
				if(c=='>'){
					flag = NONE;
					sb.append("  ");
				} else {
					flag = SCRIPT;
					sb.append('%');
					sb.append(c);
				}
			} else if(c=='\n' || c=='\r' || c=='\t'){
				sb.append(c);
			} else {
				sb.append(' ');
			}
		}
		
		return new CompileResult(header.toString(), sb.toString(), "}}");
	}
	
	private static String getAttibuteValue(String source, String name){
		Pattern pattern = Pattern.compile(
				"\\s+" + name + "\\s*?=\\s*?\"(.+?)\"", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(source);
		if(matcher.find()){
			return matcher.group(1);
		}
		return null;
	}
	
}
