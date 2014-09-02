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
     * * ���е�ǰ�����ݣ� 1����û�б����� 2����������������5������ƽ���ɱ��� 3��Σ�շ����������е���15�֣�
     * 4������������������������--�Ƕ����Ʒֻ��ᣩ
     * 13�����ǳ�ʼ��̬����.
     */
    public int makeStrategy(final List<ICard> cardsInHand) {

	int result = 0;
	result = initStrategy(cardsInHand, result);

	return result;
    }

    private int initStrategy(List<ICard> cardsInHand, int result) {
	boolean size13 = (cardsInHand.size() == IGameRule.PLAYER_CARDS_SIZE);
	if (size13) {
	    // ��ʤ����
	    boolean hasTopCard = !robotPowerCardsBag.getTopCard().isEmpty();// �е�����
	    boolean hasParityTrumpCards = ((robotPowerCardsBag.getOtherTrumpCards().size()) >= 4);// ƽ����
	    boolean hasLongTrumpCards = (robotPowerCardsBag.getOtherTrumpCards().size() >= 7);// �����ˣ�21������ÿ��ƽ��5.5�Ų�����7����������ˣ�
	    boolean hasCard4 = !(robotPowerCardsBag.getCards4().isEmpty());// ����

	    // ��ʤ����
	    boolean setWin1 = hasTopCard && hasParityTrumpCards && hasCard4;// �൱��9����
	    boolean setWin2 = !hasTopCard && hasParityTrumpCards && hasCard4;// �൱��8����
	    boolean setWin3 = hasTopCard && hasLongTrumpCards;// �൱��8����
	    boolean setWin4 = !hasTopCard && hasLongTrumpCards;// �൱��7����
	    boolean setWin5 = hasTopCard && hasParityTrumpCards;// ����4����

	    // ����һ�ֶ�����ʤ
	    boolean roundWinComboJoker = setWin1 || setWin3 || setWin5;
	    boolean roundWinCombo = setWin2 || setWin4;

	    // �Ʒִ���
	    boolean weakMoreThan3 = (robotPowerCardsBag.weakCards() >= 3); // ������������©��
	    boolean weakMoreThan2 = (robotPowerCardsBag.weakCards() == 2); // ��������©��
	    boolean weakMoreThan1 = (robotPowerCardsBag.weakCards() == 1); // һ������©��
	    boolean weakMoreThan0 = (robotPowerCardsBag.weakCards() == 0); // �������©��
	    // ��������w�� _ ����
	    boolean w1_0 = robotPowerCardsBag.allNorStraightDangerScoreCardsSum() == 0;
	    boolean w1_5 = robotPowerCardsBag.allNorStraightDangerScoreCardsSum() == 5;
	    boolean w1_10 = robotPowerCardsBag.allNorStraightDangerScoreCardsSum() == 10;
	    boolean w2_5 = robotPowerCardsBag.allNorStraightDangerScoreCardsSum() == 5;

	    // ��©��������ɵ�ʧ�ַ���(�Է����ܵĹܷ�)
	    boolean score0_0 = weakMoreThan0;// ��©���������
	    boolean score1_5 = weakMoreThan1 && w1_0;// 1©����
	    boolean score1_10 = weakMoreThan1 && w1_0;// 1©����
	    boolean score1_15 = weakMoreThan1 && w1_5;// 1©����
	    boolean score1_20 = weakMoreThan1 && w1_10;// 1©������߿���ʧ20��
	    boolean score2_25 = weakMoreThan2 && w2_5;// 2©����
	    // -------------------�Ʒָ��Ʒ�ˮ��--------------------------

	    // ����һ�ֶ�����
	    boolean safescore = score1_5 || score1_10 || score1_15 || score1_20 || score2_25;

	    // ������
	    boolean target_win_both_0score_joker = roundWinComboJoker && score0_0;
	    boolean target_win_both_0score = roundWinCombo && score0_0;
	    // ��ɲ���
	    boolean target_win_both_fewscore_joker = roundWinComboJoker && safescore;
	    boolean target_win_both_fewscore = roundWinCombo && safescore;
	    // ���ײ���
	    boolean target_win_round_joker = roundWinComboJoker && !safescore;
	    boolean target_win_round = roundWinCombo && !safescore;
	    // ���ֲ���
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
	// ���֣����ݲ��Ժţ�����������
	boolean strategy1001 = (target == IBankerStrategy.TARGET_WIN_BOTH_0SCORE_JOKER);// �������
	boolean strategy1000 = (target == IBankerStrategy.TARGET_WIN_BOTH_0SCORE);// �������
	boolean strategy2001 = (target == IBankerStrategy.TARGET_WIN_BOTH_FEWSCORE_JOKER);// �������
	boolean strategy2000 = (target == IBankerStrategy.TARGET_WIN_BOTH_FEWSCORE);// �������
	boolean strategy3001 = (target == IBankerStrategy.TARGET_WIN_ROUND_JOKER);// ��������
	boolean strategy3000 = (target == IBankerStrategy.TARGET_WIN_ROUND);// ��������
	boolean strategy4000 = (target == IBankerStrategy.TARGET_WIN_SCORES);// ��������
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
	// ��������
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// ��������û��
	boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();
	if (cards4) {
	    // ���̳���
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
		    // �ȳ��з��и����е�С����
		    List<ICard> score_norscoreCards = new ArrayList<ICard>();
		    score_norscoreCards.addAll(robotPowerCardsBag.getComlexScoreCards_otherCardsSuit());

		    boolean hasScore_Nor = !score_norscoreCards.isEmpty();

		    if (hasScore_Nor) {
			// ���зָ�������
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
			// ����������
			// Ӧѡ����û�еĻ�ɫ��
			Collections.sort(complexCards);
			Collections.reverse(complexCards);
			suggestCards.add(complexCards.get(0));
		    }

		} else {
		    // �ٵ�����,�ȴ���С��������Ȩ
		    boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// ����û����2
		    boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// ����û�н���
		    boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// ��û��С��

		    // ����Ҫ���з������ų���
		    List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
		    List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
		    otherTrumpsCards.removeAll(scoreTrumpCards);

		    boolean othertrumps = !otherTrumpsCards.isEmpty();// ��û��ȥ�ֺ������
		    boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// ������
		    if (cardPoint2) {
			suggestCards.add(powerCardsBag.getPoint2TrumpCards().get(0));
		    } else if (cardClassPoint) {
			suggestCards.add(powerCardsBag.getClassPointTrumpCards().get(0));
		    } else if (joker2) {
			suggestCards.add(powerCardsBag.getJoker2TrumpCards().get(0));
		    } else if (othertrumps) {
			// �������򣬳�����
			List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
			// ����ace
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
			// �з������ȳ�С����
			suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// �����ص���
		    }
		}
	    }
	makeSureSuggestCard(suggestCards);
	}


    private void suggest_3000(IPowerCardsBag powerCardsBag, List<ICard> suggestCards) {
	// ����������������-������1000�൱
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// ��������û��
	boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// ����û����2
	boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// ����û�н���
	boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// ��û��С��

	// ����Ҫ���з������ų���
	List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
	List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
	otherTrumpsCards.removeAll(scoreTrumpCards);

	boolean othertrumps = !otherTrumpsCards.isEmpty();// ��û��ȥ�ֺ������
	boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// ������
	// TODO:
	boolean opponentTrumps = robot.actions().isOpponentTrump();// �Է��Ƿ�����
	boolean hasTrump = !powerCardsBag.getAllTrumpCards().isEmpty();// �Լ�����
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
		// �������򣬳�����
		List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
		// ����ace
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
		// �з������ȳ�С����
		suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
	    }
	    // û�и��������������
	    
	      boolean emptysuggestCards=suggestCards.isEmpty();
	      
	      if (emptysuggestCards) {
	      suggestCards.add(powerCardsBag.getAllTrumpCards().get(0));
	      }
	     
	} else {
	    // �Է�û��������ת�븱��ս
	    // ��˦
	    boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	    boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	    boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	    boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();

	    // ��ʱ����˳�����ƾ��У���
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
		// �и��ȳ����ƣ��ֵ������е��Ӹ���,�Ӵ����С���
		if (hasComplex) {
		    Collections.sort(complexCards);
		    Collections.reverse(complexCards);
		    suggestCards.add(complexCards.get(0));
		} else {
		    // ��û���ͳ�����
		    boolean hasTrumpToo = !powerCardsBag.getOtherTrumpCards().isEmpty();
		    if (hasTrumpToo) {
			suggestCards.add(powerCardsBag.getOtherTrumpCards().get(0));
		    } else if (scoreTrumps) {
			suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// �����ص���
		    }
		}
	    }
	}
	makeSureSuggestCard(suggestCards);
    }

    private void suggest_3001(IPowerCardsBag powerCardsBag, List<ICard> suggestCards) {
	// ����������������Ȩ--�帱��
	// ���̳���
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// ��������û��
	// ��˦�ȳ�
	if (cards4) {
	    suggestCards.addAll(powerCardsBag.getCards4());
	} else {
	    boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	    boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	    boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	    boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();
	    // ��ʱ����˳�����ƾ��У���
	    if (spadeStraight) {
		suggestCards.addAll(powerCardsBag.getStraightSpadeCards());
	    } else if (diamondStraight) {
		suggestCards.addAll(powerCardsBag.getStraightDiamondCards());
	    } else if (clubStraight) {
		suggestCards.addAll(powerCardsBag.getStraightClubCards());

	    } else if (heartStraight) {
		suggestCards.addAll(powerCardsBag.getStraightHeartCards());
	    }
	    // �д��ȳ� �����е��Ӹ���,�Ӵ����С���
	    else {
		List<ICard> complexCards = new ArrayList<ICard>();
		complexCards.addAll(powerCardsBag.getComplex4suitCards());
		boolean hasComplex = !complexCards.isEmpty();
		if (hasComplex) {
		    Collections.sort(complexCards);
		    Collections.reverse(complexCards);
		    suggestCards.add(complexCards.get(0));
		} else {
		    // �ٵ�����,�ȴ���С��������Ȩ
		    boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// ����û����2
		    boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// ����û�н���
		    boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// ��û��С��

		    // ����Ҫ���з������ų���
		    List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
		    List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
		    otherTrumpsCards.removeAll(scoreTrumpCards);

		    boolean othertrumps = !otherTrumpsCards.isEmpty();// ��û��ȥ�ֺ������
		    boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// ������
		    if (cardPoint2) {
			suggestCards.add(powerCardsBag.getPoint2TrumpCards().get(0));
		    } else if (cardClassPoint) {
			suggestCards.add(powerCardsBag.getClassPointTrumpCards().get(0));
		    } else if (joker2) {
			suggestCards.add(powerCardsBag.getJoker2TrumpCards().get(0));
		    } else if (othertrumps) {
			// �������򣬳�����
			List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
			// ����ace
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
			// �з������ȳ�С����
			suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// �����ص���
		    }
		}
	    }
	}
	//ȷ�����Ƴ�
	makeSureSuggestCard(suggestCards);
    }

    /**
     * ȷ�����Ƴ�
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
	// �˲��Թؼ�����--��1001�Ĳ����൱
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// ��������û��
	boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// ����û����2
	boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// ����û�н���
	boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// ��û��С��

	// ����Ҫ���з������ų���
	List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
	List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
	otherTrumpsCards.removeAll(scoreTrumpCards);

	boolean othertrumps = !otherTrumpsCards.isEmpty();// ��û��ȥ�ֺ������
	boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// ������

	boolean opponentTrumps = robot.actions().isOpponentTrump();// �Է��Ƿ�����
	boolean hasTrump = !powerCardsBag.getAllTrumpCards().isEmpty();// ��������
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
		// �������򣬳�����
		List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
		// ����ace
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
		// �з������ȳ�С����
		suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
	    }
	    // û�и��������������
	   
	      boolean emptysuggestCards=suggestCards.isEmpty();
	      
	      if (emptysuggestCards) {
	     suggestCards.add(powerCardsBag.getAllTrumpCards().get(0)); 
	     }
	     
	} else {
	    // �Է�û��������ת�븱��ս
	    // ��˦
	    boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	    boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	    boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	    boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();

	    // ��ʱ����˳�����ƾ��У���
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
		// �и��ȳ����ƣ��ֵ������е��Ӹ���,�Ӵ����С���
		if (hasComplex) {
		    Collections.sort(complexCards);
		    Collections.reverse(complexCards);
		    suggestCards.add(complexCards.get(0));
		} else {
		    // ��û���ͳ�����
		    boolean hasTrumpToo = !powerCardsBag.getOtherTrumpCards().isEmpty();
		    if (hasTrumpToo) {
			suggestCards.add(powerCardsBag.getOtherTrumpCards().get(0));
		    } else if (scoreTrumps) {
			suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// �����ص���
		    }
		}
	    }
	}
	makeSureSuggestCard(suggestCards);
    }

    private void suggest_2001(IPowerCardsBag powerCardsBag, List<ICard> suggestCards) {
	// �˲��Թؼ�������ͬʱ���Ҫ����
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// ��������û��
	if (cards4) {
	    // ���̳���
	    suggestCards.addAll(powerCardsBag.getCards4());
	} else {
	    // ��˦�ȳ�
	    boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	    boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	    boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	    boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();
	    // ��ʱ����˳�����ƾ��У���
	    if (spadeStraight) {
		suggestCards.addAll(powerCardsBag.getStraightSpadeCards());
	    } else if (diamondStraight) {
		suggestCards.addAll(powerCardsBag.getStraightDiamondCards());
	    } else if (clubStraight) {
		suggestCards.addAll(powerCardsBag.getStraightClubCards());

	    } else if (heartStraight) {
		suggestCards.addAll(powerCardsBag.getStraightHeartCards());
	    }
	    // �д��ȳ� �����е��Ӹ���,�Ӵ����С���
	    else {
		List<ICard> complexCards = new ArrayList<ICard>();
		complexCards.addAll(powerCardsBag.getComplex4suitCards());
		boolean hasComplex = !complexCards.isEmpty();
		if (hasComplex) {
		    Collections.sort(complexCards);
		    Collections.reverse(complexCards);
		    suggestCards.add(complexCards.get(0));
		} else {
		    // �ٵ�����,�ȴ���С��������Ȩ
		    boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// ����û����2
		    boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// ����û�н���
		    boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// ��û��С��

		    // ����Ҫ���з������ų���
		    List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
		    List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
		    otherTrumpsCards.removeAll(scoreTrumpCards);

		    boolean othertrumps = !otherTrumpsCards.isEmpty();// ��û��ȥ�ֺ������
		    boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// ������
		    if (cardPoint2) {
			suggestCards.add(powerCardsBag.getPoint2TrumpCards().get(0));
		    } else if (cardClassPoint) {
			suggestCards.add(powerCardsBag.getClassPointTrumpCards().get(0));
		    } else if (joker2) {
			suggestCards.add(powerCardsBag.getJoker2TrumpCards().get(0));
		    } else if (othertrumps) {
			// �������򣬳�����
			List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
			// ����ace
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
			// �з������ȳ�С����
			suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// �����ص���
		    }
		}
	    }
	}
	makeSureSuggestCard( suggestCards);
    }

    private void suggest_1000(IPowerCardsBag powerCardsBag, List<ICard> suggestCards) {
	// Ӧ��ǿ�����ĶԷ������ƣ������޷����Լ��ĸ��ƹ�����в
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// ��������û��
	boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// ����û����2
	boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// ����û�н���
	boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// ��û��С��

	// ����Ҫ���з������ų���
	List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
	List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
	otherTrumpsCards.removeAll(scoreTrumpCards);

	boolean othertrumps = !otherTrumpsCards.isEmpty();// ��û��ȥ�ֺ������
	boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// ������

	boolean opponentTrumps = robot.actions().isOpponentTrump();// �Է��Ƿ�����
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
		// �������򣬳�����
		List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
		// ����ace
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
		// �з������ȳ�С����
		suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
	    }
	    // û�и��������������
	    
	      boolean emptysuggestCards=suggestCards.isEmpty();
	      
	      if (emptysuggestCards) {
	      suggestCards.add(powerCardsBag.getAllTrumpCards().get(0));
	      }
	} else {
	    // �Է�û��������ת�븱��ս
	    // ��˦
	    boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	    boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	    boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	    boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();

	    // ��ʱ����˳�����ƾ��У���
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
		// �и��ȳ����ƣ��ֵ������е��Ӹ���,�Ӵ����С���
		if (hasComplex) {
		    Collections.sort(complexCards);
		    Collections.reverse(complexCards);
		    suggestCards.add(complexCards.get(0));
		} else {
		    // ��û���ͳ�����
		    boolean hasTrumpToo = !powerCardsBag.getOtherTrumpCards().isEmpty();
		    if (hasTrumpToo) {
			suggestCards.add(powerCardsBag.getOtherTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// �����ص���
		    }
		}
	    }
	}
	makeSureSuggestCard( suggestCards);
    }

    private void suggest_1001(IPowerCardsBag powerCardsBag, List<ICard> suggestCards) {
	// Ӧ��ǿ�����ĶԷ������ƣ������޷����Լ��ĸ��ƹ�����в
	boolean cards4 = !powerCardsBag.getCards4().isEmpty();// ��������û��
	boolean cardPoint2 = !powerCardsBag.getPoint2TrumpCards().isEmpty();// ����û����2
	boolean cardClassPoint = !powerCardsBag.getClassPointTrumpCards().isEmpty();// ����û�н���
	boolean joker2 = !powerCardsBag.getJoker2TrumpCards().isEmpty();// ��û��С��

	// ����Ҫ���з������ų���
	List<ICard> otherTrumpsCards = powerCardsBag.getOtherTrumpCards();
	List<ICard> scoreTrumpCards = powerCardsBag.getScoreTrumpCards();
	otherTrumpsCards.removeAll(scoreTrumpCards);

	boolean othertrumps = !otherTrumpsCards.isEmpty();// ��û��ȥ�ֺ������
	boolean scoreTrumps = !powerCardsBag.getScoreTrumpCards().isEmpty();// ������

	boolean opponentTrumps = robot.actions().isOpponentTrump();// �Է��Ƿ�����
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
		// �������򣬳�����
		List<ICard> otherTrumps = powerCardsBag.getOtherTrumpCards();
		// ����ace
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
		// �з������ȳ�С����
		suggestCards.add(powerCardsBag.getScoreTrumpCards().get(0));
	    }
	    // û�и��������������
	    
	      boolean emptysuggestCards=suggestCards.isEmpty();
	      
	      if (emptysuggestCards) {
	      suggestCards.add(powerCardsBag.getAllTrumpCards().get(0));
	      }
	} else {
	    // �Է�û��������ת�븱��ս
	    // ��˦
	    boolean heartStraight = !powerCardsBag.getStraightHeartCards().isEmpty();
	    boolean clubStraight = !powerCardsBag.getStraightClubCards().isEmpty();
	    boolean diamondStraight = !powerCardsBag.getStraightDiamondCards().isEmpty();
	    boolean spadeStraight = !powerCardsBag.getStraightSpadeCards().isEmpty();

	    // ��ʱ����˳�����ƾ��У���
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
		// �и��ȳ����ƣ��ֵ������е��Ӹ���,�Ӵ����С���
		if (hasComplex) {
		    Collections.sort(complexCards);
		    Collections.reverse(complexCards);
		    suggestCards.add(complexCards.get(0));
		} else {
		    // ��û���ͳ�����
		    boolean hasTrumpToo = !powerCardsBag.getOtherTrumpCards().isEmpty();
		    if (hasTrumpToo) {
			suggestCards.add(powerCardsBag.getOtherTrumpCards().get(0));
		    } else {
			suggestCards.addAll(powerCardsBag.getTopCard());// �����ص���
		    }
		}
	    }
	}
	makeSureSuggestCard(suggestCards);
    }

    public List<ICard> suggestCardsAsAfterHand(IPowerCardsBag powerCardsBag, List<ICard> opponentCards) {
	List<ICard> suggestCards = new ArrayList<ICard>();
	// ���������ƻ�����Ϣ����-1��˦-2����-3����-4������
	Map<String, Integer> opponentInfo = this.analysisInfo(opponentCards);
	// �ܲ��ܹ���
	boolean canSuppress = this.canSuppressOpponentCards(powerCardsBag, opponentInfo, opponentCards);
	// Ӧ�ùܷ�
	boolean shouldSuppress = this.shouldSuppressOpponentCards(powerCardsBag, opponentInfo, opponentCards);

	if (canSuppress && shouldSuppress) {

	    // �������ƽ���----��С���ۣ������ܴ�����
	    suggestCards.addAll(this.makeSuppressSuggestion(powerCardsBag, opponentInfo, opponentCards));
	} else {
	    // �������ƽ���----ԭ���ǡ���С���ۡ���С���ƣ���ò�����֣�
	    suggestCards.addAll(this.makeFollowSuggestion(powerCardsBag, opponentInfo, opponentCards));
	}

	return suggestCards;
    }

    private List<ICard> makeFollowSuggestion(IPowerCardsBag powerCardsBag, Map<String, Integer> opponentInfo, List<ICard> opponentCards) {

	// ��С���ۡ���С���ƣ���ò�����֣�
	List<ICard> result = new ArrayList<ICard>();
	// �������������
	Integer type = opponentInfo.get("type");
	// ��----1��˦----2����----3����----4
	switch (type) {
	case 1:
	    System.out.println("@BankerStrategy  makeFollowSuggestion ��");
	    result.addAll(powerCardsBag.followCards4(opponentCards));
	    System.out.println("@BankerStrategy  makeFollowSuggestion ��" + result.size());
	    break;
	case 2:
	    System.out.println("@BankerStrategy  makeFollowSuggestion ˦");
	    result.addAll(powerCardsBag.followStraightCards(opponentCards));
	    System.out.println("@BankerStrategy  makeFollowSuggestion ˦" + result.size());
	    break;
	case 3:
	    System.out.println("@BankerStrategy  makeFollowSuggestion ��");
	    result.addAll(powerCardsBag.followTrumpCard(opponentCards));
	    System.out.println("@BankerStrategy  makeFollowSuggestion ��" + result.size());
	    break;
	case 4:
	    System.out.println("@BankerStrategy  makeFollowSuggestion  ��");
	    result.addAll(powerCardsBag.followNorTrumpCard(opponentCards));
	    System.out.println("@BankerStrategy  makeFollowSuggestion  ��" + result.size());
	    break;

	default:
	    break;
	}

	return result;
    }

    private List<ICard> makeSuppressSuggestion(IPowerCardsBag powerCardsBag, Map<String, Integer> opponentInfo, List<ICard> opponentCards) {

	List<ICard> result = new ArrayList<ICard>();
	// �������������
	Integer type = opponentInfo.get("type");
	// ��----1��˦----2����----3����----4
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
	// ������Ʊ����ͬ���ƣ�û��С�ڶ��ֵ��ƣ���ֻ�ñ���ѹ��
	boolean haveNoSmaller = powerCardsBag.smallerCardsVsOpponentInSameKind(opponentInfo, opponentCards).isEmpty();

	if (haveNoSmaller) {
	    result = true;
	}
	// ǿ�Ʋ���Ŀ��1001��1000��������һ��
	boolean powerfulJoker = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_BOTH_0SCORE_JOKER;
	boolean powerfulLong = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_BOTH_0SCORE;
	boolean powerful = powerfulJoker || powerfulLong;
	if (powerful) {
	    result = true;
	}
	// ���Ŀ�꣺2001��2000��Ҫ��˱��׺��طֵġ�
	boolean successNoJoker1 = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_BOTH_FEWSCORE;
	boolean successJoker1 = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_BOTH_FEWSCORE_JOKER;
	boolean success = successNoJoker1 || successJoker1;
	if (success) {
	    // ��ɲ��ԣ�����û���Ƶ��������ֲ�������
	    int score = opponentInfo.get("score");// ��ǰ�Ʒ���״̬
	    // ����Ҫ��ĶԷ��ɵ÷����
	    int scorePermission = this.askForScorePermission();
	    // �Է��Ѿ��õ��ķ���
	    int opponentScoreValue = this.findOpponentScoreValue();
	    // ����Ҫ������ѵ÷ֱȽ�--��û��ɱ�ֵļ�ֵ
	    boolean deserve = (opponentScoreValue <= scorePermission);// �Է�����û��ͻ�Ʋ���Ҫ��
	    if (deserve) {
		boolean hasScore = (score > 0);// �Է������з�
		boolean myOddScore = this.thereIsOddSameSuitScoreCard(opponentCards);// //����ͬɫ�����Ƿ���,ҲҪ��,���������ͷ�
		if (hasScore || myOddScore) {
		    result = true;
		}
	    }
	}
	// ����Ŀ�꣺3001 ֻ��2001����Ǳ��©�����ˣ��������Ӽ��������ܹ���ܰɣ�����ͬ��ǿ�Ʋ���
	boolean roundWinJoker1 = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_ROUND_JOKER;
	if (roundWinJoker1) {
	    int score = opponentInfo.get("score");// ��ǰ�Ʒ���״̬
	    // ����Ҫ��ĶԷ��ɵ÷����
	    int scorePermission = this.askForScorePermission();
	    // �Է��Ѿ��õ��ķ���
	    int opponentScoreValue = this.findOpponentScoreValue();
	    // ����Ҫ������ѵ÷ֱȽ�--��û��ɱ�ֵļ�ֵ
	    boolean deserve = (opponentScoreValue <= scorePermission);// �Է�����û��ͻ�Ʋ���Ҫ��
	    if (deserve) {
		boolean hasScore = (score > 0);// �Է������з�
		boolean myOddScore = this.thereIsOddSameSuitScoreCard(opponentCards);// //����ͬɫ�����Ƿ���,ҲҪ��,���������ͷ�
		if (hasScore || myOddScore) {
		    result = true;
		}
	    }
	}
	// ����Ҫ����Ȩ,������������ƣ�3000
	boolean roundWinNoJoker = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_ROUND;
	if (roundWinNoJoker) {
	    result = true;
	}
	// ����Ŀ��:4000 û��ǰ�����־͹�
	boolean keepScore = this.robot.getMyStrategy() == IBankerStrategy.TARGET_WIN_SCORES;
	if (keepScore) {
	    int score = opponentInfo.get("score");// ��ǰ�Ʒ���״̬
	    int scorePermission = this.askForScorePermission();// ����Ҫ��ĶԷ��ɵ÷����
	    int opponentScoreValue = this.findOpponentScoreValue();// �Է��Ѿ��õ��ķ���
	    // ����Ҫ������ѵ÷ֱȽ�--��û��ɱ�ֵļ�ֵ
	    boolean deserve = (opponentScoreValue <= scorePermission);// �Է�����û��ͻ�Ʋ���Ҫ��
	    boolean hasScore = (score > 0);// �Է������з�
	    if (deserve && hasScore) {
		result = true;
	    }
	}
	return result;
    }

    private boolean thereIsOddSameSuitScoreCard(List<ICard> opponentCards) {

	boolean result = false;
	// 1��һ�ţ�2�����ƣ�3����ɫ���У�4��ͬɫ�з��ƣ�5�����Ʋ���˦�У�6������С�ڶ���
	boolean oneCard = opponentCards.size() == 1;
	boolean norTrump = !tools.isAllTrumpCards(opponentCards);
	if (oneCard && norTrump) {
	    // ���ֻ�ɫ
	    String suit = opponentCards.get(0).getSuit();
	    // �Լ�ͬ����
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
	// �����
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
	// ��ǰ����
	int strategy = ((RobotPlayer) robot).getMyStrategy();
	boolean zero = (strategy == IBankerStrategy.TARGET_WIN_BOTH_0SCORE_JOKER || strategy == IBankerStrategy.TARGET_WIN_BOTH_0SCORE);
	if (zero) {
	    result = IGameRule.BOTH_WIN_SCORE_PERMISSION;
	} else {
	    result = IGameRule.BREAK_SET_SCORES_BOTTOM_LINE - 5; // �Է������Եõ�25��
	}
	return result;
    }

    private boolean canSuppressOpponentCards(IPowerCardsBag powerCardsBag, Map<String, Integer> opponentInfo, List<ICard> opponentCards) {
	boolean result = false;
	// ����Ϣ�������ͣ������Ʊȶԣ��ù���Ƚ�
	int type = opponentInfo.get("type");
	switch (type) {
	case 1:

	    // �鿴�������ޣ����ܷ�ѹ��
	    boolean hasMyCards4 = !powerCardsBag.getCards4().isEmpty();
	    if (hasMyCards4) {
		// �Ƚ�˭��
		IGameRule gameRule = GameRule.getInstance();
		Player player = gameRule.whoIsRoundWinner(opponentCards, powerCardsBag.getCards4());
		boolean isMe = player.equals(robot);
		if (isMe) {
		    result = true;
		}
	    }

	    break;
	case 2:
	    // ��ͬ��ɫ�Ƿ����ƣ�û�еĻ��������Ƿ���
	    String suit = opponentCards.get(0).getSuit();
	    boolean noTheSuit = tools.seperateNoTrumpSameSuitCards(robot.actions().getSelfCardDeck(), suit).isEmpty();
	    if (noTheSuit) {
		// ����������>=��������
		int trumpSize = tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder).size();
		boolean canSuppress = (trumpSize >= opponentCards.size());
		if (canSuppress) {
		    result = true;
		}
	    }

	    break;
	case 3:
	    // ���������ޣ��еĻ����ܷ�ѹ��
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
	    // ����ͬ��ɫ����ͬ���ܷ�ѹ�ƣ��ޣ��������ƿ�ѹ��
	    String thesuit = opponentCards.get(0).getSuit();
	    boolean noSuit = tools.seperateNoTrumpSameSuitCards(robot.actions().getSelfCardDeck(), thesuit).isEmpty();
	    if (noSuit) {
		// ����������>=��������
		int trumpSize = tools.seperateTrumpsFromCards(robot.actions().getSelfCardDeck(), recorder).size();
		boolean canSuppress = (trumpSize >= opponentCards.size());
		if (canSuppress) {
		    result = true;
		}
	    } else {
		// ��ͬ����
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
	// ������ࣺʲô���Σ�����
	// �̣�4�ţ�ͬ����
	boolean isCards4 = !tools.seperate4SamePointCards(opponentCards).isEmpty() && opponentCards.size() == 4;
	if (isCards4) {
	    info.put("type", 1);
	    System.out.println("@BankerStrategy analysisInfo(List<ICard> opponentCards)--isCards4-->"+isCards4);
	}else{
	    // ����
	    boolean isTrumpCard = tools.isAllTrumpCards(opponentCards) && opponentCards.size() == 1;
	    if (isTrumpCard) {
		info.put("type", 3);
		System.out.println("@BankerStrategy analysisInfo(List<ICard> opponentCards)--isTrumpCard-->"+isTrumpCard);
	    }else{
		    // ˦��
		    //boolean isStraightCards =tools.seperateStraightCards(opponentCards).size()>1;
			boolean isStraightCards =opponentCards.size()>1;
		    if (isStraightCards) {
			info.put("type", 2);
			System.out.println("@BankerStrategy analysisInfo(List<ICard> opponentCards)--isStraightCards-->"+isStraightCards);
		    }else{
		    // ����ֻ��һ�ţ�ѡʣ��
			info.put("type", 4);
			System.out.println("@BankerStrategy analysisInfo(List<ICard> opponentCards)--isNonTrumpCard-->ѡʣ��");
		    }
		
	    }
	    
	}

	int scores = tools.sumScores(opponentCards);
	info.put("score", scores);

	return info;
    }

}
