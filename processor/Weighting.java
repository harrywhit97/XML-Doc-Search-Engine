package processor;

import java.util.ArrayList;

/**
 * 
 * @author Harry
 * @since 05/06/2017
 * 
 * performs tf*idf and BM25 weighting
 */
public class Weighting {
	
	 /**
	  * Calculate BM25 weighting, relevance of a doc to a query term
	  * where 1.0 is relevant and 0.0  is non-relevant
	  * @param doc BowDocument
	  * @param numDocs number of documents in dataset
	  * @param queryTerm indiv term from query string
	  * @param df document frequency hashmap
	  * @return double BM25
	  */
	
	/**
	 * 
	 * @param doc Bow Document to calculate bm25 score for
	 * @param docs set of document that doc exists within
	 * @param query query to use to judge doc's relevance
	 * @return documents relevance score for query
	 */
	 public static double calculateBM25(
			 BowDocument doc,
			 ArrayList<BowDocument> docs,			
			 ArrayList<String> query){				
		 
		 double numDocs = (double) docs.size();
		 double docLength = (double)doc.getNumTerms();
		 double avgDocLength = (double)doc.getTotalDocLength() / numDocs;		
		 double b = 0.75;		 
		 double K = 1.2 * ((1 - b) + b * (docLength / avgDocLength));	
		 double sum = 0.0;
		 
		 for(String term : query){
			 
			 double df = calcDf(docs, term);			 
			 double tf = calcTf(doc, term);			 
			 double tfInQuery = calcTermFrequencyInQuery(query, term);
			 sum += calcBM(numDocs, tf, df, K, tfInQuery);
		 }	 
		 return sum;
	 }
	 
	 /**
	  * Perform the BM25 equation
	  * @param numDocs number of documents in set
	  * @param tf term frequency of query term in doc
	  * @param df doc frequency of query term
	  * @param K 
	  * @param tfInQuery term frequency of query term in query
	  * @return BM25 score for a query term
	  */
	 private static double calcBM(
			 double numDocs,
			 double tf,
			 double df,
			 double K,
			 double tfInQuery){
		 
		 return (Math.log((numDocs - df + 0.5) / (df + 0.5))) *
				 ((2.2 * tf) / (K + tf) * 101.0 / (100.0 + tfInQuery));
	 }
	 
	 /**
	  * Calculate the terms frequency in the query
	  * @param query
	  * @param term
	  * @return number of occurrences of term in query
	  */
	 private static double calcTermFrequencyInQuery(
			 ArrayList<String> query, 
			 String term){
		 
		 double termFrequencyInQuery = 0.0;
		 
		 for(String termf : query){
			 if(term.equals(termf)){
				 termFrequencyInQuery++;
			 }
		 }
		 return termFrequencyInQuery;		 
	 }
	 	 	
	/**
	 * Calculate and normalize term frequency
	 * @return normalised term frequency
	 */
	private static double calcTf(
			BowDocument doc, 
			String term){
		
		double termFrequency = 0.0;
		
		if(doc.containsTerm(term)) termFrequency = doc.getTermCount(term);			

		
		if(termFrequency == 0.0) return termFrequency;
		return 1 + (Math.log10(termFrequency));		
	}
	
	/**
	 * Calculate the document frequency(df)
	 * of a specified term in a set of documents
	 * @param docs ArrayList<BowDocument>
	 * @param term	String term to use
	 * @return double document frequency of term
	 */
	private static double calcDf(
			ArrayList<BowDocument> docs,
			String term){
		
		double df = 0.0;		
		for(BowDocument doc : docs){
			if(doc.containsTerm(term)){
				df++;
			}
		}		
		return df;
	}
	
	/**
	 * Calculate the inverse document frequency (idf) of a term and normalize
	 * @param numberOfDocs total number of docs in a set
	 * @param df document frequency of a term
	 * @return normalized idf
	 */
	private static double calcIdf(double numberOfDocs, double df){		
		return Math.log10(numberOfDocs / df);
	}
	
	/**
	 * Calculate the tfidf weighting
	 * @param docs ArrayList<BowDocument> set of bow documents
	 * @param doc a single BowDocument
	 * @param term the term to use
	 * @return tf * idf
	 */
	public static double calcTfidf(	
			ArrayList<BowDocument> docs,
			BowDocument doc,
			String term){
		
		double tf = calcTf(doc, term);			
		double idf = calcIdf(docs.size(), calcDf(docs, term));
		
		return tf * idf;
	}
}
