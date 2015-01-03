package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.common.storage.StorageBuffer;
import com.lezo.iscript.common.storage.StorageBufferFactory;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class JdBrandShopStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(JdBrandShopStrategy.class);
	private static volatile boolean running = false;
	private Timer timer;

	public JdBrandShopStrategy() {
		CreateTaskTimer task = new CreateTaskTimer();
		this.timer = new Timer("CreateTaskTimer");
		this.timer.schedule(task, 60 * 1000, 12 * 60 * 60 * 1000);
	}

	private class CreateTaskTimer extends TimerTask {
		private Map<String, Set<String>> typeMap;

		public CreateTaskTimer() {
			typeMap = new HashMap<String, Set<String>>();
			Set<String> urlSet = new HashSet<String>();
			urlSet.add("http://list.jd.com/737-794-798.html");
			urlSet.add("http://list.jd.com/737-794-870.html");
			urlSet.add("http://list.jd.com/737-794-878.html");
			urlSet.add("http://list.jd.com/737-794-880.html");
			urlSet.add("http://list.jd.com/737-794-823.html");
			urlSet.add("http://list.jd.com/737-794-965.html");
			urlSet.add("http://list.jd.com/737-794-1199.html");
			urlSet.add("http://list.jd.com/737-794-1300.html");
			urlSet.add("http://list.jd.com/737-794-1706.html");
			urlSet.add("http://list.jd.com/737-794-1301.html");
			urlSet.add("http://list.jd.com/737-794-1707.html");
			urlSet.add("http://list.jd.com/737-794-12392.html");
			urlSet.add("http://list.jd.com/737-794-12401.html");
			urlSet.add("http://list.jd.com/737-794-877.html");
			urlSet.add("http://list.jd.com/737-738-747.html");
			urlSet.add("http://list.jd.com/737-738-749.html");
			urlSet.add("http://list.jd.com/737-738-748.html");
			urlSet.add("http://list.jd.com/737-738-12394.html");
			urlSet.add("http://list.jd.com/737-738-745.html");
			urlSet.add("http://list.jd.com/737-738-1279.html");
			urlSet.add("http://list.jd.com/737-738-1052.html");
			urlSet.add("http://list.jd.com/737-738-806.html");
			urlSet.add("http://list.jd.com/737-738-897.html");
			urlSet.add("http://list.jd.com/737-738-1283.html");
			urlSet.add("http://list.jd.com/737-738-12395.html");
			urlSet.add("http://list.jd.com/737-738-801.html");
			urlSet.add("http://list.jd.com/737-738-751.html");
			urlSet.add("http://list.jd.com/737-738-1278.html");
			urlSet.add("http://list.jd.com/737-738-825.html");
			urlSet.add("http://list.jd.com/737-738-12396.html");
			urlSet.add("http://list.jd.com/737-738-898.html");
			urlSet.add("http://list.jd.com/737-738-750.html");
			urlSet.add("http://list.jd.com/737-752-755.html");
			urlSet.add("http://list.jd.com/737-752-756.html");
			urlSet.add("http://list.jd.com/737-752-753.html");
			urlSet.add("http://list.jd.com/737-752-881.html");
			urlSet.add("http://list.jd.com/737-752-899.html");
			urlSet.add("http://list.jd.com/737-752-761.html");
			urlSet.add("http://list.jd.com/737-752-758.html");
			urlSet.add("http://list.jd.com/737-752-759.html");
			urlSet.add("http://list.jd.com/737-752-757.html");
			urlSet.add("http://list.jd.com/737-752-882.html");
			urlSet.add("http://list.jd.com/737-752-902.html");
			urlSet.add("http://list.jd.com/737-752-762.html");
			urlSet.add("http://list.jd.com/737-752-9249.html");
			urlSet.add("http://list.jd.com/737-752-760.html");
			urlSet.add("http://list.jd.com/737-752-754.html");
			urlSet.add("http://list.jd.com/737-752-901.html");
			urlSet.add("http://list.jd.com/737-752-803.html");
			urlSet.add("http://list.jd.com/737-752-12397.html");
			urlSet.add("http://list.jd.com/737-752-12398.html");
			urlSet.add("http://list.jd.com/737-1276-739.html");
			urlSet.add("http://list.jd.com/737-1276-742.html");
			urlSet.add("http://list.jd.com/737-1276-741.html");
			urlSet.add("http://list.jd.com/737-1276-740.html");
			urlSet.add("http://list.jd.com/737-1276-795.html");
			urlSet.add("http://list.jd.com/737-1276-1287.html");
			urlSet.add("http://list.jd.com/737-1276-12400.html");
			urlSet.add("http://list.jd.com/737-1276-1291.html");
			urlSet.add("http://list.jd.com/737-1276-967.html");
			urlSet.add("http://list.jd.com/737-1276-963.html");
			urlSet.add("http://list.jd.com/737-1276-966.html");
			urlSet.add("http://list.jd.com/737-1276-1289.html");
			urlSet.add("http://list.jd.com/737-1276-1225.html");
			urlSet.add("http://list.jd.com/737-1276-1292.html");
			urlSet.add("http://list.jd.com/737-1276-1290.html");
			urlSet.add("http://list.jd.com/737-1276-968.html");
			urlSet.add("http://list.jd.com/737-1277-934.html");
			urlSet.add("http://list.jd.com/737-1277-3979.html");
			urlSet.add("http://list.jd.com/737-1277-6974.html");
			urlSet.add("http://list.jd.com/737-1277-900.html");
			urlSet.add("http://list.jd.com/737-1277-1295.html");
			urlSet.add("http://list.jd.com/737-1277-6975.html");
			urlSet.add("http://list.jd.com/737-1277-4934.html");
			urlSet.add("http://list.jd.com/737-1277-5004.html");
			urlSet.add("http://list.jd.com/737-1277-5005.html");
			urlSet.add("http://list.jd.com/737-1277-5006.html");
			urlSet.add("http://list.jd.com/737-1277-1293.html");
			urlSet.add("http://list.jd.com/737-1277-4834.html");
			urlSet.add("http://list.jd.com/737-1277-1294.html");
			urlSet.add("http://list.jd.com/737-1277-6976.html");
			urlSet.add("http://list.jd.com/737-1277-6977.html");
			urlSet.add("http://list.jd.com/737-1277-6978.html");
			urlSet.add("http://list.jd.com/737-1277-6979.html");
			urlSet.add("http://list.jd.com/737-1277-1299.html");
			urlSet.add("http://list.jd.com/670-671-672.html");
			urlSet.add("http://list.jd.com/670-671-6864.html");
			urlSet.add("http://list.jd.com/670-671-1105.html");
			urlSet.add("http://list.jd.com/670-671-2694.html");
			urlSet.add("http://list.jd.com/670-671-5146.html");
			urlSet.add("http://list.jd.com/670-671-673.html");
			urlSet.add("http://list.jd.com/670-671-674.html");
			urlSet.add("http://list.jd.com/670-671-675.html");
			urlSet.add("http://list.jd.com/670-677-678.html");
			urlSet.add("http://list.jd.com/670-677-681.html");
			urlSet.add("http://list.jd.com/670-677-679.html");
			urlSet.add("http://list.jd.com/670-677-683.html");
			urlSet.add("http://list.jd.com/670-677-11303.html");
			urlSet.add("http://list.jd.com/670-677-680.html");
			urlSet.add("http://list.jd.com/670-677-687.html");
			urlSet.add("http://list.jd.com/670-677-691.html");
			urlSet.add("http://list.jd.com/670-677-688.html");
			urlSet.add("http://list.jd.com/670-677-684.html");
			urlSet.add("http://list.jd.com/670-677-682.html");
			urlSet.add("http://list.jd.com/670-677-5008.html");
			urlSet.add("http://list.jd.com/670-677-5009.html");
			urlSet.add("http://list.jd.com/670-677-11762.html");
			urlSet.add("http://list.jd.com/670-686-693.html");
			urlSet.add("http://list.jd.com/670-686-694.html");
			urlSet.add("http://list.jd.com/670-686-690.html");
			urlSet.add("http://list.jd.com/670-686-689.html");
			urlSet.add("http://list.jd.com/670-686-826.html");
			urlSet.add("http://list.jd.com/670-686-692.html");
			urlSet.add("http://list.jd.com/670-686-698.html");
			urlSet.add("http://list.jd.com/670-686-695.html");
			urlSet.add("http://list.jd.com/670-686-1047.html");
			urlSet.add("http://list.jd.com/670-686-1049.html");
			urlSet.add("http://list.jd.com/670-686-1048.html");
			urlSet.add("http://list.jd.com/670-686-1050.html");
			urlSet.add("http://list.jd.com/670-686-696.html");
			urlSet.add("http://list.jd.com/670-686-697.html");
			urlSet.add("http://list.jd.com/670-686-1051.html");
			urlSet.add("http://list.jd.com/670-699-700.html");
			urlSet.add("http://list.jd.com/670-699-701.html");
			urlSet.add("http://list.jd.com/670-699-702.html");
			urlSet.add("http://list.jd.com/670-699-983.html");
			urlSet.add("http://list.jd.com/670-699-1098.html");
			urlSet.add("http://list.jd.com/670-699-11304.html");
			urlSet.add("http://list.jd.com/670-699-12370.html");
			urlSet.add("http://list.jd.com/670-716-722.html");
			urlSet.add("http://list.jd.com/670-716-5010.html");
			urlSet.add("http://list.jd.com/670-716-720.html");
			urlSet.add("http://list.jd.com/670-716-717.html");
			urlSet.add("http://list.jd.com/670-716-718.html");
			urlSet.add("http://list.jd.com/670-716-725.html");
			urlSet.add("http://list.jd.com/670-716-721.html");
			urlSet.add("http://list.jd.com/670-716-719.html");
			urlSet.add("http://list.jd.com/670-716-723.html");
			urlSet.add("http://list.jd.com/670-716-724.html");
			urlSet.add("http://list.jd.com/670-716-7373.html");
			urlSet.add("http://list.jd.com/670-716-7375.html");
			urlSet.add("http://list.jd.com/670-716-2601.html");
			urlSet.add("http://list.jd.com/670-716-4839.html");
			urlSet.add("http://list.jd.com/670-716-7374.html");
			urlSet.add("http://list.jd.com/670-716-11221.html");
			urlSet.add("http://list.jd.com/670-716-727.html");
			urlSet.add("http://list.jd.com/670-729-730.html");
			urlSet.add("http://list.jd.com/670-729-731.html");
			urlSet.add("http://list.jd.com/670-729-733.html");
			urlSet.add("http://list.jd.com/670-729-736.html");
			urlSet.add("http://list.jd.com/670-729-4837.html");
			urlSet.add("http://list.jd.com/670-729-1449.html");
			urlSet.add("http://list.jd.com/670-729-7372.html");
			urlSet.add("http://list.jd.com/670-729-4840.html");
			urlSet.add("http://list.jd.com/670-729-7371.html");
			urlSet.add("http://list.jd.com/670-729-728.html");
			urlSet.add("http://list.jd.com/670-729-2603.html");
			urlSet.add("http://list.jd.com/670-729-12376.html");
			urlSet.add("http://list.jd.com/670-729-4838.html");
			urlSet.add("http://list.jd.com/670-703-10011.html");
			urlSet.add("http://list.jd.com/670-703-10970.html");
			urlSet.add("http://list.jd.com/670-703-5011.html");
			urlSet.add("http://list.jd.com/670-703-1009.html");
			urlSet.add("http://list.jd.com/1316-1381-1389.html");
			urlSet.add("http://list.jd.com/1316-1381-1391.html");
			urlSet.add("http://list.jd.com/1316-1381-1392.html");
			urlSet.add("http://list.jd.com/1316-1381-1416.html");
			urlSet.add("http://list.jd.com/1316-1381-1396.html");
			urlSet.add("http://list.jd.com/1316-1383-1401.html");
			urlSet.add("http://list.jd.com/1316-1383-1404.html");
			urlSet.add("http://list.jd.com/1316-1383-1394.html");
			urlSet.add("http://list.jd.com/1316-1383-2562.html");
			urlSet.add("http://list.jd.com/1316-1383-5164.html");
			urlSet.add("http://list.jd.com/1316-1383-11928.html");
			urlSet.add("http://list.jd.com/1316-1383-11929.html");
			urlSet.add("http://list.jd.com/1316-1384-1405.html");
			urlSet.add("http://list.jd.com/1316-1384-1406.html");
			urlSet.add("http://list.jd.com/1316-1384-1407.html");
			urlSet.add("http://list.jd.com/1316-1384-11930.html");
			urlSet.add("http://list.jd.com/1316-1385-1408.html");
			urlSet.add("http://list.jd.com/1316-1385-1409.html");
			urlSet.add("http://list.jd.com/1316-1385-1410.html");
			urlSet.add("http://list.jd.com/1316-1385-5150.html");
			urlSet.add("http://list.jd.com/1316-1386-11922.html");
			urlSet.add("http://list.jd.com/1316-1386-11923.html");
			urlSet.add("http://list.jd.com/1316-1386-11924.html");
			urlSet.add("http://list.jd.com/1316-1386-11925.html");
			urlSet.add("http://list.jd.com/1316-1386-4699.html");
			urlSet.add("http://list.jd.com/1316-1386-6739.html");
			urlSet.add("http://list.jd.com/1316-1387-11932.html");
			urlSet.add("http://list.jd.com/1316-1387-1420.html");
			urlSet.add("http://list.jd.com/1316-1387-1421.html");
			urlSet.add("http://list.jd.com/1316-1387-1422.html");
			urlSet.add("http://list.jd.com/1316-1387-1425.html");
			urlSet.add("http://list.jd.com/1316-1387-1428.html");
			urlSet.add("http://list.jd.com/1316-1387-1429.html");
			urlSet.add("http://list.jd.com/1316-1387-1426.html");
			urlSet.add("http://list.jd.com/5025-5026-12091.html");
			urlSet.add("http://list.jd.com/5025-5026-12092.html");
			urlSet.add("http://list.jd.com/5025-5026-12093.html");
			urlSet.add("http://list.jd.com/5025-5026-12094.html");
			urlSet.add("http://list.jd.com/1319-1523-7052.html");
			urlSet.add("http://list.jd.com/1319-1523-7054.html");
			urlSet.add("http://list.jd.com/1319-1524-1537.html");
			urlSet.add("http://list.jd.com/1319-1524-1533.html");
			urlSet.add("http://list.jd.com/1319-1524-1534.html");
			urlSet.add("http://list.jd.com/1319-1524-7055.html");
			urlSet.add("http://list.jd.com/1319-1524-12191.html");
			urlSet.add("http://list.jd.com/1319-1524-1538.html");
			urlSet.add("http://list.jd.com/1319-1524-1539.html");
			urlSet.add("http://list.jd.com/1319-1524-9399.html");
			urlSet.add("http://list.jd.com/1319-1525-7057.html");
			urlSet.add("http://list.jd.com/1319-1525-1546.html");
			urlSet.add("http://list.jd.com/1319-1525-1548.html");
			urlSet.add("http://list.jd.com/1319-1525-7058.html");
			urlSet.add("http://list.jd.com/1319-1526-7060.html");
			urlSet.add("http://list.jd.com/1319-1526-1550.html");
			urlSet.add("http://list.jd.com/1319-1526-1551.html");
			urlSet.add("http://list.jd.com/1319-1526-1552.html");
			urlSet.add("http://list.jd.com/1319-1526-7061.html");
			urlSet.add("http://list.jd.com/1319-1526-1553.html");
			urlSet.add("http://list.jd.com/1319-1526-12197.html");
			urlSet.add("http://list.jd.com/1319-1527-1556.html");
			urlSet.add("http://list.jd.com/1319-1527-1555.html");
			urlSet.add("http://list.jd.com/1319-1527-1558.html");
			urlSet.add("http://list.jd.com/1319-1527-1560.html");
			urlSet.add("http://list.jd.com/1319-1527-12341.html");
			urlSet.add("http://list.jd.com/1319-1527-1557.html");
			urlSet.add("http://list.jd.com/1319-1527-1559.html");
			urlSet.add("http://list.jd.com/1319-1527-1562.html");
			urlSet.add("http://list.jd.com/1319-1528-1563.html");
			urlSet.add("http://list.jd.com/1319-1528-1565.html");
			urlSet.add("http://list.jd.com/1319-1528-1564.html");
			urlSet.add("http://list.jd.com/1319-1528-1568.html");
			urlSet.add("http://list.jd.com/1319-1528-1569.html");
			urlSet.add("http://list.jd.com/1319-1528-1566.html");
			urlSet.add("http://list.jd.com/1319-1528-1567.html");
			urlSet.add("http://list.jd.com/1319-1528-4702.html");
			urlSet.add("http://list.jd.com/1319-1528-12192.html");
			urlSet.add("http://list.jd.com/1319-6313-6314.html");
			urlSet.add("http://list.jd.com/1319-6313-11234.html");
			urlSet.add("http://list.jd.com/1319-6313-11235.html");
			urlSet.add("http://list.jd.com/1319-6313-6315.html");
			urlSet.add("http://list.jd.com/1319-6313-6317.html");
			urlSet.add("http://list.jd.com/1319-6313-6316.html");
			urlSet.add("http://list.jd.com/1319-4997-5002.html");
			urlSet.add("http://list.jd.com/1319-4997-5001.html");
			urlSet.add("http://list.jd.com/1319-4997-7062.html");
			urlSet.add("http://list.jd.com/1319-4997-4999.html");
			urlSet.add("http://list.jd.com/1319-4997-4998.html");
			urlSet.add("http://list.jd.com/1319-4997-6319.html");
			urlSet.add("http://list.jd.com/1319-4997-5000.html");
			urlSet.add("http://list.jd.com/1319-4997-12198.html");
			urlSet.add("http://list.jd.com/1319-4997-12199.html");
			urlSet.add("http://list.jd.com/1319-11842-11222.html");
			urlSet.add("http://list.jd.com/1319-11842-11223.html");
			urlSet.add("http://list.jd.com/1319-11842-11224.html");
			urlSet.add("http://list.jd.com/1319-11842-11225.html");
			urlSet.add("http://list.jd.com/1319-11842-11227.html");
			urlSet.add("http://list.jd.com/1319-11842-11226.html");
			urlSet.add("http://list.jd.com/1319-11842-4937.html");
			urlSet.add("http://list.jd.com/1319-11842-3977.html");
			urlSet.add("http://list.jd.com/1319-11842-11228.html");
			urlSet.add("http://list.jd.com/1319-11842-11229.html");
			urlSet.add("http://list.jd.com/1319-11842-11230.html");
			urlSet.add("http://list.jd.com/1319-11842-11231.html");
			urlSet.add("http://list.jd.com/1319-11842-11232.html");
			urlSet.add("http://list.jd.com/1319-11842-11233.html");
			urlSet.add("http://list.jd.com/1319-11842-11843.html");
			urlSet.add("http://list.jd.com/1319-12193-12194.html");
			urlSet.add("http://list.jd.com/1319-12193-12195.html");
			urlSet.add("http://list.jd.com/1319-12193-12196.html");
			urlSet.add("http://list.jd.com/1320-5019-5020.html");
			urlSet.add("http://list.jd.com/1320-5019-5021.html");
			urlSet.add("http://list.jd.com/1320-5019-5022.html");
			urlSet.add("http://list.jd.com/1320-5019-5023.html");
			urlSet.add("http://list.jd.com/1320-5019-5024.html");
			urlSet.add("http://list.jd.com/1320-5019-12215.html");
			urlSet.add("http://list.jd.com/1320-1581-12217.html");
			urlSet.add("http://list.jd.com/1320-1581-1589.html");
			urlSet.add("http://list.jd.com/1320-1581-2644.html");
			urlSet.add("http://list.jd.com/1320-1581-2647.html");
			urlSet.add("http://list.jd.com/1320-1581-2648.html");
			urlSet.add("http://list.jd.com/1320-1581-2653.html");
			urlSet.add("http://list.jd.com/1320-1581-2656.html");
			urlSet.add("http://list.jd.com/1320-1581-2669.html");
			urlSet.add("http://list.jd.com/1320-1581-2670.html");
			urlSet.add("http://list.jd.com/1320-1581-4693.html");
			urlSet.add("http://list.jd.com/1320-1583-1590.html");
			urlSet.add("http://list.jd.com/1320-1583-1591.html");
			urlSet.add("http://list.jd.com/1320-1583-1592.html");
			urlSet.add("http://list.jd.com/1320-1583-1593.html");
			urlSet.add("http://list.jd.com/1320-1583-1594.html");
			urlSet.add("http://list.jd.com/1320-1583-1595.html");
			urlSet.add("http://list.jd.com/1320-1583-7121.html");
			urlSet.add("http://list.jd.com/1320-1584-2675.html");
			urlSet.add("http://list.jd.com/1320-1584-2676.html");
			urlSet.add("http://list.jd.com/1320-1584-2677.html");
			urlSet.add("http://list.jd.com/1320-1584-2678.html");
			urlSet.add("http://list.jd.com/1320-1584-2679.html");
			urlSet.add("http://list.jd.com/1320-1584-2680.html");
			urlSet.add("http://list.jd.com/1320-1585-10975.html");
			urlSet.add("http://list.jd.com/1320-1585-1602.html");
			urlSet.add("http://list.jd.com/1320-1585-9434.html");
			urlSet.add("http://list.jd.com/1320-1585-3986.html");
			urlSet.add("http://list.jd.com/1320-1585-1601.html");
			urlSet.add("http://list.jd.com/1320-1585-12200.html");
			urlSet.add("http://list.jd.com/1320-1585-12201.html");
			urlSet.add("http://list.jd.com/1320-2641-2642.html");
			urlSet.add("http://list.jd.com/1320-2641-2643.html");
			urlSet.add("http://list.jd.com/1320-2641-4935.html");
			urlSet.add("http://list.jd.com/1320-2641-12216.html");
			urlSet.add("http://list.jd.com/1320-12202-12203.html");
			urlSet.add("http://list.jd.com/1320-12202-12204.html");
			urlSet.add("http://list.jd.com/1320-12202-12205.html");
			urlSet.add("http://list.jd.com/1320-12202-12206.html");
			urlSet.add("http://list.jd.com/1320-12202-12207.html");
			urlSet.add("http://list.jd.com/1320-12202-12208.html");
			urlSet.add("http://list.jd.com/1320-12202-12209.html");
			urlSet.add("http://list.jd.com/1320-12202-12210.html");
			urlSet.add("http://list.jd.com/1320-12202-12211.html");
			urlSet.add("http://list.jd.com/1320-12202-12212.html");
			urlSet.add("http://list.jd.com/1320-12202-12213.html");
			urlSet.add("http://list.jd.com/1320-12202-12214.html");
			urlSet.add("http://list.jd.com/6728-6742-11849.html");
			urlSet.add("http://list.jd.com/6728-6742-11850.html");
			urlSet.add("http://list.jd.com/6728-6742-6756.html");
			urlSet.add("http://list.jd.com/6728-6742-11852.html");
			urlSet.add("http://list.jd.com/6728-6742-6767.html");
			urlSet.add("http://list.jd.com/6728-6742-6766.html");
			urlSet.add("http://list.jd.com/6728-6742-6768.html");
			urlSet.add("http://list.jd.com/6728-6742-9988.html");
			urlSet.add("http://list.jd.com/6728-6742-9248.html");
			urlSet.add("http://list.jd.com/6728-6742-11951.html");
			urlSet.add("http://list.jd.com/6728-6742-11859.html");
			urlSet.add("http://list.jd.com/6728-6742-6769.html");
			urlSet.add("http://list.jd.com/6728-6742-9971.html");
			urlSet.add("http://list.jd.com/6728-6742-9964.html");
			urlSet.add("http://list.jd.com/6728-6742-6770.html");
			urlSet.add("http://list.jd.com/6728-6742-6795.html");
			urlSet.add("http://list.jd.com/6728-6742-12406.html");
			urlSet.add("http://list.jd.com/6728-6740-11867.html");
			urlSet.add("http://list.jd.com/6728-6740-9959.html");
			urlSet.add("http://list.jd.com/6728-6740-6964.html");
			urlSet.add("http://list.jd.com/6728-6740-9961.html");
			urlSet.add("http://list.jd.com/6728-6740-9962.html");
			urlSet.add("http://list.jd.com/6728-6740-6965.html");
			urlSet.add("http://list.jd.com/6728-6740-6807.html");
			urlSet.add("http://list.jd.com/6728-6740-6749.html");
			urlSet.add("http://list.jd.com/6728-6740-12408.html");
			urlSet.add("http://list.jd.com/6728-6740-12409.html");
			urlSet.add("http://list.jd.com/6728-6740-6752.html");
			urlSet.add("http://list.jd.com/6728-6740-6753.html");
			urlSet.add("http://list.jd.com/6728-6743-11875.html");
			urlSet.add("http://list.jd.com/6728-6743-9974.html");
			urlSet.add("http://list.jd.com/6728-6743-6757.html");
			urlSet.add("http://list.jd.com/6728-6743-11878.html");
			urlSet.add("http://list.jd.com/6728-6743-11879.html");
			urlSet.add("http://list.jd.com/6728-6743-11880.html");
			urlSet.add("http://list.jd.com/6728-6745-11883.html");
			urlSet.add("http://list.jd.com/6728-6745-11881.html");
			urlSet.add("http://list.jd.com/6728-6745-11882.html");
			urlSet.add("http://list.jd.com/6728-6745-6972.html");
			urlSet.add("http://list.jd.com/6728-6745-11887.html");
			urlSet.add("http://list.jd.com/6728-6745-6785.html");
			urlSet.add("http://list.jd.com/6728-6745-11886.html");
			urlSet.add("http://list.jd.com/6728-6745-11888.html");
			urlSet.add("http://list.jd.com/6728-6745-11889.html");
			urlSet.add("http://list.jd.com/6728-6745-11953.html");
			urlSet.add("http://list.jd.com/6728-6745-6798.html");
			urlSet.add("http://list.jd.com/6728-6747-6792.html");
			urlSet.add("http://list.jd.com/6728-6747-11954.html");
			urlSet.add("http://list.jd.com/6728-6747-11955.html");
			urlSet.add("http://list.jd.com/6728-6747-6796.html");
			urlSet.add("http://list.jd.com/6728-6747-6804.html");
			urlSet.add("http://list.jd.com/6728-6747-12407.html");
			urlSet.add("http://list.jd.com/6728-6747-6801.html");
			urlSet.add("http://list.jd.com/6728-6747-11898.html");
			urlSet.add("http://list.jd.com/6728-6747-9985.html");
			urlSet.add("http://list.jd.com/6728-12402-12403.html");
			urlSet.add("http://list.jd.com/6728-12402-12404.html");
			urlSet.add("http://list.jd.com/6728-12402-12405.html");
			urlSet.add("http://list.jd.com/6233-6234-6239.html");
			urlSet.add("http://list.jd.com/6233-6234-6240.html");
			urlSet.add("http://list.jd.com/6233-6234-6241.html");
			urlSet.add("http://list.jd.com/6233-6234-6242.html");
			urlSet.add("http://list.jd.com/6233-6234-6243.html");
			urlSet.add("http://list.jd.com/6233-6234-6244.html");
			urlSet.add("http://list.jd.com/6233-6235-6245.html");
			urlSet.add("http://list.jd.com/6233-6235-6246.html");
			urlSet.add("http://list.jd.com/6233-6235-6247.html");
			urlSet.add("http://list.jd.com/6233-6235-6248.html");
			urlSet.add("http://list.jd.com/6233-6235-6249.html");
			urlSet.add("http://list.jd.com/6233-6236-6254.html");
			urlSet.add("http://list.jd.com/6233-6236-6255.html");
			urlSet.add("http://list.jd.com/6233-6237-6257.html");
			urlSet.add("http://list.jd.com/6233-6237-6258.html");
			urlSet.add("http://list.jd.com/6233-6237-6259.html");
			urlSet.add("http://list.jd.com/6233-6253-6261.html");
			urlSet.add("http://list.jd.com/6233-6253-6262.html");
			urlSet.add("http://list.jd.com/6233-6253-6263.html");
			urlSet.add("http://list.jd.com/6233-6260-6265.html");
			urlSet.add("http://list.jd.com/6233-6260-6266.html");
			urlSet.add("http://list.jd.com/6233-6260-6268.html");
			urlSet.add("http://list.jd.com/6233-6260-6269.html");
			urlSet.add("http://list.jd.com/6233-6264-6272.html");
			urlSet.add("http://list.jd.com/6233-6264-6273.html");
			urlSet.add("http://list.jd.com/6233-6264-6274.html");
			urlSet.add("http://list.jd.com/6233-6271-6276.html");
			urlSet.add("http://list.jd.com/6233-6271-7063.html");
			urlSet.add("http://list.jd.com/6233-6271-6277.html");
			urlSet.add("http://list.jd.com/6233-6271-6278.html");
			urlSet.add("http://list.jd.com/6233-6275-6280.html");
			urlSet.add("http://list.jd.com/6233-6275-6282.html");
			urlSet.add("http://list.jd.com/6233-6275-6284.html");
			urlSet.add("http://list.jd.com/6233-6275-6286.html");
			urlSet.add("http://list.jd.com/6233-6279-6281.html");
			urlSet.add("http://list.jd.com/6233-6279-6283.html");
			urlSet.add("http://list.jd.com/6233-6279-6287.html");
			urlSet.add("http://list.jd.com/6233-6289-6292.html");
			urlSet.add("http://list.jd.com/6233-6289-6293.html");
			urlSet.add("http://list.jd.com/6233-6291-6294.html");
			urlSet.add("http://list.jd.com/6233-6291-6296.html");
			urlSet.add("http://list.jd.com/6233-6291-6298.html");
			urlSet.add("http://list.jd.com/6233-6291-6299.html");
			urlSet.add("http://list.jd.com/6233-6291-6300.html");
			urlSet.add("http://list.jd.com/6233-6291-6301.html");
			urlSet.add("http://list.jd.com/6233-6291-6302.html");
			urlSet.add("http://list.jd.com/6233-6291-6303.html");
			urlSet.add("http://list.jd.com/6233-6291-6305.html");
			urlSet.add("http://list.jd.com/6233-6291-6306.html");
			urlSet.add("http://list.jd.com/6233-6291-6308.html");

			typeMap.put("ConfigJdBrandList", urlSet);
		}

		@Override
		public void run() {
			if (running) {
				logger.warn("CreateTaskTimer is working...");
				return;
			}
			long start = System.currentTimeMillis();
			try {
				logger.info("CreateTaskTimer is start...");
				running = true;
				JSONObject argsObject = new JSONObject();
				JSONUtils.put(argsObject, "strategy", getName());
				for (Entry<String, Set<String>> entry : typeMap.entrySet()) {
					List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
					String taskId = UUID.randomUUID().toString();
					JSONUtils.put(argsObject, "bid", taskId);
					String type = entry.getKey();
					for (String url : entry.getValue()) {
						TaskPriorityDto taskDto = createPriorityDto(url, type, argsObject);
						taskList.add(taskDto);
					}
					getTaskPriorityDtoBuffer().addAll(taskList);
					logger.info("Offer task:{},size:{}", type, taskList.size());
				}
			} catch (Exception ex) {
				logger.warn(ExceptionUtils.getStackTrace(ex));
			} finally {
				long cost = System.currentTimeMillis() - start;
				logger.info("CreateTaskTimer is done.cost:{}", cost);
				running = false;
			}
		}
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void handleResult(ResultWritable rWritable) {
		if (ResultWritable.RESULT_SUCCESS != rWritable.getStatus()) {
			return;
		}
		if (rWritable.getType().indexOf("BrandList") > 0) {
			JSONObject gObject = JSONUtils.getJSONObject(rWritable.getResult());
			JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
			JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
			try {
				argsObject.remove("name@client");
				argsObject.remove("target");
				addOthers(rWritable, rsObject, argsObject);
				addNextTasks(rsObject, argsObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void addOthers(ResultWritable rWritable, JSONObject rsObject, JSONObject argsObject) throws JSONException {
		JSONArray dataArray = JSONUtils.get(rsObject, "dataList");
		if (dataArray == null) {
			return;
		}
		int len = dataArray.length();
		List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>(len * 2);
		JSONObject oParamObject = JSONUtils.getJSONObject(argsObject.toString());
		String url = JSONUtils.getString(argsObject, "url");
		JSONUtils.put(oParamObject, "fromUrl", url);
		String productType = rWritable.getType().replace("BrandList", "BrandShop");
		String argsString = oParamObject.toString();
		for (int i = 0; i < len; i++) {
			JSONObject bObject = dataArray.getJSONObject(i);
			JSONObject paramObject = new JSONObject(argsString);
			String brandUrl = JSONUtils.getString(bObject, "brandUrl");
			bObject.remove("brandUrl");
			Iterator<?> it = bObject.keys();
			while (it.hasNext()) {
				String key = it.next().toString();
				JSONUtils.put(paramObject, key, JSONUtils.get(bObject, key));
			}
			TaskPriorityDto taskPriorityDto = createPriorityDto(brandUrl, productType, paramObject);
			dtoList.add(taskPriorityDto);
		}
		getTaskPriorityDtoBuffer().addAll(dtoList);

	}

	private void addNextTasks(JSONObject rsObject, JSONObject argsObject) throws Exception {
		JSONArray nextArray = JSONUtils.get(rsObject, "nextList");
		if (nextArray == null) {
			return;
		}
		String type = JSONUtils.getString(argsObject, "type");
		List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>();
		JSONUtils.put(argsObject, "fromUrl", JSONUtils.getString(argsObject, "url"));
		for (int i = 0; i < nextArray.length(); i++) {
			String nextUrl = nextArray.getString(i);
			TaskPriorityDto taskPriorityDto = createPriorityDto(nextUrl, type, argsObject);
			dtoList.add(taskPriorityDto);
		}
		getTaskPriorityDtoBuffer().addAll(dtoList);
	}

	private TaskPriorityDto createPriorityDto(String url, String type, JSONObject argsObject) {
		String taskId = JSONUtils.getString(argsObject, "bid");
		taskId = taskId == null ? UUID.randomUUID().toString() : taskId;
		TaskPriorityDto taskPriorityDto = new TaskPriorityDto();
		taskPriorityDto.setBatchId(taskId);
		taskPriorityDto.setType(type);
		taskPriorityDto.setUrl(url);
		taskPriorityDto.setLevel(JSONUtils.getInteger(argsObject, "level"));
		taskPriorityDto.setSource(JSONUtils.getString(argsObject, "src"));
		taskPriorityDto.setCreatTime(new Date());
		taskPriorityDto.setUpdateTime(taskPriorityDto.getCreatTime());
		taskPriorityDto.setStatus(TaskConstant.TASK_NEW);
		JSONObject paramObject = JSONUtils.getJSONObject(argsObject.toString());
		paramObject.remove("bid");
		paramObject.remove("type");
		paramObject.remove("url");
		paramObject.remove("level");
		paramObject.remove("src");
		paramObject.remove("ctime");
		if (taskPriorityDto.getLevel() == null) {
			taskPriorityDto.setLevel(0);
		}
		taskPriorityDto.setParams(paramObject.toString());
		return taskPriorityDto;
	}

	private StorageBuffer<TaskPriorityDto> getTaskPriorityDtoBuffer() {
		return (StorageBuffer<TaskPriorityDto>) StorageBufferFactory.getStorageBuffer(TaskPriorityDto.class);
	}

	@Override
	public void close() throws IOException {
		if (this.timer != null) {
			this.timer.cancel();
			this.timer = null;
		}
		logger.info("close " + getName() + " strategy..");
	}
}