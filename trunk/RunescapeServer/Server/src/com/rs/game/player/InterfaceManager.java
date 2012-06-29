package com.rs.game.player;

import java.util.concurrent.ConcurrentHashMap;

public class InterfaceManager {

	public static final int FIXED_WINDOW_ID = 548;
	public static final int RESIZABLE_WINDOW_ID = 746;
	public static final int CHAT_BOX_TAB = 13;
	public static final int FIXED_SCREEN_TAB_ID = 9;
	public static final int FIXED_SCREEN2_TAB_ID = 11;
	public static final int RESIZABLE_SCREEN_TAB_ID = 12;
	public static final int FIXED_INV_TAB_ID = 199; // wrong
	public static final int RESIZABLE_INV_TAB_ID = 87;
	private Player player;

	private final ConcurrentHashMap<Integer, int[]> openedinterfaces = new ConcurrentHashMap<Integer, int[]>();

	private boolean resizableScreen;
	private int windowsPane;

	public InterfaceManager(Player player) {
		this.player = player;
	}

	public void sendTab(int tabId, int interfaceId) {
		player.getPackets().sendInterface(true,
				resizableScreen ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID, tabId,
				interfaceId);
	}

	public void sendChatBoxInterface(int interfaceId) {
		player.getPackets().sendInterface(true, 752, CHAT_BOX_TAB, interfaceId);
	}

	public void closeChatBoxInterface() {
		player.getPackets().closeInterface(CHAT_BOX_TAB);
	}

	public void sendInterface(int interfaceId) {
		player.getPackets()
				.sendInterface(
						false,
						resizableScreen ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID,
						resizableScreen ? RESIZABLE_SCREEN_TAB_ID
								: FIXED_SCREEN_TAB_ID, interfaceId);
	}

	public void sendInventoryInterface(int childId) {
		player.getPackets().sendInterface(false,
				resizableScreen ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID,
				resizableScreen ? RESIZABLE_INV_TAB_ID : FIXED_INV_TAB_ID,
				childId);
	}

	public final void sendInterfaces() {
		if (player.getDisplayMode() == 2 || player.getDisplayMode() == 3) {
			resizableScreen = true;
			sendFullScreenInterfaces();
		} else {
			resizableScreen = false;
			sendFixedInterfaces();
		}
		player.getCombatDefinitions().sendUnlockAttackStylesButtons();
		player.getMusicsManager().unlockMusicPlayer();
		player.getInventory().unlockInventoryOptions();
		player.getPrayer().unlockPrayerBookButtons();
		if (player.getFamiliar() != null && player.isRunning())
			player.getFamiliar().unlock();
		player.getControlerManager().sendInterfaces();
	}

	public void replaceRealChatBoxInterface(int interfaceId) {
		player.getPackets().sendInterface(true, 752, 12, interfaceId);
	}

	public void closeReplacedRealChatBoxInterface() {
		player.getPackets().closeInterface(752, 12);
	}

	public void sendFullScreenInterfaces() {
		player.getPackets().sendWindowsPane(746, 0);
		sendTab(15, 745);
		sendTab(19, 751);
		sendTab(72, 752);
		sendTab(73, 754);
		sendTab(177, 748); // 177
		sendTab(178, 749);
		sendTab(179, 750);
		sendTab(180, 747); // summoning
		player.getPackets().sendInterface(true, 752, 9, 137);
		sendTab(90, 884);
		sendTab(92, 320);
		sendMsPortal();
		sendTab(91, 1056);// 259); // Achievement tab
		sendInventory();
		sendEquipment();
		sendPrayerBook();
		sendMagicBook();
		sendTab(99, 550); // friend list
		sendTab(100, 1109); // 551 ignore now friendchat
		sendTab(101, 1110); // 589 old clan chat now new clan chat
		sendSettings();
		sendTab(103, 590); // emote, 590 in sendFullScreenInterfaces
		sendTab(104, 187); // music
		sendTab(105, 34); // notes
		sendTab(108, 182); // logout
	}

	public void sendEquipment() {
		sendTab(resizableScreen ? 95 : 209, 387);
	}

	public void sendMsPortal() {
		sendTab(resizableScreen ? 93 : 207, 506);
		player.getPackets().sendIComponentText(506, 0, "Holy Shit");
		player.getPackets().sendIComponentText(506, 4, "Commands");
		player.getPackets().sendIComponentText(506, 6, "Teleports");
		player.getPackets().sendIComponentText(506, 8, "Updates");
		player.getPackets().sendIComponentText(506, 10, "Vote");
		player.getPackets().sendIComponentText(506, 12, "Infomation");
		player.getPackets().sendIComponentText(506, 14, "Donate<img=10>   ");
		if (player.getRights() == 2) {
			player.getPackets()
					.sendIComponentText(506, 2,
							"        In-Game Ranks       Rank: <img=1><shad=FFFF00>Admin</shad>");
		} else if (player.getRights() == 1) {
			player.getPackets()
					.sendIComponentText(506, 2,
							"        In-Game Ranks       Rank: <img=0><shad=D8D8D8>Mod</shad>");
		} else if (player.getRights() == 4) {
			player.getPackets()
					.sendIComponentText(506, 2,
							"        In-Game Ranks       Rank: <shad=D8D8D8>Donator</shad>");
		} else if (player.getRights() == 5) {
			player.getPackets()
					.sendIComponentText(506, 2,
							"        In-Game Ranks       Rank: <shad=D8D8D8>Donator</shad>");
		} else if (player.getRights() == 6) {
			player.getPackets()
					.sendIComponentText(506, 2,
							"        In-Game Ranks       Rank: <shad=D8D8D8>Donator</shad>");
		} else if (player.getRights() == 3) {
			player.getPackets()
					.sendIComponentText(506, 2,
							"       In-Game Ranks     Rank: <img=1><col=ff0000>Hidden Admin</col>");
		} else {
			player.getPackets().sendIComponentText(506, 2,
					"        In-Game Ranks       Rank: <col=ffffff>Player");
		}
	}

	public void closeEquipment() {
		player.getPackets().closeInterface(resizableScreen ? 95 : 209);
	}

	public void sendInventory() {
		sendTab(resizableScreen ? 94 : 208, Inventory.INVENTORY_INTERFACE);
	}

	public void closeInventory() {
		player.getPackets().closeInterface(resizableScreen ? 94 : 208);
	}

	public void sendSkills() {
		sendTab(resizableScreen ? 30 : 151, 320);
	}

	public void sendSettings() {
		sendSettings(261);
	}

	public void sendSettings(int interfaceId) {
		sendTab(resizableScreen ? 102 : 216, interfaceId);
	}

	public void sendPrayerBook() {
		sendTab(resizableScreen ? 96 : 210, 271);
	}

	public void sendMagicBook() {
		sendTab(resizableScreen ? 97 : 211, player.getCombatDefinitions()
				.getSpellBook());
	}

	public void sendFixedInterfaces() {
		player.getPackets().sendWindowsPane(548, 0);
		sendTab(15, 745);
		sendTab(68, 751); // Chat options
		sendTab(192, 752); // Chatbox
		sendTab(17, 754);
		sendTab(183, 748); // HP bar
		sendTab(185, 749); // Prayer bar
		sendTab(186, 750); // Energy bank
		sendTab(188, 747);
		player.getPackets().sendInterface(true, 752, 9, 137); // Playername on
																// chat
		sendTab(204, 884); // Attack tab
		sendTab(206, 320); // Skill tab
		sendMsPortal();// Quest tab 190
		sendTab(205, 1056);// Task tab
		sendInventory(); // Inventory tab
		sendEquipment();
		sendPrayerBook();
		sendMagicBook();
		sendTab(213, 550); // Friend tab
		sendTab(214, 1109); // friendchat
		sendTab(215, 1110); // clan chat
		sendSettings();
		sendTab(217, 590); // Emote tab
		sendTab(218, 187); // Music tab
		sendTab(219, 34); // Notes tab.
		sendTab(222, 182); // Logout tab
	}

	public boolean addInterface(int windowId, int tabId, int childId) {
		if (openedinterfaces.containsKey(tabId))
			player.getPackets().closeInterface(tabId);
		openedinterfaces.put(tabId, new int[] { childId, windowId });
		return openedinterfaces.get(tabId)[0] == childId;
	}

	public boolean containsInterface(int tabId, int childId) {
		if (childId == windowsPane)
			return true;
		if (!openedinterfaces.containsKey(tabId))
			return false;
		return openedinterfaces.get(tabId)[0] == childId;
	}

	public int getTabWindow(int tabId) {
		if (!openedinterfaces.containsKey(tabId))
			return FIXED_WINDOW_ID;
		return openedinterfaces.get(tabId)[1];
	}

	public boolean containsInterface(int childId) {
		if (childId == windowsPane)
			return true;
		for (int[] value : openedinterfaces.values())
			if (value[0] == childId)
				return true;
		return false;
	}

	public boolean containsTab(int tabId) {
		return openedinterfaces.containsKey(tabId);
	}

	public void removeAll() {
		openedinterfaces.clear();
	}

	public boolean containsScreenInter() {
		return containsTab(resizableScreen ? RESIZABLE_SCREEN_TAB_ID
				: FIXED_SCREEN_TAB_ID);
	}

	public void closeScreenInterface() {
		player.getPackets()
				.closeInterface(
						resizableScreen ? RESIZABLE_SCREEN_TAB_ID
								: FIXED_SCREEN_TAB_ID);
	}

	public boolean containsInventoryInter() {
		return containsTab(resizableScreen ? RESIZABLE_INV_TAB_ID
				: FIXED_INV_TAB_ID);
	}

	public void closeInventoryInterface() {
		player.getPackets().closeInterface(
				resizableScreen ? RESIZABLE_INV_TAB_ID : FIXED_INV_TAB_ID);
	}

	public boolean containsChatBoxInter() {
		return containsTab(CHAT_BOX_TAB);
	}

	public boolean removeTab(int tabId) {
		return openedinterfaces.remove(tabId) != null;
	}

	public boolean removeInterface(int tabId, int childId) {
		if (!openedinterfaces.containsKey(tabId))
			return false;
		if (openedinterfaces.get(tabId)[0] != childId)
			return false;
		return openedinterfaces.remove(tabId) != null;
	}

	public void sendScreenInterface(int backgroundInterface, int interfaceId) {
		player.getInterfaceManager().closeScreenInterface();

		if (hasRezizableScreen()) {
			player.getPackets().sendInterface(false, RESIZABLE_WINDOW_ID, 9,
					backgroundInterface);
			player.getPackets().sendInterface(false, RESIZABLE_WINDOW_ID, 11,
					interfaceId);
		} else
			player.getPackets().sendWindowsPane(interfaceId, 0);

		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				if (hasRezizableScreen()) {
					player.getPackets().closeInterface(9);
					player.getPackets().closeInterface(11);
				} else
					player.getPackets().sendWindowsPane(FIXED_WINDOW_ID, 0);
			}
		});
	}

	public boolean hasRezizableScreen() {
		return resizableScreen;
	}

	public void setWindowsPane(int windowsPane) {
		this.windowsPane = windowsPane;
	}

	public int getWindowsPane() {
		return windowsPane;
	}

	/*
	 * returns lastGameTab
	 */
	public int openGameTab(int tabId) {
		player.getPackets().sendGlobalConfig(168, tabId);
		int lastTab = 4; // tabId
		// tab = tabId;
		return lastTab;
	}

}