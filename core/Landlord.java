package core;

import java.util.LinkedList;
import java.util.List;

import common.Hand;
import common.Message;
import common.Observer;
import common.Subject;

public class Landlord extends Poker implements PokerGame, Subject, Observer{

	public int base = 10;
	private boolean hasMsg;
	private Hand msg;
	private boolean finished;
	private String winner;
	private Bidding bid;
	
	Landlord(int suits, boolean hasJoker, String[] players) {
		super(suits, hasJoker, players);
	}

	//发牌
	@Override
	public void deal(String[] players) {
		for(String player : players) {
			Hand hand = new Hand(player, new LinkedList<Integer>());
			cardsInHands.add(hand);
		}
		int turns = 0;
		if (players.length == 3) turns = 17;
		if (players.length == 4) turns = 25;
		for(int i = 0; i < turns; i++) {
			for(Hand hand : cardsInHands) hand.hand.add(remainCards.remove(0));
		}
	}

	//整理牌
	@Override
	public void sort(Hand hand) {
		//pass不用排序
		if (hand.hand.get(0).equals(PASS)) return;
		
		LinkedList<Integer> des = new LinkedList<Integer>();
		//找大王
		int index = 0;
		while(index < hand.hand.size()) {
			if (hand.hand.get(index).equals(JOKERKING)) {
				des.add(hand.hand.remove(index));
				index--;
			}
			index++;
		}
		//找小王
		index = 0;
		while(index < hand.hand.size()) {
			if (hand.hand.get(index).equals(JOKER)) {
				des.add(hand.hand.remove(index));
				index--;
			}
			index++;
		}
		//找2
		index = 0;
		while(index < hand.hand.size()) {
			if (hand.hand.get(index) % 13 == TWO) {
				des.add(hand.hand.remove(index));
				index--;
			}
			index++;
		}
		//其余从大到小排序
		int size = hand.hand.size();
		for (int i = 0; i < size-1; i++) {
			for (int j = i + 1; j < size; j++) {
				if ((hand.hand.get(i) -2) % 13 < (hand.hand.get(j) - 2) % 13) {
					int temp = hand.hand.get(i);
					hand.hand.set(i, hand.hand.get(j));
					hand.hand.set(j, temp);					
				}
			}
		}
		//合并在一起并传回hand
		des.addAll(hand.hand);
		hand.hand = des;
	}

	//判断牌型
	@Override
	public Type getType(Hand hand) {
		sort(hand);
		int cards = hand.hand.size();	//出牌张数
		int[] mod = new int[cards];
		
		//1张（单张、pass）
		if (cards == 1) {
			if(hand.hand.get(0) == PASS) return Type.pass;
			else return Type.single;
		}
		
		//2张（火箭、对儿）
		if (cards == 2) {
			mod[0] = hand.hand.get(0);
			mod[1] = hand.hand.get(1);
			if(mod[0] == mod[1]) return Type.pair;
			if(mod[0] == JOKERKING && mod[1] == JOKER) {
				if(suits == 1) return Type.rocket;
				else return Type.notValid;
			}
			if((mod[0] % 13) == (mod[1] % 13)) return Type.pair;
			else return Type.notValid;
		}
		
		//3张
		if (cards == 3) {
			mod[0] = hand.hand.get(0) % 13;
			mod[2] = hand.hand.get(2) % 13;
			if (mod[0] == mod[2]) return Type.triple;
			else return Type.notValid;
		}
		
		//4张（火箭、炸弹、三带1）
		if (cards == 4) {
			mod[0] = hand.hand.get(0) % 13;
			mod[1] = hand.hand.get(1) % 13;
			mod[2] = hand.hand.get(2) % 13;
			mod[3] = hand.hand.get(3) % 13;

			if(mod[0] == JOKERKING && mod[3] == JOKER) return Type.rocket;
			if(mod[0] == mod[3]) return Type.bomb;
			if(mod[1] != mod[2]) return Type.notValid;
			if(mod[0] == mod[1] && suits == 1) return Type.tripleWithSingle;
			if(mod[2] == mod[3] && suits == 1) {
				int temp = hand.hand.get(0);
				hand.hand.set(0, hand.hand.get(3));
				hand.hand.set(3, temp);
				return Type.tripleWithSingle;
			}
			return Type.notValid;
		}
		
		//识别5张以上的炸弹、顺子、连对、三带2、飞机、四带2、飞机带翅膀
		for(int i = 0; i < cards; i++) mod[i] = (hand.hand.get(i) - 2) % 13;

		//炸弹
		if (mod[0] == mod[cards-1]) return Type.bomb;

		//顺子
		boolean flag = true;
		for(int i = 0; i < cards-1; i++) if(mod[i] - mod[i+1] != 1) flag =false;
		if (flag) return Type.straight;

		//连对		
		if((cards % 2) == 0) {
			flag = true;
			int[] mods = new int[cards/2];
			for(int i = 0; i < cards -1; i=i+2) {
				if(mod[i] != mod[i+1]) flag = false;
				else mods[i/2] = mod[i];
			}
			if(flag) for(int i = 0; i < cards/2 -1; i++) if(mods[i] - mods[i+1] != 1) flag = false;
			if(flag && cards >= 6) return Type.pairStright;
		}

		//三带2
		if(cards == 5) {
			if(mod[0] == mod[2] && mod[3] == mod[4]) return Type.tripleWithPair;
			if(mod[0] == mod[1] && mod[2] == mod[4]) {
				int temp = hand.hand.get(0);
				hand.hand.set(0, hand.hand.get(3));
				hand.hand.set(3, temp);
				temp = hand.hand.get(1);
				hand.hand.set(1, hand.hand.get(4));
				hand.hand.set(4, temp);
				return Type.tripleWithPair;
			}
			return Type.notValid;
		}

		//飞机
		if(cards % 3 == 0) {
			flag = true;
			int[] mods = new int[cards/3];
			for(int i = 0; i < cards -1; i = i +3) {
				if(mod[i] != mod[i+2]) flag =false;
				else mods[i/3] = mod[i];
			}
			if(flag) for(int i = 0; i < cards/3 - 1; i++) if(mods[i] - mods[i+1] != 1) flag = false;
			if(flag && cards >= 6) return Type.airplane;
		}
		
		//四带2
		if(cards == 6) {
			if(mod[0] == mod[3] || mod[1] == mod[4] || mod[2] == mod[5]) {
				int index = 0;
				for(int i = 0; i < 3; i++) {
					if(mod[i] == mod[i+3]) break;
					index++;
				}
				int temp;
				switch (index) {
				case 2:
					temp = hand.hand.get(5);
					hand.hand.set(5, hand.hand.get(1));
					hand.hand.set(1, temp);
				case 1:
					temp = hand.hand.get(4);
					hand.hand.set(4, hand.hand.get(0));
					hand.hand.set(0, temp);
				case 0:
					return Type.fourWith2;
				}
			}			
			return Type.notValid;
		}
		
		//飞机带翅膀（单）
		if(cards % 4 == 0 && suits == 1) {
			int[] point = new int[cards/4];
			for(int i = 0; i < cards/4; i++) point[i] = -1;
			int index = 0;
			for(int i = 0; i < cards -2; i++) {
				if(mod[i] == mod[i+2]) {
					point[index] = i;
					index++;
					i = i +2;	//游标跳到匹配部分尾部
				}
			}
			flag = true;
			for(int i = 0; i < cards/4; i++) if(point[i] == -1) flag = false;
			if(flag) {
				for(int i = 0; i < cards/4 - 1; i++) {
					if(mod[point[i]] - mod[point[i+1]] != 1) flag = false;
				}
				if(flag) {
					LinkedList<Integer> temp = new LinkedList<Integer>();
					for(int i = 0; i < cards/4; i++) {
						temp.add(hand.hand.get(point[i]));
						temp.add(hand.hand.get(point[i]+1));
						temp.add(hand.hand.get(point[i]+2));
						hand.hand.set(point[i], 0);
						hand.hand.set(point[i]+1, 0);
						hand.hand.set(point[i]+2, 0);
					}
					for(int i = 0; i < cards; i++) {
						if(hand.hand.get(i) != 0) temp.add(hand.hand.get(i));
					}
					hand.hand = temp;
					return Type.airplaneWithSingle;
				}
			}
			return Type.notValid;
		}
		
		//飞机带翅膀（对儿）{4,4,4,5,5,5,8,8,8,8}等牌型有bug
		if(cards % 5 == 0 ) {
			int[] point = new int[cards/5];
			for(int i = 0; i < cards/5; i++) point[i] = -1;
			int index = 0;
			for(int i = 0; i < cards -2 && index < point.length; i++) {	//&&index<point.length-1
				if(mod[i] == mod[i+2]) {
					point[index] = i;
					index++;
					i = i +2;	//游标跳到匹配部分尾部
				}
			}
			flag = true;
			for(int i = 0; i < cards/5; i++) if(point[i] == -1) flag = false;
			if(flag) {
				for(int i = 0; i < cards/5 - 1; i++) {
					if(mod[point[i]] - mod[point[i+1]] != 1) flag = false;
				}
				if(flag) {
					int[] pair = new int[cards/5*2];
					index = 0;
					int j = 0;
					for(int i = 0; i < cards && index < point.length; i++) {
						if(point[index] == i) {		//!!!
							i = i + 2;
							index++;
						}else {
							pair[j] = mod[i];
							j++;
						}
					}
					//判断对子
					for(int i = 0; i < cards / 5 * 2 -1; i = i + 2) if(pair[i] != pair[i+1]) flag = false;
					if(flag) {
						LinkedList<Integer> temp = new LinkedList<Integer>();
						for(int i = 0; i < cards/5; i++) {
							temp.add(hand.hand.get(point[i]));
							temp.add(hand.hand.get(point[i]+1));
							temp.add(hand.hand.get(point[i]+2));
							hand.hand.set(point[i], 0);
							hand.hand.set(point[i]+1, 0);
							hand.hand.set(point[i]+2, 0);
						}
						for(int i = 0; i < cards; i++) {
							if(hand.hand.get(i) != 0) temp.add(hand.hand.get(i));
						}
						hand.hand = temp;
						return Type.airplaneWithPair;
					}
				}
			}
		}
		//待扩展的牌型.....
		return Type.notValid;
	}

	//比较牌型
	@Override
	public boolean isBigger(Hand hand, Hand lastHand) {
		Type lastHandType = getType(lastHand);
		Type handType = getType(hand);
		
		if(handType == Type.pass) return true;
		if(handType == Type.rocket) return true;
		
		if(handType == lastHandType) {
			switch(handType) {
			case bomb:
				int handValue = hand.hand.size();
				int lastHandValue = lastHand.hand.size();
				if(handValue > lastHandValue) return true;
				else if(handValue < lastHandValue) return false;
				else return value(hand.hand.get(0)) > value(lastHand.hand.get(0));
			case straight:
			case pairStright:
			case airplane:
			case airplaneWithSingle:
			case airplaneWithPair:
				if(hand.hand.size() == lastHand.hand.size() && value(hand.hand.get(0)) > value(lastHand.hand.get(0))) return true;
				else return false;
			default:
				return value(hand.hand.get(0)) > value(lastHand.hand.get(0));
			}
		}else {
			if(lastHandType == Type.rocket) return false;
			if(lastHandType != Type.bomb && handType == Type.bomb) return true;
			else return false;
		}
	}
	
	private int value(int card) {
		card = card % 13;
		switch (card) {
		case JOKERKING:	return 17;
		case JOKER:		return 16;
		case TWO:		return 15;
		case 1:			return 14;		//ACE
		case 0:			return 13;		//KING
		default:		return card;	
		}
	}

	//出牌
	@Override
	public void discard( ) {
		//叫牌
		bid = new Bidding(players, players[0]);
		bid.stratBidding();
		//确认庄家
		banker = bid.getBanker();
		//拿底牌
		for(Hand hand : cardsInHands) if(hand.player.equals(banker)) hand.hand.addAll(remainCards);
		//加倍
		for(String player : players) if(!player.equals(banker)) bid.xDouble(player);
		//再加倍
		bid.xxDouble(banker);
		//打牌
		finished = false;
		player = banker;
		while(!finished) playRound(player);
		finish();
	}
	
	private void playRound(String beginner) {
		int index = 0;
		for(String str : players) {
			if (str.equals(beginner)) break;
			index++;
		}
		//第一手牌
		boolean end = false;
		while(!end) {
			if(!hasMsg) continue;
			if(!msg.player.equals(players[index])) continue;
			Type type = getType(msg);
			if(type == Type.notValid || type == Type.pass) {
//				Communication.send(this.toString(), msg.player, Type.notValid.toString());
				hasMsg = false;
				continue;
			}
			if(!contains(msg)) continue;
			currentRound = new LinkedList<Hand>();
			currentRound.add(msg);
			for(Integer card: msg.hand) cardsInHands.get(index).hand.remove(card);
			lastHand = msg;
			hasMsg = false;
			end = true;
			//通知所有玩家
			notifyObservers();
			if(cardsInHands.get(index).hand.isEmpty()) {
				playedRounds.add(currentRound);
				clearTable();
				finished = true;
				winner = players[index];
				return;
			}
			if(index == player.length() -1) index = 0;
			else index++;
		}
		//后续牌
		end = false;
		int passNum = 0;
		while(!end) {
			if(!hasMsg) continue;
			if(!msg.player.equals(players[index])) continue;
			Type type = getType(msg);
			if(type == Type.pass) {
				currentRound.add(msg);
				hasMsg = false;
				if(index == player.length() -1) index = 0;
				else index++;
				notifyObservers();
				if(++passNum == player.length() - 1) end = true;
				continue;
			}
			if(type == Type.notValid || !contains(msg) || !isBigger(msg, lastHand)) {
				hasMsg = false;
//				Communication.send(this.toString(), msg.player, Type.notValid.toString());
				continue;
			}
			currentRound.add(msg);
			for(Integer card: msg.hand) cardsInHands.get(index).hand.remove(card);
			lastHand = msg;
			hasMsg = false;
			passNum = 0;
			notifyObservers();
			if(cardsInHands.get(index).hand.isEmpty()) {
				playedRounds.add(currentRound);
				clearTable();
				finished = true;
				winner = players[index];
				return;
			}
			if(index == player.length() -1) index = 0;
			else index++;
		}
		playedRounds.add(currentRound);
		clearTable();
		player = players[index];
		return;
	}

	private boolean contains(Hand hand) {
		int index = 0;
		for(Hand inHand : cardsInHands) {
			if(inHand.player.equals(hand.player)) break;
			index++;
		}
		List<Integer> all = new LinkedList<Integer>();
		for(Integer card : cardsInHands.get(index).hand) all.add(card);
		boolean result = true;
		for(Integer card : hand.hand) if(!all.remove(card)) result = false;
		return result;
	}
	
	private void clearTable() {
		
	}
	
	private void finish() {		
		StringBuilder sb = new StringBuilder("\n本局赢家是");
		sb.append(winner.equals(banker) ? "地主":"农民") ;
		for(String name : players) {
			sb.append("\n" + name + ":");
			if(winner.equals(banker)) {
				if(name.equals(banker)) sb.append("+");
				else sb.append("-");
			}else {
				if(name.equals(banker)) sb.append("-");
				else sb.append("+");				
			}
			sb.append(Math.abs(bid.getContract(name))*base);
			sb.append("\n");
		}
		System.out.println(sb.toString());
		notifyObservers();
	}
	
	@Override
	public void registerObserver(Observer o) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void removeObserver(Observer o) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void notifyObservers() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(Message msg) {
				
	}

}
