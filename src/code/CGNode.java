package code;

public class CGNode extends Node{

    static class State{
        int i;
        int j;
        int remaining_passengers;
        int remaining_blackboxes;
        int current_capacity; // Number of passengers currently on CG

        public State(int i, int j, int remaining_passengers, int remaining_blackboxes, int current_capacity){
            this.i=i;
            this.j=j;
            this.remaining_passengers = remaining_passengers;
            this.remaining_blackboxes = remaining_blackboxes;
            this.current_capacity = current_capacity;
        }
    }
//    CGNode parent;
    State state;     //  N1    N2    N3
//    String operator; // left, right, up, down, pickup, retrieve, drop

    public CGNode(int i, int j, int remaining_passengers,
                  int remaining_blackboxes, int current_space, String operator, CGNode parent){

        this.state= new State(i,j,remaining_passengers, remaining_blackboxes,current_space);
        this.operator = operator;
        this.parent = parent;
    }

    public boolean goalTest (){
        return (this.state.remaining_passengers==0 && this.state.remaining_blackboxes ==0
        && this.state.current_capacity == 0);
    }


}
