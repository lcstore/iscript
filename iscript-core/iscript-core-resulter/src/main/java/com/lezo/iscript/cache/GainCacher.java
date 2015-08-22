package com.lezo.iscript.cache;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.Getter;

import com.lezo.iscript.IoGain;

/**
 * 果实队列
 * 
 * @author lezo
 *
 */
@Getter
public class GainCacher {
	private static final GainCacher INSTANCE = new GainCacher();
	private Queue<IoGain> queue = new LinkedBlockingQueue<IoGain>();

	public static GainCacher getInstance() {
		return INSTANCE;
	}
}
