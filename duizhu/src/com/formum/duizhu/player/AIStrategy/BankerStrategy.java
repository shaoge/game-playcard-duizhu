package com.formum.duizhu.player.AIStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import android.os.PowerManager;

import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.data.Data4Robot;
import com.formum.duizhu.gameRule.GameRule;
import com.formum.duizhu.gameRule.IGameRule;
import com.formum.duizhu.player.AIPlay;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.player.RobotPlayer;
import com.formum.duizhu.player.powerCardsBag.IPowerCardsBag;
import com.formum.duizhu.player.powerCardsBag.RobotPowerCardsBag;
import com.formum.duizhu.recorder.IRecorder;
import com.formum.duizhu.recorder.Recorder;
import com.formum.duizhu.util.IUtil;
import com.formum.duizhu.util.Util;

public class BankerStrategy implements IBankerStrategy {

    private static volatile BankerStrategy INSTANCE = null;
    private IUtil tools = Util.getInstance();
    private IPowerCardsBag robotPowerCardsBag = RobotPowerCardsBag.getInstance(RobotPlayer.getInstance(AIPlay.getInstance()));
    private Player robot = RobotPlayer.getInstance(AIPlay.getInstance());
    private IRecorder recorder = Recorder.getInstance();

    private BankerStrategy() {

    }

    public static BankerStrategy getInstance() {
	if (BankerStrategy.INSTANCE == null) {
	    synchronized (BankerStrategy.class) {
		if (BankerStrategy.INSTANCE == null) {
		    return new BankerStrategy();
		}
	    }
	}
	return INSTANCE;
    }

    @Override
    /**
     * * 已有的前提数据： 1、有没有保底牌 2、足量主牌数量（5张以上平均可保） 3、危险分数（弱牌中低于15分）
     * 4、弱牌数量（副牌三张以上--是对手破分机会）
     * 13张牌是初始静态决策.
     */
    public int makeStrategy(final List<ICard> cardsInHand) {

	int result = 0;
	result = initStrategy(cardsInHand, result);

	return result;
    }

    private int initStrategy(List<ICard> cardsInHand, int result) {
	boolean size13 = (cardsInHand.size() == IGameRule.PLAYER_CARDS_SIZE);
	if (size13) {
	    // 局胜窗口
	    boolean hasTopCard = !robotPowerCardsBag.getTopCard().isEmpty();// 有当家牌
	    boolean hasParityTrumpCards = ((robotPowerCardsBag.getOtherTrumpCards().size()) >= 4);// 平均主
	    boolean hasLongTrumpCards = (robotPowerCardsBag.getOtherTrumpCards().size() >= 7);// 主长趟（21张主，每人平均5.5张不到，7张以上算多了）
	    boolean hasCard4 = !(robotPowerCardsBag.getCards4().isEmpty());// 有翁

	    // 局胜分类
	    boolean setWin1 = hasTopCard && hasParityTrumpCards && hasCard4;// 相当于9张主
	    boolean setWin2 = !hasTopCard && hasParityTrumpCards && hasCard4;// 相当于8张主
	    boolean setWin3 = hasTopCard && hasLongTrumpCards;// 相当于8张主
	    boolean setWin4 = !hasTopCard && hasLongTrumpCards;// 相当于7张主
	    boolean setWin5 = hasTopCard && hasParityTrumpCards;// 王带4张主

	    // 任意一种都可以胜
	    boolean roundWinComboJoker = setWin1 || setWin3 || setWin5;
	    boolean roundWinCombo = setWin2 || setWin4;

	    // 破分窗口
	    boolean weakMoreThan3 = (robotPowerCardsBag.weakCards() >= 3); // 三个以上杂牌漏洞
	    boolean weakMoreThan2 = (robotPowerCardsBag.weakCards() == 2); // 二个杂牌漏洞
	    boolean weakMoreThan1 = (robotPowerCardsBag.weakCards() == 1); // 一个杂牌漏洞
	    boolean weakMoreThan0 = (robotPowerCardsBag.weakCards() == 0); // 零个杂牌漏洞
	    // 在手弱牌w数 _ 分数
	    boolean w1_0 = robotPowerCardsBag.allNorStraightDangerScoreCardsSum() == 0;
	    boolean w1_5 = robotPowerCardsBag.allNorStraightDangerScoreCardsSum() == 5;
	    boolean w1_10 = robotPowerCardsBag.allNorStraightDangerScoreCardsSum() == 10;
	    boolean w2_5 = robotPowerCardsBag.allNorStraightDangerScoreCardsSum() == 5;

	    // 按漏洞可能造成的失分分类(对方可能的管分)
	    boolean score0_0 = weakMoreThan0;// 无漏洞，无损分
	    boolean score1_5 = weakMoreThan1 && w1_0;// 1漏洞，
	    boolean score1_10 = weakMoreThan1 && w1_0;// 1漏洞，
	    boolean score1_15 = weakMoreThan1 && w1_5;// 1漏洞，
	    boolean score1_20 = weakMoreThan1 && w1_10;// 1漏洞，最高可损失20分
	    boolean score2_25 = weakMoreThan2 && w2_5;// 2漏洞，
	    // -------------------破分副牌分水岭--------------------------

	    // 任意一种都不破
	    boolean safescore = score1_5 || score1_10 || score1_15 || score1_20 || score2_25;

	    // 打光策略
	    boolean target_win_both_0score_joker = roundWinComboJoker && score0_0;
	    boolean target_win_both_0score = roundWinCombo && score0_0;
	    // 打成策略
	    boolean target_win_both_fewscore_joker = roundWinComboJoker && safescore;
	    boolean target_win_both_fewscore = roundWinCombo && safescore;
	    // 保底策略
	    boolean target_win_round_joker = roundWinComboJoker && !safescore;
	    boolean target_win_round = roundWinCombo && !safescore;
	    // 保分策略
	    boolean target_win_score = !roundWinComboJoker || !roundWinCombo;

	    if (target_win_both_0score_joker) {
		result = IBankerStrategy.TARGET_WIN_BOTH_0SCORE_JOKER;
	    } else if (target_win_both_0score) {
		result = IBankerStrategy.TARGET_WIN_BOTH_0SCORE;
	    } else if (target_win_both_fewscore_joker) {
		result = IBankerStrategy.TARGET_WIN_BOTH_FEWSCORE_JOKER;

	    } else if (target_win_both_fewscore) {
		result = IBankerStrategy.TARGET_WIN_BOTH_FEWSCORE;

	    } else if (target_win_round_joker) {
		result = IBankerStrategy.TARGET_WIN_ROUND_JOKER;

	    } else if (target_win_round) {
		result = IBankerStrategy.TARGET_WIN_ROUND;

	    } else if (target_win_score) {
		result = IBankerStrategy.TARGET_WIN_SCORES;
	    }
	}
	return result;
    }

    @Override
    public List<ICard> suggestCardsAsForeHand(int target, IPowerCardsBag powerCardsBag) {
	List<ICard> suggestCards = new ArrayList<ICard>();
	// 先手，根据策略号，经过规则检测
	boolean strategy1001 = (target == IBankerStrategy.TARGET_WIN_BOTH_0SCORE_JOKER);// 有王打光
	boolean strategy1000 = (target == IBankerStrategy.TARGET_WIN_BOTH_0SCORE);// 长主打光
	boolean strategy2001 = (target == IBankerStrategy.TARGET_WIN_BOTH_FEWSCORE_JOKER);// 有王打成
	boolean strategy2000 = (target == IBankerStrategy.TARGET_WIN_BOTH_FEWSCORE);// 长主打成
	boolean strategy3001 = (target == IBankerStrategy.TARGET_WIN_ROUND_JOKER);// 有王保底
	boolean strategy3000 = (target == IBankerStrategy.TARGET_WIN_ROUND);// 长主保底
	boolean strategy4000 = (target == IBankerStrategy.TARGET_WIN_SCORES);// 主弱保分
	if (strategy1001) {
	    suggest_1001(powerCardsBag, suggestCards);
	}
	if (strategy1000) {
	    suggest_1000(powerCardsBag, suggestCards);
	}
	if (strategy2001) {
	    suggest_2001(powerCardsBag, suggestCards);
	}
	if (strategy2000) {
	    suggest_2000(powerCardsBag, suggestCards);
	}
	if (strategy3001) {
	    suggest_3001(powerCardsBag, suggestCards);
	}
	if (strategy3000) {
	    suggest_3000(powerCardsBag, suggestCards);
	}
	if (strategy4000) {
	    suggest_4000(powerCardsBag, suggestCards);
	}

	return suggestCards;
    }

    private void suggest_4000(IPowerCardsBag powerCardsBag, List<ICard> suggestCards) {
	// 主弱保分
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// 看翁牌有没有
	boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();
	if (cards4) {
	    // 有翁出翁
	    suggestCards.addAll(powerCardsBag.getCards4());
	} 
	else  if (spadeStraight) {
		suggestCards.addAll(powerCardsBag.getStraightSpadeCards());
	    } 
	else if (diamondStraight) {
		suggestCards.addAll(powerCardsBag.getStraightDiamondCards());
	    } 
	else if (clubStraight) {
		suggestCards.addAll(powerCardsBag.getStraightClubCards());
	    } 
	else if (heartStraight) {
		suggestCards.addAll(powerCardsBag.getStraightHeartCards());
	    } 
	else {
		List<ICard> complexCards = new ArrayList<ICard>();
		complexCards.addAll(powerCardsBag.getComplex4suitCards());
		boolean hasComplex = !complexCards.isEmpty();
		if (hasComplex) {
		    // 先出有分有副牌中的小副牌
		    List<ICard> score_norscoreCards = new ArrayList<ICard>();
		    score_norscoreCards.addAll(robotPowerCardsBag.getComlexScoreCards_otherCardsSuit());

		    boolean hasScore_Nor = !score_norscoreCards.isEmpty();

		    if (hasScore_Nor) {
			// 给有分副牌升序
			tools.sortNoTrumpSameSuitInHandDesc(score_norscoreCards);
			Collections.reverse(score_norscoreCards);
			for (ICard iCard : score_norscoreCards) {
			    boolean hasNoScore = (iCard.getPoint() != IGameRule.CARD_5_EQUAL_SCORE && iCard.getPoint() != IGameRule.CARD_10_EQUAL_SCORE && iCard.getPoint() != IGameRule.CARD_13_EQUAL_SCORE);
			    if (hasNoScore) {
				suggestCards.add(iCard);
				break;
			    }
			}

		    } else {
			// 出其它副牌
			// 应选对手没有的花色出
			Collections.sort(complexCards);
			Collections.reverse(complexCards);
			suggestCards.add(complexCards.get(0));
		    }

		} else {
		    // 再到主牌,先大再小，控制牌权
		    boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// 看有没有牌2
		    boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// 看有没有将牌
		    boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// 有没有小王

		    // 杂主要将有分主牌排除掉
		    List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
		    List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
		    otherTrumpsCards.removeAll(scoreTrumpCards);

		    boolean othertrumps = !otherTrumpsCards.isEmpty();// 有没有去分后的杂主
		    boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// 分主牌
		    if (cardPoint2) {
			suggestCards.add(powerCardsBag.getPoint2TrumpCards().get(0));
		    } else if (cardClassPoint) {
			suggestCards.add(powerCardsBag.getClassPointTrumpCards().get(0));
		    } else if (joker2) {
			suggestCards.add(powerCardsBag.getJoker2TrumpCards().get(0));
		    } else if (othertrumps) {
			// 杂主排序，出大牌
			List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
			// 先找ace
			for (ICard iCard : otherTrumps) {
			    boolean ace = iCard.getPoint() == 1;
			    if (ace) {
				suggestCards.add(iCard);
				break;
			    }
			}
			boolean noSuggestCards = suggestCards.isEmpty();
			if (noSuggestCards) {
			    for (ICard iCard : otherTrumps) {
				suggestCards.add(iCard);
				break;
			    }
			}
		    } else if (scoreTrumps) {
			// 有分主，先出小分牌
			suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// 最后出守底牌
		    }
		}
	    }
	makeSureSuggestCard(suggestCards);
	}


    private void suggest_3000(IPowerCardsBag powerCardsBag, List<ICard> suggestCards) {
	// 主长，必须抢掉主-策略与1000相当
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// 看翁牌有没有
	boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// 看有没有牌2
	boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// 看有没有将牌
	boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// 有没有小王

	// 杂主要将有分主牌排除掉
	List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
	List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
	otherTrumpsCards.removeAll(scoreTrumpCards);

	boolean othertrumps = !otherTrumpsCards.isEmpty();// 有没有去分后的杂主
	boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// 分主牌
	// TODO:
	boolean opponentTrumps = robot.actions().isOpponentTrump();// 对方是否有主
	boolean hasTrump = !powerCardsBag.getAllTrumpCards().isEmpty();// 自己有主
	if (opponentTrumps && hasTrump) {

	    if (cards4) {
		suggestCards.addAll(powerCardsBag.getCards4());
	    } else if (cardPoint2) {
		suggestCards.add(powerCardsBag.getPoint2TrumpCards().get(0));
	    } else if (cardClassPoint) {
		suggestCards.add(powerCardsBag.getClassPointTrumpCards().get(0));
	    } else if (joker2) {
		suggestCards.add(powerCardsBag.getJoker2TrumpCards().get(0));
	    } else if (othertrumps) {
		// 杂主排序，出大牌
		List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
		// 先找ace
		for (ICard iCard : otherTrumps) {
		    boolean ace = iCard.getPoint() == 1;
		    if (ace) {
			suggestCards.add(iCard);
			break;
		    }
		}
		boolean noSuggestCards = suggestCards.isEmpty();
		if (noSuggestCards) {
		    for (ICard iCard : otherTrumps) {
			suggestCards.add(iCard);
			break;
		    }
		}
	    } else if (scoreTrumps) {
		// 有分主，先出小分牌
		suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
	    }
	    // 没有给出主，就任意出
	    
	      boolean emptysuggestCards=suggestCards.isEmpty();
	      
	      if (emptysuggestCards) {
	      suggestCards.add(powerCardsBag.getAllTrumpCards().get(0));
	      }
	     
	} else {
	    // 对方没主，立即转入副牌战
	    // 先甩
	    boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	    boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	    boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	    boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();

	    // 暂时按此顺序（有牌就行）出
	    if (spadeStraight) {
		suggestCards.addAll(powerCardsBag.getStraightSpadeCards());
	    } else if (diamondStraight) {
		suggestCards.addAll(powerCardsBag.getStraightDiamondCards());
	    } else if (clubStraight) {
		suggestCards.addAll(powerCardsBag.getStraightClubCards());

	    } else if (heartStraight) {
		suggestCards.addAll(powerCardsBag.getStraightHeartCards());
	    } else {
		List<ICard> complexCards = new ArrayList<ICard>();
		complexCards.addAll(powerCardsBag.getComplex4suitCards());
		boolean hasComplex = !complexCards.isEmpty();
		// 有副先出副牌，轮到可能有的杂副牌,从大点往小点出
		if (hasComplex) {
		    Collections.sort(complexCards);
		    Collections.reverse(complexCards);
		    suggestCards.add(complexCards.get(0));
		} else {
		    // 再没副就出主了
		    boolean hasTrumpToo = !powerCardsBag.getOtherTrumpCards().isEmpty();
		    if (hasTrumpToo) {
			suggestCards.add(powerCardsBag.getOtherTrumpCards().get(0));
		    } else if (scoreTrumps) {
			suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// 最后出守底牌
		    }
		}
	    }
	}
	makeSureSuggestCard(suggestCards);
    }

    private void suggest_3001(IPowerCardsBag powerCardsBag, List<ICard> suggestCards) {
	// 保王，副牌抢控制权--清副牌
	// 有翁出翁
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// 看翁牌有没有
	// 有甩先出
	if (cards4) {
	    suggestCards.addAll(powerCardsBag.getCards4());
	} else {
	    boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	    boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	    boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	    boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();
	    // 暂时按此顺序（有牌就行）出
	    if (spadeStraight) {
		suggestCards.addAll(powerCardsBag.getStraightSpadeCards());
	    } else if (diamondStraight) {
		suggestCards.addAll(powerCardsBag.getStraightDiamondCards());
	    } else if (clubStraight) {
		suggestCards.addAll(powerCardsBag.getStraightClubCards());

	    } else if (heartStraight) {
		suggestCards.addAll(powerCardsBag.getStraightHeartCards());
	    }
	    // 有大副先出 可能有的杂副牌,从大点往小点出
	    else {
		List<ICard> complexCards = new ArrayList<ICard>();
		complexCards.addAll(powerCardsBag.getComplex4suitCards());
		boolean hasComplex = !complexCards.isEmpty();
		if (hasComplex) {
		    Collections.sort(complexCards);
		    Collections.reverse(complexCards);
		    suggestCards.add(complexCards.get(0));
		} else {
		    // 再到主牌,先大再小，控制牌权
		    boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// 看有没有牌2
		    boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// 看有没有将牌
		    boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// 有没有小王

		    // 杂主要将有分主牌排除掉
		    List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
		    List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
		    otherTrumpsCards.removeAll(scoreTrumpCards);

		    boolean othertrumps = !otherTrumpsCards.isEmpty();// 有没有去分后的杂主
		    boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// 分主牌
		    if (cardPoint2) {
			suggestCards.add(powerCardsBag.getPoint2TrumpCards().get(0));
		    } else if (cardClassPoint) {
			suggestCards.add(powerCardsBag.getClassPointTrumpCards().get(0));
		    } else if (joker2) {
			suggestCards.add(powerCardsBag.getJoker2TrumpCards().get(0));
		    } else if (othertrumps) {
			// 杂主排序，出大牌
			List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
			// 先找ace
			for (ICard iCard : otherTrumps) {
			    boolean ace = iCard.getPoint() == 1;
			    if (ace) {
				suggestCards.add(iCard);
				break;
			    }
			}
			boolean noSuggestCards = suggestCards.isEmpty();
			if (noSuggestCards) {
			    for (ICard iCard : otherTrumps) {
				suggestCards.add(iCard);
				break;
			    }
			}
		    } else if (scoreTrumps) {
			// 有分主，先出小分牌
			suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// 最后出守底牌
		    }
		}
	    }
	}
	//确保有牌出
	makeSureSuggestCard(suggestCards);
    }

    /**
     * 确保有牌出
     * @param suggestCards
     */
    private void makeSureSuggestCard(List<ICard> suggestCards) {
	boolean suggestCardsZero=suggestCards.size()==0;
	if (suggestCardsZero) {
	    List<ICard> robotHoldingCards = Data4Robot.getInstance().getRobotHoldingCards();
	    boolean robotHoldingCardsYes=robotHoldingCards!=null;
	    if (robotHoldingCardsYes) {
		suggestCards.add(robotHoldingCards.get(0));
		System.out.println("@*BankerStrategy makeSureSuggestCard()--suggestCard-->"+suggestCards.get(0).getSuit()+suggestCards.get(0).getPoint());
	    }
	}
    }

    private void suggest_2000(IPowerCardsBag powerCardsBag, List<ICard> suggestCards) {
	// 此策略关键掉主--与1001的策略相当
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// 看翁牌有没有
	boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// 看有没有牌2
	boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// 看有没有将牌
	boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// 有没有小王

	// 杂主要将有分主牌排除掉
	List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
	List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
	otherTrumpsCards.removeAll(scoreTrumpCards);

	boolean othertrumps = !otherTrumpsCards.isEmpty();// 有没有去分后的杂主
	boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// 分主牌

	boolean opponentTrumps = robot.actions().isOpponentTrump();// 对方是否有主
	boolean hasTrump = !powerCardsBag.getAllTrumpCards().isEmpty();// 手里有主
	if (opponentTrumps && hasTrump) {
	    if (cards4) {
		suggestCards.addAll(powerCardsBag.getCards4());
	    } else if (cardPoint2) {
		suggestCards.add(powerCardsBag.getPoint2TrumpCards().get(0));
	    } else if (cardClassPoint) {
		suggestCards.add(powerCardsBag.getClassPointTrumpCards().get(0));
	    } else if (joker2) {
		suggestCards.add(powerCardsBag.getJoker2TrumpCards().get(0));
	    } else if (othertrumps) {
		// 杂主排序，出大牌
		List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
		// 先找ace
		for (ICard iCard : otherTrumps) {
		    boolean ace = iCard.getPoint() == 1;
		    if (ace) {
			suggestCards.add(iCard);
			break;
		    }
		}
		boolean noSuggestCards = suggestCards.isEmpty();
		if (noSuggestCards) {
		    for (ICard iCard : otherTrumps) {
			suggestCards.add(iCard);
			break;
		    }
		}
	    } else if (scoreTrumps) {
		// 有分主，先出小分牌
		suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
	    }
	    // 没有给出主，就任意出
	   
	      boolean emptysuggestCards=suggestCards.isEmpty();
	      
	      if (emptysuggestCards) {
	     suggestCards.add(powerCardsBag.getAllTrumpCards().get(0)); 
	     }
	     
	} else {
	    // 对方没主，立即转入副牌战
	    // 先甩
	    boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	    boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	    boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	    boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();

	    // 暂时按此顺序（有牌就行）出
	    if (spadeStraight) {
		suggestCards.addAll(powerCardsBag.getStraightSpadeCards());
	    } else if (diamondStraight) {
		suggestCards.addAll(powerCardsBag.getStraightDiamondCards());
	    } else if (clubStraight) {
		suggestCards.addAll(powerCardsBag.getStraightClubCards());

	    } else if (heartStraight) {
		suggestCards.addAll(powerCardsBag.getStraightHeartCards());
	    } else {
		List<ICard> complexCards = new ArrayList<ICard>();
		complexCards.addAll(powerCardsBag.getComplex4suitCards());
		boolean hasComplex = !complexCards.isEmpty();
		// 有副先出副牌，轮到可能有的杂副牌,从大点往小点出
		if (hasComplex) {
		    Collections.sort(complexCards);
		    Collections.reverse(complexCards);
		    suggestCards.add(complexCards.get(0));
		} else {
		    // 再没副就出主了
		    boolean hasTrumpToo = !powerCardsBag.getOtherTrumpCards().isEmpty();
		    if (hasTrumpToo) {
			suggestCards.add(powerCardsBag.getOtherTrumpCards().get(0));
		    } else if (scoreTrumps) {
			suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// 最后出守底牌
		    }
		}
	    }
	}
	makeSureSuggestCard(suggestCards);
    }

    private void suggest_2001(IPowerCardsBag powerCardsBag, List<ICard> suggestCards) {
	// 此策略关键保大王同时清分要果断
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// 看翁牌有没有
	if (cards4) {
	    // 有翁出翁
	    suggestCards.addAll(powerCardsBag.getCards4());
	} else {
	    // 有甩先出
	    boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	    boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	    boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	    boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();
	    // 暂时按此顺序（有牌就行）出
	    if (spadeStraight) {
		suggestCards.addAll(powerCardsBag.getStraightSpadeCards());
	    } else if (diamondStraight) {
		suggestCards.addAll(powerCardsBag.getStraightDiamondCards());
	    } else if (clubStraight) {
		suggestCards.addAll(powerCardsBag.getStraightClubCards());

	    } else if (heartStraight) {
		suggestCards.addAll(powerCardsBag.getStraightHeartCards());
	    }
	    // 有大副先出 可能有的杂副牌,从大点往小点出
	    else {
		List<ICard> complexCards = new ArrayList<ICard>();
		complexCards.addAll(powerCardsBag.getComplex4suitCards());
		boolean hasComplex = !complexCards.isEmpty();
		if (hasComplex) {
		    Collections.sort(complexCards);
		    Collections.reverse(complexCards);
		    suggestCards.add(complexCards.get(0));
		} else {
		    // 再到主牌,先大再小，控制牌权
		    boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// 看有没有牌2
		    boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// 看有没有将牌
		    boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// 有没有小王

		    // 杂主要将有分主牌排除掉
		    List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
		    List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
		    otherTrumpsCards.removeAll(scoreTrumpCards);

		    boolean othertrumps = !otherTrumpsCards.isEmpty();// 有没有去分后的杂主
		    boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// 分主牌
		    if (cardPoint2) {
			suggestCards.add(powerCardsBag.getPoint2TrumpCards().get(0));
		    } else if (cardClassPoint) {
			suggestCards.add(powerCardsBag.getClassPointTrumpCards().get(0));
		    } else if (joker2) {
			suggestCards.add(powerCardsBag.getJoker2TrumpCards().get(0));
		    } else if (othertrumps) {
			// 杂主排序，出大牌
			List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
			// 先找ace
			for (ICard iCard : otherTrumps) {
			    boolean ace = iCard.getPoint() == 1;
			    if (ace) {
				suggestCards.add(iCard);
				break;
			    }
			}
			boolean noSuggestCards = suggestCards.isEmpty();
			if (noSuggestCards) {
			    for (ICard iCard : otherTrumps) {
				suggestCards.add(iCard);
				break;
			    }
			}
		    } else if (scoreTrumps) {
			// 有分主，先出小分牌
			suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// 最后出守底牌
		    }
		}
	    }
	}
	makeSureSuggestCard( suggestCards);
    }

    private void suggest_1000(IPowerCardsBag powerCardsBag, List<ICard> suggestCards) {
	// 应当强行消耗对方的主牌，令其无法对自己的副牌构成威胁
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// 看翁牌有没有
	boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// 看有没有牌2
	boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// 看有没有将牌
	boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// 有没有小王

	// 杂主要将有分主牌排除掉
	List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
	List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
	otherTrumpsCards.removeAll(scoreTrumpCards);

	boolean othertrumps = !otherTrumpsCards.isEmpty();// 有没有去分后的杂主
	boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// 分主牌

	boolean opponentTrumps = robot.actions().isOpponentTrump();// 对方是否有主
	boolean hasTrump = !powerCardsBag.getAllTrumpCards().isEmpty();
	if (opponentTrumps && hasTrump) {
	    if (cards4) {
		suggestCards.addAll(powerCardsBag.getCards4());
	    } else if (cardPoint2) {
		suggestCards.add(powerCardsBag.getPoint2TrumpCards().get(0));
	    } else if (cardClassPoint) {
		suggestCards.add(powerCardsBag.getClassPointTrumpCards().get(0));
	    } else if (joker2) {
		suggestCards.add(powerCardsBag.getJoker2TrumpCards().get(0));
	    } else if (othertrumps) {
		// 杂主排序，出大牌
		List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
		// 先找ace
		for (ICard iCard : otherTrumps) {
		    boolean ace = iCard.getPoint() == 1;
		    if (ace) {
			suggestCards.add(iCard);
			break;
		    }
		}
		boolean noSuggestCards = suggestCards.isEmpty();
		if (noSuggestCards) {
		    for (ICard iCard : otherTrumps) {
			suggestCards.add(iCard);
			break;
		    }
		}
	    } else if (scoreTrumps) {
		// 有分主，先出小分牌
		suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
	    }
	    // 没有给出主，就任意出
	    
	      boolean emptysuggestCards=suggestCards.isEmpty();
	      
	      if (emptysuggestCards) {
	      suggestCards.add(powerCardsBag.getAllTrumpCards().get(0));
	      }
	} else {
	    // 对方没主，立即转入副牌战
	    // 先甩
	    boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	    boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	    boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	    boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();

	    // 暂时按此顺序（有牌就行）出
	    if (spadeStraight) {
		suggestCards.addAll(powerCardsBag.getStraightSpadeCards());
	    } else if (diamondStraight) {
		suggestCards.addAll(powerCardsBag.getStraightDiamondCards());
	    } else if (clubStraight) {
		suggestCards.addAll(powerCardsBag.getStraightClubCards());

	    } else if (heartStraight) {
		suggestCards.addAll(powerCardsBag.getStraightHeartCards());
	    } else {
		List<ICard> complexCards = new ArrayList<ICard>();
		complexCards.addAll(powerCardsBag.getComplex4suitCards());
		boolean hasComplex = !complexCards.isEmpty();
		// 有副先出副牌，轮到可能有的杂副牌,从大点往小点出
		if (hasComplex) {
		    Collections.sort(complexCards);
		    Collections.reverse(complexCards);
		    suggestCards.add(complexCards.get(0));
		} else {
		    // 再没副就出主了
		    boolean hasTrumpToo = !powerCardsBag.getOtherTrumpCards().isEmpty();
		    if (hasTrumpToo) {
			suggestCards.add(powerCardsBag.getOtherTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// 最后出守底牌
		    }
		}
	    }
	}
	makeSureSuggestCard( suggestCards);
    }

    private void suggest_1001(IPowerCardsBag powerCardsBag, List<ICard> suggestCards) {
	// 应当强行消耗对方的主牌，令其无法对自己的副牌构成威胁
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// 看翁牌有没有
	boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// 看有没有牌2
	boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// 看有没有将牌
	boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// 有没有小王

	// 杂主要将有分主牌排除掉
	List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
	List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
	otherTrumpsCards.removeAll(scoreTrumpCards);

	boolean othertrumps = !otherTrumpsCards.isEmpty();// 有没有去分后的杂主
	boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// 分主牌

	boolean opponentTrumps = robot.actions().isOpponentTrump();// 对方是否有主
	boolean hasTrump = !powerCardsBag.getAllTrumpCards().isEmpty();
	if (opponentTrumps && hasTrump) {
	    if (cards4) {
		suggestCards.addAll(powerCardsBag.getCards4());
	    } else if (cardPoint2) {
		suggestCards.add(powerCardsBag.getPoint2TrumpCards().get(0));
	    } else if (cardClassPoint) {
		suggestCards.add(powerCardsBag.getClassPointTrumpCards().get(0));
	    } else if (joker2) {
		suggestCards.add(powerCardsBag.getJoker2TrumpCards().get(0));
	    } else if (othertrumps) {
		// 杂主排序，出大牌
		List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
		// 先找ace
		for (ICard iCard : otherTrumps) {
		    boolean ace = iCard.getPoint() == 1;
		    if (ace) {
			suggestCards.add(iCard);
			break;
		    }
		}
		boolean noSuggestCards = suggestCards.isEmpty();
		if (noSuggestCards) {
		    for (ICard iCard : otherTrumps) {
			suggestCards.add(iCard);
			break;
		    }
		}
	    } else if (scoreTrumps) {
		// 有分主，先出小分牌
		suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
	    }
	    // 没有给出主，就任意出
	    
	      boolean emptysuggestCards=suggestCards.isEmpty();
	      
	      if (emptysuggestCards) {
	      suggestCards.add(powerCardsBag.getAllTrumpCards().get(0));
	      }
	} else {
	    // 对方没主，立即转入副牌战
	    // 先甩
	    boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	    boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	    boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	    boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();

	    // 暂时按此顺序（有牌就行）出
	    if (spadeStraight) {
		suggestCards.addAll(powerCardsBag.getStraightSpadeCards());
	    } else if (diamondStraight) {
		suggestCards.addAll(powerCardsBag.getStraightDiamondCards());
	    } else if (clubStraight) {
		suggestCards.addAll(powerCardsBag.getStraightClubCards());

	    } else if (heartStraight) {
		suggestCards.addAll(powerCardsBag.getStraightHeartCards());
	    } else {
		List<ICard> complexCards = new ArrayList<ICard>();
		complexCards.addAll(powerCardsBag.getComplex4suitCards());
		boolean hasComplex = !complexCards.isEmpty();
		// 有副先出副牌，轮到可能有的杂副牌,从大点往小点出
		if (hasComplex) {
		    Collections.sort(complexCards);
		    Collections.reverse(complexCards);
		    suggestCards.add(complexCards.get(0));
		} else {
		    // 再没副就出主了
		    boolean hasTrumpToo = !powerCardsBag.getOtherTrumpCards().isEmpty();
		    if (hasTrumpToo) {
			suggestCards.add(powerCardsBag.getOtherTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// 最后出守底牌
		    }
		}
	    }
	}
	makeSureSuggestCard(suggestCards);
    }

    public List<ICard> suggestCardsAsAfterHand(IPowerCardsBag powerCardsBag, List<ICard> opponentCards) {
	List<ICard> suggestCards = new ArrayList<ICard>();
	// 分析对手牌基本信息：翁-1，甩-2，主-3，副-4；分数
	Map<String, Integer> opponentInfo = this.analysisInfo(opponentCards);
	// 能不能管牌
	boolean canSuppress = this.canSuppressOpponentCards(powerCardsBag, opponentInfo, opponentCards);
	// 应该管否
	boolean shouldSuppress = this.shouldSuppressOpponentCards(powerCardsBag, opponentInfo, opponentCards);

	if (canSuppress && shouldSuppress) {

	    // 给出管牌建议----最小代价，尽可能带分跑
	    suggestCards.addAll(this.makeSuppressSuggestion(powerCardsBag, opponentInfo, opponentCards));
	} else {
	    // 给出随牌建议----原则是‘最小代价’（小点牌，最好不加入分）
	    suggestCards.addAll(this.makeFollowSuggestion(powerCardsBag, opponentInfo, opponentCards));
	}

	return suggestCards;
    }

    private List<ICard> makeFollowSuggestion(IPowerCardsBag powerCardsBag, Map<String, Integer> opponentInfo, List<ICard> opponentCards) {

	// 最小代价’（小点牌，最好不加入分）
	List<ICard> result = new ArrayList<ICard>();
	// 分清对手牌类型
	Integer type = opponentInfo.get("type");
	// 翁----1，甩----2，主----3，副----4
	switch (type) {
	case 1:
	    System.out.println("@BankerStrategy  makeFollowSuggestion 翁");
	    result.addAll(powerCardsBag.followCards4(opponentCards));
	    System.out.println("@BankerStrategy  makeFollowSuggestion 翁" + result.size());
	    break;
	case 2:
	    System.out.println("@BankerStrategy  makeFollowSuggestion 甩");
	    result.addAll(powerCardsBag.followStraightCards(opponentCards));
	    System.out.println("@BankerStrategy  makeFollowSuggestion 甩" + result.size());
	    break;
	case 3:
	    System.out.println("@BankerStrategy  makeFollowSuggestion 主");
	    result.addAll(powerCardsBag.followTrumpCard(opponentCards));
	    System.out.println("@BankerStrategy  makeFollowSuggestion 主" + result.size());
	    break;
	case 4:
	    System.out.println("@BankerStrategy  makeFollowSuggestion  副");
	    result.addAll(powerCardsBag.followNorTrumpCard(opponentCards));
	    System.out.println("@BankerStrategy  makeFollowSuggestion  副" + result.size());
	    break;

	default:
	    break;
	}

	return result;
    }

    private List<ICard> makeSuppressSuggestion(IPowerCardsBag powerCardsBag, Map<String, Integer> opponentInfo, List<ICard> opponentCards) {

	List<ICard> result = new ArrayList<ICard>();
	// 分清对手牌类型
	Integer type = opponentInfo.get("type");
	// 翁----1，甩----2，主----3，副----4
	switch (type) {
	case 1:
	    result.addAll(powerCardsBag.getCards4());
	    break;
	case 2:
	    result.addAll(powerCardsBag.trumpWithScoreSuppressCards(opponentCards));
	    break;
	case 3:
	    result.addAll(powerCardsBag.higherRankThanOpponent(opponentCards));
	    break;
	case 4:
	    result.addAll(powerCardsBag.killerToOpponentNoTrumpCards(opponentCards));
	    break;

	default:
	    break;
	}

	return result;
    }

    private boolean shouldSuppressOpponentCards(IPowerCardsBag powerCardsBag, Map<String, Integer> opponentInfo, List<ICard> opponentCards) {
	boolean result = false;
	// 如果手牌必须出同类牌，没有小于对手的牌，则只好被迫压制
	boolean haveNoSmaller = powerCardsBag.smallerCardsVsOpponentInSameKind(opponentInfo, opponentCards).isEmpty();

	if (haveNoSmaller) {
	    result = true;
	}
	// 强势策略目标1001和1000与其它不一样
	boolean powerfulJoker = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_BOTH_0SCORE_JOKER;
	boolean powerfulLong = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_BOTH_0SCORE;
	boolean powerful = powerfulJoker || powerfulLong;
	if (powerful) {
	    result = true;
	}
	// 打成目标：2001和2000是要兼顾保底和守分的。
	boolean successNoJoker1 = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_BOTH_FEWSCORE;
	boolean successJoker1 = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_BOTH_FEWSCORE_JOKER;
	boolean success = successNoJoker1 || successJoker1;
	if (success) {
	    // 打成策略：分数没有破掉，不见分不动大牌
	    int score = opponentInfo.get("score");// 当前牌分数状态
	    // 策略要求的对方可得分情况
	    int scorePermission = this.askForScorePermission();
	    // 对方已经得到的分数
	    int opponentScoreValue = this.findOpponentScoreValue();
	    // 策略要求分与已得分比较--有没有杀分的价值
	    boolean deserve = (opponentScoreValue <= scorePermission);// 对方分数没有突破策略要求
	    if (deserve) {
		boolean hasScore = (score > 0);// 对方牌中有分
		boolean myOddScore = this.thereIsOddSameSuitScoreCard(opponentCards);// //跟随同色副牌是分牌,也要管,不能主动送分
		if (hasScore || myOddScore) {
		    result = true;
		}
	    }
	}
	// 保底目标：3001 只是2001分数潜在漏洞多了，算是其子集，分数能管则管吧，适用同样强制策略
	boolean roundWinJoker1 = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_ROUND_JOKER;
	if (roundWinJoker1) {
	    int score = opponentInfo.get("score");// 当前牌分数状态
	    // 策略要求的对方可得分情况
	    int scorePermission = this.askForScorePermission();
	    // 对方已经得到的分数
	    int opponentScoreValue = this.findOpponentScoreValue();
	    // 策略要求分与已得分比较--有没有杀分的价值
	    boolean deserve = (opponentScoreValue <= scorePermission);// 对方分数没有突破策略要求
	    if (deserve) {
		boolean hasScore = (score > 0);// 对方牌中有分
		boolean myOddScore = this.thereIsOddSameSuitScoreCard(opponentCards);// //跟随同色副牌是分牌,也要管,不能主动送分
		if (hasScore || myOddScore) {
		    result = true;
		}
	    }
	}
	// 保底要抢牌权,掉主，必须管牌：3000
	boolean roundWinNoJoker = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_ROUND;
	if (roundWinNoJoker) {
	    result = true;
	}
	// 保分目标:4000 没破前，见分就管
	boolean keepScore = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_SCORES;
	if (keepScore) {
	    int score = opponentInfo.get("score");// 当前牌分数状态
	    int scorePermission = this.askForScorePermission();// 策略要求的对方可得分情况
	    int opponentScoreValue = this.findOpponentScoreValue();// 对方已经得到的分数
	    // 策略要求分与已得分比较--有没有杀分的价值
	    boolean deserve = (opponentScoreValue <= scorePermission);// 对方分数没有突破策略要求
	    boolean hasScore = (score > 0);// 对方牌中有分
	    if (deserve && hasScore) {
		result = true;
	    }
	}
	return result;
    }

    private boolean thereIsOddSameSuitScoreCard(List<ICard> opponentCards) {

	boolean result = false;
	// 1、一张；2、副牌；3、花色我有；4、同色有分牌；5、分牌不在甩中；6、此牌小于对手
	boolean oneCard = opponentCards.size() == 1;
	boolean norTrump = !tools.isAllTrumpCards(opponentCards);
	if (oneCard && norTrump) {
	    // 对手花色
	    String suit = opponentCards.get(0).getSuit();
	    // 自己同花牌
	    List<ICard> mySameSuitCards = new ArrayList<ICard>();
	    mySameSuitCards.addAll(tools.seperateNoTrumpSameSuitCards(RobotPowerCardsBag.getInstance(robot).getAllNonTrumpCards(), suit));
	    boolean hasTheSuit = !mySameSuitCards.isEmpty();
	    if (hasTheSuit) {
		List<ICard> temp = new ArrayList<ICard>();
		for (ICard iCard : mySameSuitCards) {
		    temp.clear();
		    temp.add(iCard);
		    boolean inScoreCollections = RobotPowerCardsBag.getInstance(robot).getComplex4suitCardsWithScore().contains(iCard);
		    boolean smaller = !GameRule.getInstance().whoIsRoundWinner(opponentCards, temp).equals(robot);
		    if (inScoreCollections && smaller) {
			result = true;
		    }
		}
	    }
	}
	return result;
    }

    private int findOpponentScoreValue() {
	int result = 0;
	// 查对手
	List<Player> players = recorder.getPlayerRegistered();
	Player opponent = null;
	for (Player player : players) {
	    boolean notMe = !player.equals(robot);
	    if (notMe) {
		opponent = player;
	    }
	}

	result = opponent.actions().getCurrentScore();
	return result;
    }

    private int askForScorePermission() {
	int result = 0;
	// 当前策略
	int strategy = ((RobotPlayer) robot).getMyStrategy();
	boolean zero = (strategy == IBankerStrategy.TARGET_WIN_BOTH_0SCORE_JOKER || strategy == IBankerStrategy.TARGET_WIN_BOTH_0SCORE);
	if (zero) {
	    result = IGameRule.BOTH_WIN_SCORE_PERMISSION;
	} else {
	    result = IGameRule.BREAK_SET_SCORES_BOTTOM_LINE - 5; // 对方最多可以得到25分
	}
	return result;
    }

    private boolean canSuppressOpponentCards(IPowerCardsBag powerCardsBag, Map<String, Integer> opponentInfo, List<ICard> opponentCards) {
	boolean result = false;
	// 看信息返回类型，与手牌比对，用规则比较
	int type = opponentInfo.get("type");
	switch (type) {
	case 1:

	    // 查看翁牌有无，看能否压制
	    boolean hasMyCards4 = !powerCardsBag.getCards4().isEmpty();
	    if (hasMyCards4) {
		// 比较谁大
		IGameRule gameRule = GameRule.getInstance();
		Player player = gameRule.whoIsRoundWinner(opponentCards, powerCardsBag.getCards4());
		boolean isMe = player.equals(robot);
		if (isMe) {
		    result = true;
		}
	    }

	    break;
	case 2:
	    // 看同花色是否有牌；没有的话，主牌是否够数
	    String suit = opponentCards.get(0).getSuit();
	    boolean noTheSuit = tools.seperateNoTrumpSameSuitCards(robot.actions().getSelfCardDeck(), suit).isEmpty();
	    if (noTheSuit) {
		// 看主牌数量>=对手牌数
		int trumpSize = tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder).size();
		boolean canSuppress = (trumpSize >= opponentCards.size());
		if (canSuppress) {
		    result = true;
		}
	    }

	    break;
	case 3:
	    // 看主牌有无，有的话，能否压制
	    boolean hasTrump = !tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder).isEmpty();
	    if (hasTrump) {
		List<ICard> myTrumpCards = tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder);
		List<ICard> list = new ArrayList<ICard>();
		IGameRule gameRule = GameRule.getInstance();
		for (ICard iCard : myTrumpCards) {
		    list.clear();
		    list.add(iCard);
		    Player player = gameRule.whoIsRoundWinner(opponentCards, list);
		    boolean isMe = player.equals(robot);
		    if (isMe) {
			result = true;
			break;
		    }
		}
	    }

	    break;
	case 4:
	    // 有无同花色，有同花能否压制；无，有无主牌可压制
	    String thesuit = opponentCards.get(0).getSuit();
	    boolean noSuit = tools.seperateNoTrumpSameSuitCards(robot.actions().getSelfCardDeck(), thesuit).isEmpty();
	    if (noSuit) {
		// 看主牌数量>=对手牌数
		int trumpSize = tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder).size();
		boolean canSuppress = (trumpSize >= opponentCards.size());
		if (canSuppress) {
		    result = true;
		}
	    } else {
		// 有同花牌
		List<ICard> sameSuitCards = tools.seperateNoTrumpSameSuitCards(robot.actions().getSelfCardDeck(), thesuit);
		List<ICard> list = new ArrayList<ICard>();
		IGameRule gameRule = GameRule.getInstance();
		for (ICard iCard : sameSuitCards) {
		    list.clear();
		    list.add(iCard);
		    Player player = gameRule.whoIsRoundWinner(opponentCards, list);

		    boolean isMe = player.equals(robot);
		    if (isMe) {
			result = true;
			break;
		    }
		}
	    }

	    break;

	default:
	    break;
	}

	return result;
    }

    private Map<String, Integer> analysisInfo(List<ICard> opponentCards) {
	Map<String, Integer> info = new HashMap<String, Integer>();
	// 检查两类：什么类形，分数
	// 翁，4张，同点牌
	boolean isCards4 = !tools.seperate4SamePointCards(opponentCards).isEmpty() && opponentCards.size() == 4;
	if (isCards4) {
	    info.put("type", 1);
	    System.out.println("@BankerStrategy analysisInfo(List<ICard> opponentCards)--isCards4-->"+isCards4);
	}else{
	    // 主牌
	    boolean isTrumpCard = tools.isAllTrumpCards(opponentCards) && opponentCards.size() == 1;
	    if (isTrumpCard) {
		info.put("type", 3);
		System.out.println("@BankerStrategy analysisInfo(List<ICard> opponentCards)--isTrumpCard-->"+isTrumpCard);
	    }else{
		    // 甩副
		    //boolean isStraightCards =tools.seperateStraightCards(opponentCards).size()>1;
			boolean isStraightCards =opponentCards.size()>1;
		    if (isStraightCards) {
			info.put("type", 2);
			System.out.println("@BankerStrategy analysisInfo(List<ICard> opponentCards)--isStraightCards-->"+isStraightCards);
		    }else{
		    // 副，只有一张，选剩的
			info.put("type", 4);
			System.out.println("@BankerStrategy analysisInfo(List<ICard> opponentCards)--isNonTrumpCard-->选剩的");
		    }
		
	    }
	    
	}

	int scores = tools.sumScores(opponentCards);
	info.put("score", scores);

	return info;
    }

}
