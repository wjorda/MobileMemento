package org.mitre.mobilememento.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Static class that handles all Web and HTTP operations.
 * Created by wes on 12/3/14.
 */
public final class HttpIO
{

    public static final String MOBILE_REGEX[] =
            {"", "m.", "mobile.", "wap.", "touch.", "mweb."};
    public static final String WWW_REGEX[][] = {
            {"http://", "http://www.", "http://www2."},
            {"https://", "https://www.", "https://www2."}
    };

    /**
     * Queries the Los Alamos National Laboratory Memento server for the list of Mementos of a specific url.
     *
     * @param urlToRead The resource URL to query the server for.
     * @return A string containing the entire document HTML text
     */
    public static final String getMementoHTML(String urlToRead)
    {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        StringBuilder result = new StringBuilder();
        try {
            Log.d("MEMENTO_URL", urlToRead);
            url = new URL("http://timetravel.mementoweb.org/timemap/link/1/" + urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result.append(line).append("\n");
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * Determines if a resource described by a URL is accessible to the live web.
     *
     * @param urlToCheck URL to check
     * @return True if the resource exists and is accessible. (If HTTP request returns < 400)
     */
    public static final boolean exists(String urlToCheck)
    {
        try {
            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection connection = (HttpURLConnection) new URL(urlToCheck).openConnection();
            connection.setRequestMethod("HEAD");
            Log.d("Response Code for " + urlToCheck, connection.getResponseCode() + "");
            return (connection.getResponseCode() < 400);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Uses regex matching to get the URL of the desktop version of a given mobile site. If the
     *
     * @param url URL of the mobile site. If this is a
     * @return
     * @throws URISyntaxException
     */
    public static final String getDesktopURL(String url) throws URISyntaxException
    {

        String modUrl = url;

        for (String regex : MOBILE_REGEX) {
            if (url.contains(regex)) modUrl = url.replace(regex, "");
        }

        return modUrl;
    }

    public static final String getDomainName(String url) throws URISyntaxException
    {
        URI uri = new URI(url);
        String host = uri.getHost();
        return host.startsWith("www.") ? host.substring(4) : host;
    }

    public static final List<String> getMobileDomains(String url) throws URISyntaxException
    {
        Log.d("URL:", url);
        url = getDesktopURL(url);
        Log.d("URL:", url);
        String domain = getDomainName(url);

        List<String> domains = new ArrayList<String>();

        for (String regex : MOBILE_REGEX) {

            String wwwRegeces[];
            if (url.contains("https://")) wwwRegeces = WWW_REGEX[1];
            else wwwRegeces = WWW_REGEX[0];

            for (String wwwRegex : wwwRegeces) {
                String modDomain = regex + domain;
                String[] split = url.split(domain);
                String modURL = wwwRegex + modDomain + split[1];
                domains.add(modURL);
            }
        }

        return domains;
    }

}
