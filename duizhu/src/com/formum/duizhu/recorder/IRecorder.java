package com.formum.duizhu.recorder;

import java.util.List;
import java.util.Map;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.player.Player;

public interface IRecorder {

	public String getCurrentTrumpSuit();
	public void setCurrentTrumpSuit(String currentTrumpSuit);
	public int getCurrentClassPoint() ;
	public void setCurrentClassPoint(int currentClassPoint) ;
	public int getSetScore();
	public void setSetScore(int setScore) ;
	public Player getRoundWinner() ;
	public void setRoundWinner(Player roundWinner);
	public Player getSetWinner();
	public void setSetWinner(Player setWinner);
	public Player getGameWinner() ;
	public void setGameWinner(Player gameWinner) ;
	public int getRound() ;
	public void setRound(int round);
	public int getMainLineFlag() ;
	public void setMainLineFlag(int mainLineFlag);
	/**
	 * ע�����
	 * @param player
	 */
	public void registerPlayer(Player player);
	 /**
	  * �����Ҽ���
	  * @return
	  */
	public  List<Player> getPlayerRegistered();
	/**
	 * �������ʵ���ص�������������ע�ᵽRecorder������.
	 * @param player
	 */
	public void onPlayerGetInstance(Player player);
	public String getLastTrumpSuit();

	public void setLastTrumpSuit(String lastTrumpSuit);

	public int getLastClassPoint() ;

	public void setLastClassPoint(int lastClassPoint);


	/**
	 * �ǼǴ���ĸ�����
	 * @param cardsOnTable
	 */
	public void recordCardsOnTable(List<ICard> cardsOnTable);
	/**
	 * ��մ���ĸ�����
	 */
	public void emptyCardsOnTable();
	/**
	 * ��ȡȫ�����������
	 * @return
	 */
	public List<ICard> getCardsOnTable();
/**
 * ���˫��������Ϣ,0λ���ȳ���1λ����
 * @return
 */
	public List<Map<Player,List<ICard>>> getBothOutCards();
/**
 * ���������Ϣ,0λ���ȳ���1λ����
 * @param bothOutCards
 */
	public void setBothOutCards(List<Map<Player,List<ICard>>> bothOutCards) ;
	/**
	 * �����ץ����
	 * @return
	 */
	public Player getFirstFetcher();

	/**
	 * ������ץ����
	 * @param firstFetcher
	 */
	public void setFirstFetcher(Player firstFetcher);
	
	/**
	 * ��õ�ǰׯ��
	 * @return
	 */
	public Player getCurrentBanker();
/**
 * ���õ�ǰׯ��
 * @param currentBanker
 */
	public void setCurrentBanker(Player currentBanker);
	
	public void setStageOfRound(int stageOfRound);
}
