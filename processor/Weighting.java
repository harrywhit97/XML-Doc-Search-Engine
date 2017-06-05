package processor;

import java.util.ArrayList;
import java.util.HashMap;

/* Author : Harry Whittaker
 * Date   : 19/04/2017 
 * 
 * This class can perform two different weightings; ifidf and BM25.
 * 
 */

public class Weighting {
	
	 /**************************Calculate BM25******************************************/
	 /**
	  * calcs BM25 weighting
	  * @param doc BowDocument
	  * @param numDocs number of documents in dataset
	  * @param queryTerm indiv term from query string
	  * @param df documnt frequency hashmap
	  * @return double BM25
	  */
	 public static double calculateBM25(
			 BowDocument doc,
			 int numDocs,
			 String queryTerm,
			 HashMap<String, Integer> documentFrequency,
			 ArrayList<String> query){		 
		
		 double qTermFreqInDoc = documentFrequency.get(queryTerm) != null ? documentFrequency.get(queryTerm) : 0.0;	 
		 
		 double termFrequency = doc.getTermCount(queryTerm);
		 double queryTermFrequency = calcQueryFreq(query, queryTerm);
		 double docLength = doc.getNumTerms();
		 double avgDocLength = doc.getTotalDocLength() / (double)numDocs;
		 
		 double k1 = 1.2;
		 double k2 = 100.0;
		 double b = 0.75;	
		 
		 double K = k1 * ((1 - b) + b * (docLength / avgDocLength));		 
		 double f1 = 1 / ((qTermFreqInDoc  + 0.5) / (numDocs - qTermFreqInDoc + 0.5));		 
		 double f2 = ((k1 + 1) * termFrequency) / (K + termFrequency);		 
		 double f3 = ((k2 + 1) * queryTermFrequency) / (k2 + queryTermFrequency);
		 
		 return Math.log(f1) * (f2 * f3);
	 }
	 
	 /**
	  * 
	  * @param query
	  * @param term
	  * @return
	  */
	 private static int calcQueryFreq(ArrayList<String> query, String term){
		 int count = 0;
		 
		 for(String t : query){
			 if(t.equals(term)){
				 count++;
			 }			 
		 }		 
		 return count;		 
	 }
	 
	 /**************************Calculate tf*idf******************************************/
	 
	 /**
	  * Calculate the tfIdf ranking for given bow documents
	  * @param bDocs
	  * @return sorted hashmap<String, Double> Term, tfidf ranking
	  */
	 public static HashMap<String, Double> claculateTfIdfAndSort(BowDocument bDocs[], HashMap<String, Integer> df){
		 HashMap<String, Double> tfIdf = calculateTfIdf(bDocs[0], bDocs.length, calculateDF(bDocs));
		 return SortMap.sortMapByValuesDouble(tfIdf); 		 
	 }	 
	 
	 /**
	 * Create document frequency hashmap
	 *
	 * @param docCollection - a Collection of BowDocument, i.e. HashMap<String,
	 * BowDocument> of docId:aBowDocument
	 * @return a HashMap<String, Integer> of term:df
	 */
	public static HashMap<String, Integer> calculateDF(BowDocument bDocs[]) {
		HashMap<String, Integer> df = new HashMap<>();	
		
		for(BowDocument bDoc : bDocs){
			for(String term : bDoc.getSortedTermList()){
				if(df.containsKey(term)){
					df.put(term, df.get(term) + 1);
				}else{
					df.put(term, 1);
				}
			}
		}		
		
		return df;
	}
	 
	/**
	 * @param aDoc - a BowDocument
	 * @param noDocs - number of documents in given document set
	 * @param dfs - a HashMap of term:df
	 * @return a HashMap of term:tfidf for every term in a document
	 */
	private static HashMap<String, Double> calculateTfIdf(BowDocument doc, int noDocs,
		HashMap<String, Integer> df) {		
		HashMap<String, Double> tfidfMap = new HashMap<>();
		
		double numerator;
		double denominator;	
		
		double tfidf;		
		double sum = 0;		
		
		for(String term : doc.getMap().keySet()){
			
			int termFreq = doc.getMap().get(term);	
			int docFreq = df.get(term);
			numerator = Tfidf(termFreq, noDocs, docFreq);
			
			for(String termSum : doc.getMap().keySet()){
				termFreq = doc.getMap().get(termSum);	
				docFreq = df.get(termSum);
				sum += (Math.pow(Tfidf(termFreq, noDocs, docFreq), 2.0));
			}
			denominator = Math.sqrt(sum);	
						
			tfidf = numerator / denominator;	
			
			
			tfidfMap.put(term, tfidf);
		}	
		
		
		return tfidfMap;
	}
	
	/** tf * idf
	 * 
	 * @param termFreq term frequency
	 * @param noDocs number of documents in dataset
	 * @param docFreq	document frequency of term
	 * @return
	 */
	private static double Tfidf(int termFreq, int noDocs, int docFreq){
		double tf = 1 + (Math.log10(termFreq));			
		double idf = Math.log10(noDocs/docFreq);	
		return tf * idf;
	}
}
