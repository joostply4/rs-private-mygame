package com.rs.game.player.starter;

import com.rs.game.player.Player;

/**
 * This class handles the giving of a starter kit.
 * 
 * @author Emperial
 * 
 */
public class Starter {

	public static final int MAX_STARTER_COUNT = 3;

	public static void appendStarter(Player player) {
		String ip = player.getSession().getIP();
		int count = StarterMap.getSingleton().getCount(ip);
		player.starter = 1;
		if (count >= MAX_STARTER_COUNT) {
			return;
		}
		
		player.getInventory().addItem(995, 5000000);

		player.getHintIconsManager().removeUnsavedHintIcon();
		player.getMusicsManager().reset();
		player.getCombatDefinitions().setAutoRelatie(true);
		player.getCombatDefinitions().refreshAutoRelatie();
		StarterMap.getSingleton().addIP(ip);
	}

}
