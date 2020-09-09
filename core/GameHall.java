package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import common.Message;
import common.Observer;
import common.TunnelThread;

public class GameHall implements Observer {

	public static final int PORT = 8189;
	public static final String FOUR = "four";
	public static final String THREE = "three";
	private Map<String, List<String>> waitRoom = new HashMap<String, List<String>>();
	
	
	public void main(String args[]) {
		GameHall gameHall = new GameHall();
		try (ServerSocket server = new ServerSocket(PORT);)
		{			
			Socket socket = null;
			while(true) {
				socket = server.accept();
				TunnelThread tunnel = new TunnelThread(socket);
				tunnel.registerObserver(gameHall);
				tunnel.run();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(Message msg) {
		switch (msg.order) {
		case Message.BID:
			break;
		case Message.ECHO:
			break;
		case Message.JOIN:
			List<String> players;
			String[] game;
			switch (msg.msgs.get(0)) {
			case THREE:
				players = waitRoom.get(THREE);
				if(players == null) players = new LinkedList<String>();
				players.add(msg.from);
				waitRoom.put(THREE, players);
				game = (String[]) players.toArray();
				StringBuilder echo = new StringBuilder();
				if(players.size() == 3) {
					System.out.println("服务器信息：\n新开3人局……");
					echo.append("客户端信息：\\n新开3人局……\n");
					players.clear();
				}
				System.out.println("当前玩家有：");
				echo.append("当前玩家有：\n");
				for(String str : game) {
					System.out.println(str);
					echo.append(str + '\n');
				}
				msg.out.println(echo);
				break;
			case FOUR:
				players = waitRoom.get(FOUR);
				if(players == null) players = new LinkedList<String>();
				players.add(msg.from);
				waitRoom.put(FOUR, players);
				game = (String[]) players.toArray();
				if(players.size() == 4) {
					System.out.println("新开4人局……");
					players.clear();
				}
				System.out.println("当前玩家有：");
				for(String str : game) System.out.println(str);
				break;
			}
			break;
		default:
		}
	}

}
