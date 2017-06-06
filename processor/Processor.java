package processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
	
	//location of folder containing docs folder and stopwords file
	
	private static String[] stopWords;	
	
	private static BowDocument bDocs[] = new BowDocument[NUM_DOCS];
	
	private static double bm[]; 
	
	//Weighing hashmaps
	private static HashMap<String, Integer> df;
	private static HashMap<String, Double> tfIdf;
	private static HashMap<BowDocument, Double> docsAndBM;
	
	//Query
	private static String query;
	private static ArrayList<String> queryTerms;	 
	 
	 public static void main(String[] args) throws Exception {		 
		
		 String docs[] = getDocuments();			 
		 getStopWords();
		 initializeBDocs(docs);		 
		 addDocsToBdocs(docs); 		 
		 bDocs[0].displayDocInfo();
		 calcIfidf();
		 
		 Scanner reader = new Scanner(System.in);		 
		 
		 while(true){
			 getQuery(reader);		
			 calcBM25();
			 printResult();	
			 System.out.println();
		 }			
	}	
	 
	 /**
	  * prints result
	  */
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
	  * calculates the ifidf weighting and saves to ifIdf hashmap	 
	  */
	 private static void calcIfidf(){
		 df = Weighting.calculateDF(bDocs); 		 
		 tfIdf = Weighting.claculateTfIdfAndSort(bDocs, df);
	 }
	 
	 /**
	  * Calculates BM25 weighting and saves to docs and bm hashmap
	  */
	 private static void calcBM25(){
		 bm= initBm();		 
		 
		 //calc bm25
		 for(int i = 0; i < queryTerms.size(); i++){
			 for(int d = 0; d < bDocs.length; d++){
				 double b = Weighting.calculateBM25(bDocs[d], bDocs.length, queryTerms.get(i), df, queryTerms);
				 bm[d] += b;				 
			 }
		 }		 
		 docsAndBM = new HashMap<>();
		 
		 for(int i = 0; i < NUM_DOCS; i++){
			 docsAndBM.put(bDocs[i], bm[i]);
		 }
		 
		 docsAndBM = SortMap.sortBowDoc(docsAndBM);
	 }
	 
	 /**
	  * 
	  * @return double array of size numDocs and sets all elements to 0.0
	  */
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
	 private static void getQuery(Scanner reader){		
		 System.out.print("Enter query: ");
		 query = reader.nextLine();			 	
		 queryTerms =  Preprocessor.tokenizeStemAndStopDoc(query, stopWords);
	 }
	 
	 /**************************BowDocument manipulation**************************/
	 private static void addDocsToBdocs(String[] docs){
		 for(int i = 0; i < NUM_DOCS; i++){
			 ArrayList<String> terms = Preprocessor.tokenizeStemAndStopDoc(docs[i], stopWords);
			 addTerms(terms, bDocs[i++]);
		 }	
	 }
	 	 
	/**
	 * Initializes array of BowDocument
	 * @param docs array of dcrings representing the documents
	 * @param bDocs array of bdocs
	 * @return
	 */
	private static void initializeBDocs(String docs[]){
		for(int i  = 0; i < NUM_DOCS; i++){	 
			 String id = findDocID(docs[i]);			 
			 bDocs[i] = new BowDocument(id);
		 }	
	}
		 
	/**
	 * Adds terms to BowDocument 
	 * @param tokens Array of terms to be added to bdoc
	 * @param bDoc BowDocument
	 */
	public static void addTerms(ArrayList<String> terms, BowDocument bDoc){	
		
		for(String term : terms){			
			if(isValid(term)){
				bDoc.addTerm(term);
			}
		}
	}	
			
	/**
	 * Checks that term is a valid term
	 * @param s term
	 * @return
	 */
	private static boolean isValid(String s){
		String pattern = "[a-z][a-z]*";
	    Pattern r = Pattern.compile(pattern);		 
	     Matcher m = r.matcher(s);
	     if (m.find()) {
	        return true;
	     }else {
	        return false;
	     }     
	}
	
	/**
	 * Finds the doc ID of inputed document	
	 * @param doc String containing unedited document
	 * @return doc id [0-9]* or null id no id was found
	 */
	private static String findDocID(String doc){
		String pattern = "itemid=\"[0-9]*";
	    Pattern r = Pattern.compile(pattern);
		 
	     String temp = null;
	     Matcher m = r.matcher(doc);
	     if (m.find()) {
	        temp = doc.substring(m.start() + 8, m.end());
	     }else {
	        System.out.println("NO MATCH");
	     }     
	     return temp;
	}
		
	/*******************************read documents*******************************/	
		
	/**
	 * 
	 * @param location directory path of queries file
	 * @return single String with all raw queries in it
	 */
	private static String getDocString(File file){
		String thisLine = null;
		String line = "";
		
		try {
		     BufferedReader br = new BufferedReader(new FileReader(file));
		     br.readLine();
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
