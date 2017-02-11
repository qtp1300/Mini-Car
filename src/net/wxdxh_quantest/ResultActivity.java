package net.wxdxh_quantest;

import java.io.IOException;

import net.wxdxh_quantest.global.Global;
import net.wxdxh_quantest.saveUtil.FileService;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import net.wxdxh_ourcar07.R;

public class ResultActivity extends Activity{
	private ImageView distance,encode,licese,shape,traffic,threeD;
	private TextView dis_title,en_title,lice_tilte,sh_title,traffic_title,threeD_titile;
	private FileService myService;
	private Bitmap disBitmap,enBitmap,liceBitmap,shBitmap,traBitmap,threeBitmap;
	private String en;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.dialog);
		init();
		value();
	}
	private void init(){
		myService=new FileService();
		distance=(ImageView) findViewById(R.id.distance);
		encode=(ImageView) findViewById(R.id.encode);
		licese=(ImageView) findViewById(R.id.licese);
		shape=(ImageView) findViewById(R.id.shape);
		traffic=(ImageView) findViewById(R.id.traffic);
		threeD=(ImageView) findViewById(R.id.threeD);
		dis_title=(TextView) findViewById(R.id.dis_title);
		en_title=(TextView) findViewById(R.id.encode_title);
		lice_tilte=(TextView) findViewById(R.id.lience_title);
		sh_title=(TextView) findViewById(R.id.shape_title);
		traffic_title=(TextView) findViewById(R.id.traffic_title);
		threeD_titile=(TextView) findViewById(R.id.threeD_title);
	}
	private void value(){
		disBitmap=myService.readPhoto(Global.RANGING+".png");
		if(disBitmap!=null)
			distance.setImageBitmap(disBitmap);
		enBitmap=myService.readPhoto(Global.QRNAME+".png");
		if(enBitmap!=null)
			encode.setImageBitmap(enBitmap);
		liceBitmap=myService.readPhoto(Global.LICENSE+".png");
		if(liceBitmap!=null)
			licese.setImageBitmap(liceBitmap);
		shBitmap=myService.readPhoto(Global.SHAPENAME+".png");
		if(shBitmap!=null)
			shape.setImageBitmap(shBitmap);
		traBitmap=myService.readPhoto(Global.TRAFFIC+".png");
		if(traBitmap!=null)
			traffic.setImageBitmap(traBitmap);
		threeBitmap=myService.readPhoto(Global.THREED+".png");
		if(threeBitmap!=null)
			threeD.setImageBitmap(threeBitmap);
		try {
			dis_title.setText("测距："+myService.read(Global.RANGING+".txt")+"mm");
			en=myService.read(Global.QRNAME+".txt");
			if(!en.equals(""))
				en_title.setText("颜色形状个数："+en);
			lice_tilte.setText("国"+myService.read(Global.LICENSE+".txt"));
			sh_title.setText("个数："+myService.read(Global.SHAPENAME+".txt"));
			traffic_title.setText("交通灯："+myService.read(Global.TRAFFICNAME+".txt"));
			threeD_titile.setText("立体显示："+myService.read(Global.TRAFFICNAME+".txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
