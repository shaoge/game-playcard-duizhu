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


    // 能量牌套集：有威力的组合或单牌 ----翁；守底大牌；其它主；副 ：甩牌（Ace以下连牌），杂副

    private List<ICard> cards4 = new ArrayList<ICard>();// ----翁
    private List<ICard> topCard = new ArrayList<ICard>();// ----最大守底单牌

    private List<ICard> allTrumpCards = new ArrayList<ICard>();// ----所有主牌
    private List<ICard> allNonTrumpCards = new ArrayList<ICard>();// ----所有副牌

    private List<ICard> otherTrumpCards = new ArrayList<ICard>();// ----其它主牌（不含翁中主和最大守底牌的其它主牌）
    private List<ICard> joker2TrumpCards = new ArrayList<ICard>();// ----小王主牌
    private List<ICard> classPointTrumpCards = new ArrayList<ICard>();// ----将点主牌
    private List<ICard> point2TrumpCards = new ArrayList<ICard>();// ----2主牌
    private List<ICard> scoreTrumpCards = new ArrayList<ICard>();// ----分数主牌
    private List<ICard> smallTrumpCards = new ArrayList<ICard>();// ----去掉各种分类后的主牌

    private List<ICard> straightHeartCards = new ArrayList<ICard>();// ----红心甩牌
    private List<ICard> straightClubCards = new ArrayList<ICard>();// ----草花甩牌
    private List<ICard> straightSpadeCards = new ArrayList<ICard>();// ----黑桃甩牌
    private List<ICard> straightDiamondCards = new ArrayList<ICard>();// ----方块甩牌
    private List<ICard> allStraightCards = new ArrayList<ICard>();// ----所有甩牌
    private List<ICard> allStraightCardsWithScore = new ArrayList<ICard>();// ----所有甩牌中分牌
    private List<ICard> allStraightCardsWithAce = new ArrayList<ICard>();// ----所有甩牌中ACE
    private List<ICard> allStraightCardsNoScore = new ArrayList<ICard>();// ----所有去分数后的甩牌
    private List<ICard> allStraightCardsNoScoreNoAce = new ArrayList<ICard>();// ----所有去分数去ACE后的甩牌
    private List<ICard> heartCards = new ArrayList<ICard>();// ----红心其它副牌
    private List<ICard> clubCards = new ArrayList<ICard>();// ----草花其它副牌
    private List<ICard> spadeCards = new ArrayList<ICard>();// ----黑桃其它副牌
    private List<ICard> diamondCards = new ArrayList<ICard>();// ----方块其它副牌
    private List<ICard> complex4suitCards = new ArrayList<ICard>();// ----剩下副牌的合集形式---不对子元素原集合做减法
    private List<ICard> complex4suitCardsWithScore = new ArrayList<ICard>();// ----剩下副牌有分的牌
    private List<ICard> complex4suitCardsWithAce = new ArrayList<ICard>();// ----剩下副牌有ACE的牌
    private List<ICard> complex4suitCardsNoScore = new ArrayList<ICard>();// ----剩下副牌的无分合集
    private List<ICard> complex4suitCardsNoScoreNoAce = new ArrayList<ICard>();// ----剩下副牌的无分牌，无Ace合集
    private List<ICard> comlexScoreCards_otherCardsSuit = new ArrayList<ICard>();// ----剩下副牌的合集中单花色--有分又有非分牌

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
     * 将手中牌整理出有力量的组合来.
     */
    public void makeUpPowerCardsBags() {
	// 先集体清空容器：
	this.clearAllContainers();
	
	//TODO:
	Log.v("whoIsRoundWinner", "@RobotPowerCardsBag Robot getSelfCardDeck().size() "+RobotPlayer.getInstance(AIPlay.getInstance()).actions().getSelfCardDeck().size());
	Log.v("whoIsRoundWinner", "@RobotPowerCardsBag Robot Human getSelfCardDeck().size() "+HumanPlayer.getInstance(GeneralPlay.getInstance()).actions().getSelfCardDeck().size());
	// 获得手中--变化--的牌
	List<ICard> myCardsInHand = new ArrayList<ICard>();
	myCardsInHand.addAll(data4Robot.getRobotHoldingCards());//数据强制约束正确
	// 有没有牌？
	boolean yesMyCardsInHand = !myCardsInHand.isEmpty();
	// 主副分开
	if (yesMyCardsInHand) {
	    this.allTrumpCards.addAll(tools.seperateTrumpsFromCards(myCardsInHand, recorder));
	    this.allNonTrumpCards.addAll(this.seperateAllNonTrumpCards(myCardsInHand));
	}
	// 先分离---翁牌
	if (yesMyCardsInHand) {
	    this.seprateCards4(myCardsInHand);
	}
	// 如果还有牌
	// 分离---最大守底牌
	if (yesMyCardsInHand) {
	    this.seprateTopCard(myCardsInHand);
	}
	// 如果还有牌
	// 分离---主牌和副牌
	if (yesMyCardsInHand) {
	    // 其它主牌
	    otherTrumpCards.addAll(tools.seperateTrumpsFromCards(myCardsInHand, recorder));
	    myCardsInHand.removeAll(otherTrumpCards);// 减主牌
	}
	// 如果还有牌
	// 分离--各花色副牌和甩牌(myCardsInHand的差额部分)
	if (yesMyCardsInHand) {
	    // 甩牌和各花色副牌
	    this.seprateVarySuitsAsStraightCards(myCardsInHand);
	    this.allStraightCards.addAll(this.seperateAllStraightCards());// 全部甩牌
	    this.allStraightCardsNoScore.addAll(this.seperateAllStraightCardsNoScore());// 甩牌去分
	    this.allStraightCardsNoScoreNoAce.addAll(this.seperateAllStraightCardsNoScoreNoAce());// 甩牌去分去ACE
	    this.allStraightCardsWithScore.addAll(this.seperateAllStraightCardsWithScore());// 甩牌中分牌
	    this.allStraightCardsWithAce.addAll(this.seperateAllStraightCardsWithAce());// 甩牌中ACE牌

	    // 剩杂副牌合集
	    this.seprateOddNorTrumpsCards();
	    boolean hasComplex = !complex4suitCards.isEmpty();
	    // 分出有分有杂，且分最多的副牌
	    if (hasComplex) {
		this.complex4suitCardsNoScore.addAll(this.seperateComplex4suitCardsNoScore(complex4suitCards));// 杂副无分牌
		this.complex4suitCardsNoScoreNoAce.addAll(this.seperateComplex4suitCardsNoScoreNoAce(complex4suitCards));// 杂副无分无ACE牌
		this.complex4suitCardsWithScore.addAll(this.seperateComplex4suitCardsWithScore());// 杂副中分牌
		this.complex4suitCardsWithAce.addAll(this.seperateComplex4suitCardsWithAce());// 杂副中ACE牌

		this.seperateComlexScoreCards_otherCardsSuit(complex4suitCards);
	    }
	}
	// 如果有其它主牌，再分类（镜像式取牌）：小王，将点主，主2,分主
	boolean yesOtherTrumpCards = !otherTrumpCards.isEmpty();
	if (yesOtherTrumpCards) {
	    this.seprateDiffTrumpCards();
	}

	// 给一些有次序关系的牌做升序排列
	this.sortSomeBags();
    }

    private void sortSomeBags() {
	// 因为实现Comparable接口直接实现默认方法，即升序
	Collections.sort(cards4);// ----翁

	Collections.sort(allNonTrumpCards);// ----所有副牌

	Collections.sort(scoreTrumpCards);// ----分数主牌
	Collections.sort(smallTrumpCards);// --------去掉各种分类后的主牌
	Collections.sort(straightHeartCards);// ----红心甩牌
	Collections.sort(straightClubCards);// ----草花甩牌
	Collections.sort(straightSpadeCards);// ----黑桃甩牌
	Collections.sort(straightDiamondCards);// ----方块甩牌
	Collections.sort(allStraightCards);// ----所有甩牌
	Collections.sort(allStraightCardsWithScore);// ----所有甩牌中分牌
	Collections.sort(allStraightCardsWithAce);// ----所有甩牌中ACE
	Collections.sort(allStraightCardsNoScore);// ----所有去分数后的甩牌
	Collections.sort(allStraightCardsNoScoreNoAce);// ----所有去分数去ACE后的甩牌
	Collections.sort(heartCards);// ----红心其它副牌
	Collections.sort(clubCards);// ----草花其它副牌
	Collections.sort(spadeCards);// ----黑桃其它副牌
	Collections.sort(diamondCards);// ----方块其它副牌
	Collections.sort(complex4suitCards);// ----剩下副牌的合集形式---不对子元素原集合做减法
	Collections.sort(complex4suitCardsWithScore);// ----剩下副牌有分的牌
	Collections.sort(complex4suitCardsWithAce);// ----剩下副牌有ACE的牌
	Collections.sort(complex4suitCardsNoScore);// ----剩下副牌的无分合集
	Collections.sort(complex4suitCardsNoScoreNoAce);// ----剩下副牌的无分牌，无Ace合集
	Collections.sort(comlexScoreCards_otherCardsSuit);// ----剩下副牌的合集中单花色--有分又有非分牌

    }

    private List<ICard> seperateAllNonTrumpCards(List<ICard> myCardsInHand) {
	List<ICard> result = new ArrayList<ICard>();
	// 全牌减主牌
	result.addAll(myCardsInHand);
	result.removeAll(tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder));
	return result;
    }

    /**
     * 杂副中ACE牌
     *
     * @return
     */
    private List<ICard> seperateComplex4suitCardsWithAce() {
	List<ICard> result = new ArrayList<ICard>();
	// 牌中ACE
	for (ICard iCard : this.complex4suitCards) {
	    boolean ace = iCard.getPoint() == 1;
	    if (ace) {
		result.add(iCard);
	    }
	}
	return result;
    }

    /**
     * 杂副中分牌
     *
     * @return
     */
    private List<ICard> seperateComplex4suitCardsWithScore() {
	List<ICard> result = new ArrayList<ICard>();
	// 分牌
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
     * 甩牌中ACE牌
     *
     * @return
     */
    private List<ICard> seperateAllStraightCardsWithAce() {
	List<ICard> result = new ArrayList<ICard>();
	// 牌中ACE
	for (ICard iCard : this.allStraightCards) {
	    boolean ace = iCard.getPoint() == 1;
	    if (ace) {
		result.add(iCard);
	    }
	}
	return result;
    }

    /**
     * 甩牌中分牌
     *
     * @return
     */
    private List<ICard> seperateAllStraightCardsWithScore() {
	List<ICard> result = new ArrayList<ICard>();
	// 分牌
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
	// 排除分牌中ACE
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
	// 排除分牌
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
	// 全部甩牌相加
	result.addAll(this.straightClubCards);
	result.addAll(this.straightDiamondCards);
	result.addAll(this.straightHeartCards);
	result.addAll(this.straightSpadeCards);
	return result;
    }

    /**
     * 分离无分无Ace副牌
     *
     * @param complex4suitCards2
     * @return
     */
    private List<ICard> seperateComplex4suitCardsNoScoreNoAce(List<ICard> complex4suitCards) {
	List<ICard> result = new ArrayList<ICard>();
	List<ICard> tempList = new ArrayList<ICard>();
	tempList.addAll(this.seperateComplex4suitCardsNoScore(complex4suitCards));// 去完分数了

	for (ICard iCard : tempList) {
	    boolean ace = (iCard.getPoint() == 1);// 判断ACE
	    if (!ace) {
		result.add(iCard);
	    }
	}
	return result;
    }

    /**
     * 分离无分副牌
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

	// 有分有杂，分最多的副牌
	// 分色
	List<ICard> hearts = tools.seperateSameSuitCardsSortedDesc(complex4suitCards, "heart");
	List<ICard> clubs = tools.seperateSameSuitCardsSortedDesc(complex4suitCards, "club");
	List<ICard> spades = tools.seperateSameSuitCardsSortedDesc(complex4suitCards, "spade");
	List<ICard> diamonds = tools.seperateSameSuitCardsSortedDesc(complex4suitCards, "diamond");

	// 全张数-分牌张数
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
	    // 没分的返回0
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
	complex4suitCards.addAll(heartCards);// 最后剩下的牌
	complex4suitCards.addAll(clubCards);// 最后剩下的牌
	complex4suitCards.addAll(spadeCards);// 最后剩下的牌
	complex4suitCards.addAll(diamondCards);// 最后剩下的牌

    }

    private void seprateStraightDiamond(List<ICard> myCardsInHand) {
	diamondCards.addAll(tools.seperateSameSuitCardsSortedDesc(myCardsInHand, "diamond"));
	// ---甩牌

	straightDiamondCards.addAll(tools.seperateStraightCards(diamondCards));
	// 其它方块副牌
	diamondCards.removeAll(straightDiamondCards);
    }

    private void seprateStraightSpade(List<ICard> myCardsInHand) {
	spadeCards.addAll(tools.seperateSameSuitCardsSortedDesc(myCardsInHand, "spade"));
	// ---甩牌

	straightSpadeCards.addAll(tools.seperateStraightCards(spadeCards));
	// 其它黑桃副牌
	spadeCards.removeAll(straightSpadeCards);
    }

    private void seprateStraightClub(List<ICard> myCardsInHand) {
	clubCards.addAll(tools.seperateSameSuitCardsSortedDesc(myCardsInHand, "club"));
	// ---甩牌

	straightClubCards.addAll(tools.seperateStraightCards(clubCards));
	// 其它草花副牌
	clubCards.removeAll(straightClubCards);
    }

    private void seprateStraightHeart(List<ICard> myCardsInHand) {
	heartCards.addAll(tools.seperateSameSuitCardsSortedDesc(myCardsInHand, "heart"));
	// ---甩牌
	straightHeartCards.addAll(tools.seperateStraightCards(heartCards));
	// 其它红心副牌
	heartCards.removeAll(straightHeartCards);
    }

    private void seprateDiffTrumpCards() {
	for (ICard iCard : otherTrumpCards) {
	    // ----小王主牌
	    boolean joker2 = (iCard.getSuit().equals("joker") && iCard.getPoint() == 2);
	    // ----将点主牌
	    boolean classPoint = (iCard.getPoint() == recorder.getCurrentClassPoint());
	    // ----2主牌
	    boolean point2 = (!iCard.getSuit().equals("joker") && iCard.getPoint() == 2);
	    // ----分数 主牌
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
	// 再无分类的主
	this.smallTrumpCards.addAll(this.otherTrumpCards);
	this.smallTrumpCards.removeAll(this.joker2TrumpCards);
	this.smallTrumpCards.removeAll(this.classPointTrumpCards);
	this.smallTrumpCards.removeAll(this.point2TrumpCards);
	this.smallTrumpCards.removeAll(this.scoreTrumpCards);
    }

    private void seprateTopCard(List<ICard> myCardsInHand) {
	topCard.clear();
	topCard.addAll(tools.confirmTopCard(myCardsInHand));

	myCardsInHand.removeAll(topCard);// 减最大守底牌
    }

    private void seprateCards4(List<ICard> myCardsInHand) {
	cards4.clear();

	cards4.addAll(tools.seperate4SamePointCards(myCardsInHand));
	myCardsInHand.removeAll(cards4);// 减翁牌
    }

    private void clearAllContainers() {
	cards4.clear();// ----翁
	topCard.clear();// ----最大守底单牌

	allTrumpCards.clear();// ----所有主牌
	allNonTrumpCards.clear();// ----所有副牌

	otherTrumpCards.clear();// ----其它主牌（不含翁中主和最大守底牌的其它主牌）
	joker2TrumpCards.clear();// ----小王主牌
	classPointTrumpCards.clear();// ----将点主牌
	point2TrumpCards.clear();// ----2主牌
	scoreTrumpCards.clear();// ----分数主牌
	smallTrumpCards.clear();// ----去掉各种分类后的主牌

	straightHeartCards.clear();// ----红心甩牌
	straightClubCards.clear();// ----草花甩牌
	straightSpadeCards.clear();// ----黑桃甩牌
	straightDiamondCards.clear();// ----方块甩牌
	allStraightCards.clear();// ----所有甩牌
	allStraightCardsWithScore.clear();// ----所有甩牌中分牌
	allStraightCardsWithAce.clear();// ----所有甩牌中ACE
	allStraightCardsNoScore.clear();// ----所有去分数后的甩牌
	allStraightCardsNoScoreNoAce.clear();// ----所有去分数去ACE后的甩牌
	heartCards.clear();// ----红心其它副牌
	clubCards.clear();// ----草花其它副牌
	spadeCards.clear();// ----黑桃其它副牌
	diamondCards.clear();// ----方块其它副牌
	complex4suitCards.clear();// ----剩下副牌的合集形式---不对子元素原集合做减法
	complex4suitCardsWithScore.clear();// ----剩下副牌有分的牌
	complex4suitCardsWithAce.clear();// ----剩下副牌有ACE的牌
	complex4suitCardsNoScore.clear();// ----剩下副牌的无分合集
	complex4suitCardsNoScoreNoAce.clear();// ----剩下副牌的无分牌，无Ace合集
	comlexScoreCards_otherCardsSuit.clear();// ----剩下副牌的合集中单花色--有分又有非分牌
    }

    @Override
    public int allStraightSuitSum() {
	int result = 0;
	// 遍历四套有甩副牌集合，统计
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
	// 合并四套副牌集合，统计;分ACE状态
	boolean aceTrump = recorder.getCurrentClassPoint() == 1;

	List<ICard> fourCollection = new ArrayList<ICard>();
	fourCollection.addAll(heartCards);
	fourCollection.addAll(clubCards);
	fourCollection.addAll(spadeCards);
	fourCollection.addAll(diamondCards);

	// 是，找KING
	if (aceTrump) {
	    for (ICard card : fourCollection) {
		boolean king = (card.getPoint() == 13);
		result += king ? 1 : 0;
	    }
	}
	// 不是，找ACE
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
	// 弱副合集中个数，减强副数
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
	// 合并杂牌集合，减去副牌中大牌
	List<ICard> fourCollection = new ArrayList<ICard>();
	fourCollection.addAll(heartCards);
	fourCollection.addAll(clubCards);
	fourCollection.addAll(spadeCards);
	fourCollection.addAll(diamondCards);
	List<ICard> tempKing = new ArrayList<ICard>();
	boolean aceTrump = recorder.getCurrentClassPoint() == 1;
	// king大副，不是危分，可减
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
	// 定下数量，找分数，找小主
	int size = opponentCards.size();
	// 先在其它主中找分数牌
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
	    tools.swapAceFromTopToBottomForOneSuitCards(this.smallTrumpCards);// Ace放到最后
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
	// 将全部主分成两部分，分和非分--依层找
	List<ICard> noScoreTrumps = new ArrayList<ICard>();
	noScoreTrumps.addAll(tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder));
	noScoreTrumps.removeAll(this.scoreTrumpCards);
	// 先在有分中找
	List<ICard> temp = new ArrayList<ICard>();
	Collections.sort(this.scoreTrumpCards);// 从低分主出起
	for (ICard iCard : this.scoreTrumpCards) {
	    temp.clear();
	    temp.add(iCard);
	    boolean higher = gameRule.whoIsRoundWinner(opponentCards, temp).equals(robot);
	    if (higher) {
		result.add(iCard);
		break;
	    }
	}
	// 然后再在其它主中依层找
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
	// 花色
	String suit = opponentCards.get(0).getSuit();

	if (result.isEmpty()) {
	    // 同花有分副牌
	    result.addAll(this.findSameSuitCardsWithScore(opponentCards, suit));
	    if (result.isEmpty()) {
		// 同花杂副牌--没分也没ACE
		result.addAll(this.findSameSuitSmallCards(opponentCards, suit));
		if (result.isEmpty()) {
		    // 同花孤零ACE
		    result.addAll(this.findAceCards(opponentCards, suit));
		    if (result.isEmpty()) {
			// 同花所有副牌
			result.addAll(this.findAllSameSuitCards(opponentCards, suit));
			if (result.isEmpty()) {
			    // 主牌中找（依次是分主，小主）
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

	// 首先，从有分主牌中找一张
	boolean hasScore = !scoreTrumpCards.isEmpty();
	if (hasScore) {
	    result.add(scoreTrumpCards.get(0));
	}
	if (result.isEmpty()) {
	    // 如果没有，从其它主中找一张
	    tools.swapAceFromTopToBottomForOneSuitCards(smallTrumpCards);
	    boolean hasCards = smallTrumpCards != null && smallTrumpCards.size() > 0;
	    if (hasCards) {
		result.add(smallTrumpCards.get(0));
	    }
	    if (result.isEmpty()) {
		// 再没有，用2
		boolean hasPoint2TrumpCards = point2TrumpCards.size() > 0;
		if (hasPoint2TrumpCards) {
		    result.add(point2TrumpCards.get(0));
		}
		if (result.isEmpty()) {
		    // 再没有，用将主
		    boolean hasclassPointTrumpCards = classPointTrumpCards.size() > 0;
		    if (hasclassPointTrumpCards) {
			result.add(classPointTrumpCards.get(0));
		    }
		    if (result.isEmpty()) {
			// 再没有，用小王
			boolean hasjoker2TrumpCards = joker2TrumpCards.size() > 0;
			if (hasjoker2TrumpCards) {
			    result.add(joker2TrumpCards.get(0));
			}
			if (result.isEmpty()) {
			    // 再没有，主牌就可以
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
	// 同色全副牌集合
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
	// 同色杂副牌集合
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
     * 后手随翁牌.
     */
    @Override
    public List<ICard> followCards4(List<ICard> opponentCards) {
	List<ICard> result = new ArrayList<ICard>();
	// 三种情况：有翁||主够---->主不够
	boolean hasCards4 = !this.cards4.isEmpty();
	if (hasCards4) {
	    result.addAll(this.cards4);
	} else {
	    boolean trumpFull = (tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder).size() >= 4);
	    if (trumpFull) {
		// 按最小代价出主
		result.addAll(this.minCostTrumpCards(opponentCards.size()));

	    } else {
		// 主不够，副来凑
		result.addAll(tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder));
		result.addAll(this.minCostNorTrumpCards(opponentCards.size() - result.size()));
	    }

	}

	return result;
    }

    /**
     * 后手主牌不足时补上的最低成本的副牌
     *
     * @param subSize
     * @return
     */
    private List<ICard> minCostNorTrumpCards(int subSize) {
	List<ICard> result = new ArrayList<ICard>();
	// 尽可能: 1、出小牌；2、不出分
	if (result.size() < subSize) {
	    // 无分无ACE杂副牌
	    for (int i = 0; i < this.complex4suitCardsNoScoreNoAce.size(); i++) {
		result.add(this.complex4suitCardsNoScoreNoAce.get(i));
		boolean same = (subSize == result.size());
		if (same) {
		    break;
		}
	    }

	    if (result.size() < subSize) {
		// 不足，则杂副牌中Ace集合
		for (int i = 0; i < this.complex4suitCardsWithAce.size(); i++) {
		    result.add(this.complex4suitCardsWithAce.get(i));
		    boolean same = (subSize == result.size());
		    if (same) {
			break;
		    }
		}

		if (result.size() < subSize) {
		    // 再没有，甩牌中无分无ACE牌
		    for (int i = 0; i < this.allStraightCardsNoScoreNoAce.size(); i++) {
			result.add(this.allStraightCardsNoScoreNoAce.get(i));
			boolean same = (subSize == result.size());
			if (same) {
			    break;
			}
		    }
		    if (result.size() < subSize) {
			// 再没有，甩牌中ACE牌
			for (int i = 0; i < this.allStraightCardsWithAce.size(); i++) {
			    result.add(this.allStraightCardsWithAce.get(i));
			    boolean same = (subSize == result.size());
			    if (same) {
				break;
			    }
			}

			if (result.size() < subSize) {
			    // 再没有，杂副中分牌
			    for (int i = 0; i < this.complex4suitCardsWithScore.size(); i++) {
				result.add(this.complex4suitCardsWithScore.get(i));
				boolean same = (subSize == result.size());
				if (same) {
				    break;
				}
			    }

			    if (result.size() < subSize) {
				// 再没有，甩牌中分牌
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
     * 后手最低成本的主牌
     *
     * @param size
     * @return
     */
    private List<ICard> minCostTrumpCards(int size) {
	// 代价由低到高的出主牌法
	List<ICard> result = new ArrayList<ICard>();
	if (result.size() < size) {

	    // 无分的小主
	    tools.swapAceFromTopToBottomForOneSuitCards(smallTrumpCards);
	    for (int i = 0; i < smallTrumpCards.size(); i++) {
		result.add(smallTrumpCards.get(i));
		boolean same = (size == result.size());
		if (same) {
		    break;
		}

	    }
	    if (result.size() < size) {
		// 不够，用2
		for (int i = 0; i < point2TrumpCards.size(); i++) {
		    result.add(point2TrumpCards.get(i));
		    boolean same = (size == result.size());
		    if (same) {
			break;
		    }
		}
		if (result.size() < size) {
		    // 不够，用将点牌
		    for (int i = 0; i < classPointTrumpCards.size(); i++) {
			result.add(classPointTrumpCards.get(i));
			boolean same = (size == result.size());
			if (same) {
			    break;
			}
		    }
		    if (result.size() < size) {
			// 不够，用小王
			result.add(joker2TrumpCards.get(0));
			if (result.size() < size) {
			    // 不够，分牌就可以
			    for (int i = 0; i < this.scoreTrumpCards.size(); i++) {
				result.add(scoreTrumpCards.get(i));
				boolean same = (size == result.size());
				if (same) {
				    break;
				}
			    }
			    if (result.size() < size) {
				// 不够，是主牌就可以
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
     * 后手随甩牌
     */
    @Override
    public List<ICard> followStraightCards(List<ICard> opponentCards) {
	return giveResult4StraightAndNorTrump(opponentCards);

/*
	List<ICard> result = new ArrayList<ICard>();
	boolean strategy3000 = (robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_ROUND);
	if (!strategy3000) {
	    // 非3000策略的跟牌
	    result.addAll(this.focusOnScore(opponentCards));
	} else {
	    // 3000策略的跟牌，分是次要，留主保底
	    result.addAll(this.focusOnWinRound(opponentCards));
	}

	return result;
	*/

    }

    private List<ICard> focusOnWinRound(List<ICard> opponentCards) {
	return giveResult4StraightAndNorTrump(opponentCards);
/*
	// 尽量不加入主牌
	List<ICard> result = new ArrayList<ICard>();
	List<ICard> targetCards = new ArrayList<ICard>();
	// 先是必须同花色,不足任意凑;尽量不加分
	String suit = opponentCards.get(0).getSuit();
	int size = opponentCards.size();// 差

	if (result.size() < size) {
	    // 杂牌同花色牌中无分无ACE牌
	    boolean argList = this.complex4suitCardsNoScoreNoAce.size() > 0;
	    if (argList) {
		List<ICard> cardsNoScoreNoAce = tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsNoScoreNoAce, suit);
		 targetCards.clear();
		targetCards.addAll(cardsNoScoreNoAce);
		result.addAll(this.pickFollowingCards(size - result.size(), targetCards));
	    }

	    if (result.size() < size) {
		// 杂牌同花色小分优先牌(升序)
		List<ICard> cardScore = tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithScore, suit);
		Collections.reverse(cardScore);
		 targetCards.clear();
		targetCards.addAll(cardScore);
		result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

		if (result.size() < size) {
		    // 杂牌同花色Ace牌
		    List<ICard> cardAce = tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithAce, suit);
			 targetCards.clear();
			targetCards.addAll(cardAce);
		    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

		    if (result.size() < size) {
			// 其它杂副牌中无分，无ACE牌
			 targetCards.clear();
			targetCards.addAll(this.complex4suitCardsNoScoreNoAce);
			targetCards.removeAll(tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsNoScoreNoAce, suit));
			result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

			if (result.size() < size) {
			    // 其它杂副牌中有分牌
			    targetCards.clear();
			    targetCards.addAll(this.complex4suitCardsWithScore);
			    targetCards.removeAll(tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithScore, suit));
			    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

			    if (result.size() < size) {
				// 杂副ACE牌
				targetCards.clear();
				targetCards.addAll(this.complex4suitCardsWithAce);
				targetCards.removeAll(tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithAce, suit));
				result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

				if (result.size() < size) {
				    // 甩牌中无分，无ACE牌
				    targetCards.clear();
				    targetCards.addAll(this.allStraightCardsNoScoreNoAce);
				    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

				    if (result.size() < size) {
					// 甩牌中ACE牌
					targetCards.clear();
					targetCards.addAll(this.allStraightCardsWithAce);
					result.addAll(this.pickFollowingCards(size - result.size(), targetCards));
					if (result.size() < size) {
					    // 甩牌中分数牌
					    targetCards.clear();
					    targetCards.addAll(allStraightCardsWithScore);
					    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));
					    if (result.size() < size) {
						// 主牌中无分小牌
						targetCards.clear();
						targetCards.addAll(smallTrumpCards);
						result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

						if (result.size() < size) {
						    // 主牌2
						    targetCards.clear();
						    targetCards.addAll(point2TrumpCards);
						    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

						    if (result.size() < size) {
							// 主将牌
							targetCards.clear();
							targetCards.addAll(classPointTrumpCards);
							result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

							if (result.size() < size) {
							    // 小王
							    targetCards.clear();
							    targetCards.addAll(joker2TrumpCards);
							    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));
							    if (result.size() < size) {
								// 主牌分数牌
								targetCards.clear();
								targetCards.addAll(scoreTrumpCards);
								result.addAll(this.pickFollowingCards(size - result.size(), targetCards));

								if (result.size() < size) {
								    // 守底牌
								    targetCards.clear();
								    targetCards.addAll(topCard);
								    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));
									if (result.size() < size) {
									    // 任意副牌
									    targetCards.clear();
									    targetCards.addAll(allNonTrumpCards);
									    result.addAll(this.pickFollowingCards(size - result.size(), targetCards));
        									    if (result.size() < size) {
        									    // 任意主牌
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
     * 在规定的能量小包中，找选跟随出牌.
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
     * 后手随主牌
     */
    @Override
    public List<ICard> followTrumpCard(List<ICard> opponentCards) {

	systemOutPrintLSubBags("followTrumpCard");

	List<ICard> result = new ArrayList<ICard>();

	// 只有一张，有主必须给小于对手牌的主，尽可能不给分;没主给副，给无分小副
	boolean hasTrump = this.allTrumpCards.size()>0||tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder).size()>0;
	//TODO:
	System.out.println("@RobotPowerCardBag followTrumpCard()  allTrumpCards---->"+hasTrump);
	if (hasTrump) {
	    // 有主行为
	    if (result.size() == 0) {
		// 无分主
		result.addAll(this.pickCardToFollowingTrump(smallTrumpCards));
		if (result.size() == 0) {
		    // 主牌2
		    result.addAll(this.pickCardToFollowingTrump(point2TrumpCards));
		    if (result.size() == 0) {
			// 将牌
			result.addAll(this.pickCardToFollowingTrump(classPointTrumpCards));
			if (result.size() == 0) {
			    // 分主牌
			    result.addAll(this.pickCardToFollowingTrump(scoreTrumpCards));
				if (result.size() == 0) {
				    // 全部主牌
				    result.addAll(this.pickCardToFollowingTrump(allTrumpCards));
				    if (result.size() == 0) {
					// 全部主牌_备用
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
	    // 无主行为
	    if (result.size() == 0) {
		// 杂副牌无分无ACE
		result.addAll(this.pickNorTrumpCardToFollowTrump(complex4suitCardsNoScoreNoAce));
		if (result.size() == 0) {
		    // 甩牌中无分无ACE
		    result.addAll(this.pickNorTrumpCardToFollowTrump(allStraightCardsNoScoreNoAce));
		    if (result.size() == 0) {
			// 杂副牌ACE
			result.addAll(this.pickNorTrumpCardToFollowTrump(complex4suitCardsWithAce));
			if (result.size() == 0) {
			    // 甩牌ACE
			    result.addAll(this.pickNorTrumpCardToFollowTrump(allStraightCardsWithAce));
			    if (result.size() == 0) {
				// 杂副牌分牌
				result.addAll(this.pickNorTrumpCardToFollowTrump(complex4suitCardsWithScore));
				if (result.size() == 0) {
				    // 甩牌分牌
				    result.addAll(this.pickNorTrumpCardToFollowTrump(allStraightCardsWithScore));
				    if (result.size() == 0) {
					// 任意副牌
					 result.addAll(this.pickNorTrumpCardToFollowTrump(allNonTrumpCards));
					 if (result.size() == 0) {
					     // 任意牌1
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
	 * ----红心甩牌 private List<ICard> straightClubCards = new
	 * ArrayList<ICard>();// ----草花甩牌 private List<ICard> straightSpadeCards
	 * = new ArrayList<ICard>();// ----黑桃甩牌 private List<ICard>
	 * straightDiamondCards = new ArrayList<ICard>();// ----方块甩牌 private
	 * List<ICard> allStraightCards = new ArrayList<ICard>();// ----所有甩牌
	 * private List<ICard> allStraightCardsWithScore = new
	 * ArrayList<ICard>();// ----所有甩牌中分牌 private List<ICard>
	 * allStraightCardsWithAce = new ArrayList<ICard>();// ----所有甩牌中ACE
	 * private List<ICard> allStraightCardsNoScore = new
	 * ArrayList<ICard>();// ----所有去分数后的甩牌 private List<ICard>
	 * allStraightCardsNoScoreNoAce = new ArrayList<ICard>();//
	 * ----所有去分数去ACE后的甩牌 private List<ICard> heartCards = new
	 * ArrayList<ICard>();// ----红心其它副牌 private List<ICard> clubCards = new
	 * ArrayList<ICard>();// ----草花其它副牌 private List<ICard> spadeCards = new
	 * ArrayList<ICard>();// ----黑桃其它副牌 private List<ICard> diamondCards =
	 * new ArrayList<ICard>();// ----方块其它副牌 private List<ICard>
	 * complex4suitCards = new ArrayList<ICard>();//
	 * ----剩下副牌的合集形式---不对子元素原集合做减法 private List<ICard>
	 * complex4suitCardsWithScore = new ArrayList<ICard>();// ----剩下副牌有分的牌
	 * private List<ICard> complex4suitCardsWithAce = new
	 * ArrayList<ICard>();// ----剩下副牌有ACE的牌 private List<ICard>
	 * complex4suitCardsNoScore = new ArrayList<ICard>();// ----剩下副牌的无分合集
	 * private List<ICard> complex4suitCardsNoScoreNoAce = new
	 * ArrayList<ICard>();// ----剩下副牌的无分牌，无Ace合集 private List<ICard>
	 * comlexScoreCards_otherCardsSuit = new ArrayList<ICard>();//
	 * ----剩下副牌的合集中单花色--有分又有非分牌
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
     * 跟随主牌选副牌（无主时）
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
     * 给随主牌选主牌
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
	    // 找一张最小的牌
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
		// 如果没有小于对手的牌，只好找一张最小的牌出--被动达到管牌的效果
		tools.swapAceFromTopToBottomForOneSuitCards(cardsForPicking);
		result.add(cardsForPicking.get(0));
	    }
	}
	*/



	return result;
    }

    /**
     * 后手随副牌
     */
    @Override
    public List<ICard> followNorTrumpCard(List<ICard> opponentCards) {

	return giveResult4StraightAndNorTrump(opponentCards);

/*
	List<ICard> result = new ArrayList<ICard>();
	String suit = opponentCards.get(0).getSuit();

	List<ICard> sameSuitCards = new ArrayList<ICard>();
	int size = opponentCards.size();

	//同花色，分两部分：无分，有分；先后分别从小往大出；不够再从其它副牌的散牌中出（）

	sameSuitCards.addAll(this.tools.seperateSameSuitCardsSortedDesc(this.allNonTrumpCards, suit));
	Collections.reverse(sameSuitCards);// 改成升序
	boolean hasSameSuit = !sameSuitCards.isEmpty();// 有同花色

	if (hasSameSuit) {
	    // 同花色
	    if (result.size() < size) {
		// 同花非分牌
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
		    // 同花分牌
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
	    // 非同花色
	    if (result.size() < size) {
		// 杂牌非分非Ace
		boolean hasCards = !this.complex4suitCardsNoScoreNoAce.isEmpty();
		result.addAll(this.pickNotSameSuitTofollowNorTrumpCard(opponentCards, hasCards, this.complex4suitCardsNoScoreNoAce));
		if (result.size() < size) {
		    // 杂牌Ace
		    boolean hasCards0 = !this.complex4suitCardsWithAce.isEmpty();
		    result.addAll(this.pickNotSameSuitTofollowNorTrumpCard(opponentCards, hasCards0, this.complex4suitCardsWithAce));
		    if (result.size() < size) {
			// 甩牌非分非ACE
			boolean hasCards1 = !this.allStraightCardsNoScoreNoAce.isEmpty();
			result.addAll(this.pickNotSameSuitTofollowNorTrumpCard(opponentCards, hasCards1, this.allStraightCardsNoScoreNoAce));
			if (result.size() < size) {
			    // 甩牌ACE
			    boolean hasCards2 = !this.allStraightCardsWithAce.isEmpty();
			    result.addAll(this.pickNotSameSuitTofollowNorTrumpCard(opponentCards, hasCards2, this.allStraightCardsWithAce));
			    if (result.size() < size) {
				// 杂牌分
				boolean hasCards3 = !this.complex4suitCardsWithScore.isEmpty();
				result.addAll(this.pickNotSameSuitTofollowNorTrumpCard(opponentCards, hasCards3, this.complex4suitCardsWithScore));
				if (result.size() < size) {
				    // 任意副牌
				    boolean hasCards4 = !this.allNonTrumpCards.isEmpty();
				    result.addAll(this.pickNotSameSuitTofollowNorTrumpCard(opponentCards, hasCards4, this.allNonTrumpCards));
				    if (result.size() < size) {
					// 任意主牌
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
 * 跟单张副牌和跟甩牌是一样的，单张只是特例
 * @param opponentCards
 * @return
 */
    private List<ICard> giveResult4StraightAndNorTrump(List<ICard> opponentCards) {
	List<ICard> result = new ArrayList<ICard>();
	// 先是必须同花色,不足任意凑;尽量不加分
	String suit = opponentCards.get(0).getSuit();
	int size = opponentCards.size();// 求差用
	List<ICard> targetCards = new ArrayList<ICard>();

	if (result.size() < size) {
	    // 杂牌同花色牌中无分无ACE牌
	    System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump() this.complex4suitCardsNoScoreNoAce-->"+this.complex4suitCardsNoScoreNoAce.size());
	    List<ICard> cardsNoScoreNoAce = tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsNoScoreNoAce, suit);
	    targetCards.clear();
	    targetCards.addAll(cardsNoScoreNoAce);
	    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
		//TODO:
		System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  cardsNoScoreNoAce.isEmpty---->"+targetCards.isEmpty());
		System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());


	    if (result.size() < size) {
		// 杂牌同花色Ace牌
		List<ICard> cardAce = tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithAce, suit);
		 targetCards.clear();
		 targetCards.addAll(cardAce);
		result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
		//TODO:
		System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  cardAce.isEmpty---->"+targetCards.isEmpty());
		System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
		if (result.size() < size) {
		    // 杂牌同花色小分优先牌(升序)
		    List<ICard> cardScore = tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithScore, suit);
		    targetCards.clear();
		    targetCards.addAll(cardScore);
		    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
			//TODO:
			System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  cardScore.isEmpty---->"+targetCards.isEmpty());
			System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
		    if (result.size() < size) {
			// 杂副牌中其它无分，无ACE牌
			targetCards.clear();
			targetCards.addAll(this.complex4suitCardsNoScoreNoAce);
			targetCards.removeAll(tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsNoScoreNoAce, suit));
			result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
			//TODO:
			System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  杂副牌中其它无分，无ACE牌.isEmpty---->"+targetCards.isEmpty());
			System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
			if (result.size() < size) {
			    // 杂副ACE牌
			    targetCards.clear();
			    targetCards.addAll(this.complex4suitCardsWithAce);
			    targetCards.removeAll(tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithAce, suit));
			    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
				//TODO:
				System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  杂副ACE牌.isEmpty---->"+targetCards.isEmpty());
				System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
			    if (result.size() < size) {
				// 甩牌中无分，无ACE牌
				targetCards.clear();
				targetCards.addAll(this.allStraightCardsNoScoreNoAce);
				result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
				//TODO:
				System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  甩牌中无分，无ACE牌.isEmpty---->"+targetCards.isEmpty());
				System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
				if (result.size() < size) {
				    // 甩牌中ACE牌
				    targetCards.clear();
				    targetCards.addAll(this.allStraightCardsWithAce);
				    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
					//TODO:
					System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  甩牌中ACE牌.isEmpty---->"+targetCards.isEmpty());
					System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
				    if (result.size() < size) {
					// 主牌中无分小牌
					targetCards.clear();
					targetCards.addAll(smallTrumpCards);
					result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
					//TODO:
					System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  主牌中无分小牌.isEmpty---->"+targetCards.isEmpty());
					System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
					if (result.size() < size) {
					    // 主牌2
					    targetCards.clear();
					    targetCards.addAll(point2TrumpCards);
					    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
						//TODO:
						System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  主牌2.isEmpty---->"+targetCards.isEmpty());
						System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
					    if (result.size() < size) {
						// 主将牌
						targetCards.clear();
						targetCards.addAll(classPointTrumpCards);
						result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
						//TODO:
						System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  主将牌.isEmpty---->"+targetCards.isEmpty());
						System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
						if (result.size() < size) {
						    // 小王
						    targetCards.clear();
						    targetCards.addAll(joker2TrumpCards);
						    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
							//TODO:
							System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  小王.isEmpty---->"+targetCards.isEmpty());
							System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
						    if (result.size() < size) {
							// 其它杂副牌中有分牌
							targetCards.clear();
							targetCards.addAll(this.complex4suitCardsWithScore);
							targetCards.removeAll(tools.seperateNoTrumpSameSuitCards(this.complex4suitCardsWithScore, suit));
							result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
							//TODO:
							System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  其它杂副牌中有分牌.isEmpty---->"+targetCards.isEmpty());
							System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());

							if (result.size() < size) {
							    // 甩牌中分数牌
							    targetCards.clear();
							    targetCards.addAll(allStraightCardsWithScore);
							    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
								//TODO:
								System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  甩牌中分数牌.isEmpty---->"+targetCards.isEmpty());
								System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
							    if (result.size() < size) {
								// 主牌分数牌
								targetCards.clear();
								targetCards.addAll(scoreTrumpCards);
								result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
								//TODO:
								System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump() 主牌分数牌.isEmpty---->"+targetCards.isEmpty());
								System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
								if (result.size() < size) {
								    // 守底牌
								    targetCards.clear();
								    targetCards.addAll(topCard);
								    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
									//TODO:
									System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump() 守底牌.isEmpty---->"+targetCards.isEmpty());
									System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
								    if (result.size() < size) {
									// 任意副牌
									targetCards.clear();
									targetCards.addAll(allNonTrumpCards);
									result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
									//TODO:
									System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump() 任意副牌.isEmpty---->"+targetCards.isEmpty());
									System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());
									if (result.size() < size) {
									    // 任意主牌
									    targetCards.clear();
									    targetCards.addAll(allTrumpCards);
									    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
										//TODO:
										System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump() 任意主牌.isEmpty---->"+targetCards.isEmpty());
										System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump()  result---->"+result.size());

										if (result.size() < size) {
										    // 任意牌
										    targetCards.clear();
										    targetCards.addAll(robot.actions().getSelfCardDeck());
										    result.addAll(this.pickFollowingCards(size - result.size(), targetCards,result));
										    //TODO:
										    System.out.println("@RobotPowerCardBag giveResult4StraightAndNorTrump() 任意牌.isEmpty---->"+targetCards.isEmpty());
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
     * 给跟随副牌选非同花色副
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
	// 分清对手牌类型
	Integer type = opponentInfo.get("type");
	// 对手牌花色--非翁时有意义
	String suit = opponentCards.get(0).getSuit();
	// 单牌的集合型门面模式
	List<ICard> temp = new ArrayList<ICard>();
	// 翁----1，甩----2，主----3，副----4
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
