REM Converts POEM files to the XML serialisation
REM You first need to create a distribution JAR file by running "mvn install assembly:assembly"
REM Run without arguments to see command-line options

@echo off
java -classpath target/opmbuild-0.1-jar-with-dependencies.jar uk.ac.kcl.informatics.opmbuild.tools.PoemToXML "%1" "%2"
