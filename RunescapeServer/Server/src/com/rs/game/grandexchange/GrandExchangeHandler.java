package com.rs.game.grandexchange;

import com.rs.cache.loaders.ObjectDefinitions;

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;

public class GrandExchangeHandler {
	
	//NPC Handling only with Exchange Clerk
	
	//Talk to 
	public static boolean HandleNPCFirstClick(Player player, NPC npc) {
		
		return false;
	}
	
	//Exchange
	public static boolean HandleNPCSecondClick(Player player, NPC npc) {
		player.setGesession(new GESession(player));
		player.getGesession().SendMainGEWindow();
		return false;
	}
	
	//History -- Or Collect
	public static boolean HandleNPCThirdClick(Player player, NPC npc) {
		
		return false;
	}
	
	//Sets
	public static boolean HandleNPCFourthClick(Player player, NPC npc) {
		
		return false;
	}
	
	public static boolean HandleObjectThirdClick(Player player, ObjectDefinitions object) {
		if (!object.containsOption("Collect")) {
			return false;
		} else {
			
		}
		
		return false;
	}

	public static boolean HandleButtons(Player player, int interfaceID,
			int childID, int slotInInventory, int itemID, int packetID) {
		
		switch (interfaceID) {
		case 105: // Main GE Menu
			if (player.getGesession() == null) {
				break;
			}
			
			switch (childID) {
			case 19: //View Offer - BOX 1
				break;
			case 35: //View Offer - BOX 2
				break;
			case 51: //View Offer - BOX 3
				break;
			case 70: //View Offer - BOX 4
				break;
			case 89: //View Offer - BOX 5
				break;
			case 108: //View Offer - BOX 6
				break;
			case 31: //Box 1 -- BUY
				player.getGesession().InitNewBuyOffer(0);
				break;
			case 32: //Box 1 -- SELL
				player.getGesession().InitNewSellOffer(0);
				break;
			case 47: //Box 2 -- BUY
				player.getGesession().InitNewBuyOffer(1);
				break;
			case 48: //Box 2 -- SELL
				player.getGesession().InitNewSellOffer(1);
				break;
			case 63: //Box 3 -- BUY
				player.getGesession().InitNewBuyOffer(2);
				break;
			case 64: //Box 3 -- SELL
				player.getGesession().InitNewSellOffer(2);
				break;
			case 82: //Box 4 -- BUY
				player.getGesession().InitNewBuyOffer(3);
				break;
			case 83: //Box 4 -- SELL
				player.getGesession().InitNewSellOffer(3);
				break;
			case 101: //Box 5 -- BUY
				player.getGesession().InitNewBuyOffer(4);
				break;
			case 102: //Box 5 -- SELL
				player.getGesession().InitNewSellOffer(4);
				break;
			case 120: //Box 6 -- BUY
				player.getGesession().InitNewBuyOffer(5);
				break;
			case 121: //Box 6 -- SELL
				player.getGesession().InitNewSellOffer(5);
				break;
			}
			break;
		case 109: // Main Collection Box
			
			break;
		}
		
		return false;
	}


}
