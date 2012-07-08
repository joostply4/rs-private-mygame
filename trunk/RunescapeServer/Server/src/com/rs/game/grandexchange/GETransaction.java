package com.rs.game.grandexchange;

import com.rs.game.player.Player;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class GETransaction {
	@XStreamAlias("Type")
	private GETransactionType type;

	@XStreamAlias("Slot")
	private int geSlot;
	
	@XStreamAlias("Progress")
	private int progress;
	
	@XStreamAlias("ItemID")
	private int itemID;
	
	@XStreamAlias("PricePerItem")
	private int price;
	
	@XStreamAlias("TotalItems")
	private int totalAmountToTrade;
	
	@XStreamAlias("TotalItemsTraded")
	private int totalAmountTraded;
	
	@XStreamAlias("PlayerWhoStarted")
	private String ownerName;
	
	@XStreamAlias("TransactionID")
	private long transactionID;
	
	@XStreamOmitField
	private Player parent;
	
	public GETransaction() {
		
	}
	
	public GETransaction(GETransactionType type, int geSlot, int progress,
			int itemID, int price, int totalAmountToTrade, int totalAmountTraded) {
		super();
		this.type = type;
		this.geSlot = geSlot;
		this.progress = progress;
		this.itemID = itemID;
		this.price = price;
		this.totalAmountToTrade = totalAmountToTrade;
		this.totalAmountTraded = totalAmountTraded;
	}
	
	public GETransactionType getType() {
		return type;
	}
	
	public void setType(GETransactionType type) {
		this.type = type;
	}
	
	public int getGeSlot() {
		return geSlot;
	}
	
	public void setGeSlot(int geSlot) {
		this.geSlot = geSlot;
	}
	
	public int getProgress() {
		return progress;
	}
	
	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public int getTotalAmountToTrade() {
		return totalAmountToTrade;
	}
	
	public void setTotalAmountToTrade(int totalAmountToTrade) {
		this.totalAmountToTrade = totalAmountToTrade;
	}
	
	public int getTotalAmountTraded() {
		return totalAmountTraded;
	}
	
	public void setTotalAmountTraded(int totalAmountTraded) {
		this.totalAmountTraded = totalAmountTraded;
	}

	public Player getParent() {
		return parent;
	}

	public void setParent(Player parent) {
		this.parent = parent;
		this.ownerName = parent.getUsername();
	}

	public long getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(long transactionID) {
		this.transactionID = transactionID;
	}

	public String getOwnerName() {
		return ownerName;
	}
	
}
