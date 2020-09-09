package core;

import java.util.LinkedList;
import java.util.List;

import common.BidRecord;
import common.Message;
import common.Observer;

public class Bidding implements Observer{
	public static final int NOBIDDING = 0;
	public static final int DEW = -1;		//露水
	public static final int BANKER = 1;
	public static final int BANKERKING = 2;
	public static final int NODOUBLE = 0;
	public static final int DOUBLE = 4;
	public static final int REDOUBLE = 8;
	
	private String[] players;
	private String beginner;
	private List<BidRecord> bidRecords;
	private String banker;
	private List<String> doublePlayer;
	private boolean redouble = false;
	private BidRecord msg;
	private boolean hasMsg = false;
	private int contract;
	
	public Bidding(String[] players, String beginner){
		this.players = players;
		this.beginner = beginner;
	}
	
	//叫牌
	public void stratBidding( ) {
		bidRecords = new LinkedList<BidRecord>();
		contract = 0;
		banker = "";
		int point = 0;
		int lastBidding = 0;
		boolean end =false;
		//获取首叫牌用户游标
		for(int i = 0; i < players.length; i++) {
			if(players[i].equals(beginner)) {
				point = i;
				break;
			}
		}
		//叫牌
		for(int i = 0; i < players.length; i ++) {
			end = false;
			while(!end) {
				if (!hasMsg) continue;								//无消息
				if(!players[point].equals(msg.player)) continue;	//消息不是给当前用户的

				switch(msg.bidding) {
				case DEW:
					if(!bidRecords.isEmpty() || players.length != 4) continue;
					break;
				case NOBIDDING:
					break;
				case BANKER:
				case BANKERKING:
					if(msg.bidding <= lastBidding) continue;
					break;
				default:
					continue;
				}
				bidRecords.add(msg);
				hasMsg = false;
				if(msg.bidding != NOBIDDING) lastBidding = msg.bidding;
				if(point<players.length -1) point++;
				else point = 0;
				end = true;						
			}
			if(bidRecords.get(bidRecords.size() -1).bidding == BANKERKING) break;
		}
		//计算叫牌结果
		for(int i = bidRecords.size() -1; i >= 0; i--) {
			if(bidRecords.get(i).bidding != NOBIDDING) {
				contract = bidRecords.get(i).bidding;
				banker = bidRecords.get(i).player;
				break;
			}
		}		
	}
	
	//获取庄家
	public String getBanker( ) {
		for(int i = 0; i < bidRecords.size(); i++) {
			if(bidRecords.get(i).bidding == BANKERKING) {
				banker = bidRecords.get(i).player;
				return banker;
			}
		}
		for(int i = 0; i < bidRecords.size(); i++) {
			if(bidRecords.get(i).bidding == BANKER) {
				banker = bidRecords.get(i).player;
				return banker;
			}
		}
		for(int i = 0; i < bidRecords.size(); i++) {
			if(bidRecords.get(i).bidding == DEW) {
				banker = bidRecords.get(i).player;
				return banker;
			}
		}
		return null;
	}
	
	//计算叫牌倍数
	public int getContract(String player) {
		if(doublePlayer.isEmpty()) return contract;
		if(doublePlayer.contains(player) || banker.equals(player)) {
			if(redouble) return 4 * contract;
			else return 2 * contract;
		}else return contract;
	}

	//加倍（未来以线程实现）
	public void xDouble(String peasant) {
		while(true) {
			if(!hasMsg) continue;
			if(!peasant.equals(msg.player)) continue;
			if(msg.bidding == NODOUBLE) break;
			if(msg.bidding == DOUBLE) {
				if(doublePlayer == null) doublePlayer = new LinkedList<String>();
				doublePlayer.add(peasant);
				break;
			}
		}
		return;
	}
	
	//再加倍
	public void xxDouble(String banker) {
		while(true) {
			if(!hasMsg) continue;
			if(!banker.equals(msg.player)) continue;
			if(msg.bidding == NODOUBLE) break;
			if(msg.bidding == REDOUBLE) {
				redouble = true;
				break;
			}
		}
		return;
	}
	
	@Override
	public void update(Message msg) {
		// TODO Auto-generated method stub
		
	}

}
