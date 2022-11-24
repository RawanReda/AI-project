package code;

import java.util.*;
//(int i, int j, int remaining_passengers, int remaining_blackboxes, int current_space, String operator,  Node parent)
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

    public static void notifyObservers(List<Ship> observers, Node node){
        for(int i=0; i< observers.size(); i++){
            observers.get(i).update(node);
        }
    }
    public static void printGrid( GridCell[][] grid, Node node){
        int cg_i = node.state.i;
        int cg_j = node.state.j;
        System.out.println("operator: "+ node.operator);
        System.out.println(grid.length+" "+ grid[0].length);
        for(int i=0; i<grid.length; i++){
            for(int j=0; j<grid[i].length; j++){

                GridCell curr= grid[i][j];
                if(curr!=null && curr instanceof Station){
                    System.out.print(i+" "+j+"  "+ "ST     |  ");

                }
                else if(curr!=null && curr instanceof Ship){
                    System.out.print(i+" "+j+" "+" D:" +((Ship) curr).deaths + "  P: " + ((Ship) curr).passengers + "   | " );

                }
                else if( i== cg_i && j==cg_j)
                    System.out.print(i+" "+j+" "+"CG" +"   |   ");
                else
                    System.out.print(i+" "+j+" "+"empty" +"   |   ");
            }
System.out.println();
        }
        System.out.println("......................................................................");
    }
    public static boolean isRedundantState(Node n1, ArrayList<Node> expanded){
        if(expanded.isEmpty())
            return false;
        for( int i =0; i<expanded.size(); i++){
            Node pre_node = expanded.get(i);
            if (n1.state.i == pre_node.state.i && n1.state.j == pre_node.state.j && !n1.operator.equals("pickup") && !n1.operator.equals("retrieve") && !n1.operator.equals("drop")
                    && n1.state.remaining_capacity == pre_node.state.remaining_capacity && n1.state.rescued_passengers == pre_node.state.rescued_passengers)
                return true;
        }
        return false;
    }
    // right, left, up, down

    public static String BFS(GridCell[][] grid ,Node cg,int capacity, int totalPassengers, ArrayList<Ship> observers) {
        Node pass_goal=null;
        int count_nodes_expanded=0;
        Queue<Node> q = new LinkedList<>();
        ArrayList<Node> expanded = new ArrayList<Node>();
        q.add(cg);

        while (!q.isEmpty()) {
            cg= q.poll();

            if (isRedundantState(cg, expanded))
                continue;
            count_nodes_expanded++;
            printGrid(grid, cg);
            expanded.add(cg);
            if(cg.operator!=null)
                notifyObservers(observers, cg);

            int cg_i = cg.state.i;
            int cg_j = cg.state.j;

            if (cg.goalTest(capacity)) {
                pass_goal= cg;
                String result= printPath(pass_goal)+";"+count_nodes_expanded; // number of deaths, number of retrieved boxes?
                System.out.println("res returned "+result);
                return result;
            }

            if (cg_i < grid.length - 1) {
                q.add(new Node(cg_i + 1, cg_j,
                        cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                        cg.state.remaining_capacity,cg.state.rescued_passengers, "down", cg.parent));
            }
            if (cg_i > 0) {
                q.add(new Node(cg_i - 1, cg_j,
                        cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                        cg.state.remaining_capacity,cg.state.rescued_passengers ,"up", cg.parent));
            }
            if (cg_j < grid[0].length - 1) {
                q.add(new Node(cg_i, cg_j + 1,
                        cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                        cg.state.remaining_capacity,cg.state.rescued_passengers ,"right", cg.parent));
            }
            if (cg_j > 0) {
                q.add(new Node(cg_i, cg_j - 1,
                        cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                        cg.state.remaining_capacity,cg.state.rescued_passengers,"left", cg.parent));
            }

            if(grid[cg_i][cg_j]!=null){
            if(grid[cg_i][cg_j] instanceof Ship){
                Ship ship= (Ship) grid[cg_i][cg_j];
                int remaining_capacity= cg.state.remaining_capacity;
                if(!ship.wrecked && remaining_capacity>0){
                    int passengers_carried= Math.min(remaining_capacity, ship.passengers);
                    ship.passengers-=passengers_carried;
                    q.add(new Node(cg_i, cg_j,
                            cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                            cg.state.remaining_capacity-passengers_carried, cg.state.rescued_passengers, "pickup", cg.parent));

                }
                else if (ship.wrecked && ship.black_box==0){
                    ship.black_box=1;
                    q.add(new Node(cg_i, cg_j,
                            cg.state.remaining_passengers, cg.state.remaining_blackboxes-1,
                            cg.state.remaining_capacity,cg.state.rescued_passengers,  "retrieve", cg.parent));
                }
                grid[cg_i][cg_j]=ship;
            }

            if(grid[cg_i][cg_j] instanceof Station && cg.state.remaining_passengers<capacity){
                Station ship= (Station) grid[cg_i][cg_j];

                   int passengers_dropped_off= capacity- cg.state.remaining_passengers;
                   q.add(new Node(cg_i, cg_j,
                           cg.state.remaining_passengers-passengers_dropped_off, cg.state.remaining_blackboxes,
                           capacity, cg.state.rescued_passengers+passengers_dropped_off, "drop", cg.parent));
            }
        }
        }

        return "";
    }


    public static String printPath(Node cg){
        StringBuilder path=new StringBuilder(cg.operator);
        cg=cg.parent;
        while(cg!=null){
            path.insert(0,cg.operator+",");
            cg= cg.parent;
        }
        return "";
    }
    }

