package com.formum.duizhu.cardDeck.cardMaker;

import com.formum.duizhu.cardDeck.card.ICard;

public interface ICardMaker {

	/**
	 * ��Ҫ������
	 * @param suit
	 * @param point
	 * @return
	 */
	public  ICard makeCard(String suit,int point);
}
