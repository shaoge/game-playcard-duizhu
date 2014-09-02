package com.formum.duizhu.cardDeck.card;

import com.formum.duizhu.player.Player;

import android.app.Activity;
import android.widget.ImageView;

public interface ICard extends Comparable<ICard> {
	public int getDrawableInt();

	public int getPoint();

	public String getSuit();

	/**
	 * �ڹ涨�������ĺ�������ͼ����ʾ�Լ���ͼƬ
	 * 
	 * @param context
	 * @param card
	 * @param viewHost
	 * @return
	 */
	public ImageView showImage(Activity context, ICard card, int viewHost);

	/**
	 * �ڹ涨�������ĺ�������ͼ����ʾ�Լ��ı���ͼƬ
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
	 * ��ÿ���Ƽ���������ԣ�����ʶ��
	 * @param player1
	 */
	public void setPlayer(Player player);

	/**
	 * ��ȡ���
	 * @return
	 */
	public Player getPlayer();
	/**
	 * ��ȡ���ƽ׼�
	 * @return
	 */
	public int getTrumpRank();

/**
 * �����ƽ׼�-20�����
 * @param trumpRank
 */
	public void setTrumpRank(int trumpRank);

}