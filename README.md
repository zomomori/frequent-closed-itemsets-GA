# frequent-closed-itemsets-GA

Parameters that need to be altered: max_improvement_count (number of times crossover is performed without obtaining a offspring not already present in the sub population, before moving to the next generation)

NOTES:
- Selection of parents is ranked selection, based on frequency (more frequency -> higher chance of getting picked for crossing)
- Mutation if done when offspring is already present in population (one random bit is reversed)
- When max_improvement_count is reached, we move to the next generation
- Population for the next generation is the resulting frequent-closed itemset list obtained

TO DO:
- Tweaking of max_improvement_count for each query
- Comparison with real-world large datasets
- Possible optimization


COMPARISON WITH NA-FCP ALGORITHM
======================================================================

INPUT:
1 3 4
2 3 5
1 2 3 5
2 5 
1 2 3 5 

OUTPUT:
3 
1 3 
2 5 
2 3 5 
1 2 3 5 

========== GA-FCI - STATS ===========
 Minsup : 2
 Number of transactions: 5
 Number of frequent 1-items  : 4
 Number of closed  itemsets: 5
 Total time ~: 4 ms
 Max memory:6.931571960449219 MB
 
 ========== NAFCP - STATS ===========
 Minsup : 2
 Number of transactions: 5
 Number of frequent 1-items  : 3
 Number of closed  itemsets: 5
 Total time ~: 4 ms
 Max memory:18.899696350097656 MB
=====================================


INPUT:
1 3 4
2 3 5 20
1 2 3 5 30
2 5 9 30 
1 2 3 5 9 20
1 4 5 9 11 30
2 5 9 13
2 5 9 5
2 5 9 12 13 14 30
1 2 3 5 9 11
1 4 5 9 11 14 

OUTPUT:
1 
3 
5 
2 5 
1 5 
1 3 
1 4 
5 9 
2 3 5 
1 5 9 
2 5 9 
1 5 9 11 
5 30 
5 9 30 
2 5 30 
1 2 3 5 

========== GA-FCI - STATS ===========
 Minsup : 3
 Number of transactions: 11
 Number of frequent 1-items  : 8
 Number of closed  itemsets: 16
 Total time ~: 14 ms
 Max memory:7.3220672607421875 MB
 
========== NAFCP - STATS ============
 Minsup : 3
 Number of transactions: 11
 Number of frequent 1-items  : 8
 Number of closed  itemsets: 16
 Total time ~: 5 ms
 Max memory:14.883987426757812 MB
=====================================
