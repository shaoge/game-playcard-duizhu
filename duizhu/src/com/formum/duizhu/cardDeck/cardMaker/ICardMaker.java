package com.formum.duizhu.cardDeck.cardMaker;

import com.formum.duizhu.cardDeck.card.ICard;

public interface ICardMaker {

	/**
	 * 按要求造牌
	 * @param suit
	 * @param point
	 * @return
	 */
	public  ICard makeCard(String suit,int point);
}
