package com.fw.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class CSVUtil {
	
	public static void main(String[] args) {
		File directory = new File("");//设定为当前文件夹 
		try{ 
		    System.out.println(directory.getCanonicalPath());//获取标准的路径 
		    System.out.println(directory.getAbsolutePath());//获取绝对路径 
		}catch(Exception e){} 
		
		System.out.println("123");
		
	}
	public static void read(String filePath){

        

        try {
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(filePath);

            // 读表头
            csvReader.readHeaders();
            while (csvReader.readRecord()){
                // 读一整行
                System.out.println(csvReader.getRawRecord());
                // 读这行的某一列
                System.out.println(csvReader.get("Link"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void write(String filePath){

        
        
        try {
            // 创建CSV写对象
            CsvWriter csvWriter = new CsvWriter(filePath,',', Charset.forName("GBK"));
            //CsvWriter csvWriter = new CsvWriter(filePath);

            // 写表头
            String[] headers = {"编号","姓名","年龄"};
            String[] content = {"12365","张山","34"};
            csvWriter.writeRecord(headers);
            csvWriter.writeRecord(content);
            csvWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
