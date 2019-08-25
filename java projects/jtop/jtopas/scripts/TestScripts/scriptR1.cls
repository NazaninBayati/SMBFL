#!/bin/sh
# The following is an R script type
unset CLASSPATH
CLASSPATH=/nfs/spectre/u5/dohy/Prior/jtopas/source:/nfs/spectre/u5/dohy/Prior/jtopas/source/junit:/nfs/spectre/u5/dohy/Prior/jtopas/source/lib/junit.jar:.
export CLASSPATH

echo ">>>>>>>>running test 1"
java junit.textui.TestRunner de.susebox.java.util.TestTokenizerProperties  > /nfs/spectre/u5/dohy/Prior/jtopas/outputs/t1 2>&1
/nfs/spectre/u5/dohy/Prior/jtopas/testplans.alt/testscripts/RemoveTime.sh 1

echo ">>>>>>>>running test 2"
java junit.textui.TestRunner de.susebox.java.util.TestTokenProperties  > /nfs/spectre/u5/dohy/Prior/jtopas/outputs/t2 2>&1
/nfs/spectre/u5/dohy/Prior/jtopas/testplans.alt/testscripts/RemoveTime.sh 2

echo ">>>>>>>>running test 3"
java junit.textui.TestRunner de.susebox.java.util.TestInputStreamTokenizer  > /nfs/spectre/u5/dohy/Prior/jtopas/outputs/t3 2>&1
/nfs/spectre/u5/dohy/Prior/jtopas/testplans.alt/testscripts/RemoveTime.sh 3

echo ">>>>>>>>running test 4"
java junit.textui.TestRunner de.susebox.java.util.TestDifficultSituations  > /nfs/spectre/u5/dohy/Prior/jtopas/outputs/t4 2>&1
/nfs/spectre/u5/dohy/Prior/jtopas/testplans.alt/testscripts/RemoveTime.sh 4

echo ">>>>>>>>running test 5"
java junit.textui.TestRunner de.susebox.java.util.TestEmbeddedTokenizer  > /nfs/spectre/u5/dohy/Prior/jtopas/outputs/t5 2>&1
/nfs/spectre/u5/dohy/Prior/jtopas/testplans.alt/testscripts/RemoveTime.sh 5

echo ">>>>>>>>running test 6"
java junit.textui.TestRunner de.susebox.jtopas.TestPluginTokenizer  > /nfs/spectre/u5/dohy/Prior/jtopas/outputs/t6 2>&1
/nfs/spectre/u5/dohy/Prior/jtopas/testplans.alt/testscripts/RemoveTime.sh 6

echo ">>>>>>>>running test 7"
java junit.textui.TestRunner de.susebox.jtopas.TestTokenizerSpeed  > /nfs/spectre/u5/dohy/Prior/jtopas/outputs/t7 2>&1
/nfs/spectre/u5/dohy/Prior/jtopas/testplans.alt/testscripts/RemoveTime.sh 7
