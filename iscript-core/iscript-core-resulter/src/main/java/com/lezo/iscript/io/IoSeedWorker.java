package com.lezo.iscript.io;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

import com.lezo.iscript.IoGain;
import com.lezo.iscript.IoSeed;
import com.lezo.iscript.cache.GainCacher;
import com.lezo.iscript.cache.SeedCacher;

@Getter
@Log4j
public class IoSeedWorker implements Runnable {
	private IoSeed ioSeed;

	public IoSeedWorker(IoSeed ioSeed) {
		super();
		this.ioSeed = ioSeed;
	}

	@Override
	public void run() {
		try {
			IoGain result = ioSeed.getFetcher().doFetch(ioSeed);
			if (result != null) {
				GainCacher.getInstance().getQueue().offer(result);
			} else {
				log.warn("null result for key:" + ioSeed.toKey());
			}
		} catch (Exception e) {
			log.warn("doRetry:" + ioSeed.getRetry() + ",cause:", e);
			if (ioSeed.getRetry() < 3) {
				ioSeed.setRetry(ioSeed.getRetry() + 1);
				SeedCacher.getInstance().getQueue().offer(ioSeed.getLevel(), ioSeed);
			}
		}
	}

}
