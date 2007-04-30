package tk.eclipse.plugin.xmleditor.editors;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLUtil;

/**
 * An implementation of <code>EntityResolver</code> 
 * to load DTDs or XSDs from locacl file system.
 * 
 * @author Naoki Takezoe
 */
public class DTDResolver implements EntityResolver2 {
	
//	private static HashMap namedXSDMap = new HashMap();
	private HashMap map = new HashMap();
	private IDTDResolver[] resolvers = null;
	private File basedir;
	
	public DTDResolver(IDTDResolver[] resolvers){
		this(resolvers, null);
	}
	
	public DTDResolver(IDTDResolver[] resolvers, File basedir) {
		super();
		this.resolvers = resolvers;
		this.basedir = basedir;
		
		// load from preference store
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		String[] uri  = store.getString(HTMLPlugin.PREF_DTD_URI).split("\n");
		String[] path = store.getString(HTMLPlugin.PREF_DTD_PATH).split("\n");
		for(int i=0;i<uri.length;i++){
			if(!uri[i].trim().equals("") && !path[i].trim().equals("")){
				map.put(uri[i].trim(),path[i].trim());
			}
		}
	}
	
	public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
		// nothing to do
		return null;
	}

	public InputSource resolveEntity(String name, String publicId,
			String baseURI, String systemId) throws SAXException, IOException {
		
		InputStream in = getInputStream(systemId);
		if(in!=null){
			return new InputSource(in);
		}
		return null;
	}
	/**
	 * Returns InputStream of DTD or XSD.
	 * 
	 * @param url URL
	 * @return InputStream
	 */
	public InputStream getInputStream(String url){
		
		InputStream in = null;
		
		// get from contributed IDTDResolver
		for(int i=0;i<resolvers.length;i++){
			in = resolvers[i].getInputStream(url);
			if(in!=null){
				return in;
			}
		}
		// get from preferences
		String path = (String)map.get(url);
		if(path!=null){
			File file = new File(path);
			if(file.exists() && file.isFile()){
				try {
					return new FileInputStream(file);
				} catch(Exception ex){
					HTMLPlugin.logException(ex);
				}
			}
		}
		// get from internal DTDs/XSDs
		Map innerDTD = HTMLPlugin.getInnerDTD();
		if(innerDTD.get(url)!=null){
			return getClass().getResourceAsStream((String)innerDTD.get(url));
		}
		
		// get from local file system
		if(url.indexOf(':')<0){
			in = getFileInputStream(url);
			if(in != null){
				return in;
			}
		}
		
		// retry only file name
		if(url.indexOf('/') >= 0 && (url.endsWith(".xsd") || url.endsWith(".dtd"))){
			in = getInputStream(url.substring(url.lastIndexOf('/') + 1));
			if(in!=null){
				return in;
			}
		}
		
		// get directly
		try {
			return getStreamFromURL(url);
		} catch(Exception ex){
		}
		
		return null;
	}
	
	private InputStream getFileInputStream(String path){
		File file = new File(path);
		try {
			if(file.exists() && file.isFile()){
				return new FileInputStream(file);
			}
			if(basedir != null){
				file = new File(basedir, path);
				if(file.exists() && file.isFile()){
					return new FileInputStream(file);
				}
			}
		} catch(IOException ex){
			HTMLPlugin.logException(ex);
		}
		return null;
	}
	
	private InputStream getStreamFromURL(String url){
		
		// when caching configuration is disabled
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		if(!store.getBoolean(HTMLPlugin.PREF_DTD_CACHE) || url.startsWith("file:")){
			try {
				return new URL(url).openStream();
			} catch(Exception ex){
			}
			return null;
		}
		// get file name
		String name = url.substring(url.lastIndexOf("/")+1);
		if(name.indexOf("\\")>0){
			name = name.substring(name.lastIndexOf("\\")+1);
		}
		// create cache directory
		File cacheDir = new File(Platform.getStateLocation(
				HTMLPlugin.getDefault().getBundle()).toFile(),"dtd");
		if(!cacheDir.exists() || !cacheDir.isDirectory()){
			cacheDir.mkdirs();
		}
		// use cache file if that exists
		File cacheFile = new File(cacheDir,name);
		
		try {
			// create cache file
			InputStream in = new URL(url).openStream();
			byte[] buf = HTMLUtil.readStream(in);
			FileOutputStream out = new FileOutputStream(cacheFile);
			out.write(buf);
			out.close();
			in.close();
			// register to the preference store
			String uri  = store.getString(HTMLPlugin.PREF_DTD_URI);
			String path = store.getString(HTMLPlugin.PREF_DTD_PATH);
			store.setValue(HTMLPlugin.PREF_DTD_URI  ,uri  + "\n" + url);
			store.setValue(HTMLPlugin.PREF_DTD_PATH ,path + "\n" + cacheFile.getAbsolutePath());
			HTMLPlugin.getDefault().savePluginPreferences();
			
			return new ByteArrayInputStream(buf);
			
		} catch(Exception ex){
		}
		return null;
	}
	
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if(systemId!=null){
			InputStream in = getInputStream(systemId);
			if(in!=null){
				return new InputSource(in);
			}
		}
		return null;
	}
}
