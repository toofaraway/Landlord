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
			System.out.println("�����е��������" + players.size());
			if(!players.contains(msg.thread)) players.add(msg.thread);
			//ȷ��JOIN����
			echoMsg = new Message(Message.ROOM, msg.from, Message.ECHO, Message.OK, null);
			try {
				msg.out.write(echoMsg.toString());
				msg.out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}			
			//������־
			StringBuilder sb = new StringBuilder();
			sb.append("��ǰ�û��У�\n");
			for(TunnelThread t : players) sb.append(t.msg.from + '\n');
			switch (msg.msgs.get(0)) {
			case Message.THREE:
				if(players.size() == 3) sb.append("�¿�3�˾֡���\n");
				break;
			case Message.FOUR:
				if(players.size() == 4) sb.append("�¿�4�˾֡���\n");
			}
			sb.deleteCharAt(sb.length()-1);		//ȥ�����һ��\n
			System.out.println(sb.toString());
			//�����������ͻ���
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
			//֪ͨ��Ϣ
			echoMsg.msgs.clear();
			echoMsg.msgs.add(msg.from);
			echoMsg.msgs.add("�Ѽ��뷿��");
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
				//��landlordע�ᵽ�û�socket�߳�
				for(TunnelThread t : players) t.registerObserver(landlord);
				landlord.threads = players;
				//���ȷ��˳��
				LinkedList<String> p = new LinkedList<String>();
				int n = players.size();
				Random rand = new Random();
				for(int i = n; i > 1; i--) {
					p.add(users.remove(rand.nextInt(i)));
				}
				p.add(users.get(0));
				users = p;
				//����
				landlord.deal(users);
				Bidding bid = new Bidding(users, users.get(0));
				bid.landlord = landlord;
				bid.threads = players;
				landlord.bid = bid;
				landlord.players = users;
				//�����ͻ��˽��ƽ���
				for(TunnelThread t : bid.threads) {
					Message startBid = new Message(Message.ROOM, t.msg.from, Message.BID, Message.BEGIN, t);
					try {
						startBid.out.write(startBid.toString());
						startBid.out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					t.registerObserver(bid);	//�ѽ���ע�ᵽthread��
				}
				players.clear();;	//����ѿ��ֵķ���
			}
			break;
		case Message.LEAVE:
			for(String game : waitRoom.keySet()) {
				if(waitRoom.get(game).remove(msg.thread)){
					//ȷ��JOIN����
					echoMsg = new Message(Message.ROOM, msg.from, Message.ECHO, Message.OK, null);
					try {
						msg.out.write(echoMsg.toString());
						msg.out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					players = waitRoom.get(game);
					if(players.isEmpty()) {
						System.out.println("��ǰ���������û�");
						continue;
					}
					//ϵͳ��־
					sb = new StringBuilder();
					sb.append("��ǰ�û��У�\n");
					users = new LinkedList<String>();
					for(TunnelThread t : players) {
						sb.append(t.msg.from + '\n');
						users.add(t.msg.from);
					}
					sb.deleteCharAt(sb.length()-1);		//ȥ�����һ��\n
					System.out.println(sb.toString());
					//�����������ͻ���
					for(TunnelThread t : players) {
							Message playerList = new Message(Message.ROOM, t.msg.from, Message.PLAYERS, users, t);
							try {
								playerList.out.write(playerList.toString());
								playerList.out.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
					}
					//֪ͨ
					echoMsg.msgs.clear();
					echoMsg.msgs.add(msg.from);
					echoMsg.msgs.add(msg.from + "���뿪����");
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
				//ȷ��JOIN����
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
