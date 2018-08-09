package com.skplanet.nlp.io;

import com.skplanet.nlp.PROP;
import com.skplanet.nlp.data.Document;
import com.skplanet.nlp.data.NLPDocument;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 11/6/14.
 */
public class NLPDocumentLoader extends AbstractLoader {
	private static final Logger LOGGER = Logger.getLogger(NLPDocumentLoader.class.getName());

	public NLPDocumentLoader() {
		super();
		this.documents = new ArrayList<Document>();
	}

	/**
	 * Load Resources
	 */
	@Override
	public void load(String inputPath) {
		long btime, etime;
		try {
			File fileList = new File(inputPath);
			if (!fileList.isDirectory()) {
				LOGGER.error("collection must be a directory");
				return;
			}

			btime = System.currentTimeMillis();
			File[] files = fileList.listFiles();
			BufferedReader reader;
			int i = 0;
			assert files != null;
			int total = files.length;
			for (File file : files) {
				i++;
				LOGGER.info("loading : " + file.getName() + " (" + i + "/" + total + ")");
				Document document = new NLPDocument();
				document.setDocumentName(file.getName());
				reader = new BufferedReader(new FileReader(file));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.trim().length() == 0) {
						continue;
					}

					String[] tokens = line.split(" ");
					for (String token : tokens) {
						if (!token.contains("/") || !token.contains("/")) {
							LOGGER.debug("token parsing error : " + line);
							continue;
						}
						// stopword / stoptag filter
						if (!isStopword(token.split("/")[0]) && isTargetTag(token.split("/")[1])) {
							// keyword weighting
							for (int j = 0; j < keywordWeighting(token.split("/")[0]); j++) {
								document.addToken(token);
							}
						}
					}
				}
				documents.add(document);
				reader.close();
			}
			etime = System.currentTimeMillis();
			LOGGER.info("document loading done in " + (etime - btime) + "msec.");
		} catch (IOException e) {
			LOGGER.error("failed to load main configuration file " + PROP.MAIN_CONFIG_NAME);
		}
	}

	public Document loadSingleDocument(String path) {
		long btime, etime;
		Document document = new NLPDocument();
		try {
			btime = System.currentTimeMillis();
			BufferedReader reader;
			int i = 0;
			File file = new File(path);
			assert file != null;
			i++;
			LOGGER.info("loading : " + path);
			document.setDocumentName(file.getName());
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() == 0) {
					continue;
				}

				String[] tokens = line.split(" ");
				for (String token : tokens) {
					if (!token.contains("/") || !token.contains("/")) {
						LOGGER.debug("token parsing error : " + line);
						continue;
					}
					// stopword / stoptag filter
					if (!isStopword(token.split("/")[0]) && isTargetTag(token.split("/")[1])) {
						// keyword weighting
						for (int j = 0; j < keywordWeighting(token.split("/")[0]); j++) {
							document.addToken(token);
						}
					}
				}
			}
			reader.close();
			etime = System.currentTimeMillis();
			LOGGER.info("document loading done in " + (etime - btime) + "msec.");
		} catch (IOException e) {
			LOGGER.error("failed to load main configuration file " + PROP.MAIN_CONFIG_NAME);
		}
		return document;
	}

	/**
	 * Get Loaded Documents
	 *
	 * @return loaded documents
	 */
	@Override
	public List<Document> getDocuments() {
		if (documents.size() == 0) {
			LOGGER.debug("no documents are loaded!");
		}
		return documents;
	}

	/**
	 * Check if the given word is stopword
	 *
	 * @param word a word to be tested
	 * @return true if the word is stopword
	 */
	@Override
	protected boolean isStopword(String word) {
		return this.stopwords.contains(word);
	}


	/**
	 * Get the weighting for the given word
	 *
	 * @param word a word to be tested
	 * @return weighting factor
	 */
	@Override
	protected int keywordWeighting(String word) {
		if (this.keywordWeight.containsKey(word)) {
			return this.keywordWeight.get(word);
		}
		return 1;
	}

	/**
	 * Check if given tag is used in analysis
	 * @param tag nlp tag
	 * @return true if the given tag is used in analysis
	 */
	protected boolean isTargetTag(String tag) {
		return this.nlpTagSet.contains(tag);
	}

}
