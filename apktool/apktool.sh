#!/bin/bash
script_path=${0%/*}
java -jar "$script_path/apktool.jar" $*
