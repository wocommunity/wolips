wolips
======

Fork of wonder/wolips with changes to develop and debug in Eclipse 4.4 PDE

## Installation of WOLips to develop under Eclipse 4.4

### Prepare eclipse:

1) Use an extra Eclipse installation to develop and debug WOLips, e.g “Eclipse IDE for Eclipse Committers 4.4.1”

2) Be sure to use Java 1.7 or higher

3) m2e : Plugin for Maven - http://download.eclipse.org/technology/m2e/releases/ 
   Click the checkbox to the left of "Maven Integration for Eclipse"
   
4) Google Mechanic - http://workspacemechanic.eclipselabs.org.codespot.com/git.update/mechanic/

5) Usefull: JRebel - http://www.zeroturnaround.com/update-site

6) Usefull: Install JProfile in the new eclipse installation


### Prepare WOLips source folder

1) clone https://github.com/swklein/wolips/ to your desktop
   or fork it in your own repository an clone that
   
2) If you don't have JRebel installed:
   Open ../wolips/build.xml and comment all occurrences of “jrebel”
   
3) If you don't have JProfile installed:
   Open ../wolips/build.xml and comment all occurrences of “jprofile”
   
4) Open terminal in WOLips root folder and run
   ant -Dbuild.version=4.4.0

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

