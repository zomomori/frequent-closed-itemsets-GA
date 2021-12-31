# frequent-closed-itemsets-Genetic-Algorithm

Parameters that need to be altered: max_improvement_count (number of times crossover is performed without obtaining a offspring not already present in the sub population, before moving to the next generation)

NOTES:
- Selection of parents is ranked selection, based on frequency (more frequency -> higher chance of getting picked for crossing)
- Mutation if done when offspring is already present in population (one random bit is reversed)
- When max_improvement_count is reached, we move to the next generation
- Population for the next generation is the resulting frequent-closed itemset list obtained

TO DO:
- Determining criteria of max_improvement_count for each query
- Comparison with larger/real-world datasets
- Possible optimization


Comparison With NA-FCP
==========================================

INPUT:<br />
1 3 4<br />
2 3 5<br />
1 2 3 5<br />
2 5 <br />
1 2 3 5 <br />

OUTPUT:<br />
3 <br />
1 3 <br />
2 5 <br />
2 3 5 <br />
1 2 3 5 <br />

========== GA-FCI - STATS ===========<br />
 Minsup : 2<br />
 Number of transactions: 5<br />
 Number of frequent 1-items  : 4<br />
 Number of closed  itemsets: 5<br />
 Total time ~: 4 ms<br />
 Max memory:6.931571960449219 MB<br />
 
 ========== NAFCP - STATS ===========<br />
 Minsup : 2<br />
 Number of transactions: 5<br />
 Number of frequent 1-items  : 4<br />
 Number of closed  itemsets: 5<br />
 Total time ~: 4 ms<br />
 Max memory:18.899696350097656 MB<br />
=================================<br /><br />


INPUT:<br />
1 3 4<br />
2 3 5 20<br />
1 2 3 5 30<br />
2 5 9 30 <br />
1 2 3 5 9 20<br />
1 4 5 9 11 30<br />
2 5 9 13<br />
2 5 9 5<br />
2 5 9 12 13 14 30<br />
1 2 3 5 9 11<br />
1 4 5 9 11 14 <br />

OUTPUT:<br />
1 <br />
3 <br />
5 <br />
2 5 <br />
1 5 <br />
1 3 <br />
1 4 <br />
5 9 <br />
2 3 5 <br />
1 5 9 <br />
2 5 9 <br />
1 5 9 11 <br />
5 30 <br />
5 9 30 <br />
2 5 30 <br />
1 2 3 5 <br />

========== GA-FCI - STATS ===========<br />
 Minsup : 3<br />
 Number of transactions: 11<br />
 Number of frequent 1-items  : 8<br />
 Number of closed  itemsets: 16<br />
 Total time ~: 14 ms<br />
 Max memory:7.3220672607421875 MB<br />
 
========== NAFCP - STATS ============<br />
 Minsup : 3<br />
 Number of transactions: 11<br />
 Number of frequent 1-items  : 8<br />
 Number of closed  itemsets: 16<br />
 Total time ~: 5 ms<br />
 Max memory:14.883987426757812 MB<br />
=================================<br />
