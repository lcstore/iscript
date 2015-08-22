package com.lezo.iscript.io;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

import com.lezo.iscript.IoGain;

@Getter
@Log4j
public class IoGainWorker implements Runnable {
	private IoGain ioGain;

	public IoGainWorker(IoGain ioGain) {
		super();
		this.ioGain = ioGain;
	}

	@Override
	public void run() {
		try {
			ioGain.getParser().doParse(ioGain);
		} catch (Exception e) {
			log.warn("key:" + ioGain.getIoSeed().toKey(), e);
		}
	}

}
