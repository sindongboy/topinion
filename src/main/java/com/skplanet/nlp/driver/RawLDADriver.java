package com.skplanet.nlp.driver;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.cluster.LDACluster;
import com.skplanet.nlp.data.Document;
import com.skplanet.nlp.io.Loader;
import com.skplanet.nlp.io.RawDocumentLoader;
import com.skplanet.nlp.util.MapUtil;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Topic clustering driver for Raw Document set
 *
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 11/6/14.
 */
public class RawLDADriver {
    // logger
    private static final Logger LOGGER = Logger.getLogger(RawLDADriver.class.getName());

    public static void main(String[] args) throws IOException {
        // ------------------- //
        // interfaces
        // ------------------- //
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("i", "input", true, "collection path", true);
        cli.addOption("k", "keyword", true, "keyword file", true);
        cli.addOption("t", "topic", true, "topic file", true);
        cli.addOption("c", "class", true, "number of topic", false);
        cli.parseOptions(args);

        // ------------------- //
        // document loading
        // ------------------- //
        long btime, etime;
        LOGGER.info("Document Loading ....");
        btime = System.currentTimeMillis();
        Loader loader = new RawDocumentLoader();
        loader.load(cli.getOption("i"));
        List<Document> documents = loader.getDocuments();
        etime = System.currentTimeMillis();
        LOGGER.info("Document Loading done in " + (etime - btime) + " msec.");


        // ------------------- //
        // analysis
        // ------------------- //
        LOGGER.info("Clustering ....");
        btime = System.currentTimeMillis();
        LDACluster cluster;
        if (cli.hasOption("c")) {
            cluster = new LDACluster(cli.getOption("c"));
        } else {
            cluster = new LDACluster();
        }
        cluster.buildModel(documents);
        cluster.analysis(documents);
        etime = System.currentTimeMillis();
        LOGGER.info("Clustering done in " + (etime - btime) + " msec.");


        // ------------------- //
        // result writing
        // ------------------- //
        LOGGER.info("Topic Allocation start ....");
        BufferedWriter keywordWriter = new BufferedWriter(new FileWriter(new File(cli.getOption("k"))));
        BufferedWriter topicWriter = new BufferedWriter(new FileWriter(new File(cli.getOption("t"))));
        Map<Integer, List<String>> topicKeywords = cluster.getTopicKeywords();
        Map<String, double[]> topicMatrix = cluster.getTopicMatrix();
        // topic writer
        btime = System.currentTimeMillis();
        NumberFormat format = new DecimalFormat("#0.00000");
        for (Document document : documents) {
            topicWriter.write(document.getDocumentName() + "\t");
            double[] prob = topicMatrix.get(document.getDocumentName());
            Map<Integer, Double> maxProp = new HashMap<Integer, Double>();
            for (int i = 0; i < prob.length; i++) {
                maxProp.put(i, prob[i]);
            }
            maxProp = MapUtil.sortByValue(maxProp, MapUtil.SORT_DESCENDING);
            //int maxTopic = maxProp.keySet().iterator().next() + 1;
            List<Integer> topicSorted = new ArrayList<Integer>();
            List<Double> topicProbSorted = new ArrayList<Double>();
            Iterator iter = maxProp.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                topicSorted.add((Integer) entry.getKey() + 1);
                topicProbSorted.add((Double) entry.getValue());
                topicWriter.write(((Integer) entry.getKey() + 1) + ":" + format.format(-1*(1/Math.log((Double) entry.getValue()))) + " ");
            }
            //topicWriter.write(maxTopic + ":" + format.format(maxProp.get(maxTopic - 1)));
            topicWriter.newLine();
        }
        etime = System.currentTimeMillis();
        LOGGER.info("Topic Allocation done in " + (etime - btime) + " msec.");

        // keyword writer
        LOGGER.info("Topic Keyword writing start ....");
        btime = System.currentTimeMillis();
        Iterator iter = topicKeywords.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            keywordWriter.write(((Integer) entry.getKey() + 1) + "\t");
            List<String> keywords = (List<String>) entry.getValue();
            for (String k : keywords) {
                keywordWriter.write(k + " ");
            }
            keywordWriter.newLine();
        }
        etime = System.currentTimeMillis();
        LOGGER.info("Topic Keyword writing start done in " + (etime - btime) + " msec.");
        keywordWriter.close();
        topicWriter.close();
    }
}
