package com.formum.duizhu.player;


public abstract class Player {
	
	protected  IPlayer concreteIPlayer=null;


	public Player(IPlayer concreteIPlayer) {
		this.concreteIPlayer=concreteIPlayer;
		
	}

	/**
	 * ������ҵ���Ϊ
	 * @return
	 */
	public  IPlayer actions() {
		return concreteIPlayer;
	}
/**
 * ����������
 * @return
 */
	public abstract String getName() ;
/**
 *��ò��Ժ�
 * @return
 */
	public abstract int getMyStrategy();	

}
