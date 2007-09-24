WORKSPACE=/Users/mschrag/Documents/workspace
ECLIPSE=/Users/mschrag/Desktop/eclipse
TEMPDIR=/tmp/eomodeldocjar
OUTPUT=/tmp

mkdir -p $TEMPDIR
cp -r $WORKSPACE/org.objectstyle.wolips.eomodeler/bin/* $TEMPDIR
cp -r $WORKSPACE/org.objectstyle.wolips.eomodeler.core/bin/* $TEMPDIR
cp -r $WORKSPACE/org.objectstyle.wolips.eomodeler.eclipse/bin/* $TEMPDIR
cp -r $WORKSPACE/org.objectstyle.wolips.thirdparty.cayenne/bin/* $TEMPDIR
cp -r $WORKSPACE/woproject/wolips/eomodeldoc/bin/* $TEMPDIR
cp -r $WORKSPACE/woproject/wolips/core/plugins/org.objectstyle.wolips.eomodeler.doc/bin/* $TEMPDIR
cp -r $WORKSPACE/woproject/wolips/core/plugins/org.objectstyle.wolips.eomodeler.factories/bin/* $TEMPDIR

cd $TEMPDIR

cp $ECLIPSE/plugins/org.eclipse.equinox.common_3.3.0.v20070426.jar $TEMPDIR
jar xvf org.eclipse.equinox.common_3.3.0.v20070426.jar
cp $WORKSPACE/org.objectstyle.wolips.thirdparty.cayenne/lib/cayenne-1.2M12.jar $TEMPDIR
jar xvf cayenne-1.2M12.jar

rm *.jar
rm -rf META-INF
rm /tmp/MANIFEST.MF
echo Manifest-Version: 1.0 >> /tmp/MANIFEST.MF
echo Main-Class: eomodeldoc.EOModelDoc >> /tmp/MANIFEST.MF

rm $OUTPUT/eomodeldoc.jar
jar cvfm $OUTPUT/eomodeldoc.jar /tmp/MANIFEST.MF *
rm /tmp/MANIFEST.MF
