package com.skplanet.nlp.io;

import com.skplanet.nlp.PROP;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.data.Document;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.*;
/**
 * Abstract Document Loader
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 9/12/14.
 */
public abstract class AbstractLoader implements Loader {
    // logger
    private static final Logger LOGGER = Logger.getLogger(AbstractLoader.class.getName());

    // document list
    protected List<Document> documents = null;

    // set of nlp tag used
    protected Set<String> nlpTagSet = null;

    // set of stopword
    protected Set<String> stopwords = null;

    // keyword weighting
    protected Map<String, Integer> keywordWeight = null;

    /**
     * Sole Constructor
     */
    protected AbstractLoader() {
        this.nlpTagSet = new HashSet<String>();
        this.stopwords = new HashSet<String>();
        this.keywordWeight = new HashMap<String, Integer>();

        // ====== TARGET NLP TAG SET ===== //
        LOGGER.info("Loading NLP Tag Set ....");
        Configuration config = Configuration.getInstance();
        try {
            config.loadProperties(PROP.MAIN_CONFIG_NAME);
            Collections.addAll(nlpTagSet, config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.TARGET_TAG).split(","));
        } catch (IOException e) {
            LOGGER.error("Failed load NLP Configuration : " + PROP.MAIN_CONFIG_NAME, e);
        }
        LOGGER.info("Loading NLP Tag Set Done");

        // ====== STOPWORDS ===== //
        LOGGER.info("Loading Stopword ....");
        URL stopwordURL = config.getResource(PROP.STOPWORD_DICT);
        BufferedReader reader;
        String line;
        try {
            reader = new BufferedReader(new FileReader(new File(stopwordURL.getFile())));
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0 || line.startsWith("#")) {
                    continue;
                }
                this.stopwords.add(line.trim().toLowerCase());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            LOGGER.error("Stopword File not found : " + PROP.STOPWORD_DICT, e);
        } catch (IOException e) {
            LOGGER.error("Failed to read stopword file : " + PROP.STOPWORD_DICT, e);
        }
        LOGGER.info("Loading Stopword Done");

        // ====== KEYWORD WEIGHTING ===== //
        LOGGER.info("Loading keyword weighting ....");
        URL keywordWeightURL = config.getResource(PROP.KEYWORD_WEIGHT);
        try {
            reader = new BufferedReader(new FileReader(new File(keywordWeightURL.getFile())));
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0 || line.startsWith("#")) {
                    continue;
                }
                String[] fields = line.split("\\t");
                if (fields.length != 2) {
                    LOGGER.debug("wrong format : " + line);
                    continue;
                }

                this.keywordWeight.put(fields[0].toLowerCase().trim(), Integer.parseInt(fields[1].trim()));
            }
            reader.close();
        } catch (FileNotFoundException e) {
            LOGGER.error("Keyword weighting file not found: " + PROP.KEYWORD_WEIGHT, e);
        } catch (IOException e) {
            LOGGER.error("Failed to read keyword weighting file : " + PROP.KEYWORD_WEIGHT, e);
        }
        LOGGER.info("Loading keyword weighting done");
    }

    /**
     * Check if the given word is stopword
     * @param word a word to be tested
     * @return true if the word is stopword
     */
    protected abstract boolean isStopword(String word);

    /**
     * Get the weighting for the given word
     * @param word a word to be tested
     * @return weighting factor
     */
    protected abstract int keywordWeighting(String word);
}
