package net.wxdxh_quantest.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Delayed;

import net.wxdxh_quantest.MainActivity;
import net.wxdxh_quantest.global.Global;
import net.wxdxh_quantest.saveUtil.FileService;
import net.wxdxh_quantest.util.CRCUtil;
import net.wxdxh_quantest.util.TrafficUtil;

import android.R.integer;
import android.bluetooth.BluetoothClass.Device.Major;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class Client {
	private Handler quanHandler;//全自动处理
	private Context context;
	public Client(Context context,Handler qHandler){
		this.quanHandler=qHandler;
		this.context=context;
	}
	private int port = 60000;
	private DataInputStream bInputStream;
	private DataOutputStream bOutputStream;
	private Socket socket=null;
	private byte[] rbyte = new byte[15];
	private Timer timer;
	public byte TYPE = (byte) 0xAA;
	public short MAJOR = 0x00;
	public short FIRST = 0x00;
	public short SECOND = 0x00;
	public short THRID = 0x00;
	public short CHECKSUM=0x00;
	public int dis;
	public static byte[] openbyte=new byte[6];
	public int inx;
	long  UltraSonic=0;
	long  UltraSonic1,UltraSonic2,UltraSonic3,UltraSonic4,UltraSonic5;// 超声波
	private Thread reThread = new Thread(new Runnable() {
		@Override
		public void run() {
			// TODO Auto1-generated method stub
			while (socket != null && !socket.isClosed()) {
				try {
					bInputStream.read(rbyte);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	});
	

	public void connect(final Handler handler, String IP) {
		try {
			socket = new Socket(IP, port);
			bInputStream = new DataInputStream(socket.getInputStream());
			bOutputStream = new DataOutputStream(socket.getOutputStream());
			reThread.start();
			Message message = new Message();
			message.obj = rbyte;
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					Message message = new Message();
					message.obj = rbyte;
					message.what = 1;
					handler.sendMessage(message);
				}
			}, 0, 500);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	// 发送函数
	public void send() {
		try {
			CHECKSUM=(short) ((MAJOR+FIRST+SECOND+THRID)%256);
			// 发送数据字节数组
			byte[] sbyte = { 0x55, (byte) TYPE, (byte) MAJOR, (byte) FIRST, (byte) SECOND, (byte) THRID ,(byte) CHECKSUM,(byte) 0xBB};
			if (socket != null && !socket.isClosed()) {
				bOutputStream.write(sbyte, 0, sbyte.length);
				bOutputStream.flush();
			}
			else{
				Toast.makeText(context, "wifi连接异常,请检查WiFi连接是否正确！", Toast.LENGTH_LONG).show();
				quanHandler.sendEmptyMessage(-5);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public void send_voice(byte [] textbyte) {
		try {
			// 发送数据字节数组
			if (socket != null && !socket.isClosed()) {
				bOutputStream.write(textbyte, 0, textbyte.length);
				bOutputStream.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	
	private byte[] bytesend(byte[] sbyte) {
		byte[] textbyte = new byte[sbyte.length + 5];
		textbyte[0] = (byte) 0xFD;
		textbyte[1] = (byte) (((sbyte.length + 2) >> 8) & 0xff);
		textbyte[2] = (byte) ((sbyte.length + 2) & 0xff);
		textbyte[3] = 0x01;// 合成语音命令
		textbyte[4] = (byte) 0x01;// 编码格式
		for (int i = 0; i < sbyte.length; i++) {
			textbyte[i + 5] = sbyte[i];
		}
		return textbyte;
	}

	
	
    private void sendvoice(String src){      //发送语音
    	byte[] sbyte = null;
		try {
			sbyte = bytesend(src.getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		send_voice(sbyte);
//		while (rbyte[2] != 79 && rbyte[2] != 65 ); 	
    } 
	
	
	    // 主车前进
		public void go(int sp_n, int en_n) {
			MAJOR = 0x02;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = (byte) (en_n & 0xff);
			THRID = (byte) (en_n >> 8);
			send();
			if(mark>0)
			while(rbyte[2]!=3);
		}
		//机械臂车前进
		public void goduty(int sp_n, int en_n) {
			MAJOR = 0x02;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = (byte) (en_n & 0xff);
			THRID = (byte) (en_n >> 8);
			send();
		    yanchi(2000);		
		}	
		// 主车后退
		public void back(int sp_n, int en_n) {
			MAJOR = 0x03;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = (byte) (en_n & 0xff);
			THRID = (byte) (en_n >> 8);
			send();
			if(mark>0)
			while(rbyte[2]!=3);
		}
		//机械臂车后退
		public void backduty(int sp_n, int en_n) {
			MAJOR = 0x03;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = (byte) (en_n & 0xff);
			THRID = (byte) (en_n >> 8);
			send();
			yanchi(2000);
		}

	// 主车左转
	    public void left(int sp_n) {
	       light(1, 0);
		   MAJOR = 0x04;
		   FIRST = (byte) (sp_n & 0xFF);
		   SECOND = 0x00;
		   THRID = 0x00;
		   send();
		   if (mark > 0)
			  while (rbyte[2] != 2);
	}

	// 机械臂车左转
	   public void leftduty(int sp_n) {
		  MAJOR = 0x04;
		  FIRST = (byte) (sp_n & 0xFF);
		  SECOND = 0x00;
		  THRID = 0x00;
		  send();
		  yanchi(2000);
	  }
		//修改主车左转	
        public void leftchange(int sp_n){
        	MAJOR = 0x08;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			if(mark>0)
			while(rbyte[2]!=2);
        }   
           
        //修改主车右转
        public void rightchange(int sp_n) {
			MAJOR = 0x09;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			if(mark>0)
			while(rbyte[2]!=2);
		}
               
		// 主车右转
		public void right(int sp_n) {
			light(0, 1);
			MAJOR = 0x05;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			if(mark>0)
			while(rbyte[2]!=2);
		}
		// 机械臂车右转
		public void rightduty(int sp_n) {
		    MAJOR = 0x05;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			yanchi(2000);
		}

		// 主车停车
		public void stop() {
			MAJOR = 0x01;
			FIRST = 0x00;
			SECOND = 0x00;
			THRID = 0x00;
			send();
			if(mark>0)
			while(rbyte[2]!=0);
		}
		// 机械臂车停车
		public void stopduty() {
			MAJOR = 0x01;
			FIRST = 0x00;
			SECOND = 0x00;
			THRID = 0x00;
			send();
			yanchi(1000);
		}
		// 主车循迹
		public void line(int sp_n) {
			light(0, 0);
			MAJOR = 0x06;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			if(mark>0)
				while (rbyte[2] != 1 && rbyte[2] != 4){
					if(fan_flag){
						if(rbyte[3]==1){
							fan();
							fan_flag=false;
							line(500);
						}
					}
				}				
		}
		
		//机械臂车循迹
		public void lineduty(int sp_n) {
			MAJOR = 0x06;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			yanchi(4000);		
		}
		
		
		
		//修改主车循迹
		public void linechange(int sp_n ,int line) {
			MAJOR = 0x15;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = (byte) (line & 0xFF);
			THRID = 0x00;
			send();
			if(mark>0)
			while (rbyte[2] != 1 && rbyte[2] != 4);						
		}
		
		public void gotoBZW(){        //标志物识别距离计算
		    dis = (int)((float)(distance()-220)/15*13);	
			go(80, dis);
		}
		
		public void deputy(int i){//从车机械臂
			if(i==1)//从车
			TYPE=0x05;
			else if(i==2)//主车
				TYPE=(byte) 0xAA;
		}
		public void vice(int i){//主从车状态转换
			byte type=TYPE;
			if(i==1){//从车状态
				TYPE=(byte) 0xAA;
				MAJOR = 0x80;
				FIRST = 0x01;
				SECOND = 0x00;
				THRID = 0x00;
				send();
				yanchi(500);
				TYPE=0x05;
				MAJOR = 0x80;
				FIRST = 0x01;
				SECOND = 0x00;
				THRID = 0x00;
				send();
			}
			else if(i==2){//主车状态
				TYPE=(byte) 0xAA;
				MAJOR = 0x80;
				FIRST = 0x00;
				SECOND = 0x00;
				THRID = 0x00;
				send();
				yanchi(500);
				TYPE=0x05;
				MAJOR = 0x80;
				FIRST = 0x00;
				SECOND = 0x00;
				THRID = 0x00;
				send();
			}
			TYPE= type;
		}
	//机械臂
	public void arm(int one,int two,int thrid){
		MAJOR=(short) one;
		FIRST=(short) two;
		SECOND = (short) thrid;
		THRID = 0x00;
		send();
	}
	public void armcontrol(int data){    //机械臂三个状态控制
		switch (data) {
		case 0:
			arm(0x72, 0x07, 0x00);
			break;
		case 1:
			arm(0x70, 0x02, 0x01);
			break;
        case 2:
        	arm(0x70, 0x02, 0x02);
        	break;
        case 3:
        	arm(0x70, 0x02, 0x03);
        	break;
		default:
			break;
		}
	}
	
	// 红外
	public void infrared(byte one, byte two, byte thrid, byte four, byte five,
			byte six) {
		MAJOR = 0x10;
		FIRST = one;
		SECOND = two;
		THRID = thrid;
		send();
		yanchi(1000);
		MAJOR = 0x11;
		FIRST = four;
		SECOND = five;
		THRID = six;
		send();
		yanchi(1000);
		MAJOR = 0x12;
		FIRST = 0x00;
		SECOND = 0x00;
		THRID = 0x00;
		send();
		yanchi(2000);
	}
	// 红外
		public void infrared() {
			MAJOR = 0x10;
			FIRST = openbyte[0];
			SECOND = openbyte[1];
			THRID = openbyte[2];
			send();
			yanchi(1000);
			MAJOR = 0x11;
			FIRST = openbyte[3];
			SECOND = openbyte[4];
			THRID = openbyte[5];
			send();
			yanchi(1000);
			MAJOR = 0x12;
			FIRST = 0x00;
			SECOND = 0x00;
			THRID = 0x00;
			send();
			yanchi(2000);
		}
	// 双色LED灯
	public void lamp(byte command) {
		MAJOR = 0x40;
		FIRST = command;
		SECOND = 0x00;
		THRID = 0x00;
		send();
	}

	// 指示灯
	public void light(int left, int right) {
		if (left == 1 && right == 1) {
			MAJOR = 0x20;
			FIRST = 0x01;
			SECOND = 0x01;
			THRID = 0x00;
			send();
		} else if (left == 1 && right == 0) {
			MAJOR = 0x20;
			FIRST = 0x01;
			SECOND = 0x00;
			THRID = 0x00;
			send();
		} else if (left == 0 && right == 1) {
			MAJOR = 0x20;
			FIRST = 0x00;
			SECOND = 0x01;
			THRID = 0x00;
			send();
		} else if (left == 0 && right == 0) {
			MAJOR = 0x20;
			FIRST = 0x00;
			SECOND = 0x00;
			THRID = 0x00;
			send();
		}
		yanchi(800);
	}

	// 蜂鸣器
	public void buzzer(int i) {
		if (i == 1)             //开蜂鸣器
			FIRST = 0x01;
		else if (i == 0)        //关蜂鸣器
			FIRST = 0x00;
		MAJOR = 0x30;
		SECOND = 0x00;
		THRID = 0x00;
		send();
	}

	public void picture(int i) {// 图片上翻和下翻
		if (i == 1)
			MAJOR = 0x50;
		else
			MAJOR = 0x51;
		FIRST = 0x00;
		SECOND = 0x00;
		THRID = 0x00;
		send();
	}

	public void gear(int i) {// 光照档位加
		if (i == 1)
			MAJOR = 0x61;
		else if (i == 2)
			MAJOR = 0x62;
		else if (i == 3)
			MAJOR = 0x63;
		FIRST = 0x00;
		SECOND = 0x00;
		THRID = 0x00;
		send();
		yanchi(3000);
	}

	public void fan() {// 风扇
		infrared((byte) 0x67, (byte) 0x34,
				(byte) 0x78, (byte) 0xA2, (byte) 0xFD,
				(byte) 0x27);
//		infrared();
	}

	public void gate(int i) {// 闸门
		byte type=TYPE;
		if (i == 1) {
			TYPE = 0x03;
			MAJOR = 0x01;
			FIRST = 0x01;
			SECOND = 0x00;
			THRID = 0x00;
			send();
		} else if (i == 2) {
			TYPE = 0x03;
			MAJOR = 0x01;
			FIRST = 0x02;
			SECOND = 0x00;
			THRID = 0x00;
			send();
		}
		TYPE=type;
	}
	//LCD显示标志物进入计时模式
	public void digital_close(){//数码管关闭
		byte type=TYPE;
		TYPE = 0x04;
		MAJOR = 0x03;
		FIRST = 0x00;
		SECOND = 0x00;
		THRID = 0x00;
		send();
		TYPE = type;
	}
	public void digital_open(){//数码管打开
		byte type=TYPE;
		TYPE = 0x04;
		MAJOR = 0x03;
		FIRST = 0x01;
		SECOND = 0x00;
		THRID = 0x00;
		send();
		TYPE = type;
	}
	public void digital_clear(){//数码管清零
		byte type=TYPE;
		TYPE = 0x04;
		MAJOR = 0x03;
		FIRST = 0x02;
		SECOND = 0x00;
		THRID = 0x00;
		send();
		TYPE = type;
	}
	public void digital_dic(long ultraSonic){//LCD显示标志物第二排显示距离
		byte type=TYPE;
		TYPE = 0x04;
		MAJOR = 0x04;
		FIRST = 0x00;
		SECOND = (short) (ultraSonic/100);
		THRID = (short) (ultraSonic%100);
		send();
		TYPE = type;
	}
	public void digital(int i, int one, int two, int three) {// 数码管
		byte type=TYPE;
		TYPE = 0x04;
		if (i == 1) {//数据写入第一排数码管
			MAJOR = 0x01;
			FIRST = (byte) one;
			SECOND = (byte) two;
			THRID = (byte) three;
		} else if (i == 2) {//数据写入第二排数码管
			MAJOR = 0x02;
			FIRST = (byte) one;
			SECOND = (byte) two;
			THRID = (byte) three;
		}
		send();
		TYPE = type;
	}
	public void infrared_stereo(short [] data){//立体显示
		MAJOR = 0x10;
		FIRST =  0xff;
		SECOND = data[0];
		THRID = data[1];
		send();
		yanchi(500);
		MAJOR = 0x11;
		FIRST = data[2];
		SECOND = data[3];
		THRID = data[4];
		send();
		yanchi(500);
		MAJOR = 0x12;
		FIRST = 0x00;
		SECOND = 0x00;
		THRID = 0x00;
		send();
		yanchi(500);
	}
	private void getGear(int i){    //获取光照档位
		long []array=new long[4];
		long nowNum;
		array[0]=light();
		for(int m=1;m<array.length;m++){
			gear(1);
			yanchi(1000);
			array[m]=light();
		}
		nowNum=array[3];
		Arrays.sort(array);
		for(int n=0;n<array.length;n++){
			if(nowNum==array[n]){
				gear((i+3-n)%4);
				yanchi(500);
				break;
			}
		}
		quanHandler.sendEmptyMessage(26);
	}
	private long light(){     //光照强度
		long Light = 0;
		Light = rbyte[7] & 0xff;
		Light = Light << 8;
		Light += rbyte[6] & 0xff;
		return Light;
	}
	private long distance(){		  //超声波测距
		
		UltraSonic1 = rbyte[5] & 0xff;
		UltraSonic1 = UltraSonic1 << 8;
		UltraSonic1 += rbyte[4] & 0xff;
		yanchi(100);
		
		UltraSonic2 = rbyte[5] & 0xff;
		UltraSonic2 = UltraSonic2 << 8;
		UltraSonic2 += rbyte[4] & 0xff;
		yanchi(100);
		
		UltraSonic3 = rbyte[5] & 0xff;
		UltraSonic3 = UltraSonic3 << 8;
		UltraSonic3 += rbyte[4] & 0xff;
		yanchi(100);
		
		UltraSonic4 = rbyte[5] & 0xff;
		UltraSonic4 = UltraSonic4 << 8;
		UltraSonic4 += rbyte[4] & 0xff;
		yanchi(100);
		
		UltraSonic5 = rbyte[5] & 0xff;
		UltraSonic5 = UltraSonic5 << 8;
		UltraSonic5 += rbyte[4] & 0xff;
		yanchi(100);
		
		UltraSonic = (UltraSonic1+UltraSonic2+UltraSonic3+UltraSonic4+UltraSonic5)/5;
		
		return UltraSonic;	
	}
	// 沉睡
	public void yanchi(int time) {       //延迟函数
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
		
	
	
	public static boolean lice_flag  = false;
	public int check = 0;
	public int mark=-50;
	private int[] speeds={80,80,10,80,80};//前进、循迹、左右转
	private int[] encoders={100,120,100,80,220,50,150};//前进、进车库、进车库、进车库、出车库
	public int gearNum=3;
	private boolean fan_flag = false;
	public int deputyNum=5;
	public int zhuNum=1;			//	B1,	  D1,	F1,	   H1,  J1
	public static boolean[] carports={false,false,false,false,false};
	private void quan(){
		switch (mark) {
		case 1010:
			go(80, 100);
			linechange(80, 1);
			leftchange(80);
			linechange(80, 1);
			leftchange(80);
			linechange(80, 1);
			leftchange(80);
			linechange(80, 1);
			leftchange(80);
			left(80);
			linechange(80, 1);
			rightchange(80);
			linechange(80, 1);
			rightchange(80);
			linechange(80, 1);
			rightchange(80);
			linechange(80, 1);
			rightchange(80);
			linechange(80, 1);
			
		break;
//		case 10://出车库
//			mark= 20;
//			outCarport();
//			checkway(1, 1, 3,1);	//测距 算法       主车下一任务点	     从车执行          mark跳转
//			break;
//		case 20://前往二维码位置扫描
//			goQrcode();
//			mark=60;
//			break;
////		case 30://前往超声波测距位置
////			goRanging();
////			mark=40;
////			break;
////		case 40://超声波测距
////			yanchi(1000);
////			quanHandler.sendEmptyMessage(17);
////			mark=50;
////			break;			
////		case 50:      //前往风扇排风系统位置
////			goFan();  
////			mark = -50;
////			break;
////		case 50:
////			digital_dic(distance());
////			yanchi(1000);
////			mark = 60;
////			break;
//		case 60:    //去往车牌1
//			mark = 61;
//			goShape1();
////			mark = -50;
//			break;
//		case 61:         //去车牌2
//			mark = 62;
//			goShape2();
//			break;
//		case 62:    
//			goDisplay();//前往立体显示   并显示
//			mark = 63;
//			break;
//		case 63:    //去往图形
//			gotuxing();
//			mark = 70;
//			break;
//		case 70://图形图像识别
//			yanchi(5000);
//			quanHandler.sendEmptyMessage(9);      //图形个数识别
//			yanchi(2000);
//			mark = 80;
//			break;
	    case 10:      
	    	mark= 20;
	    	outCarport();//出车库	    	
				break;
		    case 20:  
		    	mark= 25;
		    	
		    	
		    	go(80, 100);	
		    	sendvoice("前往二维码位置   ");
				yanchi(1000);
		    	line(speeds[1]);//前往二维码位置   
		    	checkway(1, 1, 7, 1);
		        break;
		    case 25:  
		    	mark= 30;		    	
		    	goQrcode();//二维码扫描       e4
		        break;
		    case 30:   
		    	mark= 40;
		    	gojingtai();//前往静态    d1
		    	break;
		    case 40:  	
		    	mark= 50;
		    	goShape();//去往车牌    F1	
				break;		
		    case 50:      
		    	mark= 90;
		    	goDisplay(1);//前往立体显示   并显示   E3	    	
				break;
		    case 90:      
		    	mark= 91;
		    	sendvoice("去往图形 ， b1");
				yanchi(1000);
		    	right(80);
		    	line(80);		    	
		    	checkway(1, 2, 11, 2);
				break;				
		    case 91:    
		    	mark= 92;
		    	gotuxing(); //去往图形   b1
		
				break;
		    case 92:    
		    	mark= 95;		    	
		    	yanchi(4000);
				quanHandler.sendEmptyMessage(9); //图形个数识别
				yanchi(4000);
			/////////////////////////
		deputy(1);//从车状态
		yanchi(500);
		vice(1);//从车接受
		yanchi(500);
		deputy(2);//主车状态
		yanchi(500);
		vice(2);//主车接受
		yanchi(500);	
		//////////////////////////////////
				break;
				
		    case 95:    //b4
		    	mark= 100;
		    	sendvoice("前往风扇排风系统位置");
				yanchi(3000);
		    	right(80);
		    	stop();
		    	right(80);
		    	line(80);
		    	checkway(3, 3, 5, 3);
				break;				
		    case 100:     
		    	mark= 101;
		    	goFan();  //前往风扇排风系统位置
		    
				break;
		    case 101:     
		    	mark= 110;
		    	go(80, 100);
		    	right(speeds[3]);
		    	checkway(3, 4, 4, 4);
				break;
				
		    case 110://前往开档位位置
		    	sendvoice("前往开档位位置");
				yanchi(3000);
			/////////////////////////
				deputy(1);//从车状态
				yanchi(500);
				vice(1);//从车接受
				yanchi(500);
				deputy(2);//主车状态
				yanchi(500);
				vice(2);//主车接受
				yanchi(500);	
				//////////////////////////////////
			    goGear();
			    mark=120;
			     break;
		    case 120://运算开启档位方法
			   yanchi(2000);
//			   quanHandler.sendEmptyMessage(11);
//			   yanchi(1000);
//			//运算档位方法
			   gear(1);
			   yanchi(1500);
			   gear(2);
			   yanchi(1500);
			   gear(3);
			   yanchi(1500);
			   
			   mark=130;
			break;
		case 130://开档位
//			getGear(gearNum);
			mark=140;
			break;
		case 140://前往交通灯位置
			sendvoice("前往交通灯位置");
			yanchi(3500);
			goTraffic();
			mark=150;
			break;
		case 150://按照交通灯行走			
			trafficGo();
			mark=-50;
			break;	
	
		default:
			
			break;
		}
	}
	
	private void gojingtai(){//前往静态  d1    
		sendvoice("前往静态  d1");
		yanchi(1000);
		rightchange(80);
		line(30);
		go(80, 100);
		quanHandler.sendEmptyMessage(7);//识别二位码开启
		yanchi(1000);
	}
	//ceju为主车相对于机械车的测距位置                zhu为主车避让路线              jixie为机械臂车路线
	private void checkway(int ceju , int zhu ,int jixie , int huifu){
		if (distance()<550) {
			back(speeds[0], 200);    //后退
			line(30);                 //再循迹
			buzzer(1);
			yanchi(1000);
			buzzer(0);
			yanchi(1000);
			for (int i = 0; i < 5; i++) {
				lamp((byte)0x55);
				yanchi(500);
				lamp((byte)0xff);
				yanchi(500);
			}
		    //主车测距路线
			switch (ceju) {
			case 1:  
				check = (int) ((float)distance()/15*13);    //机械臂和主车路线一致
				sendvoice("距离为："+distance()+"");
				yanchi(1000);
				try {
					new FileService().saveToSDCard(Global.RANGING+".txt", String.valueOf(distance()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				yanchi(1000);
				break;
            case 2:
            	check = (int) ((float)(700-200-distance())/15*13);    //机械臂和主车路线不一致且故障车在长线段
            	sendvoice("距离为："+distance()+"");
            	yanchi(1000);
            	try {
					new FileService().saveToSDCard(Global.RANGING+".txt", String.valueOf(distance()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				yanchi(1000);
            	break;
            case 3:
            	check = (int) ((float)(550-200-distance())/15*13);    //机械臂和主车路线不一致且故障车在短线段
            	sendvoice("距离为："+distance()+"");
            	yanchi(1000);
            	try {
					new FileService().saveToSDCard(Global.RANGING+".txt", String.valueOf(distance()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				yanchi(1000);
            	break;
			default:
				break;
			}
			
			//主车避让路线       去往下个任务点
			
			switch (zhu) {   
			case 1:      //故障车在7
				quanHandler.sendEmptyMessage(4);//调取朝向正右方低一格		
				go(speeds[0],encoders[0]);
				left(speeds[3]);
				quanHandler.sendEmptyMessage(7);//识别二位码开启
				yanchi(1000);
				go(speeds[0],encoders[3]);
				for(int i=0;i<7;i++){
					go(speeds[0], 40);
					stop();
					yanchi(500);
					if(MainActivity.result_qr!=null||i==6){
						break;
					}	
				}
				quanHandler.sendEmptyMessage(5);//调取朝向正前方低一格
				line(speeds[1]);
				break;
			case 2:      //故障车在E4
//                leftchange(speeds[3]);
//                line(speeds[1]);
//                rightchange(speeds[3]);
//                line(speeds[1]);
//				go(speeds[0], encoders[0]);
//				left(speeds[3]);
//				back(speeds[0], 400);
//				line(20);
//				gotoBZW();
//				yanchi(5000);
//				quanHandler.sendEmptyMessage(8);
//				yanchi(5000);
//				back(speeds[0], dis);
				
				quanHandler.sendEmptyMessage(20);  //调取正前方低二格
				go(speeds[0], encoders[0]);	
				left(speeds[3]);
				line(70);
				go(speeds[0], encoders[0]);	
				right(speeds[3]);		
				line(70);	
//				go(speeds[0], encoders[0]);
//				right(speeds[3]);	
//				line(30);
//				go(speeds[0], encoders[0]);	
                break;
            case 3:      //故障车在5
                go(speeds[0], encoders[0]);
    		    left(speeds[3]);
    		    go(speeds[0], encoders[0]);
    			yanchi(1000);
    			sendvoice("通过隧道");
    			yanchi(1000);
    			fan_flag = true;
    			line(50);
            	break;
            case 4:      //故障车在4
            	rightchange(speeds[3]);
            	line(speeds[1]);
            	leftchange(speeds[3]);
            	line(speeds[1]);
            	go(speeds[0], encoders[0]);
                line(speeds[1]);
				stop();
            	break;
			default:
				break;	
			
			}
			
			
			//机械臂车清障路线
			
			switch (jixie) {
			
			case 1:				//e6
				deputy(1);//从车状态
				yanchi(500);
				vice(1);//从车接受
				yanchi(500);
				
				goduty(speeds[0], 90);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				rightduty(80);
				backduty(speeds[0], 90);
				lineduty(speeds[1]);				
				goduty(speeds[0], check);
				
				armcontrol(0);
				yanchi(5000);
				armcontrol(1);
				yanchi(8000);
				armcontrol(2);
				yanchi(8000);
				lineduty(speeds[1]);
				leftduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				leftduty(speeds[3]);
				lineduty(speeds[1]);
				armcontrol(3);
				yanchi(8000);
				armcontrol(0);
				yanchi(5000);
				goduty(speeds[0], 90);
				deputy(2);//主车状态
				yanchi(1000);
				vice(2);//主车接受
				yanchi(1000);		
				
				break;
			case 2:				//c6
				deputy(1);//从车状态
				yanchi(500);
				vice(1);//从车接受
				yanchi(500);
				
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				leftduty(speeds[3]);
				backduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);				
				goduty(speeds[0], check);
				
				armcontrol(0);
				yanchi(5000);
				armcontrol(1);
				yanchi(8000);
				armcontrol(2);
				yanchi(8000);
				
				lineduty(speeds[1]);
				rightduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				rightduty(speeds[3]);
				lineduty(speeds[1]);
				armcontrol(3);
				yanchi(8000);
				armcontrol(0);
				yanchi(5000);
				goduty(speeds[0], encoders[0]);
				
				deputy(2);//主车状态
				yanchi(1000);
				vice(2);//主车接受
				yanchi(1000);		
				
				break;
			case 3:				//f5
				deputy(1);//从车状态
				yanchi(500);
				vice(1);//从车接受
				yanchi(500);
				
				goduty(speeds[0], 90);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				rightduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				leftduty(speeds[3]);
				
				
				backduty(speeds[0], 200);//
				lineduty(speeds[1]);				
				goduty(speeds[0], check);
				
				armcontrol(0);
				yanchi(5000);
				armcontrol(1);
				yanchi(8000);
				armcontrol(2);
				yanchi(8000);
				
				lineduty(speeds[1]);
				rightduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				rightduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				leftduty(speeds[3]);
				lineduty(speeds[1]);
				
				armcontrol(3);
				yanchi(8000);
				armcontrol(0);
				yanchi(5000);
				goduty(speeds[0], 180);//回库
				
				deputy(2);//主车状态
				yanchi(1000);
				vice(2);//主车接受
				yanchi(1000);		
				
				break;
			case 4:				//d5
				deputy(1);//从车状态
				yanchi(500);
				vice(1);//从车接受
				yanchi(500);
				
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);				
				goduty(speeds[0], check);
				
				armcontrol(0);
				yanchi(5000);
				armcontrol(1);
				yanchi(8000);
				armcontrol(2);
				yanchi(8000);
				
				lineduty(speeds[1]);
				goduty(speeds[0], check);
				lineduty(speeds[1]);				
				
				leftduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				armcontrol(3);
				yanchi(8000);
				armcontrol(0);
				yanchi(5000);
				goduty(speeds[0], encoders[0]);
				
				deputy(2);//主车状态
				yanchi(1000);
				vice(2);//主车接受
				yanchi(1000);		
				
				break;
			case 5:				//b5
				deputy(1);//从车状态
				yanchi(500);
				vice(1);//从车接受
				yanchi(500);
				
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				leftduty(speeds[3]);
				
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				rightduty(speeds[3]);
				
				
				
				backduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);				
				goduty(speeds[0], check);
				
				armcontrol(0);
				yanchi(5000);
				armcontrol(1);
				yanchi(8000);
				armcontrol(2);
				yanchi(8000);
				
				lineduty(speeds[1]);
				
				
				
				leftduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				leftduty(speeds[3]);
				lineduty(speeds[1]);
				
				goduty(speeds[0], encoders[0]);
				rightduty(speeds[3]);
				lineduty(speeds[1]);
				
				armcontrol(3);
				yanchi(8000);
				armcontrol(0);
				yanchi(5000);
				goduty(speeds[0], 220);//???
				
				deputy(2);//主车状态
				yanchi(1000);
				vice(2);//主车接受
				yanchi(1000);		
				
				break;
			case 6:				//e4
				deputy(1);//从车状态
				yanchi(500);
				vice(1);//从车接受
				yanchi(500);
				
				goduty(speeds[0], 90);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				rightduty(speeds[3]);
				backduty(speeds[0], 90);
				lineduty(speeds[1]);				
				goduty(speeds[0], check);///改check
				
				armcontrol(0);
				yanchi(5000);
				armcontrol(1);
				yanchi(8000);
				armcontrol(2);
				yanchi(8000);
				
				lineduty(speeds[1]);
				leftduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				leftduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				lineduty(speeds[1]);
				armcontrol(3);
				yanchi(8000);
				armcontrol(0);
				yanchi(5000);
				goduty(speeds[0], 90);
				
				deputy(2);//主车状态
				yanchi(1000);
				vice(2);//主车接受
				yanchi(1000);			
				
				break;
			case 7:				//f3
				deputy(1);//从车状态
				yanchi(500);
				vice(1);//从车接受
				yanchi(500);
				
				goduty(speeds[0], 90);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				rightduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				leftduty(speeds[3]);
				lineduty(speeds[1]);				
				goduty(speeds[0], check);
				
				armcontrol(0);
				yanchi(5000);
				armcontrol(1);
				yanchi(8000);
				armcontrol(2);
				yanchi(8000);
				
//				lineduty(speeds[1]);
				rightduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				rightduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				leftduty(speeds[3]);
				lineduty(speeds[1]);
				
				armcontrol(3);
				yanchi(8000);
				armcontrol(0);
				yanchi(5000);
				goduty(speeds[0], 90);
				
				
				deputy(2);//主车状态
				yanchi(1000);
				vice(2);//主车接受
				yanchi(1000);			
				
				break;
			case 8:	//d3
				deputy(1);//从车状态
				yanchi(500);
				vice(1);//从车接受
				yanchi(500);
				
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], check);
				
				armcontrol(0);
				yanchi(5000);
				armcontrol(1);
				yanchi(8000);
				armcontrol(2);
				yanchi(8000);
				
				lineduty(speeds[1]);
				rightduty(speeds[3]);
				lineduty(speeds[1]);				
				
				goduty(speeds[0], encoders[0]);				
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				armcontrol(3);
				yanchi(8000);
				armcontrol(0);
				yanchi(5000);
				goduty(speeds[0], encoders[0]);
				
				deputy(2);//主车状态
				yanchi(1000);
				vice(2);//主车接受
				yanchi(1000);	
				
				break;
			case 9:	//b3
				deputy(1);//从车状态
				yanchi(500);
				vice(1);//从车接受
				yanchi(500);
				
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				leftduty(speeds[3]);
				
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				rightduty(speeds[3]);
				lineduty(speeds[1]);				
				goduty(speeds[0], check);
				
				armcontrol(0);
				yanchi(5000);
				armcontrol(1);
				yanchi(8000);
				armcontrol(2);
				yanchi(8000);
				
				lineduty(speeds[1]);	
				rightduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				leftduty(speeds[3]);
				lineduty(speeds[1]);
				
				goduty(speeds[0], encoders[0]);
				rightduty(speeds[3]);
				lineduty(speeds[1]);
				
				armcontrol(3);
				yanchi(8000);
				armcontrol(0);
				yanchi(5000);
				goduty(speeds[0], 220);
				
				deputy(2);//主车状态
				yanchi(1000);
				vice(2);//主车接受
				yanchi(1000);		
				
				
				
				break;
			case 10:				//e2
				deputy(1);//从车状态
				yanchi(500);
				vice(1);//从车接受
				yanchi(500);
				
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				rightduty(speeds[3]);
				backduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], check);
				
				
				armcontrol(0);
				yanchi(5000);
				armcontrol(1);
				yanchi(8000);
				armcontrol(2);
				yanchi(8000);
				
				
				lineduty(speeds[1]);				
				
				leftduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				leftduty(speeds[3]);
				
				
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				
				armcontrol(3);
				yanchi(8000);
				armcontrol(0);
				yanchi(5000);
				goduty(speeds[0], encoders[0]);
				
				deputy(2);//主车状态
				yanchi(1000);
				vice(2);//主车接受
				yanchi(1000);			
				
				break;
			case 11:		   ///c2
				deputy(1);//从车状态
				yanchi(500);
				vice(1);//从车接受
				yanchi(500);
				
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				leftduty(speeds[3]);
				backduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], check);
				
				
				armcontrol(0);
				yanchi(5000);
				armcontrol(1);
				yanchi(8000);
				armcontrol(2);
				yanchi(8000);
				
				
				lineduty(speeds[1]);				
				
				leftduty(speeds[3]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				rightduty(speeds[3]);
				
				
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				goduty(speeds[0], encoders[0]);
				lineduty(speeds[1]);
				
				armcontrol(3);
				yanchi(8000);
				armcontrol(0);
				yanchi(5000);
				goduty(speeds[0], encoders[0]);
				
				deputy(2);//主车状态
				yanchi(1000);
				vice(2);//主车接受
				yanchi(1000);				
				
				break;
			
			default:
				break;
			}
			switch (huifu ) {
			case 1:
				mark = 30;
				break;
			case 2:
				go(speeds[0], encoders[0]);
				right(speeds[3]);	
				line(30);
				go(speeds[0], encoders[0]);	
				mark = 92;
				break;
			case 3:
				mark = 101;
				break;
			case 4:
				mark = 120;
				break;	
				
			default:
				break;
			}
		}
	
	}
	
	private void goShape(){  //去往车牌位置
		sendvoice("去往车牌位置");
		yanchi(1000);
		quanHandler.sendEmptyMessage(20);  //调取正前方低二格		
		left(80);
		stop();
		left(80);
		linechange(80, 1);
		leftchange(80);
		linechange(80, 1);
		leftchange(80);
		line(30);
		gotoBZW();		
		yanchi(3000);
		quanHandler.sendEmptyMessage(8);//车牌识别
		yanchi(6000);
/////////////////////////
deputy(1);//从车状态
yanchi(500);
vice(1);//从车接受
yanchi(500);
deputy(2);//主车状态
yanchi(500);
vice(2);//主车接受
yanchi(500);	
//////////////////////////////////
	}

//	private void figure_recognition(){
//		yanchi(500);
//		picture(2);
//		yanchi(2500);
//		quanHandler.sendEmptyMessage(8);    //车牌识别
//		yanchi(2000);
//		if (lice_flag) {             //第一次翻页
//			lice_flag = false;
//			picture(2);   //图片下翻
//			yanchi(2000);
//			quanHandler.sendEmptyMessage(8);    //车牌识别	
//			lice_flag = false;
//			yanchi(2000);
//		}
//		picture(2);//下翻图片
//		yanchi(3000);
//		quanHandler.sendEmptyMessage(9);      //图形个数识别
//		yanchi(1000);
//	}
	
	private void goDisplay(int fangxiang){//方向立体显示
		//前进右转到5行道
		if (fangxiang ==1) {
			sendvoice("去往立体显示");
			yanchi(1000);
			
//			back(80, dis);
//			go(80, 100);
			left(speeds[3]);
			//左转45发送红外
			MAJOR = 0x04;
			FIRST = (byte) (speeds[3] & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			yanchi(500);
			stop();
			yanchi(1000);
			quanHandler.sendEmptyMessage(15);//立体显示
			yanchi(3000);
		}
		if (fangxiang ==2) {
			
			//右转45发送红外
			MAJOR = 0x04;
			FIRST = (byte) (speeds[3] & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			yanchi(500);
			stop();
			yanchi(1000);
			quanHandler.sendEmptyMessage(15);//立体显示
			yanchi(3000);
		}
	}	

	private void goGear() {    //去往档位灯位置   
		// TODO Auto-generated method stub
		
		linechange(speeds[1], 1);
		rightchange(80);
		linechange(speeds[1], 1);
		leftchange(80);
		line(80);
		
	    }


	private void goFan(){
//		    left(speeds[3]);
//		    line(speeds[1]);
		    go(speeds[0], encoders[0]);
		    left(speeds[3]);
//		    go(speeds[0], encoders[0]);
			yanchi(1000);
			sendvoice("通过隧道");
			yanchi(1000);
//			fan_flag = true;
			fan();
			yanchi(3000);
			line(50);
		}
	private void goTraffic(){//去往交通灯

		back(speeds[0], 220);
		left(speeds[3]);
		line(speeds[1]);
		
		go(speeds[0], encoders[0]);
		left(speeds[3]);
		line(speeds[1]);
		
		go(speeds[0], encoders[0]);
		right(speeds[3]);
		line(30);
		yanchi(2000);	
	}
	private void goalarm() {
		// TODO Auto-generated method stub
		//写去往警报的路线
		go(speeds[0], encoders[0]);
		right(speeds[3]);
		line(speeds[1]);
	}


	private void goDisplay(){
		//前进右转到5行道

		left(speeds[3]);
		//左转发送红外
		MAJOR = 0x04;
		FIRST = (byte) (speeds[3] & 0xFF);
		SECOND = 0x00;
		THRID = 0x00;
		send();
		yanchi(500);
		stop();
		yanchi(1000);
		quanHandler.sendEmptyMessage(15);//立体显示
		yanchi(3000);
	}	
	private void duty(int i){
		if (i == 1) {			
			deputy(1);//从车状态
			yanchi(500);
			vice(1);//从车接受
			yanchi(500);
			go(speeds[4], encoders[5]);
			line(speeds[1]);
			go(speeds[4], encoders[5]);
			light(0, 1);
			right(speeds[3]);
			light(0, 0);
			line(speeds[1]);
			go(speeds[4], encoders[5]);
			light(0, 1);
			right(speeds[3]);
			light(0, 0);
			line(speeds[1]);
			go(speeds[4], encoders[6]);
			deputy(2);//主车状态
			yanchi(1000);
			vice(2);//主车接受
			yanchi(1000);		
		} else if(i == 2){
			deputy(1);//从车状态
			yanchi(500);
			vice(1);//从车接受
			yanchi(500);
			go(speeds[4], encoders[5]);
			line(speeds[1]);
			go(speeds[4], encoders[5]);
			light(0, 1);
			right(speeds[3]);
			light(0, 0);
			line(speeds[1]);
			go(speeds[4], encoders[5]);
			light(0, 1);
			right(speeds[3]);
			light(0, 0);
			line(speeds[1]);
			go(speeds[0], encoders[6]);
			deputy(2);//主车状态
			yanchi(1000);
			vice(2);//主车接受
			yanchi(1000);				
		}else if(i == 3){
			deputy(1);//从车状态
			yanchi(500);
			vice(1);//从车接受
			yanchi(500);
			go(speeds[4], encoders[5]);
			line(speeds[1]);
			go(speeds[4], encoders[5]);
			light(0, 1);
			right(speeds[3]);
			light(0, 0);
			line(speeds[1]);
			go(speeds[4], encoders[5]);
			light(0, 1);
			right(speeds[3]);
			light(0, 0);
			line(speeds[1]);
			go(speeds[0], encoders[6]);
			deputy(2);//主车状态
			yanchi(1000);
			vice(2);//主车接受
			yanchi(1000);				
		}
	}

	private void outCarport(){
		gate(1);//开道闸
		yanchi(2000);
		digital_open();   //开数码管计时
		yanchi(1000);
		//出车库动作
		go(speeds[0], encoders[0]);//出车库
		line(speeds[1]);//循迹到第一个十字路口
		sendvoice("交通巡逻车正在巡逻请避让");
		yanchi(1000);
		mark=20;
		}
	private void goRanging(){//前往测距位置
		//写前往测距的位置
		go(speeds[0], encoders[0]);
		right(speeds[3]);
		line(speeds[1]);
	}
	
	private void goQrcode(){
		quanHandler.sendEmptyMessage(6);//调取朝向正右方低一格		
		go(speeds[0],encoders[0]);
		left(speeds[3]);
		quanHandler.sendEmptyMessage(7);//识别二位码开启
		yanchi(1000);
		go(speeds[0],encoders[3]);
		for(int i=0;i<7;i++){
			go(speeds[0], 40);
			stop();
			yanchi(500);
			if(MainActivity.result_qr!=null||i==6){
				break;
			}	
		}
		quanHandler.sendEmptyMessage(5);//调取朝向正前方低一格
		line(speeds[1]);
	}
			

	private void goShape1(){  //去往车牌位置
		quanHandler.sendEmptyMessage(20);  //调取正前方低二格
		go(speeds[0], encoders[0]);		
		right(speeds[3]);
		line(speeds[1]);
		go(speeds[0], encoders[0]);	
		right(speeds[3]);
		checkway(1, 2, 6, 1);
	}
	private void goShape2(){	
		line(speeds[1]);
		go(speeds[0], encoders[0]);
		left(speeds[3]);
		checkway(1, 3, 7, 1);		
		go(speeds[0], encoders[0]);
		line(30);
		go(speeds[0], encoders[0]);			
		yanchi(5000);
		quanHandler.sendEmptyMessage(8);
		yanchi(5000);
	}
	private void gotuxing(){ //去往图形位置
		quanHandler.sendEmptyMessage(20);  //调取正前方低二格
		go(speeds[0], encoders[0]);	
		left(speeds[3]);
		line(70);
		go(speeds[0], encoders[0]);	
		right(speeds[3]);		
		line(70);	
		go(speeds[0], encoders[0]);
		right(speeds[3]);	
		line(30);
		go(speeds[0], encoders[0]);	
	}
	
	
	
	public int state = 0;
	private void trafficGo(){
		yanchi(5000);
		quanHandler.sendEmptyMessage(10);//交灯识别
		yanchi(3000);
		if(state==2||state==3){//向右转
			go(speeds[0], encoders[0]);//
			right(speeds[3]);
			line(speeds[1]);
			go(speeds[0], encoders[0]);//到J7
			
			line(speeds[1]);
			gate(1);//开道闸
			yanchi(2000);
			
			go(speeds[0], 220);
			
			infrared((byte) 0x03, (byte) 0x05,
					(byte) 0x14, (byte) 0x45, (byte) 0xDE,
					(byte) 0x92);
			yanchi(200);
			
			digital_close();   //关数码管计时
			yanchi(1000);
			sendvoice("本次任务完成");
			yanchi(3000);
		}
		else if(state==1||state==4){//向左转
			go(speeds[0], encoders[0]);
			left(speeds[3]);
			line(speeds[1]);
			
			
			left(speeds[3]);
			line(speeds[1]);
			go(speeds[0], encoders[0]);//到J3
			
			line(speeds[1]);
			go(speeds[0], encoders[0]);
			
			line(speeds[1]);
			
			gate(1);//开道闸
			yanchi(2000);
			
			go(speeds[0], 220);
			
			infrared((byte) 0x03, (byte) 0x05,
					(byte) 0x14, (byte) 0x45, (byte) 0xDE,
					(byte) 0x92);
			yanchi(200);
			
			digital_close();   //关数码管计时
			yanchi(1000);
			sendvoice("本次任务完成");
			yanchi(3000);
		}
		else if(state==5){// 拐弯
			right(speeds[3]);
			
			
			line(speeds[1]);
			go(speeds[0], encoders[0]);
			
			left(speeds[3]);
			
			line(speeds[1]);
			go(speeds[0], encoders[0]);
			
			left(speeds[3]);
			
			line(speeds[1]);
			go(speeds[0], encoders[0]);
			right(speeds[3]);
			
			line(speeds[1]);
			
			gate(1);//开道闸
			yanchi(2000);
			
			go(speeds[0], 220);
			
			infrared((byte) 0x03, (byte) 0x05,
					(byte) 0x14, (byte) 0x45, (byte) 0xDE,
					(byte) 0x92);
			yanchi(200);
			
			digital_close();   //关数码管计时
			yanchi(1000);
			sendvoice("本次任务完成");
			yanchi(1000);
		}
		else{//识别识别
//			back(speeds[0], 70);
			go(speeds[0], encoders[0]);//
			right(speeds[3]);
			line(speeds[1]);
			go(speeds[0], encoders[0]);//到J7
			
			line(speeds[1]);
			gate(1);//开道闸
			yanchi(2000);
			
			go(speeds[0], 220);
			
			infrared((byte) 0x03, (byte) 0x05,
					(byte) 0x14, (byte) 0x45, (byte) 0xDE,
					(byte) 0x92);
			yanchi(200);
			
			digital_close();   //关数码管计时
			yanchi(1000);
			sendvoice("本次任务完成");
			yanchi(3000);
		}
		
	}
	private void goEnd(){
		digital_close();
		yanchi(500);
		buzzer(1);
		yanchi(3000);
		buzzer(0);
		mark=-50;
	}
	
	private void goDeputyB1(){
	//给从车权限
	deputy(1);//从车状态
	yanchi(500);
	vice(1);//从车接受
	yanchi(500);
	//写控制从车控制
	line(speeds[1]);
	go(speeds[0], encoders[0]);
//	arm(0x70, 0x02, 0x01);
	line(speeds[1]);
	go(speeds[0], encoders[2]);
	//主车拿回权限
	deputy(2);//主车状态
	yanchi(1000);
	vice(2);//主车接受
	yanchi(1000);
}
	
	
	private void goDeputy(int MO){
		deputy(1);//从车状态
		yanchi(500);
		vice(1);
		yanchi(500);
		stop();
		if(MO%2==0){//B7-J7
			back(speeds[0],encoders[4]);
			if(MO!=2){
				left(speeds[3]);
				for(int i=0;i<(MO/2-1);i++){
					line(speeds[1]);
					go(speeds[0], encoders[0]);
				}
			}
		}
		else{//B3-J3
			back(speeds[0],encoders[4]);
			right(speeds[3]);
			stop();
			right(speeds[3]);
			for(int i=0;i<2;i++){
				line(speeds[1]);
				go(speeds[0], encoders[0]);
			}
			if(MO!=1){
				left(speeds[3]);
				for(int i=0;i<MO/2;i++){
					line(speeds[1]);
					go(speeds[0], encoders[0]);
				}
			}
		}
		deputy(2);//主车状态
		yanchi(500);
		vice(2);
		yanchi(500);
	}

	public Thread quanThread=new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			quan();
		}
	});
	private Timer timer1;
	private int deputy_index;
	private void deputyTimer(final int what){
		timer1=new Timer();
		timer1.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				switch (what) {
				case 1://前进
					
					break;
				case 2://后退
					
					break;
				case 3://左转
					
					break;
				case 4://right
					
					break;
				case 5://line
					
					break;
				default:
					break;
				}
			}
		}, 0, 50);
	}
}
