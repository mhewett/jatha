#!/bin/sh

# This script creates a jatha.jar file.
TMP=${HOME}/tmp
CLASS_DIR=${JATHA_HOME}/build/classes
LIB_DIR=${JATHA_HOME}/build/lib

echo "Copying initialization files to build directory"
if [ -d ${CLASS_DIR}/init ]
then
  rm -rf ${CLASS_DIR}/init
fi
cp -pr ${JATHA_HOME}/src/init ${CLASS_DIR}/

echo "Creating jatha.jar using manifest jatha.mf"
cd ${CLASS_DIR}
echo "Entering ${PWD}"
jar cfm jatha.jar ${JATHA_HOME}/misc/manifest/jatha.mf org/jatha init
mv jatha.jar ${LIB_DIR}/
echo "Done creating the Jatha jar file"

cd ${TMP}
echo "Creating jatha-src.jar"
if [ -d ${TMP}/jatha ]
then
  rm -rf ${TMP}/jatha/*
else
  mkdir ${TMP}/jatha
fi

cd ${JATHA_HOME}
echo "Entering ${PWD}"
cp -pr ${JATHA_HOME}/src ${TMP}/jatha/
cp -pr ${JATHA_HOME}/license ${TMP}/jatha/
cp -pr ${JATHA_HOME}/README.txt ${TMP}/jatha/
cd ${TMP}
echo "Entering ${PWD}"

echo "Removing CVS directories in ${PWD}/jatha"
for cvsdir in `find ${TMP}/jatha -name CVS`
do
  echo "  Deleting $cvsdir"
  rm -rf $cvsdir
done

cd ${TMP}/jatha
echo "Entering ${PWD}"
echo "Creating build directories files"
mkdir build
mkdir build/classes
mkdir build/lib
cp -p ${JATHA_HOME}/build/classes/README.txt build/classes/
cp -p ${JATHA_HOME}/build/lib/README.txt build/lib/
cp -p ${JATHA_HOME}/build.xml ./

echo "Creating jar file"
cd ${TMP}
jar cfm  jatha-src.jar ${JATHA_HOME}/misc/manifest/jatha-src.mf jatha
echo "Done creating the Jatha source jar file"

mv jatha-src.jar ${LIB_DIR}/
echo "Moved jatha-src.jar to ${LIB_DIR}"

cp ${LIB_DIR}/jatha.jar ${ALGERNON_HOME}/lib/
echo "Copied jatha.jar to ${ALGERNON_HOME}/lib/"

echo "done"
