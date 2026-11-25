package app.model.shop;

import app.model.shop.enums.CurrencyType;

public record Price(int amount, CurrencyType currencyType) {

}
