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
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class TmallBrandShopStrategy implements ResultStrategy, Closeable {
	private static Logger logger = LoggerFactory.getLogger(TmallBrandShopStrategy.class);
	private static volatile boolean running = false;
	private Timer timer;

	public TmallBrandShopStrategy() {
		CreateTaskTimer task = new CreateTaskTimer();
		this.timer = new Timer("CreateTaskTimer");
		this.timer.schedule(task, 60 * 1000, 240 * 60 * 60 * 1000);
	}

	private class CreateTaskTimer extends TimerTask {
		private Map<String, Set<String>> typeMap;

		public CreateTaskTimer() {
			typeMap = new HashMap<String, Set<String>>();
			Set<String> urlSet = new HashSet<String>();
			urlSet.add("http://brand.tmall.com/brandMap.htm?spm=a3200.2192449.0.0.6OiqFL");
			for (int i = 65; i <= 90; i++) {
				char word = (char) i;
				urlSet.add("http://brand.tmall.com/azIndexInside.htm?firstLetter=" + word);
			}
			typeMap.put("ConfigTmallBrandList", urlSet);
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=100&categoryId=50025135&etgId=59");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=100&categoryId=50025174&etgId=58");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=100&categoryId=50023887&etgId=60");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=100&categoryId=50025983&etgId=61");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=109&categoryId=50025829&etgId=64");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=109&categoryId=50026637&etgId=63");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=109&categoryId=51052003&etgId=65");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=109&categoryId=51042006&etgId=66");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=109&categoryId=50072916&etgId=188");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=109&categoryId=50095658&etgId=68");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=111&categoryId=50108176&etgId=190");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=111&categoryId=50026474&etgId=74");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=111&categoryId=50026478&etgId=78");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=111&categoryId=50026461&etgId=80");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=111&categoryId=50023064&etgId=82");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50026502&etgId=70");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50026391&etgId=69");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50026506&etgId=73");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50026505&etgId=71");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50026426&etgId=72");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50026393&etgId=187");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=101&categoryId=50043479&etgId=138");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=110&categoryId=50020894&etgId=83");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=110&categoryId=50020909&etgId=84");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=110&categoryId=50043669&etgId=194");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=110&categoryId=50022787&etgId=195");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024400&etgId=99");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024399&etgId=100");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024401&etgId=101");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50047403&etgId=103");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50047396&etgId=110");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024407&etgId=102");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024406&etgId=104");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50043917&etgId=105");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50099232&etgId=106");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024410&etgId=107");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50094901&etgId=108");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=108&categoryId=50024411&etgId=109");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=103&categoryId=50900004&etgId=94");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=103&categoryId=50892008&etgId=95");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=103&categoryId=50902003&etgId=96");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=103&categoryId=50886005&etgId=97");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=103&categoryId=50894004&etgId=98");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030787&etgId=111");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50067162&etgId=112");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50067174&etgId=113");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50051691&etgId=114");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50097362&etgId=115");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030207&etgId=116");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030215&etgId=117");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030223&etgId=118");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030221&etgId=119");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030212&etgId=120");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030213&etgId=121");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030204&etgId=122");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030220&etgId=124");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50030203&etgId=125");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50069204&etgId=126");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50069234&etgId=127");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=102&categoryId=50067917&etgId=128");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50024531&etgId=129");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50068087&etgId=130");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50072436&etgId=133");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50068090&etgId=131");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50036568&etgId=134");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50067939&etgId=132");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50036640&etgId=135");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50034368&etgId=136");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=104&categoryId=50072285&etgId=137");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50100151&etgId=174");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50072046&etgId=168");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50072044&etgId=169");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50100152&etgId=175");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50100153&etgId=176");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50100154&etgId=177");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50099890&etgId=178");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50074901&etgId=171");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50099887&etgId=179");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50100167&etgId=180");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50100166&etgId=181");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50099298&etgId=182");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50074804&etgId=170");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50074917&etgId=172");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=107&categoryId=50074933&etgId=173");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=105&categoryId=50025137&etgId=139");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=105&categoryId=50023647&etgId=148");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=105&categoryId=50029253&etgId=152");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=105&categoryId=50036697&etgId=147");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=105&categoryId=50024803&etgId=191");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=105&categoryId=50033500&etgId=193");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=106&categoryId=50106135&etgId=155");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=106&categoryId=50029838&etgId=157");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=106&categoryId=50029836&etgId=158");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=106&categoryId=50029852&etgId=159");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=106&categoryId=50029840&etgId=162");
			urlSet.add("http://brand.tmall.com/categoryIndex.htm?industryId=106&categoryId=50044102&etgId=163");
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
				//ignore old task
				typeMap.clear();
				for (Entry<String, Set<String>> entry : typeMap.entrySet()) {
					List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
					String taskId = UUID.randomUUID().toString();
					JSONUtils.put(argsObject, "bid", taskId);
					JSONUtils.put(argsObject, "retry", 0);
					String type = entry.getKey();
					for (String url : entry.getValue()) {
						TaskPriorityDto taskDto = createPriorityDto(url, type, argsObject);
						taskList.add(taskDto);
					}
					getTaskPriorityDtoBuffer().addAll(taskList);
					logger.info("Offer task:{},size:{}", type, taskList.size());
				}
				TaskPriorityService taskPriorityService = SpringBeanUtils.getBean(TaskPriorityService.class);

				//进口食品，1206
				Integer[] brandIdArray = new Integer[]{111512,3422124,76097500,683798426,718482514,4536992,7042789,6057170,243262366,4536999,36629018,11251751,322554631,78005572,111500,3503333,74604174,110071,110072,255754159,111508,41396507,9934015,110074,362226542,243164839,110066,33795268,216552520,9433123,20038,107647738,74330945,17301504,7983608,92410668,299596878,4539516,786120554,3738153,4536184,122144911,591574399,3320243,4536976,7474789,51462495,131286271,78956277,7393939,11732619,279900558,4538948,10452594,3333564,22467434,228204056,249344958,7930758,542266377,9540391,33716,4538260,217654204,280064842,300360126,10453463,152974195,8278133,7529950,286360413,7906370,35616568,101219656,3241236,103277074,76999432,6700297,8224326,309338418,153057356,5532244,184008616,213786444,3422704,10724435,3219783,707170733,57295,828432933,118663302,8103320,6895233,3917178,187130707,201582773,3335129,54002234,8590023,10140895,84703,104519737,7634624,258648876,20080516,118692231,27043680,596794513,4539760,121740228,615224520,98182839,3751374,144076802,606456040,675618692,25800371,138234744,32648721,9041130,7080991,81636461,59274536,8124813,84105527,282988160,68000482,4060079,137887959,12916066,54437723,366252491,132425778,3336237,86180791,16458815,6817726,203268132,19979321,200300349,240460186,107318579,7585318,8420015,25296856,215462494,665592004,3287672,7544095,11498597,365054210,7971414,46693129,293250470,664030015,142273576,6128845,27714,3825942,139960759,619484009,530438193,17512499,94958271,74626407,68421736,8441379,325786043,3358056,8626000,15415442,7290694,16334037,82885746,25320736,583608085,204250887,139258,21912640,15015411,204074749,93928454,664496020,3951656,619254168,751890150,4533616,6778423,18756822,175374560,361490400,114151517,22070740,70614341,53657673,10527220,4533628,7879469,4533829,7045146,74735845,25466909,119615,8039947,4535635,133723996,6811404,17370867,31848584,186476856,543254204,27354809,69385267,121721938,90841573,492916773,7808611,6702871,256120324,8049223,265498028,30703004,119062,6088054,4535620,9391078,119066,73010968,96916718,4535629,8575827,3984958,6759140,241430132,156424147,177264337,559174849,21553551,3352809,46837762,714996592,277462309,492290849,541034248,8038129,4532355,5827242,17325255,94580,94583,6819087,108290346,4535615,51612340,141844360,98508189,451720458,7833769,4535618,230136819,191768891,247216307,227366783,119080,64196372,110079,6040977,247150374,130512999,718254209,4537662,9548637,88011067,119855,5415920,3230013,4269924,4535609,6817947,206418566,7609754,155793051,7444256,6757959,153452553,3316522,8228055,588230350,64449123,322154818,232286751,119072,66615,7427519,27885342,8725116,119079,110457,309406323,194582918,4503419,95475007,7303413,6850730,20000948,84473,14200873,110459,38743,126177941,132911907,9254828,20290944,656962398,56638,5049513,8193255,121204875,110460,54198669,6850930,587770567,24827279,7227013,98164800,110462,49922182,158225540,11972125,8080580,132094217,3272716,49960593,95321119,248562165,9797872,9698333,86184724,140326947,9604844,235456820,147696666,4536305,212408420,6709933,723908357,232908634,91176967,60418984,3529486,154109149,7349682,6797010,30416005,4366397,16304553,6795247,122387230,206279000,662442259,170178948,721952211,3524174,9790953,3982506,18063069,3815198,110441,110442,110445,7436043,26455189,4537038,125845115,11714362,74551491,366590645,67107964,7648981,27879732,10651874,97815915,644784170,3328080,242190607,110418,11364163,103573005,82680404,110416,22342529,110415,92507698,110413,7871390,152788134,3277100,25407510,44808,3744084,4536557,7012827,148547497,10738414,492644496,7752154,7259025,241438600,142287484,7326664,142239218,3790546,9129198,124854,306272584,7241808,11827,292326181,8046059,4016675,42624577,612898206,7312239,3284238,4533284,24557059,266768132,22066496,323302271,257638807,4534218,380972536,131301771,3868101,7342536,7076820,125251,4537006,92541,92543,548502459,662578114,274838064,14323892,4534622,135385360,22353190,39887589,134103785,4229814,10871455,4537002,8986296,32750309,92538,376998276,778772889,7612611,7293886,148773685,175416986,593228176,4537400,118024463,4051258,8740641,123412612,121865378,4536321,3346688,8067784,333898904,127392494,3986854,60972289,49278058,10069187,595016096,139041082,6777710,4534806,7512197,12188780,219144225,215924248,112642912,66116323,12962520,559428814,144019167,30417,7037893,254398165,305866866,191108424,6789296,7705830,69269732,4132700,6491780,4540695,57020,36394924,36213747,9381299,3512737,715254050,603988903,8970905,4534618,13552626,119225411,115912196,7877873,6325603,6720359,195272165,40749964,65580165,3371061,111365,11242321,111367,88521941,230722431,292738683,107037750,4535992,43292502,7746177,19821958,100255622,93848014,338712047,3979239,7625358,8058972,7076326,621682448,46669444,588390738,129379485,11554304,52914106,3231764,56730107,109164,188736976,132328517,22247811,4183199,9256733,245616477,174922928,6769075,75808993,6761319,24905750,62764170,6861096,20842911,56670,649414120,223588072,131964571,111356,31350380,257498120,3233257,9187525,111359,6742212,111358,6297380,92564431,3917822,58184073,3568189,206812229,7995915,6738675,4214900,8053744,3232752,119837986,9426570,111360,111361,341406960,13610545,57518,8005649,4424871,611468464,374128461,3734614,22167107,40872107,7183767,8104442,56663,76451710,9814232,6801575,15761830,6735462,7625988,11881929,137887,308002652,51020015,7061599,105804,216570006,7079612,3459447,300534803,33483,33482,11498417,33481,33480,7962590,119538,3670389,147280915,3495606,368770350,233130402,8930129,556692221,9081320,8648602,150191830,30526701,65613782,39655781,232594802,119543,120421039,119542,746430201,10272519,3228163,44261578,211522919,8645843,11021721,554076409,147798992,8690881,156067577,57367,112890385,76042714,4539417,4540495,4417988,26567436,108639387,117564022,25999160,3266761,90952622,62610905,11917216,156460959,4070094,4539429,239630321,3229833,374322542,4341264,7043677,52725390,14597813,8243941,7702122,319498065,33477,33478,4142891,15854428,8380310,3227278,599844834,3293454,29408,608588726,567572218,317370640,4101168,10741694,136282931,14569271,7337878,122049492,203882737,43225899,30558,246068021,4539449,52070483,617150123,16053381,54972710,157786198,89214035,656722046,257488506,56895,53857635,559402824,21663,50846732,4539015,66557,66554,6752076,307100063,4100405,8418656,30462431,19009358,8551740,156205354,3561443,3274238,96137669,30573133,378744695,4539451,721678113,14524936,51260298,3231201,197870365,69553971,4540208,141275800,243462758,73483903,58698794,377642457,8180828,10355120,179626482,52914076,9875359,6889360,187650229,120970533,110912,687280197,238130595,110913,828998026,130639009,110910,201118392,129850,9354716,110915,26113244,9966263,216618235,107547,9166597,7724367,25590188,19980377,11331650,13639472,152368641,270178104,8702343,4537745,4461844,4005853,3694456,7510530,7041362,659312238,155147097,600524091,83777901,7588823,134745355,650782749,7055307,7028684,172744493,97097173,192526028,118550485,37435222,137867649,28263932,3788315,617072870,7529812,7011612,4194763,3599502,289188237,6905051,26594019,66589,66588,66590,66591,93370,8827039,14775737,39589,3471026,4534137,286030388,6344276,650118016,374980073,8558371,187690126,7121617,4266264,26439913,663024085,18346753,10643788,4002477,7874032,117777609,3283425,3428996,6537007,271820556,151896892,742594762,3255485,12106786,4247147,115231050,103472,38240259,726060111,3227978,364626958,119588,199152254,8839688,121130887,8358319,6775656,50641283,3855740,10123380,559376774,3227983,287156237,12509254,28792230,116354671,98673226,3318554,3232334,13548797,24263690,3232338,16068324,20330,82747828,4539085,38860658,12476582,10010914,8767913,6873846,36976779,146917374,4504555,6871081,133424078,11330189,56849,559376796,6742606,25465796,3328014,3235392,9532377,4533324,134585722,7469769,4264379,109669745,656550695,216798505,317988527,20328,651616332,13929072,3232350,3720662,6741733,17902605,102557649,10472595,64088949,47571,137508973,107544,8685226,107545,5413560,152402120,19756596,653084733,105445120,207664350,115509760,20316,6827068,3721262,225832815,3564346,110588453,204752831,7097649,139065669,4220523,153899392,4536417,4537166,96161979,4564676,252204112,96911897,117217660,9039401,8101604,50468875,12141107,201580599,3571128,375310228,110761,33033588,313114841,56742,3584437,88288468,9396449,4540190,22583314,3324608,78435250,37582,705012725,3356048,8453350,8237350,128588724,7369515,41005559,263842776,9288393,43144936,283612222,29205596,32160947,6306825,7498937,3483540,216616331,29544272,4491663,4533800,10041156,7459239,4535579,15518510,5416895,149963213,8203889,74670811,96025562,44902751,4013410,29415975,95277052,3250005,197676596,6614362,22596408,69962547,49400041,663772007,333904640,131497134,594184111,234704113,183672265,15369950,7527727,310358648,7684484,137692901,105343,29295,14206546,6845391,6728647,55574478,177896382,7643976,134140,17376083,204010941,219178987,4536459,84779246,7461278,7671424,9212371,6481757,6852943,7553710,215104954,3271526,129586402,12930374,3604057,4536640,8746224,14510504,134836616,775078191,3597021,287612944,138101365,118948551,56718,115692432,3789548,45605911,608354359,47797377,211762978,111333541,49362093,34294528,3990321,4536465,137823913,605788893,3864639,543204160,7611243,3563551,139342346,40460756,7643551,11371277,285038789,5771128,54381,3352112,612766565,7418525,96486239,45489901,772306569,553536677,108579,111485,144279835,18028782,18703815,6805567,89955972,155485740,6133335,645052854,7909618,3692885,8375125,44041237,4536490,4540103,6965217,4540104,4536492,111495,111494,4537358,100531456,309718999,265410110,105807216,111496,3218590,11409065,82632131,3428314,30521,359976888,6886862,147570559,3229193,4540117,4536485,188444440,16743421,132949809,3278190,11801191,107802,8908651,46437724,15172423,157803237,18157638,84178123,4230060,178964970,137330879,117549702,6156967,6466769,603816770,25723974,50051458,59955386,51824971,127129759,39482539,28944961,7233422,181350053,602690672,7244613,546570549,551664404,225300406,621348070,7280906,6811883,7410842,71052504,3228729,31068662,157728871,21437275,115273248,30316800,6783056,194616969,9246941,3945185,121118663,7351688,19569222,220256366,8624004,28260718,11505292,7136408,3936272,7467956,559434802,9352155,18810925,120932,83767967,7504595,730170750,7365268,535786745,8046107,652498457,36531865,9426695,109776845,7307074,25641438,7016234,680314022,11255011,78957908,155313303,230630760,18524685,7104454,29220533,89251511,6109742,39355795,3250854,26413021,553332899,34032112,4513648,287048435,3372444,40192008,20160976};
				List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
				String taskId = UUID.randomUUID().toString();
				String type = "ConfigTmallBrandShop";
				JSONUtils.put(argsObject, "bid", taskId);
				JSONUtils.put(argsObject, "retry", 0);
				for(Integer brandId:brandIdArray){
					String url = "http://list.tmall.com/search_product.htm?brand="+brandId+"&sort=s&style=w#J_Filter";
					TaskPriorityDto taskDto = createPriorityDto(url, type, argsObject);
					taskList.add(taskDto);
				}
				taskPriorityService.batchInsert(taskList);
				logger.info("insert new task into db.type:"+type+",count:"+taskList.size());
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
		JSONObject gObject = JSONUtils.getJSONObject(rWritable.getResult());
		JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
		JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
		argsObject.remove("name@client");
		argsObject.remove("target");
		try {
			addNextTasks(rsObject, argsObject);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (rWritable.getType().indexOf("BrandList") > 0) {
			try {
				addOthers(rWritable, rsObject, argsObject);
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