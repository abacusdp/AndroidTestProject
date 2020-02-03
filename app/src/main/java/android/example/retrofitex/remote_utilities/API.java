package android.example.retrofitex.remote_utilities;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

    @GET(APIList.OTP_GENERATION)
    Call<JsonObject> doGetOTP(@Query("email") String email);

    @GET(APIList.VERIFY_OTP)
    Call<JsonObject> doVerifyOTP(@Query("email") String email,@Query("otp") String otp,@Query("token") String token);

    @GET(APIList.FETCH_USER)
    Call<JsonObject> doGetUser(@Query("userId") String userId);

    @POST(APIList.UPDATE_TOKEN)
    Call<JsonObject> updateToken(@Body JsonObject jsonObject);

    @POST(APIList.LAST_OPENED_ON)
    Call<JsonObject> lastOpenedOn(@Body JsonObject jsonObject);

    @POST(APIList.LOGOUT)
    Call<JsonObject> logout(@Body JsonObject jsonObject);


}
