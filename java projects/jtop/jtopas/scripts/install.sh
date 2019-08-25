#!/bin/sh
if test $# -ne 2
then echo "Usage: ${0} <orig/seeded> <version>" 
     exit 1
fi

unset CLASSPATH
CLASSPATH=${experiment_root}/jtopas/source:${experiment_root}/jtopas/source/junit:${experiment_root}/jtopas/source/lib/junit.jar
export CLASSPATH

curr_dir=${experiment_root}/jtopas/scripts
rm -r ${experiment_root}/jtopas/source/*
echo Copy junit.jar into source/lib/
cp -r ${experiment_root}/jtopas/versions.alt/lib ${experiment_root}/jtopas/source

echo Copy versions.alt/$1/v$2/src into source
cp -r ${experiment_root}/jtopas/versions.alt/$1/v$2/src/* ${experiment_root}/jtopas/source
cd ${experiment_root}/jtopas/source

echo Compile Jtopas source 
find . -name "*.java" | xargs javac -encoding Cp1252 -deprecation > /dev/null

echo Copy testdrivers/v$2/junit into source/junit
cp -r ${experiment_root}/jtopas/testdrivers/v$2/junit ./
cd ./junit 
echo Compile junit test suite
find . -name "*.java" | xargs javac -encoding Cp1252 -deprecation > /dev/null
