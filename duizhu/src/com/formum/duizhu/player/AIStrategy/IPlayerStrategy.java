package com.formum.duizhu.player.AIStrategy;

import java.util.List;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.player.powerCardsBag.IPowerCardsBag;

public interface IPlayerStrategy {
	
	
	public static final int TARGET_WIN_BOTH=1;//连扣带破（留大牌，破分）
	public static final int TARGET_WIN_ROUND=2;//扣底（留大牌，杀分有限--杀大分，后手分杀）
	public static final int TARGET_WIN_SCORES=3;//破分（见分能得则得）
	
	/**
	 * 根据手牌制定策略.
	 * @param cardsInHand
	 * @return int 策略代码
	 */
	public int makeStrategy(List<ICard> cardsInHand);
	
	/**
	 * 先手建议
	 * @param tagert
	 * @param powerCardsBag
	 * @return List<ICard>
	 */

	public List<ICard> suggestCardsAsForeHand(int target,IPowerCardsBag powerCardsBag);
	/**
	 * 后手建议
	 * @param tagert
	 * @param powerCardsBag 
	 * @return List<ICard>
	 */
	public List<ICard> suggestCardsAsAfterHand(IPowerCardsBag powerCardsBag,List<ICard> opponentCards);
		
	
}
