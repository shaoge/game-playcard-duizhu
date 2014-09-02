package com.formum.duizhu.player.playerCardDeck;

import java.util.List;

import com.formum.duizhu.cardDeck.card.ICard;

public interface IPlayerCardDeck{
	/**
	 * 单张 进牌
	 * 
	 * @param card
	 */
	public void inCard(ICard card);

	/**
	 * 整理出手牌
	 * 
	 * @return
	 */
	public List<ICard> getPlayerCardDeck();

	/**
	 * 批量流出牌
	 * 
	 * @param cardDrawable_Ids
	 * @return
	 */
	public List<ICard> outCards(List<ICard> readyOutCards);

	/**
	 * 单张确定流出牌
	 * @param suit
	 * @param point
	 * @return
	 */
	public ICard outCard(String suit,int point);
	
	/**
	 * 手牌总分数
	 * @param obj
	 * @return
	 */
	
	public int sumCardScore(IPlayerCardDeck playerCardDeck);
/**
 * 设置主牌花色
 * @param trumpSuit
 */
	public void setTrumpSuit(String trumpSuit);
/**
 * 设置主牌点
 * @param trumpPoint
 */
	public void setTrumpPoint(int trumpPoint);
/**
 * 获得主牌花色
 * @return
 */
	public String getTrumpSuit();
 /**
  * 获得主牌点
  * @return
  */
	public int getTrumpPoint();
/**
 *  获得手牌数
 * @return
 */
	public int size();
	/**
	 * 一次性灌牌入套
	 * @param playerCardDeck
	 */
	public void setPlayerCardDeck(List<ICard> playerCardDeck);
}
