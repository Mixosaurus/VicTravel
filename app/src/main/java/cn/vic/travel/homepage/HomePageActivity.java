package cn.vic.travel.homepage;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageButton;

import java.util.HashMap;
import java.util.Map;

import cn.vic.travel.R;
import cn.vic.travel.conversation.ConversationFragment;
import cn.vic.travel.usercenter.UserCenterFragment;

import static cn.vic.travel.network.VicApplication.addToActivities;

public class HomePageActivity extends AppCompatActivity {
    private ImageButton btnWriteTip;
    private ImageButton btnSeekTip;
    private static final int REQUEST_CODE = 0x00000011;

    private Map<String, Fragment> fragments=new HashMap<String, Fragment>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //ft.show((R.id.fl_fragment_container,fragment.get("HomePageFragment"));
                    ft.replace(R.id.fl_fragment_container,fragments.get("HomePageFragment"));
                    ft.commitAllowingStateLoss();
                    return true;
                case R.id.navigation_dashboard:
                    ft.replace(R.id.fl_fragment_container,fragments.get("ConversationFragment"));
                    ft.commitAllowingStateLoss();
                    return true;
                case R.id.navigation_notifications:
                    ft.replace(R.id.fl_fragment_container,fragments.get("UserCenterFragment"));
                    ft.commitAllowingStateLoss();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        addToActivities("HomePageActivity",this);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragments();    //载入所有Fragment
        setDefaultFragment();       //设置默认Fragment

    }

    //返回键响应函数
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);       //保存页面状态
    }

    /**
     * 选择图片成功回调
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragments.get("UserCenterFragment").onActivityResult(requestCode, resultCode, data);
    }

    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_fragment_container, fragments.get("HomePageFragment"));
        transaction.commit();
    }

    private void loadFragments() {
        fragments.put("HomePageFragment",HomePageFragment.getInstance());
        fragments.put("ConversationFragment", ConversationFragment.getInstance());
        fragments.put("UserCenterFragment", UserCenterFragment.getInstance());
    }

}
