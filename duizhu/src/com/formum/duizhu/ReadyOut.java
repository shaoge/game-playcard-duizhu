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
    private LinearLayout top4Robot;// 机器人放牌档位
    private ICardDeck cardDeck = CardDeck.getInstance();

    private Activity activity;

    private IHumanCardsControler humanCardsControler = HumanCardsControler.getInstance();
    private IRobotCardsControler robotCardsControler = RobotCardsControler.getInstance();

    private List<ICard> tempRobotRealOutCards = new ArrayList<ICard>();
    private Data4Human data = Data4Human.getInstance();// 因为cards的值不稳定，用它传值。

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	activity = getActivity();
	top4Robot = (LinearLayout) activity.findViewById(R.id.top);
	View v = inflater.inflate(R.layout.readyout, container, false);

	// cards承接Data传过来的值
	cards = data.getReadyOutCards();
	/*
	 * cards.addAll(data.getReadyOutCards()); HashSet h=new HashSet(cards);
	 * cards.clear(); cards.addAll(h);
	 */
	// 手势全局变量赋值================================================
	gestureDetector = new GestureDetector(activity, new OnGestureListener() {

	    @Override
	    public boolean onSingleTapUp(MotionEvent e) {
		boolean stage_battle = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_BATTLE);
		if (stage_battle) {
		    // 将牌放回原位
		    putCardBack();
		}
		return true;
	    }

	    private void putCardBack() {

		// 点击一次，返回去选出的牌
		// 如果手中用牌，cards减少此牌；bottom增加之；两边都各显示一次
		List<ICard> cardsInBottom = humanCardsControler.getAllCardBeingShowedByThis();
		boolean hasCardsInBottom = cardsInBottom != null;
		if (hasCardsInBottom) {

		    cards.remove(thePressedCard);// 一减
		    data.setReadyOutCards(cards);// 更新Data中的cards值
		    cardsInBottom.add(thePressedCard);// 一加

		}

		// 更新显示bottom牌
		LinearLayout bottom = (LinearLayout) activity.findViewById(R.id.bottom);
		bottom.removeAllViews();

		List<ICard> sortedCards = tools.sortCardsAfterShowingTrumpSuit(cardsInBottom);

		humanCardsControler.getAllCardBeingShowedByThis().clear();// 将bottom中的牌清空,多一层防范
		for (ICard iCard : sortedCards) {
		    int viewHost = R.id.bottom;
		    humanCardsControler.showImage(activity, iCard, viewHost);
		}

		// 更新显示readyout的牌

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
		// 【每局阶段标界】：打牌

		Log.v("tag", "@ReadyOut human is banker?-->" + human.actions().isBanker());
		Log.v("tag", "@ReadyOut robot is banker?-->" + robot.actions().isBanker());

		boolean stage_battle = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_BATTLE);
		if (stage_battle) {

		    boolean humanMayOut = gameRule.mayIPutOutCards(human);
		    if (humanMayOut) {
			// 1、常规情况：上轮胜家，对家已经出牌；特别情况：开局还是庄家
			boolean meOffensive = tools.meOnTheOffensive(human, recorder);
			if (meOffensive) {
			    // 2、当先手时的检查
			    boolean sizeMoreThanOne = cards.size() > 1;
			    if (sizeMoreThanOne) {
				// 多张牌合规检查后 出牌
				boolean straight = gameRule.isRight2PreSameNonTrumpSuitGroupOut(cards, robot);
				boolean multiTrump = gameRule.isRight2PreTrumpGroupOut(cards);
				boolean fourSamePoint = gameRule.is4SamePointCardsAsTrumpCards(cards);
				boolean anyOne = straight || multiTrump || fourSamePoint;
				if (anyOne) {
				    // 符合三种条件之一
				    foreCardOutChain();
				} else {

				    // 语音提示：组合牌，不正确
				    ISoundPlay soundPlay = SoundPlay.getInstance();
				    soundPlay.play(getActivity(), R.raw.composite_cards_error);
				    new Thread(SoundPlay.getInstance()).start();
				}

			    } else {
				// 单张牌出牌
				foreCardOutChain();
			    }

			} else {
			    // 后手检查出牌是否合格(黑名单)
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
				    // 语音提示：跟出的牌不正确
				    ISoundPlay soundPlay = SoundPlay.getInstance();
				    soundPlay.play(getActivity(), R.raw.following_cards_error);
				    new Thread(SoundPlay.getInstance()).start();

				    Toast toast = Toast.makeText(getActivity(), "跟出的牌不正确", Toast.LENGTH_SHORT);
				    toast.setGravity(Gravity.CENTER, 0, 0);
				    toast.show();
				}

			    } else {
				// 语音提示：跟出的牌数量不对
				ISoundPlay soundPlay = SoundPlay.getInstance();
				soundPlay.play(getActivity(), R.raw.following_quantity_error);
				new Thread(SoundPlay.getInstance()).start();

				Toast toast = Toast.makeText(getActivity(), "跟出的牌数量不对", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			    }
			}

		    } else {
			// 语音提示：应当对家出牌
			ISoundPlay soundPlay = SoundPlay.getInstance();
			soundPlay.play(getActivity(), R.raw.should_offensive_out);
			new Thread(SoundPlay.getInstance()).start();

			Toast toast = Toast.makeText(getActivity(), "应当对家出牌", Toast.LENGTH_SHORT);
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
		    // 先用数量将断定分开：翁和甩是牌张数大于1的
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
	     * 人类先手出牌
	     */
	    private void foreCardOutChain() {

		// 3、本家出牌-------------------->
		List<ICard> humanPutOutCards = humanPlayCardOut();

		// 人类选出的牌清掉
		clearReadyOutShowingCards(humanPutOutCards);

		// 桌面显示牌
		tools.showOutCardOnTable(activity, cards);

		// 4、机器人对手回应牌<------------------------
		// 当前局策略号
		int target = robot.getMyStrategy();
		boolean hasHumanCards = (humanPutOutCards != null);
		if (hasHumanCards) {
		    robotReAction(humanPutOutCards, target);
		}
	    }

	    /**
	     * 人类后手出牌
	     */
	    private void afterCardOutChain(List<ICard> foreCards) {

		// 机器人已出牌了
		// 3、本家出牌-------------------->
		List<ICard> humanPutOutCards = humanPlayCardOut();

		// 人类选出的牌清掉
		clearReadyOutShowingCards(humanPutOutCards);

		// 桌面显示牌
		tools.showOutCardOnTable(activity, cards);

		// 4、胜负断定：三种情况
		// 轮胜负
		Player theRoundWinner = gameRule.whoIsRoundWinner(foreCards, humanPutOutCards);
		System.out.println("@ReadyOut whoIsRoundWinner(foreCards, humanPutOutCards)->" + foreCards.size() + " , " + humanPutOutCards.size() + " " + theRoundWinner.getName());
		// 通知机器人人类是否有主
		tools.informRobot_HumanTrumpStatus(foreCards, humanPutOutCards);

		boolean round = (theRoundWinner != null);
		if (round) {

		    this.tellWhoIsRoundWinnerByVoice(theRoundWinner);
		    // 处理分数
		    sumScoreAndShow(foreCards, humanPutOutCards, theRoundWinner);

		    // 【轮尾标识】
		    boolean stageEnd = setRoundEndFlag() == IGameRule.ROUND_STAGE_END;
		    if (stageEnd) {
			// 局胜负
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

			    // 游戏胜负
			    Player theGameWinner = gameRule.whoIsGameWinner(theSetWinner);
			    boolean game = theGameWinner != null;
			    if (game) {
				resetGameDefaultValues(theGameWinner);

			    }
			}

		    } else {
			System.out.println("继续调出下面的程序");

			// 继续调出下面的程序
			goOnNextRoundAsAfterHand(foreCards, humanPutOutCards, theRoundWinner);
		    }

		}
	    }

	    private void goOnNextRoundAsAfterHand(List<ICard> foreCards, List<ICard> humanPutOutCards, Player theRoundWinner) {
		// 5、记录相关数据

		recorder.setRoundWinner(theRoundWinner);

		// 6、如果机器人胜，则机器人出牌了。
		boolean robotWin = recorder.getRoundWinner().equals(robot);
		if (robotWin) {
		    // 出牌
		    robotPlayCardsOutAsWinner();

		} else {
		    TimerTask task = new TimerTask() {
			public void run() {

			    // 清空机器人桌面牌
			    tools.removeFragmentByTag(activity, "showTableRobot");

			    // 清空人类桌面牌
			    tools.removeFragmentByTag(activity, "showTableHuman");

			}
		    };
		    Timer timer = new Timer();
		    timer.schedule(task, 3000);

		}

	    }

	    private List<ICard> humanPlayCardOut() {

		// 记录
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

		// 延时3秒显示桌上的牌
		TimerTask task = new TimerTask() {
		    public void run() {
			// 机器人牌在桌上显示
			tools.showOutCardOnTable(activity, tempRobotRealOutCards);

			// 清空人类桌面牌
			tools.removeFragmentByTag(activity, "showTableHuman");
		    }

		};
		Timer timer = new Timer();
		timer.schedule(task, 3000);

		// 重新显示机器人一方的背面牌
		reShowRobotTopView(tempRobotRealOutCards);

	    }

	    private void robotReAction(List<ICard> humanPutOutCards, int target) {

		List<ICard> reActCards = robot.consultStrategy(target, humanPutOutCards);

		List<ICard> realOutCards = robot.actions().putCardsOut(reActCards);
		// 机器人牌在桌上显示
		tools.showOutCardOnTable(activity, realOutCards);
		// 重新显示机器人一方的背面牌
		reShowRobotTopView(realOutCards);// 重新显示机器人一方的牌数
		// 记录机器人牌
		recorder.recordCardsOnTable(reActCards);
		// cardDeck.retrieveCards(reActCards);
		tools.recordBothSideOutCards(reActCards, robot, recorder);// 对攻牌保存地

		// 5、胜负断定：三种情况

		// 轮，胜负，要确定先后手
		Log.v("whoIsRoundWinner", "@ReadyOut Robot getSelfCardDeck().size() " + RobotPlayer.getInstance(AIPlay.getInstance()).actions().getSelfCardDeck().size());
		Log.v("whoIsRoundWinner", "@ReadyOut Robot Human getSelfCardDeck().size() " + HumanPlayer.getInstance(GeneralPlay.getInstance()).actions().getSelfCardDeck().size());
		Player theRoundWinner = gameRule.whoIsRoundWinner(humanPutOutCards, reActCards);

		boolean round = theRoundWinner != null;
		if (round) {

		    this.tellWhoIsRoundWinnerByVoice(theRoundWinner);

		    // 处理分数
		    sumScoreAndShow(humanPutOutCards, reActCards, theRoundWinner);

		    // 【轮尾标识】
		    boolean stageEnd = setRoundEndFlag() == IGameRule.ROUND_STAGE_END;
		    if (stageEnd) {
			// 局胜负，要确定庄家，下局打点数，各自点数
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

			    // 游戏胜负
			    Player theGameWinner = gameRule.whoIsGameWinner(theSetWinner);
			    boolean game = theGameWinner != null;
			    if (game) {
				resetGameDefaultValues(theGameWinner);
			    }
			}

		    } else {

			System.out.println("继续调出下面的程序");

			// 继续下面调出下面的程序
			goOnNextRoundAsForeHand(humanPutOutCards, reActCards, theRoundWinner);
		    }

		}
	    }

	    private void tellWhoIsRoundWinnerByVoice(Player theRoundWinner) {
		boolean humanWin = theRoundWinner.getName().equals("Human");
		if (humanWin) {
		    // 语音提示：你的牌大
		    ISoundPlay soundPlay = SoundPlay.getInstance();
		    soundPlay.play(getActivity(), R.raw.you_round_win);
		    new Thread(SoundPlay.getInstance()).start();

		} else {
		    // 语音提示：对家牌大
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

		// recorder设定currentClassPoint
		// TODO:
		Log.v("tag", "@ReadyOut theSetWinner.actions().getMyClassPoint()--->" + theSetWinner.actions().getMyClassPoint());

		// 结局
		boolean lastSetIsAce = theSetWinner.actions().getMyLastSetClassPoint() == 1;
		boolean endSetIsAce = theSetWinner.actions().getMyClassPoint() == 2 || theSetWinner.actions().getMyClassPoint() == 3;
		boolean doubleAce = lastSetIsAce && endSetIsAce;

		if (doubleAce) {
		    theSetWinner.actions().setMyClassPoint(100);// 两次打1才是成ACE--归100

		    recorder.setSetWinner(theSetWinner);// 局胜记录
		    recorder.setCurrentBanker(theSetWinner);// 一轮庄家设定
		    tools.putBankerOn(theSetWinner);// Toggle式赋值

		    System.out.println("@ReadyOut resetSetValues(Player theSetWinner) --DoubleAce-->" + doubleAce);
		} else {

		    boolean human = theSetWinner.getName().equals("Human");
		    if (human) {
			// 语音提示：恭喜你，本局，获胜，下局做庄
			ISoundPlay soundPlay = SoundPlay.getInstance();
			soundPlay.play(getActivity(), R.raw.congratulation_set_win);
			new Thread(SoundPlay.getInstance()).start();

		    } else {
			// 语音提示：对 家，本局，获胜，下局做庄
			ISoundPlay soundPlay = SoundPlay.getInstance();
			soundPlay.play(getActivity(), R.raw.offensive_set_win);
			new Thread(SoundPlay.getInstance()).start();

		    }

		    // 清理准备出牌的残留数据
		    Data4Human data4Human = Data4Human.getInstance();
		    data4Human.getHumanCards().clear();
		    data4Human.getReadyOutCards().clear();

		    Data4Robot data4Robot = Data4Robot.getInstance();
		    data4Robot.getRobotCards().clear();
		    data4Robot.getRobotRealThroughJugeCards().clear();

		    recorder.getCardsOnTable().clear();// 桌面牌清零

		    // 局分清零,表现清空
		    recorder.setSetScore(0);
		    TextView player_score = (TextView) activity.findViewById(R.id.player_score);
		    player_score.setText("");
		    TextView my_player_score = (TextView) activity.findViewById(R.id.my_player_score);
		    my_player_score.setTextColor(Color.TRANSPARENT);
		    my_player_score.setText("");
		    
		    
		    recorder.setCurrentClassPoint(theSetWinner.actions().getMyClassPoint());
		    theSetWinner.actions().setMyLastSetClassPoint(theSetWinner.actions().getMyClassPoint());

		    // 双方玩家庄家属性调整：胜者做庄,败者为闲
		    recorder.setSetWinner(theSetWinner);// 局胜记录
		    recorder.setCurrentBanker(theSetWinner);// 一轮庄家设定
		    
		    //记录中的主牌花色清空
		    recorder.setCurrentTrumpSuit("");

		    // 玩家各自庄闲属性
		    tools.putBankerOn(theSetWinner);// Toggle式赋值

		    // 机器人对方主牌状态设真
		    robot.actions().setOpponentTrump(true);
		    // 机器人Data4Robot备份清空
		    com.formum.duizhu.data.Data4Robot.getInstance().getRobotCards().clear();
		    // 牌套换新牌
		    cardDeck.reNewCards();

		    // 隐主牌花色
		    TextView current_suit_begin = (TextView) activity.findViewById(R.id.current_suit_begin);
		    ImageView suit_image = (ImageView) activity.findViewById(R.id.suit_image);
		    TextView current_suit_end = (TextView) activity.findViewById(R.id.current_suit_end);
		    current_suit_begin.setText("");
		    current_suit_end.setText("");
		    suit_image.setVisibility(View.GONE);
		    
		    //庄家表示清空
		    TextView banker_top = (TextView) activity.findViewById(R.id.banker);
		    TextView mybanker = (TextView) activity.findViewById(R.id.my_banker);
		    banker_top.setText("");
		    mybanker.setText("");

		    // 隐去中间界面两个小Layout
		    LinearLayout center_robot = (LinearLayout) activity.findViewById(R.id.center_robot);
		    LinearLayout center_human = (LinearLayout) activity.findViewById(R.id.center_human);
		    center_robot.setVisibility(View.GONE);
		    center_human.setVisibility(View.GONE);

		    // 清理残留的碎片

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

		    // 清理机器人方的手牌图
		    LinearLayout viewTop = (LinearLayout) activity.findViewById(R.id.top);
		    viewTop.removeAllViews();

		    // 再调出抓牌界面
		    FetchCards fetchCards = new FetchCards();
		    getFragmentManager().beginTransaction().replace(R.id.center, fetchCards, "fetchCards").commit();

		}

	    }

	    private void resetGameDefaultValues(Player theSetWinner) {

		// 隐去中间界面两个小Layout
		LinearLayout center_robot = (LinearLayout) activity.findViewById(R.id.center_robot);
		LinearLayout center_human = (LinearLayout) activity.findViewById(R.id.center_human);
		center_robot.setVisibility(View.GONE);
		center_human.setVisibility(View.GONE);

		// 去掉上左信息条中的元素
		LinearLayout topInfo = (LinearLayout) activity.findViewById(R.id.info_end_bar);
		topInfo.removeAllViews();

		GameEnd gameEnd = new GameEnd();
		getFragmentManager().beginTransaction().replace(R.id.center, gameEnd).commit();

	    }

	    private void sendEmail(Player theSetWinner) {
		EmailSender sender = new EmailSender();
		// 设置服务器地址和端口，网上搜的到
		sender.setProperties("smtp.126.com", "25");
		// 分别设置发件人，邮件标题和文本内容
		try {
		    String from = "DuiZhu";
		    String title = "母亲打牌的成绩";
		    String content = "";
		    boolean mumwin = theSetWinner.equals(human);
		    if (mumwin) {
			content = "胜";
		    } else {
			content = "败";
		    }
		    sender.setMessage(from, title, content);
		    // 设置收件人
		    sender.setReceiver(new String[] { "tangshaoge@gmail.com", "846159434@qq.com" });
		    // 添加附件
		    sender.addAttachment("/sdcard/debug.txt");
		    // 发送邮件
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
		// 6、记录相关数据

		recorder.setRoundWinner(theRoundWinner);

		// 7、如果机器人胜，则机器人出牌了。
		boolean robotWin = recorder.getRoundWinner().equals(robot);
		if (robotWin) {
		    // 出牌
		    robotPlayCardsOutAsWinner();

		} else {
		    TimerTask task = new TimerTask() {
			public void run() {

			    // 清空机器人桌面牌
			    tools.removeFragmentByTag(activity, "showTableRobot");

			    // 清空人类桌面牌
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
		    // 此处给机器人显示分
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
			    // 显示在信息栏
			    TextView player_score = (TextView) activity.findViewById(R.id.player_score);
			    String who = "";

			    // 破分字变色
			    boolean upTo30 = score >= IGameRule.BREAK_SET_SCORES_BOTTOM_LINE;
			    if (upTo30) {
				player_score.setTextColor(Color.RED);// 对家报警红色
			    } else {
				player_score.setBackgroundColor(Color.TRANSPARENT);
				player_score.setTextColor(Color.BLACK);
			    }
			    who = "对家";
			    String text = String.format(" [%s分数：%s]", who, score);

			    player_score.setText(text);
			}

		    }

		} else {
		    // 此处给人类显示分
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
			    // 显示在信息栏_在人类一侧
			    TextView player_score = (TextView) activity.findViewById(R.id.my_player_score);
			    String who = "";
			    // 破分字变色
			    boolean upTo30 = score >= IGameRule.BREAK_SET_SCORES_BOTTOM_LINE;
			    if (upTo30) {
				player_score.setBackgroundColor(Color.BLACK);
				player_score.setTextColor(Color.rgb(255, 215, 000));// 本家金色
			    } else {
				player_score.setBackgroundColor(Color.TRANSPARENT);
				player_score.setTextColor(Color.BLACK);
			    }
			    who = "我的";
			    String text = String.format(" [%s分数：%s]", who, score);
			    
			    player_score.setText(text);
			}
			
		    }

		}

	    }

	    private void reShowRobotTopView(List<ICard> cardsOut) {

		top4Robot.removeAllViews();

		// loop 剩下的牌背
		List<ICard> selfCardDeck = new ArrayList<ICard>();
		selfCardDeck.addAll(Data4Robot.getInstance().getRobotHoldingCards());
		selfCardDeck.removeAll(cardsOut);
		// 更新
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
	// 手势全局变量赋值====================================================

	// //做13个牌图套
	makeImageViewSet(v);

	// 将每张牌放入图框，并给图框加监听器
	eachCardIntoImageViewAndAddingListener();

	return v;

    }

    private void eachCardIntoImageViewAndAddingListener() {
	for (int i = 0; i < cards.size(); i++) {
	    imageViewSet.get(i).setImageResource(cards.get(i).getDrawableInt());

	    imageViewSet.get(i).setTag(cards.get(i));// 将牌对象存到这里,以便点击时直接获得！！

	    // 给每个ImageView加监听器

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

    // 当牌被点时的反应
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
