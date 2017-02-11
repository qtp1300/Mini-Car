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
   // ��������ͼƬLinearLayout  
    private ViewGroup main;  
   // ����СԲ���LinearLayout  
    private ViewGroup group;  
 
    //��ǰҳ��  
    private int currentIndex;  
      
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��Ҫ��ʾ��ͼƬ�ŵ�ArrayList���У��浽��������  
       LayoutInflater inflater = getLayoutInflater();    
       pageViews = new ArrayList<View>();    
       pageViews.add(inflater.inflate(R.layout.guide_item1, null));  
       pageViews.add(inflater.inflate(R.layout.guide_item2, null));  
//       pageViews.add(inflater.inflate(R.layout.guide_item3, null));
       pageViews.add(inflater.inflate(R.layout.guide_item4, null));
       //��ͼƬ��ŵ�ImageView������
       imageViews = new ImageView[pageViews.size()];    
       main = (ViewGroup)inflater.inflate(R.layout.guide, null);    
       //��ȡ��ŵײ�������ViewGroup  
       group = (ViewGroup)main.findViewById(R.id.guide_point_ll);    
       viewPager = (ViewPager)main.findViewById(R.id.guide_viewpager); 
       for (int i = 0; i < pageViews.size(); i++) {    
       	System.out.println("pageViews.size() = " + pageViews.size());
           imageView = new ImageView(StartActivity.this);    
           imageView.setLayoutParams(new LayoutParams(20,20));    
           imageView.setPadding(20, 0, 20, 0);    
           imageViews[i] = imageView;    
             
           if (i == 0) {    
            //Ĭ��ѡ�е�һ��ͼƬ  
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
	
	/* ʹUser���TextView�ı����¼����� */
	 private Button.OnClickListener  textView1_OnClickListener = new Button.OnClickListener() {
	        public void onClick(View v) {
	            //�����Ѿ�����
	            setGuided();
	            //��ת
	            Intent mIntent = new Intent();
	            mIntent.setClass(StartActivity.this, MainActivity.class);
	            StartActivity.this.startActivity(mIntent);
	            StartActivity.this.finish();
	        }
	    };
	
	 private Button.OnClickListener  Button_OnClickListener = new Button.OnClickListener() {
	        public void onClick(View v) {
	            //�����Ѿ�����
	            setGuided();
	            //��ת
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
	        //����positionλ�õĽ���
	        public void destroyItem(View v, int position, Object arg2) {
	            ((ViewPager)v).removeView(pageViews.get(position));
	        }
	        public void finishUpdate(View arg0) {
	        }
	        //��ȡ��ǰ���������
	        public int getCount() {
	            return pageViews.size();
	        }
	        //��ʼ��positionλ�õĽ���
	        @Override
	        public Object instantiateItem(View v, int position) {
	            ((ViewPager) v).addView(pageViews.get(position));  
	            // ����ҳ��1�ڵİ�ť�¼�  
	            if (position == 0) {          	               
	                //�ı��¼�  
	                TextView mTextView01 = (TextView)findViewById(R.id.textView1);  
	                mTextView01.setOnClickListener(textView1_OnClickListener); 
	            } 
	            // ����ҳ��1�ڵİ�ť�¼�  
	            if (position == 2) {  
	            	TextView btn = (TextView)findViewById(R.id.textstart);  
	                btn.setOnClickListener(Button_OnClickListener);  
	            }  
	            return pageViews.get(position);  
	        }
	            
	        // �ж��Ƿ��ɶ������ɽ���
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


