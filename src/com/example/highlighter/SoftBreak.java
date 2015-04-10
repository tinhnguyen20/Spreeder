package com.example.highlighter;

public enum SoftBreak {

	COMMA(','),
	COLON(':'),
	SEMICOLON(';'),
	DASH((char)8212), // 8212 is ascii value of a big dash
	LEFT_PARENTH('('),
	RIGHT_PARENTH(')'),
	QUOTE('"'),
	LEFT_QUOTE((char)8220),
	RIGHT_QUOTE((char)8221);


	private char character;


	SoftBreak(char ch){
		character = ch;

	}

	public char character(){
		return character;
	}

}