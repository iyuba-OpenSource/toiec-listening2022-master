package com.iyuba.toeiclistening.activity.holder;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.activity.test.rankinto.SpeakWorkFragment;

public class FragmentHolderActivity extends AppCompatActivity {


    public static final String FRAGMENT_NAME = "fragmentname";
    public static final String TITLE = "title";
    public static final String ARGUMENTS = "arguments";


    public interface Names {
        String SPEAK_WORK = "speakwork";
    }

    Toolbar mToolbar;

    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);


        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String fragmentName = getIntent().getStringExtra(FRAGMENT_NAME);
        if (TextUtils.isEmpty(fragmentName)) {
            return;
        }
        switch (fragmentName) {
            case Names.SPEAK_WORK: {
                FragmentTransaction transaction = fm.beginTransaction();
                Bundle bundle = getIntent().getBundleExtra(ARGUMENTS);
                transaction.replace(R.id.frame_container, SpeakWorkFragment.newInstance(bundle));
                mToolbar.setTitle(bundle.getString(SpeakWorkFragment.UNAME_KEY));
                transaction.commit();
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
