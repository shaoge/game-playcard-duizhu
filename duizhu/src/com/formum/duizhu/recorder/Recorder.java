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

	private Player firstFetcher=null;//��һץ����
	private Player currentBanker=null;//��ǰ��ׯ��
	private int stageOfRound = IGameRule.ROUND_STAGE_READY;//ÿ���н׶�
	private int round = 0;// ÿ�ֻغ��ۼ���
	private String currentTrumpSuit = "";// ��ǰ����
	private int currentClassPoint = 0;// ��ǰ����
	private String lastTrumpSuit = "";// ��������
	private int lastClassPoint = 0;// ���ֽ���
	private int setScore = 0;// ��ǰ�ַ�
	private Player roundWinner = null;// ��ǰ�غ�ʤ��
	private Player setWinner = null;// ��ǰ�ִ�ʤ��
	private int mainLineFlag = 0;// ��3��12�ľִ�·��,���ȵ���ͬ��
	private Player gameWinner = null;// ��Ϸʤ��
	private List<Player> playerRegistered = new ArrayList<Player>();// ע������
	private List<ICard> cardsOnTable=new ArrayList<ICard>();// ����ĸ�������
	private List<Map<Player,List<ICard>>> bothOutCards=new ArrayList<Map<Player, List<ICard>>>();//˫���Ⱥ�����ƣ����ڴ�ֵ

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



	// ��������
	private void onResetCurrentClassPoint() {
		this.lastClassPoint = this.getCurrentClassPoint();
	}

	// ��������
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
		this.onResetCurrentClassSuit();// ���Ͼ����ƻ�ɫ����
		this.currentTrumpSuit = currentTrumpSuit;
		this.onCurrentTrumpSuitChanged();// ֪ͨplayer
		//��ʼ��robot�ٶ��Է��Ļ�ɫ
		RobotPlayer.getInstance(AIPlay.getInstance()).actions().initialDefaultOpponentNorTrumpSuits(currentTrumpSuit);
	}

	public int getCurrentClassPoint() {
		return currentClassPoint;
	}

	public void setCurrentClassPoint(int currentClassPoint) {
	    //TODO:
		//this.onResetCurrentClassPoint();// ���Ͼֽ������
		this.currentClassPoint = currentClassPoint;
		//this.onCurrentClassPointChanged();// ֪ͨplayer
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
		// ֱ�Ӽ������ĸ�������
		this.cardsOnTable.addAll(cardsOnTable);
		
	}

	@Override
	public void emptyCardsOnTable() {
		// �˶�����ÿ��֮ǰ��һ�����������
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
