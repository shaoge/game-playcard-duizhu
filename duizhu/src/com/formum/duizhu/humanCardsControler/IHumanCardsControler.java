package com.formum.duizhu.humanCardsControler;

import java.util.List;

import android.app.Activity;
import android.widget.ImageView;

import com.formum.duizhu.cardDeck.card.ICard;
/**
 * 对人类的牌可视图进行控制
 * @author tangshaoge
 */
public interface IHumanCardsControler {
	/**
	 * 对人类抓到的牌可视图进行控制
	 * @param context
	 * @param card
	 * @param viewHost
	 * @return
	 */
	public ImageView showImage(final Activity context, final ICard card, int viewHost);
	/**
	 *获得bottom中显示的牌
	 * @return
	 */
	public List<ICard> getAllCardBeingShowedByThis();
/**
 * 向bottom中存入牌
 * @param allCardBeingShowedByThis
 */
	public void setAllCardBeingShowedByThis(List<ICard> allCardBeingShowedByThis);
}
