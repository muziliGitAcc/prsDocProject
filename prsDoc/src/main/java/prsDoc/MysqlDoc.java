package prsDoc;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.StyleDescription;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MysqlDoc {

    public static void readAndWriterTest3() throws IOException {
        File file = new File(Config.docFile);
        File fileExcel = new File(Config.excelFile);
        File writeName = new File(Config.txtFile);
        //判断文件是否存在
        if(!writeName.exists()){
            // 创建新文件
            writeName.createNewFile();
        }
        FileWriter writer = new FileWriter(writeName);
        BufferedWriter outTxt = new BufferedWriter(writer);
///*        // \r\n即为换行
//        outTxt.write(str+"\r\n");
//        // 把缓存区内容压入文件
//        outTxt.flush();*/
        try {
            //获取excel
            Workbook workBook = Excel.getWorkbok(fileExcel);
            // sheet 对应一个工作页
            Sheet sheet = workBook.getSheetAt(0);

            //doc 文件解析代码
            FileInputStream fis = new FileInputStream(file);
            HWPFDocument doc = new HWPFDocument(fis);
            Range rang = doc.getRange();
            //获取标题列表
            List<String> stringList = getParagraph(rang, doc);
            TableIterator it = new TableIterator(rang);
            int set = 1;
            while (it.hasNext() && set <= stringList.size()) {
                Table tb = (Table) it.next();
                System.out.println("这是第" + set + "个表的数据");
                //迭代行，默认从0开始,可以依据需要设置i的值,改变起始行数，也可设置读取到那行，只需修改循环的判断条件即可
                TableRow tr = tb.getRow(0);
                System.out.println("该表列数" + tr.numCells());
                if (tr.numCells() <= 3) {
                    continue;
                }

                //创建excel行。用于接受doc文档中对应的数据
//                    Row row = sheet.createRow( i+ 3);
                Map map = parsingString(stringList.get(set - 1));
                //开始一个新的建表sql
                String name = map.get("name").toString();
                name = name.replaceAll("\r|\n", "");
                outTxt.write("CREATE TABLE `"+name+"`( "+"\r\n");
                Excel.createRow(sheet, map);
                for (int i = 0; i < tb.numRows(); i++) {
                    String str = "";
                    tr = tb.getRow(i);
                    //创建一行excel
                    Row sRow = sheet.createRow(sheet.getLastRowNum() + 1);
                    Excel.copyRow(sheet.getRow(2), sRow);
                    //迭代列，默认从0开始
                    for (int j = 0; j < tr.numCells(); j++) {
                        TableCell td = tr.getCell(j);//取得单元格
                        //取得单元格的内容

                        for (int k = 0; k < td.numParagraphs(); k++) {
                            Paragraph para = td.getParagraph(k);
                            String s = para.text();
                            //去除后面的特殊符号
                            if (null != s && !"".equals(s)) {
                                //doc 表字段的内容
                                s = s.substring(0, s.length() - 1);
                            }
                            Excel.setCell(j, s, sRow);
                            if (i > 0){
                                str = text(str,s,j);
                            }
                            System.out.print(s + "\t");
                        }
                    }
                    if (!str.equals("")){
                        outTxt.write(str+","+"\r\n");
                    }

                }
                //sql结束
                outTxt.write("PRIMARY KEY (`id`)"+"\r\n");
                outTxt.write(")ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"+"\r\n");
                set++;
            }
            outTxt.flush();
            // 创建文件输出流，输出电子表格：这个必须有，否则在sheet上做的任何操作都不会有效
            OutputStream out = new FileOutputStream(fileExcel);
            workBook.write(out);
            out.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取表格的标题名称。
    public static List<String> getParagraph(Range rang, HWPFDocument doc) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < rang.numParagraphs(); i++) {
            Paragraph p = rang.getParagraph(i);// 获取段落
            int numStyles = doc.getStyleSheet().numStyles();
            int styleIndex = p.getStyleIndex();
            if (numStyles > styleIndex) {
                StyleSheet style_sheet = doc.getStyleSheet();
                StyleDescription style = style_sheet.getStyleDescription(styleIndex);
                String styleName = style.getName();// 获取每个段落样式名称
//                    System.System.out.println(styleName);
                // 获取自己理想样式的段落文本信息
                String styleLoving = Config.test;
                if (styleName != null && styleName.contains(styleLoving)) {
                    stringList.add(p.text());
                }
            }
        }
        return stringList;
    }


    public static Map parsingString(String str) {
        Map map = new HashMap();
        String s = QtoB(str);
        String[] strList = str.split("#");
        System.out.println(str);
        map.put("name", strList[1]);
        String[] strList2 = strList[0].split("、");
        map.put("comment", strList2[1]);
        return map;
    }


    //符号转换
    public static String QtoB(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    public static String text(String str,String s,Integer j){
        if (j == 0){
            str = str + "`"+s+"` ";
        }else if (j == 1){
            str = str + s+" ";
        }else if (j == 2){
            if ("是".equals(s)){
                str = str + "DEFAULT NULL ";
            }else{
                str = str + "NOT NULL ";
            }
        }else if (j == 4){
            str = str + " COMMENT "+"'"+s+"'";
        }
        return str;
    }

}
