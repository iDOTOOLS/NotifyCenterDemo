package com.idotools.notifycenterdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import com.idotools.notifycenterdemo.Tools.MyPicasso;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    Context mContext =this;
    ViewPager viewPager;
    PhotoViewAttacher mAttacher;
    PhotoView[] photoViews;
    ActionBar actionBar;
    int length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        viewPager = (ViewPager) findViewById(R.id.pager);
        //load image
        Intent intent = getIntent();
        //String imageUrl = intent.getStringExtra("imageurl");
        String[] imageUrls = intent.getStringArrayExtra("imageurls");
        length = imageUrls.length;
        int position = intent.getIntExtra("clickPosition", 0);
        actionBar.setTitle(String.valueOf(position+1) + "/" + String.valueOf(length));





        photoViews = new PhotoView[length];
        for(int i = 0 ;i < length;i++) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            PhotoView photoView = new PhotoView(this);
            photoView.setLayoutParams(layoutParams);
            photoViews[i]=photoView;
            MyPicasso.getInstance(this).load(imageUrls[i]).into(photoViews[i]);
            mAttacher = new PhotoViewAttacher(photoViews[i]);
            mAttacher.update();
        }

        viewPager.setAdapter(new MyAdapter(length));
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(position);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        actionBar.setTitle(String.valueOf(position + 1) + "/" + String.valueOf(length));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class MyAdapter extends PagerAdapter {
        int count;
        public  MyAdapter(int count){
            this.count = count;
        }

        @Override
        public int getCount() {
            return  length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {

            //((ViewPager)container).removeView(mImageViews[position]);

        }

        @Override
        public Object instantiateItem(View container, int position) {
            if (position < count) {
                try {
                    ((ViewPager) container).addView(photoViews[position]);
                    return photoViews[position];
                } catch (Exception e) {
                    return null;
                }
            }else {
                return null;
            }


        }
    }

}
