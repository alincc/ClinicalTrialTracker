package com.yuanwei.android.db;


import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

import com.yuanwei.android.rss.domain.Article;

public class DbAdapter{
	
	public static final String KEY_ROWID = BaseColumns._ID;
	public static final String KEY_GUID = "guid";
	public static final String KEY_TITLE = "title";
	public static final String KEY_PUBDATE = "date";
	public static final String KEY_UPDATE = "lastupdate";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_URI = "uri";
	public static final String KEY_FEED ="feed";
	public static final String KEY_TAG = "tag";
	public static final String KEY_READ = "read";
	public static final String KEY_STAR="star";
	public static final String KEY_OFFLINE = "offline";    
	
	private static final String DATABASE_NAME = "ClinicalTrials";
	private static final String DATABASE_TABLE = "ClinicalTrialsList";
	private static final int DATABASE_VERSION = 5;
	
	private static final String DATABASE_CREATE_LIST_TABLE = "create table " + DATABASE_TABLE + " (" + 
																KEY_ROWID +" integer primary key autoincrement, "+ 
																KEY_GUID + " text not null, " +
																KEY_TITLE + " text not null, " +
																KEY_PUBDATE + " text not null, " +
																KEY_UPDATE +" text not null, " +
																KEY_DESCRIPTION + " text not null, " +
																KEY_URI + " text not null, " +
																KEY_FEED+ " text not null, " +
																KEY_TAG + " text, " +
																KEY_READ + " boolean not null, " + 
																KEY_STAR+" boolean not null, "+
																KEY_OFFLINE + " boolean not null);";


	private SQLiteHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;
	private Context context;

	public DbAdapter(Context c){
		context = c;
	}

	public DbAdapter openToRead() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this; 
	}

	public DbAdapter openToWrite() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this; 
	}

	public void close(){
		sqLiteHelper.close();
	}

	public class SQLiteHelper extends SQLiteOpenHelper {
		public SQLiteHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_LIST_TABLE);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE );
            onCreate(db);
		}
	}

    public long insertBlogListing(String guid,String title,String pubDate,String update,String description,Uri uri,String feedtitle) {
    	
        ContentValues initialValues = new ContentValues();
        if (guid==null){
        	initialValues.put(KEY_GUID," ");
        }else{
            initialValues.put(KEY_GUID, guid);
        }
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_PUBDATE, pubDate);
        initialValues.put(KEY_UPDATE,update);
        initialValues.put(KEY_DESCRIPTION, description);
        initialValues.put(KEY_URI, uri.toString());
        initialValues.put(KEY_FEED, feedtitle);
        initialValues.put(KEY_READ, false);;
        initialValues.put(KEY_OFFLINE, false);
        initialValues.put(KEY_STAR, false);
        return sqLiteDatabase.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public Article getBlogListing(String guid) throws SQLException {
        Cursor mCursor =
        		sqLiteDatabase.query(true, DATABASE_TABLE, new String[] {
                		KEY_ROWID,
                		KEY_GUID,
                		KEY_TITLE,
                		KEY_PUBDATE,
                		KEY_UPDATE,
                		KEY_DESCRIPTION,
                        KEY_URI,
                        KEY_FEED,
                        KEY_TAG,
                		KEY_READ,
                		KEY_STAR,
                		KEY_OFFLINE
                		}, 
                		KEY_GUID + "= '" + guid + "'", 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null && mCursor.getCount() > 0) {
        	mCursor.moveToFirst();
        	Article a = new Article();
   			a.setGuid(mCursor.getString(mCursor.getColumnIndex(KEY_GUID)));
   			a.setTitle(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE)));
   			a.setPubDate(mCursor.getString(mCursor.getColumnIndex(KEY_PUBDATE)));
   			a.setUpdateDate(mCursor.getString(mCursor.getColumnIndex(KEY_UPDATE)));
   			a.setDescription(mCursor.getString(mCursor.getColumnIndex(KEY_DESCRIPTION)));
   			a.setUrl(Uri.parse(mCursor.getString(mCursor.getColumnIndex(KEY_URI))));
   			a.setFeed(mCursor.getString(mCursor.getColumnIndex(KEY_FEED)));
   			a.setRead(mCursor.getInt(mCursor.getColumnIndex(KEY_READ)) > 0);
   			a.setDbId(mCursor.getLong(mCursor.getColumnIndex(KEY_ROWID)));
   			a.setStar(mCursor.getInt(mCursor.getColumnIndex(KEY_STAR))>0);
   			a.setOffline(mCursor.getInt(mCursor.getColumnIndex(KEY_OFFLINE)) > 0);
   			mCursor.close();
   			return a;
        }
        return null;
    }
    public ArrayList<Article> getFeedListing(String feed) throws SQLException {
        Cursor mCursor =
        		sqLiteDatabase.query(true, DATABASE_TABLE, new String[] {
                		KEY_ROWID,
                		KEY_GUID,
                		KEY_TITLE,
                		KEY_PUBDATE,
                		KEY_UPDATE,
                		KEY_DESCRIPTION,
                        KEY_URI,
                        KEY_FEED,
                        KEY_TAG,
                		KEY_READ,
                		KEY_STAR,
                		KEY_OFFLINE
                		}, 
                		KEY_FEED + "= '" + feed + "'", 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null && mCursor.getCount() > 0) {
        	mCursor.moveToFirst();
        	ArrayList<Article> list = new ArrayList<Article>();
        	while(!mCursor.isAfterLast()){
        		Article a = cursorToArticle(mCursor);
        		list.add(a);
        		mCursor.moveToNext();
        	}
        	mCursor.close();
   			return list;
        }
        return null;
    }
    public ArrayList<Article> getAllArticlesByDate() {
        ArrayList<Article> articles = new ArrayList<Article>();

        Cursor cursor = sqLiteDatabase.query(true,DATABASE_TABLE,
        		new String[] {
        		KEY_ROWID,
        		KEY_GUID,
        		KEY_TITLE,
        		KEY_PUBDATE,
        		KEY_UPDATE,
        		KEY_DESCRIPTION,
                KEY_URI,
                KEY_FEED,
                KEY_TAG,
        		KEY_READ,
        		KEY_STAR,
        		KEY_OFFLINE
        		}, null 
        		, null, null, null, KEY_UPDATE+" desc",null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
          Article player = cursorToArticle(cursor);
          articles.add(player);
          cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return articles;
      }
    public ArrayList<Article> getAllUnreadArticlesByDate() {
        ArrayList<Article> articles = new ArrayList<Article>();

        Cursor cursor = sqLiteDatabase.query(true,DATABASE_TABLE,
        		new String[] {
        		KEY_ROWID,
        		KEY_GUID,
        		KEY_TITLE,
        		KEY_PUBDATE,
        		KEY_UPDATE,
        		KEY_DESCRIPTION,
                KEY_URI,
                KEY_FEED,
                KEY_TAG,
        		KEY_READ,
        		KEY_STAR,
        		KEY_OFFLINE
        		},  KEY_READ + "= '0' ",
        		null, null, null, KEY_UPDATE+" desc",null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
          Article player = cursorToArticle(cursor);
          articles.add(player);
          cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return articles;
      }
    public ArrayList<Article> getAllReadArticlesByDate() {
        ArrayList<Article> articles = new ArrayList<Article>();

        Cursor cursor = sqLiteDatabase.query(true,DATABASE_TABLE,
        		new String[] {
        		KEY_ROWID,
        		KEY_GUID,
        		KEY_TITLE,
        		KEY_PUBDATE,
        		KEY_UPDATE,
        		KEY_DESCRIPTION,
                KEY_URI,
                KEY_FEED,
                KEY_TAG,
        		KEY_READ,
        		KEY_STAR,
        		KEY_OFFLINE
        		},  KEY_READ + "= '1' ",
        		null, null, null, KEY_UPDATE+" desc",null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
          Article player = cursorToArticle(cursor);
          articles.add(player);
          cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return articles;
      }
    /*
    while (!mCursor.isAfterLast()) {
        RssChannel Rsschannel = cursorToRssChannel(mCursor);
        RssChannels.add(Rsschannel);
        mCursor.moveToNext();
      }
      */
    
    public boolean markAsUnread(String guid) {
        ContentValues args = new ContentValues();
        args.put(KEY_READ, false);
        return sqLiteDatabase.update(DATABASE_TABLE, args, KEY_GUID + "='" + guid+"'", null) > 0;
    }
    
    public boolean markAsRead(String guid) {
        ContentValues args = new ContentValues();
        args.put(KEY_READ, true);
        return sqLiteDatabase.update(DATABASE_TABLE, args, KEY_GUID + "='" + guid+"'", null) > 0;
    }
    public boolean markStar(String guid){
    	ContentValues cv =new ContentValues();
    	cv.put(KEY_STAR, true);
    	return sqLiteDatabase.update(DATABASE_TABLE, cv, KEY_GUID+"='"+guid+"'", null)>0;
    			
    }
    public boolean deMarkStar(String guid){
    	ContentValues cv =new ContentValues();
    	cv.put(KEY_STAR, false);
    	return sqLiteDatabase.update(DATABASE_TABLE, cv, KEY_GUID+"='"+guid+"'", null)>0;
    }

    public boolean saveForOffline(String guid) {
        ContentValues args = new ContentValues();
        args.put(KEY_OFFLINE, true);
        return sqLiteDatabase.update(DATABASE_TABLE, args, KEY_GUID + "='" + guid+"'", null) > 0;
    }
    public boolean unsaveForOffline(String guid) {
        ContentValues args = new ContentValues();
        args.put(KEY_OFFLINE, false);
        return sqLiteDatabase.update(DATABASE_TABLE, args, KEY_GUID + "='" + guid+"'", null) > 0;
    }
    private Article cursorToArticle(Cursor cursor){
    	Article a = new Article();
			a.setGuid(cursor.getString(cursor.getColumnIndex(KEY_GUID)));
			a.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
			a.setPubDate(cursor.getString(cursor.getColumnIndex(KEY_PUBDATE)));
			a.setUpdateDate(cursor.getString(cursor.getColumnIndex(KEY_UPDATE)));
			a.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
			a.setUrl(Uri.parse(cursor.getString(cursor.getColumnIndex(KEY_URI))));
			a.setFeed(cursor.getString(cursor.getColumnIndex(KEY_FEED)));
			a.setRead(cursor.getInt(cursor.getColumnIndex(KEY_READ)) > 0);
			a.setDbId(cursor.getLong(cursor.getColumnIndex(KEY_ROWID)));
			a.setStar(cursor.getInt(cursor.getColumnIndex(KEY_STAR))>0);
			a.setOffline(cursor.getInt(cursor.getColumnIndex(KEY_OFFLINE)) > 0);
			return a;
    }

	public void deleteByGuid(String guid) {
		
	    sqLiteDatabase.delete(DATABASE_TABLE, KEY_GUID
	            + " = '" + guid+"'", null);
	    System.out.printf("Delete article",guid);
	}
	public void deleteById(long id) {
		// TODO Auto-generated method stub
	    sqLiteDatabase.delete(DATABASE_TABLE, KEY_ROWID
	            + " = '" + id+"'", null);
	    System.out.printf("Delete article",id);
	}
}