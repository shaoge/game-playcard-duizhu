package com.formum.duizhu.player;

import java.util.List;
import java.util.Map;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.recorder.IRecorder;


public interface IPlayer {

	/**
	 * ����
	 * @param card
	 * @return
	 */
	public Map<?,?> selectTrumpSuit(ICard card);
	
	/**
	 *  ץ��
	 * @param card
	 */
	public void fetchCard(ICard card);
	/**
	 * ����ǰ���ƺͻ�ɫ����Լ�������(�۲���ģʽ)
	 * @param trumpSuit
	 */
	public void updateCurrentTrumpSuit(String currentTrumpSuit);
	/**
	 * ����ǰ���ƺ;ִε����Լ�������(�۲���ģʽ)
	 * @param currentClassPoint
	 */
	public void updateCurrentClassPoint(int currentClassPoint);
	
	
	/**
	 * ��̽����
	 * @param readyOutCards
	 * @return
	 */
	public  List<ICard> tryPutCardsOut( List<ICard> testOutCards);
	
	/**
	 *  ����
	 * @param cardDrawable_Ids
	 * @return
	 */
	public List<ICard> putCardsOut( List<ICard> readyOutCards);
	
	/**
	 * ��������
	 * @param suit
	 * @param point
	 * @return
	 */
	public ICard putCardOut(String suit,int point);
	
	/**
	 *  ����һ����
	 * @return
	 */
	public List<ICard> getSelfCardDeck();
	/**
	 * ����������
	 * @return
	 */
	public int countCards();
	/**
	 * �����Ʒ���
	 * @return
	 */
	public int sumScoreInHand();	
/**
 * ��ʾ���ƻ�ɫ
 * @return
 */
	public String getTrumpSuit();
	/**
	 * ��ʾ���ƽ���
	 * @return
	 */
	public int getTrumpPoint();
	/**
	 * ��������
	 * @return
	 */
	public String getPlayerName();
/**
 * ���������
 * @param playerName
 */
	public void setPlayerName(String playerName) ;
	/**
	 * �ڼ�¼Ա�ϵǼ�
	 * @param recorder
	 */
	public void registerOnRecorder(IRecorder recorder,Player player);



	public int getMyClassPoint() ;
	public void setMyClassPoint(int myClassPoint) ;
	
	public boolean isBanker();
	
	public void setBanker(boolean isBanker);
	
	public int getCurrentScore();
	public void setCurrentScore(int currentScore);

	public boolean isOpponentTrump();
	public void setOpponentTrump(boolean isOpponentTrump) ;
	public List<String> getOpponentNorTrumpSuits() ;
	public void setOpponentNorTrumpSuits(List<String> opponentNorTrumpSuits) ;

	
	/**
	 * ��ʼ�²�Է��ִ渱�ƻ�ɫ
	 * @param currentTrumpSuit
	 */
	public void initialDefaultOpponentNorTrumpSuits(String currentTrumpSuit);
	
	/**
	 * ����
	 * @return
	 */
	public ICard showTrumpSuit();
	/**
	 * ���������ע����
	 * @param playerCardDeck
	 */
	public void setPlayerCardDeck(List<ICard> playerCardDeck) ;
	/**
	 *��� �Ͼּ���
	 * @return
	 */
	public int getMyLastSetClassPoint() ;
/**
 *  �����Ͼּ���
 * @param myLastSetClassPoint
 */
	public void setMyLastSetClassPoint(int myLastSetClassPoint) ;
}
