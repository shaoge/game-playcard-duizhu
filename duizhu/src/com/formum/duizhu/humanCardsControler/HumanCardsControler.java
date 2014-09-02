package com.formum.duizhu.humanCardsControler;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.formum.duizhu.R;
import com.formum.duizhu.ReadyOut;
import com.formum.duizhu.cardDeck.card.ICard;
import com.formum.duizhu.data.Data4Human;
import com.formum.duizhu.gameRule.IGameRule;
import com.formum.duizhu.player.AIPlay;
import com.formum.duizhu.player.GeneralPlay;
import com.formum.duizhu.player.HumanPlayer;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.player.RobotPlayer;
import com.formum.duizhu.recorder.IRecorder;
import com.formum.duizhu.recorder.Recorder;
import com.formum.duizhu.soundplay.ISoundPlay;
import com.formum.duizhu.soundplay.SoundPlay;
import com.formum.duizhu.util.IUtil;
import com.formum.duizhu.util.Util;

public class HumanCardsControler implements IHumanCardsControler {

	private static volatile IHumanCardsControler INSTANCE = null;

	private IRecorder recorder = Recorder.getInstance();
	private IUtil tools = Util.getInstance();
	private HumanPlayer human = HumanPlayer.getInstance(GeneralPlay
			.getInstance());
	private Player robot=RobotPlayer.getInstance(AIPlay.getInstance());
	private List<ICard> tempList = new ArrayList<ICard>();
	private List<ICard> allCardBeingShowedByThis=new ArrayList<ICard>();
	private Activity activity;

	private HumanCardsControler() {

	}

	public static IHumanCardsControler getInstance() {
		if (INSTANCE == null) {
			synchronized (HumanCardsControler.class) {
				// when more than two threads run into the first null check same
				// time, to avoid instanced more than one time, it needs to be
				// checked again.
				if (INSTANCE == null) {
					INSTANCE = new HumanCardsControler();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	public ImageView showImage(Activity context, final ICard card, int viewHost) {
		// ����Ƽ�
		boolean contains = allCardBeingShowedByThis.contains(card);
		if (!contains) {
			allCardBeingShowedByThis.add(card);
		}
		

		
		activity = context;
		LinearLayout layout = (LinearLayout) activity.findViewById(viewHost);
		// ����С����
		LinearLayout newLayout = (LinearLayout) new LinearLayout(
				layout.getContext());
		int width = 96;
		int height = 130;
		// newLayout.setBackgroundColor(Color.rgb(0, 42, 200));
		LayoutParams params = new LayoutParams(width, height);
		newLayout.setLayoutParams(params);
		newLayout.setPadding(-3, 1, -3, 0);

		// ����ͼ���
		final ImageView imageView = new ImageView(activity);
		int left = 0;
		int top = 0;
		int right = 0;
		int bottom = 0;
		imageView.setPadding(left, top, right, bottom);
		// imageView.setBackgroundColor(Color.rgb(230, 122, 29));

		// ͼ������С����
		newLayout.addView(imageView);
		// С���ּ���󲼾�
		layout.addView(newLayout);

		// ������Դ�������
		Resources res = activity.getResources();
		// ����Դ����������ԴĿ¼��drawable�л��ת�͵�ͼƬ
		Drawable drawable = res.getDrawable(card.getDrawableInt());

		// ͼƬ��ʾ�������ͼƬ����
		imageView.setImageDrawable(drawable);
		
		//TODO:�����Ƶ㶯����ʾ
		boolean stage_deal = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_DEAL);
		if (stage_deal) {
		    boolean classpoint=card.getPoint()==recorder.getCurrentClassPoint();
		    boolean suitEmpty=recorder.getCurrentTrumpSuit().equals("");
		    if (classpoint&&suitEmpty) {
			AnimationSet animationSet=new AnimationSet(true); 
			ScaleAnimation scaleAnimation=new ScaleAnimation( 
			1, 0.1f, 1, 0.1f, 
			Animation.RELATIVE_TO_SELF, 0.5f, 
			Animation.RELATIVE_TO_SELF, 0.5f); 
			scaleAnimation.setDuration(2000); 
			animationSet.addAnimation(scaleAnimation); 
			imageView.startAnimation(scaleAnimation); 
		    }
		}
		
		
		
		
		
		

		/**
		 * �˷������Ե���¼�������Ӧ����
		 */
		this.handleImageViewOnClickEvent(activity, card, imageView);

		return imageView;
	}

	private void handleImageViewOnClickEvent(Activity context,
			final ICard card, final ImageView imageView) {
		imageView.setOnClickListener(new OnClickListener() {

			private LinearLayout bottom = (LinearLayout) activity
					.findViewById(R.id.bottom);

			

			@Override
			public void onClick(View v) {
				// ��ÿ�ֽ׶α�硿��ץ����
				boolean stage_deal = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_DEAL);
				if (stage_deal) {
					//Ŀ��ֻ��һ��������
					boolean notJoker = !card.getSuit().equals("joker");//������Ʋ�������
					boolean noSuit = recorder.getCurrentTrumpSuit().equals("");//���ƻ�ɫû��
					boolean istheClassPoint = recorder.getCurrentClassPoint() == card.getPoint();//�뽫���Ƶ������
					if ( notJoker && noSuit && istheClassPoint) {
						//���Ʋ�д��ɫ��ׯ��
						showAndWriteTrumpSuitAndBanker();
					}
				}
				// ��ÿ�ֽ׶α�硿������
				boolean stage_battle = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_BATTLE);
				if (stage_battle) {
				    

				    
				    //��ȥ�����������
				    removeCardOnTableFrom_TempList();
					//������Ƽ���װ�뵥��
					tempList.add(card);
					//��ʾ�����Ƽ�����
					ReadyOut readyOut =new ReadyOut(); 
					//readyOut.setCards(tempList);
					Data4Human data = Data4Human.getInstance();
					data.setReadyOutCards(tempList);
					
					activity.getFragmentManager().beginTransaction().replace(R.id.readyOutCards,readyOut,"readyOutNow").commit();
			      
					
					 //������ʾ����״̬
			       boolean hasCards=allCardBeingShowedByThis.size()>0;
					if (hasCards) {
						allCardBeingShowedByThis.removeAll(tempList);
						List<ICard> sortedCards = tools.sortCardsAfterShowingTrumpSuit(allCardBeingShowedByThis);
						bottom.removeAllViews();
						allCardBeingShowedByThis.clear();//���Ϊ��װ
						//��װ
						for (ICard iCard : sortedCards) {
							int viewHost = R.id.bottom;
							showImage(activity, iCard, viewHost);
						}

					}
					
				}
			}

			private void removeCardOnTableFrom_TempList() {
			    List<ICard> humanCardsOnTable=new ArrayList<ICard>();
			    List<ICard> temp=new ArrayList<ICard>();
			    List<ICard> cardsOnTable = recorder.getCardsOnTable();
			    
			    for (ICard iCard : cardsOnTable) {
			    boolean humancard=iCard.getPlayer().equals(human);
			    if (humancard) {
			        humanCardsOnTable.add(iCard);
			    }
			    }
			    for (ICard iCard : humanCardsOnTable) {
			    for (ICard iCard2 : tempList) {
			        boolean outed=iCard.equals(iCard2);
			        if (outed) {
			    	temp.add(iCard2);
			        }
			    }
			    }
			    tempList.removeAll(temp);
			}

			private void showAndWriteTrumpSuitAndBanker() {
				// �ƶ�����
				
	    		TranslateAnimation animation = new TranslateAnimation(0,0,0, -200);   
	    		animation.setDuration(3000);
	    		imageView.startAnimation(animation);
	    		
	    		
				//����
					// ��ɫд�������ط���
					//recorder��
					recorder.setCurrentTrumpSuit(card.getSuit());
					//��Ϣ����
					TextView current_suit_begin = (TextView) activity.findViewById(R.id.current_suit_begin);
					ImageView suit_image=(ImageView) activity.findViewById(R.id.suit_image);
					suit_image.setVisibility(View.VISIBLE);
					suit_image.setBackgroundColor(Color.rgb(255, 255, 255));
					TextView current_suit_end = (TextView) activity.findViewById(R.id.current_suit_end);
					String args = tools.changeSuitFromEnglishToChinese(card.getSuit());
					current_suit_begin.setText(String.format("[�������ƣ� %s ", args));
					boolean heartSuit=card.getSuit().equals("heart");
					boolean spadeSuit=card.getSuit().equals("spade");
					boolean clubSuit=card.getSuit().equals("club");
					boolean diamondSuit=card.getSuit().equals("diamond");
					if (diamondSuit) {
					    //TODO:���飬��
						ISoundPlay soundPlay=SoundPlay.getInstance();
						soundPlay.play(activity,R.raw.trumpsuit_diamond);
						new Thread(SoundPlay.getInstance()).start();
						
					    suit_image.setImageResource(R.drawable.diamond);
					}
					if (clubSuit) {
					    //TODO: �ݻ�����
						ISoundPlay soundPlay=SoundPlay.getInstance();
						soundPlay.play(activity,R.raw.trumpsuit_club);
						new Thread(SoundPlay.getInstance()).start();
						
					    suit_image.setImageResource(R.drawable.club);
					}
					if (spadeSuit) {
					    //TODO:���ң���
						ISoundPlay soundPlay=SoundPlay.getInstance();
						soundPlay.play(activity,R.raw.trumpsuit_spade);
						new Thread(SoundPlay.getInstance()).start();
						
					    suit_image.setImageResource(R.drawable.spade);
					}
					if (heartSuit) {
					    
					    //TODO:���ң���
						ISoundPlay soundPlay=SoundPlay.getInstance();
						soundPlay.play(activity,R.raw.trumpsuit_heart);
						new Thread(SoundPlay.getInstance()).start();
						
					    suit_image.setImageResource(R.drawable.heart);
					}
					
					
					current_suit_end.setText(" ]");
					
					// recorder����ׯ��,ׯ��ֻ�е�һ�ֿ���������ׯ
					boolean firstSet=human.actions().isBanker()==false&&robot.actions().isBanker()==false;
					if (firstSet) {
					    recorder.setCurrentBanker(human);
					    TextView my_banker = (TextView) activity.findViewById(
						    R.id.my_banker);
					    my_banker.setText(" [ׯ�ң� �� ]");
					}
				
			}

		});
	}

	public List<ICard> getAllCardBeingShowedByThis() {
		return allCardBeingShowedByThis;
	}

	public void setAllCardBeingShowedByThis(List<ICard> allCardBeingShowedByThis) {
		this.allCardBeingShowedByThis = allCardBeingShowedByThis;
	}


}
