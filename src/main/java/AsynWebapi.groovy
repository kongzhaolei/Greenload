import static net.grinder.script.Grinder.grinder
import org.gradle.needle.client.HttpClientFactory
import org.gradle.needle.engine.HttpReqGen
import org.gradle.needle.util.ExcelUtils
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

import static org.awaitility.Awaitility.await

import static java.util.concurrent.TimeUnit.MILLISECONDS
import static org.assertj.core.api.Assertions.assertThat;
import org.codehaus.groovy.reflection.ReflectionUtils;

/**
 * asynchronous webapi
 * @author kongzhaolei
 *
 */


//@Repeat(200)
@RunWith(GrinderRunner)
class AsynWebapi {
	public static GTest test1
	public static GTest test2
	private String testset = "main"
	private String submit = "t005"
	private String query = "t006"
	private String currentwarn = "t007"

	public ExcelUtils edu
	public Iterator<Map<String, String>> datamap
	public Iterator<Object[]> requestfiles

	@BeforeProcess
	public static void beforeProcess() {
		test1 = new GTest(1, "10.1.3.151")

	}

	@BeforeThread
	public void beforeThread() {
		test1.record(this, "test2")
		grinder.statistics.delayReports=true;
	}


	@Before
	public void before() {
		try {
			InputStream bis = ReflectionUtils.getCallingClass(0).getResourceAsStream("/webapi.xls");
			edu = new ExcelUtils(bis);
			edu.setWorkSheet("Input");
			datamap = edu.getCaseSet(testset);
			requestfiles = HttpReqGen.preReqGen(datamap);
		} catch (Exception e) {
			e.printStackTrace()
		}
	}

	/*
	 * 异步校验，报表
	 */
	@Ignore
	@Test
	public void test1() {
		while (requestfiles.hasNext()) {
			Object[] rf = requestfiles.next()
			if (submit.equals(rf[0])) {
				HttpClientFactory.invokeServiceMethod(rf[1],rf[2],rf[3],rf[4])
			};
			if (query.equals(rf[0])) {
				await().atMost(120000, MILLISECONDS).until {
					JsonUtils.getTaskStatusCode(HttpClientFactory
							.invokeServiceMethod(rf[1],rf[2],rf[3],rf[4]), "code") == "done" }
			}
		}
	}

	/*
	 * 	告警日志
	 */
	@Test
	public void test2() {
		int n = 0
		while (requestfiles.hasNext()) {
			Object[] rf = requestfiles.next()
			if (currentwarn.equals(rf[0])) {
				while (n < 100) {
					String res = HttpClientFactory.invokeServiceMethod(rf[1],rf[2],rf[3],rf[4])
					int bindex = res.indexOf("added")
					int eindex = res.indexOf("updated")
					n = n + JsonUtils.KeyFrequency(res.substring(bindex, eindex), "warnID")
					//println n
				}
			}
		}
	}
}