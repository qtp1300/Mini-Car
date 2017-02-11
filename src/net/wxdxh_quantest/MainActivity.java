package net.wxdxh_quantest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.wxdxh_quantest.client.Client;
import net.wxdxh_quantest.global.Global;
import net.wxdxh_quantest.saveUtil.FileService;
import net.wxdxh_quantest.service.SearchService;
import net.wxdxh_quantest.util.CRCUtil;
import net.wxdxh_quantest.util.LicenseUtil;
import net.wxdxh_quantest.util.MathUtil1;
import net.wxdxh_quantest.util.RGBLuminanceSource;
import net.wxdxh_quantest.util.ShapeUtil;
import net.wxdxh_quantest.util.ShapeUtil1;
import net.wxdxh_quantest.util.TrafficUtil;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;

import net.wxdxh_ourcar07.R;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

public class MainActivity extends Activity implements
		android.view.View.OnClickListener {
	// 全自动,预设位，其它（二维码、车牌，个数识别）、结果
	private Button quanbtn,setbtn,otherbtn,resultbtn,nfcbtn;
	// 前进、后退、左拐和右拐
	private ImageButton upbtn, downbtn, leftbtn, rightbtn;
	// 停止、循迹、指示灯、红外和蜂鸣器、zigbee、二维码、副车状态
	private Button stopbtn, trackbtn, lightbtn, infrarebtn, buzzerbtn,
			zigbeebtn, zfbtn, vicebtn;
	private Button armbtn;//机械臂
	// 速度、码盘和接受数据编辑框
	private EditText speedText, encoderText, show;
	// 图片
	private ImageView showView;
	// WiFi管理器
	private WifiManager wifiManager;
	// 服务器管理器
	private DhcpInfo dhcpInfo;
	// 小车ip
	private String IPCar;
	// 摄像头IP
	private String IPCamera;
	// socket类
	private Client client;
	// 接受传感器
	long psStatus = 0;// 状态
	long UltraSonic = 0;// 超声波
	long Light = 0;// 光照
	long CodedDisk = 0;// 码盘值
	private byte[] mByte = new byte[11];
	// 速度与码盘值
	private int sp_n, en_n;
	// 报警器,指示灯,蜂鸣器,红外,颜色
	int k = 3, i = 3, h = 1, m = -1, c = -1, zg = -1,set=-1,other=-1;
	//语音播报标识
		private boolean flag_voice;
		
	// 接受显示小车发送的数据
	private Handler rehHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				mByte = (byte[]) msg.obj;
				if (mByte[0] == 0x55) {
					// 光敏状态
					psStatus = mByte[3] & 0xff;
					// 超声波数据
					UltraSonic = mByte[5] & 0xff;
					UltraSonic = UltraSonic << 8;
					UltraSonic += mByte[4] & 0xff;
					// 光照强度
					Light = mByte[7] & 0xff;
					Light = Light << 8;
					Light += mByte[6] & 0xff;
					// 码盘
					CodedDisk = mByte[9] & 0xff;
					CodedDisk = CodedDisk << 8;
					CodedDisk += mByte[8] & 0xff;
					if (mByte[1] == (byte) 0xaa) {
						// 显示数据
						show.setText("主车各状态信息：" + " 超声波：" + UltraSonic
								+ "mm 光照：" + Light + "lx" + "  码盘：" + CodedDisk
								+ "光敏状态：" + psStatus + "  状态：" + (mByte[2])
								+ "mark值：" + client.mark);
						vicebtn.setText("主车状态");
					} else if (mByte[1] == 0x02) {
						// 显示数据
						show.setText("运输标志物各状态信息：" + " 超声波：" + UltraSonic
								+ "mm 光照：" + Light + "lx" + "  码盘：" + CodedDisk
								+ "光敏状态：" + psStatus + "  状态：" + (mByte[2])
								+ "mark值：" + client.mark);
						vicebtn.setText("从车状态");
					}

				}
			}
		};
	};
	// 广播名称
	public static final String A_S = "com.a_s";
	// 广播接收器接受SearchService搜索的摄像头IP地址加端口
	private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			IPCamera = arg1.getStringExtra("IP");
			phThread.start();
		}
	};
	// 图片
	private Bitmap bitmap;
	// 摄像头工具
	private CameraCommandUtil cameraCommandUtil;
	public boolean flag_camera;
	// 开启线程接受摄像头当前图片
	private Thread phThread = new Thread(new Runnable() {
		public void run() {
			while (true)
				getBitmap();
		}
	});
	// 显示图片
	public Handler phHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 10) {
				showView.setImageBitmap(bitmap);
			}
		}
	};

	// 得到当前摄像头的图片信息
	public void getBitmap() {
		bitmap = cameraCommandUtil.httpForImage(IPCamera);
		phHandler.sendEmptyMessage(10);
	}

	// 搜索摄像cameraIP进度条
	private void search() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SearchService.class);
		startService(intent);
	}

	// 同服务器连接socket
	private Thread socketThread = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 同wifi服务器连接
			client.connect(rehHandler, IPCar);
		}
	});

	// 创建生命周期
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);// 引用布局文件
		// 注册广播接收器
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(A_S);
		registerReceiver(myBroadcastReceiver, intentFilter);
		// 搜索摄像头图片工具
		cameraCommandUtil = new CameraCommandUtil();
		// 开启后台服务搜索摄像头
		search();
		// 得到服务器的IP地址
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		dhcpInfo = wifiManager.getDhcpInfo();
		IPCar = Formatter.formatIpAddress(dhcpInfo.gateway);
		// 控件初始化
		init();
		// 实例化client类
		client = new Client(MainActivity.this,quanHandler);
		// 同wifi服务器连接
		socketThread.start();
		client.quanThread.start();
	}

	// 图片区域滑屏监听点击和弹起坐标位置
	private final int MINLEN = 30;
	private float x1 = 0;
	private float x2 = 0;
	private float y1 = 0;
	private float y2 = 0;

	// 初始化方法
	private void init() {
		// 全自动
		quanbtn = (Button) findViewById(R.id.quanbtn);
		setbtn=(Button) findViewById(R.id.setbtn);
		otherbtn=(Button) findViewById(R.id.otherbtn);
		resultbtn=(Button) findViewById(R.id.resultbtn);
		nfcbtn=(Button) findViewById(R.id.nfcbtn);
		// 前后左右
		upbtn = (ImageButton) findViewById(R.id.upbtn);
		downbtn = (ImageButton) findViewById(R.id.downbtn);
		leftbtn = (ImageButton) findViewById(R.id.leftbtn);
		rightbtn = (ImageButton) findViewById(R.id.rightbtn);
		// 停循迹
		stopbtn = (Button) findViewById(R.id.stopbtn);
		trackbtn = (Button) findViewById(R.id.trackbtn);
		// 其他
		lightbtn = (Button) findViewById(R.id.lightbtn);
		infrarebtn = (Button) findViewById(R.id.infrarebtn);
		buzzerbtn = (Button) findViewById(R.id.buzzerbtn);
		zigbeebtn = (Button) findViewById(R.id.zigbeebtn);
		zfbtn = (Button) findViewById(R.id.zfbtn);
		vicebtn = (Button) findViewById(R.id.vicebtn);
		armbtn=(Button) findViewById(R.id.armbtn);
		// 速度和码盘编辑框
		speedText = (EditText) findViewById(R.id.speedText);
		encoderText = (EditText) findViewById(R.id.encoderText);
		// 接受到的数据显示框
		show = (EditText) findViewById(R.id.show);
		// 摄像头图片
		showView = (ImageView) findViewById(R.id.showView);
		// 全自动监听
		quanbtn.setOnClickListener(this);
		setbtn.setOnClickListener(this);
		otherbtn.setOnClickListener(this);
		resultbtn.setOnClickListener(this);
		nfcbtn.setOnClickListener(this);
		// 前进、后退、左右、停止和循迹按钮监听
		upbtn.setOnClickListener(this);
		downbtn.setOnClickListener(this);
		leftbtn.setOnClickListener(this);
		rightbtn.setOnClickListener(this);
		stopbtn.setOnClickListener(this);
		trackbtn.setOnClickListener(this);
		// 其他按钮监听
		lightbtn.setOnClickListener(this);
		infrarebtn.setOnClickListener(this);
		buzzerbtn.setOnClickListener(this);
		zigbeebtn.setOnClickListener(this);
		zfbtn.setOnClickListener(this);
		vicebtn.setOnClickListener(this);
		armbtn.setOnClickListener(this);
		// 图片触屏监听
		showView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				// 点击位置坐标
				case MotionEvent.ACTION_DOWN:
					x1 = event.getX();
					y1 = event.getY();
					break;
				// 弹起坐标
				case MotionEvent.ACTION_UP:
					x2 = event.getX();
					y2 = event.getY();
					float xx = x1 > x2 ? x1 - x2 : x2 - x1;
					float yy = y1 > y2 ? y1 - y2 : y2 - y1;
					// 判断滑屏趋势
					if (xx > yy) {
						if ((x1 > x2) && (xx > MINLEN)) {// left
							cameraCommandUtil.postHttp(IPCamera, 4, 1);
						} else if ((x1 < x2) && (xx > MINLEN)) {// right
							cameraCommandUtil.postHttp(IPCamera, 6, 1);
						}
					} else {
						if ((y1 > y2) && (yy > MINLEN)) {// down
							cameraCommandUtil.postHttp(IPCamera, 2, 1);
						} else if ((y1 < y2) && (yy > MINLEN)) {// up
							cameraCommandUtil.postHttp(IPCamera, 0, 1);
						}
					}
					x1 = 0;
					x2 = 0;
					y1 = 0;
					y2 = 0;
					break;
				}
				return true;
			}
		});
	}

	// 单击事件处理方法
	public void onClick(View v) {
		sp_n = getSpeed();
		en_n = getEncoder();
		switch (v.getId()) {
		case R.id.nfcbtn:
			startActivity(new Intent(MainActivity.this,NfcDemoActivity.class));
			break;
		case R.id.quanbtn:// 全自动
			client.mark= 10;
			break;
		case R.id.setbtn://预设位
			AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
			builder.setTitle("预设位设置");
			String[] set_item = { "set1", "set2" ,"set3","set4","call1","call2","call3","call4"};
			builder.setSingleChoiceItems(set_item, set,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								quanHandler.sendEmptyMessage(1);
								break;
							case 1:
								quanHandler.sendEmptyMessage(2);
								break;
							case 2:
								quanHandler.sendEmptyMessage(3);
								break;
							case 3:
								quanHandler.sendEmptyMessage(19);
								break;
							case 4:
								quanHandler.sendEmptyMessage(4);
								break;
							case 5:
								quanHandler.sendEmptyMessage(5);
								break;
							case 6:
								quanHandler.sendEmptyMessage(6);
								break;
							case 7:
								quanHandler.sendEmptyMessage(20);
								break;	
							default:
								break;
							}
							dialog.cancel();
						}
					});
			builder.create().show();
			break;
		case R.id.otherbtn:
			AlertDialog.Builder builder2=new AlertDialog.Builder(MainActivity.this);
			builder2.setTitle("附件功能");
			String[] other_item = { "二维码", "车牌" ,"形状个数","交通灯","所有形状个数"};
			builder2.setSingleChoiceItems(other_item, other,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0://二维码
								qrHandler.sendEmptyMessage(10);
								break;
							case 1://车牌
								qrHandler.sendEmptyMessage(20);
								break;
							case 2://形状个数
								quanHandler.sendEmptyMessage(16);
								break;
							case 3://交通灯识别
								quanHandler.sendEmptyMessage(10);
								break;
							case 4://所有形状个数
								quanHandler.sendEmptyMessage(25);
								break;	
							default:
								break;
							}
						}
					});
			builder2.create().show();
			break;
		case R.id.resultbtn:
			Intent intent=new Intent();
			intent.setClass(MainActivity.this, ResultActivity.class);
			startActivity(intent);
			break;
		case R.id.vicebtn:// 主副车状态转换按钮
			if (vicebtn.getText().toString().equals("主车状态")) {
				vicebtn.setText("从车状态");
				client.vice(1);
			} else {
				vicebtn.setText("主车状态");
				client.vice(2);
			}
			break;
		case R.id.upbtn:// 前进
			client.go(sp_n, en_n);
			break;
		case R.id.downbtn:// 后退
			client.back(sp_n, en_n);
			break;
		case R.id.leftbtn:// 左转
			client.left(sp_n);
			break;
		case R.id.rightbtn:// 右转
			client.right(sp_n);
			break;
		case R.id.stopbtn:// 停止
			client.stop();
			break;
		case R.id.trackbtn:// 循迹
			client.line(sp_n);
			break;
		case R.id.lightbtn:// 指示灯
			Builder lg_builder = new AlertDialog.Builder(MainActivity.this);
			lg_builder.setTitle("指示灯");
			String[] lg_item = { "双色LED灯", "左右标识灯" };
			lg_builder.setSingleChoiceItems(lg_item, m,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {// 双色LED灯
								ledController();
							} else if (which == 1) {// 左右标识灯
								lightController();
							}
							dialog.cancel();
						}
					});
			lg_builder.create().show();
			break;
		case R.id.buzzerbtn:// 蜂鸣器
			buzzerController();
			break;
		case R.id.infrarebtn:// 红外线
			Builder infrare_builder = new AlertDialog.Builder(MainActivity.this);
			infrare_builder.setTitle("红外");
			String[] infrare_item = { "报警器", "图片器", "档位器", "风扇", "立体显示" };
			infrare_builder.setSingleChoiceItems(infrare_item, m,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {// 报警
								policeController();
							} else if (which == 1) {// 图片
								pictureController();
							} else if (which == 2) {// 档位
								gearController();
							} else if (which == 3) {// 风扇
								client.fan();
							} else if (which == 4) {// 立体显示
								threeDisplay();
							}
							dialog.cancel();
						}
					});
			infrare_builder.create().show();
			break;
		case R.id.zfbtn:// 主副车指令
			if (zfbtn.getText().toString().equals("主指令")) {
				zfbtn.setText("副指令");
				client.deputy(1);
				armbtn.setEnabled(true);
			} else {
				zfbtn.setText("主指令");
				client.deputy(2);
				armbtn.setEnabled(false);
			}
			break;
		case R.id.zigbeebtn:// zigbee应用
			Builder zg_builder = new AlertDialog.Builder(MainActivity.this);
			zg_builder.setTitle("zigbee");
			String[] zg_item = { "闸门", "数码管" ,"语音播报"};
			zg_builder.setSingleChoiceItems(zg_item, zg,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {// 闸门
								gateController();
							} else if (which == 1) {// 数码管
								digital();
							}else if (which == 2) {
								voiceController();
							}
							
							dialog.cancel();
						}
					});
			zg_builder.create().show();
			break;
		case R.id.armbtn:
			AlertDialog.Builder armBuilder=new AlertDialog.Builder(MainActivity.this);
			armBuilder.setTitle("达到状态");
			String [] item={"状态1","状态2","状态3","状态4"};
			armBuilder.setSingleChoiceItems(item, arm_i, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					arm_i=which;
					if (which == 0) {
						client.arm(0x70, 0x02, 0x01);
					} else if (which == 1) {
						client.arm(0x70, 0x02, 0x2);
					} else if (which == 2) {
						client.arm(0x70, 0x02, 0x3);
					}else if (which == 3) {
						client.arm(0x72, 0x07, 0x00);
					} 
					dialog.dismiss();
				}
			});
			armBuilder.create().show();
			break;
		default:
			break;
		}
	}
	private int arm_i=-1;
	// 指示灯遥控器
	private void lightController() {
		AlertDialog.Builder lt_builder = new AlertDialog.Builder(
				MainActivity.this);
		lt_builder.setTitle("指示灯");
		String[] item = { "左亮", "全亮", "右亮", "全灭" };
		lt_builder.setSingleChoiceItems(item, i,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {
							client.light(1, 0);
							i = 00;
						} else if (which == 1) {
							client.light(1, 1);
							i = 01;
						} else if (which == 2) {
							client.light(0, 1);
							i = 02;
						} else if (which == 3) {
							client.light(0, 0);
							i = 03;
						}
						dialog.dismiss();
					}
				});
		lt_builder.create().show();
	}

	// 蜂鸣器
	private void buzzerController() {
		AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
		build.setTitle("蜂鸣器");
		String[] im = { "开", "关" };
		build.setSingleChoiceItems(im, h,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {
							// 打开蜂鸣器
							client.buzzer(1);
							h = 0;
						} else if (which == 1) {
							// 关闭蜂鸣器
							client.buzzer(0);
							h = 1;
						}
						dialog.dismiss();
					}
				});
		build.create().show();
	}

	// 报警器
	private void policeController() {
		AlertDialog.Builder police = new AlertDialog.Builder(MainActivity.this);
		police.setTitle("报警器");
		String[] item2 = { "开", "关" };
		police.setSingleChoiceItems(item2, k,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {
							// 打开报警器
							// 0x03 0x05 0x14 0x45 0xDE 0x92
							client.infrared((byte) 0x03, (byte) 0x05,
									(byte) 0x14, (byte) 0x45, (byte) 0xDE,
									(byte) 0x92);
							k = 0;
						} else if (which == 1) {
							// 关闭报警器
							// 0x67 0x34 0x78 0xA2 0xFD 0x27
							client.infrared((byte) 0x67, (byte) 0x34,
									(byte) 0x78, (byte) 0xA2, (byte) 0xFD,
									(byte) 0x27);
							k = 1;
						}
						dialog.dismiss();
					}
				});
		police.create().show();
	}

	private int pt_index = -1;// 图片指标

	private void pictureController() {
		Builder pt_builder = new AlertDialog.Builder(MainActivity.this);
		pt_builder.setTitle("图片遥控器");
		String[] pt_item = { "上翻", "下翻" };
		pt_builder.setSingleChoiceItems(pt_item, pt_index,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {// 图片上翻
							client.picture(1);
							pt_index = 0;
						} else if (which == 1) {// 图片下翻
							client.picture(0);
							pt_index = 1;
						}
						dialog.dismiss();// 取消对话框
					}
				});
		pt_builder.create().show();// 创建对话框和显示
	}

	// 光照档位控制
	private int gr_index = -1;

	private void gearController() {
		Builder gr_builder = new AlertDialog.Builder(MainActivity.this);
		gr_builder.setTitle("档位遥控器");
		String[] gr_item = { "光强加1档", "光强加2档", "光强加3档" };
		gr_builder.setSingleChoiceItems(gr_item, gr_index,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {// 加一档
							client.gear(1);
							gr_index = 0;
						} else if (which == 1) {// 加二档
							client.gear(2);
							gr_index = 1;
						} else if (which == 2) {// 加三档
							client.gear(3);
							gr_index = 2;
						}
						dialog.dismiss();
					}
				});
		gr_builder.create().show();
	}

	private int threeindex = -1;

	private void threeDisplay() {
		AlertDialog.Builder threeBuilder = new AlertDialog.Builder(
				MainActivity.this);
		threeBuilder.setTitle("立体显示");
		String[] three_item = { "颜色信息", "图形信息", "距离信息", "车牌信息", "路况信息", "默认信息" };
		threeBuilder.setSingleChoiceItems(three_item, threeindex,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							color();
							break;
						case 1:
							shape();
							break;
						case 2:
							dis();
							break;
						case 3:
							lic();
							break;
						case 4:
							road();
							break;
						case 5:
							data[0] = 0x15;
							data[1] = 0x01;
							client.infrared_stereo(data);
							break;
						default:
							break;
						}
						dialog.cancel();
					}
				});
		threeBuilder.create().show();
	}

	private short[] data = { 0x00, 0x00, 0x00, 0x00, 0x00 };
	private int cor = -1, s = -1, r = -1;

	private void color() {
		Builder colorBuilder = new AlertDialog.Builder(this);
		colorBuilder.setTitle("颜色信息");
		String[] lg_item = { "红色", "绿色", "蓝色", "黄色", "紫色", "青色", "黑色", "白色" };
		colorBuilder.setSingleChoiceItems(lg_item, cor,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						cor = which;
						data[0] = 0x13;
						data[1] = (short) (which + 0x01);
						client.infrared_stereo(data);
					}
				});
		colorBuilder.create().show();
	}

	private void shape() {
		Builder shapeBuilder = new AlertDialog.Builder(this);
		shapeBuilder.setTitle("图形信息");
		String[] shape_item = { "矩形", "圆形", "三角形", "菱形", "梯形", "饼图", "靶图",
				"条形图" };
		shapeBuilder.setSingleChoiceItems(shape_item, s,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						s = which;
						data[0] = 0x12;
						data[1] = (short) (which + 0x01);
						client.infrared_stereo(data);
					}
				});
		shapeBuilder.create().show();
	}

	private void road() {
		Builder roadBuilder = new AlertDialog.Builder(this);
		roadBuilder.setTitle("路况信息");
		String[] road_item = { "隧道有事故，请绕行", "前方施工，请绕行" };
		roadBuilder.setSingleChoiceItems(road_item, r,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						r = which;
						data[0] = 0x14;
						data[1] = (short) (which + 0x01);
						client.infrared_stereo(data);
					}
				});
		roadBuilder.create().show();
	}

	private int dis = -1;
	private String[] road_item = { "10cm", "15cm", "20cm", "28cm", "39cm" };

	private void dis() {
		Builder disBuilder = new AlertDialog.Builder(this);
		disBuilder.setTitle("距离信息");

		disBuilder.setSingleChoiceItems(road_item, dis,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dis = which;
						int disNum = Integer.parseInt(road_item[which]
								.substring(0, 2));
						Log.e("dddddddddddddddd", disNum + "");
						data[0] = 0x11;
						data[1] = (short) (disNum / 10 + 0x30);
						data[2] = (short) (disNum % 10 + 0x30);
						client.infrared_stereo(data);
					}
				});
		disBuilder.create().show();
	}

	private int lic = -1;
	private String[] lic_item = { "K365G9F1", "B427F8B1", "D227C3F1","F467I7F1","H833E8F1",
			"J556C2F1","J996E9F1","D582G8F1","B543E8F1"};

	private void lic() {
		Builder licBuilder = new AlertDialog.Builder(this);
		licBuilder.setTitle("车牌信息");
		licBuilder.setSingleChoiceItems(lic_item, lic,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						lic = which;
						licHandler.sendEmptyMessage(which);
					}
				});
		licBuilder.create().show();
	}

	// 从string中得到short数据数组
	private short[] StringToBytes(String licString) {
		if (licString == null || licString.equals("")) {
			return null;
		}
		licString = licString.toUpperCase();
		int length = licString.length();
		char[] hexChars = licString.toCharArray();
		short[] d = new short[length];
		for (int i = 0; i < length; i++) {
			d[i] = (short) hexChars[i];
		}
		return d;
	}

	private Handler licHandler = new Handler() {
		public void handleMessage(Message msg) {
			short[] li = StringToBytes(lic_item[msg.what]);
			data[0] = 0x20;
			data[1] = (short) (li[0]);
			data[2] = (short) (li[1]);
			data[3] = (short) (li[2]);
			data[4] = (short) (li[3]);
			client.infrared_stereo(data);
			data[0] = 0x10;
			data[1] = (short) (li[4]);
			data[2] = (short) (li[5]);
			data[3] = (short) (li[6]);
			data[4] = (short) (li[7]);
			client.infrared_stereo(data);
			new FileService().savePhoto(bitmap, Global.THREED+".png");
		};
	};
	private int dig_index;

	private void digital() {// 数码管
		AlertDialog.Builder dig_timeBuilder = new AlertDialog.Builder(
				MainActivity.this);
		dig_timeBuilder.setTitle("数码管");
		String[] dig_item = { "数码管显示", "数码管计时", "显示距离" };
		dig_timeBuilder.setSingleChoiceItems(dig_item, dig_index,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {// 数码管显示
							digitalController();
							dig_index = 0;
						} else if (which == 1) {// 数码管计时
							digital_time();
							dig_index = 1;
						} else if (which == 2) {// 显示距离
							digital_dis();
							dig_index = 2;
						}
						dialog.dismiss();
					}
				});
		dig_timeBuilder.create().show();
	}

	private int dgtime_index = -1;

	private void digital_time() {// 数码管计时
		AlertDialog.Builder dg_timeBuilder = new AlertDialog.Builder(
				MainActivity.this);
		dg_timeBuilder.setTitle("数码管计时");
		String[] dgtime_item = { "计时结束", "计时开始", "计时清零" };
		dg_timeBuilder.setSingleChoiceItems(dgtime_item, dgtime_index,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {// 计时结束
							client.digital_close();
							dgtime_index = 0;
						} else if (which == 1) {// 计时开启
							client.digital_open();
							dgtime_index = 1;
						} else if (which == 2) {// 计时清零
							client.digital_clear();
							dgtime_index = 2;
						}
						dialog.dismiss();
					}
				});
		dg_timeBuilder.create().show();
	}

	private int dgdis_index = -1;

	private void digital_dis() {
		AlertDialog.Builder dis_timeBuilder = new AlertDialog.Builder(
				MainActivity.this);
		dis_timeBuilder.setTitle("显示距离");
		final String[] dis_item = { "10cm", "20cm", "40cm" };
		dis_timeBuilder.setSingleChoiceItems(dis_item, dgdis_index,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {// 距离10cm
							client.digital_dic(Integer.parseInt(dis_item[which]
									.substring(0, 2)));
							dgtime_index = 0;
						} else if (which == 1) {// 距离20cm
							client.digital_dic(Integer.parseInt(dis_item[which]
									.substring(0, 2)));
							dgtime_index = 1;
						} else if (which == 2) {// 距离40cm
							client.digital_dic(Integer.parseInt(dis_item[which]
									.substring(0, 2)));
							dgtime_index = 2;
						}
						dialog.dismiss();
					}
				});
		dis_timeBuilder.create().show();
	}

	// 数码管显示方法
	private String[] itmes = { "1", "2" };
	int main, one, two, three;

	private void digitalController() {

		AlertDialog.Builder dg_Builder = new AlertDialog.Builder(
				MainActivity.this);
		View view = LayoutInflater.from(MainActivity.this).inflate(
				R.layout.item_digital, null);
		dg_Builder.setTitle("数码管显示");
		dg_Builder.setView(view);
		// 下拉列表
		Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
		final EditText editText1 = (EditText) view.findViewById(R.id.editText1);
		final EditText editText2 = (EditText) view.findViewById(R.id.editText2);
		final EditText editText3 = (EditText) view.findViewById(R.id.editText3);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				MainActivity.this, android.R.layout.simple_spinner_item, itmes);
		spinner.setAdapter(adapter);
		// 下拉列表选择监听
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				main = position + 1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		dg_Builder.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String ones = editText1.getText().toString();
						String twos = editText2.getText().toString();
						String threes = editText3.getText().toString();
						// 显示数据，一个文本编译框最多两个数据显示数目管中两个数据
						if (ones.equals(""))
							one = 0x00;
						else
							one = Integer.parseInt(ones) / 10 * 16
									+ Integer.parseInt(ones) % 10;
						if (twos.equals(""))
							two = 0x00;
						else
							two = Integer.parseInt(twos) / 10 * 16
									+ Integer.parseInt(twos) % 10;
						if (threes.equals(""))
							three = 0x00;
						else
							three = Integer.parseInt(threes) / 10 * 16
									+ Integer.parseInt(threes) % 10;
						client.digital(main, one, two, three);
					}
				});

		dg_Builder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
		dg_Builder.create().show();

	}

	private int ld = -1;

	private void ledController() {// LED灯
		AlertDialog.Builder builder1 = new AlertDialog.Builder(
				MainActivity.this);
		builder1.setTitle("LED灯");
		String[] item1 = { "亮绿灯", "亮红灯" };
		builder1.setSingleChoiceItems(item1, ld,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {
							client.lamp((byte) 0xAA);
							ld = 0;
						} else if (which == 1) {
							client.lamp((byte) 0x55);
							ld = 1;
						}
						dialog.dismiss();
					}
				});
		builder1.create().show();
	}

	
	// 语言播报
		EditText voiceText;

		private void voiceController() {
			View view = LayoutInflater.from(MainActivity.this).inflate(
					R.layout.item_car, null);
			voiceText = (EditText) view.findViewById(R.id.voiceText);

			Builder voiceBuilder = new AlertDialog.Builder(MainActivity.this);
			voiceBuilder.setTitle("语音播报");
			voiceBuilder.setView(view);
			voiceBuilder.setPositiveButton("播报",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String src = voiceText.getText().toString();
							if (src.equals("")) {
								src = "请输入你要播报的内容";
							}
							
							try {
								flag_voice=true;
								byte[] sbyte = bytesend(src.getBytes("GBK"));
								client.send_voice(sbyte);
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							dialog.cancel();
						}
					});
			voiceBuilder.setNegativeButton("取消", null);
			voiceBuilder.create().show();

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
			client.send_voice(sbyte);
			while (mByte[2] != 79 && mByte[2] != 65); 
	    } 
		
		
		
	private int g = -1;
	// 闸门方法
	private void gateController() {
		Builder gt_builder = new AlertDialog.Builder(MainActivity.this);
		gt_builder.setTitle("闸门控制");
		String[] gt = { "开", "关" };
		gt_builder.setSingleChoiceItems(gt, g,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							// 打开闸门
							client.gate(1);
							g = 0;
						} else if (which == 1) {
							// 关闭闸门
							client.gate(2);
							g = 1;
						}
						dialog.dismiss();
					}
				});
		gt_builder.create().show();
	}

	public static String result_qr = null;
	private Timer timer;
	private int index_lic = 0;
	// 二维码、车牌处理
	Handler qrHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				showToast(MainActivity.this, result_qr, 1000);
				break;
//			case 10:// 二维码识别
//				timer = new Timer();
//				timer.schedule(new TimerTask() {
//					@Override
//					public void run() {
//						result_qr = null;
//						Result result = null;
//						RGBLuminanceSource rSource = new RGBLuminanceSource(
//								bitmap);
//						try {
//							BinaryBitmap binaryBitmap = new BinaryBitmap(
//									new HybridBinarizer(rSource));
//							Map<DecodeHintType, String> hint = new HashMap<DecodeHintType, String>();
//							hint.put(DecodeHintType.CHARACTER_SET, "utf-8");
//							QRCodeReader reader = new QRCodeReader();
//							result = reader.decode(binaryBitmap, hint);
//							if (result.toString() != null) {
//								try {
//									result_qr = result.toString();
//									new FileService().saveToSDCard(
//											Global.QRNAME + ".txt", result_qr);
//									new FileService().savePhoto(bitmap,
//											Global.QRNAME + ".png");
//									qrHandler.sendEmptyMessage(1);
//									timer.cancel();
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//
//							}
//						} catch (NotFoundException e) {
//							e.printStackTrace();
//						} catch (ChecksumException e) {
//							e.printStackTrace();
//						} catch (FormatException e) {
//							e.printStackTrace();
//						}
//					}
//				}, 0, 200);
//				break;
				
			case 10:// 二维码识别
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						result_qr = null;
						Result result = null;
						RGBLuminanceSource rSource = new RGBLuminanceSource(
								bitmap);
						try {
							BinaryBitmap binaryBitmap = new BinaryBitmap(
									new HybridBinarizer(rSource));
							Map<DecodeHintType, String> hint = new HashMap<DecodeHintType, String>();
							hint.put(DecodeHintType.CHARACTER_SET, "utf-8");
							QRCodeReader reader = new QRCodeReader();
							result = reader.decode(binaryBitmap, hint);
							if (result.toString() != null) {
								try {
									result_qr = result.toString();
									String str = new String();
									str = result_qr.replace('d', 'a');
									str = str.replace('e', 'b');
									str = str.replace('f', 'c');
									str = str.replace('g', 'd');
									str = str.replace('h', 'e');
									str = str.replace('i', 'f');
									str = str.replace('j', 'g');
									str = str.replace('k', 'h');
									str = str.replace('l', 'i');
									str = str.replace('m', 'j');
									str = str.replace('n', 'k');
									str = str.replace('o', 'l');
									str = str.replace('p', 'm');
									str = str.replace('q', 'n');
									str = str.replace('r', 'o');
									str = str.replace('s', 'p');
									str = str.replace('t', 'q');
									str = str.replace('u', 'r');
									str = str.replace('v', 's');
									str = str.replace('w', 't');
									str = str.replace('x', 'u');
									str = str.replace('y', 'v');
									str = str.replace('z', 'w');
									str = str.replace('a', 'x');
									str = str.replace('b', 'y');
									str = str.replace('c', 'z');
//									sendvoice(str);
									
									new FileService().saveToSDCard(
											Global.QRNAME + ".txt", str);
									new FileService().savePhoto(bitmap,
											Global.QRNAME + ".png");
									qrHandler.sendEmptyMessage(1);
									timer.cancel();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						} catch (NotFoundException e) {
							e.printStackTrace();
						} catch (ChecksumException e) {
							e.printStackTrace();
						} catch (FormatException e) {
							e.printStackTrace();
						}
					}
				}, 0, 200);
				break;
				
				
			case 20:// 车牌识别
				index_lic++;
				Bitmap bp = new LicenseUtil().convertToGrayscale(bitmap);
				Log.e("执行到了", "已经灰度化了");
				String result_lice = new LicenseUtil().doOcr(bp);
				Log.e("执行到了", "获取了车牌号");
				if (result_lice.length() == 6) {
					MO6 = result_lice;
					try {
						new FileService().saveToSDCard(Global.LICENSE + ".txt",
								MO6);
						new FileService().savePhoto(bitmap,
								Global.LICENSE + ".png");
						showToast(MainActivity.this, result_lice, 1000);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (index_lic > 6) {
					MO6 = "K365G9";
					try {
						new FileService().saveToSDCard(Global.LICENSE + ".txt",
								MO6);
						new FileService().savePhoto(bitmap,
								Global.LICENSE + ".png");
						showToast(MainActivity.this, result_lice, 1000);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					quanHandler.sendEmptyMessage(8);
				}
				break;
			default:
				break;
			}

		};
	};
	private int MO2 = 0;
	private int MO8 = 0;
	private int[] Number= new int[9];
	private String MO6 = "B543E8";
	private Handler quanHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -5:
				MainActivity.this.finish();
				break;
			// /预设位1到3
			case 1:
				cameraCommandUtil.postHttp(IPCamera, 30, 0);   //设定为朝向正右方低一格
				break;
			case 2:
				cameraCommandUtil.postHttp(IPCamera, 32, 0);   //设定为朝向正前方低一格
				break;
			case 3:
				cameraCommandUtil.postHttp(IPCamera, 34, 0);   //设定为朝向正左方低一格
				break;	
		    case 19:
		    	cameraCommandUtil.postHttp(IPCamera, 36, 0);   //设定为正前方低二格
		    	break;
			case 4:
				cameraCommandUtil.postHttp(IPCamera, 31, 0);   //调取朝向正右方低一格
				break;
			case 5:
				cameraCommandUtil.postHttp(IPCamera, 33, 0);   //调取朝向正前方低一格
				break;
			case 6:
				cameraCommandUtil.postHttp(IPCamera, 35, 0);   //调取朝向正左方低一格
				break;
			case 20:
				cameraCommandUtil.postHttp(IPCamera, 37, 0);   //调取正前方低二格
				break;
			case 7:// 二维码识别
				qrHandler.sendEmptyMessage(10);
				break;
			case 8:// 车牌识别
				qrHandler.sendEmptyMessage(20);	
				break;
			case 9:// 形状个数识别
				String result=null;
				String colorName = null;
				String shapeName = null;
				try {
					result = new FileService().read(Global.QRNAME + ".txt");
					if(result.length()>3){//判断数据不为空
						 colorName=result.substring(0,1);
						 shapeName=result.substring(3,5);
					}
					else{
						colorName = "红";
						shapeName = "矩形";
					}

					int index = 0;
					if (colorName.equals("红"))
						index = 1;
					else if (colorName.equals("绿"))
						index = 2;
					else if (colorName.equals("黄"))
						index = 3;
					int shape = 0;
					if (shapeName.equals("三角"))
						shape = 1;
					else if (shapeName.equals("圆形"))
						shape = 2;
					else if (shapeName.equals("矩形"))
						shape = 3;
					new FileService().savePhoto(bitmap, Global.SHAPENAME+".png");
					MO8=new ShapeUtil1().shapeNum(bitmap,index,shape);
					new FileService().saveToSDCard(Global.SHAPENAME+".txt", MO8+"");
					sendvoice(colorName+"色"+shapeName+"个数为"+MO8+"");
//					client.yanchi(3000);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
				break;
			case 10:// 交通灯识别
				int state = new TrafficUtil().shapeIdentification(bitmap);
				new FileService().savePhoto(bitmap, Global.TRAFFIC+".png");
				client.state=state;
				if(state==0)
						client.state=4;
				switch (state) {
				case 0:// 识别失败
					showToast(MainActivity.this, "识别失败", 500);
					try {
						new FileService().saveToSDCard(Global.TRAFFICNAME+".txt", "识别失败");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case 1:// 红色向右
					showToast(MainActivity.this, "禁止右转", 500);
					sendvoice("请左转");
					client.yanchi(3000);
					try {
						new FileService().saveToSDCard(Global.TRAFFICNAME+".txt", "禁止右转");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case 2:// 绿色向右
					showToast(MainActivity.this, "向右转弯", 500);
					sendvoice("请右转");
					client.yanchi(3000);
					try {
						new FileService().saveToSDCard(Global.TRAFFICNAME+".txt", "向右转弯");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case 3:// 红色向左
					showToast(MainActivity.this, "禁止左转", 500);
					sendvoice("请右转");
					client.yanchi(3000);
					try {
						new FileService().saveToSDCard(Global.TRAFFICNAME+".txt", "禁止左转");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case 4:// 绿色向左
					showToast(MainActivity.this, "向左转弯", 500);
					sendvoice("请左转");
					client.yanchi(3000);
					try {
						new FileService().saveToSDCard(Global.TRAFFICNAME+".txt", "向左转弯");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case 5:// 拐弯
					showToast(MainActivity.this, "允许掉头", 500);
					sendvoice("请掉头");
					client.yanchi(3000);
					try {
						new FileService().saveToSDCard(Global.TRAFFICNAME+".txt", "允许掉头");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				default:
					break;
				}
				Log.e("MainActivity", state+"");
				break;
			case 11:// 运算档位
				try {
					MO2=Integer.parseInt(new FileService().read(Global.RANGING));
					if(MO2<100){
						MO2=400;
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				client.gearNum=new MathUtil1().f1Util(MO2);
				break;
			case 12:// 红外运算
				new CRCUtil().CRC16();
				break;
			case 13:// 主车停靠运算
				client.zhuNum=new MathUtil1().f3Util(MO6, MO8);
				break;
			case 14:// 副车停靠位置运算
				String deputy=new MathUtil1().f2Util(MO6);
				if(deputy.equals("B3")){
					client.deputyNum=1;
				}
				else if(deputy.equals("D3")){
					client.deputyNum=3;
				}
				else if(deputy.equals("F3")){
					client.deputyNum=5;
				}
				else if(deputy.equals("H3")){
					client.deputyNum=7;
				}
				else if(deputy.equals("J3")){
					client.deputyNum=9;
				}
				else if(deputy.equals("B7")){
					client.deputyNum=2;
				}
				else if(deputy.equals("D7")){
					client.deputyNum=4;
				}
				else if(deputy.equals("F7")){
					client.deputyNum=6;
				}
				else if(deputy.equals("H7")){
					client.deputyNum=8;
				}
				else if(deputy.equals("J7")){
					client.deputyNum=10;
				}
				break;
			case 15:// 立体显示标志物
				int licence = 0;
				if (MO6.equals("K365G9") || MO6.equals("K36569") || MO6.equals("K565G9") || MO6.equals("K56569")) {
					licence = 0;
				} else if(MO6.equals("B427E8")){
                    licence = 1;
				}else if(MO6.equals("D227C3")||MO6.equals("DZZ7C3")||MO6.equals("D22YC3")||MO6.equals("DZZYC3")){
                    licence = 2;
				}else if(MO6.equals("F467I7")||MO6.equals("F46717") || MO6.equals("F4671?")||MO6.equals("F467I?")){
                    licence = 3;
				}else if(MO6.equals("H833E8")){
                    licence = 4;
				}else if(MO6.equals("J556C2")||MO6.equals("J556CZ")){
                    licence = 5;
				}else if(MO6.equals("J996E9")){
                    licence = 6;
				}else if(MO6.equals("D582G8")||MO6.equals("D58268")||MO6.equals("B582G8")||MO6.equals("B58268")){
                    licence = 7;
				}else if(MO6.equals("B543E8")){
                    licence = 8;
				}else {
					licence = 0;
				}
				licHandler.sendEmptyMessage(licence);
				break;
			case 16:	  //调校图形颜色识别		
			    String colorName1 = null;
			    String shapeName1 = null;					
			    colorName1 = "绿";
			    shapeName1 = "圆形";
				int index = 0;
				if (colorName1.equals("红"))
					index = 1;
				else if (colorName1.equals("绿"))
					index = 2;
				else if (colorName1.equals("黄"))
					index = 3;
				int shape = 0;
				if (shapeName1.equals("三角"))
					shape = 1;
				else if (shapeName1.equals("圆形"))
					shape = 2;
				else if (shapeName1.equals("矩形"))
					shape = 3;
				new FileService().savePhoto(bitmap, Global.SHAPENAME+".png");
				MO8=new ShapeUtil1().shapeNum(bitmap,index,shape);	
			try {
				new FileService().saveToSDCard(Global.SHAPENAME+".txt", MO8+"");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			break;
			case 17://测距拍照
				new FileService().savePhoto(bitmap, Global.RANGING+".png");
				long UltraSonic = 700;
				int index_u=0;
				while (UltraSonic > 600) {
					UltraSonic = 0;
					UltraSonic = mByte[5] & 0xff;
					UltraSonic = UltraSonic << 8;
					UltraSonic += mByte[4] & 0xff;
					index_u++;
					if(index_u>20){
						UltraSonic=400;
						break;
					}
				}
				client.digital_dic(UltraSonic);//数码管显示
				client.yanchi(1000);
//				sendvoice("距离信息为"+Long.toString(UltraSonic)+"毫米");//发送距离信息
				try {
					new FileService().saveToSDCard(Global.RANGING+".txt", UltraSonic+"");
					client.yanchi(500);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 18:   //颜色识别	
				try {
					new FileService().saveToSDCard(Global.COLOUR+".txt", new ShapeUtil().colour(bitmap));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				new FileService().savePhoto(bitmap, Global.COLOUR+".png");
				break;
				
			case 25:	  //所有图形颜色识别		
			    String[] colorName2 = {"红","绿","黄"};
			    String[] shapeName2 = {"三角","圆形","矩形"};
			    int number = 0;
			    for (int i = 0; i < colorName2.length; i++) {
			    	int index2 = 0;
					if (colorName2[i].equals("红"))
						index2 = 1;
					else if (colorName2[i].equals("绿"))
						index2 = 2;
					else if (colorName2[i].equals("黄"))
						index2 = 3;
					
					
					for (int j = 0; j < shapeName2.length; j++) {
						int shape2 = 0;
						if (shapeName2[j].equals("三角"))
							shape2 = 1;
						else if (shapeName2[j].equals("圆形"))
							shape2 = 2;
						else if (shapeName2[j].equals("矩形"))
							shape2 = 3;						
					 Number[number] = new ShapeUtil1().shapeNum(bitmap,index2,shape2);
					 number++;
						if (number>8) {
							number = 0;
						}
					}
				}						    
			new FileService().savePhoto(bitmap, Global.SHAPENAME+".png");					
			try {
				new FileService().saveToSDCard(Global.SHAPENAME+".txt",
						"红色三角"+Number[0]+""+"红色圆形"+Number[1]+""+"红色矩形"+Number[2]+""+
						"绿色三角"+Number[3]+""+"绿色圆形"+Number[4]+""+"绿色矩形"+Number[5]+""+
						"黄色三角"+Number[6]+""+"黄色圆形"+Number[7]+""+"黄色矩形"+Number[8]+"");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
			break;
			
			
			case 26:      //光照强度
				long Light = 0;				
				Light = mByte[7] & 0xff;
				Light = Light << 8;
				Light += mByte[6] & 0xff;
				client.digital_dic(Light);//数码管显示光照强度
				client.yanchi(1000);
//				sendvoice("光照信息为"+Long.toString(Light)+"勒克斯");//发送光照强度信息
//				client.yanchi(4000);
//				if (Light>30 && Light<45) {
//				     sendvoice("当前档位为1档");//发送档位信息
//				}else if (Light>210 && Light<240) {
//					sendvoice("当前档位为2档");//发送档位信息
//				}else if (Light>420 && Light<530) {
//					sendvoice("当前档位为3档");//发送档位信息
//				}else if (Light>852 && Light<1280) {
//					sendvoice("当前档位为4档");//发送档位信息
//				}
				client.yanchi(3000);
				break;	
				
              case 28:    //显示图形  矩形
				client.infrared((byte)0xff, (byte)0x12, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00);
				break;
				
              case 29:    //显示图形  圆形
  				client.infrared((byte)0xff, (byte)0x12, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00);
  				break;
  				
              case 30:    //显示图形   三角形
  				client.infrared((byte)0xff, (byte)0x12, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00);
  				break;
  				
              case 31:    //显示图形  菱形
  				client.infrared((byte)0xff, (byte)0x12, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00);
  				break;
  				
              case 32:    //显示颜色  红色
    			client.infrared((byte)0xff, (byte)0x13, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00);
    		    break;
    		    
              case 33:    //显示颜色  绿色
      			client.infrared((byte)0xff, (byte)0x13, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00);
      		    break;
      		    
              case 34:    //显示颜色  黄色
      			client.infrared((byte)0xff, (byte)0x13, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00);
      		    break;
      		    
              case 35:    //隧道有事故请绕行
        		client.infrared((byte)0xff, (byte)0x14, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00);
        		break; 
        		
              case 36:    //前方施工请绕行
          		client.infrared((byte)0xff, (byte)0x14, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00);
          		break; 				
			default:
				break;
			}
		};
	};

	// 速度和码盘方法
	private int getSpeed() {
		String src = speedText.getText().toString();
		int speed = 40;
		if (!src.equals("")) {
			speed = Integer.parseInt(src);
		} else {
			showToast(MainActivity.this, "请输入速度值", 500);
		}
		return speed;
	}

	private int getEncoder() {
		String src = encoderText.getText().toString();
		int encoder = 70;
		if (!src.equals("")) {
			encoder = Integer.parseInt(src);
		} else {
			showToast(MainActivity.this, "请输入码盘值", 1000);
		}
		return encoder;
	}

	private Toast mToast;

	private void showToast(Context context, String text, int duration) {
		taHandler.removeCallbacks(runnable);
		if (mToast != null)
			mToast.setText(text);
		else
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		taHandler.postDelayed(runnable, duration);
		mToast.show();
	}

	private Handler taHandler = new Handler();
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			mToast.cancel();
		}
	};
}
