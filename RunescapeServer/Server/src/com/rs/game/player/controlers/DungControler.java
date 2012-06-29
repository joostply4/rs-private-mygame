package com.rs.game.player.controlers;

import com.rs.game.Animation;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.content.Dungeoneering;
import com.rs.game.player.content.Dungeoneering.Dungeon;
import com.rs.game.player.content.Magic;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class DungControler extends Controler {

	private Dungeon dungeon;

	@Override
	public void start() {
		dungeon = (Dungeon) getArguments()[0];
		getArguments()[0] = 0;
		showDeaths();
	}

	public void showDeaths() {
		player.getInterfaceManager()
				.sendTab(
						player.getInterfaceManager().hasRezizableScreen() ? 7
								: 17, 945);
		refreshDeathCount();
	}

	public void refreshDeathCount() {
		player.getPackets().sendIComponentText(945, 0,
				"Deaths " + getArguments()[0]);
	}

	@Override
	public void sendInterfaces() {
		showDeaths();
	}

	@Override
	public boolean sendDeath() {
		player.addStopDelay(7);
		player.stopAll();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage(
							"Oh dear, you have died.");
				} else if (loop == 3) {
					player.reset();
					player.setNextWorldTile(dungeon.getHomeTeleTile());
					player.setNextAnimation(new Animation(-1));
					stop();
					getArguments()[0] = ((Integer) getArguments()[0]) + 1;
					refreshDeathCount();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean login() {
		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		if (dungeon == null
				|| !player.getCombatDefinitions().isDungeonneringSpellBook()
				|| !dungeon.hasStarted())
			return false;
		return true;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		return false;
	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (dungeon == null || !dungeon.hasStarted() || dungeon.isDestroyed())
			return false;
		if (isDoor(object.getId())) {
			openDoor(object);
			return false;
		} else if (isExit(object.getId())) {
			player.getDialogueManager().startDialogue("DungeonExit", this);
			return false;
		}
		return true;
	}

	public static boolean isExit(int objectId) {
		for (int id : Dungeoneering.DUNGEON_EXITS)
			if (id == objectId)
				return true;
		return false;
	}

	public static boolean isDoor(int objectId) {
		for (int id : Dungeoneering.DUNGEON_DOORS)
			if (id == objectId)
				return true;
		return false;
	}

	public void openDoor(WorldObject object) {
		int[] pos = dungeon.getCurrentRoomPos(player);
		if (pos[0] == 0 && pos[1] == 0) {
			if (!dungeon.checkRoom(pos[0], pos[1] + 1)) {
				dungeon.playMusic(player, pos[0], pos[1] + 1);
				player.setNextWorldTile(new WorldTile(player.getX(), player
						.getY() + 3, 0));
			}
		}
	}

	@Override
	public boolean processObjectClick2(final WorldObject object) {
		if (dungeon == null || !dungeon.hasStarted())
			return false;
		return true;
	}

	/*
	 * return process normaly
	 */
	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int packetId) {
		if (dungeon == null || !dungeon.hasStarted())
			return false;
		if (interfaceId == 950 && componentId == 24) {
			Magic.sendNormalTeleportSpell(player, 0, 0,
					dungeon.getHomeTeleTile());
			dungeon.playMusic(player, 0, 0);
			return false;
		}
		return true;
	}

	@Override
	public void forceClose() {
		leaveDungeon(false);
	}

	@Override
	public boolean logout() {
		leaveDungeon(true);
		return false;
	}

	public void leaveDungeon(boolean logout) {
		if (dungeon == null || !dungeon.hasStarted()) {
			if (logout)
				player.setLocation(new WorldTile(new WorldTile(3460, 3720, 1),
						2));
			else
				player.setNextWorldTile(new WorldTile(new WorldTile(3460, 3720,
						1), 2));
			return;
		}
		player.stopAll();
		dungeon.remove(player);
		player.getCombatDefinitions().removeDungeonneringBook();
		player.setMapSize(0);
		player.setForceMultiArea(false);
		if (logout)
			player.setLocation(new WorldTile(new WorldTile(3460, 3720, 1), 2));
		else
			player.setNextWorldTile(new WorldTile(new WorldTile(3460, 3720, 1),
					2));

		removeControler();
		player.getControlerManager().startControler("Kalaboss");
		player.getPackets().closeInterface(
				player.getInterfaceManager().hasRezizableScreen() ? 7 : 17);
	}

	public boolean validControler() {
		return dungeon != null && dungeon.hasStarted()
				&& !dungeon.isDestroyed()
				&& player.getControlerManager().getControler() == this;
	}

}
