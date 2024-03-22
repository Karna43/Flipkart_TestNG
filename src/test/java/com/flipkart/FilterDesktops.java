package com.flipkart;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

public class FilterDesktops {
	public static WebDriver driver;
	public static List<WebElement> HPdesktopNames;
	public static String HPdesktopName;
	public static int currentMethod = 0;
	public static File file;
	public static Actions action;
	
	@BeforeSuite(groups = "general")
	public void beforeSuite() {
		System.out.println("\nBefore Suite\t: FilterDesktops");
	}
	
	@BeforeTest(groups = "general")
	public void beforeTest() {
		System.out.println("Before Test\t: FilterDesktops");
	}
	
	@Parameters({"browser"})
	@BeforeClass(groups = "general")
	public static void launch_flipkart_website(String browserName) {
		if(browserName.equals("chrome")) {
			WebDriverManager.chromedriver().setup();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("start-maximized");
			driver = new ChromeDriver(options);
		}
		else {
			WebDriverManager.edgedriver().setup();
			EdgeOptions options = new EdgeOptions();
			options.addArguments("start-maximized");
			driver = new EdgeDriver(options);
		}
		action = new Actions(driver);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get("https://www.flipkart.com/");
		System.out.println("Before Class\t: FilterDesktops");
	}
	
	@BeforeMethod(groups = "general")
	public void beforeMethod() {
		currentMethod+=1;
		System.out.println("Before Method\t: FilterDesktops.Method"+currentMethod);
	}
	
	@Parameters({"desktop"})
	@Test(priority=0, groups = "checkFilter")
	public void search_Desktop(@Optional("computer") String value) {
		WebElement search = driver.findElement(By.xpath("//input[contains(@placeholder,'Search')]"));
		search.sendKeys(value, Keys.ENTER);
	}
	
	@Test(priority=1, groups = "checkFilter")
	public void scroll_To_Brand(){
		try {
			Thread.sleep(2000);
			action.moveToElement(driver.findElement(By.xpath("//div[text()='HP']"))).build().perform();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test(priority=2, groups = "checkFilter")
	public void check_Hp_Brand(){
			driver.findElement(By.xpath("//div[text()='HP']")).click();
	}
	
	@Test(priority=3, groups = "checkFilter", dependsOnMethods = "check_Hp_Brand")
	public void get_Filtered_Hp_Desktops(){
		try {
			Thread.sleep(3000);
			HPdesktopNames = driver.findElements(By.xpath("//a[@class='s1Q9rs']"));
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test(priority=4, groups = "checkFilter", dependsOnMethods = "get_Filtered_Hp_Desktops")
	public void validate_Filtered_Data(){
		for(int i=0;i<5;i++) {
			String desktopName = HPdesktopNames.get(i).getText();
			HPdesktopName = desktopName.substring(0, desktopName.indexOf("("));
			if(HPdesktopName.startsWith("HP")) {
				System.out.println(HPdesktopName+" : Related Product");
			}
			else {
				System.out.println(HPdesktopName+" : Not a Related Product");
			}
		}
	}
	
	@Test(priority=5, groups = "storeData")
	public void create_Excel_File(){
		try {
			Thread.sleep(2000);
			file = new File("C:\\Users\\Karna\\Pictures\\HpDesktopData.xlsx");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test(priority=6, groups = "storeData")
	public void store_Filtered_Data(){
		try {
			FileOutputStream writeFile = new FileOutputStream(file);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("HP Desktops");
			for(int i=0;i<HPdesktopNames.size();i++) {
				XSSFRow row = sheet.createRow(i);
				XSSFCell cell = row.createCell(0);
				cell.setCellValue(HPdesktopNames.get(i).getText());
			}
			workbook.write(writeFile);
			writeFile.close();
			System.out.println("List of filtered HP desktop names updated in Excel");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@AfterMethod(groups = "general")
	public void afterMethod() {
		System.out.println("After Method\t: FilterDesktops.Method"+currentMethod);
	}
	
	@AfterClass(groups = "general")
	public void browserClose() {
	System.out.println("After Class\t: FilterDesktops");
	driver.quit();
	}
	
	@AfterTest(groups = "general")
	public void afterTest() {
		System.out.println("After Test\t: FilterDesktops");
	}
	
	@AfterSuite(groups = "general")
	public void afterSuite() {
		System.out.println("After Suite\t: FilterDesktops");
	}
}