package com.formum.duizhu.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.data.Data4Robot;
import com.formum.duizhu.player.AIStrategy.BankerStrategy;
import com.formum.duizhu.player.AIStrategy.PlayerStrategy;
import com.formum.duizhu.player.powerCardsBag.IPowerCardsBag;
import com.formum.duizhu.player.powerCardsBag.RobotPowerCardsBag;
import com.formum.duizhu.recorder.Recorder;


public class RobotPlayer extends Player {
private static volatile RobotPlayer INSTANCE = null;
private final String name="Robot";
private int myStrategy=0;


	private RobotPlayer(IPlayer concreteIPlayer) {
		super(concreteIPlayer);
	}

	public static RobotPlayer getInstance(IPlayer concreteIPlayer) {
		if (INSTANCE == null) {
			synchronized (RobotPlayer.class) {
				if (INSTANCE == null) {
					INSTANCE = new RobotPlayer(concreteIPlayer);
					Recorder.getInstance().onPlayerGetInstance(INSTANCE);//注册到RECORDER
				}
			}
		}
		return INSTANCE;
	}

	@Override
	public String getName() {
		return this.name;
	}
	public int getMyStrategy() {
		return myStrategy;
	}
	public void setMyStrategy(int myStrategy) {
		this.myStrategy = myStrategy;
	}

/**
 * 咨询出牌
 * @param target 策略目标
 * @param opponentCards 对手牌
 * @return
 */
	public List<ICard> consultStrategy(int target,List<ICard> opponentCards){
	List<ICard> suggestingCards=new ArrayList<ICard>();


		try {
		    //整理能量包--动态整理
		    IPowerCardsBag powerCardBag = RobotPowerCardsBag.getInstance(this);
		    powerCardBag.makeUpPowerCardsBags();
		    //要方案，根据庄闲家和先后手
		    boolean banker = this.actions().isBanker();
		    
		    boolean foreHand = (opponentCards == null || opponentCards.isEmpty());
		    if (banker) {

			//实际建议的出牌
			suggestingCards.clear();
			if (foreHand) {
			    suggestingCards.addAll(BankerStrategy.getInstance().suggestCardsAsForeHand(this.myStrategy, powerCardBag));
			    /*	boolean empty=suggestingCards.isEmpty();
			    if (empty) {
				suggestingCards.addAll(emergencySuggestingCards(null));
			    }*/
			    // TODO: handle exception
				System.out.println("@RobotPlayer myStrategy==============>"+myStrategy);
				System.out.println("@RobotPlayer (as banker foreHand)  suggestingCards==========>"+suggestingCards.size());
			} else {
			    suggestingCards.addAll(BankerStrategy.getInstance().suggestCardsAsAfterHand(powerCardBag, opponentCards));
/*			    boolean empty=suggestingCards.isEmpty();
			    if (empty) {
				suggestingCards.addAll(emergencySuggestingCards(opponentCards));
			    }*/
			    // TODO: handle exception
				System.out.println("@RobotPlayer myStrategy==============>"+myStrategy);
				System.out.println("@RobotPlayer (as banker afterHand)  suggestingCards==========>"+suggestingCards.size());
			}
		    } else {
			//实际建议的出牌
			suggestingCards.clear();
			if (foreHand) {
			    suggestingCards.addAll(PlayerStrategy.getInstance().suggestCardsAsForeHand(this.myStrategy, powerCardBag));
/*			    boolean empty=suggestingCards.isEmpty();
			    if (empty) {
				suggestingCards.addAll(emergencySuggestingCards(null));
			    }*/
			    // TODO: handle exception
				System.out.println("@RobotPlayer myStrategy==============>"+myStrategy);
				System.out.println("@RobotPlayer (as player foreHand)  suggestingCards==========>"+suggestingCards.size());
			} else {
			    suggestingCards.addAll(PlayerStrategy.getInstance().suggestCardsAsAfterHand(powerCardBag, opponentCards));
/*			    boolean empty=suggestingCards.isEmpty();
			    if (empty) {
				suggestingCards.addAll(emergencySuggestingCards(opponentCards));
			    }*/
			    // TODO: handle exception
				System.out.println("@RobotPlayer myStrategy==============>"+myStrategy);
				System.out.println("@RobotPlayer(as player afterHand)  suggestingCards   ==========>"+suggestingCards.size());
			}

		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}


		return suggestingCards;
	}
/**
 * 咨询不到牌时，紧急出应付牌
 * @param opponentCards
 * @return
 */
private List<ICard> emergencySuggestingCards(List<ICard> opponentCards) {
    // TODO Auto-generated method stub
    List<ICard> suggestingCards=new ArrayList<ICard>();
    
    boolean isNull=opponentCards==null;
    if (isNull) {
	System.out.println("@ReadyOut !!!进入紧急建议牌===限定单张");
	//先出只随机出一张先
	Random r=new Random();
	int n=this.actions().getSelfCardDeck().size();
	if (n>0) {
	    suggestingCards.add(this.actions().getSelfCardDeck().get( r.nextInt(n)));
	}
	
    } else {
	System.out.println("@ReadyOut !!!进入紧急建议牌===任意张数");
	//后出数要对，随机选--调用方评价是否正确
	int n=this.actions().getSelfCardDeck().size();
	if (n>0) {
	int cardSize=opponentCards.size();
	int count=0;
	while (count!=cardSize) {
		Random r=new Random();
		    ICard iCard = this.actions().getSelfCardDeck().get( r.nextInt(n));
		    boolean notIn=suggestingCards.contains(iCard);
		    if (notIn) {
			suggestingCards.add(iCard);
			count++;
		    }
		}
	}
    }
    
   
    
    return suggestingCards;
}





}
