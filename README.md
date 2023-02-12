# AI-project

This project was part of course CSEN701 (Introduction to Artificial Intelligence) where we implemented our knowledge of search strategies algorithms (both informed an d uninformed). 

**Problem Description:** 

Passengers on sinking ships are drowning. Each ship also has a black box that can be retrieved within a certain period after the ship becomes a wreck. Our coast guard is in charge of rescuing as many passengers by picking passengers up from the ship before they die and dropping them at the station as possible and retrieving black boxes from wrecked ships. The coast guard is allowed to move in any 4 directions ( up, down, left, and right).  Our problem is to identify the best sequence of actions the coast guard can take to minimize the number of deaths and retrieve as many boxes. 

To aid in representing the problem, we use a 2D array of objects GridCell which is implemented as follows: 
<p align="center">
<img src="https://user-images.githubusercontent.com/51987270/206745429-45632e03-aad9-4445-b1b6-57412d4d898d.png" width="500">
</p>


**The search-tree node ADT**
<p align="center">
<img src="https://user-images.githubusercontent.com/51987270/206745438-a6a2d4c9-0728-430f-bbc6-4444f24cd92e.png" width="500">
</p>

The search-tree node ADT was implemented as a class that includes the following parameters:
- Parent node 
- Operator applied to generate this node 
- State

The state itself was represented as an inner class to the node. The state was created to include information about the current state of the node with reference to the coast guard problem. In other words, the state included the location of the coast guard in that state, the total number of remaining passengers and black coxes left for the coast guard to save or retrieve, the current remaining capacity of the coast guard, and a hashmap containing the observers (the ships). The observer design pattern was used, the ships are the observers, observing the coast guard (observable) where with every action the coast guard performs, the ships observe the action and act accordingly to remove one passenger or add an additional damage point to the black box. Furthermore, the state includes the depth of the node. 
- Depth (For convenience it was added as a parameter in the inner class State.) 
- Cost (For convenience it was added as a parameter in the inner class State.) 

The goal test method returns true when all the passengers are either dead or rescued and there are no remaining black boxes. 

**The Search Problem ADT:**
<p align="center" width="100"  height="100">
<img src="https://user-images.githubusercontent.com/51987270/206745444-864ac99f-7be9-4dc0-88b6-d2506c9afbfa.png" width="500">
</p>
The search problem ADT included the implementation of the general search problem. Depending on the search strategy that was provided as input in the solve method in the CoastGaurd class, a specific method is called. There are two main expansion methods: one is for informed search (expand_IS) and the other is for uninformed search (expand). Both of these will be explained in the main functions section. 

BF, DF, and ID make use of expand method, while GR1, GR2, AS1, and AS2 make use of the expand_IS method. 

In this class, we also have the visualize method which prints the state of a node. Visualize updates the string value of final_visualized_path.

printNode method is used to print attributes that are directly related to the node such as the operator used at the time of expansion, the depth of the node, and the parent of the node. 

Every time a node is expanded, it will have to notify its observers (Ships) to update the number of deaths, the number of remaining passengers, and the remaining time on the expiration of the black box (if the ship is wrecked). It also updates the state in the Node object accordingly. This is achieved by the method notifyObservers. 

The search algorithms check for redundant states by checking if this node was previously expanded, this is done by checking if the coast guard was in the exact location before, with the same number of passengers on board, the same number of rescued passengers, and the same number of retrieved black boxes. This is achieved by the method isRedundant. 

The search problem ADT included the implementation of the general search problem. The problem simply adds the initial state to a queue, and while this queue is not empty it keeps pulling nodes, expanding them using the operators of the problem if possible.

**CoastGuard problem**
<p align="center">
<img src="https://user-images.githubusercontent.com/51987270/206745456-503a3d2a-a00b-4663-a873-0c2f9e9d197e.png" width="500">
</p>

In the CoastGaurd class, we have several attributes: 
- cg_i and cg_j are the positions of the coast guard in the grid.
- capacity is the max. number of passengers a coast guard can carry
- total_passengers is the sum of all passengers across all ships 
- saved_passengers is the number of passengers rescued (i.e dropped at the station)
- We also have grid_string, and locations_occupied which are used by the genGrid method to construct a new grid. 
- grid array attribute is the result of parsing the String grid in the solve method and genGrid method. 


CoastGuard implements GridSearchProblem class. 

<p align="center">
<img src="https://user-images.githubusercontent.com/51987270/206745485-f2b020ca-f396-4592-9473-7abf1f9e94ec.png" width="700">
</p>

**Main functions implemented**

To find the path that leads to a goal state, we first call the method solve in CoastGuard class. 

public static String solve(String g,String strategy,Boolean visualise){

solve takes in three parameters: String grid, String strategy, and Boolean visualize. 

A 2D grid of type GridCell is constructed from the string input parameter, grid. 

Each ship is an observer and will be added to the list of observers through the addObserver method. The initial node is created, containing the state with the observers, and is passed to a search method. 

Depending on the string input strategy provided, a certain method is called in the GeneralSearchProblem class to implement the search algorithm. 

**Search algorithms implemented:**

We have two types of search algorithms: Uninformed and Informed search.

Uninformed search includes: 
- Breadth-First-Search (BFS)
- Depth-First-Search (DFS) 
- Iterative-Deepening-Search (IDS)
- Uniform cost (UC) 

Informed search strategies: 
- Greedy heuristic 1 (GR1)
- Greedy heuristic 2 (GR2)
- A* heuristic 1 (AS1)
- A* heuristic 2 (AS2)

In BFS, DFS, and IDS we are not interested in the cost or any heuristic value. Hence, we implement a simple method expand that expands nodes based on the operator of the input node. 

Meanwhile, the order of expansion is altered depending on the search strategy used. 

In BFS, we expand adjacent nodes first before moving to the next depth level. It uses a Queue data structure that follows first in first out.

In DFS, we expand as far as possible along each branch before backtracking. It uses a Queue data structure that follows last in first out ( hence we created a QueueLIFO data structure). 

In IDS, we make use of depth-first search which imposes a cut-off,l, on the maximum depth of a path, and we try all depths,l, starting from 0. 

In general, When we expand a node, we also call the method ‘notifyObservers’ on the node we expand to notify all the ships(observers of the node) that an action was taken, and accordingly each ship will update important attributes such as the number of deaths, number of remaining passengers, black box expiration date if the ship becomes a wreck. In addition, attributes in the node such as total remaining passengers, the total number of deaths, and the total number of boxes retrieved (in case the action was ‘retrieve’) are also updated.

—-----------------------------------------------------------------------------------------------------------------------------------------

**expand method works as follows:**

`public static void expand(GridCell[][] grid,Node cg,Queue q, int capacity){`

input parameter grid is the grid in which the coast guard moves. cg is the node we are expanding. q is a queue containing the nodes yet to be expanded. capacity is the full capacity of the coast guard which aids us when creating an unexpanded node that has the operator ‘pickup’ or ‘drop’. 

Depending on the position(cg_i and cg_j) of the coast guard, a new node is created and added to the queue with new node positions as long as the new positions are within the grid boundaries. There are 4 possible moves of the nodes: up, down, left, and right. 

If the cell on which the node is currently is a ship, we check if the ship is wrecked or not. If wrecked and the coast guard still has space for carrying passengers, a new node is created with the operator ‘pickup’, and added to the queue. On the other hand, is the ship is a wreck and the black box expiration date has not passed, a new node is created with the operator ‘retrieve’ and added to the queue. 

If the cell on which the node is currently is a station, we check if the node is carrying any passengers. If true, a new node is created with the operator ‘drop’ and added to the queue. 

—-----------------------------------------------------------------------------------------------------------------------------------------

For UC, GR1, GR2, AS1, and AS2, we use expand_IS

In these 5 search strategies, we use PriorityQueue data structure to sort the node based on their assigned value( different values are assigned depending on the search strategy used). 

**UC/ Cost function:**
Our cost function entails: 
- number of deaths
- number of boxes lost 

Since we care more about the number of deaths, we assigned a higher weight for it, hence the cost equation: 

`cost =2*number_of_deaths + number_of_lost_blackboxes`

**GR1 Method:**
For greedy 1, the following heuristic is used, where the total_ships_CB is the total city block distances the coast guard will cover to go to all the ships that could be saved, where at each point the coast guard will have to choose the closest ship.  total_BB_CB is the total city block distances the coast guard will cover to go to all the wrecked ships to retrieve their black boxes when all ships have become wrecks, by choosing the closest wreck ship at each point before making a move. The heuristic function is applied when the number of passengers on the closest ship is less than the number of steps the coast guard will take to reach the ship at each point. The total_ships_CB is multiplied by 2 to give it more weight because we want to prioritize saving passengers over retrieving black boxes.

The node with a smaller value is expanded first, and once all ships become wrecks and all black boxes are retrieved/ got lost, the value of the heuristic becomes a zero, making it admissible. 
`h1 = (2 * total_ships_CB) + total_BB_CB;`

**AS1 Method:**
assigns the summation of heuristic 1 value and cost value of the node to the node. 
a_star1= h1 + cost; 
GR2 Method:

For greedy 2, the following heuristic was used, where closestShip_CB is the closest ship to the coast guard and closestShip_passengers is the number of passengers in the closest ship. To get the values of closestShip_CB and cosestShip_passengers, the distance between the coast guard and every ship is calculated. The heuristic function is applied when the number of passengers on the closest ship is less than the number of steps the coast guard will take to reach the ship. 

As the number of passengers in the closest ship increases, the heuristic value decreases, getting closer to zero, making it admissible. If all the ships become a wreck, the node is assigned a value of zero. 

The node with a smaller value will be expanded first. 

`h2 = 1 /( 1 + (closestShip_passengers - closestShip_CB))`

**AS2 Method:**
AS2 method assigns the summation of the heuristic 2 value and cost value of the node to the node. 

`a_star2= h2 + cost`


**expand_IS method:**

`public static String expand_IS(GridCell[][] grid, Node initial_state, int capacity, String strategy) {`

This is exactly the same as expand, but with an additional parameter ‘strategy’, which helps us in determining which function to use to assign a value to the node. Before adding a node to the queue, we assign its value by calling the method ‘calculateNodeVal’ which takes as input the node and a key that maps to a search strategy. 

If the key is 0, the node value is assigned the cost. If 1, the node value is assigned the value of heuristic1. If 2, the node value is assigned the value of heuristic2.  If 3, the node value is assigned the value of A\*1. If 4, the node value is assigned the value of A*2.








