package com.slymask3.instantblocks.tileentity;

import com.slymask3.instantblocks.init.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityStatue extends TileEntityInstant {
	public String username;
	public boolean head;
	public boolean body;
	public boolean armLeft;
	public boolean armRight;
	public boolean legLeft;
	public boolean legRight;
	public boolean rgb;

	public TileEntityStatue(BlockPos pos, BlockState state) {
		super(ModTiles.STATUE.get(), pos, state);
		this.username = "";
		this.head = true;
		this.body = true;
		this.armLeft = true;
		this.armRight = true;
		this.legLeft = true;
		this.legRight = true;
		this.rgb = true;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.username = nbt.getString("Username");
		this.head = nbt.getBoolean("Head");
		this.body = nbt.getBoolean("Body");
		this.armLeft = nbt.getBoolean("ArmLeft");
		this.armRight = nbt.getBoolean("ArmRight");
		this.legLeft = nbt.getBoolean("LegLeft");
		this.legRight = nbt.getBoolean("LegRight");
		this.rgb = nbt.getBoolean("RGB");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putString("Username", username);
		nbt.putBoolean("Head", head);
		nbt.putBoolean("Body", body);
		nbt.putBoolean("ArmLeft", armLeft);
		nbt.putBoolean("ArmRight", armRight);
		nbt.putBoolean("LegLeft", legLeft);
		nbt.putBoolean("LegRight", legRight);
		nbt.putBoolean("RGB", rgb);
	}
}