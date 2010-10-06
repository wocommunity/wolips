package org.objectstyle.wolips.eomodeler.core.model;

import java.io.File;
import java.net.URL;

import org.objectstyle.wolips.baseforplugins.util.URLUtils;

public class EOLastModified {
	private File _file;

	private long _lastModified;

	public EOLastModified(File file) {
		_file = file;
		update();
	}
	
	public File getFile() {
		return _file;
	}

	public EOLastModified(URL url) {
		if ("file".equals(url.getProtocol())) {
			_file = URLUtils.cheatAndTurnIntoFile(url);
		}
		update();
	}

	public boolean hasBeenModified() {
		return _file != null && _lastModified != -1 && _file.lastModified() != _lastModified;
	}

	public void update() {
		if (_file != null) {
			_lastModified = _file.lastModified();
		} else {
			_lastModified = -1;
		}
	}
	
	public String toString() {
		return "[EOLastModified: " + _file + "]";
	}
}
