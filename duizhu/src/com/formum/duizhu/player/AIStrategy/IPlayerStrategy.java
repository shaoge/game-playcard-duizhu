package com.formum.duizhu.player.AIStrategy;

import java.util.List;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.player.powerCardsBag.IPowerCardsBag;

public interface IPlayerStrategy {
	
	
	public static final int TARGET_WIN_BOTH=1;//���۴��ƣ������ƣ��Ʒ֣�
	public static final int TARGET_WIN_ROUND=2;//�۵ף������ƣ�ɱ������--ɱ��֣����ַ�ɱ��
	public static final int TARGET_WIN_SCORES=3;//�Ʒ֣������ܵ���ã�
	
	/**
	 * ���������ƶ�����.
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
