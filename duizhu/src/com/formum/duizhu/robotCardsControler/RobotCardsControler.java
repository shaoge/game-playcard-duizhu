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
		// ����С����
		LinearLayout newLayout = (LinearLayout) new LinearLayout(
				layout.getContext());
		int width = 96;
		int height = 130;
		// newLayout.setBackgroundColor(Color.rgb(0, 42, 200));
		LayoutParams params = new LayoutParams(width, height);
		newLayout.setLayoutParams(params);
		newLayout.setPadding(0, 1, 0, 0);

		// ����ͼ���
		ImageView imageView = new ImageView(context);
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
		Resources res = context.getResources();
		// ����Դ����������ԴĿ¼��drawable�л��ת�͵�ͼƬ
		Drawable drawable = res.getDrawable(drawableBackInt);

		// ͼƬ��ʾ�������ͼƬ����
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
