package com.formum.duizhu.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.data.Data4Human;
import com.formum.duizhu.gameRule.GameRule;
import com.formum.duizhu.gameRule.IGameRule;
import com.formum.duizhu.player.playerCardDeck.IPlayerCardDeck;
import com.formum.duizhu.player.playerCardDeck.PlayerCardDeck;
import com.formum.duizhu.recorder.IRecorder;

public class GeneralPlay implements IPlayer {
	private static volatile GeneralPlay INSTANCE = null;
	private String playerName = "";// 玩家名称
	private int myClassPoint = 0;// 都从3打起，开始还不是3
	private int myLastSetClassPoint = 0;
	private boolean isBanker = false;// 庄家标志
	private int currentScore = 0;// 如果不是庄家要计分
	private boolean isWinLasthand = false;// 每局最后是否牌大
	private IPlayerCardDeck myPlayerCardDeck = new PlayerCardDeck();// 手持个人牌套	
	private IGameRule gameRule =GameRule.getInstance();// 规则法官
	
	private GeneralPlay() {
		// TODO Auto-generated constructor stub
	}
	public static GeneralPlay getInstance() {
		if (INSTANCE == null) {
			synchronized (GeneralPlay.class) {
				if (INSTANCE == null) {
					INSTANCE = new GeneralPlay();
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
		card.setPlayer(HumanPlayer.getInstance(this));
		Data4Human.getInstance().backUpCards(card);//数据类备份
	}

	@Override
	public List<ICard> putCardsOut( List<ICard> readyOutCards) {
	    //TODO:
	    System.out.println("@GeneralPlay   readyOutCards--------------->"+readyOutCards.size());
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

	public  List<ICard> tryPutCardsOut( List<ICard> testOutCards) {
		return testOutCards;
	}

	@Override
	public void registerOnRecorder(IRecorder recorder,Player player) {
		recorder.registerPlayer(player);
	}
	@Override
	public ICard putCardOut(String suit, int point) {
		return myPlayerCardDeck.outCard(suit, point);
	}
	@Override
	public boolean isOpponentTrump() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setOpponentTrump(boolean isOpponentTrump) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getOpponentNorTrumpSuits() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setOpponentNorTrumpSuits(List<String> opponentNorTrumpSuits) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void initialDefaultOpponentNorTrumpSuits(String currentTrumpSuit) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ICard showTrumpSuit() {
		// TODO Auto-generated method stub
		return null;
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
