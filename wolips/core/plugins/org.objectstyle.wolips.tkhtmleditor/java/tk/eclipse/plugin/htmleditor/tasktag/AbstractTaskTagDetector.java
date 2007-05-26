package tk.eclipse.plugin.htmleditor.tasktag;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

import tk.eclipse.plugin.htmleditor.HTMLUtil;

/**
 * The base class for the {@link tk.eclipse.plugin.htmleditor.tasktag.ITaskTagDetector}
 * implementations.
 * This class provides the base implementation and some utility methods for subclasses.
 * 
 * @author Naoki Takezoe
 */
public abstract class AbstractTaskTagDetector implements ITaskTagDetector {

	protected String _contents;
	protected IFile _file;
	protected TaskTag[] _tags;
	private List<String> _extensions = new ArrayList<String>();
	
	/**
	 * Adds supported file extensions.
	 * 
	 * @param ext the file extension (dot isn't required)
	 */
	protected void addSupportedExtension(String ext){
		_extensions.add(ext);
	}
	
	public boolean isSupported(IFile file) {
		String fileName = file.getName();
		for(int i=0;i<_extensions.size();i++){
			String ext = _extensions.get(i);
			if(fileName.endsWith("." + ext)){
				return true;
			}
		}
		return false;
	}

	public void detect(IFile file, TaskTag[] tags) throws Exception {
		this._file = file;
		this._tags = tags;
		
		this._contents = new String(
				HTMLUtil.readStream(file.getContents()), 
				file.getCharset());
		
		this._contents = this._contents.replaceAll("\r\n", "\n");
		this._contents = this._contents.replaceAll("\r", "\n");
		
		doDetect();
	}
	
	/**
	 * Please implement this method in the subclass.
	 * 
	 * @throws Exception
	 */
	protected abstract void doDetect() throws Exception;
	
	
	protected void detectTaskTag(String value, int offset){
		String[] lines = value.split("\n");
		for(int i=0;i<lines.length;i++){
			tags : for(int j=0;j<_tags.length;j++){
				int index = lines[i].indexOf(_tags[j].getTag());
				if(index >= 0){
					HTMLUtil.addTaskMarker(_file, IMarker.PRIORITY_NORMAL, 
							getLineAtOffset(offset) + i,
							lines[i].substring(index));
					break tags;
				}
			}
		}
	}
	
	private int getLineAtOffset(int offset){
		String text = this._contents.substring(0,offset);
		int line  = 0;
		int index = 0;
		while((index = text.indexOf('\n', index))>=0){
			line++;
			index++;
		}
		return line + 1;
	}	
}
