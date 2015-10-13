package me.yang.smsbackup;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.net.Uri;
import android.provider.Telephony;
import android.provider.Telephony.Sms;
import android.database.Cursor;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.os.Environment;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*

         */
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)
                && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            File dirFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "smsbackup");
            //File dirFile = new File(Environment.getExternalStorageDirectory(), "smsbackup");
            if(!dirFile.exists()) {
                dirFile.mkdirs();
            }

            File backupFile = new File(dirFile, "sms.csv");
            Log.d("Main", backupFile.getPath());
            FileWriter fw = null;
            try {
                fw = new FileWriter(backupFile);

                String test = Telephony.Sms.getDefaultSmsPackage(this);
                String test2 = Telephony.Sms.CONTENT_URI.toString();
                Log.d("Main", "sms package:" + test + ":" + test2);

                Cursor cur = getContentResolver().query(Sms.CONTENT_URI,
                        new String[]{Sms.ADDRESS, Sms.BODY,
                                Sms.DATE, Sms.DATE_SENT, Sms.TYPE
                        },
                        "",
                        null,
                        Sms.ADDRESS + " DESC, " + Sms.DATE + " DESC"
                );
                if (cur != null) {

                    int count = cur.getColumnCount();
                    for (int i = 0; i < count; i++) {
                        String col = cur.getColumnName(i);
                        Log.d("Main", "column name " + i + ":" + col);
                    }
                    int addrIdx = cur.getColumnIndex(Sms.ADDRESS);
                    int bodyIdx = cur.getColumnIndex(Sms.BODY);
                    int dateIdx = cur.getColumnIndex(Sms.DATE);
                    int dateSentIdx = cur.getColumnIndex(Sms.ADDRESS);
                    int typeIdx = cur.getColumnIndex(Sms.TYPE);
                    int limit = 0;

                    Calendar cal = Calendar.getInstance();
                    TimeZone tz = cal.getTimeZone();//get your local time zone.
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    sdf.setTimeZone(tz);//set time zone.

                    while (cur.moveToNext()) {
                        String addr = cur.getString(addrIdx);
                        String body = cur.getString(bodyIdx);
                        long date = cur.getLong(dateIdx);
                        long dateSent = cur.getLong(dateSentIdx);
                        int type = cur.getInt(typeIdx);

                        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(addr));
                        Cursor contactCur = getContentResolver().query(uri,
                                new String[]{PhoneLookup.DISPLAY_NAME
                                },
                                "",
                                null,
                                PhoneLookup.DISPLAY_NAME + " ASC"
                        );
                        String name = "";
                        if (contactCur != null) {
                            if (contactCur.getCount() > 0) {
                                if (contactCur.moveToFirst()) {
                                    name = contactCur.getString(0);
                                }
                            }
                            contactCur.close();
                        }
                        String dir;
                        if (Sms.MESSAGE_TYPE_SENT == type) {
                            dir = "TO";
                        } else if (Sms.MESSAGE_TYPE_INBOX == type) {
                            dir = "FROM";
                        } else {
                            dir = "OTHER";
                        }


                        String dateString = sdf.format(new Date(date));
                        String dateSentString = sdf.format(new Date(date));

                        String d = dir + "," + name + "," + addr + "," + dateString  + "," + body + "\n";
                        Log.d("Main", "sms package:" + d);
                        fw.write(d, 0, d.length());
                    }// while
                    fw.flush();
                    fw.close();
                }

                cur.close();
            }
            catch(IOException e) {
                Log.d("Main", e.toString());
            }
            finally {
            }
        }// media mounted
        else {
            Log.d("Main", "unmounted or readonly");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
