<img src="http://wiki.wocommunity.org/download/attachments/2624275/icon_256x256.png?version=1&modificationDate=1287449464000&api=v2" alt="WOLips Icon" width="30%" style="float: right;"/>

# WOLips

With the deprecation of the Apple-supplied WebObjects development tools in the WO5.4 timeframe, WOProject/WOLips has become the recommended toolset for WebObjects development and is the toolset that Apple itself uses internally.

Almost all of the functionality provided by the Apple toolset (XCode, EOModeler, WOBuilder, etc) has been duplicated, greatly expanded upon or re-thought from the ground up in Eclipse and WOLips. WOProject/WOLips is the net effect of thousands of hours of freely-given and paid-for labor by many talented developers, and it is constantly improving.

## WOLips Project Home

The home page for the WOLips project is <a href="http://wiki.wocommunity.org/display/WOL/Home">http://wiki.wocommunity.org/display/WOL/Home</a>


## Installing WOLips

Installing prebuilt versions of WOLips in existing Eclipse installations can be done the same way any other Eclipse plugin is installed.


1. Add the WOLips update URL to the Available Software Sites list (either through **Eclipse > Preferences > Install/Update > Available Software Sites** or **Help > Install New Software... > Add**)
2. Use the WOLips update URL to install the plugin http://jenkins.wocommunity.org/job/WOLips410/lastSuccessfulBuild/artifact/temp/dist/




### Building WOLips

#### Prerequisites
* Eclipse ( this document is current as of 4.10 2018-12)
* Git
* JRebel and JProfiler are required if you want to build those parts of WOLips

#### From the command line...

1. Checkout source from Github: git clone https://github.com/wocommunity/wolips.git wolips
2. Identify path to eclipse for the value of eclipse.home (path to the folder enclosing the eclipse program and configuration directories) Edit ~/Library/wobuild.properties to include the line: eclipse.home=/path/to/your/eclipse (or pass the path in the build command using -Declipse.home=/path/to/your/eclipse)
3. build with ant. Example: 
	
	```bash
	user$ ant -Dbuild.version=4.10.0 -Declipse.home=/path/to/eclipse -Dskip.jprofiler=true -Dskip.jrebel=true
	```
	
4. verify the build succeeded and the product is in the dist directory
5. Use the eclipse plugin installation process to install from the dist directory.


wolips
======

Fork of wonder/wolips with changes to develop and debug in Eclipse 4.10 PDE

## Installation of WOLips to develop under Eclipse 4.10

### Prepare eclipse:

1) Use an extra Eclipse installation to develop and debug WOLips

2) Be sure to use Java 1.7 or higher

3) If not already contained in the Eclipse package:
   m2e : Plugin for Maven - http://download.eclipse.org/technology/m2e/releases/ 
   Click the checkbox to the left of "Maven Integration for Eclipse"
   
4) Google Mechanic - http://workspacemechanic.eclipselabs.org.codespot.com/git.update/mechanic/

5) Usefull: JRebel - http://www.zeroturnaround.com/update-site

6) Usefull: Install JProfile in the new eclipse installation


### Prepare WOLips source folder

1) clone https://github.com/wocommunity/wolips/ to your desktop
   or fork it in your own repository an clone that
   
2) If you don't have JRebel installed:
   Open ../wolips/build.xml and comment all occurrences of “jrebel”
   
3) If you don't have JProfile installed:
   Open ../wolips/build.xml and comment all occurrences of “jprofile”
   
4) Open terminal in WOLips root folder and run
   ant -Dbuild.version=4.10.0

### Prepare workspace:

1) Open eclipse and create a new workspace, e.g. WOLips

2) Create WO_HOME classpath variable under eclipse → Preference → Java → Build Path → Classpath Variabels:
   eg.: WO_HOME /Library/WebObjects/lib
   
3) Import woenviroment project
   - Import → General → Existing Projects into Workspace
   - Select ./woenviroment

4) Import wolips projects
   - Import → General → Existing Projects into Workspace
   - Select ./wolips
   - Deselect following entries:
	 EntityModeler
	 eomodeldoc
	 veogen
   In the case you have not installed JRebel deselect as well ...jrebel and ...jrebel.feature
   In the case you have not installed JProfile deselect as well ...jprofile.launching and ...jprofil.feature

5) Depending on the Google Mechanic version you had to close org.objetstyle.wolips.mechanic

### Debug WOLips

1) Open Debug Configurations Dialog

2) Select Eclipse Application and press new

3) Change Execution environment to Java 1.7
   Press Debug
   
4) In the new instance select your preferred project(s) and start testing and enhancing WOLips

