import org.gradle.needle.client.TCPDataClientHandler
import static net.grinder.script.Grinder.grinder
import org.gradle.needle.client.DataClient
import org.gradle.needle.client.TCPDataClient
import org.gradle.needle.util.DBUtils
import org.gradle.needle.util.VTimer
import org.gradle.needle.util.VerifyUtils
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
import net.grinder.scriptengine.groovy.junit.annotation.RunRate

import static org.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.MILLISECONDS
import org.codehaus.groovy.reflection.ReflectionUtils;

/**
 * socket IO tasks in an uniform way
 * @author kongzhaolei
 *
 */
//@Repeat(100)
@RunWith(GrinderRunner)
class DataDisp {
	public static GTest test
	private static String host = "10.10.7.160";
	private static int port = 8090;
	// private String url = "10.1.3.152:5432";
	private String instance = "v5";
	private String username = "postgres";
	private String password = "postgres";
	DataClient dc  = new TCPDataClient();
	

	@BeforeProcess
	public static void beforeProcess() {
		test = new GTest(1, "10.1.3.152")
		VTimer.timerStart()
		new TCPDataClient(host, port).TcpConnect()
	}

	@BeforeThread
	public void beforeThread() {
		test.record(this, "test6")
		grinder.statistics.delayReports = true;
	}

	//5分钟
	@Ignore
	@RunRate(10)
	@Test
	public void test1() {
		TCPDataClient.sendDevTenData()
		//tcp通信校验
		await().atMost(5000, MILLISECONDS).until { TCPDataClientHandler.getchannelRead() == "(ok)"}
		//Thread.sleep(600000)
	}

	//一分钟
	@Ignore
	@RunRate(50)
	@Test
	public void test2() {
		TCPDataClient.sendDevOneData()
		//tcp通信校验
		await().atMost(5000, MILLISECONDS).until { TCPDataClientHandler.getchannelRead() == "(ok)"}
		//Thread.sleep(60000)

//		//数据库校验
//		await().atMost(120000, MILLISECONDS).untilAsserted {
//			new VerifyUtils(url, instance, username, password)
//					.assertTbaleChanges("public.onedata")
//		};
	}
	
	//历史瞬态
	@Ignore
	@RunRate(100)
	@Test
	public void test3() {
		TCPDataClient.sendDevRealTimeData()
		//tcp通信校验
		await().atMost(5000, MILLISECONDS).until { TCPDataClientHandler.getchannelRead() == "(ok)"}
	}
	
	//告警日志
	@Ignore
	@RunRate(10)
	@Test
	public void test4() {
		dc.sendDevWarnLog()
		//tcp通信校验
		await().atMost(5000, MILLISECONDS).until { TCPDataClientHandler.getchannelRead() == "(ok)"}
	}
	
	//沉积数据，一分钟
	@Ignore
	//@RunRate(10)
	@Test
	public void test5() {
		println TCPDataClient.sendDevSedimentOneData()
		//tcp通信校验
		await().atMost(60000, MILLISECONDS).until { TCPDataClientHandler.getchannelRead() == "(ok)"}
	}
	
	//沉积数据，历史瞬态
	//@Ignore
	//@RunRate(10)
	@Test
	public void test6() {
		TCPDataClient.sendDevSedimentRealData()
		//tcp通信校验
		await().atMost(60000, MILLISECONDS).until { TCPDataClientHandler.getchannelRead() == "(ok)"}
	}
}
