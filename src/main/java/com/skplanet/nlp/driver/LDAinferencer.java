package com.skplanet.nlp.driver;

import cc.mallet.pipe.*;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.skplanet.nlp.PROP;
import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.data.Document;
import com.skplanet.nlp.io.NLPDocumentLoader;
import com.skplanet.nlp.util.MapUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Donghun Shin / donghun.shin@sk.com
 * @since 9/5/16
 */
public class LDAinferencer {
	private static final Logger LOGGER = Logger.getLogger(LDAinferencer.class.getName());

	private ParallelTopicModel model = null;
	private TopicInferencer inferencer = null;
	private static Pipe pipe = null;
	private InstanceList instanceList  = null;

	public LDAinferencer(String modelPath) throws Exception {
		this.model = ParallelTopicModel.read(new File(modelPath));
		this.inferencer = this.model.getInferencer();

		Configuration config = Configuration.getInstance();
		try {
			config.loadProperties(PROP.MAIN_CONFIG_NAME);

			// set topic number
			//topicNum = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.NUM_TOPIC));
			// set alpha sum
			//alpha = Double.parseDouble(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.ALPHA_SUM));
			// set beta
			//beta = Double.parseDouble(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.BETA));
			// set keyword number
			//keywordNum = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.NUM_KEYWORD));
			// set iteration number
			//iterNum = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.ITER));
			// set seed
			//seed = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.SEED));
			// set number of thread
			//threadNum = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.NUM_THREAD));

		} catch (IOException e) {
			LOGGER.error("Failed to load Main Configuration File : " + PROP.MAIN_CONFIG_NAME, e);
		}

		// build pipe
		this.pipe = buildPipe();
		instanceList = new InstanceList(this.pipe);
	}

	/**
	 * Build Instance Pipe
	 * @return {@link Pipe}
	 */
	private static Pipe buildPipe() {

		List<Pipe> pipeList = new ArrayList<Pipe>();
		// File -> CharSequence
		pipeList.add(new Input2CharSequence("UTF-8"));

		// CharSequence -> TokenSequence
		pipeList.add(new CharSequence2TokenSequence(Pattern.compile("[/\\p{L}\\p{N}_]+")));

		// TokenSequence.toLowerCase
		pipeList.add(new TokenSequenceLowercase());

		// TokensSequence -> FeatureSequence
		pipeList.add(new TokenSequence2FeatureSequence());

		// Print out the features and the label
		//pipeList.add(new PrintInputAndTarget());

		return new SerialPipes(pipeList);
	}

	public Map<Integer, Double> infer(String path) throws IOException {
		NLPDocumentLoader loader = new NLPDocumentLoader();
		Document document = loader.loadSingleDocument(path);

		String documentName = document.getDocumentName();
		String content = document.toString();
		instanceList.addThruPipe(new Instance(content, null, documentName, null));

		double[] prob = this.inferencer.getSampledDistribution(instanceList.get(0), 0, 1, 5);
		Map<Integer, Double> maxProp = new HashMap<Integer, Double>();
		for (int i = 0; i < prob.length; i++) {
			maxProp.put(i + 1, prob[i]);
		}
		maxProp = MapUtil.sortByValue(maxProp, MapUtil.SORT_DESCENDING);

		return maxProp;
	}

	public static void main(String[] args) throws Exception {

		CommandLineInterface cli = new CommandLineInterface();
		cli.addOption("m", "model", true, "serialized model path", true);
		cli.addOption("i", "input", true, "input path", true);
		cli.parseOptions(args);

		LDAinferencer ldAinferencer = new LDAinferencer(cli.getOption("m"));

		File fileList = new File(cli.getOption("i"));

		for (File file : fileList.listFiles()) {
			Map<Integer, Double> result = ldAinferencer.infer(file.getCanonicalPath());
			Set<Integer> keySet = result.keySet();
			Iterator iter = result.keySet().iterator();
			Integer topic = (Integer) iter.next();
			if (topic == 2) {
				System.out.println("class for " + file.getName() + ": " + topic + " ==> " + result.get(topic));
			}
		}

		LOGGER.info("inference done");
	}


}
