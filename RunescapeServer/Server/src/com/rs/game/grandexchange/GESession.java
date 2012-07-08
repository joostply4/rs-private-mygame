package com.rs.game.grandexchange;

import com.rs.game.player.*;

public class GESession {
	public int GE_MAIN_INTERFACE = 105;
	
	private Player player;
	private int currentSlot;
	private GETransaction currentTransaction;
	
	public GESession(Player player) {
		this.player = player;
		ResetGEInterface();
	}
	
	public void SendMainGEWindow() {
		player.getInterfaceManager().sendInterface(GE_MAIN_INTERFACE);
	}
	
	//send collection window
	//send history window

	public void ResetGEInterface() {
		player.getPackets().sendConfig(1109, -1);
		player.getPackets().sendConfig(1110, 0);
		player.getPackets().sendConfig(1111, 1);
		player.getPackets().sendConfig(1112, -1);
		player.getPackets().sendConfig(1113, -1);
	}
	
	public void InitNewBuyOffer(int slot) {
		ResetGEInterface();
		currentSlot = slot;
		currentTransaction = new GETransaction();
		currentTransaction.setType(GETransactionType.BUYING);
		currentTransaction.setGeSlot(currentSlot);
		currentTransaction.setParent(player);
		
		player.getPackets().sendConfig(1112, slot);
		player.getPackets().sendConfig(1113, 0);
		
		//force inventory options to just say "Offer"
		//automatically open the "GE Search"
	}
	
	public void InitNewSellOffer(int slot) {
		ResetGEInterface();
		
		currentSlot = slot;
		currentTransaction = new GETransaction();
		currentTransaction.setType(GETransactionType.SELLING);
		currentTransaction.setGeSlot(currentSlot);
		currentTransaction.setParent(player);
		
		player.getPackets().sendConfig(1112, slot);
		player.getPackets().sendConfig(1113, 1);
		
		player.getPackets().closeInterface(220);
	}
}
