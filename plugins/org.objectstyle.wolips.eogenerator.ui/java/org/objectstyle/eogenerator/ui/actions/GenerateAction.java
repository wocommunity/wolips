package org.objectstyle.wolips.eogenerator.ui.actions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.objectstyle.wolips.eogenerator.ui.editors.CommandLineTokenizer;
import org.objectstyle.wolips.eogenerator.ui.editors.EOGeneratorEditor;
import org.objectstyle.wolips.eogenerator.ui.editors.EOGeneratorModel;
import org.objectstyle.wolips.preferences.Preferences;

public class GenerateAction implements IObjectActionDelegate {
  private ISelection mySelection;

  public GenerateAction() {
    super();
  }

  public void setActivePart(IAction _action, IWorkbenchPart _targetPart) {
  }

  public void run(IAction _action) {
    try {
      IStructuredSelection selection = (IStructuredSelection) mySelection;
      if (selection != null && !selection.isEmpty()) {
        IFile eogenFile = (IFile) selection.getFirstElement();
        EOGenerateWorkspaceJob generateJob = new EOGenerateWorkspaceJob(eogenFile);
        generateJob.schedule();
      }
    }
    catch (Throwable t) {
      t.printStackTrace();
      MessageDialog.openError(new Shell(), "Generate Failed", t.getMessage());
    }
  }

  public void selectionChanged(IAction _action, ISelection _selection) {
    mySelection = _selection;
  }

  protected static class EOGenerateWorkspaceJob extends WorkspaceJob {
    private IFile myEOGenFile;

    public EOGenerateWorkspaceJob(IFile _eogenFile) {
      super("EOGenerating " + _eogenFile.getName() + " ...");
      myEOGenFile = _eogenFile;
    }

    public IStatus runInWorkspace(IProgressMonitor _monitor) throws CoreException {
      try {
        EOGeneratorModel eogenModel = EOGeneratorEditor.createEOGeneratorModel(myEOGenFile);
        String eogenFileContents = eogenModel.writeToString(Preferences.getEOGeneratorPath(), Preferences.getEOGeneratorTemplateDir(), Preferences.getEOGeneratorJavaTemplate(), Preferences.getEOGeneratorSubclassJavaTemplate());
        List commandsList = new LinkedList();
        CommandLineTokenizer tokenizer = new CommandLineTokenizer(eogenFileContents);
        while (tokenizer.hasMoreTokens()) {
          commandsList.add(tokenizer.nextToken());
        }
        String[] tokens = (String[]) commandsList.toArray(new String[commandsList.size()]);
        IProject project = myEOGenFile.getProject();
        Process process = Runtime.getRuntime().exec(tokens, null, project.getLocation().toFile());

        InputStream inputstream = process.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

        StringBuffer output = new StringBuffer();
        output.append("EOGenerator Finished!\n\n");
        
        String line;
        while ((line = bufferedreader.readLine()) != null) {
          output.append(line);
        }

        try {
          if (process.waitFor() != 0) {
            output.append("\n\nExit value = " + process.exitValue());
          }
        }
        catch (InterruptedException e) {
          System.err.println(e);
        }
        
        project.getFolder(new Path(eogenModel.getDestination())).refreshLocal(IResource.DEPTH_INFINITE, _monitor);
        project.getFolder(new Path(eogenModel.getSubclassDestination())).refreshLocal(IResource.DEPTH_INFINITE, _monitor);

        final String outputStr = output.toString();
        Display.getDefault().asyncExec(new Runnable() {
          public void run() {
            MessageDialog.openInformation(new Shell(), "EOGenerator Finished", outputStr);
          }
        });
      }
      catch (Throwable t) {
        throw new ResourceException(IStatus.ERROR, myEOGenFile.getFullPath(), "Failed to generate.", t);
      }
      return new Status(IStatus.OK, org.objectstyle.wolips.eogenerator.ui.Activator.PLUGIN_ID, IStatus.OK, "Done", null);
    }
  }
}
