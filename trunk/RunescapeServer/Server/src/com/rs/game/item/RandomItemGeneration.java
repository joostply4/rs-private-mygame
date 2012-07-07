package com.rs.game.item;

import com.rs.game.player.Player;
import com.rs.utils.Misc;

public class RandomItemGeneration {
	private static int HARD_BOX = 18937;
	private static int EASY_BOX = 19005;
	private static int MEDIUM_BOX = 19065;

	public static int[] RandomItemGenerators = { EASY_BOX, MEDIUM_BOX, HARD_BOX };

	public static int[] RANDOM_ITEMS_EASY = {
			// full black (t) and (g)
			2583, 2585, 2587, 2589, 2591, 2593, 2595, 2597, 3472, 3473,
			// wizard robes (t) and (g)
			7386, 7388, 7390, 7392, 7394, 7396,
			// studded (t) and (g)
			7362, 7364, 7366, 7368,
			// elegant (blue green red)
			10424, 10426, 10428, 10430, 10432, 10434, 10404, 10406, 10408,
			10410, 10412, 10414,
			// zammy sara guthix robes
			10458, 10460, 10462, 10464, 10466, 10468, 10470 };
	
	public static int[] RANDOM_ITEMS_MEDIUM = {
			// adamant (t) and (g)
			2599, 2601, 2603, 2605, 2607, 2609, 2611, 2613,
			// boaters
			7319, 7321, 7323, 7325, 7327,
			// armadyl bandos ancient robes
			19380, 19382, 19384, 19386, 19388, 19390,
			// black purple elegant
			10400, 10402, 10416, 10418, 10420, 10422, 10436, 10438,
			// green d hide (t) and (g)
			7370, 7372, 7378, 7380,
			// ranger mage boots
			2577, 2579 };
	
	public static int[] RANDOM_ITEMS_HARD = {
			// rune (t) and (g) --
			2615, 2617, 2619, 2621, 2623, 2625,
			2627,
			2629,
			3476,
			3477,
			// zammy guthix sara rune --
			2653, 2655, 2657, 2659, 2661, 2663, 2665, 2667, 2669, 2671, 2673,
			2675,
			3478,
			3479,
			3480,
			// gilded --
			3481, 3483,
			3485,
			3486,
			3488,
			// guthix sara zam dhide
			10368, 10370, 10372, 10374,
			10376,
			10378,
			10380,
			10382,
			10384,
			10386,
			10388,
			10390,
			// blue d hide (t) and (g) --
			7374,
			7376,
			7382,
			7384,
			// robin hood hat --
			2581,
			// pirate hat --
			2651,
			// top hat
			13101,
			// dragon masks
			19281, 19284, 19287, 19290, 19293, 19296, 19299,
			19302,
			19305,
			// third age and druidic
			19308, 19311, 19314, 19317, 19320, 10330, 10332, 10334, 10336,
			10338, 10340, 10342, 10344, 10346, 10348, 10350,
			10352,
			// arma bandos ancient rune and d hide
			19398, 19401, 19404, 19407, 19410, 19413, 19416, 19419, 19422,
			19425, 19428, 19431, 19434, 19437, 19440, 19443, 19445, 19447,
			19449, 19451, 19453, 19455, 19457, 19459, 19461, 19463, 19465,
			// cavalier --
			2639, 2641, 2643 };

	private static int RANDOM_ITEMS_EASY_AMT = 6;
	private static int RANDOM_ITEMS_MEDIUM_AMT = 4;
	private static int RANDOM_ITEMS_HARD_AMT = 3;

	public static boolean HandleInventoryItemInteraction(Player player,
			int itemID) {
		if (!isRandItemGenerator(itemID)) {
			return false;
		} else {
			int[] RandomItems = getRandomItems(itemID);

			if (RandomItems == null) {
				return false;
			} else {
				int i = Misc.random(1, getRandItemsAmt(itemID));

				if (i == -1) {
					return false;
				} else {
					int[] ItemsToGiveToPlayer = new int[i];

					for (int iter = 0; iter < ItemsToGiveToPlayer.length; iter++) {
						ItemsToGiveToPlayer[iter] = RandomItems[Misc
								.random(RandomItems.length)];
					}

					if (player.getInventory().getFreeSlots() >= ItemsToGiveToPlayer.length) {
						player.getInventory().deleteItem(itemID, 1);

						for (int z : ItemsToGiveToPlayer) {
							player.getInventory().addItem(z, 1);
						}

						return true;
					} else {
						player.getPackets().sendGameMessage(
								"Please empty your inventory first.");
					}

				}
			}
		}

		return false;
	}

	private static int[] getRandomItems(int itemID) {
		if (itemID == EASY_BOX) {
			return RANDOM_ITEMS_EASY;
		} else if (itemID == MEDIUM_BOX) {
			return RANDOM_ITEMS_MEDIUM;
		} else if (itemID == HARD_BOX) {
			return RANDOM_ITEMS_HARD;
		}

		return null;
	}

	private static int getRandItemsAmt(int itemID) {
		if (itemID == EASY_BOX) {
			return RANDOM_ITEMS_EASY_AMT;
		} else if (itemID == MEDIUM_BOX) {
			return RANDOM_ITEMS_MEDIUM_AMT;
		} else if (itemID == HARD_BOX) {
			return RANDOM_ITEMS_HARD_AMT;
		}

		return -1;
	}

	private static boolean isRandItemGenerator(int itemID) {
		for (int i : RandomItemGenerators) {
			if (itemID == i) {
				return true;
			}
		}

		return false;
	}
}
