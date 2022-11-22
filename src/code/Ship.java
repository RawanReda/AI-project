package code;

public class Ship extends GridCell implements Observer {
     int passengers;
     int black_box;
     int deaths;
     boolean wrecked;

     int savedPassengers;
     boolean done;

     public Ship(int passengers, int i, int j) {
          this.passengers = passengers;
          this.black_box = 0;
          this.done = false;
          super.i = i;
          super.j = j;
          this.deaths = 0;
          this.wrecked = false;
          this.savedPassengers = 0;
     }

     public void update(CGNode node){
          if(!done) {
               int remaining = passengers - deaths;
               if(node.operator.equals("pickup") && node.state.i == i && node.state.j == j){
                    int saved = Math.min(remaining, node.state.remaining_capacity);
                    savedPassengers += saved;
                    passengers -= saved;
                    node.state.remaining_capacity -= saved;
               }
               else if(node.operator.equals("retrieve") && node.state.i == i && node.state.j == j && remaining == 0){
                    this.done = true;
               }
               else if(wrecked){
                    if(black_box == 100){
                         node.state.remaining_blackboxes--;
                    }
                    else {
                         black_box++;
                    }
               }
               else if (deaths == passengers || remaining == 0) {
                    this.wrecked = true;
               }
               else if (deaths < passengers) {
                    node.state.remaining_passengers--;
                    deaths++;
               }
          }



     }
}
