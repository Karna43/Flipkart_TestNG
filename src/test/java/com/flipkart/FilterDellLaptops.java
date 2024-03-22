package com.flipkart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import io.github.bonigarcia.wdm.WebDriverManager;

public class FilterDellLaptops {
	public static WebDriver driver;
	public static Actions action;
	public static List<WebElement> dellLaptopName, dellLaptopPrice;
	public static WebElement maxPriceOption, minPriceOption;
	public static SoftAssert softAssert;
	public static JavascriptExecutor js;
	public static File fileLocation;
	public static FileOutputStream writeFile;
	public static FileInputStream readFile;
	public static XSSFWorkbook workbook;
	public static XSSFSheet sheet;
	public static XSSFCell lapPrice;
	public static String excelLaptopName, actualLaptopName;
	public static String excelData, actualData;
	public static int excelLaptopPrice, actualLaptopPrice;
	public static Select minPrice, maxPrice;
	public static WebDriverWait wait;
	
	@DataProvider (name="laptop")
	public Object[][] television(){
		return new Object[][] {{"laptop"}};
	}
	@BeforeClass
	public static void launch_flipkart_website() {
		WebDriverManager.edgedriver().setup();
		EdgeOptions options = new EdgeOptions();
		options.addArguments("start-maximized");
		driver = new EdgeDriver(options);
		action = new Actions(driver);
		js = (JavascriptExecutor) driver;
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get("https://www.flipkart.com/");
	}
	@Test(priority=0, dataProvider = "laptop")
	public void search_Laptop(String value) {
		WebElement search = driver.findElement(By.xpath("//input[contains(@placeholder,'Search')]"));
		search.sendKeys(value, Keys.ENTER);
	}
	@Test(priority=1)
	public void click_Brand() {
		driver.findElement(By.xpath("//div[text()='Brand']")).click();
	}
	@Test(priority=2)
	public void check_Dell_Brand(){
		try {
			WebElement brand = driver.findElement(By.xpath("//div[text()='DELL']"));
			brand.click();
			Thread.sleep(2000);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	@Test(priority=3)
	public void scroll_To_SSD_Capacity() {
		try {
			
			WebElement processGen = driver.findElement(By.xpath("//div[text()='Processor Generation']"));
			WebElement storageCapacity = driver.findElement(By.xpath("//div[text()='SSD Capacity']"));
			js.executeScript("arguments[0].scrollIntoView(true)", processGen);
			storageCapacity.click();
			Thread.sleep(2000);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	@Test(priority=4)
	public void check_512gb_Capacity(){
		try {
			WebElement ssdCapacity = driver.findElement(By.xpath("//div[text()='512 GB']"));
			ssdCapacity.click();
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test(priority=5)
	public void select_Min_Max_Price(){
		try{
			minPriceOption = driver.findElement(By.xpath("//option[text()='Min']/ancestor::select"));
			maxPriceOption = driver.findElement(By.xpath("//div[contains(@class,'3uDYxP')]/select"));
			minPrice = new Select(minPriceOption);
			maxPrice = new Select(maxPriceOption);
			minPrice.selectByVisibleText("₹40000");
			Thread.sleep(2000);
			maxPrice.selectByVisibleText("₹60000");
			Thread.sleep(2000);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	@Test(priority=6)
	public void get_Filtered_Dell_Laptop(){
		try {
			dellLaptopName = driver.findElements(By.xpath("//div[contains(@class,'rR01T')]"));
			dellLaptopPrice = driver.findElements(By.xpath("//div[contains(@class,'WHN1')]"));
			Thread.sleep(4000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test(priority=7)
	public void create_Excel_File(){
		try {
			Thread.sleep(4000);
			fileLocation = new File("C:\\Users\\Karna\\Pictures\\DellLaptopData.xlsx");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test(priority=8)
	public void store_Filtered_Data_In_Excel(){
		try {
			writeFile = new FileOutputStream(fileLocation);
			workbook = new XSSFWorkbook();
			sheet = workbook.createSheet("Dell Laptops");
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("Laptop Name");
			cell = row.createCell(1);
			cell.setCellValue("Laptop Price");
			for(int i=0;i<dellLaptopName.size();i++) {
				row = sheet.createRow(i+1);
				for(int j=0;j<2;j++) {
					cell = row.createCell(j);
					if(j==0) {
						cell.setCellValue(dellLaptopName.get(i).getText());
					}
					else {
						cell.setCellValue(dellLaptopPrice.get(i).getText());
					}
				}
			}
			workbook.write(writeFile);
			writeFile.close();
			System.out.println("List of filtered dell laptops updated in Excel\n");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	@Test(priority=9)
	public void compare_Excel_Data_With_Actual(){
		try {
			readFile = new FileInputStream(fileLocation);
			workbook = new XSSFWorkbook(readFile);
			sheet = workbook.getSheet("Dell Laptops");
			softAssert = new SoftAssert();
			for(int i=1;i<sheet.getPhysicalNumberOfRows();i++) {
				excelLaptopName = sheet.getRow(i).getCell(0).getStringCellValue();
				lapPrice = sheet.getRow(i).getCell(1);
				actualLaptopName = dellLaptopName.get(i-1).getText();
				
				Assert.assertEquals(actualLaptopName, excelLaptopName);
				
				excelData = lapPrice.getStringCellValue().substring(1).replaceAll(",", "");
				excelLaptopPrice = Integer.parseInt(excelData);
				actualData = dellLaptopPrice.get(i-1).getText().substring(1).replaceAll(",", "");
				actualLaptopPrice = Integer.valueOf(actualData);
				
				softAssert.assertEquals(actualLaptopPrice, excelLaptopPrice);
				
				if(excelLaptopName.contains("DELL") && excelLaptopName.contains("512 GB") &&
						(excelLaptopPrice>=40000 && excelLaptopPrice<=60000)) {
					System.out.println(excelLaptopName+" : Related Product");
				}
				else {
					System.out.println(excelLaptopName+" : Not a related product");
				}
			}
			workbook.close();
			readFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public void browserClose() {
	driver.quit();
	}	
}
