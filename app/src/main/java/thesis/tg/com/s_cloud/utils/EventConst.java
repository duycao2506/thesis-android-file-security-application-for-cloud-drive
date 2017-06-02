package thesis.tg.com.s_cloud.utils;

/**
 * Created by admin on 5/2/17.
 */

public interface EventConst {
    String OPEN_FOLDER = "OPEN_FOLDER";
    int RESOLVE_CONNECTION_REQUEST_CODE = 25;
    String GET_FILE_LIST = "GET_FILE_LIST";
    String LOGIN_SUCCESS = "LOGIN_SUCCESS";
    String LOGIN_FAIL = "LOGIN_FAIL";
    int LOGIN_REQUEST_CODE = 26 ;
    int LOGIN_CANCEL_RESULT_CODE = 27;
    int LOGIN_SUCCESS_RESULT_CODE = 28;
    String SERVER_NOT_CONNECTED = "SERVER_NOT_CONNECTED";
    String ERROR = "Error";
    String FILE_MENU = "FILE_MENU";
    int FILE_SELECT_REQUEST_CODE = 29;
    String FINISH_UPLOADING = "finish_upload";
    String FINISH_DOWNLOADING = "finish_download";
    String ADD_DRIVE = "ADD_DRIVE";
    String BACK_FOLDER = "BACK_FOLDER";
    String SCROLL_DOWN = "SCROLL_DOWN";
    String SCROLL_UP = "SCROLL_UP";
    int PROFILE_OPEN = 30;
    String RELOGIN_FAIL = "RELOGIN_FAIL";
    String RELOGIN_SUCCESS = "RELOGIN_SUCCESS";
    String DISCONNECT = "DISCONNECT";
    String CONNECT_DRIVE = "CONNECT_DRIVE";
}
