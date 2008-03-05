WORKSPACE=/Users/mschrag/Documents/workspace
ECLIPSE=/Developer/Applications/eclipse
TEMPDIR=/tmp/eomodeldocjar
MANIFEST=/tmp/MANIFEST.MF
OUTPUT=/tmp/eomodeldoc.jar

mkdir -p $TEMPDIR
cd $TEMPDIR

cp -r $WORKSPACE/org.objectstyle.wolips.eomodeler/bin/* .
cp -r $WORKSPACE/org.objectstyle.wolips.eomodeler.core/bin/* .
cp -r $WORKSPACE/org.objectstyle.wolips.eomodeler.eclipse/bin/* .
cp -r $WORKSPACE/org.objectstyle.wolips.thirdparty.cayenne/bin/* .
cp -r $WORKSPACE/org.objectstyle.wolips.baseforplugins/bin/* .
cp -r $WORKSPACE/eomodeldoc/bin/* .
cp -r $WORKSPACE/org.objectstyle.wolips.eomodeler.doc/bin/* .
cp -r $WORKSPACE/org.objectstyle.wolips.eomodeler.factories/bin/* .

jar xvf $ECLIPSE/plugins/org.eclipse.equinox.common_3.3.0.v20070426.jar
jar xvf $ECLIPSE/plugins/org.eclipse.core.resources_3.3.1.R33x_v20080205.jar
jar xvf $ECLIPSE/plugins/org.eclipse.core.jobs_3.3.1.R33x_v20070709.jar
jar xvf $WORKSPACE/org.objectstyle.wolips.woproject/lib/woproject.jar
jar xvf $WORKSPACE/org.objectstyle.wolips.thirdparty.cayenne/lib/cayenne-1.2M12.jar

rm -rf META-INF
rm $MANIFEST
echo Manifest-Version: 1.0 >> $MANIFEST
echo Main-Class: eomodeldoc.EOModelDoc >> $MANIFEST

rm $OUTPUT
jar cvfm $OUTPUT $MANIFEST *
rm $MANIFEST
