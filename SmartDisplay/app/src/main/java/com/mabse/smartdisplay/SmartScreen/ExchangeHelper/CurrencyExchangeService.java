package com.mabse.smartdisplay.SmartScreen.ExchangeHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CurrencyExchangeService {
    @GET("latest?base=USD")
    Call<CurrencyExchange> getCurrency();
}
