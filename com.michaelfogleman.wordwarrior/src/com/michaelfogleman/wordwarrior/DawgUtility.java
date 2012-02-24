package com.michaelfogleman.wordwarrior;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.michaelfogleman.wordwarrior.engine.Dictionary;
import com.michaelfogleman.wordwarrior.engine.Node;
import com.michaelfogleman.wordwarrior.model.DictionaryType;

public class DawgUtility {
	
	public static void main(String[] args) {
		Dictionary dictionary = Dictionary.getInstance(DictionaryType.TWL06);
		Node root = dictionary.getRoot();
		System.out.println(root.getSubtreeValue());
		System.out.println(root.getSubtreeSize());
		System.out.println(root.countUniqueSubtreeSize());
		
		List<String> words1 = root.getAllWords();
		System.out.println(words1.size());
		
		for (int level = 0; level < 15; level++) {
			condenseLevel(root, level);
		}
		
		List<String> words2 = root.getAllWords();
		System.out.println(words2.size());
		
		System.out.println(words1.equals(words2));
		root.save("d:/dawg.dat");
	}
	
	private static void condenseLevel(Node root, int level) {
		System.out.println("Condensing Level " + level);
		List<Node> list = root.getLevel(level);
		System.out.println("Level Size: " + list.size());
		Map<Node, Node> map = new HashMap<Node, Node>();
		for (Node node : list) {
			map.put(node, node);
		}
		System.out.println("Unique Level Nodes: " + map.size());
		for (Node node : list) {
			node.replaceWith(map.get(node));
		}
		System.out.println(root.getSubtreeValue());
		System.out.println(root.getSubtreeSize());
		System.out.println(root.countUniqueSubtreeSize());
		System.out.println("---");
	}

}
