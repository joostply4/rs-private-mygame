package com.rs.tools;

public class NPCSpawnGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int NPCID = 1265;
		
		//Rectangle of how you want them spawned
		
		//Taking the perspective of facing NORTH
		//bottom right x should ALWAYS be higher than top left x
		//top left y should ALWAYS be higher than bottom right y
		int top_left_x = 2694;
		int top_left_y = 3726;
		int bottom_right_x = 2714;
		int bottom_right_y = 3707;
		
		int difference_in_x = bottom_right_x - top_left_x;
		int difference_in_y = top_left_y - bottom_right_y;
		
		int z = 0;
		
		int how_many_npcs_do_you_want = 40;
		
		for (int i = 0; i <= how_many_npcs_do_you_want; i++) {
			int randX = top_left_x + (int)(Math.random() * difference_in_x);
			int randY = bottom_right_y + (int)(Math.random() * difference_in_y);
			System.out.println(NPCID + " - " + randX + " " + randY + " " + z);
		}
	}

}
