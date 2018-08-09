package com.skplanet.nlp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Document Object
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 9/15/14.
 */
public abstract class AbstractDocument implements Document {

    protected List<String> tokenList = null;

    // document name
    protected String documentName = null;

    /**
     * Sole Constructor
     */
    protected AbstractDocument() {
        this.tokenList = new ArrayList<String>();
    }

}
