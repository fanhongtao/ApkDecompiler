#!/bin/bash
# History:
#   2012-04-03 0.1
#       Basic version
#   2012-04-04 0.2 
#       1. Handle AndroidManifest.xml. 
#       2. Put decompiled .xml files where their are.
#       3. Replace number in .xml files.

if [ $# -lt 1 ]; then
    echo "Usage: $0  apk_name [decomplie_dir]"
    exit -1
fi

old_dir=`pwd`
script_path=${0%/*}
script_name=${0##*/}
JAD=$script_path/jad158g.win/jad.exe
APKTOOL=$script_path/apktool/apktool.sh

apk_full_name=$1
apk_name=${apk_full_name##*/}

if [ $# -gt 1 ]; then
    dest_dir=$2
else
    dest_dir=`echo $apk_name | awk '{print substr($0, 0, length($0) - 4)}'`
fi

# decode resources
$APKTOOL d $apk_full_name $dest_dir

# decode .apk to .jar
dest_jar=${apk_name%.*}_dex2jar.jar

cp $apk_full_name  $dest_dir
${script_path}/dex2jar-0.0.9.8/dex2jar.sh $dest_dir/$apk_name


 
