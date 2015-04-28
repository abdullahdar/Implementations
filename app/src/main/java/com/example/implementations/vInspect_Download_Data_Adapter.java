package com.example.implementations;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

public class vInspect_Download_Data_Adapter {

	protected static final String TAG = "DataAdapter";

	// private final Context mContext;
	private SQLiteDatabase mDb;
	private vInspect_Download_Data_DB_Helper mDbHelper;

	public vInspect_Download_Data_Adapter(Context context) {
		try {
			Context mContext = context;
			mDbHelper = new vInspect_Download_Data_DB_Helper(mContext);
		} catch (Exception ex) {
			Log.d("TestAdapterConstructor", ex.getMessage());
		}
	}

	public vInspect_Download_Data_Adapter createDatabase(String table)
			throws SQLException {
		try {
			mDbHelper.createDataBase(table);
		} catch (IOException mIOException) {
			Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	public void open() {
		try {
			/*
			 * mDbHelper.openDataBase(); mDbHelper.close();
			 */
			mDb = mDbHelper.getWritableDatabase();
			mDb.execSQL("PRAGMA foreign_keys=ON;");
		} catch (Exception mSQLException) {
			Log.e(TAG, "open >>" + mSQLException.getMessage());
		}
	}

	public void close() {
		mDbHelper.close();
	}

	/*
	 * public void DeleteDB(Context context) {
	 * context.deleteDatabase("vInspect_DB.db"); }
	 */

	public String InsertAttendants(int AttendantID, String AttendantName) {
		String Status = null;
		try {
			/*
			 * ContentValues values = new ContentValues();
			 * values.put("Attendants.AttendantID", AttendantID);
			 * values.put("Attendants.AttendantName", AttendantName); long
			 * insertId = mDb.insert("Attendants", null, values);
			 */
			String InsertString = "INSERT INTO Attendants (AttendantID,AttendantName) VALUES ("
					+ AttendantID + " , " + "'" + AttendantName + "'" + " )";
			mDb.execSQL(InsertString);
			Status = "awandies";
		} catch (Exception e) {
			Status = e.getMessage();
		}
		return Status;
	}

	public String InsertLocation(int LocationID, String LocationName) {
		String Status = null;
		try {
			String InsertString = "INSERT INTO Location (LocationID,LocationName) VALUES ("
					+ LocationID + " , " + "'" + LocationName + "'" + " )";
			mDb.execSQL(InsertString);
			Status = "awandies";
		} catch (Exception e) {
			Status = e.getMessage();
		}
		return Status;
	}

	public String InsertAttendantLocation(int LocationID, int AttendantID) {
		String Status = null;
		try {
			String InsertString = "INSERT INTO AttendantsLocation (LocationID,AttendantsID) VALUES ("
					+ LocationID + " , " + AttendantID + " )";
			mDb.execSQL(InsertString);
			Status = "awandies";
		} catch (Exception e) {
			Status = e.getMessage();
		}
		return Status;
	}

	public String InsertForm(int FormID, String FormName) {
		String Status = null;
		try {
			String InsertString = "INSERT INTO InspectionForm (InspectionFormID,InspectionFormName) VALUES ("
					+ FormID + " , " + "'" + FormName + "'" + " )";
			mDb.execSQL(InsertString);
			Status = "awandies";
		} catch (Exception e) {
			Status = e.getMessage();
		}
		return Status;
	}

	public String InsertLocationForm(int LocationID, int FormID) {
		String Status = null;
		try {
			String InsertString = "INSERT INTO LocationInspectionForm (LocationID,InspectionFormID) VALUES ("
					+ LocationID + " , " + FormID + " )";
			mDb.execSQL(InsertString);
			Status = "awandies";
		} catch (Exception e) {
			Status = e.getMessage();
		}
		return Status;
	}

	public String InsertAttendantStatus(int AttendantStatusID,
			String AttendantStatusText) {
		String Status = null;
		try {
			String InsertString = "INSERT INTO AttendantStatus (AttendantStatusID,AttendantStatusText) VALUES ("
					+ AttendantStatusID
					+ " , "
					+ "'"
					+ AttendantStatusText
					+ "'" + " )";
			mDb.execSQL(InsertString);
			Status = "awandies";
		} catch (Exception e) {
			Status = e.getMessage();
		}
		return Status;
	}

	public String InsertItemData(int ItemDataID, String ItemCategory,
			String ItemText, int MinScore, int MaxScore, int InspectionFormID) {
		String Status = null;
		try {
			String InsertString = "INSERT INTO ItemData (ItemDataID,ItemCategory,ItemText,ItemMin,ItemMax,InspectionFormID) VALUES ("
					+ ItemDataID
					+ " , "
					+ "'"
					+ ItemCategory
					+ "'"
					+ " , "
					+ "'"
					+ ItemText
					+ "'"
					+ " , "
					+ MinScore
					+ " , "
					+ MaxScore + " , " + InspectionFormID + " )";
			mDb.execSQL(InsertString);
			Status = "awandies";
		} catch (Exception e) {
			Status = e.getMessage();
		}
		return Status;
	}

	public String getSingleAttendant() {

		String[] allColumns = { "AttendantName" };
		String AttendantName = null;

		Cursor cursor = mDb.query("Attendants", allColumns, null, null, null,
				null, null);

		cursor.moveToFirst();
		AttendantName = cursor.getString(0);
		// Make sure to close the cursor
		cursor.close();
		return AttendantName;
	}

	public Cursor getAllLocations() {
		Cursor cursor = mDb.rawQuery("SELECT LocationName FROM Location", null);
		return cursor;
	}

	public Cursor getAllItems(String formid) {
		Cursor cursor = mDb.rawQuery(
				"SELECT ItemText, ItemMax, ItemDataID FROM ItemData WHERE InspectionFormID="
						+ formid, null);
		return cursor;
	}

	public Cursor getAllAttendantss() {
		Cursor cursor = mDb.rawQuery("SELECT AttendantName FROM Attendants",
				null);
		return cursor;
	}

	public Cursor getAttendantsStatus() {
		Cursor cursor = mDb.rawQuery(
				"SELECT DISTINCT AttendantStatusText FROM AttendantStatus",
				null);
		return cursor;
	}

	public Cursor getAllAttendantID(String AttendantName) {
		Cursor cursor = mDb.rawQuery(
				"SELECT AttendantID FROM Attendants WHERE AttendantName=" + "'"
						+ AttendantName + "'", null);
		return cursor;
	}

	public Cursor getAttendantStatusID(String Status) {
		Cursor cursor = mDb.rawQuery(
				"SELECT AttendantStatusID FROM AttendantStatus WHERE AttendantStatusText="
						+ "'" + Status + "'", null);
		return cursor;
	}

	public Cursor getAllDiseases() {
		Cursor cursor = mDb.rawQuery(
				"SELECT * FROM disease ORDER BY DiseaseName ASC", null);
		return cursor;
	}

	public Cursor GetDiseaseByFilter(String filter) {
		Cursor cursor = mDb.rawQuery(
				"select * from disease where disease.DiseaseName like '%"
						+ filter + "%'", null);
		return cursor;
	}

	public Cursor GetDiseaseID(String filter) {
		Cursor cursor = mDb.rawQuery(
				"select _id from disease where disease.DiseaseName ==  '"
						+ filter + "'", null);
		return cursor;
	}

	public Cursor getAllSymptoms() {
		Cursor cursor = mDb.rawQuery(
				"SELECT * FROM symptom ORDER BY SymptomName ASC", null);
		return cursor;
	}

	public Cursor GetSymptomByFilter(String filter) {
		Cursor cursor = mDb.rawQuery(
				"select * from symptom where symptom.SymptomName like '%"
						+ filter + "%'", null);
		return cursor;
	}
	
	public Cursor GetSymptomID(String filter) {
		Cursor cursor = mDb.rawQuery(
				"select _id from symptom where symptom.SymptomName ==  '"
						+ filter + "'", null);
		return cursor;
	}

	public Cursor getAllTests() {
		Cursor cursor = mDb.rawQuery(
				"SELECT * FROM testnprocedure ORDER BY TestName ASC", null);
		return cursor;
	}

	public Cursor GetTestByFilter(String filter) {
		Cursor cursor = mDb.rawQuery(
				"select * from testnprocedure where testnprocedure.TestName like '%"
						+ filter + "%'", null);
		return cursor;
	}
	
	public Cursor GetTestID(String filter) {
		Cursor cursor = mDb.rawQuery(
				"select _id from testnprocedure where testnprocedure.TestName ==  '"
						+ filter + "'", null);
		return cursor;
	}
}