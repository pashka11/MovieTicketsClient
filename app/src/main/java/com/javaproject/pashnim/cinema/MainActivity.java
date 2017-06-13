package com.javaproject.pashnim.cinema;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    RecyclerView m_moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_moviesList = (RecyclerView) findViewById(R.id.rv_movies);

        m_moviesList.setAdapter(new MoviesListAdapter());

        m_moviesList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_page,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.home:
            {
                Toast.makeText(MainActivity.this, "HOME", Toast.LENGTH_SHORT).show();
                Intent goToNextActivity = new Intent(MainActivity.this, MainActivity.class);
                startActivity(goToNextActivity);

                break;
            }
            case R.id.admin:
            {
                Toast.makeText(MainActivity.this, "Admin", Toast.LENGTH_SHORT).show();

                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}
