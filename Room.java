package core;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import common.Message;
import common.Observer;

public class Room implements Observer{

	public GameHall gameHall;
	private Message echoMsg;
	public LinkedList<TunnelThread> threads = new LinkedList<TunnelThread>();
	private Map<String, LinkedList<TunnelThread>> waitRoom = new HashMap<String, LinkedList<TunnelThread>>();
	public LinkedList<TunnelThread> players;

	public Room() {
		waitRoom.put(Message.THREE, new LinkedList<TunnelThread>());
		waitRoom.put(Message.FOUR, new LinkedList<TunnelThread>());
	}

	@Override
	public void update(Message msg) {
		switch (msg.order) {
		case Message.JOIN:
			players = waitRoom.get(msg.msgs.get(0));
			System.out.println("房间中的玩家数：" + players.size());
			if(!players.contains(msg.thread)) players.add(msg.thread);
			//确认JOIN命令
			echoMsg = new Message(Message.ROOM, msg.from, Message.ECHO, Message.OK, null);
			try {
				msg.out.write(echoMsg.toString());
				msg.out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}			
			//主机日志
			StringBuilder sb = new StringBuilder();
			sb.append("当前用户有：\n");
			for(TunnelThread t : players) sb.append(t.msg.from + '\n');
			switch (msg.msgs.get(0)) {
			case Message.THREE:
				if(players.size() == 3) sb.append("新开3人局……\n");
				break;
			case Message.FOUR:
				if(players.size() == 4) sb.append("新开4人局……\n");
			}
			sb.deleteCharAt(sb.length()-1);		//去掉最后一个\n
			System.out.println(sb.toString());
			//发玩家数组给客户端
			LinkedList<String> users = new LinkedList<String>();
			for(TunnelThread t : players) users.add(t.msg.from);
			for(TunnelThread t : players) {
				Message playerList = new Message(Message.ROOM, t.msg.from, Message.PLAYERS, users, t);
				try {
					playerList.out.write(playerList.toString());
					playerList.out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//通知信息
			echoMsg.msgs.clear();
			echoMsg.msgs.add(msg.from);
			echoMsg.msgs.add("已加入房间");
			echoMsg.order = Message.MSG;
			echoMsg.from = Message.ROOM;
			for(TunnelThread t : players) {
				echoMsg.to = t.msg.from;
				try {
					t.out.write(echoMsg.toString());
					t.out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
			
			boolean full = false;
			switch (msg.msgs.get(0)) {
			case Message.THREE:
				if(players.size() == 3) full = true;
				break;
			case Message.FOUR:
				if(players.size() == 4) full = true;
			}
			if(full) {
				Landlord landlord = new Landlord(users.size() == 3 ? 1:2, true, users);
				//将landlord注册到用户socket线程
				for(TunnelThread t : players) t.registerObserver(landlord);
				landlord.threads = players;
				//随机确定顺序
				LinkedList<String> p = new LinkedList<String>();
				int n = players.size();
				Random rand = new Random();
				for(int i = n; i > 1; i--) {
					p.add(users.remove(rand.nextInt(i)));
				}
				p.add(users.get(0));
				users = p;
				//发牌
				landlord.deal(users);
				Bidding bid = new Bidding(users, users.get(0));
				bid.landlord = landlord;
				bid.threads = players;
				landlord.bid = bid;
				landlord.players = users;
				//启动客户端叫牌界面
				for(TunnelThread t : bid.threads) {
					Message startBid = new Message(Message.ROOM, t.msg.from, Message.BID, Message.BEGIN, t);
					try {
						startBid.out.write(startBid.toString());
						startBid.out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					t.registerObserver(bid);	//把叫牌注册到thread上
				}
				players.clear();;	//清空已开局的房间
			}
			break;
		case Message.LEAVE:
			for(String game : waitRoom.keySet()) {
				if(waitRoom.get(game).remove(msg.thread)){
					//确认JOIN命令
					echoMsg = new Message(Message.ROOM, msg.from, Message.ECHO, Message.OK, null);
					try {
						msg.out.write(echoMsg.toString());
						msg.out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					players = waitRoom.get(game);
					if(players.isEmpty()) {
						System.out.println("当前房间已无用户");
						continue;
					}
					//系统日志
					sb = new StringBuilder();
					sb.append("当前用户有：\n");
					users = new LinkedList<String>();
					for(TunnelThread t : players) {
						sb.append(t.msg.from + '\n');
						users.add(t.msg.from);
					}
					sb.deleteCharAt(sb.length()-1);		//去掉最后一个\n
					System.out.println(sb.toString());
					//发玩家数组给客户端
					for(TunnelThread t : players) {
							Message playerList = new Message(Message.ROOM, t.msg.from, Message.PLAYERS, users, t);
							try {
								playerList.out.write(playerList.toString());
								playerList.out.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
					}
					//通知
					echoMsg.msgs.clear();
					echoMsg.msgs.add(msg.from);
					echoMsg.msgs.add(msg.from + "已离开房间");
					echoMsg.order = Message.MSG;
					echoMsg.from = Message.ROOM;
					for(TunnelThread t : players) {
						echoMsg.to = t.msg.from;
						try {
							t.out.write(echoMsg.toString());
							t.out.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}				
					}
				}
			}			
			break;
		case Message.EXIT:
			if(threads.remove(msg.thread)) {
				gameHall.threads.add(msg.thread);
				//确认JOIN命令
				echoMsg = new Message(Message.ROOM, msg.from, Message.ECHO, Message.OK, null);
				try {
					msg.out.write(echoMsg.toString());
					msg.out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
