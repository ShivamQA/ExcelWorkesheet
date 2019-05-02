package com.qainfotech.ExcelReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;
import org.w3c.dom.NodeList;
/**
 * Hello world!
 *
 */
public class DifferentDOIs
{

	ReadWriteExcelSheet sheet = new ReadWriteExcelSheet();
	
	public ArrayList<String> getTypeList(int index) throws IOException {
		
		ArrayList<String> AsapElements = new ArrayList<String>();
		ArrayList<String> JamsElements = new ArrayList<String>();
		ArrayList<String> CurrentElements = new ArrayList<String>();
		String excelFile = "RSS Feeds Validation.xlsx";
		ArrayList<String> issueType = sheet.readExcelFile(excelFile,0);
		System.out.println(issueType.size());	
		int i = 0; 
		while(!issueType.get(i).equals("JAMS")) {
			AsapElements.add(issueType.get(i));
			i++;
			
		}
	    issueType.subList(0, i).clear();
		int j=0;
	    while(!issueType.get(j).equals("Current Issue/LatestIssue")) {
			JamsElements.add(issueType.get(j));
			j++;
			
		}
	    issueType.subList(0, j).clear();
	    CurrentElements = issueType;
	    if(index == 1)
	    	return AsapElements;
	    if(index == 2)
	    	return JamsElements;
	    if(index == 3)
	    	return CurrentElements;
		return null;
	    
	}
	
	public File getXMLFile(String data) throws Exception {

    	String fileName = "DOI.xml";
    	File file = new File(fileName);
		FileOutputStream fout=new FileOutputStream(fileName);  
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fout));

		bw.write(data);
		bw.newLine();
		bw.flush();
		return file;
	}
	
	@Test
    public void DOIDifference() throws Exception
    { 
		ArrayList<String> typeList = getTypeList(3);
		ArrayList<String> elementCode = new ArrayList<String>();
		ArrayList<Integer> articlesSize = new ArrayList<Integer>();
		ArrayList<String> prodURL = new ArrayList<String>();
		ArrayList<String> rssURL = new ArrayList<String>();
		String type = typeList.get(0);
		sheet.writeTypeToExcel(type);
		for(int i = 1; i< typeList.size(); i++) {
			elementCode.add(typeList.get(i).split("journal '", 2)[1].split("'")[0]);
			articlesSize.add(Integer.parseInt(typeList.get(i).split("have ")[1].split(" ")[0]));
		}
		
    	System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
    	WebDriver driver = new ChromeDriver();
    	for(int i = 0; i< elementCode.size();i++) {
        	if(type.equals("ASAPs")) {
        		prodURL.add("https://achs-prod.literatumonline.com/toc/"+elementCode.get(i)+"/0/0");
        		rssURL.add("http://stag-lnx-155.acs.org:8080/iapps/wld/rss/rss?coden="+elementCode.get(i));
        	}
        	else if(type.equals("JAMS")) {
        		prodURL.add("https://achs-prod.literatumonline.com/toc/"+elementCode.get(i)+"/0/ja");
        		rssURL.add("http://stag-lnx-155.acs.org:8080/iapps/wld/rss/rss?coden=*"+elementCode.get(i));
        	}
        	else if(type.equals("Current Issue/LatestIssue")) {
        		prodURL.add("https://achs-prod.literatumonline.com/toc/"+elementCode.get(i)+"/current");
        		rssURL.add("http://stag-lnx-155.acs.org:8080/iapps/wld/rss/rss?coden=_"+elementCode.get(i)+"&count=200");
        	}
    		
    	}
    	
    	for(int k = 0 ; k < prodURL.size(); k++) {
    		
        	driver.get(prodURL.get(k));
        	ArrayList<String> DOI = new ArrayList<String>();
        	int size = driver.findElements(By.cssSelector(".issue-item_title a[href*=\"doi\"]")).size();
        	for(int i = 0; i < size; i++) {
        		DOI.add(driver.findElements(By.cssSelector(".issue-item_title a[href*=\"doi\"]")).get(i).getAttribute("href").split("/",5)[4]);
        	}    	

        	System.out.println("Prod:"+DOI.size());
        	driver.get(rssURL.get(k));
        	String data = driver.findElement(By.xpath("//pre[contains(@style,\"word\")]")).getText();

        	File file = getXMLFile(data);
        	ReadRSSXML xml = new ReadRSSXML();
        	ArrayList<String> list = new ArrayList<String>();
        	NodeList nodes = xml.readRSSXMLDocument(file,"/rss/channel/item/description/text()");
    		for (int i = 0; i < nodes.getLength(); i++){
    			String doi = nodes.item(i).getNodeValue();
    			list.add(doi.split("DOI: ")[1].split("<")[0]);
    		}
    		
        	System.out.println("RSS:"+list.size());
        	ArrayList<String> finalList = new ArrayList<String>();
        	if(DOI.size() == articlesSize.get(k) && list.size() == articlesSize.get(k)) {
        		System.out.println("Same number of DOIs are present");
        		sheet.writeTypeToExcel(elementCode.get(k), articlesSize.get(k), "Same");
        	}
        	else {
        		if(list.size()<DOI.size()) {
        			
        			for(int i=0;i<DOI.size();i++) {
        				if(!list.contains(DOI.get(i)))
        					finalList.add(DOI.get(i));
        			}
                	sheet.writeTypeToExcel(elementCode.get(k), articlesSize.get(k), "Difference in prod", finalList.size(),finalList);
        		}
        		else { 
        			for(int i=0;i<list.size();i++) {
        				if(!DOI.contains(list.get(i)))
        					finalList.add(list.get(i));
        			}
        			sheet.writeTypeToExcel(elementCode.get(k), articlesSize.get(k), "Difference in RSS", finalList.size(),finalList);
        		}
        	
        	System.out.println("Differnce:"+finalList.size());
        	System.out.println(finalList);
        	}
    	}

    	driver.close();
    	
    	
    	
     }

	
}


