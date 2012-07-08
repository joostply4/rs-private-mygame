package com.rs.game.grandexchange;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.rs.game.player.Player;
import com.thoughtworks.xstream.XStream;

public class GrandExchange {
	private static int CurrentTransactionID;
	
	private static XStream xstream;
	
	static {
		xstream = new XStream();
		xstream.autodetectAnnotations(true);
	}
	
	public static void init() {
		if (xstream == null) {
			xstream = new XStream();
			xstream.autodetectAnnotations(true);
		}
				
		//Determine current transaction id
		LoadTransactionID();
		
		//Load all GE Offers
		//Load all GE Reminders -- mainly for OnLogin()
	}
	
	public static void OnLogin(Player player) {
		//Determine if we need to send the message
		//"One or more of your GE Trades have been updated..."
	}
	
	public static void SaveGE() {
		//save transaction id
		SaveTransactionID();
		
		//save ge offers
		//save reminders 
	}
	
	private static void LoadTransactionID() {
        try {
            FileInputStream fis = new FileInputStream("data/grandexchange/TransactionID.xml");
            CurrentTransactionID = (int) xstream.fromXML(fis);
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
	}
	
	private static void SaveTransactionID() {
        try {
            FileOutputStream fs = new FileOutputStream("data/grandexchange/TransactionID.xml");
            xstream.toXML(CurrentTransactionID, fs);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
	}

}
