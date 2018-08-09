package com.skplanet.nlp.cluster;

import cc.mallet.pipe.*;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.skplanet.nlp.PROP;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.data.Document;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * LDA Cluster
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 9/15/14.
 */
public class LDACluster extends AbstractCluster {
	private static final Logger LOGGER = Logger.getLogger(LDACluster.class.getName());

	// ------ LDA Configuration ------ //
	// input pipe
	private Pipe pipe = null;
	// topic number
	private int topicNum = -1;
	// alpha sum
	private double alpha = -1.0;
	// beta
	private double beta = -1.0;
	// iteration number
	private int iterNum = -1;
	// keyword number
	private int keywordNum = 10; // by default
	// thread number
	private int threadNum = 2; // by default
	// seed
	private int seed = -1;

	// ------ LDA model ------ //
	private ParallelTopicModel model = null;
	private InstanceList instanceList = null;
	private Alphabet dataAlphabet = null;

	// ------ LDA Artifacts ----- //
	private Map<Integer, List<String>> topicKeywords = null;
	private Map<String, double[]> topicMatrix = null;


	/**
	 * Constructor
	 */
	public LDACluster() {
		super();
		Configuration config = Configuration.getInstance();
		try {
			config.loadProperties(PROP.MAIN_CONFIG_NAME);

			// set topic number
			topicNum = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.NUM_TOPIC));
			// set alpha sum
			alpha = Double.parseDouble(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.ALPHA_SUM));
			// set beta
			beta = Double.parseDouble(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.BETA));
			// set keyword number
			keywordNum = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.NUM_KEYWORD));
			// set iteration number
			iterNum = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.ITER));
			// set seed
			seed = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.SEED));
			// set number of thread
			threadNum = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.NUM_THREAD));

		} catch (IOException e) {
			LOGGER.error("Failed to load Main Configuration File : " + PROP.MAIN_CONFIG_NAME, e);
		}

		// build pipe
		this.pipe = buildPipe();
		instanceList = new InstanceList(this.pipe);
	}


	/**
	 * Constructor
	 */
	public LDACluster(String numTopic) {
		super();
		Configuration config = Configuration.getInstance();
		try {
			config.loadProperties(PROP.MAIN_CONFIG_NAME);

			// set topic number
			topicNum = Integer.parseInt(numTopic);
			// set alpha sum
			alpha = Double.parseDouble(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.ALPHA_SUM));
			// set beta
			beta = Double.parseDouble(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.BETA));
			// set keyword number
			keywordNum = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.NUM_KEYWORD));
			// set iteration number
			iterNum = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.ITER));
			// set seed
			seed = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.SEED));
			// set number of thread
			threadNum = Integer.parseInt(config.readProperty(PROP.MAIN_CONFIG_NAME, PROP.NUM_THREAD));

		} catch (IOException e) {
			LOGGER.error("Failed to load Main Configuration File : " + PROP.MAIN_CONFIG_NAME, e);
		}

		// build pipe
		this.pipe = buildPipe();
		instanceList = new InstanceList(this.pipe);
	}

	/**
	 * Load Document Instance
	 * @param documents documents to be added
	 */
	public void buildModel(List<Document> documents) {
		long btime, etime;
		// load instances
		LOGGER.info("Loading instances ....");
		btime = System.currentTimeMillis();
		int count = 0;
		int total = documents.size();
		for (Document document : documents) {
			if (count % 100 == 0) {
				LOGGER.info("instance added : " + count + "/" + total);
			}
			String documentName = document.getDocumentName();
			String content = document.toString();
			instanceList.addThruPipe(new Instance(content, null, documentName, null));
			count++;
		}
		dataAlphabet = this.instanceList.getDataAlphabet();
		etime = System.currentTimeMillis();
		LOGGER.info("Loading instances done : " + (etime - btime) + " msec.");

		// build model
		LOGGER.info("Building LDA Model ....");
		btime = System.currentTimeMillis();
		this.model = new ParallelTopicModel(this.topicNum, alpha, beta);
		this.model.setRandomSeed(seed);
		this.model.addInstances(instanceList);
		this.model.setNumThreads(threadNum);
		this.model.setNumIterations(iterNum);
		try {
			this.model.estimate();
		} catch (IOException e) {
			LOGGER.error("Failed to build LDA Model", e);
		}
		etime = System.currentTimeMillis();
		LOGGER.info("Building LDA Model done : " + (etime - btime) + " msec.");

		// get topic keywords
		this.topicKeywords = new HashMap<Integer, List<String>>();
		for (int topic = 0; topic < topicNum; topic++) {
			Iterator<IDSorter> iterator = model.getSortedWords().get(topic).iterator();

			List<String> keywordList = new ArrayList<String>();
			int rank = 0;
			//System.out.print(topic + "\t");
			while (iterator.hasNext() && rank < keywordNum) {
				//while (iterator.hasNext()) {
				IDSorter idCountPair = iterator.next();
				keywordList.add((String) dataAlphabet.lookupObject(idCountPair.getID()));
				//System.out.print(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
				rank++;
				}
			//System.out.println();
			this.topicKeywords.put(topic, keywordList);
			}
		}

		/**
		 * LDA analysis for the given set of documents
		 * @param documents document list to be analysed
		 */
		public void analysis(List<Document> documents) {
			this.topicMatrix = new HashMap<String, double[]>();
			for (Document document : documents) {
				double[] classProbabilities = this.analysis(document);
				this.topicMatrix.put(document.getDocumentName(), classProbabilities);
			}
		}

		/**
		 * Get topic matrix
		 * @return topic matrix
		 */
		public Map<String, double[]> getTopicMatrix() {
			return this.topicMatrix;
		}

		/**
		 * LDA analysis for single document
		 * @param document {@link Document}
		 * @return similarity map for each class with the given document
		 */
		private double[] analysis(Document document) {
			String documentName = document.getDocumentName();
			String text = document.toString();
			// Create a new instance named "test instance" with empty target and source fields.
			InstanceList testing = new InstanceList(instanceList.getPipe());
			Instance newInstance = new Instance(text, null, documentName, null);
			testing.addThruPipe(newInstance);

			TopicInferencer inferencer = model.getInferencer();
			double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
			/*
			   Map<Integer, Double> sortedMap = new HashMap<Integer, Double>();
			   for (int i = 0; i < topicNum; i++) {
			   sortedMap.put(i, testProbabilities[i]);
			   }

			   return MapUtil.sortByValue(sortedMap, MapUtil.SORT_DESCENDING);
			   */
			return testProbabilities;
		}


		/**
		 * Build Pipe (File -> CharSequence -> TokenSequence (lowercase) -> FeatureSequence (integer)
		 *
		 * @return {@link cc.mallet.pipe.Pipe}
		 */
		private Pipe buildPipe() {
			ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

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

		/**
		 * Get number of topic
		 * @return number of topic
		 */
		public int getTopicSize() {
			return this.topicNum;
		}

		/**
		 * Set the number of topic
		 * WARN : This Method must be run before building LDA model!!!
		 * @param size topic size
		 */
		public void setTopicSize(int size) {
			this.topicNum = size;
		}

		/**
		 * Get Top Keywords for Topic classes
		 * @return top keywords
		 */
		public Map<Integer, List<String>> getTopicKeywords() {
			return this.topicKeywords;
		}

		/**
		 * Save the serialized model
		 * @param path path to where the model is saved
		 */
		public void saveModel(String path) {
			if (this.model != null) {
				this.model.write(new File(path));
			}
		}
	}
