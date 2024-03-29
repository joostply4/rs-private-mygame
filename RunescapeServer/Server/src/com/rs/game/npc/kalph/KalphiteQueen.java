package com.rs.game.npc.kalph;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

@SuppressWarnings("serial")
public class KalphiteQueen extends NPC {

	public KalphiteQueen(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(0);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playerIndexes = World.getRegion(regionId)
					.getPlayerIndexes();
			if (playerIndexes != null) {
				for (int npcIndex : playerIndexes) {
					Player player = World.getPlayers().get(npcIndex);
					if (player == null
							|| player.isDead()
							|| player.hasFinished()
							|| !player.isRunning()
							|| !player.withinDistance(this, 64)
							|| ((!isAtMultiArea() || !player.isAtMultiArea())
									&& player.getAttackedBy() != this && player
									.getAttackedByDelay() > Utils
									.currentTimeMillis())
							|| !clipedProjectile(player, false))
						continue;
					possibleTarget.add(player);
				}
			}
		}
		return possibleTarget;
	}

	@Override
	public void sendDeath(final Entity source) {
		// TODO Finish up first & second death
		final NPC n = this;
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int step;
			NPC kq;

			@Override
			public void run() {
				if (getId() == 1158) {
					if (step == 10) {
						stop();
					}
					if (step == 0) {
						setNextAnimation(new Animation(defs.getDeathEmote()));
					}
					if (step == 2) {
						finish();
						kq = new NPC(1160, n, 0, true, true);
						kq.setNextGraphics(new Graphics(1055));
					}
					if (step == 8) {
					}
				} else if (getId() == 1160) {
					if (step == 4) {
						finish();
						stop();
					}
					if (step == 0) {
						setNextAnimation(new Animation(defs.getDeathEmote()));
					}
				}
				step++;
			}
		}, 0, 1);
	}

	public static boolean atKQ(WorldTile tile) {
		if ((tile.getX() >= 3462 && tile.getX() <= 3510)
				&& (tile.getY() >= 9462 && tile.getY() <= 9528))
			return true;
		return false;
	}

}
