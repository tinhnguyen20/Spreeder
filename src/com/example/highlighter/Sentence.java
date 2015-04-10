package com.example.highlighter;

import java.util.*;
/**
 * - Contains an arraylists of chunks
 * - Chunks the sentences
 *   - chunks based off of SoftBreak marks, conjunctions, and prepositions
 * - Breaks down chunks that are too large 
 * - Parses up chunks that are too small
 * 
 * Using set allows for quick lookup
 */
public class Sentence{
    private String sentence;
    private ArrayList<Chunk> chunks;
    private Set<String> prepositions;
    private Set<String> conjunctions;
    private int upperbound;
    private int lowerbound;
    private int desired_length;
    /**
     * Constructor
     * - loads prepositions 
     * - sentence -> array of words
     * 
     */
    public Sentence(String sentence, int desired_length, Set<String> prepositions, Set<String> conjunctions) {
        this.sentence = sentence;
        this.desired_length = desired_length;
        chunks = new ArrayList<Chunk>();
        this.prepositions = prepositions;
        this.conjunctions = conjunctions;
        upperbound = 0;
        lowerbound = 0;
        

        run();
        

    }
    
    
    public void run(){
    	chunkSentence();
        breakDown(desired_length);
        parseUp();
    }
    
    



    /**
     * Sentence -> Chunk
     * 
     * adds chunks to the chunks(arraylist)
     * 
     *  - 1. commas
     *  - 2. prepositions
     *  
     *  NOTE:
     *  To avoid ConcurrentModifierException
     *  temp_chunks and chunks are used interchangably
     */
    public void chunkSentence(){
        ArrayList<Chunk> temp_chunks = new ArrayList<Chunk>(); // to avoid concurrent modifier exception
        String temp = sentence;

        // ---- Main Subphrase
        // -- Commas, SemiColon, Colon, parenthesis, quotes
        int commaIndex = temp.indexOf(SoftBreak.COMMA.character());
        int colonIndex = temp.indexOf(SoftBreak.COLON.character());
        int semiColonIndex = temp.indexOf(SoftBreak.SEMICOLON.character());
        int dashIndex = temp.indexOf(SoftBreak.DASH.character());
        int leftParenthIndex = temp.indexOf(SoftBreak.LEFT_PARENTH.character());
        int rightParenthIndex = temp.indexOf(SoftBreak.RIGHT_PARENTH.character());
        int quoteIndex = temp.indexOf(SoftBreak.QUOTE.character());
        int leftQuoteIndex = temp.indexOf(SoftBreak.LEFT_QUOTE.character());
        int rightQuoteIndex = temp.indexOf(SoftBreak.RIGHT_QUOTE.character());
        
        
        while(commaIndex > -1 || colonIndex > -1 || 
        semiColonIndex > -1 || dashIndex > -1 ||
        leftParenthIndex > -1 || rightParenthIndex > -1 ||
        quoteIndex > -1 || rightQuoteIndex > -1 || 
        leftQuoteIndex > -1){

        	
            
            SoftBreak SoftBreak = findEarlistSoftBreak(commaIndex, colonIndex, semiColonIndex, dashIndex, leftParenthIndex, 
            												rightParenthIndex, quoteIndex, leftQuoteIndex, rightQuoteIndex);

            if(SoftBreak == null){
            	break;
            }
            else{
	            switch(SoftBreak){
	            case COMMA:{
	            	Chunk ch = createCommaChunk(temp, commaIndex);
	            	temp =  temp.substring(ch.getLength());
	            	temp_chunks.add(ch);
	            	break;
	            }
	            case COLON:{
	            	Chunk ch = createNormalChunk(temp, colonIndex);
	            	temp =  temp.substring(ch.getLength());
	            	temp_chunks.add(ch);
	            	break;
	            }
	            case SEMICOLON:{
	            	Chunk ch = createNormalChunk(temp, semiColonIndex);
	            	temp =  temp.substring(ch.getLength());
	            	temp_chunks.add(ch);
	            	break;
	            }
	            case DASH:{
	            	Chunk ch = createNormalChunk(temp, dashIndex);
	            	temp =  temp.substring(ch.getLength());
	            	temp_chunks.add(ch);
	            	break;
	            }
	            case LEFT_PARENTH:{
	            	Chunk ch = createSpecialLeftChunk(temp, leftParenthIndex);
	            	temp =  temp.substring(ch.getLength());
	            	temp_chunks.add(ch);
	            	break;
	            }
	            case RIGHT_PARENTH:{
	            	Chunk ch = createNormalChunk(temp, rightParenthIndex);
	            	temp =  temp.substring(ch.getLength());
	            	temp_chunks.add(ch);
	            	break;
	            }
	            case QUOTE:{
	            	Chunk ch = createQuoteChunk(temp, quoteIndex);
	            	temp =  temp.substring(ch.getLength());
	            	temp_chunks.add(ch);
	            	break;
	            }
	            case LEFT_QUOTE:{
	            	Chunk ch = createSpecialLeftChunk(temp, leftQuoteIndex);
	            	temp =  temp.substring(ch.getLength());
	            	temp_chunks.add(ch);
	            	break;
	            }
	            case RIGHT_QUOTE:{
	            	Chunk ch = createNormalChunk(temp, rightQuoteIndex);
	            	temp =  temp.substring(ch.getLength());
	            	temp_chunks.add(ch);
	            	break;
	            }
				default:
					System.out.println("DID NOTHING");
					break;
	            
	            }
            }
            
            commaIndex = temp.indexOf(SoftBreak.COMMA.character());
            colonIndex = temp.indexOf(SoftBreak.COLON.character());
            semiColonIndex = temp.indexOf(SoftBreak.SEMICOLON.character());
            dashIndex = temp.indexOf(SoftBreak.DASH.character());
            leftParenthIndex = temp.indexOf(SoftBreak.LEFT_PARENTH.character());
            rightParenthIndex = temp.indexOf(SoftBreak.RIGHT_PARENTH.character());
            quoteIndex = temp.indexOf(SoftBreak.QUOTE.character());
            leftQuoteIndex = temp.indexOf(SoftBreak.LEFT_QUOTE.character());
            rightQuoteIndex = temp.indexOf(SoftBreak.RIGHT_QUOTE.character());
        }
        temp_chunks.add(new Chunk(temp));

        
        
        
        // SoftBreak Tester 

       
        chunks.clear();
        for(Chunk chunk:temp_chunks){
        	chunks.add(chunk);
        }

        // ---- Conjunctions
        
        
        chunkByConjunction(chunks);
        clearEmptyChunks(chunks);             
        
        
        
        chunkByPreposition(chunks);
        clearEmptyChunks(chunks);

        
       
        
    }
    


	private void clearEmptyChunks(ArrayList<Chunk> chunks) {
		for(int i = 0; i < chunks.size(); i++){
			if(chunks.get(i).isEmpty()){
				chunks.remove(i);
				i--;
			}
		}
	}

	private void chunkByConjunction(ArrayList<Chunk> list) {
    	ArrayList<Chunk> temp_chunks = new ArrayList<Chunk>();
    	for(Chunk ch: list){
    		temp_chunks.add(ch);
    	}
    	chunks.clear();
    	
        for(Chunk chunk:temp_chunks){
            String chunk_text = chunk.getText().trim();
            for(String word:chunk.getWords()){
                // check if conjunction
                int conj_pos = chunk_text.indexOf(word);
                if(conjunctions.contains(word)){
                    if((conj_pos == 0) && 
                    (chunk_text.substring(word.length() + 1).indexOf(word) > -1)){
                        // making variations to chunk_text to avoid re looking up the first word
                        // ex. found 2nd "and" in "and played with his brothers and sisters all day"
                        conj_pos = chunk_text.substring(word.length() + 1).indexOf(word) + word.length() + 1;
                        //conj_pos -= word.length();
                        if((chunk_text.charAt(conj_pos - 1) == ' ') &&
                        (chunk_text.charAt(conj_pos + word.length()) == ' ')){
                            chunks.add(new Chunk(chunk_text.substring(0, conj_pos)));
                            chunk_text = chunk_text.substring(conj_pos);
                        }
                    }
                    else if((conj_pos == chunk_text.length() - word.length()) ||
                    (conj_pos == 0) || 
                    ((chunk_text.charAt(conj_pos - 1) == ' ') &&
                        (chunk_text.charAt(conj_pos + word.length()) == ' '))){
                        chunks.add(new Chunk(chunk_text.substring(0, chunk_text.indexOf(word))));
                        chunk_text = chunk_text.substring(conj_pos);
                    }
                }
            }
            chunks.add(new Chunk(chunk_text));
        }
		
	}
	
	private void chunkByPreposition(ArrayList<Chunk> chunks){
		ArrayList<Chunk> temp_chunks = new ArrayList<Chunk>();
		for(Chunk ch: chunks){
			temp_chunks.add(ch);
		}
		chunks.clear();
		
    	int temp_prep_pos = 0;
        int prep_pos = 0;
        for(Chunk chunk:temp_chunks){
            String chunk_text = chunk.getText();
            for(String word:chunk.getWords()){
                //if a preposition and not just a prep inside word. ex.  'as' in 'was'
                prep_pos = chunk_text.indexOf(" " + word + " ") + 1;
                temp_prep_pos = 0;
                if(prepositions.contains(word)){ 
                    if(prep_pos == 0){
                        // if there is another instance of that word, split it there
                        temp_prep_pos = (chunk_text.substring(word.length() + 1).indexOf(word));
                        if((temp_prep_pos > -1) &&
                        (chunk_text.charAt(temp_prep_pos - 1) == ' ') && 
                        (chunk_text.charAt(temp_prep_pos + word.length()) == ' ')){
                            prep_pos = temp_prep_pos;
                            chunks.add(new Chunk(chunk_text.substring(0, prep_pos)));
                            //System.out.println(chunk_text.substring(0, prep_pos));
                            chunk_text = chunk_text.substring(prep_pos);
                        }
                    }
                    else if((prep_pos > 0) &&
                    (chunk_text.indexOf(" " + word + " ") > -1)){
                        prep_pos = chunk_text.indexOf(" " + word + " ") + 1;
                        chunks.add(new Chunk(chunk_text.substring(0, prep_pos)));
                        chunk_text = chunk_text.substring(prep_pos);
                    }
                }
            }
            chunks.add(new Chunk(chunk_text));
            //System.out.println(chunk_text);
        }
	}

	private Chunk createQuoteChunk(String temp, int quote) {
    	
    	if(temp.charAt(quote - 1) == ' '){
    		//System.out.println("HELLO");
            return new Chunk(temp.substring(0, quote));
        }
        else{
            return new Chunk(temp.substring(0, quote + 2));
        }
	}

	private Chunk createSpecialLeftChunk(String temp, int left_symbol) {
		return new Chunk(temp.substring(0, left_symbol));
        //temp = temp.substring(temp.indexOf("("));
	}

	private Chunk createNormalChunk(String temp, int symbol) {	
		return new Chunk(temp.substring(0, symbol + 2));
        //temp = temp.substring(temp.indexOf(":") + 1);
	}

	private Chunk createCommaChunk(String temp, int comma) {
		if(temp.charAt(comma + 1) == ' '){
            return new Chunk(temp.substring(0, comma + 2));
            //temp = temp.substring(temp.indexOf(",") + 1);
        }
        else if((temp.charAt(comma + 1) == '"') || ((int)temp.charAt(comma + 1) == SoftBreak.RIGHT_QUOTE.character())){
            return new Chunk(temp.substring(0, comma + 3));
            //temp = temp.substring(temp.indexOf(",") + 2);
        }
		return null;
		

	}

	public SoftBreak findEarlistSoftBreak(int comma, int colon, int semi_colon,
			int dash, int left_parenth, int right_parenth, int quote,
			int left_quote, int right_quote) {
    	
//		System.out.println("comma: " + comma);
//		System.out.println("colon: " + colon);
//		System.out.println("semi: " + semi_colon);
//		System.out.println("dash: " + dash);
//		System.out.println("left_p: " + left_parenth);
//		System.out.println("right_p: " + right_parenth);
//		System.out.println("quote: " + quote);
//		System.out.println("left_q: " + left_quote);
//		System.out.println("right_q: " + right_quote);
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("comma", comma);
		map.put("colon", colon);
		map.put("semi_colon", semi_colon);
		map.put("dash", dash);
		map.put("right_parenth", right_parenth);
		map.put("right_quote", right_quote);
		
		
		if(left_parenth > 0){
			map.put("left_parenth", left_parenth);
		}
		if(quote > 0){
			map.put("quote", quote);
		}
		if(left_quote > 0){
			map.put("left_quote", left_quote);
		}

		String smallest = null;
		int num = 100000;
		for(String key: map.keySet()){
			int value = map.get(key);
			if(value < num &&
					value != -1){
				smallest = key;
				num =  value;
			}
		}
		
		
		
		if(smallest == null){
			return null;
		}
		else{
			return getSoftBreak(smallest);
		}




	}

	private SoftBreak getSoftBreak(String smallest) {
		SoftBreak punct = null;
		if(smallest.equals("comma")){
			punct = SoftBreak.COMMA;
		}
		else if(smallest.equals("colon")){
			punct = SoftBreak.COLON;
		}
		else if(smallest.equals("semi_colon")){
			punct = SoftBreak.SEMICOLON;
		}
		else if(smallest.equals("dash")){
			punct = SoftBreak.DASH;
		}
		else if(smallest.equals("right_parenth")){
			punct = SoftBreak.RIGHT_PARENTH;
		}
		else if(smallest.equals("right_quote")){
			punct = SoftBreak.RIGHT_QUOTE;
		}
		else if(smallest.equals("quote")){
			punct = SoftBreak.QUOTE;
		}
		else if(smallest.equals("left_parenth")){
			punct = SoftBreak.LEFT_PARENTH;
		}
		else if(smallest.equals("left_quote")){
			punct = SoftBreak.LEFT_QUOTE;
		}

		return punct;

	}

	public String getSentence(){
        return sentence;
    }

    public ArrayList<Chunk> getChunks(){
        return chunks;
    }

    public void breakDown(int max_words){
        ArrayList<Chunk> temp_chunks = new ArrayList<Chunk>();
        String chunk_text;
        int chunk_length;
        int temp = 0;
        int num_chunks = 0;

        double ratio = 0.0;
        int split_pos = 0;
        int nearest_space = 0;
        int compare = 0;
        for(Chunk chunk: chunks){
            chunk_text = chunk.getText();
            chunk_length = chunk_text.length();
            temp = 0;
            if(chunk.getNumWords() > max_words){
                // determines how many chunks will be made
                if((chunk.getNumWords() / (double)(max_words)) > 2){
                    if(((int)(((double)(chunk.getNumWords()) / max_words) + 0.5) == chunk.getNumWords()/max_words))
                        num_chunks = chunk.getNumWords()/max_words;

                    else
                        num_chunks = (chunk.getNumWords() / max_words) + 1;
                }
                else
                    num_chunks = 2;

                for(int i= 1; i< num_chunks; i++){
                    ratio = ((double)(i)) / num_chunks;
                    split_pos = (int)(ratio * chunk_length);
                    nearest_space = 1000000; // set to arbitrary high number 
                    compare = 1000000;
                    for(int space_pos: chunk.getSpacePositions()){
                        // find nearest sapce
                        if(Math.abs(space_pos - split_pos) < compare){
                            //System.out.println("CHANGED");
                            nearest_space = space_pos;
                            compare = Math.abs(space_pos - split_pos);
                        }
                    }

                    temp_chunks.add(new Chunk(chunk_text.substring(0, nearest_space - temp + 1)));
                    chunk_text = chunk_text.substring(nearest_space - temp + 1);
                    temp = nearest_space;

                }
                temp_chunks.add(new Chunk(chunk_text));
            }
            else{
                temp_chunks.add(chunk);
            }
        }

        chunks.clear();

        for(Chunk chunk: temp_chunks){
            chunks.add(chunk);
        }

    }
    /**
     *  interp: Parses the chunks arraylsit, starting from the smallest.
     */
    public void parseUp(){
        int average_word_length = 4;
        int average_chunk_length = average_word_length * desired_length;
        upperbound = average_chunk_length + average_word_length;
        lowerbound = average_chunk_length - average_word_length;
        int present = 0;
        
        

        
        if(chunks.size() > 1){
            while(!allInBounds(chunks)  && chunks.size() > 1){
                Chunk new_chunk;
                present = chunks.indexOf(getSmallest(chunks));

                System.out.println("printlist");
                for(Chunk ch:chunks){
                	System.out.println(ch.getText());
                }
                
                if(present == 0){
                	
                    // if the smallest is the first, there is no choice then to add to the second
                	System.out.println("test");
                	System.out.println(chunks.get(present).getText());
                	System.out.println(chunks.get(present + 1).getText());
                    new_chunk = new Chunk(chunks.get(present).getText() + " " + chunks.get(present + 1).getText());
                    chunks.remove(present + 1);
                    chunks.remove(present);
                    chunks.add(0, new_chunk);
                }
                else if(present == chunks.size() - 1){
                    // if the smallest is the last, you must add to the second to last
                    new_chunk = new Chunk(chunks.get(present - 1).getText() + " " + chunks.get(present).getText());
                    chunks.remove(present);
                    chunks.remove(present - 1);
                    chunks.add(new_chunk);
                }
                // else it is in the middle
                else{
                    Chunk combo1 = new Chunk(chunks.get(present - 1).getText() + " " + chunks.get(present).getText());
                    Chunk combo2 = new Chunk(chunks.get(present).getText() + " " + chunks.get(present + 1).getText());

                    // if both are inbounds, compare
                    if(combo1.inBounds(lowerbound, upperbound) && combo2.inBounds(lowerbound, upperbound)){
                        // make combo1
                        if(Math.abs(average_chunk_length - combo1.getLength()) < Math.abs(average_chunk_length - combo2.getLength())){
                            chunks.remove(present);
                            chunks.remove(present - 1);
                            chunks.add(present - 1, combo1);
                        }
                        else{
                            chunks.remove(present + 1);
                            chunks.remove(present);
                            chunks.add(present, combo2);
                        }
                    }
                    else if(combo1.inBounds(lowerbound, upperbound)){
                        chunks.remove(present);
                        chunks.remove(present - 1);
                        chunks.add(present - 1, combo1);
                    }
                    else if(combo2.inBounds(lowerbound, upperbound)){
                        chunks.remove(present + 1);
                        chunks.remove(present);
                        chunks.add(present, combo2);
                    } // none of the combos are inbounds, take the smaller one.
                    else{
                        // check if all three combined are applicable
                        Chunk combo3 = new Chunk(chunks.get(present - 1).getText() + " " + chunks.get(present).getText() + " " + chunks.get(present+1).getText());
                        if(combo3.inBounds(lowerbound, upperbound)){

                            chunks.remove(present + 1);
                            chunks.remove(present);
                            chunks.remove(present - 1);
                            chunks.add(present - 1, combo3);
                        } 

                        // check all combos and compare
                        else{
                            //chunks.add(present + 1);

                            if(Math.abs(average_chunk_length - combo3.getLength()) <= Math.abs(average_chunk_length - combo1.getLength()) &&
                            (Math.abs(average_chunk_length - combo3.getLength()) <= Math.abs(average_chunk_length - combo2.getLength()))){
                                chunks.remove(present + 1);
                                chunks.remove(present);
                                chunks.remove(present - 1);
                                chunks.add(present - 1, combo3);  
                            }// combo1 is closer to average
                            else if(Math.abs(average_chunk_length - combo1.getLength()) <= Math.abs(average_chunk_length - combo3.getLength()) &&
                            (Math.abs(average_chunk_length - combo1.getLength()) <= Math.abs(average_chunk_length - combo2.getLength()))){
                                chunks.remove(present);
                                chunks.remove(present - 1);
                                chunks.add(present - 1, combo1);
                            }// combo2 is closer to average
                            else if(Math.abs(average_chunk_length - combo2.getLength()) <= Math.abs(average_chunk_length - combo1.getLength()) &&
                            (Math.abs(average_chunk_length - combo2.getLength()) <= Math.abs(average_chunk_length - combo3.getLength()))){
                                chunks.remove(present + 1);
                                chunks.remove(present);
                                chunks.add(present, combo2);
                            }

                        }
                    }
                }
            }
        }
    }

    /**
     * gets the smallest chunk that is out of bounds
     *  - if all are inbounds, return null
     * 
     * ArrayList<Chunk> -> Chunk, null
     */
    public Chunk getSmallest(ArrayList<Chunk> list){
        Chunk smallest_chunk = null;

        for(Chunk chunk: list){
            // if the chunk is out of bounds and is the smallest
            // if the chunk is larger than the upperbounds, leave it
            if((smallest_chunk == null) || 
            (!(chunk.inBounds(lowerbound, upperbound)) && chunk.getLength() < smallest_chunk.getLength()))
            {
                //System.out.println(chunk.getText());
                smallest_chunk = chunk;
            }
        }

        return smallest_chunk;
    }

    /**
     *  Checks if most of the chunks are inbounds
     *   - 80%
     */
    public boolean allInBounds(ArrayList<Chunk> list){
        for(Chunk chunk: list){
            if(!chunk.inBounds(lowerbound, upperbound) && chunk.getLength() < upperbound){
                return false;
            }
        }
        return true;
    }
    
    public void printChunks(){
    	for(Chunk chunk: chunks){
    		System.out.println(chunk.getText());
    	}
    }


	public int getLength() {
		// TODO Auto-generated method stub
		return getSentence().length();
	}

}