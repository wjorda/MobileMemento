package org.mitre.mobilememento;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.mitre.mobilememento.util.HttpIO;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author wes
 *
 * An Activity that provides a display for the TimeMap resultant from this web page.
 */
public class ViewArchiveActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener
{

    public static final long OUTDATED_INTERVAL_DAYS = 14;

    //public static final ArrayList<Map<String, ScreenType>> groups = ScreenType.getBuckets();

    private String suppliedUrl, webName;
    private TimeMap timeMap;
    private MementoArrayAdapter adapter;

    /**
     * Loads a TimeMap for this pages and sets up UI components
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Restore Instance State
        super.onCreate(savedInstanceState);
        Intent i = getIntent();

        //If not launched using an implicit ACTION.SEND Intent, quit.
        if (i.getAction() != Intent.ACTION_SEND) quit();

        //Get the url and title of the web page, quit if they don't exist.
        suppliedUrl = i.getStringExtra(Intent.EXTRA_TEXT);
        webName = i.getStringExtra(Intent.EXTRA_SUBJECT);
        if(suppliedUrl == null || webName == null) quit();

        //Load the TimeMap for this page in a separate thread, Dummy Content or otherwise. Display
        //a progress wheel while this loads.
        GenerateTimeMapTask task = new GenerateTimeMapTask();
        task.execute(suppliedUrl);

        //Display UI components
        setContentView(R.layout.activity_view_archive);

        //Set the up UI components
        ((TextView) findViewById(R.id.labelTitle)).setText(webName + ":");

        final ListView list = (ListView) findViewById(R.id.archiveList);
        adapter = new MementoArrayAdapter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);

        final Spinner monthSpinner = (Spinner) findViewById(R.id.monthJumpSpinner),
                yearSpinner = (Spinner) findViewById(R.id.yearJumpSpinner);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(timeMap != null) {
                    int index = timeMap.getDateIndex(i, new Integer((String) yearSpinner.getSelectedItem()) - 1900);
                    list.setSelection(index);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                list.setSelection(0);
            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(timeMap != null) {
                    int index = timeMap.getDateIndex(monthSpinner.getSelectedItemPosition(), new Integer((String) yearSpinner.getSelectedItem()) - 1900);
                    list.setSelection(index);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("MobileMink");
    }

    public TimeMap getTimeMap() { return timeMap; }

    private void showArchiveDialog() {
        AlertDialog.Builder g = new AlertDialog.Builder(this);
        g.setTitle("Create New Archive?");
        g.setMessage("This site's most recent archive is rather old. Create a new one?");
        g.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Launch CreateArchiveActivity
                Intent i = new Intent(ViewArchiveActivity.this, CreateArchiveActivity.class);
                startActivity(i);
            }
        });
        g.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Close dialog
                dialog.dismiss();
            }
        });
        g.create().show();
    }

    /**
     * Courtesy method to inform the user if the activity was started incorrectly.
     */
    private void quit() {
        Toast.makeText(this, "Activity needs to be started via a web browser!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_archive, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //Launch Settings Activity
            Intent i = new Intent(this, SettingsActivity.class);
            i.putExtra("SETTINGSAPP", 2);
            startActivity(i);
            return true;
        } else if (id == R.id.action_create) {
            //Launch CreateArchiveActivity
            Intent i = new Intent(ViewArchiveActivity.this, CreateArchiveActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Memento selected = timeMap.getMementos().get(i);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(selected.getArchiveUrl()));

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException r) {
            Toast.makeText(this, "No Activity found to handle Intent!", Toast.LENGTH_LONG);
        }
        //finish();
    }

    /**
     * @return True if the most recent Memento from this TimeMap is older than a certain time.
     */
    private boolean isOutdated() {
        if(timeMap == null) return true;

        long time1 = timeMap.mostRecent().getTime(), time2 = System.currentTimeMillis();

        return(time2 - time1 > 24 * 60 * 60 * 1000 * OUTDATED_INTERVAL_DAYS);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Memento selected = timeMap.getMementos().get(position);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Details")
                .setMessage("Archive URL: " + selected.getArchiveUrl() +
                        "\nContent URL: " + selected.getContentUrl() +
                        "\nArchive Time: " + selected.getSubtitle() +
                        "\nScreen DPI: " + selected.getScreenType())
                .create();
        dialog.show();

        return true;
    }

    /**
     * Task to load a TimeMap for a given URL in the Background, either from file or via Dummy Content.
     * TODO: Use file download and XML parsing to generate TimeMap
     */
    public class GenerateTimeMapTask extends AsyncTask<String, Integer, TimeMap> {

        public ProgressDialog dialog;

        /**
         * Show a progress wheel while loading time map.
         */
        @Override
        protected void onPreExecute() {
            //Show this while content loads
            dialog = ProgressDialog.show(ViewArchiveActivity.this, "Fetching Mementos...",
                    "Loading Mementos for this site... (This may take a few minutes)");
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ViewArchiveActivity.this.finish();
                }
            });
            dialog.show();
        }

        /**
         * Generate a TimeMap from a given URL.
         */
        @Override
        protected TimeMap doInBackground(String... strings) {
            try {
                List<String> domains = HttpIO.getMobileDomains(strings[0]);
                TimeMap[] maps = new TimeMap[domains.size()];
                ArrayList<MementoGetter> threads = new ArrayList<MementoGetter>();
                MobileMemento.urls.clear();
                ExecutorService threadPool = Executors.newCachedThreadPool();

                for (int i = 0; i < domains.size(); i++) {
                    MementoGetter thread = new MementoGetter(domains.get(i), (i < 2) ? ScreenType.DESKTOP : ScreenType.PHONE);
                    threads.add(thread);
                    threadPool.execute(thread);
                }

                threadPool.shutdown();
                threadPool.awaitTermination(10, TimeUnit.MINUTES);
                for (int i = 0; i < threads.size(); i++) maps[i] = threads.get(i).getTimeMap();
                return TimeMap.union(maps);

            } catch (URISyntaxException e) {
                e.printStackTrace();
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }



        @Override
        protected void onProgressUpdate(Integer... progress) {
            dialog.setProgress(progress[0]);
        }

        /**
         * Dismiss dialog when task is finished.
         */
        @Override
        protected void onPostExecute(TimeMap result) {
            timeMap = result;
            adapter.setTimeMap(timeMap);
            dialog.dismiss();
            Toast.makeText(ViewArchiveActivity.this, "Found " + timeMap.size() + " Mementos", Toast.LENGTH_LONG).show();

            if(isOutdated()) showArchiveDialog();
        }

        private class MementoGetter implements Runnable
        {
            private final String url;
            private final ScreenType screenType;
            private TimeMap myTimeMap = null;

            MementoGetter(String url, ScreenType screenType)
            {
                this.url = url;
                this.screenType = screenType;
            }

            @Override
            public void run()
            {
                Log.d("URL", url);
                if (HttpIO.exists(url)) {
                    myTimeMap = TimeMap.newInstance(url, HttpIO.getMementoHTML(url), screenType);
                    MobileMemento.urls.add(url);
                }
            }

            public TimeMap getTimeMap()
            {
                return myTimeMap;
            }
        }


    }

}
