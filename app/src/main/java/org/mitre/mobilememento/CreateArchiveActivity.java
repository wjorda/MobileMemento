package org.mitre.mobilememento;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Allows the user to create and upload archives of the provided web page.
 *
 * @author wes
 */
public class CreateArchiveActivity extends ActionBarActivity
{

    private static final String ARCHIVE_TODAY = "http://archive.today/submit/";
    private static final String WEB_ARCHIVE = "https://web.archive.org/save/";

    /**
     * Shows a dialog that this is a test page and redirects the user back to a web browser in order
     * launch the app through a sharing intent.
     *
     * @param savedInstanceState
     */
    @Override
    @Deprecated
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_archive);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_archive, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_ok) {

        }
        return super.onOptionsItemSelected(item);
    }

    public void submit(View v)
    {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        CheckBox archiveToday = (CheckBox) findViewById(R.id.archiveToday);
        CheckBox webArchive = (CheckBox) findViewById(R.id.webArchive);

        for (String url : MobileMemento.urls) {
            if (webArchive.isChecked())
                threadPool.execute(new SubmitToInternetArchiveThread(url));
            if (archiveToday.isChecked())
                threadPool.execute(new SubmitToArchiveTodayThread(url));
        }

        threadPool.shutdown();
        finish();
    }

    private class SubmitToInternetArchiveThread implements Runnable
    {
        private final String url;

        SubmitToInternetArchiveThread(String url)
        {
            this.url = WEB_ARCHIVE + url;
        }

        @Override
        public void run()
        {
            HttpURLConnection connection = null;
            try {
                HttpURLConnection.setFollowRedirects(true);
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                //Toast.makeText(CreateArchiveActivity.this, "Upload finished.", Toast.LENGTH_SHORT).show();
                Log.d("Request", connection.getResponseCode() + " " + url);
            } catch (Exception e) {
                Log.e("Exception", "", e);
                //Toast.makeText(CreateArchiveActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class SubmitToArchiveTodayThread implements Runnable
    {
        private final String url;

        SubmitToArchiveTodayThread(String url)
        {
            this.url = url;
        }

        @Override
        public void run()
        {
            HttpURLConnection connection = null;
            try {
                HttpURLConnection.setFollowRedirects(true);
                connection = (HttpURLConnection) new URL(ARCHIVE_TODAY).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStream out = connection.getOutputStream();
                out.write(("url=" + url).getBytes());
                out.flush();
                out.close();
                //Toast.makeText(CreateArchiveActivity.this, "Upload finished.", Toast.LENGTH_SHORT).show();
                Log.d("Request", connection.getResponseCode() + " " + ARCHIVE_TODAY + url);
            } catch (Exception e) {
                Log.e("Exception", "", e);
                //Toast.makeText(CreateArchiveActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
            }

        }
    }


}
