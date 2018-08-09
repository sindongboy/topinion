#!/bin/bash

mallet import-dir --input /Users/sindongboy/Dropbox/Documents/resource/keyphrase_extraction/nlp --output ./cosmetic.input.mallet --keep-sequence 

mallet train-topics --input cosmetic.input.mallet --num-topics 50 --output-model topic.model --output-state topic-serialized.gz --output-doc-topics cosmetic.topic --output-topic-keys cosmetic.keywords
