package com.formum.duizhu.player;

import java.util.List;
import java.util.Map;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.recorder.IRecorder;


public interface IPlayer {

	/**
	 * 亮主
	 * @param card
	 * @return
	 */
	public Map<?,?> selectTrumpSuit(ICard card);
	
	/**
	 *  抓牌
	 * @param card
	 */
	public void fetchCard(ICard card);
	/**
	 * 将当前主牌和花色标给自己的牌套(观察者模式)
	 * @param trumpSuit
	 */
	public void updateCurrentTrumpSuit(String currentTrumpSuit);
	/**
	 * 将当前主牌和局次点标给自己的牌套(观察者模式)
	 * @param currentClassPoint
	 */
	public void updateCurrentClassPoint(int currentClassPoint);
	
	
	/**
	 * 试探出牌
	 * @param readyOutCards
	 * @return
	 */
	public  List<ICard> tryPutCardsOut( List<ICard> testOutCards);
	
	/**
	 *  出牌
	 * @param cardDrawable_Ids
	 * @return
	 */
	public List<ICard> putCardsOut( List<ICard> readyOutCards);
	
	/**
	 * 出具体牌
	 * @param suit
	 * @param point
	 * @return
	 */
	public ICard putCardOut(String suit,int point);
	
	/**
	 *  手中一副牌
	 * @return
	 */
	public List<ICard> getSelfCardDeck();
	/**
	 * 查手中牌数
	 * @return
	 */
	public int countCards();
	/**
	 * 计算牌分数
	 * @return
	 */
	public int sumScoreInHand();	
/**
 * 显示主牌花色
 * @return
 */
	public String getTrumpSuit();
	/**
	 * 显示主牌将点
	 * @return
	 */
	public int getTrumpPoint();
	/**
	 * 获得玩家名
	 * @return
	 */
	public String getPlayerName();
/**
 * 设置玩家名
 * @param playerName
 */
	public void setPlayerName(String playerName) ;
	/**
	 * 在记录员上登记
	 * @param recorder
	 */
	public void registerOnRecorder(IRecorder recorder,Player player);



	public int getMyClassPoint() ;
	public void setMyClassPoint(int myClassPoint) ;
	
	public boolean isBanker();
	
	public void setBanker(boolean isBanker);
	
	public int getCurrentScore();
	public void setCurrentScore(int currentScore);

	public boolean isOpponentTrump();
	public void setOpponentTrump(boolean isOpponentTrump) ;
	public List<String> getOpponentNorTrumpSuits() ;
	public void setOpponentNorTrumpSuits(List<String> opponentNorTrumpSuits) ;

	
	/**
	 * 初始猜测对方现存副牌花色
	 * @param currentTrumpSuit
	 */
	public void initialDefaultOpponentNorTrumpSuits(String currentTrumpSuit);
	
	/**
	 * 亮主
	 * @return
	 */
	public ICard showTrumpSuit();
	/**
	 * 向玩家牌套注入牌
	 * @param playerCardDeck
	 */
	public void setPlayerCardDeck(List<ICard> playerCardDeck) ;
	/**
	 *获得 上局级数
	 * @return
	 */
	public int getMyLastSetClassPoint() ;
/**
 *  设置上局级数
 * @param myLastSetClassPoint
 */
	public void setMyLastSetClassPoint(int myLastSetClassPoint) ;
}
