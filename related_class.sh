#!/bin/bash
# 
#
# History:
#   2013-06-14 0.1
#       Basic version

function show_usage() {
    echo "Grep to search '.java' files that imported the specified class."
    echo ""
    echo "Usage:"
    echo "    $script_name  package-name  class-name"
    echo "    $script_name  package-name.class-name"
    echo "    $script_name  package-name/class-name"
    exit -1
}

function list_files() {
    for file in $*
    do
        echo $file
    done
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

full_name=`echo $str | sed 's/\//\./g'`
find_str="import $full_name;"
echo $find_str
echo

echo "imported files:"
imported_files=`find . -type f -printf "\"%p\"\n" | xargs grep "$find_str" | awk -F: '{print $1}'`
list_files $imported_files

exit 0