package com.example.implementations;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ListActivity demonstrating using the AlphabetIndexer to derive section headers
 * @author Eric
 *
 */
public class DemoActivity extends Activity {
 
    private SQLiteDatabase db;
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        
        try{
         
        //NOTE: you should never actually start database operations from the context of the UI thread
        final DbHelper helper = new DbHelper(this);
        db = helper.getWritableDatabase();
         
        //populate the db with our dummy data, might take a while, so in real scenarios spawn a
        //new thread to do this
        final int result = helper.insertDummyData(db);
            String asdf = "http://www.talesmaze.com/29-fascinating-photos-youve-probably-never-seen-before/";
         
        if (result > 0){
            //query the db to obtain our cursor and set the list adapter, only if the rows were
            //successfully inserted
             
            final Cursor cursor = db.query(DbHelper.TABLE_COUNTRIES, null, null,
                    null, null, null, DbHelper.COUNTRIES_NAME + " ASC" );
            startManagingCursor(cursor);
            Toast.makeText(this, "Finished populating.", Toast.LENGTH_SHORT).show();
            
            ListView listview = (ListView) findViewById(R.id.listView1);
            
             MyAlphabetizedAdapter alphadizedadapter = new MyAlphabetizedAdapter(this, android.R.layout.simple_list_item_1,
                    cursor, new String[]{DbHelper.COUNTRIES_NAME}, new int[]{android.R.id.text1});
             listview.setAdapter(alphadizedadapter);
             listview.setFastScrollEnabled(true);
             listview.setBackgroundColor(Color.RED);
             
				/*
				 * setListAdapter(new MyAlphabetizedAdapter(this,
				 * android.R.layout.simple_list_item_1, cursor, new
				 * String[]{DbHelper.COUNTRIES_NAME}, new
				 * int[]{android.R.id.text1}));
				 * 
				 * //don't ever forget to do this, either here or in your
				 * ListView layout getListView().setFastScrollEnabled(true);
				 */
             
        } else {
            Toast.makeText(this, "Database could not be populated. Restart the activity.", Toast.LENGTH_LONG).show();  
        }
        }
        catch(Exception e)
        {
        	Log.w("Error", e.getMessage());
        }
 
    }
 
    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
 
    /**
     * CursorAdapter that uses an AlphabetIndexer widget to keep track of the section indicies.
     * These are the positions where we want to show a section header showing the respective alphabet letter.
     * @author Eric
     *
     */
    public class MyAlphabetizedAdapter extends SimpleCursorAdapter implements SectionIndexer{
 
        private static final int TYPE_HEADER = 1;
        private static final int TYPE_NORMAL = 0;
 
        private static final int TYPE_COUNT = 2;
 
        private AlphabetIndexer indexer;
 
        private int[] usedSectionNumbers;
 
        private Map<Integer, Integer> sectionToOffset;
        private Map<Integer, Integer> sectionToPosition;
     
        public MyAlphabetizedAdapter(Context context, int layout, Cursor c,
                String[] from, int[] to) {
            super(context, layout, c, from, to);
             
            indexer = new AlphabetIndexer(c, c.getColumnIndexOrThrow(DbHelper.COUNTRIES_NAME), "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            sectionToPosition = new TreeMap<Integer, Integer>(); //use a TreeMap because we are going to iterate over its keys in sorted order
            sectionToOffset = new HashMap<Integer, Integer>();
 
            final int count = super.getCount();
             
            int i;
            //temporarily have a map alphabet section to first index it appears
            //(this map is going to be doing somethine else later)
            for (i = count - 1 ; i >= 0; i--){
                sectionToPosition.put(indexer.getSectionForPosition(i), i);
            }
 
            i = 0;
            usedSectionNumbers = new int[sectionToPosition.keySet().size()];
             
            //note that for each section that appears before a position, we must offset our
            //indices by 1, to make room for an alphabetical header in our list
            for (Integer section : sectionToPosition.keySet()){
                sectionToOffset.put(section, i);
                usedSectionNumbers[i] = section;
                i++;
            }
 
            //use offset to map the alphabet sections to their actual indicies in the list
            for(Integer section: sectionToPosition.keySet()){
                sectionToPosition.put(section, sectionToPosition.get(section) + sectionToOffset.get(section));
            }
        }
 
        @Override
        public int getCount() {
            if (super.getCount() != 0){
                //sometimes your data set gets invalidated. In this case getCount()
                //should return 0 and not our adjusted count for the headers.
                //The only way to know if data is invalidated is to check if
                //super.getCount() is 0.
                return super.getCount() + usedSectionNumbers.length;
            }
             
            return 0;
        }
         
        @Override
        public Object getItem(int position) {
            if (getItemViewType(position) == TYPE_NORMAL){//we define this function in the full code later
                //if the list item is not a header, then we fetch the data set item with the same position
                //off-setted by the number of headers that appear before the item in the list
                return super.getItem(position - sectionToOffset.get(getSectionForPosition(position)) - 1);
            }
 
            return null;
        }
 
        @Override
        public int getPositionForSection(int section) {
            if (! sectionToOffset.containsKey(section)){
                //This is only the case when the FastScroller is scrolling,
                //and so this section doesn't appear in our data set. The implementation
                //of Fastscroller requires that missing sections have the same index as the
                //beginning of the next non-missing section (or the end of the the list if
                //if the rest of the sections are missing).
                //So, in pictorial example, the sections D and E would appear at position 9
                //and G to Z appear in position 11.
                int i = 0;
                int maxLength = usedSectionNumbers.length;
                 
                //linear scan over the sections (constant number of these) that appear in the
                //data set to find the first used section that is greater than the given section, so in the
                //example D and E correspond to F
                while (i < maxLength && section > usedSectionNumbers[i]){
                    i++;
                }
                if (i == maxLength) return getCount(); //the given section is past all our data
 
                return indexer.getPositionForSection(usedSectionNumbers[i]) + sectionToOffset.get(usedSectionNumbers[i]);
            }
 
            return indexer.getPositionForSection(section) + sectionToOffset.get(section);
        }
 
        @Override
        public int getSectionForPosition(int position) {
            int i = 0;     
            int maxLength = usedSectionNumbers.length;
     
            //linear scan over the used alphabetical sections' positions
            //to find where the given section fits in
            while (i < maxLength && position >= sectionToPosition.get(usedSectionNumbers[i])){
                i++;
            }
            return usedSectionNumbers[i-1];
        }
 
        @Override
        public Object[] getSections() {
            return indexer.getSections();
        }
        //nothing much to this: headers have positions that the sectionIndexer manages.
        @Override
        public int getItemViewType(int position) {
            if (position == getPositionForSection(getSectionForPosition(position))){
                return TYPE_HEADER;
            } return TYPE_NORMAL;
        }
 
        @Override
        public int getViewTypeCount() {
            return TYPE_COUNT;
        }
 
        //return the header view, if it's in a section header position
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int type = getItemViewType(position);
            if (type == TYPE_HEADER){
                if (convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.header, parent, false);
                }
                ((TextView)convertView.findViewById(R.id.header)).setText((String)getSections()[getSectionForPosition(position)]);
                return convertView;
            }
            return super.getView(position - sectionToOffset.get(getSectionForPosition(position)) - 1, convertView, parent);
        }
 
 
        //these two methods just disable the headers
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }
 
        @Override
        public boolean isEnabled(int position) {
            if (getItemViewType(position) == TYPE_HEADER){
                return false;
            }
            return true;
        }
    }
}
