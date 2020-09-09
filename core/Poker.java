package core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import common.Hand;

public class Poker {
	/*
	 * �˿��Ʊ����
	 * ������	0
	 * ������	-2
	 * С����	-1
	 * 			����	�ݻ�	����	����
	 * 2��		2	15	28	41
	 * 3��		3	16	29	42
	 * 4��		4	17	30	43
	 * 5��		5	18	31	44
	 * 6��		6	19	32	45
	 * 7��		7	20	33	46
	 * 8��		8	21	34	47
	 * 9��		9	22	35	48
	 * 10��		10	23	36	49
	 * J��		11	24	37	50
	 * Q:		12	25	38	51
	 * K:		13	26	39	52
	 * A:		14	27	40	53
	 */
	protected static final int JOKERKING = -2;		//����
	protected static final int JOKER = -1;			//С��
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
	
	protected List<Integer> remainCards = new LinkedList<Integer>();//̨��ʣ�µ���
	protected List<Hand> cardsInHands = new LinkedList<Hand>();		//����δ������
	protected Hand lastHand;										//��һ����
	protected List<Hand> currentRound;								//��ǰ������
	protected List<List<Hand>> playedRounds = new LinkedList<List<Hand>>();	//�Ѵ������
	protected String[] players;										//�������
	protected String banker;										//ׯ��
	protected String player;										//��ǰ������
	protected int suits;											//������
	protected boolean hasJoker;										//�Ƿ񺬡�����
	
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
		//ϴ��
		Collections.shuffle(remainCards);
		Collections.shuffle(remainCards);
		Collections.shuffle(remainCards);
	}
	
}
