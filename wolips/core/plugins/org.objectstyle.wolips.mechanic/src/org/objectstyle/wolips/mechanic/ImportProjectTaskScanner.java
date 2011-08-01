package org.objectstyle.wolips.mechanic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.google.eclipse.mechanic.DirectoryIteratingTaskScanner;
import com.google.eclipse.mechanic.IResourceTaskProvider;
import com.google.eclipse.mechanic.IResourceTaskReference;
import com.google.eclipse.mechanic.TaskCollector;

public class ImportProjectTaskScanner extends DirectoryIteratingTaskScanner {
  private static final Logger log = Logger.getLogger(ImportProjectsTask.class.getName());

  public ImportProjectTaskScanner() {
  }

  @Override
  public void scan(IResourceTaskProvider source, TaskCollector collector) {
    Pattern variablePattern = Pattern.compile("^#\\s*@(\\S+)\\s+(.*)");
    
    for (Iterator<IResourceTaskReference> iterator = source.getTaskReferences(".proj").iterator(); iterator.hasNext();) {
      IResourceTaskReference ref = iterator.next();
   	  File projectFile = ref.asFile();
      try {
        BufferedReader br = new BufferedReader(new FileReader(projectFile));
        try {
          List<IPath> importPaths = new LinkedList<IPath>(); 
          String id = projectFile.getName();
          String title = id;
          String description = null;
          boolean reconcile = true;
          
          String line;
          while ((line = br.readLine()) != null) {
            Matcher variableMatcher = variablePattern.matcher(line);
            if (variableMatcher.matches()) {
              String key = variableMatcher.group(1);
              String value = variableMatcher.group(2);
              if ("title".equals(key)) {
                title = value;
              }
              else if ("description".equals(key)) {
                description = value;
              }
              else if ("reconcile".equals(key)) {
                reconcile = Boolean.parseBoolean(value);
              }
            }
            else if (line.startsWith("#")) {
              // ignore comments
            }
            else {
              String pathStr = line.trim();
              if (pathStr.length() > 0) {
                importPaths.add(new Path(pathStr));
              }
            }
          }
          
          collector.add(new ImportProjectsTask(id, title, description, importPaths, reconcile));
        }
        finally {
          br.close();
        }
      }
      catch (Throwable t) {
        log.log(Level.SEVERE, "Failed to read project file '" + projectFile + "'.", t);
      }
    }
  }
}
