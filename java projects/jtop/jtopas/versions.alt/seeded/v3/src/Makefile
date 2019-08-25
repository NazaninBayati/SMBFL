current_dir=`pwd`
CLASSPATH=${current_dir}:${current_dir}/junit:/nfs/spectre/u5/dohy/Galileo/gal:/nfs/spectre/u5/dohy/Galileo/gal/BCEL:/nfs/spectre/u5/dohy/Galileo/gal/galileo/lib/commons-collections.jar

all:
	@find . -name "*.java" | xargs javac -classpath ${CLASSPATH} -deprecation > /dev/null
#find . -name "*.java" | xargs javac -deprecation > /dev/null
.PHONY: all

