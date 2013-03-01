package com.zfdang.wscreen;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.touch.TouchImageView;

public class MainActivity extends Activity
	implements OnPageChangeListener, OnClickListener, OnLongClickListener {

    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;
    
    // image sources
    private static final int[] pics = { R.drawable.snoopy,
            R.drawable.belle};

    // navigator icons
    private ImageView[] dots;
    // current position
    private int currentIndex;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		views = new ArrayList<View>();
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        
        //��ʼ������ͼƬ�б�
        for(int i=0; i<pics.length; i++) {
    		TouchImageView iv = new TouchImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageResource(pics[i]);
            iv.setMaxZoom(4f);
            iv.setOnLongClickListener(this);

            views.add(iv);
        }		
		
        vp = (ViewPager) findViewById(R.id.viewpager);
        vpAdapter = new ViewPagerAdapter(views);
        vp.setAdapter(vpAdapter);
        
        vp.setOnPageChangeListener(this);
        
        initDots();
	}

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

        dots = new ImageView[pics.length];

        //ѭ��ȡ��С��ͼƬ
        for (int i = 0; i < pics.length; i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(true);//����Ϊ��ɫ
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);//����λ��tag������ȡ���뵱ǰλ�ö�Ӧ
        }
        currentIndex = 0;
        dots[currentIndex].setEnabled(false);//����Ϊ��ɫ����ѡ��״̬
    }	

    /**
     * set current page 
     */
    private void setCurView(int position)
    {
        if (position < 0 || position >= pics.length) {
            return;
        }

        vp.setCurrentItem(position);
    }
    
    /**
     * set current navigator icon to be selected 
     */
    private void setCurDot(int positon)
    {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
        dots[positon].setEnabled(false);
        dots[currentIndex].setEnabled(true);
        currentIndex = positon;
    }
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int arg0) {
        // set navigator icon status
        setCurDot(arg0);		
		
	}

	@Override
	public void onClick(View v) {
        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);		
	}

	@Override
	public boolean onLongClick(View v) {
		Toast.makeText(this, "onLongClick", Toast.LENGTH_SHORT).show();
		return true;
	}

}
