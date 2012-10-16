#!/bin/sh

if [ ! -e "./Entity Modeler.app" ]; then
	echo "Entity Modeler.app not found in current directory"
	exit 1
fi

em_home=`dirname $0`
contents="Entity Modeler.app/Contents"
resources="${contents}/Resources"
test -d configuration && mv configuration "${resources}"
test -d plugins && mv plugins "${resources}"
cp ${em_home}/icons/EOModel.icns "${resources}"
cp ${em_home}/Info.plist "${contents}"
version=`grep '<product' "${em_home}/EntityModeler.product"  | sed -E 's/.*version="([^"]*)".*/\1/'`
sed -i "" "s/__VERSION__/${version}/" "${contents}/Info.plist"