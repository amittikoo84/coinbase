# coinbase

The algorithm works by iterating all paths from the source currency to the desitantion currency. 

For traversal it uses BFS to generate paths while keeping a running multiplication of all the conversion rates. 
If it finds a better rate than what is present for any source-> currency it updates the rate. 

Forward Cycles:
Since we start with a path with weight 1 between the source currency with itself,we will update it if we find a better rate that involves the source currency again.  
