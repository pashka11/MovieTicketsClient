package com.javaproject.pashnim.cinema;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_page,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.home:
            {
                Toast.makeText(MovieDetailsActivity.this, "HOME", Toast.LENGTH_SHORT).show();
                Intent goToHome = new Intent(MovieDetailsActivity.this, MainActivity.class);
                startActivity(goToHome);
                break;
            }
            case R.id.admin:
            {
                Toast.makeText(MovieDetailsActivity.this, "Admin", Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
