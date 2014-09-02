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

	private IUtil tools = Util.getInstance();// 工具类
	private IRecorder recorder = Recorder.getInstance();// 记录
	private String playerName = "";// 玩家名称
	private int myClassPoint = 0;// 都从3打起，开始还不是3
	private int myLastSetClassPoint = 0;
	private boolean isBanker = false;// 庄家标志
	private int currentScore = 0;// 如果不是庄家要计分
	private boolean isWinLasthand = false;// 每局最后是否牌大
	private IPlayerCardDeck myPlayerCardDeck = new PlayerCardDeck();// 手持个人牌套
	private IGameRule gameRule = GameRule.getInstance();// 规则法官

	// 对方情况记录
	private boolean isOpponentTrump = true;// 对方是否有主，默认有主
	private List<String> opponentNorTrumpSuits = new ArrayList<String>();// 对方现存副牌花色

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
		//回调setPlayer，注册玩家到牌上
		card.setPlayer(RobotPlayer.getInstance(this));
		data.backUpCards(card);//数据类备份
		onCardsFullMakeStrategy();//当牌达到足够张数开始制定策略目标
		


	}

	private void onCardsFullMakeStrategy() {
		// <<<<<<初始化策略>>>>>>

		// 当牌达到规定张数，调用策略决策方法
		int size = this.countCards();//查手中牌数
		boolean enough = (size == IGameRule.PLAYER_CARDS_SIZE);//获得规则允许的张数
		if (enough) {
			//整理能量包，给策略制定创造前置条件
			RobotPlayer robot = RobotPlayer.getInstance(this);
			IPowerCardsBag powerCardBag=RobotPowerCardsBag.getInstance(robot);
			powerCardBag.makeUpPowerCardsBags();//实施整理
			
			boolean banker =this.isBanker();//是否庄家
			if (banker) {
				//庄家策略目标 
				IBankerStrategy bankerStrategy=BankerStrategy.getInstance();
				robot.setMyStrategy(bankerStrategy.makeStrategy(this.getSelfCardDeck()));
				
			}else {
				//闲家策略目标 
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

	// 初始猜测对方现存副牌花色
	public void initialDefaultOpponentNorTrumpSuits(String currentTrumpSuit) {
		opponentNorTrumpSuits.add("heart");
		opponentNorTrumpSuits.add("spade");
		opponentNorTrumpSuits.add("club");
		opponentNorTrumpSuits.add("diamond");
		opponentNorTrumpSuits.remove(currentTrumpSuit);// 减主牌花色
	}

	@Override
	public ICard showTrumpSuit() {
		ICard showingCard=null;
		
		boolean noSuit=recorder.getCurrentTrumpSuit().equals("");//还没有主牌花色
		boolean less13=this.getSelfCardDeck().size()<13;//小于14张牌
		if (noSuit&&less13) {
			List<ICard> classPointCards=new ArrayList<ICard>();
			
			//已在手牌
			List<ICard> cardsInHand = this.getSelfCardDeck();
			boolean hasCard=cardsInHand!=null&&cardsInHand.size()>0;
			if (hasCard) {
				//找主点牌
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
			//算在手主点牌花色有否>=4张的
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
