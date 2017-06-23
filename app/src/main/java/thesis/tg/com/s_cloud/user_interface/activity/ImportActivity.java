package thesis.tg.com.s_cloud.user_interface.activity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import fi.iki.elonen.NanoHTTPD;
import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.SConnectOutputstream;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.content.ContentValues.TAG;

public class ImportActivity extends AppCompatActivity {

    private final int PORT = 8800;
    public static final String
            JS_FOLDER = "js",
            CSS_FOLDER = "css",
            FONT_FOLDER = "fonts",
            IMG_FOLDER = "img",
            HTML_FILE = "html",
            FAVICON = "favicon";

    TextView tvIpAddress;
    private SCloudHttpd server;
    File tmpFolder;
    File destFolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);


        tvIpAddress = (TextView) findViewById(R.id.tvIpAddress);


        createTmpFolder();
        startWebServer();

    }

    private void createTmpFolder() {
        tmpFolder = new File(Environment.getExternalStorageDirectory().getPath()+"/"+"Scloudtmp");
        destFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "S-Cloud");
        if (!destFolder.exists()){
            boolean iss = destFolder.mkdir();
        }
        if (!tmpFolder.exists()){
            boolean issuc = tmpFolder.mkdir();
        }
    }

    private boolean destroyTmpFolder(){
        return tmpFolder.delete();
    }

    private void startWebServer() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format(Locale.getDefault(),"%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));//String.format();
        tvIpAddress.setText(formatedIpAddress +":" + PORT);

        try {
            server = new SCloudHttpd(formatedIpAddress, PORT);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.stop();
        destroyTmpFolder();

    }

    private class SCloudHttpd extends NanoHTTPD {
        private SCloudHttpd(String hostname, int port) throws IOException {
            super(hostname, port);
        }

        private Response POSTHandler(IHTTPSession session) {
            Map<String, String> headers = session.getHeaders();
            Map<String, String> parms = session.getParms();
            Method method = session.getMethod();
            String uri = session.getUri();
            Map<String, String> files = new HashMap<>();

            if (Method.POST.equals(method) || Method.PUT.equals(method)) {
                try {
                    session.parseBody(files);
                } catch (IOException ioe) {
                    return new Response("Internal Error IO Exception: " + ioe.getMessage());
                } catch (ResponseException re) {
                    return new Response(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
                }
            }

            if ("/transfer".equalsIgnoreCase(uri)) {
                String filename = parms.get("uploadbuttonTransfer");
                String tmpFilePath = files.get("uploadbuttonTransfer");
                if (null == filename || null == tmpFilePath) {
                    // Response for invalid parameters
                }
                File dst = new File(destFolder, filename);
                if (dst.exists()) {
                    // Response for confirm to overwrite
                }
                File src = new File(tmpFilePath);
                try {
                    InputStream in = new FileInputStream(src);
                    OutputStream out = new FileOutputStream(dst);
                    byte[] buf = new byte[65536];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                } catch (IOException ioe) {
                    // Response for failed
                    return new Response("ERRORRRRRR !!!!" + ioe.getMessage());
                }

                // Response for success
                return new Response("SUCC");
            }

            if ("/decode".equalsIgnoreCase(uri)) {
                String filename = parms.get("uploadbuttonDecode");
                String tmpFilePath = files.get("uploadbuttonDecode");
                if (null == filename || null == tmpFilePath) {
                    // Response for invalid parameters
                }
                File dst = new File(tmpFolder, filename);
                if (dst.exists()) {
                    // Response for confirm to overwrite
                }
                File src = new File(tmpFilePath);
                try {
                    InputStream in = new FileInputStream(src);
                    SConnectOutputstream scos = new SConnectOutputstream(DataUtils.getDataHeader(),new FileOutputStream(dst));
                    byte[] buf = new byte[65536];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        scos.write(buf, 0, len);
                    }
                    in.close();
                    scos.close();
                } catch (IOException ioe) {
                    // Response for failed
                    return new Response("ERRORRRRRR !!!!" + ioe.getMessage());
                }

                // Response for success
                return new Response(buildLinkElement(filename, dst.getPath()));
            }

            return null;
        }

        @Override
        public Response serve(IHTTPSession session) {
//            final StringBuilder buf = new StringBuilder();
//            for (Entry<Object, Object> kv : session.getHeaders())
//                buf.append(kv.getKey() + " : " + kv.getValue() + "\n");
            Method method = session.getMethod();
            if (method == Method.GET){
                return this.GETHandler(session);
            }
            else
            if (method == Method.POST){
                return this.POSTHandler(session);
            }
            return null;

//
        }

        private String buildLinkElement(String content, String link){
            StringBuilder sb = new StringBuilder();
            sb.append("<a href='");
            sb.append(link);
            sb.append("'>");
            sb.append(content);
            sb.append("</a>");
            return sb.toString();
        }

        private Response GETHandler(IHTTPSession session) {
            InputStream mbuffer = null;
            String uri = session.getUri();
            String[] tokens = uri.split("[/\\.]");
            String prefix = tokens.length > 1 ?tokens[1] : uri;
            String postfix = tokens.length > 1 ? tokens[tokens.length - 1] : uri;
            if (uri.compareTo("/") == 0){
                postfix = "html";
                uri = "index.html";
            }
            try {
                switch (prefix) {
                    case JS_FOLDER:
                    case FONT_FOLDER:
                    case CSS_FOLDER:
                    case IMG_FOLDER:
                    case FAVICON:
                        mbuffer = getAssets().open(uri.substring(1));
                        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(postfix);
                        return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, mime, mbuffer);
                    default:
                        switch (postfix) {
                            case HTML_FILE:
                                mbuffer = getAssets().open(uri);
                                return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, MIME_HTML, mbuffer);
                            default:
                                Log.d(TAG, "request for media on sdCard " + uri);

                                File request = new File(uri);
                                mbuffer = new FileInputStream(request);
                                FileNameMap fileNameMap = URLConnection.getFileNameMap();
                                String mimeType = fileNameMap.getContentTypeFor(uri);

                                Response streamResponse = new Response(NanoHTTPD.Response.Status.OK, mimeType, mbuffer);
                                Random rnd = new Random();
                                String etag = Integer.toHexString(rnd.nextInt());
                                streamResponse.addHeader("ETag", etag);
                                streamResponse.addHeader("Connection", "Keep-alive");
                                return streamResponse;
                        }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }
}

