#!/bin/bash
# History:
#   2013-06-14 v0.1
#   2013-07-18 v0.2

function show_usage() {
    echo "Decompile one .class file into .java file, and edit the .java file."
    echo ""
    echo "Usage:"
    echo "    $script_name  package-name  class-name"
    echo "    $script_name  package-name.class-name"
    echo "    $script_name  package-name/class-name"
    exit -1
}

#============== main ================

script_path=${0%/*}
script_name=${0##*/}
if [ "x$1" == "x-h" ]; then
    show_usage
fi
if [ $# -eq 1 ]; then
    str=$1
elif [ $# -eq 2 ]; then
    str=$1.$2
else
    show_usage
fi

JAD=$script_path/jad158g.win/jad.exe
# need to add notepad++'s install path to 'PATH' 
EDITOR=notepad++

full_name=`echo $str | sed 's/\./\//g'`
echo $full_name
$JAD -pi64 -o $full_name

class_name=`echo $full_name | awk -F"/" '{print $NF}'`
echo $class_name
$EDITOR  $class_name.jad

exit 0