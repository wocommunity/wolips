<img src="https://wiki.wocommunity.org/xwiki/bin/download/WOL/Home/WebHome/icon_256x256.png" alt="WOLips Icon" width="30%" style="float: right;"/>

# WOLips

WOLips is the Eclipse feature for developing WebObjects applications.

## Downloading WOLips
To download a full WOLips installation, including java development tools, plugin development tools, the latest JRE, and helpful plugins like a decompiler and spotbugs, look for the latest release under org.objectstyle.woproject.wolips.product on the right sidebar, or just choose your platform below.

| Platform | x86_64 | aarch64 | riscv64 |
| -------- | ------ | ------- | ------- |
| linux.gtk | [linux.gtk](https://github-registry-files.githubusercontent.com/1585278/9e784c00-c81f-11ef-93b2-6e440142df11?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20250101%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250101T091047Z&X-Amz-Expires=300&X-Amz-Signature=af166996c794b4f6642794ca120318e845aab03009748bedcd8232a73259ae17&X-Amz-SignedHeaders=host&response-content-disposition=filename%3Dwolips.product-5.0.0-linux.gtk.x86_64.tgz&response-content-type=application%2Foctet-stream) | [linux.gtk](https://github-registry-files.githubusercontent.com/1585278/9e784c00-c81f-11ef-8b42-14df94f49584?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20250101%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250101T091047Z&X-Amz-Expires=300&X-Amz-Signature=5b2268dcfb13d03945ff030c22afc777e3b54363ef5fdc9691abf42b65aaeed3&X-Amz-SignedHeaders=host&response-content-disposition=filename%3Dwolips.product-5.0.0-linux.gtk.aarch64.tgz&response-content-type=application%2Foctet-stream) | [linux.gtk](https://github-registry-files.githubusercontent.com/1585278/9e784c00-c81f-11ef-8dc3-fcf87a8faf66?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20250101%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250101T091047Z&X-Amz-Expires=300&X-Amz-Signature=0fc698c3675413b3aad3f757396e62dd82b79ac965f2e9e37c76d20b69c58e98&X-Amz-SignedHeaders=host&response-content-disposition=filename%3Dwolips.product-5.0.0-linux.gtk.riscv64.tgz&response-content-type=application%2Foctet-stream) |
| macosx.cocoa | [macosx.cocoa](https://github-registry-files.githubusercontent.com/1585278/9e784c00-c81f-11ef-8c9f-b1bce8a20d4e?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20250101%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250101T091047Z&X-Amz-Expires=300&X-Amz-Signature=8ab24631a0526fb81a17f36e0958d2c8e145d8f2b81639557da67c194d048d5d&X-Amz-SignedHeaders=host&response-content-disposition=filename%3Dwolips.product-5.0.0-macosx.cocoa.x86_64.tgz&response-content-type=application%2Foctet-stream) | [macosx.cocoa](https://github-registry-files.githubusercontent.com/1585278/a33d0000-c81f-11ef-8979-65d0bf830a29?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20250101%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250101T091047Z&X-Amz-Expires=300&X-Amz-Signature=1268707b78d29d93bf25411e31e44b00abdc654aff52718e5b7490e78610344b&X-Amz-SignedHeaders=host&response-content-disposition=filename%3Dwolips.product-5.0.0-macosx.cocoa.aarch64.tgz&response-content-type=application%2Foctet-stream) | n/a |
| win32.win32 | [win32.win32](https://github-registry-files.githubusercontent.com/1585278/a3d59680-c81f-11ef-8407-6f370431a4f3?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20250101%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250101T091047Z&X-Amz-Expires=300&X-Amz-Signature=cf3c72cd3f63c978251d78e4dbb8233b9daff8a723e6836140d61f44f02e3879&X-Amz-SignedHeaders=host&response-content-disposition=filename%3Dwolips.product-5.0.0-win32.win32.x86_64.zip&response-content-type=application%2Foctet-stream) | [win32.win32](https://github-registry-files.githubusercontent.com/1585278/a6d08700-c81f-11ef-92f3-3fa97a50bd07?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20250101%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250101T091047Z&X-Amz-Expires=300&X-Amz-Signature=e1299906bbbe207283562b3d97058bdffa462fbb081d931ada9fc8d57e0ae50b&X-Amz-SignedHeaders=host&response-content-disposition=filename%3Dwolips.product-5.0.0-win32.win32.aarch64.zip&response-content-type=application%2Foctet-stream) | n/a |

## Installing WOLips

Installing prebuilt versions of WOLips in existing Eclipse installations can be done the same way any other Eclipse plugin is installed.


1. Add the WOLips update URL to the Available Software Sites list (either through **Eclipse > Preferences > Install/Update > Available Software Sites** or **Help > Install New Software... > Add**)
2. Use the WOLips update URL to install the plugin <a href="https://wocommunity.github.io/wolips/repository/">https://wocommunity.github.io/wolips/repository/</a>




### Building WOLips

#### Prerequisites
* Git
* Latest Java LTS or newer
* A recent version of maven

#### From the command line...

1. Checkout source from Github:

	```bash
	git clone https://github.com/wocommunity/wolips.git
	```

2. Build with maven:
	
	```bash
	cd wolips && mvn clean package
	```

3. There is no step 3!

## Installing the build

A p2 repository is created at wolips/wolips.p2/target/repository/. You can install it as with the install site above, but using the local directory instead of a URL to the remote repository. The version number is timestamped, so you can immediately update your local wolips install after a fresh build if you desire.

## Develop WOLips under Eclipse

### Prepare eclipse:

1) Install the latest "Eclipse IDE for Eclipse Committers" found at <a href="https://www.eclipse.org/downloads/packages/">https://www.eclipse.org/downloads/packages/</a> for your platform. You need the PDE plugins for WOLips development. 

2) In the eGit perspective, add the WOLips github repository

3) Right click on your repository's "Working Tree" and "Import Projects..." to import the WOLips project
   
4) Switch to your plugin development view. You can now build the project with maven for the first time, which generates the necessary woenvironment.jar and woproject-ant-tasks.jar files and the entire project should compile in Eclipse without errors.


### Run WOLips in Eclipse

To run WOLips with your Eclipse installation and all your existing plugins,

1) Open the wolips plugin at wolips/wolips_wolips/core/plugins/org.objectstyle.wolips/plugin.xml
   
2) Click on "Launch an Eclipse application" under the Testing heading. You can also choose to debug here if you want to set breakpoints and debug. Once you do this once, a launcher will be added to your Run/Debug configurations and you can launch from there next time.

### Run the WOLips product in Eclipse:

To run WOLips with a barebones Eclipse application and just the WOLips feature installed

1) Open the wolips product at wolips/wolips_wolips/wolips.product/wolips.product

2) Click on "Launch an Eclipse application" under the Testing heading. You can also choose to debug here if you want to set breakpoints and debug. Once you do this once, a launcher will be added to your Run/Debug configurations and you can launch from there next time.

