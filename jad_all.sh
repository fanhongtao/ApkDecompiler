#!/bin/bash
# History:
#   2013-06-14 0.1
#       Basic version

function show_usage() {
    echo "Unpack file '.jar' into directory 'class', and decompile all .class files to .java files into directory 'jad'"
    echo ""
    echo "Usage:"
    echo "    $script_name  xxx.jar"
    exit -1
}

#============== main ================

script_path=${0%/*}
script_name=${0##*/}
if [ "x$1" == "x-h" ]; then
    show_usage
fi
if [ $# -ne 1 ]; then
    show_usage
fi

JAD=$script_path/jad158g.win/jad.exe
jar_path=${1%/*}
jar_name=${1##*/}

cd $jar_path

# Unpack file '.jar' into directory 'class'
rm -rf class
mkdir class
cd class
jar xvf ../$jar_name
class_list=`find . -type f -printf "%p\n" -name "*.class"`
cd ..

# Decompile all the '.class' files into '.java'
rm -rf jad
mkdir jad
cd jad
for file in $class_list
do
    class_path=${file%/*}
    $JAD -r -s .java ../class/$file
done

exit 0