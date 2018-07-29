#!/bin/sh

# This script copies the web site from the
# working directory to the official web site.
# It archives the old site under jatha.previous
# and copies everything under the www/ directory
# into a new jatha directory.

# Same as deploy except that it doesn't copy jar files
# from the build directory.

SYSTEM=jatha
WWW_ROOT=${WEB_ROOT}/research/ai
WORKING_ROOT=${HOME}/projects/jatha/www

echo "Deploying ${SYSTEM} from ${WORKING_ROOT} to ${WWW_ROOT}"

# Move the old web site.
if [ -d ${WWW_ROOT}/${SYSTEM}.previous ]
then
  echo "Deleting ${WWW_ROOT}/${SYSTEM}.previous"
  rm -rf ${WWW_ROOT}/${SYSTEM}.previous
fi

# Store the old web site.
echo "Archiving ${WWW_ROOT}/${SYSTEM} to ${WWW_ROOT}/${SYSTEM}.previous"
mv ${WWW_ROOT}/${SYSTEM} ${WWW_ROOT}/${SYSTEM}.previous

# Copy the files
echo "Copying from ${WORKING_ROOT} to ${WWW_ROOT}/${SYSTEM}"
mkdir ${WWW_ROOT}/${SYSTEM}
cp -pr ${WORKING_ROOT}/* ${WWW_ROOT}/${SYSTEM}/

# Remove all CVS directories in the new tree.
echo "Removing CVS directories in ${WWW_ROOT}/${SYSTEM}"
for cvsdir in `find ${WWW_ROOT}/${SYSTEM} -name CVS`
do
  echo "  Deleting $cvsdir"
  rm -rf $cvsdir
done

# Copy ChangeLog.
echo "Copying ChangeLog"
cp ${JATHA_HOME}/src/ChangeLog.txt ${WWW_ROOT}/${SYSTEM}/download/

# Copy License
echo "Copying License"
mkdir ${WWW_ROOT}/${SYSTEM}/download/license
cp ${JATHA_HOME}/license/jatha-license.txt ${WWW_ROOT}/${SYSTEM}/download/license/
cp ${JATHA_HOME}/license/lgpl.txt          ${WWW_ROOT}/${SYSTEM}/download/license/

echo "Done deploying ${SYSTEM} web site"

