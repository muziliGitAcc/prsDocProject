package prsDoc;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class Excel {
    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";

    public static Workbook getWorkbok(File file) throws IOException {
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        if (file.getName().endsWith(EXCEL_XLS)) {     //Excel&nbsp;2003
            wb = new HSSFWorkbook(in);
        } else if (file.getName().endsWith(EXCEL_XLSX)) {    // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }

    //新表为0，第二行为1，其他为普通行
    public static void createRow(Sheet sheet, Map map) {
        Row row0 = sheet.getRow(0);
        Row row1 = sheet.getRow(1);
        Row row2 = sheet.getRow(2);
        CellStyle rowStyle0 = row0.getCell(0).getCellStyle();
        CellStyle rowStyle1 = row1.getCell(0).getCellStyle();
        CellStyle rowStyle2 = row2.getCell(0).getCellStyle();
        Row fRow = sheet.createRow(sheet.getLastRowNum() + 3);
        Cell A = fRow.createCell(0);
        A.setCellValue(map.get("name").toString());
        A.setCellStyle(rowStyle0);
        Cell B = fRow.createCell(1);
        B.setCellValue(map.get("comment").toString());
        B.setCellStyle(rowStyle0);
        Row sRow = sheet.createRow(sheet.getLastRowNum() + 1);
        copyRow(row1, sRow);
    }

    //复制一行信息
    public static void copyRow(Row from, Row to) {
        Iterator<Cell> it = from.cellIterator();
        int i = 0;
        System.out.println(it.hasNext());
        while (it.hasNext())                      //①先探测能否继续迭代
        {
            Cell next = it.next();
            to.createCell(i);
            CellStyle cellStyle = next.getCellStyle();
            to.getCell(i).setCellStyle(cellStyle);
            to.getCell(i).setCellValue(next.getStringCellValue());
            i++;
        }
    }

    public static void setCell(Integer num, String s, Row row) {
        switch (num) {
            case 0:
                row.getCell(1).setCellValue(s);
            case 1:
                row.getCell(2).setCellValue(s);
            case 2:
                if (s.equals("是")) {
                    row.getCell(5).setCellValue("null");
                } else {
                    row.getCell(5).setCellValue("not null");
                }
            case 3:break;
            case 4:row.getCell(0).setCellValue(s);
        }
    }


}
