package com.rs.game.player.dialogues;

import com.rs.game.WorldTile;
import com.rs.game.player.content.Magic;

/*
 *
 *@author Will
 *
 */
public class TeleportTraining extends Dialogue {
	@Override
	public void start() {
		stage = 0;
		sendDialogue(SEND_5_OPTIONS, "Training Teleports", "Rock Crabs",
				"Hill Giants", "Taverly Dungeon", "Slayer Tower",
				"More Options");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 0) {
			if (componentId == 1) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2707,
						3735, 0));
			} else if (componentId == 2) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3117,
						9852, 0));
			} else if (componentId == 3) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2884,
						9798, 0));
			} else if (componentId == 4) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3429,
						3538, 0));
			} else if (componentId == 5) {
				stage = 1;
				sendDialogue(SEND_5_OPTIONS, "Training Teleports",
						"Brimhaven Dungeon", "Under Karamja",
						"Green Drags(lvl 13 wildy)",
						"Green Drags(lvl 20 wildy)", "More Options");
			}
		} else if (stage == 1) {
			if (componentId == 1) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2710,
						9466, 0));
			} else if (componentId == 2) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2857,
						9569, 0));
			} else if (componentId == 3) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2979,
						3602, 0));
			} else if (componentId == 4) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3350,
						3675, 0));
			} else if (componentId == 5) {
				stage = 0;
				sendDialogue(SEND_5_OPTIONS, "Training Teleports",
						"Rock Crabs", "Hill Giants", "Taverly Dungeon",
						"Slayer Tower", "More Options");
			}
		}
	}

	@Override
	public void finish() {

	}
}
