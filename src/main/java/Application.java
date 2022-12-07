import entities.Trade;
import htmlparser.HtmlParser;

import java.io.IOException;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        HtmlParser htmlParser = new HtmlParser();
        try {

            List<Trade> trades = htmlParser.getAllTrades();
            System.out.println(trades.size());
            System.out.println(trades);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
