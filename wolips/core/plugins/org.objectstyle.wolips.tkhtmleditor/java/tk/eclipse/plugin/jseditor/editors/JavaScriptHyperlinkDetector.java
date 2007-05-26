package tk.eclipse.plugin.jseditor.editors;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLProjectParams;
import tk.eclipse.plugin.htmleditor.HTMLUtil;
import tk.eclipse.plugin.jseditor.launch.JavaScriptLibraryTable;

/**
 * <code>IHyperlinkDetector</code> for JavaScript.
 * <p>
 * This detector detects available functions and variables 
 * at the caret position.
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptHyperlinkDetector implements IHyperlinkDetector {

	/** Functions which are defined in the library files */
	private List<FunctionInfo> functions = new ArrayList<FunctionInfo>();
	
	/**
	 * Returns hyperlinks or null.
	 */
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		
		String source = textViewer.getDocument().get();
		int offset = region.getOffset();
		
		// extracts the word at the caret position
		String word = String.valueOf(source.charAt(offset));
		int index = offset -1;
		while(index >= 0){
			char c = source.charAt(index);
			if(Character.isJavaIdentifierPart(c)){
				word = c + word;
				index--;
			} else {
				break;
			}
		}
		index = offset + 1;
		offset = offset - word.length() + 1;
		while(source.length()>index){
			char c = source.charAt(index);
			if(Character.isJavaIdentifierPart(c)){
				word = word + c;
				index++;
			} else if(c=='('){
				break;
			} else {
				return null;
			}
		}
		
		// itselfs
		JavaScriptModel model = new JavaScriptModel(source);
		JavaScriptContext context = model.getContextFromOffset(offset);
		if(context!=null){
			JavaScriptElement[] children = context.getVisibleElements();
			for(int i=0;i<children.length;i++){
				if(children[i] instanceof JavaScriptFunction){
					JavaScriptFunction function = (JavaScriptFunction)children[i];
					if(children[i].getName().equals(word)){
						IRegion hyperlinkRegion = new Region(offset, word.length());
						return new IHyperlink[]{
								new JavaScriptHyperlink(hyperlinkRegion, textViewer, 
										function.getOffset())
						};
					}
				}
			}
		}
		
		// libraries
		for(int i=0;i<functions.size();i++){
			FunctionInfo info = functions.get(i);
			if(info.function.getName().equals(word)){
				IRegion hyperlinkRegion = new Region(offset, word.length());
				return new IHyperlink[]{
						new JavaScriptHyperlink(hyperlinkRegion, info.resource, 
								info.function.getOffset())
				};
			}
		}
		
		return null;
	}
	
	/**
	 * Updates internal informations.
	 * 
	 * @param file the editing file
	 */
	public void update(IFile file){
		try {
			functions.clear();
			
			HTMLProjectParams params = new HTMLProjectParams(file.getProject());
			String[] javaScripts = params.getJavaScripts();
			IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
			
			for(int i=0;i<javaScripts.length;i++){
				InputStream in = null;
				Object obj = null;
				if(javaScripts[i].startsWith(JavaScriptLibraryTable.PREFIX)){
					IResource resource = wsroot.findMember(javaScripts[i].substring(JavaScriptLibraryTable.PREFIX.length()));
					if(resource!=null && resource instanceof IFile && resource.exists()){
						in = ((IFile)resource).getContents();
						obj = resource;
					}
				} else {
					obj = new File(javaScripts[i]);
					in = new FileInputStream(javaScripts[i]);
				}
				String source = new String(HTMLUtil.readStream(in));
				JavaScriptModel model = new JavaScriptModel(source);
				JavaScriptElement[] elements = model.getChildren();
				
				for(int j=0;j<elements.length;j++){
					if(elements[j] instanceof JavaScriptFunction){
						FunctionInfo info = new FunctionInfo();
						info.resource = obj;
						info.function = (JavaScriptFunction)elements[j];
						functions.add(info);
					}
				}
			}
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
	}
	
	/**
	 * An internal object to stores <code>JavaScriptFunction</code> 
	 * and resource which defines them.
	 */
	private class FunctionInfo {
		private Object resource;
		private JavaScriptFunction function;
	}
	
	/**
	 * <code>IHyperlink</code> implementation to jump the target resource.
	 */
	private class JavaScriptHyperlink implements IHyperlink {
		
		private IRegion region;
		private Object resource;
		private int beginOffset;
		
		/**
		 * The constructor.
		 * 
		 * @param region the hyperlink region
		 * @param resource the target resource 
		 *   (<code>IFile</code>, <code>ITextViewer</code> or <code>java.io.File</code>)
		 * @param beginOffset the begin offset
		 */
		public JavaScriptHyperlink(IRegion region, Object resource, int beginOffset){
			this.region = region;
			this.resource = resource;
			this.beginOffset = beginOffset;
		}
		
		public IRegion getHyperlinkRegion() {
			return region;
		}

		public String getTypeLabel() {
			return null;
		}

		public String getHyperlinkText() {
			return null;
		}
		
		/**
		 * TODO <code>java.io.File</code> is unsupported.
		 */
		public void open() {
			// IFile
			if(resource instanceof IFile){
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IEditorPart editor = IDE.openEditor(page, (IFile)resource, true);
					IGotoMarker gotoMarker = (IGotoMarker)editor.getAdapter(IGotoMarker.class);
					if(gotoMarker!=null){
						IMarker marker= ((IFile)resource).createMarker(IMarker.TEXT);
						marker.setAttribute(IMarker.CHAR_START, beginOffset);
						marker.setAttribute(IMarker.CHAR_END, beginOffset);
						gotoMarker.gotoMarker(marker);
					}
				} catch (Exception ex) {
					HTMLPlugin.logException(ex);
				}
			}
			if(resource instanceof ITextViewer){
				((ITextViewer)resource).setSelectedRange(beginOffset, 0);
			}
			// java.io.File (external file)
			if(resource instanceof File){
				
			}
		}
	}

}
