package tk.eclipse.plugin.htmleditor.tasktag;

import tk.eclipse.plugin.jseditor.editors.JavaScriptComment;
import tk.eclipse.plugin.jseditor.editors.JavaScriptModel;

/**
 * {@link ITaskTagDetector} implementation for JavaScript.
 * This detector supports following extensions:
 * 
 * <ul>
 *   <li>.js</li>
 * </ul>
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptTaskTagDetector extends AbstractTaskTagDetector {
	
	public JavaScriptTaskTagDetector(){
		addSupportedExtension("js");
	}
	
	public void doDetect() throws Exception {
		JavaScriptModel model = new JavaScriptModel(this.contents);
		JavaScriptComment[] comments = model.getComments();
		for(int i=0;i<comments.length;i++){
			detectTaskTag(comments[i].getText(), comments[i].getOffset());
		}
	}
}
