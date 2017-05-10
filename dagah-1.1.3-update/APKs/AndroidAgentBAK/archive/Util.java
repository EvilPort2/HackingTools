package com.shevirah.androidagent;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class Util {
    public static Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void showToast(final Context context, final CharSequence message) {
        HANDLER.post(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    /**
     * http://stackoverflow.com/a/13007325/1934509
     *
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces =
                    Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4) return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase()
                                        : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignore) {
        }

        return "";
    }

    public static void printExtras(String tag, Bundle bundle, String prefix) {
        if (bundle.isEmpty()) {
            Log.d(tag, prefix != null ? prefix + "is empty" : "is empty");
            return;
        }

        StringBuilder str = new StringBuilder();
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            str.append(String.format("%s - %s (%s)", key, value.toString(),
                    value.getClass().getName())).append('\n');
        }
        if (str.length() > 0) {
            str.deleteCharAt(str.length() - 1);
        }

        int maxLogSize = 1000;
        for (int i = 0; i <= str.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > str.length() ? str.length() : end;
            Log.d(tag, prefix != null ? (prefix + str.substring(start, end))
                    : str.substring(start, end));
        }
    }

    public static void printExtras(String tag, Bundle bundle) {
        printExtras(tag, bundle, null);
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec(propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
        } catch (IOException ex) {
            if (Const.DEBUG) Log.e("Util", "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    if (Const.DEBUG) Log.e("Util", "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    public static boolean isFileExist(String path) {
        return new File(path).exists();
    }

    public static String readFileAsString(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    public static String fileSizeInMb(String path) {
        try {
            return new DecimalFormat("#.##").format(
                    ((double) (new File(path).length())) / 1024 / 1024) + "Mb";
        } catch (Exception e) {
            if (Const.DEBUG) Log.e("Util", "fileSize: ", e);
            return "none";
        }
    }

    public static File createFile(String path) {
        File file = new File(path);
        try {
            boolean newFile = file.createNewFile();
            if (newFile) return file;
        } catch (IOException e) {
            if (Const.DEBUG) Log.e("Util", "createFile: ", e);
        }
        return null;
    }

    public static void logRequest(String tag, int code, String responseBody, String url,
            String postBody) {
        String msg = "----------------------------"
                + "\nurl " + url
                + (postBody != null ? "\npostBody " + postBody : "")
                + "\ncode " + code
                + "\nresponseBody " + (!isEmpty(responseBody) ? responseBody: "<empty>"
                + "\n----------------------------");
        if (code != 200) {
            Log.e(tag, msg);
        } else {
            Log.d(tag, msg);
        }
    }

    public static void logRequest(String tag, int code, String body, String url) {
        logRequest(tag, code, body, url, null);
    }

    public static boolean setMaximumTimeToLock(ContentResolver resolver, int val) {
        try {
            return Settings.System.putInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT, val);
        } catch (Throwable e) {
            if (Const.DEBUG) Log.e("Util", "setMaximumTimeToLock: ", e);
            return false;
        }
    }

    public static long getMaximumTimeToLock(ContentResolver resolver) {
        try {
            return Settings.System.getInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT, -1);
        } catch (Throwable e) {
            if (Const.DEBUG) Log.e("Util", "getMaximumTimeToLock: ", e);
            return -1;
        }
    }
}
