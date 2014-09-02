package com.formum.duizhu.data;

import java.util.ArrayList;
import java.util.List;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.player.AIPlay;
import com.formum.duizhu.player.GeneralPlay;
import com.formum.duizhu.player.HumanPlayer;
import com.formum.duizhu.player.RobotPlayer;
import com.formum.duizhu.recorder.IRecorder;
import com.formum.duizhu.recorder.Recorder;

public class Data4Human {

    private static volatile Data4Human INSTANCE = null;

    private List<ICard> readyOutCards = new ArrayList<ICard>();
    private List<ICard> humanCards = new ArrayList<ICard>();

    private Data4Human() {

    }

    public static Data4Human getInstance() {
	if (INSTANCE == null) {
	    synchronized (Data4Human.class) {
		// when more than two threads run into the first null check same
		// time, to avoid instanced more than one time, it needs to be
		// checked again.
		if (INSTANCE == null) {
		    INSTANCE = new Data4Human();
		}
	    }
	}
	return INSTANCE;
    }

    public void backUpCards(ICard card) {
	this.humanCards.add(card);
    }

    public List<ICard> getRobotHoldingCards() {
	List<ICard> result = new ArrayList<ICard>();

	IRecorder recorder = Recorder.getInstance();
	HumanPlayer humanPlayer = HumanPlayer.getInstance(GeneralPlay.getInstance());
	List<ICard> humanCardsOutedOnTable = new ArrayList<ICard>();// 已经在桌上的机器人牌
	for (ICard iCard : recorder.getCardsOnTable()) {
	    boolean humanCard = iCard.getPlayer().equals(humanPlayer);
	    if (humanCard) {
		humanCardsOutedOnTable.add(iCard);
	    }
	}
	result.addAll(humanCards);
	result.removeAll(humanCardsOutedOnTable);

	// 检查手牌，有错以result为准进行纠正。
	boolean unNormal = humanPlayer.actions().getSelfCardDeck().size() != result.size();
	System.out.println("@Data4Robot  getRobotHoldingCards() 检查手牌，有错以result为准进行纠正 -unNormal---->" + unNormal);
	if (unNormal) {
	    humanPlayer.actions().setPlayerCardDeck(result);
	}

	return result;
    }

    public List<ICard> getReadyOutCards() {
	return readyOutCards;
    }

    public void setReadyOutCards(List<ICard> readyOutCards) {
	this.readyOutCards = readyOutCards;
    }

    public List<ICard> getHumanCards() {
	return humanCards;
    }

    public void setHumanCards(List<ICard> humanCards) {
	this.humanCards = humanCards;
    }

}
