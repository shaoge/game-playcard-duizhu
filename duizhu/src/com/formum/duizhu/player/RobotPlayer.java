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
					Recorder.getInstance().onPlayerGetInstance(INSTANCE);//ע�ᵽRECORDER
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
 * ��ѯ����
 * @param target ����Ŀ��
 * @param opponentCards ������
 * @return
 */
	public List<ICard> consultStrategy(int target,List<ICard> opponentCards){
	List<ICard> suggestingCards=new ArrayList<ICard>();


		try {
		    //����������--��̬����
		    IPowerCardsBag powerCardBag = RobotPowerCardsBag.getInstance(this);
		    powerCardBag.makeUpPowerCardsBags();
		    //Ҫ����������ׯ�мҺ��Ⱥ���
		    boolean banker = this.actions().isBanker();
		    
		    boolean foreHand = (opponentCards == null || opponentCards.isEmpty());
		    if (banker) {

			//ʵ�ʽ���ĳ���
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
			//ʵ�ʽ���ĳ���
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
 * ��ѯ������ʱ��������Ӧ����
 * @param opponentCards
 * @return
 */
private List<ICard> emergencySuggestingCards(List<ICard> opponentCards) {
    // TODO Auto-generated method stub
    List<ICard> suggestingCards=new ArrayList<ICard>();
    
    boolean isNull=opponentCards==null;
    if (isNull) {
	System.out.println("@ReadyOut !!!�������������===�޶�����");
	//�ȳ�ֻ�����һ����
	Random r=new Random();
	int n=this.actions().getSelfCardDeck().size();
	if (n>0) {
	    suggestingCards.add(this.actions().getSelfCardDeck().get( r.nextInt(n)));
	}
	
    } else {
	System.out.println("@ReadyOut !!!�������������===��������");
	//�����Ҫ�ԣ����ѡ--���÷������Ƿ���ȷ
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
