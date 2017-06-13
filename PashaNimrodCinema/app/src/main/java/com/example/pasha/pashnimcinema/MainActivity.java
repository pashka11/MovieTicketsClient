package com.example.pasha.pashnimcinema;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                Toast.makeText(MainActivity.this,"HOME",Toast.LENGTH_SHORT).show();
                Intent goToNextActivity = new Intent(MainActivity.this, MainActivity.class);
                startActivity(goToNextActivity);
                return true;
            case R.id.admin:
                Toast.makeText(MainActivity.this,"Admin",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.moviedetails:
                Intent goToMoviedetails = new Intent(MainActivity.this, MovieDetails.class);
                startActivity(goToMoviedetails);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
