package com.rs.game.minigames;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;

public class GodWars {
	public static boolean HandleObjects(Player player, ObjectDefinitions objDef) {
		int id = objDef.id;

		switch (objDef.name.toLowerCase()) {
		case "ice bridge":
			if (id == 26439) {
				player.setNextWorldTile(new WorldTile(2885, 5345, 2));
				return true;
			}
			break;

		case "big door":
			if (id == 26384) {
				if (player.getInventory().containsItem(2347, 1)) {
					player.setNextWorldTile(new WorldTile(2850, 5333, 2));
				} else {
					player.getPackets().sendGameMessage("You need a Hammer.");
				}
				return true;
			}
			// arma door
			if (id == 26426) {
				// if( == 40) {
				player.setNextWorldTile(new WorldTile(2839, 5296, 2));
				// }
			}
			break;

		case "pillar":
			if (id == 26303) {
				if (player.getInventory().containsItem(9418, 1)) {
					player.setNextWorldTile(new WorldTile(2871, 5269, 2));
				} else {
					player.getPackets().sendGameMessage(
							"You need a Mith grapple.");
				}
				return true;
			}
			break;
		}
		return false;
	}

	public void armKillCount() {

	}

	public void banKillCount() {

	}

	public void sarKillCount() {

	}

	public void zamKillCount() {

	}
}