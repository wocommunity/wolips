#!/bin/sh
archive=EntityModeler-macosx.cocoa.x86_64.zip
basedir=`dirname $0`
basedir=`(cd ${basedir}; pwd -P)`

if [ -e "${archive}" ]; then
  rm -fr dist
  mkdir -p dist
  (cd dist; unzip -q ../${archive})
  mv dist/EntityModeler/* dist
  rm -fr dist/EntityModeler
  cd dist
fi

if [ ! -e "Entity Modeler.app" ]; then
  echo "Entity Modeler.app not found in current directory"
  exit 1
fi
contents="Entity Modeler.app/Contents"
resources="${contents}/Resources"
test -d configuration && mv configuration "${resources}"
test -d plugins && mv plugins "${resources}"
cp ${basedir}/icons/EOModel.icns "${resources}"
cp ${basedir}/Info.plist "${contents}"
version=`grep '<product' "${basedir}/EntityModeler.product"  | sed -E 's/.*version="([^"]*)".*/\1/'`
sed "s/__VERSION__/${version}/" "${contents}/Info.plist" > Info.plist.$$
mv Info.plist.$$ "${contents}/Info.plist"

rm -f EntityModeler-*.zip
zip -qr EntityModeler-${version}.zip Entity\ Modeler.app