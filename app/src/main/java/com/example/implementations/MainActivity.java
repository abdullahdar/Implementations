package com.example.implementations;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    Context context = this;
    private vInspect_Download_Data_Adapter datasource;
    private Cursor mCursor;
    private SimpleCursorAdapter adapter=null;
    private SQLiteCursorLoader loader=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		        int i  = 0;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        datasource = new vInspect_Download_Data_Adapter(this);
        datasource.createDatabase("disease");
        datasource.open();
        mCursor = datasource.getAllDiseases();

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setFastScrollEnabled(true);
        adapter= new SimpleCursorAdapter(this, R.layout.row,null,new String[]{"DiseaseName"}, new int[] {R.id.textView},0);
        listView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, NavigationDrawer.class);
                startActivity(intent);
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        vInspect_Download_Data_DB_Helper helper = new vInspect_Download_Data_DB_Helper(context);
        datasource = new vInspect_Download_Data_Adapter(this);
        datasource.createDatabase("disease");
        datasource.open();
        loader =
                	   new SQLiteCursorLoader(
                	         this.getApplicationContext(),
                             helper,
                	         "select * from disease",
                	         null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        this.loader=(SQLiteCursorLoader)loader;
        adapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }
}
