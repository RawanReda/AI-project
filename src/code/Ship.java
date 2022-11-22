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
               if(node.operator.equals("pickup") && node.state.i == i && node.state.j == j){

                    int saved = Math.min(passengers, node.state.remaining_capacity);
                    this.savedPassengers += saved;
                    this.passengers -= saved;
                    System.out.println("Remaining Passengers: "+ this.passengers);
                    node.state.remaining_capacity -= saved;
                    if(this.passengers > 0)
                         this.deaths ++;

               }
               else if(node.operator.equals("retrieve") && node.state.i == i && node.state.j == j && passengers == 0){
                    this.done = true;
               }
               else if ( passengers > 0) {
                    node.state.remaining_passengers--;
                    this.passengers -=1;
                    this.deaths++;
               }
               else if(wrecked){
                    if(black_box == 100){
                         node.state.remaining_blackboxes--;
                         this.done = true;
                    }
                    else {
                         black_box++;
                    }
               }
               else if (passengers == 0) {
                    this.wrecked = true;
               }


          }



     }
}
