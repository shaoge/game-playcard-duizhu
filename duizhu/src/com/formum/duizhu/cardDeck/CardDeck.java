package com.formum.duizhu.cardDeck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import com.formum.duizhu.cardDeck.card.Card;
import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.cardDeck.cardMaker.CardMaker;
import com.formum.duizhu.cardDeck.cardMaker.ICardMaker;
import com.formum.duizhu.player.GeneralPlay;
import com.formum.duizhu.player.HumanPlayer;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.recorder.IRecorder;
import com.formum.duizhu.recorder.Recorder;
import com.formum.duizhu.util.IUtil;
import com.formum.duizhu.util.Util;

public class CardDeck implements ICardDeck {

    // ����
    private static volatile CardDeck INSTANCE = null;
    // ջʽ����
    private Stack<ICard> cardDeck = new Stack<ICard>();
    // ����
    private Stack<ICard> cardMirror = new Stack<ICard>();

    private IUtil tools = Util.getInstance();
    private Player human = HumanPlayer.getInstance(GeneralPlay.getInstance());
    private IRecorder recorder = Recorder.getInstance();

    private CardDeck() {
	// ��ʵ����
	this.possessCards();
	// ����һ��
	if (cardDeck.size() == 54) {
	    this.mirrorCards();
	}
    }

    public static CardDeck getInstance() {
	if (INSTANCE == null) {
	    synchronized (CardDeck.class) {
		// when more than two threads run into the first null check same
		// time, to avoid instanced more than one time, it needs to be
		// checked again.
		if (INSTANCE == null) {
		    INSTANCE = new CardDeck();
		}
	    }
	}
	return INSTANCE;
    }

    // ϴ��
    @Deprecated
    @Override
    public void shuffleCards() {
	Map<Integer, ICard> map = new HashMap<Integer, ICard>();
	// [...] fill the map
	for (int i = 0; i < 54; i++) {
	    map.put(i, cardDeck.pop());
	}

	List<Integer> keys = new ArrayList<Integer>(map.keySet());
	Collections.shuffle(keys);
	if (cardDeck.isEmpty()) {
	    for (Integer o : keys) {
		// Access keys/values in a random order
		ICard card = map.get(o);
		cardDeck.push((Card) card);

	    }
	} else {
	    cardDeck.clear();
	    for (Integer o : keys) {
		ICard card = map.get(o);
		cardDeck.push((Card) card);
	    }
	}
    }

    // ����
    @Override
    public ICard dealCard() {
	ICard card = null;
	if (!cardDeck.isEmpty()) {
	    card = cardDeck.pop();
	}
	return card;
    }

    /**
     * һ�������˼Ҹ��˵ķ�ˮһ�Ŵ��Ƴ���+ϴ�Ƴ�����
     */
    private void shuffleAndOneOfSixBigCards() {

	// ת��List
	List<ICard> tmpLst = new ArrayList<ICard>();
	for (ICard iCard : cardDeck) {
	    tmpLst.add(iCard);
	}
	// ��ϴ��
	Collections.shuffle(tmpLst);
	// ���ѡһ��6���֮һ
	int cardIndex = findOneOfSixIndex(tmpLst);
	// �϶�human�Ⱥ��֣�
	boolean humanFirst = recorder.getSetWinner() == human || recorder.getSetWinner() == null;
	if (humanFirst) {
	    Collections.swap(tmpLst, cardIndex, 43);
	} else {
	    Collections.swap(tmpLst, cardIndex, 44);
	}
	// ����ѹ�ư�
	cardDeck.clear();
	for (ICard iCard : tmpLst) {
	    cardDeck.push(iCard);
	}

    }

    private int findOneOfSixIndex(List<ICard> tmplst) {
	int result = 0;
	Random rdm = new Random();
	int key = rdm.nextInt(2);
	switch (key) {
	case 0:
	    for (ICard iCard : tmplst) {
		boolean joker = iCard.getSuit().equals("joker");
		if (joker) {
		    result = tmplst.indexOf(iCard);
		    break;
		}
	    }
	    break;

	case 1:
	    for (ICard iCard : tmplst) {
		boolean classPointCard = !iCard.getSuit().equals("joker") && iCard.getPoint() == recorder.getCurrentClassPoint();
		if (classPointCard) {
		    result = tmplst.indexOf(iCard);
		    break;
		}
	    }
/*	    for (ICard iCard : tmplst) {
		boolean card2 = !iCard.getSuit().equals("joker") && iCard.getPoint() == 2;
		if (card2) {
		    result = tmplst.indexOf(iCard);
		    break;
		}
	    }
*/	    break;
	default:
	    break;
	}
	return result;
    }

    @Override
    public ICard dealTheCard(String suit, int point) {
	ICard card = null;

	for (ICard theCard : this.cardDeck) {
	    if (theCard.getSuit().equals(suit) && theCard.getPoint() == point) {
		card = theCard;
		cardDeck.remove(theCard);
		break;
	    }
	}

	return card;
    }

    @Override
    public int size() {
	return cardDeck.size();
    }

    @Override
    public void retrieveCards(List<ICard> cards) {
	for (ICard card : cards) {
	    cardDeck.push(card);
	}
    }

    private int possessCards() {
	int result = 0;
	try {
	    // ����ӡ�ƻ�
	    ICardMaker cardmaker = (CardMaker) CardMaker.getInstance();
	    // ����-->����
	    for (int i = 1; i < 14; i++) {
		ICard card = cardmaker.makeCard("heart", i);
		this.cardDeck.push(card);
	    }
	    for (int i = 1; i < 14; i++) {
		ICard card = cardmaker.makeCard("spade", i);
		this.cardDeck.push(card);
	    }
	    for (int i = 1; i < 14; i++) {
		ICard card = cardmaker.makeCard("club", i);
		this.cardDeck.push(card);
	    }
	    for (int i = 1; i < 14; i++) {
		ICard card = cardmaker.makeCard("diamond", i);
		this.cardDeck.push(card);
	    }
	    for (int i = 1; i < 3; i++) {
		ICard card = cardmaker.makeCard("joker", i);
		this.cardDeck.push(card);
	    }
	    result = 1;
	} catch (Exception e) {
	    result = 0;
	}

	// �ڴ˼�һ�������˼�ÿ�ֿ�ʼץ�ƶ��ܵõ�6����ķ���
	shuffleAndOneOfSixBigCards();

	return result;
    }

    @Override
    public int reNewCards() {
	int result = 0;
	try {
	    cardDeck.clear();// ��ԭ��
	    possessCards();//��������
	    mirrorCards();// ������
	    result = 1;

	} catch (Exception e) {
	    result = 0;
	}
	return result;
    }

    private void mirrorCards() {
	cardMirror.clear();
	for (ICard iCard : cardDeck) {
	    cardMirror.push(iCard);
	}
    }

    @Override
    public List<ICard> getCardsMirror() {
	List<ICard> result = new ArrayList<ICard>();
	for (ICard iCard : cardMirror) {
	    result.add(iCard);
	}
	return result;
    }

    @Override
    public List<ICard> getNoJokersCardsLefted() {
	List<ICard> result = new ArrayList<ICard>();
	for (ICard iCard : this.cardDeck) {
	    boolean noJokers = !iCard.getSuit().equals("joker");
	    if (noJokers) {
		result.add(iCard);
	    }
	}
	return result;
    }

}
