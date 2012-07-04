package com.rs.game.player.dialogues;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.content.slayer.Slayer;
import com.rs.game.player.content.slayer.SlayerMaster;

public class Kuradal extends Dialogue {

	/**
	 * Starts the dialogue
	 */

	public Kuradal() {

	}

	@Override
	public void start() {
		npcId = ((Integer) parameters[0]).intValue();
		sendEntityDialogue((short) 241,
				new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
						"Hello there! Just right-click on me to interact." },
				(byte) 1, npcId, 9827);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}

	private int npcId;
}