package gametv.com.game.tv;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.JavascriptExecutor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.restassured.RestAssured;
import junit.framework.Assert;

/**
 * Author: Aditya Pratap Singh
 */

public class GameActionClass 
{
	//storing locators
	private static String availableGamesSection = "section.available-games h3";
	private static String gameList = "//div[@id='game_list']//li//figcaption";
	private static String pageHeading = "//div[@class='banner-content']//h1";
    public static void main( String[] args ) throws IOException, InterruptedException
    {
    	    	System.setProperty("webdriver.chrome.driver","D:\\chromedriver.exe");
    			WebDriver driver = new ChromeDriver();
    			//driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    			WebDriverWait wait = new WebDriverWait(driver, 20);
    			
    			//Writing Code to write in Excel file
    			Workbook workbook = new HSSFWorkbook();
    		    Sheet sheet = workbook.createSheet();    		 
    		    int rowCount = 0;    	
    		    Row titlerow = sheet.createRow(0);
    		    Cell cellTitle = titlerow.createCell(0);
    		    cellTitle.setCellValue("S.No.");    		 
    		    cellTitle = titlerow.createCell(1);
    		    cellTitle.setCellValue("Game name");    		 
    		    cellTitle = titlerow.createCell(2);
    		    cellTitle.setCellValue("Page URL");
    		    cellTitle = titlerow.createCell(3);
    		    cellTitle.setCellValue("Page Status");
    		    cellTitle = titlerow.createCell(4);
    		    cellTitle.setCellValue("Tournament count");
    		    
    	        String baseUrl = "https://www.game.tv/";
    	        driver.manage().window().maximize();
    	        driver.get(baseUrl);
    	        Assert.assertTrue(driver.getTitle().contains("Game.tv"));
    	        System.out.print("Successfully launched page: " + driver.getTitle());
    	        Actions actions = new Actions(driver);
    	        actions.moveToElement(driver.findElement(By.cssSelector(availableGamesSection)));
    	        actions.perform();
    	        
    	        List<WebElement> gameListName = new ArrayList<>();
    	        List<String> allLinkNames = new ArrayList<>();
    	        gameListName = driver.findElements(By.xpath(gameList));int i=1;
    	        
    	        for(WebElement els : gameListName)
    	        {
    	        	allLinkNames.add(els.getText());
    	        }
    	        for(String linkName : allLinkNames)
    	        {
    	        	ExpectedCondition<Boolean> pageLoadCondition = new
    	                    ExpectedCondition<Boolean>() {
    	                        public Boolean apply(WebDriver driver) {
    	                            return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
    	                        }
    	                    };
    	            wait.until(pageLoadCondition);
    	            String name = linkName.substring(0, linkName.indexOf("Tournaments"));
    	            	
    	        	System.out.println("Game name "+ i++ + " " + name);
    	        	WebElement gameLink = driver.findElement(By.xpath("//ul/li//figcaption[contains(text(),'"+name+"')]/parent::a"));
    	            wait.until(ExpectedConditions.elementToBeClickable(gameLink));
    	           // int statusCode = RestAssured.get(gameLink.getAttribute("href")).statusCode();
    	            String href_url = gameLink.getAttribute("href");
    	            System.out.println(href_url);
    	            URL url = new URL(href_url);
    	            HttpURLConnection http = (HttpURLConnection)url.openConnection();
    	            int statusCode = http.getResponseCode();
    	            System.out.println("status code = "+statusCode);
    	            for(int k=0;k<3;k++)
	        		{
    	            	try{gameLink.click();break;}
        	        	catch(StaleElementReferenceException e)
        	        	{ //do nothing and retry        	        		
        	        	}
    	            	catch(ElementClickInterceptedException e)
        	        	{ //do nothing and retry   //jsexecuter click by i
        	        	}
    	            	
	        		}
    	        	System.out.println("Clicked on game link: "+ name + "Tournaments");
    	        	WebElement pageHeadingName = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(pageHeading)));
    	        	System.out.println("-->"+pageHeadingName.getText());
    	        	Assert.assertTrue(pageHeadingName.getText().contains(name));
    	        	System.out.println(" Suucessfully navigated to "+  name +" Tournaments page");
    	        	String tournamentCount = driver.findElement(By.className("count-tournaments")).getText();
    	        	tournamentCount =tournamentCount.replaceAll(",", "");
    	        	System.out.println("->"+tournamentCount);
    	        	
    	        	//write to file before navigate back:
    	        	Row row = sheet.createRow(++rowCount);
    	        	Cell cell = row.createCell(0);
    	            cell.setCellValue(i-1);
    	            cell = row.createCell(1);
    	            cell.setCellValue(name);
    	            cell = row.createCell(2);
    	            cell.setCellValue(href_url);
    	            cell = row.createCell(3);
    	            cell.setCellValue(statusCode);
    	            cell = row.createCell(4);
    	            cell.setCellValue(tournamentCount);
    	            try (FileOutputStream outputStream = new FileOutputStream("./savedBook.xls")) {
    	                workbook.write(outputStream);
    	            }
    	        	
    	        	driver.navigate().back();
    	        	System.out.print("Clicked to navigate back to main page");
    	        	Thread.sleep(2000);
    	        	
    	        }
    	        

    }
}
