## Extraction of Closed Frequent Itemsets using Genetic Algorithm

#### Description
A novel approach for extracting Closed Frequent Itemsets in Data Mining using a Genetic Algorithm. Using Java, I implemented the algorithm and evaluated its performance against the current best NA-FCP algorithm. The project aimed to improve the efficiency and accuracy of Closed Frequent Itemset extraction, with a focus on identifying patterns in large datasets. 
<br />
<br />
<br />

 Tested on database: https://www.kaggle.com/ekrembayar/apriori-association-rules-grocery-store/data?select=Grocery+Products+Purchase.csv<br /><br />
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
===============================<br />
Crossover time: 282 ms; Crossover calls: 13257<br />
Frequency calculated from database time: 251 ms; calls made: 2877<br />
Frequency calculated from anti-monotonicity time: 13 ms; calls made: 16844<br /> 

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
