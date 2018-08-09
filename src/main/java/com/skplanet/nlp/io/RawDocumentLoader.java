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
public class RawDocumentLoader extends AbstractLoader {
	private static final Logger LOGGER = Logger.getLogger(RawDocumentLoader.class.getName());

	public RawDocumentLoader() {
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
			for (File file : files) {

				reader = new BufferedReader(new FileReader(file));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.trim().length() == 0) {
						continue;
					}
					i++;
					LOGGER.info("loading : " + i);
					Document document = new NLPDocument();
					document.setDocumentName(file.getName());
					String[] tokens = line.split(" ");
					for (String token : tokens) {
						// stopword / stoptag filter
						/*
						if (Integer.parseInt(token.split("/")[1]) < 36) {
							continue;
						}
						*/

						/*
						if (Integer.parseInt(token.split("/")[1]) < 30 && Integer.parseInt(token.split("/")[1]) > 12) {
							continue;
						}
						*/

						if (
							//Integer.parseInt(token.split("/")[1]) == 31 ||
							//Integer.parseInt(token.split("/")[1]) == 33 ||
							Integer.parseInt(token.split("/")[1]) == 38 ||
							Integer.parseInt(token.split("/")[1]) == 41 ||
							Integer.parseInt(token.split("/")[1]) == 42 ||
							Integer.parseInt(token.split("/")[1]) == 43 ||
							Integer.parseInt(token.split("/")[1]) == 44 ||
							Integer.parseInt(token.split("/")[1]) == 45 ||
							Integer.parseInt(token.split("/")[1]) == 46 ||
							Integer.parseInt(token.split("/")[1]) == 47 ||
							//Integer.parseInt(token.split("/")[1]) == 59 ||
							Integer.parseInt(token.split("/")[1]) == 60
						) {
							if (!isStopword(token)) {
								// keyword weighting
								for (int j = 0; j < keywordWeighting(token); j++) {
									document.addToken(token);
								}
							}
						}
					}
					documents.add(document);
				}
				reader.close();
			}
			etime = System.currentTimeMillis();
			LOGGER.info("document loading done in " + (etime - btime) + "msec.");
		} catch (IOException e) {
			LOGGER.error("failed to load main configuration file " + PROP.MAIN_CONFIG_NAME);
		}
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


}
