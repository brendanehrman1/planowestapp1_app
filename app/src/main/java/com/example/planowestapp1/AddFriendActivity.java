package com.example.planowestapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static com.example.planowestapp1.MainActivity.PREF_NAME;

public class AddFriendActivity extends AppCompatActivity {

    private ListView listView;
    private EditText displayNameInput;
    private EditText nicknameInput;
    private TextView statusDisplay;
    private String tab;
    private View wrapper;

    private String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        loadData();
        listView = (ListView) findViewById(R.id.friendList);
        statusDisplay = (TextView) findViewById(R.id.statusDisplay);
        try {
            String info = getFriendData(displayName);
            JSONObject json = new JSONObject(info);
            JSONArray jArr = json.getJSONArray("friends");
            ArrayList<FriendEntry> friendList = new ArrayList<>();
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject friendOb = new JSONObject(jArr.getString(i));
                friendList.add(new FriendEntry(friendOb.getString("friendName"), friendOb.getString("nickname")));
            }
            FriendListAdapter friendListAdapter = new FriendListAdapter(this, R.layout.friend_entry_layout, friendList);
            friendListAdapter.setTab(tab);
            listView.setAdapter(friendListAdapter);
        } catch (IOException | JSONException e) {
            statusDisplay.setText(e.getMessage());
        }
    }

    public void sendFriendRequest(View v) throws IOException, JSONException {
        String displayNameInputStr = displayNameInput.getText().toString();
        String nicknameInputStr = nicknameInput.getText().toString();
        if (displayNameInputStr.length() == 0 || nicknameInputStr.length() == 0) {
            wrapper.getLayoutParams().height = 600;
            wrapper.requestLayout();
            statusDisplay.setText("Sorry, you must enter your friend's display name and the nickname that you would like them to be displayed as. Please try again.");
            return;
        }
        String info = getRequestData(displayNameInputStr, nicknameInputStr);
        JSONObject json = new JSONObject(info);
        String status = json.getString("status");
        if (status.equals("friendNotExist")) {
            wrapper.getLayoutParams().height = 600;
            wrapper.requestLayout();
            statusDisplay.setText("Sorry, your friend's display name does not exist. Please try again.");
        } else if (status.equals("alreadyPending")) {
            wrapper.getLayoutParams().height = 600;
            wrapper.requestLayout();
            statusDisplay.setText("Sorry, this friend either already exists or is currently pending. Please try again.");
        } else if (status.equals("nicknameUsed")) {
            wrapper.getLayoutParams().height = 600;
            wrapper.requestLayout();
            statusDisplay.setText("Sorry, you are already using this nickname for another friend. Please try again.");
        } else {
            startActivity(new Intent(AddFriendActivity.this, FriendActivity.class));
        }
    }

    public void goBack(View v) {
        Intent intent = new Intent(AddFriendActivity.this, FriendActivity.class);
        intent.putExtra("TAB", tab);
        startActivity(intent);
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        displayName = sharedPreferences.getString("NAME", null);
        Bundle extras = getIntent().getExtras();
        if (extras == null)
            tab = "FRIENDS";
        else
            tab = extras.getString("TAB");
        displayNameInput = (EditText) findViewById(R.id.displayName);
        nicknameInput = (EditText) findViewById(R.id.nickname);
        wrapper = (View) findViewById(R.id.wrapper);
    }

    public String getRequestData(String friendName, String nickname) throws IOException {

        displayName = displayName.replaceAll(" ", "%20");
        displayName = displayName.replaceAll("&", "%26");
        displayName = displayName.replaceAll("#", "%23");
        friendName = friendName.replaceAll(" ", "%20");
        friendName = friendName.replaceAll("&", "%26");
        friendName = friendName.replaceAll("#", "%23");
        nickname = nickname.replaceAll(" ", "%20");
        nickname = nickname.replaceAll("&", "%26");
        nickname = nickname.replaceAll("#", "%23");

        HttpURLConnection connection = (HttpURLConnection) new URL("http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/friends/add?userName=" + displayName + "&friendName=" + friendName +  "&nickname=" + nickname).openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if(responseCode == 200){
            String response = "";
            Scanner scanner = new Scanner(connection.getInputStream());
            while(scanner.hasNextLine()){
                response += scanner.nextLine();
                response += "\n";
            }
            scanner.close();

            return response;
        }

        // an error happened
        return null;
    }

    public String getFriendData(String displayName) throws IOException {

        displayName = displayName.replaceAll(" ", "%20");
        displayName = displayName.replaceAll("&", "%26");
        displayName = displayName.replaceAll("#", "%23");

        String url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/friends/get?userName=" + displayName + "&pending=0";

        if (tab.equals("FRIEND_REQUESTS"))
            url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/friends/get?friendName=" + displayName + "&pending=1";
        else if (tab.equals("MY_REQUESTS"))
            url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/friends/get?userName=" + displayName + "&pending=1";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if(responseCode == 200){
            String response = "";
            Scanner scanner = new Scanner(connection.getInputStream());
            while(scanner.hasNextLine()){
                response += scanner.nextLine();
                response += "\n";
            }
            scanner.close();

            return response;
        }

        // an error happened
        return null;
    }
}
