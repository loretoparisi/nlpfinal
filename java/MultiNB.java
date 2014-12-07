import java.io.*;
import java.util.*;
import java.lang.Character;

/**
 *
 * Assignment 7 - Natural Language Processing
 *
 * Summary:
 * Implementation of a naive bayes multinomial classifier that 
 * calculates probabilities of the given sentence in testing data
 * and determines if its label is engitive or chiative, based on
 * training data and given lambda.
 *
 *
 * @author Viona Lam and Szeyin Lee
 * @version 11/21/2014
 *
 */
public class MultiNB {

	// private data members
	private double lambda;
	private HashMap<Character, Double> engCounts = new HashMap<Character, Double>();
	private HashMap<Character, Double> chiCounts = new HashMap<Character, Double>();
	private HashMap<Character, Double> engTheta = new HashMap<Character, Double>();
	private HashMap<Character, Double> chiTheta = new HashMap<Character, Double>();

	private ArrayList<Pair> engArray = new ArrayList<Pair>();
	private ArrayList<Pair> chiArray = new ArrayList<Pair>();

	private HashSet<Character> vocabs = new HashSet<Character>();
	private double engLogProb;
	private double chiLogProb;
	private double chiCount = 0.0;
	private double engCount = 0.0;
	private	double engThetaDenom;
	private	double chiThetaDenom;
	private double engLogLambda;
	private double chiLogLambda;
	private	double correct = 0.0;
	private double incorrect = 0.0;
	/**
	 * Create a MultiNB object from files that contain the training and test data, and lambda
	 * 
	 * @param trainingFile File that contains a set of sentences to be trained on
	 * @param testingFile File that contains a set of sentences to be sentiment-evaluated
	 * @param lambda Value to be used in lambda smoothing.
	 */
	public MultiNB(String trainingEngFile, String trainingChFile, String testingEngFile,
			String testingChFile, double lambda) throws IOException{

		this.lambda = lambda;
		try {
			readTrainingFile(trainingChFile, "chinese");
			readTrainingFile(trainingEngFile, "english");	
			calculateThetas();
			readTestingFile(testingEngFile, "english");
			readTestingFile(testingChFile, "chinese");
			System.out.println( "correct = " + correct);
			System.out.println( "incorrect = " + incorrect);
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Reads the training file and stores counts for each word in each sentence
	 * @param filename File Training file
	 */
	public void readTrainingFile(String filename, String tag) throws IOException {
		FileReader in = null;
		try {
		    in = new FileReader(filename);
		    BufferedReader reader = new BufferedReader(in);
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	line = line.replaceAll("\\s+","");
		    	// loops through each word in line
		    	for (int i = 1; i < line.length(); i++ ) {
		    		char ipa = line.charAt(i);

		    		// adds to hashset of words
		    		//System.out.println(ipa);
		    		vocabs.add(ipa);

		    		// chiative tags
		    		if (tag.equals("chinese")) {
		    			chiCount++;
		    			if (chiCounts.containsKey(ipa)) {
		    				chiCounts.put(ipa, chiCounts.get(ipa)+1.0);
		    			} else {
		    				chiCounts.put(ipa, 1.0);
		    			}
		    		} else {
		    			// engitive tags
		    			engCount++;
		    			if (engCounts.containsKey(ipa)) {
		    				engCounts.put(ipa, engCounts.get(ipa)+1.0);
		    			} else {
		    				engCounts.put(ipa, 1.0);
		    			}
		    		}
		    	}
		    }

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Calculates theta values for every word seen in the vocabulary
	 * 
	 */
	public void calculateThetas() {

	    // calculates log 10  of p(label)
	    double sum = engCount + chiCount;
	    engLogProb = Math.log10(engCount/sum);
	    chiLogProb = Math.log10(chiCount/sum);

		double vocabLambaSize = vocabs.size() * lambda;
		engThetaDenom = engCount + vocabLambaSize;
		chiThetaDenom = chiCount + vocabLambaSize;
		engLogLambda = Math.log10(lambda/engThetaDenom);
		chiLogLambda = Math.log10(lambda/chiThetaDenom);

		// loops through engitive hash; add lambda 
		for (char w : engCounts.keySet()) {
			double theta = (engCounts.get(w) + lambda)/engThetaDenom;
			engTheta.put(w, Math.log10(theta));
		}
		// loops through chiative hash; add lambda 
		for (char w : chiCounts.keySet()) {
			double theta = (chiCounts.get(w) + lambda)/chiThetaDenom;
			chiTheta.put(w, Math.log10(theta));
		}

	}

	/**
	 * For every line in the testing file, calculate the engitive Log Probility, and the
	 * chiative Log Probability according to the multinomial NB model, compare the two
	 * values and assign a sentiment to the one with a higher probability.
	 * @param filename Testing file
	 */
	public void readTestingFile(String filename, String tag) throws IOException {
		FileReader in = null;
		try {
		    in = new FileReader(filename);
		    BufferedReader reader = new BufferedReader(in);
		    String line;

		    while ((line = reader.readLine()) != null) {
		    	line = line.replaceAll("\\s+","");
		    	Double lineChiProbs = chiLogProb;
		    	Double lineEngProbs = engLogProb;

		    	// loops through each word in sentence
		    	for (int i = 1; i < line.length(); i++ ) {
		    		char word = line.charAt(i);
		    		if (vocabs.contains(word)) {
		    			// adds up the log  eng & chi probs for all the seen words in vocab

		    			if (engTheta.containsKey(word)) {
		    				lineEngProbs += engTheta.get(word);
		    			} else {
		    				lineEngProbs += engLogLambda;
		    			}

		    			if (chiTheta.containsKey(word)) {
		    				lineChiProbs += chiTheta.get(word);
		    			} else {
		    				lineChiProbs += chiLogLambda;
		    			}
		    		}
		    		// ignore unseen words
		    	}

		    	// print prob for each line
		    	if (lineEngProbs >= lineChiProbs) {
		    		System.out.println(line + " english \t" + lineEngProbs);
		    		if (tag.equals("english")) {
		    			correct++;
		    		} else {
		    			incorrect++;
		    		}
		    	} else {
		    		System.out.println(line + " chinese \t" + lineChiProbs);
		    		if (tag.equals("chinese")) {
		    			correct++;
		    		} else {
		    			incorrect++;
		    		}
		    	}

		    }

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 *  Class for a pair of word and its log prob, for use in sorting.
	 */
	public class Pair implements Comparable<Pair> {
		private final char word;
		private final Double prob;

		public char word() {return word;}
		public double prob() {return prob;}

		/**
		 * Creates Pair object from the inputWord and inputProb
		 * @param inputWord word to be associated with log prob
		 * @param inputProb log prob value used in sorting
		 */
		public Pair(char inputWord,double inputProb) {
			this.word = inputWord;
			this.prob = inputProb;
		}

		/**
		 * Overrides the comparison function
		 * @param o SimPair to be compared with
		 */
		public int compareTo(Pair o) {
			return this.prob.compareTo(o.prob);
		}
	}

	public static void main(String[] args) throws IOException {

		// if (args.length > 5) {
		// 	System.out.println("Wrong number of arguments provided");
		// 	return;
		// }

		// get the parameters from input args 
		String trainingEngFile = args[0];
		String trainingChFile = args[1];
		String testingEngFile = args[2];
		String testingChFile = args[3];
		double lambda = Double.parseDouble(args[4]);

		MultiNB multiNB = new MultiNB(trainingEngFile, trainingChFile, testingEngFile,
			testingChFile, lambda);
	}


}