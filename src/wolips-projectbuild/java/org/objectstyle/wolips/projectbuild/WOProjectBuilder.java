package org.objectstyle.wolips.projectbuild;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.projectbuild.nature.*;
import org.objectstyle.wolips.projectbuild.util.*;

/**
 * @author Harald Niesche
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class WOProjectBuilder extends IncrementalProjectBuilder {

  /**
   * Constructor for WOProjectBuilder.
   */
  public WOProjectBuilder() {
    super();
  }

  /**
   * @see org.eclipse.core.internal.events.InternalBuilder#build(int, Map, IProgressMonitor)
   */
  protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
    throws CoreException 
  {
    
    if (null == monitor) {
      monitor = new NullProgressMonitor ();
    }
  
  
    //monitor  = new SubProgressMonitor (monitor, 100*1000);
    
    monitor.beginTask("building WebObjects layout ...", 100);
    
    try {
      System.out.println(getProject());
      
      IJavaProject javaProject = getJavaProject();
      
      System.out.println(javaProject.getOutputLocation());
  
      IResourceDelta delta = getDelta(getProject());
      System.out.println(delta);
  
      WOBuildVisitor builder = new WOBuildVisitor(monitor, getProject());
 
      monitor.subTask("checking directory structure ...");
 
      if (!builder._checkDirs ()) {
        delta = null;
        monitor.worked(5);
      } else if (kind == FULL_BUILD) {
        delta = null;
        IFolder buildFolder = getProject().getFolder("build");
        monitor.subTask("scrubbing build folder ...");
        buildFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
        monitor.worked(1);
        buildFolder.delete(true, true, null);
        monitor.worked(2);
        buildFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
        monitor.subTask("re-creating structure ...");
        builder._checkDirs ();
        monitor.worked(2);
      }    
      
      monitor.subTask("creating Info.plist");
     
      _createInfoPlist();
    
      monitor.worked(1);
  
      if (null != delta) {
        System.out.println("<partial build>");

        monitor.subTask("preparing partial build");
        

        BeanCounter bc = new BeanCounter(monitor, getProject());
        
        long t0 = System.currentTimeMillis();
        
        delta.accept(bc, IResourceDelta.ALL_WITH_PHANTOMS);
        
        System.out.println("visiting "+bc.count+"deltas took: "+(System.currentTimeMillis()-t0)+" ms");
        

        t0 = System.currentTimeMillis();
        

        delta.accept(builder, IResourceDelta.ALL_WITH_PHANTOMS);
        
        System.out.println("delta.accept took: "+(System.currentTimeMillis()-t0)+" ms");


        
        System.out.println("</partial build>");
        monitor.worked(12);
      } else {
        System.out.println("<full build>");
        monitor.subTask("preparing full build");
        
        BeanCounter bc = new BeanCounter(monitor, getProject());
        
        long t0 = System.currentTimeMillis();
        
        getProject().accept(bc);
        
        System.out.println("visiting "+bc.count+" project nodes took: "+(System.currentTimeMillis()-t0)+" ms");

        t0 = System.currentTimeMillis();
        getProject().accept(builder);
        System.out.println("preparing with "+bc.count+" project nodes took: "+(System.currentTimeMillis()-t0)+" ms");
        
        System.out.println("</full build>");
        monitor.worked(12);
      }

      long t0 = System.currentTimeMillis();
      builder.executeTasks(monitor);
      System.out.println("building structure took: "+(System.currentTimeMillis()-t0)+" ms");

      t0 = System.currentTimeMillis();
      monitor.subTask("copying classes");
      _jarBuild (delta, monitor);
      System.out.println("copying classes took: "+(System.currentTimeMillis()-t0)+" ms");

      monitor.done();
    } catch (CoreException up) {
      up.printStackTrace();
      throw up;
    } 
    
    return null;
  }
  
  private void _createInfoPlist () throws CoreException {
    WOIncrementalBuildNature won = WOIncrementalBuildNature.s_getNature(getProject());
    String infoPlist;
    
    if (won.isFramework()) {
      infoPlist = INFO_PLIST_FRAMEWORK;
    } else {
      infoPlist = INFO_PLIST_APPLICATION;
    }
    
    infoPlist = replace (infoPlist, "$$name$$",     won.getResultName());
    infoPlist = replace (infoPlist, "$$basename$$", getProject().getName());
    infoPlist = replace (infoPlist, "$$res$$",      won.getResourceName().toString());
    infoPlist = replace (infoPlist, "$$wsr$$",      won.getWebResourceName().toString());
    infoPlist = replace (infoPlist, "$$type$$",     won.isFramework() ? "FMWK" : "APPL");

    IPath infoPath = won.getInfoPath().append("Info.plist");
    IFile resFile = getProject().getWorkspace().getRoot().getFile(infoPath);
    resFile.delete(true, false, null);

    try {    
      InputStream is = new ByteArrayInputStream (infoPlist.getBytes("UTF-8"));
      
      resFile.create (is, true, null);
    } catch (UnsupportedEncodingException uee) {
      // shouldn't happen anyway, since utf8 must be supported by every JVM
      uee.printStackTrace(); 
    }
  }
    
  private void _jarBuild (IResourceDelta delta, IProgressMonitor m) throws CoreException {
    
    
    System.out.println("<jar build>");
    WOJarBuilder jarBuilder = new WOJarBuilder (m, getProject());

    long t0 = System.currentTimeMillis();

    if (null != delta) {
      delta.accept (jarBuilder, IResourceDelta.ALL_WITH_PHANTOMS);
    } else {
      IPath outPath = getJavaProject().getOutputLocation();
  
      IContainer output = getProject();
  
      if (!outPath.segment(0).equals(getProject().getName())) {
        output = getProject().getParent().getFolder(outPath);
      }
      output.accept (jarBuilder);
    }

    System.out.println ("prepare jar copy took "+(System.currentTimeMillis()-t0)+" ms");

    m.worked (10);

    t0 = System.currentTimeMillis();

    jarBuilder.executeTasks(m);    
    
    System.out.println ("executing jar copy took "+(System.currentTimeMillis()-t0)+" ms");

    System.out.println("</jar build>");
    
  }


  private IJavaProject getJavaProject () {
    try {
      return ((IJavaProject)(getProject().getNature(JavaCore.NATURE_ID)));
    } catch (CoreException up) {
    }
    return null;
  }

  /**
   * @see org.eclipse.core.resources.IncrementalProjectBuilder#startupOnInitialize()
   */
  protected void startupOnInitialize() {
    try {
      IJavaProject javaProject = getJavaProject();
      
      System.out.println(javaProject.getOutputLocation());
    } catch (Throwable up) {
    }

    
    super.startupOnInitialize();
  }

  static abstract class WOBuildHelper 
      extends    ResourceUtilities
      implements IResourceDeltaVisitor 
                , IResourceVisitor 
  {
    
    public static interface Buildtask {
      public int amountOfWork ();
      public void doWork (IProgressMonitor m) throws CoreException;
    }
    
    public static abstract class BuildtaskAbstract implements Buildtask {
      public int amountOfWork () {
        return (_workAmount);
      }
      
      protected int _workAmount = 1000;
    }
    
    public static class CopyTask extends BuildtaskAbstract {
      public CopyTask (IResource res, IPath destination, String msgPrefix) {
        _res = res;
        _dest = destination;
        _msgPrefix = msgPrefix;
        
        File file = _res.getFullPath().toFile();
        if (file.isFile()) {
          /*
          InputStream is = null;
          try {
            is = new FileInputStream (file);
            _workAmount = is.available();
          } catch (IOException up) {
          } finally {
            if (null != is) {
              try {
                is.close();
              } catch (IOException upYours) {
                // ignore
              }
            }
          }
          */
        }
      }
      
      public void doWork (IProgressMonitor m) throws CoreException {
        try {
          IContainer cont = _res.getParent();
          cont.refreshLocal(IResource.DEPTH_INFINITE, m);

          //System.out.println (_msgPrefix+" copy "+_res+" -> "+_dest);
          
          int n = _dest.segmentCount()-3;
          IPath dstShortened = _dest;
          if (n > 0) {
            dstShortened = _dest.removeFirstSegments(n);
          }
          
          m.subTask("create " + dstShortened);
          checkDestination(_dest, m);
          _res.copy(_dest, true, null);
        } catch (CoreException up) {
          m.setCanceled(true);
        }
      }
      
      IResource _res;
      IPath     _dest;
      String    _msgPrefix;
    }

    public static class DeleteTask extends BuildtaskAbstract {
      public DeleteTask (IPath path, String msgPrefix) {
        _workAmount = 1000;
        _path = path;
        _msgPrefix = msgPrefix;
      }
      
      public void doWork (IProgressMonitor m) throws CoreException {
        if (_path == null) {
          // this really really should not happen! (again ...)
          throw new OperationCanceledException ("(deleting a null path wipes the workspace)");
        }
        
        IResource res = getWorkspaceRoot().findMember(_path);
        if (null != res) {
          res.refreshLocal (IResource.DEPTH_ONE, m);
        }
        
        IFile theFile = getWorkspaceRoot().getFile(_path);
        IContainer theFolder = getWorkspaceRoot().getFolder(_path);
        
        if (null != theFile) {
          //System.out.println (_msgPrefix+" delete "+_path);
          m.subTask("delete " + _path);
          theFile.delete(true, true, null);
        } else if (
          (null != theFolder) 
          && (theFolder instanceof IFolder)
        ) {
          System.out.println (_msgPrefix+" delete "+_path);
          m.subTask("delete " + _path);
          ((IFolder)theFolder).delete(true, true, null);
        }
        
          /*
        if (theFile.exists()) {
          if (theFile.isFile()) {
            System.out.println (_msgPrefix+" delete "+_path);
            theFile.delete();
          } else if (theFile.isDirectory()) {
            System.out.println ("*** not deleting folder: "+theFile);
          }
        }
          */
        /*
        if ((null != res) && res.exists()) {
          System.out.println (_msgPrefix+" delete "+_path);
          res.delete (true, m);
        } else {
          System.out.println (_msgPrefix+" delete (not) "+_path);
        }
        */
      }
      
      IPath _path;
      String _msgPrefix;
    }


    public WOBuildHelper (IProgressMonitor monitor, IProject project) 
      throws CoreException
    {
      _monitor = monitor;
      _project = project;
      _woNature = WOIncrementalBuildNature.s_getNature(project);
    }

    /**
     * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(IResourceDelta)
     */
    public boolean visit(IResourceDelta delta) throws CoreException {
      handleResource (delta.getResource(), delta);
      return true;
    }

    /**
     * @see org.eclipse.core.resources.IResourceVisitor#visit(IResource)
     */
    public boolean visit(IResource resource) throws CoreException {
      handleResource (resource, null);
      return true;
    }

    
    public synchronized void addTask (Buildtask task) {
      _buildTasks.add(task);
      _buildWork += task.amountOfWork();
    }
    
    public void executeTasks (IProgressMonitor m)
      throws CoreException
    {
      List tasks = null;
      int amountOfWork = 0;
      synchronized (this) {
        tasks = _buildTasks;
        _buildTasks = new ArrayList ();
        amountOfWork = _buildWork;
        _buildWork = 0;
      }
      
      m = new SubProgressMonitor (m, 41);
      m.beginTask("building ...", amountOfWork);
      
      Iterator iter = tasks.iterator();

      while (iter.hasNext()) {
        Buildtask thisTask = (Buildtask)iter.next();
        
        thisTask.doWork(m);
        m.worked(thisTask.amountOfWork());
        if (m.isCanceled()) {
          throw new OperationCanceledException ();
        }
      }
      
      m.done();
    }

    
    abstract void handleResource (IResource res, IResourceDelta delta) 
      throws CoreException;

    protected IProgressMonitor _monitor;
    protected IProject _project;
    protected WOIncrementalBuildNature _woNature = null;
    private List _buildTasks = new ArrayList ();
    private int  _buildWork = 0;

  }

  static class WOJarBuilder
      extends WOBuildHelper
  {
    public WOJarBuilder (IProgressMonitor monitor, IProject project) 
      throws CoreException
    {
      super (monitor, project);
      _outPath = _woNature.getJavaOutputPath();
      _compilerOutPath = _woNature.getJavaProject().getOutputLocation();
      _baseSegments = _compilerOutPath.segmentCount();
    }

    public void handleResource (IResource resource, IResourceDelta delta)
      throws CoreException
    {
      
      if (
        "class".equals(resource.getFileExtension())
      ) {
        IPath path = resource.getFullPath();
        if (_compilerOutPath.isPrefixOf(path) && !_outPath.isPrefixOf(path)) {
          boolean delete = false;
          IPath cp = path.removeFirstSegments(_baseSegments);
          path = _outPath.append(cp);
          if ((null != delta) && (delta.getKind() == IResourceDelta.REMOVED)) {
            addTask (new DeleteTask (path, "jar"));
          } else {
            addTask (new CopyTask (resource, path, "jar"));
          }
        }
      }
    }
    
    int _baseSegments;
    IPath _outPath;
    IPath _compilerOutPath;
  }


  static class WOBuildVisitor
      extends WOBuildHelper
  {
    
    WOBuildVisitor (IProgressMonitor monitor, IProject project) 
      throws CoreException 
    {
      super (monitor, project);
      
      try {
        _outputPath = _woNature.getJavaProject().getOutputLocation();
      } catch (CoreException up) {
        _outputPath = new Path ("/dummy");
      }
    }

    private boolean _checkDirs () throws CoreException {
      
      IPath path       = _woNature.getBuildPath();
      IPath resultPath = _woNature.getResultPath();
      IPath resPath    = _woNature.getResourceOutputPath();
      IPath javaPath   = _woNature.getJavaOutputPath();
      IPath webresPath = _woNature.getWebResourceOutputPath();
      
      boolean result = checkDir (path, null);
      result = checkDir (resPath, null) && result;
      result = checkDir (javaPath, null) && result;
      result = checkDir (webresPath, null) && result;

      _buildPath = path;

      return (result);
    }


    
    public void handleResource (IResource res, IResourceDelta delta) 
      throws CoreException
    {
      {
        IPath copyToPath = _woNature.getDestinationPath(res);
        
        if (null != copyToPath) {
          if ((null != delta) && (delta.getKind() == IResourceDelta.REMOVED)) {
            addTask (new DeleteTask (copyToPath, "build"));
          } else {
            addTask (new CopyTask (res, copyToPath, "build"));
          }
        } else {
          //System.out.println("//ignore: "+res);
        }
      }
      
/*
      IPath resPath = res.getFullPath();
      if (!_outputPath.isPrefixOf(resPath)) {
        System.out.println(res);
        if (null != delta) System.out.println(delta);
      }
*/
    }
    
    IPath _outputPath = null;
    IPath _buildPath = null;

  };


  static class BeanCounter extends WOBuildHelper {
    BeanCounter (IProgressMonitor m, IProject p) throws CoreException {
      super (m, p);
    }
    
    void handleResource (IResource res, IResourceDelta delta) {
      ++count;
    }
    
    public int count = 0;
  };

  /**
   * replace every occurence of oldPart with newPart in origin
   * returns changed origin (since String is immutable...)
   */

  static public String replace(String origin, String oldPart, String newPart) {
    if ((origin == null) || (origin.length() == 0)) {
      return origin;
    }

    StringBuffer buffer = new StringBuffer (origin);

    int index;
    int end = origin.length();
    int oldLength = oldPart.length();

    while (end >= 0) {
      index = origin.lastIndexOf(oldPart, end);
      // no occurence of oldPart
      if (index== -1)
          break;

      end = index-oldLength;

      buffer.replace(index,  index+oldLength, newPart);
    }
    return buffer.toString();
  }



  static final String INFO_PLIST_APPLICATION = 
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\r\n"
+"<!DOCTYPE plist SYSTEM \"file://localhost/System/Library/DTDs/PropertyList.dtd\">" + "\r\n"
+"<plist version=\"0.9\">" + "\r\n"
+"<dict>" + "\r\n"
+"  <key>CFBundleDevelopmentRegion</key>" + "\r\n"
+"  <string>English</string>" + "\r\n"
+"  <key>CFBundleExecutable</key>" + "\r\n"
+"  <string>$$basename$$</string>" + "\r\n"
+"  <key>CFBundleIconFile</key>" + "\r\n"
+"  <string>WOAfile.icns</string>" + "\r\n"
+"  <key>CFBundleInfoDictionaryVersion</key>" + "\r\n"
+"  <string>6.0</string>" + "\r\n"
+"  <key>CFBundlePackageType</key>" + "\r\n"
+"  <string>APPL</string>" + "\r\n"
+"  <key>CFBundleSignature</key>" + "\r\n"
+"  <string>webo</string>" + "\r\n"
+"  <key>CFBundleVersion</key>" + "\r\n"
+"  <string>0.0.1d1</string>" + "\r\n"
+"  <key>NSExecutable</key>" + "\r\n"
+"  <string>$$basename$$</string>" + "\r\n"
+"  <key>NSJavaNeeded</key>" + "\r\n"
+"  <true/>" + "\r\n"
+"  <key>NSJavaPath</key>" + "\r\n"
+"  <array>" + "\r\n"
+"    <string>$$basename$$.jar</string>" + "\r\n"
+"  </array>" + "\r\n"
+"  <key>NSJavaPathClient</key>" + "\r\n"
+"  <string>$$basename$$.jar</string>" + "\r\n"
+"  <key>NSJavaRoot</key>" + "\r\n"
+"  <string>Contents/Resources/Java</string>" + "\r\n"
+"  <key>NSJavaRootClient</key>" + "\r\n"
+"  <string>Contents/WebServerResources/Java</string>" + "\r\n"
+"</dict>" + "\r\n"
+"</plist>" + "\r\n";

  static final String INFO_PLIST_FRAMEWORK = 
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\r\n"
+"<plist version=\"0.9\">" + "\r\n"
+"<dict>" + "\r\n"
+"  <key>NOTE</key>" + "\r\n"
+"  <string>Please, feel free to change this file -- It was generated by the Eclipse WO Support plugin.</string>" + "\r\n"
+"  <key>NSJavaPathClient</key>" + "\r\n"
+"  <string>theTestFramework.jar</string>" + "\r\n"
+"  <key>CFBundleIconFile</key>" + "\r\n"
+"  <string>WOAfile.icns</string>" + "\r\n"
+"  <key>CFBundleExecutable</key>" + "\r\n"
+"  <string>$$basename$$</string>" + "\r\n"
+"  <key>NSJavaRoot</key>" + "\r\n"
+"  <string>$$res$$/Java</string>" + "\r\n"
+"  <key>NSJavaRootClient</key>" + "\r\n"
+"  <string>$$wsr$$/Java</string>" + "\r\n"
+"  <key>NSJavaNeeded</key>" + "\r\n"
+"  <true/>" + "\r\n"
+"  <key>CFBundleName</key>" + "\r\n"
+"  <string></string>" + "\r\n"
+"  <key>NSExecutable</key>" + "\r\n"
+"  <string>$$basename$$</string>" + "\r\n"
+"  <key>NSJavaPath</key>" + "\r\n"
+"  <array>" + "\r\n"
+"    <string>thetestframework.jar</string>" + "\r\n"
+"  </array>" + "\r\n"
+"  <key>CFBundleInfoDictionaryVersion</key>" + "\r\n"
+"  <string>6.0</string>" + "\r\n"
+"  <key>Has_WOComponents</key>" + "\r\n"
+"  <true/>" + "\r\n"
+"  <key>CFBundleSignature</key>" + "\r\n"
+"  <string>webo</string>" + "\r\n"
+"  <key>CFBundleShortVersionString</key>" + "\r\n"
+"  <string></string>" + "\r\n"
+"  <key>CFBundleIdentifier</key>" + "\r\n"
+"  <string></string>" + "\r\n"
+"  <key>CFBundlePackageType</key>" + "\r\n"
+"  <string>$$type$$</string>" + "\r\n"
+"</dict>" + "\r\n"
+"</plist>" + "\r\n";


}
