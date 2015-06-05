package nl.elastique.poetry.demo.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import nl.elastique.poetry.data.json.JsonPathException;
import nl.elastique.poetry.data.json.JsonPathResolver;
import nl.elastique.poetry.data.json.JsonPersister;
import nl.elastique.poetry.demo.R;
import nl.elastique.poetry.demo.data.DatabaseHelper;
import nl.elastique.poetry.demo.data.JsonLoader;
import nl.elastique.poetry.demo.models.Group;
import nl.elastique.poetry.demo.models.User;

public class MainActivity extends ActionBarActivity
{
    private DatabaseHelper mDatabaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mDatabaseHelper = DatabaseHelper.getHelper(this);

        try
        {
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

    @Override
    public void onDestroy()
    {
        DatabaseHelper.releaseHelper();
        mDatabaseHelper = null;

        super.onDestroy();
    }
}
