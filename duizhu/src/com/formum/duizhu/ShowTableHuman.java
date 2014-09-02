package com.formum.duizhu;

import java.util.ArrayList;
import java.util.List;

import com.formum.duizhu.cardDeck.card.ICard;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowTableHuman extends Fragment {

    private List<ICard> cards = new ArrayList<ICard>();
    private List<ImageView> imageViewSet = new ArrayList<ImageView>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View v = inflater.inflate(R.layout.show_table_human, container, false);


	makeImageViewSet(v);// 做13个牌图套--robot
	// 显示牌
	for (int i = 0; i < this.cards.size(); i++) {
	    (imageViewSet).get(i).setImageResource(this.cards.get(i).getDrawableInt());
	}
	// 给牌设动画
	TranslateAnimation animation = new TranslateAnimation(0, 0, 120, 0);
	animation.setDuration(1000); // 设置持续时间5秒
	for (ImageView imageView : imageViewSet) {
	    imageView.startAnimation(animation);
	}
	return v;
    }

    private void makeImageViewSet(View v) {
	ImageView iv14 = (ImageView) v.findViewById(R.id.iv14);
	ImageView iv15 = (ImageView) v.findViewById(R.id.iv15);
	ImageView iv16 = (ImageView) v.findViewById(R.id.iv16);
	ImageView iv17 = (ImageView) v.findViewById(R.id.iv17);
	ImageView iv18 = (ImageView) v.findViewById(R.id.iv18);
	ImageView iv19 = (ImageView) v.findViewById(R.id.iv19);
	ImageView iv20 = (ImageView) v.findViewById(R.id.iv20);
	ImageView iv21 = (ImageView) v.findViewById(R.id.iv21);
	ImageView iv22 = (ImageView) v.findViewById(R.id.iv22);
	ImageView iv23 = (ImageView) v.findViewById(R.id.iv23);
	ImageView iv24 = (ImageView) v.findViewById(R.id.iv24);
	ImageView iv25 = (ImageView) v.findViewById(R.id.iv25);
	ImageView iv26 = (ImageView) v.findViewById(R.id.iv26);
	imageViewSet.add(iv14);
	imageViewSet.add(iv15);
	imageViewSet.add(iv16);
	imageViewSet.add(iv17);
	imageViewSet.add(iv18);
	imageViewSet.add(iv19);
	imageViewSet.add(iv20);
	imageViewSet.add(iv21);
	imageViewSet.add(iv22);
	imageViewSet.add(iv23);
	imageViewSet.add(iv24);
	imageViewSet.add(iv25);
	imageViewSet.add(iv26);
    }

    public List<ICard> getCards() {
	return cards;
    }

    public void setCards(List<ICard> cards) {
	this.cards = cards;
    }


}
