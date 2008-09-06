package org.objectstyle.woenvironment.frameworks;

import java.io.File;

public class FrameworkLibrary {
	private File libraryFile;

	private File sourceJar;

	private String sourcePath;

	private File docJar;

	private String docPath;

	public FrameworkLibrary(File libraryFile, File sourceJar, String sourcePath, File docJar, String docPath) {
		this.libraryFile = libraryFile;
		this.sourceJar = sourceJar;
		this.sourcePath = sourcePath;
		this.docJar = docJar;
		this.docPath = docPath;
	}

	public File getLibraryFile() {
		return this.libraryFile;
	}

	public File getSourceJar() {
		return this.sourceJar;
	}

	public String getSourcePath() {
		return this.sourcePath;
	}

	public File getDocJar() {
		return this.docJar;
	}

	public String getDocPath() {
		return this.docPath;
	}
}
