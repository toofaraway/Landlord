package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import common.Hand;
import common.Message;
import common.Observer;

public class Player {

	private static final String server = "localhost";
	private static final int PORT =8189;
	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	private String name;
	private int room;
	private Hand cardsInHand;
	private List<Hand> currentRound;
	private String[] players;									//所有玩家
	private String banker;										//庄家
	
	
	public Player(String name) {
		this.name = name;
	}

	public void createSocket() throws UnknownHostException, IOException {
		if(socket == null)	socket = new Socket(server, PORT);
		in = new Scanner(socket.getInputStream());
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
	}
	
	public void releaseSocket() throws IOException {
		out.close();
		in.close();
		socket.close();
	}
	
	public boolean register() {
		return false;
	}
	
	public boolean login() {
		return false;
	}
	
	public boolean recharge(int money) {
		return false;
	}
	
	public int joinRoom(String game) {
		request("HALL", Message.JOIN, game);		
		return 0;
	}
	
	public boolean leaveRoom(int roomNo) {
		return false;
	}
	
	public int bid(int bidding) throws IOException {
		List<String> msgs = new LinkedList<String>();
		msgs.add(Integer.toString(bidding));
		Message msg = new Message(name, room, Message.BID, msgs, in, out);
		out.println(msg.toString());
		msgs.clear();
		while(in.hasNext()) {
			msgs.add(in.nextLine());
		}
		if(msgs.get(3).equals("ok")) return 0;
		else return -1;
	}
	
	public int discard(Hand hand) {
		return 0;
	}
	
	public void review() {
		
	}
	
	public void reviewHistory(int num) {
		
	}
	
	private Message request(String to, String order, String arg) {
		Message msg = new Message(name, to, order, arg, in, out);
		out.println(msg.toString());
		return msg;
	}

}
