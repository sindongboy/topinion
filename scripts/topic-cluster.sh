#!/bin/bash

function usage() {
	echo "usage: $0 [options]"
	echo "	-h	help"
	echo "	-t	type of document [ raw | nlp ]"
	echo "	-i	collection path"
	echo "	-k	keyword mapping output file"
	echo "	-m	topic mapping output file"
    echo "  -o  path to where the model is saved"
	echo "	-c	number of topics [optional]"
	exit 1
}

if [[ $# -eq 0 ]]; then
	usage
fi

TYPE=""
INPUT=""
KEYWORD=""
TOPIC=""
while test $# -gt 0; do
	case "$1" in
		-h)
			usage ;;
		-i)
			shift
			INPUT=$1
			shift ;;
		-k)
			shift
			KEYWORD=$1
			shift ;;
		-t)
			shift
			TYPE=$1
			shift ;;
		-m)
			shift
			TOPIC=$1
			shift ;;
		-c)
			shift
			NUM_TOPIC=$1
			shift ;;
        -o)
            shift
            OUTPUT=$1
            shift ;;
		*)
			break ;;
	esac
done

if [[ -z ${TYPE} ]] || [[ -z ${INPUT} ]] || [[ -z ${KEYWORD} ]] || [[ -z ${TOPIC} ]] || [[ -z ${OUTPUT} ]]; then
	usage
fi

# env
CONFIG="../config"
RESOURCE="../resource"
NLPCONFIG="/Users/sindongboy/Dropbox/Documents/workspace/nlp_indexterms/config"
NLPRES="/Users/sindongboy/Dropbox/Documents/workspace/nlp_indexterms/resource"
TARGET="../target/topinion-1.0.0.jar"

# dependency
DEP=`find ../lib -type f -name "*" | awk '{printf("%s:", $0);}' | sed 's/:$//g'`
CP="${DEP}:${CONFIG}:${RESOURCE}:${NLPRES}:${NLPCONFIG}:${TARGET}"

if [[ ${TYPE} == raw ]]; then
	if [[ ! -z ${NUM_TOPIC} ]]; then
		java -Xmx4G -Dfile.encoding=UTF-8 -cp ${CP} com.skplanet.nlp.driver.RawLDADriver -i ${INPUT} -k ${KEYWORD} -t ${TOPIC} -c ${NUM_TOPIC} -o ${OUTPUT}
	else
		java -Xmx4G -Dfile.encoding=UTF-8 -cp ${CP} com.skplanet.nlp.driver.RawLDADriver -i ${INPUT} -k ${KEYWORD} -t ${TOPIC} -o ${OUTPUT}
	fi
elif [[ ${TYPE} == nlp ]]; then
	if [[ ! -z ${NUM_TOPIC} ]]; then
		java -Xmx4G -Dfile.encoding=UTF-8 -cp ${CP} com.skplanet.nlp.driver.NLPLDADriver -i ${INPUT} -k ${KEYWORD} -t ${TOPIC} -c ${NUM_TOPIC} -o ${OUTPUT}
	else
		java -Xmx4G -Dfile.encoding=UTF-8 -cp ${CP} com.skplanet.nlp.driver.NLPLDADriver -i ${INPUT} -k ${KEYWORD} -t ${TOPIC} -o ${OUTPUT}
	fi
else
	echo "check type!!"
	usage
fi
