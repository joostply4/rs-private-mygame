package com.rs.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.rs.cache.Cache;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.utils.Utils;

public class ItemListDumper {

	public static void main(String[] args) throws IOException {
		Cache.init();
		File file = new File("information/itemlist.txt");
		if (file.exists())
			file.delete();
		else
			file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append("//Version = 667.700\n");
		writer.flush();
		for (int id = 0; id < Utils.getItemDefinitionsSize(); id++) {
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(id);
			writer.append(id + " - " + def.getName() + "\n");
			// writer.append("AAAAAAAA"+id+"GGGGGGGGBBBBBBB"+def.getName()+"BBBBBBBCCCCCCCCC");
			// //Apache's SQL format
			writer.newLine();
			System.out.println(id + " - " + def.getName());
			writer.flush();
		}
		writer.close();
	}

}
