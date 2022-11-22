package code;

import java.util.ArrayList;

public class GeneralSearchProblem {
    
    static class Queue {
        private ArrayList<CGNode> myArray = new ArrayList<CGNode>();

        public void add(CGNode n) {
            myArray.add(0, n);
        }
        public CGNode remove() {
            CGNode n = myArray.get(0);
            myArray.remove(0);
            return n;
        }
        public Integer size() {
            return myArray.size();
        }
        public Boolean isEmpty() {
            return myArray.size() == 0;
        }
    }
//    public Node GeneralSearch(Node initial_state, String [] operators , String strategy){
//        Queue queue = new Queue();
//        queue.add(initial_state);
//        while(!queue.isEmpty()){
//            Node Node = queue.remove();
//            if (Node.goalTest())
//                return Node;
//            else{
//                if (strategy.equals("DF")){
//                    DFS(queue, Node, operators);
//                }
//            }
//        }
//        return null;
//    }

    public static void DFS (Queue nodes, Node node, String [] Operators){

    }


}
