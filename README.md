
<img src="http://wiki.wocommunity.org/download/attachments/2624275/icon_256x256.png?version=1&modificationDate=1287449464000&api=v2" alt="WOLips Icon" width="30%" style="float: right;"/>
#WOLips

With the deprecation of the Apple-supplied WebObjects development tools in the WO5.4 timeframe, WOProject/WOLips has become the recommended toolset for WebObjects development and is the toolset that Apple itself uses internally.

Almost all of the functionality provided by the Apple toolset (XCode, EOModeler, WOBuilder, etc) has been duplicated, greatly expanded upon or re-thought from the ground up in Eclipse and WOLips. WOProject/WOLips is the net effect of thousands of hours of freely-given and paid-for labor by many talented developers, and it is constantly improving.

##WOLips Project Home

The home page for the WOLips project is <a href=""http://wiki.wocommunity.org/display/WOL/Home">http://wiki.wocommunity.org/display/WOL/Home</a>


##Installing WOLips

Installing prebuilt versions of WOLips in existing Eclipse installations can be done the same way any other Eclipse plugin is installed.


1. Add the WOLips update URL to the Available Software Sites list (either through **Eclipse > Preferences > Install/Update > Available Software Sites** or **Help > Install New Software... > Add**)
2. Use the WOLips update URL to install the plugin (is there an active plugin URL?)




###Building WOLips

####Prerequisites
* Eclipse ( this document is current as of 4.4 Luna)
* Git
* JRebel and JProfiler are required if you want to build those parts of WOLips

#### From the command line...

1. Checkout source from Github: git clone https://github.com/wocommunity/wolips.git wolips
2. Identify path to eclipse for the value of eclipse.home (path to the folder enclosing the eclipse program and configuration directories) Edit ~/Library/wobuild.properties to include the line: eclipse.home=/path/to/your/eclipse (or pass the path in the build command using -Declipse.home=/path/to/your/eclipse)
3. build with ant. Example: 
	
	`user$ ant -Dbuild.version=4.4.0 \
		-Declipse.home=/path/to/eclipse` 
	
4. verify the build succeeded and the product is in the dist directory
5. Use the eclipse plugin installation process to install from the dist directory.



