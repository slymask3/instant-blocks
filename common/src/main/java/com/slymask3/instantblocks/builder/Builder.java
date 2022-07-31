package com.slymask3.instantblocks.builder;

import com.slymask3.instantblocks.Common;
import com.slymask3.instantblocks.builder.type.Single;
import net.minecraft.core.BlockPos;

import java.util.*;

public class Builder {
	public enum Status { SETUP, BUILD, DONE }

	public HashMap<BlockPos,Single> queueMap;
	public List<Single> queue;
	public Status status;
	public int speed;
	public int ticks;

	public Builder() {
		this(1);
	}

	public Builder(int speed) {
		Common.Timer.start();
		this.queueMap = new HashMap<>();
		this.queue = new ArrayList<>();
		this.status = Status.SETUP;
		this.speed = speed;
		this.ticks = speed - 1;
	}

	public void tick() {
		if(this.status.equals(Status.BUILD) && !queue.isEmpty()) {
			this.ticks++;
			if(this.ticks == this.speed) {
				Single first = queue.get(0);
				int priority = first.priority;
				first.build();
				queue.remove(0);
				while(!queue.isEmpty() && queue.get(0).priority == priority) {
					queue.get(0).build();
					queue.remove(0);
				}
				if(queue.isEmpty()) {
					this.status = Status.DONE;
				}
				this.ticks = 0;
			}
		}
	}

	public void queue(Single single, boolean replace) {
		BlockPos pos = single.getBlockPos();
		if(replace) {
			if(this.queueMap.containsKey(pos)) {
				this.queueMap.replace(single.getBlockPos(),single);
			} else {
				this.queueMap.put(single.getBlockPos(),single);
			}
		} else {
			this.queue.add(single);
		}
	}

	public void build() {
		Common.LOG.info("build(): " + Common.Timer.end());

		Common.Timer.start();
		for(Map.Entry<BlockPos,Single> set : this.queueMap.entrySet()) {
			this.queue.add(set.getValue());
		}
		this.queue.sort(Comparator.comparingInt(one -> one.priority));
		this.queueMap.clear();
		Common.LOG.info("sort(): " + Common.Timer.end());

		this.status = Status.BUILD;
		builders.add(this);
	}

	public static List<Builder> builders = new ArrayList<>();

	public static void globalTick() {
		if(!builders.isEmpty()) {
			for(Builder builder : builders) {
				builder.tick();
				if(builder.status.equals(Status.DONE)) {
					builders.remove(builder);
					break;
				}
			}
		}
	}
}