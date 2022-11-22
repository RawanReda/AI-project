package code;

public class Ship extends GridCell {
     int passengers;
     int black_box;
     int deaths;
     boolean wrecked;

     public Ship(int passengers, int i, int j) {
          this.passengers = passengers;
          this.black_box = 0;
          super.i = i;
          super.j = j;
          this.deaths = 0;
          this.wrecked = false;
     }

     public void update(Node node) {

     }
}