#!/bin/bash

if [ $# -ne 2 ]; then
    echo "Usage: $0  apk_name decomplie_dir"
    exit -1
fi

old_dir=`pwd`
script_path=${0%/*}

apk_full_name=$1
apk_name=${apk_full_name##*/}

dest_dir=$2
if [ ! -d $dest_dir ]; then
    mkdir $dest_dir
fi


# decode .apk to .jar
dest_jar=${apk_name%.*}_dex2jar.jar
cp $apk_full_name  $dest_dir
${script_path}/dex2jar-0.0.9.8/dex2jar.sh $dest_dir/$apk_name

# extract 'res' from .apk
cd $dest_dir
jar -xvf $apk_name  res

# decode .xml files from 'res' into 'dec_res'
xml_files=`find res -name "*.xml"`
for file in $xml_files
do
    target_xml_file=dec_$file
    target_path=${target_xml_file%/*}
    if [ ! -d $target_path ]; then
        echo "mkdir $target_path"
        mkdir -p $target_path
    fi
    echo $file
    java -jar ${script_path}/AXMLPrinter2.jar $file > $target_xml_file
done

