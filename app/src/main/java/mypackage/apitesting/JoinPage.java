package mypackage.apitesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class JoinPage extends AppCompatActivity {
    private String getIntent = "";
    private String status ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_page);

        getIntent = getIntent().getStringExtra("siteId");

        TextView text = findViewById(R.id.text);
        text.setText(getIntent);
    }
    public void Join(View view) {
        new JoinedIn().execute();
    }

    //    POST DATA
    private class JoinedIn extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            status = HttpHandler.joinInSite(getIntent);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if(status.equals("Success: OK")){
                Toast.makeText(JoinPage.this, status, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(JoinPage.this, JoinSite.class);
                startActivity(intent);
            } else {
                Toast.makeText(JoinPage.this, "Something's wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
}