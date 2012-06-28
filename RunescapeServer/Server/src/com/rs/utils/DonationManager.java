package com.rs.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.rs.game.player.Player;
import com.rs.utils.Misc;
import com.rs.game.World;

public class DonationManager {

	public static Connection con = null;
	public static Statement stmt;
	public static boolean connectionMade;

	public static void createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		String IP="WEBHOST IP";
        String DB="DATABASE_NAME";
        String User="DATABASE_USERNAME";
        String Pass="DATABASE_PASSWORD"; 
			con = DriverManager.getConnection("jdbc:mysql://"+IP+"/"+DB, User, Pass);
			stmt = con.createStatement();
		System.out.println("Connection to Donation database successful!"); //You can take these system prints out. Get annoying sometimes.
		} catch (Exception e) {
        System.out.println("Connection to Donation database failed");
        e.printStackTrace();
		}
	}
	
	public static void startProcess(final Player player) {  //Choose which payment options give which things.
		createConnection();
		if(checkDonation(player.getUsername())) {
			if(player.getInventory().getFreeSlots() < 10) {
//				player.getPackets().sendMessage("<col=00FFCC>Create some more space in your inventory Before doing this!");			
				return;
			}
			if(checkDonationItem(player.getUsername()) == 1) { //Productid 1 
				player.getPackets().sendGameMessage("<shad=cc0ff><img=1>You Have Received: Regular Donator!");
				player.getPackets().sendGameMessage("<col=00FFCC>The donatorzone can be found by doing ::donatorzone");
				if (!player.isDonator()) {
					player.setDonator(true);
					for(Player p : World.getPlayers()) {
						if (p ==null) {
							continue; 
						}
				player.getPackets().sendGameMessage("Enjoy Your Donation, Thanks for Donating!!");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1>" + player.getUsername() + "has donated tell him thanks for contributing!<img=1>");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1Donate by clicking donate in the quest tab!<img=1>");
					}
				}
				donationGiven(player.getUsername());		
			}
			if(checkDonationItem(player.getUsername()) == 2) { //Productid 2 
				player.getPackets().sendGameMessage("<shad=cc0ff><img=1>You Have Received: Extreme Donator!");
				player.getPackets().sendGameMessage("<col=00FFCC>The donatorzone can be found by doing ::donatorzone");
				if (!player.isDonator()) {
					player.setDonator(true);
				/*if (!player.issuperDonator()) {
					player.setsuperDonator(true);*/
					for(Player p : World.getPlayers()) {
						if (p ==null) {
							continue; 
						}
					player.getPackets().sendGameMessage("Enjoy Your Donation, Thanks for Donating!!");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1>" + player.getUsername() + "has donated tell him thanks for contributing!<img=1>");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1Donate by clicking donate in the quest tab!<img=1>");
					}
				//}
				}
				donationGiven(player.getUsername());		
			}
			if(checkDonationItem(player.getUsername()) == 3) { //Productid 3
				player.getPackets().sendGameMessage("<shad=cc0ff><img=1>You Have Received : Super Extreme Donator!");
				player.getPackets().sendGameMessage("<col=00FFCC>Donate by clicking donate in the quest tab!");
				if (!player.isDonator()) {
					player.setDonator(true);
				/*if (!player.issuperDonator()) {
					player.setsuperDonator(true);
				if (!player.isextremeDonator()) {
					player.setextremeDonator(true);*/
					for(Player p : World.getPlayers()) {
						if (p ==null) {
							continue; 
						}
					player.getPackets().sendGameMessage("Enjoy Your Donation, Thanks for Donating!!");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1>" + player.getUsername() + "has donated tell him thanks for contributing!<img=1>");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1Donate by clicking donate in the quest tab!<img=1>");
					}
				}
				//}
				//}
				donationGiven(player.getUsername());		
			}
			if(checkDonationItem(player.getUsername()) == 4) { //Productid 4 
				player.getPackets().sendGameMessage("<shad=cc0ff><img=1>You Have Recieved Your: PartyHat Set!");
				player.getInventory().addItem(1038, 1);
				player.getInventory().addItem(1040, 1);
				player.getInventory().addItem(1042, 1);
				player.getInventory().addItem(1044, 1);
				player.getInventory().addItem(1046, 1);
				player.getInventory().addItem(1048, 1);
					for(Player p : World.getPlayers()) {
						if (p ==null) {
							continue; 
						}
					player.getPackets().sendGameMessage("Enjoy Your Donation, Thanks for Donating!!");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1>" + player.getUsername() + "has donated tell him thanks for contributing!<img=1>");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1Donate by clicking donate in the quest tab!<img=1>");
					}
				donationGiven(player.getUsername());		
			}
			if(checkDonationItem(player.getUsername()) == 5) { //Productid 5
				player.getPackets().sendGameMessage("<shad=cc0ff><img=1>You Have Recieved Your : H'Ween Set!");
				player.getInventory().addItem(1053, 1);
				player.getInventory().addItem(1055, 1);
				player.getInventory().addItem(1057, 1);
					for(Player p : World.getPlayers()) {
						if (p ==null) {
							continue; 
						}
					player.getPackets().sendGameMessage("Enjoy Your Donation, Thanks for Donating!!");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1>" + player.getUsername() + "has donated tell him thanks for contributing!<img=1>");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1Donate by clicking donate in the quest tab!<img=1>");
					}
				donationGiven(player.getUsername());		
			}
			if(checkDonationItem(player.getUsername()) == 6) { //Productid 6
				player.getPackets().sendGameMessage("<shad=cc0ff><img=1>You Have Recieved Your : Santa Hat!");
				player.getPackets().sendGameMessage("<col=00FFCC>The donatorzone can be found by doing ::donatorzone");
				player.getInventory().addItem(1050, 1);
					for(Player p : World.getPlayers()) {
						if (p ==null) {
							continue; 
						}
					player.getPackets().sendGameMessage("Enjoy Your Donation, Thanks for Donating!!");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1>" + player.getUsername() + "has donated tell him thanks for contributing!<img=1>");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1Donate by clicking donate in the quest tab!<img=1>");
					}
				donationGiven(player.getUsername());		
			}
			if(checkDonationItem(player.getUsername()) == 7) { //Productid 7
				player.getPackets().sendGameMessage("<shad=cc0ff><img=1>You Have Recieved The ability to Use the summoning skill.");
				//player.getInventory().addItem(6199, 10);
					for(Player p : World.getPlayers()) {
						if (p ==null) {
							continue; 
						}
					player.getPackets().sendGameMessage("Enjoy Your Donation, Thanks for Donating!!");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1>" + player.getUsername() + "has donated tell him thanks for contributing!<img=1>");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1Donate by clicking donate in the quest tab!<img=1>");
					}
				donationGiven(player.getUsername());		
			}
			if(checkDonationItem(player.getUsername()) == 8) { //Productid 8  start here!
				player.getPackets().sendGameMessage("<shad=cc0ff><img=1>You Have Recieved Your: 100k Tokens!");
				player.getInventory().addItem(12852, 100000);
					for(Player p : World.getPlayers()) {
						if (p ==null) {
							continue; 
						}
					player.getPackets().sendGameMessage("Enjoy Your Donation, Thanks for Donating!!");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1>" + player.getUsername() + "has donated tell him thanks for contributing!<img=1>");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1Donate by clicking donate in the quest tab!<img=1>");
					}
				donationGiven(player.getUsername());		
			}
			if(checkDonationItem(player.getUsername()) == 9) { //Productid 9
				player.getPackets().sendGameMessage("<shad=cc0ff><img=1>You Have Recieved Your: 50k Tokens!!");
				player.getInventory().addItem(12852, 50000);
					for(Player p : World.getPlayers()) {
						if (p ==null) {
							continue; 
						}
					player.getPackets().sendGameMessage("Enjoy Your Donation, Thanks for Donating!!");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1>" + player.getUsername() + "has donated tell him thanks for contributing!<img=1>");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1Donate by clicking donate in the quest tab!<img=1>");
					}
				donationGiven(player.getUsername());		
			}
			if(checkDonationItem(player.getUsername()) == 10) { //Productid 10
				player.getPackets().sendGameMessage("<shad=cc0ff><img=1>You Have Recieved Your: 10k Tokens");
				player.getInventory().addItem(12852, 10000);
					for(Player p : World.getPlayers()) {
						if (p ==null) {
							continue; 
						}
					player.getPackets().sendGameMessage("Enjoy Your Donation, Thanks for Donating!!");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1>" + player.getUsername() + "has donated tell him thanks for contributing!<img=1>");
				p.getPackets().sendGameMessage("<shad=cc0ff><img=1Donate by clicking donate in the quest tab!<img=1>");
					}
				donationGiven(player.getUsername());		
			}
		}
	}

	public static void destroyConnection() {
		try {
			stmt.close();
			con.close();
		} catch (Exception e) {
		}
	}

	public static boolean checkDonation(String playerName) {
   
        try {
			String name2 = playerName.replaceAll("_", " ");
            Statement statement = con.createStatement();
            String query = "SELECT * FROM donation WHERE username = '" + name2 + "'";
            ResultSet results = statement.executeQuery(query);
				while(results.next()) {
                    int tickets = results.getInt("tickets"); //tickets are "Recieved" technically. 0 for claimed, 1 for not claimed.
                        if(tickets == 1) {                     
                         return true;
                         }
                                
                        }
                } catch(SQLException e) {
                        e.printStackTrace();
                }
				
                return false;
				
        }
	
	public static int checkDonationItem(String playerName) {
        
        try {
			String name2 = playerName.replaceAll("_", " ");
			Statement statement = con.createStatement();
            String query = "SELECT * FROM donation WHERE username = '" + name2 + "'";
            ResultSet results = statement.executeQuery(query);
                while(results.next()) {
                    int productid = results.getInt("productid");
                        if(productid >= 1) {				                          
							return productid;
                            }
								                               
                        }
                } catch(SQLException e) {
                        e.printStackTrace();
                }
				
                return 0;
				
        }		
	
	public static boolean donationGiven(String playerName) {       
              
			  try
                {
				String name2 = playerName.replaceAll("_", " ");
//				query("DELETE FROM `donation` WHERE username = '"+name2+"';");
                       // query("UPDATE donations SET tickets = 0 WHERE username = '" + playerName + "'");
						//query("UPDATE donations SET productid = 0 WHERE username = '" + playerName + "'");
						
                } catch (Exception e) {
                        e.printStackTrace();
						
                        return false;
                }
                return true;
        }	
	
}