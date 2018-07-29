#!/bin/sh

############################################################
# JAVADOC generator file for Jatha
# JavaDocs end up in ${JATHA_HOME}/www/doc/api
#
# Note: This uses the JDK1.4 version of javadoc
#
#
# Mike Hewett   10 Mar 1999
#                9 Mar 2001  Modified for PharmGen
#               13 Feb 2003  Modified for Jatha
############################################################

JATHA_LIB=${JATHA_HOME}/lib

JAVADOC=${JAVA_HOME}/bin/javadoc

# Location of documentation for Java
# The -link option lets us link our docs to theirs.
JAVA_SRC_DOC=http://java.sun.com/j2se/1.4.2/docs/api/
JUNIT_SRC_DOC=http://www.junit.org/junit/javadoc/3.8.1/

# Root location of my code to be documented.
SOURCE_ROOT=${JATHA_HOME}/src

# Library contains external programs we reference
LIB=${JATHA_LIB}/junit.jar:${SOURCE_ROOT}

HEADER='Jatha Software'
WINDOW_TITLE='Jatha API documentation'

# Should use -header and -windowtitle here.
JAVADOC_OPTIONS="-use -charset iso-8859-1 -private -link ${JAVA_SRC_DOC} -link ${JUNIT_SRC_DOC} -classpath ${LIB} "
# -sourcepath ${SOURCE_ROOT} -classpath ${LIB}

# What packages do we want to document?
# JAVADOC doesn't recursively enter directories, so we list them all
# PACKAGES='javalib javalib.clock javalib.compress javalib.net javalib.graph javalib.picture javalib.sort'

# Destination of documentation web pages
DOC_DIR=${JATHA_HOME}/www/doc/api

# Where are we now?  Needed later.
CURRENT=${PWD}

# - - - - - Now we are ready to run - - - - - - - - - - - - - - - -

# Go to the source location (this helps javadoc)
echo "cd ${SOURCE_ROOT}"
cd ${SOURCE_ROOT}

# Collect the list of packages to process
SOURCE_PACKAGES="@${JATHA_HOME}/src/doc/packages.list"

# Run javadoc, including any command-line options
echo "Executing ${JAVADOC}  $* -header ${HEADER} -windowtitle ${WINDOW_TITLE} ${JAVADOC_OPTIONS} -d ${DOC_DIR} ${SOURCE_PACKAGES}"
${JAVADOC}  $* -header "${HEADER}" -windowtitle "${WINDOW_TITLE}" ${JAVADOC_OPTIONS} -d ${DOC_DIR} ${SOURCE_PACKAGES}


# - - - - - Done running - show the results in Netscape - - - - - -

cd ${CURRENT}

# --------- Copy the results to the Jatha web site

# JATHA_WEB=${WEB_ROOT}/research/ai/jatha/doc/api

# rm -rf ${JATHA_WEB}/*

# cp -pr ${DOC_DIR}/* ${JATHA_WEB}/
