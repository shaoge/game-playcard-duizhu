package com.formum.duizhu.robotCardsControler;

import java.util.List;

import android.app.Activity;
import android.widget.ImageView;

import com.formum.duizhu.cardDeck.card.ICard;
/**
 * 对人类的牌可视图进行控制
 * @author tangshaoge
 */
public interface IRobotCardsControler {
	/**
	 * 对机器人抓到的牌可视图进行控制
	 * @param context
	 * @param card
	 * @param viewHost
	 * @return
	 */
	public ImageView showImage(Activity context, ICard card, int viewHost,
			int drawableBackInt);
	/**
	 *获得top中显示的牌
	 * @return
	 */
	public List<ICard> getAllCardBeingShowedByThis();
/**
 * 向top中存入牌
 * @param allCardBeingShowedByThis
 */
	public void setAllCardBeingShowedByThis(List<ICard> allCardBeingShowedByThis);
}
