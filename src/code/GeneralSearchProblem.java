package code;

import java.util.LinkedList;
import java.util.Queue;
//(int i, int j, int remaining_passengers, int remaining_blackboxes, int current_space, String operator,  Node parent)
public class GeneralSearchProblem {
    // right, left, up, down

    public static void BFS(GridCell[][] grid ,Node cg,int capacity, int totalPassengers) {
        Node pass_goal=null;
        Queue<Node> q = new LinkedList<>();
        q.add(cg);

        while (!q.isEmpty()) {
            cg= q.poll();
            int cg_i = cg.state.i;
            int cg_j = cg.state.j;

            if (cg.goalTest()) {
                pass_goal= cg;
                break;
            }

            if (cg_i < grid.length - 1) {
                q.add(new Node(cg_i + 1, cg_j,
                        cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                        cg.state.current_capacity, "down", cg.parent));
            }
            if (cg_i > 0) {
                q.add(new Node(cg_i - 1, cg_j,
                        cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                        cg.state.current_capacity, "up", cg.parent));
            }
            if (cg_j < grid[0].length - 1) {
                q.add(new Node(cg_i, cg_j + 1,
                        cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                        cg.state.current_capacity, "right", cg.parent));
            }
            if (cg_j > 0) {
                q.add(new Node(cg_i, cg_j - 1,
                        cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                        cg.state.current_capacity, "left", cg.parent));
            }

            if(grid[cg_i][cg_j]!=null){
            if(grid[cg_i][cg_j] instanceof Ship){
                Ship ship= (Ship) grid[cg_i][cg_j];
                int remaining_capacity= capacity-cg.state.current_capacity;
                if(!ship.wrecked && remaining_capacity>0){
                    int passengers_carried= Math.min(remaining_capacity, ship.passengers);
                    ship.passengers-=passengers_carried;
                    q.add(new Node(cg_i, cg_j,
                            cg.state.remaining_passengers, cg.state.remaining_blackboxes,
                            cg.state.current_capacity+passengers_carried, "pickup", cg.parent));

                }
                else if (ship.wrecked && ship.black_box==0){
                    ship.black_box=1;
                    q.add(new Node(cg_i, cg_j,
                            cg.state.remaining_passengers, cg.state.remaining_blackboxes-1,
                            cg.state.current_capacity, "retrieve", cg.parent));
                }
                grid[cg_i][cg_j]=ship;
            }

            if(grid[cg_i][cg_j] instanceof Station && cg.state.current_capacity>0){
                Station ship= (Station) grid[cg_i][cg_j];
               if(cg.state.current_capacity>0){
                   int passengers_dropped_off= cg.state.current_capacity;
                   q.add(new Node(cg_i, cg_j,
                           cg.state.remaining_passengers-passengers_dropped_off, cg.state.remaining_blackboxes,
                           0, "drop", cg.parent));
               }
            }
        }
        }
    }

    }

