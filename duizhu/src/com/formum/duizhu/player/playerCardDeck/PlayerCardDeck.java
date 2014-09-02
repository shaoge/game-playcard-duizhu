package com.formum.duizhu.player.playerCardDeck;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.formum.duizhu.cardDeck.card.ICard;

public class PlayerCardDeck implements IPlayerCardDeck {

	private String trumpSuit = "";// 主牌
	private int trumpPoint = 0;// 局次主牌将点

	private List<ICard> playerCardDeck = new ArrayList<ICard>();// CardDeckOfOneHand



	public PlayerCardDeck() {
	}

	@Override
	public void inCard(ICard card) {
		playerCardDeck.add(card);
	}

	public List<ICard> getPlayerCardDeck() {
	    
		return this.playerCardDeck;
	}

	@Override
	public int sumCardScore(IPlayerCardDeck playerCardDeck) {
		List<ICard> deck = playerCardDeck.getPlayerCardDeck();
		int sum = 0;

		for (ICard card : deck) {
			if (card.getPoint() == 5) {
				sum += 5;
			}
			if (card.getPoint() == 10 || card.getPoint() == 13) {
				sum += 10;
			}

		}
		return sum;
	}

	@Override
	public List<ICard> outCards(List<ICard> readyOutCards) {

	    playerCardDeck.removeAll(readyOutCards);
		return readyOutCards;
	}

	
	public String getTrumpSuit() {
		return trumpSuit;
	}

	public void setTrumpSuit(String trumpSuit) {
		this.trumpSuit = trumpSuit;
	}

	public int getTrumpPoint() {
		return trumpPoint;
	}

	public void setTrumpPoint(int trumpPoint) {
		this.trumpPoint = trumpPoint;
	}

	@Override
	public int size() {
		return this.playerCardDeck.size();
	}

	@Override
	public ICard outCard(String suit, int point) {
		ICard card = null;
		// 从手牌中找出，并从手牌中减掉
		for (ICard iCard : playerCardDeck) {
			boolean itIs = ((iCard.getSuit().equals(suit)) && (iCard.getPoint() == point));
			if (itIs) {
				card = iCard;
				playerCardDeck.remove(iCard);
				break;
			}
		}
		return card;
	}
	public void setPlayerCardDeck(List<ICard> playerCardDeck) {
	    this.playerCardDeck = playerCardDeck;
	}

}
