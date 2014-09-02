package com.formum.duizhu;

import java.util.ArrayList;
import java.util.List;
import com.formum.duizhu.cardDeck.CardDeck;
import com.formum.duizhu.cardDeck.ICardDeck;
import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.data.Data4Human;
import com.formum.duizhu.data.Data4Robot;
import com.formum.duizhu.gameRule.GameRule;
import com.formum.duizhu.gameRule.IGameRule;
import com.formum.duizhu.humanCardsControler.HumanCardsControler;
import com.formum.duizhu.humanCardsControler.IHumanCardsControler;
import com.formum.duizhu.player.AIPlay;
import com.formum.duizhu.player.GeneralPlay;
import com.formum.duizhu.player.HumanPlayer;
import com.formum.duizhu.player.RobotPlayer;
import com.formum.duizhu.recorder.IRecorder;
import com.formum.duizhu.recorder.Recorder;
import com.formum.duizhu.robotCardsControler.IRobotCardsControler;
import com.formum.duizhu.robotCardsControler.RobotCardsControler;
import com.formum.duizhu.soundplay.ISoundPlay;
import com.formum.duizhu.soundplay.SoundPlay;
import com.formum.duizhu.util.IUtil;
import com.formum.duizhu.util.Util;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FetchCards extends Fragment {
    private IUtil tools = Util.getInstance();
    private IRecorder recorder = Recorder.getInstance();
    private IGameRule gameRule = GameRule.getInstance();
    private HumanPlayer human = HumanPlayer.getInstance(GeneralPlay.getInstance());
    private RobotPlayer robot = RobotPlayer.getInstance(AIPlay.getInstance());
    private ICardDeck cardDeck = CardDeck.getInstance();
    private ImageView allcards;
    private ImageView show_trump_suit_cards;
    private LinearLayout top4Robot;// 机器人放牌档位
    private IHumanCardsControler humanCardsControler = HumanCardsControler.getInstance();
    private IRobotCardsControler robotCardsControler;
    private TextView banker;
    private Data4Human data=Data4Human.getInstance();
    private TextView mybanker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View v = inflater.inflate(R.layout.fetch_cards, container, false);

	// 预先设置的相关参数
	preSetup4FetchCards(v);

	// 显示一摞牌图，供点击表示抓牌
	allcards.setOnClickListener(new OnClickListener() {
	    int count = 0;// 抓牌计数

	    @Override
	    public void onClick(View v) {
		// 【每局阶段标界】：抓亮牌
		boolean stage_ready = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_READY);
		if (stage_ready) {
		    recorder.setMainLineFlag(IGameRule.ROUND_STAGE_DEAL);
		}
		// 查看是否进入抓亮牌阶段
		boolean stage_deal = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_DEAL);
		if (stage_deal) {
		    // 设主牌花色;并成为庄家
		    boolean notTo13 = (count < IGameRule.PLAYER_CARDS_SIZE);
		    if (notTo13) {
			// 确定人类是否先拿牌
			boolean humanFirst = recorder.getFirstFetcher().equals(human);
			if (humanFirst) {
			    human.actions().fetchCard(cardDeck.dealCard());
			    robot.actions().fetchCard(cardDeck.dealCard());
			} else {
			    robot.actions().fetchCard(cardDeck.dealCard());
			    human.actions().fetchCard(cardDeck.dealCard());
			}
			boolean noSuit = recorder.getCurrentTrumpSuit().equals("");

			// 机器在此亮主，回调robot 亮牌方法
			robotShowTrumpSuit(noSuit);

			boolean to13 = (count == IGameRule.PLAYER_CARDS_SIZE - 1);
			// 到13张时自动亮主
			if (to13) {
			    boolean noSuitAtAll = recorder.getCurrentTrumpSuit().equals("");
			    if (noSuitAtAll) {
				// 自动亮主
				choseOneCardAsTrumpSuit();
			    }
			    // 全牌授阶，以便轮次比较胜者
			    tools.grantTenTrumpCardsAndAceRank();
			    
			    //安排将点牌显示的地方
			    controlClassPointTextViews();
			}
			// 双方分别显示
			showOnEachSide();
			count++;
		    } else {
			// 标定【打牌】阶段
			boolean hasTrumpSuit = gameRule.isThereTrumpSuit(recorder);
			if (hasTrumpSuit) {
			    recorder.setMainLineFlag(IGameRule.ROUND_STAGE_BATTLE);
			    // 清空抓牌桌面

			    Fragment frgmt = getFragmentManager().findFragmentByTag("fetchCards");
			    boolean notNullFrgmt = frgmt != null;
			    if (notNullFrgmt) {
				getFragmentManager().beginTransaction().remove(frgmt).commit();
			    }
			}
		    }

		}
		// 【每局阶段标界】：可以打牌
		boolean stage_battle = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_BATTLE);
		if (stage_battle) {
		    // 查看誰可以出牌
		    boolean robotCanOut = gameRule.mayIPutOutCards(robot);
		    // 机器人
		    if (robotCanOut) {
			robotPlayOutCards();

			//语音提示：对家，已出牌

			ISoundPlay soundPlay=SoundPlay.getInstance();
			soundPlay.play(getActivity(), R.raw.offensive_putout_card);
			new Thread(SoundPlay.getInstance()).start();

		    } else {
			//语音提示：抓完牌了,请你出牌吧

			ISoundPlay soundPlay=SoundPlay.getInstance();
			soundPlay.play(getActivity(), R.raw.finished_fetching);
			new Thread(SoundPlay.getInstance()).start();
		    }
		}
	    }

	    private void choseOneCardAsTrumpSuit() {
		// 双方还都没亮主牌
		ICard theSuitCard = cardDeck.getNoJokersCardsLefted().get(0);

		String txt = String.format("翻牌亮的主是：%s", tools.changeSuitFromEnglishToChinese(theSuitCard.getSuit()));
		Toast toast = Toast.makeText(getActivity(), txt, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		// 翻亮的主显示

		show_trump_suit_cards.setImageResource(theSuitCard.getDrawableInt());// 翻主显示

		// 初始化 Translate动画
		TranslateAnimation translateAnimation = new TranslateAnimation(0.1f, 100.0f, 0.1f, 100.0f);
		// 初始化 Alpha动画
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);

		// 动画集
		AnimationSet set = new AnimationSet(true);
		set.addAnimation(translateAnimation);
		set.addAnimation(alphaAnimation);

		// 设置动画时间 (作用到每个动画)
		set.setDuration(1000);
		show_trump_suit_cards.startAnimation(set);

		// 写花色和庄家
		writeTrumpSuitAndBanker(theSuitCard, human, null);

	    }

	    private void robotPlayOutCards() {
		int target = robot.getMyStrategy();
		List<ICard> suggestCards = robot.consultStrategy(target, null);
		List<ICard> cardsOut = robot.actions().putCardsOut(suggestCards);

		// 显示出来
		boolean hasCards = cardsOut != null;
		if (hasCards) {
		    // 显示战斗碎片
		    tools.showOutCardOnTable(getActivity(), cardsOut);
		    // 机器人手牌显示要相应减少
		    top4Robot.removeAllViews();
		    // loop 剩下的牌背
		    List<ICard> selfCardDeck = new ArrayList<ICard>();
		    selfCardDeck.addAll(Data4Robot.getInstance().getRobotHoldingCards());
		    robotCardsControler.getAllCardBeingShowedByThis().clear();// 清空
		    selfCardDeck.removeAll(cardsOut);
		    boolean hasSelfCard = selfCardDeck.size() > 0;
		    if (hasSelfCard) {
			for (ICard iCard : selfCardDeck) {
			    robotCardsControler.showImage(getActivity(), iCard, R.id.top, R.drawable.back);
			}
		    }

		}

		// 记录出牌信息
		recorder.recordCardsOnTable(cardsOut);
		cardDeck.retrieveCards(cardsOut);// 牌套回收牌
		tools.recordBothSideOutCards(cardsOut, robot, recorder);

	    }

	    private void robotShowTrumpSuit(boolean nosuit) {
		if (nosuit) {
		    ICard showingCard = robot.actions().showTrumpSuit();
		    boolean hasShowingCard = showingCard != null;
		    if (hasShowingCard) {
			// 在机器人牌中显示出来
			// 先删除机器人显示的牌背
			top4Robot.removeAllViews();
			// loop 其他牌背，一张亮的牌
			int size4Repeat = (robot.actions().getSelfCardDeck().size() - 1);
			int count = 1;
			while (count < size4Repeat) {
			    tools.showImage(getActivity(), showingCard, R.id.top, R.drawable.back);
			    count++;
			}
			tools.showImage(getActivity(), showingCard, R.id.top);

			// 写花色和庄家
			writeTrumpSuitAndBanker(showingCard, null, robot);
		    }
		}
	    }

	    private void writeTrumpSuitAndBanker(ICard showingCard, HumanPlayer humanPlayer, RobotPlayer robotPlayer) {
		recorder.setCurrentTrumpSuit(showingCard.getSuit());

		TextView current_suit_begin = (TextView) getActivity().findViewById(R.id.current_suit_begin);
		ImageView suit_image = (ImageView) getActivity().findViewById(R.id.suit_image);
		suit_image.setVisibility(View.VISIBLE);
		suit_image.setBackgroundColor(Color.rgb(255, 255, 255));
		TextView current_suit_end = (TextView) getActivity().findViewById(R.id.current_suit_end);
		String args = tools.changeSuitFromEnglishToChinese(showingCard.getSuit());
		current_suit_begin.setText(String.format("[本局主牌： %s ", args));
		boolean heartSuit = showingCard.getSuit().equals("heart");
		boolean spadeSuit = showingCard.getSuit().equals("spade");
		boolean clubSuit = showingCard.getSuit().equals("club");
		boolean diamondSuit = showingCard.getSuit().equals("diamond");
		if (diamondSuit) {
		    //TODO:方块，主
			ISoundPlay soundPlay=SoundPlay.getInstance();
			soundPlay.play(getActivity(),R.raw.trumpsuit_diamond);
			new Thread(SoundPlay.getInstance()).start();

		    suit_image.setImageResource(R.drawable.diamond);
		}
		if (clubSuit) {
		    //TODO: 草花，主
			ISoundPlay soundPlay=SoundPlay.getInstance();
			soundPlay.play(getActivity(),R.raw.trumpsuit_club);
			new Thread(SoundPlay.getInstance()).start();

		    suit_image.setImageResource(R.drawable.club);
		}
		if (spadeSuit) {
		    //TODO:黑桃，主
			ISoundPlay soundPlay=SoundPlay.getInstance();
			soundPlay.play(getActivity(),R.raw.trumpsuit_spade);
			new Thread(SoundPlay.getInstance()).start();

		    suit_image.setImageResource(R.drawable.spade);
		}
		if (heartSuit) {

		    //TODO:红桃，主
			ISoundPlay soundPlay=SoundPlay.getInstance();
			soundPlay.play(getActivity(),R.raw.trumpsuit_heart);
			new Thread(SoundPlay.getInstance()).start();

		    suit_image.setImageResource(R.drawable.heart);
		}

		current_suit_end.setText(" ]");

		//只有第一局可以抢亮主做庄
		boolean firstSet=recorder.getCurrentBanker()==null;//庄家没有设定
		if (firstSet) {
		    if (humanPlayer != null) {
			recorder.setCurrentBanker(human);
			tools.putBankerOn(human);//Toggle式赋值
			mybanker.setText(" [庄家： 我 ]");
		    } else {
			recorder.setCurrentBanker(robot);
			tools.putBankerOn(robot);//Toggle式赋值
			banker.setText("[庄家： 对家 ]");
		    }
		}
	    }

	    private void showOnEachSide() {
		// 人
		// 要先清理原来的imageView
		LinearLayout bottom = (LinearLayout) getActivity().findViewById(R.id.bottom);
		bottom.removeAllViews();// 上次显示的牌
		// Sort here:
		boolean nosuit = recorder.getCurrentTrumpSuit().equals("");//未确定主牌花色前
		if (nosuit) {
		    List<ICard> cards = tools.sortCardsBeforeShowingTrumpSuit(human.actions().getSelfCardDeck());
		    if (cards != null) {
			for (ICard iCard : cards) {
			    humanCardsControler.showImage(getActivity(), iCard, R.id.bottom);
			}
		    }
		} else {
		    List<ICard> cards = tools.sortCardsAfterShowingTrumpSuit(human.actions().getSelfCardDeck());
		    if (cards != null) {

			for (ICard iCard : cards) {
			    humanCardsControler.showImage(getActivity(), iCard, R.id.bottom);
			}
		    }
		}

		// 机器人
		LinearLayout top = (LinearLayout) getActivity().findViewById(R.id.top);
		top.removeAllViews();
		for (ICard rcard : robot.actions().getSelfCardDeck()) {
		    robotCardsControler.showImage(getActivity(), rcard, R.id.top, R.drawable.back);
		}

	    }
	});

	return v;

    }

    private void preSetup4FetchCards(View v) {

	// 检查信息栏信息源，人-机器人和记录；如果有，信息栏就显示出来
	   robotCardsControler = RobotCardsControler.getInstance();
	   banker=(TextView) getActivity().findViewById(R.id.banker);
	   mybanker=(TextView) getActivity().findViewById(R.id.my_banker);
	   mybanker.setTextColor(Color.BLACK);

	//先抓牌并示有庄家，先抓牌者即是它，显示它;没有时，设定抓牌者为人类：
	boolean hasBanker=recorder.getCurrentBanker()!=null;
	if (hasBanker) {
	    boolean humanBanker = human.equals(recorder.getCurrentBanker());
	    if (humanBanker) {
		recorder.setFirstFetcher(human);
		mybanker.setText(" [庄家：我]");
	    } else{
		recorder.setFirstFetcher(robot);
		banker.setText(" [庄家：对家]");
	    }
	}else{
	    recorder.setFirstFetcher(human);
	}

	
	//如果上次产生庄家，先显示将牌点在相对应的地方
	// 当前打主：当主点为0时，从3开始打
	boolean classPointEmpty = (recorder.getCurrentClassPoint() == 0);
	if (classPointEmpty) {
	    recorder.setCurrentClassPoint(IGameRule.INITIAL_CLASSPOINT);
	}else{
	    //用庄家的升级点
	    int myClassPoint = recorder.getCurrentBanker().actions().getMyClassPoint();
	    recorder.setCurrentClassPoint(myClassPoint);
	}
	controlClassPointTextViews();


	// Recorder中的当前牌花色值要清除
	recorder.setCurrentTrumpSuit("");
	// cardDeck.shuffleCards();// 洗牌--这是标准打法，为了给老人家每次至少一张好牌，先不用
	allcards = (ImageView) v.findViewById(R.id.allcards);
	show_trump_suit_cards = (ImageView) v.findViewById(R.id.show_trump_suit_cards);
	top4Robot = (LinearLayout) getActivity().findViewById(R.id.top);

	// 【每局阶段标界】：准备好了
	recorder.setMainLineFlag(IGameRule.ROUND_STAGE_READY);

    }
/**
 * 控制将牌点文本框的分别显示
 */
    private void controlClassPointTextViews() {

	//显示将牌级数
	int current_classpoint = recorder.getCurrentClassPoint();
	Activity activity = getActivity();
	///上左信息栏部分
	TextView current_class = (TextView) activity.findViewById(R.id.current_class);
	boolean robotBanker = robot.equals(recorder.getCurrentBanker());
	if (robotBanker) {
	    showVaryClassPointContent(current_classpoint, current_class);
	} else {
	    current_class.setText("");
	}
	
	///人类信息栏部分
	TextView my_banker_class = (TextView) activity.findViewById(R.id.my_banker_class);
	my_banker_class.setTextColor(Color.BLACK);
	boolean humanBanker = human.equals(recorder.getCurrentBanker());
	if (humanBanker) {
	    showVaryClassPointContent(current_classpoint, my_banker_class);
	}else{
	    my_banker_class.setText("");
	}
    }
/**
 * 显示打的将点级数
 * @param current_classpoint
 * @param current_class
 */
    private void showVaryClassPointContent(int current_classpoint, TextView textView) {
	boolean notIn=current_classpoint!=11&&current_classpoint!=12&&current_classpoint!=13&&current_classpoint!=1&&current_classpoint!=0;
        if (notIn) {
            textView.setText(String.format("[打 %d 主] ", current_classpoint));
        } else {
            	switch (current_classpoint) {
            	case 11:
            	textView.setText(String.format("[打 %s 主] ", "J"));
            	    break;
            	case 12:
            	textView.setText(String.format("[打 %s 主] ", "Q"));
            	    break;
            	case 13:
            	textView.setText(String.format("[打 %s 主] ", "K"));
            	    break;
            	case 1:
            	textView.setText(String.format("[打 %s 主] ", "A"));
            	    break;
            	case 0:
            	    textView.setText(String.format("[打 %s 主] ", "3"));
            	    break;
            	}
        }
    }

    public IRobotCardsControler getRobotCardsControler() {
	return robotCardsControler;
    }

    public void setRobotCardsControler(IRobotCardsControler robotCardsControler) {
	this.robotCardsControler = robotCardsControler;
    }

}
