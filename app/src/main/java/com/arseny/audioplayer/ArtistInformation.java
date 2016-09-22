package com.arseny.audioplayer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class ArtistInformation extends AppCompatActivity {

    public static final String LOG_TAG = ArtistInformation.class.getSimpleName();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_information);

        String query = getIntent().getStringExtra("Artist name");
        String answer;

        Log.e(LOG_TAG, query);
        setTitle(query);

        query = query.replace(" ", "%20");
        query = "Metallica";

        TextView textView = (TextView) findViewById(R.id.text_information);
        if (textView != null) {
            //if ( (answer = getInformation(query)) != null) textView.setText(answer);
            //    else textView.setText("Can not get artist's information");
            textView.setText("Can not get artist's information");
        }
    }

    /*private String getInformation(String qw) {
        HttpURLConnection address = null;
        try {
            String endpointWithParams = String.format("https://en.wikipedia.org/w/api.php?action=query&titles=%s&prop=revisions&rvprop=content&format=json", qw);
            address = ((HttpURLConnection) new URL(endpointWithParams).openConnection());
            Log.e(LOG_TAG, endpointWithParams);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.toString());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert address != null;
            address.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //URLConnection connection = address.connect();
        StringBuilder result = new StringBuilder();
        try {
            InputStream is = address.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            Log.i(LOG_TAG, String.valueOf(address.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
        } finally {
            address.disconnect();
        }

        return result.toString();
    }*/

    /*@Override
    protected void onPostExecute(String strJson) {
        //super.onPostExecute(strJson);
        // выводим целиком полученную json-строку
        Log.d(LOG_TAG, strJson);

        JSONObject dataJsonObj = null;
        String secondName = "";

        try {
            dataJsonObj = new JSONObject(strJson);
            JSONArray friends = dataJsonObj.getJSONArray("friends");

            // 1. достаем инфо о втором друге - индекс 1
            JSONObject secondFriend = friends.getJSONObject(1);
            secondName = secondFriend.getString("name");
            Log.d(LOG_TAG, "Второе имя: " + secondName);

            // 2. перебираем и выводим контакты каждого друга
            for (int i = 0; i < friends.length(); i++) {
                JSONObject friend = friends.getJSONObject(i);

                JSONObject contacts = friend.getJSONObject("contacts");

                String phone = contacts.getString("mobile");
                String email = contacts.getString("email");
                String skype = contacts.getString("skype");

                Log.d(LOG_TAG, "phone: " + phone);
                Log.d(LOG_TAG, "email: " + email);
                Log.d(LOG_TAG, "skype: " + skype);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}