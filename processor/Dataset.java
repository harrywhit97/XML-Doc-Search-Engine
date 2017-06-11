package processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Dataset {
	private ArrayList<BowDocument> docs;
	private String title;
	
	private HashMap<BowDocument, Double> positive;
	private HashMap<BowDocument, Double> negative;
	
	/**
	 * Class constructor
	 * @param numDocs number of docs that will be added
	 * @param name name of this set
	 */
	public Dataset(
			String name){
	
		this.title = name;		
		docs = new ArrayList<>();
	}
	
	/**
	 * add a bow doc to this set
	 * @param doc BowDocument to be added
	 * @throws Exception	when dataset is full
	 */
	public void addDoc(
			BowDocument doc) 
					throws Exception{
		
		docs.add(doc);	
	}
	
	/**
	 * prints the docs in this set
	 */
	public void displayDocIds(){
		
		for(BowDocument doc : docs){
			System.out.println("\t" + doc.toString());
		}
	}
	
	/**
	 * get the name of this set
	 */
	public String toString(){
		return title;
	}
	
	/**
	 * get the bow docs in this set
	 * @return arraylist of bowdocs
	 */
	public ArrayList<BowDocument> getDocs(){
		
		return docs;
	}
	
	/**
	 * display dataset title and doc titles
	 */
	public void displaySetInfo(){
		
		System.out.println("Dataset : " + title);
		displayDocIds();
	}
	
	/**
	 * Set weighting hash map
	 * @param map HashMap<String, Double> where key is doc name and value is BM25
	 */	
	public void setPosNeg(
			TreeMap<String, Double> map){		
		
		positive = new HashMap<BowDocument, Double>();
		negative = new HashMap<BowDocument, Double>();
		double percentagePos = 20;
		double numPosDocs = ((double)map.size() / 100.0) * percentagePos;
		double currentNumPosDocs = 0.0;
		for(String key : map.keySet()){	
			
			try {
				if(currentNumPosDocs++ < numPosDocs){	
					positive.put(getDocByName(key), map.get(key));
		
				}else{
					negative.put(getDocByName(key), map.get(key));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		positive = SortMap.sortBowDoc(positive);
		negative = SortMap.sortBowDoc(negative);
	}
		
	/**
	 * Get a document by the docID
	 * @param name DocID
	 * @return BowDocument with same name as input name
	 * @throws Exception When document does not exist with inut name
	 */
	private BowDocument getDocByName(String name) throws Exception{
		for(BowDocument doc : docs){
			if(doc.toString().equals(name)){
				return doc;
			}
		}
		throw new Exception("Invalid doc name");
	}
	
	/**
	 * Print the weightings hash map
	 */
	public void printWeightedMap(){
		String title = "rank\t Doc\tWeight bm25";
		int rank = 1;
		
		System.out.println("D+");
		System.out.println(title);		
		for(BowDocument doc : positive.keySet()){
			System.out.println(rank++ +"\t" + doc.toString() + "\t" + positive.get(doc));
		}
		
		System.out.println("\nD-");
		System.out.println(title);
		for(BowDocument doc : negative.keySet()){
			System.out.println(rank++ +"\t" + doc.toString() + "\t" + negative.get(doc));
		}
	}
	
	/**
	 * Get the positive documents with their BM25 weighting
	 * @return hashmap<BowDocument, double>
	 */
	public HashMap<BowDocument, Double> getPosMap(){
		return positive;
	}
	
	/**
	 * Get the negative documents with their BM25 weighting
	 * @return hashmap<BowDocument, double>
	 */
	public HashMap<BowDocument, Double> getNegMap(){
		return negative;
	}
	
	/**
	 * Get the positive documents of this set
	 * @return Array of BowDocuments that are positive
	 */
	public BowDocument[] getPosDocs(){
		BowDocument[] docs = new BowDocument[positive.size()];
		int i = 0;
		for(BowDocument d : positive.keySet()){
			docs[i++] = d;
		}
		return docs;
	}
	
	/**
	 * Get the negative documents of this set
	 * @return Array of BowDocuments that are negative
	 */
	public BowDocument[] getNegDocs(){
		BowDocument[] docs = new BowDocument[negative.size()];
		int i = 0;
		for(BowDocument d : negative.keySet()){
			docs[i++] = d;
		}
		return docs;
	}
}
