package core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import common.Hand;

public class Poker {
	/*
	 * 扑克牌编码表
	 * 不出：	0
	 * 大王：	-2
	 * 小王：	-1
	 * 			方块	草花	红心	黑桃
	 * 2：		2	15	28	41
	 * 3：		3	16	29	42
	 * 4：		4	17	30	43
	 * 5：		5	18	31	44
	 * 6：		6	19	32	45
	 * 7：		7	20	33	46
	 * 8：		8	21	34	47
	 * 9：		9	22	35	48
	 * 10：		10	23	36	49
	 * J：		11	24	37	50
	 * Q:		12	25	38	51
	 * K:		13	26	39	52
	 * A:		14	27	40	53
	 */
	protected static final int JOKERKING = -2;		//大王
	protected static final int JOKER = -1;			//小王
	protected static final int TWO = 2;				//2
	protected static final int THREE = 3;			//3
	protected static final int FOUR = 4;			//4
	protected static final int FIVE = 5;			//5
	protected static final int SIX = 6;				//6
	protected static final int SEVEN = 7;			//7
	protected static final int EIGHT = 8;			//8
	protected static final int NINE = 9;			//9
	protected static final int TEN = 10;			//10
	protected static final int JACK = 11;			//J
	protected static final int QUEEN = 12;			//Q
	protected static final int KING = 13;			//K
	protected static final int ACE = 14;			//A
	protected static final int PASS = 0;			//pass
	
	protected List<Integer> remainCards = new LinkedList<Integer>();//台面剩下的牌
	protected List<Hand> cardsInHands = new LinkedList<Hand>();		//手上未出的牌
	protected Hand lastHand;										//上一手牌
	protected List<Hand> currentRound;								//当前出的牌
	protected List<List<Hand>> playedRounds = new LinkedList<List<Hand>>();	//已打出的牌
	protected String[] players;										//所有玩家
	protected String banker;										//庄家
	protected String player;										//当前出牌者
	protected int suits;											//几副牌
	protected boolean hasJoker;										//是否含“王”
	
	public Poker(int suits, boolean hasJoker, String[] players){
		this.suits = suits;
		this.hasJoker = hasJoker;
		this.players = players;
		remainCards = new LinkedList<Integer>();
		for (int i = 1 ; i <= suits; i++) {
			for (int j = 2; j < 54; j++) {
				remainCards.add(j);
			}
			if(hasJoker) {
				remainCards.add(JOKER);
				remainCards.add(JOKERKING);
			}
		}
		//洗牌
		Collections.shuffle(remainCards);
		Collections.shuffle(remainCards);
		Collections.shuffle(remainCards);
	}
	
}
