package entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Trade {
    // Ticket	Open Time	Type	Size	Item	Price	S / L	T / P	Close Time	Price	Commission	Taxes	Swap	Profit

    private String ticket, openTime, type, size, item, openingPrice, sl, tp, closeTime, closingPrice, commission, taxes, swap, profit;

    public void pourEssayer(){};
}
