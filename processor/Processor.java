package processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tartarus.snowball.SnowballStemmer;
import java.util.*;

/**
 * 
 * @author Harry Whittaker
 * @since 09/06/2017
 *
 * This class handles the main computation of this xml document search engine.
 * This works for XML documents with <title> and <text> tags.
 * The number of document will have to me manually changed when there is more or less than 10 docs.
 * The Location of the documents and stop words will also have to be manually inputed.
 * 
 */
public class Processor {
	private static final String STOP_WORDS_FILE = "./stopWords.txt";
	private static final String XML_DOCUMENTS = "./documents/";
	 
	 public static void main(String[] args) throws Exception {	 
		
		 Preprocessor stemmer = new Preprocessor();
		 String[] stopWords = getStopWords(STOP_WORDS_FILE);
		 Dataset set = makeDataSet(XML_DOCUMENTS, stopWords, stemmer);
		 		 
		 
		 Scanner reader = new Scanner(System.in);		 
		 
		 while(true){
			System.out.println("Enter one of the following options:"
						+ "\n1	:	 to query dataset"
		 				+ "\n2	:	 to find optimal query for dataset");
			
			String input = reader.nextLine();
			
			if(input.equals("1")){			
				
				 ArrayList<String> queryTerms = getQuery(reader, stopWords, stemmer);		
				 TreeMap<String, Double> bmScores = calcBM25(set, queryTerms);
				 set.setPosNeg(bmScores);
				 set.printWeightedMap();
				 System.out.println();
			}else if(input.equals("2")){
				
				 System.out.println("option 2");
			}else System.out.println("invalid input");			
						
		 }
		 
	}	
	 
	 /**
	  * Calculates BM25 weighting and saves to docs and bm hashmap
	  */
	 private static TreeMap<String, Double> calcBM25(
			 Dataset set, 
			 ArrayList<String> query){
		 
		 TreeMap<String, Double> bmScores = new TreeMap<>();
		 for(BowDocument doc : set.getDocs()){
			 bmScores.put(doc.toString(), Weighting.calculateBM25(doc, set.getDocs(), query));
		 }
		 return bmScores;
	 } 
	 
	 /**
	  * Prompts user to input query then saves it
	 * @throws Exception 
	  */
	 private static ArrayList<String> getQuery(
			 Scanner reader, 
			 String[] stopWords,
			 Preprocessor stemmer) throws Exception{		
		 
		 System.out.print("Enter query: ");
		 String query = reader.nextLine();		
		 ArrayList<String> tokens = new ArrayList<>();
		 tokens =  Preprocessor.tokenize(query);
		 tokens =  Preprocessor.removeStopWords(stopWords, tokens);
		 return stemTerms(tokens, stemmer);
		 
	 }
	 
	 /**
	  * Make a dataset and populate it with BowDocuments from a inputed folder
	  * @param docsLocation folder contain documents to put in to dataset
	  * @param stopWords array of stop words
	  * @param stemmer Preprocessor object to be used to stem terms
	  * @return populated Dataset  
	  */
	 private static Dataset makeDataSet(
			 String docsLocation, 
			 String[] stopWords, 
			 Preprocessor stemmer){
		 
		Dataset set;	
		File docsFolder = new File(docsLocation);
		File[] docs = docsFolder.listFiles();
		
		set = new Dataset(docsFolder.getName());
		
		for(File doc : docs){	
			if(!doc.getName().contains("._")){
				try {
					set.addDoc(buildBdoc(doc, stopWords, stemmer));
				} catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}											
		}
		return set;
	 }
	 
 	/**
	* Make a BowDocument from a XML file
	* @param doc	XML file to make BowDucument form
	* @return	BowDocument
	* @throws Exception 
	*/
	private static BowDocument buildBdoc(
			File doc, 
			String[] stopWords, 
			Preprocessor stemmer) 
					throws Exception{
		
		String rawDoc = getDocString(doc);
		String docID = findDocID(rawDoc);	
		
		BowDocument bDoc = new BowDocument(docID);	
		
		ArrayList<String> tokens = Preprocessor.tokenize(rawDoc);				
		int numWords = tokens.size();
		
		tokens = Preprocessor.removeStopWords(stopWords, tokens);
		ArrayList<String> terms = stemTerms(tokens, stemmer);												
		
		for(String term : terms){
			if(!term.equals("")){
				bDoc.addTerm(term);
			}else{
				numWords--;
			}
		}
		bDoc.setNumWords(numWords);	
		return bDoc;
	}
		
	/**
	 * Stem a String arraylist of tokens
	 * @param tokens ArrayList<String> of tokens
	 * @param stemmer Preprocessor object to be used as stemmer
	 * @return ArrayList<String> stemmed terms
	 * @throws Exception is stemmer is not initiazed
	 */
	private static ArrayList<String> stemTerms(
			ArrayList<String>tokens, 
			Preprocessor stemmer) throws Exception{
		
		ArrayList<String> terms = new ArrayList<>();
		for(String term : tokens){
			terms.add(stemmer.stemTerm(term));
		}	
		return terms;
	}	 
		
	/**
	 * Finds the doc ID of inputed document	
	 * @param doc String containing unedited document
	 * @return doc id [0-9]* or null id no id was found
	 */
	private static String findDocID(
			String doc) 
					throws Exception{
		
		String pattern = "itemid=\"[0-9]*";
	    Pattern r = Pattern.compile(pattern);
		 
	     String temp = null;
	     Matcher m = r.matcher(doc);
	     if (m.find()) {
	        temp = doc.substring(m.start() + 8, m.end());
	     }else {
	        throw new Exception("No Doc id found");
	     }     
	     return temp;
	}
	
	/**
	 * Get a list of stop words contained in a 	comma separated file
	 * @param file containing the stop words
	 * @return String array of stop words
	 */
	public static String[] getStopWords(
			String file){
		
		String rawString = getDocString(new File(file));
		return rawString.split(",");
	}
		
	/**
	 * 
	 * @param location directory path of queries file
	 * @return single String with all raw queries in it
	 */
	private static String getDocString(
			File file){
		
		String thisLine = null;
		String line = "";
		
		try {
		     BufferedReader br = new BufferedReader(new FileReader(file));
		     while ((thisLine = br.readLine()) != null) {
	    		 line += thisLine;	   
		     }
		     br.close();
		  } catch(Exception e) {
		     e.printStackTrace();
		  }
		return line;
	}	
}
