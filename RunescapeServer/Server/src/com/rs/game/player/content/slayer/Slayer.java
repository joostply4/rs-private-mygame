package com.rs.game.player.content.slayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Misc;

public class Slayer {

	public static int[] RING_OF_SLAYING = { 13281, 13282, 13283, 13284, 13285,
			13286, 13287, 13288 };

	private static int BUY_EXP_PRICE = 400;
	private static int BUY_EXP_EXP_AMT = 10000;

	private static int BUY_RING_PRICE = 75;
	private static int BUY_RING_ITEM_ID = RING_OF_SLAYING[0];

	private static int BUY_SLAYER_DART_PRICE = 35;
	private static int BUY_SLAYER_DART_AMT = 250;
	private static int BUY_SLAYER_DART_MINDS_ID = 558;
	private static int BUY_SLAYER_DART_DEATHS_ID = 560;

	private static int BUY_BROAD_BOLTS_PRICE = 35;
	private static int BUY_BROAD_BOLTS_AMT = 250;
	private static int BUY_BROAD_BOLTS_ID = 13280;

	private static int BUY_BROAD_ARROWS_PRICE = 35;
	private static int BUY_BROAD_ARROWS_AMT = 250;
	private static int BUY_BROAD_ARROWS_ID = 4160;

	private static int BUY_SLAYER_HELMET_PRICE = 400;
	private static int BUY_SLAYER_HELMET_ID = 13263;

	private static int PERM_REMOVE_CURRENT_TASK_PRICE = 100;
	private static int REASSIGN_CURRENT_TASK_PRICE = 30;

	public static void assignTask(Player player) {
		TaskSet ts = determineTaskSet(player.getSkills().getCombatLevel());

		List<SlayerTasks> possibleTasks = new ArrayList<SlayerTasks>();
		for (SlayerTasks task : SlayerTasks.values()) {
			if (task.type == ts) {
				if (player.getSkills().getLevel(Skills.SLAYER) >= task.requiredLevel) {
					if (!isMonsterRemovedFromChoices(player, task.simpleName)) {
						possibleTasks.add(task);
					}
				}
			}
		}

		if (possibleTasks.isEmpty()) {
			player.getSlayerTask().setTask(null);
			player.getSlayerTask().setMonstersLeft(0);

			return;
		} else {
			Collections.shuffle(possibleTasks);
			SlayerTasks newTask = possibleTasks.get(0);
			player.getSlayerTask().setTask(newTask);
			player.getSlayerTask().setMonstersLeft(
					Misc.random(newTask.min, newTask.max));
		}

		player.getPackets().sendGameMessage(
				"You have been assigned to kill "
						+ player.getSlayerTask().getTaskMonstersLeft() + " "
						+ player.getSlayerTask().getTask().simpleName + ".");
	}

	public static void reassignTask(Player player) {
		TaskSet ts = TaskSet.EASY;

		if (player.getSlayerTask().getTask().type == ts) {
			player.getPackets()
					.sendGameMessage(
							"Sorry but your current task is much to simple to be reassigned!");
			
			messageKillsLeft(player);

		} else {
			List<SlayerTasks> possibleTasks = new ArrayList<SlayerTasks>();
			for (SlayerTasks task : SlayerTasks.values()) {
				if (task.type == ts) {
					if (player.getSkills().getLevel(Skills.SLAYER) >= task.requiredLevel) {
						possibleTasks.add(task);
					}
				}
			}

			if (possibleTasks.isEmpty()) {
				player.getSlayerTask().setTask(null);
				player.getSlayerTask().setMonstersLeft(0);
			} else {
				Collections.shuffle(possibleTasks);
				SlayerTasks newTask = possibleTasks.get(0);
				player.getSlayerTask().setTask(newTask);
				player.getSlayerTask().setMonstersLeft(
						Misc.random(newTask.min, newTask.max));
			}

			player.getPackets().sendGameMessage(
					"You have been reassigned to kill "
							+ player.getSlayerTask().getTaskMonstersLeft()
							+ " " + player.getSlayerTask().getTask().simpleName
							+ ".");
		}

	}

	public static boolean isMonsterRemovedFromChoices(Player player,
			String simpleName) {
		String temp[] = player.getRemovedSlayerTasks();

		for (String s : temp) {
			if (s.equals(simpleName)) {
				return true;
			}
		}

		return false;
	}

	private static TaskSet determineTaskSet(int combatLevel) {
		if (combatLevel <= 40) { // Easy Tasks
			return TaskSet.EASY;
		} else if (combatLevel <= 80) { // Medium Tasks
			return TaskSet.MEDIUM;
		} else if (combatLevel > 81) {
			return TaskSet.HARD;
		}

		return TaskSet.EASY;
	}

	public static void initInterface(Player player, int interfaceId) {
		if (player != null) {
			switch (interfaceId) {
			case 161:
				player.getInterfaceManager().sendInterface(interfaceId);
				player.getPackets().sendIComponentText(interfaceId, 19,
						"" + player.getSlayerPoints());

				player.getPackets().sendIComponentText(interfaceId, 36,
						player.getRemovedSlayerTasks()[0]);
				player.getPackets().sendIComponentText(interfaceId, 30,
						player.getRemovedSlayerTasks()[1]);
				player.getPackets().sendIComponentText(interfaceId, 31,
						player.getRemovedSlayerTasks()[2]);
				player.getPackets().sendIComponentText(interfaceId, 32,
						player.getRemovedSlayerTasks()[3]);
				player.getPackets().sendIComponentText(interfaceId, 33,
						player.getRemovedSlayerTasks()[4]);

				player.getPackets().sendIComponentText(interfaceId, 21,
						"Buy More");
				break;
			case 163:
				player.getInterfaceManager().sendInterface(interfaceId);
				player.getPackets().sendIComponentText(interfaceId, 18,
						"" + player.getSlayerPoints());
				player.getPackets().sendIComponentText(interfaceId, 22, "");
				player.getPackets().sendIComponentText(interfaceId, 23, "");
				player.getPackets().sendIComponentText(interfaceId, 24,
						"Buy Slayer Helmet");

				player.getPackets().sendIComponentText(interfaceId, 25, "");
				player.getPackets().sendIComponentText(interfaceId, 26, "");
				player.getPackets().sendIComponentText(interfaceId, 27, "");
				player.getPackets().sendIComponentText(interfaceId, 20,
						"Buy More");

				player.getPackets().sendIComponentText(interfaceId, 29, "");
				player.getPackets().sendIComponentText(interfaceId, 30, "");

				player.getPackets().sendIComponentModel(interfaceId, 33, -1);
				player.getPackets().sendIComponentModel(interfaceId, 34, -1);
				player.getPackets().sendIComponentModel(interfaceId, 35, -1);
				break;
			case 164:
				player.getInterfaceManager().sendInterface(interfaceId);
				player.getPackets().sendIComponentText(interfaceId, 20,
						"" + player.getSlayerPoints());

				player.getPackets().sendIComponentText(interfaceId, 22,
						"Buy More");
				break;
			}
		}
	}

	public static void handleRewardsInterface(Player player, int interfaceId,
			int componentId) {
		switch (interfaceId) {
		case 161:
			switch (componentId) {
			case 14:
				initInterface(player, 163);
				break;
			case 15:
				initInterface(player, 164);
				break;
			case 37:
				player.setRemovedSlayerTasks(0, "-");
				initInterface(player, interfaceId);
				break;
			case 38:
				player.setRemovedSlayerTasks(1, "-");
				initInterface(player, interfaceId);
				break;
			case 39:
				player.setRemovedSlayerTasks(2, "-");
				initInterface(player, interfaceId);
				break;
			case 40:
				player.setRemovedSlayerTasks(3, "-");
				initInterface(player, interfaceId);
				break;
			case 41:
				player.setRemovedSlayerTasks(4, "-");
				initInterface(player, interfaceId);
				break;
			case 23:// reassign current task
				if (player.getSlayerTask().getTaskMonstersLeft() > 0) {
					if (canAffordIt(player, REASSIGN_CURRENT_TASK_PRICE)) {
						player.setSlayerPoints(player.getSlayerPoints()
								- REASSIGN_CURRENT_TASK_PRICE);
						resetCurrentSlayerTask(player);
					}
				} else {
					player.getPackets()
							.sendGameMessage(
									"You need to have a slayer task in order to get reassigned one.");
				}

				initInterface(player, interfaceId);
				break;
			case 24:// perm. remove the current task
				if (canAffordIt(player, PERM_REMOVE_CURRENT_TASK_PRICE)) {
					handleRemoveCurrentSlayerTask(player);
					player.setSlayerPoints(player.getSlayerPoints()
							- PERM_REMOVE_CURRENT_TASK_PRICE);
				}

				initInterface(player, interfaceId);
				break;
			}
			break;
		case 163:
			switch (componentId) {
			case 14:
				initInterface(player, 161);
				break;
			case 15:
				initInterface(player, 164);
				break;
			case 24:// buy slayer helmet
				if (canAffordIt(player, BUY_SLAYER_HELMET_PRICE)) {
					if (player.getInventory().addItem(BUY_SLAYER_HELMET_ID, 1)) {
						player.setSlayerPoints(player.getSlayerPoints()
								- BUY_SLAYER_HELMET_PRICE);
					}
				}

				initInterface(player, interfaceId);
				break;
			}
			break;
		case 164:
			switch (componentId) {
			case 17:
				initInterface(player, 161);
				break;
			case 16:
				initInterface(player, 163);
				break;
			case 24: // buy slayer exp
				if (canAffordIt(player, BUY_EXP_PRICE)) {
					player.setSlayerPoints(player.getSlayerPoints()
							- BUY_EXP_PRICE);
					player.getSkills().addXp(Skills.SLAYER, BUY_EXP_EXP_AMT,
							false);
				}

				initInterface(player, interfaceId);
				break;
			case 26:// buy ring of slaying
				if (canAffordIt(player, BUY_RING_PRICE)) {
					if (player.getInventory().addItem(BUY_RING_ITEM_ID, 1)) {
						player.setSlayerPoints(player.getSlayerPoints()
								- BUY_RING_PRICE);
					}
				}

				initInterface(player, interfaceId);
				break;
			case 28:// buy runes for slayer dart
				if (canAffordIt(player, BUY_SLAYER_DART_PRICE)) {
					if ((player.getInventory().getFreeSlots() >= 2)
							|| (player.getInventory().containsOneItem(
									BUY_SLAYER_DART_MINDS_ID,
									BUY_SLAYER_DART_DEATHS_ID))) {

						player.getInventory().addItem(BUY_SLAYER_DART_MINDS_ID,
								BUY_SLAYER_DART_AMT * 4);
						player.getInventory().addItem(
								BUY_SLAYER_DART_DEATHS_ID, BUY_SLAYER_DART_AMT);
						player.setSlayerPoints(player.getSlayerPoints()
								- BUY_SLAYER_DART_PRICE);
					} else {
						player.getPackets().sendGameMessage(
								"Not enough space in your inventory" + ".");
					}
				}

				initInterface(player, interfaceId);
				break;
			case 37:// buy broad bolts
				if (canAffordIt(player, BUY_BROAD_BOLTS_PRICE)) {
					if (player.getInventory().addItem(BUY_BROAD_BOLTS_ID,
							BUY_BROAD_BOLTS_AMT)) {
						player.setSlayerPoints(player.getSlayerPoints()
								- BUY_BROAD_BOLTS_PRICE);
					}
				}

				initInterface(player, interfaceId);
				break;
			case 39:// buy broad arrows
				if (canAffordIt(player, BUY_BROAD_ARROWS_PRICE)) {
					if (player.getInventory().addItem(BUY_BROAD_ARROWS_ID,
							BUY_BROAD_ARROWS_AMT)) {
						player.setSlayerPoints(player.getSlayerPoints()
								- BUY_BROAD_ARROWS_PRICE);
					}
				}

				initInterface(player, interfaceId);
				break;
			}
			break;
		}
	}

	public static void resetCurrentSlayerTask(Player player) {
		player.getSlayerTask().setMonstersLeft(0);

		player.getPackets().sendGameMessage(
				"You are now free to be assigned a new slayer task" + ".");
	}

	private static void handleRemoveCurrentSlayerTask(Player player) {
		if (player.getSlayerTask().getTaskMonstersLeft() > 0) {
			if (canPlayerRemoveAnymoreTasks(player)) {
				int i = findFreeRemovedTasksID(player);
				if (i == -1) {
					player.getPackets()
							.sendGameMessage(
									"Please notify an Admin that i == -1 when removing a slayer task. Thank you!");
				} else {
					player.setRemovedSlayerTasks(i, player.getSlayerTask()
							.getTask().simpleName);
				}
				resetCurrentSlayerTask(player);
			} else {
				player.getPackets()
						.sendGameMessage(
								"Sorry but you have already removed the maximum amount of tasks.");
			}
		} else {
			player.getPackets()
					.sendGameMessage(
							"You need to have a slayer task in order to remove a task.");

		}
	}

	private static int findFreeRemovedTasksID(Player player) {
		String[] temp = player.getRemovedSlayerTasks();

		for (int i = 0; i < temp.length; i++) {
			if (temp[i].equals("-")) {
				return i;
			}
		}

		return -1;
	}

	private static boolean canPlayerRemoveAnymoreTasks(Player player) {
		String[] temp = player.getRemovedSlayerTasks();

		for (String s : temp) {
			if (s.equals("-")) {
				return true;
			}
		}

		return false;
	}

	private static boolean canAffordIt(Player player, int buyPrice) {
		if (player.getSlayerPoints() >= buyPrice) {
			return true;
		} else {
			player.getPackets()
					.sendGameMessage(
							"Sorry but you dont have enough slayer points to afford that!");
		}

		return false;
	}

	public static void messageKillsLeft(Player player) {
		player.getPackets().sendGameMessage(
				"Your current task is to kill "
						+ player.getSlayerTask().getTaskMonstersLeft() + " "
						+ player.getSlayerTask().getTask().simpleName + ".");

	}

}
