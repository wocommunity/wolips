package org.objectstyle.wolips.eomodeler.factories;

import java.io.File;
import java.io.IOException;

public class SimpleManifestUtilities {
	public static interface SearchFolderDelegate {
		public void fileMatched(File file) throws IOException;
	}
	
	public static int fillInSearchFolders(File baseFolder, String pattern, SearchFolderDelegate delegate) throws IOException {
		int patternSegment;
		File finalBaseFolder;
		if (pattern.startsWith(File.separator)) {
			patternSegment = 1;
			finalBaseFolder = new File(File.separator);
		}
		else {
			patternSegment = 0;
			finalBaseFolder = baseFolder;
		}
		return SimpleManifestUtilities.fillInSearchFolders(finalBaseFolder, pattern.split(File.separator), patternSegment, delegate);
	}
	
	public static int fillInSearchFolders(File baseFile, String[] pattern, int patternSegment, SearchFolderDelegate delegate) throws IOException {
		int matchesFound = 0;
		if (patternSegment == pattern.length) {
			if (baseFile.exists()) {
				matchesFound ++;
				delegate.fileMatched(baseFile);
			}
		}
		else if (pattern[patternSegment].startsWith("*")) {
			File[] childrenFiles = baseFile.listFiles();
			if (childrenFiles != null) {
				for (File childFile : childrenFiles) {
					if (pattern[patternSegment].length() == 1 || childFile.getName().endsWith(pattern[patternSegment].substring(1))) {
						matchesFound += fillInSearchFolders(childFile, pattern, patternSegment + 1, delegate);
					}
				}
			}
		}
		else {
			matchesFound += fillInSearchFolders(new File(baseFile, pattern[patternSegment]), pattern, patternSegment + 1, delegate);
		}
		return matchesFound;
	}

}
