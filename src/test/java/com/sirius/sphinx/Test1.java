package com.sirius.sphinx;

import com.sirius.sphinx.protocol.LongRangeFilter;
import com.sirius.sphinx.protocol.MatchItem;
import com.sirius.sphinx.protocol.MatchPage;
import com.sirius.sphinx.protocol.Search;
import com.sirius.sphinx.protocol.Search.MatchMode;
import com.sirius.sphinx.protocol.Sort.SortMode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @project application-sphinx
 * @date 2014-8-8-上午9:42:49
 * @author pippo
 */
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
