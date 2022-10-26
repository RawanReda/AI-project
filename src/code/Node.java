package code;

public class Node {

    static class State{
        int i;
        int j;
        public State(int i, int j){
            this.i=i; this.j=j;
        }
    }
//    Node parent;
    State state;
    String type; // I, S, CG

    public Node(int i, int j, String type){
        this.state= new State(i,j);
        this.type = type;
    }
}
