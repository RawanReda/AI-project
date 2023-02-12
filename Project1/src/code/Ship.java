package code;

public class Ship extends GridCell implements Observer {
     int passengers; // remaining passengers
     int black_box;
     int deaths;
     boolean wrecked;

     int savedPassengers;
     boolean done;
     int expiry_date=20;
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
          this.done = false;
          super.i = i;
          super.j = j;
          this.deaths = 0;
          this.wrecked = false;
          this.savedPassengers = 0;
     }

     public void update(Node node){
          if(!done) {
               if(!wrecked && node.operator.equals("pickup") && node.state.i == i && node.state.j == j){
                    int saved = Math.min(passengers, node.state.remaining_capacity);
                    this.savedPassengers += saved;
                    this.passengers -= saved;
                    node.state.remaining_capacity -= saved;

                    if(this.passengers > 0){
                         this.passengers--;
                         node.state.remaining_passengers--;
                         this.deaths ++;}
                    if(passengers==0){
                         this.wrecked = true;
                         this.black_box=1;
                    }

               }
               else if(node.operator.equals("retrieve") && node.state.i == i && node.state.j == j && wrecked && black_box<expiry_date){
                    this.done = true;
                    node.state.remaining_blackboxes--;
                    node.state.retrieved_boxes++;
               }
               else if ( passengers > 0) {
                    node.state.remaining_passengers--;
                    this.passengers--;
                    this.deaths++;
                    if (this.passengers == 0) {
                         this.black_box=1;
                         this.wrecked = true;
                    }
               }
               else if(wrecked){
                    if(black_box <= expiry_date){
                         this.black_box++;
                         if(black_box==expiry_date) {
                              node.state.remaining_blackboxes--;
                              this.done = true;
                         }
                    }
               }
               else if (passengers == 0) {
                    this.wrecked = true;
                    this.black_box++;
               }


          }
     }
}
