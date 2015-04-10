package com.example.highlighter;

import android.text.SpannableString;
import android.text.TextUtils;

public class ColoredText {
	private int time;
	private CharSequence string;
	private int numWords;
	
	public ColoredText(SpannableString pStr, SpannableString str, int speed, int numWords){
		string =  TextUtils.concat(pStr, str);
		this.numWords = numWords;
		time =  numWords * speed;
	}
	

	public CharSequence getSpannableString(){
		return string;
	}
	
	public int getNumWords(){
		return numWords;
	}
	
	public int getTime(){
		return time;
	}
}
