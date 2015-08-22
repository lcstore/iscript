package com.lezo.iscript.cache;

import lombok.Getter;

import com.lezo.iscript.IoSeed;
import com.lezo.iscript.common.queue.LevelQueue;

/**
 * 种子队列
 * 
 * @author lezo
 *
 */
@Getter
public class SeedCacher {
	private static final SeedCacher INSTANCE = new SeedCacher();
	private LevelQueue<IoSeed> queue = new LevelQueue<IoSeed>(1000);

	public static SeedCacher getInstance() {
		return INSTANCE;
	}
}
