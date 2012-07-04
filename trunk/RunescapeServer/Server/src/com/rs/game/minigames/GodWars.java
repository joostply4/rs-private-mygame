package com.rs.game.minigames;

import com.rs.Settings;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;

public class GodWars {
	public static int GW_MIN_X = 2821;
	public static int GW_MAX_X = 2931;
	public static int GW_MIN_Y = 5243;
	public static int GW_MAX_Y = 5373;
	public static int GW_MIN_Z = 0;
	public static int GW_MAX_Z = 2;

	public static int[] GOD_WARS_NPCS_ARMA = { 6229, 6230, 6231, 6232, 6233,
			6234, 6235, 6236, 6237, 6238, 6239, 6240, 6241, 6242, 6243, 6244,
			6245, 6246 };

	public static int[] GOD_WARS_NPCS_SARA = { 6255, 6257, 6256, 6258, 6259,
			6254 };

	public static int[] GOD_WARS_NPCS_ZAMM = { 6211, 6216, 6214, 6215, 6212,
			6213, 6219, 6220, 6221, 6210, 6218 };

	public static int[] GOD_WARS_NPCS_BAND = { 6277, 6278, 6276, 6279, 6280,
			6281, 6282, 6283, 6275, 6268, 6269, 6270, 6271, 6272, 6273, 6274 };

	public static void handlePointIncrement(Player player, int npcID) {
		if (isZammyMinion(npcID)) {
			player.setZamKillCount(player.getZamKillCount() + 1);
			player.getPackets().sendGameMessage(
					"Zammy Kill Count: " + player.getZamKillCount());
		}

		if (isArmadylMinion(npcID)) {
			player.setArmKillCount(player.getArmKillCount() + 1);
			player.getPackets().sendGameMessage(
					"Armadyl Kill Count: " + player.getArmKillCount());
		}

		if (isBandosMinion(npcID)) {
			player.setBanKillCount(player.getBanKillCount() + 1);
			player.getPackets().sendGameMessage(
					"Bandos Kill Count: " + player.getBanKillCount());
		}

		if (isSaraMinion(npcID)) {
			player.setSarKillCount(player.getSarKillCount() + 1);
			player.getPackets().sendGameMessage(
					"Saradomin Kill Count: " + player.getSarKillCount());
		}

		if (Settings.DEBUG)
			System.err.println("Unhandled God Minion: " + npcID + "["
					+ player.getUsername() + "]");
	}

	public static boolean isZammyMinion(int npcID) {
		for (int i : GOD_WARS_NPCS_ZAMM) {
			if (npcID == i) {
				return true;
			}
		}
		return false;
	}

	public static boolean isArmadylMinion(int npcID) {
		for (int i : GOD_WARS_NPCS_ARMA) {
			if (npcID == i) {
				return true;
			}
		}
		return false;
	}

	public static boolean isBandosMinion(int npcID) {
		for (int i : GOD_WARS_NPCS_ARMA) {
			if (npcID == i) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSaraMinion(int npcID) {
		for (int i : GOD_WARS_NPCS_BAND) {
			if (npcID == i) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInGodWars(WorldTile tile) {
		int x = tile.getX();
		int y = tile.getY();
		int z = tile.getPlane();

		if (x >= GW_MIN_X && x <= GW_MAX_X && y >= GW_MIN_Y && y <= GW_MAX_Y
				&& z >= GW_MIN_Z && z <= GW_MAX_Z) {
			return true;
		}

		return false;
	}

	public static boolean isGWNPC(int npcID, WorldTile tile) {
		for (int i : GOD_WARS_NPCS_ARMA) {
			if (npcID == i) {
				if (isInGodWars(tile)) {
					return true;
				}
			}
		}

		for (int i : GOD_WARS_NPCS_SARA) {
			if (npcID == i) {
				if (isInGodWars(tile)) {
					return true;
				}
			}
		}

		for (int i : GOD_WARS_NPCS_ZAMM) {
			if (npcID == i) {
				if (isInGodWars(tile)) {
					return true;
				}
			}
		}

		for (int i : GOD_WARS_NPCS_BAND) {
			if (npcID == i) {
				if (isInGodWars(tile)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean HandleObjects(Player player, ObjectDefinitions objDef) {
		int id = objDef.id;

		switch (objDef.name.toLowerCase()) {
		case "armadyl altar":
			if (id == 26439) {

			}
			break;
		case "bandos altar":
			if (id == 26289) {

			}
			break;
		case "zamorak altar":
			if (id == 26286) {

			}
			break;
		case "saradomin altar":
			if (id == 26287) {

			}
			break;

		case "ice bridge":
			// zammy bridge
			if (id == 26439) {
				if (player.getSkills().getLevel(Skills.AGILITY) >= 70) {
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

		case "rock":
			// sara rock
			if (id == 26444) {
				player.setNextWorldTile(new WorldTile(2908, 5265, 0));
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

			// sara boss door
			if (id == 26427) {
				if (player.getSarKillCount() == 40) {
					if (player.getLocation().getX() == 2908
							&& player.getLocation().getY() == 5265
							&& player.getLocation().getPlane() == 0) {
						player.setNextWorldTile(new WorldTile(2907, 5265, 0));
					} else if (player.getLocation().getX() == 2907
							&& player.getLocation().getY() == 5265
							&& player.getLocation().getPlane() == 0) {
						player.setNextWorldTile(new WorldTile(2912, 5300, 2));
					}
				}
			}
			
			// arma boss door
			if (id == 26426) {
				if (player.getArmKillCount() == 40) {
					if (player.getLocation().getX() == 2839
							&& player.getLocation().getY() == 5295
							&& player.getLocation().getPlane() == 2) {
						player.setNextWorldTile(new WorldTile(2839, 5296, 2));
					} else {
						player.setNextWorldTile(new WorldTile(2839, 5295, 2));
					}
				}
			}

			// bandos boss door
			if (id == 26425) {
				if (player.getBanKillCount() == 40) {
					if (player.getLocation().getX() == 2863
							&& player.getLocation().getY() == 5354
							&& player.getLocation().getPlane() == 2) {
						player.setNextWorldTile(new WorldTile(2864, 5354, 2));
					} else {
						player.setNextWorldTile(new WorldTile(2863, 5354, 2));
					}
				}
			}

			// zam boss door
			if (id == 26428) {
				if (player.getZamKillCount() == 40) {
					if (player.getLocation().getX() == 2925
							&& player.getLocation().getY() == 5332
							&& player.getLocation().getPlane() == 2) {
						player.setNextWorldTile(new WorldTile(2925, 5331, 2));
					} else {
						player.setNextWorldTile(new WorldTile(2925, 5332, 2));
					}
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
}