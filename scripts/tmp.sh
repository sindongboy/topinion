#!/bin/bash

custom=$1

if [[ ! -z ${custom} ]]; then 
    ./topic-cluster.sh -t nlp -i /Users/sindongboy/Dropbox/Documents/resource/keyphrase_extraction/digital/nlp/${custom} -k ./${custom}.keyword -m ${custom}.topic -o ./${custom}.model -c 10
    exit 1
fi

for cat in `find /Users/sindongboy/Dropbox/Documents/resource/keyphrase_extraction/digital/nlp -type d -name "*NLP"`
do 
    echo "${cat}"
    catname=`echo "${cat}" | grep -o "[0-9][0-9]*-NLP" | sed 's/-NLP//g'`
    echo "$catname"

    ./topic-cluster.sh -t nlp -i ${cat} -k ./${catname}.keyword -m ${catname}.topic -o ./${catname}.model -c 10
done

