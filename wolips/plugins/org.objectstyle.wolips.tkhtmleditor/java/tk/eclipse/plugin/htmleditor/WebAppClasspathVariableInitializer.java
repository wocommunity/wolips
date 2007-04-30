package tk.eclipse.plugin.htmleditor;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathVariableInitializer;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

/**
 * Provides the classpath variable <code>WEBAPP_LIBS</code>.
 * <p>
 * This variable points tk.eclipse.plugin.htmleditor/lib.
 * This folder has following jar files:
 * <ul>
 *   <li>servlet-api.jar</li>
 *   <li>jsp-api.jar</li>
 * </ul>
 * 
 * @author Naoki Takezoe
 * @since 2.0.3
 */
public class WebAppClasspathVariableInitializer extends
		ClasspathVariableInitializer {
	
	public void initialize(String variable) {
		Bundle bundle = HTMLPlugin.getDefault().getBundle();
		if(bundle==null){
			JavaCore.removeClasspathVariable(variable, null);
			return;
		}
		URL installLocation = bundle.getEntry("/");
		URL local = null;
		try {
			local = FileLocator.toFileURL(installLocation);
		} catch(IOException e){
			JavaCore.removeClasspathVariable(variable, null);
			return;
		}
		try {
			String fullpath = new File(local.getPath(), "lib").getAbsolutePath();
			JavaCore.setClasspathVariable(variable, new Path(fullpath), null);
		} catch(JavaModelException e){
			JavaCore.removeClasspathVariable(variable, null);
		}
	}

}
