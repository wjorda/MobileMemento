package org.mitre.mobilememento.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by wes on 12/13/14.
 */
public final class FileIO {

    public static final String CACHE_LIST = "mobile_cache.txt";

    public static boolean addSiteRecord(Context context, List<String> domains) {
        ArrayList<List<String>> cache = getCache(context);
        if(cache == null) return false;
        boolean flag = false;

        for(int i = 0; i < cache.size(); i++) {
            List<String> record = cache.get(i);
            if(record.get(0).matches(domains.get(0))) {
                cache.set(0, domains);
                flag = true;
            }
        }

        if(!flag) cache.add(domains);

        return writeCache(context, cache);
    }

    public static boolean writeCache(Context context, ArrayList<List<String>> elements) {
        try {
            FileOutputStream fos = context.openFileOutput(CACHE_LIST, Context.MODE_PRIVATE);
            for(List<String> record : elements) {
                for(String s : record) {
                    s = s.replace(" ", "&nbsp");
                    fos.write((s + " ").getBytes());
                }
                fos.write("end ".getBytes());
            }
            return true;
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            return false;
        }
    }

    public static ArrayList<List<String>> getCache(Context context) {
        try {
            ArrayList<ArrayList<String>> cache = new ArrayList<>();
            Scanner scan = new Scanner(context.openFileInput(CACHE_LIST));
            ArrayList<String> record = new ArrayList<>();

            while(scan.hasNext()) {
                String e = scan.next();

                if(e.matches("end")) {
                    cache.add(record);
                    record = new ArrayList<>();
                } else {
                    e = e.replace("&nbsp", " ");
                    record.add(e);
                }

            }
        } catch (IOException e) {
            Log.e("Exception", "File read failed: " + e.toString());
            return null;
        }
    }
}
