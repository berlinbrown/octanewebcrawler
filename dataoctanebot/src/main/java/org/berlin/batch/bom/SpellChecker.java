package org.berlin.batch.bom;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpellChecker {

	private final HashMap<String, Integer> nWords = new HashMap<String, Integer>();

	public SpellChecker(final String file) throws IOException {
		
		final BufferedReader in = new BufferedReader(new FileReader(file));
		// Split all the words //
		final Pattern p = Pattern.compile("\\w+");
		for (String word = ""; word != null; word = in.readLine()) {
			final Matcher m = p.matcher(word.toLowerCase());
			while (m.find()) {
				word = m.group();
				// If it already has the word then increment the counter //
				final int count = nWords.containsKey(word) ? nWords.get(word) + 1 : 1;
				nWords.put(word, count);
			}
		}
		in.close();
	} // End of the method //

	private final ArrayList<String> edits(final String word) {
		
		final ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < word.length(); ++i) {
			result.add(word.substring(0, i) + word.substring(i + 1));
		}
		for (int i = 0; i < word.length() - 1; ++i) {
			result.add(word.substring(0, i) + word.substring(i + 1, i + 2) + word.substring(i, i + 1) + word.substring(i + 2));
		}
		for (int i = 0; i < word.length(); ++i) {
			for (char c = 'a'; c <= 'z'; ++c) {
				result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i + 1));
			}
		}
		for (int i = 0; i <= word.length(); ++i) {
			for (char c = 'a'; c <= 'z'; ++c) {
				result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));
			}
		} // End of the for //
		return result;
	}

	public final String correct(final String word) {
		// Fail fast, return the word if it is found in our database //
		if (nWords.containsKey(word)) {
			return word;
		}
		final ArrayList<String> list = edits(word);
		final HashMap<Integer, String> candidates = new HashMap<Integer, String>();
		for (String s : list) {
			if (nWords.containsKey(s)) {
				// Bi-directional map the 'count' to the word
				candidates.put(nWords.get(s), s);
			}
		}
		if (candidates.size() > 0) {
			return candidates.get(Collections.max(candidates.keySet()));
		}
		// Loop through the edits
		for (String s : list)
			for (String w : edits(s)) {
				if (nWords.containsKey(w)) {
					candidates.put(nWords.get(w), w);
				}
			} // End of the for //
		
		return candidates.size() > 0 
				? candidates.get(Collections.max(candidates.keySet())) : word;
	}

	public static void main(String args[]) throws IOException {
		if (args.length > 0) {
			System.out.println((new SpellChecker("big.txt")).correct(args[0]));
		}
	}
} // End of the class //
