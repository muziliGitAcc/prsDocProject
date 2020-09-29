package prsDoc;

import java.io.IOException;


/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        try {
            PostgresqlDoc.readAndWriterTest3("home");

        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            json.JsonJXIn();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
