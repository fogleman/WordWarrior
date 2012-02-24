package com.michaelfogleman.wordwarrior.engine;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.michaelfogleman.wordwarrior.Util;

public class Node implements Comparable<Node> {
	
	private Node parent;
	private Node[] children;
	private char letter;
	private int value;
	
	public Node() {
		this(null, ' ');
	}
	
	public Node(Node parent, char letter) {
		this.parent = parent;
		this.letter = letter;
	}
	
	// BEGIN TEST CODE
	
	public static void convertToDawg(Node root) {
		System.out.println("Converting trie to dawg...");
		for (int level = 0; level < 15; level++) {
			condenseLevel(root, level);
		}
//		root.eraseParentData();
		System.out.println(root.countUniqueSubtreeSize() + " Nodes");
		System.out.println(root.getSubtreeValue() + " Words");
	}
	
	private static void condenseLevel(Node root, int level) {
		List<Node> list = root.getLevel(level);
		Map<Node, Node> map = new HashMap<Node, Node>();
		for (Node node : list) {
			map.put(node, node);
		}
		for (Node node : list) {
			node.replaceWith(map.get(node));
		}
	}
	
//	private void eraseParentData() {
//		parent = null;
//		if (children != null) {
//			for (Node child : children) {
//				child.eraseParentData();
//			}
//		}
//	}
	
	public void save(String fileName) {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(fileName));
			serialize(out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Util.close(out);
		}
	}
	
	public void serialize(ObjectOutputStream out) throws IOException {
		out.writeChar(letter);
		out.writeByte((byte)value);
		if (children == null) {
			out.writeByte(0);
		}
		else {
			out.writeByte((byte)children.length);
			for (Node child : children) {
				child.serialize(out);
			}
		}
	}
	
	public void deserialize(ObjectInputStream in) throws IOException {
		letter = in.readChar();
		value = in.readByte();
		children = null;
		int count = in.readByte();
		if (count > 0) {
			children = new Node[count];
			for (int i = 0; i < count; i++) {
				Node child = new Node(this, ' ');
				child.deserialize(in);
				children[i] = child;
			}
			Arrays.sort(children);
		}
	}
	
	private int hashCode = 0;
	
	public int hashCode() {
		if (hashCode != 0) return hashCode;
		StringBuffer b = new StringBuffer();
		buildHashString(b);
		int result = b.toString().hashCode();
		if (result == 0) result = 17;
		hashCode = result;
		return result;
	}
	
	private void buildHashString(StringBuffer b) {
		b.append(letter);
		b.append(value);
		if (children != null) {
			for (Node child : children) {
				child.buildHashString(b);
			}
		}
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof Node)) return false;
		Node node = (Node)object;
		if (letter != node.letter) return false;
		if (isEndState() != node.isEndState()) return false;
		if (isLeaf() != node.isLeaf()) return false;
		if (this.isDescendantOf(node)) return false;
		if (node.isDescendantOf(this)) return false;
		if (!isLeaf()) {
			if (children.length != node.children.length) return false;
			for (int i = 0; i < children.length; i++) {
				Node child1 = children[i];
				Node child2 = node.children[i];
				if (!child1.equals(child2)) return false;
			}
		}
		return true;
	}
	
	private int height = -1;
	
	public int getHeight() {
		if (height >= 0) return height;
		if (children == null) return 0;
		int result = Integer.MIN_VALUE;
		for (Node child : children) {
			int n = child.getHeight() + 1;
			if (n > result) {
				result = n;
			}
		}
		height = result;
		return result;
	}
	
	public void replaceChild(Node currentChild, Node newChild) {
		if (currentChild == newChild) return;
		int length = children.length;
		for (int i = 0; i < length; i++) {
			Node child = children[i];
			if (child == currentChild) {
				children[i] = newChild;
				break;
			}
		}
	}
	
	public void replaceWith(Node node) {
		if (parent == null) return;
		parent.replaceChild(this, node);
	}
	
	public List<Node> getLevel(int level) {
		List<Node> result = new ArrayList<Node>();
		getLevel(result, level);
		return result;
	}
	
	private void getLevel(List<Node> results, int level) {
		if (getHeight() == level) {
			results.add(this);
		}
		if (children != null) {
			for (Node child : children) {
				child.getLevel(results, level);
			}
		}
	}
	
	public int getSubtreeSize() {
		if (children == null) {
			return 1;
		}
		else {
			int result = 1;
			for (Node child : children) {
				result += child.getSubtreeSize();
			}
			return result;
		}
	}
	
	public int countUniqueSubtreeSize() {
		Set<Object> set = new HashSet<Object>();
		countUniqueSubtreeSize(set);
		return set.size();
	}
	
	private void countUniqueSubtreeSize(Set<Object> set) {
		set.add(getIdentityHashObject());
		if (children != null) {
			for (Node child : children) {
				child.countUniqueSubtreeSize(set);
			}
		}
	}
	
	private Object hashObject;
	public Object getIdentityHashObject() {
		if (hashObject == null) {
			hashObject = new Object() {
				public int hashCode() {
					return System.identityHashCode(Node.this);
				}
			};
		}
		return hashObject;
	}
	
	public List<String> getAllWords() {
		List<String> list = new ArrayList<String>();
		getAllWords(list, new StringBuffer());
		return list;
	}
	
	private void getAllWords(List<String> list, StringBuffer word) {
		word.append(letter);
		if (isEndState()) {
			list.add(word.toString());
		}
		if (children != null) {
			for (Node child : children) {
				child.getAllWords(list, word);
			}
		}
		word.setLength(word.length()-1);
	}
	
	// END TEST CODE
	
	public Node getParent() {
		return parent;
	}
	
	public Node[] getChildren() {
		return children;
	}
	
	public char getLetter() {
		return letter;
	}
	
	public int getValue() {
		return value;
	}
	
	public boolean isLeaf() {
		return children == null;
	}
	
	public boolean isEndState() {
		return value > 0;
	}
	
	public int getDepth() {
		if (parent == null) return 0;
		return parent.getDepth() + 1;
	}
	
	public int getSubtreeValue() {
		int result = value;
		if (children != null) {
			for (Node child : children) {
				result += child.getSubtreeValue();
			}
		}
		return result;
	}
	
	public Node getChild(char letter) {
		for (Node child : children) {
			if (child.letter == letter) {
				return child;
			}
		}
		return null;
	}
	
	public Node getDescendant(String path) {
		return getDescendant(path, 0);
	}
	
	private Node getDescendant(String path, int index) {
		int length = path.length();
		if (index >= length) {
			return this;
		}
		char letter = path.charAt(index);
		if (children != null) {
			for (Node child : children) {
				if (child.letter == letter) {
					return child.getDescendant(path, index+1);
				}
				else if (child.letter > letter) {
					break;
				}
			}
		}
		return null;
	}
	
	public boolean isDescendantOf(Node node) {
		if (parent == null) return false;
		if (parent == node) return true;
		return parent.isDescendantOf(node);
	}
	
	public void addString(String string) {
		addString(string.toLowerCase(), 0);
	}
	
	private void addString(String string, int index) {
		int length = string.length();
		if (index >= length) {
			value++;
			return;
		}
		char letter = string.charAt(index);
		if (children == null) {
			Node child = new Node(this, letter);
			children = new Node[1];
			children[0] = child;
			child.addString(string, index+1);
		}
		else {
			for (Node child : children) {
				if (child.letter == letter) {
					child.addString(string, index+1);
					return;
				}
				else if (child.letter > letter) {
					break;
				}
			}
			Node[] source = children;
			Node[] dest = new Node[children.length+1];
			System.arraycopy(source, 0, dest, 0, source.length);
			children = dest;
			Node child = new Node(this, letter);
			children[children.length-1] = child;
			Arrays.sort(children);
			child.addString(string, index+1);
		}
	}
	
	public static boolean containsString(Node node, String string) {
		string = string.toLowerCase();
		int index = 0;
		int length = string.length();
		while (index < length) {
			if (node.children == null) {
				return false;
			}
			char letter = string.charAt(index);
			boolean match = false;
			for (Node child : node.children) {
				if (child.letter == letter) {
					node = child;
					match = true;
					break;
				}
				else if (child.letter > letter) {
					return false;
				}
			}
			if (!match) {
				return false;
			}
			index++;
		}
		return node.isEndState();
	}
	
	public boolean containsString(String string) {
		return containsString(string.toLowerCase(), 0);
	}
	
	private boolean containsString(String string, int index) {
		int length = string.length();
		if (index >= length) {
			return isEndState();
		}
		if (children == null) {
			return false;
		}
		char letter = string.charAt(index);
		for (Node child : children) {
			if (child.letter == letter) {
				return child.containsString(string, index+1);
			}
			else if (child.letter > letter) {
				return false;
			}
		}
		return false;
	}
	
	public List<String> getWords(int[] tiles, int wildcards, String state, boolean[] neighbored, boolean[][] letters, int index) {
		List<String> list = new ArrayList<String>();
		if (index == 0 || state.charAt(index-1) == ' ') {
			getWords(list, new StringBuffer(32), tiles, wildcards, state, neighbored, letters, index, 0, false);
		}
		return list;
	}
	
	private void getWords(List<String> results, StringBuffer word, int[] tiles, int wildcards, String state, boolean[] neighbored, boolean[][] letters, int index, int tilesUsed, boolean adjacent) {
		int length = state.length();
		if (tilesUsed > 0 && isEndState() && adjacent) {
			if (index >= length || state.charAt(index) == ' ') {
				results.add(word.toString());
			}
		}
		if (children == null) {
			return;
		}
		if (index >= length) {
			return;
		}
		char letter = state.charAt(index);
		boolean neighbor = neighbored[index];
		if (letter == ' ') {
			for (Node child : children) {
				int n = child.letter - 'a';
				if (!letters[index][n]) continue;
				if (tiles[n] > 0) {
					tiles[n]--;
					word.append(child.letter);
					child.getWords(results, word, tiles, wildcards, state, neighbored, letters, index+1, tilesUsed+1, adjacent || neighbor);
					word.setLength(word.length()-1);
					tiles[n]++;
				}
				if (wildcards > 0) {
					char upper = Character.toUpperCase(child.letter);
					wildcards--;
					word.append(upper);
					child.getWords(results, word, tiles, wildcards, state, neighbored, letters, index+1, tilesUsed+1, adjacent || neighbor);
					word.setLength(word.length()-1);
					wildcards++;
				}
			}
		}
		else {
			char lower = Character.toLowerCase(letter);
			for (Node child : children) {
				if (child.letter == lower) {
					word.append(letter);
					child.getWords(results, word, tiles, wildcards, state, neighbored, letters, index+1, tilesUsed, adjacent);
					word.setLength(word.length()-1);
				}
				else if (child.letter > lower) {
					break;
				}
			}
		}
	}
	
	public int compareTo(Node node) {
		return this.letter - node.letter;
	}

}
