package org.berlin.batch.util;

import org.berlin.batch.bean.BotDataUser;

class SGraphNode {

	public enum State {
		Unvisited, Visited, Visiting;
	}

	private SGraphNode adjacent[];
	public int adjacentCount;
	public State state;

	private BotDataUser vertexData;

	public SGraphNode(final BotDataUser vertex, final int adjacentLength) {
		this.vertexData = vertex;
		adjacentCount = 0;
		adjacent = new SGraphNode[adjacentLength];
	}

	public void addAdjacent(SGraphNode x) {
		if (adjacentCount < 30) {
			this.adjacent[adjacentCount] = x;
			adjacentCount++;
		} else {
			System.out.print("No more adjacent can be added");
		}
	}
	public SGraphNode[] getAdjacent() {
		return adjacent;
	}
	public BotDataUser getVertex() {
		return vertexData;
	}

} // End of the class //
