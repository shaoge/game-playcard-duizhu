package com.formum.duizhu.gameRule;

import java.util.List;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.player.HumanPlayer;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.recorder.IRecorder;

public interface IGameRule {
	/**
	 * 每玩家手牌允许张数
	 */
	public static final int PLAYER_CARDS_SIZE=13;
/**
 * 牌分定义
 */
	public static final int CARD_5_EQUAL_SCORE=5;
	public static final int CARD_10_EQUAL_SCORE=10;
	public static final int CARD_13_EQUAL_SCORE=10;
	
	/**
	 * 每局破分底线
	 */
	public static final int BREAK_SET_SCORES_BOTTOM_LINE=30;
	/**
	 * 庄家打光允许对方得分
	 */
	public static final int BOTH_WIN_SCORE_PERMISSION=0;
	/**
	 * 庄家多升一级的分数线
	 */
	public static final int SET_ADD_BANKER_CLASS_SCORE=0;
	/**
	 * 每局规定次序段
	 */
	public static final int ROUND_STAGE_READY=0;
	public static final int ROUND_STAGE_DEAL=1;
	public static final int ROUND_STAGE_BATTLE=2;
	public static final int ROUND_STAGE_END=3;
	/**
	 * 每场游戏起点局次点数
	 */
	public static final int INITIAL_CLASSPOINT=3;
	/**
	 * 取胜必须打到的点数--即打到A的意思
	 */
	public static final int WINNING_CLASSPOINT=1;
	public static final int STRAIGHT_SUIT_CARDS=2;

//-----------------------------开始攻守前的准备-----------------------------------------------------	
/**
 * 亮牌是否合法
 * 思路：如果亮牌 1、记录中没有主牌花色  2、必须是当前应打局点数   3、不是joker;
 * @param cardShowedByplayer
 * @return
 */
	public boolean isShowingCard4ConfirmTrumpSuitRight(ICard cardShowedByplayer);
	
	/**
	 * 是否确定主牌，否则双方不能出牌
	 * @param recorder
	 * @return
	 */
	public boolean isThereTrumpSuit(IRecorder recorder);
	
	//-------------------------------先手------------------------------

	

	/**
	 *先出牌，断定甩副牌合法
	 * @param cards
	 * @return
	 */
	public boolean isRight2PreSameNonTrumpSuitGroupOut(List<ICard> cards,Player opponentPlayer);
	/**
	 * 先出牌，断定主牌多张是否合法（除2，局将的翁牌，都是不合法的）
	 * @param cards
	 * @return
	 */
	public boolean isRight2PreTrumpGroupOut(List<ICard> cards);
	
	//---------------------------------共用-------------------------------------
	/**
	 * 断定是不是翁牌--四张异色同点数牌，先出相当于主牌
	 * @param cards
	 * @return
	 */
	public boolean is4SamePointCardsAsTrumpCards(List<ICard> cards);
	
	//---------------------------------后手-------------------------------------
	/**
	 * 后手出牌数与前手相等
	 * @param foreCards
	 * @param afterCards
	 * @return
	 */
	public boolean isSameAfterSize(List<ICard> foreCards,List<ICard> afterCards);
	/**
	 *后出牌，先手出主牌单张，后手不得以副牌充主牌
	 * @param list
	 * @return
	 */
	public boolean isRight2FollowOneTrumpCardOut(List<ICard> foreCards,List<ICard> afterCards); 
	/**
	 * 后出牌，先手出副牌单张，后手如果有，则不得以其它副牌或主牌
	 * @param foreCards
	 * @param afterCards
	 * @return
	 */
	public boolean isRight2FollowOneNonTrumpCardOut(List<ICard> foreCards,List<ICard> afterCards); 
	/**
	 * 后出牌，先手翁牌，只有同样翁牌，或主，或主不足补副牌是正确的
	 * @param foreCards
	 * @param afterCards
	 * @return
	 */
	public boolean isRight2Follow4SameCardsOut(List<ICard> foreCards,List<ICard> afterCards); 
	
	/**
	 * 后出牌，先手甩副，后手有同花色的副牌不得隐藏
	 * @param foreCards
	 * @param afterCards
	 * @return
	 */
	public boolean isRight2FollowStraightGroupOut(List<ICard> foreCards,List<ICard> afterCards);
	//--------------------------------节点-------------------------------
	/**
	 *  每次出牌的攻守胜方（先出的为攻，后出的为守）
	 * @param offensiveCard
	 * @param defensiveCard
	 * @return
	 */
	public Player whoIsRoundWinner(List<ICard> offensiveCard,List<ICard> defensiveCard);

	/**
	 * 每局胜方
	 * @param players
	 * @return
	 */
	public Player whoIsSetWinner(Player roundWinner);
	
	/**
	 * 断定先打到A的胜家
	 * @param players
	 * @return
	 */
	public Player whoIsGameWinner(Player setWinner);
	/**
	 * 玩家是否可以重新洗牌
	 * @param player
	 * @return boolean
	 */
	public boolean mayReDealCards(Player player);
	
/**
 * 本家可否出牌
 * @param human
 * @return
 */
	public boolean mayIPutOutCards(Player me);
}
