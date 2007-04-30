package tk.eclipse.plugin.jseditor.editors;

import tk.eclipse.plugin.htmleditor.HTMLUtil;

/**
 * Provides utility methods about JavaScript.
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptUtil {
	
	public static String removeComments(String source){
		source = HTMLUtil.cssComment2space(source);
		
		int index = 0;
		int last  = 0;
		
		StringBuffer sb = new StringBuffer();
		while((index = source.indexOf("//",last))!=-1){
			int end1 = source.indexOf("\n",index);
			int end2 = source.indexOf("\r",index);
			if(end1 > end2){
				end1 = end2;
			}
			if(end1 != -1){
				sb.append(source.substring(last,index));
				int length = end1 - index + 2;
				for(int i=0;i<length;i++){
					sb.append(" ");
				}
			} else {
				break;
			}
			last = end1 + 1;
		}
		if(last != source.length()-1){
			sb.append(source.substring(last));
		}
		return sb.toString();

	}
	
}
