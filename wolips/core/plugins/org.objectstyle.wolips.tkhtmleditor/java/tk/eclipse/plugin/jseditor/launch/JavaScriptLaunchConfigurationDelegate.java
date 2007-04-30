package tk.eclipse.plugin.jseditor.launch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

/**
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate {
	
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		IVMInstall install = getVMInstall(configuration);
		IVMRunner runner = install.getVMRunner(launch.getLaunchMode());
		if (runner == null) {
			abort("VM not found", null, IJavaLaunchConfigurationConstants.ERR_VM_RUNNER_DOES_NOT_EXIST);
		}
		
		//int port= SocketUtil.findFreePort();
		VMRunnerConfiguration runConfig = launchTypes(configuration, mode);
		setDefaultSourceLocator(launch, configuration);
		
		runner.run(runConfig, launch, monitor);
	}
	
	protected VMRunnerConfiguration launchTypes(ILaunchConfiguration configuration, String mode) throws CoreException {
		
		File workingDir = verifyWorkingDirectory(configuration);
		String workingDirName = null;
		if (workingDir != null) 
			workingDirName = workingDir.getAbsolutePath();
		
		// Program & VM args
		String vmArgs = getVMArguments(configuration);
		ExecutionArguments execArgs = new ExecutionArguments(vmArgs, "");
		String[] envp = DebugPlugin.getDefault().getLaunchManager().getEnvironment(configuration);
		
		// copy jar files which are required to execute JavaScript
		JavaScriptLaunchUtil.copyLibraries();
		
		VMRunnerConfiguration runConfig = new VMRunnerConfiguration(
				JavaScriptLaunchConstants.JAVASCRIPT_EXECUTOR,
				JavaScriptLaunchUtil.getClassPathAsStringArray());
		
		List args = new ArrayList();
		
		String script = configuration.getAttribute(
				JavaScriptLaunchConstants.ATTR_JAVASCRIPT_FILE, "");
		args.add(fixArgument(script));
		
		List includes = configuration.getAttribute(
				JavaScriptLaunchConstants.ATTR_JAVASCRIPT_INCLUDES, Collections.EMPTY_LIST);
		IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
		
		for(int i=0;i<includes.size();i++){
			String include = (String)includes.get(i);
			if(include.startsWith(JavaScriptLibraryTable.PREFIX)){
				IResource resource = wsroot.findMember(include.substring(JavaScriptLibraryTable.PREFIX.length()));
				if(resource!=null && resource instanceof IFile && resource.exists()){
					args.add(fixArgument(((IFile)resource).getLocation().toString()));
				}
			} else {
				args.add(fixArgument(include));
			}
		}
		
		runConfig.setVMArguments(execArgs.getVMArgumentsArray());
		runConfig.setProgramArguments((String[])args.toArray(new String[args.size()]));
		runConfig.setWorkingDirectory(workingDirName);
		runConfig.setEnvironment(envp);
		
		Map vmAttributesMap = getVMSpecificAttributesMap(configuration);
		runConfig.setVMSpecificAttributesMap(vmAttributesMap);
		
		String[] bootpath = getBootpath(configuration);
		runConfig.setBootClassPath(bootpath);
		
		return runConfig;
	}
	
	private static String fixArgument(String arg){
		if(arg.indexOf(' ')>=0){
			return '"' + arg + '"';
 		}
		return arg;
	}
}
