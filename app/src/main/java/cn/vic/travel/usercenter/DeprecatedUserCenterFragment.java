package cn.vic.travel.usercenter;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;

import java.util.List;

import cn.vic.travel.R;
import cn.vic.travel.localdata.FileUtil;
import cn.vic.travel.network.VicApplication;
import cn.vic.travel.widgets.BottomDialog;
import cn.vic.travel.widgets.SquareImageSwitcher;

import static cn.vic.travel.Utils.debugToast;
import static cn.vic.travel.localdata.Flag.REQUEST_CODE;

/**
 * Snake 创建于 2018/5/29.
 * 用户主页页面，已废弃
 */

public class DeprecatedUserCenterFragment extends Fragment {

    private SquareImageSwitcher mImageSwitcher;
    private BottomDialog bottomDialog;
    private float downX;        //按下屏幕时的坐标
    private Toolbar tbTest;
    private Button btn_NewPhoto;
    private VicApplication vicApplication=VicApplication.getInstance();
    //测试目录,格式：外部存储路径/TestImage/用户名/
    String testDir = Environment.getExternalStorageDirectory()+ "/TestImage/"+VicApplication.getInstance().getCurrentUserName()+"/";
    //List<String> nativeImages;       //原始图片路径数组
    List<String> savedImages;       //储存的图片路径数组
    String privateDir;  //本应用的私有数据存储路径
    int currentPosition=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //加载布局文件
        View view = inflater.inflate(R.layout.fragment_user_center, null);
        return view;
    }
    @SuppressLint("ClickableViewAccessibility")
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        bottomDialog=new BottomDialog(getActivity(),R.style.BottomDialog);
        tbTest=(Toolbar)getActivity().findViewById(R.id.tb_setting);
        btn_NewPhoto=(Button)getActivity().findViewById(R.id.btn_new_photo);
        mImageSwitcher=(SquareImageSwitcher) getActivity().findViewById(R.id.isr_photo_test);
        //todo 本应用的私有数据存储路径，格式：本应用私有存储路径/用户名/images/
        privateDir=getActivity().getExternalFilesDir("").getAbsolutePath()+VicApplication.getInstance().getCurrentUserName()+"/images/";
        savedImages=new FileUtil().findFile(testDir);       //获取存储目录下的图片路径和文件名
        mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            /**
             * @return 当前需要显示的ImageView控件，用于填充进ImageSwitcher中
             */
            @Override
            public View makeView() {
                ImageView image = new ImageView(getActivity());
                image.setImageResource(R.drawable.please_upload_your_photo);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                image.setLayoutParams(new ImageSwitcher.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
               return image;
            }
        });
        if(savedImages.size()!=0) {
            mImageSwitcher.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeFile(savedImages.get(0))));      //若之前曾选择过图片，将第一张图片设为默认图片
        }
        btn_NewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.show();
            }
        });

        mImageSwitcher.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:{
                        //手指按下的X坐标
                        downX = event.getX();
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        float upX = event.getX();
                        //若图片路径列表不为空
                        if(savedImages!=null){
                            //若列表内有内容
                            if(savedImages.size()>0){
                                //向右划
                                if(upX > downX){
                                    if(currentPosition>0) {
                                        currentPosition--;
                                        //currentPosition = currentPosition % images.size();
                                        debugToast("向右划，currentPosition：" + currentPosition);
                                        //mImageSwitcher.setImageResource(images[currentPosition % images.size()]);
                                        mImageSwitcher.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeFile(savedImages.get(currentPosition % savedImages.size()))));
                                    }
                                    else{
                                        currentPosition=savedImages.size()-1;
                                        debugToast("向右划，currentPosition：" + currentPosition);
                                        mImageSwitcher.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeFile(savedImages.get(currentPosition))));
                                    }
                                }
                                //向左划
                                if(upX < downX){
                                    currentPosition ++;
                                    currentPosition=currentPosition%savedImages.size();
                                    debugToast("向左划，currentPosition："+currentPosition);
                                    //mImageSwitcher.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeFile(path)));
                                    mImageSwitcher.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeFile(savedImages.get(currentPosition))));
                                }
                            }
                        }
                    }
                    break;
                }
                return true;
            }
        });

        bottomDialog.setOnClickListener(new BottomDialog.BottonDialogListener() {
            @Override
            public void onButton1Listener() {
                ImageSelectorUtils.openPhoto(getActivity(), REQUEST_CODE, false, 9);
            }
            @Override
            public void onButton2Listener() {
                debugToast("拍照");
            }

        });
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            List<String> nativeImages = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);     //原始路径的图片列表
            try {
                if(savedImages!=null){
                    //删除之前的图片
                    for(String path :savedImages){
                        new FileUtil().deleteFile(path);
                    }
                }
                for(String path :nativeImages){
                    new FileUtil().saveBitmap(BitmapFactory.decodeFile(path),testDir);        //以文件路径生成Bitmap后转为Drawable，存储至指定路径
                }
                //把图片上传至数据库
                uploadBtimap(new FileUtil().findFile(testDir));
            }
            catch (Exception e) {
                debugToast(e.toString());
                e.printStackTrace();
            }
            //在选择图片之后，将第一张图片设为默认图片
            mImageSwitcher.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeFile(nativeImages.get(0))));
        }
    }
    private void uploadBtimap(List<String> images){
        for(String path :images){
            vicApplication.uploadBtimap(path);
        }
    }

    public static DeprecatedUserCenterFragment getInstance() {
        return new DeprecatedUserCenterFragment();
    }

}
