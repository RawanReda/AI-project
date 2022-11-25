package code;

public class Ship extends GridCell implements Observer {
     int passengers; // remaining passengers
     int black_box;
     int deaths;
     boolean wrecked;

     int savedPassengers;
     boolean done;

     @Override
     public Object clone() {
               Ship new_ship= new Ship(this.passengers, this.i, this.j);
               new_ship.black_box = this.black_box;
               new_ship.done= this.done ;
               new_ship.deaths = this.deaths;
               new_ship.wrecked=this.wrecked;
               new_ship.savedPassengers=savedPassengers;
               return new_ship;
     }

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

     public void update(Node node){
//          System.out.println("ship crossed ");
//          System.out.println("operator: "+ node.operator+" i: "+node.state.i+" j: "+node.state.j);

          if(!done) {
               if(node.operator.equals("pickup") && node.state.i == i && node.state.j == j){

                    int saved = Math.min(passengers, node.state.remaining_capacity);
                    this.savedPassengers += saved;
                    this.passengers -= saved;
//                    System.out.println("ship Remaining Passengers: "+ this.passengers);
                    node.state.remaining_capacity -= saved;

                  //  node.state.rescued_passengers += saved;
                    if(this.passengers > 0)
                         this.deaths ++;
                    if(passengers==0){
                         this.wrecked = true;
                    }

               }
               else if(node.operator.equals("retrieve") && node.state.i == i && node.state.j == j && passengers == 0){
//                    System.out.println("ship Black Box Retrieved ");
                    this.done = true;
                    node.state.remaining_blackboxes--;
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
//          System.out.println("ship crossed end ");


     }
}
