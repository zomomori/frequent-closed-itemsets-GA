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
import java.util.Random;

public class GACFI_algo {
	int max_improvement_count = 20; // maximum iterations allowed for a subpopulation

	ProductDb pDB;
	int totalItems; // Total number of Items in a set
	int minSupport; // Minimum support threshold
	int no_improvement_count = 0; // counter for no improvement of result

	List<FCI> population = new ArrayList<FCI>();
	List<FCI> subPopulation = new ArrayList<FCI>();

	/** Hashmap for frequent closed itemsets indexed by frequency **/
	Map<Integer, ArrayList<BitSet>> closedItemsets = new LinkedHashMap<>();

	/** Result parameters **/
	BufferedWriter writer = null; // object to write the output file
	int numOfTrans; // number of transactions
	int outputCount; // output total number of closed frequent itemsets
	int fcis_1_size; // 1 fcs size
	long startTimestamp; // start time of the last algorithm execution
	long endTimestamp; // end time of the last algorithm execution
	
	List<FCI> resultList = new ArrayList<FCI>();

	public GACFI_algo() {

	}

	/** Method to read the input file */
	ProductDb readFile(String filename) throws IOException {
		ProductDb pDb = new ProductDb();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;
		totalItems = 0;

		int item;
		while (((line = reader.readLine()) != null)) {
			if (line.isEmpty() == true || line.charAt(0) == '#' || line.charAt(0) == '%' || line.charAt(0) == '@') {
				continue;
			}
			FCI entry = new FCI();
			String[] lineSplited = line.split(" ");
			for (String itemString : lineSplited) {
				item = Integer.parseInt(itemString) - 1;
				if (item > totalItems)
					totalItems = item;
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

		pDB = readFile(filename);
		numOfTrans = pDB.products.size();

		// calculate the minSupport
		this.minSupport = (int) Math.ceil(pDB.products.size() * minSupport);

		// calculate and add the order 1-FCIs to initial population
		for (int i = 0; i <= totalItems; ++i) {
			BitSet bitset = new BitSet();
			bitset.set(i);
			
			FCI newEntry = new FCI();
			newEntry.bitset = bitset;
			setFrequency(newEntry);
			
			if (newEntry.frequency >= this.minSupport) {
				insert(newEntry);
				population.add(newEntry);
			}
		}
		fcis_1_size = population.size();

		int selection_1 = 0;
		int selection_2 = 0;
		for (int i = 0; i < totalItems; ++i) {
			while ((no_improvement_count < max_improvement_count)) {
				selection_1 = selectChromosome();
				selection_2 = selectChromosome();

				while (selection_1 == selection_2) {
					selection_2 = selectChromosome();
				}
				crossover(selection_1, selection_2, (i+1));
			}
			no_improvement_count = 0;

			// get population for next iteration
			for (int j = 0; j < subPopulation.size(); ++j) {
				insert(subPopulation.get(j));
			}
			population = new ArrayList<FCI>(resultList);
			
			subPopulation.clear();			
		}

		/** Generate output **/
		System.out.println("Frequent closed-itemsets:");
		outputCount = resultList.size();
		for (int x = 0; x < resultList.size(); x++) {
			printItemset(resultList.get(x).bitset);
		}
		System.out.println();
		writer.close();
		MemoryLogger.getInstance().checkMemory();
		endTimestamp = System.currentTimeMillis();
	}

	/**
	 * Method to select an itemset from the population for crossover
	 *  weighted random based on frequency (Higher frequency -> more probability for selection)
	 */
	private int selectChromosome() {
				int i, temp = 0;
		
				double totalFrequency = 0.0;
				for (i = 0; i < population.size(); ++i) {
					totalFrequency += population.get(i).frequency;
				}
		
				double randNum = Math.random() * totalFrequency;
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
	 * Method to crossover population[selection1] and population[selection2] at position
	 */
	private void crossover(int selection_1, int selection_2, int position) {
		int i = 0;
		int flag = 0;
		FCI offspring_1 = new FCI();
		FCI offspring_2 = new FCI();

		FCI temp;

		//int position = (int) (Math.random() * totalItems);// crossover position

		for (i = 0; i <= totalItems; i++) { // i <= position, crossover
			if (i <= position) {
				if ((population.get(selection_2).bitset.get(i))) {
					offspring_1.bitset.set(i);
				}
				if ((population.get(selection_1).bitset.get(i))) {
					offspring_2.bitset.set(i);
				}
			} else { // i > position, not crossover
				if ((population.get(selection_1).bitset.get(i))) {
					offspring_1.bitset.set(i);
				}
				if ((population.get(selection_2).bitset.get(i))) {
					offspring_2.bitset.set(i);
				}
			}
		}

		temp = offspring_1;
		if (alreadyPresent(temp)) { 	// if itemset already exists, mutation
			temp.bitset = mutation(temp.bitset);
		}
		setFrequency(temp); 
		if (!subPopulation.contains(temp) && (temp.frequency >= this.minSupport)) {
			subPopulation.add(temp); // insert offspring into the subPopulation if frequency > minSupport
			flag = 1;
		}

		temp = offspring_2;
		if (alreadyPresent(temp)) { 	// if itemset already exists, mutation
			temp.bitset = mutation(temp.bitset);
		}
		setFrequency(temp); 
		if (!subPopulation.contains(temp) && (temp.frequency >= this.minSupport)) {
			subPopulation.add(temp); // insert coffspring into the subPopulation if frequency > minSupport
			flag = 1;
		}
		
		// increment no improvement counter if no new offspring generated in subPopulation
		if (flag == 0) {
			++no_improvement_count;
		} else {
			no_improvement_count = 0;
		}
	}

	/** function to check whether bitset is aleady present in population **/
	private boolean alreadyPresent(FCI tempNode) {
		return population.contains(tempNode) || subPopulation.contains(tempNode);
	}

	/** mutation of chromosome at a random index **/
	private BitSet mutation(BitSet bitset) {
		int temp = (int) (Math.random() * totalItems);
		bitset.set(temp, !bitset.get(temp));
		return bitset;
	}

	/** Method to set the frequency of a itemset from the transaction database */
	private void setFrequency(FCI tempItemset) {
		int p;
		int freq = 0;
		int flag;

		BitSet bitSet = tempItemset.bitset;
		if (bitSet.cardinality() == 0) {
			tempItemset.frequency = 0;
			return;
		}

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
				freq++;
			}
		}
		tempItemset.frequency = freq;
	}

	/**
	 * Method to insert an itemset into the result frequent-closed itemsets list
	 * adjusts itemset list accordingly to account only for closed itemsets
	 */
	private boolean insert(FCI tempChroNode) {
		if (resultList.contains(tempChroNode)) return false; // not inserted
		ArrayList<BitSet> list = closedItemsets.get(tempChroNode.frequency);

		if (list == null) {
			list = new ArrayList<BitSet>();
		}

		int carTemp = tempChroNode.bitset.cardinality();
		int carIter;

		boolean isSubset = false;

		List<Integer> toRemove = new ArrayList<Integer>();

		BitSet x, y;
		FCI delete = new FCI();

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
					flag = 0; // the itemset was different
					break;
				}
			}
			if (flag == 1 && isSubset) { // itemset to be inserted is a subset of
				return false; // a itemset of same frequency -> exit, not inserted
			}
			if (flag == 1 && !isSubset) { // another itemset of the same frequency is a subset
				toRemove.add(j); // of our itemset to be inserted -> add to delete list
			}
		}
		Collections.sort(toRemove, Collections.reverseOrder());
		for (Integer i : toRemove) { // remove all the same frequency subsets of the itemset to be inserted
			delete.bitset = list.get(i);
			resultList.remove(delete);
			list.remove(delete.bitset);
		}

		list.add(tempChroNode.bitset); // add the itemset to be inserted in the FCI list
		resultList.add(tempChroNode);
		closedItemsets.put(tempChroNode.frequency, list);
		return true; // inserted
	}

	/**
	 * method to print an itemset in the console as well as output file
	 * @throws IOException 
	 */
	private void printItemset(BitSet bitset) throws IOException {
		for (int i = 0; i <= totalItems; ++i) {
			if (bitset.get(i)) {
				System.out.print((i + 1) + " ");
				writer.write((i + '1') + " ");
			}
		}
		System.out.println();
		writer.write("\n");
	}

	/**
	 * Print statistics about the latest execution of the algorithm to System.out.
	 */
	public void printStats() {
		System.out.println("========== GA-FCI - STATS ============");
		System.out.println(" Minsup : " + this.minSupport);
		System.out.println(" Number of transactions: " + numOfTrans);
		System.out.println(" Number of frequent 1-items  : " + fcis_1_size);
		System.out.println(" Number of closed  itemsets: " + outputCount);
		System.out.println(" Total time ~: " + (endTimestamp - startTimestamp) + " ms");
		System.out.println(" Max memory:" + MemoryLogger.getInstance().getMaxMemory() + " MB");
		System.out.println("=====================================");
	}

	// ============================

	/** Class representing a product database */
	class ProductDb {
		List<FCI> products;

		ProductDb() {
			products = new ArrayList<FCI>();
		}
	}

	// ============================

	/** Class representing a frequent closed itemset */
	class FCI {
		BitSet bitset;
		int frequency;

		public FCI() {
			bitset = new BitSet();
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			/*
			 * Check if o is an instance of Complex or not "null instanceof [type]" also
			 * returns false
			 */
			if (!(o instanceof FCI)) {
				return false;
			}

			// typecast o to Complex so that we can compare data members
			FCI c = (FCI) o;

			// Compare the data members and return accordingly
			return this.bitset.equals(c.bitset);
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
//==========================================================
