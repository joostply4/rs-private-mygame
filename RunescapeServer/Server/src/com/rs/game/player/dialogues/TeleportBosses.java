package com.rs.game.player.dialogues;

import com.rs.game.WorldTile;
import com.rs.game.player.content.Magic;

/*
 *
 *@author Ashton
 *
 */
public class TeleportBosses extends Dialogue {

	@Override
	public void start() {
		stage = 0;
		
		sendDialogue(SEND_5_OPTIONS, "Boss Teleports", "Corporeal Beast",
				"Tormented Demons", "God Wars Dungeon", "Barrows",
				"More Options");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 0) {
			if (componentId == 1) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2966,
						4383, 0));
			} else if (componentId == 2) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2562,
						5739, 0));
			} else if (componentId == 3) {
				player.getPackets().sendGameMessage(
						"God Wars Dungeon is coming soon.");
				end();
			} else if (componentId == 4) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3565,
						3289, 0));
			} else if (componentId == 5) {
				stage = 1;
				sendDialogue(SEND_5_OPTIONS, "Boss Teleports", "Chaos Elemental",
						"King Black Dragon", "Coming Soon",
						"Coming Soon", "More Options");
			}
		} else if(stage == 1) {
			if (componentId == 1) {
				player.getPackets().sendGameMessage(
						"Chaos Elemental is coming soon.");
				end();
			} else if (componentId == 2) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2258,
						4709, 0));
			} else if (componentId == 3) {
				player.getPackets().sendGameMessage(
						"New Bosses are coming soon.");
				end();
			} else if (componentId == 4) {
				player.getPackets().sendGameMessage(
						"New Bosses are  coming soon.");
				end();
			} else if (componentId == 5) {
				stage = 0;
				sendDialogue(SEND_5_OPTIONS, "Boss Teleports", "Corporeal Beast",
						"Tormented Demons", "God Wars Dungeon", "Barrows",
						"More Options");
			}
		}

	}

	@Override
	public void finish() {

	}
}
