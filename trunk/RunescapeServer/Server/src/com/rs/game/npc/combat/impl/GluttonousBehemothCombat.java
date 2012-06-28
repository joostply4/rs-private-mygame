package com.rs.game.npc.combat.impl;

import java.util.ArrayList;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.dungeonnering.DungeonBoss;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

public class GluttonousBehemothCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Gluttonous behemoth" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int size = npc.getSize();
		ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
		if ((npc.getHitpoints() < npc.getMaxHitpoints() / 2)
				&& npc.getTemporaryAttributtes().get("healed") == null) {
			DungeonBoss boss = (DungeonBoss) npc;
			int[] pos = boss.getDungeon().getCurrentRoomPos(npc);
			final WorldTile baseTile = boss.getDungeon().getBaseCoords(pos);
			boolean left = true;
			boolean right = boss.getDungeon().getPartySize() >= 2;
			for (Entity t : possibleTargets) {
				if (left) {
					if (t.getX() == baseTile.getX() - 4
							&& t.getY() == baseTile.getY() + 4) {
						left = false;
						continue;
					}
				}
				if (right) {
					if (t.getX() == baseTile.getX() + 2
							&& t.getY() == baseTile.getY() + 4)
						right = false;
				}
			}
			if (left || right) {
				npc.getTemporaryAttributtes().put("healed", Boolean.TRUE);
				npc.addWalkSteps(left ? npc.getX() - 1 : npc.getX() + 1,
						npc.getY());
				npc.setNextFaceEntity(null);
				final boolean wasLeft = left;
				WorldTasksManager.schedule(new WorldTask() {
					int tick;

					@Override
					public void run() {
						if (tick == 0) {
							npc.setNextAnimation(new Animation(13720));
						} else if (npc.getHitpoints() >= (npc.getMaxHitpoints() * 0.75)
								|| npc.hasFinished() || npc.isDead()) {
							npc.setNextAnimation(new Animation(-1));
							npc.addWalkSteps(
									wasLeft ? npc.getX() + 1 : npc.getX() - 1,
									npc.getY());
							npc.getCombat().setCombatDelay(3);
							npc.getCombat().removeTarget();
							stop();
							return;
						}
						npc.applyHit(new Hit(npc, 50 + Utils.getRandom(50),
								HitLook.HEALED_DAMAGE));
						npc.setNextFaceEntity(null);
						tick++;
					}
				}, 1, 0);
				return Integer.MAX_VALUE;
			}
		}
		boolean stomp = false;
		for (Entity t : possibleTargets) {
			int distanceX = t.getX() - npc.getX();
			int distanceY = t.getY() - npc.getY();
			if (distanceX < size && distanceX > -1 && distanceY < size
					&& distanceY > -1) {
				stomp = true;
				delayHit(
						npc,
						0,
						t,
						getRegularHit(
								npc,
								getRandomMaxHit(npc, defs.getMaxHit(),
										NPCCombatDefinitions.MELEE, t)));
			}
		}
		if (stomp) {
			npc.setNextAnimation(new Animation(13718));
			return defs.getAttackDelay();
		}
		int attackStyle = Utils.getRandom(2);
		if (attackStyle == 2) {
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			if (!(distanceX < size && distanceX > -1 && distanceY < size && distanceY > -1))
				attackStyle = Utils.getRandom(1);
			else {
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(
						npc,
						0,
						target,
						getMeleeHit(
								npc,
								getRandomMaxHit(npc, defs.getMaxHit(),
										NPCCombatDefinitions.MELEE, target)));

				return defs.getAttackDelay();
			}
		}
		if (attackStyle == 0) {
			npc.setNextAnimation(new Animation(13719));
			World.sendProjectile(npc, target, 2612, 41, 16, 41, 35, 16, 0);
			int damage = getRandomMaxHit(npc, defs.getMaxHit(),
					NPCCombatDefinitions.MAGE, target);
			delayHit(npc, 2, target, getMagicHit(npc, damage));
			if (damage != 0) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						target.setNextGraphics(new Graphics(2613));
					}
				}, 1);
			}
		} else if (attackStyle == 1) {
			npc.setNextAnimation(new Animation(13722));
			World.sendProjectile(npc, target, 2611, 41, 16, 41, 35, 16, 0);
			delayHit(
					npc,
					2,
					target,
					getRangeHit(
							npc,
							getRandomMaxHit(npc, defs.getMaxHit(),
									NPCCombatDefinitions.RANGE, target)));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.setNextGraphics(new Graphics(2611));
				}
			}, 1);
		}
		return defs.getAttackDelay();
	}
}
