WOLips Readme.txt 09/Aug/2002


Here is a short how to set up a WOFramework with WOLips:

1. File -> new -> project

2. WO Version 5.* -> WOFramework -> and go in the dialog.

3. In the dialog create a new folder "src" as the source folder and accept bin as the output folder

4. the woframework is build in a folder named framework-export which you should find in the same directory where your workspace is located


If you add a file named WOLipsBuild.properties in your project root you can manipulate the build process. A later version should have a preferences dialog for that.

The available keys are:

basedir 
destdir -> Where to install your framework c:/foo/bar default ../../framework-export
frameworkname
projectname


WOApplication

Don't use it.


Working with WOLips in the PDE.

1. Check out the stuff at woproject/src/wolips
2. Right click on your project and select Update Classpath...

You should now be able to run it as a Runtime Workbench