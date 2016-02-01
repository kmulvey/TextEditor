package spelling;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * An trie data structure that implements the Dictionary and the AutoComplete
 * ADT
 * 
 * @author You
 *
 */
public class AutoCompleteDictionaryTrie implements Dictionary, AutoComplete {

	private TrieNode root;
	private int size;

	public AutoCompleteDictionaryTrie() {
		root = new TrieNode();
		size = 0;
	}

	/**
	 * Insert a word into the trie. For the basic part of the assignment (part 2),
	 * you should ignore the word's case. That is, you should convert the string
	 * to all lower case as you insert it.
	 */
	public boolean addWord(String word) {
		word = word.toLowerCase();
		TrieNode node = root;
		for (int i = 0; i < word.length(); i++) {
			if (node.getChild(word.charAt(i)) == null) {
				node = node.insert(word.charAt(i));
				if (word.length() == i + 1) {
					node.setEndsWord(true);
					size++;
					return true;
				}
			} else {
				node = node.getChild(word.charAt(i));
				if (word.length() == i + 1 && node.endsWord() == false) {
					node.setEndsWord(true);
					size++;
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * Return the number of words in the dictionary. This is NOT necessarily the
	 * same as the number of TrieNodes in the trie.
	 */
	public int size() {
		return size;
	}

	/** Returns whether the string is a word in the trie */
	@Override
	public boolean isWord(String s) {
		s = s.toLowerCase();
		TrieNode node = root;
		for (int i = 0; i < s.length(); i++) {
			if (node.getChild(s.charAt(i)) != null) {
				node = node.getChild(s.charAt(i));
				if (node.endsWord() == true && s.length() == i + 1) {
					return true;
				}
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * * Returns up to the n "best" predictions, including the word itself, in
	 * terms of length If this string is not in the trie, it returns null.
	 * 
	 * @param text
	 *          The text to use at the word stem
	 * @param n
	 *          The maximum number of predictions desired.
	 * @return A list containing the up to n best predictions
	 */
	// @Override
	// public List<String> predictCompletions(String prefix, int numCompletions) {
	// TODO: Implement this method
	// This method should implement the following algorithm:
	// 1. Find the stem in the trie. If the stem does not appear in the trie,
	// return an
	// empty list
	@Override
	public List<String> predictCompletions(String prefix, int numCompletions) {
		TrieNode node = root;
		for (char c : prefix.toCharArray()) {
			if (!node.getValidNextCharacters().contains(c)) {
				return new LinkedList<String>();
			}
			node = node.getChild(c);
		}
		List<String> findings = digDeep(node, numCompletions);
		List<String> sorted = mergeSort(findings, 0, findings.size() - 1);
		List<String> results = new ArrayList<String>();

		for (int i = 0; i < Math.min(numCompletions, sorted.size()); i++) {
			results.add(sorted.get(i));
		}
		return results;
	}

	private List<String> digDeep(TrieNode node, int max) {
		List<String> results = new LinkedList<String>();

		if (node.endsWord()) {
			results.add(node.getText());
		}
		Set<Character> next = node.getValidNextCharacters();
		for (Character c : next) {
			TrieNode child = node.getChild(c);
			List<String> prefixes = digDeep(child, max);
			results.addAll(prefixes);
		}
		return results;
	}

	private List<String> mergeSort(List<String> arr, int lo, int hi) {
		int low = lo, high = hi;
		if (low >= high) return null;

		int middle = (low + high) / 2;
		mergeSort(arr, low, middle);
		mergeSort(arr, middle + 1, high);

		int endLow = middle, startHigh = middle + 1;

		while ((low <= endLow) && (startHigh <= high)) {
			if (arr.get(low).length() < arr.get(startHigh).length()) {
				low++;
			} else {
				String temp = arr.get(startHigh);
				for (int i = startHigh - 1; i >= low; i--) {
					arr.set(i + 1, arr.get(i));
				}
				arr.set(low, temp);
				low++;
				endLow++;
				startHigh++;
			}
		}
		return arr;
	}

	// For debugging
	public void printTree() {
		printNode(root);
	}

	/** Do a pre-order traversal from this node down */
	public void printNode(TrieNode curr) {
		if (curr == null) return;

		System.out.println(curr.getText());

		TrieNode next = null;
		for (Character c : curr.getValidNextCharacters()) {
			next = curr.getChild(c);
			printNode(next);
		}
	}

}