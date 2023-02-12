:- include('KB.pl').
agent(X,Y,S,Ships, Capacity):- agent_loc(X,Y),ships_loc(Ships),capacity(Capacity), S=S.

agent(X, Y, result(A,S),Ships, Capacity ):-

	grid(W,H),
        (
	(agent(X2, Y, S, Ships, Capacity),A = up, X2 > 0, X is X2-1);
        (agent(X2, Y, S, Ships, Capacity), A = down, X2 < W-1, X is X2+1);
        (agent(X, Y2, S, Ships, Capacity), A = left, Y2 > 0, Y is Y2-1);
        (agent(X, Y2, S, Ships, Capacity), A = right, Y2 <H-1 , Y is Y2+1);
	(agent(X, Y, S, Ships2, Capacity2), A = pickup, Capacity2>0,
	 update_ship([X,Y],Ships2, Ships) , Capacity is Capacity2-1);
	(agent(X, Y, S, Ships, Capacity2), A = drop, station(X,Y), capacity(N), Capacity2<N,Capacity is N )
	).


update_ship(Member, Ships, Ships2):-
	memberchk(Member, Ships), delete(Ships, Member, Ships2).

goal_helper2(S):- agent(X,Y,S,Ships,Capacity),station(X,Y), Ships=[],capacity(Capacity).

goal_helper1(X,L):-
(call_with_depth_limit(goal_helper2(X),L,R), number(R));
(call_with_depth_limit(goal_helper2(X),L,R), R=depth_limit_exceeded,
L1 is L+1, goal_helper1(X,L1)).


goal(X):- goal_helper1(X,10).
