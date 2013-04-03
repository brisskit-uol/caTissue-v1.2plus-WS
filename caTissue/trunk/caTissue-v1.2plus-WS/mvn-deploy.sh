#!/bin/bash
#-----------------------------------------------------------------------------------------------
# Updates the eclipse development environment and deploys the ws artifact
# to the Brisskit maven repository.
#
#
# Notes:
# (1) Maven 2 must be installed
# (2) Your maven settings.xml file should be set up to point to the brisskit maven repo.
# (3) You must have authority to deploy, which depends upon having an account on the machine
#     hosting the maven repository.
#
# Author: Jeff Lusted (jl99@leicester.ac.uk)
#-----------------------------------------------------------------------------------------------


#===========================================================================
# Cleaning the lib directory...
#===========================================================================
rm -Rf ./WebContent/WEB-INF/lib/*

#===========================================================================
# Changing the pom packaging to 'war'...
#===========================================================================
mv ./pom.xml ./pom.xml.bak
sed s:\<packaging\>pom\<\/packaging\>:\<packaging\>war\<\/packaging\>: <./pom.xml.bak >./pom.xml

#===========================================================================
# Clean the output directories, setup the classpath and the classes...
#=========================================================================== 
mvn clean eclipse:eclipse compile

#===========================================================================
# Changing the pom packaging back to 'pom'...
#===========================================================================
mv ./pom.xml ./pom.xml.bak
sed s:\<packaging\>war\<\/packaging\>:\<packaging\>pom\<\/packaging\>: <./pom.xml.bak >./pom.xml

#===========================================================================
# Do a build and deploy into the maven repo
#=========================================================================== 
mvn deploy

#===================================================================================
# Ensure the war's lib directory contents is available to the local environment...
#=================================================================================== 
mkdir ./target/tmp
mv ./target/catissueplusWS.war ./target/tmp
cd ./target/tmp/
unzip catissueplusWS.war >/dev/null 2>/dev/null
cd ../..
cp ./target/tmp/WEB-INF/lib/* ./WebContent/WEB-INF/lib

#===========================================================================
# Tidy up...
#===========================================================================
rm ./pom.xml.bak
rm -Rf ./target/tmp

echo ""
echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
echo "         Please refresh your Eclipse environment.            "
echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
