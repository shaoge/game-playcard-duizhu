package com.formum.duizhu.player.powerCardsBag;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.formum.duizhu.cardDeck.card.ICard;

public interface IPowerCardsBag {
	/**
	 * 为robot加工能量包。牌是靠不同的组合产生威力的。
	 */
	public void makeUpPowerCardsBags() ;
	
	public List<ICard> getCards4() ;
	public List<ICard> getTopCard() ;
	public List<ICard> getOtherTrumpCards();

	public List<ICard> getStraightHeartCards();

	public List<ICard> getStraightClubCards();

	public List<ICard> getStraightSpadeCards();

	public List<ICard> getStraightDiamondCards() ;
	public List<ICard> getHeartCards() ;

	public List<ICard> getClubCards();

	public List<ICard> getSpadeCards() ;

	public List<ICard> getDiamondCards();
	public List<ICard> getJoker2TrumpCards();

	public List<ICard> getClassPointTrumpCards() ;
	public List<ICard> getScoreTrumpCards() ;

	public List<ICard> getPoint2TrumpCards();

	public List<ICard> getComlexScoreCards_otherCardsSuit();
	
	public List<ICard> getSmallTrumpCards(); 
	
	/**
	 * 小副牌同步合集
	 * @return
	 */
	public List<ICard> getComplex4suitCards() ;
	/**
	 * 查找甩牌牌数
	 * @return
	 */
	public int allStraightSuitSum();
	/**
	 * 非甩牌中副牌顶级牌（只限于ACE或King(当ACE为主将点时)）
	 * @return
	 */
	public int allNorStraightSuitTopCardSum();
	/**
	 * 副牌中危险分数牌分数和
	 * @return
	 */
	public int allNorStraightDangerScoreCardsSum();
	/**
	 *  弱牌数
	 * @return
	 */
	public int weakCards();
/**
 * 返回压制性牌，性价比高，最好带上分
 * @param opponentCards
 * @return
 */
	public List<ICard> trumpWithScoreSuppressCards(
			List<ICard> opponentCards);
/**
 * 返回主牌能管事的牌，最好分主，其次小主，再次其它较大牌
 * @param opponentCards
 * @return
 */
public List<ICard> higherRankThanOpponent(List<ICard> opponentCards);
/**
 * 返回能管副牌的牌，先副后主，最好用分。
 * @param opponentCards
 * @return
 */
public List<ICard> killerToOpponentNoTrumpCards(List<ICard> opponentCards);

/**
 * 随翁牌
 * @param opponentCards
 * @return
 */
public List<ICard> followCards4(List<ICard> opponentCards);
/**
 *  随甩牌
 * @param opponentInfo 
 * @param opponentCards
 * @return
 */
public List<ICard> followStraightCards(List<ICard> opponentCards);
/**
 * 随主牌
 * @param opponentCards
 * @return
 */
public List<ICard> followTrumpCard(List<ICard> opponentCards);
/**
 * 随副牌
 * @param opponentCards
 * @return
 */
public List<ICard> followNorTrumpCard(List<ICard> opponentCards);
/**
 * 杂牌中有分的牌
 * @return
 */
public List<ICard> getComplex4suitCardsWithScore() ;
/**
 * 杂牌中有ACE的牌
 * @return
 */
public List<ICard> getComplex4suitCardsWithAce() ;
/**
 * 无分杂副牌
 * @return
 */
public List<ICard> getComplex4suitCardsNoScore() ;
/**
 * 无分无Ace杂副牌
 * @return
 */
public List<ICard> getComplex4suitCardsNoScoreNoAce() ;
/**
 * 全部甩牌
 * @return
 */
public List<ICard> getAllStraightCards() ;
/**
 * 全部甩牌中无分部分
 * @return
 */
public List<ICard> getAllStraightCardsNoScore() ;
/**
 * 全部甩牌中无分并无ACE部分
 * @return
 */
public List<ICard> getAllStraightCardsNoScoreNoAce();
/**
 * 全部甩牌中分牌
 * @return
 */
public List<ICard> getAllStraightCardsWithScore();
/**
 * 全部甩牌中ACE牌
 * @return
 */
public List<ICard> getAllStraightCardsWithAce() ;
/**
 * 全部主牌
 * @return
 */
public List<ICard> getAllTrumpCards();
/**
 * 全部副牌
 * @return
 */
public List<ICard> getAllNonTrumpCards();
/**
 * 根据对手出的同类牌，找到更小的同类牌的集合
 * @param opponentInfo 
 * @param opponentCards
 * @return
 */
public List<ICard> smallerCardsVsOpponentInSameKind(Map<String, Integer> opponentInfo, List<ICard> opponentCards);


}
