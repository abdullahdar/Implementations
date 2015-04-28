package com.example.implementations;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class vInspect_Download_Data_DB_Helper extends SQLiteOpenHelper {

	private static String TAG = "DataBaseHelper"; // Tag just for the LogCat
													// window
	// destination path (location) of our database on device
	private static String DB_PATH = "";
	private static String DB_NAME = "pakwebmd.db";// Database name
	private SQLiteDatabase mDataBase;
	private Context mContext;

	public vInspect_Download_Data_DB_Helper(Context context) {
		super(context, DB_NAME, null, 1);// 1? its Database Version
		try {
			if (android.os.Build.VERSION.SDK_INT >= 4.2) {
				DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
			} else {
				DB_PATH = "/data/data/" + context.getPackageName()
						+ "/databases/";
			}
			this.mContext = context;
		} catch (Exception ex) {
			Log.d("TestAdapterConstructor", ex.getMessage());
		}
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		/*
		 * boolean mDataBaseExist = checkDataBase(); if(!mDataBaseExist) {
		 * //this.getWritableDatabase(); this.close(); try { //Copy the database
		 * from assests copyDataBase(); Log.e(TAG,
		 * "createDatabase database created"); } catch (IOException
		 * mIOException) { throw new Error("ErrorCopyingDataBase"); } }
		 */
	}

	public void createDataBase(String table) throws IOException {
		// If database not exists copy it from the assets

		boolean mDataBaseExist = checkDataBase(table);
		if (mDataBaseExist) {
		} else {
			this.getReadableDatabase();
			this.close();
			try {
				copyDataBase();
				Log.e(TAG, "createDatabase database created");
			} catch (IOException mIOException) {
				throw new Error("ErrorCopyingDataBase");
			}
		}
	}

	private boolean checkDataBase(String table) {
		try {
			SQLiteDatabase db = getWritableDatabase();

			Cursor mCursor = db.rawQuery("SELECT * FROM " + table, null);
			if (mCursor.moveToFirst()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.e("CheckDatabase", e.getMessage());
			return false;
		}
	}

	private void copyDataBase() throws IOException {
		InputStream mInput = mContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream mOutput = new FileOutputStream(outFileName);
		byte[] mBuffer = new byte[1024];
		int mLength;
		while ((mLength = mInput.read(mBuffer)) > 0) {
			mOutput.write(mBuffer, 0, mLength);
		}
		mOutput.flush();
		mOutput.close();
		mInput.close();
	}

	public boolean openDataBase() throws SQLException {
		String mPath = DB_PATH + DB_NAME;
		// Log.v("mPath", mPath);
		mDataBase = SQLiteDatabase.openDatabase(mPath, null,
				SQLiteDatabase.CREATE_IF_NECESSARY);
		// mDataBase = SQLiteDatabase.openDatabase(mPath, null,
		// SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		return mDataBase != null;
	}

	@Override
	public synchronized void close() {
		if (mDataBase != null)
			mDataBase.close();
		super.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
