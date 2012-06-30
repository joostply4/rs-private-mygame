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
		
		player.getInventory().addItem(995, 5000000); //5m cash
		player.getInventory().addItem(1323, 1); //iron scim
		player.getInventory().addItem(1333, 1); //rune scim
		player.getInventory().addItem(841, 1); //short bow
		player.getInventory().addItem(861, 1); //magic shortbow
		player.getInventory().addItem(884, 10000); // iron arrows
		player.getInventory().addItem(386, 1000); // sharks
		player.getInventory().addItem(554, 10000); // fire rune
		player.getInventory().addItem(555, 10000); // water rune
		player.getInventory().addItem(556, 10000); // air rune
		player.getInventory().addItem(557, 10000); // earth rune
		player.getInventory().addItem(558, 10000); // mind rune
		player.getInventory().addItem(562, 10000); // chaos rune
		player.getInventory().addItem(560, 10000); // death rune
		player.getInventory().addItem(565, 10000); // blood rune
		player.getInventory().addItem(1381, 1); //staff of air
		//player.getInventory().addItem(0, 0); //
		//player.getInventory().addItem(0, 0); //
		//player.getInventory().addItem(0, 0); //
		//player.getInventory().addItem(0, 0); //
		//player.getInventory().addItem(0, 0); //
		//player.getInventory().addItem(0, 0); //
		//player.getInventory().addItem(0, 0); //
		//player.getInventory().addItem(0, 0); //
		//player.getInventory().addItem(0, 0); //
		//player.getInventory().addItem(0, 0); //
		//player.getInventory().addItem(0, 0); //
		//player.getInventory().addItem(0, 0); //

		player.getHintIconsManager().removeUnsavedHintIcon();
		player.getMusicsManager().reset();
		player.getCombatDefinitions().setAutoRelatie(true);
		player.getCombatDefinitions().refreshAutoRelatie();
		StarterMap.getSingleton().addIP(ip);
	}

}
