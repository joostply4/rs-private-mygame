// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DiceGame.java

package com.rs.game.player.content;

import java.util.Iterator;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Misc;

// Referenced classes of package com.rs.game.player.content:
//            FriendChatsManager

public class DiceGame {

	public DiceGame() {
	}

	public static void handleItem(Player player, int slot) {
		switch (slot) {
		case 3: // '\003'
		case 4: // '\004'
		default:
			break;

		case 1: // '\001'
			rollDice(player, null);
			break;

		case 2: // '\002'
			if (player.getCurrentFriendChat() != null)
				rollDice(player, player.getCurrentFriendChat().getPlayers());
			else
				player.getPackets().sendGameMessage(
						"You need to be in friends chat to roll this.");
			break;
		}
	}

	private static void rollDice(final Player player, final List<?> players) {
		WorldTile gfxPosition = new WorldTile(player.getX(), player.getY() - 1,
				player.getPlane());
		player.setNextFaceWorldTile(gfxPosition);
		player.setNextAnimation(ROLL_ANIMATION);
		World.sendGraphics(null, new Graphics(2075), gfxPosition);
		player.getPackets().sendGameMessage("Rolling...");
		player.addStopDelay(3L);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.getPackets().sendGameMessage(
						(new StringBuilder("You rolled <col=FF0000>"))
								.append(chance)
								.append("</col> on the perecentile dice.")
								.toString());
				if (players != null) {
					for (Iterator<?> iterator = players.iterator(); iterator
							.hasNext();) {
						Player otherMembers = (Player) iterator.next();
						if (otherMembers != player)
							otherMembers
									.getPackets()
									.sendGameMessage(
											(new StringBuilder(
													"Friends Chat channel-mate <col=FF0000>"))
													.append(Misc
															.formatPlayerNameForDisplay(player
																	.getUsername()))
													.append("</col> rolled <col=FF0000>")
													.append(chance)
													.append("</col> on the percentile dice.")
													.toString());
					}

				}
			}

			int chance = Misc.random(Commands.diceChance ? 60 : 1, 100);

		}, 3);
	}

	public static final int PRIVATE_ROLL = 1;
	public static final int FRIENDS_ROLL = 2;
	public static final int CHOOSE_DICE = 3;
	public static final int PUT_AWAY = 4;
	private static final Animation ROLL_ANIMATION = new Animation(11900);

}