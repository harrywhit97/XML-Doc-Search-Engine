package processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tartarus.snowball.SnowballStemmer;

/**
 * 
 * @author Harry Whittaker
 * @since 05/06/2017
 * 
 * This class handles the pre-processing of the raw  XML documents.
 * This includes the extraction of the text with in the documents <text> tags,
 * the removal of all paragraph tags and non alphabetical characters with in this text,
 * converting to lower case,
 * the separation of this text in to terms   
 * and the stemming of these terms.
 */
public class Preprocessor {
			
	
	/**
	 * Removes all words in stop words array from terms array list
	 * @param stopWords String array of stop words
	 * @param terms	String array list of terms
	 * @return terms array list with all words from stopWords removed
	 */
	public static ArrayList<String> removeStopWords(
			String[] stopWords, 
			ArrayList<String> terms){
		
		for(String stopWord : stopWords){
			while(terms.contains(stopWord)){
				terms.remove(stopWord);
			}
		}
		return terms;
	}
			
	/**
	 * Gets text and removes tags and punctuation from raw document also converts to lowercase and adds spacing around occurrences of "quot"
	 * @param doc String containing raw document
	 * @return String of edited text section
	 */
 	public static ArrayList<String> tokenize(
 			String rawDoc){
 		
		String docText = getText(rawDoc);
		docText = removeNonAlphabeticalChars(docText);	
		ArrayList<String> tokens = new ArrayList<>();
		Collections.addAll(tokens, docText.split(" "));
		return tokens;
	}
	
	/**
	 * Removes all non a-z characters, makes string lower case, adds spacing around occurrences of "quot"
	 * @param doc String containing text section of document
	 * @return String containing only lowercase a-z text  
	 */
	private static String removeNonAlphabeticalChars(
			String doc){	
		
	    String temp  = doc.toLowerCase();		
		
		temp = temp.replaceAll("[^a-z ]", "");		
		temp = temp.replaceAll("quot", " quot ");
		temp = temp.replaceAll("-", " ");
		temp = temp.replaceAll("[.]", " ");
		
		return temp;
	}
	
	/**
	 * Removes all text that does not exist with in the documents <text> tags
	 * Removes all <p> and </p> tags
	 * @param doc String containing unedited document
	 * @return String of all characters exclusively between the <text> tags
	 */
	private static String getText(
			String doc){
		
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
			
	 /**
	 *Stems a inputed term using the snowball tartarus stemmer
	 * @param term a String
	 * @param stemmer to be used
	 * @return a stemmed form of given word
	 */
	public static String stemTerm(
			String term, 
			SnowballStemmer stemmer) {		
		
		stemmer.setCurrent(term);		
		stemmer.stem();
		
		return stemmer.getCurrent();
	}	 
}
