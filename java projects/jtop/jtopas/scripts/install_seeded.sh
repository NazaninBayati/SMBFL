#!/bin/sh
if test $# -ne 1
then echo "Usage: ${0} <version>" 
     exit 1
fi

unset CLASSPATH
CLASSPATH=${experiment_root}/jtopas/source:${experiment_root}/jtopas/source/junit:${experiment_root}/jtopas/source/lib/junit.jar
export CLASSPATH

curr_dir=${experiment_root}/jtopas/scripts
rm -r ${experiment_root}/jtopas/source/*

echo Copy versions.alt/seeded/v$1/src into source
cp -r ${experiment_root}/jtopas/versions.alt/seeded/v$1/src/* ${experiment_root}/jtopas/source
cd ${experiment_root}/jtopas/source

echo LineEqualizing
cp ${curr_dir}/EqualizeLineNumbers* .
find ./ -name "*.cpp" >  __tmpfile
while read LINE
do
	java EqualizeLineNumbers $LINE 0 `echo $LINE | sed "s/\.cpp/\.java/"`
done < __tmpfile
rm __tmpfile
rm EqualizeLineNumbers*
echo Compile Jtopas source 
find . -name "*.java" | xargs javac -encoding Cp1252 -deprecation > /dev/null

echo Copy testdrivers/v$1/junit into source/junit
cp -r ${experiment_root}/jtopas/testdrivers/v$1/junit ./
cd ./junit 
echo Compile junit test suite
find . -name "*.java" | xargs javac -encoding Cp1252 -deprecation > /dev/null
