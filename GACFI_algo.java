package GAFCI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GACFI_algo {
	final int pop_size = 100;//the size of population 
	final int max_iter = 2000;// maximum iterations 

	ProductDb pDB; 
	int maxDigit; //size of the bitsets
	int minSupport; //Minimum support threshold

	List<FCI> population = new ArrayList<FCI>();
	List<FCI> subPopulation = new ArrayList<FCI>();

	int numOfTrans; // number of transactions
	int outputCount; // output total number of closed frequent itemsets

	/** List of frequent closed itemsets */
	Map<BitSet, Integer> fcis = new LinkedHashMap<>();
	Map<Integer, ArrayList<BitSet>> closedItemsets = new LinkedHashMap<>();

	long startTimestamp; //start time of the last algorithm execution
	long endTimestamp; //end time of the last algorithm execution

	BufferedWriter writer = null; //object to write the output file

	public GACFI_algo() {

	}

	/** Read the input file */
	ProductDb readFile(String filename) throws IOException {
		ProductDb pDb = new ProductDb();

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;

		maxDigit = 0;

		int item;
		while (((line = reader.readLine()) != null)) {
			if (line.isEmpty() == true || line.charAt(0) == '#' || line.charAt(0) == '%' || line.charAt(0) == '@') {
				continue;
			}

			FCI entry = new FCI();
			String[] lineSplited = line.split(" ");
			for (String itemString : lineSplited) {
				item = Integer.parseInt(itemString) - 1;
				if (item > maxDigit) maxDigit = item;
				entry.bitset.set(item);
			}
			pDb.products.add(entry);
		}
		reader.close();
		return pDb;
	}

	/**
	 * Run the algorithm
	 */
	public void runAlgorithm(String filename, double minSupport, String output) throws IOException {

		MemoryLogger.getInstance().reset();

		// create object for writing the output file
		writer = new BufferedWriter(new FileWriter(output));

		// record the start time
		startTimestamp = System.currentTimeMillis();
		// ==========================
		
		
		pDB = readFile(filename);
		numOfTrans = pDB.products.size();
		
		// calculate the minSupport
		this.minSupport = (int) Math.ceil(pDB.products.size() * minSupport);

		// scan database (1) to calculate frequency of each Item
		// The count of items is stored in map where
		// key = item value = count count
		Map<Integer, Integer> mapItemCount = new HashMap<Integer, Integer>();
		for (int i = 0; i < pDB.products.size(); i++) {
			FCI item = pDB.products.get(i);
			for (int j = 0; j <= maxDigit; ++j) {
				if (item.bitset.get(j)) {
					Integer count = mapItemCount.get(j);
					if (count == null) {
						mapItemCount.put(j, 1);
					} else {
						mapItemCount.put(j, ++count);
					}
				}
			}
		}

		for (Map.Entry<Integer, Integer> entry : mapItemCount.entrySet()) {
			FCI newEntry = new FCI();
			if (entry.getValue() >= this.minSupport) {
				newEntry.bitset.set(entry.getKey());
				newEntry.frequency = entry.getValue();
				// add the order 1-FCIs to initial population
				insert(newEntry);
				population.add(newEntry);
			}
		}

		int temp1 = 0;
		int temp2 = 0;

		for (int i = 0; i < max_iter; i++) {
			while (subPopulation.size() < pop_size) {

				// select two chromosomes from population
				temp1 = selectChromosome();
				temp2 = selectChromosome();

				while (temp1 == temp2) {
					temp2 = selectChromosome();
				}

				// crossover the chromosomes and insert in subPopulation
				crossover(temp1, temp2);
			}

			// get population for next iteration
			subPopulation.addAll(population);
			Collections.sort(subPopulation, fc.reversed()); //sort the population in order of frequency

			for (int j = 0; j < pop_size; j++) {
				population.set(j, subPopulation.get(j));
			}
			subPopulation.clear();
		}

		System.out.println("FINAL SET");
		for (var entry : fcis.entrySet()) {
			printBitset(entry.getKey());
		    System.out.println("  /" + entry.getValue());
		}
		
		
		MemoryLogger.getInstance().checkMemory();
		
		// record the end time
		endTimestamp = System.currentTimeMillis();
	}


	/**
	 * Seclect a Chromosome from the population; weighted random
	 */
	private int selectChromosome() {
		int i, temp = 0;

		double totalFrequency = 0.0;
		for(i = 0; i < population.size(); ++i){
			totalFrequency += population.get(i).frequency;
		}

		double randNum  = Math.random() * totalFrequency;
		double countFrequency = 0.0;
		for (i = 0; i < population.size(); i++) {
			countFrequency += population.get(i).frequency;

			if (countFrequency >= randNum) {
				return i;
			}
		}
		return temp;
	}

	/**
	 * Method to crossover population[temp1] and population[temp2]
	 */
	private void crossover(int temp1, int temp2) {
		int i = 0;
		FCI temp1Chro = new FCI();
		FCI temp2Chro = new FCI();

		FCI tempNode;
		boolean inserted = false;

		int position = (int) (Math.random() * maxDigit);// crossover position
		
		for (i = 0; i <= maxDigit; i++) {	// i <= position, crossover
			if (i <= position) {
				if ((population.get(temp2).bitset.get(i))) {
					temp1Chro.bitset.set(i);
				}
				if ((population.get(temp1).bitset.get(i))) {
					temp2Chro.bitset.set(i);
				}
			} else {						// i > position, not crossover
				if ((population.get(temp1).bitset.get(i))) {
					temp1Chro.bitset.set(i);
				}
				if ((population.get(temp2).bitset.get(i))) {
					temp2Chro.bitset.set(i);
				}
			}
		}
		
		// if closed itemsets already exist, mutation
		if (fcis.containsKey(temp1Chro.bitset)) {
			temp1Chro.bitset = mutation(temp1Chro.bitset);
		}
		if (fcis.containsKey(temp2Chro.bitset)) {
			temp2Chro.bitset = mutation(temp2Chro.bitset);
		}

		// insert crossed chromosomes into the subPopulation
		if (temp1Chro.bitset.cardinality() > 0) {
			tempNode = temp1Chro;
			tempNode.frequency = freqCalculate(temp1Chro);
			if (tempNode.frequency >= minSupport) {
				inserted = insert(tempNode);
			}
			if (inserted) subPopulation.add(tempNode);
		}
		if (temp2Chro.bitset.cardinality() > 0) {
			tempNode = temp2Chro;
			tempNode.frequency = freqCalculate(temp2Chro);
			if (tempNode.frequency >= minSupport) {
				inserted = insert(tempNode);
			}
			if (inserted) subPopulation.add(tempNode);
		}
	}
	
	
	// mutation of chromosome at a random index
	private BitSet mutation(BitSet bitset) {
		int temp = (int) (Math.random() * maxDigit);
		bitset.set(temp, !bitset.get(temp));
		return bitset;
	}

	/**
	 * Method to calculate the frequency of a bitset in the transaction database
	 */
	private int freqCalculate(FCI tempChroNode) {
		int p;
		int freq = 0;
		int flag;

		BitSet bitSet = tempChroNode.bitset;

		for (p = 0; p < pDB.products.size(); p++) {
			FCI item = pDB.products.get(p);
			flag = 1;
			for (int i = bitSet.nextSetBit(0); i != -1; i = bitSet.nextSetBit(i + 1)) {
				if (!item.bitset.get(i)) {
					flag = 0;
					break;
				}
			}
			if (flag == 1) {
				freq ++;
			}
		}
		return freq;
	}


	/**
	 * Method to insert a chromosome into the frequent-closed itemsets list
	 */	
	private boolean insert(FCI tempChroNode) {
		if (!fcis.containsKey(tempChroNode.bitset)) { // if does not already exist
			ArrayList<BitSet> list = closedItemsets.get(tempChroNode.frequency);

			if (list == null) {
				list = new ArrayList<BitSet>();
			}

			int carTemp = tempChroNode.bitset.cardinality();
			int carIter;

			boolean isSubset = false;

			List<Integer> toRemove = new ArrayList<Integer>();

			BitSet x, y;

			for (int j = 0; j < list.size(); ++j) {
				BitSet b = list.get(j);
				carIter = b.cardinality();
				if (carIter < carTemp) {
					y = b;
					x = tempChroNode.bitset;
					isSubset = false;
				} else if (carIter > carTemp) {
					y = tempChroNode.bitset;
					x = b;
					isSubset = true;
				} else {
					continue;
				}

				int flag = 1;
				for (int i = y.nextSetBit(0); i != -1; i = y.nextSetBit(i + 1)) {
					if (!x.get(i)) {
						flag = 0; // the bitset was different
						break;
					}
				}
				if (flag == 1 && isSubset) { //bitset to be inserted is a subset of 
					return false;			 //a bitset of same frequency, exit
				}
				if (flag == 1 && !isSubset) { //another bitset of the same frequency is a subset
					toRemove.add(j);		  //of our bitset to be inserted, add to delete list
				}
			}

			for(Integer i : toRemove) { // remove all the subsets of same frequency
				fcis.remove(list.get(i));
				list.remove(i);
			}

			list.add(tempChroNode.bitset); // add the bitset to be inserted in the FCI list
			fcis.put(tempChroNode.bitset, tempChroNode.frequency);
			closedItemsets.put(tempChroNode.frequency, list);
			
			return true; // inserted
		}
		return false; // couldn't insert
	}

	/** PLEASE IGNORE - method for testing 
	 * Prints a bitset
	 */
	private void printBitset(BitSet bitset) {
		for (int i = 0; i <= maxDigit; ++i) {
			int k = bitset.get(i) ? 1 : 0;
			System.out.print(k);
		}
	}

	/**
	 * Print statistics about the latest execution of the algorithm to System.out.
	 */
	public void printStats() {
		System.out.println("========== GA-FCI - STATS ============");
		System.out.println(" Minsup : " + this.minSupport);
		System.out.println(" Number of transactions: " + numOfTrans);
		//System.out.println(" Number of frequent 1-items  : " + fcis_1.size());
		System.out.println(" Number of closed  itemsets: " + outputCount);
		System.out.println(" Total time ~: " + (endTimestamp - startTimestamp) + " ms");
		System.out.println(" Max memory:" + MemoryLogger.getInstance().getMaxMemory() + " MB");
		System.out.println("=====================================");
	}


	//============================
	
	
	/** Class representing a product database */
	class ProductDb {
		List<FCI> products;

		ProductDb() {
			products = new ArrayList<FCI>();
		}
	}

	//============================
	
	/** Class representing a frequent closed itemset */
	class FCI {
		BitSet bitset;
		int frequency;

		public FCI() {
			bitset = new BitSet();
		}
	}
	static Comparator<FCI> fc = new Comparator<FCI>() {
		@Override
		public int compare(FCI x, FCI y) {
			{
				if (x.frequency > y.frequency)
					return 1;
				else if (x.frequency == y.frequency)
					return 0;
				else
					return -1;
			}
		}
	};
}
//============================
