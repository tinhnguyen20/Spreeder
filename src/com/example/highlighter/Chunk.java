package com.example.highlighter;

import java.util.ArrayList;
/**
 * 
 * A chunk can consist of 1-4 words
 */
public class Chunk
{
    private String text;
    private String[] words;
    private ArrayList<Integer> space_positions;
    
    
    /**
     * Creates a chunk of words 
     */
    public Chunk(String text){
        this.text = text.trim();
        words = new String[this.text.split(" ").length];
        words = this.text.split(" ");
        space_positions = new ArrayList<Integer>();        
        
        String temp_text = this.text;
        int add = 0;
        int space = 0;
        while(temp_text.indexOf(" ") > -1){
            space = temp_text.indexOf(" ");
            temp_text = temp_text.substring(space + 1);
            space_positions.add(space + add);
            add += space + 1;
        }
    }
    
    public String getText(){
        return text;
    }
    
    public String[] getWords(){
        return words;
    }
    
    public boolean isEmpty(){
        if(text.length() == 0)
            return true;
        else
            return false;
    }
    
    public int getNumWords(){
        return words.length;
    }
    
    public ArrayList<Integer> getSpacePositions(){
        return space_positions;
    }
    
    public int getLength(){
        return text.length();
    }
    
    public boolean inBounds(int lowerbound, int upperbound){
        if(lowerbound <= text.length() && text.length() <= upperbound){
            return true;
        }
        return false;
    }

    
}