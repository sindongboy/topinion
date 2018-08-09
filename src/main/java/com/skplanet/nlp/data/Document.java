package com.skplanet.nlp.data;

/**
 * Document Object
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 9/15/14.
 */
public interface Document {

    /**
     * Get Document Name
     * @return document name
     */
    public String getDocumentName();

    /**
     * Set Document Name
     * @param name document name to be set
     */
    public void setDocumentName(String name);

    /**
     * Add Token to the token list
     * @param token token to be added
     */
    public void addToken(String token);
}
