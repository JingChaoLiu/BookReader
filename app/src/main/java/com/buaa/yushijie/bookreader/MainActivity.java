package com.buaa.yushijie.bookreader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.buaa.yushijie.bookreader.Fragments.AboutMeFragment;
import com.buaa.yushijie.bookreader.Fragments.AboutMeNavigationFragment;
import com.buaa.yushijie.bookreader.Fragments.BookCategoryFragment;
import com.buaa.yushijie.bookreader.Fragments.MyBookShelfMainPartFragment;
import com.buaa.yushijie.bookreader.Fragments.MyBookShelfNavigationFragment;
import com.buaa.yushijie.bookreader.Fragments.NavigationFragment;
import com.buaa.yushijie.bookreader.Services.CurrentApplication;
import com.buaa.yushijie.bookreader.Services.DownLoadBookInfoService;
import com.buaa.yushijie.bookreader.Services.DownLoadMyBookShelfService;

import java.util.concurrent.TimeoutException;

import bean.UserBean;

public class MainActivity extends AppCompatActivity {

    private NavigationFragment cityNavigationFragment;
    private MyBookShelfNavigationFragment bookshelfNavigationFragment;
    private BookCategoryFragment bookCategoryFragment;
    private MyBookShelfMainPartFragment myBookShelfMainPartFragment;
    private AboutMeFragment aboutMeFragment;
    private AboutMeNavigationFragment aboutMeNavigationFragment;

    private static final String USERNAME="username";


    private UserBean user;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.navigation_container,cityNavigationFragment)
                            .commit();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.book_category_container,new BookCategoryFragment())
                            .commit();
                    return true;
                case R.id.navigation_dashboard:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.navigation_container,bookshelfNavigationFragment)
                            .commit();
                    FragmentManager fm1 = getSupportFragmentManager();
                    fm1.beginTransaction().replace(R.id.book_category_container,new MyBookShelfMainPartFragment()).commit();
                    return true;
                case R.id.navigation_notifications:
                    getSupportFragmentManager().beginTransaction().replace(R.id.navigation_container,aboutMeNavigationFragment)
                            .commit();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.book_category_container,aboutMeFragment)
                            .commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init
        cityNavigationFragment = new NavigationFragment();
        bookshelfNavigationFragment = new MyBookShelfNavigationFragment();
        bookCategoryFragment = new BookCategoryFragment();
        myBookShelfMainPartFragment = new MyBookShelfMainPartFragment();
        aboutMeFragment = new AboutMeFragment();
        aboutMeNavigationFragment = new AboutMeNavigationFragment();
        //get data
        getNecessaryData();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.book_category_container, new BookCategoryFragment())
                .commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.navigation_container, cityNavigationFragment)
                .commit();



    }

    public void getNecessaryData(){
        //get user info and user category thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownLoadBookInfoService service = new DownLoadBookInfoService();
                user = service.getUserInfo(getIntent().getStringExtra(USERNAME));
                CurrentApplication cu = (CurrentApplication)getApplication();
                cu.setUser(user);
                //get category
                try {
                    DownLoadMyBookShelfService services = new DownLoadMyBookShelfService();
                    UserBean ub = cu.getUser();
                    cu.setUserCategories(services.getCategoryNameList(ub.account));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
