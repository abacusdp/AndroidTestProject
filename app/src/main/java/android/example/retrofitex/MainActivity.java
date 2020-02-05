package android.example.retrofitex;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.example.retrofitex.remote_utilities.API;
import android.example.retrofitex.remote_utilities.APIClient;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();


    API apiInterface;
    SharedPreferences sharedpreferences;
    TextView textView;
    Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiInterface = APIClient.getAPIClient(getApplicationContext()).create(API.class);
        sharedpreferences = getSharedPreferences("MyPREFERENCE", MODE_PRIVATE);

        setTitle("Main Activity");

        textView = findViewById(R.id.status_text);

        Button taskButton = findViewById(R.id.task_button);
        taskButton.setOnClickListener(v -> {
            if (flag) {
                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                intent.putExtra("onboard_id", sharedpreferences.getString("onboard_id", ""));
                startActivity(intent);
            }
        });


        Button taskButton1 = findViewById(R.id.wifiscan_button);
        taskButton1.setOnClickListener(v -> {
            if (flag) {
                Intent intent = new Intent(getApplicationContext(), WifiScan.class);
                intent.putExtra("onboard_id", sharedpreferences.getString("onboard_id", ""));
                startActivity(intent);
            }
        });


        if (sharedpreferences.getString("onboard_id", "abcd").equals("abcd"))
            getOTP();
        else getCurrentUser();

    }

    private void getOTP() {
        textView.setText(getResources().getString(R.string.sending_otp));

        Call<JsonObject> call = apiInterface.doGetOTP("surveshoeb@gmail.com");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject jsonObject = response.body();
                if (jsonObject != null && jsonObject.has("status")) {
                    Log.d(TAG, jsonObject.get("status").getAsString());
                    verifyOTP();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

            }
        });
    }

    private void verifyOTP() {
        textView.setText(getResources().getString(R.string.verifying_otp));

        Call<JsonObject> call = apiInterface.doVerifyOTP("surveshoeb@gmail.com", "163380", "");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject jsonObject = response.body();

                Log.d(TAG, "onResponse: " + jsonObject);

                if (jsonObject.get("status").getAsString().equals("Error")) {
                    Log.d(TAG, "onResponse: " + jsonObject.get("message"));
                    Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_LONG).show();
                    textView.setText(getResources().getString(R.string.invalid));

                } else {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("onboard_id", jsonObject.get("onboard_id").getAsString());
                    editor.putString("token", jsonObject.get("token").getAsString());
                    editor.apply();
                    getCurrentUser();

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    private void getCurrentUser() {
        textView.setText(getResources().getString(R.string.user_fetched));


        Call<JsonObject> call = apiInterface.doGetUser(sharedpreferences.getString("onboard_id", ""));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG, "onResponse: " + response.body());
                updateToken();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    private void updateToken() {
        JsonObject object = new JsonObject();
        object.addProperty("userid", sharedpreferences.getString("onboard_id", ""));
        object.addProperty("tokenid", "ct98DKtq-_Q:APA91bGQuJwsYVrTsUNPcka0OeDbMsUGIhxhku8IHqzSEDlJYabwhQjGsFt_N8BPcfQgy68KyrnsgfmCgkl25C5hV629Ygz6U26WLw46RXPba2qecO75VJDofvmjDbDBb3DJy2CUT-fJ");
        Call<JsonObject> call = apiInterface.updateToken(object);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG, "onResponse: " + response.body());
                lastUpdatedOn();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void lastUpdatedOn() {
        JsonObject object = new JsonObject();
        object.addProperty("last_opened_on", "Android");
        object.addProperty("userId", sharedpreferences.getString("onboard_id", ""));
        Call<JsonObject> call = apiInterface.lastOpenedOn(object);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG, "onResponse: " + response.body());
                flag = true;


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


}
