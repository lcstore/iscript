package com.lezo.iscript.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.IoGain;
import com.lezo.iscript.IoSeed;
import com.lezo.iscript.cache.SeedCacher;
import com.lezo.iscript.io.FileFetcher;
import com.lezo.iscript.io.IFetcher;
import com.lezo.iscript.io.IoConstants;
import com.lezo.iscript.io.IoWatcher;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.rest.data.RestFile;
import com.lezo.rest.data.RestList;

@Log4j
public class PathParser implements IParser {
	private static AtomicLong count = new AtomicLong();

	@Override
	public void doParse(IoGain ioGain) {

		RestList restList = (RestList) ioGain.getValue();
		if (CollectionUtils.isEmpty(restList.getDataList())) {
			return;
		}
		addNextFetcherSeeds(ioGain);
		addNextSeeds(ioGain);

	}

	private void addNextSeeds(IoGain ioGain) {
		RestList restList = (RestList) ioGain.getValue();
		int newCount = restList.getDataList() == null ? 0 : restList.getDataList().size();
		count.getAndAdd(newCount);
		if (restList.isEOF()) {
			IoWatcher.getInstance().addDir(ioGain.getIoSeed(), newCount, true);
			return;
		} else {
			IoWatcher.getInstance().addDir(ioGain.getIoSeed(), newCount, false);
		}
		IoSeed fromSeed = ioGain.getIoSeed();
		Map<String, String> params = fromSeed.getParams();
		if (params == null) {
			return;
		}
		String limitKey = "limit";
		String limitValue = params.get(limitKey);
		if (StringUtils.isNotBlank(limitValue)) {
			String[] sNumArr = limitValue.split("-");
			if (sNumArr.length == 2) {
				int index = -1;
				int fromCount = Integer.valueOf(sNumArr[++index]);
				int toCount = Integer.valueOf(sNumArr[++index]);
				int limit = toCount - fromCount;
				params.put(limitKey, toCount + "-" + (toCount + limit));
			}
		}
		SeedCacher cacher = SeedCacher.getInstance();
		cacher.getQueue().offer(fromSeed.getLevel(), fromSeed);
		if (log.isDebugEnabled()) {
			log.debug("key:" + fromSeed.toKey() + ",param:" + JSONUtils.getJSONObject(fromSeed.getParams()));
		}
	}

	private void addNextFetcherSeeds(IoGain ioGain) {
		RestList restList = (RestList) ioGain.getValue();
		SeedCacher cacher = SeedCacher.getInstance();
		IFetcher fetcher = new FileFetcher();
		IoSeed fromSeed = ioGain.getIoSeed();
		List<IoSeed> newSeeds = new ArrayList<IoSeed>(restList.getDataList().size());
		int level = IoConstants.LEVEL_FILE;
		for (RestFile data : restList.getDataList()) {
			IoSeed ioSeed = new IoSeed();
			ioSeed.setBucket(fromSeed.getBucket());
			ioSeed.setDomain(fromSeed.getDomain());
			ioSeed.setDataPath(data.getPath());
			ioSeed.setLevel(level);
			ioSeed.setFetcher(fetcher);
			newSeeds.add(ioSeed);
		}
		if (!newSeeds.isEmpty()) {
			int addCount = cacher.getQueue().offer(level, newSeeds);
			log.info("add new seed.level:" + level + ",srcCount:" + newSeeds.size() + ",addCount:" + addCount);
		}
	}

	public static AtomicLong getCount() {
		return count;
	}
}
