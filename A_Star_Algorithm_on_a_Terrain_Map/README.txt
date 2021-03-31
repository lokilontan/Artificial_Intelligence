A* algo: 
Implemented 2 heuristic functions for 2 different cost functions. The goal is that heuristic function never overestimates the REAL cost to get to the neighbor. 

Usage: 
java Main {YourAIModule} -seed x 
java Main {YourAIModule} -load MTAFT.XYZ

Where {YourAIModule} = AStarDiv (Divisive cost function)
		    = AStarExp (Exponential cost function)
x = 1,2,3,4,5,...
