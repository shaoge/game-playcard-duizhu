package com.formum.duizhu.gameRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.util.Log;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.data.Data4Human;
import com.formum.duizhu.data.Data4Robot;
import com.formum.duizhu.player.AIPlay;
import com.formum.duizhu.player.GeneralPlay;
import com.formum.duizhu.player.HumanPlayer;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.player.RobotPlayer;
import com.formum.duizhu.recorder.IRecorder;
import com.formum.duizhu.recorder.Recorder;
import com.formum.duizhu.util.IUtil;
import com.formum.duizhu.util.Util;

public class GameRule implements IGameRule {

    private static volatile GameRule INSTANCE = null;
    private IUtil tools = Util.getInstance();
    private IRecorder recorder = Recorder.getInstance();

    private GameRule() {

    }

    public static GameRule getInstance() {
	if (GameRule.INSTANCE == null) {
	    synchronized (GameRule.class) {
		if (GameRule.INSTANCE == null) {
		    return new GameRule();
		}
	    }
	}
	return INSTANCE;
    }

    @Override
    public boolean isShowingCard4ConfirmTrumpSuitRight(ICard cardShowedByplayer) {
	boolean result = false;
	int banker = tools.isThereBanker();// 有无庄家状态
	switch (banker) {
	case 0:
	    // 无庄家局（开始局）: 1、不能亮王； 2、不能亮2；3、亮牌必须为3； ----此牌通过

	    boolean suit0 = !cardShowedByplayer.getSuit().equals("joker");
	    boolean point0 = (cardShowedByplayer.getPoint() != 2);
	    boolean bankerClassPoint0 = (3 == cardShowedByplayer.getPoint());
	    if (suit0 && point0 && bankerClassPoint0) {
		result = true;
	    }
	    break;
	case 1:
	    // 有庄家局: 1、不能亮王； 2、不能亮2；3、亮牌必须与庄家打到的级数相等； ----此牌通过
	    boolean suit = !cardShowedByplayer.getSuit().equals("joker");
	    boolean point = (cardShowedByplayer.getPoint() != 2);
	    boolean bankerClassPoint = (tools.capableShowingClassPoint() == cardShowedByplayer.getPoint());
	    if (suit && point && bankerClassPoint) {
		result = true;
	    }
	    break;
	}
	return result;
    }

    @Override
    public boolean isThereTrumpSuit(IRecorder recorder) {
	// 攻守前，要确定是否有将牌。每次打完一局要清空的属性，抓牌过程中可以亮主出将牌
	boolean trumpsuit = recorder.getCurrentTrumpSuit().equals("") || recorder.getCurrentTrumpSuit().equals("joker");
	if (trumpsuit) {
	    return false;
	} else {
	    return true;
	}

    }

    @Override
    public boolean is4SamePointCardsAsTrumpCards(List<ICard> cards) {
	boolean result = false;
	// 翁当主用： 1、必须四张牌 2、每种花色各一 3、点数必须相等 4、花色不能为joker
	boolean size4 = (cards.size() == 4);
	boolean eachsuit = tools.noRepeatSuit(cards);
	boolean samepoint = tools.eachCardSamePoint(cards);
	boolean nojoker = !tools.isthereJoker(cards);
	if (size4 && eachsuit && samepoint && nojoker) {
	    result = true;
	}
	return result;
    }

    @Override
    public boolean isRight2PreSameNonTrumpSuitGroupOut(List<ICard> cards, Player opponentPlayer) {
	boolean result = true;
	// 1、数量大于一张； 2、非同花色副牌不得混出； 3、甩牌中没有比对方小的牌;

	boolean sizeMoreThan1 = (cards.size() > 1);
	if (sizeMoreThan1) {
	    System.out.println("@GameRule isRight2PreSameNonTrumpSuitGroupOut sizeMoreThan1--->" + sizeMoreThan1);
	    boolean sameSuit = tools.isSameNoTrumpCardsSuit(cards);
	    if (sameSuit) {
		System.out.println("@GameRule isRight2PreSameNonTrumpSuitGroupOut sameSuit--->" + sameSuit);
		// 对方手牌
		List<ICard> opponentCards = new ArrayList<ICard>();
		boolean robot = opponentPlayer.equals(RobotPlayer.getInstance(AIPlay.getInstance()));
		if (robot) {
		    opponentCards.addAll(Data4Robot.getInstance().getRobotHoldingCards());
		} else {
		    opponentCards.addAll(Data4Human.getInstance().getRobotHoldingCards());
		}

		// 对方与我同花色副牌
		List<ICard> opponentSameSuitCards = tools.seperateNoTrumpSameSuitCards(opponentCards, cards.get(0).getSuit());

		// 比较有没有比自己大的同花色副牌
		// Ace是主牌
		boolean aceIsClassPointTrump = recorder.getCurrentClassPoint() == 1;
		if (aceIsClassPointTrump) {
		    System.out.println("@GameRule isRight2PreSameNonTrumpSuitGroupOut aceIsClassPointTrump--->" + aceIsClassPointTrump);
		    // 副牌用点数可以比较
		    A: for (ICard iCard : opponentSameSuitCards) {
			for (ICard icard : cards) {
			    boolean bigger = iCard.getPoint() > icard.getPoint();
			    if (bigger) {
				result = false;
				break A;
			    }
			}
		    }
		} else {
		    // 对方如有同花Ace
		    for (ICard iCard : opponentSameSuitCards) {
			boolean aceInSide = iCard.getPoint() == 1;
			if (aceInSide) {
			    System.out.println("@GameRule isRight2PreSameNonTrumpSuitGroupOut aceInSide--->" + aceInSide);
			    result = false;
			    break;
			}
		    }
		    boolean resultStillTrue = result == true;
		    if (resultStillTrue) {
			System.out.println("@GameRule isRight2PreSameNonTrumpSuitGroupOut resultStillTrue--->" + resultStillTrue);
			// 副牌用点数可以比较;先排除手中ACE
			List<ICard> tempMyCards = new ArrayList<ICard>();
			tempMyCards.addAll(cards);
			for (ICard iCard : cards) {
			    boolean ace = iCard.getPoint() == 1;
			    if (ace) {
				tempMyCards.remove(iCard);
				break;
			    }
			}
			B: for (ICard iCard : opponentSameSuitCards) {
			    for (ICard icard : tempMyCards) {
				boolean bigger = iCard.getPoint() > icard.getPoint();
				if (bigger) {
				    result = false;
				    break B;
				}
			    }
			}
		    }
		}
	    } else {
		result = false;
	    }

	} else {
	    result = false;
	}

	return result;
    }

    @Override
    public boolean isRight2PreTrumpGroupOut(List<ICard> cards) {
	boolean result = false;
	// 1、四张牌；2、将牌翁；3、是2翁
	boolean size4 = (cards.size() == 4);
	boolean classpoints = (tools.isSameClassPoint(cards));
	boolean card2 = (tools.isSameCard2(cards));
	if (size4) {
	    if (classpoints || card2) {
		result = true;
	    }
	}

	return result;
    }

    @Override
    public Player whoIsRoundWinner(List<ICard> offensiveCard, List<ICard> defensiveCard) {
	
	Log.v("whoIsRoundWinner", "whoIsRoundWinner    Robot getSelfCardDeck().size() "+RobotPlayer.getInstance(AIPlay.getInstance()).actions().getSelfCardDeck().size());
	Log.v("whoIsRoundWinner", "whoIsRoundWinner    Human getSelfCardDeck().size() "+HumanPlayer.getInstance(GeneralPlay.getInstance()).actions().getSelfCardDeck().size());
	Log.v("whoIsRoundWinner", "whoIsRoundWinner    recorder cardOntable.size() "+recorder.getCardsOnTable().size());
	
	Player player = null;

	boolean bothCards = offensiveCard.size() > 0 && defensiveCard.size() > 0;
	if (bothCards) {
	    // 捕捉机器人的幽灵牌
	    catchGhostCards(offensiveCard, defensiveCard);
	    
	    //基本条件
	    boolean someSize = offensiveCard.size() == defensiveCard.size();
	    boolean bothMorethanOne = (offensiveCard.size() > 0 && defensiveCard.size() > 0);
	    boolean baseOk = someSize && bothMorethanOne;
	    if (baseOk) {
		// 单张
		boolean oneCard = offensiveCard.size() == 1;
		if (oneCard) {
		    boolean offensiveTrump=tools.isAllTrumpCards(offensiveCard);
		    if (offensiveTrump) {
			//先手是主
			boolean defensiveTrump=tools.isAllTrumpCards(defensiveCard);
			if (defensiveTrump) {
			    //后手是主
				player = trump_1_VS_1(offensiveCard, defensiveCard, player);
			} else {
			    //后手是副
			    player = offensiveCard.get(0).getPlayer();
			}
		    } else {
			//先手是副
			boolean defensiveTrump=tools.isAllTrumpCards(defensiveCard);
			if (defensiveTrump) {
			    //后手是主
			    player = defensiveCard.get(0).getPlayer();
			} else {
			    //后手是副
				player = nonTrump_1_VS_1(offensiveCard, defensiveCard, player);
			}
		    }
		} else {
		    // 多张
		    boolean offensiveFourSamePoint=tools.seperate4SamePointCards(offensiveCard).size()==4;
		    if (offensiveFourSamePoint) {
			//先手翁
			boolean defensiveFourSamePoint=tools.seperate4SamePointCards(defensiveCard).size()==4;
			if (defensiveFourSamePoint) {
			    //后手翁
				player = four_VS_four(offensiveCard, defensiveCard, player);
			} else {
			    //后手非翁
			    player = offensiveCard.get(0).getPlayer();
			}
		    } else {
			//先手非翁，只能是甩牌
			boolean defensiveAllTrump=tools.isAllTrumpCards(defensiveCard);
			if (defensiveAllTrump) {
			    //后手全是主牌
			    player = defensiveCard.get(0).getPlayer();
			} else {
			    //后手不全是主
			    player = offensiveCard.get(0).getPlayer();
			    
			}
		    }
		}
	    }
	}

	return player;
    }
/**
 * 翁对翁PK
 * @param offensiveCard
 * @param defensiveCard
 * @param player
 * @return
 */
    private Player four_VS_four(List<ICard> offensiveCard, List<ICard> defensiveCard, Player player) {
	// 各选一张牌，比PK点，将两种点转成PK点
	int offPkPoint = turnIntoPKPoint(offensiveCard);
	int defPkPoint = turnIntoPKPoint(defensiveCard);
	boolean offbigger = offPkPoint > defPkPoint;
	if (offbigger) {
	    player = offensiveCard.get(0).getPlayer();
	} else {
	    player = defensiveCard.get(0).getPlayer();
	}
	return player;
    }
/**
 * 单牌主对主PK
 * @param offensiveCard
 * @param defensiveCard
 * @param player
 * @return
 */
    private Player trump_1_VS_1(List<ICard> offensiveCard, List<ICard> defensiveCard, Player player) {
	//主对主
	// 比PK点，将两种点转成PK点
	int offPkPoint = turnIntoPKPoint(offensiveCard);
	int defPkPoint = turnIntoPKPoint(defensiveCard);
	
	boolean equal = (offPkPoint == defPkPoint);// 小二和将点牌的情况即是
	if (equal) {
	    player = offensiveCard.get(0).getPlayer();// 前手胜后手
	} else {
	    boolean offbigger = (offPkPoint > defPkPoint);//前手大
	    if (offbigger) {
		 player = offensiveCard.get(0).getPlayer();
	    } else {
		 player = defensiveCard.get(0).getPlayer();
	    }
	}
	return player;
    }
/**
 * 副牌单牌PK
 * @param offensiveCard
 * @param defensiveCard
 * @param player
 * @return
 */
    private Player nonTrump_1_VS_1(List<ICard> offensiveCard, List<ICard> defensiveCard, Player player) {
	// 副对副
	boolean sameSuit=offensiveCard.get(0).getSuit().equals(defensiveCard.get(0).getSuit());
	if (sameSuit) {
	    //同花色可以比大小
		// 比PK点， 将两种点转成PK点
		// 有阶的要挑出来
		int offPkPoint = turnIntoPKPoint(offensiveCard);
		int defPkPoint = turnIntoPKPoint(defensiveCard);
	    boolean offbigger = offPkPoint > defPkPoint;//先手大
	    if (offbigger) {
		player = offensiveCard.get(0).getPlayer();
	    } else {
		player = defensiveCard.get(0).getPlayer();
	    }
	} else {
	   //异花色，不可比，先出的胜
	    player = offensiveCard.get(0).getPlayer();
	}
	return player;
    }
/**
 * 根据点数或阶数转成PK点
 * @param card
 * @return
 */
private int turnIntoPKPoint(List<ICard> card) {
    int pkPoint;
    boolean hasRank = card.get(0).getTrumpRank() != 0;
    if (hasRank) {
	pkPoint = card.get(0).getTrumpRank();
    } else {
	pkPoint = card.get(0).getPoint();
    }
    return pkPoint;
}

    /**
     * 捕捉机器人可能丢失的幽灵牌
     * 
     * @param offensiveCard
     * @param defensiveCard
     */
    private void catchGhostCards(List<ICard> offensiveCard, List<ICard> defensiveCard) {
	Data4Robot data4Robot = Data4Robot.getInstance();
	List<ICard> robotRealThroughJugeCards = data4Robot.getRobotRealThroughJugeCards();

	boolean isrobot = offensiveCard.get(0).getPlayer().getName().equals("Robot");
	if (isrobot) {
	    List<ICard> temp = new ArrayList<ICard>();
	    temp.addAll(offensiveCard);
	    boolean notNull = robotRealThroughJugeCards != null;
	    if (notNull) {
		temp.addAll(robotRealThroughJugeCards);
	    }

	    HashSet h = new HashSet(temp);
	    temp.clear();
	    temp.addAll(h);
	    data4Robot.setRobotRealThroughJugeCards(temp);

	    data4Robot.catchAndReSendGhostCards4Robot(offensiveCard);
	} else {
	    List<ICard> temp = new ArrayList<ICard>();
	    temp.addAll(defensiveCard);
	    boolean notNull = robotRealThroughJugeCards != null;
	    if (notNull) {
		temp.addAll(robotRealThroughJugeCards);
	    }

	    HashSet h = new HashSet(temp);
	    temp.clear();
	    temp.addAll(h);
	    data4Robot.setRobotRealThroughJugeCards(temp);

	    data4Robot.catchAndReSendGhostCards4Robot(defensiveCard);
	}
    }

    @Override
    public Player whoIsSetWinner(Player roundWinner) {
	// TODO:显示双方级数
	System.out.println("@GameRule whoIsSetWinner showHumanClassPoint---->" + HumanPlayer.getInstance(GeneralPlay.getInstance()).actions().getMyClassPoint());
	System.out.println("@GameRule whoIsSetWinner showRobotClassPoint---->" + RobotPlayer.getInstance(AIPlay.getInstance()).actions().getMyClassPoint());

	Player setWinner = null;
	// 双方牌打完启动
	boolean bothShowHands = recorder.getCardsOnTable().size() == IGameRule.PLAYER_CARDS_SIZE * 2;
	if (bothShowHands) {
	    Player banker = recorder.getCurrentBanker();
	    boolean isBankerWin = banker.equals(roundWinner);

	    if (isBankerWin) {
		// 庄家：底胜/不破分胜家，但升的级不一样
		boolean scoreUnderline = recorder.getSetScore() < IGameRule.BREAK_SET_SCORES_BOTTOM_LINE;
		boolean moreThan0 = recorder.getSetScore() > 0;
		boolean notBreaking = scoreUnderline && moreThan0;
		if (notBreaking) {
		    // 没破分
		    int initScore = banker.actions().getMyClassPoint();
		    boolean initZero = (initScore == 0);
		    if (initZero) {
			// 首次成牌升到4级
			int myClassPoint = IGameRule.INITIAL_CLASSPOINT + 1;
			banker.actions().setMyClassPoint(limitToAce(myClassPoint));
		    } else {
			// 继续增升1级
			int myClassPoint = initScore + 1;
			banker.actions().setMyClassPoint(limitToAce(myClassPoint));
		    }
		    setWinner = banker;
		}
		boolean scoreNoAtAll = recorder.getSetScore() == 0;
		if (scoreNoAtAll) {
		    // 首次光牌升到5级
		    int initScore = banker.actions().getMyClassPoint();
		    boolean initZero = (initScore == 0);
		    if (initZero) {
			int myClassPoint = IGameRule.INITIAL_CLASSPOINT + 2;
			banker.actions().setMyClassPoint(limitToAce(myClassPoint));
		    } else {
			// 光牌增升2级
			int myClassPoint = initScore + 2;
			banker.actions().setMyClassPoint(limitToAce(myClassPoint));
		    }
		    setWinner = banker;
		}
		boolean scoreBreakLine = recorder.getSetScore() >= IGameRule.BREAK_SET_SCORES_BOTTOM_LINE;
		if (scoreBreakLine) {
		    // 守底但破分
		    List<Player> players = recorder.getPlayerRegistered();
		    Player player = RobotPlayer.getInstance(AIPlay.getInstance());// 临时的
		    for (Player thePlayer : players) {
			boolean isPlayer = !thePlayer.equals(banker);
			if (isPlayer) {
			    player = thePlayer;
			    break;
			}
		    }
		    boolean initZero = (player.actions().getMyClassPoint() == 0);
		    if (initZero) {
			int myClassPoint = 0;
			player.actions().setMyClassPoint(myClassPoint);
		    } else {
			int myClassPoint = player.actions().getMyClassPoint() + 1;
			player.actions().setMyClassPoint(limitToAce(myClassPoint));
		    }
		    setWinner = player;
		}

	    } else {
		// 闲家: 底胜或破分都是胜家，但升的级不一样
		Player player = roundWinner;

		boolean scoreUnderline = recorder.getSetScore() < IGameRule.BREAK_SET_SCORES_BOTTOM_LINE;
		if (scoreUnderline) {
		    int initScore = player.actions().getMyClassPoint();
		    boolean initZero = (initScore == 0);
		    if (initZero) {
			// 没有级数，系统将分配3级
			int myClassPoint = 0;
			player.actions().setMyClassPoint(myClassPoint);
		    } else {
			int myClassPoint = initScore + 1;
			player.actions().setMyClassPoint(limitToAce(myClassPoint));
		    }
		    setWinner = player;
		} else {
		    int initScore = player.actions().getMyClassPoint();
		    boolean initZero = (initScore == 0);
		    if (initZero) {
			// 没有级数，系统将分配3级
			int myClassPoint = 0;
			player.actions().setMyClassPoint(myClassPoint);
		    } else {
			int myClassPoint = initScore + 2;
			player.actions().setMyClassPoint(limitToAce(myClassPoint));
		    }
		    setWinner = player;
		}
	    }
	}
	// TODO:显示双方级数
	System.out.println("@GameRule whoIsSetWinner showHumanClassPoint---->" + HumanPlayer.getInstance(GeneralPlay.getInstance()).actions().getMyClassPoint());
	System.out.println("@GameRule whoIsSetWinner showRobotClassPoint---->" + RobotPlayer.getInstance(AIPlay.getInstance()).actions().getMyClassPoint());
	return setWinner;
    }

    /**
     * 限制打点数超过14--ACE的值
     * 
     * @param myClassPoint
     * @return
     */
    private int limitToAce(int myClassPoint) {
	int result = 0;
	boolean ace = myClassPoint > 13;
	if (ace) {
	    result = 1;
	} else {
	    result = myClassPoint;
	}
	return result;
    }

    @Override
    public Player whoIsGameWinner(Player setWinner) {
	Player gameWinner = null;

	// 1 、局胜家是庄家；2、将牌点ace
	boolean hasSetWinner = setWinner != null;
	if (hasSetWinner) {
	    boolean isWinnerBanker = setWinner.actions().isBanker();
	    System.out.println("@GameRule whoIsGameWinner()--isWinnerBanker-->" + isWinnerBanker);
	    boolean twiceAce = setWinner.actions().getMyClassPoint() == 100;
	    System.out.println("@GameRule whoIsGameWinner()--twiceAce-->" + twiceAce);
	    if (isWinnerBanker && twiceAce) {
		gameWinner = setWinner;
	    }
	}

	return gameWinner;
    }

    @Override
    public boolean isRight2FollowOneTrumpCardOut(List<ICard> foreCards, List<ICard> afterCards) {
	boolean result = false;
	boolean oneCard = afterCards.size() == 1;
	boolean equalSize = tools.isArraySizeEqual(foreCards, afterCards);
	boolean foreCardsIsTrump = tools.seperateTrumpsFromCards(foreCards, recorder).size() > 0;
	boolean preOk = oneCard && equalSize && foreCardsIsTrump;
	if (preOk) {
	    List<ICard> afterHandSelfCardDeck = afterCards.get(0).getPlayer().actions().getSelfCardDeck();
	    boolean afterHandHasTrump = !tools.seperateTrumpsFromCards(afterHandSelfCardDeck, recorder).isEmpty();// 跟牌方有主
	    if (afterHandHasTrump) {
		// 有主并出主
		boolean trump = !tools.seperateTrumpsFromCards(afterCards, recorder).isEmpty();
		if (trump) {
		    result = true;
		}
	    } else {
		// 没主，出什么都行
		result = true;
	    }

	}

	return result;
    }

    @Override
    public boolean isRight2FollowStraightGroupOut(List<ICard> foreCards, List<ICard> afterCards) {
	boolean result = false;
	boolean moreThanOne1 = foreCards.size() > 1;
	boolean moreThanOne2 = afterCards.size() > 1;
	boolean moreThan4 = moreThanOne1 && moreThanOne2;
	if (moreThan4) {
	    // 假定先手甩牌,后手花色杂，后手同类牌大于等于先手出牌数

	    String foreSuit = foreCards.get(0).getSuit();// 先手出的花色
	    Player afterPlayer = afterCards.get(0).getPlayer();// 后手牌玩家
	    List<ICard> selfCardDeck = new ArrayList<ICard>();
	    boolean robot = afterPlayer.equals(RobotPlayer.getInstance(AIPlay.getInstance()));
	    if (robot) {
		selfCardDeck.addAll(Data4Robot.getInstance().getRobotHoldingCards());
	    } else {
		selfCardDeck.addAll(Data4Human.getInstance().getRobotHoldingCards());
	    }
	    // 过滤掉主牌==纯副牌
	    List<ICard> noTrumpSameSuitCards = new ArrayList<ICard>();
	    if (selfCardDeck.size() > 0) {
		noTrumpSameSuitCards.addAll(tools.seperateNoTrumpSameSuitCards(selfCardDeck, foreSuit));
	    }

	    // 不 藏牌
	    boolean bothSameOrMore = noTrumpSameSuitCards.size() >= foreCards.size();
	    if (bothSameOrMore) {
		boolean contain = noTrumpSameSuitCards.containsAll(afterCards);
		if (contain) {
		    result = true;
		}
	    }
	    boolean morethan0 = (0 < noTrumpSameSuitCards.size() && noTrumpSameSuitCards.size() < foreCards.size());
	    if (morethan0) {
		boolean contain = afterCards.containsAll(noTrumpSameSuitCards);
		if (contain) {
		    result = true;
		}
	    }

	    boolean noSameSuit = noTrumpSameSuitCards.size() == 0;
	    if (noSameSuit) {
		result = true;
	    }
	}

	return result;
    }

    @Override
    public boolean isSameAfterSize(List<ICard> foreCards, List<ICard> afterCards) {
	boolean result = false;
	if (tools.isArraySizeEqual(foreCards, afterCards)) {
	    result = true;
	}
	return result;
    }

    @Override
    public boolean mayReDealCards(Player player) {
	// 1.按规定牌达数量标准；2.手中牌一分没有；3.身份是分家
	boolean size = player.actions().countCards() == IGameRule.PLAYER_CARDS_SIZE;
	boolean empty = (tools.sumScores(player.actions().getSelfCardDeck()) == 0);
	boolean notbanker = !player.actions().isBanker();
	boolean allTrue = size && empty & notbanker;
	boolean result = allTrue ? true : false;
	return result;
    }

    @Override
    public boolean mayIPutOutCards(Player me) {
	// 常规情况：上轮胜家，对家已经出牌；特别情况：开局还是庄家
	boolean result = false;

	// 首张牌:必须是庄家;

	boolean noCardsOnTable = recorder.getCardsOnTable().size() == 0;
	if (noCardsOnTable) {
	    boolean banker = recorder.getCurrentBanker().equals(me);
	    if (banker) {
		result = true;
	    }
	} else {
	    boolean hasRoundWinner = recorder.getRoundWinner() != null;
	    if (hasRoundWinner) {
		boolean isMe = recorder.getRoundWinner().equals(me);
		if (isMe) {
		    result = true;
		}
	    }

	    boolean hasOutRecorder = recorder.getBothOutCards().size() == 1;
	    if (hasOutRecorder) {
		boolean meFollower = recorder.getBothOutCards().get(0).get(me) == null;
		if (meFollower) {
		    result = true;
		}

	    }
	}

	return result;
    }

    @Override
    public boolean isRight2FollowOneNonTrumpCardOut(List<ICard> foreCards, List<ICard> afterCards) {
	boolean result = false;
	// 各是一张牌；先手是副牌
	boolean one = foreCards.size() == 1;
	boolean eachOne = foreCards.size() == afterCards.size();
	boolean foreCardsIsNotTrump = !tools.isAllTrumpCards(foreCards);
	boolean preOk = one && eachOne && foreCardsIsNotTrump;

	if (preOk) {
	    // 有同花副，跟牌同花；没同花，跟牌没有同花
	    boolean afterNoTrumpSameSuit = !tools.seperateNoTrumpSameSuitCards(afterCards.get(0).getPlayer().actions().getSelfCardDeck(), foreCards.get(0).getSuit()).isEmpty();
	    if (afterNoTrumpSameSuit) {
		boolean oneOf = tools.seperateNoTrumpSameSuitCards(afterCards.get(0).getPlayer().actions().getSelfCardDeck(), foreCards.get(0).getSuit()).containsAll(afterCards);
		if (oneOf) {
		    result = true;
		}
	    } else {
		result = true;
	    }
	}

	return result;
    }

    @Override
    public boolean isRight2Follow4SameCardsOut(List<ICard> foreCards, List<ICard> afterCards) {
	boolean result = false;
	// 如果前后都是翁，即可；否则，查看跟牌中主牌数是否小于手持主牌
	boolean foreCards4Same = !tools.seperate4SamePointCards(foreCards).isEmpty();
	if (foreCards4Same) {
	    boolean afterCards4Same = !tools.seperate4SamePointCards(afterCards).isEmpty();
	    if (afterCards4Same) {
		result = true;
	    } else {
		int afterAllTrumpSize = tools.seperateTrumpsFromCards(afterCards.get(0).getPlayer().actions().getSelfCardDeck(), recorder).size();
		int afterCardsSize = afterCards.size();
		int trumpInAfterCardsSize = tools.seperateTrumpsFromCards(afterCards, recorder).size();
		boolean enoughTrump = afterAllTrumpSize >= afterCardsSize;
		if (enoughTrump) {
		    boolean allTrump = afterCardsSize == trumpInAfterCardsSize;
		    if (allTrump) {
			result = true;
		    }
		} else {
		    // 主牌不足，但必须全出
		    boolean showTrumpHand = afterCards.containsAll(tools.seperateTrumpsFromCards(afterCards.get(0).getPlayer().actions().getSelfCardDeck(), recorder));
		    if (showTrumpHand) {
			result = true;
		    }
		}
	    }
	}

	return result;
    }

}
