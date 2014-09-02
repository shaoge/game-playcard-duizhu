package com.formum.duizhu.util;

import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.widget.ImageView;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.recorder.IRecorder;


public interface IUtil {
	/**
	 * 显示单个图像.
	 * @param context
	 * @param card
	 * @param viewlocation
	 * @return
	 */
	public ImageView showImage(Activity context,ICard card, int viewHost);
	/**
	 * 显示牌的背面图像
	 * @param context
	 * @param card
	 * @param viewHost
	 * @param drawableBackInt ---背面图的int
	 * @return
	 */
	public ImageView showImage(Activity context, ICard card, int viewHost,int drawableBackInt);
	/**
	 * 堆叠显示图片
	 * @param context
	 * @param card
	 * @param viewHost
	 * @param drawableBackInt
	 * @return
	 */
	public void showImageInLayers(Activity context, List<ICard> cards,int viewHost);
	
	/**
	 * 从一堆牌中分出所有当前主牌_参数1 List
	 * @param cards
	 * @return
	 */
	public List<ICard> seperateTrumpsFromCards(List<ICard> cards,IRecorder recorder);
	
	/**
	 * 比较两个数组元素数量相等
	 * @param o1
	 * @param o2
	 * @return
	 */
	public boolean isArraySizeEqual(List<ICard> firstcards,List<ICard> secondcards);
	/**
	 * 断定出牌中是否全是主牌
	 * @param cards
	 * @return
	 */
	public boolean isAllTrumpCards(List<ICard> cards);
	/**
	 * 在主牌中找出最大一张牌
	 * @param cards
	 * @return
	 */
	public ICard pickTopOneInTrumpCollection(List<ICard> cards);
	/**
	 * 在同花色中找出最大一张（非：王，将，2）
	 * @param cards
	 * @return
	 */
	public ICard topSuitCard(List<ICard> cards);
	/**
	 * 镜像有序主牌
	 * @return
	 */
	public Stack<ICard> sortMirrorTrumpCards();
	/**
	 *获得可以亮牌的将点数
	 * @return
	 */
	public int capableShowingClassPoint();
	/**
	 * 查看是否有庄家,1=有，0=无
	 * @return
	 */
	public int isThereBanker();
	/**
	 * 查是否没有同花色
	 * @param cards
	 * @return
	 */
	public boolean noRepeatSuit(List<ICard> cards);
	/**
	 * 几张牌是否各点数相等
	 * @param cards
	 * @return
	 */
	public boolean eachCardSamePoint(List<ICard> cards);
	/**
	 * 是否有王
	 * @param cards
	 * @return
	 */
	public boolean isthereJoker(List<ICard> cards);
	/**
	 * 是否同花色副牌
	 * @param cards
	 * @return
	 */
	public boolean isSameNoTrumpCardsSuit(List<ICard> cards);
	/**
	 * 是否同点数将点牌
	 * @param cards
	 * @return
	 */
	public boolean isSameClassPoint(List<ICard> cards);
	/**
	 * 断定是否全是不含王的异花“2”牌
	 * @param cards
	 * @return
	 */
	public boolean isSameCard2(List<ICard> cards);
	/**
	 * 给各选手牌授阶——方便单张比较大小
	 * 返加1，表示完
	 * @return
	 */
	public int grantTenTrumpCardsAndAceRank();
	/**
	 * 花色清单
	 * @param cards
	 * @return
	 */
	public List<String> suitList(List<ICard> cards);
	/**
	 * 从集合中查出单花色牌数量
	 * @param cards
	 * @param suitName
	 * @return
	 */
	public int countSingleSuit(List<ICard> cards,String suitName);
/**
 * 给玩家发庄标识
 * @param player
 * @return int 1 equals success
 */
	public int putBankerOn(Player player);
	
	/**
	 * 分离翁牌
	 * @param cards
	 * @return List
	 */
	public List<ICard> seperate4SamePointCards(List<ICard> cards);

	/**
	 * 找出最大权值单牌
	 * @param cards
	 * @return List
	 */
	public List<ICard> confirmTopCard(List<ICard> cards);
	/**
	 * 分数和
	 * @param cards
	 * @return int
	 */
	public int sumScores(List<ICard> cards);
	/**
	 * 分离某花色副牌中的甩牌
	 * @param cards
	 * @param suit
	 * @return List
	 */
	public List<ICard> seperateStraightCards(List<ICard> cards) ;
	/**
	 * 分离降序排列的单色牌
	 * @param cards
	 * @param suit
	 * @return List
	 */
	public List<ICard> seperateSameSuitCardsSortedDesc(List<ICard> cards,String suit);
	/**
	 * 分离一色标准花色的副牌并降序（要特别考虑ACE）--共11张牌_以54张牌为基础
	 * @param allCards
	 * @param suit
	 * @return
	 */
	public List<ICard> seperateStandardNoTrumpSameSuitSortedDesc(String suit);
	/**
	 * 为同色在手副牌排降序(要特别考虑ACE)
	 * @param noTrumpCardsInHand
	 * @return
	 */
	public List<ICard> sortNoTrumpSameSuitInHandDesc(List<ICard> noTrumpSameSuitCardsInHand);
/**
 * 分出单色副牌集_任意牌
 * @param cards
 * @param suit
 * @return
 */
	public List<ICard> seperateNoTrumpSameSuitCards(List<ICard> cards,String suit);
/**
 * 分出多色副牌集_任意牌
 * @param cards
 * @return
 */
	public List<ICard> seperateNoTrumpCards(List<ICard> cards);
	
	/**
	 * 推断对手在手的副牌状态
	 * @return
	 */
	public List<String> reasoningOpponentEmptyNorTrumpSuit(List<ICard> myCards_fore,List<ICard> opponentCards_after);

	/**
	 * 推断对方是否有主
	 * @param forehandCards
	 * @param afterhandCards
	 * @return
	 */
	public boolean reasoningOpponentTrump(List<ICard> myCards_fore,List<ICard> opponentCards_after);
	/**
	 * 推断对方某花色是否有分
	 * @param suit
	 * @return
	 */
	public boolean reasoningOpponentScoresInOneSuit(String suit);

	/**
	 * 将单色牌中ACE放入末端
	 * @param cardsWithAce
	 */
	public void swapAceFromTopToBottomForOneSuitCards(List<ICard> cardsWithAce);
	/**
	 * 翻译英文花色成中文
	 * @param suit
	 * @return
	 */
	public String changeSuitFromEnglishToChinese(String suit);
	
	/**
	 * 亮主牌花色前的排序：各花色+6大调
	 * @param myCardsFetched
	 * @return
	 */
	public List<ICard> sortCardsBeforeShowingTrumpSuit(List<ICard> myCardsFetched);
	/**
	 * 亮主牌花色前的排序：副牌各分，主牌+6大调
	 * @param myCardsFetched
	 * @return
	 */
	public List<ICard> sortCardsAfterShowingTrumpSuit(List<ICard> myCardsFetched);
	/**
	 * 在牌桌上显示出牌
	 * @param outCards
	 */
	public void showOutCardOnTable(Activity context,List<ICard> outCards);
	/**
	 * 向记录中记录双方的一轮出牌
	 * @param cardsOut
	 * @param robot
	 * @param recorder
	 */
	public void recordBothSideOutCards(List<ICard> cardsOut, Player player,
			IRecorder recorder);
	/**
	 * 查看是不是先手
	 * @param human
	 * @param recorder
	 */
	public boolean meOnTheOffensive(Player me, IRecorder recorder);
	/**
	 * 根据Tag删除碎片
	 * @param string
	 */
	public void removeFragmentByTag(Activity context,String string);
	/**
	 * 通知机器人测算人类是否有主
	 * @param foreCards
	 * @param humanPutOutCards
	 */
	public void informRobot_HumanTrumpStatus(List<ICard> foreCards, List<ICard> humanPutOutCards);
	
}
