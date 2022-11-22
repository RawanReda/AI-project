package code;

import java.util.*;

public class CoastGaurd extends GeneralSearchProblem {
    static StringBuilder grid_string;
    static GridCell [][] grid;
    static HashMap< Integer, HashSet<Integer>> locations_occupied;
    static int cg_i;
    static int cg_j;
    static int capacity;
    static int total_passengers=0;
    private static List<Ship> observers = new ArrayList<>();



    public static void addObserver(Ship ship) {
        observers.add(ship);
    }

    public static void removeObserver(Ship ship) {
        observers.remove(ship);
    }

    public static String genGrid(){
        grid_string= new StringBuilder();
        locations_occupied= new HashMap<>();

        int m= (int) (Math.random()*10+1)+5; //0-(m-1)
        int n= (int) (Math.random()*10+1)+5; //0-(n-1)
        capacity= (int) (Math.random()*70+1)+30;
        cg_i=(int) (Math.random()*n); //row
        cg_j=(int) (Math.random()*m); //col

        grid_string.append(m+","+n+";"+capacity+";"+cg_i+","+cg_j+";");

        System.out.println("col:"+m+ " row:"+n+" cg_i:"+ cg_i+" cg_c:"+cg_j);

        // save location of the coast gaurd in the hashmap
        HashSet<Integer> cg_col= new HashSet<>();
        cg_col.add(cg_j);
        locations_occupied.put(cg_i, cg_col);

        int rem_cells= (m*n)-1;

        System.out.println("rem cell  "+ rem_cells);
        int n_stations= (int) (Math.random()*rem_cells)+1;
        rem_cells-= n_stations;
        int n_ships= (int) (Math.random()*rem_cells)+1;
        rem_cells-= n_ships;

        System.out.println(n_ships+" ss "+n_stations);
        occupyPositions("I", n_stations);
        occupyPositions("S", n_ships);

        return grid_string.toString();
    }

    public static void printGrid(CGNode[][] grid){

        System.out.println(grid.length+" "+ grid[0].length);
        for(int i=0; i<grid.length; i++){
            for(int j=0; j<grid[i].length; j++){

                CGNode curr= grid[i][j];
                if(curr!=null){
                System.out.print(i+" "+j+" "+"   |   "); }
                else System.out.print(i+" "+j+" "+"empty" +"   |   ");
            }
            System.out.println();
            }

        }

    public static void occupyPositions(String type, int count){
        int m= grid[0].length; // number of columns
        int n= grid.length; // number of rows
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

    public static String solve(String g, String strategy, Boolean visualise){
        String [] grid_info = g.split(";");
        int m = Integer.parseInt(grid_info[0].split(",")[0]);
        int n = Integer.parseInt(grid_info[0].split(",")[1]);
        capacity = Integer.parseInt(grid_info[1]);
        cg_i = Integer.parseInt(grid_info[2].split(",")[0]);
        cg_j = Integer.parseInt(grid_info[2].split(",")[1]);

        grid = new GridCell [n][m];

        // Stations
        String [] station_location = grid_info[3].split(",");
        for(int i=0; i<station_location.length; i+=2){
            int x = Integer.parseInt(station_location[i]);
            int y = Integer.parseInt(station_location[i+1]);
            Station station = new Station(x,y);
            grid[x][y] = station;
        }
        // Ships
        String [] ship_location = grid_info[4].split(",");
        for(int i=0; i<ship_location.length; i+=3){
            int x = Integer.parseInt(station_location[i]);
            int y = Integer.parseInt(station_location[i+1]);
            int c = Integer.parseInt(station_location[i+2]);
            total_passengers += c;
            Ship ship = new Ship(c,x,y);
            addObserver(ship);
            grid[x][y] = ship;
        }

        CGNode initial_state = new CGNode(cg_i, cg_j, total_passengers,
                ship_location.length/3, 0, null, null  );
        if (strategy.equals("DF")){


        }
        return " ";
    }



    public static void main(String[] args){
        System.out.println(genGrid());
    }
}
