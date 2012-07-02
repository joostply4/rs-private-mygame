package com.rs.game.minigames;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;

public class GodWars {
	public static boolean HandleObjects(Player player, ObjectDefinitions objDef) {
		int id = objDef.id;

		switch (objDef.name.toLowerCase()) {
		case "ice bridge":
			// zammy bridge
			if (id == 26439) {
				if (player.getSkills().getLevel(Skills.AGILITY) == 70) {
					if (player.getLocation().getX() == 2885
							&& player.getLocation().getY() == 5333
							&& player.getLocation().getPlane() == 2) {
						player.setNextWorldTile(new WorldTile(2885, 5345, 2));
						return true;
					} else {
						player.setNextWorldTile(new WorldTile(2885, 5333, 2));
					}
				}
			}
			break;

		case "big door":
			// First door to bandos
			if (id == 26384) {
				if (player.getLocation().getX() == 2851
						&& player.getLocation().getY() == 5333
						&& player.getLocation().getPlane() == 2) {
					if (player.getInventory().containsItem(2347, 1)) {
						player.setNextWorldTile(new WorldTile(2850, 5333, 2));
					} else {
						player.getPackets().sendGameMessage(
								"You need a Hammer.");
					}
				} else {
					player.setNextWorldTile(new WorldTile(2851, 5333, 2));
				}
				return true;
			}

			// arma boss door
			if (id == 26426) {
				if (player.getLocation().getX() == 2839
						&& player.getLocation().getY() == 5295
						&& player.getLocation().getPlane() == 2) {
					player.setNextWorldTile(new WorldTile(2839, 5296, 2));
				} else {
					player.setNextWorldTile(new WorldTile(2839, 5295, 2));
				}
			}

			// bandos boss door
			if (id == 26425) {
				if (player.getLocation().getX() == 2863
						&& player.getLocation().getY() == 5354
						&& player.getLocation().getPlane() == 2) {
					player.setNextWorldTile(new WorldTile(2864, 5354, 2));
				} else {
					player.setNextWorldTile(new WorldTile(2863, 5354, 3));
				}
			}
			
			if(id == 26428) {
				if (player.getLocation().getX() == 2925
						&& player.getLocation().getY() == 5332
						&& player.getLocation().getPlane() == 2) {
					player.setNextWorldTile(new WorldTile(2925, 5331, 2));
				} else {
					player.setNextWorldTile(new WorldTile(2925, 5332, 2));
				}
			}
			break;

		case "pillar":
			// arma grapple
			if (id == 26303) {
				if (player.getLocation().getX() == 2871
						&& player.getLocation().getY() == 5279
						&& player.getLocation().getPlane() == 2) {
					if (player.getInventory().containsItem(9418, 1)) {
						player.setNextWorldTile(new WorldTile(2871, 5269, 2));
					} else {
						player.getPackets().sendGameMessage(
								"You need a Mith grapple.");
					}
				} else {
					player.setNextWorldTile(new WorldTile(2871, 5279, 2));
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