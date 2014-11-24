package org.crl.utilities;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.crl.imagedata.Color;
/**
 * Useful tools when manipulating hash maps
 * */
public class MapUtilities {
	/**
	 * Merges two maps with the colors and their occurence count where a color
	 * contained 3 times in map A and 5 times in map B will appear with count 8
	 * in the result
	 * */
	public static void mergeMaps(Map<Color, Integer> main,
			Map<Color, Integer> secondary) {
		Iterator it = secondary.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Color colorInSecondary = (Color) pair.getKey();
			// the color is contained we sum
			if (main.containsKey(colorInSecondary)) {
				Integer value = main.get(colorInSecondary)
						+ secondary.get(colorInSecondary);
				main.remove(colorInSecondary);
				main.put(colorInSecondary, value);
			} else {
				main.put(colorInSecondary, secondary.get(colorInSecondary));
			}
		}
	}

	/**
	 * Sorts the map ascending by key
	 * */
	public static <K extends Comparable, V extends Comparable> Map<K, V> sortByKeys(
			Map<K, V> map) {
		List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				if (o1.getKey().equals(o2.getKey())) {
					return o1.getValue().compareTo(o2.getValue());
				}
				return o1.getKey().compareTo(o2.getKey());
			}
		});

		Map<K, V> sortedMap = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	/**
	 * Sorts descending by value primary, ascending by key secondary
	 * */
	public static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(
			Map<K, V> map) {
		List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				if (o2.getValue().equals(o1.getValue())) {
					return o1.getKey().compareTo(o2.getKey());
				}
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		Map<K, V> sortedMap = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
/**
 * Extracts the set of scrapers that appear with longest length 
 * The key is the position of the scraper and the value is its length
 * Longest scrapers are marking the positions of the values on a scale
 * */
	public static Map<Integer, Integer> getLongestScrapers(
			Map<Integer, Integer> scrapers) {
		Map<Integer, Integer> longestScrapers = new HashMap<Integer, Integer>();

		// the longest scrapers are at the front
		int maxWidth = 0;
		for (Integer coord : scrapers.keySet()) {
			int width = scrapers.get(coord);

			if (maxWidth == 0) {
				maxWidth = width;
			}
			if (width < maxWidth - 1) {
				break;
			}
			if (!(longestScrapers.containsKey(coord - 1) || longestScrapers
					.containsKey(coord + 1))) {
				longestScrapers.put(coord, width);
			}
		}
		return longestScrapers;
	}
/**
 * Transforms a hashmap to a vector of pairs
 * */
	public static Vector<Pair<Long, Double>> toVector(HashMap<Long, Double> map) {
		Vector<Pair<Long, Double>> vector = new Vector<Pair<Long, Double>>();
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			vector.add(new Pair<Long, Double>((Long) entry.getKey(),
					(Double) entry.getValue()));
		}
		return vector;

	}
}
