package com.example.focustrackr.data.repository;

import androidx.annotation.Nullable;

import com.example.focustrackr.data.remote.api.QuotesApiService;
import com.example.focustrackr.data.remote.model.QuoteResponse;
import com.example.focustrackr.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuotesRepository {

    private final QuotesApiService apiService;

    public interface QuoteCallback {
        void onResult(@Nullable QuoteResponse quote);
    }

    public QuotesRepository() {
        apiService = RetrofitClient.getClient().create(QuotesApiService.class);
    }

    public void getRandomQuote(final QuoteCallback callback) {
        apiService.getRandomQuote().enqueue(new Callback<QuoteResponse>() {
            @Override
            public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                if (response.isSuccessful()) {
                    callback.onResult(response.body());
                } else {
                    callback.onResult(null);
                }
            }

            @Override
            public void onFailure(Call<QuoteResponse> call, Throwable t) {
                callback.onResult(null);
            }
        });
    }
}
