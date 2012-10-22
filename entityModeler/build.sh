#!/bin/bash
if [ -z "${ECLIPSE_HOME}" ]; then
  echo '$ECLIPSE_HOME has not been set'
  exit 1
fi
java -jar ${ECLIPSE_HOME}/plugins/org.eclipse.equinox.launcher_*.jar -application org.eclipse.ant.core.antRunner -buildfile build-antrunner.xml
