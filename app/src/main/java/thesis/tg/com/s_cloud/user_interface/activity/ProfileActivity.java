package thesis.tg.com.s_cloud.user_interface.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.entities.DriveUser;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView civProfileAvatar;
    TextView tvNameHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        setUpView();
    }

    private void setUpView() {
        civProfileAvatar = (CircleImageView) findViewById(R.id.ivProfileAvatar);
        Picasso.with(this).load(DriveUser.getInstance().getAvatar()).into(civProfileAvatar);
        tvNameHeader = (TextView) findViewById(R.id.tvHeaderName);
        tvNameHeader.setText(DriveUser.getInstance().getName());
    }
}
