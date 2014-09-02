package com.formum.duizhu.gameRule;

import java.util.List;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.player.HumanPlayer;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.recorder.IRecorder;

public interface IGameRule {
	/**
	 * ÿ���������������
	 */
	public static final int PLAYER_CARDS_SIZE=13;
/**
 * �Ʒֶ���
 */
	public static final int CARD_5_EQUAL_SCORE=5;
	public static final int CARD_10_EQUAL_SCORE=10;
	public static final int CARD_13_EQUAL_SCORE=10;
	
	/**
	 * ÿ���Ʒֵ���
	 */
	public static final int BREAK_SET_SCORES_BOTTOM_LINE=30;
	/**
	 * ׯ�Ҵ������Է��÷�
	 */
	public static final int BOTH_WIN_SCORE_PERMISSION=0;
	/**
	 * ׯ�Ҷ���һ���ķ�����
	 */
	public static final int SET_ADD_BANKER_CLASS_SCORE=0;
	/**
	 * ÿ�ֹ涨�����
	 */
	public static final int ROUND_STAGE_READY=0;
	public static final int ROUND_STAGE_DEAL=1;
	public static final int ROUND_STAGE_BATTLE=2;
	public static final int ROUND_STAGE_END=3;
	/**
	 * ÿ����Ϸ���ִε���
	 */
	public static final int INITIAL_CLASSPOINT=3;
	/**
	 * ȡʤ����򵽵ĵ���--����A����˼
	 */
	public static final int WINNING_CLASSPOINT=1;
	public static final int STRAIGHT_SUIT_CARDS=2;

//-----------------------------��ʼ����ǰ��׼��-----------------------------------------------------	
/**
 * �����Ƿ�Ϸ�
 * ˼·��������� 1����¼��û�����ƻ�ɫ  2�������ǵ�ǰӦ��ֵ���   3������joker;
 * @param cardShowedByplayer
 * @return
 */
	public boolean isShowingCard4ConfirmTrumpSuitRight(ICard cardShowedByplayer);
	
	/**
	 * �Ƿ�ȷ�����ƣ�����˫�����ܳ���
	 * @param recorder
	 * @return
	 */
	public boolean isThereTrumpSuit(IRecorder recorder);
	
	//-------------------------------����------------------------------

	

	/**
	 *�ȳ��ƣ��϶�˦���ƺϷ�
	 * @param cards
	 * @return
	 */
	public boolean isRight2PreSameNonTrumpSuitGroupOut(List<ICard> cards,Player opponentPlayer);
	/**
	 * �ȳ��ƣ��϶����ƶ����Ƿ�Ϸ�����2���ֽ������ƣ����ǲ��Ϸ��ģ�
	 * @param cards
	 * @return
	 */
	public boolean isRight2PreTrumpGroupOut(List<ICard> cards);
	
	//---------------------------------����-------------------------------------
	/**
	 * �϶��ǲ�������--������ɫͬ�����ƣ��ȳ��൱������
	 * @param cards
	 * @return
	 */
	public boolean is4SamePointCardsAsTrumpCards(List<ICard> cards);
	
	//---------------------------------����-------------------------------------
	/**
	 * ���ֳ�������ǰ�����
	 * @param foreCards
	 * @param afterCards
	 * @return
	 */
	public boolean isSameAfterSize(List<ICard> foreCards,List<ICard> afterCards);
	/**
	 *����ƣ����ֳ����Ƶ��ţ����ֲ����Ը��Ƴ�����
	 * @param list
	 * @return
	 */
	public boolean isRight2FollowOneTrumpCardOut(List<ICard> foreCards,List<ICard> afterCards); 
	/**
	 * ����ƣ����ֳ����Ƶ��ţ���������У��򲻵����������ƻ�����
	 * @param foreCards
	 * @param afterCards
	 * @return
	 */
	public boolean isRight2FollowOneNonTrumpCardOut(List<ICard> foreCards,List<ICard> afterCards); 
	/**
	 * ����ƣ��������ƣ�ֻ��ͬ�����ƣ��������������㲹��������ȷ��
	 * @param foreCards
	 * @param afterCards
	 * @return
	 */
	public boolean isRight2Follow4SameCardsOut(List<ICard> foreCards,List<ICard> afterCards); 
	
	/**
	 * ����ƣ�����˦����������ͬ��ɫ�ĸ��Ʋ�������
	 * @param foreCards
	 * @param afterCards
	 * @return
	 */
	public boolean isRight2FollowStraightGroupOut(List<ICard> foreCards,List<ICard> afterCards);
	//--------------------------------�ڵ�-------------------------------
	/**
	 *  ÿ�γ��ƵĹ���ʤ�����ȳ���Ϊ���������Ϊ�أ�
	 * @param offensiveCard
	 * @param defensiveCard
	 * @return
	 */
	public Player whoIsRoundWinner(List<ICard> offensiveCard,List<ICard> defensiveCard);

	/**
	 * ÿ��ʤ��
	 * @param players
	 * @return
	 */
	public Player whoIsSetWinner(Player roundWinner);
	
	/**
	 * �϶��ȴ�A��ʤ��
	 * @param players
	 * @return
	 */
	public Player whoIsGameWinner(Player setWinner);
	/**
	 * ����Ƿ��������ϴ��
	 * @param player
	 * @return boolean
	 */
	public boolean mayReDealCards(Player player);
	
/**
 * ���ҿɷ����
 * @param human
 * @return
 */
	public boolean mayIPutOutCards(Player me);
}
