package com.zju.util;

import com.zju.pojo.CompanyAndReview;
import org.apache.ibatis.jdbc.Null;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.zju.pojo.CompanyAndReview;
import com.zju.pojo.Company;
import com.zju.pojo.Review;
import com.zju.pojo.Comment;
import com.zju.util.ltp;

import java.time.Duration;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.text.ParseException;

import com.google.gson.Gson;

public class Crawler {

    //根据企业名爬取企业数据并封装为企业及其相关评论的Class
    public CompanyAndReview crawl(String companyName){
        CompanyAndReview currentCompanyAndReview = new CompanyAndReview();

        try {
            currentCompanyAndReview.setCompany(setCurCompany(companyName));
            currentCompanyAndReview.setReviews(setCurReviewArray(companyName,currentCompanyAndReview.getCompany()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentCompanyAndReview;
    }

    // 解析注册资本文本内容为 BigDecimal 类型
    private static BigDecimal parseRegisteredCapital(String capitalText) {
        // 移除非数字部分，只保留数字和小数点
        String cleanedText = capitalText.replaceAll("[^0-9.]", "");
        // 将字符串转换为 BigDecimal 类型
        return new BigDecimal(cleanedText);
    }

    private static Comment CommentAnalysis(String commentContext) throws IOException {
        ltp Myltp =new ltp(commentContext);
        String result = ltp.itp(commentContext);
        Gson gson = new Gson();
        Comment curComment = gson.fromJson(commentContext,Comment.class);
        return curComment;
    }

    private static double CommentScore(Comment curComment){
        double score=0;
        if(curComment.getCode()=="10105"){
            return 0;
        }
        else{
            score=curComment.getData().getScore();
            return score*5;
        }
    }

    private static Company setCurCompany(String companyName) {
        // 设置 ChromeDriver 路径，请根据本地情况修改
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\Google\\Chrome\\Application\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        Company currentCompany = new Company();

        try {
            driver.get("https://m.tianyancha.com");
            // 等待时间为10秒
            int waitTimeInSeconds = 10;
            // 将等待时间转换为 Duration 对象
            Duration duration = Duration.ofSeconds(waitTimeInSeconds);
            // 使用 WebDriverWait，并传入 Duration 对象
            WebDriverWait wait = new WebDriverWait(driver, duration);

            WebElement companyNameInput = driver.findElement(By.id("live-search"));
            WebElement searchButton = driver.findElement(By.xpath("/html/body/div[3]/div[1]/div[1]/div/div[2]/div"));

            companyNameInput.sendKeys(companyName);
            searchButton.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-name a")));
            WebElement companyLinkElement = driver.findElement(By.cssSelector(".search-name a"));

            String companyLink = companyLinkElement.getAttribute("href");
            System.out.println("公司链接: " + companyLink);

            companyLinkElement.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));

            String pageSource = driver.getPageSource();
            Document document = Jsoup.parse(pageSource);

            // 在这里可以使用 jsoup 的方法提取需要的信息
            //id
            String curBrn = document.select("div.single:containsOwn(工商注册号) + div.value").text();
            Integer curId=Integer.parseInt(curBrn);
            currentCompany.setId(curId);

            //companyName
            String curCompanyName = document.select("div.inner-name.inner-title").text();
            currentCompany.setCompanyName(curCompanyName);

            //注册资本文本内容,并将注册资本文本内容转换为 BigDecimal 类型
            String capitalText = document.select("div.divide-title:containsOwn(注册资本) + div.divide-val").text();
            BigDecimal curRegisteredCapital = parseRegisteredCapital(capitalText);
            currentCompany.setRegisteredCapital(curRegisteredCapital);

            //registrationType
            String curRegistrationType = document.select("div:contains(企业类型) + div.value").text();
            currentCompany.setRegistrationType(curRegistrationType);

            //注册日期registrationTime
            String regDateString = document.select("div:contains(核准日期) + div.value").text();
            LocalDate curRegistrationTime = LocalDate.parse(regDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            currentCompany.setRegistrationTime(curRegistrationTime);

            //成立日期establishmentDate
            String dateString = document.select("div:contains(成立日期) + div.value").text();
            LocalDate curEstablishmentDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            currentCompany.setEstablishmentDate(curEstablishmentDate);

            //registeredAddress地址
            String curRegisteredAddress = document.select("div:contains(注册地址) + div.value").text();
            currentCompany.setRegisteredAddress(curRegisteredAddress);

            //businessScope
            Element businessScopeElement = document.select("div:contains(经营范围) + div.value div.auto-folder").first();
            String businessScopeText = businessScopeElement.text();
            currentCompany.setBusinessScope(businessScopeText);

            //contactNumber
            Element innerElement = document.select("span.icon.tic.tic-Phone + span.inner").first();
            if (innerElement != null) {//号码不为空时
                Element phoneElement = innerElement.select("a.change1018").first();
                String phoneNumber = phoneElement.text();
                currentCompany.setContactNumber(phoneNumber);
            } else {//为空时
                String phoneNumber = innerElement.text();
                currentCompany.setContactNumber(phoneNumber);
            }

            //businessStatus
            String curBusinessStatus = document.select("div:contains(经营状态) + div.value").text();
            currentCompany.setBusinessStatus(curBusinessStatus);

            //USCC
            String curUscc = document.select("div.single:containsOwn(统一社会信用代码) + div.value").text();
            currentCompany.setUscc(curUscc);

            //Brn
            currentCompany.setBrn(curBrn);

            //industry行业
            String curIndustry = document.select("div:contains(行业) + div.value").text();
            currentCompany.setIndustry(curIndustry);

            //numberOfEmployees
            String curNumberOfEmployees = document.select("div:contains(人员规模) + div.value").text();
            currentCompany.setNumberOfEmployees(curNumberOfEmployees);

            //numberOfInsured;参保人数
            Element insuranceElement = document.select("div:contains(参保人数) + div.value").first();
            if (insuranceElement != null) {
                String insuranceText = insuranceElement.text();
                if (!insuranceText.equals("-")) {
                    // 将字符串转换为整数
                    int insuranceCount = Integer.parseInt(insuranceText);
                    currentCompany.setNumberOfInsured(insuranceCount);
                }
            }

            //selfRisk
            Element selfRiskElement = document.select(".title:contains(自身风险) .count").first();
            String selfRisk = selfRiskElement.text();
            currentCompany.setSelfRisk(selfRisk);

            //surroundingRisk
            Element surroundingRiskElement = document.select(".title:contains(周边风险) .count").first();
            String surroundingRisk = surroundingRiskElement.text();
            currentCompany.setSurroundingRisk(surroundingRisk);

            //historicalRisk
            Element historyRiskElement = document.select(".title:contains(历史风险) .count").first();
            String historyRisk = historyRiskElement.text();
            currentCompany.setHistoricalRisk(historyRisk);

            //businessRisk
            int businessRiskCount = Integer.parseInt(selfRisk) + Integer.parseInt(surroundingRisk);
            String businessRisk = Integer.toString(businessRiskCount);
            currentCompany.setBusinessRisk(businessRisk);

            //legalRisk
            int legalRiskCount = Integer.parseInt(selfRisk) + Integer.parseInt(surroundingRisk) + Integer.parseInt(historyRisk);
            String legalRisk = Integer.toString(legalRiskCount);
            currentCompany.setLegalRisk(legalRisk);

            //alertReminder
            Element carouselItem = document.select(".carousel-item").first();
            String carouselItemTextContent = carouselItem.text();
            currentCompany.setAlertReminder(carouselItemTextContent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return currentCompany;
    }

    private static ArrayList<Review> setCurReviewArray(String companyName,Company curCompany){
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\Google\\Chrome\\Application\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        ArrayList<Review> currentReviewArray = new ArrayList<>();

        int reviewCount=0;
        int stopMark=0;

        try {

            driver.get("https://www.nowcoder.com/");
            // 等待时间
            int waitTimeInSeconds = 5;
            // 将等待时间转换为 Duration 对象
            Duration duration = Duration.ofSeconds(waitTimeInSeconds);
            // 使用 WebDriverWait，并传入 Duration 对象
            WebDriverWait wait = new WebDriverWait(driver, duration);

            WebElement inputBox = driver.findElement(By.className("el-input__inner"));
            WebElement searchButton = driver.findElement(By.xpath("//span[text()='搜索']"));

            inputBox.sendKeys(companyName);
            searchButton.click();

            // 点击“公司评价”按钮
            WebElement companyReviewButton = driver.findElement(By.xpath("//button[@class='el-button el-button--default search-tag' and contains(text(),'公司评价')]"));
            wait.until(ExpectedConditions.elementToBeClickable(companyReviewButton));
            companyReviewButton.click();

            if(stopMark==0 && reviewCount<200) {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
                String pageSource = driver.getPageSource();
                Document document = Jsoup.parse(pageSource);
                Elements divElements = document.select("div[data-v-69abe70e]");

                // 遍历每个<div>元素
                for (Element divElement : divElements) {
                    Document innerDocument = Jsoup.parseBodyFragment(divElement.html());

                    Review newReview=new Review();

                    reviewCount++;
                    //评价ID（ID）：唯一标识符，用于区分不同的评价记录。INT
                    newReview.setId(reviewCount);

                    //公司ID（Company_ID）：关联到企业表中的公司ID，表示该评价所属的公司。INT
                    newReview.setCompanyId(curCompany.getId());

                    //评价人（Reviewer_Name）：对公司进行评价的用户姓名或ID。VARCHAR
                    Element nicknameElements = innerDocument.selectFirst("div.user-nickname");
                    String nickname = divElement.select("span.name-text").text();
                    newReview.setReviewerName(nickname);

                    //评价时间（Review_Time）：评价被添加的日期和时间。DATE
                    Element timeElements = innerDocument.selectFirst("div.user-job-name");
                    String timeText = divElement.select("div.tw-text-sm-pure.tw-text-gray-500").text();
                      // 创建 DateTimeFormatter 对象，指定日期时间的格式
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
                      // 解析时间字符串为 LocalDateTime 对象
                    LocalDateTime localDateTime = LocalDateTime.parse(timeText, formatter);
                      // 获取 LocalDateTime 对象的 LocalDate 部分
                    LocalDate localDate = localDateTime.toLocalDate();
                    newReview.setReviewTime(localDate);

                    //评价内容（Review_Content）：用户对公司的文字评价或评论。TEXT
                    Element contentElement = innerDocument.selectFirst("span.vue-ellipsis-js-content");
                    String contentText = contentElement.ownText();
                    newReview.setReviewContent(contentText);

                    //评分（Rating）：对公司的评分，通常是1到5分的范围。FLOAT
                    Comment curComment = CommentAnalysis(contentText);
                    double curScore = CommentScore(curComment);
                    newReview.setRating((float)curScore);

                    //评价来源（Review_Source）：标识评价信息的来源渠道或平台 TEXT
                    newReview.setReviewSource("牛客网");

                    currentReviewArray.add(newReview);
                }

                //获取下一页的按钮
                WebElement buttonElement = driver.findElement(By.cssSelector("div.search-agination button.el-icon.el-icon-arrow-right"));
                // 判断按钮是否可点击
                if (buttonElement.isEnabled()) {
                    // 执行点击操作
                    buttonElement.click();
                } else {
                    //无法点击，证明到了末页
                    stopMark=1;
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return currentReviewArray;
    }
}
