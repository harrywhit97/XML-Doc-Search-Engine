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
 * @since 24/04/2017
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
		
		 SnowballStemmer stemmer = initStemmer(args);		
		 String[] stopWords = getStopWords(STOP_WORDS_FILE);
		 Dataset set = makeDataSet(XML_DOCUMENTS, stopWords, stemmer);
		 		 
		 
		 Scanner reader = new Scanner(System.in);		 
		 
		 while(true){
			 ArrayList<String> queryTerms = getQuery(reader, stopWords, stemmer);		
			 TreeMap<String, Double> bmScores = calcBM25(set, queryTerms);
			 set.setPosNeg(bmScores);
			 set.displaySetInfo();
			 System.out.println();
		 }			
	}	
	 
	 private static SnowballStemmer initStemmer(
			 String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		 
		 Class stemClass = Class.forName("org.tartarus.snowball.ext." +
					args[0] + "Stemmer");
		 SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
		 return stemmer;
	 }
	 
	 /**
	  * prints result
	  *
	 private static void printResult(){
		 int avgL = bDocs[0].getTotalDocLength()/bDocs.length;
		
		 int print = 3;
		 
		 switch(print){		 	
		 	case 0:
		 		 int count = 0;
				 int max = 20;
				 
				 System.out.println("Document "+ bDocs[0].getDocID() + " contains " + bDocs[0].getNumTerms() + " terms.");
				 for(String term : tfIdf.keySet()){
					 System.out.println(term + " : " + tfIdf.get(term));
					 if(count >= max-1){
						 break;
					 }
					 count++;			 
				 }	
				 break;
		 	case 1:
		 		System.out.println("DocID \t docLength  avgDocLength");
		 		for(BowDocument bDoc : bDocs){
		 			System.out.println(bDoc.getDocID() + "   " + bDoc.getNumTerms() + " \t       " + bDoc.getTotalDocLength()/bDocs.length );
		 		}
		 		
		 		break;
		 	case 2:
		 		System.out.println("Average document length "+ avgL + " for query : " + query);
	 			for(BowDocument d : docsAndBM.keySet()){
	 				System.out.println("Document : " + d.getDocID() +
	 						", Length : " + d.getNumTerms() + ", BM25 score: " + docsAndBM.get(d));
	 			}	 		
		 		
		 		break;
		 	case 3:
		 		System.out.println("For query "+ query +", three recommended relevant documents and their BM25 score:");
		 		int c = 0;
	 			for(BowDocument d : docsAndBM.keySet()){
	 				if(c < 3){
	 					System.out.println("Document : " + d.getDocID() +" : " +  docsAndBM.get(d));
	 				}else{
	 					break;
	 				}
	 				c++;
	 			}	
		 		break;
			default:
				break;
		 } 
	 }
	 /*******************************Weighting*******************************/

	 
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
	  * 
	  * @return double array of size numDocs and sets all elements to 0.0
	  *
	 private static double[] initBm(){
		//set bm values to zero
		 double bm[]= new double[NUM_DOCS];
		 for(@SuppressWarnings("unused") double d : bm){
			 d = 0.0;
		 }
		 return bm;
	 }
	 
		/*******************************Query*******************************/
	 /**
	  * Prompts user to input query then saves it
	  */
	 private static ArrayList<String> getQuery(
			 Scanner reader, 
			 String[] stopWords,
			 SnowballStemmer stemmer){		
		 
		 System.out.print("Enter query: ");
		 String query = reader.nextLine();		
		 ArrayList<String> tokens = new ArrayList<>();
		 tokens =  Preprocessor.tokenize(query);
		 tokens =  Preprocessor.removeStopWords(stopWords, tokens);
		 return stemTerms(tokens, stemmer);
		 
	 }
	 
	 /**************************BowDocument manipulation**************************/
	 
	 private static Dataset makeDataSet(
			 String docsLocation, 
			 String[] stopWords, 
			 SnowballStemmer stemmer){
		 
		Dataset set;	
		File docsFolder = new File(docsLocation);
		File[] docs = docsFolder.listFiles();
		
		set = new Dataset(docsFolder.getName());
		
		for(File doc : docs){									
			try {
				set.addDoc(buildBdoc(doc, stopWords, stemmer));
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
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
				SnowballStemmer stemmer) 
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
		
		private static ArrayList<String> stemTerms(
				ArrayList<String>tokens, 
				SnowballStemmer stemmer){
			
			ArrayList<String> terms = new ArrayList<>();
			for(String term : tokens){
				terms.add(Preprocessor.stemTerm(term, stemmer));
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
