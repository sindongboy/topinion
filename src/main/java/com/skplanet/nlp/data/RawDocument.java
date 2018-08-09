package com.skplanet.nlp.data;

/**
 * Raw Document Object
 *
 * Document consist of tokens delimited by space
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 11/7/14.
 */
public class RawDocument extends AbstractDocument {

    /**
     * Constructor
     */
    public RawDocument() {
    }

    /**
     * Get Document Name
     *
     * @return document name
     */
    @Override
    public String getDocumentName() {
        return this.documentName;
    }

    /**
     * Set Document Name
     *
     * @param name document name to be set
     */
    @Override
    public void setDocumentName(String name) {
        this.documentName = name;
    }

    /**
     * Add Token to the token list
     *
     * @param token token to be added
     */
    @Override
    public void addToken(String token) {
        this.tokenList.add(token);
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.tokenList != null) {
            for (String token : this.tokenList) {
                sb.append(token).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
