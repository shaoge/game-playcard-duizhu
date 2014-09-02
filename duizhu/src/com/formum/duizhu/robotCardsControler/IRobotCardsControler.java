package com.formum.duizhu.robotCardsControler;

import java.util.List;

import android.app.Activity;
import android.widget.ImageView;

import com.formum.duizhu.cardDeck.card.ICard;
/**
 * ��������ƿ���ͼ���п���
 * @author tangshaoge
 */
public interface IRobotCardsControler {
	/**
	 * �Ի�����ץ�����ƿ���ͼ���п���
	 * @param context
	 * @param card
	 * @param viewHost
	 * @return
	 */
	public ImageView showImage(Activity context, ICard card, int viewHost,
			int drawableBackInt);
	/**
	 *���top����ʾ����
	 * @return
	 */
	public List<ICard> getAllCardBeingShowedByThis();
/**
 * ��top�д�����
 * @param allCardBeingShowedByThis
 */
	public void setAllCardBeingShowedByThis(List<ICard> allCardBeingShowedByThis);
}
