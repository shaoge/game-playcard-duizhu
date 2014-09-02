package com.formum.duizhu.player.AIStrategy;

import java.util.List;
import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.player.powerCardsBag.IPowerCardsBag;

public interface IBankerStrategy {
	/**
	 * 规则目标：保底，保分，打光
	 * 
	 * 资源及价值：自手（主，翁，分，甩，Ace副牌）,把牌分成 组合牌形List
	 * 
	 *现实目标及策略：打成（保大牌到底，分能杀则杀），保底（保大牌到底），保分（分杀杀分）
	 * 
	 *
	 * 
	 * 情报来源：静态--手牌；动态--对手跟或杀，对手牌中分数，双方出手后对手牌概率，
	 * 
	 * 情报分析：对手：有无最大主（大王信息特例）; 我方能否守住。
	 * 	 * 
	 * 对方可能目标：破分，扣底；连扣带破
	 * 
	 *
	 * 
	 * 每轮出牌建议：根据策略、规则和先后手，返回一种攻防牌形
	 * 
	 */
	public static final int TARGET_WIN_BOTH_0SCORE_JOKER=1001;//王打光（留大牌，杀分）
	public static final int TARGET_WIN_BOTH_0SCORE=1000;//打光（留大牌，杀分）
	public static final int TARGET_WIN_BOTH_FEWSCORE_JOKER=2001;//王打成（留大牌，杀分）
	public static final int TARGET_WIN_BOTH_FEWSCORE=2000;//打成（留大牌，杀分）
	public static final int TARGET_WIN_ROUND_JOKER=3001;//王保底（留大牌，杀分有限--杀大分，后手分杀）
	public static final int TARGET_WIN_ROUND=3000;//保底（留大牌，杀分有限--杀大分，后手分杀）
	public static final int TARGET_WIN_SCORES=4000;//保不破（见分能杀则杀，后手分杀）
	
	/**
	 * 根据手牌制定策略
	 * -------------------------------------------------
 	 * 已有的前提数据：
	 * 1、有没有保底牌
	 * 2、足量主牌数量（5张以上平均可保）
	 * 3、危险分数（弱牌中低于15分）
	 * 4、弱牌数量（副牌三张以上--是对手破分机会）
	 * -------------------------------------------------
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
