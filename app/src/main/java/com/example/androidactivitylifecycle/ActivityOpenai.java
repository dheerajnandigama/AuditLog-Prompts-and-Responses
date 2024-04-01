package com.example.androidactivitylifecycle;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;
import okhttp3.MediaType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;
import java.util.Date;

public class ActivityOpenai extends AppCompatActivity {
    private EditText userPromptEditText;
    private TextView responseTextView;
    private Button sendButton;

    private Button saveButton;

    private DBHandler dbHandler;

    private String userPrompt;

    private String ans;

    private Integer seq=0;

    private static final String API_KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openai);

        userPromptEditText = findViewById(R.id.userPromptEditText);
        responseTextView = findViewById(R.id.responseTextView);
        sendButton = findViewById(R.id.sendButton);

        saveButton = findViewById(R.id.saveButton);

        dbHandler = new DBHandler(ActivityOpenai.this);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPrompt = userPromptEditText.getText().toString();
                seq=seq+1;
                new OpenAIRequestTask().execute(userPrompt);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timestamp = new java.text.SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
                dbHandler.addNewPrompt(seq, timestamp, userPrompt);
                dbHandler.addNewResponse(seq, timestamp, ans);
                Toast.makeText(ActivityOpenai.this, "SAVED TO DB", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class OpenAIRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(@NonNull String... params) {
            String userPrompt = params[0];
            String response = null;

            try {
                OkHttpClient client = new OkHttpClient();
                //String json = "{\"prompt\":\"" + userPrompt + "\"}";
                String json = "{\"model\":\"gpt-3.5-turbo\",\"messages\":[{\"role\":\"user\",\"content\":\""+userPrompt+"\"}],\"temperature\":0.7}";
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

                Request request = new Request.Builder()
                        .url("https://api.openai.com/v1/chat/completions")
                        .addHeader("Authorization", "Bearer " + "")
                        .post(requestBody)
                        .build();

                Response apiResponse = client.newCall(request).execute();
                response = apiResponse.body().string();
                Log.d("OpenAIResponse", response);
                for (int i = 0; i < params.length; i++) {
                    Log.d("ArrayValues", "Index " + i + ": " + params[i]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray choices = jsonObject.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject choice = choices.getJSONObject(0);
                    System.out.println(choice);
                    JSONObject generatedText = choice.getJSONObject("message");
                    ans = generatedText.getString("content");
                    responseTextView.setText(ans);
                }
            } catch (JSONException e) {
                e.toString();
                e.printStackTrace();
                responseTextView.setText("Error parsing response");
            }
        }
    }
}
