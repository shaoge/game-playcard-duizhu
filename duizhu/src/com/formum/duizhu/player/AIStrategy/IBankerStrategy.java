package com.formum.duizhu.player.AIStrategy;

import java.util.List;
import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.player.powerCardsBag.IPowerCardsBag;

public interface IBankerStrategy {
	/**
	 * ����Ŀ�꣺���ף����֣����
	 * 
	 * ��Դ����ֵ�����֣������̣��֣�˦��Ace���ƣ�,���Ʒֳ� �������List
	 * 
	 *��ʵĿ�꼰���ԣ���ɣ������Ƶ��ף�����ɱ��ɱ�������ף������Ƶ��ף������֣���ɱɱ�֣�
	 * 
	 *
	 * 
	 * �鱨��Դ����̬--���ƣ���̬--���ָ���ɱ���������з�����˫�����ֺ�����Ƹ��ʣ�
	 * 
	 * �鱨���������֣������������������Ϣ������; �ҷ��ܷ���ס��
	 * 	 * 
	 * �Է�����Ŀ�꣺�Ʒ֣��۵ף����۴���
	 * 
	 *
	 * 
	 * ÿ�ֳ��ƽ��飺���ݲ��ԡ�������Ⱥ��֣�����һ�ֹ�������
	 * 
	 */
	public static final int TARGET_WIN_BOTH_0SCORE_JOKER=1001;//����⣨�����ƣ�ɱ�֣�
	public static final int TARGET_WIN_BOTH_0SCORE=1000;//��⣨�����ƣ�ɱ�֣�
	public static final int TARGET_WIN_BOTH_FEWSCORE_JOKER=2001;//����ɣ������ƣ�ɱ�֣�
	public static final int TARGET_WIN_BOTH_FEWSCORE=2000;//��ɣ������ƣ�ɱ�֣�
	public static final int TARGET_WIN_ROUND_JOKER=3001;//�����ף������ƣ�ɱ������--ɱ��֣����ַ�ɱ��
	public static final int TARGET_WIN_ROUND=3000;//���ף������ƣ�ɱ������--ɱ��֣����ַ�ɱ��
	public static final int TARGET_WIN_SCORES=4000;//�����ƣ�������ɱ��ɱ�����ַ�ɱ��
	
	/**
	 * ���������ƶ�����
	 * -------------------------------------------------
 	 * ���е�ǰ�����ݣ�
	 * 1����û�б�����
	 * 2����������������5������ƽ���ɱ���
	 * 3��Σ�շ����������е���15�֣�
	 * 4������������������������--�Ƕ����Ʒֻ��ᣩ
	 * -------------------------------------------------
	 * @param cardsInHand
	 * @return int ���Դ���
	 */
	public int makeStrategy(List<ICard> cardsInHand);
	
	/**
	 * ���ֽ���
	 * @param tagert
	 * @param powerCardsBag
	 * @return List<ICard>
	 */

	public List<ICard> suggestCardsAsForeHand(int target,IPowerCardsBag powerCardsBag);
	/**
	 * ���ֽ���
	 * @param tagert
	 * @param powerCardsBag 
	 * @return List<ICard>
	 */
	public List<ICard> suggestCardsAsAfterHand(IPowerCardsBag powerCardsBag,List<ICard> opponentCards);
	
	
	
}
