package com.rs.game.player.dialogues;

import com.rs.game.WorldTile;
import com.rs.game.player.content.Magic;

/*
 *
 *@author Ashton
 *
 */
public class TeleportMinigame extends Dialogue {

	@Override
	public void start() {
		stage = 0;
		sendDialogue(SEND_5_OPTIONS, "Minigame Teleports", "Duel Arena",
				"Castle Wars", "Soul Wars", "Dominion Tower", "More Options");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 0) {
			if (componentId == 1) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3366,
						3266, 0));
			} else if (componentId == 2) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2443,
						3089, 0));
			} else if (componentId == 3) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1886,
						3178, 0));
			} else if (componentId == 4) {
				player.getControlerManager().startControler("GodWars");
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3366,
						3083, 0));
			} else if (componentId == 5) {
				stage = 1;
				sendDialogue(SEND_5_OPTIONS, "Minigame Teleports",
						"Dungeoneering", "Godwars Dungeon", "", "",
						"More Options");
			}
		} else if (stage == 1) {
			if (componentId == 1) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3447,
						3697, 0));
			} else if (componentId == 2) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2882,
						5311, 2));
			} else if (componentId == 3) {
			} else if (componentId == 4) {
			} else if (componentId == 5) {
				stage = 0;
				sendDialogue(SEND_5_OPTIONS, "Minigame Teleports",
						"Duel Arena", "Castle Wars", "Soul Wars",
						"Dominion Tower", "More Options");
			}
		}
	}

	@Override
	public void finish() {

	}
}
