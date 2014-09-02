package com.formum.duizhu.humanCardsControler;

import java.util.List;

import android.app.Activity;
import android.widget.ImageView;

import com.formum.duizhu.cardDeck.card.ICard;
/**
 * ��������ƿ���ͼ���п���
 * @author tangshaoge
 */
public interface IHumanCardsControler {
	/**
	 * ������ץ�����ƿ���ͼ���п���
	 * @param context
	 * @param card
	 * @param viewHost
	 * @return
	 */
	public ImageView showImage(final Activity context, final ICard card, int viewHost);
	/**
	 *���bottom����ʾ����
	 * @return
	 */
	public List<ICard> getAllCardBeingShowedByThis();
/**
 * ��bottom�д�����
 * @param allCardBeingShowedByThis
 */
	public void setAllCardBeingShowedByThis(List<ICard> allCardBeingShowedByThis);
}
