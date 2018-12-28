package mykyta.minenko.gmail.com;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.*;

import org.apache.commons.io.FileUtils;

public class FirstTest {

    public static void sleep(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void takeScreen(WebDriver driver, String name) throws IOException {
        File f = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(f, new File(name));
    }

    public static ArrayList<String> getHrefs(WebDriver driver) {
        String path = "//*[@class='r']/a";
        ArrayList<String> hrefs = new ArrayList<String>();
        List<WebElement> list = driver.findElements(By.xpath(path));
        for (WebElement el : list) hrefs.add(el.getAttribute("href"));
        return hrefs;
    }

    public static String hrefToNextPage(WebDriver driver){
        String path = "//*[@id=\"pnnext\"]";
        if (driver.findElements(By.xpath(path)).size() != 0)
            return driver.findElement(By.xpath(path)).getAttribute("href");
        else return "null";
    }

    public static String searchHref(ArrayList<String> hrefs, String value){
        for (String href: hrefs) if (href.toLowerCase().contains(value.toLowerCase())) return href;
        return "null";
    }
    public static WebDriver startPage(WebDriver driver, String address){
        driver.get(address);
        System.out.println(driver.getCurrentUrl());
        return driver;
    }

    public static WebDriver googleNextPage(WebDriver driver, String input){
        String path1 = "/html/body/div/div[3]/form/div[2]/div/div[1]/div/div[1]/input";
        String path2 = "/html/body/div/div[3]/form/div[2]/div/div[3]/center/input[1]";
        driver.findElement(By.xpath(path1)).sendKeys(input);
        driver.findElement(By.xpath(path2)).click();
        System.out.println("Current page: " + driver.getCurrentUrl());
        return driver;
    }

    public static WebDriver enterMinPrice(WebDriver driver, Integer minPrice){
        driver.findElement(By.xpath("//*[@id=\"price[min]\"]")).sendKeys(minPrice.toString());
        driver.findElement(By.xpath("//*[@id=\"submitprice\"]")).click();
        sleep(3000);
        System.out.println("Current page: " + driver.getCurrentUrl());
        return driver;
    }

    public static ArrayList<Float> getPriceList(WebDriver driver){
        String path = "//*[@class='g-price-uah']";
        List<WebElement> list = driver.findElements(By.xpath(path));
        ArrayList<Float> prices = new ArrayList<Float>();
        for (WebElement el : list)
            prices.add(Float.valueOf(el.getText().replaceAll("\\D+","")));
        return prices;
    }

    @Test
    public void googleTest1() throws IOException {
        System.setProperty("webdriver.gecko.driver", "/home/mykyta/geckodriver");
        WebDriver driver = new FirefoxDriver();
        Integer counter = 1;
        driver = startPage(driver,"https://www.google.com/");
        driver = googleNextPage(driver,"Guitars");
        String nextPageHref = new String();
        String firmHref = new String();
        do {
            nextPageHref = hrefToNextPage(driver);
            if (nextPageHref.equals("null")) break;
            driver = startPage(driver, nextPageHref);
            ArrayList<String> hrefs = getHrefs(driver);
            firmHref = searchHref(hrefs,"Vintage");
            counter++;
        } while (firmHref.equals("null"));
        takeScreen(driver, "firmOnPageNumber_" + counter.toString() + ".png");
        driver.close();
        Assert.assertNotEquals(firmHref,"null");
    }

    @Test
    public void GoogleTest2() throws IOException {
        System.setProperty("webdriver.gecko.driver", "/home/mykyta/geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver = startPage(driver,"https://www.google.com/");
        driver = googleNextPage(driver,"Guitars");
        ArrayList<String> hrefs = getHrefs(driver);
        String firmHref = searchHref(hrefs,"Fender");
        takeScreen(driver, "firmOnPage1" + ".png");
        driver.close();
        Assert.assertNotEquals(firmHref,"null");
    }

    @Test
    public void googleTest3() throws IOException {
        System.setProperty("webdriver.gecko.driver", "/home/mykyta/geckodriver");
        WebDriver driver = new FirefoxDriver();
        Integer counter = 1;
        driver = startPage(driver,"https://www.google.com/");
        driver = googleNextPage(driver,"apollodc 135c");
        String nextPageHref = new String();
        String firmHref = new String();
        do {
            takeScreen(driver, "firmNotMatch_" + counter.toString() + ".png");
            nextPageHref = hrefToNextPage(driver);
            if (nextPageHref.equals("null")) break;
            driver = startPage(driver, nextPageHref);
            counter ++;
            ArrayList<String> hrefs = getHrefs(driver);
            firmHref = searchHref(hrefs,"blabla");
        } while (firmHref.equals("null"));
        driver.close();
        Assert.assertEquals(firmHref,"null");
    }

    @Test
    public void rozetkaTest(){
        System.setProperty("webdriver.gecko.driver", "/home/mykyta/geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver = startPage(driver,"https://rozetka.com.ua/pivo/c4626589/");
        driver = enterMinPrice(driver,200);
        ArrayList<Float> priceList = getPriceList(driver);
        boolean check=true;
        for (float price: priceList){
            if (price<200) check=false;
        }
        driver.close();
        Assert.assertTrue(check);
    }
}