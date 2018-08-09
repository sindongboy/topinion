package com.skplanet.nlp.driver;

import com.skplanet.nlp.NLPAPI;
import com.skplanet.nlp.NLPDoc;
import com.skplanet.nlp.PROP;
import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.morph.Morph;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Create NLP Document {@link com.skplanet.nlp.data.NLPDocument} from Raw text
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 11/7/14.
 */
public class CreateNLPDocument {
    // logger
    private static final Logger LOGGER = Logger.getLogger(CreateNLPDocument.class.getName());

    // NLP
    private static NLPAPI nlp = new NLPAPI("nlp_api.properties", Configuration.CLASSPATH_LOAD);

    // NLP Tag Filter
    private static Set<String> nlpTag = null;

    // Format Option
    private static boolean wordOnly = false;

    public static void main(String[] args) throws IOException {
        // ========= Interfaces ========= //
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("m", "mode", true, "run type [\"s\" for single document | \"m\" for multiple documents]", true);
        cli.addOption("i", "input", true, "input [file for single mode, path for multiple mode]", true);
        cli.addOption("o", "output", true, "output path", true);
        cli.addOption("t", "tag", false, "use nlp tag filter?", false);
        cli.addOption("l", "lex", false, "word only?", false);
        cli.parseOptions(args);

        // ========= Configuration ========= //

        // nlp tag filter
        if (cli.hasOption("t")) {
            Configuration config = Configuration.getInstance();
            config.loadProperties(PROP.MAIN_CONFIG_NAME);
            nlpTag = new HashSet<String>();
            Collections.addAll(nlpTag, config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.TARGET_TAG).split(","));
        }

        File input = new File(cli.getOption("i"));
        if (cli.getOption("m").equals("m")) {
            if (!input.isDirectory()) {
                LOGGER.error("input must be a directory in multiple mode");
                System.exit(1);
            }
            multipleMode(input.listFiles(), new File(cli.getOption("o")));
        } else if (cli.getOption("m").equals("s")) {

        } else {
            LOGGER.error("run type must be either \"s\" or \"m\"");
            System.exit(1);
        }

    }

    /**
     * Multiple document processing
     * @param fileList file list
     * @param outputPath output path
     * @throws IOException
     */
    static void multipleMode(File[] fileList, File outputPath) throws IOException {
        BufferedReader reader;
        BufferedWriter writer;
        String line;
        long btime, etime;
        LOGGER.info("processing start ....");
        btime = System.currentTimeMillis();
        int total = fileList.length;
        int count = 0;
        for (File file : fileList) {
            LOGGER.debug("document : " + file.getName() + " (" + count + "/" + total + ")");
            count++;
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(new File(outputPath + "/" + file.getName())));
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }

                List<NLPDoc> nlpDocs = nlp.doAnalyze(line);
                StringBuffer sb = new StringBuffer();
                for (NLPDoc nlpDoc : nlpDocs) {
                    String str = "";
                    for (Morph morph : nlpDoc.getMorphs().getMorphs()) {
                        if (morph == null) {
                            break;
                        }
                        if (nlpTag != null) {
                            if (nlpTag.contains(morph.getPosStr())) {
                                if (wordOnly) {
                                    str += morph.getTextStr() + " ";
                                } else {
                                    str += morph.getTextStr() + "/" + morph.getPosStr() + " ";
                                }
                            }
                        } else {
                            if (wordOnly) {
                                str += morph.getTextStr() + " ";
                            } else {
                                str += morph.getTextStr() + "/" + morph.getPosStr() + " ";
                            }
                        }
                    }
                    sb.append(str.trim()).append("\n");
                }
                writer.write(sb.toString());
            }
            reader.close();
            writer.close();
        }
        etime = System.currentTimeMillis();
        LOGGER.info("processing done in " + (etime - btime) + " msec.");
    }

    /**
     * Single document processing
     * @param file single file
     * @param outputPath output path
     * @throws IOException
     */
    static void singleMode(File file, File outputPath) throws IOException{
        BufferedReader reader;
        BufferedWriter writer;
        String line;
        long btime, etime;
        LOGGER.info("processing start ....");
        btime = System.currentTimeMillis();
        reader = new BufferedReader(new FileReader(file));
        writer = new BufferedWriter(new FileWriter(new File(outputPath + "/" + file.getName())));
        while ((line = reader.readLine()) != null) {
            if (line.trim().length() == 0) {
                continue;
            }

            List<NLPDoc> nlpDocs = nlp.doAnalyze(line);
            StringBuffer sb = new StringBuffer();
            for (NLPDoc nlpDoc : nlpDocs) {
                String str = "";
                for (Morph morph : nlpDoc.getMorphs().getMorphs()) {
                    if (morph == null) {
                        break;
                    }
                    str += morph.getTextStr() + "/" + morph.getPosStr() + " ";
                }
                sb.append(str.trim()).append("\n");
            }
            writer.write(sb.toString());
        }
        reader.close();
        writer.close();
        etime = System.currentTimeMillis();
        LOGGER.info("processing done in " + (etime - btime) + " msec.");
    }
}
