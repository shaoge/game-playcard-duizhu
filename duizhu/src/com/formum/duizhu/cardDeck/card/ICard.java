package com.formum.duizhu.cardDeck.card;

import com.formum.duizhu.player.Player;

import android.app.Activity;
import android.widget.ImageView;

public interface ICard extends Comparable<ICard> {
	public int getDrawableInt();

	public int getPoint();

	public String getSuit();

	/**
	 * 在规定的上下文和宿主视图中显示自己的图片
	 * 
	 * @param context
	 * @param card
	 * @param viewHost
	 * @return
	 */
	public ImageView showImage(Activity context, ICard card, int viewHost);

	/**
	 * 在规定的上下文和宿主视图中显示自己的背面图片
	 * 
	 * @param context
	 * @param card
	 * @param viewHost
	 * @param drawableBackInt
	 * @return
	 */
	public ImageView showImage(Activity context, ICard card, int viewHost,
			int drawableBackInt);

	/**
	 * 给每张牌加上玩家属性，便于识别
	 * @param player1
	 */
	public void setPlayer(Player player);

	/**
	 * 获取玩家
	 * @return
	 */
	public Player getPlayer();
	/**
	 * 获取主牌阶级
	 * @return
	 */
	public int getTrumpRank();

/**
 * 设主牌阶级-20级最大
 * @param trumpRank
 */
	public void setTrumpRank(int trumpRank);

}