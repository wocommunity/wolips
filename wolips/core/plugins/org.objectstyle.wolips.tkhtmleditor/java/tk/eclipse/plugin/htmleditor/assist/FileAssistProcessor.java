package tk.eclipse.plugin.htmleditor.assist;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.Image;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.IFileAssistProcessor;

/**
 * This provides code completion for attributes which specify files on the server
 * such as &lt;a href=&quot;...&quot;&gt; or &lt;img src=&quot;&quot;&gt;.
 */
public class FileAssistProcessor implements IFileAssistProcessor {
	
	private IFile file;
	
	public void reload(IFile file){
		this.file = file;
	}
	
	public AssistInfo[] getAssistInfo(String value){
		
		IPath  path   = null;
		String parent = null;;
		
		// if value starts with '/', don't completion.
		if(value.startsWith("/")){
			return new AssistInfo[0];
//			try {
//				HTMLProjectParams params = new HTMLProjectParams(file.getProject());
//				path = new Path(params.getRoot());
//			} catch(Exception ex){
//				HTMLPlugin.logException(ex);
//			}
		}
		// if value doesn't start with '/', process as a relative path from a file
		if(path==null){
			path = file.getParent().getProjectRelativePath();
		}
		
		// create path of parent folder
		int index = value.lastIndexOf('/');
		if(index >= 0){
			path   = path.append(value.substring(0,index));
			parent = value.substring(0,index) + "/";
		} else{
			parent = "";
		}
		IResource resource = file.getProject().findMember(path);
		if(resource!=null && resource.exists() && resource instanceof IContainer){
			try {
				IContainer container = (IContainer)resource;
				IResource[] children = container.members();
				ArrayList list = new ArrayList();
				for(int i=0;i<children.length;i++){
					// ignore dot files.
					if(children[i].getName().startsWith(".")){
						continue;
					}
					// ignore WEB-INF and children.
					if(children[i].getName().equals("WEB-INF") || parent.indexOf("WEB-INF")>=0){
						continue;
					}
					Image image = null;
					if(children[i] instanceof IContainer){
						image = HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_FOLDER);
					} else {
						image = getFileImage(children[i].getName().toLowerCase());
					}
					
					list.add(new AssistInfo(
							parent + children[i].getName(),
							children[i].getName(),
							image
					));
				}
				return (AssistInfo[])list.toArray(new AssistInfo[list.size()]);
			} catch(Exception ex){
				HTMLPlugin.logException(ex);
			}
		}
		return new AssistInfo[0];
	}
	
	
	private Image getFileImage(String name){
		if(name.endsWith(".html") || name.endsWith(".htm") || name.endsWith(".shtml")){
			return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_HTML);
		}
		if(name.endsWith(".xml") || name.endsWith(".xhtml") || name.equals(".tld")){
			return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_XML);
		}
		if(name.endsWith(".jsp")){
			return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_JSP);
		}
		if(name.endsWith(".css")){
			return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_CSS);
		}
		for(int i=0;i<HTMLPlugin.SUPPORTED_IMAGE_TYPES.length;i++){
			if(name.endsWith("." + HTMLPlugin.SUPPORTED_IMAGE_TYPES[i])){
				return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_WEB);
			}
		}
		return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_FILE);
	}
}
