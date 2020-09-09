package core;

import common.Hand;

public interface PokerGame {
	
	public enum Type {bomb, rocket, single, pair, triple, straight, tripleWithSingle, tripleWithPair,
		fourWith2, fourWith2Pair, pairStright, airplane, airplaneWithSingle, airplaneWithPair, pass,
		notValid
	}

	public void deal(String[] players);
	public void sort(Hand hand);
	public Type getType(Hand hand);
	public boolean isBigger(Hand hand, Hand lastHand);
	public void discard( );
/*
1、四个王，称为天尊，什么牌都可以打，是最大的牌。
2、8个一样的，如八个Q，威力紧次于天尊，被称为天炸。
3、7个一样的，如七个J，威力小于天炸，被称为导弹(民间称七巧)。
4、6个一样的，如六个10，威力小于导弹，被称为火箭(民间称六喜)。
5、5个一样的，如五个9，威力小于火箭，被称为炮。
6、4个一样的，如四个8，威力小于炮，被称为枪。
以上牌除比自己大的炸弹外，什么牌型都可打。
7、单牌(一手牌)：单个牌。
8、对牌(一手牌)：数值相同的两张牌。
9、三张牌：数值相同的三张牌(如三个10)。
10、三带一对：数值相同的三张牌+一对牌。例如：333+44 只能带对子。
11、五张或五张以上牌点连续的牌，花色不限。例如：3、4、5、6、7、8、9、10、J、Q、K等。注意：可以选择1，2，3，4，5；或者2，3，4，5，6，或者10，J，Q，K，A。
12、双顺：三对或更多的连续对牌(如：334455、88991010JJ)。包括AA2233，223344，QQKKAA
13、三顺：二个或更多的连续三张牌(如：333444、444555666777)。包括AAA222333,222333444,QQQKKKAAA。也叫飞机不带翅膀。
14、飞机带翅膀。三顺+同数量的一对牌。例如：333444+6677(双飞)333444555+667788(三飞)333444555666+7788991010(四飞)等。注意，所带的一对牌，必须是连顺：333444+6688
牌型比较编辑
天尊是最大的牌。天炸，导弹，火箭，炮，枪除了比自己大的炸弹外，比其它牌型都大。一般牌型而言，只有当牌型相同和总张数相同的牌，才可比较大小。其中像三带二、飞机带翅膀等组合牌型，只要比较其牌数最多牌值就行。只有比当前出的牌(场牌)大的牌才能出。
*/

}
