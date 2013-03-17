package org.berlin.data.test.misc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.berlin.batch.json.JSONParser;
import org.berlin.batch.net.QueryRequest;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class TestParseJSON2 {

	public static void test() {
		
		final StringBuffer document = new StringBuffer(128);
		FileInputStream stream = null;
		try {
	        stream = new FileInputStream("./tests/json/test_json_twit.txt");
	        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));              
	        String line = "";	        
	        while ((line = reader.readLine()) != null) {
	        	document.append(line);
	        } // End of the while //
	        
	        final ObjectMapper mapper = new ObjectMapper();	        
	        final JsonNode rootNode = mapper.readValue(document.toString(), JsonNode.class);
	        final JsonNode nextPage = rootNode.get("next_page");
	        System.out.println(String.valueOf(nextPage));
	        /// Make URL connection //
	        
	        final QueryRequest req = new QueryRequest();
	        req.connect("obama");
		} catch(final Exception e) {	
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				} // End of the try - catch //
			}
		} // End of the try - catch //
		
		final JSONParser p = new JSONParser();		
	} // End of the method //
	
	public static void main(final String [] args) {		
		System.out.println("Running Test");
		test();
		System.out.println("Done Running Test");
	} // End of the method //
	
} // End of the class //
