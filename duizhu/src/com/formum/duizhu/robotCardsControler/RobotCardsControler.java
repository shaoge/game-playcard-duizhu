package com.formum.duizhu.robotCardsControler;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.formum.duizhu.cardDeck.card.ICard;

public class RobotCardsControler implements IRobotCardsControler {

	private static volatile IRobotCardsControler INSTANCE = null;
	private List<ICard> allCardBeingShowedByThis=new ArrayList<ICard>();
	private RobotCardsControler() {

	}

	public static IRobotCardsControler getInstance() {
		if (INSTANCE == null) {
			synchronized (RobotCardsControler.class) {
				// when more than two threads run into the first null check same
				// time, to avoid instanced more than one time, it needs to be
				// checked again.
				if (INSTANCE == null) {
					INSTANCE = new RobotCardsControler();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	public ImageView showImage(Activity context, ICard card, int viewHost,
			int drawableBackInt) {

		boolean contains = allCardBeingShowedByThis.contains(card);
		if (!contains) {
			allCardBeingShowedByThis.add(card);
		}
		
		LinearLayout layout = (LinearLayout) context.findViewById(viewHost);
		// 生成小布局
		LinearLayout newLayout = (LinearLayout) new LinearLayout(
				layout.getContext());
		int width = 96;
		int height = 130;
		// newLayout.setBackgroundColor(Color.rgb(0, 42, 200));
		LayoutParams params = new LayoutParams(width, height);
		newLayout.setLayoutParams(params);
		newLayout.setPadding(0, 1, 0, 0);

		// 生成图像框
		ImageView imageView = new ImageView(context);
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
		Resources res = context.getResources();
		// 用资源管理对象从资源目录的drawable中获得转型的图片
		Drawable drawable = res.getDrawable(drawableBackInt);

		// 图片显示对象加入图片对象
		imageView.setImageDrawable(drawable);
		return imageView;
	
	}

	public List<ICard> getAllCardBeingShowedByThis() {
		return allCardBeingShowedByThis;
		
	}

	public void setAllCardBeingShowedByThis(List<ICard> allCardBeingShowedByThis) {
		this.allCardBeingShowedByThis = allCardBeingShowedByThis;
	}


}
