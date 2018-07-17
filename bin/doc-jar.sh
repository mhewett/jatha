#!/bin/sh

# This script creates an jatha-doc.jar file
# The user should set the JATHA_HOME variable.

TMP=${HOME}/tmp

if [ "JATHA_HOME" = "" ]
then
  echo "Please set JATHA_HOME before running this script."
  exit 1
fi

DOC_HOME=${JATHA_HOME}/www/doc
API_DIR=api
COMMANDS_DIR=commands

echo "Copying doc files to temporary location"
if [ -d ${TMP}/doc ]
then
  echo "  Removing old doc export directory"
  rm -rf ${TMP}/doc
fi
cp -pr ${DOC_HOME} ${TMP}
echo "  doc files copied"

# Remove all CVS directories in the new tree.
echo "Removing CVS directories in ${TMP}/doc"
for cvsdir in `find ${TMP}/doc -name CVS`
do
  echo "  Deleting $cvsdir"
  rm -rf $cvsdir
done

echo "Removing .DS_Store files in ${TMP}/doc"
for dsFile in `find ${TMP}/doc -name .DS_Store`
do
  echo "  Deleting $dsFile"
  rm -rf $dsFile
done

echo "Creating jatha-doc.jar"
cd ${TMP}
jar cfm ${TMP}/jatha-doc.jar ${JATHA_HOME}/misc/manifest/jatha-doc.mf doc

echo "Copying to the build directory"
cp ${TMP}/jatha-doc.jar ${JATHA_HOME}/build/lib/
ls -l ${JATHA_HOME}/build/lib/jatha*.jar

echo "Done creating the Jatha documentation jar file"

