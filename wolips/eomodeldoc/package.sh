#/bin/bash
WORKSPACE=/Users/mschrag/Documents/workspace
ECLIPSE=/Developer/Applications/eclipse
TEMPDIR=/tmp/eomodeldocjar
MANIFEST=/tmp/MANIFEST.MF
OUTPUT=/tmp/eomodeldoc.jar

echo EOModelDoc
rm -rf $TEMPDIR
mkdir -p $TEMPDIR
cd $TEMPDIR

echo Copying classes ...
cp -r $WORKSPACE/woproject/wolips/core/plugins/org.objectstyle.wolips.eomodeler/bin/* .
cp -r $WORKSPACE/woproject/wolips/core/plugins/org.objectstyle.wolips.eomodeler.core/bin/* .
cp -r $WORKSPACE/woproject/wolips/core/plugins/org.objectstyle.wolips.eomodeler.eclipse/bin/* .
#cp -r $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.cayenne/bin/* .
cp -r $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.commonscollections/bin/* .
cp -r $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.velocity/bin/* .
cp -r $WORKSPACE/woproject/wolips/base/plugins/org.objectstyle.wolips.baseforplugins/bin/* .
cp -r $WORKSPACE/woproject/wolips/eomodeldoc/bin/* .
cp -r $WORKSPACE/woproject/wolips/core/plugins/org.objectstyle.wolips.eomodeler.doc/bin/* .
cp -r $WORKSPACE/woproject/wolips/core/plugins/org.objectstyle.wolips.eomodeler.factories/bin/* .

echo Unjarring plugins ...
jar xvf $ECLIPSE/plugins/org.eclipse.equinox.common_3.4.0.v20080421-2006.jar > /dev/null
jar xvf $ECLIPSE/plugins/org.eclipse.core.resources_3.4.0.v20080604-1400.jar > /dev/null
jar xvf $ECLIPSE/plugins/org.eclipse.core.jobs_3.4.0.v20080512.jar > /dev/null
jar xvf $WORKSPACE/woproject/wolips/core/plugins/org.objectstyle.wolips.woproject/lib/woproject.jar > /dev/null
#jar xvf $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.cayenne/lib/cayenne-1.2M12.jar > /dev/null
jar xvf $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.commonscollections/lib/commons-collections-3.1.jar > /dev/null
jar xvf $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.velocity/lib/avalon-logkit-2.1.jar > /dev/null
jar xvf $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.velocity/lib/commons-beanutils-1.7.0.jar > /dev/null
jar xvf $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.velocity/lib/commons-chain-1.1.jar > /dev/null
jar xvf $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.velocity/lib/commons-digester-1.8.jar > /dev/null
jar xvf $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.velocity/lib/commons-lang-2.1.jar > /dev/null
jar xvf $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.velocity/lib/commons-validator-1.3.1.jar > /dev/null
jar xvf $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.velocity/lib/oro-2.0.8.jar > /dev/null
jar xvf $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.velocity/lib/velocity-1.5.jar > /dev/null
jar xvf $WORKSPACE/woproject/wolips/3rdparty/plugins/org.objectstyle.wolips.thirdparty.velocity/lib/velocity-tools-generic-1.4.jar > /dev/null

rm -rf META-INF
if [ -e $MANIFEST ]; then
rm $MANIFEST
fi
if [ -e $OUTPUT ]; then
rm $OUTPUT
fi

echo Creating manifest ...
echo Manifest-Version: 1.0 >> $MANIFEST
echo Main-Class: eomodeldoc.EOModelDoc >> $MANIFEST

echo Creating $OUTPUT ...
jar cvfm $OUTPUT $MANIFEST * > /dev/null
rm $MANIFEST
