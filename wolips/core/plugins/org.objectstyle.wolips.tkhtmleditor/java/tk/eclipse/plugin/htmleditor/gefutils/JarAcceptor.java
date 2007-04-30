package tk.eclipse.plugin.htmleditor.gefutils;

import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.jspeditor.editors.TLDInfo;

/**
 * An acceptor for each entries of <tt>WEB-INF/lib/*.jar</tt>.
 * 
 * @since 2.0.5
 * @author Naoki Takezoe
 * @see IJarVisitor
 */
public class JarAcceptor {
	
	public static Object accept(IProject project, IJarVisitor visitor){
		try {
			IContainer container = TLDInfo.getBaseDir(project);
			File basedir = container.getLocation().makeAbsolute().toFile();
			return accept(basedir, visitor);
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
		return null;
	}
	
	public static Object accept(File basedir, IJarVisitor visitor){
		try {
			File lib = new File(basedir,"/WEB-INF/lib");
			
			if(lib.exists() && lib.isDirectory()){
				File[] files = lib.listFiles();
				try {
					for(int i=0;i<files.length;i++){
						if(files[i].getName().endsWith(".jar")){
							JarFile jarFile = new JarFile(files[i]);
							Enumeration e = jarFile.entries();
							while(e.hasMoreElements()){
								JarEntry entry = (JarEntry)e.nextElement();
								Object result = visitor.visit(jarFile, entry);
								if(result != null){
									return result;
								}
							}
						}
					}
				} catch(Exception ex){
					HTMLPlugin.logException(ex);
				}
			}
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
		}
		
		return null;
	}
}
