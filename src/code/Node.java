package code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Node {

    static class State{
        int i;
        int j;
        int remaining_passengers;
        int remaining_blackboxes;
        int remaining_capacity; // Number of passengers currently on CG
        int rescued_passengers;
        int retrieved_boxes;
        int depth;
        int deaths;
        HashMap<String, Ship> observers;

        public State(int i, int j, int remaining_passengers, int remaining_blackboxes, int current_capacity, int rescued_passengers,  HashMap<String, Ship> s, int retrieved_boxes, int depth){
            this.i=i;
            this.j=j;
            this.remaining_passengers = remaining_passengers;
            this.remaining_blackboxes = remaining_blackboxes;
            this.remaining_capacity = current_capacity;
            this.rescued_passengers = rescued_passengers;
            this.observers = s;
            this.retrieved_boxes=retrieved_boxes;
            this.depth=depth;
            this.deaths=0;
        }
    }


    Node parent;
    State state;     //  N1    N2    N3
    String operator; // left, right, up, down, pickup, retrieve, drop

    public Node(int i, int j, int remaining_passengers,
                int remaining_blackboxes, int current_space, int rescued_passengers, String operator, Node parent, HashMap<String,Ship> observers,int retrieved_boxes, int depth ){

        this.state= new State(i,j,remaining_passengers, remaining_blackboxes,current_space,rescued_passengers, observers,retrieved_boxes,depth);
        this.operator = operator;
        this.parent = parent;
    }

    public boolean goalTest (int full_capacity){
        System.out.println("Check Goal State");
        System.out.println("Remaining Passengers: " + this.state.remaining_passengers );
        System.out.println("Remaining BB:  +"+ this.state.remaining_blackboxes);
        System.out.println("Remaining Capacity" + this.state.remaining_capacity);
        return (this.state.remaining_passengers==0 && this.state.remaining_blackboxes ==0
        && this.state.remaining_capacity == full_capacity);
    }


}
