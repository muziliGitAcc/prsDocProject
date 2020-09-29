package prsDoc;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;

public class json {
    private static Long id = 500000L;
    private static String insterSql = "INSERT INTO `caicai`.`ss_area`(`id`, `pid`, `path`, `name`, `comment`, `deleted`, `added`, `edited`) VALUES ({0,number,#}, {1,number,#}, ''{2}'', ''{3}'', '''', b''0'', ''2020-08-03 17:56:32'', ''2020-08-03 17:56:34'');\n";

    /**
     * @Description: Java 取 省 市 县   三级json数据
     * 每一级 打印控制台
     * @Param:
     * @return:
     * @Author: wangxa
     * @Date: 9:25 2019/1/7
     */
    public static void JsonJXIn() throws IOException {
        String sql;
        File writeName = new File(Config.sqlFile);
        //判断文件是否存在
        if(!writeName.exists()){
            // 创建新文件
            writeName.createNewFile();
        }
        FileWriter writer = new FileWriter(writeName);
        BufferedWriter outTxt = new BufferedWriter(writer);
        String data = readJsonFile();
        //把字符串转化为json对象
        JSONObject jsonObject = JSONObject.parseObject(data);
        //省一级所有信息
        List<prsJsonEntity> provinces = prsJson(jsonObject);
        for (prsJsonEntity provinceJsonEntity : provinces) {
            System.out.println(provinceJsonEntity.getKey());
            sql = insterSql;
            id = id + 1;
            Long provinceId = id;
            outTxt.write(MessageFormat.format(sql, id,0,provinceId.toString(),provinceJsonEntity.getKey()));
            //市一级信息
            List<prsJsonEntity> citys = prsJson((JSONObject)provinceJsonEntity.getValue());
            for (prsJsonEntity cityJsonEntity : citys) {
                System.out.println(cityJsonEntity.getKey());
                sql = insterSql;
                id = id + 1;
                Long cityId = id;
                outTxt.write(MessageFormat.format(sql, id,provinceId,provinceId+"."+cityId,cityJsonEntity.getKey()));
                //县一级信息
                List<prsJsonEntity> countys = prsJson((JSONObject)cityJsonEntity.getValue());
                for (prsJsonEntity countyJsonEntity : countys) {
                    sql = insterSql;
                    id = id + 1;
                    Long countyId = id;
                    outTxt.write(MessageFormat.format(sql, id,cityId,provinceId+"."+cityId+"."+countyId,countyJsonEntity.getKey()));
                    //从县级信息中获取 镇信息数组
                    JSONArray value = (JSONArray) countyJsonEntity.getValue();
                    for (Object o : value) {
                        sql = insterSql;
                        id = id + 1;
                        outTxt.write(MessageFormat.format(sql, id,countyId,provinceId+"."+cityId+"."+countyId+"."+id.toString(),o.toString()));
                    }
                    System.out.println(value.toJSONString());
                }
            }
        }
        outTxt.flush();
    }



    private static List<prsJsonEntity> prsJson(JSONObject jsonObject) {
        Iterator<String> keys = jsonObject.keySet().iterator();
        List<prsJsonEntity> prsJsonEntities = new ArrayList<>();
        while (keys.hasNext()) {
            prsJsonEntity jsonEntity = new prsJsonEntity();
            String key = keys.next();
            jsonEntity.setKey(key);
            jsonEntity.setValue(jsonObject.get(key));
            prsJsonEntities.add(jsonEntity);
        }
        return prsJsonEntities;
    }

    /**
     * 读取json文件，返回json串
     *
     * @return
     */
    public static String readJsonFile() {
        String jsonStr;
        try {
            File jsonFile = new File("D:\\caicai\\prsDoc\\src\\main\\java\\prsDoc\\addr.json");
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
