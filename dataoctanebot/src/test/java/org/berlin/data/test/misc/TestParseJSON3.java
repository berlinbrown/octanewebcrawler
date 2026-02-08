package org.berlin.data.test.misc;

import java.io.FileInputStream;
import java.io.IOException;

import org.berlin.batch.json.JSONParser;
import org.berlin.batch.net.FriendsRequest;
import org.codehaus.jackson.JsonNode;

public class TestParseJSON3 {

	public static void test() {

		final StringBuffer document = new StringBuffer(128);
		FileInputStream stream = null;
		try {
			final FriendsRequest req = new FriendsRequest();
			final JsonNode n = req.connect("obama");
		} catch (final Exception e) {
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

	public static void main(final String[] args) {
		System.out.println("Running Test");
		test();
		System.out.println("Done Running Test");
	} // End of the method //

} // End of the class //
