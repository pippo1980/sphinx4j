##sphinx4j
========

A java driver for sphinx. 
It's based on [xSocket](http://xsocket.sourceforge.net/) and [commons-pool2](http://commons.apache.org/proper/commons-pool2/).
It can connet to sphinx serchd with long connection, and the long connection managed by a connection pool.
It also provide clearness sphinx protocol model whitch is mush better then default Implementation.

##Requirement
You must have jdk1.6 or higher and maven3.0 orhigher.

##Configuration By Spring
======================  
  Simple
      <bean class="com.sirius.sphinx.XSphinxClient">
        <constructor-arg index="0" name="host" value="127.0.0.1"/>
        <constructor-arg index="1" name="port" value="9312"/>
      </bean>

  customization connection pool
  
      <bean class="com.sirius.sphinx.XSphinxClient">
        <!-- server address -->
        <constructor-arg index="0" name="host" value="127.0.0.1"/>
        <constructor-arg index="1" name="port" value="9312"/>
        <!-- customization connection pool -->
        <property name="maxIdle" value="2"/>
        <property name="maxTotal" value="100"/>
        <property name="testOnCreate" value="true"/>
        <property name="testOnBorrow" value="true"/>
        <property name="testOnReturn" value="true"/>
        <property name="testWhileIdle" value="true"/>
        <property name="blockWhenExhausted" value="false"/>
      </bean>
      
##Example
        public class Test1 {
        
        	static XSphinxClusterClient client;
        
        	@Before
        	public void init() throws IOException {
        		if (client == null) {
        			client = new XSphinxClusterClient("192.168.230.129:9312");
        		}
        	}
        
        	@Test
        	public void search() throws IOException, InterruptedException {
        		/* match the key word */
        		Search search = new Search("互联网数据", "test2");
        		search.mode=MatchMode.PHRASE;
        		search.offset = 0;
        		search.limit = 20;
        		search.group.max_matches = 20;
        		/* filt the attribute */
        		search.filters.add(new LongRangeFilter("left_p", 29692L, 33793L));
        		search.filters.add(new LongRangeFilter("right_p", 29692L, 33793L));
        		/* order by the attribute */
        		search.sort.attribute = "access_num";
        		search.sort.mode = SortMode.ATTR_ASC;
        
        		/* execute search */
        		MatchPage page = client.execute(search);
        		int total = page.found;
        		Assert.assertTrue(total > 0);
        		System.out.println(String.format("total=%s", total));
        		List<MatchItem> items = page.items;
        		for (MatchItem item : items) {
        			System.out.println(String.format("item=%s", item));
        		}
        	}
        
        	//@Test
        	public void page() throws IOException, InterruptedException {
        		int start = 1;
        		int limit = 10;
        		int total = limit + 1;
        
        		/* loop the records with start and limit */
        		while (start * limit < total) {
        			long begin = System.currentTimeMillis();
        			/* match the key word */
        			Search search = new Search("电信", "test2");
        			search.offset = start;
        			search.limit = limit;
        			search.group.max_matches = start * limit;
        			/* filt the attribute */
        			search.filters.add(new LongRangeFilter("left_p", 29692L, 33793L));
        			search.filters.add(new LongRangeFilter("right_p", 29692L, 33793L));
        			/* order by the attribute */
        			search.sort.attribute = "access_num";
        			search.sort.mode = SortMode.ATTR_ASC;
        
        			MatchPage page = client.execute(search);
        
        			List<MatchItem> items = page.items;
        			for (MatchItem item : items) {
        				System.out.println(item);
        			}
        
        			System.out.println("#############cost#" + (System.currentTimeMillis() - begin));
        
        			/* the next page setting */
        			total = page.found;
        			start = start * limit + 1;
        		}
        	}
        	
        	//@Test
        	public void current_page() throws InterruptedException, ExecutionException{
        		final Collection<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        		for (int i = 0; i < 100; i++) {
        			tasks.add(new Callable<Object>() {
        
        				@Override
        				public Object call() throws Exception {
        					page();
        					return null;
        				}
        				
        			});
        		}
        		
        		ExecutorService executorService= 	Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        		List<Future<Object>> futures = executorService.invokeAll(tasks);
        		for (Future<Object> future : futures) {
        			future.get();
        		}
        		executorService.shutdownNow();
        	}
        }

