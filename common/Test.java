package common;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.net.UnknownHostException;

import client.Player;
import core.Bidding;

public class Test {

	public static void main(String[] args) {
/*		String[] players = {"¼×", "ÒÒ", "±û", "¶¡"};
		int suits = 0;
		if(players.length == 3) suits = 1;
		if(players.length == 4) suits = 2;
		Landlord lord = new Landlord(suits, true,players);
		lord.deal(players);
		for (Hand hand : lord.cardsInHands) {
			lord.sort(hand);
			System.out.println(hand.playerId + ": " + hand.hand.toString());
		}
		System.out.println(lord.remainCards.toString());
		
		LinkedList<Integer> t = new LinkedList<Integer>();
		LinkedList<Integer> t0 = new LinkedList<Integer>();
		//int[] test = {14,14,14,13,13,13,14,13,13,14,12,12,12,12,12};
		int[] test = {-2};
		int[] test0 = {14};
		for(int i = 0; i < test.length; i++) t.add(test[i]);
		Hand hand= new Hand("¼×", t);
		lord.player = "¼×";
		for(int i = 0; i < test0.length; i++) t0.add(test0[i]);
		Hand lasthand= new Hand("¼×", t0);
		System.out.println(hand.hand.toString());
		System.out.println(lord.getType(hand));
		System.out.println(lasthand.hand.toString());
		System.out.println(lord.getType(lasthand));
		System.out.println(lord.isBigger(hand, lasthand));
		String[] players = {"A","B","C","D"};
		Bidding bid = new Bidding(players, "B");
		bid.stratBidding();
		System.out.println("µØÖ÷£º" + bid.getBanker());
		System.out.println("A-" + bid.getContract("A"));
		System.out.println("B-" + bid.getContract("B"));
		System.out.println("C-" + bid.getContract("C"));
		System.out.println("D-" + bid.getContract("D"));*/
		Player player1, player2, player3;
		player1 = new Player("Player-1");
		player2 = new Player("Player-2");
		player3 = new Player("Player-3");
		try {
			Robot r = new Robot();
			player1.createSocket();
			player2.createSocket();
			player3.createSocket();
			player1.joinRoom("three");
			r.delay(20000);
			player2.joinRoom("three");
			r.delay(10000);
			player3.joinRoom("three");
			r.delay(5000);
			player1.releaseSocket();
			player2.releaseSocket();
			player3.releaseSocket();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
