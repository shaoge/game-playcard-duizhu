package com.formum.duizhu.data;

import java.util.ArrayList;
import java.util.List;

import com.formum.duizhu.cardDeck.CardDeck;
import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.player.AIPlay;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.player.RobotPlayer;
import com.formum.duizhu.recorder.IRecorder;
import com.formum.duizhu.recorder.Recorder;

public class Data4Robot {

    private static volatile Data4Robot INSTANCE = null;

    private List<ICard> robotCards = new ArrayList<ICard>();
    private List<ICard> robotRealThroughJugeCards = new ArrayList<ICard>();

    private Data4Robot() {

    }

    public static Data4Robot getInstance() {
	if (INSTANCE == null) {
	    synchronized (Data4Robot.class) {
		// when more than two threads run into the first null check same
		// time, to avoid instanced more than one time, it needs to be
		// checked again.
		if (INSTANCE == null) {
		    INSTANCE = new Data4Robot();
		}
	    }
	}
	return INSTANCE;
    }

    public void backUpCards(ICard card) {
	this.robotCards.add(card);
    }

 

    public List<ICard> getRobotHoldingCards() {
	List<ICard> result = new ArrayList<ICard>();
	
	IRecorder recorder = Recorder.getInstance();
	RobotPlayer robotPlayer = RobotPlayer.getInstance(AIPlay.getInstance());
	List<ICard> robotCardsOutedOnTable = new ArrayList<ICard>();// 已经在桌上的机器人牌
	for (ICard iCard : recorder.getCardsOnTable()) {
	    boolean robotCard = iCard.getPlayer().equals(robotPlayer);
	    if (robotCard) {
		robotCardsOutedOnTable.add(iCard);
	    }
	}
	result.addAll(robotCards);
	boolean onTable=robotCardsOutedOnTable.size()>0;
	if (onTable) {
	    result.removeAll(robotCardsOutedOnTable);
	}

	//检查手牌，有错以result为准进行纠正。
	boolean unNormal=robotPlayer.actions().getSelfCardDeck().size()!=result.size();
	System.out.println("@Data4Robot  getRobotHoldingCards() 检查手牌，有错以result为准进行纠正 -unNormal---->" + unNormal);
	if (unNormal) {
	    robotPlayer.actions().setPlayerCardDeck(result);
	}
	
	return result;
    }

    public List<ICard> getRobotCards() {
	return robotCards;
    }

    public void setRobotCards(List<ICard> robotCards) {
	this.robotCards = robotCards;
    }
/**
 * 捕捉幽灵牌
 * @param cardsIntoRoundJuger
 */
    public void catchAndReSendGhostCards4Robot(List<ICard> cardsIntoRoundJuger){
	//找到 幽灵牌 并 从桌面牌中拿出，送回机器人在手牌
	Player robot=cardsIntoRoundJuger.get(0).getPlayer();

	List<ICard> cardsOnTable = Recorder.getInstance().getCardsOnTable();
	List<ICard> robotCardsOnTable = new ArrayList<ICard>();//桌面机器人牌

	if (cardsOnTable!=null) {
	    for (ICard iCard : cardsOnTable) {
		boolean isrobot=iCard.getPlayer().equals(robot);
		if (isrobot) {
		    robotCardsOnTable.add(iCard);
		}
	    }
	}

	System.out.println("@Data4Robot catchAndReSendGhostCards4Robot() robotCardsOnTable-->"+robotCardsOnTable.size());
	System.out.println("@Data4Robot catchAndReSendGhostCards4Robot() robotRealThroughJugeCards-->"+robotRealThroughJugeCards.size());
	
	
	List<ICard> robotGhostCards = new ArrayList<ICard>();//幽灵牌
	
	//桌面有幽灵牌: 幽灵牌 =桌面机器人牌-断定大小牌后的机器人已出牌；
	List<ICard> tempRobotCardsOnTable = new ArrayList<ICard>();
	tempRobotCardsOnTable.addAll(robotCardsOnTable);
	tempRobotCardsOnTable.removeAll(robotRealThroughJugeCards);
	boolean hasGhost=tempRobotCardsOnTable.size()>0;
	if (hasGhost) {
	    robotGhostCards.addAll(tempRobotCardsOnTable);
	  //TODO:
		System.out.println("@Data4Robot catchAndReSendGhostCards4Robot() robotGhostCards-->"+robotGhostCards.size());

	}
	//桌面没有幽灵牌: 幽灵牌=机器人全牌-机器人在手牌-桌面牌
	List<ICard> robotGhostCards1 = new ArrayList<ICard>();//幽灵牌
	List<ICard> tempRobotCards = new ArrayList<ICard>();//机器人全牌
	tempRobotCards.addAll(this.robotCards);
	tempRobotCards.removeAll(robot.actions().getSelfCardDeck());
	tempRobotCards.removeAll(robotCardsOnTable);
	boolean hasGhost1=tempRobotCards.size()>0;
	if (hasGhost1) {
	    robotGhostCards1.addAll(tempRobotCards);
		System.out.println("@Data4Robot catchAndReSendGhostCards4Robot() robotGhostCards1-->"+robotGhostCards1.size());
	} 
	//如果都有，取桌面上的幽灵牌；如果只有一个有，取它==>将牌送回机器人在手牌
	boolean bothHasGhost=robotGhostCards.size()>0&&robotGhostCards1.size()>0;
	boolean oneHas=robotGhostCards.size()>0;
	if (bothHasGhost||oneHas) {
	    List<ICard> newSelfCardDeck = new ArrayList<ICard>();
	    newSelfCardDeck.addAll(robot.actions().getSelfCardDeck());
	    newSelfCardDeck.addAll(robotGhostCards);
	    robot.actions().setPlayerCardDeck(newSelfCardDeck);//向手牌加回
	    Recorder.getInstance().getCardsOnTable().removeAll(robotGhostCards);//从桌牌减去
	}
	boolean secondHas=robotGhostCards1.size()>0;
	if (secondHas) {
	    List<ICard> newSelfCardDeck = new ArrayList<ICard>();
	    newSelfCardDeck.addAll(robot.actions().getSelfCardDeck());
	    newSelfCardDeck.addAll(robotGhostCards1);
	    robot.actions().setPlayerCardDeck(newSelfCardDeck);
	}
	
	
	
    }

public List<ICard> getRobotRealThroughJugeCards() {
    return robotRealThroughJugeCards;
}

public void setRobotRealThroughJugeCards(List<ICard> robotRealThroughJugeCards) {
    this.robotRealThroughJugeCards = robotRealThroughJugeCards;
}
}
