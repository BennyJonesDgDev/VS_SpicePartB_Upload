package Wrappers;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.CapabilityType.ForSeleniumServer;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

@SuppressWarnings("deprecation")
public class CommonFunctions {
	final static Logger logger = Logger.getLogger(CommonFunctions.class.getName());
	
	public WebDriver driver;
	public ArrayList<String> ManualActionsMessage = new ArrayList<String>();
	private ThreadLocal<UnexpectedAlertBehaviour> alertCapability = ThreadLocal
			.withInitial(() -> UnexpectedAlertBehaviour.ACCEPT);

	private ThreadLocal<UnexpectedAlertBehaviour> currentAlertCapability = ThreadLocal
			.withInitial(() -> UnexpectedAlertBehaviour.ACCEPT);

	public void waitUntilElementPresent(final WebDriver driver, final By by) {
		WebDriverWait wait = new WebDriverWait(driver, 5);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			// Ignore the timeout exception
		}
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
	}

	public boolean isElementPresent(By by, long waitSeconds) {
		// driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
		try {
			driver.findElement(by);
			// System.out.println("Reached here1");
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			// System.out.println("Reached here2");
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		}
	}

	public void waitUntilManualActions(final WebDriver driver, String message, Map<String, String> dataMap) {
		int tabSize;
		String ticketId = dataMap.get("ticketId");
		try {
			logger.info("For ticket #  " + ticketId +" - "+message);
			String Msg = ("For ticket #  " + ticketId + " - MANUAL_ACTIONS - " + message);
			dataMap.put("ManualActions_Message", Msg);
			ManualActionsMessage_Add(dataMap);
			((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
			driver.switchTo().defaultContent();
			JavascriptExecutor javascript = (JavascriptExecutor) driver;
			javascript.executeScript("alert('" + message + "');");

			ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(tabs.get(0));
			tabSize = tabs.size();
			while (tabSize >= 2) {
				Thread.sleep(1000);
				tabs = new ArrayList<String>(driver.getWindowHandles());
				tabSize = tabs.size();
			}
			;
		} catch (Exception e) {
		}
	}
	

	public void startAutomation(Map<String, String> dataMap) {
		String ticketId = dataMap.get("ticketId");
		String serviceName = dataMap.get("serviceName");
		long startTime = System.currentTimeMillis();
		String str = String.valueOf(startTime);
		dataMap.put("Start", str);
		
	}
	
	public void stopAutomation(Map<String, String> dataMap) {
		String Start1 = dataMap.get("Start");
		long start = Long.parseLong(Start1);
		long end = System.currentTimeMillis();
		float msec = end - start;
		float sec = msec / 1000F;
		float minutes = sec / 60F;
		String timetaken = Float.toString(minutes);
		dataMap.put("timeTaken", timetaken);
		System.out.println("Time taken - "+timetaken);
	}
	
	
	public void ManualActionsMessage_Add(Map<String, String> dataMap) {
		String Message = dataMap.get("ManualActions_Message");
		ManualActionsMessage.add(Message);
	}

	public List<String> ManualActionsMessage_Return() {
		ArrayList<String> manualActionsMessage = (ArrayList<String>) ManualActionsMessage;
		return manualActionsMessage;
	}

	public void ManualActionsMessage_Clear() {
		ManualActionsMessage.clear();
	}

	public void waitUntilManualActions_old(final WebDriver driver, String message, Map<String, String> dataMap) {
		int tabSize;
		String ticketId = dataMap.get("ticketId");
		try {
			String Msg = ("For ticket #  " + ticketId + " - MANUAL_ACTIONS - " + message);
			((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
			driver.switchTo().defaultContent();
			JavascriptExecutor javascript = (JavascriptExecutor) driver;
			javascript.executeScript("alert('" + message + "');");

			ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(tabs.get(0));
			tabSize = tabs.size();
			// System.out.println(tabs.size());
			while (tabSize >= 2) {
				Thread.sleep(1000);
				tabs = new ArrayList<String>(driver.getWindowHandles());
				tabSize = tabs.size();
				// System.out.println(tabs.size());
			}
			;
		} catch (Exception e) {
			// logger.error(e);
		}
	}

	public void waitUntilElementPresence(final WebDriver driver, By by) {
		WebDriverWait wait = new WebDriverWait(driver, 3);
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated(by));
		} catch (Exception e) {
			// Ignore the timeout exception
		}
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
	}

	/**
	 * This function helps to clear, find and add values to the elements
	 * 
	 * @param driver Webdriver
	 * @param name   Name of the element
	 * @param value  Values to be added to the element
	 * @throws Exception
	 */
	public void findAndAddElementById(final WebDriver driver, String elementid, String value) throws Exception {
		try {
			waitUntilElementPresent(driver, By.id(elementid));
			if (isElementPresent(driver, By.id(elementid))) {
				driver.findElement(By.id(elementid)).clear();
				driver.findElement(By.id(elementid)).sendKeys(value);
			} else {
				logger.error("Expected element " + elementid + " does not seems to available " + driver.getCurrentUrl()
						+ "\n");
			}
		} catch (NoSuchElementException e) {
			logger.error("Expected element " + elementid + " does not seems to available " + driver.getCurrentUrl()
					+ "\n " + "due to " + e);
		} catch (StaleElementReferenceException s) {
			logger.error("Expected element " + elementid + " does not seems to available " + driver.getCurrentUrl()
					+ "\n " + "due to " + s);
		}
	}

	public void findAndAddElementByXpath(final WebDriver driver, String xpath, String value) throws Exception {
		try {
			waitUntilElementPresent(driver, By.xpath(xpath));
			if (isElementPresent(driver, By.xpath(xpath))) {
				// isElementClickable(driver,xpath);
				driver.findElement(By.xpath(xpath)).click();
				;
				driver.findElement(By.xpath(xpath)).clear();
				driver.findElement(By.xpath(xpath)).click();
				;
				if (value.equals("empty")) {
					value = "";
				}
				driver.findElement(By.xpath(xpath)).sendKeys(value);
			} else {
				logger.error(
						"Expected element " + xpath + " does not seems to available " + driver.getCurrentUrl() + "\n");
			}
		} catch (NoSuchElementException e) {
			logger.error("Expected element " + xpath + " does not seems to available " + driver.getCurrentUrl() + "\n "
					+ "due to " + e);
		} catch (StaleElementReferenceException s) {
			logger.error("Expected element " + xpath + " does not seems to available " + driver.getCurrentUrl() + "\n "
					+ "due to " + s);
		}
	}
	
	public void addElementByXpath(final WebDriver driver, String xpath, String value) {
			waitUntilElementPresent(driver, By.xpath(xpath));
			if (isElementPresent(driver, By.xpath(xpath))) {
				// isElementClickable(driver,xpath);
				WebElement element= driver.findElement(By.xpath(xpath));
				scrollToElement(driver, element);
				driver.findElement(By.xpath(xpath)).click();
				;
				driver.findElement(By.xpath(xpath)).clear();
				driver.findElement(By.xpath(xpath)).click();
				;
				if (value.equals("empty")) {
					value = "";
				}
				driver.findElement(By.xpath(xpath)).sendKeys(value);
			} else {
				logger.error(
						"Expected element " + xpath + " does not seems to available " + driver.getCurrentUrl() + "\n");
			}
	}
	

	public void AddElementByXpath(String xpath, String value, final WebDriver driver, Map<String, String> dataMap)
			throws Exception {
		try {
			waitUntilElementPresent(driver, By.xpath(xpath));
			if (isElementPresent(driver, By.xpath(xpath))) {
				// isElementClickable(driver,xpath);
				driver.findElement(By.xpath(xpath)).click();
				;
				driver.findElement(By.xpath(xpath)).clear();
				driver.findElement(By.xpath(xpath)).click();
				;
				String Value = dataMap.get(value);
				driver.findElement(By.xpath(xpath)).sendKeys(Value);
			} else {
				logger.error(
						"Expected element " + xpath + " does not seems to available " + driver.getCurrentUrl() + "\n");
			}
		} catch (NoSuchElementException e) {
			logger.error("Expected element " + xpath + " does not seems to available " + driver.getCurrentUrl() + "\n "
					+ "due to " + e);
		} catch (StaleElementReferenceException s) {
			logger.error("Expected element " + xpath + " does not seems to available " + driver.getCurrentUrl() + "\n "
					+ "due to " + s);
		}
	}

	public Boolean isElementClickable(final WebDriver driver, String xpath) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		boolean flag;
		try {
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
			// element.click();
			flag = true;
		} catch (Exception e) {
			flag = false;
			// TODO: handle exception
		}
		return flag;
	}

	public void findAndSelectValueByXpath(final WebDriver driver, String xpath, String value) throws Exception {

		isElementClickable(driver, xpath);
		WebDriverWait wait = new WebDriverWait(driver, 3);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpath)));
		new Select(driver.findElement(By.xpath(xpath))).selectByVisibleText(value);
		Thread.sleep(300);
	}
	
	public void findAndSelectValueByPartialText(final WebDriver driver, String xpath, String value) throws Exception {
		 boolean found =false;
		isElementClickable(driver, xpath);
		WebDriverWait wait = new WebDriverWait(driver, 3);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpath)));
		WebElement element = driver.findElement(By.xpath(xpath));
		scrollToElement(driver, element);
		List<WebElement> options = driver.findElements(By.xpath(xpath+"/option"));
		for (WebElement option : options) {
		    if ((option.getText().toUpperCase()).contains(value.toUpperCase())) {
		        option.click();
		        found =true;
		        break;
		    }
		}
		 if(found==false) {
		    	throw new IOException("Value not available");  
		    }
	}

	public void findAndSelectValueByIndex(final WebDriver driver, String xpath, int i) throws Exception {
		boolean flag = false;
		waitUntilElementPresent(driver, By.xpath(xpath));
		WebElement districtElement = driver.findElement(By.xpath(xpath));
		Select listBox = new Select(districtElement);
		int optionCount = listBox.getOptions().size();
		do {
			Thread.sleep(100);
			optionCount = listBox.getOptions().size();
			if (optionCount == 1) {
				flag = false;
			} else {
				flag = true;
			}
		} while (flag == false);
		isElementClickable(driver, xpath);
		new Select(driver.findElement(By.xpath(xpath))).selectByIndex(i);
		Thread.sleep(300);
	}

	public static boolean isElementPresent(final WebDriver driver, By by) {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try {
			driver.findElement(by);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		}
	}

	public void waitForElementPresent2(final WebDriver driver, final By by) {
		WebDriverWait wait = new WebDriverWait(driver, CommonVariables.WAIT_SECONDS);
		try {
			ExpectedCondition<Boolean> elementIsDisplayed = new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver arg0) {
					try {
						WebElement webElement = driver.findElement(by);
						if (webElement.isDisplayed()) {
							return true;
						} else {
							return false;
						}
					} catch (NoSuchElementException e) {
						return false;
					} catch (StaleElementReferenceException f) {
						return false;
					}
				}
			};
			wait.until(elementIsDisplayed);
		} catch (Exception e) {
			// Ignore the timeout exception
		} finally {
			// driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		}
	}

	public void dologin(final WebDriver driver, String sAgentUserName, String ticketId, String userEmail)
			throws Exception {
		try {
			driver.get(CommonVariables.customerDBUrl + ticketId);
			findAndAddElement(driver, CommonVariables.userName, userEmail);
			// CommonFunctions.waitUntilManualActions(driver);
			if (userEmail.equals("vinod@vakilsearch.com")) {
				findAndAddElement(driver, CommonVariables.userPassword, CommonVariables.sAgentPwd);
			} else if (userEmail.equals("nirmala.shanmugam@vakilsearch.com")) {
				findAndAddElement(driver, CommonVariables.userPassword, CommonVariables.sNirmalaAgentPwd);
			} else if (userEmail.equals("sureshbabu.perumal@vakilsearch.com")) {
				findAndAddElement(driver, CommonVariables.userPassword, CommonVariables.sSureshBabuAgentPwd);
			} else if (userEmail.equals("vijay@vakilsearch.com")) {
				findAndAddElement(driver, CommonVariables.userPassword, CommonVariables.sVijayAgentPwd);
			} else if (userEmail.equals("manochithra@vakilsearch.com")) {
				findAndAddElement(driver, CommonVariables.userPassword, CommonVariables.sManoChitraAgentPwd);
			} else if (userEmail.equals("aarthi.nr@vakilsearch.com")) {
				findAndAddElement(driver, CommonVariables.userPassword, CommonVariables.sAarthiAgentPwd);
			} else if (userEmail.equals("meena.s@vakilsearch.com")) {
				findAndAddElement(driver, CommonVariables.userPassword, CommonVariables.sMeenaAgentPwd);
			} else if (userEmail.equals("devanandhini.m@vakilsearch.com")) {
				findAndAddElement(driver, CommonVariables.userPassword, CommonVariables.sDevanandhiniAgentPwd);
			} else if (userEmail.equals("Renukadevi@vakilsearch.com")) {
				findAndAddElement(driver, CommonVariables.userPassword, CommonVariables.sRenukaAgentPwd);
			}

			driver.findElement(By.name(CommonVariables.COMMITBUTTON)).click();
			// waitForElementPresent(driver,By.xpath("//div[@class='flash success']"));

		} catch (Exception e) {
			logger.error("The entire process is failed due to " + e + " in " + driver.getCurrentUrl() + "\n");
		}
	}

	public void findAndAddElement(final WebDriver driver, String name, String value) throws Exception {
		try {
			waitUntilElementPresent(driver, By.name(name));
			if (isElementPresent(driver, By.name(name))) {
				driver.findElement(By.name(name)).clear();
				driver.findElement(By.name(name)).sendKeys(value);
			} else {
				logger.error("The entire test cases failed since , the expected element " + name
						+ " does not seems to available " + driver.getCurrentUrl() + "\n");
			}
		} catch (NoSuchElementException e) {
			logger.error("The entire test cases failed since , the expected element " + name
					+ " does not seems to available " + driver.getCurrentUrl() + "\n");
		} catch (StaleElementReferenceException s) {
			logger.error("The entire test cases failed since , the expected element " + name
					+ " does not seems to available " + driver.getCurrentUrl() + "\n");
		}
	}

	public String getSelectBoxValue(final WebDriver driver, String fieldName) throws IOException {
		String targetElement = null;
		List<WebElement> resultResources = null;
		try {
			if ((fieldName.equals("Business Type")) || (fieldName.equals("Country *"))
					|| (fieldName.equals("Pincode *"))) {
				waitUntilElementPresent(driver, By.xpath(
						"//*[text()='" + fieldName + "']/following-sibling::select[1]/option[@selected='selected']"));
				resultResources = driver.findElements(By.xpath(
						"//*[text()='" + fieldName + "']/following-sibling::select[1]/option[@selected='selected']"));
			} else {
				waitUntilElementPresent(driver, By.xpath("//*[text()='" + fieldName + "']/following::span[1]"));
				resultResources = driver.findElements(By.xpath("//*[text()='" + fieldName + "']/following::span[1]"));
			}

			Iterator<WebElement> calIterator = resultResources.iterator();
			while (calIterator.hasNext()) {
				WebElement eachElements = calIterator.next();
				if (eachElements.isDisplayed()) {
					targetElement = eachElements.getText().trim();
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return targetElement;
	}

	public String getSelectBoxValueByXpath(final WebDriver driver, String xpath) throws IOException {
		String targetElement = null;
		try {
			List<WebElement> resultResources = driver.findElements(By.xpath(xpath));
			Iterator<WebElement> calIterator = resultResources.iterator();
			while (calIterator.hasNext()) {
				WebElement eachElements = calIterator.next();
				if (eachElements.isDisplayed()) {
					targetElement = eachElements.getText().trim();
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return targetElement;
	}

	public void clickByxpath(final WebDriver driver, String xpath) throws Exception {
		try {
			waitUntilElementPresent(driver, By.xpath(xpath));
			if (isElementPresent(driver, By.xpath(xpath))) {
				driver.findElement(By.xpath(xpath)).click();
			} else {
				logger.error("The entire test cases failed since, the expected element " + xpath
						+ " does not seems to available " + driver.getCurrentUrl() + "\n");
			}
		} catch (NoSuchElementException e) {
			logger.error("The entire test cases failed since, the expected element " + xpath
					+ " does not seems to available " + driver.getCurrentUrl() + "\n");
		} catch (StaleElementReferenceException s) {
			logger.error("The entire test cases failed since, the expected element " + xpath
					+ " does not seems to available " + driver.getCurrentUrl() + "\n");
		}
	}
	
	
	public void clickElement(final WebDriver driver, String xpath) throws Exception {
			waitUntilElementPresent(driver, By.xpath(xpath));
			if (isElementPresent(driver, By.xpath(xpath))) {
				driver.findElement(By.xpath(xpath)).click();
			} else {
				logger.error("The entire test cases failed since, the expected element " + xpath
						+ " does not seems to available " + driver.getCurrentUrl() + "\n");
			}
		
	}
	
	public static void scrollToElement(WebDriver driver, WebElement element) {
		try {
			int x = element.getLocation().getX();
			int y = element.getLocation().getY();
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(" + (x - 200) + "," + (y - 200) + ")");
		} catch (Exception e) {
			System.err.println("Scrolling to element is not working ");
		}
	}

	public void clickMenuName(final WebDriver driver, String menuPath, String menuName) throws IOException {
		try {
			waitUntilElementPresent(driver, By.xpath("//*[" + menuPath + "]/li"));
			List<WebElement> resultResources = driver.findElements(By.xpath("//*[" + menuPath + "]/li"));
			int noOfMenus = resultResources.size();
			for (int i = 1; i <= noOfMenus; i++) {
				String actualResources = driver
						.findElement(By.xpath("//*[" + menuPath + "]/li[" + i + "]/a[@role='tab']")).getText();
				if (actualResources.contains(menuName)) {
					WebElement element = driver
							.findElement(By.xpath("//*[" + menuPath + "]/li[" + i + "]/a[@role='tab']"));
					element.click();
					break;
				}
			}
		} catch (Exception e) {
			logger.error("The entire add company test is failed due to...:" + e.getMessage() + "\n");
		}
	}

	public Integer getTotalNoOfApplicants(final WebDriver driver, String applicantTab) throws IOException {
		int noOfMenus = 0;
		try {
			List<WebElement> resultResources = driver
					.findElements(By.xpath("//*[" + applicantTab + "]/li[contains(@class,'director_details_show')]"));
			noOfMenus = resultResources.size();

		} catch (Exception e) {
			logger.error("The entire add company test is failed due to...:" + e.getMessage() + "\n");
		}
		return noOfMenus;
	}

	public String getTextBoxValue(final WebDriver driver, String fieldName) throws IOException {
		String targetElement = null;
		WebDriverWait wait = new WebDriverWait(driver, 1);
		try {
			waitUntilElementPresent(driver, By.xpath("//*[text()='" + fieldName + "']/following::input[1]"));
			List<WebElement> resultResources = driver
					.findElements(By.xpath("//*[text()='" + fieldName + "']/following::input[1]"));
			Iterator<WebElement> calIterator = resultResources.iterator();
			while (calIterator.hasNext()) {
				WebElement eachElements = calIterator.next();
				if (eachElements.isDisplayed()) {

					wait.until(new ExpectedCondition<Boolean>() {
						public Boolean apply(WebDriver driver) {
							return eachElements.getAttribute("value").length() != 0;
						}
					});

					targetElement = eachElements.getAttribute("value").trim();
					break;
				}
			}
		} catch (Exception e) {
			logger.error("The entire test cases failed since, the expected element " + fieldName
					+ " does not seems to available " + driver.getCurrentUrl() + "\n");
		}
		return targetElement;
	}

	public String getTextBoxValueByXpath(final WebDriver driver, String xpath) throws IOException {
		String targetElement = null;
		try {
			waitUntilElementPresent(driver, By.xpath(xpath));
			List<WebElement> resultResources = driver.findElements(By.xpath(xpath));
			Iterator<WebElement> calIterator = resultResources.iterator();
			while (calIterator.hasNext()) {
				WebElement eachElements = calIterator.next();
				if (eachElements.isDisplayed()) {
					targetElement = eachElements.getAttribute("value").trim();
					break;
				}
			}
		} catch (Exception e) {
			logger.error("The entire test cases failed since, the expected element " + xpath
					+ " does not seems to available " + driver.getCurrentUrl() + "\n");
		}
		return targetElement;
	}

	public String getNextTDValue(final WebDriver driver, String fieldName) throws IOException {
		String targetElement = null;
		List<WebElement> resultResources;
		try {
			if (fieldName.contains("Father")) {
				waitUntilElementPresent(driver, By.xpath("//*[contains(text()," + fieldName + ")]/following::td[1]"));
				resultResources = driver
						.findElements(By.xpath("//*[contains(text()," + fieldName + ")]/following::td[1]"));
			} else if (fieldName.contains("Registered Address")) {
				waitUntilElementPresent(driver, By.xpath("//*[contains(text(),'" + fieldName + ")]/following::td[1]"));
				resultResources = driver
						.findElements(By.xpath("//*[contains(text(),'" + fieldName + "')]/following::td[1]"));
			} else {
				waitUntilElementPresent(driver, By.xpath("//*[text()='" + fieldName + "']/following::td[1]"));
				resultResources = driver.findElements(By.xpath("//*[text()='" + fieldName + "']/following::td[1]"));
			}
			Iterator<WebElement> calIterator = resultResources.iterator();
			while (calIterator.hasNext()) {
				WebElement eachElements = calIterator.next();
				if (eachElements.isDisplayed()) {
					targetElement = eachElements.getText().trim();
					break;
				}
			}
		} catch (Exception e) {
			logger.error("The entire test cases failed since, the expected element " + fieldName
					+ " does not seems to available " + driver.getCurrentUrl() + "\n");
		}
		return targetElement;
	}

	public Map<String, String> getDirectorsDetails(final WebDriver driver, String preFixXpath, String suffixXpath,
			Map<String, String> dataMap) throws IOException {
		Map<String, String> directorDINDetails = new HashMap<String, String>();
		List<WebElement> resultResources;
		ArrayList<String> listofDirectors = new ArrayList<String>();
		try {

			resultResources = driver.findElements(
					By.xpath(CommonVariables.PATH_MCADINNOS_PREFIX + CommonVariables.PATH_MCADINNOS_SUFFIX));
			int noOfDirector = resultResources.size();
			for (int i = 1; i <= noOfDirector; i++) {
				String actualValue = driver.findElement(By.xpath(
						CommonVariables.PATH_MCADINNOS_PREFIX + "[" + i + "]" + CommonVariables.PATH_MCADINNOS_SUFFIX))
						.getText();
				String actualDirectorName = driver.findElement(By.xpath(CommonVariables.PATH_MCADINNOS_PREFIX + "[" + i
						+ "]" + CommonVariables.PATH_MCADIRECTOR_NAME_SUFFIX)).getText().toUpperCase().trim();
				listofDirectors.add(actualDirectorName);
				directorDINDetails.put(actualDirectorName, actualValue);

			}
			Collections.sort(listofDirectors);

			directorDINDetails = sortbykey(directorDINDetails);
			for (int i = 1; i <= noOfDirector; i++) {
				dataMap.put("DINNo " + i, directorDINDetails.get(listofDirectors.get(i - 1)));
			}

			dataMap.putAll(directorDINDetails);
			sortbykey(dataMap);
			// System.out.println(CommonVariables.applicant);
		} catch (Exception e) {
			logger.error("The entire test cases failed since, the expected element " + preFixXpath + suffixXpath
					+ " does not seems to available " + driver.getCurrentUrl() + "\n");
		}
		return directorDINDetails;
	}

	public void getPanDetailsFromPDF(Map<String, String> dataMap, int j) throws IOException {
		String dinNo = dataMap.get("DINNo " + j);
		Map<String, String> directorPDFPanDetails = new HashMap<String, String>();
		String str = null;
		Response response = null;
		String ticketId = dataMap.get("ticketId");
		try {
			OkHttpClient client = new OkHttpClient();
			// Proxy proxyTest = new Proxy(Proxy.Type.HTTP,new
			// InetSocketAddress("192.168.5.102", 57707));
			// OkHttpClient.Builder builder = new OkHttpClient.Builder().proxy(proxyTest);
			// OkHttpClient client = builder.build();
			MediaType mediaType = MediaType.parse("text/xml");
			RequestBody body = RequestBody.create(mediaType,
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">  "
							+ " <soap:Body>     "
							+ " <tns:getNCAPrefillDetails xmlns:tns=\"http://ncaprifill.org/wsdl\">     "
							+ "    <NCAPrefillProcessorDTO>       " + "     <callID>DIN</callID>        "
							+ "    <formID>ZI01</formID>       " + "     <input/>          " + "  <panPassport>" + dinNo
							+ "</panPassport>     " + "       <sid>NCA</sid>     " + "    </NCAPrefillProcessorDTO>  "
							+ "    </tns:getNCAPrefillDetails> " + "  </soap:Body></soap:Envelope>");
			Request request = new Request.Builder().url("http://www.mca.gov.in/FOServicesWeb/NCAPrefillService")
					.post(body).addHeader("Cache-Control", "no-cache").addHeader("Content-Type", "text/xml")
					.addHeader("Postman-Token",
							"d5478e35-44f2-44fe-8983-c34c833c6cfe,42cf42c0-03bd-4200-9188-79615a8fb0c9")
					.addHeader("accept-encoding", "gzip, deflate").addHeader("cache-control", "no-cache").build();

			response = client.newCall(request).execute();
			str = response.body().string();

			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(str));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("return");

			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);

				NodeList panNo = element.getElementsByTagName("pan");
				Element panNoLine = (Element) panNo.item(0);
				directorPDFPanDetails.put("applicantPan " + (j), getCharacterDataFromElement(panNoLine));

				NodeList dateOfBirth = element.getElementsByTagName("dateOfBirth");
				Element dateOfBirthLine = (Element) dateOfBirth.item(0);
				String[] dobs = getCharacterDataFromElement(dateOfBirthLine).split("T");
				String expectedDate = convertDateFormat(dobs[0], "yyyy-MM-dd", "dd/MM/yyyy");
				directorPDFPanDetails.put("applicantDOB " + (j), expectedDate);

				NodeList aadharNo = element.getElementsByTagName("adhaar");
				Element aadharNoLine = (Element) aadharNo.item(0);
				directorPDFPanDetails.put("applicantaadharNo " + (j), getCharacterDataFromElement(aadharNoLine));

				NodeList paddressLineOne = element.getElementsByTagName("paddressLineOne");
				Element paddressLineOneLine = (Element) paddressLineOne.item(0);

				directorPDFPanDetails.put("applicantAddressLine1 " + (j),
						getCharacterDataFromElement(paddressLineOneLine));

				NodeList paddressLineTwo = element.getElementsByTagName("paddressLineTwo");
				Element paddressLineTwoLine = (Element) paddressLineTwo.item(0);

				directorPDFPanDetails.put("applicantAddressLine2 " + (j),
						getCharacterDataFromElement(paddressLineTwoLine));

				NodeList cityOne = element.getElementsByTagName("cityOne");
				Element cityOneLine = (Element) cityOne.item(0);
				directorPDFPanDetails.put("applicantCity " + (j), getCharacterDataFromElement(cityOneLine));

				NodeList state1 = element.getElementsByTagName("state1");
				Element state1Line = (Element) state1.item(0);
				directorPDFPanDetails.put("applicantState " + (j), getCharacterDataFromElement(state1Line));

				NodeList country = element.getElementsByTagName("country");
				Element countryLine = (Element) country.item(0);
				directorPDFPanDetails.put("applicantCountry " + (j), getCharacterDataFromElement(countryLine));

				NodeList postCodeOne = element.getElementsByTagName("postCodeOne");
				Element postCodeOneLine = (Element) postCodeOne.item(0);
				directorPDFPanDetails.put("applicantPincode " + (j), getCharacterDataFromElement(postCodeOneLine));
			}
			dataMap.putAll(directorPDFPanDetails);
			dataMap.put("fetchPDFDetails", "Yes");
			sortbykey(dataMap);
			logger.info("For ticket #  " + ticketId + " Got DIN information using INC_Form1");
		} catch (Exception e) {
			logger.error(e);
			dataMap.put("fetchPDFDetails", "No");
		} finally {
			response.body().close();
			logger.info("For ticket #  " + ticketId + " Closed INC_Form1 API call successfully");
		}
	}

	public String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

	public Boolean getIncorporationDetailsFromMCA(final WebDriver driver, Map<String, String> dataMap)
			throws Exception {
		boolean flag = false;
		int capTry = 0;
		String ticketId = dataMap.get("ticketId");
		try {

			CommonFunctions commonFunctions = new CommonFunctions();
			driver.get("https://www.google.com/");
			Thread.sleep(1500);
			driver.findElement(By.name("q")).sendKeys("mca.gov.in");

			driver.findElement(By.name("q")).sendKeys(Keys.ENTER);
			Thread.sleep(1500);
//			WebElement MCA = driver.findElement(By.xpath("//*[@id=\"rso\"]/div[1]/div/div/div/div[1]/a/h3/span"));
//			MCA.click();
			// driver.findElement(By.xpath("//a[@href='http://www.mca.gov.in/']")).click();
			commonFunctions.waitUntilManualActions_old(driver,
					"Click Company/LLP Master Data Link manually and Close the empty tab once the site is loaded",
					dataMap);
			Thread.sleep(1000);
			Actions action = new Actions(driver);
//			commonFunctions.waitForElementPresent(driver, By.linkText("MCA SERVICES"));
//			WebElement MCA_Serviceslink = driver.findElement(By.linkText("MCA SERVICES"));
//			action.moveToElement(MCA_Serviceslink).perform();
//			commonFunctions.waitForElementPresent(driver, By.linkText("View Company or LLP Master Data"));
//			WebElement viewCompanyLLPmasterData = driver.findElement(By.linkText("View Company or LLP Master Data"));
//			action.moveToElement(viewCompanyLLPmasterData).click().perform();
			commonFunctions.waitForElementPresent(driver, By.xpath(CommonVariables.mcaCINFieldName));

			if (isElementPresent(driver, By.xpath(CommonVariables.mcaCINFieldName)) == true) {
				findAndAddElementByXpath(driver, CommonVariables.mcaCINFieldName, dataMap.get("CIN"));

				commonFunctions.waitUntilManualActions_old(driver,
						"Enter the Captcha, Click Submit button and close the empty tab", dataMap);

				// Breaking MCA captcha
//				capTry = 0;
//				do{
//					capTry = capTry + 1;
//					getCaptchaAndEnter(driver, capTry, dataMap,By.xpath(CommonVariables.PATH_MCA_CAPTCHA),By.xpath(CommonVariables.mcaCaptchaFieldName),By.xpath(CommonVariables.MCASUBMIT));
//					if(isElementPresent(driver, By.xpath(CommonVariables.PATH_MCA_CAPTCHA_ERROR_OVERLAY))==true) {
//						clickElementByJS(driver, CommonVariables.PATH_MCA_CAPTCHA_ERROR_OVERLAY_CLOSE);
//						waitForElementPresent(driver, By.xpath("//div[@id='bg_overlay' and @style='opacity: 0.5; display: none;']"));
//					}
//				}while(isElementPresent(driver, By.xpath(CommonVariables.MCASUBMIT))==true);
				Thread.sleep(3000);
				if (isElementPresent(driver, By.xpath("//*[text()='Our apologies...']")) == true) {
					flag = false;
				} else {
					if (isElementPresent(driver,
							By.xpath("//*[text()='" + CommonVariables.authorizedCompanyFieldName + "']")) == true) {
						dataMap.put("authorizedCompanyName",
								getNextTDValue(driver, CommonVariables.authorizedCompanyFieldName));
					} else if (isElementPresent(driver,
							By.xpath("//*[text()='" + CommonVariables.authorizedLLPCompanyFieldName + "']")) == true) {
						dataMap.put("authorizedCompanyName",
								getNextTDValue(driver, CommonVariables.authorizedLLPCompanyFieldName));
					}
					dataMap.put("dateOfIncorporation",
							getNextTDValue(driver, CommonVariables.dateOfIncorporationFieldName));
					int nOD = driver.findElements(By.xpath("//a[@id='aShowDirectorMasterdata']")).size();
					dataMap.put("noOfDirectors", String.valueOf(nOD));
					getDirectorsDetails(driver, CommonVariables.PATH_MCADINNOS_PREFIX,
							CommonVariables.PATH_MCADINNOS_SUFFIX, dataMap);
					flag = true;
				}
				sortbykey(dataMap);
			} else {
				flag = false;
			}
		} catch (IOException e) {
			logger.error(e);
			flag = false;
		} catch (StaleElementReferenceException s) {
			logger.error(s);
			flag = false;
		} catch (NoSuchElementException e) {
			logger.error("For ticket #  " + ticketId + " The entire process is failed since, due to " + e + " \n");
		}
		// logger.error(CommonVariables.applicant);
		return flag;

	}

	public Boolean getIncorporationDetailsFromMCAUsingAPI(Map<String, String> dataMap) throws IOException {
		String ticketId = dataMap.get("ticketId");
		logger.info("Problem getting info from MCA website so getting info from our vakilsearch API for the ticket: -- "
				+ ticketId + "");
		boolean flag = false;

		Map<String, String> directorDINDetails = new HashMap<String, String>();
		Response response = null;
		ArrayList<String> listofDirectors = new ArrayList<String>();
		try {
			OkHttpClient client = new OkHttpClient();
			String url = null;
			Request initialRequest = new Request.Builder()
					.url("https://vakilsearch.com/company-name-search/libra?type=search_companies&keyword="
							+ dataMap.get("CIN")
							+ "&api_token=eg43780f6d77732767ff79639338a153bcc210e97461222b7948c40a42048ffg")
					.get().addHeader("Accept", "*/*").addHeader("Cache-Control", "no-cache")
					.addHeader("Host", "vakilsearch.com").build();

			Response initialResponse = client.newCall(initialRequest).execute();

			String initialResponseStr = initialResponse.body().string();
			JSONObject initialResponseJsonObject = new JSONObject(initialResponseStr);
			JSONArray initialResponseJsonArray = initialResponseJsonObject.getJSONArray("data");

			for (int i = 0; i < initialResponseJsonArray.length(); i++) {
				url = initialResponseJsonArray.getJSONObject(i).getString("url");
			}
			String[] requiredUrls = url.split("/company/");

			Request request = new Request.Builder()
					.url("https://vakilsearch.com/company-name-search/libra?type=libra_company&keyword="
							+ requiredUrls[1]
							+ "&api_token=eg43780f6d77732767ff79639338a153bcc210e97461222b7948c40a42048ffg")
					.get().addHeader("Accept", "*/*").addHeader("Cache-Control", "no-cache")
					.addHeader("Host", "vakilsearch.com").build();

			response = client.newCall(request).execute();
			String str = response.body().string();
			JSONObject jsonObject = new JSONObject(str);
			dataMap.put("authorizedCompanyName",
					jsonObject.getJSONObject("data").getJSONObject("company_detail_list").getString("company_name"));
			dataMap.put("dateOfIncorporation", jsonObject.getJSONObject("data").getJSONObject("company_detail_list")
					.getString("date_of_incorporation"));

			JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("directors_detail_list");

			if (jsonArray.length() > 0) {
				dataMap.put("noOfDirectors", String.valueOf(jsonArray.length()));

				for (int i = 1; i <= jsonArray.length(); i++) {
					String dinValue = jsonArray.getJSONObject(i - 1).getString("din");
					directorDINDetails.put("DINNo " + i, dinValue);
					String actualDirectorName = jsonArray.getJSONObject(i - 1).getString("name");
					listofDirectors.add(actualDirectorName);
					directorDINDetails.put(actualDirectorName, dinValue);
				}
				Collections.sort(listofDirectors);
				directorDINDetails = sortbykey(directorDINDetails);
				for (int i = 1; i <= jsonArray.length(); i++) {
					dataMap.put("DINNo " + i, directorDINDetails.get(listofDirectors.get(i - 1)));
				}

				dataMap.putAll(directorDINDetails);
				flag = true;
				logger.info("For ticket #  " + ticketId
						+ ", Got incorporation details from Vakilsearch elastic search API");
			} else {
				dataMap.put("noOfDirectors", "0");
				logger.info("For ticket #  " + ticketId
						+ ", Got No. of directors as 0 from Vakilsearch elastic search API");
			}
		} catch (Exception e) {
			flag = false;
			logger.error(e);
		} finally {
			response.body().close();
			logger.info("For ticket #  " + ticketId + ", Closed VakilSearch Elastic Search API call successfully");
		}
		return flag;
	}

	public boolean getApplicantsDetailsFromPSA(final WebDriver driver, Map<String, String> dataMap) throws Exception {
		boolean flag = true;
		try {
			int loopCount = 1;
			if (dataMap.get("businessType").equals("Company / LLP / Partnership Firm")) {
				loopCount = Integer.valueOf(dataMap.get("noOfDirectors"));
			} else if (dataMap.get("businessType").equals("Proprietorship")) {
				loopCount = 1;
			}
			Map<String, String> tempapplicantDetails = new HashMap<String, String>();
			driver.get(CommonVariables.psaUrl);
			for (int i = 1; i <= loopCount; i++) {
				String appFirstName;
				String appMidName;
				String appLastName;
				String appFatherFirstName;
				String appFatherMidName;
				String appFatherLastName;
				String appGender;
				String applicantNameinPan;
				String appPanNo;
				String appPan = dataMap.get("applicantPan " + i);
				if (isElementPresent(driver, By.xpath(CommonVariables.panVerificationFieldName)) == true) {
					if ((appPan == null) || (appPan.isEmpty())) {
						dataMap.put("panNullAt " + i, String.valueOf(i));
						dataMap.put("isPanDataNull", "Yes");
						waitUntilManualActions(driver, "DIN of director " + dataMap.get("applicantName " + i)
								+ " is deactivated. So using value as in CRM ", dataMap);
						logger.info("For ticket #: " + dataMap.get("ticketId") + ", DIN of director "
								+ dataMap.get("applicantName " + i) + " is deactivated. So using value as in CRM ");
					} else {
						findAndAddElementByXpath(driver, CommonVariables.panVerificationFieldName, appPan);
						clickByxpath(driver, CommonVariables.PANVERIFICATIONSUBMIT);
						if (isElementPresent(driver,
								By.xpath("//*[text()='" + CommonVariables.applicantPanNoFieldName + "']")) == true) {

							appPanNo = getNextTDValue(driver, CommonVariables.applicantPanNoFieldName);
							if (appPanNo.equals(appPan)) {
								appFirstName = getNextTDValue(driver, CommonVariables.applicantFirstNameFieldName);
								appMidName = getNextTDValue(driver, CommonVariables.applicantMidNameFieldName);
								appLastName = getNextTDValue(driver, CommonVariables.applicantSurNameFieldName);
								appFatherFirstName = getNextTDValue(driver,
										CommonVariables.applicantFatherFirstNameFieldName);
								appFatherMidName = getNextTDValue(driver,
										CommonVariables.applicantFatherMidNameFieldName);
								appFatherLastName = getNextTDValue(driver,
										CommonVariables.applicantFatherSurNameFieldName);
								appGender = getNextTDValue(driver, CommonVariables.applicantGenderFieldName);

								tempapplicantDetails.put("applicantFirstName " + i, appFirstName);
								tempapplicantDetails.put("applicantMidName " + i, appMidName);
								tempapplicantDetails.put("applicantSurName " + i, appLastName);
								tempapplicantDetails.put("applicantFatherFirstName " + i, appFatherFirstName);
								tempapplicantDetails.put("applicantFatherMidName " + i, appFatherMidName);
								tempapplicantDetails.put("applicantFatherSurName " + i, appFatherLastName);
								tempapplicantDetails.put("applicantGender " + i, appGender);
								applicantNameinPan = appFirstName + " " + appMidName + " " + appLastName;
								applicantNameinPan = applicantNameinPan.trim().replaceAll(" +", " ").toUpperCase();
								if (!dataMap.get("applicantName " + i).equals(applicantNameinPan)) {
									waitUntilManualActions(driver, "Name in CRM is " + dataMap.get("applicantName " + i)
											+ " but in PAN Website is " + applicantNameinPan + " ", dataMap);
									dataMap.put("panMisMatchAt " + i, String.valueOf(i));
									dataMap.put("isPanDataMisMatch", "Yes");
									dataMap.put("applicantFullName " + i, dataMap.get("applicantName " + i));
								} else {
									if (dataMap.get("businessType").equals("Proprietorship")) {
										dataMap.put("applicantFullName 1",
												appFirstName + " " + appMidName + " " + appLastName);
										dataMap.put("applicantFullName 1", dataMap.get("applicantFullName 1").trim()
												.replaceAll(" +", " ").toUpperCase());
									} else {
										dataMap.put("applicantFullName " + i,
												appFirstName + " " + appMidName + " " + appLastName);
										dataMap.put("applicantFullName " + i, dataMap.get("applicantFullName " + i)
												.trim().replaceAll(" +", " ").toUpperCase());
									}
								}
							} else {
								flag = false;
								waitUntilManualActions(driver,
										"There is some error while getting pan details. Kindly close and try again",
										dataMap);
								logger.info("For ticket #: " + dataMap.get("ticketId")
										+ ", There is some error while getting pan details. Kindly close and try again");
							}
						} else {
							flag = false;
							waitUntilManualActions(driver, "Seems PAN is invalid. Check the PAN and try again",
									dataMap);
							logger.info("For ticket #: " + dataMap.get("ticketId")
									+ ", Seems PAN is invalid. Check the PAN and try again");
						}
					}
				} else {
					flag = false;
					break;
				}
			}
			dataMap.putAll(tempapplicantDetails);
			System.out.println(dataMap);
			sortbykey(dataMap);
			// logger.error(CommonVariables.applicant);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			flag = false;
		} catch (StaleElementReferenceException s) {
			logger.error(s);
		}
		return flag;

	}

	public boolean formatNamesAndSort(String name, String who, int j, Map<String, String> dataMap) {
		boolean flag = false;
		try {
			name = name.trim().replaceAll(" +", " ").toUpperCase();
			String[] appNames = name.split(" ");
			String firstName = "";
			String middleName = "";
			String lastNames = "";
			int nameCount = appNames.length;
			if (nameCount == 1) {
				firstName = appNames[0];
			} else if (nameCount == 2) {
				firstName = appNames[0];
				lastNames = appNames[1];
			} else if (nameCount >= 3) {
				firstName = appNames[0];
				middleName = appNames[1];
				for (int i = 2; i < appNames.length; i++) {
					lastNames = lastNames + " " + appNames[i];
				}
			}
			if (!who.contains("Father")) {
				dataMap.put("applicantFirstName " + j, firstName.trim());
				dataMap.put("applicantMidName " + j, middleName.trim());
				dataMap.put("applicantSurName " + j, lastNames.trim());
				dataMap.put("applicantFullName " + j,
						firstName.trim() + " " + middleName.trim() + " " + lastNames.trim());
			} else {
				dataMap.put("applicantFatherFirstName " + j, firstName.trim());
				dataMap.put("applicantFatherMidName " + j, middleName.trim());
				dataMap.put("applicantFatherSurName " + j, lastNames.trim());
			}
			flag = true;
		} catch (Exception e) {
			flag = false;
			logger.error(e);

		}
		return flag;
	}

	public void waitForTextToAppear(final WebDriver driver, String textToAppear, String elementPath, long waitSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, waitSeconds);
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try {
			wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath(elementPath), textToAppear));
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void waitForTextToAppear(final WebDriver driver, String textToAppear, String elementPath) {
		waitForTextToAppear(driver, textToAppear, elementPath, CommonVariables.WAIT_SECONDS);
	}

	public void waitUntilSpinnerLoads(final WebDriver driver) throws InterruptedException {
		if (isElementPresent(driver, By.xpath("//*[contains(@style,'display: block;')]"))) {
			waitForElementPresent(driver, By.xpath("//*[contains(@style,'display: block;')]"));
			waitForInvisibility(driver, "//*[contains(@style,'display: block;')]", 5);
			waitForElementPresent(driver, By.xpath("//*[contains(@style,'display: none;')]"));
			waitForInvisibility(driver, "//*[contains(@style,'display: none;')]", 5);
			Thread.sleep(1000);
		}
	}

	public void waitUntilMCASpinnerLoads(final WebDriver driver) throws InterruptedException {
		if (isElementPresent(driver,
				By.xpath("//*[@id=\\\"companiesact1\\\"]/section/app-spice/form/div/div/div[6]/div[7]"))) {
			waitForElementPresent(driver,
					By.xpath("//*[@id=\\\"companiesact1\\\"]/section/app-spice/form/div/div/div[6]/div[7]"));
			waitForInvisibility(driver, "//*[@id=\\\"companiesact1\\\"]/section/app-spice/form/div/div/div[6]/div[7]",
					5);
			waitForElementPresent(driver,
					By.xpath("//*[@id=\\\"companiesact1\\\"]/section/app-spice/form/div/div/div[6]/div[7]"));
			waitForInvisibility(driver, "//*[@id=\\\"companiesact1\\\"]/section/app-spice/form/div/div/div[6]/div[7]",
					5);
			Thread.sleep(1000);
		}
	}

	public void waitUntilHalfSpinnerLoads(final WebDriver driver) throws InterruptedException {
		if (isElementPresent(driver, By.xpath("//*[contains(@style,'display: block;')]"))) {
			waitForElementPresent(driver, By.xpath("//*[contains(@style,'display: block;')]"));
			waitForInvisibility(driver, "//*[contains(@style,'display: block;')]", 5);
			Thread.sleep(1000);
		}
	}

	public String getDistrictName(final WebDriver driver, String pinCode) {
		((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
		driver.switchTo().defaultContent();

		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(1));
		driver.get("https://pincode.net.in/" + pinCode);

		String districtName;

		districtName = driver.findElement(By.xpath(CommonVariables.PATH_PINCODE_DISTRICT)).getText();
		districtName = districtName.substring(0, 1) + districtName.substring(1, districtName.length()).toLowerCase();
		driver.close();
		driver.switchTo().window(tabs.get(0));
		return districtName;

	}

	public String convertDateFormat(String date, String inPutDateFormat, String outPutDateFormat)
			throws ParseException {
		Date initDate = new SimpleDateFormat(inPutDateFormat).parse(date);
		SimpleDateFormat formatter = new SimpleDateFormat(outPutDateFormat);
		String formattedDate = formatter.format(initDate);
		return formattedDate;
	}

	public void clickElementByLocation(final WebDriver driver, By by) {
		WebElement webElement = driver.findElement(by);
		Point point = webElement.getLocation();
		int ycoordinate = point.getY();
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollTo(0," + ycoordinate + ")", "");
		jse.executeScript("arguments[0].click();", webElement);
	}

	public void clickElementIfVisible(final WebDriver driver, By by) throws Exception {
		try {
			waitUntilElementPresent(driver, by);
			if (isElementPresent(driver, by)) {
				List<WebElement> resultResources = driver.findElements(by);
				Iterator<WebElement> calIterator = resultResources.iterator();
				while (calIterator.hasNext()) {
					WebElement eachElements = calIterator.next();
					if (eachElements.isDisplayed()) {
						eachElements.click();
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public boolean isElementPresent1(WebDriver driver,By by) {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try {
			driver.findElement(by);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		}
	}
	public boolean isElementPresent2(WebDriver driver,By by) {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try {
			driver.findElement(by);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		}
	}
	

	public boolean checkAllRetrievedValuesIsNotNull(Map<String, String> dataMap) {
		boolean flag = true;
		for (String key : dataMap.keySet()) {
			if (dataMap.get(key) == null) {
				flag = false;
			}

		}
		return flag;
	}

	public void selectModalOption(final WebDriver driver, String modalPath, String modalOption) throws Exception {
		WebElement modal = driver.findElement(By.xpath(modalPath));
		WebElement child = modal.findElement(By.xpath(modalOption));
		clickBywebElement(driver, child);
	}

	protected static void clickBywebElement(final WebDriver driver, WebElement webElement) throws Exception {
		try {
			webElement.click();
		} catch (NoSuchElementException e) {
			logger.error("The entire test cases failed since, the expected element " + webElement
					+ " does not seems to available " + driver.getCurrentUrl() + "\n");
		} catch (StaleElementReferenceException s) {
			logger.error("The entire test cases failed since, the expected element " + webElement
					+ " does not seems to available " + driver.getCurrentUrl() + "\n");
		}
	}

	/**
	 * Wait till the element loads
	 * 
	 * @param driver
	 * @param element
	 */
	public void waitForElementToDisappear(final WebDriver driver, final By by) {
		WebDriverWait wait = new WebDriverWait(driver, CommonVariables.DISAPPEAR_TIMEOUT);
		try {
			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver webDriver) {
					return (!isElementPresent(driver, by));
				}
			});

		} catch (Exception e) {
			logger.error(e);
		}
	}

	public String formatAddressLines(String addressLine) {

		addressLine = addressLine.replaceAll("[^-/ ,a-zA-Z0-9]", "");

		return addressLine;
	}

	public String[] checkAddressLengthAndSplit(String addressLine, int fieldLength) {
		String[] splittedAddressLine = null;
		ArrayList<String> addressList = new ArrayList<String>();
		String[] addresses = null;
		String firstValue = "";
		int actualLength = addressLine.length();
		if (actualLength > fieldLength) {
			splittedAddressLine = addressLine.split(",");
			for (int i = 0; i < splittedAddressLine.length - 1; i++) {
				if (i == splittedAddressLine.length - 2) {
					firstValue = firstValue + splittedAddressLine[i];
				} else if (i != splittedAddressLine.length - 1) {
					firstValue = firstValue + splittedAddressLine[i] + ",";
				}
			}
			addressList.add(firstValue);
			addressList.add(splittedAddressLine[splittedAddressLine.length - 1].trim());
			addresses = new String[] { firstValue, splittedAddressLine[splittedAddressLine.length - 1].trim() };
		} else {
			addresses = new String[] { addressLine };
		}
		return addresses;
	}

	public String GetText_TBX(final WebDriver driver, String Xpath) {
		WebElement element = driver.findElement(By.xpath(Xpath));
		String Value = element.getAttribute("value");
		return Value;
	}

	public String GetText_DRN(final WebDriver driver, String Xpath) {
		String Value = null;
		try {
			Select select = new Select(driver.findElement(By.xpath(Xpath)));
			WebElement option = select.getFirstSelectedOption();
			Value = option.getText();
		} catch (Exception e) {
			logger.error(e);
		}
		return Value;
	}

	public String GetText_DATEPICKER(final WebDriver driver, String Xpath) {

		String date = null;
		try {
			WebElement element = driver.findElement(By.xpath(Xpath));
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			date = (String) jse.executeScript("return arguments[0].value", element);
			return date;
		} catch (Exception e) {
			logger.error(e);
		}
		return date;
	}

	public String GetText_PAGE_ELEMENT_TEXT(final WebDriver driver, String Xpath) {
		String Value = driver.findElement(By.xpath(Xpath)).getText();
		return Value;
	}

	public void selectState(final WebDriver driver, String PageName, String ApplicantName, String xpath,
			String expectedDistrictName, String pincode, Map<String, String> dataMap) {
		boolean optionFlag = false;
		try {
			String districtName = null;
			boolean flag = false;
			WebElement districtElement = driver.findElement(By.xpath(xpath));
			Select listBox = new Select(districtElement);
			int optionCount = listBox.getOptions().size();
			do {
				Thread.sleep(100);
				optionCount = listBox.getOptions().size();
				if (optionCount == 1) {
					flag = false;
				} else {
					flag = true;
				}
			} while (flag == false);
			// int districtCount = listBox.getOptions().size();
			for (int i = 0; i < optionCount; i++) {
				districtName = listBox.getOptions().get(i).getText().trim();
				if (districtName.equals(expectedDistrictName)) {
					new Select(driver.findElement(By.xpath(xpath))).selectByVisibleText(districtName);
					optionFlag = true;
					break;
				}
			}
			if (optionFlag == false) {
				waitUntilManualActions(driver,
						PageName + " = " + ApplicantName + "  Select State manually for -" + pincode + "", dataMap);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void selectDistrict(final WebDriver driver, String PageName, String ApplicantName, String xpath,
			String expectedDistrictName, String pincode, Map<String, String> dataMap) {
		boolean optionFlag = false;
		try {
			String districtName = null;
			boolean flag = false;
			WebElement districtElement = driver.findElement(By.xpath(xpath));
			Select listBox = new Select(districtElement);
			int optionCount = listBox.getOptions().size();
			do {
				Thread.sleep(100);
				optionCount = listBox.getOptions().size();
				if (optionCount == 1) {
					flag = false;
				} else {
					flag = true;
				}
			} while (flag == false);
			// int districtCount = listBox.getOptions().size();
			for (int i = 0; i < optionCount; i++) {
				districtName = listBox.getOptions().get(i).getText().trim();
				if (districtName.equals(expectedDistrictName)) {
					new Select(driver.findElement(By.xpath(xpath))).selectByVisibleText(districtName);
					optionFlag = true;
					break;
				}
			}
			if (optionFlag == false) {
				waitUntilManualActions(driver,
						PageName + " | " + ApplicantName + " - Select district manually for " + pincode + "", dataMap);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	// Function to sort map by Key
	public Map<String, String> sortbykey(Map<String, String> map) {
		Map<String, String> treeMap = new TreeMap<String, String>(map);
		// Display the TreeMap which is naturally sorted
		/*
		 * for (Map.Entry<String, String> entry : sorted.entrySet()) ;
		 * System.out.println("Key = " + entry.getKey() + ", Value = " +
		 * entry.getValue());
		 */
		return treeMap;
	}

	public void waitForInvisibility(final WebDriver driver, String xpath, int maxSeconds) {
		WebElement element = driver.findElement(By.xpath(xpath));
		Long startTime = System.currentTimeMillis();

		try {
			while (System.currentTimeMillis() - startTime < maxSeconds * 1000 && element.isDisplayed()) {
			}
		} catch (NoSuchElementException e) {
			logger.error(e);
		}

	}

	public void selectFileUploadType(final WebDriver driver, String uploadType, String dropDownPath,
			Map<String, String> dataMap) {
		try {
			findAndSelectValueByXpath(driver, dropDownPath, uploadType);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void fileUpload(final WebDriver driver, String xpath, String ticketId, String fileName,
			Map<String, String> dataMap) {
		String directFolderPath = dataMap.get("directFolderPath");
		try {
			driver.findElement(By.xpath(xpath)).sendKeys(directFolderPath + fileName + "");
			waitForInvisibility(driver, CommonVariables.BTN_CANCEL_UPLOAD, 30);
			Thread.sleep(300);
		} catch (Exception e) {
			// logger.error(e);
			// TODO: handle exception
		}
	}

	public static void CheckFile_UploadDoc_Network(String PDF_Name, String Upload_Xpath, final WebDriver driver,
			Map<String, String> dataMap) throws Exception {

		String ticketId = dataMap.get("ticketId");

		final CommonFunctions commonFunctions = new CommonFunctions();
		try {

			String Upload_filePath = dataMap.get("NetworkPath") + PDF_Name;
			System.out.println(Upload_filePath);
			logger.info(Upload_filePath);
			File Doc = new File(Upload_filePath);
			System.out.println(Doc);
			System.out.println(PDF_Name);
			if (Doc.getName().equals(PDF_Name)) {
				commonFunctions.fileUploadSpice(driver, Upload_Xpath, ticketId, Upload_filePath,
						(Map<String, String>) dataMap);
			} else {
				commonFunctions.waitUntilManualActions_old(driver, "ATTACHMENTS ERROR- Expected document " + PDF_Name
						+ " is not available, Kindly attach the file manually and close t", dataMap);
			}

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	

	public static void CheckFile_UploadDoc_Network2(String PDF_Name, String Upload_Xpath, final WebDriver driver,
			Map<String, String> dataMap) throws Exception {

		String ticketId = dataMap.get("ticketId");

		final CommonFunctions commonFunctions = new CommonFunctions();
		try {

			String Upload_filePath = dataMap.get("NetworkPath") + PDF_Name;
			System.out.println(Upload_filePath);
			logger.info(Upload_filePath);
			File Doc = new File(Upload_filePath);
			System.out.println(Doc);
			System.out.println(PDF_Name);
			if (Doc.getName().equals(PDF_Name)) {
				commonFunctions.fileUploadSpice(driver, Upload_Xpath, ticketId, Upload_filePath,
						(Map<String, String>) dataMap);
			} else {
				logger.info("ATTACHMENTS ERROR- Expected document " + PDF_Name
						+ " is not available, Kindly attach the file manually and close tab");
			}

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public static void CheckFile_UploadDoc_Network_Backend(String ticketId,String PDF_Name, String Upload_Xpath, final WebDriver driver,
			Map<String, String> dataMap) throws Exception {

		final CommonFunctions commonFunctions = new CommonFunctions();
		try {

			String Upload_filePath = dataMap.get("NetworkPath") +"/"+ticketId+"/"+ PDF_Name;
			System.out.println(Upload_filePath);
			logger.info(Upload_filePath);
			File Doc = new File(Upload_filePath);
			System.out.println(Doc);
			System.out.println(PDF_Name);
			if (Doc.getName().equals(PDF_Name)) {
				commonFunctions.fileUploadSpice(driver, Upload_Xpath, ticketId, Upload_filePath,
						(Map<String, String>) dataMap);
			} else {
				commonFunctions.waitUntilManualActions_old(driver, "ATTACHMENTS ERROR- Expected document " + PDF_Name
						+ " is not available, Kindly attach the file manually and close t", dataMap);
			}

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	public void fileUploadSpice(final WebDriver driver, String xpath, String ticketId, String filePath,
			Map<String, String> dataMap) {

		try {
			WebElement Upload = driver.findElement(By.xpath(xpath));
			Upload.sendKeys(filePath + "");

			Thread.sleep(3000);
		} catch (Exception e) {
			// logger.error(e);
			// TODO: handle exception
		}
	}

	public void fileUploadSpicePlus(final WebDriver driver, String xpath, String ticketId, String filePath,
			Map<String, String> dataMap) {

		try {

			driver.findElement(By.xpath(xpath)).sendKeys(filePath + "");
			waitForInvisibility(driver, CommonVariables.BTN_CANCEL_UPLOAD, 10);
			Thread.sleep(300);
		} catch (Exception e) {
			// logger.error(e);
			// TODO: handle exception
		}
	}

	public void getPanNullNos(Map<String, String> dataMap) {
		int i = 0;
		for (String key : dataMap.keySet()) {
			if (key.contains("panNullAt")) {
				i = i + 1;
			}
		}
		dataMap.put("panNullNos", String.valueOf(i));
	}

	public void getPanMismatchNos(Map<String, String> dataMap) {
		int i = 0;
		for (String key : dataMap.keySet()) {
			if (key.contains("panMisMatchAt")) {
				i = i + 1;
			}
		}
		dataMap.put("panMisMatchNos", String.valueOf(i));
	}

	public boolean checkKeyAvailable(Map<String, String> dataMap, String keyName) {
		boolean flag = false;
		for (String key : dataMap.keySet()) {
			if (key.contains(keyName)) {
				flag = true;
			}
		}
		return flag;
	}

	public void getSuccessfulRun(Map<String, String> dataMap) {
		int i = 0;
		for (String key : dataMap.keySet()) {
			if (key.contains("successfulRun")) {
				i = i + 1;
			}
		}
		dataMap.put("successfulRunNos", String.valueOf(i));
	}

	/**
	 * 
	 * @return returns true if the element present else false
	 */
	public boolean isAlertPresent(final WebDriver driver) {
		boolean foundAlert = false;
		WebDriverWait wait = new WebDriverWait(driver, CommonVariables.WAIT_SECONDS);
		try {
			Alert a = wait.until(ExpectedConditions.alertIsPresent());
			if (a != null) {
				driver.switchTo().alert();
				foundAlert = true;
				System.out.println("come here if");
			} else {
				foundAlert = false;
				System.out.println("come here else");
			}
		} catch (Throwable e) {
			foundAlert = false;
		} // catch
		return foundAlert;
	}

	/**
	 * 
	 * @return returns true if the element present else false
	 */
	public boolean waitForFrameToLoadAndSwitch(final WebDriver driver, String frameName) {
		boolean foundAlert = false;
		WebDriverWait wait = new WebDriverWait(driver, CommonVariables.WAIT_SECONDS);
		try {
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
			Thread.sleep(300);
		} catch (Throwable e) {
			foundAlert = false;
		} // catch
		return foundAlert;
	}

	public void switchtoDefaultFrame(final WebDriver driver) {
		try {
			driver.switchTo().defaultContent();
			Thread.sleep(300);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public String getCustomDateValue(String date, String inPutDateFormat, String expectedMonthFormat,
			String expectedValue) throws ParseException {
		String expectedDateValue = null;
		Date initDate = new SimpleDateFormat(inPutDateFormat).parse(date);
		Calendar c = Calendar.getInstance();
		c.setTime(initDate);
		if (expectedValue.contentEquals("date")) {
			expectedDateValue = String.valueOf(c.get(Calendar.DATE));
		} else if (expectedValue.contentEquals("month")) {
			SimpleDateFormat month_date = new SimpleDateFormat(expectedMonthFormat, Locale.ENGLISH);
			expectedDateValue = String.valueOf(month_date.format(initDate));
		} else if (expectedValue.contentEquals("year")) {
			expectedDateValue = String.valueOf(c.get(Calendar.YEAR));
		}
		return expectedDateValue;

	}

	public void clickElementByJS(final WebDriver driver, String elementPath) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		WebElement targetElement = driver.findElement(By.xpath(elementPath));
		js.executeScript("arguments[0].scrollIntoView(true);", targetElement);
		js.executeScript("arguments[0].click();", targetElement);
	}
	
	public void clickElementByJS_WebElement(final WebDriver driver, WebElement targetElement) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView(true);", targetElement);
		js.executeScript("arguments[0].click();", targetElement);
	}
	
	public void FindAndAddElementByJS(final WebDriver driver, String elementPath,String input) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		WebElement targetElement = driver.findElement(By.xpath(elementPath));
		js.executeScript("arguments[0].scrollIntoView(true);", targetElement);
		targetElement.sendKeys(input);
	}

	public void findAndSelectContainsValueByXpath(final WebDriver driver, String elementPath, String expectedValue,
			String referenceValue, Map<String, String> dataMap) throws InterruptedException {
		boolean flag = false;
		boolean isValuePresent = false;
		WebElement districtElement = driver.findElement(By.xpath(elementPath));
		Select listBox = new Select(districtElement);
		try {
			int optionCount = listBox.getOptions().size();
			do {
				Thread.sleep(100);
				optionCount = listBox.getOptions().size();
				if (optionCount == 1) {
					flag = false;
				} else {
					flag = true;
				}
			} while (flag == false);

			districtElement = driver.findElement(By.xpath(elementPath));
			listBox = new Select(districtElement);
			int districtCount = listBox.getOptions().size();
			for (int i = 0; i < districtCount; i++) {
				String actualType = listBox.getOptions().get(i).getText().toUpperCase().trim();
				// System.out.println(actualType);
				if (actualType.contains(expectedValue.toUpperCase())) {
					isValuePresent = true;
					WebElement targetElement = listBox.getOptions().get(i);
					targetElement.click();
					break;
				}
			}
			if (isValuePresent == false) {
				waitUntilManualActions(driver, "Select values manually for:--- " + referenceValue + "", dataMap);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public boolean checkValuesInSelectBox(final WebDriver driver, String elementPath, String expectedValue)
			throws InterruptedException {
		boolean flag = false;
		WebElement districtElement = driver.findElement(By.xpath(elementPath));
		Select listBox = new Select(districtElement);
		try {
			int optionCount = listBox.getOptions().size();
			do {
				Thread.sleep(500);
				optionCount = listBox.getOptions().size();
				if (optionCount == 1) {
					flag = false;
					System.out.println("having only 1");
				} else {
					flag = true;
				}
			} while (flag == false);
		} catch (Exception e) {
			logger.error(e);
		}
		return flag;
	}

	public Boolean checkKeyInMap(Map<String, String> dataMap, String expectedKey) {
		boolean flag = false;
		for (String key : dataMap.keySet()) {
			if (key.contains(expectedKey)) {
				flag = true;
			}
		}
		return flag;
	}

	public void createDynamicMapKeyValue(Map<String, String> mapName, String expectedKeyName, String newKeyName) {
		int i = 0;
		for (String key : mapName.keySet()) {
			if (key.contains(expectedKeyName)) {
				i = i + 1;
			}
		}
		mapName.put(newKeyName, String.valueOf(i));
	}

	/**
	 * Below method takes the download directory and the file name, which will check
	 * for the file name mention in the directory and will return 'True' if the
	 * document is available in the folder else 'false'. When we are sure of the
	 * file name, we can make use of this method to verify.
	 * 
	 * @param downloadPath Downloaded path of the file
	 * @param fileName     Name of the downloaded file to be checked
	 * @return returns true if file downloaded else false
	 */
	public boolean isFileDownloaded(String downloadPath, String fileName) {
		boolean flag = false;
		File dir = new File(downloadPath);
		File[] dir_contents = dir.listFiles();
		for (int i = 0; i < dir_contents.length; i++) {
			System.out.println(dir_contents[i].getName());
			if (dir_contents[i].getName().contains(fileName))
				return flag = true;
		}
		return flag;
	}

	public Boolean downloadFile(final WebDriver driver, By by, String fileName, String filePath) {
		boolean flag = true;
		try {
			driver.findElement(by).click();
			long timeStart = System.currentTimeMillis();
			long timeEnd;
			while ((isFileDownloaded(filePath, fileName) == false)
					|| (isFileDownloaded(filePath, ".crdownload") == true)) {
				Thread.sleep(1000);
				timeEnd = System.currentTimeMillis();
				double executionTime = (timeEnd - timeStart) / 1000;
				if (executionTime > CommonVariables.DOWNLOAD_WAIT_SECONDS) {
					flag = false;
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return flag;
	}

	public Boolean saveFileAsPDF(final WebDriver driver, By by, String filePath, String fileName) {
		final long timeStart;
		long timeEnd;
		boolean flag = true;
		boolean fileExist = false;
		File fileDirectory = new File(filePath);
		File file = new File(filePath + "\\" + fileName + ".pdf");
		try {
			if (fileDirectory.exists()) {
				if (file.exists()) {
					fileExist = true;
					file.delete();
					System.out.println("deleted file" + filePath + "\\" + fileName + ".pdf");
				} else {
					// System.out.println("Does not Exists");
				}
			} else {
				try {
					fileDirectory.mkdirs();
				} catch (SecurityException se) {

				}
			}
			WebElement printByClickElement = driver.findElement(by);
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("window.print();", printByClickElement);
			Thread.sleep(2000);
			// if(fileExist == false) {
			Runtime.getRuntime().exec(CommonVariables.saveFileAsPDF + " " + filePath + "\\" + fileName);
			// } else {
			// Runtime.getRuntime().exec(CommonVariables.replaceFileAsPDF+"
			// "+filePath+"\\"+fileName);
			// }
			// Thread.sleep(2000);
			System.out.println("saved file" + filePath + "\\" + fileName + ".pdf");
			timeStart = System.currentTimeMillis();
			while ((isFileDownloaded(filePath, fileName) == false)
					|| (isFileDownloaded(filePath, ".crdownload") == true)) {
				Thread.sleep(1000);
				timeEnd = System.currentTimeMillis();
				double executionTime = (timeEnd - timeStart) / 1000;
				System.out.println(executionTime);
				if (executionTime > CommonVariables.DOWNLOAD_WAIT_SECONDS) {
					flag = false;
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return flag;
	}

	public String getCurrentDate(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String strDate = sdf.format(cal.getTime());
		return strDate;
	}

	public void uploadFileUsingAutoIT(final WebDriver driver, String filePath, String fileName, String uploadedPath,
			String uploadedElementType, Map<String, String> dataMap) {
		System.out.println(filePath + "\\" + fileName);
		try {
			String cmd1 = "cmd C:\\webdrivers\\FileUpload.exe";
			// runCommand(cmd1);
			String currentServerHostname = "Uber9-Sys-135";
			String currentServerUser = "Administrator";
			String currentServerPass = "vakilsearch";
			String commandToRunOnRemoteMachine = "C:\\webdrivers\\FileUpload.exe";
			/*
			 * String psexecCmd =
			 * "cmd.exe /c Start C:\\webdrivers\\PsExec.exe \\%s -u %s -p %s -i C:\\webdrivers\\FileUpload.exe"
			 * ; Object[] nodeDetails = new Object[2]; nodeDetails[0] = "192.168.5.69";
			 * nodeDetails[1] = 3322; Process p =
			 * Runtime.getRuntime().exec(String.format(psexecCmd,"Uber9-Sys-135",
			 * "Admin","Vakil@123")); p.waitFor();
			 */

			String psCommand = "C:\\webdrivers\\PsExec.exe \\\\" + currentServerHostname + " -u " + currentServerUser
					+ " -p " + currentServerPass;

			psCommand = psCommand + " " + commandToRunOnRemoteMachine;

			String[] cmd = new String[5];

			cmd[0] = "cmd.exe";

			cmd[1] = "/C";

			cmd[2] = psCommand;

			cmd[3] = "";

			cmd[4] = "";
			// Run remote command

			File f = new File(System.getProperty("user.dir") + "\\lib");

			Process p = Runtime.getRuntime().exec(cmd, null, f);

			// Runtime.getRuntime().exec(CommonVariables.fileUpload+"
			// "+filePath+"\\"+fileName);
			if (uploadedElementType.equals("textBox")) {
				do {
					Thread.sleep(100);
				} while (!getTextBoxValueByXpath(driver, uploadedPath).contains(fileName));
			} else if (uploadedElementType.equals("xpath")) {
				waitUntilTextToAppear(driver, uploadedPath, fileName);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void uploadFileManually(final WebDriver driver, String filePath, String fileName, String uploadedPath,
			String uploadedElementType, Map<String, String> dataMap) {
		System.out.println(filePath + "\\" + fileName);
		try {
			waitUntilManualActions(driver, "Kindly pase the file name and save " + filePath + "\\" + fileName + "",
					dataMap);
			if (uploadedElementType.equals("textBox")) {
				do {
					Thread.sleep(100);
				} while (!getTextBoxValueByXpath(driver, uploadedPath).contains(fileName));
			} else if (uploadedElementType.equals("xpath")) {
				waitUntilTextToAppear(driver, uploadedPath, fileName);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public boolean waitUntilTextToAppear(final WebDriver driver, String xpath, String expectedString) {
		boolean flag = false;
		try {
			String actualText;
			do {
				actualText = driver.findElement(By.xpath(xpath)).getText();
				Thread.sleep(100);
			} while (!actualText.contains(expectedString));
		} catch (Exception e) {
			logger.error(e);
		}
		return flag;
	}

	/**
	 * 
	 * @param driver      Webdriver
	 * @param elementPath element to be searched by driver to wait
	 * @param waitSeconds setting driver to wait for how long seconds
	 */
	public void waitForElementPresent(final WebDriver driver, final By by) {
		WebDriverWait wait = new WebDriverWait(driver, CommonVariables.WAIT_SECONDS);
		try {
			ExpectedCondition<Boolean> elementIsDisplayed = new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver arg0) {
					try {
						WebElement webElement = driver.findElement(by);
						if (webElement.isDisplayed()) {
							return true;
						} else {
							return false;
						}
					} catch (NoSuchElementException e) {
						return false;
					} catch (StaleElementReferenceException f) {
						return false;
					}
				}
			};
			wait.until(elementIsDisplayed);
		} catch (Exception e) {
			// Ignore the timeout exception
		} finally {
			// driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		}
	}


	public boolean waitForElementPresent_return(final WebDriver driver, final By by) {
		WebDriverWait wait = new WebDriverWait(driver, CommonVariables.WAIT_SECONDS);
		try {
			ExpectedCondition<Boolean> elementIsDisplayed = new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver arg0) {
					try {
						WebElement webElement = driver.findElement(by);
						if (webElement.isDisplayed()) {

							return true;
						} else {
							return false;
						}
					} catch (NoSuchElementException e) {
						return false;
					} catch (StaleElementReferenceException f) {
						return false;
					}
				}
			};
			wait.until(elementIsDisplayed);
		} catch (Exception e) {
			// Ignore the timeout exception
		} finally {
			// driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		}
		return false;
	}

	public void waitForElementEnabled(final WebDriver driver, final By by) {
		WebDriverWait wait = new WebDriverWait(driver, CommonVariables.WAIT_SECONDS);
		try {
			ExpectedCondition<Boolean> elementIsDisplayed = new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver arg0) {
					try {
						WebElement webElement = driver.findElement(by);
						if (webElement.isDisplayed() && webElement.isEnabled()) {
							return true;
						} else {
							return false;
						}
					} catch (NoSuchElementException e) {
						return false;
					} catch (StaleElementReferenceException f) {
						return false;
					}
				}
			};
			wait.until(elementIsDisplayed);
		} catch (Exception e) {
			// Ignore the timeout exception
		} finally {
			// driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		}
	}

	public void addNoteCannedResponse(final WebDriver driver, String ticketId, String userEmail, boolean doLogin,
			String cannedResponse, String filePath, String fileName, String cannedResponseConfirmationText,
			Map<String, String> dataMap) {
		try {
			String cannedResponseMessage;
			String sentNote;
			if (doLogin == true) {
				dologin(driver, CommonVariables.sAgentUserName, ticketId, userEmail);
			}
			driver.get(CommonVariables.cRMAgentsUrl + ticketId);
			clickByxpath(driver, CommonVariables.BTN_ADD_NOTE);
			clickByxpath(driver, CommonVariables.LNK_ADD_NOTE_CANNED_RESPONSE);
			findAndAddElementByXpath(driver, CommonVariables.PATH_CANNED_RESPONSE_TEXTAREA, cannedResponse);
			chooseCannedResponse(driver, cannedResponse);
			clickElementByJS(driver, CommonVariables.PATH_ADD_NOTE_ATTACHMENT);
			uploadFileUsingAutoIT(driver, filePath, fileName + ".pdf",
					"//input[@value='Delete']/parent::div[contains(text(),'" + fileName + ".pdf')]", "xpath", dataMap);
			waitForElementPresent(driver, By.cssSelector(CommonVariables.PATH_ADD_NOTE_IFRAME));
			driver.switchTo().frame(driver.findElement(By.cssSelector(CommonVariables.PATH_ADD_NOTE_IFRAME)));
			cannedResponseMessage = driver.findElement(By.cssSelector(CommonVariables.PATH_IFRAME_BODY)).getText();
			if (!cannedResponseMessage.contains(cannedResponseConfirmationText)) {
				logger.error("For ticket #: " + ticketId + ", There is an error in selected canned response");
				waitUntilManualActions(driver, "There is an error in selected canned response. Not having "
						+ cannedResponseConfirmationText + "", dataMap);
			} else {
				Thread.sleep(1000);
				driver.switchTo().defaultContent();
				clickElementByJS(driver, CommonVariables.BTN_ADD_NOTE_SEND);
				waitForElementPresent(driver, By.className("display_messages_and_notes_block1"));
				sentNote = driver.findElement(By.className("display_messages_and_notes_block1")).getText();
				if (!sentNote.contains(cannedResponseConfirmationText)) {
					logger.error("For ticket #: " + ticketId + ", There is an error in sent canned response");
					waitUntilManualActions(driver, "There is an error in sent canned response. Not having "
							+ cannedResponseConfirmationText + "", dataMap);
				} else {
					logger.info("For ticket #: " + ticketId + ", Canned response has been sent successfully");
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public String jvmBitVersion() {
		return System.getProperty("sun.arch.data.model");
	}

	public void ticketTakeAction(final WebDriver driver, String ticketId, String userEmail, boolean doLogin,
			String cannedResponse, String filePath, ArrayList<String> fileNames, String cannedResponseConfirmationText,
			Map<String, String> dataMap) {
		try {
			int noOfFiles = fileNames.size();
			String cannedResponseMessage;
			String sentNote;
			if (doLogin == true) {
				dologin(driver, CommonVariables.sAgentUserName, ticketId, userEmail);
			}
			driver.get(CommonVariables.cRMAgentsUrl + ticketId);
			clickByxpath(driver, CommonVariables.BTN_TAKE_ACTION);
			clickByxpath(driver, CommonVariables.LNK_TAKE_ACTION_CANNED_RESPONSE);
			findAndAddElementByXpath(driver, CommonVariables.PATH_CANNED_RESPONSE_TEXTAREA, cannedResponse);
			// findAndAddElementByXpath(driver,
			// CommonVariables.PATH_CANNED_RESPONSE_TEXTAREA, cannedResponse);
			chooseCannedResponse(driver, cannedResponse);
			for (int i = 0; i < noOfFiles; i++) {
				// driver.switchTo().defaultContent();
				clickElementByJS(driver, CommonVariables.PATH_TAKE_ACTION_ATTACHMENT);

				uploadFileUsingAutoIT(driver, filePath, fileNames.get(i) + ".pdf",
						"//input[@value='Delete']/parent::div[contains(text(),'" + fileNames.get(i) + ".pdf')]",
						"xpath", dataMap);
			}
			waitForElementPresent(driver, By.cssSelector(CommonVariables.PATH_TAKE_ACTION_IFRAME));
			driver.switchTo().frame(driver.findElement(By.cssSelector(CommonVariables.PATH_TAKE_ACTION_IFRAME)));
			cannedResponseMessage = driver.findElement(By.cssSelector(CommonVariables.PATH_IFRAME_BODY)).getText();
			if (!cannedResponseMessage.contains(cannedResponseConfirmationText)) {
				logger.error("For ticket #: " + ticketId + ", There is an error in selected canned response");
				waitUntilManualActions(driver, "There is an error in selected canned response. Not having "
						+ cannedResponseConfirmationText + "", dataMap);
			} else {
				Thread.sleep(1000);
				driver.switchTo().defaultContent();
				if (!isElementPresent(driver, By.xpath(CommonVariables.PATH_EXISTING_NEXT_EVENT))) {
					String eventDate = getNextBusinessDate(1, "yyyy/MM/dd");
					findAndAddElementById(driver, CommonVariables.PATH_EVENT_NAME, "IEC CERTIFICATE SENT");
					editReadOnlyFields(driver, "Id", CommonVariables.PATH_EVENT_TIME, eventDate + " 04:00 PM");
					findAndAddElementById(driver, CommonVariables.PATH_EVENT_DESCRIPTION,
							"SENT CERTIFICATE TO THE CUSTOMER");
				}
				clickElementByJS(driver, CommonVariables.BTN_TAKE_ACTION_UPDATE);
				waitForElementPresent(driver, By.className("display_messages_and_notes_block1"));
				sentNote = driver.findElement(By.className("display_messages_and_notes_block1")).getText();
				if (!sentNote.contains(cannedResponseConfirmationText)) {
					logger.error("For ticket #: " + ticketId + ", There is an error in sent canned response");
					waitUntilManualActions(driver, "There is an error in sent canned response. Not having "
							+ cannedResponseConfirmationText + "", dataMap);
				} else {
					logger.info("For ticket #: " + ticketId + ", Canned response has been sent successfully");
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void uploadFileUsingJacobAUtoIT() {
		/*
		 * String jacobDllVersionToUse; if (jvmBitVersion().contains("32")){
		 * jacobDllVersionToUse = "jacob-1.19-x86.dll"; } else { jacobDllVersionToUse =
		 * "jacob-1.19-x64.dll"; }
		 * 
		 * File file = new File("lib", jacobDllVersionToUse);
		 * System.setProperty(LibraryLoader.JACOB_DLL_PATH, file.getAbsolutePath());
		 * 
		 * uploadWindow = new AutoItX();
		 * if(uploadWindow.winExists("192.168.5.69 - Remote Desktop Connection")) {
		 * System.out.println("yes");
		 * uploadWindow.ControlSetText("192.168.5.69 - Remote Desktop Connection","",
		 * "OPContainerClass1",""+fileNames.get(i)+".pdf"); uploadWindow.sleep(1000);
		 * {ENTER} uploadWindow.send("{ENTER}"); } else {
		 * uploadWindow.ControlSetText("Open","","Edit1",""+fileNames.get(i)+".pdf");
		 * uploadWindow.sleep(1000); uploadWindow.controlClick("Open","","Button1"); }
		 * 
		 */}

	public void chooseCannedResponse(final WebDriver driver, String expectedCannedResponse) {
		try {
			WebElement cannedResponseArea = driver.findElement(By.xpath(CommonVariables.PATH_CANNED_RESPONSE_TEXTAREA));
			cannedResponseArea.sendKeys(Keys.RETURN);
			Thread.sleep(2000);
			List<WebElement> cannedResponsesList = driver
					.findElements(By.xpath(CommonVariables.PATH_CANNED_RESPONSE_FILTERED_LIST));
			Iterator<WebElement> calIterator = cannedResponsesList.iterator();
			while (calIterator.hasNext()) {
				WebElement eachElements = calIterator.next();
				String actualResources = eachElements.getText();
				// System.out.println(actualResources);
				if (actualResources.equals(expectedCannedResponse)) {
					eachElements.click();
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void editReadOnlyFields(final WebDriver driver, String locatorType, String elementPath, String values) {
		try {
			JavascriptExecutor javascript = (JavascriptExecutor) driver;
			javascript.executeScript(
					"document.getElementBy" + locatorType + "('" + elementPath + "').removeAttribute('readonly')");
			javascript.executeScript(
					"document.getElementBy" + locatorType + "('" + elementPath + "').value='" + values + "';");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public String getNextBusinessDate(int workdays, String dateFormat) {
		LocalDateTime date = LocalDateTime.now();

		String expectedDate;

		LocalDateTime result = date;
		int addedDays = 0;
		while (addedDays < workdays) {
			result = result.plusDays(1);
			if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY || result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
				++addedDays;
			}
		}
		expectedDate = result.format(DateTimeFormatter.ofPattern(dateFormat));
		return expectedDate;
	}

	public void updateTicketSubMilestone(final WebDriver driver, Map<String, String> dataMap, boolean doLogin,
			String subMilestone) {
		try {
			String ticketId = dataMap.get("ticketId");
			String userEmail = dataMap.get("userEmail");
			if (doLogin == true) {
				dologin(driver, CommonVariables.sAgentUserName, ticketId, userEmail);
			}
			driver.get(CommonVariables.cRMAgentsUrl + ticketId);
			filterByListbox(driver, "s2id_update_milestone_select2", "select2-drop", subMilestone);
			clickByxpath(driver, CommonVariables.BTN_SUBMILESTONE_UPDATE);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * 
	 * @param driver            Webdriver
	 * @param dropdownText      id of the dropdown to be clicked to enter the values
	 * @param dropdownList      id of the dropdown list to click matching value in
	 *                          the list
	 * @param expectedResources value to be clicked in the dropdown list
	 */
	public void filterByListbox(final WebDriver driver, String dropdownText, String dropdownList,
			String expectedResources) {
		// clickElementByLocation(By.xpath("//*[@id='" + dropdownText + "']/a"));
		driver.findElement(By.xpath("//*[@id='" + dropdownText + "']/a")).click();
		driver.findElement(By.xpath("//div[@id='" + dropdownList + "']/div/input")).sendKeys(expectedResources);
		if (isElementPresent(driver, By.xpath("//*[contains(@id,'" + dropdownList + "')]/ul/li/div/span"))) {
			List<WebElement> resultResources = driver
					.findElements(By.xpath("//*[contains(@id,'" + dropdownList + "')]/ul/li/div/span"));
			Iterator<WebElement> calIterator = resultResources.iterator();
			while (calIterator.hasNext()) {
				WebElement eachElements = calIterator.next();
				String actualResources = eachElements.getText();
				// System.out.println(actualResources);
				if (actualResources.contains(expectedResources)) {
					eachElements.click();
					break;
				}
			}
		} else {
			List<WebElement> resultResources = driver
					.findElements(By.xpath("//*[contains(@id,'" + dropdownList + "')]/ul/li"));
			Iterator<WebElement> calIterator = resultResources.iterator();
			while (calIterator.hasNext()) {
				WebElement eachElements = calIterator.next();
				String actualResources = eachElements.getText();
				// System.out.println(actualResources);
				if (actualResources.contains(expectedResources)) {
					eachElements.click();
					break;
				}
			}
		}
		waitForElementToDisappear(driver, By.name("nprogress-busy"));
	}

	public String[] getHostNameAndPort(String hostName, int port, SessionId session) {
		String[] hostAndPort = new String[2];
		try {
			HttpHost host = new HttpHost(hostName, port);
			CloseableHttpClient client = HttpClients.createSystem();
			URL sessionURL = new URL("http://" + hostName + ":" + port + "/grid/api/testsession?session=" + session);
			BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("POST",
					sessionURL.toExternalForm());
			HttpResponse response = client.execute(host, r);
			String url = extractObject(response);
			if (url != null) {
				URL myURL = new URL(url);
				if ((myURL.getHost() != null) && (myURL.getPort() != -1)) {
					hostAndPort[0] = myURL.getHost();
					hostAndPort[1] = Integer.toString(myURL.getPort());
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return hostAndPort;
	}

	public String extractObject(HttpResponse resp) throws IOException, JSONException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		StringBuffer s = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null) {
			s.append(line);
		}
		rd.close();
		// JSONObject objToReturn = new JSONObject(s.toString());
		return s.toString();
	}

	public String getKeyValue(Map<String, String> dataMap, String keyName) {
		String expectedValue = null;
		for (String key : dataMap.keySet()) {
			if (key.contains(keyName)) {
				expectedValue = dataMap.get(key);
			}
		}
		return expectedValue;
	}

	public int getNoOfKeys(Map<String, String> dataMap, String expectedKey) {
		int i = 0;
		for (String key : dataMap.keySet()) {
			if (key.contains(expectedKey)) {
				i = i + 1;
			}
		}
		return i;
	}

	public String getSpecificElementScreenshotLocal(final WebDriver driver, Collection<WebElement> elementPath,
			String filePath) {
		String imageString = null;
		try {
			File fileDirectory = new File(filePath);
			if (!fileDirectory.exists()) {
				try {
					fileDirectory.mkdirs();
				} catch (SecurityException se) {

				}
			}
			// Take a specific element screenshot
			Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000))
					.takeScreenshot(driver, elementPath);
			ImageIO.write(screenshot.getImage(), "jpg", new File(filePath));
		} catch (Exception e) {
			logger.error(e);
		}
		return imageString;
	}

	public String postRequest(String endPoint, ArrayList<NameValuePair> params) {
		String responseMsg = null;

		try {
			HttpPost post = new HttpPost(endPoint);
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// httpClient.execute(post);
			try (CloseableHttpClient httpClient = HttpClients.createDefault();
					CloseableHttpResponse response = httpClient.execute(post)) {
				responseMsg = EntityUtils.toString(response.getEntity());
			}

		} catch (Exception e) {
			logger.error(e);
		}
		return responseMsg;
	}

	public void ClientMultipartFormPost(String endPoint, String filePath) throws IOException {

		if (filePath.length() != 1) {
			System.out.println("File path not given");
			System.exit(1);
		}
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httppost = new HttpPost(endPoint);

			FileBody bin = new FileBody(new File(filePath));
			StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

			HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("bin", bin).addPart("comment", comment)
					.build();

			httppost.setEntity(reqEntity);

			System.out.println("executing request " + httppost.getRequestLine());
			CloseableHttpResponse response = null;
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					System.out.println("Response content length: " + resEntity.getContentLength());
				}
				EntityUtils.consume(resEntity);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}

	public String getCaptchaTextUsingAntiCaptcha(String endPoint, String imageText) {
		try {
			ArrayList<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("apiKey", "55b2708ee5d59a06bc429f75a8dc2314"));
			params.add(new BasicNameValuePair("image64", imageText.trim()));
			// StringEntity POST_PARAMS = new StringEntity("{apiKey:
			// 55b2708ee5d59a06bc429f75a8dc2314,image64: "+imageText+"}");
			HttpPost post = new HttpPost(endPoint);
			post.setEntity(new UrlEncodedFormEntity(params));
			// post.addHeader("Content-Type", "application/json");
			Scanner in = null;

			try (CloseableHttpClient httpClient = HttpClients.createDefault();
					CloseableHttpResponse response = httpClient.execute(post)) {

				System.out.println(EntityUtils.toString(response.getEntity()));
				HttpEntity entity = response.getEntity();
				in = new Scanner(entity.getContent());
				while (in.hasNext()) {
					System.out.println(in.next());

				}
				EntityUtils.consume(entity);
			} catch (Exception e) {
				logger.error(e);
			} finally {
				in.close();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	public String getCaptchaTextUsingAntiCaptcha1(String endPoint, String imageText) {
		try {
			imageText = imageText.replace("\n", "");
			imageText = imageText.replace("\r", "");
			String postEndpoint = endPoint;

			CloseableHttpClient httpclient = HttpClients.createDefault();

			HttpPost httpPost = new HttpPost(postEndpoint);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			String inputJson = "{\n" + "  \"image64\": \"" + imageText + "\",\n"
					+ "  \"apiKey\": \"f563b9e41d6f346acf0f2d4331aafb42\"\n" + "}";

			StringEntity stringEntity = new StringEntity(inputJson);
			httpPost.setEntity(stringEntity);

			System.out.println("Executing request " + httpPost.getRequestLine());

			HttpResponse response = httpclient.execute(httpPost);

			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			// Throw runtime exception if status code isn't 200
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}

			// Create the StringBuffer object and store the response into it.
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				System.out.println("Response : \n" + result.append(line));
			}

			// Lets validate if a text 'employee_salary' is present in the response
			System.out.println(
					"Does Reponse contains 'employee_salary'? :" + result.toString().contains("employee_salary"));
		} catch (Exception e) {
			logger.error(e);
		}
		return imageText;
	}

	public ReturnBooleanAndString sendPostRequest(String requestUrl, String imageText) {
		try {
			String captchaText = null;
			Boolean flag = false;
			String payload = "{\"image64\":\"" + imageText + "\",\"apiKey\":\"55b2708ee5d59a06bc429f75a8dc2314\"}";

			URL url = new URL(requestUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			writer.write(payload);
			writer.close();
			if (connection.getResponseCode() == 200) {
				flag = true;
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer jsonString = new StringBuffer();
				String line;
				while ((line = br.readLine()) != null) {
					jsonString.append(line);
				}
				br.close();
				connection.disconnect();
				JSONObject jsonObject = new JSONObject(jsonString.toString());
				// Ticket details
				JSONObject ticketDetails = (JSONObject) jsonObject.get("captchaText");
				captchaText = ticketDetails.getString("captchaText");
			} else {
				logger.error("Got response code for anti captcha as " + connection.getResponseCode() + "");
			}
			return new ReturnBooleanAndString(flag, captchaText);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	public ReturnBooleanAndString VS_sendPostRequest(String requestUrl, String imageText) {
		try {
			String captchaText = null;
			Boolean flag = false;
			System.out.println("Image " + imageText);
			String payload = "{\"image\":\"" + imageText + "\"}";

			URL url = new URL(requestUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", "Bearer s7wb28pAoEuTk9DfIcSnZr3G65HjRiFq");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			writer.write(payload);
			writer.close();
			Thread.sleep(2000);
			if (connection.getResponseCode() == 200) {
				flag = true;
				Thread.sleep(2000);
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				StringBuffer jsonString = new StringBuffer();

				String line;

				while ((line = br.readLine()) != null) {
					jsonString.append(line);

				}
				br.close();
				connection.disconnect();
				System.out.println("JSON String " + jsonString);
				JSONObject ticketDetails = new JSONObject(jsonString.toString());
				captchaText = ticketDetails.getString("ansCaptcha");
				System.out.println("JSON Captcha " + captchaText);

			} else {
				logger.error("Got response code for anti captcha as " + connection.getResponseCode() + "");
			}
			return new ReturnBooleanAndString(flag, captchaText);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	public void addCookie(final WebDriver driver, String cookiePath) {
		try {
			// Defining cookie file
			FileReader fr = new FileReader(cookiePath);

			BufferedReader br = new BufferedReader(fr);

			String dataLine, name = null, value = null, domain = null, expiry = null, secure = null, path = null;
			Date exp = null, nextYearExp = null;

			while ((dataLine = br.readLine()) != null) {
				String[] cookieData = dataLine.split(",");
				name = cookieData[0];
				value = cookieData[1];
				domain = cookieData[2];
				path = cookieData[3];
				expiry = cookieData[4];
				secure = cookieData[5];

				if ((expiry != null && !expiry.isEmpty())) {
					// Set any random day value
					exp = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
					sdf.setTimeZone(TimeZone.getTimeZone("IST"));
					sdf.format(exp);
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.YEAR, 1);
					nextYearExp = cal.getTime();
					System.out.println("Exp- " + nextYearExp);

				}
			}

			Boolean isSecure = new Boolean(secure);

			System.out.println(nextYearExp);
			// Set value to new cookie
			Cookie cookie = new Cookie(name, value, domain, path, nextYearExp, isSecure);
			System.out.println(cookie);
			driver.manage().addCookie(cookie);

		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void getCookie(final WebDriver driver, String cookiePath) {
		// create file named Cookies to store Login Information
		File file = new File(cookiePath);
		try {
			// Delete old file if exists
			// file.delete();
			// file.createNewFile();
			// Writing character in streams
			FileWriter fw = new FileWriter(file);

			// Writing texts to the above cookie file name
			BufferedWriter bw = new BufferedWriter(fw);

			// Get all cookies
			Set<Cookie> cookie = driver.manage().getCookies();

			// Run in look to get cookies information
			for (Cookie cki : cookie) {
				bw.write(cki.getName() + "," + cki.getValue() + "," + cki.getDomain() + "," + cki.getPath() + ","
						+ cki.getExpiry() + "," + cki.isSecure());
				bw.newLine();
				System.out.println("Name cki- " + cki.getName() + "\n Value cki- " + cki.getValue() + "\n Domain cki- "
						+ cki.getDomain() + "\n Path cki- " + cki.getPath() + "\n Expiry cki- " + cki.getExpiry()
						+ "\n Secure cki- " + cki.isSecure());
			}
			// Closing the BufferedWrite
			bw.close();

			// Closing the file
			fw.close();

		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	public boolean waitUntilPageLoad(final WebDriver driver, String pageUrl) {
		boolean flag = false;
		String currentUrl;
		;
		long timeStart = System.currentTimeMillis();
		long timeEnd;
		double executionTime;
		try {

			do {
				currentUrl = driver.getCurrentUrl();
				Thread.sleep(1000);
				timeEnd = System.currentTimeMillis();
				executionTime = (timeEnd - timeStart) / 1000;
				if (executionTime > CommonVariables.DOWNLOAD_WAIT_SECONDS) {
					flag = false;
					break;
				}
			} while (!currentUrl.contains(pageUrl));

		} catch (Exception e) {
			logger.error(e);
		}
		return flag;
	}

	/**
	 * 
	 * @param driver            Webdriver
	 * @param dropdownText      id of the dropdown to be clicked to enter the values
	 * @param dropdownList      id of the dropdown list to click matching value in
	 *                          the list
	 * @param expectedResources value to be clicked in the dropdown list
	 * @throws Exception
	 */
	public void checkBoxSelection(final WebDriver driver, String elementPath) throws Exception {
		List<WebElement> resultResources = driver.findElements(By.xpath(elementPath));
		for (WebElement e : resultResources)
			if (!e.isSelected()) {
				clickBywebElement(driver, e);
			}
	}

	/**
	 * 
	 * @param driver            Webdriver
	 * @param dropdownText      id of the dropdown to be clicked to enter the values
	 * @param dropdownList      id of the dropdown list to click matching value in
	 *                          the list
	 * @param expectedResources value to be clicked in the dropdown list
	 * @throws Exception
	 */
	public boolean checkCBSelection(final WebDriver driver, String elementPath) throws Exception {
		boolean flag = false;
		List<WebElement> resultResources = driver.findElements(By.xpath(elementPath));
		for (WebElement e : resultResources)
			if (e.isSelected()) {
				flag = true;
			} else {
				flag = false;
			}
		return flag;
	}

	

	public void checkEitherElementPresence(final WebDriver driver, By either, By or) {
		WebDriverWait wait = new WebDriverWait(driver, CommonVariables.WAIT_SECONDS);
		try {
			ExpectedCondition<Boolean> elementIsDisplayed = new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver arg0) {
					try {
						WebElement thisElement = driver.findElement(either);
						WebElement thatElement = driver.findElement(or);
						if (thisElement.isDisplayed() || thatElement.isDisplayed()) {
							return true;
						} else {
							return false;
						}
					} catch (NoSuchElementException e) {
						return false;
					} catch (StaleElementReferenceException f) {
						return false;
					}
				}
			};
			wait.until(elementIsDisplayed);
		} catch (Exception e) {
			// Ignore the timeout exception
		} finally {
			// driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		}
	}

	/**
	 * ********************* Received mails checks **************
	 * 
	 * @param mailUserName     the User name of the Gmail account
	 * @param mailPwd          the Password of the Gmail account
	 * @param senderId         the send of the mail
	 * @param expectedSubject1 expected subject of the received mail to be
	 * @throws Exception to catch unchecked errors
	 */
	public ReturnBooleanAndString checkRecievedMails(final WebDriver driver, Map<String, String> dataMap,
			String mailRegexText) throws Exception {
		String mailUserName = dataMap.get("mailUserName");
		String mailPwd = dataMap.get("mailPwd");
		String senderId = dataMap.get("senderId");
		String expectedSubject = dataMap.get("expectedSubject");
		String ticketId = dataMap.get("ticketId");
		boolean flag = false;
		String emailOPT;
		try {
			driver.get("http://gmail.com");
			if (!isElementPresent(driver, By.xpath("//span[@id='reauthEmail']"))) {
				driver.findElement(By.xpath("//input[@id='Email']")).sendKeys(mailUserName);
				driver.findElement(By.xpath("//input[@id='next']")).click();
			}
			driver.findElement(By.xpath("//input[@id='Passwd']")).sendKeys(mailPwd);
			driver.findElement(By.xpath("//input[@id='signIn']")).click();

			WebElement inbox = driver.findElement(By.xpath("//a[contains(@title,'Inbox')]"));
			String inboxCount = inbox.getText();
			if (inboxCount.contains("(")) {
				String unreadInboxMails = inboxCount.substring(inboxCount.indexOf("(") + 1, inboxCount.indexOf(")"));
				int unreadMailCounts = Integer.parseInt(unreadInboxMails);

				waitForElementPresent(driver, By.xpath("//table"));
				WebElement mail1Subject = driver.findElements(By.xpath("//table/tbody/tr[contains(@class, 'zA')]"))
						.get(0);

				String subject1Text = mail1Subject.getText();
				if ((!subject1Text.startsWith(senderId))) {
					logger.info("For ticket #: " + ticketId + ", The mails does not seem to have been recieved from "
							+ senderId);
				} else {
					if (!subject1Text.contains(expectedSubject)) {
						logger.info("For ticket #: " + ticketId
								+ ", The subject of the notification mail does not seem to be matching with the expected subject");
						;
					}
					List<WebElement> resultResources = driver
							.findElements(By.xpath("//*[contains(@class,'F cf zt')]/tbody/tr/td[6]"));
					Iterator<WebElement> calIterator = resultResources.iterator();
					for (int i = 0; i < unreadMailCounts; i++) {
						WebElement eachElements = calIterator.next();
						// String actualResources = eachElements.getText();
						driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
						eachElements.click();
						driver.findElement(By.xpath("//a[contains(text(), 'Inbox')]")).click();
						waitForElementPresent(driver, By.xpath("//table[@id=':3a']"));
					}
				}
			} else {
				logger.info("For ticket #: " + ticketId + ", The mails does not seem to have been recieved from "
						+ senderId);
			}
			WebElement recentinbox = driver.findElement(By.xpath("//a[contains(@title,'Inbox')]"));
			String currentinboxCount = recentinbox.getText();
			if (currentinboxCount.contains("(")) {
				// String unreadInboxMails = inboxCount.substring(inboxCount.indexOf("(") + 1,
				// inboxCount.indexOf(")"));
				// int unreadMailCounts = Integer.parseInt(unreadInboxMails);
				driver.findElement(By.xpath("//div[@data-tooltip='Select']")).click();
				driver.findElement(By.xpath("//div[@data-tooltip='Archive']")).click();
			}
			driver.findElement(By.cssSelector(
					"a[href='https://accounts.google.com/SignOutOptions?hl=en&continue=https://mail.google.com/mail&service=mail']"))
					.click();
			driver.findElement(By.xpath("//a[contains(text(),'Sign out')]")).click();
			waitForElementPresent(driver, By.xpath("//span[@id='reauthEmail']"));

		} catch (Exception e) {
			logger.info("For ticket #: " + ticketId + ", There is an error in getting email OTP due to ...:"
					+ e.getMessage());
		}
		return null;
	}// checkRecievedMails()

	public void waitUntilElementLoad(final WebDriver driver, final By by) {
		WebDriverWait wait = new WebDriverWait(driver, 5);
		WebElement el = driver.findElement(by);
		try {
			wait.until(ExpectedConditions.stalenessOf(el));
		} catch (Exception e) {
			System.out.println("waiting");
			// Ignore the timeout exception
		}
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
	}

	public DesiredCapabilities getChromeDesiredCapabilities(String automationMode) {
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors"));

		// Added to avoid yellow warning in chrome 35
		ChromeOptions options = new ChromeOptions();
		// options.addArguments("test-type");
		// For view pdf in chrome
		capabilities.setCapability("chrome.switches",
				Arrays.asList("--start-maximized", "--ignore-certificate-errors", "--ignore-ssl-errors=true",
						"--disable-popup-blocking", "--disable-default-apps", "--auto-launch-at-startup",
						"--always-authorize-plugins"));
		options.addArguments("test-type");
		options.addArguments("--no-sandbox");
		options.addArguments("--ignore-certificate-errors");
		options.addArguments("--start-maximized");
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", "C:\\Users\\Admin\\Downloads");
		chromePrefs.put("download.prompt_for_download", "false");
		options.setExperimentalOption("prefs", chromePrefs);
		options.addArguments("--test-type");
		// chromePrefs.put("profile.default_content_settings.popups", 0);
		options.setExperimentalOption("prefs", chromePrefs);

		if (automationMode.equals("headless")) {
			options.addArguments("headless");
		}
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		capabilities.setCapability(ForSeleniumServer.PROXYING_EVERYTHING, true);
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		capabilities.setCapability(CapabilityType.SUPPORTS_ALERTS, true);
		capabilities.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
		capabilities.setPlatform(Platform.WINDOWS);
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		setAlertBehaviorCapabilities(capabilities);
		// setLoggingPrefs(capabilities);
		// setProxy(capabilities);
		options.setCapability("security.default_personal_cert", "Select Automatically");
		options.merge(capabilities);

		return capabilities;
	}

	public DesiredCapabilities getFireFoxDesiredCapabilities(String downloadPath) {
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		firefoxProfile.setPreference("download.prompt_for_download", false);
		firefoxProfile.setPreference("browser.download.folderList", 2);
		firefoxProfile.setPreference("browser.download.dir", downloadPath);
		firefoxProfile.setPreference("browser.helperApps.alwaysAsk.force", false);
		firefoxProfile.setPreference("browser.download.manager.showWhenStarting", false);
		firefoxProfile.setPreference("pdfjs.disabled", true);
		firefoxProfile.setPreference("pref.downloads.disable_button.edit_actions", true);
		firefoxProfile.setPreference("dom.webnotifications.enabled", false);
		// firefoxProfile.setPreference( "layout.css.devPixelsPerPx", "0.9" );
		firefoxProfile.setAcceptUntrustedCertificates(true);
		firefoxProfile.setAssumeUntrustedCertificateIssuer(false);
		firefoxProfile.setPreference("browser.helperApps.neverAsk.openFile",
				"image/jpg,image/pjpg,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/pdf,text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/zip, application/x-zip, application/x-zip-compressed,application/octet-stream");
		firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"image/jpeg,image/pjpeg,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/pdf,text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/zip, application/x-zip, application/x-zip-compressed,application/octet-stream");
		// firefoxProfile.setPreference("network.proxy.type", 1);
		// firefoxProfile.setPreference("network.proxy.http", "us-wa.proxymesh.com");
		// firefoxProfile.setPreference("network.proxy.http_port", 31280);
		// firefoxProfile.setPreference("network.proxy.ssl", "us-wa.proxymesh.com");
		// firefoxProfile.setPreference("network.proxy.ssl_port", 31280);
		firefoxProfile.setPreference("print.always_print_silent", true);
		capabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
		return capabilities;
	}

	public DesiredCapabilities getFireFoxDesiredCapabilities_Spice(String downloadPath) {
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		FirefoxProfile firefoxProfile = new FirefoxProfile();

		firefoxProfile.setPreference("browser.download.folderList", 2);
		firefoxProfile.setPreference("browser.download.dir", downloadPath);
		firefoxProfile.setPreference("download.prompt_for_download", false);
		firefoxProfile.setPreference("browser.helperApps.alwaysAsk.force", false);
		firefoxProfile.setPreference("browser.download.manager.showWhenStarting", false);
		firefoxProfile.setPreference("pdfjs.disabled", true);
		firefoxProfile.setPreference("pref.downloads.disable_button.edit_actions", true);
		firefoxProfile.setPreference("dom.webnotifications.enabled", false);
		firefoxProfile.setPreference("layout.css.devPixelsPerPx", "0.9");
		firefoxProfile.setAcceptUntrustedCertificates(true);
		firefoxProfile.setAssumeUntrustedCertificateIssuer(false);
		// firefoxProfile.setPreference("network.proxy.type", 1);
		// firefoxProfile.setPreference("network.proxy.http", "us-wa.proxymesh.com");
		// firefoxProfile.setPreference("network.proxy.http_port", 31280);
		// firefoxProfile.setPreference("network.proxy.ssl", "us-wa.proxymesh.com");
		// firefoxProfile.setPreference("network.proxy.ssl_port", 31280);
		firefoxProfile.setPreference("print.always_print_silent", true);
		capabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
		return capabilities;
	}

	public DesiredCapabilities getFireFoxDesiredCapabilities1(String downloadPath) {
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();

		FirefoxOptions options = new FirefoxOptions();
		options.setCapability("download.prompt_for_download", false);
		options.setCapability("security.default_personal_cert", "Select Automatically");
		options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		capabilities.setCapability("chrome.switches",
				Arrays.asList("--start-maximized", "--ignore-certificate-errors", "--ignore-ssl-errors=true",
						"--disable-popup-blocking", "--disable-default-apps", "--auto-launch-at-startup",
						"--always-authorize-plugins"));
		capabilities.setCapability("browser.download.dir", downloadPath);
		options.setCapability("browser.download.folderList", 2);
		options.setCapability("browser.helperApps.alwaysAsk.force", false);
		options.setCapability("browser.download.manager.showWhenStarting", false);
		options.setCapability("print.always_print_silent", true);

		options.setCapability("pdfjs.disabled", true);
		options.setCapability("pref.downloads.disable_button.edit_actions", true);
		options.setCapability("dom.webnotifications.enabled", false);
		options.setCapability("security.default_personal_cert", "Select Automatically");

		options.setCapability("browser.helperApps.neverAsk.openFile",
				"image/jpg,image/pjpg,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/pdf,text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/zip, application/x-zip, application/x-zip-compressed,application/octet-stream");
		options.setCapability("browser.helperApps.neverAsk.saveToDisk",
				"image/jpeg,image/pjpeg,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/pdf,text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/zip, application/x-zip, application/x-zip-compressed,application/octet-stream");
		options.setCapability("network.proxy.type", 1);
		options.setCapability("network.proxy.http", "us-wa.proxymesh.com");
		options.setCapability("network.proxy.http_port", 31280);
		options.setCapability("network.proxy.ssl", "us-wa.proxymesh.com");
		options.setCapability("network.proxy.ssl_port", 31280);

		capabilities.merge(options);
		return capabilities;
	}

	public DesiredCapabilities getTestFireFoxDesiredCapabilities(String downloadPath) {
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		ProfilesIni profile = new ProfilesIni();

		FirefoxProfile ffProfile = profile.getProfile("Automation Testing");
		capabilities.setCapability("firefox_profile", ffProfile);

		ffProfile.setAcceptUntrustedCertificates(true);
		ffProfile.setAssumeUntrustedCertificateIssuer(false);
		return capabilities;
	}

	public DesiredCapabilities getInternetExplorerDesiredCapabilities(String downloadPath) {
		DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
		// capabilities.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		capabilities.setCapability(CapabilityType.SUPPORTS_ALERTS, true);
		capabilities.setCapability(CapabilityType.HAS_NATIVE_EVENTS, false);
		capabilities.setCapability(InternetExplorerDriver.UNEXPECTED_ALERT_BEHAVIOR, UnexpectedAlertBehaviour.IGNORE);
		capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, false);
		capabilities.setCapability(InternetExplorerDriver.ENABLE_ELEMENT_CACHE_CLEANUP, false);
		capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false);
		InternetExplorerOptions internetExplorerOptions = new InternetExplorerOptions();
		internetExplorerOptions.merge(capabilities);
		internetExplorerOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
		return capabilities;
	}

	public void setAlertBehaviorCapabilities(DesiredCapabilities capabilities) {
		capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, alertCapability.get());
		currentAlertCapability.set(alertCapability.get());
	}

	public void zoomInZoomOut(final WebDriver driver, String value) throws AWTException {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("document.body.style.zoom=(" + value + ");");
		/*
		 * Robot robot = new Robot(); robot.keyPress(KeyEvent.VK_CONTROL);
		 * robot.keyPress(KeyEvent.VK_MINUS); robot.keyRelease(KeyEvent.VK_CONTROL);
		 * robot.keyRelease(KeyEvent.VK_MINUS); robot.keyRelease(KeyEvent.VK_CONTROL);
		 * robot.keyRelease(KeyEvent.VK_MINUS);
		 */
	}

	public void zoomInZoomOut(final WebDriver driver) {
		for (int i = 0; i < 10; i++) {
			driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL, Keys.ADD));
		}
	}

	public ReturnBooleanAndString sendPvtOpcCompanyDetails(String requestUrl, Map<String, String> dataMap) {
		try {
			String captchaText = null;
			Boolean flag = false;
			String cin = dataMap.get("CIN");
			String authCompanyName = dataMap.get("authCompanyName");
			String dateOfIncorporation = dataMap.get("dateOfIncorporation");
			String rocCode = dataMap.get("rocCode");
			String regNo = dataMap.get("regNo");
			String companyCat = dataMap.get("companyCat");
			String companySubCat = dataMap.get("companySubCat");
			String companyClass = dataMap.get("companyClass");
			String authCapital = dataMap.get("authCapital");
			String paidUpCapital = dataMap.get("paidUpCapital");
			String noOfMembers = dataMap.get("noOfMembers");
			String regAddress = dataMap.get("regAddress");
			String otherAddress = dataMap.get("otherAddress");
			String emailId = dataMap.get("emailId");
			String listedOrNot = dataMap.get("listedOrNot");
			String activeCompliance = dataMap.get("activeCompliance");
			String isSuspended = dataMap.get("isSuspended");
			String agmDate = dataMap.get("agmDate");
			String balSheetDate = dataMap.get("balSheetDate");
			String companyStatus = dataMap.get("companyStatus");
			String ticketId = dataMap.get("ticketId");

			String payload = "{\"regNo\":\"" + regNo + "\",\"cin\":\"" + cin + "\"," + "\"authCompanyName\":\""
					+ authCompanyName + "\"," + "\"dateOfIncorporation\":\"" + dateOfIncorporation + "\","
					+ "\"rocCode\":\"" + rocCode + "\"," + "\"regNo\":\"" + regNo + "\"," + "\"companyCat\":\""
					+ companyCat + "\"," + "\"companySubCat\":\"" + companySubCat + "\"," + "\"companyClass\":\""
					+ companyClass + "\"," + "\"authCapital\":\"" + authCapital + "\"," + "\"paidUpCapital\":\""
					+ paidUpCapital + "\"," + "\"noOfMembers\":\"" + noOfMembers + "\"," + "\"regAddress\":\""
					+ regAddress + "\"," + "\"otherAddress\":\"" + otherAddress + "\"," + "\"emailId\":\"" + emailId
					+ "\"," + "\"listedOrNot\":\"" + listedOrNot + "\"," + "\"activeCompliance\":\"" + activeCompliance
					+ "\"," + "\"isSuspended\":\"" + isSuspended + "\"," + "\"agmDate\":\"" + agmDate + "\","
					+ "\"balSheetDate\":\"" + balSheetDate + "\"," + "\"companyStatus\":\"" + companyStatus + "\"}";

			URL url = new URL(requestUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			writer.write(payload);
			writer.close();
			if (connection.getResponseCode() == 200) {
				flag = true;
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer jsonString = new StringBuffer();
				String line;
				while ((line = br.readLine()) != null) {
					jsonString.append(line);
				}
				br.close();
				connection.disconnect();
				JSONObject jsonObject = new JSONObject(jsonString.toString());
				// Ticket details
				JSONObject ticketDetails = (JSONObject) jsonObject.get("captchaText");
				captchaText = ticketDetails.getString("captchaText");
			} else {
				logger.error("For ticket #  " + ticketId + " Getting response code from Elastic search as '"
						+ connection.getResponseCode() + "'");
			}
			return new ReturnBooleanAndString(flag, captchaText);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public ReturnBooleanAndString sendLlpCompanyDetails(String requestUrl, Map<String, String> dataMap) {
		try {
			String captchaText = null;
			Boolean flag = false;
			String cin = dataMap.get("CIN");
			String authCompanyName = dataMap.get("authCompanyName");
			String dateOfIncorporation = dataMap.get("dateOfIncorporation");
			String rocCode = dataMap.get("rocCode");
			String noOfPartners = dataMap.get("noOfPartners");
			String noOfDesignatedPartners = dataMap.get("noOfDesignatedPartners");
			String regAddress = dataMap.get("regAddress");
			String previousCompanyDetails = dataMap.get("previousCompanyDetails");
			String emailId = dataMap.get("emailId");
			String totOblCont = dataMap.get("totOblCont");
			String mainDivision = dataMap.get("mainDivision");
			String mainDivisionDesc = dataMap.get("mainDivisionDesc");
			String solvency = dataMap.get("solvency");
			String annualReturn = dataMap.get("annualReturn");
			String llpStatus = dataMap.get("llpStatus");
			String ticketId = dataMap.get("ticketId");

			String payload = "{\"cin\":\"" + cin + "\"," + "\"authCompanyName\":\"" + authCompanyName + "\","
					+ "\"dateOfIncorporation\":\"" + dateOfIncorporation + "\"," + "\"rocCode\":\"" + rocCode + "\","
					+ "\"noOfPartners\":\"" + noOfPartners + "\"," + "\"noOfDesignatedPartners\":\""
					+ noOfDesignatedPartners + "\"," + "\"regAddress\":\"" + regAddress + "\","
					+ "\"previousCompanyDetails\":\"" + previousCompanyDetails + "\"," + "\"emailId\":\"" + emailId
					+ "\"," + "\"totOblCont\":\"" + totOblCont + "\"," + "\"mainDivision\":\"" + mainDivision + "\","
					+ "\"mainDivisionDesc\":\"" + mainDivisionDesc + "\"," + "\"solvency\":\"" + solvency + "\","
					+ "\"annualReturn\":\"" + annualReturn + "\"," + "\"llpStatus\":\"" + llpStatus + "\"}";

			URL url = new URL(requestUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			writer.write(payload);
			writer.close();
			if (connection.getResponseCode() == 200) {
				flag = true;
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer jsonString = new StringBuffer();
				String line;
				while ((line = br.readLine()) != null) {
					jsonString.append(line);
				}
				br.close();
				connection.disconnect();
				JSONObject jsonObject = new JSONObject(jsonString.toString());
				// Ticket details
				JSONObject ticketDetails = (JSONObject) jsonObject.get("captchaText");
				captchaText = ticketDetails.getString("captchaText");
			} else {
				logger.error("For ticket #  " + ticketId + " Getting response code from Elastic search as '"
						+ connection.getResponseCode() + "'");
			}
			return new ReturnBooleanAndString(flag, captchaText);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public String cleanTextContent(String inputData) {

		// strips off all non-ASCII characters
		inputData = inputData.replaceAll("[^\\x00-\\x7F]", "");

		// erases all the ASCII control characters
		inputData = inputData.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

		// removes non-printable characters from Unicode
		inputData = inputData.replaceAll("\\p{C}", "");
		return inputData.trim();
	}

	public String getDatesDifference(String beginDate, String endDate, String dateFormat, String expectedDifference) {
		String difference = null;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date startDate = null;
		Date lastDate = null;
		try {
			startDate = sdf.parse(beginDate);
			lastDate = sdf.parse(endDate);
			int startYear = startDate.getYear();
			int endYear = lastDate.getYear();
			int yearDiff = endYear - startYear;
			difference = String.valueOf(yearDiff);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return difference;
	}

	public boolean postincomeTaxLoginValidations(final WebDriver driver, Map<String, String> dataMap) {
		boolean flag = false;
		try {
			Thread.sleep(1000);
			if (isElementPresent(driver, By.xpath(CommonVariables.PATH_ADD_UPDATE_ALERT))) {
				clickByxpath(driver, CommonVariables.BTN_UPDATE_ALERT_CONTINUE);
				clickByxpath(driver, CommonVariables.BTN_UPDATE_ALERT_SKIP);
			}
			if (isElementPresent(driver, By.xpath("//*[@id=\"loadAndOpenAadhaarForCorp\"]"))) {
				clickByxpath(driver, "html/body/div[7]/div[1]/button/span[1]");
			}
			Thread.sleep(1000);
		} catch (Exception e) {
			logger.error(e);
		}
		return flag;
	}

	/**
	 * 
	 * @param dirPath
	 * @return
	 */
	/* Get the latest file from a specific directory */
	public File getLatestFilefromDir(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			return null;
		}
		File lastModifiedFile = files[0];
		for (int i = 1; i < files.length; i++) {
			if (lastModifiedFile.lastModified() < files[i].lastModified()) {
				lastModifiedFile = files[i];
			}
		}
		return lastModifiedFile;
	}

	public boolean isElementDisplayed(WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 1);
			wait.until(ExpectedConditions.visibilityOf(element));
			return element.isDisplayed();
		} catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.StaleElementReferenceException
				| org.openqa.selenium.TimeoutException e) {
			return false;
		}
	}

	public boolean isElementVisible(String cssLocator) {
		return driver.findElement(By.cssSelector(cssLocator)).isDisplayed();
	}

	public void waitForElementToBeGone(WebElement element, int timeout) {
		if (isElementDisplayed(element)) {
			new WebDriverWait(driver, timeout).until(ExpectedConditions.not(ExpectedConditions.visibilityOf(element)));
		}
	}

	public void Spice_Declaration_Loader(final WebDriver driver) throws InterruptedException {

		for (int i = 1; i <= 200; ++i) {

			int Loader = driver.findElements(By.xpath("//h5[contains(text(),'Informational Alert')]")).size();
			if (Loader == 0) {
				Thread.sleep(1000);

			} else {
				break;
			}
		}

	}

	public static String webElementScreenShot(WebDriver driver, WebElement element) {

		String fileStorage = "\\192.168.1.9\\VakilSearch\\Balaji\\captchaScreenshots\\check" + ".PNG";
		Screenshot screenshot = new AShot().takeScreenshot(driver, element);
		try {
			ImageIO.write(screenshot.getImage(), "PNG", new File(fileStorage));
		} catch (IOException e) {

			e.printStackTrace();
		}
		return fileStorage;

	}

	public void deleteFolder(String folderPath) {
		File myObj = new File(folderPath);
		if (myObj.delete()) {
			System.out.println("Deleted the folder: " + myObj.getName());
		} else {
			System.out.println("Failed to delete the folder.");
		}
	}

	public static String convertImageToBase64String(String imagePath) throws IOException {
		byte[] fileContent = FileUtils.readFileToByteArray(new File(imagePath));
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		return encodedString;
	}

	public static void scrollTop(WebDriver driver) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(0, 0)");

		} catch (Exception e) {
			System.err.println("Scrolling top is not working ");
		}
	}

	public static void scrollDown(WebDriver driver) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("scroll(0, 600)");
			logger.info("Scroll down is passed");
		} catch (Exception e) {
			logger.info("Scroll down is Failed");
		}
	}

	public static void waitUntillElementVisiblity(WebDriverWait wait, String Xpath, WebDriver driver) {
		WebElement element = driver.findElement(By.xpath(Xpath));
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public static void wait(int timeInSeconds) {
		try {
			Thread.sleep(timeInSeconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
