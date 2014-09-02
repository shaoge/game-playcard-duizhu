package com.formum.duizhu.cardDeck;

import java.util.List;

import com.formum.duizhu.cardDeck.card.ICard;

public interface ICardDeck {
	/**
	 *洗牌.
	 *@deprecated
	 */
	public void shuffleCards();
	/**
	 * 发牌.
	 * @return
	 */
	public ICard dealCard();
	/**
	 * 发指定牌.
	 * @return
	 */
	public ICard dealTheCard(String suit,int point);
	/**
	 * 牌张数.
	 * @return
	 */
	public int size();
	/**
	 * 收回打出的牌.
	 * @param cards
	 */
	public void retrieveCards(List<ICard> cards);

	/**
	 * 重获一副牌
	 * @return
	 */
	public int reNewCards();
	/**
	 * 获重
	 * @return
	 */
	public List<ICard> getCardsMirror();
	/**
	 * 无王剩余的牌
	 * @return
	 */
	public List<ICard> getNoJokersCardsLefted();
}
