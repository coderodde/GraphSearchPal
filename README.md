# GraphSearchPal
An experimental/research GUI application for demonstrating pathfinding algorithms.

My aim is to come up with a definitive collection of pathfinding algorithms + minimum priority queue implementations and put them all under a single GUI application. The app allows users to draw their maze, set up the source/target nodes and choose the algorithm/heap combination after which the progress is displayed in real-time.

### Pathfinding algorithms
- [x] Dijkstra's algorithm
- [x] Bidirectional Dijkstra's algorithm
- [x] A*
- [x] Bidirectional A*
- [x] New Bidirectional A* (NBA*) [1]
- [x] Parallel Bidirectional A* (PNBA*)[2], (works as expected on large graphs, yet fails on jUnit tests)

### Priority queues 

- [x] `BinomialHeap`
- [x] `DaryHeap`: this is the generalization of a binary heap (`d = 2`) that allows `d` children for each element in the heap.
- [x] `FibonacciHeap`
- [x] `PairingHeap`

### References
* [1] Pijls, Wim and Post, Henk: _Yet another bidirectional algorithm for shortest paths_
* [2] Rios, Luis Henrique Oliveira and Chaimowicz, Luiz: _A Parallel Bidirectional Heuristic Search Algorithm_
