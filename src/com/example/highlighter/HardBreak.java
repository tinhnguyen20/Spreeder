package com.example.highlighter;

public enum HardBreak {
	PERIOD('.'),
	QUESTION_MARK('?'),
	EXCLAMATION('!'),
	SEMICOLON(';'),
	RIGHT_PARENTH(')'),
	RIGHT_QUOTE((char)8221);
	
	private char character;
	
	HardBreak(char ch){
		character = ch;
	}
	
	public char character(){
		return character;
	}
}
