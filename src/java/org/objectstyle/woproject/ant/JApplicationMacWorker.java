package org.objectstyle.woproject.ant;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Chmod;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

class JApplicationMacWorker implements JApplicationWorker {

	private static final String STUB = "/System/Library/Frameworks/JavaVM.framework/Versions/Current/Resources/MacOS/JavaApplicationStub";

	protected JApplication task;

	protected File contentsDir;

	protected File resourcesDir;

	protected File javaDir;

	protected File macOSDir;

	protected File stub;

	public void execute(JApplication task) throws BuildException {

		this.task = task;
		File baseDir = new File(task.getDestDir(), task.getName() + ".app");
		this.contentsDir = new File(baseDir, "Contents");
		this.macOSDir = new File(contentsDir, "MacOS");
		this.resourcesDir = new File(contentsDir, "Resources");
		this.javaDir = new File(resourcesDir, "Java");

		this.stub = new File(STUB);

		// sanity check...
		if (!stub.isFile()) {
			throw new BuildException("Java stub file not found. Is this a Mac? " + STUB);
		}

		createDirectories();
		copyStub();
		copyIcon();
		copyJars();

		// do this AFTER the jars, as we need to list them in the Info.plist
		copyInfoPlist();
	}

	void createDirectories() throws BuildException {
		createDirectory(task.getDestDir());
		createDirectory(resourcesDir);
		createDirectory(javaDir);
		createDirectory(macOSDir);
	}

	void createDirectory(File file) throws BuildException {
		if (!file.isDirectory() && !file.mkdirs()) {
			throw new BuildException("Can't create directory " + file.getAbsolutePath());
		}
	}

	void copyInfoPlist() throws BuildException {
		File targetInfoPlist = new File(contentsDir, "Info.plist");
		String targetIcon = task.getIcon() != null && task.getIcon().isFile() ? new File(resourcesDir, task.getIcon().getName()).getAbsolutePath() : "";
		String jvmOptions = task.getJvmOptions() != null ? task.getJvmOptions() : "";

		StringBuffer jars = new StringBuffer();
		String[] jarFiles = javaDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});

		for (int i = 0; i < jarFiles.length; i++) {
			jars.append("          <string>\\$JAVAROOT/").append(jarFiles[i]).append("</string>\n");
		}

		Map tokens = new HashMap();
		tokens.put("@NAME@", task.getName());
		tokens.put("@VERSION@", task.getVersion());
		tokens.put("@LONG_NAME@", task.getLongName());
		tokens.put("@MAIN_CLASS@", task.getMainClass());
		tokens.put("@VERSION@", task.getVersion());
		tokens.put("@ICON@", targetIcon);
		tokens.put("@JVM@", task.getJvm());
		tokens.put("@JVM_OPTIONS@", jvmOptions);
		tokens.put("@JARS@", jars.toString());

		new TokenFilter(tokens).copy("japplication/mac/Info.plist", targetInfoPlist);
	}

	void copyStub() throws BuildException {
		Copy cp = makeCopyTask();
		cp.setTodir(macOSDir);
		cp.setFile(stub);
		cp.execute();

		Chmod chmod = makeChmodTask();
		chmod.setPerm("755");
		chmod.setFile(new File(macOSDir, "JavaApplicationStub"));
		chmod.execute();
	}

	void copyIcon() throws BuildException {
		if (task.getIcon() != null && task.getIcon().isFile()) {
			Copy cp = makeCopyTask();
			cp.setTodir(resourcesDir);
			cp.setFile(task.getIcon());
			cp.execute();
		}
	}

	void copyJars() {
		if (!task.getLibs().isEmpty()) {
			Copy cp = makeCopyTask();
			cp.setTodir(javaDir);
			cp.setFlatten(true);

			Iterator it = task.getLibs().iterator();
			while (it.hasNext()) {
				FileSet fs = (FileSet) it.next();
				cp.addFileset(fs);
			}

			cp.execute();
		}
	}

	Copy makeCopyTask() {
		Copy cp = new Copy();
		cp.setOwningTarget(task.getOwningTarget());
		cp.setProject(task.getProject());
		cp.setTaskName(task.getTaskName());
		cp.setLocation(task.getLocation());
		return cp;
	}

	Chmod makeChmodTask() {
		Chmod chmod = new Chmod();
		chmod.setOwningTarget(task.getOwningTarget());
		chmod.setProject(task.getProject());
		chmod.setTaskName(task.getTaskName());
		chmod.setLocation(task.getLocation());
		return chmod;
	}
}
