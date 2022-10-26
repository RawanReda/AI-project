package code;

import java.util.HashMap;
import java.util.HashSet;

public class CoastGaurd extends GeneralSearchProblem {
    static StringBuilder grid_string;
    static Node[][] grid;
    static HashMap< Integer, HashSet<Integer>> locations_occupied;
    public static String genGrid(){
        grid_string= new StringBuilder();
        locations_occupied= new HashMap<>();

        int m= (int) (Math.random()*10+1)+5; //0-(m-1)
        int n= (int) (Math.random()*10+1)+5; //0-(n-1)
        grid= new Node[n][m];
        int capacity= (int) (Math.random()*70+1)+30;


        // grid[cg_x][cg_y]
        int cg_i=(int) (Math.random()*n); //row
        int cg_j=(int) (Math.random()*m); //col

        grid_string.append(m+","+n+";"+capacity+";"+cg_i+","+cg_j+";");

        System.out.println("col:"+m+ " row:"+n+" cg_i:"+ cg_i+" cg_c:"+cg_j);


        // save location of the coast gaurd in the hashmap
        HashSet<Integer> cg_col= new HashSet<>();
        cg_col.add(cg_j);
        locations_occupied.put(cg_i, cg_col);
        grid[cg_i][cg_j] = new Node(cg_i, cg_j, "CG");


        int rem_cells= (m*n)-1;

        System.out.println("rem cell  "+ rem_cells);
        int n_stations= (int) (Math.random()*rem_cells)+1;
        rem_cells-= n_stations;
        int n_ships= (int) (Math.random()*rem_cells)+1;
        rem_cells-= n_ships;

        System.out.println(n_ships+" ss "+n_stations);
        occupy_positions("I", n_stations);
        occupy_positions("S", n_ships);

        print_grid(grid);
       // System.out.println(capacity);
        return grid_string.toString();
    }

    public static void print_grid(Node[][] grid){

        System.out.println(grid.length+" "+ grid[0].length);
        for(int i=0; i<grid.length; i++){
            for(int j=0; j<grid[i].length; j++){

                Node curr= grid[i][j];
                if(curr!=null){
                System.out.print(i+" "+j+" "+curr.type +"   |   "); }
                else System.out.print(i+" "+j+" "+"empty" +"   |   ");
            }
            System.out.println();
            }

        }


    public static void occupy_positions(String type, int count){
        int m= grid[0].length;
        int n= grid.length;
        for(int i=0; i<count; i++){

            while(true){
                int col=(int) (Math.random()*m);
                int row=(int) (Math.random()*n);
                if(locations_occupied.containsKey(row)){
                    HashSet<Integer> cols= locations_occupied.get(row);
                    if(cols.contains(col)){
                        continue;
                    }
                    cols.add(col);
                    locations_occupied.put(row, cols);
                }else {
                    HashSet<Integer> cols= new HashSet<>();
                    cols.add(col);
                    locations_occupied.put(row, cols);
                }
                grid[row][col] = new Node(row, col,type);
                if(type.equals("S")){
                    grid_string.append(row+","+col+","+ (int)((Math.random()*100)+1));
                }else{
                    grid_string.append(row+","+col);
                }
                if(i<count-1)
                    grid_string.append(",");
                break;
            }

        }
        grid_string.append(";");

    }
    public static String solve(String grid, String strategy, Boolean visualise){
        return " ";
    }

    public static void main(String[] args){
        System.out.println(genGrid());
    }
}
