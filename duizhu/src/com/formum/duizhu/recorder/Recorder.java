package com.formum.duizhu.recorder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.formum.duizhu.R;
import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.gameRule.IGameRule;
import com.formum.duizhu.player.AIPlay;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.player.RobotPlayer;

public class Recorder implements IRecorder {

	private static volatile Recorder INSTANCE = null;

	private Player firstFetcher=null;//第一抓牌者
	private Player currentBanker=null;//当前局庄家
	private int stageOfRound = IGameRule.ROUND_STAGE_READY;//每局中阶段
	private int round = 0;// 每局回合累加数
	private String currentTrumpSuit = "";// 当前主牌
	private int currentClassPoint = 0;// 当前将点
	private String lastTrumpSuit = "";// 上轮主牌
	private int lastClassPoint = 0;// 上轮将点
	private int setScore = 0;// 当前局分
	private Player roundWinner = null;// 当前回合胜方
	private Player setWinner = null;// 当前局次胜方
	private int mainLineFlag = 0;// 从3打12的局次路线,与先到者同步
	private Player gameWinner = null;// 游戏胜方
	private List<Player> playerRegistered = new ArrayList<Player>();// 注册的玩家
	private List<ICard> cardsOnTable=new ArrayList<ICard>();// 打过的各轮牌套
	private List<Map<Player,List<ICard>>> bothOutCards=new ArrayList<Map<Player, List<ICard>>>();//双方先后出的牌，用于传值

	private Recorder() {

	}

	public static Recorder getInstance() {
		if (INSTANCE == null) {
			synchronized (Recorder.class) {
				if (INSTANCE == null) {
					INSTANCE = new Recorder();
				}
			}
		}
		return INSTANCE;
	}



	// 联动更新
	private void onResetCurrentClassPoint() {
		this.lastClassPoint = this.getCurrentClassPoint();
	}

	// 联动更新
	private void onResetCurrentClassSuit() {
		this.lastTrumpSuit = this.getCurrentTrumpSuit();
	}

	private void onCurrentTrumpSuitChanged() {
		for (Player player : playerRegistered) {
			player.actions().updateCurrentTrumpSuit(currentTrumpSuit);
			
		}
	}

	private void onCurrentClassPointChanged() {
		for (Player player : playerRegistered) {
			player.actions().updateCurrentClassPoint(currentClassPoint);
		}
	}

	public String getCurrentTrumpSuit() {
		return currentTrumpSuit;
	}

	public void setCurrentTrumpSuit(String currentTrumpSuit) {
		this.onResetCurrentClassSuit();// 给上局主牌花色更新
		this.currentTrumpSuit = currentTrumpSuit;
		this.onCurrentTrumpSuitChanged();// 通知player
		//初始化robot假定对方的花色
		RobotPlayer.getInstance(AIPlay.getInstance()).actions().initialDefaultOpponentNorTrumpSuits(currentTrumpSuit);
	}

	public int getCurrentClassPoint() {
		return currentClassPoint;
	}

	public void setCurrentClassPoint(int currentClassPoint) {
	    //TODO:
		//this.onResetCurrentClassPoint();// 给上局将点更新
		this.currentClassPoint = currentClassPoint;
		//this.onCurrentClassPointChanged();// 通知player
	}

	public int getSetScore() {
		return setScore;
	}

	public void setSetScore(int setScore) {
		this.setScore = setScore;
	}

	public Player getRoundWinner() {
		return roundWinner;
	}

	public void setRoundWinner(Player roundWinner) {
		this.roundWinner = roundWinner;
	}

	public Player getSetWinner() {
		return setWinner;
	}

	public void setSetWinner(Player setWinner) {
		this.setWinner = setWinner;
	}

	public Player getGameWinner() {
		return gameWinner;
	}

	public void setGameWinner(Player gameWinner) {
		this.gameWinner = gameWinner;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getStageOfRound() {
		return stageOfRound;
	}

	public void setStageOfRound(int stageOfRound) {
		this.stageOfRound = stageOfRound;
	}

	public int getMainLineFlag() {
		return mainLineFlag;
	}

	public void setMainLineFlag(int mainLineFlag) {
		this.mainLineFlag = mainLineFlag;
	}

	public List<Player> getPlayerRegistered() {
		return playerRegistered;
	}

	@Override
	public void registerPlayer(Player player) {
		playerRegistered.add(player);

	}

	public String getLastTrumpSuit() {
		return lastTrumpSuit;
	}

	public void setLastTrumpSuit(String lastTrumpSuit) {
		this.lastTrumpSuit = lastTrumpSuit;
	}

	public int getLastClassPoint() {
		return lastClassPoint;
	}

	public void setLastClassPoint(int lastClassPoint) {
		this.lastClassPoint = lastClassPoint;
	}

	@Override
	public void recordCardsOnTable(List<ICard> cardsOnTable) {
		// 直接加入打过的各轮牌套
		this.cardsOnTable.addAll(cardsOnTable);
		
	}

	@Override
	public void emptyCardsOnTable() {
		// 此动作在每局之前的一个动作中完成
		this.cardsOnTable.clear();
	}

	@Override
	public List<ICard> getCardsOnTable() {
		return this.cardsOnTable;
	}

	@Override
	public void onPlayerGetInstance(Player player) {
		this.registerPlayer(player);
	}

	public List<Map<Player,List<ICard>>> getBothOutCards() {
		return bothOutCards;
	}

	public void setBothOutCards(List<Map<Player,List<ICard>>> bothOutCards) {
		this.bothOutCards = bothOutCards;
	}

	public Player getFirstFetcher() {
		return firstFetcher;
	}

	public void setFirstFetcher(Player firstFetcher) {
		this.firstFetcher = firstFetcher;
	}

	public Player getCurrentBanker() {
		return currentBanker;
	}

	public void setCurrentBanker(Player currentBanker) {
		this.currentBanker = currentBanker;
	}

}
