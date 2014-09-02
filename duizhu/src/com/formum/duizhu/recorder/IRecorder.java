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
	 * 注册玩家
	 * @param player
	 */
	public void registerPlayer(Player player);
	 /**
	  * 获得玩家集合
	  * @return
	  */
	public  List<Player> getPlayerRegistered();
	/**
	 * 玩家生成实例回调方法，将自身注册到Recorder对象中.
	 * @param player
	 */
	public void onPlayerGetInstance(Player player);
	public String getLastTrumpSuit();

	public void setLastTrumpSuit(String lastTrumpSuit);

	public int getLastClassPoint() ;

	public void setLastClassPoint(int lastClassPoint);


	/**
	 * 登记打过的各轮牌
	 * @param cardsOnTable
	 */
	public void recordCardsOnTable(List<ICard> cardsOnTable);
	/**
	 * 清空打过的各轮牌
	 */
	public void emptyCardsOnTable();
	/**
	 * 获取全部打过各轮牌
	 * @return
	 */
	public List<ICard> getCardsOnTable();
/**
 * 获得双方出牌信息,0位表先出，1位后手
 * @return
 */
	public List<Map<Player,List<ICard>>> getBothOutCards();
/**
 * 保存出牌信息,0位表先出，1位后手
 * @param bothOutCards
 */
	public void setBothOutCards(List<Map<Player,List<ICard>>> bothOutCards) ;
	/**
	 * 获得先抓牌人
	 * @return
	 */
	public Player getFirstFetcher();

	/**
	 * 设置先抓牌人
	 * @param firstFetcher
	 */
	public void setFirstFetcher(Player firstFetcher);
	
	/**
	 * 获得当前庄家
	 * @return
	 */
	public Player getCurrentBanker();
/**
 * 设置当前庄家
 * @param currentBanker
 */
	public void setCurrentBanker(Player currentBanker);
	
	public void setStageOfRound(int stageOfRound);
}
