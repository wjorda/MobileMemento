package org.mitre.mobilememento;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Allows the user to create and upload archives of the provided web page.
 *
 * @author wes
 */
public class CreateArchiveActivity extends Activity {

    private static final String ARCHIVE_TODAY = "http://archive.today/?run=1&url=*";
    private static final String WEB_ARCHIVE = "https://web.archive.org/save/*";

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

}
