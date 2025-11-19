package com.example.focustrackr.data.remote.api;

import com.example.focustrackr.data.remote.model.QuoteResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuotesApiService {

    @GET("random") // ajusta al endpoint real que uses
    Call<QuoteResponse> getRandomQuote();
}
