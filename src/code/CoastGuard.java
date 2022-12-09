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
        grid= new GridCell[n][m];
        grid_string.append(m + "," + n + ";" + capacity + ";" + cg_i + "," + cg_j + ";");

        // save location of the coast gaurd in the hashmap
        HashSet<Integer> cg_col = new HashSet<>();
        cg_col.add(cg_j);
        locations_occupied.put(cg_i, cg_col);

        int rem_cells = (m * n) - 1;

        int n_stations = (int) (Math.random() * rem_cells) + 1;
        rem_cells -= n_stations;
        int n_ships = (int) (Math.random() * rem_cells) + 1;
        rem_cells -= n_ships;


        occupyPositions("I", n_stations);
        occupyPositions("S", n_ships);

        return grid_string.toString();
    }

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
                    grid_string.append(row + "," + col + "," + (int) ((Math.random() * 20) + 1));
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

        String res = "";
        Node goal = null;
        if (strategy.equals("DF")) {
            goal = GeneralSearch(initial_state, "DF", observers);
            res = printPath(goal,grid) + ";" + goal.state.deaths + ";" +goal.state.retrieved_boxes+";"+ goal.state.depth;

            System.out.println("result for DFS "+res);
        } else if (strategy.equals("BF")) {
            res = BFS(grid, initial_state, capacity, total_passengers);
        }
        else if (strategy.equals("ID")){
            res = IDS(grid, initial_state, capacity, total_passengers);
        }
        else {
            res = expand_IS(grid, initial_state, capacity,strategy);
        }

        if(visualise)
            System.out.println(getFinal_visualized_path());

        return res;
    }

    public static Node GeneralSearch(Node initial_state,  String strategy, HashMap<String, Ship> observers) {

        QueueLIFO queue = new QueueLIFO();
        HashSet<String> expanded= new HashSet<>();
        queue.add(initial_state);
        while (!queue.isEmpty()) {

            Node Node = queue.remove();
            if (isRedundant(Node, expanded) && queue.size()>1) {
                continue;
            }

            if (!(Node.equals(initial_state))) {
                Node.state.observers = notifyObservers(Node.state.observers,Node);
            }
            if (Node.operator != null && Node.operator.equals("drop")) {
                int num_saved = capacity - Node.state.remaining_capacity;
                saved_passengers += num_saved;
                Node.state.remaining_passengers -= num_saved;
                Node.state.remaining_capacity = capacity;

            }
            if (Node.goalTest(capacity)) {
                return Node;
            } else {
                if (strategy.equals("DF")) {
                    queue = DFS(grid, queue, Node, capacity);
                }
            }
        }
        return null;
    }


    public static void main(String[] args) {
        String grid0 = "7,5;100;3,4;2,6,3,5;0,0,4,0,1,8,1,4,77,1,5,1,3,2,94,4,3,46;";
        System.out.println(solve(grid0, "UC", false));
        System.out.println(solve(grid0, "AS1", false));
        System.out.println(solve(grid0, "AS2", false));
        System.out.println(solve(grid0, "GR1", false));
        System.out.println(solve(grid0, "GR2", false));
        System.out.println(solve(grid0, "BF", false));
        System.out.println(solve(grid0, "DF", false));

    }


}
