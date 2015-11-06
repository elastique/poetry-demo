package nl.elastique.poetry.demo.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

import nl.elastique.poetry.json.JsonPathException;
import nl.elastique.poetry.json.JsonPathResolver;
import nl.elastique.poetry.json.JsonPersister;
import nl.elastique.poetry.demo.R;
import nl.elastique.poetry.demo.data.DatabaseHelper;
import nl.elastique.poetry.demo.data.JsonLoader;
import nl.elastique.poetry.demo.models.Group;
import nl.elastique.poetry.demo.models.User;

public class MainActivity extends ActionBarActivity
{
    private static final String sTag = "MainActivity";

    private DatabaseHelper mDatabaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mDatabaseHelper = DatabaseHelper.getHelper(this);

        // Call persistJson() on a background method (quick and dirty through a new Thread)
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                persistJson();
                readDatabase();
            }
        }).start();
    }

    @Override
    public void onDestroy()
    {
        DatabaseHelper.releaseHelper();
        mDatabaseHelper = null;

        super.onDestroy();
    }

    private void persistJson()
    {
        try
        {
            Log.d(sTag, "persisting json into database");

            // Load JSON
            JSONObject json = JsonLoader.loadObject(this, R.raw.test);

            // Get child arrays from JSON
            JSONArray users_json = JsonPathResolver.resolveArray(json, "users");
            JSONArray groups_json = JsonPathResolver.resolveArray(json, "groups");

            // Persist arrays to database
            JsonPersister persister = new JsonPersister(mDatabaseHelper.getWritableDatabase());
            persister.persistArray(User.class, users_json);
            persister.persistArray(Group.class, groups_json);
        }
        catch (IOException | JSONException | JsonPathException e)
        {
            e.printStackTrace();
        }
    }

    private void readDatabase()
    {
        try
        {
            Dao<User, Integer> user_dao = mDatabaseHelper.getDao(User.class);

            // Get user 1 with its tags and groups
            User user = user_dao.queryForId(1);

            Log.d(sTag, String.format("queried user '%s' with %d tag(s) and %d group(s)",
                user.getName(),
                user.getUserTags().size(),
                user.getUserGroups().size()));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
