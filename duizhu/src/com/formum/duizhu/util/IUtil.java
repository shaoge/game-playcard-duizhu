package com.formum.duizhu.util;

import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.widget.ImageView;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.recorder.IRecorder;


public interface IUtil {
	/**
	 * ��ʾ����ͼ��.
	 * @param context
	 * @param card
	 * @param viewlocation
	 * @return
	 */
	public ImageView showImage(Activity context,ICard card, int viewHost);
	/**
	 * ��ʾ�Ƶı���ͼ��
	 * @param context
	 * @param card
	 * @param viewHost
	 * @param drawableBackInt ---����ͼ��int
	 * @return
	 */
	public ImageView showImage(Activity context, ICard card, int viewHost,int drawableBackInt);
	/**
	 * �ѵ���ʾͼƬ
	 * @param context
	 * @param card
	 * @param viewHost
	 * @param drawableBackInt
	 * @return
	 */
	public void showImageInLayers(Activity context, List<ICard> cards,int viewHost);
	
	/**
	 * ��һ�����зֳ����е�ǰ����_����1 List
	 * @param cards
	 * @return
	 */
	public List<ICard> seperateTrumpsFromCards(List<ICard> cards,IRecorder recorder);
	
	/**
	 * �Ƚ���������Ԫ���������
	 * @param o1
	 * @param o2
	 * @return
	 */
	public boolean isArraySizeEqual(List<ICard> firstcards,List<ICard> secondcards);
	/**
	 * �϶��������Ƿ�ȫ������
	 * @param cards
	 * @return
	 */
	public boolean isAllTrumpCards(List<ICard> cards);
	/**
	 * ���������ҳ����һ����
	 * @param cards
	 * @return
	 */
	public ICard pickTopOneInTrumpCollection(List<ICard> cards);
	/**
	 * ��ͬ��ɫ���ҳ����һ�ţ��ǣ���������2��
	 * @param cards
	 * @return
	 */
	public ICard topSuitCard(List<ICard> cards);
	/**
	 * ������������
	 * @return
	 */
	public Stack<ICard> sortMirrorTrumpCards();
	/**
	 *��ÿ������ƵĽ�����
	 * @return
	 */
	public int capableShowingClassPoint();
	/**
	 * �鿴�Ƿ���ׯ��,1=�У�0=��
	 * @return
	 */
	public int isThereBanker();
	/**
	 * ���Ƿ�û��ͬ��ɫ
	 * @param cards
	 * @return
	 */
	public boolean noRepeatSuit(List<ICard> cards);
	/**
	 * �������Ƿ���������
	 * @param cards
	 * @return
	 */
	public boolean eachCardSamePoint(List<ICard> cards);
	/**
	 * �Ƿ�����
	 * @param cards
	 * @return
	 */
	public boolean isthereJoker(List<ICard> cards);
	/**
	 * �Ƿ�ͬ��ɫ����
	 * @param cards
	 * @return
	 */
	public boolean isSameNoTrumpCardsSuit(List<ICard> cards);
	/**
	 * �Ƿ�ͬ����������
	 * @param cards
	 * @return
	 */
	public boolean isSameClassPoint(List<ICard> cards);
	/**
	 * �϶��Ƿ�ȫ�ǲ��������컨��2����
	 * @param cards
	 * @return
	 */
	public boolean isSameCard2(List<ICard> cards);
	/**
	 * ����ѡ�����ڽס������㵥�űȽϴ�С
	 * ����1����ʾ��
	 * @return
	 */
	public int grantTenTrumpCardsAndAceRank();
	/**
	 * ��ɫ�嵥
	 * @param cards
	 * @return
	 */
	public List<String> suitList(List<ICard> cards);
	/**
	 * �Ӽ����в������ɫ������
	 * @param cards
	 * @param suitName
	 * @return
	 */
	public int countSingleSuit(List<ICard> cards,String suitName);
/**
 * ����ҷ�ׯ��ʶ
 * @param player
 * @return int 1 equals success
 */
	public int putBankerOn(Player player);
	
	/**
	 * ��������
	 * @param cards
	 * @return List
	 */
	public List<ICard> seperate4SamePointCards(List<ICard> cards);

	/**
	 * �ҳ����Ȩֵ����
	 * @param cards
	 * @return List
	 */
	public List<ICard> confirmTopCard(List<ICard> cards);
	/**
	 * ������
	 * @param cards
	 * @return int
	 */
	public int sumScores(List<ICard> cards);
	/**
	 * ����ĳ��ɫ�����е�˦��
	 * @param cards
	 * @param suit
	 * @return List
	 */
	public List<ICard> seperateStraightCards(List<ICard> cards) ;
	/**
	 * ���뽵�����еĵ�ɫ��
	 * @param cards
	 * @param suit
	 * @return List
	 */
	public List<ICard> seperateSameSuitCardsSortedDesc(List<ICard> cards,String suit);
	/**
	 * ����һɫ��׼��ɫ�ĸ��Ʋ�����Ҫ�ر���ACE��--��11����_��54����Ϊ����
	 * @param allCards
	 * @param suit
	 * @return
	 */
	public List<ICard> seperateStandardNoTrumpSameSuitSortedDesc(String suit);
	/**
	 * Ϊͬɫ���ָ����Ž���(Ҫ�ر���ACE)
	 * @param noTrumpCardsInHand
	 * @return
	 */
	public List<ICard> sortNoTrumpSameSuitInHandDesc(List<ICard> noTrumpSameSuitCardsInHand);
/**
 * �ֳ���ɫ���Ƽ�_������
 * @param cards
 * @param suit
 * @return
 */
	public List<ICard> seperateNoTrumpSameSuitCards(List<ICard> cards,String suit);
/**
 * �ֳ���ɫ���Ƽ�_������
 * @param cards
 * @return
 */
	public List<ICard> seperateNoTrumpCards(List<ICard> cards);
	
	/**
	 * �ƶ϶������ֵĸ���״̬
	 * @return
	 */
	public List<String> reasoningOpponentEmptyNorTrumpSuit(List<ICard> myCards_fore,List<ICard> opponentCards_after);

	/**
	 * �ƶ϶Է��Ƿ�����
	 * @param forehandCards
	 * @param afterhandCards
	 * @return
	 */
	public boolean reasoningOpponentTrump(List<ICard> myCards_fore,List<ICard> opponentCards_after);
	/**
	 * �ƶ϶Է�ĳ��ɫ�Ƿ��з�
	 * @param suit
	 * @return
	 */
	public boolean reasoningOpponentScoresInOneSuit(String suit);

	/**
	 * ����ɫ����ACE����ĩ��
	 * @param cardsWithAce
	 */
	public void swapAceFromTopToBottomForOneSuitCards(List<ICard> cardsWithAce);
	/**
	 * ����Ӣ�Ļ�ɫ������
	 * @param suit
	 * @return
	 */
	public String changeSuitFromEnglishToChinese(String suit);
	
	/**
	 * �����ƻ�ɫǰ�����򣺸���ɫ+6���
	 * @param myCardsFetched
	 * @return
	 */
	public List<ICard> sortCardsBeforeShowingTrumpSuit(List<ICard> myCardsFetched);
	/**
	 * �����ƻ�ɫǰ�����򣺸��Ƹ��֣�����+6���
	 * @param myCardsFetched
	 * @return
	 */
	public List<ICard> sortCardsAfterShowingTrumpSuit(List<ICard> myCardsFetched);
	/**
	 * ����������ʾ����
	 * @param outCards
	 */
	public void showOutCardOnTable(Activity context,List<ICard> outCards);
	/**
	 * ���¼�м�¼˫����һ�ֳ���
	 * @param cardsOut
	 * @param robot
	 * @param recorder
	 */
	public void recordBothSideOutCards(List<ICard> cardsOut, Player player,
			IRecorder recorder);
	/**
	 * �鿴�ǲ�������
	 * @param human
	 * @param recorder
	 */
	public boolean meOnTheOffensive(Player me, IRecorder recorder);
	/**
	 * ����Tagɾ����Ƭ
	 * @param string
	 */
	public void removeFragmentByTag(Activity context,String string);
	/**
	 * ֪ͨ�����˲��������Ƿ�����
	 * @param foreCards
	 * @param humanPutOutCards
	 */
	public void informRobot_HumanTrumpStatus(List<ICard> foreCards, List<ICard> humanPutOutCards);
	
}
