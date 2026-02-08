package org.berlin.batch.util;

import java.util.List;

public class GenericTree {

	private final UserLinkedListNode root;

	public GenericTree(final UserLinkedListNode node) {
		root = node;
	}

	public UserLinkedListNode root() {
		return this.root;
	}

	public void print(final StringBuffer buf, final int levels) {
		if (levels >= 20) {
			return;
		}
		if (this.root != null) {
			// Print the current level
			final int n2 = (2 * levels) + 1;
			final String sps2 = String.format("%1$" + n2 + "s", " ");
			buf.append(sps2);
			buf.append("(");
			this.root.print(buf);
			// Print nodes at this level
			final List<UserLinkedListNode> curLevelList = this.root.list();
			for (final UserLinkedListNode n : curLevelList) {
				if (n.getUserData() != null) {
					// Add line sep + spaces + current
					buf.append(System.getProperty("line.separator") + sps2 + " ");
					buf.append("(");
					buf.append("/" + n.getUserData() + "==>/");
					// recurse for the nodes in list
					if (n.tree() != null) {
						n.tree().print(buf, levels + 1);
					}
					buf.append(")");
				}
			} // End of for //

			if (this.root.tree() != null) {
				// Append new line, some spacing
				final int n = (3 * levels) + 1;
				final String sps = String.format("%1$" + n + "s", " ");
				buf.append(System.getProperty("line.separator") + sps);
				this.root.tree().print(buf, levels + 1);
			}
			buf.append(")");
		}
	}
	public String report() {
		final StringBuffer buf = new StringBuffer();
		buf.append("{LinkedListNodeTree :: ");
		this.print(buf, 0);
		return buf.toString();
	}

} // End of the class //
