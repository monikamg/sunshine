package android.example.sunshine.sync;



import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.example.sunshine.data.WeatherContract;
import android.example.sunshine.utilities.NetworkUtils;
import android.example.sunshine.utilities.OpenWeatherJsonUtils;


import java.net.URL;
import java.util.Arrays;

//  COMPLETED (1) Create a class called SunshineSyncTask
public class SunshineSyncTask {

//  COMPLETED (2) Within SunshineSyncTask, create a synchronized public static void method called syncWeather

    /**
     * Performs the network request for updated weather, parses the JSON from that request, and
     * inserts the new weather information into our ContentProvider. Will notify the user that new
     * weather has been loaded if the user hasn't been notified of the weather within the last day
     * AND they haven't disabled notifications in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncWeather(Context context) {

//      COMPLETED (3) Within syncWeather, fetch new weather data

        try {
            /*
             * The getUrl method will return the URL that we need to get the forecast JSON for the
             * weather. It will decide whether to create a URL based off of the latitude and
             * longitude or off of a simple location as a String.
             */
            URL weatherRequestUrl = NetworkUtils.getUrl(context);
            /* Use the URL to retrieve the JSON */
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);

            /* Parse the JSON into a list of weather values */
            ContentValues[] weatherValues = OpenWeatherJsonUtils
                    .getWeatherContentValuesFromJson(context, jsonWeatherResponse);

            /*
             * In cases where our JSON contained an error code, getWeatherContentValuesFromJson
             * would have returned null. We need to check for those cases here to prevent any
             * NullPointerExceptions being thrown. We also have no reason to insert fresh data if
             * there isn't any to insert.
             */
            if (weatherValues != null) {
                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver sunshineContentResolver = context.getContentResolver();

//              COMPLETED (4) If we have valid results, delete the old data and insert the new
                /* Delete old weather data because we don't need to keep multiple days' data */
                sunshineContentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null);

                /* Insert our new weather data into Sunshine's ContentProvider */
                sunshineContentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        weatherValues);

                System.out.println("Debugger - DB" + sunshineContentResolver);
            }

            /* If the code reaches this point, we have successfully performed our sync */

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }

}
