package com.rs.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import com.rs.cache.Cache;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.io.InputStream;
import com.rs.utils.Logger;
import com.rs.utils.MapContainersXteas;

public class NPCSpawnsDumper {

	private static int writtenCount;

	public static final void main(String[] args) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(
				"data/npcs/unpackedSpawnsList.txt", true));
		Logger.log("Launcher", "Initing Cache...");
		Cache.init();
		Logger.log("Launcher", "Initing Data File...");
		MapContainersXteas.init();
		for (int regionId = 0; regionId < 20000; regionId++) {
			dumpRegionNPCs(regionId);
		}
		out.close();
		System.out.println("found " + writtenCount + " npc spawns on cache.");

	}

	public static final void dumpRegionNPCs(int regionId) {
		File file = new File("data/npcs/packedSpawns/" + regionId + ".ns");
		if (!file.exists())
			return;
		try {
			RandomAccessFile in = new RandomAccessFile(file, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			while (buffer.hasRemaining()) {
				int npcId = buffer.getShort() & 0xffff;
				int plane = buffer.get() & 0xff;
				int x = buffer.getShort() & 0xffff;
				int y = buffer.getShort() & 0xffff;
				boolean hashExtraInformation = buffer.get() == 1;
				int mapAreaNameHash = -1;
				boolean canBeAttackFromOutOfArea = true;
				if (hashExtraInformation) {
					mapAreaNameHash = buffer.getInt();
					canBeAttackFromOutOfArea = buffer.get() == 1;
				}
				
				NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npcId);
				
				FileWriter fstream = new FileWriter("data/npcs/unpacked/unpackedSpawnsPackableList.txt",true);
				BufferedWriter output = new BufferedWriter(fstream);
				output.write("//" + def.name);
				output.newLine();
				output.write(npcId + " - " + x + " " + y + " 0");
				output.newLine();
				output.newLine();
				output.close();
				
				writtenCount++;
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

}
