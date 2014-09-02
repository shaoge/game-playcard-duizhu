package com.formum.duizhu.player;

import com.formum.duizhu.recorder.Recorder;

public class HumanPlayer extends Player {
	private static volatile HumanPlayer INSTANCE = null;
	
	private final String name = "Human";

	private HumanPlayer(IPlayer concreteIPlayer) {
		super(concreteIPlayer);
		
	}

	public static HumanPlayer getInstance(IPlayer concreteIPlayer) {
		if (INSTANCE == null) {
			synchronized (HumanPlayer.class) {
				if (INSTANCE == null) {
					INSTANCE= new HumanPlayer(concreteIPlayer);
					Recorder.getInstance().onPlayerGetInstance(INSTANCE);//ע�ᵽRECORDER
				}
			}
		}
		return INSTANCE;
	}

	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getMyStrategy() {
		// TODO Auto-generated method stub
		return 0;
	}

}
