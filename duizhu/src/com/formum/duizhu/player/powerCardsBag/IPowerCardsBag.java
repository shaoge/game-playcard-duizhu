package com.formum.duizhu.player.powerCardsBag;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.formum.duizhu.cardDeck.card.ICard;

public interface IPowerCardsBag {
	/**
	 * Ϊrobot�ӹ������������ǿ���ͬ����ϲ��������ġ�
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
	 * С����ͬ���ϼ�
	 * @return
	 */
	public List<ICard> getComplex4suitCards() ;
	/**
	 * ����˦������
	 * @return
	 */
	public int allStraightSuitSum();
	/**
	 * ��˦���и��ƶ����ƣ�ֻ����ACE��King(��ACEΪ������ʱ)��
	 * @return
	 */
	public int allNorStraightSuitTopCardSum();
	/**
	 * ������Σ�շ����Ʒ�����
	 * @return
	 */
	public int allNorStraightDangerScoreCardsSum();
	/**
	 *  ������
	 * @return
	 */
	public int weakCards();
/**
 * ����ѹ�����ƣ��Լ۱ȸߣ���ô��Ϸ�
 * @param opponentCards
 * @return
 */
	public List<ICard> trumpWithScoreSuppressCards(
			List<ICard> opponentCards);
/**
 * ���������ܹ��µ��ƣ���÷��������С�����ٴ������ϴ���
 * @param opponentCards
 * @return
 */
public List<ICard> higherRankThanOpponent(List<ICard> opponentCards);
/**
 * �����ܹܸ��Ƶ��ƣ��ȸ�����������÷֡�
 * @param opponentCards
 * @return
 */
public List<ICard> killerToOpponentNoTrumpCards(List<ICard> opponentCards);

/**
 * ������
 * @param opponentCards
 * @return
 */
public List<ICard> followCards4(List<ICard> opponentCards);
/**
 *  ��˦��
 * @param opponentInfo 
 * @param opponentCards
 * @return
 */
public List<ICard> followStraightCards(List<ICard> opponentCards);
/**
 * ������
 * @param opponentCards
 * @return
 */
public List<ICard> followTrumpCard(List<ICard> opponentCards);
/**
 * �渱��
 * @param opponentCards
 * @return
 */
public List<ICard> followNorTrumpCard(List<ICard> opponentCards);
/**
 * �������зֵ���
 * @return
 */
public List<ICard> getComplex4suitCardsWithScore() ;
/**
 * ��������ACE����
 * @return
 */
public List<ICard> getComplex4suitCardsWithAce() ;
/**
 * �޷��Ӹ���
 * @return
 */
public List<ICard> getComplex4suitCardsNoScore() ;
/**
 * �޷���Ace�Ӹ���
 * @return
 */
public List<ICard> getComplex4suitCardsNoScoreNoAce() ;
/**
 * ȫ��˦��
 * @return
 */
public List<ICard> getAllStraightCards() ;
/**
 * ȫ��˦�����޷ֲ���
 * @return
 */
public List<ICard> getAllStraightCardsNoScore() ;
/**
 * ȫ��˦�����޷ֲ���ACE����
 * @return
 */
public List<ICard> getAllStraightCardsNoScoreNoAce();
/**
 * ȫ��˦���з���
 * @return
 */
public List<ICard> getAllStraightCardsWithScore();
/**
 * ȫ��˦����ACE��
 * @return
 */
public List<ICard> getAllStraightCardsWithAce() ;
/**
 * ȫ������
 * @return
 */
public List<ICard> getAllTrumpCards();
/**
 * ȫ������
 * @return
 */
public List<ICard> getAllNonTrumpCards();
/**
 * ���ݶ��ֳ���ͬ���ƣ��ҵ���С��ͬ���Ƶļ���
 * @param opponentInfo 
 * @param opponentCards
 * @return
 */
public List<ICard> smallerCardsVsOpponentInSameKind(Map<String, Integer> opponentInfo, List<ICard> opponentCards);


}
