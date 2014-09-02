package com.formum.duizhu.cardDeck.card;

import android.app.Activity;
import android.widget.ImageView;

import com.formum.duizhu.R;
import com.formum.duizhu.player.Player;
import com.formum.duizhu.util.IUtil;
import com.formum.duizhu.util.Util;

public class Card implements ICard  {
	public static final int backDrawbleInt=R.drawable.back;
	private String suit;
	private int point;
	private int drawableInt;
	private int trumpRank;
	private Player player;
	private IUtil util=Util.getInstance();


	public Card(String suit, int point,int drawableInt) {
		this.suit = suit;
		this.point = point;
		this.drawableInt=drawableInt;
	}


	public int getDrawableInt() {
		return drawableInt;
	}

	public int getPoint() {
		return point;
	}

	public String getSuit() {
		return suit;
	}

	@Override
	public int compareTo(ICard another) {
		Integer a=this.getPoint();
		Integer b=((Card) another).getPoint();
		return a.compareTo(b);
	}


	@Override
	public ImageView showImage(Activity context, ICard card, int viewHost) {
		return util.showImage(context, this, viewHost);
	}


	@Override
	public ImageView showImage(Activity context, ICard card, int viewHost,
			int drawableBackInt) {
		return util.showImage(context, card, viewHost, drawableBackInt);
	}


	public Player getPlayer() {
		return player;
	}


	public void setPlayer(Player player) {
		this.player = player;
	}


	public int getTrumpRank() {
		return trumpRank;
	}


	public void setTrumpRank(int trumpRank) {
		this.trumpRank = trumpRank;
	}

}
