//package SpicePart_A;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.SynchronousQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import org.apache.log4j.Logger;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.remote.DesiredCapabilities;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//
//
//import Wrappers.CommonFunctions;
//import Wrappers.Utilities;
//import io.github.bonigarcia.wdm.WebDriverManager;
//public class SpicePartB_upload_V3 {
//	public static Map<String, String> dataMap = new HashMap<String, String>();
//	static String project_path = System.getProperty("user. dir");
//	public static WebDriver driver;
//	public static WebDriverWait wait;
//	public String nodeUrl;
//	//public static Logger logger = Logger.getLogger(spicePartA.class);
//	public static String serviceName = "Spice_PART_A";
//	int corePoolSize = 100;
//	int maxPoolSize = 1000;
//	int keepAliveTime = 600;
//	BlockingQueue<Runnable> workQueue = new SynchronousQueue<Runnable>();
//	ThreadPoolExecutor pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
//			workQueue);
//	CommonFunctions commonFunctions = new CommonFunctions();
//	String sharedFolderName = "Spice_Part_B_Upload";
//
//
//
//@Test
//	public void run(WebDriver driver,Map<String, String> dataMap) {
//		WebDriverManager.chromedriver().setup();
//		driver = new ChromeDriver();
//		driver.manage().window().maximize();
//		 dataMap.put("totalCount", "5");
//			dataMap.put("uploadTickets" + "i" + "agentEmail", "john.doe@example.com");
//			dataMap.put("uploadTickets" + "i" + "mcaUserName", "sathya@vakilsearch.com");
//			dataMap.put("uploadTickets" + "i" + "mcaPassword", "Sathya_!23");
//			dataMap.put("uploadTickets" + "i" + "srnNumber", "AA6517952");
//			dataMap.put("uploadTickets" + "i" + "serviceId", "32");
//
//		try {
//
//			driver.get("https://www.mca.gov.in");
//
//			wait = new WebDriverWait(driver, 300);
//			spicePartB_UploadDetails.main(dataMap, driver, wait);
//
//		} catch (Exception ex) {
//
//		}
//
//	}
//
//}

package SpicePart_A;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Set;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;
import org.testng.annotations.Test;

import Wrappers.CommonFunctions;
import Wrappers.Utilities;
import io.github.bonigarcia.wdm.WebDriverManager;

//@Path("/spicePartBUpload")
public class SpicePartB_upload_V3 {
	public static WebDriverWait wait;

	CommonFunctions commonFunctions = new CommonFunctions();

	@Test
	public void run() throws InterruptedException, FindFailed {
		WebDriverManager.edgedriver().setup();
		WebDriver driver = new EdgeDriver();
		driver.get(
				"https://chromewebstore.google.com/detail/requestly-intercept-modif/mdnleldcmiljblolnjhpnblkcekpdkpa");
		WebDriverWait wait = new WebDriverWait(driver, 100);
		driver.manage().window().maximize();
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Add to Chrome']"))).click();
//		Screen screen = new Screen();
//		Pattern allowPopup = new Pattern(
//				"\\\\14.140.167.188\\Vakilsearch\\Automation_Documents\\Allow_Button_Edge.png");
//		Pattern addExtension = new Pattern(
//				"\\\\14.140.167.188\\Vakilsearch\\Automation_Documents\\Add_extension_Button.png");
//		Pattern QAEngineer = new Pattern("\\\\14.140.167.188\\Vakilsearch\\Automation_Documents\\QAEngineer.png");
//		Pattern CancelRequest = new Pattern(
//				"\\\\14.140.167.188\\Vakilsearch\\Automation_Documents\\Cancel_request.png");
//		Thread.sleep(2000);
//		screen.wait(allowPopup, 20);
//		screen.click(allowPopup);
//		Thread.sleep(2000);
//		screen.wait(addExtension, 20);
//		screen.click(addExtension);
//		Thread.sleep(6000);
//		Set<String> windowHandles = driver.getWindowHandles();
//		String[] handles = windowHandles.toArray(new String[0]);
//		driver.switchTo().window(handles[1]);
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Skip for now ']")));
//		commonFunctions.clickElementByJS_WebElement(driver,
//				driver.findElement(By.xpath("//span[text()='Skip for now ']")));
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-autocomplete='list']"))).click();
//		Thread.sleep(2000);
//		screen.wait(QAEngineer, 20);
//		screen.click(QAEngineer);
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Proceed']"))).click();
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Skip for now ']"))).click();
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Create new Rule']"))).click();
//		Thread.sleep(2000);
//		screen.wait(CancelRequest, 20);
//		screen.click(CancelRequest);
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Create Rule']"))).click();
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@aria-label='Skip']"))).click();
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='example']")));
//		driver.findElement(By.xpath("//input[@placeholder='example']"))
//				.sendKeys("https://cdn.jsdelivr.net/npm/disable-devtool");
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Add a new condition']"))).click();
//		Thread.sleep(2000);
//		driver.findElement(By.xpath("(//input[@placeholder='example'])[2]"))
//				.sendKeys("https://www.mca.gov.in/etc.clientlibs/mca/clientlibs/clientlib-devtool.js");
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Add a new condition']"))).click();
//		Thread.sleep(2000);
//		driver.findElement(By.xpath("(//input[@placeholder='example'])[3]"))
//				.sendKeys("https://www.mca.gov.in/content/dam/csr/icons/captcha.jpg");
//		Thread.sleep(2000);
//		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='*Save rule']"))).click();
//		driver.close();
//		driver.switchTo().window(handles[0]);
//		driver.get("https://www.mca.gov.in/content/mca/global/en/foportal/fologin.html");
		try {
//			Robot robot = new Robot();
//			robot.keyPress(KeyEvent.VK_CONTROL);
//			robot.keyPress(KeyEvent.VK_MINUS);
//			robot.keyRelease(KeyEvent.VK_MINUS);
//			robot.keyPress(KeyEvent.VK_MINUS);
//			robot.keyRelease(KeyEvent.VK_MINUS);
//			robot.keyPress(KeyEvent.VK_MINUS);
//			robot.keyRelease(KeyEvent.VK_MINUS);
//			robot.keyPress(KeyEvent.VK_MINUS);
//			robot.keyRelease(KeyEvent.VK_MINUS);
//			robot.keyRelease(KeyEvent.VK_CONTROL);
//			wait = new WebDriverWait(driver, 100);

			spicePartB_UploadDetails spice = new spicePartB_UploadDetails();
			spice.main(driver, wait);
			// }
		} catch (Exception ex) {
			Utilities.errorParsingLogger(ex.toString(), "Spice_Part_A");
			ex.printStackTrace();
		}
		
		

        }
	}

