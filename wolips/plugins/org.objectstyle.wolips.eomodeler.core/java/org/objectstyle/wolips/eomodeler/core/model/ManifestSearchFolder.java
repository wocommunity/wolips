package org.objectstyle.wolips.eomodeler.core.model;

import java.io.File;

public class ManifestSearchFolder {
	private File _folder;

	private int _depth;

	public ManifestSearchFolder(File folder) {
		this(folder, -1);
	}

	public ManifestSearchFolder(File folder, int depth) {
		_folder = folder;
		_depth = depth;
	}

	public int hashCode() {
		return _folder.hashCode();
	}

	public boolean equals(Object obj) {
		return obj instanceof ManifestSearchFolder && ((ManifestSearchFolder) obj)._folder.equals(_folder);
	}

	public File getFolder() {
		return _folder;
	}

	public int getDepth() {
		return _depth;
	}

	public boolean isInfiniteDepth() {
		return _depth == -1;
	}

	@Override
	public String toString() {
		return "[ManifestSearchFolder: " + _folder + "]";
	}
}
