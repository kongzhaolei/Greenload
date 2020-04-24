import static net.grinder.script.Grinder.grinder
import org.gradle.needle.client.HttpClientFactory
import org.gradle.needle.util.JsonUtils
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

import net.grinder.script.GTest
import net.grinder.script.Grinder
import net.grinder.scriptengine.groovy.junit.GrinderRunner
import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess
import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread
import net.grinder.scriptengine.groovy.junit.annotation.Repeat

import static org.assertj.core.api.Assertions.assertThat;
import org.codehaus.groovy.reflection.ReflectionUtils;

/**
 * API in an uniform way
 * @author kongzhaolei
 *
 */
//@Repeat(200)
@RunWith(GrinderRunner)
class DataServiceAPI {

	public static GTest test1
	private static String url1 = "http://10.12.7.160:9999/dataserver/app/totalDeviationStatistics";
	private static String body1 = "{\"date\":\"2020-04-06 00:30\",\"wfIds\":\"[640305,640328]\"}";


	@BeforeProcess
	public static void beforeProcess() {
		test1 = new GTest(1, "10.12.7.160")
	}

	@BeforeThread
	public void beforeThread() {
		test1.record(this, "test")
		grinder.statistics.delayReports=true;
	}

	@Before
	public void before() {

	}

	/*
	 * 根据需要选择不同的断言方式
	 * 支持流式断言
	 */
	@Test
	public void test() {
		String res = HttpClientFactory.httpPostJson(url1,body1)
		assertThat(200).isEqualTo(HttpClientFactory.getStatusCode())
		//assertThat(res).containsSequence("{","ModelData")
		//assertThat(72).isEqualTo(JsonUtils.keyValueSum(res, "StatusWfCount"))     //json关键字合计值断言
		//assertThat(2).isEqualTo(JsonUtils.KeyFrequency(res, "140802"))    //json关键字频次断言
	}
}