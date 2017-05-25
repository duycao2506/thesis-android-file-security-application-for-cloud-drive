package thesis.tg.com.s_cloud.data.from_third_party.google_drive;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.util.ArrayList;

import thesis.tg.com.s_cloud.data.CloudDriveWrapper;
import thesis.tg.com.s_cloud.entities.DriveUser;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;

import static thesis.tg.com.s_cloud.utils.EventConst.RESOLVE_CONNECTION_REQUEST_CODE;

/**
 * Created by admin on 5/6/17.
 */

public class GoogleDriveWrapper extends CloudDriveWrapper implements
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



    public static GoogleDriveWrapper getInstance(){
        if (gdinstance == null)
            gdinstance = new GoogleDriveWrapper();
        return gdinstance;
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
                .requestId()
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

        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... params) {
                if (result.isSuccess()) {
                    mGoogleApiClient.connect();
                    // Signed in successfully, show authenticated UI.
                    GoogleSignInAccount acct = result.getSignInAccount();

                    //TODO: Create User
                    DriveUser user = DriveUser.getInstance();
                    user.setGoogle_id(acct.getId());
                    if (user.isSignedIn())
                    {
                        //TODO: send authtoken to get add drive on server
                        return EventConst.ADD_DRIVE;
                    }

                    //TODO: send accesstoken to get userid from server
                    user.setName(acct.getDisplayName());
                    user.setId(acct.getId());
                    user.setEmail(acct.getEmail());
                    user.setAvatar(acct.getPhotoUrl());

                    //wait until connection is established
                    while (connection_ok == 0){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection_ok == -1) return EventConst.LOGIN_FAIL;
                    mGoogleCredential = GoogleAccountCredential.usingOAuth2(context, scopes);
                    mGoogleCredential.setSelectedAccountName(DriveUser.getInstance().getEmail());
                    HttpTransport transport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                    driveService = new com.google.api.services.drive.Drive.Builder(
                            transport, jsonFactory, mGoogleCredential)
                            .setApplicationName("S-Cloud")
                            .build();
                    addNewListFileTask("root");
                    return EventConst.LOGIN_SUCCESS;
                }
                return EventConst.LOGIN_FAIL;
            }

            @Override
            protected void onPostExecute(String aVoid) {
                super.onPostExecute(aVoid);
                afterLoginCallback.callback(aVoid, DriveType.GOOGLE,DriveUser.getInstance());
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


    @Override
    public void resetListFileTask() {
        super.resetListFileTask();
        this.glftList = new ArrayList<>();
        addNewListFileTask("root");
    }


    public void addNewListFileTask(String folderId){
        if (this.glftList == null)
            glftList = new ArrayList<>();
        GoogleListFileTask glft = new GoogleListFileTask(driveService,folderId);
        glftList.add(glft);
    }



    public void popListFileTask(){
        this.glftList.remove(this.glftList.size()-1);
    }

    @Override
    public void getFilesInTopFolder(boolean isMore, MyCallBack caller) {
        if (connection_ok != 1 || glftList == null) {
            caller.callback(EventConst.SERVER_NOT_CONNECTED, 1, null);
            return;
        }
        if (!isMore)
            glftList.get(glftList.size() - 1).refreshList(caller);
        else
            glftList.get(glftList.size() - 1).getMoreList(caller);

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