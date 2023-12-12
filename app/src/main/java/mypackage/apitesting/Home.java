package mypackage.apitesting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Home extends AppCompatActivity {
    private String jsonString = "";

    private String loginAccount = HttpHandler.loginEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        TextView Account = findViewById(R.id.account_text);
        CardView CreateSite = findViewById(R.id.createSite);
        CardView ManageSites = findViewById(R.id.manageSites);
        CardView JoinSite = findViewById(R.id.joinSite);
        CardView Logout = findViewById(R.id.logout);

        if (!loginAccount.equals("")){
            Account.setText(loginAccount);
        }

        CreateSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, CreateSite.class);
                startActivity(intent);
            }
        });

        ManageSites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, ManageSites.class);
                startActivity(intent);
            }
        });

        JoinSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, JoinSite.class);
                startActivity(intent);
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpHandler.loginEmail = "";
                Intent intent = new Intent(Home.this, Login.class);
                startActivity(intent);
            }
        });

    }
//    @Override
//    protected void onResume(){
//        super.onResume();
//        new Home.GetStudent().execute();
//    }

    //   GET DATA
//    private class GetStudent extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... voids){
//            jsonString = HttpHandler.getRequest();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void avoid){
//            ArrayList<String> emails = new ArrayList<String>();
//            ArrayList<String> passwords = new ArrayList<String>();
//            ArrayList<String> names = new ArrayList<String>();
//            ListView listView = findViewById(R.id.list);
//            try {
//                JSONArray jsonArray = new JSONArray(jsonString);
//                for (int i =0; i < jsonArray.length(); i++){
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    String email = jsonObject.getString("email");
//                    String password = jsonObject.getString("password");
//                    String name = jsonObject.getString("name");
//                    emails.add(email);
//                    passwords.add(password);
//                    names.add(name);
//                    Log.d("TAG", "Emails size: " + emails.size());
//                    Log.d("TAG", "Passwords size: " + passwords.size());
//                    Log.d("TAG", "Names size: " + names.size());
//                }
//                ArrayAdapter adapter = new ArrayAdapter(Home.this, android.R.layout.simple_list_item_1, emails);
//                listView.setAdapter(adapter);
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
}