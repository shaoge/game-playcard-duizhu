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
    private LinearLayout top4Robot;// �����˷��Ƶ�λ
    private IHumanCardsControler humanCardsControler = HumanCardsControler.getInstance();
    private IRobotCardsControler robotCardsControler;
    private TextView banker;
    private Data4Human data=Data4Human.getInstance();
    private TextView mybanker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View v = inflater.inflate(R.layout.fetch_cards, container, false);

	// Ԥ�����õ���ز���
	preSetup4FetchCards(v);

	// ��ʾһ����ͼ���������ʾץ��
	allcards.setOnClickListener(new OnClickListener() {
	    int count = 0;// ץ�Ƽ���

	    @Override
	    public void onClick(View v) {
		// ��ÿ�ֽ׶α�硿��ץ����
		boolean stage_ready = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_READY);
		if (stage_ready) {
		    recorder.setMainLineFlag(IGameRule.ROUND_STAGE_DEAL);
		}
		// �鿴�Ƿ����ץ���ƽ׶�
		boolean stage_deal = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_DEAL);
		if (stage_deal) {
		    // �����ƻ�ɫ;����Ϊׯ��
		    boolean notTo13 = (count < IGameRule.PLAYER_CARDS_SIZE);
		    if (notTo13) {
			// ȷ�������Ƿ�������
			boolean humanFirst = recorder.getFirstFetcher().equals(human);
			if (humanFirst) {
			    human.actions().fetchCard(cardDeck.dealCard());
			    robot.actions().fetchCard(cardDeck.dealCard());
			} else {
			    robot.actions().fetchCard(cardDeck.dealCard());
			    human.actions().fetchCard(cardDeck.dealCard());
			}
			boolean noSuit = recorder.getCurrentTrumpSuit().equals("");

			// �����ڴ��������ص�robot ���Ʒ���
			robotShowTrumpSuit(noSuit);

			boolean to13 = (count == IGameRule.PLAYER_CARDS_SIZE - 1);
			// ��13��ʱ�Զ�����
			if (to13) {
			    boolean noSuitAtAll = recorder.getCurrentTrumpSuit().equals("");
			    if (noSuitAtAll) {
				// �Զ�����
				choseOneCardAsTrumpSuit();
			    }
			    // ȫ���ڽף��Ա��ִαȽ�ʤ��
			    tools.grantTenTrumpCardsAndAceRank();
			    
			    //���Ž�������ʾ�ĵط�
			    controlClassPointTextViews();
			}
			// ˫���ֱ���ʾ
			showOnEachSide();
			count++;
		    } else {
			// �궨�����ơ��׶�
			boolean hasTrumpSuit = gameRule.isThereTrumpSuit(recorder);
			if (hasTrumpSuit) {
			    recorder.setMainLineFlag(IGameRule.ROUND_STAGE_BATTLE);
			    // ���ץ������

			    Fragment frgmt = getFragmentManager().findFragmentByTag("fetchCards");
			    boolean notNullFrgmt = frgmt != null;
			    if (notNullFrgmt) {
				getFragmentManager().beginTransaction().remove(frgmt).commit();
			    }
			}
		    }

		}
		// ��ÿ�ֽ׶α�硿�����Դ���
		boolean stage_battle = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_BATTLE);
		if (stage_battle) {
		    // �鿴�l���Գ���
		    boolean robotCanOut = gameRule.mayIPutOutCards(robot);
		    // ������
		    if (robotCanOut) {
			robotPlayOutCards();

			//������ʾ���Լң��ѳ���

			ISoundPlay soundPlay=SoundPlay.getInstance();
			soundPlay.play(getActivity(), R.raw.offensive_putout_card);
			new Thread(SoundPlay.getInstance()).start();

		    } else {
			//������ʾ��ץ������,������ư�

			ISoundPlay soundPlay=SoundPlay.getInstance();
			soundPlay.play(getActivity(), R.raw.finished_fetching);
			new Thread(SoundPlay.getInstance()).start();
		    }
		}
	    }

	    private void choseOneCardAsTrumpSuit() {
		// ˫������û������
		ICard theSuitCard = cardDeck.getNoJokersCardsLefted().get(0);

		String txt = String.format("�����������ǣ�%s", tools.changeSuitFromEnglishToChinese(theSuitCard.getSuit()));
		Toast toast = Toast.makeText(getActivity(), txt, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		// ����������ʾ

		show_trump_suit_cards.setImageResource(theSuitCard.getDrawableInt());// ������ʾ

		// ��ʼ�� Translate����
		TranslateAnimation translateAnimation = new TranslateAnimation(0.1f, 100.0f, 0.1f, 100.0f);
		// ��ʼ�� Alpha����
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);

		// ������
		AnimationSet set = new AnimationSet(true);
		set.addAnimation(translateAnimation);
		set.addAnimation(alphaAnimation);

		// ���ö���ʱ�� (���õ�ÿ������)
		set.setDuration(1000);
		show_trump_suit_cards.startAnimation(set);

		// д��ɫ��ׯ��
		writeTrumpSuitAndBanker(theSuitCard, human, null);

	    }

	    private void robotPlayOutCards() {
		int target = robot.getMyStrategy();
		List<ICard> suggestCards = robot.consultStrategy(target, null);
		List<ICard> cardsOut = robot.actions().putCardsOut(suggestCards);

		// ��ʾ����
		boolean hasCards = cardsOut != null;
		if (hasCards) {
		    // ��ʾս����Ƭ
		    tools.showOutCardOnTable(getActivity(), cardsOut);
		    // ������������ʾҪ��Ӧ����
		    top4Robot.removeAllViews();
		    // loop ʣ�µ��Ʊ�
		    List<ICard> selfCardDeck = new ArrayList<ICard>();
		    selfCardDeck.addAll(Data4Robot.getInstance().getRobotHoldingCards());
		    robotCardsControler.getAllCardBeingShowedByThis().clear();// ���
		    selfCardDeck.removeAll(cardsOut);
		    boolean hasSelfCard = selfCardDeck.size() > 0;
		    if (hasSelfCard) {
			for (ICard iCard : selfCardDeck) {
			    robotCardsControler.showImage(getActivity(), iCard, R.id.top, R.drawable.back);
			}
		    }

		}

		// ��¼������Ϣ
		recorder.recordCardsOnTable(cardsOut);
		cardDeck.retrieveCards(cardsOut);// ���׻�����
		tools.recordBothSideOutCards(cardsOut, robot, recorder);

	    }

	    private void robotShowTrumpSuit(boolean nosuit) {
		if (nosuit) {
		    ICard showingCard = robot.actions().showTrumpSuit();
		    boolean hasShowingCard = showingCard != null;
		    if (hasShowingCard) {
			// �ڻ�����������ʾ����
			// ��ɾ����������ʾ���Ʊ�
			top4Robot.removeAllViews();
			// loop �����Ʊ���һ��������
			int size4Repeat = (robot.actions().getSelfCardDeck().size() - 1);
			int count = 1;
			while (count < size4Repeat) {
			    tools.showImage(getActivity(), showingCard, R.id.top, R.drawable.back);
			    count++;
			}
			tools.showImage(getActivity(), showingCard, R.id.top);

			// д��ɫ��ׯ��
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
		current_suit_begin.setText(String.format("[�������ƣ� %s ", args));
		boolean heartSuit = showingCard.getSuit().equals("heart");
		boolean spadeSuit = showingCard.getSuit().equals("spade");
		boolean clubSuit = showingCard.getSuit().equals("club");
		boolean diamondSuit = showingCard.getSuit().equals("diamond");
		if (diamondSuit) {
		    //TODO:���飬��
			ISoundPlay soundPlay=SoundPlay.getInstance();
			soundPlay.play(getActivity(),R.raw.trumpsuit_diamond);
			new Thread(SoundPlay.getInstance()).start();

		    suit_image.setImageResource(R.drawable.diamond);
		}
		if (clubSuit) {
		    //TODO: �ݻ�����
			ISoundPlay soundPlay=SoundPlay.getInstance();
			soundPlay.play(getActivity(),R.raw.trumpsuit_club);
			new Thread(SoundPlay.getInstance()).start();

		    suit_image.setImageResource(R.drawable.club);
		}
		if (spadeSuit) {
		    //TODO:���ң���
			ISoundPlay soundPlay=SoundPlay.getInstance();
			soundPlay.play(getActivity(),R.raw.trumpsuit_spade);
			new Thread(SoundPlay.getInstance()).start();

		    suit_image.setImageResource(R.drawable.spade);
		}
		if (heartSuit) {

		    //TODO:���ң���
			ISoundPlay soundPlay=SoundPlay.getInstance();
			soundPlay.play(getActivity(),R.raw.trumpsuit_heart);
			new Thread(SoundPlay.getInstance()).start();

		    suit_image.setImageResource(R.drawable.heart);
		}

		current_suit_end.setText(" ]");

		//ֻ�е�һ�ֿ�����������ׯ
		boolean firstSet=recorder.getCurrentBanker()==null;//ׯ��û���趨
		if (firstSet) {
		    if (humanPlayer != null) {
			recorder.setCurrentBanker(human);
			tools.putBankerOn(human);//Toggleʽ��ֵ
			mybanker.setText(" [ׯ�ң� �� ]");
		    } else {
			recorder.setCurrentBanker(robot);
			tools.putBankerOn(robot);//Toggleʽ��ֵ
			banker.setText("[ׯ�ң� �Լ� ]");
		    }
		}
	    }

	    private void showOnEachSide() {
		// ��
		// Ҫ������ԭ����imageView
		LinearLayout bottom = (LinearLayout) getActivity().findViewById(R.id.bottom);
		bottom.removeAllViews();// �ϴ���ʾ����
		// Sort here:
		boolean nosuit = recorder.getCurrentTrumpSuit().equals("");//δȷ�����ƻ�ɫǰ
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

		// ������
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

	// �����Ϣ����ϢԴ����-�����˺ͼ�¼������У���Ϣ������ʾ����
	   robotCardsControler = RobotCardsControler.getInstance();
	   banker=(TextView) getActivity().findViewById(R.id.banker);
	   mybanker=(TextView) getActivity().findViewById(R.id.my_banker);
	   mybanker.setTextColor(Color.BLACK);

	//��ץ�Ʋ�ʾ��ׯ�ң���ץ���߼���������ʾ��;û��ʱ���趨ץ����Ϊ���ࣺ
	boolean hasBanker=recorder.getCurrentBanker()!=null;
	if (hasBanker) {
	    boolean humanBanker = human.equals(recorder.getCurrentBanker());
	    if (humanBanker) {
		recorder.setFirstFetcher(human);
		mybanker.setText(" [ׯ�ң���]");
	    } else{
		recorder.setFirstFetcher(robot);
		banker.setText(" [ׯ�ң��Լ�]");
	    }
	}else{
	    recorder.setFirstFetcher(human);
	}

	
	//����ϴβ���ׯ�ң�����ʾ���Ƶ������Ӧ�ĵط�
	// ��ǰ������������Ϊ0ʱ����3��ʼ��
	boolean classPointEmpty = (recorder.getCurrentClassPoint() == 0);
	if (classPointEmpty) {
	    recorder.setCurrentClassPoint(IGameRule.INITIAL_CLASSPOINT);
	}else{
	    //��ׯ�ҵ�������
	    int myClassPoint = recorder.getCurrentBanker().actions().getMyClassPoint();
	    recorder.setCurrentClassPoint(myClassPoint);
	}
	controlClassPointTextViews();


	// Recorder�еĵ�ǰ�ƻ�ɫֵҪ���
	recorder.setCurrentTrumpSuit("");
	// cardDeck.shuffleCards();// ϴ��--���Ǳ�׼�򷨣�Ϊ�˸����˼�ÿ������һ�ź��ƣ��Ȳ���
	allcards = (ImageView) v.findViewById(R.id.allcards);
	show_trump_suit_cards = (ImageView) v.findViewById(R.id.show_trump_suit_cards);
	top4Robot = (LinearLayout) getActivity().findViewById(R.id.top);

	// ��ÿ�ֽ׶α�硿��׼������
	recorder.setMainLineFlag(IGameRule.ROUND_STAGE_READY);

    }
/**
 * ���ƽ��Ƶ��ı���ķֱ���ʾ
 */
    private void controlClassPointTextViews() {

	//��ʾ���Ƽ���
	int current_classpoint = recorder.getCurrentClassPoint();
	Activity activity = getActivity();
	///������Ϣ������
	TextView current_class = (TextView) activity.findViewById(R.id.current_class);
	boolean robotBanker = robot.equals(recorder.getCurrentBanker());
	if (robotBanker) {
	    showVaryClassPointContent(current_classpoint, current_class);
	} else {
	    current_class.setText("");
	}
	
	///������Ϣ������
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
 * ��ʾ��Ľ��㼶��
 * @param current_classpoint
 * @param current_class
 */
    private void showVaryClassPointContent(int current_classpoint, TextView textView) {
	boolean notIn=current_classpoint!=11&&current_classpoint!=12&&current_classpoint!=13&&current_classpoint!=1&&current_classpoint!=0;
        if (notIn) {
            textView.setText(String.format("[�� %d ��] ", current_classpoint));
        } else {
            	switch (current_classpoint) {
            	case 11:
            	textView.setText(String.format("[�� %s ��] ", "J"));
            	    break;
            	case 12:
            	textView.setText(String.format("[�� %s ��] ", "Q"));
            	    break;
            	case 13:
            	textView.setText(String.format("[�� %s ��] ", "K"));
            	    break;
            	case 1:
            	textView.setText(String.format("[�� %s ��] ", "A"));
            	    break;
            	case 0:
            	    textView.setText(String.format("[�� %s ��] ", "3"));
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
