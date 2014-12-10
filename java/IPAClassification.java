import edu.stanford.nlp.classify.*;
import edu.stanford.nlp.ling.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 *  Final Project: IPA classification
 * 
 * @author Szeyin Lee and Viona Lam
 * @version 3/3/2011
 *
 */
public class IPAClassification {
	private static final String TRAINING_ENG_DATA = "enCorp.train";
	private static final String TRAINING_CHI_DATA = "chiCorp.train";
	private static final String TRAINING_JAP_DATA = "japCorp.train";
	private static final String TRAINING_SPAN_DATA = "spaCorp.train";
	//private static final String DEV_DATA = "names.dev";
	private static final String TEST_ENG_DATA = "enCorp.test";
	private static final String TEST_CHI_DATA = "chiCorp.test";
	private static final String TEST_JAP_DATA = "japCorp.test";
	private static final String TEST_SPAN_DATA = "spanCorp.test";


	/** 
	 *  Read in each example as a IPAExample from given files
	 * 
	 * @param filename the filename containing ipa data
	 * @return an ArrayList of IPAExamples
	 */
	private static ArrayList<IPAExample> readExamples(String chi_filename, String eng_filename, 
													  String jap_filename){
		Scanner chi_dataScanner = null;
		Scanner eng_dataScanner = null;
		Scanner jap_dataScanner = null;
		//Scanner span_dataScanner = null;

		try {
			chi_dataScanner = new Scanner(new File(chi_filename));
			eng_dataScanner = new Scanner(new File(eng_filename));
			jap_dataScanner = new Scanner(new File(jap_filename));
			//span_dataScanner = new Scanner(new File(span_filename));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Problems opening file: " + e.toString());
		}
		
		ArrayList<IPAExample> examples = new ArrayList<IPAExample>();
		
		while( chi_dataScanner.hasNextLine() ){
			examples.add(new IPAExample(chi_dataScanner.nextLine().replaceAll("\\s+","") , Language.CHINESE));
		}
		while( eng_dataScanner.hasNextLine() ){
			examples.add(new IPAExample(eng_dataScanner.nextLine().replaceAll("\\s+","") , Language.ENGLISH));
		}
		while( jap_dataScanner.hasNextLine() ){
			examples.add(new IPAExample(jap_dataScanner.nextLine().replaceAll("\\s+","") , Language.JAPANESE));
		}
		// while( span_dataScanner.hasNextLine() ){
		// 	examples.add(new IPAExample(span_dataScanner.nextLine().replaceAll("\\s+","") , Language.SPANISH));
		// }
		return examples;
	}

	
	/**
	 * Given a List of IPAExamples, extract the features from each example
	 * using the makeDataPoint method and create Datum.
	 * 
	 * @param examples a List of IPAExamples representing the data set
	 * @return an ArrayList of Datum created from the input examples
	 */
	private static ArrayList<Datum> examplesToData(List<IPAExample> examples){
		ArrayList<Datum> data = new ArrayList<Datum>();

		for( IPAExample ex: examples ){
			data.add(makeDataPoint(ex));
		}

		return data;
	}

	/**
	 * Get the accuracy of this classifier on the test data
	 * 
	 * @param testData test data to be classified
	 * @param examples original IPAExamples associated with the test data
	 * @param classifier the classifier to use
	 * @param printMistakes whether or not to print out the misclassified examples
	 * @return the accuracy of the classifier on the test data
	 */
	private static double accuracy(List<Datum> testData, List<IPAExample> examples, LinearClassifier classifier, boolean printMistakes){
		int correct = 0;

		for( int i = 0; i < testData.size(); i++ ){
			String classification = (String)classifier.classOf(testData.get(i));
			
			if( classification.equals(testData.get(i).label()) ){
				correct++;
			}else if( printMistakes ){
				System.out.println("Incorrect: " + examples.get(i) + " classified as : " + classification);
			}
		}

		return ((double)correct)/testData.size();
	}
	
	/**
	 * Get the log-prob of this classifier on the test data
	 * 
	 * @param testData test data to be examined
	 * @param classifier the classifier to use
	 * @return the log-prob of the classifier on the test data
	 */
	private static double totalLogProb(ArrayList<Datum> testData, LinearClassifier classifier){
		double total = 0.0;

		for( Datum d: testData ){
			// get the log-prob for this model for the correct label
			// ignore this warning
			total += Math.log(classifier.probabilityOf(d).getCount(d.label()));			
		}

		return total;
	}
	
	/**
	 * Creates a feature-base Datum from the IPAExample for use with the classifiers
	 * 
	 * @param example the IPAExample to be featurized
	 * @return A Datum representing the input example
	 */
	private static Datum makeDataPoint(IPAExample example){
		ArrayList<String> features = new ArrayList<String>();

		String name = example.getName();
		HashMap<Character, Integer > ipaCounts = new HashMap<Character, Integer>();
		// We only need to add the features that occur.  In this case
		// each feature is binary and is represented by adding a String
		// representing that feature to the set of features.

		for( int i = 0; i < name.length(); i++ ) {
			Character current = name.charAt(i);

			// unigram
			features.add("Contains=" + current);
			// bi-gram
			if (i < name.length() - 1) {
				features.add("Contains=" + current + name.charAt(i+1));
			}
			// tri-gram
			if (i < name.length() - 2) {
				features.add("Contains=" + current + name.charAt(i+1) + name.charAt(i+2));
			}
			// counts
			int count = 1;
			if (ipaCounts.containsKey(current)) {
				count += ipaCounts.get(current);
			}
			ipaCounts.put(current, count);
		}

		for (Character ipa : ipaCounts.keySet()) {
			features.add("Counts=" + ipa + ipaCounts.get(ipa));
		}

		if( example.isChinese() ){
			return new BasicDatum(features, "chinese");
		} else if ( example.isEnglish() ) {
			return new BasicDatum(features, "english");
		// } else if ( example.isSpanish() ) {
		// 	return new BasicDatum(features, "spanish");
		}else{
			return new BasicDatum(features, "japanese");
		}
	}

	
	public static void main(String[] args){
		ArrayList<Datum> trainingData = examplesToData(readExamples(TRAINING_CHI_DATA, 
										TRAINING_ENG_DATA, TRAINING_JAP_DATA));

		//ArrayList<IPAExample> devExamples = readExamples(DEV_DATA);
		//ArrayList<Datum> devData = examplesToData(devExamples);
		
		ArrayList<IPAExample> testExamples = readExamples(TEST_CHI_DATA, TEST_ENG_DATA, 
														TEST_JAP_DATA);
		ArrayList<Datum> testData = examplesToData(testExamples);

		
		// MaxEnt
		LinearClassifierFactory maxEntFactory = new LinearClassifierFactory();
		maxEntFactory.useConjugateGradientAscent();
		// Turn on per-iteration convergence updates
		maxEntFactory.setVerbose(true);
		//Small amount of smoothing
		maxEntFactory.setSigma(1.0);
		// Build a classifier
		LinearClassifier maxEntClassifier = (LinearClassifier)maxEntFactory.trainClassifier(trainingData);
		// Check out the learned weights
		//maxEntClassifier.dump();
		
		// NB
		NBLinearClassifierFactory nbFactory = new NBLinearClassifierFactory();
		LinearClassifier nbClassifier = (LinearClassifier)nbFactory.trainClassifier(trainingData);
		//nbClassifier.dump();	

		System.out.println("MLR Accuracy: " + accuracy(testData, testExamples, maxEntClassifier, true));
		System.out.println();
		System.out.println("NB Accuracy: " + accuracy(testData, testExamples, nbClassifier, true));
		System.out.println();
		System.out.println("MLR Log prob: " + totalLogProb(testData, maxEntClassifier));
		System.out.println("NB Log prob: " + totalLogProb(testData, nbClassifier));
	}
}
