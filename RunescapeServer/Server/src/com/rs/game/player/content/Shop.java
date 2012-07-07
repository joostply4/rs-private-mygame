package com.rs.game.player.content;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.player.CombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.ItemBonuses;
import com.rs.utils.ItemExamines;
import com.rs.utils.ItemSetsKeyGenerator;

public class Shop {

	private static final int MAIN_STOCK_ITEMS_KEY = ItemSetsKeyGenerator
			.generateKey();

	private static final int MAX_SHOP_ITEMS = 40;
	public static final int COINS = 995;

	private String name;
	private Item[] mainStock;
	private int[] defaultQuantity;
	private Item[] generalStock;
	private int money;
	private CopyOnWriteArrayList<Player> viewingPlayers;

	public Shop(String name, int money, Item[] mainStock, boolean isGeneralStore) {
		viewingPlayers = new CopyOnWriteArrayList<Player>();
		this.name = name;
		this.money = money;
		this.mainStock = mainStock;
		defaultQuantity = new int[mainStock.length];
		for (int i = 0; i < defaultQuantity.length; i++)
			defaultQuantity[i] = mainStock[i].getAmount();
		if (isGeneralStore && mainStock.length < MAX_SHOP_ITEMS)
			generalStock = new Item[MAX_SHOP_ITEMS - mainStock.length];
	}

	public boolean isGeneralStore() {
		return generalStock != null;
	}

	public void addPlayer(final Player player) {
		viewingPlayers.add(player);
		player.getTemporaryAttributtes().put("Shop", this);
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				viewingPlayers.remove(player);
				player.getTemporaryAttributtes().remove("Shop");
			}
		});
		player.getPackets().sendConfig(118, MAIN_STOCK_ITEMS_KEY); // sets
																	// mainstock
																	// items set
		player.getPackets().sendConfig(1496, -1); // sets samples items set
		player.getPackets().sendConfig(532, money);

		sendStore(player);
		player.getPackets().sendGlobalConfig(199, -1);// unknown
		player.getInterfaceManager().sendInterface(620); // opens shop
		// for (int i = 0; i < MAX_SHOP_ITEMS; i++) { // prices
		// player.getPackets().sendGlobalConfig(
		// 946 + i,
		// i < defaultQuantity.length ? defaultQuantity[i]
		// : generalStock != null ? 0 : -1);
		// }
		//
		player.getPackets().sendGlobalConfig(1241, 16750848);// unknown
		player.getPackets().sendGlobalConfig(1242, 15439903);// unknown
		player.getPackets().sendGlobalConfig(741, -1);// unknown
		player.getPackets().sendGlobalConfig(743, -1);// unknown
		player.getPackets().sendGlobalConfig(744, 0);// unknown
		if (generalStock != null)
			player.getPackets().sendHideIComponent(620, 19, false); // unlocks
																	// general
																	// store
																	// icon
		player.getPackets().sendIComponentSettings(620, 25, 0,
				getStoreSize() * 6, 1150); // unlocks stock slots
		sendInventory(player);
		player.getPackets().sendIComponentText(620, 20, name);
	}

	public void sendInventory(Player player) {
		player.getInterfaceManager().sendInventoryInterface(621);
		player.getPackets().sendItems(93, player.getInventory().getItems());
		player.getPackets().sendUnlockIComponentOptionSlots(621, 0, 0, 27, 0,
				1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(621, 0, 93, 4, 7,
				"Value", "Sell 1", "Sell 5", "Sell 10", "Sell 50", "Examine");
	}

	public int getSlotId(int clickSlotId) {
		return clickSlotId / 6;
	}

	public void buy(Player player, int clickSlot, int quantity) {
		int slotId = getSlotId(clickSlot);
		if (slotId >= getStoreSize())
			return;
		Item item = slotId >= mainStock.length ? generalStock[slotId
				- mainStock.length] : mainStock[slotId];
		if (item == null)
			return;
		if (item.getAmount() == 0) {
			player.getPackets().sendGameMessage(
					"There is no stock of that item at the moment.");
			return;
		}
		int dq = slotId >= mainStock.length ? 0 : defaultQuantity[slotId];
		int price = getBuyPrice(item, dq);
		int amountCoins = player.getInventory().getItems().getNumberOf(money);
		int maxQuantity = amountCoins / price;
		int buyQ = item.getAmount() > quantity ? quantity : item.getAmount();

		boolean enoughCoins = maxQuantity >= buyQ;
		if (!enoughCoins) {
			player.getPackets().sendGameMessage("You don't have enough coins.");
			buyQ = maxQuantity;
		} else if (quantity > buyQ)
			player.getPackets().sendGameMessage(
					"The shop has run out of stock.");
		if (item.getDefinitions().isStackable()) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.getPackets().sendGameMessage(
						"Not enough space in your inventory.");
				return;
			}
		} else {
			int freeSlots = player.getInventory().getFreeSlots();
			if (buyQ > freeSlots) {
				buyQ = freeSlots;
				player.getPackets().sendGameMessage(
						"Not enough space in your inventory.");
			}
		}
		if (buyQ != 0) {
			int totalPrice = price * buyQ;
			player.getInventory().deleteItem(money, totalPrice);
			player.getInventory().addItem(item.getId(), buyQ);
			item.setAmount(item.getAmount() - buyQ);
			if (item.getAmount() <= 0 && slotId >= mainStock.length)
				generalStock[slotId - mainStock.length] = null;
			refreshShop();
			sendInventory(player);
		}
	}

	public void restoreItems() {
		boolean needRefresh = false;
		for (int i = 0; i < mainStock.length; i++) {
			if (mainStock[i].getAmount() < defaultQuantity[i]) {
				mainStock[i].setAmount(mainStock[i].getAmount() + 1);
				needRefresh = true;
			} else if (mainStock[i].getAmount() > defaultQuantity[i]) {
				mainStock[i].setAmount(mainStock[i].getAmount() + -1);
				needRefresh = true;
			}
		}
		if (generalStock != null) {
			for (int i = 0; i < generalStock.length; i++) {
				Item item = generalStock[i];
				if (item == null)
					continue;
				item.setAmount(item.getAmount() - 1);
				if (item.getAmount() <= 0)
					generalStock[i] = null;
				needRefresh = true;
			}
		}
		if (needRefresh)
			refreshShop();
	}

	private boolean addItem(int itemId, int quantity) {
		for (Item item : mainStock) {
			if (item.getId() == itemId) {
				item.setAmount(item.getAmount() + quantity);
				refreshShop();
				return true;
			}
		}
		if (generalStock != null) {
			for (Item item : generalStock) {
				if (item == null)
					continue;
				if (item.getId() == itemId) {
					item.setAmount(item.getAmount() + quantity);
					refreshShop();
					return true;
				}
			}
			for (int i = 0; i < generalStock.length; i++) {
				if (generalStock[i] == null) {
					generalStock[i] = new Item(itemId, quantity);
					refreshShop();
					return true;
				}
			}
		}
		return false;
	}

	public void sell(Player player, int slotId, int quantity) {
		if (player.getInventory().getItemsContainerSize() < slotId)
			return;
		Item item = player.getInventory().getItem(slotId);
		if (item == null)
			return;
		int originalId = item.getId();
		if (item.getDefinitions().isNoted())
			item = new Item(item.getDefinitions().getCertId(), item.getAmount());
		if (item.getDefinitions().isDestroyItem()
				|| ItemConstants.getItemDefaultCharges(item.getId()) != -1
				|| !ItemConstants.isTradeable(item) || item.getId() == money) {
			player.getPackets().sendGameMessage("You can't sell this item.");
			return;
		}
		int dq = getDefaultQuantity(item.getId());
		if (dq == 0 && generalStock == null) {
			player.getPackets().sendGameMessage(
					"You can't sell this item to this shop.");
			return;
		}
		int price = getSellPrice(item, dq);
		int numberOff = player.getInventory().getItems()
				.getNumberOf(originalId);
		if (quantity > numberOff)
			quantity = numberOff;
		if (!addItem(item.getId(), quantity)) {
			player.getPackets().sendGameMessage("Shop is currently full.");
			return;
		}
		player.getInventory().deleteItem(originalId, quantity);
		player.getInventory().addItem(money, price * quantity);
	}

	public void sendValue(Player player, int slotId) {
		if (player.getInventory().getItemsContainerSize() < slotId)
			return;
		Item item = player.getInventory().getItem(slotId);
		if (item == null)
			return;
		if (item.getDefinitions().isNoted())
			item = new Item(item.getDefinitions().getCertId(), item.getAmount());
		if (item.getDefinitions().isNoted() || !ItemConstants.isTradeable(item)
				|| item.getId() == money) {
			player.getPackets().sendGameMessage("You can't sell this item.");
			return;
		}
		int dq = getDefaultQuantity(item.getId());
		if (dq == 0 && generalStock == null) {
			player.getPackets().sendGameMessage(
					"You can't sell this item to this shop.");
			return;
		}
		int price = getSellPrice(item, dq);
		player.getPackets().sendGameMessage(
				item.getDefinitions().getName()
						+ ": shop will buy for: "
						+ price
						+ " "
						+ ItemDefinitions.getItemDefinitions(money).getName()
								.toLowerCase()
						+ ". Right-click the item to sell.");
	}

	public int getDefaultQuantity(int itemId) {
		for (int i = 0; i < mainStock.length; i++)
			if (mainStock[i].getId() == itemId)
				return defaultQuantity[i];
		return 0;
	}

	public void sendInfo(Player player, int clickSlot) {
		int slotId = getSlotId(clickSlot);
		if (slotId >= getStoreSize())
			return;
		Item item = slotId >= mainStock.length ? generalStock[slotId
				- mainStock.length] : mainStock[slotId];
		if (item == null)
			return;
		player.getTemporaryAttributtes().put("ShopSelectedSlot", clickSlot);
		int dq = slotId >= mainStock.length ? 0 : defaultQuantity[slotId];
		int price = getBuyPrice(item, dq);
		player.getPackets().sendGameMessage(
				item.getDefinitions().getName()
						+ ": current costs "
						+ price
						+ " "
						+ ItemDefinitions.getItemDefinitions(money).getName()
								.toLowerCase() + ".");
		player.getInterfaceManager().sendInventoryInterface(449);
		player.getPackets().sendGlobalConfig(741, item.getId());
		player.getPackets().sendGlobalConfig(743, money);
		player.getPackets().sendUnlockIComponentOptionSlots(449, 15, -1, 0, 0,
				1, 2, 3, 4); // unlocks buy
		player.getPackets().sendGlobalConfig(744, price);
		player.getPackets().sendGlobalConfig(745, 0);
		player.getPackets().sendGlobalConfig(746, -1);
		player.getPackets().sendGlobalConfig(168, 98);
		player.getPackets().sendGlobalString(25, ItemExamines.getExamine(item));
		player.getPackets().sendGlobalString(34, ""); // quest id for some items
		int[] bonuses = ItemBonuses.getItemBonuses(item.getId());
		if (bonuses != null) {
			HashMap<Integer, Integer> requiriments = item.getDefinitions()
					.getWearingSkillRequiriments();
			if (requiriments != null && !requiriments.isEmpty()) {
				String reqsText = "";
				for (int skillId : requiriments.keySet()) {
					if (skillId > 24 || skillId < 0)
						continue;
					int level = requiriments.get(skillId);
					if (level < 0 || level > 120)
						continue;
					boolean hasReq = player.getSkills().getLevelForXp(skillId) >= level;
					reqsText += "<br>"
							+ (hasReq ? "<col=00ff00>" : "<col=ff0000>")
							+ "Level " + level + " "
							+ Skills.SKILL_NAME[skillId];
				}
				player.getPackets().sendGlobalString(26,
						"<br>Worn on yourself, requiring: " + reqsText);
			} else
				player.getPackets()
						.sendGlobalString(26, "<br>Worn on yourself");
			player.getPackets().sendGlobalString(
					35,
					"<br>Attack<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.STAB_ATTACK]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.SLASH_ATTACK]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.CRUSH_ATTACK]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.MAGIC_ATTACK]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.RANGE_ATTACK]
							+ "<br><col=ffff00>---" + "<br>Strength"
							+ "<br>Ranged Strength" + "<br>Magic Damage"
							+ "<br>Absorve Melee" + "<br>Absorve Magic"
							+ "<br>Absorve Ranged" + "<br>Prayer Bonus");
			player.getPackets()
					.sendGlobalString(36,
							"<br><br>Stab<br>Slash<br>Crush<br>Magic<br>Ranged<br>Summoning");
			player.getPackets().sendGlobalString(
					52,
					"<<br>Defence<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.STAB_DEF]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.SLASH_DEF]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.CRUSH_DEF]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.MAGIC_DEF]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.RANGE_DEF]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.SUMMONING_DEF]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.STRENGTH_BONUS]
							+ "<br><col=ffff00>"
							+ bonuses[CombatDefinitions.RANGED_STR_BONUS]
							+ "<br><col=ffff00>"
							+ bonuses[CombatDefinitions.MAGIC_DAMAGE]
							+ "%<br><col=ffff00>"
							+ bonuses[CombatDefinitions.ABSORVE_MELEE_BONUS]
							+ "%<br><col=ffff00>"
							+ bonuses[CombatDefinitions.ABSORVE_MAGE_BONUS]
							+ "%<br><col=ffff00>"
							+ bonuses[CombatDefinitions.ABSORVE_RANGE_BONUS]
							+ "%<br><col=ffff00>"
							+ bonuses[CombatDefinitions.PRAYER_BONUS]);
		} else
			player.getPackets().sendGlobalString(26, "");

	}

	public int getBuyPrice(Item item, int dq) {
		switch (item.getId()) {
		case 19005:
			item.getDefinitions().setValue(2500000);
			break;
		case 19065:
			item.getDefinitions().setValue(5000000);
			break;
		case 18937:
			item.getDefinitions().setValue(7500000);
			break;
		case 8009:
		case 8008:
		case 8007:
		case 8010:
		case 8011:
			item.getDefinitions().setValue(950);
			break;
		case 11694: // ags
			item.getDefinitions().setValue(15000); // 15k
			break;
		case 11696: // bgs
			item.getDefinitions().setValue(10000); // 10k
			break;
		case 6570: // fcape
			item.getDefinitions().setValue(1000); // 1k
			break;
		case 21371: // vine whip
			item.getDefinitions().setValue(3000); // 3k
			break;
		case 4151: // whip
			item.getDefinitions().setValue(100); // 100
			break;
		case 19335: // fury (or)
			item.getDefinitions().setValue(1000); // 1k
			break;
		case 18335: // arcane stream
			item.getDefinitions().setValue(5000); // 5k
			break;
		case 15220: // bring (i)
			item.getDefinitions().setValue(1000);// 1k
			break;
		case 13899: // vls
			item.getDefinitions().setValue(8000); // 8k
			break;
		case 13905: // vesta's spear
			item.getDefinitions().setValue(8000); // 8k
			break;
		case 13896: // stat helm
			item.getDefinitions().setValue(3000); // 3k
			break;
		case 13902: // stat hammer
			item.getDefinitions().setValue(6000); // 6k
			break;
		case 18349: // rapier
			item.getDefinitions().setValue(20000); // 20k
			break;
		case 18351: // cls
			item.getDefinitions().setValue(20000); // 20k
			break;
		case 18353: // cmaul
			item.getDefinitions().setValue(20000); // 20k
			break;
		case 18357: // cbow
			item.getDefinitions().setValue(20000); // 20k
			break;
		case 1050: // santa
			item.getDefinitions().setValue(500); // 500
			break;
		case 7462: // bgloves
			item.getDefinitions().setValue(100); // 100
			break;
		case 7458: // mith gloves
			item.getDefinitions().setValue(50); // 50
			break;
		case 15386: // sol
			item.getDefinitions().setValue(5000); // 5k
			break;
		case 6889: // mbook
			item.getDefinitions().setValue(1500); // 1.5k
			break;
		case 13734: // sprit shield
			item.getDefinitions().setValue(2500); // 2.5k
			break;
		case 13736: // blessed
			item.getDefinitions().setValue(2500); // 2.5k
			break;
		case 13738: // arcane
			item.getDefinitions().setValue(25000); // 25k
			break;
		case 13740: // divine
			item.getDefinitions().setValue(25000); // 25k
			break;
		case 13742: // ely
			item.getDefinitions().setValue(25000); // 25k
			break;
		case 13744: // spec
			item.getDefinitions().setValue(25000); // 25k
			break;
		case 22494: // polypore
			item.getDefinitions().setValue(30000); // 30k
			break;
		case 20748: // compelt
			item.getDefinitions().setValue(10000); // 10k
			break;

		/*
		 * ---------------------- End
		 * ---------------------------------------------
		 */
		case 11724:
			item.getDefinitions().setValue(85000000);
			break;
		case 11726:
			item.getDefinitions().setValue(90000000);
			break;
		case 11728:
			item.getDefinitions().setValue(30000000);
			break;
		case 4708:// barrows starts here
			item.getDefinitions().setValue(10000000);// 10m a pice
			break;
		case 4710:
			item.getDefinitions().setValue(10000000);
			break;
		case 4712:
			item.getDefinitions().setValue(10000000);
			break;
		case 4714:
			item.getDefinitions().setValue(10000000);
			break;
		case 4716:
			item.getDefinitions().setValue(10000000);
			break;
		case 4718:
			item.getDefinitions().setValue(10000000);
			break;
		case 4720:
			item.getDefinitions().setValue(10000000);
			break;
		case 4722:
			item.getDefinitions().setValue(10000000);
			break;
		case 4724:
			item.getDefinitions().setValue(10000000);
			break;
		case 4726:
			item.getDefinitions().setValue(10000000);
			break;
		case 4728:
			item.getDefinitions().setValue(10000000);
			break;
		case 4730:
			item.getDefinitions().setValue(10000000);
			break;
		case 4732:
			item.getDefinitions().setValue(10000000);
			break;
		case 4734:
			item.getDefinitions().setValue(10000000);
			break;
		case 4736:
			item.getDefinitions().setValue(10000000);
			break;
		case 4738:
			item.getDefinitions().setValue(10000000);
			break;
		case 4745:
			item.getDefinitions().setValue(10000000);
			break;
		case 9790:
			item.getDefinitions().setValue(200000);
			break;
		case 11700:
			item.getDefinitions().setValue(50000);
			break;
		case 14484:
			item.getDefinitions().setValue(100000);
			break;
		case 1044:
			item.getDefinitions().setValue(1000000);
			break;
		case 4747:
			item.getDefinitions().setValue(10000000);
			break;
		case 4749:
			item.getDefinitions().setValue(10000000);
			break;
		case 4751:
			item.getDefinitions().setValue(10000000);
			break;
		case 4753:
			item.getDefinitions().setValue(10000000);
			break;
		case 4755:
			item.getDefinitions().setValue(10000000);
			break;
		case 4757:
			item.getDefinitions().setValue(10000000);
			break;
		case 4759:
			item.getDefinitions().setValue(10000000);
			break;
		case 21736:
			item.getDefinitions().setValue(10000000);
			break;
		case 21744:
			item.getDefinitions().setValue(10000000);
			break;
		case 21752:
			item.getDefinitions().setValue(10000000);
			break;
		case 21760:
			item.getDefinitions().setValue(10000000);
			break;// barrows ends
		case 6585:
			item.getDefinitions().setValue(10000000);
			break;
		case 11718:
			item.getDefinitions().setValue(30000000);
			break;
		case 11720:
			item.getDefinitions().setValue(65000000);
			break;
		case 11722:
			item.getDefinitions().setValue(65000000);
			break;
		case 6685:
			item.getDefinitions().setValue(10000);
			break;
		case 11698:
			item.getDefinitions().setValue(25000000);
			break;
		case 11730:
			item.getDefinitions().setValue(14000000);
			break;
		case 11716:
			item.getDefinitions().setValue(10000000);
			break; // edge shop ends
		case 15486:// mage shop starts here
			item.getDefinitions().setValue(5000000);
			break;
		case 6916:
			item.getDefinitions().setValue(5000000);
			break;
		case 6918:
			item.getDefinitions().setValue(17000000);
			break;
		case 6924:
			item.getDefinitions().setValue(12000000);
			break;
		case 6922:
			item.getDefinitions().setValue(17000000);
			break;
		case 6920:
			item.getDefinitions().setValue(3000000);
			break;
		case 6914:
			item.getDefinitions().setValue(6000000);
			break; // mage ends here
		case 2497:// range shop starts here
			item.getDefinitions().setValue(2000);
			break;
		case 2491:
			item.getDefinitions().setValue(5000);
			break;
		case 2581:
			item.getDefinitions().setValue(2500000);
			break;
		case 2577:
			item.getDefinitions().setValue(30000000);
			break;
		case 11235:
			item.getDefinitions().setValue(5000000);
			break;
		case 11212:
			item.getDefinitions().setValue(500);
			break;
		case 9341:
			item.getDefinitions().setValue(300);
			break;
		case 4212:
			item.getDefinitions().setValue(200000);
			break;
		case 10499:
			item.getDefinitions().setValue(100000);
			break;
		case 9144:
			item.getDefinitions().setValue(800);
			break;// range shop ends

		}
		return item.getDefinitions().getValue();
	}

	public int getSellPrice(Item item, int dq) {
		switch (item.getId()) {
		case 2497:// range shop starts here
			item.getDefinitions().setValue(2000);
			break;
		case 2491:
			item.getDefinitions().setValue(5000);
			break;
		case 2581:
			item.getDefinitions().setValue(2500000);
			break;
		case 2577:
			item.getDefinitions().setValue(30000000);
			break;
		case 11235:
			item.getDefinitions().setValue(5000000);
			break;
		case 11212:
			item.getDefinitions().setValue(500);
			break;
		case 9341:
			item.getDefinitions().setValue(300);
			break;
		case 4212:
			item.getDefinitions().setValue(200000);
			break;
		case 10499:
			item.getDefinitions().setValue(100000);
			break;
		case 9144:
			item.getDefinitions().setValue(800);
			break;// range shop ends
		case 11724:
			item.getDefinitions().setValue(85000000);
			break;
		case 11726:
			item.getDefinitions().setValue(90000000);
			break;
		case 11728:
			item.getDefinitions().setValue(30000000);
			break;
		case 4708:// barrows starts here
			item.getDefinitions().setValue(10000000);// 10m a pice
			break;
		case 4710:
			item.getDefinitions().setValue(10000000);
			break;
		case 4712:
			item.getDefinitions().setValue(10000000);
			break;
		case 4714:
			item.getDefinitions().setValue(10000000);
			break;
		case 4716:
			item.getDefinitions().setValue(10000000);
			break;
		case 4718:
			item.getDefinitions().setValue(10000000);
			break;
		case 4720:
			item.getDefinitions().setValue(10000000);
			break;
		case 4722:
			item.getDefinitions().setValue(10000000);
			break;
		case 4724:
			item.getDefinitions().setValue(10000000);
			break;
		case 4726:
			item.getDefinitions().setValue(10000000);
			break;
		case 4728:
			item.getDefinitions().setValue(10000000);
			break;
		case 4730:
			item.getDefinitions().setValue(10000000);
			break;
		case 4732:
			item.getDefinitions().setValue(10000000);
			break;
		case 4734:
			item.getDefinitions().setValue(10000000);
			break;
		case 4736:
			item.getDefinitions().setValue(10000000);
			break;
		case 4738:
			item.getDefinitions().setValue(10000000);
			break;
		case 4745:
			item.getDefinitions().setValue(10000000);
			break;
		case 4747:
			item.getDefinitions().setValue(10000000);
			break;
		case 4749:
			item.getDefinitions().setValue(10000000);
			break;
		case 4751:
			item.getDefinitions().setValue(10000000);
			break;
		case 4753:
			item.getDefinitions().setValue(10000000);
			break;
		case 4755:
			item.getDefinitions().setValue(10000000);
			break;
		case 4757:
			item.getDefinitions().setValue(10000000);
			break;
		case 4759:
			item.getDefinitions().setValue(10000000);
			break;
		case 21736:
			item.getDefinitions().setValue(10000000);
			break;
		case 21744:
			item.getDefinitions().setValue(10000000);
			break;
		case 21752:
			item.getDefinitions().setValue(10000000);
			break;
		case 21760:
			item.getDefinitions().setValue(10000000);
			break;// barrows ends
		case 11696:// edge gen store starts here
			item.getDefinitions().setValue(20000000);
			break;
		case 6585:
			item.getDefinitions().setValue(10000000);
			break;
		case 11718:
			item.getDefinitions().setValue(30000000);
			break;
		case 11720:
			item.getDefinitions().setValue(65000000);
			break;
		case 11722:
			item.getDefinitions().setValue(65000000);
			break;
		case 6685:
			item.getDefinitions().setValue(10000);
			break;
		case 11698:
			item.getDefinitions().setValue(25000000);
			break;
		case 11730:
			item.getDefinitions().setValue(14000000);
			break;
		case 11700:
			item.getDefinitions().setValue(23000000);
			break;
		case 11716:
			item.getDefinitions().setValue(10000000);
			break;
		case 18349:
			item.getDefinitions().setValue(100000000);
			break;
		case 18351:
			item.getDefinitions().setValue(100000000);
			break;
		case 18353:
			item.getDefinitions().setValue(100000000);
			break;
		case 18355:
			item.getDefinitions().setValue(100000000);
			break;
		case 18357:// edgevill shop ends
			item.getDefinitions().setValue(100000000);
			break;
		case 15486:// mage shop starts here
			item.getDefinitions().setValue(5000000);
			break;
		case 6916:
			item.getDefinitions().setValue(5000000);
			break;
		case 6918:
			item.getDefinitions().setValue(17000000);
			break;
		case 6924:
			item.getDefinitions().setValue(12000000);
			break;
		case 6922:
			item.getDefinitions().setValue(17000000);
			break;
		case 6920:
			item.getDefinitions().setValue(3000000);
			break;
		case 6914:
			item.getDefinitions().setValue(6000000);
			break;
		case 6889:
			item.getDefinitions().setValue(8000000);
			break;// mage shop ends
		}
		return item.getDefinitions().getValue() / 2;
	}

	public void sendExamine(Player player, int clickSlot) {
		int slotId = getSlotId(clickSlot);
		if (slotId >= getStoreSize())
			return;
		Item item = slotId >= mainStock.length ? generalStock[slotId
				- mainStock.length] : mainStock[slotId];
		if (item == null)
			return;
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public void refreshShop() {
		for (Player player : viewingPlayers) {
			sendStore(player);
			player.getPackets().sendIComponentSettings(620, 25, 0,
					getStoreSize() * 6, 1150);
		}
	}

	public int getStoreSize() {
		return mainStock.length
				+ (generalStock != null ? generalStock.length : 0);
	}

	public void sendStore(Player player) {
		Item[] stock = new Item[mainStock.length
				+ (generalStock != null ? generalStock.length : 0)];
		System.arraycopy(mainStock, 0, stock, 0, mainStock.length);
		if (generalStock != null)
			System.arraycopy(generalStock, 0, stock, mainStock.length,
					generalStock.length);
		player.getPackets().sendItems(MAIN_STOCK_ITEMS_KEY, stock);
	}

}
