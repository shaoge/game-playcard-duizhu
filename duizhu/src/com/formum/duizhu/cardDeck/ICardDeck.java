package com.formum.duizhu.cardDeck;

import java.util.List;

import com.formum.duizhu.cardDeck.card.ICard;

public interface ICardDeck {
	/**
	 *ϴ��.
	 *@deprecated
	 */
	public void shuffleCards();
	/**
	 * ����.
	 * @return
	 */
	public ICard dealCard();
	/**
	 * ��ָ����.
	 * @return
	 */
	public ICard dealTheCard(String suit,int point);
	/**
	 * ������.
	 * @return
	 */
	public int size();
	/**
	 * �ջش������.
	 * @param cards
	 */
	public void retrieveCards(List<ICard> cards);

	/**
	 * �ػ�һ����
	 * @return
	 */
	public int reNewCards();
	/**
	 * ����
	 * @return
	 */
	public List<ICard> getCardsMirror();
	/**
	 * ����ʣ�����
	 * @return
	 */
	public List<ICard> getNoJokersCardsLefted();
}
