#!/bin/bash

#
# This script convert all the PIC sequence diagrams to a SVG file.
#

#
# NOTE: You need to install "plotutils" (http://www.gnu.org/s/plotutils)
#       (in debian like OS: apt-get install plotutils)
#     

SCRIPT_FOLDER="$( cd "$( dirname "$0" )" && pwd )"

for diagram in `find $SCRIPT_FOLDER | grep -v "sequence\.pic" | grep -v "target" | grep "\.pic"` 
do
	output=`echo $diagram | sed "s/\.pic/\.svg/"`
	pic2plot --page-size a4 -Tsvg $diagram > $output
done

