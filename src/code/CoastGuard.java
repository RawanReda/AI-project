package code;

import java.lang.reflect.Array;
import java.util.*;

public class CoastGuard extends GeneralSearchProblem {
    static StringBuilder grid_string;
    static GridCell[][] grid;
    static HashMap<Integer, HashSet<Integer>> locations_occupied; // i, j
    static int cg_i;
    static int cg_j;
    static int capacity;
    static int total_passengers;
    static int saved_passengers;

    public static void addObserver(HashMap<String, Ship> observers, Ship ship) {
        observers.put(ship.i+","+ship.j, ship);
    }

    public static String genGrid() {
        grid_string = new StringBuilder();
        locations_occupied = new HashMap<>();

        int m = (int) (Math.random() * 10 + 1) + 5; //0-(m-1)
        int n = (int) (Math.random() * 10 + 1) + 5; //0-(n-1)
        capacity = (int) (Math.random() * 70 + 1) + 30;
        cg_i = (int) (Math.random() * n); //row
        cg_j = (int) (Math.random() * m); //col

        grid_string.append(m + "," + n + ";" + capacity + ";" + cg_i + "," + cg_j + ";");

        System.out.println("col:" + m + " row:" + n + " cg_i:" + cg_i + " cg_c:" + cg_j);

        // save location of the coast gaurd in the hashmap
        HashSet<Integer> cg_col = new HashSet<>();
        cg_col.add(cg_j);
        locations_occupied.put(cg_i, cg_col);

        int rem_cells = (m * n) - 1;

        System.out.println("rem cell  " + rem_cells);
        int n_stations = (int) (Math.random() * rem_cells) + 1;
        rem_cells -= n_stations;
        int n_ships = (int) (Math.random() * rem_cells) + 1;
        rem_cells -= n_ships;

        System.out.println(n_ships + " ss " + n_stations);
        occupyPositions("I", n_stations);
        occupyPositions("S", n_ships);

        return grid_string.toString();
    }

    public static void printGrid(GridCell[][] grid, Node node) {
        cg_i = node.state.i;
        cg_j = node.state.j;

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

    }

//    public static void notifyObservers(List<Ship> observers, Node node) {
//        for (int i = 0; i < observers.size(); i++) {
//            observers.get(i).update(node);
//        }
//    }

    public static void occupyPositions(String type, int count) {
        int m = grid[0].length; // number of columns
        int n = grid.length; // number of rows
        for (int i = 0; i < count; i++) {

            while (true) {
                int col = (int) (Math.random() * m);
                int row = (int) (Math.random() * n);
                if (locations_occupied.containsKey(row)) {
                    HashSet<Integer> cols = locations_occupied.get(row);
                    if (cols.contains(col)) {
                        continue;
                    }
                    cols.add(col);
                    locations_occupied.put(row, cols);
                } else {
                    HashSet<Integer> cols = new HashSet<>();
                    cols.add(col);
                    locations_occupied.put(row, cols);
                }
                if (type.equals("S")) {
                    grid_string.append(row + "," + col + "," + (int) ((Math.random() * 100) + 1));
                } else {
                    grid_string.append(row + "," + col);
                }
                if (i < count - 1)
                    grid_string.append(",");
                break;
            }

        }
        grid_string.append(";");

    }

//    public static boolean isRedundantState(Node n1, ArrayList<Node> expanded) {
//        if (expanded.isEmpty())
//            return false;
//        for (int i = 0; i < expanded.size(); i++) {
//            Node pre_node = expanded.get(i);
//            if (n1.state.i == pre_node.state.i && n1.state.j == pre_node.state.j && !n1.operator.equals("pickup") && !n1.operator.equals("retrieve") && !n1.operator.equals("drop")
//                    && n1.state.remaining_capacity == pre_node.state.remaining_capacity && n1.state.rescued_passengers == pre_node.state.rescued_passengers)
//                return true;
//        }
//        return false;
//    }

    public static String solve(String g, String strategy, Boolean visualise) {
        HashMap<String, Ship> observers = new HashMap<>();
        String[] grid_info = g.split(";");
        total_passengers = 0;
        saved_passengers = 0;
        int m = Integer.parseInt(grid_info[0].split(",")[0]);
        int n = Integer.parseInt(grid_info[0].split(",")[1]);
        capacity = Integer.parseInt(grid_info[1]);
        cg_i = Integer.parseInt(grid_info[2].split(",")[0]);
        cg_j = Integer.parseInt(grid_info[2].split(",")[1]);

        grid = new GridCell[n][m];

        // Stations
        String[] station_location = grid_info[3].split(",");
        for (int i = 0; i < station_location.length; i += 2) {
            int x = Integer.parseInt(station_location[i]);
            int y = Integer.parseInt(station_location[i + 1]);
            Station station = new Station(x, y);
            grid[x][y] = station;
        }
        // Ships
        String[] ship_location = grid_info[4].split(",");
        for (int i = 0; i < ship_location.length; i += 3) {
            int x = Integer.parseInt(ship_location[i]);
            int y = Integer.parseInt(ship_location[i + 1]);
            int c = Integer.parseInt(ship_location[i + 2]);
            total_passengers += c;
            Ship ship = new Ship(c, x, y);
            addObserver(observers, ship);
            grid[x][y] = ship;
        }

        Node initial_state = new Node(cg_i, cg_j, total_passengers,
                ship_location.length / 3, capacity, 0, null, null, observers,0, 0);
        printGrid((grid), initial_state);
        String[] operators = {"left", "up", "right", "down", "retrieve", "pickup", "drop"};

        String res = "";
        Node goal = null;
        if (strategy.equals("DF")) {
            goal = GeneralSearch(initial_state, operators, "DF", observers);
            String result = "Remaining BB " + goal.state.remaining_blackboxes + "Remaining Pass " + goal.state.remaining_passengers;
            int deaths= total_passengers-goal.state.rescued_passengers;
            res = printPath(goal) + ";" + goal.state.deaths + ";" +goal.state.retrieved_boxes+";"+ goal.state.depth; // number of deaths, number of retrieved boxes?

            System.out.println("result for DFS "+res);
        } else if (strategy.equals("BF")) {
            res = BFS(grid, initial_state, capacity, total_passengers);
        }


        return res;
    }

    public static Node GeneralSearch(Node initial_state, String[] operators, String strategy, HashMap<String, Ship> observers) {

        QueueLIFO queue = new QueueLIFO();
        ArrayList<Node> expanded = new ArrayList<Node>();
        queue.add(initial_state);
        while (!queue.isEmpty()) {
            System.out.println("---------------------------------------------------");
            Node Node = queue.remove();
            if (isRedundantState(Node, expanded)) {
                continue;
            }
            expanded.add(Node);
//            System.out.println("Rescued People In Stations: " + saved_passengers);
//            System.out.println("# of Passengers on coast guard: " + (capacity - Node.state.remaining_capacity));
            if (!(Node.equals(initial_state))) {
//                System.out.println(Node.operator);
                Node.state.observers = notifyObservers(Node.state.observers,Node);
//                System.out.println("i: " + Node.state.i + "j: " + Node.state.j);
//                printGrid(grid, Node);
            }
            if (Node.operator != null && Node.operator.equals("drop")) {
                int num_saved = capacity - Node.state.remaining_capacity;
                saved_passengers += num_saved;
                Node.state.remaining_passengers -= num_saved;
                Node.state.remaining_capacity = capacity;

            }
            if (Node.goalTest(capacity)) {
                int total_deaths = 0;
//                for (int i = 0; i < observers.size(); i++) {
//                    Ship ship = (Ship) observers.get(i);
//                    total_deaths += ship.deaths;
//                }
                System.out.println("############### DONE ##################");
                System.out.println("Number of people rescued: " + Node.state.rescued_passengers);
                System.out.println("Number of deaths: " + total_deaths);
                System.out.println("Number of expanded nodes (depth): " + expanded.size());
                return Node;
            } else {
                if (strategy.equals("DF")) {
                    queue = DFS(queue, Node, operators);
                }
            }
        }
        return null;
    }
    public static QueueLIFO DFS(QueueLIFO queue, Node node, String[] operators) {
        String pre_Operator = node.operator;
        int iPosition = node.state.i;
        int jPosition = node.state.j;
        GridCell gridcell = grid[iPosition][jPosition];


//        for(int k=0; k<node.state.observers.size(); k++){
//            Ship s= node.state.observers.get(k);
//            System.out.println("ship "+k+": "+s.deaths);
//        }
printNode(node);
        if (jPosition > 0) { // left
            Node left = new Node(iPosition, jPosition - 1, node.state.remaining_passengers, node.state.remaining_blackboxes,
                    node.state.remaining_capacity, node.state.rescued_passengers, "left", node, node.state.observers, node.state.retrieved_boxes, node.state.depth+1);
            queue.add(left);
        }
        if (jPosition < grid[0].length - 1) { // right
            Node right = new Node(iPosition, jPosition + 1, node.state.remaining_passengers, node.state.remaining_blackboxes,
                    node.state.remaining_capacity, node.state.rescued_passengers, "right", node, node.state.observers, node.state.retrieved_boxes, node.state.depth+1);
            queue.add(right);
        }
        if (iPosition > 0) { //up
            Node up = new Node(iPosition - 1, jPosition, node.state.remaining_passengers, node.state.remaining_blackboxes,
                    node.state.remaining_capacity, node.state.rescued_passengers, "up", node, node.state.observers, node.state.retrieved_boxes, node.state.depth+1);
            queue.add(up);
        }
        if (iPosition < grid.length - 1) { //down
            Node down = new Node(iPosition + 1, jPosition, node.state.remaining_passengers, node.state.remaining_blackboxes,
                    node.state.remaining_capacity, node.state.rescued_passengers, "down", node, node.state.observers, node.state.retrieved_boxes, node.state.depth+1);
            queue.add(down);
        }
        if (gridcell != null && gridcell instanceof Ship) {
            Ship ship = (Ship) node.state.observers.get(iPosition+","+jPosition);
            if (ship.passengers > 0 && node.state.remaining_capacity > 0) {
                Node pickup = new Node(iPosition, jPosition, node.state.remaining_passengers, node.state.remaining_blackboxes,
                        node.state.remaining_capacity, node.state.rescued_passengers, "pickup", node, node.state.observers, node.state.retrieved_boxes, node.state.depth+1);
                queue.add(pickup);
            }
        }
        if (gridcell != null && gridcell instanceof Ship) {
            Ship ship = (Ship) node.state.observers.get(iPosition+","+jPosition);
            if (ship.wrecked && ship.black_box <20 && !ship.done) {
                Node retrieve = new Node(iPosition, jPosition, node.state.remaining_passengers, node.state.remaining_blackboxes,
                        node.state.remaining_capacity, node.state.rescued_passengers, "retrieve", node, node.state.observers, node.state.retrieved_boxes, node.state.depth+1);
                queue.add(retrieve);
            }
        }
        if (gridcell != null && gridcell instanceof Station && node.state.remaining_capacity < capacity) {
            Node drop = new Node(iPosition, jPosition, node.state.remaining_passengers, node.state.remaining_blackboxes,
                    node.state.remaining_capacity, node.state.rescued_passengers, "drop", node, node.state.observers, node.state.retrieved_boxes, node.state.depth+1);
            queue.add(drop);
        }

        return queue;
    }

    public static void main(String[] args) {
        String grid0 = "5,6;50;0,1;0,4,3,3;1,1,90;";
        solve(grid0, "DF", true);

    }


}
