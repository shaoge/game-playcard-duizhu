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

public class ShowTableRobot extends Fragment {
	
	private List<ICard> cards=new ArrayList<ICard>();
	private List<ImageView> imageViewSet=new ArrayList<ImageView>();
	



	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
    	View v = inflater.inflate(R.layout.show_table_robot,container, false);
    	
    	
    	makeImageViewSet(v);//做13个牌图套--robot
    	//显示牌	
    		for (int i = 0; i < cards.size(); i++) {
    			( imageViewSet).get(i).setImageResource(cards.get(i).getDrawableInt());
			}
    		//给机器人牌设动画
    		TranslateAnimation animation = new TranslateAnimation(-120, 0, -120, 0);   
    		animation.setDuration(3000); //设置持续时间5秒  
    		for (ImageView imageView : imageViewSet) {
    			imageView.startAnimation(animation); 
			}
    		
        return v;  
    }





	private void makeImageViewSet(View v) {
		ImageView iv01=(ImageView) v.findViewById(R.id.iv01);
    	ImageView iv02=(ImageView) v.findViewById(R.id.iv02);
    	ImageView iv03=(ImageView) v.findViewById(R.id.iv03);
    	ImageView iv04=(ImageView) v.findViewById(R.id.iv04);
    	ImageView iv05=(ImageView) v.findViewById(R.id.iv05);
    	ImageView iv06=(ImageView) v.findViewById(R.id.iv06);
    	ImageView iv07=(ImageView) v.findViewById(R.id.iv07);
    	ImageView iv08=(ImageView) v.findViewById(R.id.iv08);
    	ImageView iv09=(ImageView) v.findViewById(R.id.iv09);
    	ImageView iv10=(ImageView) v.findViewById(R.id.iv10);
    	ImageView iv11=(ImageView) v.findViewById(R.id.iv11);
    	ImageView iv12=(ImageView) v.findViewById(R.id.iv12);
    	ImageView iv13=(ImageView) v.findViewById(R.id.iv13);
    	imageViewSet.add(iv01);
    	imageViewSet.add(iv02);
    	imageViewSet.add(iv03);
    	imageViewSet.add(iv04);
    	imageViewSet.add(iv05);
    	imageViewSet.add(iv06);
    	imageViewSet.add(iv07);
    	imageViewSet.add(iv08);
    	imageViewSet.add(iv09);
    	imageViewSet.add(iv10);
    	imageViewSet.add(iv11);
    	imageViewSet.add(iv12);
    	imageViewSet.add(iv13);
	}


	public List<ICard> getCards() {
		return cards;
	}


	public void setCards(List<ICard> cards) {
		this.cards = cards;
	}



}
