#!/bin/bash


for src in `find . -type f -name "*keyword"`
do
    echo "${src}"
    cat=`echo "${src}" | sed 's/\.keyword//g'`

    cat ${src} | cut -f 2 | awk '{for(i=1;i<=NF;i++){printf("%s\n", $i);}}' | sort | uniq -c | sort -nr > ${cat}.result
done 
