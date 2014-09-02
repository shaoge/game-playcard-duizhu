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
	int banker = tools.isThereBanker();// ����ׯ��״̬
	switch (banker) {
	case 0:
	    // ��ׯ�Ҿ֣���ʼ�֣�: 1������������ 2��������2��3�����Ʊ���Ϊ3�� ----����ͨ��

	    boolean suit0 = !cardShowedByplayer.getSuit().equals("joker");
	    boolean point0 = (cardShowedByplayer.getPoint() != 2);
	    boolean bankerClassPoint0 = (3 == cardShowedByplayer.getPoint());
	    if (suit0 && point0 && bankerClassPoint0) {
		result = true;
	    }
	    break;
	case 1:
	    // ��ׯ�Ҿ�: 1������������ 2��������2��3�����Ʊ�����ׯ�Ҵ򵽵ļ�����ȣ� ----����ͨ��
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
	// ����ǰ��Ҫȷ���Ƿ��н��ơ�ÿ�δ���һ��Ҫ��յ����ԣ�ץ�ƹ����п�������������
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
	// �̵����ã� 1������������ 2��ÿ�ֻ�ɫ��һ 3������������� 4����ɫ����Ϊjoker
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
	// 1����������һ�ţ� 2����ͬ��ɫ���Ʋ��û���� 3��˦����û�бȶԷ�С����;

	boolean sizeMoreThan1 = (cards.size() > 1);
	if (sizeMoreThan1) {
	    System.out.println("@GameRule isRight2PreSameNonTrumpSuitGroupOut sizeMoreThan1--->" + sizeMoreThan1);
	    boolean sameSuit = tools.isSameNoTrumpCardsSuit(cards);
	    if (sameSuit) {
		System.out.println("@GameRule isRight2PreSameNonTrumpSuitGroupOut sameSuit--->" + sameSuit);
		// �Է�����
		List<ICard> opponentCards = new ArrayList<ICard>();
		boolean robot = opponentPlayer.equals(RobotPlayer.getInstance(AIPlay.getInstance()));
		if (robot) {
		    opponentCards.addAll(Data4Robot.getInstance().getRobotHoldingCards());
		} else {
		    opponentCards.addAll(Data4Human.getInstance().getRobotHoldingCards());
		}

		// �Է�����ͬ��ɫ����
		List<ICard> opponentSameSuitCards = tools.seperateNoTrumpSameSuitCards(opponentCards, cards.get(0).getSuit());

		// �Ƚ���û�б��Լ����ͬ��ɫ����
		// Ace������
		boolean aceIsClassPointTrump = recorder.getCurrentClassPoint() == 1;
		if (aceIsClassPointTrump) {
		    System.out.println("@GameRule isRight2PreSameNonTrumpSuitGroupOut aceIsClassPointTrump--->" + aceIsClassPointTrump);
		    // �����õ������ԱȽ�
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
		    // �Է�����ͬ��Ace
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
			// �����õ������ԱȽ�;���ų�����ACE
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
	// 1�������ƣ�2�������̣�3����2��
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
	    // ��׽�����˵�������
	    catchGhostCards(offensiveCard, defensiveCard);
	    
	    //��������
	    boolean someSize = offensiveCard.size() == defensiveCard.size();
	    boolean bothMorethanOne = (offensiveCard.size() > 0 && defensiveCard.size() > 0);
	    boolean baseOk = someSize && bothMorethanOne;
	    if (baseOk) {
		// ����
		boolean oneCard = offensiveCard.size() == 1;
		if (oneCard) {
		    boolean offensiveTrump=tools.isAllTrumpCards(offensiveCard);
		    if (offensiveTrump) {
			//��������
			boolean defensiveTrump=tools.isAllTrumpCards(defensiveCard);
			if (defensiveTrump) {
			    //��������
				player = trump_1_VS_1(offensiveCard, defensiveCard, player);
			} else {
			    //�����Ǹ�
			    player = offensiveCard.get(0).getPlayer();
			}
		    } else {
			//�����Ǹ�
			boolean defensiveTrump=tools.isAllTrumpCards(defensiveCard);
			if (defensiveTrump) {
			    //��������
			    player = defensiveCard.get(0).getPlayer();
			} else {
			    //�����Ǹ�
				player = nonTrump_1_VS_1(offensiveCard, defensiveCard, player);
			}
		    }
		} else {
		    // ����
		    boolean offensiveFourSamePoint=tools.seperate4SamePointCards(offensiveCard).size()==4;
		    if (offensiveFourSamePoint) {
			//������
			boolean defensiveFourSamePoint=tools.seperate4SamePointCards(defensiveCard).size()==4;
			if (defensiveFourSamePoint) {
			    //������
				player = four_VS_four(offensiveCard, defensiveCard, player);
			} else {
			    //���ַ���
			    player = offensiveCard.get(0).getPlayer();
			}
		    } else {
			//���ַ��̣�ֻ����˦��
			boolean defensiveAllTrump=tools.isAllTrumpCards(defensiveCard);
			if (defensiveAllTrump) {
			    //����ȫ������
			    player = defensiveCard.get(0).getPlayer();
			} else {
			    //���ֲ�ȫ����
			    player = offensiveCard.get(0).getPlayer();
			    
			}
		    }
		}
	    }
	}

	return player;
    }
/**
 * �̶���PK
 * @param offensiveCard
 * @param defensiveCard
 * @param player
 * @return
 */
    private Player four_VS_four(List<ICard> offensiveCard, List<ICard> defensiveCard, Player player) {
	// ��ѡһ���ƣ���PK�㣬�����ֵ�ת��PK��
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
 * ����������PK
 * @param offensiveCard
 * @param defensiveCard
 * @param player
 * @return
 */
    private Player trump_1_VS_1(List<ICard> offensiveCard, List<ICard> defensiveCard, Player player) {
	//������
	// ��PK�㣬�����ֵ�ת��PK��
	int offPkPoint = turnIntoPKPoint(offensiveCard);
	int defPkPoint = turnIntoPKPoint(defensiveCard);
	
	boolean equal = (offPkPoint == defPkPoint);// С���ͽ����Ƶ��������
	if (equal) {
	    player = offensiveCard.get(0).getPlayer();// ǰ��ʤ����
	} else {
	    boolean offbigger = (offPkPoint > defPkPoint);//ǰ�ִ�
	    if (offbigger) {
		 player = offensiveCard.get(0).getPlayer();
	    } else {
		 player = defensiveCard.get(0).getPlayer();
	    }
	}
	return player;
    }
/**
 * ���Ƶ���PK
 * @param offensiveCard
 * @param defensiveCard
 * @param player
 * @return
 */
    private Player nonTrump_1_VS_1(List<ICard> offensiveCard, List<ICard> defensiveCard, Player player) {
	// ���Ը�
	boolean sameSuit=offensiveCard.get(0).getSuit().equals(defensiveCard.get(0).getSuit());
	if (sameSuit) {
	    //ͬ��ɫ���Աȴ�С
		// ��PK�㣬 �����ֵ�ת��PK��
		// �н׵�Ҫ������
		int offPkPoint = turnIntoPKPoint(offensiveCard);
		int defPkPoint = turnIntoPKPoint(defensiveCard);
	    boolean offbigger = offPkPoint > defPkPoint;//���ִ�
	    if (offbigger) {
		player = offensiveCard.get(0).getPlayer();
	    } else {
		player = defensiveCard.get(0).getPlayer();
	    }
	} else {
	   //�컨ɫ�����ɱȣ��ȳ���ʤ
	    player = offensiveCard.get(0).getPlayer();
	}
	return player;
    }
/**
 * ���ݵ��������ת��PK��
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
     * ��׽�����˿��ܶ�ʧ��������
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
	// TODO:��ʾ˫������
	System.out.println("@GameRule whoIsSetWinner showHumanClassPoint---->" + HumanPlayer.getInstance(GeneralPlay.getInstance()).actions().getMyClassPoint());
	System.out.println("@GameRule whoIsSetWinner showRobotClassPoint---->" + RobotPlayer.getInstance(AIPlay.getInstance()).actions().getMyClassPoint());

	Player setWinner = null;
	// ˫���ƴ�������
	boolean bothShowHands = recorder.getCardsOnTable().size() == IGameRule.PLAYER_CARDS_SIZE * 2;
	if (bothShowHands) {
	    Player banker = recorder.getCurrentBanker();
	    boolean isBankerWin = banker.equals(roundWinner);

	    if (isBankerWin) {
		// ׯ�ң���ʤ/���Ʒ�ʤ�ң������ļ���һ��
		boolean scoreUnderline = recorder.getSetScore() < IGameRule.BREAK_SET_SCORES_BOTTOM_LINE;
		boolean moreThan0 = recorder.getSetScore() > 0;
		boolean notBreaking = scoreUnderline && moreThan0;
		if (notBreaking) {
		    // û�Ʒ�
		    int initScore = banker.actions().getMyClassPoint();
		    boolean initZero = (initScore == 0);
		    if (initZero) {
			// �״γ�������4��
			int myClassPoint = IGameRule.INITIAL_CLASSPOINT + 1;
			banker.actions().setMyClassPoint(limitToAce(myClassPoint));
		    } else {
			// ��������1��
			int myClassPoint = initScore + 1;
			banker.actions().setMyClassPoint(limitToAce(myClassPoint));
		    }
		    setWinner = banker;
		}
		boolean scoreNoAtAll = recorder.getSetScore() == 0;
		if (scoreNoAtAll) {
		    // �״ι�������5��
		    int initScore = banker.actions().getMyClassPoint();
		    boolean initZero = (initScore == 0);
		    if (initZero) {
			int myClassPoint = IGameRule.INITIAL_CLASSPOINT + 2;
			banker.actions().setMyClassPoint(limitToAce(myClassPoint));
		    } else {
			// ��������2��
			int myClassPoint = initScore + 2;
			banker.actions().setMyClassPoint(limitToAce(myClassPoint));
		    }
		    setWinner = banker;
		}
		boolean scoreBreakLine = recorder.getSetScore() >= IGameRule.BREAK_SET_SCORES_BOTTOM_LINE;
		if (scoreBreakLine) {
		    // �ص׵��Ʒ�
		    List<Player> players = recorder.getPlayerRegistered();
		    Player player = RobotPlayer.getInstance(AIPlay.getInstance());// ��ʱ��
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
		// �м�: ��ʤ���Ʒֶ���ʤ�ң������ļ���һ��
		Player player = roundWinner;

		boolean scoreUnderline = recorder.getSetScore() < IGameRule.BREAK_SET_SCORES_BOTTOM_LINE;
		if (scoreUnderline) {
		    int initScore = player.actions().getMyClassPoint();
		    boolean initZero = (initScore == 0);
		    if (initZero) {
			// û�м�����ϵͳ������3��
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
			// û�м�����ϵͳ������3��
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
	// TODO:��ʾ˫������
	System.out.println("@GameRule whoIsSetWinner showHumanClassPoint---->" + HumanPlayer.getInstance(GeneralPlay.getInstance()).actions().getMyClassPoint());
	System.out.println("@GameRule whoIsSetWinner showRobotClassPoint---->" + RobotPlayer.getInstance(AIPlay.getInstance()).actions().getMyClassPoint());
	return setWinner;
    }

    /**
     * ���ƴ��������14--ACE��ֵ
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

	// 1 ����ʤ����ׯ�ң�2�����Ƶ�ace
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
	    boolean afterHandHasTrump = !tools.seperateTrumpsFromCards(afterHandSelfCardDeck, recorder).isEmpty();// ���Ʒ�����
	    if (afterHandHasTrump) {
		// ����������
		boolean trump = !tools.seperateTrumpsFromCards(afterCards, recorder).isEmpty();
		if (trump) {
		    result = true;
		}
	    } else {
		// û������ʲô����
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
	    // �ٶ�����˦��,���ֻ�ɫ�ӣ�����ͬ���ƴ��ڵ������ֳ�����

	    String foreSuit = foreCards.get(0).getSuit();// ���ֳ��Ļ�ɫ
	    Player afterPlayer = afterCards.get(0).getPlayer();// ���������
	    List<ICard> selfCardDeck = new ArrayList<ICard>();
	    boolean robot = afterPlayer.equals(RobotPlayer.getInstance(AIPlay.getInstance()));
	    if (robot) {
		selfCardDeck.addAll(Data4Robot.getInstance().getRobotHoldingCards());
	    } else {
		selfCardDeck.addAll(Data4Human.getInstance().getRobotHoldingCards());
	    }
	    // ���˵�����==������
	    List<ICard> noTrumpSameSuitCards = new ArrayList<ICard>();
	    if (selfCardDeck.size() > 0) {
		noTrumpSameSuitCards.addAll(tools.seperateNoTrumpSameSuitCards(selfCardDeck, foreSuit));
	    }

	    // �� ����
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
	// 1.���涨�ƴ�������׼��2.������һ��û�У�3.����Ƿּ�
	boolean size = player.actions().countCards() == IGameRule.PLAYER_CARDS_SIZE;
	boolean empty = (tools.sumScores(player.actions().getSelfCardDeck()) == 0);
	boolean notbanker = !player.actions().isBanker();
	boolean allTrue = size && empty & notbanker;
	boolean result = allTrue ? true : false;
	return result;
    }

    @Override
    public boolean mayIPutOutCards(Player me) {
	// �������������ʤ�ң��Լ��Ѿ����ƣ��ر���������ֻ���ׯ��
	boolean result = false;

	// ������:������ׯ��;

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
	// ����һ���ƣ������Ǹ���
	boolean one = foreCards.size() == 1;
	boolean eachOne = foreCards.size() == afterCards.size();
	boolean foreCardsIsNotTrump = !tools.isAllTrumpCards(foreCards);
	boolean preOk = one && eachOne && foreCardsIsNotTrump;

	if (preOk) {
	    // ��ͬ����������ͬ����ûͬ��������û��ͬ��
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
	// ���ǰ�����̣����ɣ����򣬲鿴�������������Ƿ�С���ֳ�����
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
		    // ���Ʋ��㣬������ȫ��
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
