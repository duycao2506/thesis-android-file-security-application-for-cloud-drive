package thesis.tg.com.s_cloud.data.from_third_party;

import android.app.Activity;
import android.app.Application;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import thesis.tg.com.s_cloud.MainActivity;
import thesis.tg.com.s_cloud.entities.DriveUser;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.framework_components.utils.GlobalUtils;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.activity.LoginActivity;
import thesis.tg.com.s_cloud.utils.EventConst;

import static thesis.tg.com.s_cloud.utils.EventConst.RESOLVE_CONNECTION_REQUEST_CODE;

/**
 * Created by admin on 5/6/17.
 */

public class GoogleDriveWrapper extends DriveWrapper implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {
    private GoogleApiClient mGoogleApiClient;
    private GoogleAccountCredential mGoogleCredential;
    private Drive driveService;
    private ArrayList<String> scopes;
    private Context context;
    private int connection_ok = 0;
    private ArrayList<GoogleListFileTask> glftList;


    private GoogleDriveWrapper(){

    }

    private static GoogleDriveWrapper instance;

    public static GoogleDriveWrapper getInstance(){
        if (instance == null)
            instance = new GoogleDriveWrapper();
        return instance;
    }


    /**
     * context has to be both connection fail listenner and connection success listenner
     * @param context
     * @return
     */

    public GoogleApiClient googleSignInServiceInit(Context context) {
        this.context = context;
        scopes = new ArrayList<>();
        scopes.add(DriveScopes.DRIVE);
        scopes.add(Scopes.PROFILE);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestScopes(new Scope(DriveScopes.DRIVE))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity) context /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
        return mGoogleApiClient;
    }

    public void handleSignInResult(final GoogleSignInResult result, final MyCallBack afterLoginCallback) {
        Log.d("HELLO", "handleSignInResult:" + result.isSuccess());

        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                if (result.isSuccess()) {
                    mGoogleApiClient.connect();
                    // Signed in successfully, show authenticated UI.
                    GoogleSignInAccount acct = result.getSignInAccount();

                    //TODO: Create User
                    DriveUser.getInstance().setName(acct.getDisplayName());
                    DriveUser.getInstance().setId(acct.getId());
                    DriveUser.getInstance().setEmail(acct.getEmail());

                    //wait until connection is established
                    while (connection_ok == 0);
                    if (connection_ok == -1) return false;
                    mGoogleCredential = GoogleAccountCredential.usingOAuth2(context, scopes);
                    mGoogleCredential.setSelectedAccountName(DriveUser.getInstance().getEmail());

                    HttpTransport transport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                    driveService = new com.google.api.services.drive.Drive.Builder(
                            transport, jsonFactory, mGoogleCredential)
                            .setApplicationName("S-Cloud")
                            .build();
                    addNewListFileTask("root");
                    return true;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                afterLoginCallback.callback(connection_ok == 1 && aVoid? EventConst.LOGIN_SUCCESS : EventConst.LOGIN_FAIL,1,DriveUser.getInstance());
            }
        }.execute();

    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        mGoogleApiClient.disconnect();
                    }
                });
    }

    private void setClient(GoogleApiClient client) {
        this.mGoogleApiClient = client;
    }


    public GoogleApiClient getClient() {
        return mGoogleApiClient;
    }


    /**
     * Drive folder and files methods
     */


    public void addNewListFileTask(String folderId){
        if (this.glftList == null)
            glftList = new ArrayList<>();
        GoogleListFileTask glft = new GoogleListFileTask(folderId);
        glftList.add(glft);
    }

    public void popListFileTask(){
        this.glftList.remove(this.glftList.size()-1);
    }

    @Override
    public void getFilesByFolderId(boolean isMore, MyCallBack caller) {
        if (connection_ok != 1 || glftList == null) {
            caller.callback(EventConst.SERVER_NOT_CONNECTED, 1, null);
            return;
        }
        if (!isMore)
            glftList.get(glftList.size() - 1).refreshList(driveService, caller);
        else
            glftList.get(glftList.size() - 1).getMoreList(driveService, caller);
    }

    /**
     * Google Drive
     * @param connectionResult
     */

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        connection_ok = -1;
        if (connectionResult.hasResolution()) {
            try {

                connectionResult.startResolutionForResult((Activity) context, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
                Toast.makeText(context, "Unknown Error", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        Toast.makeText(context, "CONNECTED",Toast.LENGTH_LONG).show();

        connection_ok = 1;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    public Drive getDriveService() {
        return driveService;
    }
}
