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
	private Handler quanHandler;//ȫ�Զ�����
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
	long  UltraSonic1,UltraSonic2,UltraSonic3,UltraSonic4,UltraSonic5;// ������
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
	
	
	// ���ͺ���
	public void send() {
		try {
			CHECKSUM=(short) ((MAJOR+FIRST+SECOND+THRID)%256);
			// ���������ֽ�����
			byte[] sbyte = { 0x55, (byte) TYPE, (byte) MAJOR, (byte) FIRST, (byte) SECOND, (byte) THRID ,(byte) CHECKSUM,(byte) 0xBB};
			if (socket != null && !socket.isClosed()) {
				bOutputStream.write(sbyte, 0, sbyte.length);
				bOutputStream.flush();
			}
			else{
				Toast.makeText(context, "wifi�����쳣,����WiFi�����Ƿ���ȷ��", Toast.LENGTH_LONG).show();
				quanHandler.sendEmptyMessage(-5);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public void send_voice(byte [] textbyte) {
		try {
			// ���������ֽ�����
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
		textbyte[3] = 0x01;// �ϳ���������
		textbyte[4] = (byte) 0x01;// �����ʽ
		for (int i = 0; i < sbyte.length; i++) {
			textbyte[i + 5] = sbyte[i];
		}
		return textbyte;
	}

	
	
    private void sendvoice(String src){      //��������
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
	
	
	    // ����ǰ��
		public void go(int sp_n, int en_n) {
			MAJOR = 0x02;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = (byte) (en_n & 0xff);
			THRID = (byte) (en_n >> 8);
			send();
			if(mark>0)
			while(rbyte[2]!=3);
		}
		//��е�۳�ǰ��
		public void goduty(int sp_n, int en_n) {
			MAJOR = 0x02;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = (byte) (en_n & 0xff);
			THRID = (byte) (en_n >> 8);
			send();
		    yanchi(2000);		
		}	
		// ��������
		public void back(int sp_n, int en_n) {
			MAJOR = 0x03;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = (byte) (en_n & 0xff);
			THRID = (byte) (en_n >> 8);
			send();
			if(mark>0)
			while(rbyte[2]!=3);
		}
		//��е�۳�����
		public void backduty(int sp_n, int en_n) {
			MAJOR = 0x03;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = (byte) (en_n & 0xff);
			THRID = (byte) (en_n >> 8);
			send();
			yanchi(2000);
		}

	// ������ת
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

	// ��е�۳���ת
	   public void leftduty(int sp_n) {
		  MAJOR = 0x04;
		  FIRST = (byte) (sp_n & 0xFF);
		  SECOND = 0x00;
		  THRID = 0x00;
		  send();
		  yanchi(2000);
	  }
		//�޸�������ת	
        public void leftchange(int sp_n){
        	MAJOR = 0x08;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			if(mark>0)
			while(rbyte[2]!=2);
        }   
           
        //�޸�������ת
        public void rightchange(int sp_n) {
			MAJOR = 0x09;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			if(mark>0)
			while(rbyte[2]!=2);
		}
               
		// ������ת
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
		// ��е�۳���ת
		public void rightduty(int sp_n) {
		    MAJOR = 0x05;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			yanchi(2000);
		}

		// ����ͣ��
		public void stop() {
			MAJOR = 0x01;
			FIRST = 0x00;
			SECOND = 0x00;
			THRID = 0x00;
			send();
			if(mark>0)
			while(rbyte[2]!=0);
		}
		// ��е�۳�ͣ��
		public void stopduty() {
			MAJOR = 0x01;
			FIRST = 0x00;
			SECOND = 0x00;
			THRID = 0x00;
			send();
			yanchi(1000);
		}
		// ����ѭ��
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
		
		//��е�۳�ѭ��
		public void lineduty(int sp_n) {
			MAJOR = 0x06;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			yanchi(4000);		
		}
		
		
		
		//�޸�����ѭ��
		public void linechange(int sp_n ,int line) {
			MAJOR = 0x15;
			FIRST = (byte) (sp_n & 0xFF);
			SECOND = (byte) (line & 0xFF);
			THRID = 0x00;
			send();
			if(mark>0)
			while (rbyte[2] != 1 && rbyte[2] != 4);						
		}
		
		public void gotoBZW(){        //��־��ʶ��������
		    dis = (int)((float)(distance()-220)/15*13);	
			go(80, dis);
		}
		
		public void deputy(int i){//�ӳ���е��
			if(i==1)//�ӳ�
			TYPE=0x05;
			else if(i==2)//����
				TYPE=(byte) 0xAA;
		}
		public void vice(int i){//���ӳ�״̬ת��
			byte type=TYPE;
			if(i==1){//�ӳ�״̬
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
			else if(i==2){//����״̬
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
	//��е��
	public void arm(int one,int two,int thrid){
		MAJOR=(short) one;
		FIRST=(short) two;
		SECOND = (short) thrid;
		THRID = 0x00;
		send();
	}
	public void armcontrol(int data){    //��е������״̬����
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
	
	// ����
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
	// ����
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
	// ˫ɫLED��
	public void lamp(byte command) {
		MAJOR = 0x40;
		FIRST = command;
		SECOND = 0x00;
		THRID = 0x00;
		send();
	}

	// ָʾ��
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

	// ������
	public void buzzer(int i) {
		if (i == 1)             //��������
			FIRST = 0x01;
		else if (i == 0)        //�ط�����
			FIRST = 0x00;
		MAJOR = 0x30;
		SECOND = 0x00;
		THRID = 0x00;
		send();
	}

	public void picture(int i) {// ͼƬ�Ϸ����·�
		if (i == 1)
			MAJOR = 0x50;
		else
			MAJOR = 0x51;
		FIRST = 0x00;
		SECOND = 0x00;
		THRID = 0x00;
		send();
	}

	public void gear(int i) {// ���յ�λ��
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

	public void fan() {// ����
		infrared((byte) 0x67, (byte) 0x34,
				(byte) 0x78, (byte) 0xA2, (byte) 0xFD,
				(byte) 0x27);
//		infrared();
	}

	public void gate(int i) {// բ��
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
	//LCD��ʾ��־������ʱģʽ
	public void digital_close(){//����ܹر�
		byte type=TYPE;
		TYPE = 0x04;
		MAJOR = 0x03;
		FIRST = 0x00;
		SECOND = 0x00;
		THRID = 0x00;
		send();
		TYPE = type;
	}
	public void digital_open(){//����ܴ�
		byte type=TYPE;
		TYPE = 0x04;
		MAJOR = 0x03;
		FIRST = 0x01;
		SECOND = 0x00;
		THRID = 0x00;
		send();
		TYPE = type;
	}
	public void digital_clear(){//���������
		byte type=TYPE;
		TYPE = 0x04;
		MAJOR = 0x03;
		FIRST = 0x02;
		SECOND = 0x00;
		THRID = 0x00;
		send();
		TYPE = type;
	}
	public void digital_dic(long ultraSonic){//LCD��ʾ��־��ڶ�����ʾ����
		byte type=TYPE;
		TYPE = 0x04;
		MAJOR = 0x04;
		FIRST = 0x00;
		SECOND = (short) (ultraSonic/100);
		THRID = (short) (ultraSonic%100);
		send();
		TYPE = type;
	}
	public void digital(int i, int one, int two, int three) {// �����
		byte type=TYPE;
		TYPE = 0x04;
		if (i == 1) {//����д���һ�������
			MAJOR = 0x01;
			FIRST = (byte) one;
			SECOND = (byte) two;
			THRID = (byte) three;
		} else if (i == 2) {//����д��ڶ��������
			MAJOR = 0x02;
			FIRST = (byte) one;
			SECOND = (byte) two;
			THRID = (byte) three;
		}
		send();
		TYPE = type;
	}
	public void infrared_stereo(short [] data){//������ʾ
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
	private void getGear(int i){    //��ȡ���յ�λ
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
	private long light(){     //����ǿ��
		long Light = 0;
		Light = rbyte[7] & 0xff;
		Light = Light << 8;
		Light += rbyte[6] & 0xff;
		return Light;
	}
	private long distance(){		  //���������
		
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
	// ��˯
	public void yanchi(int time) {       //�ӳٺ���
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
	private int[] speeds={80,80,10,80,80};//ǰ����ѭ��������ת
	private int[] encoders={100,120,100,80,220,50,150};//ǰ���������⡢�����⡢�����⡢������
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
//		case 10://������
//			mark= 20;
//			outCarport();
//			checkway(1, 1, 3,1);	//��� �㷨       ������һ�����	     �ӳ�ִ��          mark��ת
//			break;
//		case 20://ǰ����ά��λ��ɨ��
//			goQrcode();
//			mark=60;
//			break;
////		case 30://ǰ�����������λ��
////			goRanging();
////			mark=40;
////			break;
////		case 40://���������
////			yanchi(1000);
////			quanHandler.sendEmptyMessage(17);
////			mark=50;
////			break;			
////		case 50:      //ǰ�������ŷ�ϵͳλ��
////			goFan();  
////			mark = -50;
////			break;
////		case 50:
////			digital_dic(distance());
////			yanchi(1000);
////			mark = 60;
////			break;
//		case 60:    //ȥ������1
//			mark = 61;
//			goShape1();
////			mark = -50;
//			break;
//		case 61:         //ȥ����2
//			mark = 62;
//			goShape2();
//			break;
//		case 62:    
//			goDisplay();//ǰ��������ʾ   ����ʾ
//			mark = 63;
//			break;
//		case 63:    //ȥ��ͼ��
//			gotuxing();
//			mark = 70;
//			break;
//		case 70://ͼ��ͼ��ʶ��
//			yanchi(5000);
//			quanHandler.sendEmptyMessage(9);      //ͼ�θ���ʶ��
//			yanchi(2000);
//			mark = 80;
//			break;
	    case 10:      
	    	mark= 20;
	    	outCarport();//������	    	
				break;
		    case 20:  
		    	mark= 25;
		    	
		    	
		    	go(80, 100);	
		    	sendvoice("ǰ����ά��λ��   ");
				yanchi(1000);
		    	line(speeds[1]);//ǰ����ά��λ��   
		    	checkway(1, 1, 7, 1);
		        break;
		    case 25:  
		    	mark= 30;		    	
		    	goQrcode();//��ά��ɨ��       e4
		        break;
		    case 30:   
		    	mark= 40;
		    	gojingtai();//ǰ����̬    d1
		    	break;
		    case 40:  	
		    	mark= 50;
		    	goShape();//ȥ������    F1	
				break;		
		    case 50:      
		    	mark= 90;
		    	goDisplay(1);//ǰ��������ʾ   ����ʾ   E3	    	
				break;
		    case 90:      
		    	mark= 91;
		    	sendvoice("ȥ��ͼ�� �� b1");
				yanchi(1000);
		    	right(80);
		    	line(80);		    	
		    	checkway(1, 2, 11, 2);
				break;				
		    case 91:    
		    	mark= 92;
		    	gotuxing(); //ȥ��ͼ��   b1
		
				break;
		    case 92:    
		    	mark= 95;		    	
		    	yanchi(4000);
				quanHandler.sendEmptyMessage(9); //ͼ�θ���ʶ��
				yanchi(4000);
			/////////////////////////
		deputy(1);//�ӳ�״̬
		yanchi(500);
		vice(1);//�ӳ�����
		yanchi(500);
		deputy(2);//����״̬
		yanchi(500);
		vice(2);//��������
		yanchi(500);	
		//////////////////////////////////
				break;
				
		    case 95:    //b4
		    	mark= 100;
		    	sendvoice("ǰ�������ŷ�ϵͳλ��");
				yanchi(3000);
		    	right(80);
		    	stop();
		    	right(80);
		    	line(80);
		    	checkway(3, 3, 5, 3);
				break;				
		    case 100:     
		    	mark= 101;
		    	goFan();  //ǰ�������ŷ�ϵͳλ��
		    
				break;
		    case 101:     
		    	mark= 110;
		    	go(80, 100);
		    	right(speeds[3]);
		    	checkway(3, 4, 4, 4);
				break;
				
		    case 110://ǰ������λλ��
		    	sendvoice("ǰ������λλ��");
				yanchi(3000);
			/////////////////////////
				deputy(1);//�ӳ�״̬
				yanchi(500);
				vice(1);//�ӳ�����
				yanchi(500);
				deputy(2);//����״̬
				yanchi(500);
				vice(2);//��������
				yanchi(500);	
				//////////////////////////////////
			    goGear();
			    mark=120;
			     break;
		    case 120://���㿪����λ����
			   yanchi(2000);
//			   quanHandler.sendEmptyMessage(11);
//			   yanchi(1000);
//			//���㵵λ����
			   gear(1);
			   yanchi(1500);
			   gear(2);
			   yanchi(1500);
			   gear(3);
			   yanchi(1500);
			   
			   mark=130;
			break;
		case 130://����λ
//			getGear(gearNum);
			mark=140;
			break;
		case 140://ǰ����ͨ��λ��
			sendvoice("ǰ����ͨ��λ��");
			yanchi(3500);
			goTraffic();
			mark=150;
			break;
		case 150://���ս�ͨ������			
			trafficGo();
			mark=-50;
			break;	
	
		default:
			
			break;
		}
	}
	
	private void gojingtai(){//ǰ����̬  d1    
		sendvoice("ǰ����̬  d1");
		yanchi(1000);
		rightchange(80);
		line(30);
		go(80, 100);
		quanHandler.sendEmptyMessage(7);//ʶ���λ�뿪��
		yanchi(1000);
	}
	//cejuΪ��������ڻ�е���Ĳ��λ��                zhuΪ��������·��              jixieΪ��е�۳�·��
	private void checkway(int ceju , int zhu ,int jixie , int huifu){
		if (distance()<550) {
			back(speeds[0], 200);    //����
			line(30);                 //��ѭ��
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
		    //�������·��
			switch (ceju) {
			case 1:  
				check = (int) ((float)distance()/15*13);    //��е�ۺ�����·��һ��
				sendvoice("����Ϊ��"+distance()+"");
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
            	check = (int) ((float)(700-200-distance())/15*13);    //��е�ۺ�����·�߲�һ���ҹ��ϳ��ڳ��߶�
            	sendvoice("����Ϊ��"+distance()+"");
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
            	check = (int) ((float)(550-200-distance())/15*13);    //��е�ۺ�����·�߲�һ���ҹ��ϳ��ڶ��߶�
            	sendvoice("����Ϊ��"+distance()+"");
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
			
			//��������·��       ȥ���¸������
			
			switch (zhu) {   
			case 1:      //���ϳ���7
				quanHandler.sendEmptyMessage(4);//��ȡ�������ҷ���һ��		
				go(speeds[0],encoders[0]);
				left(speeds[3]);
				quanHandler.sendEmptyMessage(7);//ʶ���λ�뿪��
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
				quanHandler.sendEmptyMessage(5);//��ȡ������ǰ����һ��
				line(speeds[1]);
				break;
			case 2:      //���ϳ���E4
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
				
				quanHandler.sendEmptyMessage(20);  //��ȡ��ǰ���Ͷ���
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
            case 3:      //���ϳ���5
                go(speeds[0], encoders[0]);
    		    left(speeds[3]);
    		    go(speeds[0], encoders[0]);
    			yanchi(1000);
    			sendvoice("ͨ�����");
    			yanchi(1000);
    			fan_flag = true;
    			line(50);
            	break;
            case 4:      //���ϳ���4
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
			
			
			//��е�۳�����·��
			
			switch (jixie) {
			
			case 1:				//e6
				deputy(1);//�ӳ�״̬
				yanchi(500);
				vice(1);//�ӳ�����
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
				deputy(2);//����״̬
				yanchi(1000);
				vice(2);//��������
				yanchi(1000);		
				
				break;
			case 2:				//c6
				deputy(1);//�ӳ�״̬
				yanchi(500);
				vice(1);//�ӳ�����
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
				
				deputy(2);//����״̬
				yanchi(1000);
				vice(2);//��������
				yanchi(1000);		
				
				break;
			case 3:				//f5
				deputy(1);//�ӳ�״̬
				yanchi(500);
				vice(1);//�ӳ�����
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
				goduty(speeds[0], 180);//�ؿ�
				
				deputy(2);//����״̬
				yanchi(1000);
				vice(2);//��������
				yanchi(1000);		
				
				break;
			case 4:				//d5
				deputy(1);//�ӳ�״̬
				yanchi(500);
				vice(1);//�ӳ�����
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
				
				deputy(2);//����״̬
				yanchi(1000);
				vice(2);//��������
				yanchi(1000);		
				
				break;
			case 5:				//b5
				deputy(1);//�ӳ�״̬
				yanchi(500);
				vice(1);//�ӳ�����
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
				
				deputy(2);//����״̬
				yanchi(1000);
				vice(2);//��������
				yanchi(1000);		
				
				break;
			case 6:				//e4
				deputy(1);//�ӳ�״̬
				yanchi(500);
				vice(1);//�ӳ�����
				yanchi(500);
				
				goduty(speeds[0], 90);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				lineduty(speeds[1]);
				goduty(speeds[0], 90);
				rightduty(speeds[3]);
				backduty(speeds[0], 90);
				lineduty(speeds[1]);				
				goduty(speeds[0], check);///��check
				
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
				
				deputy(2);//����״̬
				yanchi(1000);
				vice(2);//��������
				yanchi(1000);			
				
				break;
			case 7:				//f3
				deputy(1);//�ӳ�״̬
				yanchi(500);
				vice(1);//�ӳ�����
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
				
				
				deputy(2);//����״̬
				yanchi(1000);
				vice(2);//��������
				yanchi(1000);			
				
				break;
			case 8:	//d3
				deputy(1);//�ӳ�״̬
				yanchi(500);
				vice(1);//�ӳ�����
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
				
				deputy(2);//����״̬
				yanchi(1000);
				vice(2);//��������
				yanchi(1000);	
				
				break;
			case 9:	//b3
				deputy(1);//�ӳ�״̬
				yanchi(500);
				vice(1);//�ӳ�����
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
				
				deputy(2);//����״̬
				yanchi(1000);
				vice(2);//��������
				yanchi(1000);		
				
				
				
				break;
			case 10:				//e2
				deputy(1);//�ӳ�״̬
				yanchi(500);
				vice(1);//�ӳ�����
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
				
				deputy(2);//����״̬
				yanchi(1000);
				vice(2);//��������
				yanchi(1000);			
				
				break;
			case 11:		   ///c2
				deputy(1);//�ӳ�״̬
				yanchi(500);
				vice(1);//�ӳ�����
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
				
				deputy(2);//����״̬
				yanchi(1000);
				vice(2);//��������
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
	
	private void goShape(){  //ȥ������λ��
		sendvoice("ȥ������λ��");
		yanchi(1000);
		quanHandler.sendEmptyMessage(20);  //��ȡ��ǰ���Ͷ���		
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
		quanHandler.sendEmptyMessage(8);//����ʶ��
		yanchi(6000);
/////////////////////////
deputy(1);//�ӳ�״̬
yanchi(500);
vice(1);//�ӳ�����
yanchi(500);
deputy(2);//����״̬
yanchi(500);
vice(2);//��������
yanchi(500);	
//////////////////////////////////
	}

//	private void figure_recognition(){
//		yanchi(500);
//		picture(2);
//		yanchi(2500);
//		quanHandler.sendEmptyMessage(8);    //����ʶ��
//		yanchi(2000);
//		if (lice_flag) {             //��һ�η�ҳ
//			lice_flag = false;
//			picture(2);   //ͼƬ�·�
//			yanchi(2000);
//			quanHandler.sendEmptyMessage(8);    //����ʶ��	
//			lice_flag = false;
//			yanchi(2000);
//		}
//		picture(2);//�·�ͼƬ
//		yanchi(3000);
//		quanHandler.sendEmptyMessage(9);      //ͼ�θ���ʶ��
//		yanchi(1000);
//	}
	
	private void goDisplay(int fangxiang){//����������ʾ
		//ǰ����ת��5�е�
		if (fangxiang ==1) {
			sendvoice("ȥ��������ʾ");
			yanchi(1000);
			
//			back(80, dis);
//			go(80, 100);
			left(speeds[3]);
			//��ת45���ͺ���
			MAJOR = 0x04;
			FIRST = (byte) (speeds[3] & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			yanchi(500);
			stop();
			yanchi(1000);
			quanHandler.sendEmptyMessage(15);//������ʾ
			yanchi(3000);
		}
		if (fangxiang ==2) {
			
			//��ת45���ͺ���
			MAJOR = 0x04;
			FIRST = (byte) (speeds[3] & 0xFF);
			SECOND = 0x00;
			THRID = 0x00;
			send();
			yanchi(500);
			stop();
			yanchi(1000);
			quanHandler.sendEmptyMessage(15);//������ʾ
			yanchi(3000);
		}
	}	

	private void goGear() {    //ȥ����λ��λ��   
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
			sendvoice("ͨ�����");
			yanchi(1000);
//			fan_flag = true;
			fan();
			yanchi(3000);
			line(50);
		}
	private void goTraffic(){//ȥ����ͨ��

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
		//дȥ��������·��
		go(speeds[0], encoders[0]);
		right(speeds[3]);
		line(speeds[1]);
	}


	private void goDisplay(){
		//ǰ����ת��5�е�

		left(speeds[3]);
		//��ת���ͺ���
		MAJOR = 0x04;
		FIRST = (byte) (speeds[3] & 0xFF);
		SECOND = 0x00;
		THRID = 0x00;
		send();
		yanchi(500);
		stop();
		yanchi(1000);
		quanHandler.sendEmptyMessage(15);//������ʾ
		yanchi(3000);
	}	
	private void duty(int i){
		if (i == 1) {			
			deputy(1);//�ӳ�״̬
			yanchi(500);
			vice(1);//�ӳ�����
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
			deputy(2);//����״̬
			yanchi(1000);
			vice(2);//��������
			yanchi(1000);		
		} else if(i == 2){
			deputy(1);//�ӳ�״̬
			yanchi(500);
			vice(1);//�ӳ�����
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
			deputy(2);//����״̬
			yanchi(1000);
			vice(2);//��������
			yanchi(1000);				
		}else if(i == 3){
			deputy(1);//�ӳ�״̬
			yanchi(500);
			vice(1);//�ӳ�����
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
			deputy(2);//����״̬
			yanchi(1000);
			vice(2);//��������
			yanchi(1000);				
		}
	}

	private void outCarport(){
		gate(1);//����բ
		yanchi(2000);
		digital_open();   //������ܼ�ʱ
		yanchi(1000);
		//�����⶯��
		go(speeds[0], encoders[0]);//������
		line(speeds[1]);//ѭ������һ��ʮ��·��
		sendvoice("��ͨѲ�߳�����Ѳ�������");
		yanchi(1000);
		mark=20;
		}
	private void goRanging(){//ǰ�����λ��
		//дǰ������λ��
		go(speeds[0], encoders[0]);
		right(speeds[3]);
		line(speeds[1]);
	}
	
	private void goQrcode(){
		quanHandler.sendEmptyMessage(6);//��ȡ�������ҷ���һ��		
		go(speeds[0],encoders[0]);
		left(speeds[3]);
		quanHandler.sendEmptyMessage(7);//ʶ���λ�뿪��
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
		quanHandler.sendEmptyMessage(5);//��ȡ������ǰ����һ��
		line(speeds[1]);
	}
			

	private void goShape1(){  //ȥ������λ��
		quanHandler.sendEmptyMessage(20);  //��ȡ��ǰ���Ͷ���
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
	private void gotuxing(){ //ȥ��ͼ��λ��
		quanHandler.sendEmptyMessage(20);  //��ȡ��ǰ���Ͷ���
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
		quanHandler.sendEmptyMessage(10);//����ʶ��
		yanchi(3000);
		if(state==2||state==3){//����ת
			go(speeds[0], encoders[0]);//
			right(speeds[3]);
			line(speeds[1]);
			go(speeds[0], encoders[0]);//��J7
			
			line(speeds[1]);
			gate(1);//����բ
			yanchi(2000);
			
			go(speeds[0], 220);
			
			infrared((byte) 0x03, (byte) 0x05,
					(byte) 0x14, (byte) 0x45, (byte) 0xDE,
					(byte) 0x92);
			yanchi(200);
			
			digital_close();   //������ܼ�ʱ
			yanchi(1000);
			sendvoice("�����������");
			yanchi(3000);
		}
		else if(state==1||state==4){//����ת
			go(speeds[0], encoders[0]);
			left(speeds[3]);
			line(speeds[1]);
			
			
			left(speeds[3]);
			line(speeds[1]);
			go(speeds[0], encoders[0]);//��J3
			
			line(speeds[1]);
			go(speeds[0], encoders[0]);
			
			line(speeds[1]);
			
			gate(1);//����բ
			yanchi(2000);
			
			go(speeds[0], 220);
			
			infrared((byte) 0x03, (byte) 0x05,
					(byte) 0x14, (byte) 0x45, (byte) 0xDE,
					(byte) 0x92);
			yanchi(200);
			
			digital_close();   //������ܼ�ʱ
			yanchi(1000);
			sendvoice("�����������");
			yanchi(3000);
		}
		else if(state==5){// ����
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
			
			gate(1);//����բ
			yanchi(2000);
			
			go(speeds[0], 220);
			
			infrared((byte) 0x03, (byte) 0x05,
					(byte) 0x14, (byte) 0x45, (byte) 0xDE,
					(byte) 0x92);
			yanchi(200);
			
			digital_close();   //������ܼ�ʱ
			yanchi(1000);
			sendvoice("�����������");
			yanchi(1000);
		}
		else{//ʶ��ʶ��
//			back(speeds[0], 70);
			go(speeds[0], encoders[0]);//
			right(speeds[3]);
			line(speeds[1]);
			go(speeds[0], encoders[0]);//��J7
			
			line(speeds[1]);
			gate(1);//����բ
			yanchi(2000);
			
			go(speeds[0], 220);
			
			infrared((byte) 0x03, (byte) 0x05,
					(byte) 0x14, (byte) 0x45, (byte) 0xDE,
					(byte) 0x92);
			yanchi(200);
			
			digital_close();   //������ܼ�ʱ
			yanchi(1000);
			sendvoice("�����������");
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
	//���ӳ�Ȩ��
	deputy(1);//�ӳ�״̬
	yanchi(500);
	vice(1);//�ӳ�����
	yanchi(500);
	//д���ƴӳ�����
	line(speeds[1]);
	go(speeds[0], encoders[0]);
//	arm(0x70, 0x02, 0x01);
	line(speeds[1]);
	go(speeds[0], encoders[2]);
	//�����û�Ȩ��
	deputy(2);//����״̬
	yanchi(1000);
	vice(2);//��������
	yanchi(1000);
}
	
	
	private void goDeputy(int MO){
		deputy(1);//�ӳ�״̬
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
		deputy(2);//����״̬
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
				case 1://ǰ��
					
					break;
				case 2://����
					
					break;
				case 3://��ת
					
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
