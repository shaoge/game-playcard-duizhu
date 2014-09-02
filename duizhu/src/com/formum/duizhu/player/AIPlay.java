package com.formum.duizhu.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.data.Data4Human;
import com.formum.duizhu.data.Data4Robot;
import com.formum.duizhu.gameRule.GameRule;
import com.formum.duizhu.gameRule.IGameRule;
import com.formum.duizhu.player.AIStrategy.BankerStrategy;
import com.formum.duizhu.player.AIStrategy.IBankerStrategy;
import com.formum.duizhu.player.AIStrategy.IPlayerStrategy;
import com.formum.duizhu.player.AIStrategy.PlayerStrategy;
import com.formum.duizhu.player.playerCardDeck.IPlayerCardDeck;
import com.formum.duizhu.player.playerCardDeck.PlayerCardDeck;
import com.formum.duizhu.player.powerCardsBag.IPowerCardsBag;
import com.formum.duizhu.player.powerCardsBag.RobotPowerCardsBag;
import com.formum.duizhu.recorder.IRecorder;
import com.formum.duizhu.recorder.Recorder;
import com.formum.duizhu.util.IUtil;
import com.formum.duizhu.util.Util;

public class AIPlay implements IPlayer {

	private static volatile AIPlay INSTANCE = null;

	private IUtil tools = Util.getInstance();// ������
	private IRecorder recorder = Recorder.getInstance();// ��¼
	private String playerName = "";// �������
	private int myClassPoint = 0;// ����3���𣬿�ʼ������3
	private int myLastSetClassPoint = 0;
	private boolean isBanker = false;// ׯ�ұ�־
	private int currentScore = 0;// �������ׯ��Ҫ�Ʒ�
	private boolean isWinLasthand = false;// ÿ������Ƿ��ƴ�
	private IPlayerCardDeck myPlayerCardDeck = new PlayerCardDeck();// �ֳָ�������
	private IGameRule gameRule = GameRule.getInstance();// ���򷨹�

	// �Է������¼
	private boolean isOpponentTrump = true;// �Է��Ƿ�������Ĭ������
	private List<String> opponentNorTrumpSuits = new ArrayList<String>();// �Է��ִ渱�ƻ�ɫ

	private Data4Robot data=Data4Robot.getInstance();;

	private AIPlay() {

	}

	public static AIPlay getInstance() {
		if (INSTANCE == null) {
			synchronized (AIPlay.class) {
				if (INSTANCE == null) {
					INSTANCE = new AIPlay();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	public Map<String, Object> selectTrumpSuit(ICard card) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (gameRule.isShowingCard4ConfirmTrumpSuitRight(card)) {
			map.put("suit", card.getSuit());
			map.put("point", card.getPoint());
			return map;
		} else {
			return null;
		}

	}

	@Override
	public void fetchCard(ICard card) {
		myPlayerCardDeck.inCard(card);
		//�ص�setPlayer��ע����ҵ�����
		card.setPlayer(RobotPlayer.getInstance(this));
		data.backUpCards(card);//�����౸��
		onCardsFullMakeStrategy();//���ƴﵽ�㹻������ʼ�ƶ�����Ŀ��
		


	}

	private void onCardsFullMakeStrategy() {
		// <<<<<<��ʼ������>>>>>>

		// ���ƴﵽ�涨���������ò��Ծ��߷���
		int size = this.countCards();//����������
		boolean enough = (size == IGameRule.PLAYER_CARDS_SIZE);//��ù������������
		if (enough) {
			//�������������������ƶ�����ǰ������
			RobotPlayer robot = RobotPlayer.getInstance(this);
			IPowerCardsBag powerCardBag=RobotPowerCardsBag.getInstance(robot);
			powerCardBag.makeUpPowerCardsBags();//ʵʩ����
			
			boolean banker =this.isBanker();//�Ƿ�ׯ��
			if (banker) {
				//ׯ�Ҳ���Ŀ�� 
				IBankerStrategy bankerStrategy=BankerStrategy.getInstance();
				robot.setMyStrategy(bankerStrategy.makeStrategy(this.getSelfCardDeck()));
				
			}else {
				//�мҲ���Ŀ�� 
				IPlayerStrategy playerStrategy = PlayerStrategy.getInstance();
				robot.setMyStrategy(playerStrategy.makeStrategy(this.getSelfCardDeck()));
			}
		}
	}

	@Override
	public List<ICard> putCardsOut(List<ICard> readyOutCards) {
	    //TODO:
	    for (ICard iCard : readyOutCards) {
		System.out.println("@AIPlay  putCardsOut(readyOutCards) arg readyOutCards----->"+iCard.getSuit()+iCard.getPoint());
	    }
		return myPlayerCardDeck.outCards(readyOutCards);

	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getMyClassPoint() {
		return myClassPoint;
	}

	public void setMyClassPoint(int myClassPoint) {
		this.myClassPoint = myClassPoint;
	}

	public boolean isBanker() {
		return isBanker;
	}

	public void setBanker(boolean isBanker) {
		this.isBanker = isBanker;

	}

	public boolean isWinLasthand() {
		return isWinLasthand;
	}

	public void setWinLasthand(boolean isWinLasthand) {
		this.isWinLasthand = isWinLasthand;
	}

	public int getCurrentScore() {
		return currentScore;
	}

	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}

	public List<ICard> getSelfCardDeck() {
		return myPlayerCardDeck.getPlayerCardDeck();
	}

	@Override
	public void updateCurrentTrumpSuit(String currentTrumpSuit) {
		myPlayerCardDeck.setTrumpSuit(currentTrumpSuit);
	}

	@Override
	public void updateCurrentClassPoint(int currentClassPoint) {
		myPlayerCardDeck.setTrumpPoint(currentClassPoint);

	}

	@Override
	public int countCards() {
		return myPlayerCardDeck.size();
	}

	@Override
	public int sumScoreInHand() {
		return myPlayerCardDeck.sumCardScore(myPlayerCardDeck);
	}

	@Override
	public String getTrumpSuit() {
		return myPlayerCardDeck.getTrumpSuit();
	}

	@Override
	public int getTrumpPoint() {
		return myPlayerCardDeck.getTrumpPoint();
	}

	@Override
	public List<ICard> tryPutCardsOut(List<ICard> testOutCards) {
		return testOutCards;
	}

	@Override
	public void registerOnRecorder(IRecorder recorder, Player player) {
		recorder.registerPlayer(player);
	}

	@Override
	public ICard putCardOut(String suit, int point) {
		return myPlayerCardDeck.outCard(suit, point);
	}

	public boolean isOpponentTrump() {
		return isOpponentTrump;
	}

	public void setOpponentTrump(boolean isOpponentTrump) {
		this.isOpponentTrump = isOpponentTrump;
	}

	@Override
	public List<String> getOpponentNorTrumpSuits() {
		return this.opponentNorTrumpSuits;
	}

	@Override
	public void setOpponentNorTrumpSuits(List<String> opponentNorTrumpSuits) {
		this.opponentNorTrumpSuits = opponentNorTrumpSuits;

	}

	// ��ʼ�²�Է��ִ渱�ƻ�ɫ
	public void initialDefaultOpponentNorTrumpSuits(String currentTrumpSuit) {
		opponentNorTrumpSuits.add("heart");
		opponentNorTrumpSuits.add("spade");
		opponentNorTrumpSuits.add("club");
		opponentNorTrumpSuits.add("diamond");
		opponentNorTrumpSuits.remove(currentTrumpSuit);// �����ƻ�ɫ
	}

	@Override
	public ICard showTrumpSuit() {
		ICard showingCard=null;
		
		boolean noSuit=recorder.getCurrentTrumpSuit().equals("");//��û�����ƻ�ɫ
		boolean less13=this.getSelfCardDeck().size()<13;//С��14����
		if (noSuit&&less13) {
			List<ICard> classPointCards=new ArrayList<ICard>();
			
			//��������
			List<ICard> cardsInHand = this.getSelfCardDeck();
			boolean hasCard=cardsInHand!=null&&cardsInHand.size()>0;
			if (hasCard) {
				//��������
				int classPoint=recorder.getCurrentClassPoint();
				boolean hasPoint=classPoint>0;
				if (hasPoint) {
					for (ICard iCard : cardsInHand) {
						boolean yes=iCard.getPoint()==classPoint;
						if (yes) {
							classPointCards.add(iCard);
						}
					}
				}
			}
			//�����������ƻ�ɫ�з�>=4�ŵ�
			int count=0;
			A:for (ICard classCard : classPointCards) {
				B:for (ICard handCard : cardsInHand) {
					boolean samesuit=classCard.getSuit().equals(handCard.getSuit());
					if (samesuit) {
						count+=1;
						if (count>=4) {
							showingCard=classCard;
							break A;
						}
					}
				}
			count=0;
			}
			
		}
		
		return showingCard;
	}

	@Override
	public void setPlayerCardDeck(List<ICard> playerCardDeck) {
		    myPlayerCardDeck.setPlayerCardDeck(playerCardDeck);
	}

	public int getMyLastSetClassPoint() {
	    return myLastSetClassPoint;
	}

	public void setMyLastSetClassPoint(int myLastSetClassPoint) {
	    this.myLastSetClassPoint = myLastSetClassPoint;
	}



}
