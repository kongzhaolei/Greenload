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

import static org.assertj.core.api.Assertions.assertThat;
import org.codehaus.groovy.reflection.ReflectionUtils;
import static org.awaitility.Awaitility.await

import static java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * webapi in an uniform way
 * @author kongzhaolei
 *
 */
//@Repeat(200)
@RunWith(GrinderRunner)
class GMWebapi {

	public static GTest test1
	private String testset = "main"
	private String testcase = "t001"

	public ExcelUtils edu
	public Iterator<Map<String, String>> datamap
	public Iterator<Object[]> requestfiles

	@BeforeProcess
	public static void beforeProcess() {
		test1 = new GTest(1, "10.10.0.111")
	}

	@BeforeThread
	public void beforeThread() {
		test1.record(this, "test")
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
	/***
	@Before
	public void before(){
		try{
			InputStream bis = ReflectionUtils.getCallingClass(0).getResourceAsStream("/webapi.txt");
		
		}catch(Exception e){
			e.printStackTrace()
		
	}
***/
	/*
	 * 根据需要选择不同的断言方式
	 * 支持流式断言
	 */
	@Test
	public void test() {
		while (requestfiles.hasNext()) {
			Object[] rf = requestfiles.next()
			if (testcase.equals(rf[0])) {
				String res = HttpClientFactory.invokeServiceMethod(rf[1],rf[2],rf[3],rf[4])
				//println res
				if (testcase.equals(rf[0])) {
					await().atMost(120000, MILLISECONDS).until {
					HttpClientFactory.getStatusCode() == 200			
					}
			
				}
				//assertThat(200).isEqualTo(HttpClientFactory.getStatusCode())
				//assertThat(res).containsSequence("{","OK")
				//assertThat(72).isEqualTo(JsonUtils.keyValueSum(res, "StatusWfCount"))     //json关键字合计值断言
				//assertThat(2).isEqualTo(JsonUtils.KeyFrequency(res, "140802"))    //json关键字频次断言
			}
		}
	}
}