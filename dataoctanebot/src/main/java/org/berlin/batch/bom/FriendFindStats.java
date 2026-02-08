package org.berlin.batch.bom;

import java.util.concurrent.atomic.AtomicInteger;

public class FriendFindStats {

	private AtomicInteger netRequests = new AtomicInteger();

	public AtomicInteger net() {
		return netRequests;
	}

} // End of the class //
