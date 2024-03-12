package com.readutf.mcmatchmaker.utils;

import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class RetrofitHelper {

    public static @Nullable <T> T get(Call<T> call) {
        try {
            Response<T> execute = call.execute();
            return execute.body();
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T getOrDefault(Call<T> call, T def) {
        T t = get(call);
        return t == null ? def : t;
    }
    
}
