package com.rs.game.player.dialogues;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.WorldTile;
import com.rs.game.minigames.CastleWars;
import com.rs.game.Animation;
import com.rs.game.player.content.Magic;

public class MrEx extends Dialogue {

	private int npcId;

	
	
	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendEntityDialogue(SEND_3_TEXT_CHAT,
				new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
						"Hello, I am Mr. Ex, RsCali's newcomers guild.",
						"rsCalifornia was created by Victoria & Skeptic!",
						"Here are some helpfull tips;"}, IS_NPC, npcId, 9827);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendEntityDialogue(SEND_1_TEXT_CHAT,
			new String[] { player.getDisplayName(), "I'm hoping this is worth the time Mr Ex." },
			IS_PLAYER, player.getIndex(), 9827);
			stage = 1;
		} else if (stage == 1) {
			sendEntityDialogue(SEND_4_TEXT_CHAT,
					new String[] { NPCDefinitions.getNPCDefinitions(npcId).name, "Haha i'm sure it's not.",
								"You can view our range of teleports and commands,",
								"by clicking the quest tab followed by your choice.",
								"Only donate to Victoria & Skeptic!"}, IS_NPC, npcId, 9827);
			stage = 2;
		} else if (stage == 2) {
			sendEntityDialogue(SEND_4_TEXT_CHAT,
					new String[] { NPCDefinitions.getNPCDefinitions(npcId).name, "Rules of RsCalifornia!",
								"Advertising other servers is strictly banned!",
								"Bug/glitch abuse will result with a banned.",
								"Any offensive language will result in a mute. "}, IS_NPC, npcId, 9827);
			stage = 3;
		} else if (stage == 3) {
			sendEntityDialogue(SEND_4_TEXT_CHAT,
					new String[] { NPCDefinitions.getNPCDefinitions(npcId).name, "NO Duping what so ever!",
								"Begging for staff or pestering staff is forbiden.",
								"This could all result in your account getting ban.",
								""}, IS_NPC, npcId, 9827);
			stage = 4;
		} else if (stage == 4) {
			sendEntityDialogue(SEND_4_TEXT_CHAT,
					new String[] { NPCDefinitions.getNPCDefinitions(npcId).name, "Please View the rest of the rules,",
								"and other infomation indebth on our forums.",
								"",
								""}, IS_NPC, npcId, 9827);
			stage = 5;
		} else if (stage == 5) { //
			sendEntityDialogue(SEND_4_TEXT_CHAT,
					new String[] { NPCDefinitions.getNPCDefinitions(npcId).name, "",
								"We hope you enjoy your stay on RsCalifornia!",
								".",
								""}, IS_NPC, npcId, 9827);
			stage = 6;

		} else if (stage == 6) {
			//controler.updateProgress();
			end();
			} else
			end();
	}

	@Override
	public void finish() {

	}
}