package com.formum.duizhu.player.powerCardsBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.formum.duizhu.cardDeck.CardDeck;
import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.data.Data4Robot;
import com.formum.duizhu.gameRule.GameRule;
import com.formum.duizhu.gameRule.IGameRule;
import com.formum.duizhu.player.AIPlay;
import com.formum.duizhu.player.GeneralPlay;
import com.formum.duizhu.player.HumanPlayer;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.player.RobotPlayer;
import com.formum.duizhu.recorder.IRecorder;
import com.formum.duizhu.recorder.Recorder;
import com.formum.duizhu.util.IUtil;
import com.formum.duizhu.util.Util;

public class RobotPowerCardsBag implements IPowerCardsBag {
    private static volatile RobotPowerCardsBag INSTANCE = null;
    private Player robot = null;
    private IUtil tools = Util.getInstance();
    private IRecorder recorder = Recorder.getInstance();
    private Data4Robot data4Robot=Data4Robot.getInstance();


    // �������׼�������������ϻ��� ----�̣��ص״��ƣ����������� ��˦�ƣ�Ace�������ƣ����Ӹ�

    private List<ICard> cards4 = new ArrayList<ICard>();// ----��
    private List<ICard> topCard = new ArrayList<ICard>();// ----����ص׵���

    private List<ICard> allTrumpCards = new ArrayList<ICard>();// ----��������
    private List<ICard> allNonTrumpCards = new ArrayList<ICard>();// ----���и���

    private List<ICard> otherTrumpCards = new ArrayList<ICard>();// ----�������ƣ�����������������ص��Ƶ��������ƣ�
    private List<ICard> joker2TrumpCards = new ArrayList<ICard>();// ----С������
    private List<ICard> classPointTrumpCards = new ArrayList<ICard>();// ----��������
    private List<ICard> point2TrumpCards = new ArrayList<ICard>();// ----2����
    private List<ICard> scoreTrumpCards = new ArrayList<ICard>();// ----��������
    private List<ICard> smallTrumpCards = new ArrayList<ICard>();// ----ȥ�����ַ���������

    private List<ICard> straightHeartCards = new ArrayList<ICard>();// ----����˦��
    private List<ICard> straightClubCards = new ArrayList<ICard>();// ----�ݻ�˦��
    private List<ICard> straightSpadeCards = new ArrayList<ICard>();// ----����˦��
    private List<ICard> straightDiamondCards = new ArrayList<ICard>();// ----����˦��
    private List<ICard> allStraightCards = new ArrayList<ICard>();// ----����˦��
    private List<ICard> allStraightCardsWithScore = new ArrayList<ICard>();// ----����˦���з���
    private List<ICard> allStraightCardsWithAce = new ArrayList<ICard>();// ----����˦����ACE
    private List<ICard> allStraightCardsNoScore = new ArrayList<ICard>();// ----����ȥ�������˦��
    private List<ICard> allStraightCardsNoScoreNoAce = new ArrayList<ICard>();// ----����ȥ����ȥACE���˦��
    private List<ICard> heartCards = new ArrayList<ICard>();// ----������������
    private List<ICard> clubCards = new ArrayList<ICard>();// ----�ݻ���������
    private List<ICard> spadeCards = new ArrayList<ICard>();// ----������������
    private List<ICard> diamondCards = new ArrayList<ICard>();// ----������������
    private List<ICard> complex4suitCards = new ArrayList<ICard>();// ----ʣ�¸��Ƶĺϼ���ʽ---������Ԫ��ԭ����������
    private List<ICard> complex4suitCardsWithScore = new ArrayList<ICard>();// ----ʣ�¸����зֵ���
    private List<ICard> complex4suitCardsWithAce = new ArrayList<ICard>();// ----ʣ�¸�����ACE����
    private List<ICard> complex4suitCardsNoScore = new ArrayList<ICard>();// ----ʣ�¸��Ƶ��޷ֺϼ�
    private List<ICard> complex4suitCardsNoScoreNoAce = new ArrayList<ICard>();// ----ʣ�¸��Ƶ��޷��ƣ���Ace�ϼ�
    private List<ICard> comlexScoreCards_otherCardsSuit = new ArrayList<ICard>();// ----ʣ�¸��Ƶĺϼ��е���ɫ--�з����зǷ���

    private RobotPowerCardsBag(Player robot) {
	this.robot = robot;
    }

    public static RobotPowerCardsBag getInstance(Player robot) {
	if (INSTANCE == null) {
	    synchronized (RobotPowerCardsBag.class) {
		if (INSTANCE == null) {
		    INSTANCE = new RobotPowerCardsBag(robot);
		}
	    }
	}
	return INSTANCE;
    }

    /**
     * ��������������������������.
     */
    public void makeUpPowerCardsBags() {
	// �ȼ������������
	this.clearAllContainers();
	
	//TODO:
	Log.v("whoIsRoundWinner", "@RobotPowerCardsBag Robot getSelfCardDeck().size() "+RobotPlayer.getInstance(AIPlay.getInstance()).actions().getSelfCardDeck().size());
	Log.v("whoIsRoundWinner", "@RobotPowerCardsBag Robot Human getSelfCardDeck().size() "+HumanPlayer.getInstance(GeneralPlay.getInstance()).actions().getSelfCardDeck().size());
	// �������--�仯--����
	List<ICard> myCardsInHand = new ArrayList<ICard>();
	myCardsInHand.addAll(data4Robot.getRobotHoldingCards());//����ǿ��Լ����ȷ
	// ��û���ƣ�
	boolean yesMyCardsInHand = !myCardsInHand.isEmpty();
	// �����ֿ�
	if (yesMyCardsInHand) {
	    this.allTrumpCards.addAll(tools.seperateTrumpsFromCards(myCardsInHand, recorder));
	    this.allNonTrumpCards.addAll(this.seperateAllNonTrumpCards(myCardsInHand));
	}
	// �ȷ���---����
	if (yesMyCardsInHand) {
	    this.seprateCards4(myCardsInHand);
	}
	// ���������
	// ����---����ص���
	if (yesMyCardsInHand) {
	    this.seprateTopCard(myCardsInHand);
	}
	// ���������
	// ����---���ƺ͸���
	if (yesMyCardsInHand) {
	    // ��������
	    otherTrumpCards.addAll(tools.seperateTrumpsFromCards(myCardsInHand, recorder));
	    myCardsInHand.removeAll(otherTrumpCards);// ������
	}
	// ���������
	// ����--����ɫ���ƺ�˦��(myCardsInHand�Ĳ���)
	if (yesMyCardsInHand) {
	    // ˦�ƺ͸���ɫ����
	    this.seprateVarySuitsAsStraightCards(myCardsInHand);
	    this.allStraightCards.addAll(this.seperateAllStraightCards());// ȫ��˦��
	    this.allStraightCardsNoScore.addAll(this.seperateAllStraightCardsNoScore());// ˦��ȥ��
	    this.allStraightCardsNoScoreNoAce.addAll(this.seperateAllStraightCardsNoScoreNoAce());// ˦��ȥ��ȥACE
	    this.allStraightCardsWithScore.addAll(this.seperateAllStraightCardsWithScore());// ˦���з���
	    this.allStraightCardsWithAce.addAll(this.seperateAllStraightCardsWithAce());// ˦����ACE��

	    // ʣ�Ӹ��ƺϼ�
	    this.seprateOddNorTrumpsCards();
	    boolean hasComplex = !complex4suitCards.isEmpty();
	    // �ֳ��з����ӣ��ҷ����ĸ���
	    if (hasComplex) {
		this.complex4suitCardsNoScore.addAll(this.seperateComplex4suitCardsNoScore(complex4suitCards));// �Ӹ��޷���
		this.complex4suitCardsNoScoreNoAce.addAll(this.seperateComplex4suitCardsNoScoreNoAce(complex4suitCards));// �Ӹ��޷���ACE��
		this.complex4suitCardsWithScore.addAll(this.seperateComplex4suitCardsWithScore());// �Ӹ��з���
		this.complex4suitCardsWithAce.addAll(this.seperateComplex4suitCardsWithAce());// �Ӹ���ACE��

		this.seperateComlexScoreCards_otherCardsSuit(complex4suitCards);
	    }
	}
	// ������������ƣ��ٷ��ࣨ����ʽȡ�ƣ���С��������������2,����
	boolean yesOtherTrumpCards = !otherTrumpCards.isEmpty();
	if (yesOtherTrumpCards) {
	    this.seprateDiffTrumpCards();
	}

	// ��һЩ�д����ϵ��������������
	this.sortSomeBags();
    }

    private void sortSomeBags() {
	// ��Ϊʵ��Comparable�ӿ�ֱ��ʵ��Ĭ�Ϸ�����������
	Collections.sort(cards4);// ----��

	Collections.sort(allNonTrumpCards);// ----���и���

	Collections.sort(scoreTrumpCards);// ----��������
	Collections.sort(smallTrumpCards);// --------ȥ�����ַ���������
	Collections.sort(straightHeartCards);// ----����˦��
	Collections.sort(straightClubCards);// ----�ݻ�˦��
	Collections.sort(straightSpadeCards);// ----����˦��
	Collections.sort(straightDiamondCards);// ----����˦��
	Collections.sort(allStraightCards);// ----����˦��
	Collections.sort(allStraightCardsWithScore);// ----����˦���з���
	Collections.sort(allStraightCardsWithAce);// ----����˦����ACE
	Collections.sort(allStraightCardsNoScore);// ----����ȥ�������˦��
	Collections.sort(allStraightCardsNoScoreNoAce);// ----����ȥ����ȥACE���˦��
	Collections.sort(heartCards);// ----������������
	Collections.sort(clubCards);// ----�ݻ���������
	Collections.sort(spadeCards);// ----������������
	Collections.sort(diamondCards);// ----������������
	Collections.sort(complex4suitCards);// ----ʣ�¸��Ƶĺϼ���ʽ---������Ԫ��ԭ����������
	Collections.sort(complex4suitCardsWithScore);// ----ʣ�¸����зֵ���
	Collections.sort(complex4suitCardsWithAce);// ----ʣ�¸�����ACE����
	Collections.sort(complex4suitCardsNoScore);// ----ʣ�¸��Ƶ��޷ֺϼ�
	Collections.sort(complex4suitCardsNoScoreNoAce);// ----ʣ�¸��Ƶ��޷��ƣ���Ace�ϼ�
	Collections.sort(comlexScoreCards_otherCardsSuit);// ----ʣ�¸��Ƶĺϼ��е���ɫ--�з����зǷ���

    }

    private List<ICard> seperateAllNonTrumpCards(List<ICard> myCardsInHand) {
	List<ICard> result = new ArrayList<ICard>();
	// ȫ�Ƽ�����
	result.addAll(myCardsInHand);
	result.removeAll(tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder));
	return result;
    }

    /**
     * �Ӹ���ACE��
     *
     * @return
     */
    private List<ICard> seperateComplex4suitCardsWithAce() {
	List<ICard> result = new ArrayList<ICard>();
	// ����ACE
	for (ICard iCard : this.complex4suitCards) {
	    boolean ace = iCard.getPoint() == 1;
	    if (ace) {
		result.add(iCard);
	    }
	}
	return result;
    }

    /**
     * �Ӹ��з���
     *
     * @return
     */
    private List<ICard> seperateComplex4suitCardsWithScore() {
	List<ICard> result = new ArrayList<ICard>();
	// ����
	for (ICard iCard : this.complex4suitCards) {
	    boolean score5 = iCard.getPoint() == IGameRule.CARD_5_EQUAL_SCORE;
	    boolean score10 = iCard.getPoint() == IGameRule.CARD_10_EQUAL_SCORE;
	    boolean score13 = iCard.getPoint() == 13;
	    if (score13 || score10 || score5) {
		result.add(iCard);
	    }
	}

	return result;
    }

    /**
     * ˦����ACE��
     *
     * @return
     */
    private List<ICard> seperateAllStraightCardsWithAce() {
	List<ICard> result = new ArrayList<ICard>();
	// ����ACE
	for (ICard iCard : this.allStraightCards) {
	    boolean ace = iCard.getPoint() == 1;
	    if (ace) {
		result.add(iCard);
	    }
	}
	return result;
    }

    /**
     * ˦���з���
     *
     * @return
     */
    private List<ICard> seperateAllStraightCardsWithScore() {
	List<ICard> result = new ArrayList<ICard>();
	// ����
	for (ICard iCard : this.allStraightCards) {
	    boolean score5 = iCard.getPoint() == IGameRule.CARD_5_EQUAL_SCORE;
	    boolean score10 = iCard.getPoint() == IGameRule.CARD_10_EQUAL_SCORE;
	    boolean score13 = iCard.getPoint() == 13;
	    if (score13 || score10 || score5) {
		result.add(iCard);
	    }
	}
	return result;
    }

    private List<ICard> seperateAllStraightCardsNoScoreNoAce() {
	List<ICard> result = new ArrayList<ICard>();
	// �ų�������ACE
	for (ICard iCard : this.allStraightCardsNoScore) {
	    boolean ace = iCard.getPoint() == 1;
	    if (!ace) {
		result.add(iCard);
	    }
	}

	return result;
    }

    private List<ICard> seperateAllStraightCardsNoScore() {
	List<ICard> result = new ArrayList<ICard>();
	// �ų�����
	for (ICard iCard : this.allStraightCards) {
	    boolean score5 = iCard.getPoint() == IGameRule.CARD_5_EQUAL_SCORE;
	    boolean score10 = iCard.getPoint() == IGameRule.CARD_10_EQUAL_SCORE;
	    boolean score13 = iCard.getPoint() == 13;
	    if (!score13 && !score10 && !score5) {
		result.add(iCard);
	    }
	}

	return result;
    }

    private List<ICard> seperateAllStraightCards() {
	List<ICard> result = new ArrayList<ICard>();
	// ȫ��˦�����
	result.addAll(this.straightClubCards);
	result.addAll(this.straightDiamondCards);
	result.addAll(this.straightHeartCards);
	result.addAll(this.straightSpadeCards);
	return result;
    }

    /**
     * �����޷���Ace����
     *
     * @param complex4suitCards2
     * @return
     */
    private List<ICard> seperateComplex4suitCardsNoScoreNoAce(List<ICard> complex4suitCards) {
	List<ICard> result = new ArrayList<ICard>();
	List<ICard> tempList = new ArrayList<ICard>();
	tempList.addAll(this.seperateComplex4suitCardsNoScore(complex4suitCards));// ȥ�������

	for (ICard iCard : tempList) {
	    boolean ace = (iCard.getPoint() == 1);// �ж�ACE
	    if (!ace) {
		result.add(iCard);
	    }
	}
	return result;
    }

    /**
     * �����޷ָ���
     *
     * @param complex4suitCards2
     * @return
     */
    private List<ICard> seperateComplex4suitCardsNoScore(List<ICard> complex4suitCards) {
	List<ICard> result = new ArrayList<ICard>();
	for (ICard iCard : complex4suitCards) {
	    boolean score5 = (iCard.getPoint() == IGameRule.CARD_5_EQUAL_SCORE);
	    boolean score10 = (iCard.getPoint() == IGameRule.CARD_10_EQUAL_SCORE);
	    boolean score13 = (iCard.getPoint() == 13);
	    if (!score13 && !score10 && !score5) {
		result.add(iCard);
	    }
	}
	return result;
    }

    private void seperateComlexScoreCards_otherCardsSuit(List<ICard> complex4suitCards) {

	// �з����ӣ������ĸ���
	// ��ɫ
	List<ICard> hearts = tools.seperateSameSuitCardsSortedDesc(complex4suitCards, "heart");
	List<ICard> clubs = tools.seperateSameSuitCardsSortedDesc(complex4suitCards, "club");
	List<ICard> spades = tools.seperateSameSuitCardsSortedDesc(complex4suitCards, "spade");
	List<ICard> diamonds = tools.seperateSameSuitCardsSortedDesc(complex4suitCards, "diamond");

	// ȫ����-��������
	int heartSoreCards = this.totalMinusScoreCards(hearts);
	int clubSoreCards = this.totalMinusScoreCards(clubs);
	int spadeSoreCards = this.totalMinusScoreCards(spades);
	int diamondSoreCards = this.totalMinusScoreCards(diamonds);

	boolean isH = heartSoreCards > 0 && heartSoreCards >= clubSoreCards && heartSoreCards >= spadeSoreCards && heartSoreCards >= diamondSoreCards;
	boolean isC = clubSoreCards > 0 && clubSoreCards >= heartSoreCards && clubSoreCards >= spadeSoreCards && clubSoreCards >= diamondSoreCards;
	boolean isS = spadeSoreCards > 0 && spadeSoreCards >= heartSoreCards && spadeSoreCards >= clubSoreCards && spadeSoreCards >= diamondSoreCards;
	boolean isD = diamondSoreCards > 0 && diamondSoreCards >= heartSoreCards && diamondSoreCards >= clubSoreCards && diamondSoreCards >= spadeSoreCards;
	if (isH) {
	    comlexScoreCards_otherCardsSuit.addAll(hearts);
	} else if (isC) {
	    comlexScoreCards_otherCardsSuit.addAll(clubs);
	} else if (isS) {
	    comlexScoreCards_otherCardsSuit.addAll(spades);
	} else if (isD) {
	    comlexScoreCards_otherCardsSuit.addAll(diamonds);
	} else {
	    comlexScoreCards_otherCardsSuit.clear();
	}

    }

    private int totalMinusScoreCards(List<ICard> cards) {
	int result = 0;
	int totalSize = cards.size();
	int scoreCardCount = 0;
	for (ICard iCard : cards) {
	    boolean hasScore = (iCard.getPoint() == IGameRule.CARD_5_EQUAL_SCORE || iCard.getPoint() == IGameRule.CARD_10_EQUAL_SCORE || iCard.getPoint() == 13);
	    if (hasScore) {
		scoreCardCount++;
	    }
	}
	result = totalSize - scoreCardCount;
	if (scoreCardCount == 0) {
	    // û�ֵķ���0
	    result = 0;
	}
	return result;
    }

    private void seprateVarySuitsAsStraightCards(List<ICard> myCardsInHand) {
	boolean yesHeartCards = !tools.seperateSameSuitCardsSortedDesc(myCardsInHand, "heart").isEmpty();
	if (yesHeartCards) {
	    this.seprateStraightHeart(myCardsInHand);
	}
	boolean yesClubCards = !tools.seperateSameSuitCardsSortedDesc(myCardsInHand, "club").isEmpty();
	if (yesClubCards) {
	    this.seprateStraightClub(myCardsInHand);
	}
	boolean yesSpadeCards = !tools.seperateSameSuitCardsSortedDesc(myCardsInHand, "spade").isEmpty();
	if (yesSpadeCards) {
	    this.seprateStraightSpade(myCardsInHand);

	}
	boolean yesDiamondCards = !tools.seperateSameSuitCardsSortedDesc(myCardsInHand, "diamond").isEmpty();
	if (yesDiamondCards) {
	    this.seprateStraightDiamond(myCardsInHand);

	}
    }

    private void seprateOddNorTrumpsCards() {
	complex4suitCards.addAll(heartCards);// ���ʣ�µ���
	complex4suitCards.addAll(clubCards);// ���ʣ�µ���
	complex4suitCards.addAll(spadeCards);// ���ʣ�µ���
	complex4suitCards.addAll(diamondCards);// ���ʣ�µ���

    }

    private void seprateStraightDiamond(List<ICard> myCardsInHand) {
	diamondCards.addAll(tools.seperateSameSuitCardsSortedDesc(myCardsInHand, "diamond"));
	// ---˦��

	straightDiamondCards.addAll(tools.seperateStraightCards(diamondCards));
	// �������鸱��
	diamondCards.removeAll(straightDiamondCards);
    }

    private void seprateStraightSpade(List<ICard> myCardsInHand) {
	spadeCards.addAll(tools.seperateSameSuitCardsSortedDesc(myCardsInHand, "spade"));
	// ---˦��

	straightSpadeCards.addAll(tools.seperateStraightCards(spadeCards));
	// �������Ҹ���
	spadeCards.removeAll(straightSpadeCards);
    }

    private void seprateStraightClub(List<ICard> myCardsInHand) {
	clubCards.addAll(tools.seperateSameSuitCardsSortedDesc(myCardsInHand, "club"));
	// ---˦��

	straightClubCards.addAll(tools.seperateStraightCards(clubCards));
	// �����ݻ�����
	clubCards.removeAll(straightClubCards);
    }

    private void seprateStraightHeart(List<ICard> myCardsInHand) {
	heartCards.addAll(tools.seperateSameSuitCardsSortedDesc(myCardsInHand, "heart"));
	// ---˦��
	straightHeartCards.addAll(tools.seperateStraightCards(heartCards));
	// �������ĸ���
	heartCards.removeAll(straightHeartCards);
    }

    private void seprateDiffTrumpCards() {
	for (ICard iCard : otherTrumpCards) {
	    // ----С������
	    boolean joker2 = (iCard.getSuit().equals("joker") && iCard.getPoint() == 2);
	    // ----��������
	    boolean classPoint = (iCard.getPoint() == recorder.getCurrentClassPoint());
	    // ----2����
	    boolean point2 = (!iCard.getSuit().equals("joker") && iCard.getPoint() == 2);
	    // ----���� ����
	    boolean scoreCard = (iCard.getPoint() == 5 || iCard.getPoint() == 10 || iCard.getPoint() == 13);

	    if (joker2) {
		joker2TrumpCards.add(iCard);
	    }

	    if (classPoint) {

		classPointTrumpCards.add(iCard);
	    }

	    if (point2) {

		point2TrumpCards.add(iCard);
	    }

	    if (scoreCard) {

		scoreTrumpCards.add(iCard);
	    }

	}
	// ���޷������
	this.smallTrumpCards.addAll(this.otherTrumpCards);
	this.smallTrumpCards.removeAll(this.joker2TrumpCards);
	this.smallTrumpCards.removeAll(this.classPointTrumpCards);
	this.smallTrumpCards.removeAll(this.point2TrumpCards);
	this.smallTrumpCards.removeAll(this.scoreTrumpCards);
    }

    private void seprateTopCard(List<ICard> myCardsInHand) {
	topCard.clear();
	topCard.addAll(tools.confirmTopCard(myCardsInHand));

	myCardsInHand.removeAll(topCard);// ������ص���
    }

    private void seprateCards4(List<ICard> myCardsInHand) {
	cards4.clear();

	cards4.addAll(tools.seperate4SamePointCards(myCardsInHand));
	myCardsInHand.removeAll(cards4);// ������
    }

    private void clearAllContainers() {
	cards4.clear();// ----��
	topCard.clear();// ----����ص׵���

	allTrumpCards.clear();// ----��������
	allNonTrumpCards.clear();// ----���и���

	otherTrumpCards.clear();// ----�������ƣ�����������������ص��Ƶ��������ƣ�
	joker2TrumpCards.clear();// ----С������
	classPointTrumpCards.clear();// ----��������
	point2TrumpCards.clear();// ----2����
	scoreTrumpCards.clear();// ----��������
	smallTrumpCards.clear();// ----ȥ�����ַ���������

	straightHeartCards.clear();// ----����˦��
	straightClubCards.clear();// ----�ݻ�˦��
	straightSpadeCards.clear();// ----����˦��
	straightDiamondCards.clear();// ----����˦��
	allStraightCards.clear();// ----����˦��
	allStraightCardsWithScore.clear();// ----����˦���з���
	allStraightCardsWithAce.clear();// ----����˦����ACE
	allStraightCardsNoScore.clear();// ----����ȥ�������˦��
	allStraightCardsNoScoreNoAce.clear();// ----����ȥ����ȥACE���˦��
	heartCards.clear();// ----������������
	clubCards.clear();// ----�ݻ���������
	spadeCards.clear();// ----������������
	diamondCards.clear();// ----������������
	complex4suitCards.clear();// ----ʣ�¸��Ƶĺϼ���ʽ---������Ԫ��ԭ����������
	complex4suitCardsWithScore.clear();// ----ʣ�¸����зֵ���
	complex4suitCardsWithAce.clear();// ----ʣ�¸�����ACE����
	complex4suitCardsNoScore.clear();// ----ʣ�¸��Ƶ��޷ֺϼ�
	complex4suitCardsNoScoreNoAce.clear();// ----ʣ�¸��Ƶ��޷��ƣ���Ace�ϼ�
	comlexScoreCards_otherCardsSuit.clear();// ----ʣ�¸��Ƶĺϼ��е���ɫ--�з����зǷ���
    }

    @Override
    public int allStraightSuitSum() {
	int result = 0;
	// ����������˦���Ƽ��ϣ�ͳ��
	int heart = this.straightHeartCards.size();
	int club = this.straightClubCards.size();
	int spade = this.straightSpadeCards.size();
	int diamond = this.straightDiamondCards.size();
	result = heart + club + spade + diamond;
	return result;
    }

    @Override
    public int allNorStraightSuitTopCardSum() {
	int result = 0;
	// �ϲ����׸��Ƽ��ϣ�ͳ��;��ACE״̬
	boolean aceTrump = recorder.getCurrentClassPoint() == 1;

	List<ICard> fourCollection = new ArrayList<ICard>();
	fourCollection.addAll(heartCards);
	fourCollection.addAll(clubCards);
	fourCollection.addAll(spadeCards);
	fourCollection.addAll(diamondCards);

	// �ǣ���KING
	if (aceTrump) {
	    for (ICard card : fourCollection) {
		boolean king = (card.getPoint() == 13);
		result += king ? 1 : 0;
	    }
	}
	// ���ǣ���ACE
	else {
	    for (ICard card : fourCollection) {
		boolean ace = (card.getPoint() == 1);
		result += ace ? 1 : 0;
	    }

	}
	return result;
    }

    @Override
    public int weakCards() {
	int result = 0;
	// �����ϼ��и�������ǿ����
	List<ICard> fourCollection = new ArrayList<ICard>();
	fourCollection.addAll(heartCards);
	fourCollection.addAll(clubCards);
	fourCollection.addAll(spadeCards);
	fourCollection.addAll(diamondCards);

	int rawsize = fourCollection.size();

	int strongsum = this.allNorStraightSuitTopCardSum();

	result = (rawsize - strongsum);

	return result;
    }

    @Override
    public int allNorStraightDangerScoreCardsSum() {
	int result = 0;
	// �ϲ����Ƽ��ϣ���ȥ�����д���
	List<ICard> fourCollection = new ArrayList<ICard>();
	fourCollection.addAll(heartCards);
	fourCollection.addAll(clubCards);
	fourCollection.addAll(spadeCards);
	fourCollection.addAll(diamondCards);
	List<ICard> tempKing = new ArrayList<ICard>();
	boolean aceTrump = recorder.getCurrentClassPoint() == 1;
	// king�󸱣�����Σ�֣��ɼ�
	if (aceTrump) {
	    for (ICard iCard : fourCollection) {
		boolean king = iCard.getPoint() == 13;
		if (king) {
		    tempKing.add(iCard);
		}
	    }
	}
	fourCollection.removeAll(tempKing);

	for (ICard iCard : fourCollection) {
	    boolean five = iCard.getPoint() == 5;
	    boolean ten = iCard.getPoint() == 10;
	    boolean thirteen = iCard.getPoint() == 13;
	    result += five ? IGameRule.CARD_5_EQUAL_SCORE : 0;
	    result += ten ? IGameRule.CARD_10_EQUAL_SCORE : 0;
	    result += thirteen ? IGameRule.CARD_13_EQUAL_SCORE : 0;
	}

	return result;
    }

    public List<ICard> getOtherTrumpCards() {
	return otherTrumpCards;
    }

    public List<ICard> getStraightHeartCards() {
	return straightHeartCards;
    }

    public List<ICard> getStraightClubCards() {
	return straightClubCards;
    }

    public List<ICard> getStraightSpadeCards() {
	return straightSpadeCards;
    }

    public List<ICard> getStraightDiamondCards() {
	return straightDiamondCards;
    }

    @Override
    public List<ICard> getCards4() {
	return cards4;
    }

    public List<ICard> getTopCard() {
	return topCard;
    }

    public List<ICard> getHeartCards() {
	return heartCards;
    }

    public List<ICard> getClubCards() {
	return clubCards;
    }

    public List<ICard> getSpadeCards() {
	return spadeCards;
    }

    public List<ICard> getDiamondCards() {
	return diamondCards;
    }

    public List<ICard> getJoker2TrumpCards() {
	return joker2TrumpCards;
    }

    public List<ICard> getClassPointTrumpCards() {
	return classPointTrumpCards;
    }

    public List<ICard> getPoint2TrumpCards() {
	return point2TrumpCards;
    }

    public List<ICard> getScoreTrumpCards() {
	Collections.sort(this.scoreTrumpCards);

	return this.scoreTrumpCards;
    }

    public List<ICard> getComplex4suitCards() {
	return this.complex4suitCards;
    }

    public List<ICard> getComlexScoreCards_otherCardsSuit() {
	return comlexScoreCards_otherCardsSuit;
    }

    public List<ICard> getSmallTrumpCards() {
	return smallTrumpCards;
    }

    @Override
    public List<ICard> trumpWithScoreSuppressCards(List<ICard> opponentCards) {
	List<ICard> result = new ArrayList<ICard>();
	// �����������ҷ�������С��
	int size = opponentCards.size();
	// �������������ҷ�����
	int count = 0;
	for (ICard iCard : this.scoreTrumpCards) {
	    boolean scoreCard = (iCard.getPoint() == IGameRule.CARD_5_EQUAL_SCORE || iCard.getPoint() == IGameRule.CARD_10_EQUAL_SCORE || iCard.getPoint() == 13);
	    if (scoreCard) {
		result.add(iCard);
		count++;
		if (count == size) {
		    break;
		}
	    }
	}

	if (size > result.size()) {
	    tools.swapAceFromTopToBottomForOneSuitCards(this.smallTrumpCards);// Ace�ŵ����
	    for (ICard iCard : this.smallTrumpCards) {
		result.add(iCard);
		count++;
		if (count == size) {
		    break;
		}
	    }

	    if (size > result.size()) {
		for (ICard iCard : this.point2TrumpCards) {
		    result.add(iCard);
		    count++;
		    if (count == size) {
			break;
		    }
		}
		if (size > result.size()) {
		    for (ICard iCard : this.classPointTrumpCards) {
			result.add(iCard);
			count++;
			if (count == size) {
			    break;
			}
		    }
		    if (size > result.size()) {
			for (ICard iCard : this.joker2TrumpCards) {
			    result.add(iCard);
			    count++;
			    if (count == size) {
				break;
			    }
			}
			if (size > result.size()) {
			    for (ICard iCard : this.cards4) {
				boolean trump = recorder.getCurrentTrumpSuit().equals(iCard.getSuit());
				if (trump) {
				    result.add(iCard);
				    count++;
				    if (count == size) {
					break;
				    }
				}
			    }
			    if (size > result.size()) {
				result.add(this.topCard.get(0));
			    }
			}
		    }
		}
	    }
	}

	return result;
    }

    @Override
    public List<ICard> higherRankThanOpponent(List<ICard> opponentCards) {
	List<ICard> result = new ArrayList<ICard>();
	IGameRule gameRule = GameRule.getInstance();
	// ��ȫ�����ֳ������֣��ֺͷǷ�--������
	List<ICard> noScoreTrumps = new ArrayList<ICard>();
	noScoreTrumps.addAll(tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder));
	noScoreTrumps.removeAll(this.scoreTrumpCards);
	// �����з�����
	List<ICard> temp = new ArrayList<ICard>();
	Collections.sort(this.scoreTrumpCards);// �ӵͷ�������
	for (ICard iCard : this.scoreTrumpCards) {
	    temp.clear();
	    temp.add(iCard);
	    boolean higher = gameRule.whoIsRoundWinner(opponentCards, temp).equals(robot);
	    if (higher) {
		result.add(iCard);
		break;
	    }
	}
	// Ȼ��������������������
	if (result.isEmpty()) {
	    tools.swapAceFromTopToBottomForOneSuitCards(smallTrumpCards);
	    for (ICard iCard : smallTrumpCards) {
		temp.clear();
		temp.add(iCard);
		boolean higher = gameRule.whoIsRoundWinner(opponentCards, temp).equals(robot);
		if (higher) {
		    result.add(iCard);
		    break;
		}
	    }
	    if (result.isEmpty()) {
		for (ICard iCard : point2TrumpCards) {
		    temp.clear();
		    temp.add(iCard);
		    boolean higher = gameRule.whoIsRoundWinner(opponentCards, temp).equals(robot);
		    if (higher) {
			result.add(iCard);
			break;
		    }
		}
		if (result.isEmpty()) {
		    for (ICard iCard : classPointTrumpCards) {
			temp.clear();
			temp.add(iCard);
			boolean higher = gameRule.whoIsRoundWinner(opponentCards, temp).equals(robot);
			if (higher) {
			    result.add(iCard);
			    break;
			}
		    }

		    if (result.isEmpty()) {
			for (ICard iCard : joker2TrumpCards) {
			    temp.clear();
			    temp.add(iCard);
			    boolean higher = gameRule.whoIsRoundWinner(opponentCards, temp).equals(robot);
			    if (higher) {
				result.add(iCard);
				break;
			    }
			}

			if (result.isEmpty()) {
			    for (ICard iCard : noScoreTrumps) {
				temp.clear();
				temp.add(iCard);
				boolean higher = gameRule.whoIsRoundWinner(opponentCards, temp).equals(robot);
				if (higher) {
				    result.add(iCard);
				    break;
				}
			    }
			}
		    }
		}
	    }
	}

	return result;
    }

    @Override
    public List<ICard> killerToOpponentNoTrumpCards(List<ICard> opponentCards) {
	List<ICard> result = new ArrayList<ICard>();
	// ��ɫ
	String suit = opponentCards.get(0).getSuit();

	if (result.isEmpty()) {
	    // ͬ���зָ���
	    result.addAll(this.findSameSuitCardsWithScore(opponentCards, suit));
	    if (result.isEmpty()) {
		// ͬ���Ӹ���--û��ҲûACE
		result.addAll(this.findSameSuitSmallCards(opponentCards, suit));
		if (result.isEmpty()) {
		    // ͬ������ACE
		    result.addAll(this.findAceCards(opponentCards, suit));
		    if (result.isEmpty()) {
			// ͬ�����и���
			result.addAll(this.findAllSameSuitCards(opponentCards, suit));
			if (result.isEmpty()) {
			    // �������ң������Ƿ�����С����
			    result.addAll(this.findTrump2Kill());
			}
		    }
		}
	    }
	}
	return result;
    }

    private List<ICard> findAceCards(List<ICard> opponentCards, String suit) {
	List<ICard> result = new ArrayList<ICard>();
	boolean hasCards = !this.complex4suitCardsWithAce.isEmpty();
	if (hasCards) {
	    List<ICard> temp = new ArrayList<ICard>();
	    for (ICard iCard : this.complex4suitCardsWithAce) {
		boolean isTheSuit = suit.equals(iCard.getSuit());
		if (isTheSuit) {
		    temp.clear();
		    temp.add(iCard);
		    boolean bigger = GameRule.getInstance().whoIsRoundWinner(opponentCards, temp).equals(robot);
		    if (bigger) {
			result.add(iCard);
			break;
		    }
		}
	    }
	}
	return result;
    }

    private List<ICard> findTrump2Kill() {

	List<ICard> result = new ArrayList<ICard>();

	// ���ȣ����з���������һ��
	boolean hasScore = !scoreTrumpCards.isEmpty();
	if (hasScore) {
	    result.add(scoreTrumpCards.get(0));
	}
	if (result.isEmpty()) {
	    // ���û�У�������������һ��
	    tools.swapAceFromTopToBottomForOneSuitCards(smallTrumpCards);
	    boolean hasCards = smallTrumpCards != null && smallTrumpCards.size() > 0;
	    if (hasCards) {
		result.add(smallTrumpCards.get(0));
	    }
	    if (result.isEmpty()) {
		// ��û�У���2
		boolean hasPoint2TrumpCards = point2TrumpCards.size() > 0;
		if (hasPoint2TrumpCards) {
		    result.add(point2TrumpCards.get(0));
		}
		if (result.isEmpty()) {
		    // ��û�У��ý���
		    boolean hasclassPointTrumpCards = classPointTrumpCards.size() > 0;
		    if (hasclassPointTrumpCards) {
			result.add(classPointTrumpCards.get(0));
		    }
		    if (result.isEmpty()) {
			// ��û�У���С��
			boolean hasjoker2TrumpCards = joker2TrumpCards.size() > 0;
			if (hasjoker2TrumpCards) {
			    result.add(joker2TrumpCards.get(0));
			}
			if (result.isEmpty()) {
			    // ��û�У����ƾͿ���
			    boolean hasTrumpCards = robot.actions().getSelfCardDeck().size() > 0;
			    if (hasTrumpCards) {
				result.add(tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder).get(0));
			    }
			}
		    }
		}
	    }
	}
	return result;
    }

    private List<ICard> findAllSameSuitCards(List<ICard> opponentCards, String suit) {

	List<ICard> result = new ArrayList<ICard>();
	// ͬɫȫ���Ƽ���
	List<ICard> theSuitCards = new ArrayList<ICard>();
	theSuitCards.addAll(tools.seperateNoTrumpSameSuitCards(RobotPowerCardsBag.getInstance(robot).getAllNonTrumpCards(), suit));
	boolean hasCards = !theSuitCards.isEmpty();
	if (hasCards) {
	    List<ICard> temp = new ArrayList<ICard>();
	    tools.swapAceFromTopToBottomForOneSuitCards(theSuitCards);
	    for (ICard iCard : theSuitCards) {
		temp.clear();
		temp.add(iCard);
		boolean bigger = GameRule.getInstance().whoIsRoundWinner(opponentCards, temp).equals(robot);
		if (bigger) {
		    result.add(iCard);
		    break;
		}
	    }
	}
	return result;
    }

    private List<ICard> findSameSuitSmallCards(List<ICard> opponentCards, String suit) {
	List<ICard> result = new ArrayList<ICard>();
	// ͬɫ�Ӹ��Ƽ���
	List<ICard> theSuitSmallCards = new ArrayList<ICard>();
	theSuitSmallCards.addAll(tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsNoScoreNoAce, suit));
	boolean hasCards = !theSuitSmallCards.isEmpty();
	if (hasCards) {
	    tools.sortNoTrumpSameSuitInHandDesc(theSuitSmallCards);
	    for (ICard iCard : theSuitSmallCards) {
		boolean bigger = iCard.getPoint() > opponentCards.get(0).getPoint();
		if (bigger) {
		    result.add(iCard);
		    break;
		}
	    }
	}
	return result;
    }

    private List<ICard> findSameSuitCardsWithScore(List<ICard> opponentCards, String suit) {
	List<ICard> result = new ArrayList<ICard>();
	boolean hasCards = !this.complex4suitCardsWithScore.isEmpty();
	if (hasCards) {
	    List<ICard> temp = new ArrayList<ICard>();
	    for (ICard iCard : this.complex4suitCardsWithScore) {
		boolean isTheSuit = suit.equals(iCard.getSuit());
		if (isTheSuit) {
		    temp.clear();
		    temp.add(iCard);
		    boolean bigger = GameRule.getInstance().whoIsRoundWinner(opponentCards, temp).equals(robot);
		    if (bigger) {
			result.add(iCard);
			break;
		    }
		}
	    }
	}
	return result;
    }

    /**
     * ����������.
     */
    @Override
    public List<ICard> followCards4(List<ICard> opponentCards) {
	List<ICard> result = new ArrayList<ICard>();
	// �������������||����---->������
	boolean hasCards4 = !this.cards4.isEmpty();
	if (hasCards4) {
	    result.addAll(this.cards4);
	} else {
	    boolean trumpFull = (tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder).size() >= 4);
	    if (trumpFull) {
		// ����С���۳���
		result.addAll(this.minCostTrumpCards(opponentCards.size()));

	    } else {
		// ��������������
		result.addAll(tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder));
		result.addAll(this.minCostNorTrumpCards(opponentCards.size() - result.size()));
	    }

	}

	return result;
    }

    /**
     * �������Ʋ���ʱ���ϵ���ͳɱ��ĸ���
     *
     * @param subSize
     * @return
     */
    private List<ICard> minCostNorTrumpCards(int subSize) {
	List<ICard> result = new ArrayList<ICard>();
	// ������: 1����С�ƣ�2��������
	if (result.size() < subSize) {
	    // �޷���ACE�Ӹ���
	    for (int i = 0; i < this.complex4suitCardsNoScoreNoAce.size(); i++) {
		result.add(this.complex4suitCardsNoScoreNoAce.get(i));
		boolean same = (subSize == result.size());
		if (same) {
		    break;
		}
	    }

	    if (result.size() < subSize) {
		// ���㣬���Ӹ�����Ace����
		for (int i = 0; i < this.complex4suitCardsWithAce.size(); i++) {
		    result.add(this.complex4suitCardsWithAce.get(i));
		    boolean same = (subSize == result.size());
		    if (same) {
			break;
		    }
		}

		if (result.size() < subSize) {
		    // ��û�У�˦�����޷���ACE��
		    for (int i = 0; i < this.allStraightCardsNoScoreNoAce.size(); i++) {
			result.add(this.allStraightCardsNoScoreNoAce.get(i));
			boolean same = (subSize == result.size());
			if (same) {
			    break;
			}
		    }
		    if (result.size() < subSize) {
			// ��û�У�˦����ACE��
			for (int i = 0; i < this.allStraightCardsWithAce.size(); i++) {
			    result.add(this.allStraightCardsWithAce.get(i));
			    boolean same = (subSize == result.size());
			    if (same) {
				break;
			    }
			}

			if (result.size() < subSize) {
			    // ��û�У��Ӹ��з���
			    for (int i = 0; i < this.complex4suitCardsWithScore.size(); i++) {
				result.add(this.complex4suitCardsWithScore.get(i));
				boolean same = (subSize == result.size());
				if (same) {
				    break;
				}
			    }

			    if (result.size() < subSize) {
				// ��û�У�˦���з���
				for (int i = 0; i < this.allStraightCardsWithScore.size(); i++) {
				    result.add(this.allStraightCardsWithScore.get(i));
				    boolean same = (subSize == result.size());
				    if (same) {
					break;
				    }
				}
			    }
			}
		    }
		}
	    }
	}

	return result;
    }

    /**
     * ������ͳɱ�������
     *
     * @param size
     * @return
     */
    private List<ICard> minCostTrumpCards(int size) {
	// �����ɵ͵��ߵĳ����Ʒ�
	List<ICard> result = new ArrayList<ICard>();
	if (result.size() < size) {

	    // �޷ֵ�С��
	    tools.swapAceFromTopToBottomForOneSuitCards(smallTrumpCards);
	    for (int i = 0; i < smallTrumpCards.size(); i++) {
		result.add(smallTrumpCards.get(i));
		boolean same = (size == result.size());
		if (same) {
		    break;
		}

	    }
	    if (result.size() < size) {
		// ��������2
		for (int i = 0; i < point2TrumpCards.size(); i++) {
		    result.add(point2TrumpCards.get(i));
		    boolean same = (size == result.size());
		    if (same) {
			break;
		    }
		}
		if (result.size() < size) {
		    // �������ý�����
		    for (int i = 0; i < classPointTrumpCards.size(); i++) {
			result.add(classPointTrumpCards.get(i));
			boolean same = (size == result.size());
			if (same) {
			    break;
			}
		    }
		    if (result.size() < size) {
			// ��������С��
			result.add(joker2TrumpCards.get(0));
			if (result.size() < size) {
			    // ���������ƾͿ���
			    for (int i = 0; i < this.scoreTrumpCards.size(); i++) {
				result.add(scoreTrumpCards.get(i));
				boolean same = (size == result.size());
				if (same) {
				    break;
				}
			    }
			    if (result.size() < size) {
				// �����������ƾͿ���
				List<ICard> leftTrumpCards = new ArrayList<ICard>();
				leftTrumpCards.addAll(tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder));
				leftTrumpCards.removeAll(this.otherTrumpCards);
				for (int i = 0; i < leftTrumpCards.size(); i++) {
				    result.add(leftTrumpCards.get(i));
				    boolean same = (size == result.size());
				    if (same) {
					break;
				    }
				}
			    }
			}
		    }
		}
	    }
	}

	return result;
    }

    /**
     * ������˦��
     */
    @Override
    public List<ICard> followStraightCards(List<ICard> opponentCards) {
	return giveResult4StraightAndNorTrump(opponentCards);

/*
	List<ICard> result = new ArrayList<ICard>();
	boolean strategy3000 = (robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_ROUND);
	if (!strategy3000) {
	    // ��3000���Եĸ���
	    result.addAll(this.focusOnScore(opponentCards));
	} else {
	    // 3000���Եĸ��ƣ����Ǵ�Ҫ����������
	    result.addAll(this.focusOnWinRound(opponentCards));
	}

	return result;
	*/

    }

    private List<ICard> focusOnWinRound(List<ICard> opponentCards) {
	return giveResult4StraightAndNorTrump(opponentCards);
/*
	// ��������������
	List<ICard> result = new ArrayList<ICard>();
	List<ICard> targetCards = new ArrayList<ICard>();
	// ���Ǳ���ͬ��ɫ,���������;�������ӷ�
	String suit = opponentCards.get(0).getSuit();
	int size = opponentCards.size();// ��

	if (result.size() < size) {
	    // ����ͬ��ɫ�����޷���ACE��
	    boolean argList = this.complex4suitCardsNoScoreNoAce.size() > 0;
	    if (argList) {
		List<ICard> cardsNoScoreNoAce = tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsNoScoreNoAce, suit);
		 targetCards.clear();
		targetCards.addAll(cardsNoScoreNoAce);
		result.addAll(this.pickFollowingCards(size - result.size(), targetCards));
	    }

	    if (result.size() < size) {
		// ����ͬ��ɫС��������(����)
		List<ICard> cardScore = tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithScore, suit);
		Collections.reverse(cardScore);
		 targetCards.clear();
		targetCards.addAll(cardScore);
		result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

		if (result.size() < size) {
		    // ����ͬ��ɫAce��
		    List<ICard> cardAce = tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithAce, suit);
			 targetCards.clear();
			targetCards.addAll(cardAce);
		    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

		    if (result.size() < size) {
			// �����Ӹ������޷֣���ACE��
			 targetCards.clear();
			targetCards.addAll(this.complex4suitCardsNoScoreNoAce);
			targetCards.removeAll(tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsNoScoreNoAce, suit));
			result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

			if (result.size() < size) {
			    // �����Ӹ������з���
			    targetCards.clear();
			    targetCards.addAll(this.complex4suitCardsWithScore);
			    targetCards.removeAll(tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithScore, suit));
			    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

			    if (result.size() < size) {
				// �Ӹ�ACE��
				targetCards.clear();
				targetCards.addAll(this.complex4suitCardsWithAce);
				targetCards.removeAll(tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithAce, suit));
				result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

				if (result.size() < size) {
				    // ˦�����޷֣���ACE��
				    targetCards.clear();
				    targetCards.addAll(this.allStraightCardsNoScoreNoAce);
				    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

				    if (result.size() < size) {
					// ˦����ACE��
					targetCards.clear();
					targetCards.addAll(this.allStraightCardsWithAce);
					result.addAll(this.pickFollowingCards(size - result.size(), targetCards));
					if (result.size() < size) {
					    // ˦���з�����
					    targetCards.clear();
					    targetCards.addAll(allStraightCardsWithScore);
					    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));
					    if (result.size() < size) {
						// �������޷�С��
						targetCards.clear();
						targetCards.addAll(smallTrumpCards);
						result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

						if (result.size() < size) {
						    // ����2
						    targetCards.clear();
						    targetCards.addAll(point2TrumpCards);
						    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

						    if (result.size() < size) {
							// ������
							targetCards.clear();
							targetCards.addAll(classPointTrumpCards);
							result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

							if (result.size() < size) {
							    // С��
							    targetCards.clear();
							    targetCards.addAll(joker2TrumpCards);
							    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));
							    if (result.size() < size) {
								// ���Ʒ�����
								targetCards.clear();
								targetCards.addAll(scoreTrumpCards);
								result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

								if (result.size() < size) {
								    // �ص���
								    targetCards.clear();
								    targetCards.addAll(topCard);
								    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));
									if (result.size() < size) {
									    // ���⸱��
									    targetCards.clear();
									    targetCards.addAll(allNonTrumpCards);
									    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));
        									    if (result.size() < size) {
        									    // ��������
        									    targetCards.clear();
        									    targetCards.addAll(allTrumpCards);
        									    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));
								}
							    }
							}
						    }
						}
					    }
					}
				    }
				}
			    }
			}
			    }
			}
		    }
		}
	    }
	}
	return result;
	*/
    }

    private List<ICard> focusOnScore(List<ICard> opponentCards) {
	return giveResult4StraightAndNorTrump(opponentCards);
    }

    /**
     * �ڹ涨������С���У���ѡ�������.
     * @param result
     * @param size
     * @param targetCards
     */
    private List<ICard> pickFollowingCards(int balance, List<ICard> targetCards,List<ICard> allResult) {

	List<ICard> result = new ArrayList<ICard>();
	boolean hasCards = !targetCards.isEmpty();
	if (hasCards) {
	    for (ICard iCard : targetCards) {
		boolean notInAllResult=!allResult.contains(iCard);
		if (notInAllResult) {
		    result.add(iCard);
		    boolean same = (balance == result.size());
		    if (same) {
			break;
		    }
		}
	    }
	}

	return result;

    }

    /**
     * ����������
     */
    @Override
    public List<ICard> followTrumpCard(List<ICard> opponentCards) {

	systemOutPrintLSubBags("followTrumpCard");

	List<ICard> result = new ArrayList<ICard>();

	// ֻ��һ�ţ����������С�ڶ����Ƶ����������ܲ�����;û�����������޷�С��
	boolean hasTrump = this.allTrumpCards.size()>0||tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder).size()>0;
	//TODO:
	System.out.println("@RobotPowerCardBag followTrumpCard()  allTrumpCards---->"+hasTrump);
	if (hasTrump) {
	    // ������Ϊ
	    if (result.size() == 0) {
		// �޷���
		result.addAll(this.pickCardToFollowingTrump(smallTrumpCards));
		if (result.size() == 0) {
		    // ����2
		    result.addAll(this.pickCardToFollowingTrump(point2TrumpCards));
		    if (result.size() == 0) {
			// ����
			result.addAll(this.pickCardToFollowingTrump(classPointTrumpCards));
			if (result.size() == 0) {
			    // ������
			    result.addAll(this.pickCardToFollowingTrump(scoreTrumpCards));
				if (result.size() == 0) {
				    // ȫ������
				    result.addAll(this.pickCardToFollowingTrump(allTrumpCards));
				    if (result.size() == 0) {
					// ȫ������_����
					result.addAll(this.pickCardToFollowingTrump(tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder)));
				    }
				}
			}
		    }
		}
	    }
	} else {
		//TODO:
		System.out.println("@RobotPowerCardBag followTrumpCard()  in else --NoTrumpCard piece----");
	    // ������Ϊ
	    if (result.size() == 0) {
		// �Ӹ����޷���ACE
		result.addAll(this.pickNorTrumpCardToFollowTrump(complex4suitCardsNoScoreNoAce));
		if (result.size() == 0) {
		    // ˦�����޷���ACE
		    result.addAll(this.pickNorTrumpCardToFollowTrump(allStraightCardsNoScoreNoAce));
		    if (result.size() == 0) {
			// �Ӹ���ACE
			result.addAll(this.pickNorTrumpCardToFollowTrump(complex4suitCardsWithAce));
			if (result.size() == 0) {
			    // ˦��ACE
			    result.addAll(this.pickNorTrumpCardToFollowTrump(allStraightCardsWithAce));
			    if (result.size() == 0) {
				// �Ӹ��Ʒ���
				result.addAll(this.pickNorTrumpCardToFollowTrump(complex4suitCardsWithScore));
				if (result.size() == 0) {
				    // ˦�Ʒ���
				    result.addAll(this.pickNorTrumpCardToFollowTrump(allStraightCardsWithScore));
				    if (result.size() == 0) {
					// ���⸱��
					 result.addAll(this.pickNorTrumpCardToFollowTrump(allNonTrumpCards));
					 if (result.size() == 0) {
					     // ������1
					     result.addAll(this.pickNorTrumpCardToFollowTrump(robot.actions().getSelfCardDeck()));

					 }
				    }
				}
			    }
			}
		    }
		}
	    }
	}
	//TODO:
	System.out.println("@RobotPowerCardBag followTrumpCard()  result---->"+result.size());
	return result;
    }

    private void systemOutPrintLSubBags(String location) {

	/*
	 * private List<ICard> straightHeartCards = new ArrayList<ICard>();//
	 * ----����˦�� private List<ICard> straightClubCards = new
	 * ArrayList<ICard>();// ----�ݻ�˦�� private List<ICard> straightSpadeCards
	 * = new ArrayList<ICard>();// ----����˦�� private List<ICard>
	 * straightDiamondCards = new ArrayList<ICard>();// ----����˦�� private
	 * List<ICard> allStraightCards = new ArrayList<ICard>();// ----����˦��
	 * private List<ICard> allStraightCardsWithScore = new
	 * ArrayList<ICard>();// ----����˦���з��� private List<ICard>
	 * allStraightCardsWithAce = new ArrayList<ICard>();// ----����˦����ACE
	 * private List<ICard> allStraightCardsNoScore = new
	 * ArrayList<ICard>();// ----����ȥ�������˦�� private List<ICard>
	 * allStraightCardsNoScoreNoAce = new ArrayList<ICard>();//
	 * ----����ȥ����ȥACE���˦�� private List<ICard> heartCards = new
	 * ArrayList<ICard>();// ----������������ private List<ICard> clubCards = new
	 * ArrayList<ICard>();// ----�ݻ��������� private List<ICard> spadeCards = new
	 * ArrayList<ICard>();// ----������������ private List<ICard> diamondCards =
	 * new ArrayList<ICard>();// ----������������ private List<ICard>
	 * complex4suitCards = new ArrayList<ICard>();//
	 * ----ʣ�¸��Ƶĺϼ���ʽ---������Ԫ��ԭ���������� private List<ICard>
	 * complex4suitCardsWithScore = new ArrayList<ICard>();// ----ʣ�¸����зֵ���
	 * private List<ICard> complex4suitCardsWithAce = new
	 * ArrayList<ICard>();// ----ʣ�¸�����ACE���� private List<ICard>
	 * complex4suitCardsNoScore = new ArrayList<ICard>();// ----ʣ�¸��Ƶ��޷ֺϼ�
	 * private List<ICard> complex4suitCardsNoScoreNoAce = new
	 * ArrayList<ICard>();// ----ʣ�¸��Ƶ��޷��ƣ���Ace�ϼ� private List<ICard>
	 * comlexScoreCards_otherCardsSuit = new ArrayList<ICard>();//
	 * ----ʣ�¸��Ƶĺϼ��е���ɫ--�з����зǷ���
	 */

	for (ICard iCard : cards4) {
	    System.out.println("@RobotPowerCardsBag " + location + " cards4-->" + iCard.getSuit() + iCard.getPoint());
	}
	for (ICard iCard : smallTrumpCards) {
	    System.out.println("@RobotPowerCardsBag " + location + " smallTrumpCards-->" + iCard.getSuit() + iCard.getPoint());
	}
	for (ICard iCard : point2TrumpCards) {
	    System.out.println("@RobotPowerCardsBag " + location + " point2TrumpCards-->" + iCard.getSuit() + iCard.getPoint());
	}
	for (ICard iCard : classPointTrumpCards) {
	    System.out.println("@RobotPowerCardsBag " + location + " classPointTrumpCards-->" + iCard.getSuit() + iCard.getPoint());
	}

	for (ICard iCard : scoreTrumpCards) {
	    System.out.println("@RobotPowerCardsBag " + location + " scoreTrumpCards-->" + iCard.getSuit() + iCard.getPoint());
	}
	for (ICard iCard : joker2TrumpCards) {
	    System.out.println("@RobotPowerCardsBag " + location + " joker2TrumpCards-->" + iCard.getSuit() + iCard.getPoint());
	}
	for (ICard iCard : topCard) {
	    System.out.println("@RobotPowerCardsBag " + location + " topCard-->" + iCard.getSuit() + iCard.getPoint());
	}

	for (ICard iCard : allTrumpCards) {
	    System.out.println("@RobotPowerCardsBag " + location + " allTrumpCards-->" + iCard.getSuit() + iCard.getPoint());
	}
	for (ICard iCard : allNonTrumpCards) {
	    System.out.println("@RobotPowerCardsBag " + location + " allNonTrumpCards-->" + iCard.getSuit() + iCard.getPoint());
	}
    }

    /**
     * ��������ѡ���ƣ�����ʱ��
     *
     * @param result
     * @param hasCards
     * @param cardsForPicking
     */
    private List<ICard> pickNorTrumpCardToFollowTrump(List<ICard> cardsForPicking) {
	List<ICard> result = new ArrayList<ICard>();
	boolean hasCards=cardsForPicking.size()>0;
	if (hasCards) {
	    //Collections.sort(cardsForPicking);
	    result.add(cardsForPicking.get(0));
	}
	return result;
    }

    /**
     * ��������ѡ����
     *
     * @param opponentCards
     * @param result
     * @param hasCards
     * @param cardsForPicking
     */
    private List<ICard> pickCardToFollowingTrump(List<ICard> cardsForPicking) {
	List<ICard> result = new ArrayList<ICard>();
	boolean hasCards=cardsForPicking.size()>0;
	if (hasCards) {
	    // ��һ����С����
	    tools.swapAceFromTopToBottomForOneSuitCards(cardsForPicking);
	    result.add(cardsForPicking.get(0));
	}
/*
	if (hasCards) {
	    for (ICard iCard : cardsForPicking) {
		List<ICard> temp = new ArrayList<ICard>();
		temp.add(iCard);
		boolean smaller = !GameRule.getInstance().whoIsRoundWinner(opponentCards, temp).equals(robot);
		if (smaller) {
		    result.add(iCard);
		    break;
		}
	    }
	    if (result.size() == 0) {
		// ���û��С�ڶ��ֵ��ƣ�ֻ����һ����С���Ƴ�--�����ﵽ���Ƶ�Ч��
		tools.swapAceFromTopToBottomForOneSuitCards(cardsForPicking);
		result.add(cardsForPicking.get(0));
	    }
	}
	*/



	return result;
    }

    /**
     * �����渱��
     */
    @Override
    public List<ICard> followNorTrumpCard(List<ICard> opponentCards) {

	return giveResult4StraightAndNorTrump(opponentCards);

/*
	List<ICard> result = new ArrayList<ICard>();
	String suit = opponentCards.get(0).getSuit();

	List<ICard> sameSuitCards = new ArrayList<ICard>();
	int size = opponentCards.size();

	//ͬ��ɫ���������֣��޷֣��з֣��Ⱥ�ֱ��С������������ٴ��������Ƶ�ɢ���г�����

	sameSuitCards.addAll(this.tools.seperateSameSuitCardsSortedDesc(this.allNonTrumpCards, suit));
	Collections.reverse(sameSuitCards);// �ĳ�����
	boolean hasSameSuit = !sameSuitCards.isEmpty();// ��ͬ��ɫ

	if (hasSameSuit) {
	    // ͬ��ɫ
	    if (result.size() < size) {
		// ͬ���Ƿ���
		for (ICard iCard : sameSuitCards) {
		    List<ICard> temp = new ArrayList<ICard>();
		    temp.add(iCard);
		    boolean score5 = iCard.getPoint() == 5;
		    boolean score10 = iCard.getPoint() == 10;
		    boolean score13 = iCard.getPoint() == 13;
		    boolean notAll = !score5 && !score10 && !score13;
		    boolean smaller = !GameRule.getInstance().whoIsRoundWinner(opponentCards, temp).equals(robot);
		    if (notAll && smaller) {
			result.add(iCard);
			break;
		    }
		}
		if (result.size() < size) {
		    // ͬ������
		    for (ICard iCard : sameSuitCards) {
			List<ICard> temp = new ArrayList<ICard>();
			temp.add(iCard);
			boolean score5 = iCard.getPoint() == IGameRule.CARD_5_EQUAL_SCORE;
			boolean score10 = iCard.getPoint() == IGameRule.CARD_10_EQUAL_SCORE;
			boolean score13 = iCard.getPoint() == 13;
			boolean anyOne = score5 || score10 || score13;
			boolean smaller = !GameRule.getInstance().whoIsRoundWinner(opponentCards, temp).equals(robot);
			if (anyOne && smaller) {
			    result.add(iCard);
			    break;
			}
		    }
		}
	    }
	} else {
	    // ��ͬ��ɫ
	    if (result.size() < size) {
		// ���ƷǷַ�Ace
		boolean hasCards = !this.complex4suitCardsNoScoreNoAce.isEmpty();
		result.addAll(this.pickNotSameSuitTofollowNorTrumpCard(opponentCards, hasCards, this.complex4suitCardsNoScoreNoAce));
		if (result.size() < size) {
		    // ����Ace
		    boolean hasCards0 = !this.complex4suitCardsWithAce.isEmpty();
		    result.addAll(this.pickNotSameSuitTofollowNorTrumpCard(opponentCards, hasCards0, this.complex4suitCardsWithAce));
		    if (result.size() < size) {
			// ˦�ƷǷַ�ACE
			boolean hasCards1 = !this.allStraightCardsNoScoreNoAce.isEmpty();
			result.addAll(this.pickNotSameSuitTofollowNorTrumpCard(opponentCards, hasCards1, this.allStraightCardsNoScoreNoAce));
			if (result.size() < size) {
			    // ˦��ACE
			    boolean hasCards2 = !this.allStraightCardsWithAce.isEmpty();
			    result.addAll(this.pickNotSameSuitTofollowNorTrumpCard(opponentCards, hasCards2, this.allStraightCardsWithAce));
			    if (result.size() < size) {
				// ���Ʒ�
				boolean hasCards3 = !this.complex4suitCardsWithScore.isEmpty();
				result.addAll(this.pickNotSameSuitTofollowNorTrumpCard(opponentCards, hasCards3, this.complex4suitCardsWithScore));
				if (result.size() < size) {
				    // ���⸱��
				    boolean hasCards4 = !this.allNonTrumpCards.isEmpty();
				    result.addAll(this.pickNotSameSuitTofollowNorTrumpCard(opponentCards, hasCards4, this.allNonTrumpCards));
				    if (result.size() < size) {
					// ��������
					boolean hasCards5= !this.allTrumpCards.isEmpty();
					result.addAll(this.pickNotSameSuitTofollowNorTrumpCard(opponentCards, hasCards5, this.allTrumpCards));
					}
				}
			    }
			}
		    }
		}
	    }
	}

	return result;
*/
    }
/**
 * �����Ÿ��ƺ͸�˦����һ���ģ�����ֻ������
 * @param opponentCards
 * @return
 */
    private List<ICard> giveResult4StraightAndNorTrump(List<ICard> opponentCards) {
	List<ICard> result = new ArrayList<ICard>();
	// ���Ǳ���ͬ��ɫ,���������;�������ӷ�
	String suit = opponentCards.get(0).getSuit();
	int size = opponentCards.size();// �����
	List<ICard> targetCards = new ArrayList<ICard>();

	if (result.size() < size) {
	    // ����ͬ��ɫ�����޷���ACE��
	    System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump() this.complex4suitCardsNoScoreNoAce-->"+this.complex4suitCardsNoScoreNoAce.size());
	    List<ICard> cardsNoScoreNoAce = tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsNoScoreNoAce, suit);
	    targetCards.clear();
	    targetCards.addAll(cardsNoScoreNoAce);
	    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
		//TODO:
		System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  cardsNoScoreNoAce.isEmpty---->"+targetCards.isEmpty());
		System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());


	    if (result.size() < size) {
		// ����ͬ��ɫAce��
		List<ICard> cardAce = tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithAce, suit);
		 targetCards.clear();
		 targetCards.addAll(cardAce);
		result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
		//TODO:
		System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  cardAce.isEmpty---->"+targetCards.isEmpty());
		System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
		if (result.size() < size) {
		    // ����ͬ��ɫС��������(����)
		    List<ICard> cardScore = tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithScore, suit);
		    targetCards.clear();
		    targetCards.addAll(cardScore);
		    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
			//TODO:
			System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  cardScore.isEmpty---->"+targetCards.isEmpty());
			System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
		    if (result.size() < size) {
			// �Ӹ����������޷֣���ACE��
			targetCards.clear();
			targetCards.addAll(this.complex4suitCardsNoScoreNoAce);
			targetCards.removeAll(tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsNoScoreNoAce, suit));
			result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
			//TODO:
			System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  �Ӹ����������޷֣���ACE��.isEmpty---->"+targetCards.isEmpty());
			System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
			if (result.size() < size) {
			    // �Ӹ�ACE��
			    targetCards.clear();
			    targetCards.addAll(this.complex4suitCardsWithAce);
			    targetCards.removeAll(tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithAce, suit));
			    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
				//TODO:
				System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  �Ӹ�ACE��.isEmpty---->"+targetCards.isEmpty());
				System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
			    if (result.size() < size) {
				// ˦�����޷֣���ACE��
				targetCards.clear();
				targetCards.addAll(this.allStraightCardsNoScoreNoAce);
				result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
				//TODO:
				System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  ˦�����޷֣���ACE��.isEmpty---->"+targetCards.isEmpty());
				System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
				if (result.size() < size) {
				    // ˦����ACE��
				    targetCards.clear();
				    targetCards.addAll(this.allStraightCardsWithAce);
				    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
					//TODO:
					System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  ˦����ACE��.isEmpty---->"+targetCards.isEmpty());
					System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
				    if (result.size() < size) {
					// �������޷�С��
					targetCards.clear();
					targetCards.addAll(smallTrumpCards);
					result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
					//TODO:
					System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  �������޷�С��.isEmpty---->"+targetCards.isEmpty());
					System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
					if (result.size() < size) {
					    // ����2
					    targetCards.clear();
					    targetCards.addAll(point2TrumpCards);
					    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
						//TODO:
						System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  ����2.isEmpty---->"+targetCards.isEmpty());
						System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
					    if (result.size() < size) {
						// ������
						targetCards.clear();
						targetCards.addAll(classPointTrumpCards);
						result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
						//TODO:
						System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  ������.isEmpty---->"+targetCards.isEmpty());
						System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
						if (result.size() < size) {
						    // С��
						    targetCards.clear();
						    targetCards.addAll(joker2TrumpCards);
						    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
							//TODO:
							System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  С��.isEmpty---->"+targetCards.isEmpty());
							System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
						    if (result.size() < size) {
							// �����Ӹ������з���
							targetCards.clear();
							targetCards.addAll(this.complex4suitCardsWithScore);
							targetCards.removeAll(tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithScore, suit));
							result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
							//TODO:
							System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  �����Ӹ������з���.isEmpty---->"+targetCards.isEmpty());
							System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());

							if (result.size() < size) {
							    // ˦���з�����
							    targetCards.clear();
							    targetCards.addAll(allStraightCardsWithScore);
							    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
								//TODO:
								System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  ˦���з�����.isEmpty---->"+targetCards.isEmpty());
								System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
							    if (result.size() < size) {
								// ���Ʒ�����
								targetCards.clear();
								targetCards.addAll(scoreTrumpCards);
								result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
								//TODO:
								System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump() ���Ʒ�����.isEmpty---->"+targetCards.isEmpty());
								System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
								if (result.size() < size) {
								    // �ص���
								    targetCards.clear();
								    targetCards.addAll(topCard);
								    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
									//TODO:
									System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump() �ص���.isEmpty---->"+targetCards.isEmpty());
									System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
								    if (result.size() < size) {
									// ���⸱��
									targetCards.clear();
									targetCards.addAll(allNonTrumpCards);
									result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
									//TODO:
									System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump() ���⸱��.isEmpty---->"+targetCards.isEmpty());
									System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
									if (result.size() < size) {
									    // ��������
									    targetCards.clear();
									    targetCards.addAll(allTrumpCards);
									    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
										//TODO:
										System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump() ��������.isEmpty---->"+targetCards.isEmpty());
										System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());

										if (result.size() < size) {
										    // ������
										    targetCards.clear();
										    targetCards.addAll(robot.actions().getSelfCardDeck());
										    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
										    //TODO:
										    System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump() ������.isEmpty---->"+targetCards.isEmpty());
										    System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
										}
									    }
									}
								}
							    }
							}
						    }
						}
					    }
					}
				    }
				}
			    }
			}
		    }
		}
	    }
	}
	return result;
    }

    /**
     * �����渱��ѡ��ͬ��ɫ��
     *
     * @param opponentCards
     * @param result
     * @param hasCards
     * @param cardsForPicking
     */
    private List<ICard> pickNotSameSuitTofollowNorTrumpCard(List<ICard> opponentCards, boolean hasCards, List<ICard> cardsForPicking) {
	List<ICard> result = new ArrayList<ICard>();
	if (hasCards) {
	    for (ICard iCard : cardsForPicking) {
		List<ICard> temp = new ArrayList<ICard>();
		temp.add(iCard);
		boolean smaller = !GameRule.getInstance().whoIsRoundWinner(opponentCards, temp).equals(robot);
		if (smaller) {
		    result.add(iCard);
		    break;
		}
	    }
	}
	return result;
    }

    public List<ICard> getComplex4suitCardsNoScore() {
	return complex4suitCardsNoScore;
    }

    public List<ICard> getComplex4suitCardsNoScoreNoAce() {
	return complex4suitCardsNoScoreNoAce;
    }

    public List<ICard> getAllStraightCards() {
	return allStraightCards;
    }

    public List<ICard> getAllStraightCardsNoScore() {
	return allStraightCardsNoScore;
    }

    public List<ICard> getAllStraightCardsNoScoreNoAce() {
	return allStraightCardsNoScoreNoAce;
    }

    public List<ICard> getComplex4suitCardsWithScore() {
	return complex4suitCardsWithScore;
    }

    public List<ICard> getComplex4suitCardsWithAce() {
	return complex4suitCardsWithAce;
    }

    public List<ICard> getAllStraightCardsWithScore() {
	return allStraightCardsWithScore;
    }

    public List<ICard> getAllStraightCardsWithAce() {
	return allStraightCardsWithAce;
    }

    @Override
    public List<ICard> getAllTrumpCards() {
	return allTrumpCards;
    }

    @Override
    public List<ICard> getAllNonTrumpCards() {
	return allNonTrumpCards;
    }

    @Override
    public List<ICard> smallerCardsVsOpponentInSameKind(Map<String, Integer> opponentInfo, List<ICard> opponentCards) {
	List<ICard> result = new ArrayList<ICard>();
	// �������������
	Integer type = opponentInfo.get("type");
	// �����ƻ�ɫ--����ʱ������
	String suit = opponentCards.get(0).getSuit();
	// ���Ƶļ���������ģʽ
	List<ICard> temp = new ArrayList<ICard>();
	// ��----1��˦----2����----3����----4
	switch (type) {
	case 1:
	    boolean has4 = !this.cards4.isEmpty();
	    if (has4) {
		boolean smaller = !GameRule.getInstance().whoIsRoundWinner(opponentCards, this.cards4).equals(robot);
		if (smaller) {
		    result.addAll(this.cards4);
		}
	    }
	    break;
	case 2:
	    for (ICard iCard : allNonTrumpCards) {
		boolean smaller = iCard.getSuit().equals(suit);
		if (smaller) {
		    result.add(iCard);
		}
	    }
	    break;
	case 3:
	    boolean hasTrump = !this.allTrumpCards.isEmpty();
	    if (hasTrump) {
		for (ICard iCard : allTrumpCards) {
		    temp.clear();
		    temp.add(iCard);
		    boolean smaller = !GameRule.getInstance().whoIsRoundWinner(opponentCards, temp).equals(robot);
		    if (smaller) {
			result.add(iCard);
		    }
		}
	    }

	    break;
	case 4:
	    boolean hasNorTrump = !this.allNonTrumpCards.isEmpty();
	    if (hasNorTrump) {
		List<ICard> theSuitCard = tools.seperateNoTrumpSameSuitCards(allNonTrumpCards, suit);

		boolean hasTheSuit = (theSuitCard != null);
		if (hasTheSuit) {
		    tools.swapAceFromTopToBottomForOneSuitCards(theSuitCard);

		    for (ICard iCard : theSuitCard) {
			temp.clear();
			temp.add(iCard);
			boolean smaller = !GameRule.getInstance().whoIsRoundWinner(opponentCards, temp).equals(robot);
			if (smaller) {
			    result.add(iCard);
			}
		    }
		}
	    }
	    break;

	default:
	    break;
	}

	return result;
    }

}
