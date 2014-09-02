package com.formum.duizhu.player;


public abstract class Player {
	
	protected  IPlayer concreteIPlayer=null;


	public Player(IPlayer concreteIPlayer) {
		this.concreteIPlayer=concreteIPlayer;
		
	}

	/**
	 * 返回玩家的行为
	 * @return
	 */
	public  IPlayer actions() {
		return concreteIPlayer;
	}
/**
 * 获得玩家名称
 * @return
 */
	public abstract String getName() ;
/**
 *获得策略号
 * @return
 */
	public abstract int getMyStrategy();	

}
