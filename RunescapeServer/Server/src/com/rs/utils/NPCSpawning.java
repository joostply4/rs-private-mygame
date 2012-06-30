package com.rs.utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;

public class NPCSpawning {

	/**
	 * Contains the custom npc spawning
	 */

	public static void spawnNPCS() {
		/**
		 * NPCS
		 */
		npcSpawn();
		World.spawnNPC(2676, new WorldTile(3179, 5706, 0), -1, true, true);// makover
																			// mage
		World.spawnNPC(14358, new WorldTile(3175, 5710, 0), -1, true, true);//

		World.spawnNPC(8528, new WorldTile(3105, 3934, 0), -1, true, true);// nomad

		World.spawnNPC(8461, new WorldTile(3087, 3502, 0), -1, true, true);// slayer
		World.spawnNPC(550, new WorldTile(3093, 3485, 0), -1, true, true);// range

		World.spawnNPC(445, new WorldTile(3087, 3495, 0), -1, true, true);// frog
																			// runes
		World.spawnNPC(519, new WorldTile(3094, 3500, 0), -1, true, true);// bob

		World.spawnNPC(576, new WorldTile(3088, 3494, 0), -1, true, true);// harry

		World.spawnNPC(2253, new WorldTile(3091, 3500, 0), -1, true, true);// skillcape

		// World.spawnNPC(6537, new WorldTile(3087, 3500, 0), -1, true, true);
		// // mandrith

		/* ------------ Glacors ------------ */

		World.spawnNPC(14301, new WorldTile(4194, 5716, 0), -1, true, true);
		World.spawnNPC(14301, new WorldTile(4190, 5708, 0), -1, true, true);
		World.spawnNPC(14301, new WorldTile(4182, 5708, 0), -1, true, true);
		World.spawnNPC(14301, new WorldTile(4714, 5722, 0), -1, true, true);
		World.spawnNPC(14301, new WorldTile(4192, 5720, 0), -1, true, true);
		World.spawnNPC(14301, new WorldTile(4203, 5716, 0), -1, true, true);
		World.spawnNPC(14301, new WorldTile(4176, 5721, 0), -1, true, true);
		World.spawnNPC(14301, new WorldTile(4171, 5715, 0), -1, true, true);
		World.spawnNPC(14301, new WorldTile(4184, 5717, 0), -1, true, true);
		World.spawnNPC(14301, new WorldTile(4212, 5710, 0), -1, true, true);
		World.spawnNPC(14301, new WorldTile(4212, 5718, 0), -1, true, true);

		/*
		 * World.spawnNPC(14301, new WorldTile(14301, 5710, 0), -1, true, true);
		 * World.spawnNPC(14301, new WorldTile(14301, 5710, 0), -1, true, true);
		 * World.spawnNPC(14301, new WorldTile(14301, 5710, 0), -1, true, true);
		 * World.spawnNPC(14301, new WorldTile(14301, 5710, 0), -1, true, true);
		 */

		/* ----------- End of Glacors ------ */

		/**
		 * Object custom add
		 */
		// World.spawnObject(new WorldObject(17010, 10, 0, 3158, 5710, 0),
		// true);//altar rune
		World.spawnObject(new WorldObject(59463, 10, 0, 3083, 3486, 0), true);// moble
																				// thing
	}

	/**
	 * The NPC classes.
	 */
	private static final Map<Integer, Class<?>> CUSTOM_NPCS = new HashMap<Integer, Class<?>>();

	public static void npcSpawn() {
		int size = 0;
		boolean ignore = false;
		try {
			for (String string : FileUtilities
					.readFile("data/npcs/npcspawns.txt")) {
				if (string.startsWith("//") || string.equals("")) {
					continue;
				}
				if (string.contains("/*")) {
					ignore = true;
					continue;
				}
				if (ignore) {
					if (string.contains("*/")) {
						ignore = false;
					}
					continue;
				}
				String[] spawn = string.split(" ");
				@SuppressWarnings("unused")
				int id = Integer.parseInt(spawn[0]), x = Integer
						.parseInt(spawn[1]), y = Integer.parseInt(spawn[2]), z = Integer
						.parseInt(spawn[3]), faceDir = Integer
						.parseInt(spawn[4]);
				NPC npc = null;
				Class<?> npcHandler = CUSTOM_NPCS.get(id);
				if (npcHandler == null) {
					npc = new NPC(id, new WorldTile(x, y, z), -1, true, false);
				} else {
					npc = (NPC) npcHandler.getConstructor(int.class)
							.newInstance(id);
				}
				if (npc != null) {
					WorldTile spawnLoc = new WorldTile(x, y, z);
					npc.setLocation(spawnLoc);
					World.spawnNPC(npc.getId(), spawnLoc, -1, true, false);
					size++;
				}
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		System.err.println("Loaded " + size + " custom npc spawns!");
	}

}