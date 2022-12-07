package htmlparser;

import entities.Trade;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HtmlParser {

    /**
     * Retourne l'objet Jsoup Document du fichier extrait de l'historique mt4
     *
     * @return
     * @throws IOException
     */
    private Document getFile() throws IOException {
        File input = new File("C:\\Users\\romai\\Desktop\\DetailedStatement.htm");
        return Jsoup.parse(input, "UTF-8");
    }

    /**
     * Retour chaque lignes du tableau html du document extrait de l'historique mt4 sous forme d'un objet Elements
     * de la librairie Jsoup
     *
     * @return
     * @throws IOException
     */
    private Elements getTableRows() throws IOException {
        Element body = this.getFile().body();
        Elements table = body.select("table");
        return table.select("tr");
    }

    /**
     * Retourne une liste de Trade extrait du fichier de l'historique de mt4
     * Comprend les trades fermés et les trades en cours
     *
     * @return
     * @throws IOException
     */
    public List<Trade> getAllTrades() throws IOException {
        Elements tableRows = this.getTableRows();
        List<Element> tradeRows = tableRows.subList(3, tableRows.size());

        List<Trade> tradeList = tradeRows.stream().map(this::createTrade).collect(Collectors.toList());
        tradeList.removeIf(Objects::isNull);
        return tradeList;
    }

    /**
     * Créer l'objet Trade à partir d'un objet de type Element qui doit être une ligne du tableau html de
     * l'historique extrait de mt4
     *
     * @param element
     * @return
     */
    private Trade createTrade(Element element) {
        Trade trade = null;

        if (element.select("td").size() > 10) {
            if (getTextByTableColumn(element, 10).startsWith("cancelled") || getTextByTableColumn(element, 10).startsWith("deleted")) {

                // Le trade était un ordre limit, il a été annulé avant d'entrer
                trade = createRunningTrade(element);

            } else if (!getTextByTableColumn(element, 0).equals("Ticket")) {

                // On sait que le trade a eu lieu et est fermé, on s'assure qu'il ne s'agit pas de l'entête du tableau
                trade = createRunningTrade(element);
                trade.setCommission(getTextByTableColumn(element, 10));
                trade.setTaxes(getTextByTableColumn(element, 11));
                trade.setSwap(getTextByTableColumn(element, 12));
                trade.setProfit(getTextByTableColumn(element, 13));
            }
        }

        return trade;
    }

    /**
     * Renvoi le texte d'une balise td d'un tableau html
     *
     * @param tableRow
     * @param index
     * @return
     */
    private String getTextByTableColumn(Element tableRow, Integer index) {
        return tableRow.select("td").get(index).text();
    }

    /**
     * Créer une position dont l'ordre n'a pas encore été effectué sur mt4 (ordre limit ou stop)
     * Renvoi un trade sans les commissions, taxes, swap ni profit
     *
     * @param tableRow
     * @return
     */
    private Trade createRunningTrade(Element tableRow) {
        return Trade.builder()
                .ticket(getTextByTableColumn(tableRow, 0))
                .openTime(getTextByTableColumn(tableRow, 1))
                .type(getTextByTableColumn(tableRow, 2))
                .size(getTextByTableColumn(tableRow, 3))
                .item(getTextByTableColumn(tableRow, 4))
                .openingPrice(getTextByTableColumn(tableRow, 5))
                .sl(getTextByTableColumn(tableRow, 6))
                .tp(getTextByTableColumn(tableRow, 7))
                .closeTime(getTextByTableColumn(tableRow, 8))
                .closingPrice(getTextByTableColumn(tableRow, 9))
                .build();
    }

}
