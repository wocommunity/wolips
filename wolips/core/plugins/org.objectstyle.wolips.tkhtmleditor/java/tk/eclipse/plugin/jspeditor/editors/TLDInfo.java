package tk.eclipse.plugin.jspeditor.editors;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;

import tk.eclipse.plugin.htmleditor.HTMLProjectParams;
import tk.eclipse.plugin.htmleditor.assist.TagInfo;

public class TLDInfo {
	
	private String prefix;
	private String uri;
	private String taglibUri;
	private String tagdir;
	private List<TagInfo> tagInfoList = new ArrayList<TagInfo>();
	
	/** cache results of TLD parsing */
	private static HashMap<String, TLDInfo> cache = new HashMap<String, TLDInfo>();
	
	/**
	 * This method returns empty TLDInfo.
	 * 
	 * @param file   IFile
	 * @param prefix prefix of taglib
	 * @param tagdir tagdir
	 * @return an instance of TLDInfo
	 */
	public static TLDInfo getTLDInfoFromTagdir(IFile file, String prefix,String tagdir){
		try {
			File basedir = getBaseDir(file.getProject()).getLocation().makeAbsolute().toFile();
			File folder = new File(basedir, tagdir);
			if(folder.exists() && folder.isDirectory()){
				return new TLDInfo(folder, prefix, tagdir);
			}
		} catch(Exception ex){
		}
		return null;
	}
	
	/**
	 * Creates an instance of TLDInfo.
	 * 
	 * @param file   IFile
	 * @param prefix prefix of taglib
	 * @param url    TURL of taglib
	 * @return an instance of TLDInfo
	 */
	public static TLDInfo getTLDInfo(IFile file,String prefix,String uri){
		if(cache.get(uri)!=null){
			return cache.get(uri);
		}
		try {
			return new TLDInfo(file,prefix,uri);
		} catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * 
	 * 
	 * @param prefix
	 */
	private TLDInfo(File folder, String prefix, String tagdir){
		super();
		this.prefix = prefix;
		this.tagdir = tagdir;
		this.tagInfoList = new ArrayList<TagInfo>();
		
		File[] files = folder.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].isFile() && files[i].getName().endsWith(".tag")){
				try {
					tagInfoList.add(TagFileParser.parseTagFile(prefix, files[i]));
				} catch(Exception ex){
				}
			}
		}
	}
	
	/**
	 * private constructor.
	 * 
	 * @param file   IFile
	 * @param prefix prefix of taglib
	 * @param url    URL of TLD file
	 */
	private TLDInfo(IFile file,String prefix,String uri) throws Exception {
		super();
		this.prefix = prefix;
		this.uri    = uri;
		
		IContainer basedir = getBaseDir(file.getProject());
		InputStream in = TLDLoader.get(basedir.getLocation().makeAbsolute().toFile(),uri);
		
		TLDParser parser = new TLDParser(JavaCore.create(file.getProject()), this.prefix);
		parser.parse(in);
		
		tagInfoList = parser.getResult();
		taglibUri   = parser.getUri();
		
		// add to cache
		cache.put(uri, this);
	}
	
	/**
	 * Returns the <code>IContainer</code> of the web application root folder.
	 * <p>
	 * TODO This method should be moved to the utility class.
	 * 
	 * @param project the project
	 * @return the <code>IContainer</code> of the web application root folder
	 */
	public static IContainer getBaseDir(IProject project) throws Exception {
		HTMLProjectParams params = new HTMLProjectParams(project);
		String root = params.getRoot();
		
		IContainer basedir = null;
		if(root.equals("") || root.equals("/")){
			basedir = project;
		} else {
			basedir = project.getFolder(new Path(root));
		}
		
		return basedir;
	}
	
	/**
	 * Returns tagdir that's defined by taglib directive.
	 * @return tagdir
	 */
	public String getTagdir(){
		return tagdir;
	}
	
	/**
	 * Returns prefix of taglib.
	 * @return prefix
	 */
	public String getPrefix(){
		return prefix;
	}
	
	/**
	 * Returns URI of taglib (that's defined by taglib directive).
	 * @return URI
	 */
	public String getUri(){
		return uri;
	}
	
	/**
	 * Returns URI of taglib (that's defined by &lt;uri&gt; of TLD file)
	 * @return URI
	 */
	public String getTaglibUri(){
		return taglibUri;
	}
	
	/**
	 * Returns List of all TagInfo.
	 * @return List of TagInfo
	 */
	public List<TagInfo> getTagInfo(){
		return tagInfoList;
	}
	
	@Override
  public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("[TLDInfo]");
		sb.append(" uri=").append(getUri());
		sb.append(" prefix=").append(getPrefix());
		return sb.toString();
	}
}
