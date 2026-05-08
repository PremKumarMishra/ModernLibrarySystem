package com.dsa.modernlibrarysystem;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExcelParser
{
    public static void readExcelFile(String path)
    {
        try(FileInputStream fin = new FileInputStream(path);
            XSSFWorkbook workbook = new XSSFWorkbook(fin);)
        {
            int sheetCount = workbook.getNumberOfSheets();
            for(int i = 0; i< sheetCount;i++)
            {
                Sheet sheet = workbook.getSheetAt(i);
                for (Row row:sheet)
                {
                    List<String> bookList = new ArrayList<String>();
                    for(int j =0;j< 6;j++)
                    {
                        Cell cell = row.getCell(j);
                        if (cell != null)
                        {
                            switch (cell.getCellType())
                            {
                                case NUMERIC:
                                    bookList.add(String.valueOf((int)cell.getNumericCellValue()));
                                    break;
                                case STRING:
                                    bookList.add(String.valueOf(cell.getStringCellValue().replace("'", "''")));
                                    break;
                            }
                        }
                    }
                    if(bookList.size() > 5)
                    {
                        int count = 1;
                        List<List<String>> result = Util.executeSQLQuery("SELECT TOTAL FROM BOOKS WHERE TITLE = '%s';".formatted(bookList.get(4)),null);
                        String query;
                        System.out.println(result);
                        if (result.isEmpty())
                        {
                            query = String.format(
                                    "INSERT INTO BOOKS(TITLE,AUTHOR,CATEGORY,ISBN,TOTAL,AVAILABLE,PUB_YEAR,ENTRY_DATE) " +
                                            "VALUES('%s','%s','%s','%s',%d,%d,'%s','%s');",
                                    bookList.get(4),  // TITLE
                                    bookList.get(2),  // AUTHOR
                                    bookList.get(3),  // CATEGORY
                                    bookList.get(1),  // ISBN
                                    count,                // TOTAL
                                    count,                // AVAILABLE
                                    bookList.get(5),  // PUB_YEAR
                                    bookList.get(0)   // ENTRY_DATE
                            );
                        }
                        else
                        {
                            count = Integer.parseInt(result.get(0).get(0)) + 1;
                            query = String.format(
                                    "UPDATE BOOKS SET TOTAL = %d,AVAILABLE=%d WHERE TITLE = '%s'",
                                    count,
                                    count,
                                    bookList.get(4)
                            );
                        }

//                        System.out.println(result);
                        Util.updateSQLQuery(query);
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static void writeExcelFile(File file, List<List<String>> data,String[] headers) throws Exception
    {
        int numRow = 0;
        int numHeader = 0;
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadSheet = workbook.createSheet();
        XSSFRow row;

        //Write Headers
        row = spreadSheet.createRow(numRow++);
        for(String header:headers)
        {
            Cell cell = row.createCell(numHeader++);
            cell.setCellValue(header);
        }

        //Write Data
        for(List<String> item:data)
        {
            int numCell = 0;
            row = spreadSheet.createRow(numRow++);
            for(String obj:item)
            {
                Cell cell = row.createCell(numCell++);
                if (obj != null && Util.isNumeric(obj.strip()))
                {
                    cell.setCellValue(Long.parseLong(obj));
                }
                else
                {
                    cell.setCellValue(obj);
                }

            }
        }
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();
    }
}
