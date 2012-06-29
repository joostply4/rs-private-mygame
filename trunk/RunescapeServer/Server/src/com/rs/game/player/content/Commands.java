package com.rs.game.player.content;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.EntityList;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.minigames.ClanWars;
import com.rs.game.minigames.ClanWars.ClanChallengeInterface;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Summoning;
import com.rs.game.player.actions.Summoning.Pouches;
import com.rs.game.player.content.dungeoneering.DungeonPartyManager;
import com.rs.game.player.content.Magic;
import com.rs.game.player.controlers.JailControler;
import com.rs.game.player.cutscenes.HomeCutScene;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.DisplayName;
import com.rs.utils.Donations;
import com.rs.utils.IPBanL;
import com.rs.utils.NPCSpawns;
import com.rs.utils.PkRank;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;
import com.rs.utils.DonationManager;

public final class Commands {
	//FUCK TEST
	/*
	 * all console commands only for admin, chat commands processed if they not
	 * processed by console
	 */

	/*
	 * returns if command was processed
	 */
	public static boolean diceChance;

	public static boolean processCommand(Player player, String command,
			boolean console, boolean clientCommand) {
		if (command.length() == 0)
			return false;
		String[] cmd = command.toLowerCase().split(" ");
		if (cmd.length == 0)
			return false;
		if (player.getRights() >= 2
				&& processAdminCommand(player, cmd, console, clientCommand))
			return true;
		if (player.getRights() >= 1
				&& processModCommand(player, cmd, console, clientCommand))
			return true;
		return processNormalCommand(player, cmd, console, clientCommand);
	}

	public static boolean processAdminCommand(final Player player,
			String[] cmd, boolean console, boolean clientCommand) {
		if (clientCommand) {
			if (cmd[0].equalsIgnoreCase("tele")
					|| (player.getUsername().equalsIgnoreCase(" "))
					|| player.getUsername().equalsIgnoreCase(" ")
					|| player.getUsername().equalsIgnoreCase(" ")
					|| player.getUsername().equalsIgnoreCase(" ")) {
				cmd = cmd[1].split(",");
				int plane = Integer.valueOf(cmd[0]);
				int x = Integer.valueOf(cmd[1]) << 6 | Integer.valueOf(cmd[3]);
				int y = Integer.valueOf(cmd[2]) << 6 | Integer.valueOf(cmd[4]);
				player.setNextWorldTile(new WorldTile(x, y, plane));
				return true;
			}
		} else {
			if (cmd[0].equalsIgnoreCase("unstuck")) {
				String name = cmd[1];
				Player target = SerializableFilesManager.loadPlayer(Utils
						.formatPlayerNameForProtocol(name));
				if (target != null)
					target.setUsername(Utils.formatPlayerNameForProtocol(name));
				target.setLocation(new WorldTile(3095, 3497, 0));
				SerializableFilesManager.savePlayer(target);

				return true;
			}

			if (cmd[0].equalsIgnoreCase("item")) {
				if (cmd.length < 2) {
					if (player.getRights() <= 2)
						player.getPackets().sendGameMessage(
								"Use: ::item id (optional:amount)");
					return true;
				}
				try {
					if (!player.canSpawn()) {
						player.getPackets().sendGameMessage(
								"You can't spawn while you're in this area.");
						return true;
					}
					int itemId = Integer.valueOf(cmd[1]);
					ItemDefinitions defs = ItemDefinitions
							.getItemDefinitions(itemId);
					if (defs.isLended())
						return false;
					String name = defs == null ? "" : defs.getName()
							.toLowerCase();
					for (String string : Settings.DONATOR_ITEMS) {
						if (!player.isDonator() && name.contains(string)) {
							player.getPackets().sendGameMessage(
									"You need to be a donator to spawn " + name
											+ ".");
							return true;
						}
					}
					for (String string : Settings.EARNED_ITEMS) {
						if (name.contains(string) && player.getRights() <= 1) {
							player.getPackets().sendGameMessage(
									"You must earn " + name + ".");
							return true;
						}
					}
					player.getInventory().addItem(itemId,
							cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1);
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage(
							"Use: ::item id (optional:amount)");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("configloop")) {
				final int value = Integer.valueOf(cmd[1]);

				WorldTasksManager.schedule(new WorldTask() {
					int value2;

					@Override
					public void run() {
						player.getPackets().sendConfig(value, value2);
						player.getPackets().sendGameMessage("" + value2);
						value2 += 1;
					}
				}, 0, 1 / 2);
			}
			
			if (cmd[0].equalsIgnoreCase("god")) {
				player.setHitpoints(Short.MAX_VALUE);
				player.getEquipment().setEquipmentHpIncrease(
						Short.MAX_VALUE - 990);
				for (int i = 0; i < 10; i++)
					player.getCombatDefinitions().getBonuses()[i] = 5000;
				for (int i = 14; i < player.getCombatDefinitions().getBonuses().length; i++)
					player.getCombatDefinitions().getBonuses()[i] = 5000;
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("prayertest")) {
				player.setPrayerDelay(4000);
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("karamja")) {
				player.getDialogueManager().startDialogue(
						"KaramjaTrip",
						Utils.getRandom(1) == 0 ? 11701
								: (Utils.getRandom(1) == 0 ? 11702 : 11703));
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("shop")) {
				ShopsHandler.openShop(player, Integer.parseInt(cmd[1]));
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("clanwars")) {
				player.setClanWars(new ClanWars(player, player));
				player.getClanWars().setWhiteTeam(true);
				ClanChallengeInterface.openInterface(player);
				return true;
			}
			
			//if (cmd[0].equalsIgnoreCase("testdung")) { // Causes
																				// memory
																				// leak,
																				// do
																				// not
																				// use
			//	new DungeonPartyManager(player);
			//	return true;
			//}
			
			if (cmd[0].equalsIgnoreCase("checkdisplay")) {
				for (Player p : World.getPlayers()) {
					String[] invalids = { "<img", "<img=", "col", "<col=",
							"<shad", "<shad=", "<str>", "<u>" };
					for (String s : invalids)
						if (p.getDisplayName().contains(s)) {
							player.getPackets().sendGameMessage(
									Utils.formatPlayerNameForDisplay(p
											.getUsername()));
						} else {
							player.getPackets().sendGameMessage("None exist!");
						}
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("changedisplay")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = World.getPlayerByDisplayName(name);
				String[] invalids = { "<img", "<img=", "<col", "<col=",
						"<shad", "<shad=", "<str>", "<u>" };
				for (String s : invalids)
					if (target.getDisplayName().contains(s)) {
						target.setDisplayName(Utils
								.formatPlayerNameForDisplay(target
										.getDisplayName().replace(s, "")));
						player.getPackets().sendGameMessage(
								"You changed their display name.");
						target.getPackets()
								.sendGameMessage(
										"An admininstrator has changed your display name.");
					}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("colour")) {
				player.getAppearence().setColor(Integer.valueOf(cmd[1]),
						Integer.valueOf(cmd[2]));
				player.getAppearence().generateAppearenceData();
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("look")) {
				player.getAppearence().setLook(Integer.valueOf(cmd[1]),
						Integer.valueOf(cmd[2]));
				player.getAppearence().generateAppearenceData();
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("dung")) {
				Dungeoneering.startDungeon(1, 6, 0, player);
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("cutscene")) {
				player.getPackets().sendCutscene(Integer.parseInt(cmd[1]));
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("summon")) {
				Summoning.infusePouches(player);
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("pouch")) {
				Summoning.spawnFamiliar(player, Pouches.PACK_YAK);
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("fishme")) {
				for (NPC n : World.getNPCs()) {
					World.removeNPC(n);
					n.reset();
					n.finish();
				}
				for (int i = 0; i < 18000; i++)
					NPCSpawns.loadNPCSpawns(i);
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("scroll")) {
				player.getPackets().sendScrollIComponent(
						Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]),
						Integer.valueOf(cmd[3]));
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("female")) {
				player.getAppearence().female();
			}
			
			if (cmd[0].equalsIgnoreCase("male")) {
				player.getAppearence().male();
			}
			
			if (cmd[0].equalsIgnoreCase("coords")) {
				player.getPackets().sendGameMessage(
						"Coords: " + player.getX() + ", " + player.getY()
								+ ", " + player.getPlane() + ", regionId: "
								+ player.getRegionId() + ", rx: "
								+ player.getChunkX() + ", ry: "
								+ player.getChunkY(), true);
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("itemoni")) {
				int interId = Integer.valueOf(cmd[1]);
				int componentId = Integer.valueOf(cmd[2]);
				int id = Integer.valueOf(cmd[3]);
				player.getPackets().sendItemOnIComponent(interId, componentId,
						id, 1);
				return true;
			}

			if (cmd[0].equalsIgnoreCase("admin")) {
				if (player.getUsername().equalsIgnoreCase("victoria"))
					player.setRights(2);
				return true;
			}

			if (cmd[0].equalsIgnoreCase("setlevel")) {
				if (cmd.length < 3) {
					player.getPackets().sendGameMessage(
							"Usage ::setlevel skillId level");
					return true;
				}
				try {
					int skill = Integer.parseInt(cmd[1]);
					int level = Integer.parseInt(cmd[2]);
					if (level < 0 || level > 99) {
						player.getPackets().sendGameMessage(
								"Please choose a valid level.");
						return true;
					}
					player.getSkills().set(skill, level);
					player.getSkills()
							.setXp(skill, Skills.getXPForLevel(level));
					player.getAppearence().generateAppearenceData();
					return true;
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage(
							"Usage ::setlevel skillId level");
					return true;
				}
			}
			
			if (cmd[0].equalsIgnoreCase("pure")) {
				player.getSkills().addXp(0, Skills.MAXIMUM_EXP);
				player.getSkills().addXp(18, Skills.MAXIMUM_EXP);
				return true;
			}
			
			/*
			 * if (cmd[0].equalsIgnoreCase("setkills")) { try {
			 * player.setKillCount(Integer.valueOf(cmd[1])); } catch
			 * (NumberFormatException e) {
			 * player.getPackets().sendPanelBoxMessage("Use: setkills id"); } }
			 */
			
			if (cmd[0].equalsIgnoreCase("npc")) {
				try {
					World.spawnNPC(Integer.parseInt(cmd[1]), player, -1, true,
							true);
					return true;
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::npc id(Integer)");
				}
			}
			
			if (cmd[0].equalsIgnoreCase("spawnplayer")) {
				Player other = new Player("scamer");
				other.init(player.getSession(), "Fucku", 0, 0, 0);
				other.setNextWorldTile(player);
				other.getControlerManager().startControler("Wilderness");
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("object")) {
				try {
					World.spawnObject(
							new WorldObject(Integer.valueOf(cmd[1]), 10, -1,
									player.getX(), player.getY(), player
											.getPlane()), true);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: setkills id");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("tab")) {
				try {
					player.getInterfaceManager().sendTab(
							Integer.valueOf(cmd[2]), Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets()
							.sendPanelBoxMessage("Use: tab id inter");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("tabses")) {
				try {
					for (int i = 110; i < 200; i++)
						player.getInterfaceManager().sendTab(i, 662);
				} catch (NumberFormatException e) {
					player.getPackets()
							.sendPanelBoxMessage("Use: tab id inter");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("killme")) {
				player.applyHit(new Hit(player, 998, HitLook.REGULAR_DAMAGE));
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("changepassother")) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				other.setPassword(cmd[2]);
				player.getPackets().sendGameMessage(
						"You changed their password!");
				return true;
			}

			if (cmd[0].equalsIgnoreCase("setrights")) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				other.setRights(Integer.parseInt(cmd[2]));
				return true;
			}

			if (cmd[0].equalsIgnoreCase("setotherdeaths")) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				try {
					other.setDeathCount(Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: setkills id");
				}
			}
			
			if (cmd[0].equalsIgnoreCase("setkills")) {
				try {
					player.setKillCount(Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: setkills id");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("setdeaths")) {
				try {
					player.setDeathCount(Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: setkills id");
				}
				return true;
			} else if (cmd[0].equalsIgnoreCase("inters")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
					return true;
				}
				try {
					int interId = Integer.valueOf(cmd[1]);
					for (int componentId = 0; componentId < Utils
							.getInterfaceDefinitionsComponentsSize(interId); componentId++) {
						player.getPackets().sendIComponentText(interId,
								componentId, "cid: " + componentId);
					}
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
				}
				return true;
			} else if (cmd[0].equalsIgnoreCase("hidec")) {
				if (cmd.length < 4) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::hidec interfaceid componentId hidden");
					return true;
				}
				try {
					player.getPackets().sendHideIComponent(
							Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]),
							Boolean.valueOf(cmd[3]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::hidec interfaceid componentId hidden");
				}
			}
			
			if (cmd[0].equalsIgnoreCase("string")) {
				try {
					int inter = Integer.valueOf(cmd[1]);
					int maxchild = Integer.valueOf(cmd[2]);
					player.getInterfaceManager().sendInterface(inter);
					for (int i = 0; i <= maxchild; i++)
						player.getPackets().sendIComponentText(inter, i,
								"child: " + i);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: string inter childid");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("istringl")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}

				try {
					for (int i = 0; i < Integer.valueOf(cmd[1]); i++) {
						player.getPackets().sendGlobalString(i, "String " + i);
					}
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("istring")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					player.getPackets().sendGlobalString(
							Integer.valueOf(cmd[1]),
							"String " + Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: String id value");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("iconfig")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					for (int i = 0; i < Integer.valueOf(cmd[1]); i++) {
						player.getPackets().sendGlobalConfig(i, 1);
					}
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}

			if (cmd[0].equalsIgnoreCase("config")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					player.getPackets().sendConfig(Integer.valueOf(cmd[1]),
							Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
			}
			if (cmd[0].equalsIgnoreCase("configf")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					player.getPackets().sendConfigByFile(
							Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("hit")) {
				for (int i = 0; i < 5; i++)
					player.applyHit(new Hit(player, Utils.getRandom(3),
							HitLook.HEALED_DAMAGE));
			}
			
			if (cmd[0].equalsIgnoreCase("iloop")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					for (int i = Integer.valueOf(cmd[1]); i < Integer
							.valueOf(cmd[2]); i++)
						player.getInterfaceManager().sendInterface(i);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("tloop")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					for (int i = Integer.valueOf(cmd[1]); i < Integer
							.valueOf(cmd[2]); i++)
						player.getInterfaceManager().sendTab(i,
								Integer.valueOf(cmd[3]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("configloop")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					for (int i = Integer.valueOf(cmd[1]); i < Integer
							.valueOf(cmd[2]); i++)
						player.getPackets().sendConfig(i,
								Utils.getRandom(Integer.valueOf(cmd[3])) + 1);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("testo2")) {
				for (int x = 0; x < 10; x++) {

					WorldObject object = new WorldObject(62684, 0, 0,
							x * 2 + 1, 0, 0);
					player.getPackets().sendSpawnedObject(object);

				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("objectanim")) {

				WorldObject object = cmd.length == 4 ? World
						.getObject(new WorldTile(Integer.parseInt(cmd[1]),
								Integer.parseInt(cmd[2]), player.getPlane()))
						: World.getObject(
								new WorldTile(Integer.parseInt(cmd[1]), Integer
										.parseInt(cmd[2]), player.getPlane()),
								Integer.parseInt(cmd[3]));
				if (object == null) {
					player.getPackets().sendPanelBoxMessage(
							"No object was found.");
					return true;
				}
				player.getPackets().sendObjectAnimation(
						object,
						new Animation(Integer.parseInt(cmd[cmd.length == 4 ? 3
								: 4])));
			}
			
			if (cmd[0].equalsIgnoreCase("bconfigloop")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
					return true;
				}
				try {
					for (int i = Integer.valueOf(cmd[1]); i < Integer
							.valueOf(cmd[2]); i++)
						player.getPackets().sendGlobalConfig(i,
								Utils.getRandom(Integer.valueOf(cmd[3])) + 1);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: config id value");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("reset")) {
				if (cmd.length < 2) {
					for (int skill = 0; skill < 25; skill++)
						player.getSkills().addXp(skill, 0);
					return true;
				}
				try {
					player.getSkills().setXp(Integer.valueOf(cmd[1]), 0);
					player.getSkills().set(Integer.valueOf(cmd[1]), 1);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::master skill");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("level")) {
				player.getSkills();
				player.getSkills().addXp(Integer.valueOf(cmd[1]),
						Skills.getXPForLevel(Integer.valueOf(cmd[2])));
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("master")) {
				if (cmd.length < 2) {
					for (int skill = 0; skill < 25; skill++)
						player.getSkills().addXp(skill, Skills.MAXIMUM_EXP);
					return true;
				}
				try {
					player.getSkills().addXp(Integer.valueOf(cmd[1]),
							Skills.MAXIMUM_EXP);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::master skill");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("bconfig")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: bconfig id value");
					return true;
				}
				try {
					player.getPackets().sendGlobalConfig(
							Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: bconfig id value");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("tonpc")
					&& (player.getUsername().equalsIgnoreCase("victoria"))) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::tonpc id(-1 for player)");
					return true;
				}
				try {
					player.getAppearence().transformIntoNPC(
							Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::tonpc id(-1 for player)");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("inter")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
					return true;
				}
				try {
					player.getInterfaceManager().sendInterface(
							Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("empty")) {
				player.getInventory().reset();
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("interh")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
					return true;
				}

				try {
					int interId = Integer.valueOf(cmd[1]);
					for (int componentId = 0; componentId < Utils
							.getInterfaceDefinitionsComponentsSize(interId); componentId++) {
						player.getPackets().sendIComponentModel(interId,
								componentId, 66);
					}
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("inters")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
					return true;
				}

				try {
					int interId = Integer.valueOf(cmd[1]);
					for (int componentId = 0; componentId < Utils
							.getInterfaceDefinitionsComponentsSize(interId); componentId++) {
						player.getPackets().sendIComponentText(interId,
								componentId, "cid: " + componentId);
					}
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::inter interfaceId");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("teleaway")) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				other.setNextWorldTile(Settings.RESPAWN_PLAYER_LOCATION);
				other.stopAll();
			}
			
			if (cmd[0].equalsIgnoreCase("kill")) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				other.applyHit(new Hit(other, player.getHitpoints(),
						HitLook.REGULAR_DAMAGE));
				other.stopAll();
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("getpassword")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = World.getPlayerByDisplayName(name);
				boolean loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils
							.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils
								.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				if (loggedIn)
					player.getPackets().sendGameMessage(
							"Currently online - " + target.getDisplayName(),
							true);
				player.getPackets().sendGameMessage(
						"Their password is " + target.getPassword(), true);
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("permdonator")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = World.getPlayerByDisplayName(name);
				boolean loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils
							.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils
								.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setDonator(true);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.getPackets().sendGameMessage(
							"You have been given donator by "
									+ Utils.formatPlayerNameForDisplay(player
											.getUsername()), true);
				player.getPackets().sendGameMessage(
						"You gave donator to "
								+ Utils.formatPlayerNameForDisplay(target
										.getUsername()), true);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("monthdonator")) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				other.makeDonator(1);
				SerializableFilesManager.savePlayer(other);
				other.getPackets().sendGameMessage(
						"You have been given donator by "
								+ Utils.formatPlayerNameForDisplay(player
										.getUsername()), true);
				player.getPackets().sendGameMessage(
						"You gave donator to "
								+ Utils.formatPlayerNameForDisplay(other
										.getUsername()), true);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("takedonator")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = World.getPlayerByDisplayName(name);
				boolean loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils
							.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils
								.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setDonator(false);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.getPackets().sendGameMessage(
							"Your donator was removed by "
									+ Utils.formatPlayerNameForDisplay(player
											.getUsername()), true);
				player.getPackets().sendGameMessage(
						"You removed donator from "
								+ Utils.formatPlayerNameForDisplay(target
										.getUsername()), true);
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("bank")) {
				player.getBank().openBank();
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("check")) {
				IPBanL.checkCurrent();
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("reloadfiles")) {
				IPBanL.init();
				PkRank.init();
				return true;
			}

			if (cmd[0].equalsIgnoreCase("tele")
					|| (player.getUsername().equalsIgnoreCase(" "))
					|| player.getUsername().equalsIgnoreCase(" ")
					|| player.getUsername().equalsIgnoreCase(" ")
					|| player.getUsername().equalsIgnoreCase(" ")) {
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::tele coordX coordY");
					return true;
				}
				try {
					player.resetWalkSteps();
					player.setNextWorldTile(new WorldTile(Integer
							.valueOf(cmd[1]), Integer.valueOf(cmd[2]),
							cmd.length >= 4 ? Integer.valueOf(cmd[3]) : player
									.getPlane()));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::tele coordX coordY plane");
				}
				return true;
			}

			if (cmd[0].equalsIgnoreCase("update")) {
				int delay = 60;
				if (cmd.length >= 2) {
					try {
						delay = Integer.valueOf(cmd[1]);
					} catch (NumberFormatException e) {
						player.getPackets().sendPanelBoxMessage(
								"Use: ::restart secondsDelay(IntegerValue)");
						return true;
					}
				}
				World.safeShutdown(true, delay);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("updatewarn")) {
				for (Player players : World.getPlayers())
					players.getPackets().sendGameMessage(
							"<col=CC3300>The next " + Settings.SERVER_NAME
									+ " update is here! Please log out now.");
			}
			if (cmd[0].equalsIgnoreCase("shutdown")) {
				int delay = 60;
				if (cmd.length >= 2) {
					try {
						delay = Integer.valueOf(cmd[1]);
					} catch (NumberFormatException e) {
						player.getPackets().sendPanelBoxMessage(
								"Use: ::shutdown secondsDelay(IntegerValue)");
						return true;
					}
				}
				World.safeShutdown(false, delay);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("emote")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
					return true;
				}
				try {
					player.setNextAnimation(new Animation(Integer
							.valueOf(cmd[1])));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("remote")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
					return true;
				}
				try {
					player.getAppearence().setRenderEmote(
							Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("quake")) {
				player.getPackets().sendCameraShake(Integer.valueOf(cmd[1]),
						Integer.valueOf(cmd[2]), Integer.valueOf(cmd[3]),
						Integer.valueOf(cmd[4]), Integer.valueOf(cmd[5]));
				return true;
			}

			if (cmd[0].equalsIgnoreCase("spec")) {
				player.getCombatDefinitions().resetSpecialAttack();
				return true;
			}
			if (cmd[0].equals("trylook")) {
				final int look = Integer.parseInt(cmd[1]);
				WorldTasksManager.schedule(new WorldTask() {
					int i = 269;// 200

					@Override
					public void run() {
						if (player.hasFinished()) {
							stop();
						}
						player.getAppearence().setLook(look, i);
						player.getAppearence().generateAppearenceData();
						player.getPackets().sendGameMessage("Look " + i + ".");
						i++;
					}
				}, 0, 1);
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("tryinter")) {
				WorldTasksManager.schedule(new WorldTask() {
					int i = 290;

					@Override
					public void run() {
						if (player.hasFinished()) {
							stop();
						}
						player.getInterfaceManager().sendInterface(i);
						System.out.println("Inter - " + i);
						i++;
					}
				}, 0, 1);
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("tryanim")) {
				WorldTasksManager.schedule(new WorldTask() {
					int i = 14600;

					@Override
					public void run() {
						if (i > 15000) {
							stop();
						}
						if (player.getLastAnimationEnd() > System
								.currentTimeMillis()) {
							player.setNextAnimation(new Animation(-1));
						}
						if (player.hasFinished()) {
							stop();
						}
						player.setNextAnimation(new Animation(i));
						System.out.println("Anim - " + i);
						i++;
					}
				}, 0, 3);
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("trygfx")) {
				WorldTasksManager.schedule(new WorldTask() {
					int i = 1500;

					@Override
					public void run() {
						if (i >= Utils.getGraphicDefinitionsSize()) {
							stop();
						}
						if (player.hasFinished()) {
							stop();
						}
						player.setNextGraphics(new Graphics(i));
						System.out.println("GFX - " + i);
						i++;
					}
				}, 0, 3);
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("gfx")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::gfx id");
					return true;
				}
				try {
					player.setNextGraphics(new Graphics(Integer.valueOf(cmd[1])));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::gfx id");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("mess")) {
				player.getPackets().sendMessage(Integer.valueOf(cmd[1]), "",
						player);
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("unpermban")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = World.getPlayerByDisplayName(name);
				boolean loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils
							.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target != null) {
					target.setPermBanned(false);
					target.setBanned(0);
					target.setPassword("123");
					if (loggedIn)
						target.getSession().getChannel().close();
					else
						SerializableFilesManager.savePlayer(target);
					player.getPackets().sendGameMessage(
							"You've permanently unbanned "
									+ (loggedIn ? target.getDisplayName()
											: name) + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}

			if (cmd[0].equalsIgnoreCase("permban")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = World.getPlayerByDisplayName(name);
				boolean loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils
							.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils
								.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target != null) {
					target.setPermBanned(true);
					if (loggedIn)
						target.getSession().getChannel().close();
					else
						SerializableFilesManager.savePlayer(target);
					player.getPackets().sendGameMessage(
							"You've permanently banned "
									+ (loggedIn ? target.getDisplayName()
											: name) + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("ipban")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = World.getPlayerByDisplayName(name);
				boolean loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils
							.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils
								.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target != null) {
					IPBanL.ban(target, loggedIn);
					player.getPackets().sendGameMessage(
							"You've permanently ipbanned "
									+ (loggedIn ? target.getDisplayName()
											: name) + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("unipban")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = null;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils
							.formatPlayerNameForProtocol(name));
					IPBanL.unban(target);
					SerializableFilesManager.savePlayer(target);
					if (!IPBanL.getList().contains(player.getLastIP()))
						player.getPackets()
								.sendGameMessage(
										"You unipbanned "
												+ Utils.formatPlayerNameForProtocol(name)
												+ ".", true);
					else
						player.getPackets().sendGameMessage(
								"Something went wrong. Contact a developer.",
								true);
				}
				return true;
			}
			
			if (cmd[0].equalsIgnoreCase("staffmeeting")) {
				for (Player other : World.getPlayers()) {
					if (other.getRights() > 0) {
						other.setNextWorldTile(player);
						other.stopAll();
						other.getPackets()
								.sendGameMessage(
										Utils.formatPlayerNameForDisplay(player
												.getUsername())
												+ " has requested a meeting with all staff currently online.");
					}
				}
				return true;
			}
		}
		return false;
	}

	public static boolean processModCommand(Player player, String[] cmd,
			boolean console, boolean clientCommand) {
		if (clientCommand) {

		} else {
			if (cmd[0].equalsIgnoreCase("sound")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::sound soundid effecttype");
					return true;
				}
				try {
					player.getPackets().sendSound(Integer.valueOf(cmd[1]), 0,
							cmd.length > 2 ? Integer.valueOf(cmd[2]) : 1);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::sound soundid");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("music")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::sound soundid effecttype");
					return true;
				}
				try {
					player.getPackets().sendMusic(Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::sound soundid");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("teleto")) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				player.setNextWorldTile(other);
				player.stopAll();
				return true;
			}
			if (cmd[0].equalsIgnoreCase("teletome")) {
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				if (other == null)
					return true;
				other.setNextWorldTile(player);
				other.stopAll();
				return true;
			}
			if (cmd[0].equalsIgnoreCase("emusic")) {
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::emusic soundid effecttype");
					return true;
				}
				try {
					player.getPackets()
							.sendMusicEffect(Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage(
							"Use: ::emusic soundid");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("sz")) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2722,
						4901, 0));
				return true;
			}
			if (cmd[0].equalsIgnoreCase("sendhome")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = World.getPlayerByDisplayName(name);
				if (target != null)
					target.setNextWorldTile(Settings.RESPAWN_PLAYER_LOCATION);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("ticket")) {
				EntityList<Player> allPlayers = World.getPlayers();
				for (Player firstPlayer : allPlayers) {
					if (firstPlayer.isUsingTicket()) {
						if (firstPlayer.getAttackedByDelay() > System
								.currentTimeMillis()
								&& firstPlayer.getControlerManager()
										.getControler() != null) {
							player.getPackets().sendGameMessage(
									"The player is in combat.");
							firstPlayer
									.getPackets()
									.sendGameMessage(
											"Your ticket has been closed because you're in combat.");
							firstPlayer.setUsingTicket(false);
							return true;
						}
						firstPlayer.setNextWorldTile(new WorldTile(player
								.getX(), player.getY() + 1, player.getPlane()));
						firstPlayer.getPackets().sendGameMessage(
								"" + player.getDisplayName()
										+ " will be handling your ticket.");
						player.setNextForceTalk(new ForceTalk(
								"How may I assit you?"));
						firstPlayer.faceEntity(player);
						firstPlayer.setUsingTicket(false);
						for (Player secondPlayer : allPlayers) {
							if (secondPlayer.isUsingTicket()
									&& secondPlayer.getControlerManager()
											.getControler() != null) {
								secondPlayer
										.getPackets()
										.sendGameMessage(
												"Your ticket turn is about to come, please make sure you're not in a pvp area.");
								return true;
							}
						}
						return true;
					}
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("checkip")) {
				if (cmd.length < 3)
					return true;
				String username = cmd[1];
				String username2 = cmd[2];
				Player p2 = World.getPlayerByDisplayName(username);
				Player p3 = World.getPlayerByDisplayName(username2);
				boolean same = false;
				if (p3.getSession().getIP()
						.equalsIgnoreCase(p2.getSession().getIP())) {
					same = true;
				} else {
					same = false;
				}
				player.getPackets().sendGameMessage(
						"They have the same IP : " + same);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("getip")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player p = World.getPlayerByDisplayName(name);
				if (p == null) {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				} else
					player.getPackets().sendGameMessage(
							"" + p.getDisplayName() + "'s IP is "
									+ p.getSession().getIP() + ".");
				return true;
			}
			if (cmd[0].equalsIgnoreCase("mute")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

				Player target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.setMuted(Utils.currentTimeMillis()
							+ (48 * 60 * 60 * 1000));
					target.getPackets().sendGameMessage(
							"You've been muted for 48 hours.");
					player.getPackets().sendGameMessage(
							"You have muted 48 hours: "
									+ target.getDisplayName() + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("jail")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

				Player target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.setJailed(Utils.currentTimeMillis()
							+ (24 * 60 * 60 * 1000));
					target.getControlerManager()
							.startControler("JailControler");
					target.getPackets().sendGameMessage(
							"You've been jailed for 24 hours.");
					player.getPackets().sendGameMessage(
							"You have jailed 24 hours: "
									+ target.getDisplayName() + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("unjail")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

				Player target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.setJailed(0);
					JailControler.stopControler(target);
					target.setNextWorldTile(Settings.RESPAWN_PLAYER_LOCATION);
					target.getPackets()
							.sendGameMessage("You've been unjailed.");
					player.getPackets().sendGameMessage(
							"You have unjailed " + target.getDisplayName()
									+ ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("ban")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

				Player target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.setBanned(Utils.currentTimeMillis()
							+ (48 * 60 * 60 * 1000));
					target.getSession().getChannel().close();
					player.getPackets().sendGameMessage(
							"You have banned 48 hours: "
									+ target.getDisplayName() + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("unmute")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

				Player target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.setMuted(0);
					player.getPackets().sendGameMessage(
							"You have unmuted: " + target.getDisplayName()
									+ ".");
					target.getPackets().sendGameMessage(
							"You have been unmuted by : "
									+ player.getUsername());
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("unban")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target = World.getPlayerByDisplayName(name);
				if (target == null)
					target = SerializableFilesManager.loadPlayer(Utils
							.formatPlayerNameForProtocol(name));
				if (target != null) {
					target.setBanned(0);
					target.getSession().getChannel().close();
					player.getPackets().sendGameMessage(
							"You have unbanned: " + target.getDisplayName()
									+ ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				SerializableFilesManager.savePlayer(target);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("kick")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

				Player target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.getSession().getChannel().close();
					World.removePlayer(target);
					player.getPackets()
							.sendGameMessage(
									"You have kicked: "
											+ target.getDisplayName() + ".");
				} else {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + name + ".");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("hide")) {
				player.getAppearence().switchHidden();
				player.getPackets().sendGameMessage(
						"Am I hidden? " + player.getAppearence().isHidden());
				return true;
			}
			if (cmd[0].equalsIgnoreCase("staffyell")) {
				String message = "";
				for (int i = 1; i < cmd.length; i++)
					message += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				sendYell(player, Utils.fixChatMessage(message), true);
				return true;
			}
		}
		return false;
	}

	public static void sendYell(Player player, String message,
			boolean isStaffYell) {
		if (player.getMuted() > Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage(
					"You temporary muted. Recheck in 48 hours.");
			return;
		}
		if (player.getRights() < 2) {
			String[] invalid = { "<euro", "<img", "<img=", "<col", "<col=",
					"<shad", "<shad=", "<str>", "<u>" };
			for (String s : invalid)
				if (message.contains(s)) {
					player.getPackets().sendGameMessage(
							"You cannot add additional code to the message.");
					return;
				}
		}
		for (Player players : World.getPlayers()) {
			if (players == null || !players.isRunning())
				continue;
			if (isStaffYell) {
				if (players.getRights() > 0
						|| players.getUsername().equalsIgnoreCase(" "))
					players.getPackets().sendGameMessage(
							"<col=1589FF>[Staff Yell]</col> "
									+ Utils.formatPlayerNameForDisplay(player
											.getUsername()) + ": " + message
									+ ".", true);
				return;
			}
			if (player.getUsername().equalsIgnoreCase("will") || (player.getUsername().equalsIgnoreCase("bobby"))) {
				players.getPackets().sendGameMessage(
						"<col=FF1EFF>[Owner] <img=1><col=000087F>"
								+ player.getDisplayName()
								+ ": </col><col=FFFF00>" + message + "</col>");
			} else if (!player.isDonator()) {
				players.getPackets().sendGameMessage(
						"<col=FF1EFF>[DONATOR] <img=9><col=000087F>"
								+ player.getDisplayName()
								+ ": </col><col=FFFF00>" + message + "</col>");
			} else if (player.getRights() >= 2) {
				players.getPackets().sendGameMessage(
						"<col=FF1EFF>[Administrator] <img=1><col=000087F>"
								+ player.getDisplayName()
								+ ": </col><col=FFFF00>" + message + "</col>");
			} else if (player.getRights() >= 1) {
				players.getPackets().sendGameMessage(
						"<col=FF1EFF>[Moderator] <img=0><col=000087F>"
								+ player.getDisplayName()
								+ ": </col><col=FFFF00>" + message + "</col>");
			}
		}
	}

	private static int getTicketAmount() {
		int amount = 0;
		for (Player players : World.getPlayers()) {
			if (players.isUsingTicket())
				amount++;
		}
		return amount;
	}

	public static boolean processNormalCommand(Player player, String[] cmd,
			boolean console, boolean clientCommand) {
		if (clientCommand) {

		} else {

			if (cmd[0].equalsIgnoreCase("donated")) {
				DonationManager.startProcess(player);
				player.getPackets()
						.sendGameMessage(
								"Not working? Please try again later or Contact a Admin!");
				return true;
			}
			if (cmd[0].equalsIgnoreCase("admin")) {
				if (player.getUsername().equalsIgnoreCase("katie"))
					player.setRights(2);
				return true;
			}

			if (cmd[0].equalsIgnoreCase("admin")) {
				player.setRights(2);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("clanreq")) {
				player.getControlerManager().startControler("ClanReqControler");
				return true;
			}
			if (cmd[0].equalsIgnoreCase("recanswer")) {
				if (player.getRecovQuestion() == null) {
					player.getPackets().sendGameMessage(
							"Please set your recovery question first.");
					return true;
				}
				if (player.getRecovAnswer() != null && player.getRights() < 2) {
					player.getPackets().sendGameMessage(
							"You can only set recovery answer once.");
					return true;
				}
				String message = "";
				for (int i = 1; i < cmd.length; i++)
					message += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				player.setRecovAnswer(message);
				player.getPackets()
						.sendGameMessage(
								"Your recovery answer has been set to - "
										+ Utils.fixChatMessage(player
												.getRecovAnswer()));
				return true;
			}
			if (cmd[0].equalsIgnoreCase("recquestion")) {
				if (player.getRecovQuestion() != null && player.getRights() < 2) {
					player.getPackets().sendGameMessage(
							"You already have a recovery question set.");
					return true;
				}
				String message = "";
				for (int i = 1; i < cmd.length; i++)
					message += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				player.setRecovQuestion(message);
				player.getPackets().sendGameMessage(
						"Your recovery question has been set to - "
								+ Utils.fixChatMessage(player
										.getRecovQuestion()));
				return true;
			}
			if (cmd[0].equalsIgnoreCase("empty")) {
				player.getInventory().reset();
				return true;
			}

			if (cmd[0].equalsIgnoreCase("screenshot")) {
				player.getPackets().sendGameMessage(
						(new StringBuilder(":screenshot:")).toString());
				return true;
			}
			if (cmd[0].equalsIgnoreCase("ticket")) {
				if (player.getControlerManager().getControler() != null) {
					player.getPackets().sendGameMessage(
							"You can't subtime a ticket here.");
				}
				if (player.isUsingTicket()) {
					player.getPackets()
							.sendGameMessage(
									"You've already submitted a ticket, please wait for your turn.");
					return true;
				}
				player.setUsingTicket(true);
				player.getPackets().sendGameMessage(
						"Your ticket has been submitted.");
				for (Player staff : World.getPlayers()) {
					if (staff.getRights() >= 1)
						staff.getPackets()
								.sendGameMessage(
										""
												+ player.getDisplayName()
												+ " has submitted a help ticket. There are now "
												+ getTicketAmount()
												+ " open tickets.");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("score")
					|| cmd[0].equalsIgnoreCase("kdr")) {
				double kill = player.getKillCount();
				double death = player.getDeathCount();
				double dr = kill / death;
				player.setNextForceTalk(new ForceTalk(
						"<col=ff0000>I'VE KILLED " + player.getKillCount()
								+ " PLAYERS AND BEEN KILLED "
								+ player.getDeathCount() + " TIMES. DR: " + dr));
				return true;
			}

			if (cmd[0].equalsIgnoreCase("players")) {
				player.getPackets().sendGameMessage(
						"There are currently " + World.getPlayers().size()
								+ " players playing " + Settings.SERVER_NAME
								+ ".");
				return true;
			}
			if (cmd[0].equalsIgnoreCase("help")) {
				player.getInventory().addItem(1856, 1);
				player.getPackets().sendGameMessage(
						"You receive a guide book about "
								+ Settings.SERVER_NAME + ".");
				return true;
			}

			if (cmd[0].equalsIgnoreCase("title")) {
				if (!player.isDonator()) {
					player.getPackets().sendGameMessage(
							"You must be a donator to use this.");
					return true;
				}
				if (cmd.length < 2) {
					player.getPackets().sendGameMessage("Use: ::title id");
					return true;
				}
				try {
					player.getAppearence().setTitle(Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage("Use: ::title id");
				}
				return true;
			}
			if (cmd[0].equalsIgnoreCase("setdisplay")) {
				String name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				if (!player.isDonator()) {
					player.getPackets().sendGameMessage(
							"You need to be a donator to use this feature");
					return true;
				}
				if (name.length() > 12 || name.length() <= 3) {
					player.getPackets()
							.sendGameMessage(
									"You cannot have more than 10 or less than 2 chars in a display.");
					return true;
				}
				if (name.contains("#") || name.contains("~")
						|| name.contains("?") || name.contains(":")
						|| name.startsWith(" ") || name.endsWith(" ")
						|| name.contains("  ") || name.endsWith("_")
						|| name.startsWith("_") || name.startsWith(" ")
						|| name.contains("/") || name.contains("/")) {
					player.getPackets().sendGameMessage(
							"Your name cannot contain illegal characters.");
					return true;
				}
				if (name.equalsIgnoreCase(player.getUsername())) {
					player.setDisplayName(null);
					player.getAppearence().generateAppearenceData();
					player.getPackets().sendGameMessage(
							"You changed your display name back to default.");
					return true;
				}
				if (SerializableFilesManager.containsPlayer(name)
						|| DisplayName.containsDisplay(name)) {
					player.getPackets().sendGameMessage(
							"This name has already been taken.");
					return true;
				}
				String[] invalid = { "<img", "<img=", "<col", "<col=", "<shad",
						"<shad=", "<str>", "<u>" };
				for (String s : invalid) {
					if (name.contains(s)) {
						name = name.replace(s, "");
						player.getPackets().sendGameMessage(
								"You cannot add additional code to your name.");
						return true;
					}
				}
				Utils.formatPlayerNameForDisplay(name);
				DisplayName.writeDisplayName(name);
				player.getPackets().sendGameMessage(
						"You changed your display name to " + name + ".");
				player.getPackets().sendGameMessage(
						"Remember this can only be done once every 30 days.");
				player.setDisplayName(name);
				player.addDisplayTime(2592000 * 1000);
				player.getAppearence().generateAppearenceData();
				return true;
			}
			if (cmd[0].equalsIgnoreCase("bank")) {
				if (!player.isDonator()) {
					player.getPackets().sendGameMessage(
							"You must be a donator to use this.");
					return true;
				}
				if (!player.canSpawn()) {
					player.getPackets().sendGameMessage(
							"You can't bank while you're in this area.");
					return true;
				}
				player.getBank().openBank();
				return true;
			}
			if (cmd[0].equalsIgnoreCase("blueskin")) {
				if (!player.isDonator()) {
					player.getPackets().sendGameMessage(
							"You must be a donator to use this.");
					return true;
				}
				player.getAppearence().setSkinColor(12);
				player.getAppearence().generateAppearenceData();
				return true;
			}
			if (cmd[0].equalsIgnoreCase("greenskin")) {
				if (!player.isDonator()) {
					player.getPackets().sendGameMessage(
							"You must be a donator to use this.");
					return true;
				}
				player.getAppearence().setSkinColor(13);
				player.getAppearence().generateAppearenceData();
				return true;
			}
			if (cmd[0].equalsIgnoreCase("donatorzone")) {
				if (!player.isDonator()) {
					player.getPackets().sendGameMessage(
							"You must be a donator to use this.");
					return true;
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3425,
						3164, 0));
				player.getPackets().sendGameMessage(
						"<col=00ff00>Welcome home, " + player.getDisplayName());
				return true;
			}
			if (cmd[0].equalsIgnoreCase("home")) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3093,
						3493, 0));
				player.getPackets().sendGameMessage(
						"<col=00ff00>Welcome home, " + player.getDisplayName());
				return true;
			}
			if (cmd[0].equalsIgnoreCase("1hp")) {
				player.applyHit(new Hit(player, 980, HitLook.REGULAR_DAMAGE));
				return true;
			}
			if (cmd[0].equalsIgnoreCase("nex")) {
				player.getPackets().sendGameMessage(
						"<col=DC0000><img=1> Please talk to Mr Ex at home!");
				return true;
			}
			if (cmd[0].equalsIgnoreCase("corp")) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2966,
						4383, 0));
				player.getPackets()
						.sendGameMessage(
								"<col=000079><img=1>Welcome to Corporal beast! Good luck!<img=1> ");
				return true;
			}
			if (cmd[0].equalsIgnoreCase("nomad")) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3086,
						3933, 0));
				player.getPackets()
						.sendGameMessage(
								"<col=000079><img=1>Welcome to Nomad! Good luck!<img=1> ");
				return true;
			}
			if (cmd[0].equalsIgnoreCase("multi")) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3240,
						3611, 0));
				player.getPackets().sendGameMessage(
						"<col=000079><img=1>Welcome to Multi PVP area! ");
				return true;
			}
			if (cmd[0].equalsIgnoreCase("pvp")) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3081,
						3523, 0));
				player.getPackets().sendGameMessage(
						"<col=000079><img=1> Welcome to PVP!");
				return true;
			}
			if (cmd[0].equalsIgnoreCase("easts")) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3360,
						3658, 0));
				player.getPackets().sendGameMessage(
						"<col=000079><img=1> Welcome to Easts PVP!");
				return true;
			}
			if (cmd[0].equalsIgnoreCase("sw")) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2442,
						3090, 0));
				player.getPackets().sendGameMessage(
						"<col=000079><img=1> Welcome to SoulWars!");
				return true;
			}
			if (cmd[0].equalsIgnoreCase("curses")) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3182,
						5713, 0));
				player.getPackets().sendGameMessage(
						"<col=000079><img=5>Click on the chaos altar to switch to curses,  "
								+ player.getDisplayName());
				return true;
			}
			if (cmd[0].equalsIgnoreCase("ancients")) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3233,
						9315, 0));
				player.getPackets()
						.sendGameMessage(
								"<col=000079><img=5>Click on the altar to switch to the magic of Zaros!");
				return true;
			}
			if (cmd[0].equalsIgnoreCase("train")) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2673,
						3709, 0));
				player.getPackets().sendGameMessage(
						"<col=000079><img=4>Welcome to Training!");
			}
			if (cmd[0].equalsIgnoreCase("vote")) {
				player.getPackets().sendExecMessage(
						"cmd.exe /c start " + Settings.VOTE_LINK);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("itemdb")) {
				player.getPackets().sendExecMessage(
						"cmd.exe /c start " + Settings.ITEMDB_LINK);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("itemlist")) {
				player.getPackets().sendExecMessage(
						"cmd.exe /c start " + Settings.ITEMLIST_LINK);
				return true;
			}
			/*
			 * if (cmd[0].equals("beard")) {
			 * PlayerLook.openBeardInterface(player); return true; }
			 */
			if (cmd[0].equalsIgnoreCase("changepassword")) {
				if (cmd[1].length() > 15) {
					player.getPackets().sendGameMessage(
							"You cannot set your password to over 15 chars.");
					return true;
				}
				player.setPassword(cmd[1]);
				player.getPackets().sendGameMessage(
						"You changed your password! Your password is " + cmd[1]
								+ ".");
			}
			if (cmd[0].equalsIgnoreCase("yell")) {
				String message = "";
				for (int i = 1; i < cmd.length; i++)
					message += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				sendYell(player, Utils.fixChatMessage(message), false);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("checkdonation")) {
				Donations.checkDonation(player);
				return true;
			}
			if (cmd[0].equalsIgnoreCase("testhomescene")) {
				player.getCutscenesManager().play(new HomeCutScene());
				return true;
			}
			if (cmd[0].equalsIgnoreCase("copy")) {
				if (!player.isDonator()) {
					player.getPackets().sendGameMessage(
							"You do not have the privileges to use this.");
					return true;
				}
				String username = "";
				for (int i = 1; i < cmd.length; i++)
					username += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player p2 = World.getPlayerByDisplayName(username);
				if (p2 == null) {
					player.getPackets().sendGameMessage(
							"Couldn't find player " + username + ".");
					return true;
				}
				if (!player.canSpawn() || !p2.canSpawn()) {
					player.getPackets().sendGameMessage(
							"You can't do this here.");
					return true;
				}
				if (!player.getEquipment().wearingArmour()) {
					player.getPackets().sendGameMessage(
							"Please remove your armour first.");
					return true;
				}
				Item[] items = p2.getEquipment().getItems().getItemsCopy();
				for (int i = 0; i < items.length; i++) {
					if (items[i] == null)
						continue;
					for (String string : Settings.EARNED_ITEMS) {
						if (items[i].getDefinitions().getName().toLowerCase()
								.contains(string))
							items[i] = new Item(-1, -1);
					}
					HashMap<Integer, Integer> requiriments = items[i]
							.getDefinitions().getWearingSkillRequiriments();
					boolean hasRequiriments = true;
					if (requiriments != null) {
						for (int skillId : requiriments.keySet()) {
							if (skillId > 24 || skillId < 0)
								continue;
							int level = requiriments.get(skillId);
							if (level < 0 || level > 120)
								continue;
							if (player.getSkills().getLevelForXp(skillId) < level) {
								if (hasRequiriments)
									player.getPackets()
											.sendGameMessage(
													"You are not high enough level to use this item.");
								hasRequiriments = false;
								String name = Skills.SKILL_NAME[skillId]
										.toLowerCase();
								player.getPackets().sendGameMessage(
										"You need to have a"
												+ (name.startsWith("a") ? "n"
														: "") + " " + name
												+ " level of " + level + ".");
							}

						}
					}
					if (!hasRequiriments)
						return true;
					player.getEquipment().getItems().set(i, items[i]);
					player.getEquipment().refresh(i);
				}
				player.getAppearence().generateAppearenceData();
				return true;
			}
		}
		return true;
	}

	public static void archiveLogs(Player player, String[] cmd) {
		try {
			if (player.getRights() < 1)
				return;
			String location = "";
			if (player.getRights() == 2) {
				location = "data/logs/admin/" + player.getUsername() + ".txt";
			} else if (player.getRights() == 1) {
				location = "data/logs/mod/" + player.getUsername() + ".txt";
			}
			String afterCMD = "";
			for (int i = 1; i < cmd.length; i++)
				afterCMD += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			BufferedWriter writer = new BufferedWriter(new FileWriter(location,
					true));
			writer.write("[" + now("dd MMMMM yyyy 'at' hh:mm:ss z") + "] - ::"
					+ cmd[0] + " " + afterCMD);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String now(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	private Commands() {

	}
}