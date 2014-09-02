package com.formum.duizhu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.formum.duizhu.cardDeck.CardDeck;
import com.formum.duizhu.cardDeck.ICardDeck;
import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.data.Data4Human;
import com.formum.duizhu.data.Data4Robot;
import com.formum.duizhu.email.EmailSender;
import com.formum.duizhu.gameRule.GameRule;
import com.formum.duizhu.gameRule.IGameRule;
import com.formum.duizhu.humanCardsControler.HumanCardsControler;
import com.formum.duizhu.humanCardsControler.IHumanCardsControler;
import com.formum.duizhu.player.AIPlay;
import com.formum.duizhu.player.GeneralPlay;
import com.formum.duizhu.player.HumanPlayer;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.player.RobotPlayer;
import com.formum.duizhu.recorder.IRecorder;
import com.formum.duizhu.recorder.Recorder;
import com.formum.duizhu.robotCardsControler.IRobotCardsControler;
import com.formum.duizhu.robotCardsControler.RobotCardsControler;
import com.formum.duizhu.soundplay.ISoundPlay;
import com.formum.duizhu.soundplay.SoundPlay;
import com.formum.duizhu.util.IUtil;
import com.formum.duizhu.util.Util;

public class ReadyOut extends Fragment {

    private List<ICard> cards = new ArrayList<ICard>();
    private List<ImageView> imageViewSet = new ArrayList<ImageView>();

    private ICard thePressedCard = null;

    private GestureDetector gestureDetector;

    private IUtil tools = Util.getInstance();
    private IRecorder recorder = Recorder.getInstance();
    private IGameRule gameRule = GameRule.getInstance();
    private HumanPlayer human = HumanPlayer.getInstance(GeneralPlay.getInstance());
    private RobotPlayer robot = RobotPlayer.getInstance(AIPlay.getInstance());
    private LinearLayout top4Robot;// �����˷��Ƶ�λ
    private ICardDeck cardDeck = CardDeck.getInstance();

    private Activity activity;

    private IHumanCardsControler humanCardsControler = HumanCardsControler.getInstance();
    private IRobotCardsControler robotCardsControler = RobotCardsControler.getInstance();

    private List<ICard> tempRobotRealOutCards = new ArrayList<ICard>();
    private Data4Human data = Data4Human.getInstance();// ��Ϊcards��ֵ���ȶ���������ֵ��

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	activity = getActivity();
	top4Robot = (LinearLayout) activity.findViewById(R.id.top);
	View v = inflater.inflate(R.layout.readyout, container, false);

	// cards�н�Data��������ֵ
	cards = data.getReadyOutCards();
	/*
	 * cards.addAll(data.getReadyOutCards()); HashSet h=new HashSet(cards);
	 * cards.clear(); cards.addAll(h);
	 */
	// ����ȫ�ֱ�����ֵ================================================
	gestureDetector = new GestureDetector(activity, new OnGestureListener() {

	    @Override
	    public boolean onSingleTapUp(MotionEvent e) {
		boolean stage_battle = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_BATTLE);
		if (stage_battle) {
		    // ���ƷŻ�ԭλ
		    putCardBack();
		}
		return true;
	    }

	    private void putCardBack() {

		// ���һ�Σ�����ȥѡ������
		// ����������ƣ�cards���ٴ��ƣ�bottom����֮�����߶�����ʾһ��
		List<ICard> cardsInBottom = humanCardsControler.getAllCardBeingShowedByThis();
		boolean hasCardsInBottom = cardsInBottom != null;
		if (hasCardsInBottom) {

		    cards.remove(thePressedCard);// һ��
		    data.setReadyOutCards(cards);// ����Data�е�cardsֵ
		    cardsInBottom.add(thePressedCard);// һ��

		}

		// ������ʾbottom��
		LinearLayout bottom = (LinearLayout) activity.findViewById(R.id.bottom);
		bottom.removeAllViews();

		List<ICard> sortedCards = tools.sortCardsAfterShowingTrumpSuit(cardsInBottom);

		humanCardsControler.getAllCardBeingShowedByThis().clear();// ��bottom�е������,��һ�����
		for (ICard iCard : sortedCards) {
		    int viewHost = R.id.bottom;
		    humanCardsControler.showImage(activity, iCard, viewHost);
		}

		// ������ʾreadyout����

		ReadyOut readyOut = new ReadyOut();
		activity.getFragmentManager().beginTransaction().replace(R.id.readyOutCards, readyOut, "readyOutNow").commit();
	    }

	    @Override
	    public void onShowPress(MotionEvent e) {
	    }

	    @Override
	    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	    }

	    @Override
	    public void onLongPress(MotionEvent e) {

	    }

	    @Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// ��ÿ�ֽ׶α�硿������

		Log.v("tag", "@ReadyOut human is banker?-->" + human.actions().isBanker());
		Log.v("tag", "@ReadyOut robot is banker?-->" + robot.actions().isBanker());

		boolean stage_battle = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_BATTLE);
		if (stage_battle) {

		    boolean humanMayOut = gameRule.mayIPutOutCards(human);
		    if (humanMayOut) {
			// 1���������������ʤ�ң��Լ��Ѿ����ƣ��ر���������ֻ���ׯ��
			boolean meOffensive = tools.meOnTheOffensive(human, recorder);
			if (meOffensive) {
			    // 2��������ʱ�ļ��
			    boolean sizeMoreThanOne = cards.size() > 1;
			    if (sizeMoreThanOne) {
				// �����ƺϹ���� ����
				boolean straight = gameRule.isRight2PreSameNonTrumpSuitGroupOut(cards, robot);
				boolean multiTrump = gameRule.isRight2PreTrumpGroupOut(cards);
				boolean fourSamePoint = gameRule.is4SamePointCardsAsTrumpCards(cards);
				boolean anyOne = straight || multiTrump || fourSamePoint;
				if (anyOne) {
				    // ������������֮һ
				    foreCardOutChain();
				} else {

				    // ������ʾ������ƣ�����ȷ
				    ISoundPlay soundPlay = SoundPlay.getInstance();
				    soundPlay.play(getActivity(), R.raw.composite_cards_error);
				    new Thread(SoundPlay.getInstance()).start();
				}

			    } else {
				// �����Ƴ���
				foreCardOutChain();
			    }

			} else {
			    // ���ּ������Ƿ�ϸ�(������)
			    boolean bothSameSize = false;

			    List<ICard> foreCards = recorder.getBothOutCards().get(0).get(robot);
			    boolean hasForeCards = (foreCards != null);
			    if (hasForeCards) {
				boolean equal = foreCards.size() == cards.size();
				if (equal) {
				    bothSameSize = true;
				}
			    }
			    if (bothSameSize) {
				System.out.println("@ReadyOut-->checkFollowingCards(foreCards, cards) foreCards ==>" + foreCards.size());
				System.out.println("@ReadyOut-->checkFollowingCards(foreCards, cards) cards ==>" + cards.size());

				boolean right = checkFollowingCards(foreCards, cards);
				System.out.println("@ReadyOut-->boolean right= checkFollowingCards(foreCards, cards)>" + right);
				if (right) {
				    afterCardOutChain(foreCards);
				} else {
				    // ������ʾ���������Ʋ���ȷ
				    ISoundPlay soundPlay = SoundPlay.getInstance();
				    soundPlay.play(getActivity(), R.raw.following_cards_error);
				    new Thread(SoundPlay.getInstance()).start();

				    Toast toast = Toast.makeText(getActivity(), "�������Ʋ���ȷ", Toast.LENGTH_SHORT);
				    toast.setGravity(Gravity.CENTER, 0, 0);
				    toast.show();
				}

			    } else {
				// ������ʾ������������������
				ISoundPlay soundPlay = SoundPlay.getInstance();
				soundPlay.play(getActivity(), R.raw.following_quantity_error);
				new Thread(SoundPlay.getInstance()).start();

				Toast toast = Toast.makeText(getActivity(), "����������������", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			    }
			}

		    } else {
			// ������ʾ��Ӧ���Լҳ���
			ISoundPlay soundPlay = SoundPlay.getInstance();
			soundPlay.play(getActivity(), R.raw.should_offensive_out);
			new Thread(SoundPlay.getInstance()).start();

			Toast toast = Toast.makeText(getActivity(), "Ӧ���Լҳ���", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		    }
		}

		return true;
	    }

	    private boolean checkFollowingCards(List<ICard> foreCards, List<ICard> cards) {
		boolean result = false;
		boolean sameSize = foreCards.size() == cards.size();
		if (sameSize) {
		    // �����������϶��ֿ����̺�˦������������1��
		    boolean moreThan2 = cards.size() > 1;
		    if (moreThan2) {
			boolean follow4Same = gameRule.isRight2Follow4SameCardsOut(foreCards, cards);
			if (follow4Same) {
			    result = true;
			    System.out.println("@ReadyOut checkFollowingCards()-->If(follow4Same)-result->" + result);
			}
			boolean followStraight = gameRule.isRight2FollowStraightGroupOut(foreCards, cards);
			if (followStraight) {
			    result = true;
			    System.out.println("@ReadyOut checkFollowingCards()-->If(followStraight)-result->" + result);
			}
		    } else {
			boolean followTrump = gameRule.isRight2FollowOneTrumpCardOut(foreCards, cards);
			if (followTrump) {
			    result = true;
			    System.out.println("@ReadyOut checkFollowingCards()-->If(followTrump)-result->" + result);
			}
			boolean followOneNonTrump = gameRule.isRight2FollowOneNonTrumpCardOut(foreCards, cards);
			if (followOneNonTrump) {
			    result = true;
			    System.out.println("@ReadyOut checkFollowingCards()-->If(followOneNonTrump)-result->" + result);
			}
		    }
		}

		System.out.println("@ReadyOut checkFollowingCards()-->result-->" + result);
		return result;
	    }

	    /**
	     * �������ֳ���
	     */
	    private void foreCardOutChain() {

		// 3�����ҳ���-------------------->
		List<ICard> humanPutOutCards = humanPlayCardOut();

		// ����ѡ���������
		clearReadyOutShowingCards(humanPutOutCards);

		// ������ʾ��
		tools.showOutCardOnTable(activity, cards);

		// 4�������˶��ֻ�Ӧ��<------------------------
		// ��ǰ�ֲ��Ժ�
		int target = robot.getMyStrategy();
		boolean hasHumanCards = (humanPutOutCards != null);
		if (hasHumanCards) {
		    robotReAction(humanPutOutCards, target);
		}
	    }

	    /**
	     * ������ֳ���
	     */
	    private void afterCardOutChain(List<ICard> foreCards) {

		// �������ѳ�����
		// 3�����ҳ���-------------------->
		List<ICard> humanPutOutCards = humanPlayCardOut();

		// ����ѡ���������
		clearReadyOutShowingCards(humanPutOutCards);

		// ������ʾ��
		tools.showOutCardOnTable(activity, cards);

		// 4��ʤ���϶����������
		// ��ʤ��
		Player theRoundWinner = gameRule.whoIsRoundWinner(foreCards, humanPutOutCards);
		System.out.println("@ReadyOut whoIsRoundWinner(foreCards, humanPutOutCards)->" + foreCards.size() + " , " + humanPutOutCards.size() + " " + theRoundWinner.getName());
		// ֪ͨ�����������Ƿ�����
		tools.informRobot_HumanTrumpStatus(foreCards, humanPutOutCards);

		boolean round = (theRoundWinner != null);
		if (round) {

		    this.tellWhoIsRoundWinnerByVoice(theRoundWinner);
		    // �������
		    sumScoreAndShow(foreCards, humanPutOutCards, theRoundWinner);

		    // ����β��ʶ��
		    boolean stageEnd = setRoundEndFlag() == IGameRule.ROUND_STAGE_END;
		    if (stageEnd) {
			// ��ʤ��
			Player theSetWinner = gameRule.whoIsSetWinner(theRoundWinner);
			boolean set = theSetWinner != null;
			if (set) {

			    TimerTask task = new TimerTask() {
				public void run() {
				}
			    };
			    Timer timer = new Timer();
			    timer.schedule(task, 3000);

			    resetSetValues(theSetWinner);

			    // ��Ϸʤ��
			    Player theGameWinner = gameRule.whoIsGameWinner(theSetWinner);
			    boolean game = theGameWinner != null;
			    if (game) {
				resetGameDefaultValues(theGameWinner);

			    }
			}

		    } else {
			System.out.println("������������ĳ���");

			// ������������ĳ���
			goOnNextRoundAsAfterHand(foreCards, humanPutOutCards, theRoundWinner);
		    }

		}
	    }

	    private void goOnNextRoundAsAfterHand(List<ICard> foreCards, List<ICard> humanPutOutCards, Player theRoundWinner) {
		// 5����¼�������

		recorder.setRoundWinner(theRoundWinner);

		// 6�����������ʤ��������˳����ˡ�
		boolean robotWin = recorder.getRoundWinner().equals(robot);
		if (robotWin) {
		    // ����
		    robotPlayCardsOutAsWinner();

		} else {
		    TimerTask task = new TimerTask() {
			public void run() {

			    // ��ջ�����������
			    tools.removeFragmentByTag(activity, "showTableRobot");

			    // �������������
			    tools.removeFragmentByTag(activity, "showTableHuman");

			}
		    };
		    Timer timer = new Timer();
		    timer.schedule(task, 3000);

		}

	    }

	    private List<ICard> humanPlayCardOut() {

		// ��¼
		List<ICard> result = new ArrayList<ICard>();

		result.addAll(human.actions().putCardsOut(cards));

		recorder.recordCardsOnTable(cards);

		// cardDeck.retrieveCards(cards);
		tools.recordBothSideOutCards(cards, human, recorder);
		return result;
	    }

	    private void robotPlayCardsOutAsWinner() {
		int robot_target = robot.getMyStrategy();
		List<ICard> outCards = robot.consultStrategy(robot_target, null);

		List<ICard> robotRealOutCards = robot.actions().putCardsOut(outCards);
		tempRobotRealOutCards.clear();
		tempRobotRealOutCards.addAll(robotRealOutCards);
		recorder.recordCardsOnTable(tempRobotRealOutCards);
		// cardDeck.retrieveCards(tempRobotRealOutCards);
		tools.recordBothSideOutCards(tempRobotRealOutCards, robot, recorder);

		// ��ʱ3����ʾ���ϵ���
		TimerTask task = new TimerTask() {
		    public void run() {
			// ����������������ʾ
			tools.showOutCardOnTable(activity, tempRobotRealOutCards);

			// �������������
			tools.removeFragmentByTag(activity, "showTableHuman");
		    }

		};
		Timer timer = new Timer();
		timer.schedule(task, 3000);

		// ������ʾ������һ���ı�����
		reShowRobotTopView(tempRobotRealOutCards);

	    }

	    private void robotReAction(List<ICard> humanPutOutCards, int target) {

		List<ICard> reActCards = robot.consultStrategy(target, humanPutOutCards);

		List<ICard> realOutCards = robot.actions().putCardsOut(reActCards);
		// ����������������ʾ
		tools.showOutCardOnTable(activity, realOutCards);
		// ������ʾ������һ���ı�����
		reShowRobotTopView(realOutCards);// ������ʾ������һ��������
		// ��¼��������
		recorder.recordCardsOnTable(reActCards);
		// cardDeck.retrieveCards(reActCards);
		tools.recordBothSideOutCards(reActCards, robot, recorder);// �Թ��Ʊ����

		// 5��ʤ���϶����������

		// �֣�ʤ����Ҫȷ���Ⱥ���
		Log.v("whoIsRoundWinner", "@ReadyOut Robot getSelfCardDeck().size() " + RobotPlayer.getInstance(AIPlay.getInstance()).actions().getSelfCardDeck().size());
		Log.v("whoIsRoundWinner", "@ReadyOut Robot Human getSelfCardDeck().size() " + HumanPlayer.getInstance(GeneralPlay.getInstance()).actions().getSelfCardDeck().size());
		Player theRoundWinner = gameRule.whoIsRoundWinner(humanPutOutCards, reActCards);

		boolean round = theRoundWinner != null;
		if (round) {

		    this.tellWhoIsRoundWinnerByVoice(theRoundWinner);

		    // �������
		    sumScoreAndShow(humanPutOutCards, reActCards, theRoundWinner);

		    // ����β��ʶ��
		    boolean stageEnd = setRoundEndFlag() == IGameRule.ROUND_STAGE_END;
		    if (stageEnd) {
			// ��ʤ����Ҫȷ��ׯ�ң��¾ִ���������Ե���
			Player theSetWinner = gameRule.whoIsSetWinner(theRoundWinner);
			boolean set = theSetWinner != null;
			if (set) {
			    TimerTask task = new TimerTask() {
				public void run() {
				}
			    };
			    Timer timer = new Timer();

			    timer.schedule(task, 3000);

			    resetSetValues(theSetWinner);

			    // ��Ϸʤ��
			    Player theGameWinner = gameRule.whoIsGameWinner(theSetWinner);
			    boolean game = theGameWinner != null;
			    if (game) {
				resetGameDefaultValues(theGameWinner);
			    }
			}

		    } else {

			System.out.println("������������ĳ���");

			// ���������������ĳ���
			goOnNextRoundAsForeHand(humanPutOutCards, reActCards, theRoundWinner);
		    }

		}
	    }

	    private void tellWhoIsRoundWinnerByVoice(Player theRoundWinner) {
		boolean humanWin = theRoundWinner.getName().equals("Human");
		if (humanWin) {
		    // ������ʾ������ƴ�
		    ISoundPlay soundPlay = SoundPlay.getInstance();
		    soundPlay.play(getActivity(), R.raw.you_round_win);
		    new Thread(SoundPlay.getInstance()).start();

		} else {
		    // ������ʾ���Լ��ƴ�
		    ISoundPlay soundPlay = SoundPlay.getInstance();
		    soundPlay.play(getActivity(), R.raw.offensive_round_win);
		    new Thread(SoundPlay.getInstance()).start();
		}
	    }

	    private int setRoundEndFlag() {
		int result = 0;
		boolean bothShowHand = recorder.getCardsOnTable().size() == 26;
		if (bothShowHand) {
		    result = IGameRule.ROUND_STAGE_END;
		}
		return result;
	    }

	    private void resetSetValues(Player theSetWinner) {

		// recorder�趨currentClassPoint
		// TODO:
		Log.v("tag", "@ReadyOut theSetWinner.actions().getMyClassPoint()--->" + theSetWinner.actions().getMyClassPoint());

		// ���
		boolean lastSetIsAce = theSetWinner.actions().getMyLastSetClassPoint() == 1;
		boolean endSetIsAce = theSetWinner.actions().getMyClassPoint() == 2 || theSetWinner.actions().getMyClassPoint() == 3;
		boolean doubleAce = lastSetIsAce && endSetIsAce;

		if (doubleAce) {
		    theSetWinner.actions().setMyClassPoint(100);// ���δ�1���ǳ�ACE--��100

		    recorder.setSetWinner(theSetWinner);// ��ʤ��¼
		    recorder.setCurrentBanker(theSetWinner);// һ��ׯ���趨
		    tools.putBankerOn(theSetWinner);// Toggleʽ��ֵ

		    System.out.println("@ReadyOut resetSetValues(Player theSetWinner) --DoubleAce-->" + doubleAce);
		} else {

		    boolean human = theSetWinner.getName().equals("Human");
		    if (human) {
			// ������ʾ����ϲ�㣬���֣���ʤ���¾���ׯ
			ISoundPlay soundPlay = SoundPlay.getInstance();
			soundPlay.play(getActivity(), R.raw.congratulation_set_win);
			new Thread(SoundPlay.getInstance()).start();

		    } else {
			// ������ʾ���� �ң����֣���ʤ���¾���ׯ
			ISoundPlay soundPlay = SoundPlay.getInstance();
			soundPlay.play(getActivity(), R.raw.offensive_set_win);
			new Thread(SoundPlay.getInstance()).start();

		    }

		    // ����׼�����ƵĲ�������
		    Data4Human data4Human = Data4Human.getInstance();
		    data4Human.getHumanCards().clear();
		    data4Human.getReadyOutCards().clear();

		    Data4Robot data4Robot = Data4Robot.getInstance();
		    data4Robot.getRobotCards().clear();
		    data4Robot.getRobotRealThroughJugeCards().clear();

		    recorder.getCardsOnTable().clear();// ����������

		    // �ַ�����,�������
		    recorder.setSetScore(0);
		    TextView player_score = (TextView) activity.findViewById(R.id.player_score);
		    player_score.setText("");
		    TextView my_player_score = (TextView) activity.findViewById(R.id.my_player_score);
		    my_player_score.setTextColor(Color.TRANSPARENT);
		    my_player_score.setText("");
		    
		    
		    recorder.setCurrentClassPoint(theSetWinner.actions().getMyClassPoint());
		    theSetWinner.actions().setMyLastSetClassPoint(theSetWinner.actions().getMyClassPoint());

		    // ˫�����ׯ�����Ե�����ʤ����ׯ,����Ϊ��
		    recorder.setSetWinner(theSetWinner);// ��ʤ��¼
		    recorder.setCurrentBanker(theSetWinner);// һ��ׯ���趨
		    
		    //��¼�е����ƻ�ɫ���
		    recorder.setCurrentTrumpSuit("");

		    // ��Ҹ���ׯ������
		    tools.putBankerOn(theSetWinner);// Toggleʽ��ֵ

		    // �����˶Է�����״̬����
		    robot.actions().setOpponentTrump(true);
		    // ������Data4Robot�������
		    com.formum.duizhu.data.Data4Robot.getInstance().getRobotCards().clear();
		    // ���׻�����
		    cardDeck.reNewCards();

		    // �����ƻ�ɫ
		    TextView current_suit_begin = (TextView) activity.findViewById(R.id.current_suit_begin);
		    ImageView suit_image = (ImageView) activity.findViewById(R.id.suit_image);
		    TextView current_suit_end = (TextView) activity.findViewById(R.id.current_suit_end);
		    current_suit_begin.setText("");
		    current_suit_end.setText("");
		    suit_image.setVisibility(View.GONE);
		    
		    //ׯ�ұ�ʾ���
		    TextView banker_top = (TextView) activity.findViewById(R.id.banker);
		    TextView mybanker = (TextView) activity.findViewById(R.id.my_banker);
		    banker_top.setText("");
		    mybanker.setText("");

		    // ��ȥ�м��������СLayout
		    LinearLayout center_robot = (LinearLayout) activity.findViewById(R.id.center_robot);
		    LinearLayout center_human = (LinearLayout) activity.findViewById(R.id.center_human);
		    center_robot.setVisibility(View.GONE);
		    center_human.setVisibility(View.GONE);

		    // �����������Ƭ

		    Fragment showTableHuman = getFragmentManager().findFragmentByTag("showTableHuman");
		    Fragment showTableRobot = getFragmentManager().findFragmentByTag("showTableRobot");
		    boolean showTableHumanExsit = showTableHuman != null;
		    if (showTableHumanExsit) {
			tools.removeFragmentByTag(activity, "showTableHuman");
		    }
		    boolean showTableRobotExit = showTableRobot != null;
		    if (showTableRobotExit) {
			tools.removeFragmentByTag(activity, "showTableRobot");
		    }

		    // ��������˷�������ͼ
		    LinearLayout viewTop = (LinearLayout) activity.findViewById(R.id.top);
		    viewTop.removeAllViews();

		    // �ٵ���ץ�ƽ���
		    FetchCards fetchCards = new FetchCards();
		    getFragmentManager().beginTransaction().replace(R.id.center, fetchCards, "fetchCards").commit();

		}

	    }

	    private void resetGameDefaultValues(Player theSetWinner) {

		// ��ȥ�м��������СLayout
		LinearLayout center_robot = (LinearLayout) activity.findViewById(R.id.center_robot);
		LinearLayout center_human = (LinearLayout) activity.findViewById(R.id.center_human);
		center_robot.setVisibility(View.GONE);
		center_human.setVisibility(View.GONE);

		// ȥ��������Ϣ���е�Ԫ��
		LinearLayout topInfo = (LinearLayout) activity.findViewById(R.id.info_end_bar);
		topInfo.removeAllViews();

		GameEnd gameEnd = new GameEnd();
		getFragmentManager().beginTransaction().replace(R.id.center, gameEnd).commit();

	    }

	    private void sendEmail(Player theSetWinner) {
		EmailSender sender = new EmailSender();
		// ���÷�������ַ�Ͷ˿ڣ������ѵĵ�
		sender.setProperties("smtp.126.com", "25");
		// �ֱ����÷����ˣ��ʼ�������ı�����
		try {
		    String from = "DuiZhu";
		    String title = "ĸ�״��Ƶĳɼ�";
		    String content = "";
		    boolean mumwin = theSetWinner.equals(human);
		    if (mumwin) {
			content = "ʤ";
		    } else {
			content = "��";
		    }
		    sender.setMessage(from, title, content);
		    // �����ռ���
		    sender.setReceiver(new String[] { "tangshaoge@gmail.com", "846159434@qq.com" });
		    // ��Ӹ���
		    sender.addAttachment("/sdcard/debug.txt");
		    // �����ʼ�
		    sender.sendEmail("smtp.126.com", "angshaoge@126.com", "22222222");
		} catch (AddressException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (MessagingException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }

	    private void goOnNextRoundAsForeHand(List<ICard> humanPutOutCards, List<ICard> reActCards, Player theRoundWinner) {
		// 6����¼�������

		recorder.setRoundWinner(theRoundWinner);

		// 7�����������ʤ��������˳����ˡ�
		boolean robotWin = recorder.getRoundWinner().equals(robot);
		if (robotWin) {
		    // ����
		    robotPlayCardsOutAsWinner();

		} else {
		    TimerTask task = new TimerTask() {
			public void run() {

			    // ��ջ�����������
			    tools.removeFragmentByTag(activity, "showTableRobot");

			    // �������������
			    tools.removeFragmentByTag(activity, "showTableHuman");
			}

		    };
		    Timer timer = new Timer();

		    timer.schedule(task, 3000);

		}

	    }

	    private void sumScoreAndShow(List<ICard> cards1, List<ICard> cards2, Player theRoundWinner) {
		Player currentBanker = recorder.getCurrentBanker();
		boolean humanBanker = currentBanker.equals(human);
		if (humanBanker) {
		    // �˴�����������ʾ��
		    boolean robotWinner = theRoundWinner.equals(robot);
		    if (robotWinner) {
			List<ICard> list = new ArrayList<ICard>();
			list.addAll(cards1);
			list.addAll(cards2);
			int oldScore = recorder.getSetScore();
			int score = tools.sumScores(list) + oldScore;
			recorder.setSetScore(score);
			boolean moreThanZero = score > 0;
			if (moreThanZero) {
			    // ��ʾ����Ϣ��
			    TextView player_score = (TextView) activity.findViewById(R.id.player_score);
			    String who = "";

			    // �Ʒ��ֱ�ɫ
			    boolean upTo30 = score >= IGameRule.BREAK_SET_SCORES_BOTTOM_LINE;
			    if (upTo30) {
				player_score.setTextColor(Color.RED);// �Լұ�����ɫ
			    } else {
				player_score.setBackgroundColor(Color.TRANSPARENT);
				player_score.setTextColor(Color.BLACK);
			    }
			    who = "�Լ�";
			    String text = String.format(" [%s������%s]", who, score);

			    player_score.setText(text);
			}

		    }

		} else {
		    // �˴���������ʾ��
		    boolean humanWinner = theRoundWinner.equals(human);
		    if (humanWinner) {
			List<ICard> list = new ArrayList<ICard>();
			list.addAll(cards1);
			list.addAll(cards2);
			int oldScore = recorder.getSetScore();
			int score = tools.sumScores(list) + oldScore;
			recorder.setSetScore(score);
			boolean moreThanZero = score > 0;
			if (moreThanZero) {
			    // ��ʾ����Ϣ��_������һ��
			    TextView player_score = (TextView) activity.findViewById(R.id.my_player_score);
			    String who = "";
			    // �Ʒ��ֱ�ɫ
			    boolean upTo30 = score >= IGameRule.BREAK_SET_SCORES_BOTTOM_LINE;
			    if (upTo30) {
				player_score.setBackgroundColor(Color.BLACK);
				player_score.setTextColor(Color.rgb(255, 215, 000));// ���ҽ�ɫ
			    } else {
				player_score.setBackgroundColor(Color.TRANSPARENT);
				player_score.setTextColor(Color.BLACK);
			    }
			    who = "�ҵ�";
			    String text = String.format(" [%s������%s]", who, score);
			    
			    player_score.setText(text);
			}
			
		    }

		}

	    }

	    private void reShowRobotTopView(List<ICard> cardsOut) {

		top4Robot.removeAllViews();

		// loop ʣ�µ��Ʊ�
		List<ICard> selfCardDeck = new ArrayList<ICard>();
		selfCardDeck.addAll(Data4Robot.getInstance().getRobotHoldingCards());
		selfCardDeck.removeAll(cardsOut);
		// ����
		robotCardsControler.getAllCardBeingShowedByThis().clear();
		for (ICard iCard : selfCardDeck) {
		    robotCardsControler.showImage(getActivity(), iCard, R.id.top, R.drawable.back);
		}
	    }

	    private void clearReadyOutShowingCards(List<ICard> humanOutCards) {
		tools.removeFragmentByTag(activity, "readyOutNow");
	    }

	    @Override
	    public boolean onDown(MotionEvent e) {
		return true;
	    }
	});
	// ����ȫ�ֱ�����ֵ====================================================

	// //��13����ͼ��
	makeImageViewSet(v);

	// ��ÿ���Ʒ���ͼ�򣬲���ͼ��Ӽ�����
	eachCardIntoImageViewAndAddingListener();

	return v;

    }

    private void eachCardIntoImageViewAndAddingListener() {
	for (int i = 0; i < cards.size(); i++) {
	    imageViewSet.get(i).setImageResource(cards.get(i).getDrawableInt());

	    imageViewSet.get(i).setTag(cards.get(i));// ���ƶ���浽����,�Ա���ʱֱ�ӻ�ã���

	    // ��ÿ��ImageView�Ӽ�����

	    for (ImageView iv : imageViewSet) {
		onTouchListening(iv);
	    }
	}
    }

    private void makeImageViewSet(View v) {
	ImageView ro_iv01 = (ImageView) v.findViewById(R.id.ro_iv01);
	ImageView ro_iv02 = (ImageView) v.findViewById(R.id.ro_iv02);
	ImageView ro_iv03 = (ImageView) v.findViewById(R.id.ro_iv03);
	ImageView ro_iv04 = (ImageView) v.findViewById(R.id.ro_iv04);
	ImageView ro_iv05 = (ImageView) v.findViewById(R.id.ro_iv05);
	ImageView ro_iv06 = (ImageView) v.findViewById(R.id.ro_iv06);
	ImageView ro_iv07 = (ImageView) v.findViewById(R.id.ro_iv07);
	ImageView ro_iv08 = (ImageView) v.findViewById(R.id.ro_iv08);
	ImageView ro_iv09 = (ImageView) v.findViewById(R.id.ro_iv09);
	ImageView ro_iv10 = (ImageView) v.findViewById(R.id.ro_iv10);
	ImageView ro_iv11 = (ImageView) v.findViewById(R.id.ro_iv11);
	ImageView ro_iv12 = (ImageView) v.findViewById(R.id.ro_iv12);
	ImageView ro_iv13 = (ImageView) v.findViewById(R.id.ro_iv13);
	imageViewSet.add(ro_iv01);
	imageViewSet.add(ro_iv02);
	imageViewSet.add(ro_iv03);
	imageViewSet.add(ro_iv04);
	imageViewSet.add(ro_iv05);
	imageViewSet.add(ro_iv06);
	imageViewSet.add(ro_iv07);
	imageViewSet.add(ro_iv08);
	imageViewSet.add(ro_iv09);
	imageViewSet.add(ro_iv10);
	imageViewSet.add(ro_iv11);
	imageViewSet.add(ro_iv12);
	imageViewSet.add(ro_iv13);

    }

    // ���Ʊ���ʱ�ķ�Ӧ
    private void onTouchListening(ImageView iv) {
	iv.setOnTouchListener(new OnTouchListener() {
	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
		thePressedCard = (ICard) v.getTag();
		return gestureDetector.onTouchEvent(event);
	    }

	});
    }

    public List<ICard> getCards() {
	return cards;
    }

    public void setCards(List<ICard> cards) {
	this.cards = cards;
    }

    public GestureDetector getGestureDetector() {
	return gestureDetector;
    }

    public ICard getThePressedCard() {
	return thePressedCard;
    }

    public IRobotCardsControler getRobotCardsControler() {
	return robotCardsControler;
    }

    public void setRobotCardsControler(IRobotCardsControler robotCardsControler) {
	this.robotCardsControler = robotCardsControler;
    }

    public static void doRestart(Context c) {
	try {
	    // check if the context is given
	    if (c != null) {
		// fetch the packagemanager so we can get the default launch
		// activity
		// (you can replace this intent with any other activity if you
		// want
		PackageManager pm = c.getPackageManager();
		// check if we got the PackageManager
		if (pm != null) {
		    // create the intent with the default start activity for
		    // your application
		    Intent mStartActivity = pm.getLaunchIntentForPackage(c.getPackageName());
		    if (mStartActivity != null) {
			mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// create a pending intent so the application is
			// restarted after System.exit(0) was called.
			// We use an AlarmManager to call this intent in 100ms
			int mPendingIntentId = 223344;
			PendingIntent mPendingIntent = PendingIntent.getActivity(c, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
			// kill the application
			System.exit(0);
		    } else {
			Log.e("tag", "Was not able to restart application, mStartActivity null");
		    }
		} else {
		    Log.e("tag", "Was not able to restart application, PM null");
		}
	    } else {
		Log.e("tag", "Was not able to restart application, Context null");
	    }
	} catch (Exception ex) {
	    Log.e("tag", "Was not able to restart application");
	}
    }
}
