package com.rs.game.player.controlers;

import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.content.Dungeoneering;

public class Kalaboss extends Controler {

	private boolean showingOption;

	@Override
	public void start() {
		setInviteOption(true);
	}

	@Override
	public boolean canPlayerOption1(Player target) {
		player.setNextFaceWorldTile(target);
		// player.getPackets().sendGameMessage("You can't do that right now.");
		target.getPackets().sendGameMessage(
				"You have been invited to a dung by an admin.");
		Dungeoneering.startDungeon(1, 6, 0, player, target);
		return false;
	}

	@Override
	public boolean login() {
		moved();
		return false;
	}

	@Override
	public boolean sendDeath() {
		setInviteOption(false);
		removeControler();
		return true;
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public void forceClose() {
		setInviteOption(false);
	}

	@Override
	public void moved() {
		if (player.getX() == 3385 && player.getY() == 3615) {
			setInviteOption(false);
			removeControler();
			player.getControlerManager().startControler("Wilderness");
		} else {
			if (!isAtKalaboss(player)) {
				setInviteOption(false);
				removeControler();
			} else
				setInviteOption(true);
		}
	}

	public static boolean isAtKalaboss(WorldTile tile) {
		return tile.getX() >= 3385 && tile.getX() <= 3513
				&& tile.getY() >= 3605 && tile.getY() <= 3794;
	}

	public void setInviteOption(boolean show) {
		if (show == showingOption)
			return;
		showingOption = show;
		player.getPackets()
				.sendPlayerOption(show ? "Invite" : "null", 1, false);
	}
}
