package org.berlin.batch.util;

public class SGraph {
	private SGraphNode vertices[];
	public int count;

	public SGraph() {
		vertices = new SGraphNode[6];
		count = 0;
	}

	public void addNode(final SGraphNode x) {
		if (count < 30) {
			vertices[count] = x;
			count++;
		} else {
			System.out.print("Graph full");
		}
	}

	public SGraphNode[] getNodes() {
		return vertices;
	}

} // End of the class //
