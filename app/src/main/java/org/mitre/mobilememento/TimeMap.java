package org.mitre.mobilememento;

import android.util.Log;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author wes
 *
 * A detailed set of <code>Memento</code>s for a given URL. They are sorted by time and screen DPI.
 */
public class TimeMap implements Comparator<Memento> {

    private ArrayList<Memento> mementos = new ArrayList<Memento>();
    private static final long cutoff = new Date(98, 0, 1, 12, 1, 32).getTime();
    private final ScreenType screenType;
    private final String contentUrl;

    /**
     * Required dummy constructor
     */
    public TimeMap(String contentURL, ScreenType screenType) {
        this.screenType = screenType;
        this.contentUrl = contentURL;
    }

    private TimeMap(TimeMap... others) {
        this.screenType = ScreenType.DESKTOP;
        this.contentUrl = others[0].contentUrl;
        for(TimeMap map : others) mementos.addAll(map.getMementos());
    }

    public static TimeMap newInstance(String contentURL, String httpResult, ScreenType screenType) {
        TimeMap database = new TimeMap(contentURL, screenType);

        for(String record : httpResult.split("\n,")) {
            try {
                if(record.indexOf('<') != -1 && record.indexOf("/>") != -1 && record.indexOf("datetime=\"") != -1)
                    database.mementos.add(new Memento(contentURL, record, screenType));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return database;
    }

    /**
     * Gets an array of all the years that this TimeMap has <code>Memento</code>s from.
     * @return An array of <code>Integer</code>s representing all of the years that this <code>TimeMap</code>
     * has <code>Memento</code>s from. Using wrapper objects because Java is stupid.
     */
    public Integer[] getAvailableYears() {
        Date curr = new Date(System.currentTimeMillis()), oldest = mementos.get(mementos.size() - 1).getDate();
        Integer years[] = new Integer[curr.getYear() - oldest.getYear() + 1], y = curr.getYear();

        for(int i = 0; i < years.length; i++) {
            years[i] = y;
            y--;
        }

        return years;
    }

    /**
     * Returns whether each of the 12 calendar months contains a memento for a given calendar year.
     * @param queryYear The year to calculate which months have mementos
     * @return A 12-long array of booleans relating to whether each month has a memento in this TimeMap.
     */
    public boolean[] getAvailableMonths(int queryYear) {
        boolean monthHasMemento[] = new boolean[12];
        Arrays.fill(monthHasMemento, false);

        for(Memento m : mementos) {
            monthHasMemento[m.getDate().getMonth()] = true;
        }

        return monthHasMemento;
    }

    /**
     * Returns an array of <code>Memento</code> for a given date.
     * @param date The date to query from this <code>TimeMap</code>
     * @return an array containing all of the <code>Memento</code>s that exist in this <code>TimeMap</code> on the date.
     */
    public Memento[] query(Date date) {
        ArrayList<Memento> result = new ArrayList<Memento>();
        for(Memento m : mementos)
            if(Memento.dateCompare(m.getDate(), date)) result.add(m);
        return result.toArray(new Memento[result.size()]);
    }

    /**
     * @return The most recent archive in this TimeMap.
     */
    public Date mostRecent() {
        Collections.sort(mementos, this);
        return mementos.get(0).getDate();
    }

    /**
     * Compares two <code>Memento</code>s by date
     * @param memento
     * @param memento2
     * @return
     */
    @Override
    public int compare(Memento memento, Memento memento2) {
        return memento2.getDate().compareTo(memento.getDate());
    }

    public int size() {
        return mementos.size();
    }

    protected ArrayList<Memento> getMementos() {
        Collections.sort(mementos, this);
        return mementos;
    }

    public int getDateIndex(int month, int year) {
        Collections.sort(mementos, this);
        for(int i = 0; i<mementos.size(); i++) {
            Date m = mementos.get(i).getDate();
            if(m.getYear() <= year && m.getMonth() <= month) return i;
        }

        return 0;
    }

    public static TimeMap union(TimeMap... timeMaps) {
        return new TimeMap(timeMaps);
    }

}
