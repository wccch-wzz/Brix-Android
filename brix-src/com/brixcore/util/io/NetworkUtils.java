package com.brixcore.util.io;

import androidx.core.text.HtmlCompat;
import com.brixcore.R;
import com.brixcore.util.Pair;
import com.brixcore.util.StringUtils;
import com.brixcore.utils.BrixPath;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.apache.commons.lang3.CharEncoding;
import org.jsoup.helper.HttpConnection;

/* JADX INFO: loaded from: classes3.dex */
public final class NetworkUtils {
    public static final String NAME_VALUE_SEPARATOR = "=";
    public static final String PARAMETER_SEPARATOR = "&";
    private static final int TIME_OUT = 8000;

    private NetworkUtils() {
    }

    public static String addHttpsIfMissing(String url) {
        if (Pattern.compile("^(?<scheme>[a-zA-Z][a-zA-Z0-9+.-]*)://").matcher(url).find()) {
            return url;
        }
        if (url.startsWith("//")) {
            return "https:" + url;
        }
        return "https://" + url;
    }

    public static String withQuery(String baseUrl, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(baseUrl);
        boolean first = true;
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (param.getValue() != null) {
                if (first) {
                    if (!baseUrl.isEmpty()) {
                        sb.append('?');
                    }
                    first = false;
                } else {
                    sb.append(PARAMETER_SEPARATOR);
                }
                sb.append(encodeURL(param.getKey()));
                sb.append(NAME_VALUE_SEPARATOR);
                sb.append(encodeURL(param.getValue()));
            }
        }
        return sb.toString();
    }

    public static List<Pair<String, String>> parseQuery(URI uri) {
        return parseQuery(uri.getRawQuery());
    }

    public static List<Pair<String, String>> parseQuery(String queryParameterString) {
        if (queryParameterString == null) {
            return Collections.emptyList();
        }
        List<Pair<String, String>> result = new ArrayList<>();
        Scanner scanner = new Scanner(queryParameterString);
        try {
            scanner.useDelimiter(PARAMETER_SEPARATOR);
            while (scanner.hasNext()) {
                String[] nameValue = scanner.next().split(NAME_VALUE_SEPARATOR);
                if (nameValue.length <= 0 || nameValue.length > 2) {
                    throw new IllegalArgumentException("bad query string");
                }
                String name = decodeURL(nameValue[0]);
                String value = nameValue.length == 2 ? decodeURL(nameValue[1]) : null;
                result.add(Pair.pair(name, value));
            }
            scanner.close();
            return result;
        } catch (Throwable th) {
            try {
                scanner.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private static boolean endsWithDomainSuffix(String host, String domainSuffix) {
        return host.endsWith(domainSuffix.toLowerCase());
    }

    public static URLConnection createConnection(URL url) throws IOException {
        try {
            URLConnection connection = url.openConnection();
            String host = url.getHost().toLowerCase();
            if (endsWithDomainSuffix(host, "d.pcs.baidu.com") || endsWithDomainSuffix(host, "baidupcs.com")) {
                connection.setRequestProperty("User-Agent", "pan.baidu.com");
            } else {
                connection.setRequestProperty("User-Agent", "Brix/" + (BrixPath.CONTEXT != null ? BrixPath.CONTEXT.getString(R.string.app_version) : "1.0"));
            }
            connection.setUseCaches(false);
            connection.setConnectTimeout(TIME_OUT);
            connection.setReadTimeout(TIME_OUT);
            connection.setRequestProperty("Accept-Language", Locale.getDefault().toLanguageTag());
            return connection;
        } catch (IllegalArgumentException | MalformedURLException e) {
            throw new IOException(e);
        }
    }

    public static HttpURLConnection createHttpConnection(URL url) throws IOException {
        return (HttpURLConnection) createConnection(url);
    }

    public static String encodeLocation(String location) {
        StringBuilder sb = new StringBuilder();
        boolean left = true;
        for (char ch : location.toCharArray()) {
            switch (ch) {
                case ' ':
                    if (left) {
                        sb.append("%20");
                        continue;
                    } else {
                        sb.append('+');
                    }
                    break;
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    left = false;
                    break;
            }
            if (ch >= 128) {
                sb.append(encodeURL(Character.toString(ch)));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static HttpURLConnection resolveConnection(HttpURLConnection conn) throws IOException {
        return resolveConnection(conn, null);
    }

    public static HttpURLConnection resolveConnection(HttpURLConnection conn, List<String> redirects) throws IOException {
        int redirect = 0;
        while (true) {
            conn.setUseCaches(false);
            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(TIME_OUT);
            conn.setInstanceFollowRedirects(false);
            Map<String, List<String>> properties = conn.getRequestProperties();
            String method = conn.getRequestMethod();
            int code = conn.getResponseCode();
            if (code < 300 || code > 307 || code == 306 || code == 304) {
                break;
            }
            String newURL = conn.getHeaderField("Location");
            conn.disconnect();
            if (redirects != null) {
                redirects.add(newURL);
            }
            if (redirect > 20) {
                throw new IOException("Too much redirects");
            }
            final HttpURLConnection redirected = (HttpURLConnection) new URL(conn.getURL(), encodeLocation(newURL)).openConnection();
            properties.forEach(new BiConsumer() { // from class: com.brixcore.util.io.NetworkUtils$$ExternalSyntheticLambda1
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((List) obj2).forEach(new Consumer() { // from class: com.brixcore.util.io.NetworkUtils$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj3) {
                            httpURLConnection.addRequestProperty(str, (String) obj3);
                        }
                    });
                }
            });
            redirected.setRequestMethod(method);
            conn = redirected;
            redirect++;
        }
        return conn;
    }

    public static String doGet(URL url) throws IOException {
        HttpURLConnection con = createHttpConnection(url);
        return IOUtils.readFullyAsString(resolveConnection(con).getInputStream());
    }

    public static String doPost(URL u, Map<String, String> params) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (params != null) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                sb.append(e.getKey()).append(NAME_VALUE_SEPARATOR).append(e.getValue()).append(PARAMETER_SEPARATOR);
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return doPost(u, sb.toString());
    }

    public static String doPost(URL u, String post) throws IOException {
        return doPost(u, post, HttpConnection.FORM_URL_ENCODED);
    }

    public static String doPost(URL url, String post, String contentType) throws IOException {
        byte[] bytes = post.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection con = createHttpConnection(url);
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty(HttpConnection.CONTENT_TYPE, contentType + "; charset=utf-8");
        con.setRequestProperty("Content-Length", "" + bytes.length);
        OutputStream os = con.getOutputStream();
        try {
            os.write(bytes);
            if (os != null) {
                os.close();
            }
            return readData(con);
        } catch (Throwable th) {
            if (os != null) {
                try {
                    os.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static String readData(HttpURLConnection con) throws IOException {
        try {
            InputStream stdout = con.getInputStream();
            try {
                String fullyAsString = IOUtils.readFullyAsString("gzip".equals(con.getContentEncoding()) ? IOUtils.wrapFromGZip(stdout) : stdout);
                if (stdout != null) {
                    stdout.close();
                }
                return fullyAsString;
            } catch (Throwable th) {
                if (stdout != null) {
                    try {
                        stdout.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IOException e) {
            InputStream stderr = con.getErrorStream();
            try {
                if (stderr == null) {
                    throw e;
                }
                String fullyAsString2 = IOUtils.readFullyAsString("gzip".equals(con.getContentEncoding()) ? IOUtils.wrapFromGZip(stderr) : stderr);
                if (stderr != null) {
                    stderr.close();
                }
                return fullyAsString2;
            } catch (Throwable th3) {
                if (stderr != null) {
                    try {
                        stderr.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        }
    }

    public static String detectFileName(URL url) throws IOException {
        HttpURLConnection conn = resolveConnection(createHttpConnection(url));
        int code = conn.getResponseCode();
        if (code / 100 == 4) {
            throw new FileNotFoundException();
        }
        if (code / 100 != 2) {
            throw new IOException(url + ": response code " + conn.getResponseCode());
        }
        return detectFileName(conn);
    }

    public static String detectFileName(HttpURLConnection conn) {
        String disposition = conn.getHeaderField("Content-Disposition");
        if (disposition == null || !disposition.contains("filename=")) {
            String u = conn.getURL().toString();
            return decodeURL(StringUtils.substringAfterLast(u, org.apache.commons.io.IOUtils.DIR_SEPARATOR_UNIX));
        }
        return decodeURL(StringUtils.removeSurrounding(StringUtils.substringAfter(disposition, "filename="), "\""));
    }

    public static URL toURL(String str) {
        try {
            return new URL(str);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean isURL(String str) {
        try {
            new URL(str);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean urlExists(URL url) throws IOException {
        HttpURLConnection con = resolveConnection(createHttpConnection(url));
        int responseCode = con.getResponseCode();
        con.disconnect();
        return responseCode / 100 == 2;
    }

    public static String encodeURL(String toEncode) {
        try {
            return URLEncoder.encode(toEncode, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new Error();
        }
    }

    public static String decodeURL(String toDecode) {
        try {
            return URLDecoder.decode(toDecode, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new Error();
        }
    }
}
