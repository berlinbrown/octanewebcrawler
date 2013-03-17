package org.berlin.batch.json;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class JSONParser {
	
	public enum Type {	
		START_BLOCK,
		END_BLOCK,		
		QUOTE,
		KEYVALUE_DELIM,
		KEY,
		COMMA_BREAK,
		ARRAY_START,
		ARRAY_END,		
		NONE
	}
	
	private final Queue<Block> blocks = new LinkedList<Block>();
	
	public void parse(final String document) {
		// Lexical analysis, build the proper tokens //
		final char data [] = document.toCharArray();
		int pos = 1;
		
		final Stack<Token> expectedTokens = new Stack<Token>();		
		Token lastExpectedToken = null;
		StringBuffer currentBuffer = new StringBuffer();
		int localStackSize = 0;
		
		final Stack<String> stringStack = new Stack<String>();
		
		for (final char c : data) {			
			Type t = Type.NONE;
			if (c == '{') {
				t = Type.START_BLOCK;
			} else if (c == '}') {
				t = Type.END_BLOCK;
			} else if (c == ',') {
				t = Type.COMMA_BREAK;
			} else if (c == '\"') {
				t = Type.QUOTE;
			} else if (c == ':') {
				t = Type.KEYVALUE_DELIM;
			} else if (c == '[') {
				t = Type.ARRAY_START;
			} else if (c == ']') {
				t = Type.ARRAY_END;
			} // End of the if //
			
			// Process the expected token character
			if (expectedTokens.size() > 0) {				
				final Token expected = expectedTokens.peek();								
				if (Type.QUOTE.equals(expected.type) && Type.QUOTE.equals(t)) {					
					stringStack.push(currentBuffer.toString());					
					currentBuffer = new StringBuffer();					
					expectedTokens.pop();
					lastExpectedToken = null;										
				} else if (Type.END_BLOCK.equals(expected.type) && Type.END_BLOCK.equals(t)) {					
					expectedTokens.pop();
					lastExpectedToken = null;
				}
			} // End of the if - else //														
			
			// Process the current character //
			if (Type.QUOTE.equals(t)) {								
				// In this case, the expected token is a quote //				
				if (localStackSize == 0) {					
					final Token expected = new Token(Type.QUOTE);
					lastExpectedToken = expected;
					expectedTokens.push(expected);
					localStackSize++;					
				} else if (localStackSize == 1) {
					localStackSize = 0;
				}		
			} else if (Type.START_BLOCK.equals(t)) {
				final Token expected = new Token(Type.END_BLOCK);
				lastExpectedToken = expected;
				expectedTokens.push(expected);
			} else if (Type.COMMA_BREAK.equals(t)) {
				// Build a key value, extract last two strings
				if (stringStack.size() >= 2) {
					// Pop last two //
					final String val = stringStack.pop();
					final String key = stringStack.pop();
					System.out.println("key=" + key);
				}
			} else {
				if (lastExpectedToken != null) {					
					if (Type.QUOTE.equals(lastExpectedToken.type)) {
						/// Build a character buffer //
						currentBuffer.append(c);
					}
				} // End of if last expected //
			} // End of processing current token //
			pos++;
		} // End of the for //
	} // End of the method //
 
	public class KeyValue {
		private String key;
		private String value;
		public KeyValue(final String k, final String v) {
			key = k;
			value = v;
		}
	}

	
	public class Block {
		// A JSON block consists of a key/value pair set
		// with a string as a key and a value of 'string' or 'block'
		private Map<String, Object> data = new HashMap<String, Object>();	
		private String lastKey = "";
		public String toString() {
			final StringBuffer b = new StringBuffer();
			b.append("{key=");
			b.append(lastKey);
			b.append(",val=");
			for (final String key : data.keySet()) {
				b.append("/1key=");
				b.append(key);
				b.append("/");
				b.append(data.get(key));
			} // End of the for ///
			b.append("}");
			return b.toString();
		}
	} // End of the class //
	
	public class Token {
		private Type type = Type.NONE;
		private String data;
		private int pos;		
		public Token(final Type t, final String c, final int pos) {
			this.type = t;
			this.data = c;
			this.pos = pos;
		} // End of the constructor //
		public Token(final Type t) {
			this.type = t;			
		} // End of the constructor //
		public String toString() {
			return "[T=" + this.type + " data='" + data + "']";
		}
	} // End of the class //
	
	public void report() {
		for (final Block block : this.blocks) {
			 if (block.data.size() > 0) {
				 System.out.println("Block has data:");
				 for (final String key : block.data.keySet()) {
					 final Object value = block.data.get(key);
					 System.out.println("! key=" + key + ", value=" + value);
				 }
			 } else {
				 System.out.println("Block does not have data");
			 }
		} // End of the for //
	} // End of the method //	
	
} // End of the class //
