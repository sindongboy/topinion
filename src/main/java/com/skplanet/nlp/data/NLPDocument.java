package com.skplanet.nlp.data;

import java.util.ArrayList;

/**
 * General Document Object
 * Document consist of morph/postag tokens
 *
 * ex) 맥/nng 으로/jkb 찍/vv 은/etm 엽기/nng 사진/nng
 *
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 11/6/14.
 */
public class NLPDocument extends AbstractDocument {

    /**
     * Constructor
     */
    public NLPDocument() {
    }

    @Override
    public String getDocumentName() {
        return documentName;
    }

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
        if (this.tokenList == null) {
            this.tokenList = new ArrayList<String>();
        }
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
