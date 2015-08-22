package com.lezo.iscript.io;

import com.lezo.iscript.IoGain;
import com.lezo.iscript.IoSeed;

public interface IFetcher {
	IoGain doFetch(IoSeed seed) throws Exception;
}
