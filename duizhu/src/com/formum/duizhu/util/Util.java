package com.formum.duizhu.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.formum.duizhu.FetchCards;
import com.formum.duizhu.ShowTableHuman;
import com.formum.duizhu.ShowTableRobot;
import com.formum.duizhu.R;
import com.formum.duizhu.cardDeck.CardDeck;
import com.formum.duizhu.cardDeck.ICardDeck;
import com.formum.duizhu.cardDeck.card.Card;
import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.gameRule.IGameRule;
import com.formum.duizhu.player.AIPlay;
import com.formum.duizhu.player.GeneralPlay;
import com.formum.duizhu.player.HumanPlayer;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.player.RobotPlayer;
import com.formum.duizhu.recorder.IRecorder;
import com.formum.duizhu.recorder.Recorder;

public class Util implements IUtil {

    private static volatile Util INSTANCE = null;

    public static IUtil getInstance() {
	if (INSTANCE == null) {
	    synchronized (Util.class) {
		// when more than two threads run into the first null check same
		// time, to avoid instanced more than one time, it needs to be
		// checked again.
		if (INSTANCE == null) {
		    INSTANCE = new Util();
		}
	    }
	}
	return INSTANCE;
    }

    private IRecorder recorder = Recorder.getInstance();

    private Util() {

    }

    @Override
    public int capableShowingClassPoint() {
	int classPoint = 0;
	IRecorder recorder = Recorder.getInstance();
	List<Player> players = recorder.getPlayerRegistered();
	for (Player player : players) {
	    if (player.actions().isBanker()) {
		classPoint = player.actions().getMyClassPoint();
		break;
	    }
	}

	return classPoint;
    }

    @Override
    public String changeSuitFromEnglishToChinese(String suit) {
	String result = "";
	if (suit.equals("heart")) {
	    result = "红桃";
	}
	if (suit.equals("spade")) {
	    result = "黑桃";
	}
	if (suit.equals("club")) {
	    result = "草花";
	}
	if (suit.equals("diamond")) {
	    result = "方块";
	}

	return result;
    }

    @Override
    public List<ICard> confirmTopCard(List<ICard> cards) {
	List<ICard> topCard = new ArrayList<ICard>();
	IRecorder recorder = Recorder.getInstance();
	List<ICard> allCards = CardDeck.getInstance().getCardsMirror();// 全套牌
	List<ICard> allTrumpCards = this.seperateTrumpsFromCards(allCards, recorder);// 全套主牌

	List<ICard> cardsOntable = recorder.getCardsOnTable();// 桌面牌
	List<ICard> trumpCardsOntable = this.seperateTrumpsFromCards(cardsOntable, recorder);// 桌面主牌

	List<ICard> trumpCards = this.seperateTrumpsFromCards(cards, recorder);// 手牌主

	boolean hasTrump = !trumpCards.isEmpty();
	boolean noTrumpCardsOntable = trumpCardsOntable.isEmpty();

	// 手中有主牌,桌上无主

	if (hasTrump && noTrumpCardsOntable) {
	    // 找王
	    for (ICard iCard : cards) {
		boolean joker1 = (iCard.getSuit().equals("joker") && iCard.getPoint() == 1);
		if (joker1) {
		    topCard.clear();
		    topCard.add(iCard);
		    break;
		}
	    }
	}

	// 手中有主牌,桌上有主
	if (hasTrump && !noTrumpCardsOntable) {
	    // 所有主中减去桌上牌，剩下主中最大的与手中牌相同即是它！！！。
	    List<ICard> trumpsExceptTrumpsOnTable = new ArrayList<ICard>();
	    allTrumpCards.removeAll(trumpCardsOntable);// 非桌上主牌--减剩下的
	    trumpsExceptTrumpsOnTable.addAll(allTrumpCards);

	    ICard myTempTopCard = this.pickTopOneInTrumpCollection(trumpCards);// 自身暂时最大主牌

	    boolean myTempTopCardNotNull = myTempTopCard != null;
	    if (myTempTopCardNotNull) {
		ICard balanceTopCard = this.pickTopOneInTrumpCollection(trumpsExceptTrumpsOnTable);// 全局当前最大主牌
		// 如果二者完全相等
		boolean sameObj = myTempTopCard.equals(balanceTopCard);
		// 只是点数相等，在双方都有牌的情况下，只有在先手的情况下，才能守底
		boolean samepoint = myTempTopCard.getPoint() == balanceTopCard.getPoint();

		if (sameObj) {
		    topCard.clear();
		    topCard.add(myTempTopCard);
		} else if (!sameObj && samepoint) {
		    topCard.clear();
		    topCard.add(myTempTopCard);
		}
	    }

	}

	return topCard;
    }

    @Override
    public int countSingleSuit(List<ICard> cards, String suitName) {
	int count = 0;
	boolean sizeOk = (cards.size() > 0);
	if (sizeOk) {
	    for (ICard iCard : cards) {
		boolean suitSame = iCard.getSuit().equals(suitName);
		if (suitSame) {
		    count += 1;
		}
	    }
	}
	return count;
    }

    @Override
    public boolean eachCardSamePoint(List<ICard> cards) {
	boolean result = true;
	int thePoint = cards.get(0).getPoint();

	for (ICard iCard : cards) {
	    boolean condition = (thePoint != iCard.getPoint());
	    if (condition) {
		result = false;
	    }
	}

	return result;
    }

    @Override
    public int grantTenTrumpCardsAndAceRank() {
	// 说明：为了方便，副牌的 ACE 也在这里给了阶，14表示区别，功能也可以用。
	int result = 0;

	// 记录--将牌点--全牌--授阶（特别处理ACE --16）
	IRecorder recorder = Recorder.getInstance();

	try {

	    // 将牌点
	    int currentClassPoint = recorder.getCurrentClassPoint();

	    // 全牌
	    ICardDeck cardDeck = CardDeck.getInstance();
	    List<ICard> allCards = cardDeck.getCardsMirror();

	    // 部分授阶，因为主点牌是变动的
	    for (ICard iCard : allCards) {
		boolean jokers=iCard.getSuit().equals("joker");
		if (jokers) {
		    boolean one=iCard.getPoint() == 1;
		    if (one) {
			 iCard.setTrumpRank(20);
		    } else {
			 iCard.setTrumpRank(19);
		    }
		} else {
		    boolean cardIs2=iCard.getPoint() == 2;//主牌2
		    if (cardIs2) {
			iCard.setTrumpRank(17);
		    } else {
			boolean currentClasspointCard = (iCard.getPoint() == currentClassPoint);//主牌将点
			if (currentClasspointCard) {
			    iCard.setTrumpRank(18);
			 }else{
				boolean ace= iCard.getPoint() ==1;//其它牌：ACE当前不是主点，又因为点是1但实际大于13，所以特别给阶
				if (ace) {
				       iCard.setTrumpRank(14);
				}
			}
		    }
		}
		
	    }

	    result = 1;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	Log.v("whoIsRoundWinner", "@Util grantRank Robot getSelfCardDeck().size() "+RobotPlayer.getInstance(AIPlay.getInstance()).actions().getSelfCardDeck().size());
	Log.v("whoIsRoundWinner", "@Util grantRank  Human getSelfCardDeck().size() "+HumanPlayer.getInstance(GeneralPlay.getInstance()).actions().getSelfCardDeck().size());
	return result;
	
    }

    @Override
    public boolean isAllTrumpCards(List<ICard> cards) {
	boolean result = false;
	// 全数是主与出牌数相等
	int cardSize = cards.size();
	int trumpSize = seperateTrumpsFromCards(cards, Recorder.getInstance()).size();
	boolean trumps = cardSize == trumpSize;
	if (trumps) {
	    result = true;
	}
	return result;
    }

    @Override
    public boolean isArraySizeEqual(List<ICard> firstcards, List<ICard> secondcards) {
	boolean result = false;
	boolean same = (firstcards.size() == secondcards.size());
	if (same) {
	    result = true;
	}
	return result;
    }

    @Override
    public boolean isSameCard2(final List<ICard> cards) {
	boolean result = false;
	// 1、花色没有joker；2、点数2
	int count = 0;
	for (ICard iCard : cards) {
	    boolean nojoker = !iCard.getSuit().equals("joker");
	    boolean point2 = iCard.getPoint() == 2;
	    if (nojoker && point2) {
		count += 1;
	    }
	}
	if (count == cards.size()) {
	    result = true;
	}
	return result;
    }

    @Override
    public boolean isSameClassPoint(List<ICard> cards) {
	boolean result = false;
	Recorder recorder = Recorder.getInstance();
	// 1、与recorder登记将点相等
	int count = 0;
	for (ICard iCard : cards) {
	    if (iCard.getPoint() == recorder.getCurrentClassPoint()) {
		count += 1;
	    }
	}
	if (count == cards.size()) {
	    result = true;
	}
	return result;
    }

    @Override
    public boolean isSameNoTrumpCardsSuit(List<ICard> cards) {
	boolean result = false;
	// 得到全部副牌
	List<ICard> allCards = new ArrayList<ICard>();
	allCards.addAll(CardDeck.getInstance().getCardsMirror());
	List<ICard> trumps = seperateTrumpsFromCards(allCards, Recorder.getInstance());
	allCards.removeAll(trumps);
	List<ICard> noTrumpCards = new ArrayList<ICard>();
	noTrumpCards.addAll(allCards);// --全部副牌
	// 1、全属于副牌；2、同花色
	boolean notrump = noTrumpCards.containsAll(cards);
	if (notrump) {
/*	    
	    int count = 0;
	    boolean hasCards = (cards != null && cards.size() > 0);
	    if (hasCards) {
		String suit = cards.get(0).getSuit();
		for (ICard iCard : cards) {
		    if (suit.equals(iCard.getSuit())) {
			count += 1;
		    }
		}
		if (count == cards.size()) {
		    result = true;
		}
	    }
	    */
	    result = true;
	}

	return result;
    }

    @Override
    public int isThereBanker() {
	int result = 0;
	IRecorder recorder = Recorder.getInstance();
	List<Player> players = recorder.getPlayerRegistered();
	for (Player player : players) {
	    if (player.actions().isBanker()) {
		result = 1;
		break;
	    }
	}
	return result;
    }

    @Override
    public boolean isthereJoker(List<ICard> cards) {
	boolean result = false;
	for (ICard iCard : cards) {
	    boolean joker = iCard.getSuit().equals("joker");
	    if (joker) {
		result = true;
	    }
	}
	return result;
    }

    @Override
    public boolean meOnTheOffensive(Player me, IRecorder recorder) {
	boolean result = false;
	// 有轮胜者记录还是自己：先手||桌上还没有牌，出牌即： 先手
	boolean hasWinner = recorder.getRoundWinner() != null;
	if (hasWinner) {
	    boolean roundWinnerIsMe = recorder.getRoundWinner().equals(me);
	    if (roundWinnerIsMe) {
		result = true;
	    }
	}
	boolean zeroTable = recorder.getCardsOnTable().size() == 0;
	if (zeroTable) {
	    result = true;
	}
	return result;
    }

    @Override
    public boolean noRepeatSuit(List<ICard> cards) {

	boolean result = true;
	int countheart = 0;
	int countdiamond = 0;
	int countspade = 0;
	int countclub = 0;
	int countjoker = 0;

	for (int i = 0; i < cards.size(); i++) {
	    if (cards.get(i).getSuit().equals("heart")) {
		countheart += 1;
	    }
	}
	for (int i = 0; i < cards.size(); i++) {
	    if (cards.get(i).getSuit().equals("diamond")) {
		countdiamond += 1;
	    }
	}
	for (int i = 0; i < cards.size(); i++) {
	    if (cards.get(i).getSuit().equals("spade")) {
		countspade += 1;
	    }
	}
	for (int i = 0; i < cards.size(); i++) {
	    if (cards.get(i).getSuit().equals("club")) {
		countclub += 1;
	    }
	}
	for (int i = 0; i < cards.size(); i++) {
	    if (cards.get(i).getSuit().equals("joker")) {
		countjoker += 1;
	    }
	}

	boolean heart = (countheart > 1);
	boolean diamond = (countdiamond > 1);
	boolean spade = (countspade > 1);
	boolean club = (countclub > 1);
	boolean joker = (countjoker > 1);

	if (heart || diamond || spade || club || joker) {
	    result = false;
	}

	return result;

    }

    @Override
    public ICard pickTopOneInTrumpCollection(List<ICard> cards) {
	ICard topCard = null;
	Stack<ICard> orderedMirrorTrumpCards = sortMirrorTrumpCards();
	int size = orderedMirrorTrumpCards.size();
	for (int i = 0; i < size; i++) {
	    ICard theCard = orderedMirrorTrumpCards.get(i);
	    for (ICard card : cards) {
		if (theCard.getSuit().equals(card.getSuit()) && theCard.getPoint() == card.getPoint()) {
		    topCard = card;
		    break;
		}
	    }
	}
	return topCard;
    }

    @Override
    public int putBankerOn(Player player) {
	int result = 0;
	List<Player> playerList = new ArrayList<Player>();
	try {
	    // 明确对家
	    playerList.addAll(Recorder.getInstance().getPlayerRegistered());
	    playerList.remove(player);
	    Player otherPlayer = playerList.get(0);
	    // 各自赋值（互反）
	    player.actions().setBanker(true);
	    otherPlayer.actions().setBanker(false);
	    playerList.clear();
	    result = 1;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return result;
    }

    @Override
    public List<String> reasoningOpponentEmptyNorTrumpSuit(List<ICard> myCards_fore, List<ICard> opponentCards_after) {
	// 取回默认判断，如果我先出副牌，默认有此花色，对手同花色数不相等，减此花色
	Player me = myCards_fore.get(0).getPlayer();
	List<String> opponentNorTrumpSuits = new ArrayList<String>();
	opponentNorTrumpSuits.addAll(me.actions().getOpponentNorTrumpSuits());
	String mySuit = myCards_fore.get(0).getSuit();

	int opponentSuitsSum = 0;
	for (ICard iCard : opponentCards_after) {
	    boolean suit = mySuit.equals(iCard.getSuit());// 发现同花色
	    if (suit) {
		opponentSuitsSum += 1;
	    }
	}

	boolean sizeDef = (opponentSuitsSum != myCards_fore.size());
	boolean suitInopponentNorTrumpSuits = opponentNorTrumpSuits.contains(mySuit);
	if (sizeDef && suitInopponentNorTrumpSuits) {
	    opponentNorTrumpSuits.remove(mySuit);
	}

	return opponentNorTrumpSuits;

    }

    public boolean reasoningOpponentScoresInOneSuit(String suit) {
	boolean result = false;
	// 每花色25分，减去手牌和桌牌同色分之和，差就是怀疑对手有分数（概率占1/3）；
	Player robot = RobotPlayer.getInstance(AIPlay.getInstance());
	List<ICard> cards = robot.actions().getSelfCardDeck();
	List<ICard> cardsOnTable = Recorder.getInstance().getCardsOnTable();
	boolean cardsNotNull = (cards != null);
	boolean cardsOnTableNotNull = (cardsOnTable != null);
	if (cardsNotNull && cardsOnTableNotNull) {
	    List<ICard> suitCards1 = this.seperateSameSuitCardsSortedDesc(cards, suit);
	    List<ICard> suitCards2 = this.seperateSameSuitCardsSortedDesc(cardsOnTable, suit);
	    int a = this.sumScores(suitCards1);
	    int b = this.sumScores(suitCards2);
	    result = (25 - (a + b)) > 0 ? true : false;
	}
	if (cardsNotNull && !cardsOnTableNotNull) {
	    List<ICard> suitCards1 = this.seperateSameSuitCardsSortedDesc(cards, suit);
	    int a = this.sumScores(suitCards1);
	    result = (25 - a) > 0 ? true : false;
	}
	if (!cardsNotNull && cardsOnTableNotNull) {
	    List<ICard> suitCards2 = this.seperateSameSuitCardsSortedDesc(cardsOnTable, suit);
	    int b = this.sumScores(suitCards2);
	    result = (25 - b) > 0 ? true : false;
	}
	return result;
    }

    @Override
    public boolean reasoningOpponentTrump(List<ICard> myCards_fore, List<ICard> opponentCards_after) {
	boolean result = true;
	// 没有翁，跟翁主不全；前主，后跟副
	boolean cards4 = !this.seperate4SamePointCards(myCards_fore).isEmpty();
	boolean noFullTrump = !this.isAllTrumpCards(opponentCards_after);
	if (cards4 && noFullTrump) {
	    result = false;
	}
	boolean myTrump = this.isAllTrumpCards(myCards_fore);
	boolean noTrump = !this.isAllTrumpCards(opponentCards_after);
	if (myTrump && noTrump) {
	    result = false;
	}

	return result;
    }

    @Override
    public void recordBothSideOutCards(List<ICard> cardsOut, Player player, IRecorder recorder) {
	// 向里写完内容发现有两条记录就清除它们
	boolean full2 = (recorder.getBothOutCards().size() >= 2);
	if (full2) {
	    recorder.getBothOutCards().clear();
	}
	List<Map<Player, List<ICard>>> result = new ArrayList<Map<Player, List<ICard>>>();
	Map<Player, List<ICard>> map = new HashMap<Player, List<ICard>>();
	map.put(player, cardsOut);
	result.add(map);
	recorder.setBothOutCards(result);

    }

    @Override
    public void removeFragmentByTag(Activity context, String string) {

	Fragment frgmt = context.getFragmentManager().findFragmentByTag(string);
	boolean notNullFrgmt = frgmt != null;
	if (notNullFrgmt) {
	    context.getFragmentManager().beginTransaction().remove(frgmt).commit();
	}
    }

    @Override
    public List<ICard> seperate4SamePointCards(List<ICard> cards) {
	List<ICard> cards4 = new ArrayList<ICard>();
	boolean mayHas4Cards=cards!=null&&cards.size()>=4;
	if (mayHas4Cards) {
	    List<ICard> cardsRaw = new ArrayList<ICard>();
	    cardsRaw.addAll(cards);
	    // 1. 暂时排除王牌
	    List<ICard> jokers = new ArrayList<ICard>();
	    for (ICard iCard : cardsRaw) {
		boolean joker = "joker".equals(iCard.getSuit());
		if (joker) {
		    jokers.add(iCard);
		}
	    }
	    boolean hasjoker = jokers.size() > 0;
	    if (hasjoker) {
		cardsRaw.removeAll(jokers);
	    }
	    // 找出四个同点数
	    int point = 0;
	    
	    int[] points = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
	    A: for (int thepoint : points) {
		int count = 0;
		for (ICard card : cardsRaw) {
		    boolean oneof4 = (thepoint == card.getPoint());
		    if (oneof4) {
			count++;
			if (count == 4) {
			    point = thepoint;
			    break A;
			}
		    }
		}
	    }
	    boolean haspoint = (point > 0);// 有翁牌
	    if (haspoint) {
		for (ICard iCard : cardsRaw) {
		    if (iCard.getPoint() == point) {
			cards4.add(iCard);
		    }
		}
	    }
	    
	    boolean cardIs4 = (cards4.size() == 4);// 翁牌必须四张
	    if (!cardIs4) {
		cards4.clear();
	    }
	    
	}
	return cards4;
    }

    @Override
    public List<ICard> seperateNoTrumpCards(List<ICard> cards) {
	List<ICard> noTrumpCards = new ArrayList<ICard>();
	// 减主牌
	try {
	    cards.removeAll(this.seperateTrumpsFromCards(cards, Recorder.getInstance()));
	    boolean notEmpty = !cards.isEmpty();
	    if (notEmpty) {
		noTrumpCards.addAll(cards);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return noTrumpCards;
    }

    @Override
    public List<ICard> seperateNoTrumpSameSuitCards(List<ICard> cards, String suit) {
	List<ICard> tempCards = new ArrayList<ICard>();
	tempCards.addAll(cards);
	List<ICard> noTrumpSameSuitCards = new ArrayList<ICard>();
	// 减主牌，留下一色

	try {
	    tempCards.removeAll(this.seperateTrumpsFromCards(tempCards, Recorder.getInstance()));

	    boolean notEmpty = !tempCards.isEmpty();
	    if (notEmpty) {
		for (ICard iCard : tempCards) {
		    boolean sameSuit = suit.equals(iCard.getSuit());
		    if (sameSuit) {
			noTrumpSameSuitCards.add(iCard);
		    }
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}

	return noTrumpSameSuitCards;
    }

    @Override
    public List<ICard> seperateSameSuitCardsSortedDesc(List<ICard> cards, String suit) {
	List<ICard> soloSuitCards = new ArrayList<ICard>();
	// 1.单花色；2.排降序

	for (ICard iCard : cards) {
	    boolean isSuit = iCard.getSuit().equals(suit);
	    if (isSuit) {
		soloSuitCards.add(iCard);
	    }
	}
	Collections.sort(soloSuitCards);
	Collections.reverse(soloSuitCards);
	return soloSuitCards;
    }

    @Override
    public List<ICard> seperateStandardNoTrumpSameSuitSortedDesc(String suit) {
	// 全牌减主，按四色之一返回
	List<ICard> oneSuitOrderdCards = new ArrayList<ICard>();
	List<ICard> tempList = new ArrayList<ICard>();
	List<ICard> cards = CardDeck.getInstance().getCardsMirror();
	List<ICard> trumpCards = this.seperateTrumpsFromCards(cards, Recorder.getInstance());
	cards.removeAll(trumpCards);

	for (ICard iCard : cards) {
	    boolean samesuit = iCard.getSuit().equals(suit);
	    if (samesuit) {
		tempList.add(iCard);
	    }
	}
	Collections.sort(tempList);
	Collections.reverse(tempList);
	// 如果集合中有ace，要将其串到第一个上。
	// 先拿出来
	ICard aceCard = null;
	for (ICard iCard : tempList) {
	    boolean ace = (iCard.getPoint() == 1);
	    if (ace) {
		aceCard = iCard;
		tempList.remove(iCard);
		break;
	    }
	}
	boolean aceNull = (aceCard == null);
	if (aceNull) {
	    oneSuitOrderdCards.addAll(tempList);
	} else {
	    oneSuitOrderdCards.add(aceCard);
	    oneSuitOrderdCards.addAll(tempList);
	}

	return oneSuitOrderdCards;
    }

    @Override
    public List<ICard> seperateStraightCards(List<ICard> cards) {

	// cards是副牌
	// 新的标准降序副牌-->双循环比较，得出有效甩牌;必须2张以上，否则返回null。

	List<ICard> straightCard = new ArrayList<ICard>();// 待返回的甩牌专用套

	String suit = cards.get(0).getSuit();
	List<ICard> totalStaticStandardOneSuitNoTrumpCards = this.seperateStandardNoTrumpSameSuitSortedDesc(suit);// 全部静态标准此花色降序列
	List<ICard> oneSuitNoTrumpCardsInHand = this.sortNoTrumpSameSuitInHandDesc(cards);// 在手同色副牌降序列
	// 桌上同色牌

	List<ICard> cardsOnTable = new ArrayList<ICard>();
	cardsOnTable.addAll(Recorder.getInstance().getCardsOnTable());

	List<ICard> sameSuitCardsOnTable = new ArrayList<ICard>();
	boolean tablecards = !cardsOnTable.isEmpty();
	if (tablecards) {
	    sameSuitCardsOnTable.addAll(this.seperateNoTrumpSameSuitCards(cardsOnTable, suit));
	}

	// 如果有桌上的副牌，新的标准要产生
	List<ICard> currentStandardOneSuitNoTrumpCards = new ArrayList<ICard>();
	boolean total = totalStaticStandardOneSuitNoTrumpCards != null;

	boolean onTable = !sameSuitCardsOnTable.isEmpty();

	if (total && onTable) {

	    totalStaticStandardOneSuitNoTrumpCards.removeAll(sameSuitCardsOnTable);
	}
	currentStandardOneSuitNoTrumpCards.addAll(totalStaticStandardOneSuitNoTrumpCards);// \\---这就是新标准

	// 比较找出甩牌
	boolean currentStandard = !currentStandardOneSuitNoTrumpCards.isEmpty();

	boolean inHand = oneSuitNoTrumpCardsInHand != null;

	if (currentStandard && inHand) {
	    // 找出差异牌集合，降序，取最大一个index,根据此index截取手牌以上的牌
	    List<ICard> keepCurrentStandardOneSuitNoTrumpCards = new ArrayList<ICard>();// 备份一个当前标准集
	    keepCurrentStandardOneSuitNoTrumpCards.addAll(currentStandardOneSuitNoTrumpCards);
	    currentStandardOneSuitNoTrumpCards.removeAll(oneSuitNoTrumpCardsInHand);// 差集
	    boolean sameCollection = currentStandardOneSuitNoTrumpCards.isEmpty();// 没有差集，全手是甩
	    if (sameCollection) {
		boolean morethan2 = oneSuitNoTrumpCardsInHand.size() > 1;// 甩牌要求两张以上
		if (morethan2) {

		    straightCard.addAll(oneSuitNoTrumpCardsInHand);
		}
	    } else {
		ICard theUpperCard = currentStandardOneSuitNoTrumpCards.get(0);// 得出标界牌
		int index = keepCurrentStandardOneSuitNoTrumpCards.indexOf(theUpperCard);// index

		boolean morethan2 = oneSuitNoTrumpCardsInHand.subList(0, index).size() > 1;// 甩牌要求两张以上
		if (morethan2) {

		    straightCard.addAll(oneSuitNoTrumpCardsInHand.subList(0, index));// 截取--->给目标集合
		}
	    }
	}
	//TODO:
	for (ICard iCard : straightCard) {
	    System.out.println("@Util seperateStraightCards(List<ICard> cards)-->straightCard-->"+iCard.getSuit()+iCard.getPoint());
	}
	return straightCard;
    }

    @Override
    public List<ICard> seperateTrumpsFromCards(List<ICard> cards, IRecorder recorder) {
	List<ICard> list = new ArrayList<ICard>();
	if (cards.size() > 0 && recorder != null) {
	    String trumpSuit = recorder.getCurrentTrumpSuit();
	    int trumpPoint = recorder.getCurrentClassPoint();
	    String suit = "";
	    int point = 0;
	    for (ICard card : cards) {
		suit = card.getSuit();
		point = card.getPoint();
		if (suit.equals("joker")) {
		    list.add(card);
		} else if (point == 2) {
		    list.add(card);
		} else if (point == trumpPoint) {
		    list.add(card);
		} else if (suit.equals(trumpSuit)) {
		    list.add(card);
		}

	    }

	}

	return list;

    }

    @Override
    public ImageView showImage(final Activity context, final ICard card, int viewHost) {

	LinearLayout layout = (LinearLayout) context.findViewById(viewHost);
	// 生成小布局
	LinearLayout newLayout = (LinearLayout) new LinearLayout(layout.getContext());
	int width = 96;
	int height = 130;
	// newLayout.setBackgroundColor(Color.rgb(0, 42, 200));
	LayoutParams params = new LayoutParams(width, height);
	newLayout.setLayoutParams(params);
	newLayout.setPadding(0, 1, 0, 0);

	// 生成图像框
	final ImageView imageView = new ImageView(context);
	int left = 0;
	int top = 0;
	int right = 0;
	int bottom = 0;
	imageView.setPadding(left, top, right, bottom);
	// imageView.setBackgroundColor(Color.rgb(230, 122, 29));

	// 图像框加入小布局
	newLayout.addView(imageView);
	// 小布局加入大布局
	layout.addView(newLayout);

	// 生产资源管理对象
	Resources res = context.getResources();
	// 用资源管理对象从资源目录的drawable中获得转型的图片
	Drawable drawable = res.getDrawable(card.getDrawableInt());

	// 图片显示对象加入图片对象
	imageView.setImageDrawable(drawable);

	return imageView;
    }

    @Override
    public ImageView showImage(Activity context, ICard card, int viewHost, int drawableBackInt) {
	LinearLayout layout = (LinearLayout) context.findViewById(viewHost);
	// 生成小布局
	LinearLayout newLayout = (LinearLayout) new LinearLayout(layout.getContext());
	int width = 96;
	int height = 130;
	// newLayout.setBackgroundColor(Color.rgb(0, 42, 200));
	LayoutParams params = new LayoutParams(width, height);
	newLayout.setLayoutParams(params);
	newLayout.setPadding(0, 1, 0, 0);

	// 生成图像框
	ImageView imageView = new ImageView(context);
	int left = 0;
	int top = 0;
	int right = 0;
	int bottom = 0;
	imageView.setPadding(left, top, right, bottom);
	// imageView.setBackgroundColor(Color.rgb(230, 122, 29));

	// 图像框加入小布局
	newLayout.addView(imageView);
	// 小布局加入大布局
	layout.addView(newLayout);

	// 生产资源管理对象
	Resources res = context.getResources();
	// 用资源管理对象从资源目录的drawable中获得转型的图片
	Drawable drawable = res.getDrawable(drawableBackInt);

	// 图片显示对象加入图片对象
	imageView.setImageDrawable(drawable);
	return imageView;
    }

    @Override
    public void showImageInLayers(Activity context, List<ICard> cards, int viewHost) {
	LinearLayout layout = (LinearLayout) context.findViewById(viewHost);

	// 生成图像框
	ImageView imageView = new ImageView(context);
	imageView.layout(0, 100, 0, 0);

	// 生产资源管理对象
	Resources res = context.getResources();
	// 用资源管理对象从资源目录的drawable中获得转型的图片

	Drawable[] layers = new Drawable[cards.size()];
	for (int i = 0; i < cards.size(); i++) {
	    layers[i] = res.getDrawable(Card.backDrawbleInt);
	}
	// 图片分层
	LayerDrawable layerDrawable = new LayerDrawable(layers);
	// 错开一点
	for (int i = 0; i < cards.size(); i++) {
	    layerDrawable.setLayerInset(i, i * 2, 100, 0, 0);
	}

	// 图片显示对象加入图片对象
	imageView.setImageDrawable(layerDrawable);

	AnimationSet animationSet = new AnimationSet(true);
	AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
	alphaAnimation.setDuration(2000);
	alphaAnimation.setStartOffset(1000);
	animationSet.addAnimation(alphaAnimation);
	// animationSet.setStartOffset(10000);
	animationSet.setFillBefore(false);
	animationSet.setFillAfter(true);
	imageView.startAnimation(animationSet);

	layout.addView(imageView);


    }

    @Override
    public void showOutCardOnTable(Activity context, List<ICard> outCards) {

	LinearLayout center_robot = (LinearLayout) context.findViewById(R.id.center_robot);
	LinearLayout center_human = (LinearLayout) context.findViewById(R.id.center_human);
	center_robot.setVisibility(View.VISIBLE);
	center_human.setVisibility(View.VISIBLE);
	
	RobotPlayer robot = RobotPlayer.getInstance(AIPlay.getInstance());
	HumanPlayer human = HumanPlayer.getInstance(GeneralPlay.getInstance());
	boolean hasCard = outCards != null && outCards.size() > 0;
	if (hasCard) {
	    Player player = outCards.get(0).getPlayer();
	    boolean isHuman = player.equals(human);
	    if (isHuman) {
		ShowTableHuman showTableHuman = new ShowTableHuman();
		showTableHuman.setCards(outCards);
		context.getFragmentManager().beginTransaction().replace(R.id.center_human, showTableHuman, "showTableHuman").commit();
	    }
	    boolean isRobot = player.equals(robot);
	    if (isRobot) {
		ShowTableRobot showTableRobot = new ShowTableRobot();
		showTableRobot.setCards(outCards);
		context.getFragmentManager().beginTransaction().replace(R.id.center_robot, showTableRobot, "showTableRobot").commit();
	    }
	}
    }

    @Override
    public List<ICard> sortCardsAfterShowingTrumpSuit(List<ICard> myCardsFetched) {
	List<ICard> result = new ArrayList<ICard>();
	// 六大掉与副牌分开
	List<ICard> trumps = new ArrayList<ICard>();
	List<ICard> noTrumps = new ArrayList<ICard>();

	List<ICard> jokers = new ArrayList<ICard>();
	List<ICard> classpointcards = new ArrayList<ICard>();
	List<ICard> card2s = new ArrayList<ICard>();
	
	boolean hasFetchedCards = (myCardsFetched != null && myCardsFetched.size() > 0);
	if (hasFetchedCards) {
	    for (ICard iCard : myCardsFetched) {
		    boolean joker=iCard.getSuit().equals("joker") ;
		    boolean card2=iCard.getPoint() == 2&&!iCard.getSuit().equals("joker");
		    boolean classPoint=iCard.getPoint() == recorder.getCurrentClassPoint()&&!iCard.getSuit().equals("joker");
		    boolean otherTrump= iCard.getSuit().equals(recorder.getCurrentTrumpSuit());
		    boolean trump =joker||card2||classPoint||otherTrump;
		if (trump) {
		    trumps.add(iCard);
		} else {
		    noTrumps.add(iCard);
		}
	    }
	}
	for (ICard iCard : trumps) {
	    boolean joker= iCard.getSuit().equals("joker");
	    if (joker) {
		jokers.add(iCard);
	    }
	}
	for (ICard iCard : trumps) {
	    boolean classPoint=iCard.getPoint() == recorder.getCurrentClassPoint()&&!iCard.getSuit().equals("joker");
	    if (classPoint) {
		classpointcards.add(iCard);
	    }
	}
	for (ICard iCard : trumps) {
	    boolean card2=  iCard.getPoint() == 2 &&!iCard.getSuit().equals("joker");
	    if (card2) {
		card2s.add(iCard);
	    }
	}
	trumps.removeAll(jokers);
	trumps.removeAll(classpointcards);
	trumps.removeAll(card2s);

	// 分花色
	List<ICard> club = this.seperateNoTrumpSameSuitCards(noTrumps, "club");
	List<ICard> diamond = this.seperateNoTrumpSameSuitCards(noTrumps, "diamond");
	List<ICard> heart = this.seperateNoTrumpSameSuitCards(noTrumps, "heart");
	List<ICard> spade = this.seperateNoTrumpSameSuitCards(noTrumps, "spade");
	// 排序，封装
	if (club != null) {
	    Collections.sort(club);
	    this.swapAceFromTopToBottomForOneSuitCards(club);
	    result.addAll(club);
	}
	if (diamond != null) {
	    Collections.sort(diamond);
	    this.swapAceFromTopToBottomForOneSuitCards(diamond);
	    result.addAll(diamond);
	}
	if (heart != null) {
	    Collections.sort(heart);
	    this.swapAceFromTopToBottomForOneSuitCards(heart);
	    result.addAll(heart);
	}
	if (spade != null) {
	    Collections.sort(spade);
	    this.swapAceFromTopToBottomForOneSuitCards(spade);
	    result.addAll(spade);
	}
	if (jokers.size() > 0) {
	    Collections.sort(jokers);
	    result.addAll(jokers);
	}
	if (classpointcards.size() > 0) {
	    Collections.sort(classpointcards);
	    result.addAll(classpointcards);
	}
	if (card2s.size() > 0) {
	    Collections.sort(card2s);
	    result.addAll(card2s);
	}

	if (trumps.size() > 0) {
	    Collections.sort(trumps);
	    this.swapAceFromTopToBottomForOneSuitCards(trumps);
	    result.addAll(trumps);
	}
	return result;
    }

    @Override
    public List<ICard> sortCardsBeforeShowingTrumpSuit(List<ICard> myCardsFetched) {
	List<ICard> result = new ArrayList<ICard>();
	// 六大掉与副牌分开
	List<ICard> trumps = new ArrayList<ICard>();
	List<ICard> noTrumps = new ArrayList<ICard>();
	
	List<ICard> jokers = new ArrayList<ICard>();
	List<ICard> classpointcards = new ArrayList<ICard>();
	List<ICard> card2s = new ArrayList<ICard>();
	
	for (ICard iCard : myCardsFetched) {
	    boolean joker=iCard.getSuit().equals("joker") ;
	    boolean card2=iCard.getPoint() == 2&&!iCard.getSuit().equals("joker");
	    boolean classPoint=iCard.getPoint() == recorder.getCurrentClassPoint()&&!iCard.getSuit().equals("joker");
	    boolean sixTopTrumps =joker||card2||classPoint;
	    if (sixTopTrumps) {
		trumps.add(iCard);
	    } else {
		noTrumps.add(iCard);
	    }
	}
	for (ICard iCard : trumps) {
	    boolean joker= iCard.getSuit().equals("joker");
	    if (joker) {
		jokers.add(iCard);
	    }
	}
	for (ICard iCard : trumps) {
	    boolean classPoint=iCard.getPoint() == recorder.getCurrentClassPoint()&&!iCard.getSuit().equals("joker");
	    if (classPoint) {
		classpointcards.add(iCard);
	    }
	}
	for (ICard iCard : trumps) {
	    boolean card2=  iCard.getPoint() == 2&&!iCard.getSuit().equals("joker");
	    if (card2) {
		card2s.add(iCard);
	    }
	}
	
	
	// 分花色
	List<ICard> club = this.seperateNoTrumpSameSuitCards(noTrumps, "club");
	List<ICard> diamond = this.seperateNoTrumpSameSuitCards(noTrumps, "diamond");
	List<ICard> heart = this.seperateNoTrumpSameSuitCards(noTrumps, "heart");
	List<ICard> spade = this.seperateNoTrumpSameSuitCards(noTrumps, "spade");
	// 排序，封装
	if (club != null) {
	    Collections.sort(club);
	    this.swapAceFromTopToBottomForOneSuitCards(club);
	    result.addAll(club);
	}
	if (diamond != null) {
	    Collections.sort(diamond);
	    this.swapAceFromTopToBottomForOneSuitCards(diamond);
	    result.addAll(diamond);
	}
	if (heart != null) {
	    Collections.sort(heart);
	    this.swapAceFromTopToBottomForOneSuitCards(heart);
	    result.addAll(heart);
	}
	if (spade != null) {
	    Collections.sort(spade);
	    this.swapAceFromTopToBottomForOneSuitCards(spade);
	    result.addAll(spade);
	}
	if (jokers.size() > 0) {
	    Collections.sort(jokers);
	    result.addAll(jokers);
	}
	if (classpointcards.size() > 0) {
	    Collections.sort(classpointcards);
	    result.addAll(classpointcards);
	}
	if (card2s.size() > 0) {
	    Collections.sort(card2s);
	    result.addAll(card2s);
	}

	return result;
    }

    @Override
    public Stack<ICard> sortMirrorTrumpCards() {
	ICardDeck cardDeck = CardDeck.getInstance();
	IRecorder recorder = Recorder.getInstance();
	String trumpsuit = recorder.getCurrentTrumpSuit();
	int trumppoint = recorder.getCurrentClassPoint();

	// 生产一个全主的stack,此时为攻防期，主牌定了。
	Stack<ICard> trumpStackOrdered = new Stack<ICard>();
	List<ICard> allCards = cardDeck.getCardsMirror();
	// 先找小牌
	List<ICard> currentTrumpSuitCards = new ArrayList<ICard>();
	for (ICard card : allCards) {
	    if (!card.getSuit().equals("joke") && card.getPoint() != 2 && card.getPoint() != trumppoint && card.getPoint() != 1 && card.getSuit().equals(trumpsuit)) {
		currentTrumpSuitCards.add(card);
	    }
	}
	// 排小牌序--看升降
	Collections.sort(currentTrumpSuitCards);

	// 压小牌先
	for (ICard card : currentTrumpSuitCards) {
	    trumpStackOrdered.push(card);
	}
	// 压ACE
	for (ICard card : allCards) {
	    if (card.getSuit().equals(trumpsuit) && card.getPoint() == 1 && trumppoint != 1) {// Ace要独立处理一下
		trumpStackOrdered.push(card);
	    }
	}
	// 压2
	for (ICard card : allCards) {
	    if (!card.getSuit().equals("joker") && card.getPoint() == 2) {
		trumpStackOrdered.push(card);
	    }
	}
	// 压将牌
	for (ICard card : allCards) {
	    if (!card.getSuit().equals("joker") && card.getPoint() == trumppoint) {
		trumpStackOrdered.push(card);
	    }
	}
	// 压小王
	for (ICard card : allCards) {
	    if (card.getSuit().equals("joker") && card.getPoint() == 2) {
		trumpStackOrdered.push(card);
	    }
	}
	// 压大王
	for (ICard card : allCards) {
	    if (card.getSuit().equals("joker") && card.getPoint() == 1) {
		trumpStackOrdered.push(card);
	    }
	}

	return trumpStackOrdered;
    }

    @Override
    public List<ICard> sortNoTrumpSameSuitInHandDesc(List<ICard> noTrumpSameSuitCardsInHand) {
	List<ICard> same4ReturnSortedSuitCard = new ArrayList<ICard>();

	List<ICard> sameSortedSuitCard = new ArrayList<ICard>();
	sameSortedSuitCard.addAll(noTrumpSameSuitCardsInHand);
	// 主要关心ACE
	Collections.sort(sameSortedSuitCard);
	Collections.reverse(sameSortedSuitCard);
	// 如果集合中有ace，要将其串到第一个上。
	// 先拿出来
	ICard aceCard = null;

	for (ICard iCard : sameSortedSuitCard) {
	    boolean ace = (iCard.getPoint() == 1);
	    if (ace) {
		aceCard = iCard;
		sameSortedSuitCard.remove(iCard);
		break;

	    }
	}

	boolean aceNull = (aceCard == null);
	if (aceNull) {
	    same4ReturnSortedSuitCard.addAll(sameSortedSuitCard);
	} else {
	    same4ReturnSortedSuitCard.add(aceCard);
	    same4ReturnSortedSuitCard.addAll(sameSortedSuitCard);
	}

	return same4ReturnSortedSuitCard;
    }

    @Override
    public List<String> suitList(List<ICard> cards) {
	List<String> suitList = new ArrayList<String>();

	for (ICard card : cards) {
	    if (!suitList.contains(card.getSuit())) {
		suitList.add(card.getSuit());
	    }

	}

	return suitList;
    }

    @Override
    public int sumScores(List<ICard> cards) {
	int score = 0;
	// 1.比较IGAMERULE中分数定义，加和分数
	for (ICard iCard : cards) {
	    boolean five = (iCard.getPoint() == 5);
	    boolean ten = (iCard.getPoint() == 10);
	    boolean thirteen = (iCard.getPoint() == 13);
	    score += five ? IGameRule.CARD_5_EQUAL_SCORE : 0;
	    score += ten ? IGameRule.CARD_10_EQUAL_SCORE : 0;
	    score += thirteen ? IGameRule.CARD_13_EQUAL_SCORE : 0;
	}
	return score;
    }

    @Override
    public void swapAceFromTopToBottomForOneSuitCards(List<ICard> cardsWithAce) {
	boolean noEmpty = !cardsWithAce.isEmpty();
	if (noEmpty) {
	    A: for (ICard iCard : cardsWithAce) {
		boolean hasAce = (iCard.getPoint() == 1);
		if (hasAce) {
		    // 分解成两部分
		    List<ICard> tempLst = new ArrayList<ICard>();
		    cardsWithAce.remove(iCard);
		    tempLst.addAll(cardsWithAce);

		    // 清空原数据
		    cardsWithAce.clear();
		    // 重新放入
		    cardsWithAce.addAll(tempLst);
		    cardsWithAce.add(iCard);
		    break A;
		}
	    }
	}

    }

    public ICard topSuitCard(List<ICard> cards) {
	ICard topCard = cards.get(0);
	if (cards.size() > 0) {
	    for (ICard card : cards) {
		if (card.getPoint() == 1) {
		    topCard = card;// Ace 大
		    break;
		} else if (topCard.getPoint() < card.getPoint()) {
		    topCard = card;
		}
	    }
	}
	return topCard;
    }

    @Override
    public void informRobot_HumanTrumpStatus(List<ICard> foreCards, List<ICard> humanPutOutCards) {
	RobotPlayer robot = RobotPlayer.getInstance(AIPlay.getInstance());
	// TODO 
	//先看单多张
	boolean onecard=foreCards.size()==1&&humanPutOutCards.size()==1;
	if (onecard) {
	    //先主，人类跟牌无主
	    boolean foreTrump=!this.seperateTrumpsFromCards(foreCards, recorder).isEmpty();
	    boolean afterNotTrump=this.seperateTrumpsFromCards(foreCards, recorder).isEmpty();
	    if (foreTrump&&afterNotTrump) {
		robot.actions().setOpponentTrump(false);
	    }
	} else {
	    //人类跟牌非翁且主不全
	    boolean fourSamePoint=!this.seperate4SamePointCards(foreCards).isEmpty();
	    boolean afterNotSamePoint=this.seperate4SamePointCards(humanPutOutCards).isEmpty();
	    boolean notAllTrump=humanPutOutCards.size()>this.seperateTrumpsFromCards(humanPutOutCards, recorder).size();
	    if (fourSamePoint&&afterNotSamePoint&&notAllTrump) {
		robot.actions().setOpponentTrump(false);
	    }
	}
	
    }

}
