package com.skplanet.nlp.io;

import com.skplanet.nlp.data.Document;

import java.util.List;

/**
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 9/12/14.
 */
public interface Loader {

    /**
     * Load Resources
     */
    public void load(String inputPath);

    /**
     * Get Loaded Documents
     * @return loaded documents
     */
    public List<Document> getDocuments();

}
