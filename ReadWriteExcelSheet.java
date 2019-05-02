package com.qainfotech.ExcelReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ReadWriteExcelSheet {
	
	public ArrayList<String> readExcelFile(String file,int columnIndex) throws IOException
	{
		ArrayList<String> CellValue = new ArrayList<String>();
		InputStream ExcelFileToRead = new FileInputStream(file);
		XSSFWorkbook  wb = new XSSFWorkbook(ExcelFileToRead);

		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow row; 
		XSSFCell cell;
		for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			  row = sheet.getRow(rowIndex);
			  if (row != null) {
				  
				cell = row.getCell(columnIndex);
			    if (cell != null) {
			      // Found column and there is value in the cell.
			      CellValue.add(cell.getStringCellValue());
			    }
			  }
			}
		return CellValue;
		
	}
	public void writeTypeToExcel(String type) throws Exception {
		 FileInputStream fsIP= new FileInputStream(new File("RSS.xlsx")); 
         XSSFWorkbook wb = new XSSFWorkbook(fsIP);
           
         XSSFSheet worksheet = wb.getSheetAt(0); 
           
         int lastRow=worksheet.getLastRowNum();
         Row row = worksheet.createRow(++lastRow);
         row.createCell(0).setCellValue(type);
         fsIP.close(); //Close the InputStream
         FileOutputStream output =new FileOutputStream(new File("RSS.xlsx"));  
         wb.write(output);
         output.close(); 		
	}
	public void writeTypeToExcel(String code, int articleNo, String Result) throws Exception {
		FileInputStream fsIP= new FileInputStream(new File("RSS.xlsx")); 
        XSSFWorkbook wb = new XSSFWorkbook(fsIP);
          
        XSSFSheet worksheet = wb.getSheetAt(0); 
          
        int lastRow=worksheet.getLastRowNum();
        Row row = worksheet.createRow(++lastRow);
        row.createCell(0).setCellValue(code);
        row.createCell(1).setCellValue(articleNo);
        row.createCell(2).setCellValue(Result);
        fsIP.close(); //Close the InputStream
        FileOutputStream output =new FileOutputStream(new File("RSS.xlsx"));  
        wb.write(output);
        output.close(); 		
	}
	public void writeTypeToExcel(String code, int articleNo, String Result, int count, ArrayList<String> DOI) throws Exception {
		
		String differentDOI = "";
		for (String s : DOI)
		{
		    differentDOI += s + ", ";
		}
		FileInputStream fsIP= new FileInputStream(new File("RSS.xlsx")); 
        XSSFWorkbook wb = new XSSFWorkbook(fsIP);
        XSSFSheet worksheet = wb.getSheetAt(0); 
          
        int lastRow=worksheet.getLastRowNum();
        Row row = worksheet.createRow(++lastRow);
        row.createCell(0).setCellValue(code);
        row.createCell(1).setCellValue(articleNo);
        row.createCell(2).setCellValue(Result);
        row.createCell(3).setCellValue(count);
        row.createCell(4).setCellValue(differentDOI);
        fsIP.close(); //Close the InputStream
        FileOutputStream output =new FileOutputStream(new File("RSS.xlsx"));  
        wb.write(output);
        output.close(); 		
	}
	
}