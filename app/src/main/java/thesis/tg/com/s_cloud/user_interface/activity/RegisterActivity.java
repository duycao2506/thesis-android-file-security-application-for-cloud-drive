package thesis.tg.com.s_cloud.user_interface.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import javax.annotation.RegEx;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_local.MockData;
import thesis.tg.com.s_cloud.framework_components.data.from_server.GeneralResponse;
import thesis.tg.com.s_cloud.framework_components.data.from_server.POSTRequestService;
import thesis.tg.com.s_cloud.framework_components.data.from_server.RequestService;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.EventConst;
import thesis.tg.com.s_cloud.utils.UiUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {


    //Profile View

    EditText edtFullname, edtJob, edtCountry, edtBirthday, edtEmail, edtConfirmPass, edtPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        View view = findViewById(R.id.layoutDriveActions);
        view.setVisibility(View.GONE);
        assignViews();

    }


    private void assignViews() {
        edtFullname = (EditText) findViewById(R.id.edtProfileName);

        edtBirthday = (EditText) findViewById(R.id.edtProfileBirthday);
        edtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        StringBuilder sb = new StringBuilder();
                        sb.append(selectedday);
                        sb.append("/");
                        sb.append(selectedmonth);
                        sb.append("/");
                        sb.append(selectedyear);
                        edtBirthday.setText(sb.toString());
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle(getString(R.string.se_date));
                mDatePicker.show();
            }
        });
        edtBirthday.setFocusable(false);


        edtCountry = (EditText) findViewById(R.id.edtProfileCountry);

        edtEmail = (EditText) findViewById(R.id.edtProfileEmail);
        edtEmail.setEnabled(true);

        edtJob = (EditText) findViewById(R.id.edtProfileJob);

        edtConfirmPass = (EditText) findViewById(R.id.edtProfilePasswordCnf);
        edtPass = (EditText) findViewById(R.id.edtProfilePassword);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.confirm:
                if (!checkInput()){
                    break;
                }
                Intent dataIntent = new Intent();
                dataIntent.putExtra(EventConst.USER_JSON, getRegisterObj().toString());
                setResult(RESULT_OK, dataIntent);
                finish();
                break;
        }
        return true;
    }

    private boolean checkInput() {
        String regexEmail =
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        boolean email = Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText()).matches();
        if (!email)
        {
            Toast.makeText(this, R.string.wrongemail,Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean pass = edtPass.getText().length() > 6
                && edtPass.getText().toString().compareTo(edtConfirmPass.getText().toString()) == 0;
        if (!pass){
            Toast.makeText(this, R.string.checkpass,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_has_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public JSONObject getRegisterObj(){
        JSONObject jsoRegister = new JSONObject();
        try {
            jsoRegister.put("email",edtEmail.getText().toString());
            jsoRegister.put("password", DataUtils.encodePassword(
                    edtEmail.getText().toString(),
                    edtPass.getText().toString()));
            jsoRegister.put("fullname", edtFullname.getText());
            jsoRegister.put("job",edtJob.getText());
            jsoRegister.put("country", edtCountry.getText());
            jsoRegister.put("birthday",edtBirthday.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsoRegister;
    }
}
