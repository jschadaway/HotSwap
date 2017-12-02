package com.teamcaffeine.hotswap.activity.navigation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.teamcaffeine.hotswap.R;
import com.teamcaffeine.hotswap.activity.ListItemFragment;
import com.teamcaffeine.hotswap.activity.ProfileFragment;
import com.teamcaffeine.hotswap.activity.messaging.InboxFragment;

public class NavigationActivity extends AppCompatActivity implements
        InboxFragment.InboxFragmentListener,
        ProfileFragment.ProfileFragmentListener,
        ListItemFragment.ListItemFragmentListener,
        SearchFragment.SearchFragmentListener {

    private final String TAG = "NavigationActivity";

    public BottomNavigationView navigation;

    private ListItemFragment listItemFragment;
    private InboxFragment inboxFragment;
    private ProfileFragment profileFragment;
    private SearchFragment searchFragment;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction ft;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Log.i(TAG, "nav search: ");
                    ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.dynamicContent, searchFragment);
                    ft.commit();
                    return true;
                case R.id.navigation_list:
                    Log.i(TAG, "nav list: ");
                    ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.dynamicContent, listItemFragment);
                    ft.commit();
                    return true;
                case R.id.navigation_inbox:
                    Log.i(TAG, "nav inbox: ");
                    ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.dynamicContent, inboxFragment);
                    ft.commit();
                    return true;
                case R.id.navigation_profile:
                    Log.i(TAG, "nav profile: ");
                    ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.dynamicContent, profileFragment);
                    ft.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        listItemFragment = new ListItemFragment();
        inboxFragment = new InboxFragment();
        profileFragment = new ProfileFragment();
        searchFragment = new SearchFragment();

        int intentFragment = getIntent().getExtras().getInt("frgToLoad");
        switch (intentFragment) {
            case 1:
                ft = fragmentManager.beginTransaction();
                ft.replace(R.id.dynamicContent, searchFragment);
                ft.commit();
        }

        navigation.setSelectedItemId(R.id.navigation_search);
    }
}
