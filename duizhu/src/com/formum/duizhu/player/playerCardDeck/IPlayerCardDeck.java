package com.formum.duizhu.player.playerCardDeck;

import java.util.List;

import com.formum.duizhu.cardDeck.card.ICard;

public interface IPlayerCardDeck{
	/**
	 * ���� ����
	 * 
	 * @param card
	 */
	public void inCard(ICard card);

	/**
	 * ���������
	 * 
	 * @return
	 */
	public List<ICard> getPlayerCardDeck();

	/**
	 * ����������
	 * 
	 * @param cardDrawable_Ids
	 * @return
	 */
	public List<ICard> outCards(List<ICard> readyOutCards);

	/**
	 * ����ȷ��������
	 * @param suit
	 * @param point
	 * @return
	 */
	public ICard outCard(String suit,int point);
	
	/**
	 * �����ܷ���
	 * @param obj
	 * @return
	 */
	
	public int sumCardScore(IPlayerCardDeck playerCardDeck);
/**
 * �������ƻ�ɫ
 * @param trumpSuit
 */
	public void setTrumpSuit(String trumpSuit);
/**
 * �������Ƶ�
 * @param trumpPoint
 */
	public void setTrumpPoint(int trumpPoint);
/**
 * ������ƻ�ɫ
 * @return
 */
	public String getTrumpSuit();
 /**
  * ������Ƶ�
  * @return
  */
	public int getTrumpPoint();
/**
 *  ���������
 * @return
 */
	public int size();
	/**
	 * һ���Թ�������
	 * @param playerCardDeck
	 */
	public void setPlayerCardDeck(List<ICard> playerCardDeck);
}
