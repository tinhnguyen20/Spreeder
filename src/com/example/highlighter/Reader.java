package com.example.highlighter;

import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Set;
import java.io.File;
import java.io.IOException;
/**
 * Reads in File
 * Breaks up the String (file) into chunks
 *  - Breaks up the file into sentences, and chunks it from there
 *  
 *  
 *  Desired Length = approx amount of words
 *  - Chunks will not necessarily be broken up by the number of words but rather the field of view.
 *  - Chunks will be approximately the same length
 * 
 * 
 */


public class Reader{   

	private static int RIGHT_SPECIAL_DOUBLEQUOTE = 8221;
    private String file;
    private String filename;
    private Scanner in;
    private ArrayList<Sentence> sentences; 
    private ArrayList<Chunk> chunks;
    private Set<String> honorifics;
    private Set<String> conjunctions;
    private Set<String> prepositions;
    private int[] length_hist;
    private int desired_length;
    

    /**
     * Read in a textfile
     * File -> String
     * 
     * Desired_Length is the approximate amount of words
     * in each chunk.
     * 
     * Ex. Desired_Length = 3;
     * Num Words in Chunk: 2 - 4
     * @param prepositions 
     * @param honorifics2 
     * @param conjunctions 
     * 
     */
    public Reader(int desired_length, String text, Set<String> conjunctions, Set<String> honorifics, Set<String> prepositions) {
        // 1. read in file  
    	
        file = text;
        sentences = new ArrayList<Sentence>();
        chunks = new ArrayList<Chunk>();
        this.honorifics = honorifics;
        this.conjunctions = conjunctions;
        this.prepositions = prepositions;
        this.desired_length = desired_length;
        
        createSentences();

        // put all the chunks created into chunks arrayList
        storyToChunks();
        trimSpaces();
    }
    /**
     * String (file) -> String
     *  - divides the file into sentences 
     *  - sentences are broken up by '!' , '.', '?'
     */
    public void createSentences(){
        // ----- Main Sentence Separator
        // - '.' , '!', '?'
        
        int exclamationIndex = file.indexOf(HardBreak.EXCLAMATION.character());
        int questionMarkIndex = file.indexOf(HardBreak.QUESTION_MARK.character());
        int periodIndex = file.indexOf(HardBreak.PERIOD.character());
        int semiColonIndex = file.indexOf(HardBreak.SEMICOLON.character());
        int rightParenthIndex = file.indexOf(HardBreak.RIGHT_PARENTH.character());
        int rightQuoteIndex = file.indexOf(HardBreak.RIGHT_QUOTE.character());
        
        
        String text_before_period = "";
        
        
        while(periodIndex > -1 || questionMarkIndex > -1 || exclamationIndex > -1){
            
            
            // get the correct index of the period (can be part of a honorific)
            // while text before period is a honorific, find another period.

            // getting text_before_period
            //int add = 0;
            
            // honorific is at 0
            if(periodIndex <= 4){
            	text_before_period = file.substring(0, periodIndex + 1); 
            	if(text_before_period.indexOf(" ") > -1){
            		int space = text_before_period.indexOf(" ");
            		text_before_period = text_before_period.substring(0, space);
            		
            	}
            }
            else{
            text_before_period = file.substring(periodIndex - 3, periodIndex + 1);
            	if(text_before_period.indexOf(" ") > -1){
            		int space = text_before_period.indexOf(" ");
            		text_before_period = text_before_period.substring(space + 1);                
            		//add += periodIndex;
            	}
            }
            
            // checking if honorific
            while(honorifics.contains(text_before_period)){
                String temp = file.substring(periodIndex + 1);
                periodIndex += temp.indexOf(".") + 1;
                text_before_period = file.substring(periodIndex - 4, periodIndex + 1);
                if(text_before_period.indexOf(" ") > -1){
                    int space = text_before_period.indexOf(" ");
                    text_before_period = text_before_period.substring(space + 1);
                }
                
                
            }
            HardBreak hb = findEarliestHardBreak(periodIndex, questionMarkIndex, exclamationIndex, 
            									 semiColonIndex, rightParenthIndex, rightQuoteIndex);
            
            if(hb == null){
            	break;
            }
            else{
            	switch (hb) {
				case PERIOD:{
					System.out.println("printing file: " + file);
					Sentence sent = createHardestSentence(file, periodIndex);
					file = file.substring(sent.getLength());
					sentences.add(sent);
					break;
				}
				case QUESTION_MARK:{
					Sentence sent = createHardestSentence(file, questionMarkIndex);
					file = file.substring(sent.getLength());
					sentences.add(sent);
					break;
				}
				case EXCLAMATION:{
					Sentence sent = createHardestSentence(file, exclamationIndex);
					file = file.substring(sent.getLength());
					sentences.add(sent);
					break;
				}
				case SEMICOLON:{
					Sentence sent = createNormalChunk(file, semiColonIndex);
					file = file.substring(sent.getLength());
					sentences.add(sent);
					break;
				}
				case RIGHT_PARENTH:{
					Sentence sent = createNormalChunk(file, rightParenthIndex);
					file = file.substring(sent.getLength());
					sentences.add(sent);
					break;
				}
				case RIGHT_QUOTE:{
					Sentence sent = createNormalChunk(file, rightQuoteIndex);
					file = file.substring(sent.getLength());
					sentences.add(sent);
					break;
				}
				default:{
					System.out.println("DID NOTHING");
					break;
				}
            }
                
        }

        
        
        exclamationIndex = file.indexOf(HardBreak.EXCLAMATION.character());
        questionMarkIndex = file.indexOf(HardBreak.QUESTION_MARK.character());
        periodIndex = file.indexOf(HardBreak.PERIOD.character());
        semiColonIndex = file.indexOf(HardBreak.SEMICOLON.character());
        rightParenthIndex = file.indexOf(HardBreak.RIGHT_PARENTH.character());
        rightQuoteIndex = file.indexOf(HardBreak.RIGHT_QUOTE.character());
    }
}
    
    private Sentence createHardestSentence(String file, int symbol) {
    	// if the symbol is at the very end (no other characters after) == end of the text
    	System.out.println("printing file: " + file);
    	
    	if(file.charAt(symbol) == HardBreak.PERIOD.character() ||
    	   file.charAt(symbol) == HardBreak.EXCLAMATION.character() ||
    	   file.charAt(symbol) == HardBreak.QUESTION_MARK.character() ||
    	   file.charAt(symbol) == HardBreak.RIGHT_PARENTH.character() ||
    	   file.charAt(symbol) == HardBreak.SEMICOLON.character()){
    		return new Sentence(file, desired_length, prepositions, conjunctions);
    	}
    	else if(file.charAt(symbol + 1) == ' '){
            return new Sentence(file.substring(0, symbol + 2), desired_length, prepositions, conjunctions);
        }
        else if((int)file.charAt(symbol + 1) == RIGHT_SPECIAL_DOUBLEQUOTE || file.charAt(symbol + 1) == '"'){
            return new Sentence(file.substring(0, symbol + 3), desired_length, prepositions, conjunctions);
        }
		return null;
	}
    
    private Sentence createNormalChunk(String temp, int symbol) {	
		return new Sentence(temp.substring(0, symbol + 2), desired_length, prepositions, conjunctions);
        //temp = temp.substring(temp.indexOf(":") + 1);
	}

	private HardBreak findEarliestHardBreak(int periodIndex,
			int questionMarkIndex, int exclamationIndex, int semiColonIndex,
			int rightParenthIndex, int rightQuoteIndex) {
		
    	HashMap<String, Integer> map = new HashMap<String, Integer>();
    	map.put("period", periodIndex);
    	map.put("question_mark", questionMarkIndex);
    	map.put("exclamation", exclamationIndex);
    	map.put("semi_colon", semiColonIndex);
    	map.put("right_parenth", rightParenthIndex);
    	map.put("right_quote", rightQuoteIndex);
    	
    	String smallest = null;
    	int num = 1000000;
    	
    	for(String key: map.keySet()){
    		int value = map.get(key);
    		if(value < num && value != -1){
    			smallest = key;
    			num = value;
    		}
    	}
    	
    	if(smallest == null){
    		return null;
    	}
    	else{
    		return getHardBreak(smallest);
    	}
		
	}

	private HardBreak getHardBreak(String smallest) {
		HardBreak hb = null;
		if(smallest.equals("period")){
			hb = HardBreak.PERIOD;
		}
		else if(smallest.equals("question_mark")){
			hb = HardBreak.QUESTION_MARK;
		}
		else if(smallest.equals("exclamation")){
			hb = HardBreak.EXCLAMATION;
		}
		else if(smallest.equals("semi_colon")){
			hb = HardBreak.SEMICOLON;
		}
		else if(smallest.equals("right_parenth")){
			hb = HardBreak.RIGHT_PARENTH;
		}
		else if(smallest.equals("right_quote")){
			hb = HardBreak.RIGHT_QUOTE;
		}
		return hb;
	}

	/**
     *  updates the chunk arrayList to contain all the chunks of all the sentences
     */
    public void storyToChunks(){
        for(Sentence sentence:sentences){
            for(Chunk chunk:sentence.getChunks()){
                chunks.add(chunk);
            }
        }
    }

    
    public void printStoryInChunks(){
        for(Chunk chunk:chunks){
            System.out.println(chunk.getText());

        }
    }


    public void printHistogram(){
        int num = 0;
        for(int i: length_hist){
            System.out.println(num + ": " + i);
            num++;
        }
    }

    
    public void trimSpaces(){
        ArrayList<Chunk> temp_chunks = new ArrayList<Chunk>();
        String text = "";
        int index = 0;
        for(Chunk chunk: chunks){
            text = chunk.getText();
            while(text.indexOf("  ") > -1){
                index = text.indexOf("  ");
                text = text.substring(0, index + 1) + text.substring(index + 2);
            }
            temp_chunks.add(new Chunk(text));
        }
        
        chunks.clear();
        for(Chunk chunk: temp_chunks){
            chunks.add(chunk);
        }
    }
    
    
    
    public String getOriginalText(){
    	String text = "";
		
		try{
            in = new Scanner(new File(filename));
        }catch(IOException i){
            System.out.println("Error: " + i.getMessage());
        }
        while(in.hasNextLine()){
        	String line = in.nextLine();
            if(line.equals("")){
            	text += line + "\n\n";
            }
            else{
            	text += line + " ";
            }
        }
		return reformatText(text);
    }
    
    private String reformatText(String text){
    	return text;
    }
    
    public ArrayList<Chunk> getChunks(){
    	return chunks;
    }
}