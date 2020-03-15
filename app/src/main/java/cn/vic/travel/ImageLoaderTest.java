package cn.vic.travel;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import cn.vic.travel.traveltogether.TravelInfoAdapter;
import cn.vic.travel.widgets.PullUpRefeshLayout;

import static cn.vic.travel.Utils.debugToast;

public class ImageLoaderTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_loader_test);

    }
}