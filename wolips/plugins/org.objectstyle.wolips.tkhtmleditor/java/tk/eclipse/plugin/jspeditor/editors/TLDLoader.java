package tk.eclipse.plugin.jspeditor.editors;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLParser;
import jp.aonir.fuzzyxml.XPath;

import org.eclipse.jface.preference.IPreferenceStore;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLUtil;
import tk.eclipse.plugin.htmleditor.gefutils.IJarVisitor;
import tk.eclipse.plugin.htmleditor.gefutils.JarAcceptor;

/**
 * Provides one static method to get TLD files.
 */
public class TLDLoader {
	
	private static HashMap<String, byte[]> cache = new HashMap<String, byte[]>();
	
	/**
	 * Returns the TLD file as <code>InputStream</code>.
	 * <p>
	 * If the URL starts <code>http://</code>, this method tries to
	 * get the TLD file from the remote server.
	 * Otherwise, tries to get from the local path using given URI as
	 * the relative path from the base directory.
	 * 
	 * @param basedir the base directory
	 * @param uri the URI of the TLD file
	 * @return <code>InputStream</code> of the TLD file
	 */
	public static InputStream get(File basedir,String uri) throws Exception {
		if(cache.get(uri)==null){
			// Default internal TLDs
			Map innerTLD = HTMLPlugin.getInnerTLD();
			if(innerTLD.get(uri)!=null){
				InputStream in = TLDLoader.class.getResourceAsStream((String)innerTLD.get(uri));
				byte[] bytes = HTMLUtil.readStream(in);
				cache.put(uri,bytes);
				return new ByteArrayInputStream(bytes);
			}
			// Contributed TLDs
			ITLDLocator[] locators = HTMLPlugin.getDefault().getTLDLocatorContributions();
			for ( int i = 0; i < locators.length; i++) {
				InputStream in = locators[i].locateTLD(uri);
				if ( in != null) {
					byte[] bytes = HTMLUtil.readStream(in);
					cache.put(uri,bytes);
					return new ByteArrayInputStream(bytes);
				}
			}
			// from PreferenceStore
			Map pref = getPreferenceTLD();
			if(pref.get(uri)!=null){
				InputStream in = new FileInputStream(new File((String)pref.get(uri)));
				byte[] bytes = HTMLUtil.readStream(in);
				cache.put(uri,bytes);
				return new ByteArrayInputStream(bytes);
			}
			// Check web.xml
			byte[] bytes = getTLDFromWebXML(basedir,uri);
			if(bytes!=null){
				cache.put(uri,bytes);
				return new ByteArrayInputStream(bytes);
			}
			if(uri.startsWith("http://") || uri.startsWith("https://")){
				// Search META-INF in jar files
				bytes = getTLDFromJars(basedir,uri);
				if(bytes!=null){
					cache.put(uri,bytes);
					return new ByteArrayInputStream(bytes);
				}
				// from the URL
				URL url = new URL(uri);
				InputStream in = url.openStream();
				cache.put(uri,HTMLUtil.readStream(in));
			} else {
				// from the local file
				File file = new File(basedir,uri);
				InputStream in = new FileInputStream(file);
				cache.put(uri,HTMLUtil.readStream(in));
			}
		}
		
		byte[] bytes = cache.get(uri);
		return new ByteArrayInputStream(bytes);
	}
	
	/** Load configurations from <code>IPreferenceStore</code>. */
	private static Map getPreferenceTLD(){
		HashMap<String, String> map = new HashMap<String, String>();
		
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		String[] uri  = store.getString(HTMLPlugin.PREF_TLD_URI).split("\n");
		String[] path = store.getString(HTMLPlugin.PREF_TLD_PATH).split("\n");
		for(int i=0;i<uri.length;i++){
			if(!uri[i].trim().equals("") && !path[i].trim().equals("")){
				map.put(uri[i].trim(),path[i].trim());
			}
		}
		
		return map;
	}
	
	/** Load from web.xml */
	private static byte[] getTLDFromWebXML(File basedir,String uri){
		File webXML = new File(basedir,"/WEB-INF/web.xml");
		
		if(webXML.exists() && webXML.isFile()){
			try {
				FuzzyXMLDocument doc = new FuzzyXMLParser(false).parse(new FileInputStream(webXML));
				FuzzyXMLNode[] nodes = XPath.selectNodes(doc.getDocumentElement(),"/web-app/taglib|/web-app/jsp-config/taglib");
				
				for(int i=0;i<nodes.length;i++){
					FuzzyXMLElement element = (FuzzyXMLElement)nodes[i];
					String taglibUri = HTMLUtil.getXPathValue(element,"/taglib-uri/child::text()");
					String taglibLoc = HTMLUtil.getXPathValue(element,"/taglib-location/child::text()");
					if(uri.equals(taglibUri)){
						if(taglibLoc!=null && taglibLoc.endsWith(".tld")){
							File file = new File(basedir,taglibLoc);
							return HTMLUtil.readStream(new FileInputStream(file));
						}
						break;
					}
				}
			} catch(Exception ex){
				HTMLPlugin.logException(ex);
			}
		}
		return null;
	}
	
	/** Load from META-INF in the jar file */
	private static byte[] getTLDFromJars(File basedir,final String uri){
		return (byte[])JarAcceptor.accept(basedir, new IJarVisitor(){
			public Object visit(JarFile file, JarEntry entry) throws Exception {
				if(entry.getName().endsWith(".tld")){
					byte[] bytes = HTMLUtil.readStream(file.getInputStream(entry));
					try {
						FuzzyXMLDocument doc = new FuzzyXMLParser(false).parse(new ByteArrayInputStream(bytes));
						String nodeURI = HTMLUtil.getXPathValue(doc.getDocumentElement(),"/taglib/uri/child::text()");
						if(nodeURI!=null && uri.equals(nodeURI)){
							return bytes;
						}
					} catch(Exception ex){
						HTMLPlugin.logException(ex);
					}
				}
				return null;
			}
		});
	}
}
