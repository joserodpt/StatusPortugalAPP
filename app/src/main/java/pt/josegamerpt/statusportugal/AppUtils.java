package pt.josegamerpt.statusportugal;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

public class AppUtils {

    public static JSONObject getInfoFromAPI(String link) throws IOException, JSONException {
        HttpURLConnection connection;
        BufferedReader reader;

        URL url = new URL(link);
        connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(A.b()[0], A.b()[1].toCharArray());
            }
        });

        InputStream stream = connection.getInputStream();
        reader = new BufferedReader(new InputStreamReader(stream));

        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }

        if (connection != null) {
            connection.disconnect();
        }
        if (reader != null) {
            reader.close();
        }

        return new JSONObject(buffer.toString());
    }

    public static String getRawInfoFromAPI(String link) throws IOException, JSONException {
        HttpURLConnection connection;
        BufferedReader reader;

        URL url = new URL(link);
        connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(A.b()[0], A.b()[1].toCharArray());
            }
        });

        InputStream stream = connection.getInputStream();
        reader = new BufferedReader(new InputStreamReader(stream));

        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }

        if (connection != null) {
            connection.disconnect();
        }
        if (reader != null) {
            reader.close();
        }

        return buffer.toString();
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}