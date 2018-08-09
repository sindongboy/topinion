#!/bin/bash

function usage() {
	echo "usage: $0 [options]"
	echo "	-h	help"
	echo "	-m	mode [ \"s\" for single \"m\" for multiple ]"
	echo "	-i	input [ file for single mode, path for multiple mode]"
	echo "	-o	output path"
	exit 1
}

if [[ $# -eq 0 ]]; then
	usage
fi

while test $# -gt 0; do
	case "$1" in
		-h)
			usage ;;
		-m)
			shift
			MODE=$1
			shift ;;
		-i)
			shift
			INPUT=$1
			shift ;;
		-o)
			shift
			OUTPUT=$1
			shift ;;
		*)
			break ;;
	esac
done

if [[ -z ${MODE} ]] || [[ -z ${INPUT} ]] || [[ -z ${OUTPUT} ]]; then
	usage
fi

# env
NLPCONFIG="/Users/sindongboy/Dropbox/Documents/workspace/nlp_indexterms/config"
CONFIG="../config"
RESOURCE="../resource"
NLPRESOURCE="/Users/sindongboy/Dropbox/Documents/workspace/nlp_indexterms/resource"
TARGET="../target/topinion-1.0.0.jar"

# dependency
DEP=`find ../lib -type f -name "*" | awk '{printf("%s:", $0);}' | sed 's/:$//g'`

CP="${DEP}:${TARGET}:${NLPCONFIG}:${NLPRESOURCE}:${CONFIG}:${RESOURCE}"

java -Xmx4G -Dfile.encoding=UTF-8 -cp ${CP} com.skplanet.nlp.driver.CreateNLPDocument -m ${MODE} -i ${INPUT} -o ${OUTPUT}
