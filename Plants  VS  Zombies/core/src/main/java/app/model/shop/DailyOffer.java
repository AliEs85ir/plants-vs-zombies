package app.model.shop;

import java.time.LocalDate;

public class DailyOffer {

    private final String productId;
    private final LocalDate date;
    private final int discountPercent;

    private boolean sold;

    public DailyOffer(
        String productId,
        LocalDate date,
        int discountPercent
    ) {
        this.productId = productId;
        this.date = date;
        this.discountPercent = discountPercent;
        this.sold = false;
    }

    public String getProductId() {
        return productId;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }
}
