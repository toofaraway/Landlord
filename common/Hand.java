package common;

import java.util.List;

public class Hand {
	public String player;
	public List<Integer> hand;
	
	public Hand(String player, List<Integer> hand){
		this.player = player;
		this.hand = hand;
	}
}
