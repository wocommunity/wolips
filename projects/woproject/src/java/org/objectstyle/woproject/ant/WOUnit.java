package org.objectstyle.woproject.ant;

import java.util.ArrayList;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;


public class WOUnit extends JUnitTask {
    private ArrayList frameworkSets = new ArrayList();

    public WOUnit() throws Exception {
        super();
    }
    
    public void addFrameworks(FrameworkSet frameworks) throws BuildException {
        frameworkSets.add(frameworks);
    }

    public void execute() throws BuildException {
        getCommandline().createClasspath(getProject()).createPath().add(FrameworkSet.jarsPathForFrameworkSets(getProject(), frameworkSets, false));
        super.execute();
    }

}
