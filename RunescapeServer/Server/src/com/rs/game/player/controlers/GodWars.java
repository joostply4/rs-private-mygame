package com.rs.game.player.controlers;

import com.rs.game.WorldObject;

public class GodWars extends Controler {

	@Override
	public void start() {
		// place, count1,count2,count3,count4,count5
		setArguments(new Object[] { 0, 0, 0, 0, 0, 0 });
		sendInterfaces();
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public boolean login() {
		sendInterfaces();
		return false; // so doesnt remove script
	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 57225) {
			player.getDialogueManager().startDialogue("NexEntrance");
			return false;
		}
		return true;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendTab(
				player.getInterfaceManager().hasRezizableScreen() ? 9 : 8,
				getInterface());
		modifyInterface();
	}
	
	public void modifyInterface() {
		player.getPackets().sendIComponentText(601, 2, "Kill Count");
		//3, 4, 5, 6 -- boss names
		player.getPackets().sendIComponentText(601, 3, "Armadyl");
		player.getPackets().sendIComponentText(601, 4, "Bandos");
		player.getPackets().sendIComponentText(601, 5, "Saradomin");
		player.getPackets().sendIComponentText(601, 6, "Zamorak");
		//8, 9 , 10, 11 -- kill counts
		player.getPackets().sendIComponentText(601, 8, "" + player.getArmKillCount());
		player.getPackets().sendIComponentText(601, 9, "" + player.getBanKillCount());
		player.getPackets().sendIComponentText(601, 10, "" + player.getSarKillCount());
		player.getPackets().sendIComponentText(601, 11, "" + player.getZamKillCount());
	}

	private int getInterface() {
		switch ((Integer) getArguments()[0]) {
		default:
			return 601;
		}
	}

	@Override
	public boolean sendDeath() {
		remove();
		removeControler();
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		remove();
		removeControler();
	}

	@Override
	public void forceClose() {
		remove();
	}

	public void remove() {
		player.getPackets().closeInterface(
				player.getInterfaceManager().hasRezizableScreen() ? 9 : 8);
	}

}
