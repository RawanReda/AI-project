package code;

import java.lang.reflect.Array;
import java.util.*;
import java.util.ArrayList;

public class GeneralSearchProblem {

    static class QueueLIFO {
        private ArrayList<Node> myArray = new ArrayList<Node>();

        public void add(Node n) {
            myArray.add(0, n);
        }

        public Node remove() {
            Node n = myArray.get(0);
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

    public static HashMap<String, Ship> notifyObservers(HashMap<String, Ship> observers, Node node) {
        HashMap<String, Ship> ships_list= new HashMap<>();


        for (String position: observers.keySet()) {

          //  if(observers.get(i).deaths==0)
          //  System.out.println("ship notified deaths before :"+observers.get(i).passengers);
            Ship ship = (Ship) observers.get(position).clone();
            ship.update(node);
            ships_list.put(ship.i+","+ship.j, ship);
            node.state.deaths+= ship.deaths;
          //  if(observers.get(i).deaths==0)
          //  System.out.println("ship notified deaths after :"+ship.passengers);

        }
        return ships_list;
    }

    public static void printGrid(GridCell[][] grid, Node node) {
        int cg_i = node.state.i;
        int cg_j = node.state.j;
        System.out.println("operator: " + node.operator);
        System.out.println(grid.length + " " + grid[0].length);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {

                GridCell curr = grid[i][j];
                if (curr != null && curr instanceof Station) {
                    System.out.print(i + " " + j + "  " + "ST     |  ");

                } else if (curr != null && curr instanceof Ship) {
                    System.out.print(i + " " + j + " " + " D:" + ((Ship) curr).deaths + "  P: " + ((Ship) curr).passengers + "   | ");

                } else if (i == cg_i && j == cg_j)
                    System.out.print(i + " " + j + " " + "CG" + "   |   ");
                else
                    System.out.print(i + " " + j + " " + "empty" + "   |   ");
            }
            System.out.println();
        }
        System.out.println("......................................................................");
    }

    public static boolean isRedundantState(Node n1, ArrayList<Node> expanded) {
        if (expanded.isEmpty())
            return false;
        for (int i = 0; i < expanded.size(); i++) {
            Node pre_node = expanded.get(i);
//            if (n1.state.i == pre_node.state.i && n1.state.j == pre_node.state.j  && n1.operator.equals("retrieve") && pre_node.operator.equals("retrieve") ){
//                System.out.println("retrieve redundant"+n1.state.i+" "+n1.state.j);
//                            return true;}
            if (n1.state.i == pre_node.state.i && n1.state.j == pre_node.state.j && !n1.operator.equals("pickup") && !n1.operator.equals("retrieve") && !n1.operator.equals("drop")
                    && n1.state.remaining_capacity == pre_node.state.remaining_capacity && n1.state.rescued_passengers == pre_node.state.rescued_passengers && n1.state.remaining_blackboxes==pre_node.state.remaining_blackboxes)
                return true;
                  }
        return false;
    }
public static void printNode(Node cg){
        System.out.println("i,j :"+cg.state.i+" "+cg.state.j);
        System.out.println("operator: "+cg.operator);
        System.out.println("parent i,j and operator: "+ ((cg.parent!=null)?cg.parent.state.i + " "+cg.parent.state.j+" ,"+cg.parent.operator: "no parent"));
        System.out.println("remaining capacity: "+cg.state.remaining_capacity);
        System.out.println("remaining blackboxes: "+cg.state.remaining_blackboxes);
        System.out.println("remaining passengers: "+cg.state.remaining_passengers);
        System.out.println("rescued passengers: "+cg.state.rescued_passengers);
        System.out.println("retrieved boxes: "+cg.state.retrieved_boxes);
        System.out.println("depth: "+cg.state.depth);
        System.out.println("number of ships: "+cg.state.observers.size());
}
    // right, left, up, down
    public static int checkSaved(Node node, int full_capacity) {
        if (node.operator!=null && node.operator.equals("drop")) {
            return full_capacity - node.state.remaining_capacity;
        }
        return 0;
    }

    public static void expand(GridCell[][] grid, Node cg, Queue q, int capacity){

        int cg_i= cg.state.i;
        int cg_j= cg.state.j;
        if (cg_i < grid.length - 1) {
            q.add(new Node(cg_i + 1, cg_j,
                    cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                    cg.state.remaining_capacity, cg.state.rescued_passengers, "down", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth+1 ));
        }
        if (cg_i > 0) {
            q.add(new Node(cg_i - 1, cg_j,
                    cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                    cg.state.remaining_capacity, cg.state.rescued_passengers, "up", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth+1));
        }
        if (cg_j < grid[0].length - 1) {
            q.add(new Node(cg_i, cg_j + 1,
                    cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                    cg.state.remaining_capacity, cg.state.rescued_passengers, "right", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth+1));
        }
        if (cg_j > 0) {
            q.add(new Node(cg_i, cg_j - 1,
                    cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                    cg.state.remaining_capacity, cg.state.rescued_passengers, "left", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth+1));
        }

        if (grid[cg_i][cg_j] != null) {
            if(cg.state.observers.containsKey(cg_i+","+cg_j)){
//                if (grid[cg_i][cg_j] instanceof Ship) {
//                    Ship ship = (Ship) grid[cg_i][cg_j];
                Ship ship = cg.state.observers.get(cg_i+","+cg_j);
                int remaining_capacity = cg.state.remaining_capacity;
                if (!ship.wrecked && remaining_capacity > 0) {
//                        int passengers_carried = Math.min(remaining_capacity, ship.passengers);
//                        ship.passengers -= passengers_carried;
                    q.add(new Node(cg_i, cg_j,
                            cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                            cg.state.remaining_capacity , cg.state.rescued_passengers, "pickup", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth+1));

                } else if (ship.wrecked  && !ship.done) {

                    q.add(new Node(cg_i, cg_j,
                            cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                            cg.state.remaining_capacity, cg.state.rescued_passengers, "retrieve", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth+1));
                }

            }

            if (grid[cg_i][cg_j] instanceof Station && cg.state.remaining_capacity < capacity) {
                Station ship = (Station) grid[cg_i][cg_j];

                int passengers_dropped_off = capacity - cg.state.remaining_capacity;
                q.add(new Node(cg_i, cg_j,
                        cg.state.remaining_passengers - passengers_dropped_off, cg.state.remaining_blackboxes,
                        capacity, cg.state.rescued_passengers + passengers_dropped_off, "drop", cg, cg.state.observers,cg.state.retrieved_boxes, cg.state.depth+1));
            }
        }
    }

    public static String BFS(GridCell[][] grid, Node cg, int capacity, int totalPassengers) {
        Node pass_goal = null;
        int count_nodes_expanded = 0;
        int retrieved_blackboxes = 0;
        Queue<Node> q = new LinkedList<>();
        ArrayList<Node> expanded = new ArrayList<Node>();
        q.add(cg);

        while (!q.isEmpty()) {
            cg = q.poll();
          //  System.out.println("hello");

            if (isRedundantState(cg, expanded)) {

                System.out.println("redundant node ---------------");
                printNode(cg);
                System.out.println("end print statement of redundant node ---------------");
                continue;
            }
            expanded.add(cg);
            count_nodes_expanded++;
            printNode(cg);
            if (cg.operator != null) {
                cg.state.observers = notifyObservers(cg.state.observers, cg);
                totalPassengers -= checkSaved(cg, capacity); // remaining totalPassengers is the number of deaths that happened at each time step
                if (cg.operator.equals("retrieve")) retrieved_blackboxes++;
            }

            //printGrid(grid, cg);

            if (cg.goalTest(capacity)) {
                pass_goal = cg;
                String result = printPath(pass_goal) + ";" + cg.state.deaths+ ";" +cg.state.retrieved_boxes+";"+ count_nodes_expanded; // number of deaths, number of retrieved boxes?
                
                for(String x: cg.state.observers.keySet()){
                    System.out.println(cg.state.observers.get(x)+"    kk  "+cg.state.observers.get(x).done );
                }
                System.out.println("res returned " + result);
                return result;
            }

            expand(grid, cg,q,capacity);

            }


        return "There is no goal state";
    }


    public static String printPath(Node cg) {
        StringBuilder path = new StringBuilder(cg.operator);
        cg = cg.parent;
        while (cg.operator != null) {
            path.insert(0, cg.operator + ",");
            cg = cg.parent;
        }
        return path.toString();
    }
}

