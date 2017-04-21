package processor;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/* Author : Harry Whittaker
 * Date   : 19/04/2017 
 * 
 * This is the class for the bag of words documents.
 * It contains a hashmap of where the key is a term (String) and the value is the number of 
 * Occurrences of the term in the document.
 * 
 */

public class BowDocument {
	
	private String documentID;
	
	//term freq map
	private HashMap<String, Integer> map;	
	private static int totalDocLength = 0;	
	private int numWords, numTerms;
	
	
	 /**
	 * Constructor
	 * Set the ID of the document, and initiate an empty term:frequency map.
	 * call addTerm to add terms to map
	 * @param docId
	 */
	BowDocument(String _documentID){		
		documentID = _documentID;
		map = new HashMap<>();
		numWords = 0;
		numTerms = 0;
	}

	 /**
	 * Add a term occurrence to the BOW representation
	 * @param term
	 */
	public void addTerm(String term){
		if(!map.containsKey(term)){
			map.put(term, 1);
		}else{
			int temp = (int) map.get(term) + 1;
			map.put(term, temp);
		}
		numTerms++;
		totalDocLength++;
	}
	
	/**
	 *
	 * @param term
	 * @return the term occurrence count for the given term
	 * return 0 if the term does not appear in the document
	 */
	public int getTermCount(String term){	
		if(!map.containsKey(term)){
			return 0;
		}else{
			return map.get(term);
		}		
	}
	
	/**
	 * 
	 * @return number of terms in document
	 */
	public int getNumTerms(){
		return numTerms;
	}
	
	/**
	 *
	 * @return sorted list of all terms occurring in the document
	 */
	 public ArrayList<String>getSortedTermList(){
		 ArrayList<String> list = new ArrayList<String>();
		 		 
		 for(String key : map.keySet()){
			 list.add(key);
		 }
		 
		 Collections.sort(list);		 
		 return list;
	 }
	
	 /**
	  * @return map of term:freq pairs.
	  */
	 public HashMap<String, Integer> getMap(){
		 return map;
	 }
	
	 /**
	  * get the document ID
	  * @return documnetID
	  */
	public String getDocID(){
		return documentID;
	}
	
	/**
	 * Prints the map (both term and term count) 
	 */
	public void printMap(){
		for(String key : map.keySet()){
			System.out.println(key + " : " + map.get(key));
		}
		System.out.println("\n");
	}
	
	/**
	 * prints doc description with map
	 */
	 public void  displayDocInfo(){
		 System.out.println("Document " + documentID + " contains " + numTerms + " terms and has " + numWords + " words.");
		 printMap();
	 }
	 
	 /**
	  * sets number of words for the document
	  * @param _numWords number f words in doc
	  */
	 public void setNumWords(int _numWords){
		 numWords = _numWords;
	 }
	 
	 /**
	  * Accessor for docLength
	  * @return
	  */
	 public int getTotalDocLength(){
		 return totalDocLength;
	 }
	 
}
