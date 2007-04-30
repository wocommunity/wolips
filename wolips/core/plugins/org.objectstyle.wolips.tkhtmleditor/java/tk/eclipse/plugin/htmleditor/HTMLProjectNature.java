package tk.eclipse.plugin.htmleditor;

import java.util.ArrayList;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * @author Naoki Takezoe
 */
public class HTMLProjectNature implements IProjectNature {
	
	public static final String HTML_NATURE_ID = "tk.eclipse.plugin.htmleditor.HTMLProjectNature";
	public static final String HTML_BUILDER_ID = "tk.eclipse.plugin.htmleditor.HTMLProjectBuilder";
	private IProject project;
	
	public void configure() throws CoreException {
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();
		for(int i=0;i<commands.length;i++){
			if(commands[i].getBuilderName().equals(HTML_BUILDER_ID)){
				return;
			}
		}
		ICommand command = desc.newCommand();
		command.setBuilderName(HTML_BUILDER_ID);
		ICommand[] newCommands = new ICommand[commands.length + 1];
		for(int i=0;i<commands.length;i++){
			newCommands[i] = commands[i];
		}
		newCommands[newCommands.length - 1] = command;
		desc.setBuildSpec(newCommands);
		project.setDescription(desc,null);
	}

	public void deconfigure() throws CoreException {
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();
		ArrayList list = new ArrayList();
		for(int i=0;i<commands.length;i++){
			if(!commands[i].getBuilderName().equals(HTML_BUILDER_ID)){
				list.add(commands[i]);
			}
		}
		desc.setBuildSpec((ICommand[])list.toArray(new ICommand[list.size()]));
		project.setDescription(desc,null);
	}

	public IProject getProject() {
		return this.project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
