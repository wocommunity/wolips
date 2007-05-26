package tk.eclipse.plugin.jspeditor.editors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLParser;
import jp.aonir.fuzzyxml.XPath;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLProjectParams;
import tk.eclipse.plugin.htmleditor.HTMLUtil;

/**
 * This class has informations about JSP like following:
 * <ul>
 *   <li>information about taglib directive</li>
 * </ul>
 */
public class JSPInfo {
	
	private static Map<IFile, JSPInfoCache> _jspInfoMap = new HashMap<IFile, JSPInfoCache>();
	
	private static class JSPInfoCache {
		private JSPInfo jspInfo;
		private long timestamp;
	}
	
	private ArrayList<TLDInfo> _tldInfoList  = new ArrayList<TLDInfo>();
	private FuzzyXMLDocument _doc;
	
	// Regular expressions
//	private Pattern pagePattern    = Pattern.compile("<%@\\s*page\\s+(.+?)%>",Pattern.DOTALL);
	private Pattern _taglibPattern  = Pattern.compile("<%@\\s*taglib\\s+(.+?)%>",Pattern.DOTALL);
	private Pattern _uriPattern     = Pattern.compile("uri\\s*=\\s*\"(.+?)\"");
	private Pattern _tagdirPattern  = Pattern.compile("tagdir\\s*=\\s*\"(.+?)\"");
	private Pattern _prefixPattern  = Pattern.compile("prefix\\s*=\\s*\"(.+?)\"");
	private Pattern _includePattern = Pattern.compile("<%@\\s*include\\s+(.+?)%>",Pattern.DOTALL);
	private Pattern _filePattern    = Pattern.compile("file\\s*=\\s*\"(.+?)\"");
	
	public static JSPInfo getJSPInfo(IFile file, String source){
		JSPInfoCache cache = _jspInfoMap.get(file);
		if(cache!=null){
			if(file.getLocalTimeStamp()==cache.timestamp){
				return cache.jspInfo;
			}
		}
		JSPInfo info = new JSPInfo(file, source);
		cache = new JSPInfoCache();
		cache.jspInfo = info;
		cache.timestamp = file.getLocalTimeStamp();
		_jspInfoMap.put(file, cache);
		return info;
	}
	
	/**
	 * The constructor.
	 * 
	 * @param input   IFileEditorInput
	 * @param source  JSP
	 */
	private JSPInfo(IFile file, String source){
		this(file, source, true);
	}
	
	public FuzzyXMLDocument getDocument(){
		return this._doc;
	}
	
	/**
	 * The constructor.
	 * 
	 * @param input   IFileEditorInput
	 * @param source  JSP
	 * @param include use include configuration of web.xml
	 */
	private JSPInfo(IFile file, String source, boolean include) {
		super();
		
		try {
			// load project preference
			HTMLProjectParams params = new HTMLProjectParams(file.getProject());
			String webapproot = params.getRoot();
		
			// processing for include direcive
			IContainer basedir = file.getProject();
			if (!webapproot.equals("") && !webapproot.equals("/")) {
				basedir = basedir.getFolder(new Path(webapproot));
			}

			Matcher matcher = _includePattern.matcher(source);
			while (matcher.find()) {
				String content = matcher.group(1);
				String fileInc = getAttribute(content, _filePattern);
				if(fileInc==null){
					continue;
				}
//				taglibInsertIndex = matcher.end();

				// Lecture du fichier inclus puis extraction de ses TLDs
				IFile incJspFile = null;
				if(fileInc.startsWith("/")){
					incJspFile = basedir.getFile(new Path(fileInc));
				} else {
					incJspFile = file.getParent().getFile(new Path(fileInc));
				}
				try {
					if(incJspFile!=null && incJspFile.exists()){
						String contents = new String(HTMLUtil.readStream(incJspFile.getContents()));
						JSPInfo info = new JSPInfo(incJspFile, contents);
						TLDInfo[] tldInfos = info.getTLDInfo();
						for (int i = 0; i < tldInfos.length; i++) {
							_tldInfoList.add(tldInfos[i]);
						}
					}
				} catch (IOException ioe) {
					HTMLPlugin.logException(ioe);
				} catch (CoreException ce) {
					HTMLPlugin.logException(ce);
				}
			}
		
			// getting page directove
//			matcher = pagePattern.matcher(source);
//			while(matcher.find()){
//				taglibInsertIndex = matcher.end();
//			}
			// getting taglib directive
			matcher = _taglibPattern.matcher(source);
			while(matcher.find()){
				// parsing taglib directive
				String content = matcher.group(1);
				String tagdir = getAttribute(content, _tagdirPattern);
				if(tagdir != null){
					String prefix = getAttribute(content,_prefixPattern);
					TLDInfo info = TLDInfo.getTLDInfoFromTagdir(file,prefix,tagdir);
					if(info!=null){
						_tldInfoList.add(info);
					}
				} else {
					String uri    = getAttribute(content,_uriPattern);
					String prefix = getAttribute(content,_prefixPattern);
					// creation TLDInfo
//					taglibInsertIndex = matcher.end();
					TLDInfo info = TLDInfo.getTLDInfo(file,prefix,uri);
					if(info!=null){
						_tldInfoList.add(info);
					}
				}
			}
			
			// getting TLDs from xmlns
			try {
				this._doc = new FuzzyXMLParser().parse(HTMLUtil.scriptlet2space(source,false));
				FuzzyXMLElement root = (FuzzyXMLElement)XPath.selectSingleNode(_doc.getDocumentElement(),"*");
				if(root!=null){
					FuzzyXMLAttribute[] attrs = root.getAttributes();
					for(int i=0;i<attrs.length;i++){
						if(attrs[i].getName().startsWith("xmlns:")){
							String[] dim = attrs[i].getName().split(":");
							if(dim.length > 1){
								TLDInfo info = null;
								String value = attrs[i].getValue();
								if(value.startsWith("urn:jsptagdir:")){
									value = value.replaceFirst("^urn:jsptagdir:", "");
									info = TLDInfo.getTLDInfoFromTagdir(file,dim[1],value);
									
								} else if(value.startsWith("urn:jsptld:")){
									value = value.replaceFirst("^urn:jsptld:", "");
									info = TLDInfo.getTLDInfo(file,dim[1],value);
									
								} else {
									info = TLDInfo.getTLDInfo(file,dim[1],value);
								}
								if(info!=null){
									_tldInfoList.add(info);
								}
							}
						}
					}
				}
			} catch(Exception ex){
				HTMLPlugin.logException(ex);
			}
			
			// getting TLDs from included JSP defined in web.xml
			try {
				if(include){
					IPath path = new Path(webapproot).append("/WEB-INF/web.xml");
					IFile webXML = file.getProject().getFile(path);
					if(webXML!=null && webXML.exists()){
						FuzzyXMLDocument doc = new FuzzyXMLParser().parse(webXML.getContents());
						FuzzyXMLNode[] nodes = HTMLUtil.selectXPathNodes(doc.getDocumentElement(),
							"/web-app/jsp-config/jsp-property-group[url-pattern='*.jsp']");
						for(int i=0;i<nodes.length;i++){
							FuzzyXMLNode[] includes = HTMLUtil.selectXPathNodes((FuzzyXMLElement)nodes[i],"/include-prelude|/include-coda");
							for(int j=0;j<includes.length;j++){
								IFile incFile = basedir.getFile(new Path(((FuzzyXMLElement)includes[j]).getValue()));
								if(incFile!=null && incFile.exists()){
									String contents = new String(HTMLUtil.readStream(incFile.getContents()));
									JSPInfo info = new JSPInfo(incFile, contents, false);
									TLDInfo[] tldInfos = info.getTLDInfo();
									for (int k = 0; k < tldInfos.length; k++) {
										_tldInfoList.add(tldInfos[k]);
									}
								}
							}
						}
					}
				}
			} catch(Exception ex){
				HTMLPlugin.logException(ex);
			}
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
		
	}
	
	private String getAttribute(String source,Pattern pattern){
		Matcher matcher = pattern.matcher(source);
		if(matcher.find()){
			return matcher.group(1);
		}
		return null;
	}
	
//	public void addTaglibDirective(IDocument doc,String prefix,String uri){
//		try {
//			doc.replace(taglibInsertIndex,0,"\n<%@ taglib uri=\""+uri+"\" prefix=\""+prefix+"\" %>");
//		} catch(Exception ex){
//			HTMLPlugin.logException(ex);
//		}
//	}
	
	public String getTaglibUri(String prefix){
		String uri = null;
		TLDInfo[] tlds = getTLDInfo();
		for(int i=0;i<tlds.length;i++){
			if(tlds[i].getPrefix().equals(prefix)){
				uri = tlds[i].getTaglibUri();
				break;
			}
		}
		return uri;
	}
	
	/**
	 * Returns an array of TLDInfo.
	 * 
	 * @return an array of TLDInfo.
	 */
	public TLDInfo[] getTLDInfo(){
		return _tldInfoList.toArray(new TLDInfo[_tldInfoList.size()]);
	}
	
}
