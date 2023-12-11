package mypackage.apitesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Signup extends AppCompatActivity {
    private String studentEmail="";
    private String studentName="";
    private String studentPassword="";
    private String status ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
    }

    public void goToHome(View view) {
        TextView emailText = findViewById(R.id.email);
        studentEmail = emailText.getText().toString();
        TextView passwordText = findViewById(R.id.password);
        studentPassword = passwordText.getText().toString();
        TextView nameText = findViewById(R.id.name);
        studentName = nameText.getText().toString();
        new Signup.PostStudent().execute();
    }

    public void goToLogin(View view) {
        Intent intent = new Intent(Signup.this, Login.class);
        startActivity(intent);
    }

    //    POST DATA
    private class PostStudent extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            status = HttpHandler.postRequestSignup(studentEmail,studentPassword,studentName);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("Signup", "Status: " + status);
            if(status.equals("Success: OK")){
                Toast.makeText(Signup.this, status, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Signup.this, "Something's wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
}