package es.upm.dit.aled.lab4.genome;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Callable task that performs a linear search over a specific section of a
 * byte[] array containing a genome. It looks for a specific pattern and returns
 * all matches.
 * 
 * @author rgarciacarmona
 *
 */
public class FASTASearchCallable implements Callable<List<Integer>> {

	private FASTAReaderThreads reader;
	private int lo;
	private int hi;
	private byte[] pattern;

	/**
	 * Creates a new FASTASearchCallable that performs a linear search inside the
	 * [lo, hi) range of the genome.
	 * 
	 * @param reader  FASTAReaderThreads that contains the genome (content) and how
	 *                many bytes (validBytes) of this content are valid. Usually the
	 *                one that has called this constructor.
	 * @param lo      The lower bound of the segment of content to be searched.
	 * @param hi      The higher bound of the segment of content to be searched.
	 * @param pattern The pattern to be found.
	 */
	public FASTASearchCallable(FASTAReaderThreads reader, int lo, int hi, byte[] pattern) {// TODO
		this.reader = reader;
		this.lo = lo;
		this.hi = hi;
		this.pattern = pattern;
	}

	/**
	 * Implements a linear search to look for the provided pattern in the segment of
	 * the content array bounded by [lo, hi). Returns a List of Integers that point to
	 * the initial positions of all the occurrences of the pattern in the data.
	 * 
	 * @return All the positions of the first character of every occurrence of the
	 *         pattern in the segment of content to be searched.
	 */
	@Override
	public List<Integer> call() throws Exception {
		// TODO
		return null;
	}

	/*
	 * Helper method that checks if a pattern appears at a specific position in the
	 * data array. It checks every byte of the pattern one by one, but stops
	 * checking elements of the pattern when one has been found to be different. If
	 * the pattern length would need to access a position after the valid bytes of
	 * the array, it throws a new FASTAException to indicate this fact.
	 * 
	 * Returns true if the pattern is present at the given position; false
	 * otherwise.
	 */
	private boolean compare(byte[] pattern, int position) throws FASTAException {
		if (position + pattern.length > reader.getValidBytes()) {
			throw new FASTAException("Pattern goes beyond the end of the FASTA file.");
		}
		for (int i = 0; i < pattern.length; i++) {
			if (pattern[i] != reader.getContent()[position + i]) {
				return false;
			}
		}
		return true;
	}

}
