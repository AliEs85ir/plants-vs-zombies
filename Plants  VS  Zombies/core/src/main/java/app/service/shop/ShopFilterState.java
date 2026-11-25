package app.service.shop;

import app.model.shop.enums.CurrencyType;

public final class ShopFilterState {
    private String draftCategoryId;
    private String appliedCategoryId;
    private CurrencyType draftCurrencyFilter;
    private CurrencyType appliedCurrencyFilter;
    private int draftMinPrice;
    private int draftMaxPrice;
    private int appliedMinPrice;
    private int appliedMaxPrice;

    public ShopFilterState(int defaultMin, int defaultMax) {
        this.draftMinPrice = defaultMin;
        this.draftMaxPrice = defaultMax;
        this.appliedMinPrice = defaultMin;
        this.appliedMaxPrice = defaultMax;
    }

    public String getCategoryId() {
        return appliedCategoryId;
    }

    public String getDraftCategoryId() {
        return draftCategoryId;
    }

    public void setCategoryId(String categoryId) {
        this.draftCategoryId = categoryId;
    }

    public CurrencyType getCurrencyFilter() {
        return appliedCurrencyFilter;
    }

    public CurrencyType getDraftCurrencyFilter() {
        return draftCurrencyFilter;
    }

    public void setCurrencyFilter(CurrencyType currencyFilter) {
        this.draftCurrencyFilter = currencyFilter;
    }

    public int getDraftMinPrice() {
        return draftMinPrice;
    }

    public void setDraftMinPrice(int draftMinPrice) {
        this.draftMinPrice = Math.max(0, draftMinPrice);
    }

    public int getDraftMaxPrice() {
        return draftMaxPrice;
    }

    public void setDraftMaxPrice(int draftMaxPrice) {
        this.draftMaxPrice = Math.max(draftMinPrice, draftMaxPrice);
    }

    public int getAppliedMinPrice() {
        return appliedMinPrice;
    }

    public int getAppliedMaxPrice() {
        return appliedMaxPrice;
    }

    public void applyDraft() {
        appliedCategoryId = draftCategoryId;
        appliedCurrencyFilter = draftCurrencyFilter;
        appliedMinPrice = draftMinPrice;
        appliedMaxPrice = draftMaxPrice;
    }

    public boolean hasPendingChanges() {
        return !equalsNullable(draftCategoryId, appliedCategoryId)
            || draftCurrencyFilter != appliedCurrencyFilter
            || draftMinPrice != appliedMinPrice
            || draftMaxPrice != appliedMaxPrice;
    }

    private static boolean equalsNullable(String left, String right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }
}
