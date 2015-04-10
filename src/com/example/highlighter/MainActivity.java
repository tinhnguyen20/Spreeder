package com.example.highlighter;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

public class MainActivity extends Activity {

	private static double seconds = 60.0;

	private ArrayList<ColoredText> highlights;
	private ArrayList<Chunk> chunks;
	private Set<String> conjunctions;
	private Set<String> prepositions;
	private Set<String> honorifics;
	private String story;
	private TextView textView;
	private Reader reader;
	private long currentTime = 0L;
	private int count = 0; // used to iterate through list of SpannableStrings 
	private int WPM = 300; // default speed
	private int speed;
	private boolean running = false;
	private Timer timer;
	private static final String TAG = "UpdateUI";
	private Handler mHandler;

	
	private Runnable mUpdate = new Runnable() {
		   public void run() {
		       textView.setText(highlights.get(count).getSpannableString());
		       mHandler.postDelayed(this, highlights.get(count).getTime());
		       System.out.println("time for chunk" + count + ": " + highlights.get(count).getTime());
		       count++;

		    }
		};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// instantiate variables
		story = getString(R.string.story);
		speed = Math.round((float)(1/(WPM/seconds) * 1000)); //time for each word in milliseconds
		highlights = new ArrayList<ColoredText>();
		
		// instantiate word lists (conj, prep, hon)
		conjunctions = new HashSet<String>();
		prepositions = new HashSet<String>();
		honorifics = new HashSet<String>();
		generateWordLists();
		
		reader = new Reader(4, story, conjunctions, honorifics, prepositions);
		chunks = reader.getChunks();
		generateViews();
		
		// starting the speed reader
		textView = (TextView) findViewById(R.id.story);
		
		
        
		 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



	private void generateWordLists(){
		Log.println(1,"Test", "before");
		System.out.println(getString(R.string.englishHonorifics));
		String[] englishHon = getString(R.string.englishHonorifics).split(" ");
		for(String s:englishHon){
			System.out.println(s);
			honorifics.add(s);
		}
		String[] preps = getString(R.string.prepositions).split(" ");
		String[] conjs = getString(R.string.conjunctions).split(" ");

		for(String honorific: englishHon){
			honorifics.add(honorific);
		}
		for(String prep: preps){
			prepositions.add(prep);
		}
		for(String conj: conjs){
			conjunctions.add(conj);
		}


	}


	public void generateViews(){
		String story1 = story;
		String parsedStory = "";
		String text;
		int highlightStart;

		// generates an arraylist(highlights) of Spannable Strings
		for(Chunk chunk1: reader.getChunks()){
			text = chunk1.getText();
			//highlightStart = story1.indexOf(text);
			highlightStart = 0;
			
			//display text
			SpannableString pStr = new SpannableString(parsedStory);
			SpannableString str = new SpannableString(story1);
			str.setSpan(new BackgroundColorSpan(Color.LTGRAY), highlightStart, highlightStart + text.length(), 0);
			ColoredText cText = new ColoredText(pStr, str, speed, chunk1.getNumWords());
			highlights.add(cText);
			parsedStory += story1.substring(0,  text.length());
			story1 = story1.substring(text.length());
		}
	}
	
	public void startSpeedReader(View V){
		count = 0;
        mHandler = new Handler();
        mHandler.post(mUpdate);
	}




}
