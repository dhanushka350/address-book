package com.akvasoft.addressbook.config;


import org.openqa.selenium.By;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Scraper implements InitializingBean {

    @Autowired
    private Repo repo;

    private int startPage = 1;
    private int endPage = 20000;
//    private int endPage = 999999;

    @Override
    public void afterPropertiesSet() throws Exception {
        String url = "https://order.foodsby.com/";
        // 999999 pages
        startThreads(url);
    }

    private boolean scrape(String s, FirefoxDriver driver) throws Exception, SessionNotCreatedException {
        driver.get(s);
        WebElement building_address = null;
        WebElement panel = null;
        Modal modal = new Modal();
        try {
            panel = driver.findElementByXPath("/html/body/div[1]/div/div/aside/div[2]");
            building_address = panel.findElement(By.className("location-building_address"));
        } catch (Exception e) {
            System.err.println("NO DATA IN " + s);
            return false;
        }
        String name = building_address.findElements(By.xpath("./*")).get(0).getAttribute("innerText");
        String street = building_address.findElements(By.xpath("./*")).get(1).getAttribute("innerText");
        String city = building_address.findElements(By.xpath("./*")).get(2).getAttribute("innerText");
        String state = city.split(",")[1];

        System.out.println("NAME :  " + name);
        System.out.println("STREET :  " + street);
        System.out.println("CITY :  " + city);
        System.out.println("STATE :  " + state);

        modal.setCity(city);
        modal.setLink(s);
        modal.setName(name);
        modal.setState(state);
        modal.setStreet(street);
        saveAddress(modal);

        modal = null;
        building_address = null;
        panel = null;
        name = null;
        street = null;
        city = null;
        state = null;

        System.gc();
        return true;
    }

    private synchronized void saveAddress(Modal modal) throws Exception {
        repo.save(modal);

    }

    private void startThreads(String url) {
        for (int x = 1; x < 11; x++) {
            System.err.println("DRIVER " + x + " INITIALIZED.");
            int y = getStartingPage(x);
            int z = getEndPage(x);

            new Thread(() -> {
                FirefoxDriver driver = new DriverInitializer().getFirefoxDriver();
                for (int i = y; i < z; i++) {
                    try {
                        scrape(url + i, driver);
                    } catch (SessionNotCreatedException e) {
                        i--;
                        driver.close();
                        driver = new DriverInitializer().getFirefoxDriver();
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }).start();
        }
    }

    private int getEndPage(int x) {
        if (x == 1) {
            return endPage;
        } else if (x == 2) {
            return 40000;
        } else if (x == 3) {
            return 60000;
        } else if (x == 4) {
            return 80000;
        } else if (x == 5) {
            return 100000;
        } else if (x == 6) {
            return 120000;
        } else if (x == 7) {
            return 140000;
        } else if (x == 8) {
            return 160000;
        } else if (x == 9) {
            return 180000;
        } else {
            return 200000;
        }
    }

    private int getStartingPage(int x) {
        if (x == 1) {
            return 1;
        } else if (x == 2) {
            return 20001;
        } else if (x == 3) {
            return 40001;
        } else if (x == 4) {
            return 60001;
        } else if (x == 5) {
            return 80001;
        } else if (x == 6) {
            return 100001;
        } else if (x == 7) {
            return 120001;
        } else if (x == 8) {
            return 140001;
        } else if (x == 9) {
            return 160001;
        } else {
            return 180001;
        }

    }
}
