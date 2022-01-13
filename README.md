# frequent-closed-itemsets-Genetic-Algorithm

-------------------------------UPDATE------------------------------------
=========================================================================
 Tested with database: <br /><br />

========== NAFCP - STATS ============<br />
 Minsup : 99<br />
 Number of transactions: 9835<br />
 Number of frequent 1-items  : 88<br />
 Number of closed  itemsets: 333<br />
 Total time ~: 117 ms<br />
 Max memory:14.487472534179688 MB<br />
========== GA-FCI - STATS ===========<br />
 Minsup : 99<br />
 Number of transactions: 9835<br />
 Number of frequent 1-items  : 88<br />
 Number of closed  itemsets: 333<br />
 Total time ~: 361 ms<br />
 Max memory: 14.441978454589844 MB<br />
=====================================<br />
Crossover time: 282 ms; Crossover calls: 13257<br />
Frequency calclated from database time: 251 ms; calls made: 2877<br />
Frequency calclated from anti-monotonicity time: 13 ms; calls made: 16844<br /> 

-------------------------------------------------------------------------

TO DO:
- Possible optimization
- Decrease time for calculating frequency from database

NOTES:
- Selection of parents is ranked selection, based on frequency (more frequency -> higher chance of getting picked for crossing)
- Mutation if done when offspring is already present in population (one random bit is reversed)
- When max_improvement_count is reached, we move to the next generation
- Population for the next generation is the resulting frequent-closed itemset list obtained
- Parameters that need to be altered before running: 
    - max_improvement_count (number of times crossover is performed without obtaining a offspring not already present in the sub population, before moving to the next generation)
    - population size


Comparison With NA-FCP (old)
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
 Total time ~: 3 ms<br />
 Max memory:6.4420013427734375 MB<br />
 
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
 Total time ~: 7 ms<br />
 Max memory:6.441993713378906 MB<br />
 
========== NAFCP - STATS ============<br />
 Minsup : 3<br />
 Number of transactions: 11<br />
 Number of frequent 1-items  : 8<br />
 Number of closed  itemsets: 16<br />
 Total time ~: 5 ms<br />
 Max memory:14.883987426757812 MB<br />
=================================<br />
