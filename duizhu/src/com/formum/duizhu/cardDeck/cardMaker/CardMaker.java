package com.formum.duizhu.cardDeck.cardMaker;

import java.util.HashMap;
import java.util.Map;

import com.formum.duizhu.R;
import com.formum.duizhu.cardDeck.card.Card;
import com.formum.duizhu.cardDeck.card.ICard;

public class CardMaker implements ICardMaker {
	private static volatile CardMaker INSTANCE;

	private CardMaker() {

	}

	public static CardMaker getInstance() {
		if (CardMaker.INSTANCE == null) {
			synchronized (CardMaker.class) {
				if (CardMaker.INSTANCE == null) {
					return new CardMaker();
				}
			}

		}
		return INSTANCE;
	}

	@Override
	public ICard makeCard(String suit, int point) {
		ICard card = new Card(suit, point, this.getDrawableInt(suit, point));
		return card;
	}

	private Map<String, Integer> getSpadeMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("1", R.drawable.spade01);
		map.put("2", R.drawable.spade2);
		map.put("3", R.drawable.spade3);
		map.put("4", R.drawable.spade4);
		map.put("5", R.drawable.spade5);
		map.put("6", R.drawable.spade6);
		map.put("7", R.drawable.spade7);
		map.put("8", R.drawable.spade8);
		map.put("9", R.drawable.spade9);
		map.put("10", R.drawable.spade10);
		map.put("11", R.drawable.spade11);
		map.put("12", R.drawable.spade12);
		map.put("13", R.drawable.spade13);

		return map;
	}

	private Map<String, Integer> getClubMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("1", R.drawable.club01);
		map.put("2", R.drawable.club2);
		map.put("3", R.drawable.club3);
		map.put("4", R.drawable.club4);
		map.put("5", R.drawable.club5);
		map.put("6", R.drawable.club6);
		map.put("7", R.drawable.club7);
		map.put("8", R.drawable.club8);
		map.put("9", R.drawable.club9);
		map.put("10", R.drawable.club10);
		map.put("11", R.drawable.club11);
		map.put("12", R.drawable.club12);
		map.put("13", R.drawable.club13);

		return map;
	}

	private Map<String, Integer> getDiamondMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("1", R.drawable.diamond01);
		map.put("2", R.drawable.diamond2);
		map.put("3", R.drawable.diamond3);
		map.put("4", R.drawable.diamond4);
		map.put("5", R.drawable.diamond5);
		map.put("6", R.drawable.diamond6);
		map.put("7", R.drawable.diamond7);
		map.put("8", R.drawable.diamond8);
		map.put("9", R.drawable.diamond9);
		map.put("10", R.drawable.diamond10);
		map.put("11", R.drawable.diamond11);
		map.put("12", R.drawable.diamond12);
		map.put("13", R.drawable.diamond13);

		return map;
	}

	private Map<String, Integer> getHeartMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("1", R.drawable.heart01);
		map.put("2", R.drawable.heart2);
		map.put("3", R.drawable.heart3);
		map.put("4", R.drawable.heart4);
		map.put("5", R.drawable.heart5);
		map.put("6", R.drawable.heart6);
		map.put("7", R.drawable.heart7);
		map.put("8", R.drawable.heart8);
		map.put("9", R.drawable.heart9);
		map.put("10", R.drawable.heart10);
		map.put("11", R.drawable.heart11);
		map.put("12", R.drawable.heart12);
		map.put("13", R.drawable.heart13);

		return map;
	}

	private Map<String, Integer> getJokerMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("1", R.drawable.joker0);
		map.put("2", R.drawable.joker1);

		return map;
	}

	private int getDrawableInt(String suit, int point) {
		// Suits - spade (ºÚÌÒ£©£¬heart (ºìÌÒ£©£¬club£¨Ã·»¨£©£¬diamond£¨·½¿é£© ,Joker(Íõ)
		Integer rslt =0;
		if (suit.equals("spade")) {
			rslt = this.getSpadeMap().get(point + "");
		} else if (suit.equals("heart")) {
			rslt = this.getHeartMap().get(point + "");
		} else if (suit.equals("club")) {
			rslt = this.getClubMap().get(point + "");
		} else if (suit.equals("diamond")) {
			rslt = this.getDiamondMap().get(point + "");
		} else if (suit.equals("joker")) {
			rslt = this.getJokerMap().get(point + "");
		}
		return rslt.intValue();
	}

}
