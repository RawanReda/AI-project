package code;

import java.lang.reflect.Array;
import java.util.*;

public class GeneralSearchProblem {
    static String final_visualized_path = "";


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
        HashMap<String, Ship> ships_list = new HashMap<>();


        for (String position : observers.keySet()) {
            Ship ship = (Ship) observers.get(position).clone();
            ship.update(node);
            ships_list.put(ship.i + "," + ship.j, ship);
            node.state.deaths += ship.deaths;

        }
        return ships_list;
    }

    public static boolean isRedundant(Node node, HashSet<String> expanded) {
        if (node.operator != null && !node.operator.equals("pickup") && !node.operator.equals("drop") && !node.operator.equals("retrieve")) {
            String state = node.state.i + "," + node.state.j + "," + node.state.remaining_capacity + "," + node.state.rescued_passengers;
            if (expanded.contains(state)) return true;
            expanded.add(state);
        }

        return false;
    }

    public static String printNode(Node cg) {

        StringBuilder node_info = new StringBuilder();
        node_info.append("i,j :" + cg.state.i + " " + cg.state.j + '\n');
        if (cg.operator != null) node_info.append("operator: " + cg.operator + '\n');
        node_info.append("parent i,j and operator: " + ((cg.parent != null) ? cg.parent.state.i + " " + cg.parent.state.j + " ," + cg.parent.operator : "no parent") + '\n');
        node_info.append("remaining capacity: " + cg.state.remaining_capacity + '\n');
        node_info.append("remaining blackboxes: " + cg.state.remaining_blackboxes + '\n');
        node_info.append("remaining passengers: " + cg.state.remaining_passengers + '\n');
        node_info.append("rescued passengers: " + cg.state.rescued_passengers + '\n');
        node_info.append("retrieved boxes: " + cg.state.retrieved_boxes + '\n');
        node_info.append("number of deaths: " + cg.state.deaths + '\n');
        node_info.append("depth: " + cg.state.depth + '\n');
        node_info.append("number of ships: " + cg.state.observers.size() + '\n');

        return node_info.toString();
    }

    public static String centerString(int width, String s) {
        return String.format("%-" + width + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }

    public static String visualize(GridCell[][] grid, Node node) {
        int cg_i = node.state.i;
        int cg_j = node.state.j;

        String rows = "";
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                String cell = "";
                GridCell curr = grid[i][j];
                if (curr != null && curr instanceof Station) {
                    cell = i + " " + j + " " + "ST";

                } else if (curr != null && curr instanceof Ship) {
                    Ship ship = node.state.observers.get(i + "," + j);
                    cell = i + " " + j + " " + " D:" + ship.deaths + " P:" + ship.passengers + " BB:" + ship.black_box;

                } else cell = i + " " + j + " " + "empty";

                if (cg_i == i && cg_j == j) cell += " CG";
                rows += centerString(20, cell) + "|";

            }
            rows += '\n';
            System.out.println();
        }
        return rows;
    }

    public static void expand(GridCell[][] grid, Node cg, Queue q, int capacity) {

        int cg_i = cg.state.i;
        int cg_j = cg.state.j;


        if (cg_i < grid.length - 1) {
            q.add(new Node(cg_i + 1, cg_j, cg.state.remaining_passengers, cg.state.remaining_blackboxes, cg.state.remaining_capacity, cg.state.rescued_passengers, "down", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth + 1));
        }
        if (cg_i > 0) {
            q.add(new Node(cg_i - 1, cg_j, cg.state.remaining_passengers, cg.state.remaining_blackboxes, cg.state.remaining_capacity, cg.state.rescued_passengers, "up", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth + 1));
        }
        if (cg_j < grid[0].length - 1) {
            q.add(new Node(cg_i, cg_j + 1, cg.state.remaining_passengers, cg.state.remaining_blackboxes, cg.state.remaining_capacity, cg.state.rescued_passengers, "right", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth + 1));
        }
        if (cg_j > 0) {
            q.add(new Node(cg_i, cg_j - 1, cg.state.remaining_passengers, cg.state.remaining_blackboxes, cg.state.remaining_capacity, cg.state.rescued_passengers, "left", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth + 1));
        }
        if (grid[cg_i][cg_j] != null) {
            if (cg.state.observers.containsKey(cg_i + "," + cg_j)) {
                Ship ship = cg.state.observers.get(cg_i + "," + cg_j);
                int remaining_capacity = cg.state.remaining_capacity;
                if (!ship.wrecked && remaining_capacity > 0) {

                    q.add(new Node(cg_i, cg_j, cg.state.remaining_passengers, cg.state.remaining_blackboxes, cg.state.remaining_capacity, cg.state.rescued_passengers, "pickup", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth + 1));

                } else if (ship.wrecked && !ship.done) {

                    q.add(new Node(cg_i, cg_j, cg.state.remaining_passengers, cg.state.remaining_blackboxes, cg.state.remaining_capacity, cg.state.rescued_passengers, "retrieve", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth + 1));
                }

            }

            if (grid[cg_i][cg_j] instanceof Station && cg.state.remaining_capacity < capacity) {
                Station ship = (Station) grid[cg_i][cg_j];

                int passengers_dropped_off = capacity - cg.state.remaining_capacity;
                q.add(new Node(cg_i, cg_j, cg.state.remaining_passengers - passengers_dropped_off, cg.state.remaining_blackboxes, capacity, cg.state.rescued_passengers + passengers_dropped_off, "drop", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth + 1));
            }
        }

    }

    public static String BFS(GridCell[][] grid, Node cg, int capacity, int totalPassengers) {
        Node pass_goal = null;
        Queue<Node> q = new LinkedList<>();
        HashSet<String> expanded = new HashSet<>();
        q.add(cg);

        while (!q.isEmpty()) {
            cg = q.poll();

            if (isRedundant(cg, expanded) && q.size() > 1) {
                continue;
            }
            if (cg.operator != null) {
                cg.state.observers = notifyObservers(cg.state.observers, cg);
            }

            if (cg.goalTest(capacity)) {
                pass_goal = cg;
                String result = printPath(pass_goal, grid) + ";" + cg.state.deaths + ";" + cg.state.retrieved_boxes + ";" + expanded.size(); // number of deaths, number of retrieved boxes?
                return result;
            }

            expand(grid, cg, q, capacity);

        }
        return "There is no goal state";
    }

    public static String IDS(GridCell[][] grid, Node initial_state, int capacity, int totalPassengers) {
        int total_expanded_nodes = 0;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String result = "";

            HashSet<String> expanded = new HashSet<>();
            QueueLIFO q = new QueueLIFO();

            q.add(initial_state);

            while (!q.isEmpty()) {
                Node node = q.remove();
                if (node.operator != null && isRedundant(node, expanded) && q.size() > 1) {
                    continue;
                }

                total_expanded_nodes++;

                if (node.operator != null) {
                    node.state.observers = notifyObservers(node.state.observers, node);

                    if (node.operator.equals("drop")) {
                        int num_saved = capacity - node.state.remaining_capacity;
                        node.state.remaining_passengers -= num_saved;
                        node.state.remaining_capacity = capacity;
                        node.state.rescued_passengers += num_saved;

                    }
                }

                if (node.goalTest(capacity)) {
                    result = printPath(node, grid) + ";" + node.state.deaths + ";" + node.state.retrieved_boxes + ";" + total_expanded_nodes;
                    return result;
                }
                if (node.state.depth < i) q = DFS(grid, q, node, capacity);

            }
            if (!result.equals("")) return result;
        }
        return "There is no goal state";
    }

    public static QueueLIFO DFS(GridCell[][] grid, QueueLIFO queue, Node node, int capacity) {
        int iPosition = node.state.i;
        int jPosition = node.state.j;
        GridCell gridcell = grid[iPosition][jPosition];
        if (jPosition > 0) { // left
            Node left = new Node(iPosition, jPosition - 1, node.state.remaining_passengers, node.state.remaining_blackboxes, node.state.remaining_capacity, node.state.rescued_passengers, "left", node, node.state.observers, node.state.retrieved_boxes, node.state.depth + 1);
            queue.add(left);
        }
        if (jPosition < grid[0].length - 1) { // right
            Node right = new Node(iPosition, jPosition + 1, node.state.remaining_passengers, node.state.remaining_blackboxes, node.state.remaining_capacity, node.state.rescued_passengers, "right", node, node.state.observers, node.state.retrieved_boxes, node.state.depth + 1);
            queue.add(right);
        }
        if (iPosition > 0) { //up
            Node up = new Node(iPosition - 1, jPosition, node.state.remaining_passengers, node.state.remaining_blackboxes, node.state.remaining_capacity, node.state.rescued_passengers, "up", node, node.state.observers, node.state.retrieved_boxes, node.state.depth + 1);
            queue.add(up);
        }
        if (iPosition < grid.length - 1) { //down
            Node down = new Node(iPosition + 1, jPosition, node.state.remaining_passengers, node.state.remaining_blackboxes, node.state.remaining_capacity, node.state.rescued_passengers, "down", node, node.state.observers, node.state.retrieved_boxes, node.state.depth + 1);
            queue.add(down);
        }
        if (gridcell != null && gridcell instanceof Ship) {
            Ship ship = (Ship) node.state.observers.get(iPosition + "," + jPosition);
            if (ship.passengers > 0 && node.state.remaining_capacity > 0) {
                Node pickup = new Node(iPosition, jPosition, node.state.remaining_passengers, node.state.remaining_blackboxes, node.state.remaining_capacity, node.state.rescued_passengers, "pickup", node, node.state.observers, node.state.retrieved_boxes, node.state.depth + 1);
                queue.add(pickup);
            }
        }
        if (gridcell != null && gridcell instanceof Ship) {
            Ship ship = (Ship) node.state.observers.get(iPosition + "," + jPosition);
            if (ship.wrecked && ship.black_box < 20 && !ship.done) {
                Node retrieve = new Node(iPosition, jPosition, node.state.remaining_passengers, node.state.remaining_blackboxes, node.state.remaining_capacity, node.state.rescued_passengers, "retrieve", node, node.state.observers, node.state.retrieved_boxes, node.state.depth + 1);
                queue.add(retrieve);
            }
        }
        if (gridcell != null && gridcell instanceof Station && node.state.remaining_capacity < capacity) {
            Node drop = new Node(iPosition, jPosition, node.state.remaining_passengers, node.state.remaining_blackboxes, node.state.remaining_capacity, node.state.rescued_passengers, "drop", node, node.state.observers, node.state.retrieved_boxes, node.state.depth + 1);
            queue.add(drop);
        }

        return queue;
    }

    public static String expand_IS(GridCell[][] grid, Node initial_state, int capacity, String strategy) {
        PriorityQueue<Node> q = new PriorityQueue<>();
        HashSet<String> expanded = new HashSet<>();
        q.add(initial_state);

        while (!q.isEmpty()) {
            Node node = q.poll();

            if (isRedundant(node, expanded) && q.size() > 1) {
                continue;
            }

            if (node.operator != null) {
                node.state.observers = notifyObservers(node.state.observers, node);
            }
            if (node.goalTest(capacity)) {
                String result = printPath(node, grid) + ";" + node.state.deaths + ";" + node.state.retrieved_boxes + ";" + expanded.size(); // number of deaths, number of retrieved boxes?
                return result;
            }


            if (strategy.equals("UC")) {
                expand(grid, node, q, capacity, 0);
            } else if (strategy.equals("GR1")) {
                expand(grid, node, q, capacity, 1);
            } else if (strategy.equals("GR2")) {

                expand(grid, node, q, capacity, 2);
            } else if (strategy.equals("AS1")) {
                expand(grid, node, q, capacity, 3);
            } else if (strategy.equals("AS2")) {
                expand(grid, node, q, capacity, 4);
            }

        }
        return "There is no goal state";
    }

    public static void calculateNodeVal(Node n, int method_type) {
        if (method_type == 0) {
            n.state.setValue(assignCost(n));
        } else if (method_type == 1) {
            n.state.setValue(assignHeuristic1(n)); // ?
        } else if (method_type == 2) {
            n.state.setValue(assignHeuristic2(n));
        } else if (method_type == 3) {
            n.state.setValue(assignA_star1(n));
        } else if (method_type == 4) {
            n.state.setValue(assignA_star2(n));
        }
    }

    public static void expand(GridCell[][] grid, Node cg, PriorityQueue q, int capacity, int method_type) {

        if (cg.operator == null) calculateNodeVal(cg, method_type);
        int cg_i = cg.state.i;
        int cg_j = cg.state.j;
        if (cg_i < grid.length - 1) {
            Node n = new Node(cg_i + 1, cg_j, cg.state.remaining_passengers, cg.state.remaining_blackboxes, cg.state.remaining_capacity, cg.state.rescued_passengers, "down", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth + 1);
            calculateNodeVal(n, method_type);
            q.add(n);
        }
        if (cg_i > 0) {
            Node n = new Node(cg_i - 1, cg_j, cg.state.remaining_passengers, cg.state.remaining_blackboxes, cg.state.remaining_capacity, cg.state.rescued_passengers, "up", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth + 1);
            calculateNodeVal(n, method_type);
            q.add(n);
        }
        if (cg_j < grid[0].length - 1) {
            Node n = new Node(cg_i, cg_j + 1, cg.state.remaining_passengers, cg.state.remaining_blackboxes, cg.state.remaining_capacity, cg.state.rescued_passengers, "right", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth + 1);
            calculateNodeVal(n, method_type);
            q.add(n);
        }
        if (cg_j > 0) {
            Node n = new Node(cg_i, cg_j - 1, cg.state.remaining_passengers, cg.state.remaining_blackboxes, cg.state.remaining_capacity, cg.state.rescued_passengers, "left", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth + 1);
            calculateNodeVal(n, method_type);
            q.add(n);
        }

        if (grid[cg_i][cg_j] != null) {
            if (cg.state.observers.containsKey(cg_i + "," + cg_j)) {
                Ship ship = cg.state.observers.get(cg_i + "," + cg_j);
                int remaining_capacity = cg.state.remaining_capacity;
                if (!ship.wrecked && remaining_capacity > 0) {
                    Node n = new Node(cg_i, cg_j, cg.state.remaining_passengers, cg.state.remaining_blackboxes, cg.state.remaining_capacity, cg.state.rescued_passengers, "pickup", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth + 1);
                    calculateNodeVal(n, method_type);
                    q.add(n);
                } else if (ship.wrecked && !ship.done) {
                    Node n = new Node(cg_i, cg_j, cg.state.remaining_passengers, cg.state.remaining_blackboxes, cg.state.remaining_capacity, cg.state.rescued_passengers, "retrieve", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth + 1);
                    calculateNodeVal(n, method_type);
                    q.add(n);
                }
            }

            if (grid[cg_i][cg_j] instanceof Station && cg.state.remaining_capacity < capacity) {
                Station ship = (Station) grid[cg_i][cg_j];

                int passengers_dropped_off = capacity - cg.state.remaining_capacity;
                Node n = new Node(cg_i, cg_j, cg.state.remaining_passengers - passengers_dropped_off, cg.state.remaining_blackboxes, capacity, cg.state.rescued_passengers + passengers_dropped_off, "drop", cg, cg.state.observers, cg.state.retrieved_boxes, cg.state.depth);
                calculateNodeVal(n, method_type);
                q.add(n);
            }
        }
    }

    public static double assignA_star2(Node node) {
        return assignCost(node) + assignHeuristic2(node);
    }

    public static double assignA_star1(Node node) {
        return assignCost(node) + assignHeuristic1(node);
    }

    public static int assignCost(Node node) {
        int num_bb_lost = 0;
        int num_deaths = 0;
        HashMap<String, Ship> ships = node.state.observers;
        for (String key : ships.keySet()) {
            Ship ship = ships.get(key);
            if (ship.black_box >= 20) num_bb_lost++;
            num_deaths += ship.deaths;
        }
        return 2 * num_deaths + num_bb_lost;
    }

    public static double assignHeuristic2(Node node) {

        int cg_i = node.state.i;
        int cg_j = node.state.j;
        HashMap<String, Ship> ships = node.state.observers;
        int closestShip_CB = Integer.MAX_VALUE;
        int closestShip_passengers = Integer.MAX_VALUE;
        for (String key : ships.keySet()) {
            String[] location = key.split(",");
            int distance = Math.abs(cg_i - Integer.parseInt(location[0])) + Math.abs(cg_j - Integer.parseInt(location[1]));
            if (distance < closestShip_CB && distance < ships.get(key).passengers) {
                closestShip_CB = distance;
                closestShip_passengers = ships.get(key).passengers;
            }
        }
        double h = ((double) 1 / (1 + (closestShip_passengers - closestShip_CB)));

        if (closestShip_passengers == Integer.MAX_VALUE) h = 0;
        return h;
    }

    public static int assignHeuristic1(Node node) {
        int h;
        int cg_i = node.state.i;
        int cg_j = node.state.j;

        HashMap<String, Ship> ships = node.state.observers;
        HashMap<String, Ship> noneVisitedShips = (HashMap<String, Ship>) node.state.observers.clone();
        int total_ships_CB = 0;
        int total_BB_CB = 0;


        for (String key : ships.keySet()) {
            int distance = 0;
            int distanceBB = 0;
            String chosenKey = "";
            String chosenKey_BB = "";
            int sh_i = -1;
            int sh_j = -1;
            int bb_i = -1;
            int bb_j = -1;
            int closestShip_CB = Integer.MAX_VALUE;
            int closestShip_CB_BB = Integer.MAX_VALUE;
            for (String visitedKey : noneVisitedShips.keySet()) {
                String[] location = visitedKey.split(",");
                distance = Math.abs(cg_i - Integer.parseInt(location[0])) + Math.abs(cg_j - Integer.parseInt(location[1]));
                if (!ships.get(visitedKey).wrecked && (ships.get(visitedKey).passengers - distance) > 0) {
                    if (distance < closestShip_CB) {
                        closestShip_CB = distance;
                        sh_i = Integer.parseInt(location[0]);
                        sh_j = Integer.parseInt(location[1]);
                        chosenKey = visitedKey;
                    }

                } else if (ships.get(visitedKey).wrecked && !ships.get(visitedKey).done && ((ships.get(visitedKey).expiry_date - ships.get(visitedKey).black_box) - distance) > 0) {
                    distanceBB = Math.abs(cg_i - Integer.parseInt(location[0])) + Math.abs(cg_j - Integer.parseInt(location[1]));

                    if (distanceBB < closestShip_CB_BB) {
                        closestShip_CB_BB = distanceBB;
                        bb_i = Integer.parseInt(location[0]);
                        bb_j = Integer.parseInt(location[1]);
                        chosenKey_BB = visitedKey;
                    }
                }
            }

            if (sh_i != -1) {
                cg_i = sh_i;
                cg_j = sh_j;
                noneVisitedShips.remove(chosenKey);
            } else if (bb_i != -1) {
                cg_i = bb_i;
                cg_j = bb_j;
                noneVisitedShips.remove(chosenKey_BB);
            }

            if (bb_i == -1 && sh_i == -1) break;

            if (closestShip_CB_BB < Integer.MAX_VALUE) {
                total_BB_CB += closestShip_CB_BB;
            }
            if (closestShip_CB < Integer.MAX_VALUE) {
                total_ships_CB += closestShip_CB;
            }
        }

        h = (2 * total_ships_CB) + total_BB_CB;
        return h;
    }

    public static String printPath(Node cg, GridCell[][] grid) {
        StringBuilder visualized_path = new StringBuilder();
        StringBuilder path = new StringBuilder();
        while (cg.operator != null) {
            visualized_path.insert(0, printNode(cg) + "\n");
            visualized_path.insert(0, visualize(grid, cg) + "\n");
            visualized_path.insert(0, "---------------------------------------------------------------------------------------------------------------------------------------------------------------------------" + '\n');
            path.insert(0, cg.operator + ",");
            cg = cg.parent;
        }
        visualized_path.insert(0, printNode(cg) + "\n");
        visualized_path.insert(0, visualize(grid, cg) + "\n");
        final_visualized_path = visualized_path.toString();
        return path.toString();
    }

    public static String getFinal_visualized_path() {
        return final_visualized_path;
    }
}