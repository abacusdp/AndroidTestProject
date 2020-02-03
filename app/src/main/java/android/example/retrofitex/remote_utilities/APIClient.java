package android.example.retrofitex.remote_utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getAPIClient(Context context) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new MyOkHttpInterceptor(context))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(APIList.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}

class MyOkHttpInterceptor implements Interceptor {

    Context context;

    MyOkHttpInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        SharedPreferences sharedpreferences = context.getSharedPreferences("MyPREFERENCE", MODE_PRIVATE);
        String token = sharedpreferences.getString("token","");
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", token)
                .build();

        return chain.proceed(newRequest);
    }

}
