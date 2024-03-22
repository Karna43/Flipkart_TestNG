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

public class FilterLaptops {
	public static WebDriver driver;
	public static List<WebElement> dellLaptopNames;
	public static String dellLaptopName;
	public static int currentMethod = 0;
	public static File file;
	
	@BeforeSuite(groups = "general")
	public void beforeSuite() {
		System.out.println("\nBefore Suite\t: FilterLaptops");
	}
	
	@BeforeTest(groups = "general")
	public void beforeTest() {
		System.out.println("Before Test\t: FilterLaptops");
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
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get("https://www.flipkart.com/");
		System.out.println("Before Class\t: FilterLaptops");
	}
	
	@BeforeMethod(groups = "general")
	public void beforeMethod() {
		currentMethod+=1;
		System.out.println("Before Method\t: FilterLaptops.Method"+currentMethod);
	}
	
	@Parameters({"laptop"})
	@Test(priority=0, groups = "checkFilter")
	public void search_Laptop(@Optional("computer") String value) {
		WebElement search = driver.findElement(By.xpath("//input[contains(@placeholder,'Search')]"));
		search.sendKeys(value, Keys.ENTER);
	}
	
	@Test(priority=1, groups = "checkFilter")
	public void click_Brand() {
		driver.findElement(By.xpath("//div[text()='Brand']")).click();
	}
	
	@Test(priority=2, groups = "checkFilter")
	public void check_Dell_Brand(){
			driver.findElement(By.xpath("//div[text()='DELL']")).click();
	}
	
	@Test(priority=3, groups = "checkFilter", dependsOnMethods = "check_Dell_Brand" )
	public void get_Filtered_Dell_Laptops(){
		try {
			Thread.sleep(3000);
			dellLaptopNames = driver.findElements(By.xpath("//div[contains(@class,'rR01T')]"));
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test(priority=4, groups = "checkFilter", dependsOnMethods = "get_Filtered_Dell_Laptops")
	public void validate_Filtered_Data(){
		for(int i=0;i<5;i++) {
			String laptopName = dellLaptopNames.get(i).getText();
			dellLaptopName = laptopName.substring(0, laptopName.indexOf("("));
			if(dellLaptopName.startsWith("DELL")) {
				System.out.println(dellLaptopName+" : Related Product");
			}
			else {
				System.out.println(dellLaptopName+" : Not a Related Product");
			}
		}
	}
	
	@Test(priority=5, groups = "storeData")
	public void create_Excel_File(){
		try {
			Thread.sleep(2000);
			file = new File("C:\\Users\\Karna\\Pictures\\DellLaptopData.xlsx");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test(priority=6, groups = "storeData")
	public void store_Filtered_Data(){
		try {
			FileOutputStream writeFile = new FileOutputStream(file);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Dell Laptops");
			for(int i=0;i<dellLaptopNames.size();i++) {
				XSSFRow row = sheet.createRow(i);
				XSSFCell cell = row.createCell(0);
				cell.setCellValue(dellLaptopNames.get(i).getText());
			}
			workbook.write(writeFile);
			writeFile.close();
			System.out.println("List of filtered dell laptop names updated in Excel");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@AfterMethod(groups = "general")
	public void afterMethod() {
		System.out.println("After Method\t: FilterLaptops.Method"+currentMethod);
	}
	
	@AfterClass(groups = "general")
	public void browserClose() {
	System.out.println("After Class\t: FilterLaptops");
	driver.quit();
	}
	
	@AfterTest(groups = "general")
	public void afterTest() {
		System.out.println("After Test\t: FilterLaptops");
	}
	
	@AfterSuite(groups = "general")
	public void afterSuite() {
		System.out.println("After Suite\t: FilterLaptops");
	}
}