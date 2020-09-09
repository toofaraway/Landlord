package common;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import core.GameHall;
import core.Room;

public class TunnelThread implements Runnable, Subject {

	public static final String HALL = "HALL";
	public static final String ROOM = "ROOM";
	
	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	private boolean stop;
	private Message msg;
	private List<GameHall> gameHalls = new LinkedList<GameHall>();
	private List<Room> rooms = new LinkedList<Room>();
	
	public TunnelThread(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		stop = false;
		try {
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> msgs = new LinkedList<String>();
		stop = false;
		while(!stop) {			
			while(in.hasNext()) msgs.add(in.nextLine());
			if(msgs.size() > 3) {
				msg = new Message(msgs.remove(0), msgs.remove(0) , msgs.remove(0), msgs, in, out);
				notifyObservers();				
			}else {
				List<String> list = new LinkedList<String>();
				list.add(Message.ERR);
				response(Message.ECHO, list);
			}
			msgs.clear();
		}
	}
	
	public void response(String order, List<String> info) {
		StringBuilder sb = new StringBuilder();
		String from = "TunnelThread" + this.toString() + '\n';
		String to = "Socket" + socket.toString() + '\n';
		sb.append(from);
		sb.append(to);
		sb.append(Message.ECHO + '\n');
		for(String str : info) sb.append(str + '\n');
		out.println(sb.toString());
	}
	
	public void stop( ) {
		stop = true;
		out.close();
		in.close();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void registerObserver(Observer o) {
		if(o instanceof GameHall) gameHalls.add((GameHall) o);
		if(o instanceof Room) rooms.add((Room) o);
	}

	@Override
	public void removeObserver(Observer o) {
		if(o instanceof GameHall) gameHalls.remove((GameHall) o);
		if(o instanceof Room) rooms.remove((Room) o);
	}

	@Override
	public void notifyObservers() {
		switch(msg.to) {
		case HALL:
			for(GameHall g : gameHalls) g.update(msg);
			break;
		case ROOM:
			for(Room r : rooms) r.update(msg);
			break;
		default:
		}
		return;				
	}

}
