package com.slymask3.instantblocks.util;

import com.slymask3.instantblocks.InstantBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SchematicHelper {
	public static final String SCHEMATICS_DIR = "schematics";

	public static Schematic readSchematic(String schematicName) {
		try {
			File file = new File(SCHEMATICS_DIR + "/" + schematicName);
			FileInputStream fis = new FileInputStream(file);
			CompoundTag tag = NbtIo.readCompressed(fis);
			fis.close();
			return new Schematic(tag);
		} catch(Exception e) {
			InstantBlocks.LOGGER.info("Couldn't read schematic.");
		}
		return null;
	}

	public static void createSchematicsDir() {
		File dir = new File(SCHEMATICS_DIR);
		if(!dir.exists()) {
			try {
				dir.mkdir();
				InstantBlocks.LOGGER.info("created schematics directory");
			} catch(SecurityException se) {
				InstantBlocks.LOGGER.error("failed to create schematics directory: " + se.getMessage());
			}
		}
	}

	public static ArrayList<String> getSchematics() {
		ArrayList<String> schematics = new ArrayList<>();
		File dir = new File(SCHEMATICS_DIR + "/");
		File[] files = dir.listFiles(file -> file.isFile() && (file.getName().toLowerCase().endsWith(".schematic") || file.getName().toLowerCase().endsWith(".schem")));
		if(files != null) {
			for(File file : files) {
				schematics.add(file.getName());
			}
		}
		return schematics;
	}

	public static class Schematic {
		public final short width;
		public final short height;
		public final short length;
		public final byte[] blocks;
		private final HashMap<Integer,BlockState> map;
		private final HashMap<Helper.Coords,CompoundTag> blockEntities;
		private final ArrayList<CompoundTag> entities;
		public Schematic(CompoundTag tag) {
			this.width = tag.getShort("Width");
			this.height = tag.getShort("Height");
			this.length = tag.getShort("Length");
			this.blocks = tag.getByteArray("BlockData");
			this.map = new HashMap<>();
			CompoundTag palette = tag.getCompound("Palette");
			for(String blockStateString : palette.getAllKeys()) {
				int key = palette.getInt(blockStateString);
				BlockState state = Helper.readBlockState(blockStateString);
				this.map.put(key,state);
			}
			this.blockEntities = new HashMap<>();
			ListTag blockEntities = tag.getList("BlockEntities",Tag.TAG_COMPOUND);
			for(Tag entity : blockEntities) {
				CompoundTag entityTag = (CompoundTag)entity;
				int[] pos = entityTag.getIntArray("Pos");
				Helper.Coords entityPos = new Helper.Coords(pos[0],pos[1],pos[2]);
				this.blockEntities.put(entityPos,entityTag);
			}
			this.entities = new ArrayList<>();
			ListTag entities = tag.getList("Entities",Tag.TAG_COMPOUND);
			for(Tag entity : entities) {
				CompoundTag entityTag = (CompoundTag)entity;
				this.entities.add(entityTag);
			}
		}
		public BlockState getBlockState(int index) {
			return this.map.get((int)this.blocks[index]);
		}
		public CompoundTag getBlockEntityTag(int x, int y, int z) {
			return this.blockEntities.get(new Helper.Coords(x,y,z));
		}
		public ArrayList<CompoundTag> getEntityTags() {
			return this.entities;
		}
	}
}