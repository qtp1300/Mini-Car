package net.wxdxh_quantest;

import java.util.ArrayList;
import net.wxdxh_ourcar07.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class StartActivity extends Activity {

    private ViewPager viewPager;    
    private ArrayList<View> pageViews;    
    private ImageView imageView;    
    private ImageView[] imageViews;   
   // 包裹滑动图片LinearLayout  
    private ViewGroup main;  
   // 包裹小圆点的LinearLayout  
    private ViewGroup group;  
 
    //当前页码  
    private int currentIndex;  
      
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//将要显示的图片放到ArrayList当中，存到适配器中  
       LayoutInflater inflater = getLayoutInflater();    
       pageViews = new ArrayList<View>();    
       pageViews.add(inflater.inflate(R.layout.guide_item1, null));  
       pageViews.add(inflater.inflate(R.layout.guide_item2, null));  
//       pageViews.add(inflater.inflate(R.layout.guide_item3, null));
       pageViews.add(inflater.inflate(R.layout.guide_item4, null));
       //将图片存放到ImageView集合中
       imageViews = new ImageView[pageViews.size()];    
       main = (ViewGroup)inflater.inflate(R.layout.guide, null);    
       //获取存放底部导航点ViewGroup  
       group = (ViewGroup)main.findViewById(R.id.guide_point_ll);    
       viewPager = (ViewPager)main.findViewById(R.id.guide_viewpager); 
       for (int i = 0; i < pageViews.size(); i++) {    
       	System.out.println("pageViews.size() = " + pageViews.size());
           imageView = new ImageView(StartActivity.this);    
           imageView.setLayoutParams(new LayoutParams(20,20));    
           imageView.setPadding(20, 0, 20, 0);    
           imageViews[i] = imageView;    
             
           if (i == 0) {    
            //默认选中第一张图片  
               imageViews[i].setBackgroundResource(R.drawable.green_point);    
           } else {    
               imageViews[i].setBackgroundResource(R.drawable.gray_point);    
           }                 
           group.addView(imageViews[i]);    
       }    
         
       setContentView(main);          
       viewPager.setAdapter(new GuidePageAdapter());    
       viewPager.setOnPageChangeListener(new GuidePageChangeListener());    
   } 
	
	/* 使User点击TextView文本的事件处理 */
	 private Button.OnClickListener  textView1_OnClickListener = new Button.OnClickListener() {
	        public void onClick(View v) {
	            //设置已经引导
	            setGuided();
	            //跳转
	            Intent mIntent = new Intent();
	            mIntent.setClass(StartActivity.this, MainActivity.class);
	            StartActivity.this.startActivity(mIntent);
	            StartActivity.this.finish();
	        }
	    };
	
	 private Button.OnClickListener  Button_OnClickListener = new Button.OnClickListener() {
	        public void onClick(View v) {
	            //设置已经引导
	            setGuided();
	            //跳转
	            Intent mIntent = new Intent();
	            mIntent.setClass(StartActivity.this, MainActivity.class);
	            StartActivity.this.startActivity(mIntent);
	            StartActivity.this.finish();
	        }
	    }; 
	    private static final String SHAREDPREFERENCES_NAME = "my_pref";
	    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";
	    private void setGuided(){
	        SharedPreferences settings = getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
	        SharedPreferences.Editor editor = settings.edit();
	        editor.putString(KEY_GUIDE_ACTIVITY, "false");
	        editor.commit();
	    }
	    class GuidePageAdapter extends PagerAdapter{
	        //销毁position位置的界面
	        public void destroyItem(View v, int position, Object arg2) {
	            ((ViewPager)v).removeView(pageViews.get(position));
	        }
	        public void finishUpdate(View arg0) {
	        }
	        //获取当前窗体界面数
	        public int getCount() {
	            return pageViews.size();
	        }
	        //初始化position位置的界面
	        @Override
	        public Object instantiateItem(View v, int position) {
	            ((ViewPager) v).addView(pageViews.get(position));  
	            // 测试页卡1内的按钮事件  
	            if (position == 0) {          	               
	                //文本事件  
	                TextView mTextView01 = (TextView)findViewById(R.id.textView1);  
	                mTextView01.setOnClickListener(textView1_OnClickListener); 
	            } 
	            // 测试页卡1内的按钮事件  
	            if (position == 2) {  
	            	TextView btn = (TextView)findViewById(R.id.textstart);  
	                btn.setOnClickListener(Button_OnClickListener);  
	            }  
	            return pageViews.get(position);  
	        }
	            
	        // 判断是否由对象生成界面
	        @Override
	        public boolean isViewFromObject(View v, Object arg1) {
	            return v == arg1;
	        }
	        
	        public void startUpdate(View arg0) {
	        }
	        public int getItemPosition(Object object) {
	            return super.getItemPosition(object);
	        }
	        public void restoreState(Parcelable arg0, ClassLoader arg1) {
	        }
	        public Parcelable saveState() {
	          return null;
	        }
	    }
	    class GuidePageChangeListener implements OnPageChangeListener{
	        public void onPageScrollStateChanged(int arg0) {
	        }
	        public void onPageScrolled(int arg0, float arg1, int arg2) {
	        }
 
     @Override    
     public void onPageSelected(int arg0) {    
         currentIndex = arg0;  
         for (int i = 0; i < imageViews.length; i++) {    
             imageViews[arg0].setBackgroundResource(R.drawable.green_point);  
               
             if (arg0 != i) {    
                 imageViews[i].setBackgroundResource(R.drawable.gray_point);    
             }    
         }  
     }    
	}
}


