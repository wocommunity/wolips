WOLips Readme.txt 01/Aug/2002


Here is a short how to set up a WOFramework with WOLips:

1. File -> new -> project

2. WO Version 5.* -> WOFramework -> and go through the dialog.

3. Right Click on your ProjectRoot and select properties

4. Navigate to JavaBuildPath Source

5. Select Use Source Folder in Project

6. Click create new folder and name it src

7. Click Browse to select the build output folder (This should be the bin folder)

8. Close the dialog

9. to build the framework navigate to the scripts folder

10. right click on exportwoframework.xml and select run ant

11. the woframework is build in a folder named framework-export which you should find in the same directory where your workspace is located


WOApplication

Don't use it.