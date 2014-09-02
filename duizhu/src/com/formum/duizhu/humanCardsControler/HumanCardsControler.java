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
		// 获得牌集
		boolean contains = allCardBeingShowedByThis.contains(card);
		if (!contains) {
			allCardBeingShowedByThis.add(card);
		}
		

		
		activity = context;
		LinearLayout layout = (LinearLayout) activity.findViewById(viewHost);
		// 生成小布局
		LinearLayout newLayout = (LinearLayout) new LinearLayout(
				layout.getContext());
		int width = 96;
		int height = 130;
		// newLayout.setBackgroundColor(Color.rgb(0, 42, 200));
		LayoutParams params = new LayoutParams(width, height);
		newLayout.setLayoutParams(params);
		newLayout.setPadding(-3, 1, -3, 0);

		// 生成图像框
		final ImageView imageView = new ImageView(activity);
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
		Resources res = activity.getResources();
		// 用资源管理对象从资源目录的drawable中获得转型的图片
		Drawable drawable = res.getDrawable(card.getDrawableInt());

		// 图片显示对象加入图片对象
		imageView.setImageDrawable(drawable);
		
		//TODO:给将牌点动画提示
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
		 * 此方法将对点击事件进行相应处理
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
				// 【每局阶段标界】：抓亮牌
				boolean stage_deal = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_DEAL);
				if (stage_deal) {
					//目的只有一个：亮牌
					boolean notJoker = !card.getSuit().equals("joker");//所点的牌不能是王
					boolean noSuit = recorder.getCurrentTrumpSuit().equals("");//主牌花色没定
					boolean istheClassPoint = recorder.getCurrentClassPoint() == card.getPoint();//与将点牌点数相等
					if ( notJoker && noSuit && istheClassPoint) {
						//亮牌并写花色和庄家
						showAndWriteTrumpSuitAndBanker();
					}
				}
				// 【每局阶段标界】：打牌
				boolean stage_battle = (recorder.getMainLineFlag() == IGameRule.ROUND_STAGE_BATTLE);
				if (stage_battle) {
				    

				    
				    //减去人类出过的牌
				    removeCardOnTableFrom_TempList();
					//向待出牌集合装入单牌
					tempList.add(card);
					//显示待出牌集合牌
					ReadyOut readyOut =new ReadyOut(); 
					//readyOut.setCards(tempList);
					Data4Human data = Data4Human.getInstance();
					data.setReadyOutCards(tempList);
					
					activity.getFragmentManager().beginTransaction().replace(R.id.readyOutCards,readyOut,"readyOutNow").commit();
			      
					
					 //重新显示手牌状态
			       boolean hasCards=allCardBeingShowedByThis.size()>0;
					if (hasCards) {
						allCardBeingShowedByThis.removeAll(tempList);
						List<ICard> sortedCards = tools.sortCardsAfterShowingTrumpSuit(allCardBeingShowedByThis);
						bottom.removeAllViews();
						allCardBeingShowedByThis.clear();//清空为重装
						//重装
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
				// 移动动画
				
	    		TranslateAnimation animation = new TranslateAnimation(0,0,0, -200);   
	    		animation.setDuration(3000);
	    		imageView.startAnimation(animation);
	    		
	    		
				//亮牌
					// 花色写入两个地方：
					//recorder中
					recorder.setCurrentTrumpSuit(card.getSuit());
					//信息窗口
					TextView current_suit_begin = (TextView) activity.findViewById(R.id.current_suit_begin);
					ImageView suit_image=(ImageView) activity.findViewById(R.id.suit_image);
					suit_image.setVisibility(View.VISIBLE);
					suit_image.setBackgroundColor(Color.rgb(255, 255, 255));
					TextView current_suit_end = (TextView) activity.findViewById(R.id.current_suit_end);
					String args = tools.changeSuitFromEnglishToChinese(card.getSuit());
					current_suit_begin.setText(String.format("[本局主牌： %s ", args));
					boolean heartSuit=card.getSuit().equals("heart");
					boolean spadeSuit=card.getSuit().equals("spade");
					boolean clubSuit=card.getSuit().equals("club");
					boolean diamondSuit=card.getSuit().equals("diamond");
					if (diamondSuit) {
					    //TODO:方块，主
						ISoundPlay soundPlay=SoundPlay.getInstance();
						soundPlay.play(activity,R.raw.trumpsuit_diamond);
						new Thread(SoundPlay.getInstance()).start();
						
					    suit_image.setImageResource(R.drawable.diamond);
					}
					if (clubSuit) {
					    //TODO: 草花，主
						ISoundPlay soundPlay=SoundPlay.getInstance();
						soundPlay.play(activity,R.raw.trumpsuit_club);
						new Thread(SoundPlay.getInstance()).start();
						
					    suit_image.setImageResource(R.drawable.club);
					}
					if (spadeSuit) {
					    //TODO:黑桃，主
						ISoundPlay soundPlay=SoundPlay.getInstance();
						soundPlay.play(activity,R.raw.trumpsuit_spade);
						new Thread(SoundPlay.getInstance()).start();
						
					    suit_image.setImageResource(R.drawable.spade);
					}
					if (heartSuit) {
					    
					    //TODO:红桃，主
						ISoundPlay soundPlay=SoundPlay.getInstance();
						soundPlay.play(activity,R.raw.trumpsuit_heart);
						new Thread(SoundPlay.getInstance()).start();
						
					    suit_image.setImageResource(R.drawable.heart);
					}
					
					
					current_suit_end.setText(" ]");
					
					// recorder中设庄家,庄家只有第一局可以抢亮做庄
					boolean firstSet=human.actions().isBanker()==false&&robot.actions().isBanker()==false;
					if (firstSet) {
					    recorder.setCurrentBanker(human);
					    TextView my_banker = (TextView) activity.findViewById(
						    R.id.my_banker);
					    my_banker.setText(" [庄家： 我 ]");
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
