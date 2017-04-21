package processor;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collections; 

import org.tartarus.snowball.SnowballStemmer;

/* Author : Harry Whittaker
 * Date   : 19/04/2017 
 * 
 * This class handles the pre-processing of the raw  XML documents.
 * This includes the extraction of the text with in the documents <text> tags,
 * the removal of all paragraph tags and non alphabetical characters with in this text,
 * converting to lower case,
 * the separation of this text in to terms   
 * and the stemming of these terms.
 * 
 */

public class Preprocessor {
	
	//Snowball stemmer that will be used
	static SnowballStemmer stemmer;
	
	/**
	 * Class constructor
	 * @param docs array of strings representing the documents
	 */
	public Preprocessor(String docs[]){
		
		//Initialize stemmer
		 Class<?> stemClass;
		try {
			stemClass = Class.forName("org.tartarus.snowball.ext.englishStemmer");
			stemmer = (SnowballStemmer)stemClass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		 
	}	
	
	/**
	 * Tokenizes, stems then removes stop words from doc
	 * @param doc String representing a xml document
	 * @param stopWords array of Strings that are stop words sorted alphabetically
	 * @return String[] of pre-processed document
	 */
	public static ArrayList<String> tokenizeStemAndStopDoc(String doc, String[] stopWords){
		ArrayList<String> terms = new ArrayList<>();
		Collections.addAll(terms, tokenize(doc)); 
		terms = removeStopWords(stopWords, terms);
		
		return terms;
	}
	
	/**
	 * 
	 * @param stopWords String array of stop words
	 * @param terms	String array list of terms
	 * @return
	 */
	private static ArrayList<String> removeStopWords(String[] stopWords, ArrayList<String> terms){		
		for(String stop : stopWords){
			if(terms.contains(stop)){
				terms.remove(stop);
			}
		}
		return terms;
	}
			
	/*******************************tokenize text*******************************/

	/**
	 * Gets text and removes tags and punctuation from raw document also converts to lowercase and adds spacing around occurrences of "quot"
	 * @param doc String containing raw document
	 * @return String of edited text section
	 */
 	public static String[] tokenize(String doc){
		String docText = getText(doc);
		docText = removeNonAlphabeticalChars(docText);	
		String tokens[] = docText.split(" ");
		return tokens;
	}
	
	/**
	 * Removes the <p> tags, and all non a-z characters, makes string lower case, adds spacing around occurrences of "quot"
	 * @param doc String containing text section of document
	 * @return String containing actual text that with out punctuation and tags
	 */
	private static String removeNonAlphabeticalChars(String doc){		
	    String temp = doc;
		temp  = temp.toLowerCase();
		
		//remove all non alphabetical characters
		temp = temp.replaceAll("[^a-z ]", "");		
		temp = temp.replaceAll("quot", " quot ");
		return temp;
	}
	
	/**
	 * Removes All text that does not exist with in the documents <text> tags
	 * @param doc String containing unedited document
	 * @return String of all characters exclusively between the <text> tags
	 */
	private static String getText(String doc){
		String pattern = "<text>.*<" + "\\" + "/text>";
	    Pattern r = Pattern.compile(pattern);
		 
	     String temp = doc;
	     Matcher m = r.matcher(doc);
	     if (m.find( )) {
	        temp = doc.substring(m.start()+6, m.end()-7);
	        temp = doc.replaceAll("<p>", "");
	    	temp = temp.replaceAll("</p>", "");
	     }    
	     return temp;	
	}
			
	 /**************************Stemming******************************************/
	 /**
	 *This stems a inputed term using the snowball stemmer
	 * @param word a String
	 * @return a stemmed form of given word
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static String stemTerm(String word) throws InstantiationException, IllegalAccessException, ClassNotFoundException {		
		stemmer.setCurrent(word);		
		stemmer.stem();
		return stemmer.getCurrent();
	}	 
}
