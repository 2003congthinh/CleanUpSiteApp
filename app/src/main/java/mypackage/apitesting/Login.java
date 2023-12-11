package mypackage.apitesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    private String studentEmail="";
    private String studentPassword="";
    private String status ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void goToHome(View view) {
        TextView emailText = findViewById(R.id.email);
        studentEmail = emailText.getText().toString();
        TextView passwordText = findViewById(R.id.password);
        studentPassword = passwordText.getText().toString();
        new PostStudent().execute();
    }

    public void goToSignup(View view) {
        Intent intent = new Intent(Login.this, Signup.class);
        startActivity(intent);
    }

    //    POST DATA
    private class PostStudent extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            status = HttpHandler.postRequestLogin(studentEmail,studentPassword);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("Login", "Status: " + status);
            if(status.equals("Success: OK")){
                Toast.makeText(Login.this, status, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, Home.class);
                startActivity(intent);
            } else {
                Toast.makeText(Login.this, "Something's wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
}