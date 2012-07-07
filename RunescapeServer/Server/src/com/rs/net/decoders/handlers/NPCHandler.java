package com.rs.net.decoders.handlers;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.slayer.Strykewyrm;
import com.rs.game.player.CoordsEvent;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Fishing;
import com.rs.game.player.actions.PickPocketAction;
import com.rs.game.player.actions.PickPocketableNPC;
import com.rs.game.player.actions.Fishing.FishingSpots;
import com.rs.game.player.content.PlayerLook;
import com.rs.game.player.content.slayer.Slayer;
import com.rs.game.player.dialogues.FremennikShipmaster;
import com.rs.io.InputStream;
import com.rs.utils.ShopsHandler;

public class NPCHandler {

	public static void handleOption1(final Player player, InputStream stream) {
		@SuppressWarnings("unused")
		boolean unknown = stream.readByte128() == 1;
		int npcIndex = stream.readUnsignedShort128();
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead()
				|| npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		player.stopAll(false);
		if (npc.getDefinitions().name.contains("Banker")
				|| npc.getDefinitions().name.contains("banker")) {
			player.faceEntity(npc);
			if (!player.withinDistance(npc, 2))
				return;
			npc.faceEntity(player);
			player.getDialogueManager().startDialogue("Banker", npc.getId());
			return;
		}
		player.setCoordsEvent(new CoordsEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				player.faceEntity(npc);
				FishingSpots spot = FishingSpots.forId(npc.getId() | 1 << 24);
				if (spot != null) {
					player.getActionManager().setSkill(new Fishing(spot, npc));
					return; // its a spot, they wont face us
				}
				npc.faceEntity(player);
				if (!player.getControlerManager().processNPCClick1(npc))
					return;
				if (npc.getId() == 1569)
					player.getDialogueManager().startDialogue("Veliaf",
							npc.getId());
				if (npc.getId() == 3709)
					player.getDialogueManager().startDialogue("MrEx",
							npc.getId());
				if (npc.getId() == 14358)
					player.getDialogueManager().startDialogue("Lucien",
							npc.getId());
				else if (npc.getId() == 949)
					player.getDialogueManager().startDialogue("QuestGuide",
							npc.getId(), null);
				else if (npc.getId() == 9462)
					Strykewyrm.handleStomping(player, npc);
				else if (npc.getId() == 9707)
					player.getDialogueManager().startDialogue(
							"FremennikShipmaster", npc.getId(), true);
				else if (npc.getId() == 9708)
					player.getDialogueManager().startDialogue(
							"FremennikShipmaster", npc.getId(), false);
				if (npc.getId() == 9085)
					player.getDialogueManager().startDialogue("Kuradal",
							npc.getId());
				else if (npc.getId() == 6537)
					ShopsHandler.openShop(player, 13);
				else if (npc.getId() == 2676)
					player.getDialogueManager().startDialogue("MakeOverMage",
							npc.getId(), 0);
				else {
					player.getPackets().sendGameMessage(
							"Nothing interesting happens.");
					if (Settings.DEBUG)
						System.out.println("cliked 1 at npc id : "
								+ npc.getId() + ", " + npc.getX() + ", "
								+ npc.getY() + ", " + npc.getPlane());
				}
			}
		}, npc.getSize()));
	}

	public static void handleOption2(final Player player, InputStream stream) {
		@SuppressWarnings("unused")
		boolean unknown = stream.readByte128() == 1;
		int npcIndex = stream.readUnsignedShort128();
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead()
				|| npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		player.stopAll(false);
		if (npc.getDefinitions().name.contains("Banker")
				|| npc.getDefinitions().name.contains("banker")) {
			player.faceEntity(npc);
			if (!player.withinDistance(npc, 2))
				return;
			npc.faceEntity(player);
			player.getBank().openBank();
			return;
		}
		player.setCoordsEvent(new CoordsEvent(npc, new Runnable() {
			@Override
			public void run() {
				npc.resetWalkSteps();
				player.faceEntity(npc);
				FishingSpots spot = FishingSpots.forId(npc.getId() | (2 << 24));
				if (spot != null) {
					player.getActionManager().setSkill(new Fishing(spot, npc));
					return;
				}
				PickPocketableNPC pocket = PickPocketableNPC.get(npc.getId());
				if (pocket != null) {
					player.getActionManager().setSkill(
							new PickPocketAction(npc, pocket));
					return;
				}
				if (npc instanceof Familiar) {
					if (npc.getDefinitions().hasOption("store")) {
						if (player.getFamiliar() != npc) {
							player.getPackets().sendGameMessage(
									"That isn't your familiar.");
							return;
						}
						player.getFamiliar().store();
					} else if (npc.getDefinitions().hasOption("cure")) {
						if (player.getFamiliar() != npc) {
							player.getPackets().sendGameMessage(
									"That isn't your familiar.");
							return;
						}
						if (!player.getPoison().isPoisoned()) {
							player.getPackets().sendGameMessage(
									"Your arent poisoned or diseased.");
							return;
						} else {
							player.getFamiliar().drainSpecial(2);
							player.addPoisonImmune(120);
						}
					}
					return;
				}
				if (npc.getDefinitions().hasPickupOption()
						|| npc.getDefinitions().hasTakeOption()) {
					if (!player.withinDistance(npc, 2)) {
						return;
					}
					player.faceEntity(npc);
					if (player.getPetFollow() != player.getIndex()) {
						player.sendMessage("This isn't your pet!");
						return;
					}
					if (player.getPetId() == 0) {
						return;
					}
					player.getPet().dissmissPet(false);
					return;
				}
				npc.faceEntity(player);
				if (!player.getControlerManager().processNPCClick2(npc))
					return;
				if (npc.getId() == 9707)
					FremennikShipmaster.sail(player, true);
				else if (npc.getId() == 9708)
					FremennikShipmaster.sail(player, false);
				else if (npc.getId() == 13455)
					player.getBank().openBank();
//				else if (npc.getId() == 528 || npc.getId() == 529)
//					ShopsHandler.openShop(player, 1);
//				else if (npc.getId() == 537)
//					ShopsHandler.openShop(player, 25);
//				else if (npc.getId() == 445)
//					ShopsHandler.openShop(player, 11);
				else if (npc.getId() == 3381)
					ShopsHandler.openShop(player, 12);
//				else if (npc.getId() == 521)
//					ShopsHandler.openShop(player, 4);
				else if (npc.getId() == 538)
					ShopsHandler.openShop(player, 6);
//				else if (npc.getId() == 522 || npc.getId() == 523)
//					ShopsHandler.openShop(player, 8);
//				else if (npc.getId() == 546)
//					ShopsHandler.openShop(player, 10);
				else if (npc.getId() == 11475)
					ShopsHandler.openShop(player, 9);
//				else if (npc.getId() == 551)
//					ShopsHandler.openShop(player, 13);
//				else if (npc.getId() == 548)
//					ShopsHandler.openShop(player, 18);
//				else if (npc.getId() == 2233 || npc.getId() == 3671)
//					ShopsHandler.openShop(player, 20);
				else if (npc.getId() == 2676)
					PlayerLook.openMageMakeOver(player);
				else if (npc.getId() == 970)
					ShopsHandler.openShop(player, 7);
				else if (npc.getId() == 587)
					ShopsHandler.openShop(player, 8);
				else if (npc.getId() == 519)
					ShopsHandler.openShop(player, 10);
				else if (npc.getId() == 549)
					ShopsHandler.openShop(player, 11);
				else if (npc.getId() == 520)
					ShopsHandler.openShop(player, 1);
				else if (npc.getId() == 1699)
					ShopsHandler.openShop(player, 2);
				else if (npc.getId() == 534)
					ShopsHandler.openShop(player, 3);
				else if (npc.getId() == 550)
					ShopsHandler.openShop(player, 4);
				else if (npc.getId() == 554)
					ShopsHandler.openShop(player, 5);
				else if (npc.getId() == 9085) {
					if (player.getSlayerTask().getTaskMonstersLeft() <= 0) {
						Slayer.assignTask(player);
					} else {
						Slayer.reassignTask(player);
					}
				} else {
					player.getPackets().sendGameMessage(
							"Nothing interesting happens.");
					if (Settings.DEBUG)
						System.out.println("cliked 2 at npc id : "
								+ npc.getId() + ", " + npc.getX() + ", "
								+ npc.getY() + ", " + npc.getPlane());
				}
			}
		}, npc.getSize()));
	}

	public static void handleOption3(Player player, InputStream stream) {
		@SuppressWarnings("unused")
		boolean unknown = stream.readByte128() == 1;
		int npcIndex = stream.readUnsignedShort128();
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead()
				|| npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		player.stopAll(false);
		
		if (npc.getId() == 9085) { //Open Slayer Shop
			
		} else if (npc.getId() == 5913) {
			ShopsHandler.openShop(player, 9);
		}

		if (Settings.DEBUG)
			System.out.println("cliked 3 at npc id : " + npc.getId() + ", "
					+ npc.getX() + ", " + npc.getY() + ", " + npc.getPlane());
	}

	public static void handleOption4(Player player, InputStream stream) {
		@SuppressWarnings("unused")
		boolean unknown = stream.readByte128() == 1;
		int npcIndex = stream.readUnsignedShort128();
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead()
				|| npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		player.stopAll(false);
		
		if (npc.getId() == 9085) { //Open Slayer Rewards
			//interfaces 161, 163, 164
			//assignment, learn, buy
			Slayer.initInterface(player, 161);
		} else if (npc.getId() == 14860) {
			ShopsHandler.openShop(player, 6);
		}

		if (Settings.DEBUG)
			System.out.println("cliked 4 at npc id : " + npc.getId() + ", "
					+ npc.getX() + ", " + npc.getY() + ", " + npc.getPlane());
	}
}