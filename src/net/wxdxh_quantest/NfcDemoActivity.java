package net.wxdxh_quantest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import net.wxdxh_quantest.global.Global;
import net.wxdxh_quantest.saveUtil.FileService;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.wxdxh_ourcar07.R;
public class NfcDemoActivity extends Activity {
	//NFC适配器声明
	private NfcAdapter nfcAdapter = null;
	//传达意图声明
	private PendingIntent pi = null;
	//滤掉组件无法响应和处理的Intent
	private IntentFilter tagDetected = null;
	//文本控件的声明
	private TextView promt = null;
	//是否支持NFC功能的标签
	private boolean isNFC_support = false;
	//读、写、删按钮控件的声明
	private Button readBtn, writeBtn, deleteBtn;
	//输入内容
	private EditText out_content=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc_demo);
		//控件的绑定
		out_content=(EditText) findViewById(R.id.content);
		promt = (TextView) findViewById(R.id.promt);
		readBtn = (Button) findViewById(R.id.read_btn);
		writeBtn = (Button) findViewById(R.id.write_btn);
		deleteBtn = (Button) findViewById(R.id.delete_btn);
		//给文本控件赋值初始文本
		promt.setText("等待RFID标签");
		//监听读、写、删按钮控件
		readBtn.setOnClickListener(new MyOnClick());
		writeBtn.setOnClickListener(new MyOnClick());
		deleteBtn.setOnClickListener(new MyOnClick());
		//初始化设备支持NFC功能
		isNFC_support = true;
		//得到默认nfc适配器
		nfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
		//提示信息定义
		String metaInfo = "";
		//判定设备是否支持NFC或启动NFC
		if (nfcAdapter == null) {
			metaInfo = "设备不支持NFC！";
			Toast.makeText(this, metaInfo, Toast.LENGTH_SHORT).show();
			isNFC_support = false;
		}
		if (!nfcAdapter.isEnabled()) {
			metaInfo = "请在系统设置中先启用NFC功能！";
			Toast.makeText(this, metaInfo, Toast.LENGTH_SHORT).show();
			isNFC_support = false;
		}

		if (isNFC_support == true) {
			this.init_NFC();
		} else {
			promt.setTextColor(Color.RED);
			promt.setText(metaInfo);
		}
	}
//读、写、删按钮点击触发后实现相应功能
	private class MyOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			//点击读按钮后
			if (v.getId() == R.id.read_btn) {
				try {
					String content = read(tagFromIntent);
					if (content != null && !content.equals("")) {
						promt.setText(promt.getText() + "nfc标签内容：\n" + content+ "\n");
						new FileService().saveToSDCard(Global.CRCNAME, content.substring(7));
//						startActivity(new Intent(NfcDemoActivity.this,MainActivity.class));
					} else {
						promt.setText(promt.getText() + "nfc标签内容：\n" + "内容为空\n");
					}
				} catch (IOException e) {
					promt.setText(promt.getText() + "错误:" + e.getMessage()
							+ "\n");
					Log.e("myonclick", "读取nfc异常", e);
				} catch (FormatException e) {
					promt.setText(promt.getText() + "错误:" + e.getMessage()
							+ "\n");
					Log.e("myonclick", "读取nfc异常", e);
				}
				//点击写后写入
			} else if (v.getId() == R.id.write_btn) {
				try {
					write(tagFromIntent);
				} catch (IOException e) {
					promt.setText(promt.getText() + "错误:" + e.getMessage()
							+ "\n");
					Log.e("myonclick", "写nfc异常", e);
				} catch (FormatException e) {
					promt.setText(promt.getText() + "错误:" + e.getMessage()
							+ "\n");
					Log.e("myonclick", "写nfc异常", e);
				}
			} else if (v.getId() == R.id.delete_btn) {
				try {
					delete(tagFromIntent);
				} catch (IOException e) {
					promt.setText(promt.getText() + "错误:" + e.getMessage()
							+ "\n");
					Log.e("myonclick", "删除nfc异常", e);
				} catch (FormatException e) {
					promt.setText(promt.getText() + "错误:" + e.getMessage()
							+ "\n");
					Log.e("myonclick", "删除nfc异常", e);
				}
			}
		}
	}
	// 字符序列转换为16进制字符串
	private String bytesToHexString(byte[] src) {
		return bytesToHexString(src, true);
	}

	private String bytesToHexString(byte[] src, boolean isPrefix) {
		StringBuilder stringBuilder = new StringBuilder();
		if (isPrefix == true) {
			stringBuilder.append("0x");
		}
		if (src == null || src.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.toUpperCase(Character.forDigit(
					(src[i] >>> 4) & 0x0F, 16));
			buffer[1] = Character.toUpperCase(Character.forDigit(src[i] & 0x0F,
					16));
			System.out.println(buffer);
			stringBuilder.append(buffer);
		}
		return stringBuilder.toString();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		if (isNFC_support == true) {
			stopNFC_Listener();
		}

		if (isNFC_support == false)
			return;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (isNFC_support == false)
			return;

		startNFC_Listener();

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(this.getIntent()
				.getAction())) {
			// 处理该intent
			processIntent(this.getIntent());
		}
	}

	private Tag tagFromIntent;

	/**
	 * Parses the NDEF Message from the intent and prints to the TextView
	 */
	public void processIntent(Intent intent) {
		if (isNFC_support == false)
			return;

		// 取出封装在intent中的TAG
		tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		promt.setTextColor(Color.BLUE);
		String metaInfo = "";
		metaInfo += "卡片ID：" + bytesToHexString(tagFromIntent.getId()) + "\n";
		Toast.makeText(this, "找到卡片", Toast.LENGTH_SHORT).show();

		// Tech List
//		String prefix = "android.nfc.tech.";
		String[] techList = tagFromIntent.getTechList();

		// Mifare Classic/UltraLight Info
		String CardType = "";
		for (int i = 0; i < techList.length; i++) {
			if (techList[i].equals(NfcA.class.getName())) {
				try {
					if ("".equals(CardType))
						CardType = "MifareClassic卡片类型 \n 不支持NDEF消息 \n";
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (techList[i].equals(MifareUltralight.class.getName())) {
				MifareUltralight mifareUlTag = MifareUltralight
						.get(tagFromIntent);
				String lightType = "";
				// Type Info
				switch (mifareUlTag.getType()) {
				case MifareUltralight.TYPE_ULTRALIGHT:
					lightType = "Ultralight";
					break;
				case MifareUltralight.TYPE_ULTRALIGHT_C:
					lightType = "Ultralight C";
					break;
				}
				CardType = lightType + "卡片类型\n";

				Ndef ndef = Ndef.get(tagFromIntent);
				CardType += "最大数据尺寸:" + ndef.getMaxSize() + "\n";

			}
		}
		metaInfo += CardType;
		promt.setText(metaInfo);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);

		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			processIntent(intent);
		}
	}
	//读取方法
	private String read(Tag tag) throws IOException, FormatException {
		if (tag != null) {
			// Get an instance of Ndef for the tag.
			Ndef ndef = Ndef.get(tag);
			// Enable I/O
			ndef.connect();
			NdefMessage message = ndef.getNdefMessage();
			// Write the message
			byte[] data = message.toByteArray();
			String str = new String(data, Charset.forName("UTF-8")).substring(3);
//			Log.e("adsfas", new String(data, Charset.forName("UTF-8")).substring(3));
			// Close the connection
			ndef.close();
			
			return str;
		} else {
			Toast.makeText(NfcDemoActivity.this, "设备与nfc卡连接断开，请重新连接...",
					Toast.LENGTH_SHORT).show();
		}
		return null;
	}
//写入方法
	private void write(Tag tag) throws IOException, FormatException {
		if (tag != null) {
			NdefRecord[] records = { createRecord() };
			if(records[0]!=null){
				NdefMessage message = new NdefMessage(records);
				// Get an instance of Ndef for the tag.
				Ndef ndef = Ndef.get(tag);
				// Enable I/O
				ndef.connect();
				// Write the message
				ndef.writeNdefMessage(message);
				// Close the connection
				ndef.close();
				promt.setText(promt.getText() + "写入数据成功！" + "\n");
			}
			else{
				Toast.makeText(NfcDemoActivity.this, "请输入内容信息。。。",
						Toast.LENGTH_SHORT).show();
			}
			
		} else {
			Toast.makeText(NfcDemoActivity.this, "设备与nfc卡连接断开，请重新连接...",
					Toast.LENGTH_SHORT).show();
		}
	}
	//删除方法
	private void delete(Tag tag) throws IOException, FormatException {
		if (tag != null) {
			NdefRecord[] records = { new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
					null,null, null) };
			NdefMessage message = new NdefMessage(records);
			// Get an instance of Ndef for the tag.
			Ndef ndef = Ndef.get(tag);
			// Enable I/O
			ndef.connect();
			// Write the message
			ndef.writeNdefMessage(message);
			// Close the connection
			ndef.close();
			promt.setText(promt.getText() + "删除数据成功！" + "\n");
		} else {
			Toast.makeText(NfcDemoActivity.this, "设备与nfc卡连接断开，请重新连接...",
					Toast.LENGTH_SHORT).show();
		}
	}

	private NdefRecord createRecord() throws UnsupportedEncodingException {
		String msg=out_content.getText().toString();
		if(msg!=null&&!msg.equals("")){
			byte[] textBytes = msg.getBytes();
			NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
					null,null, textBytes);
			return textRecord;
		}
		return null;
		
	}

	@SuppressWarnings("unused")
	private MediaPlayer ring() throws Exception, IOException {
		// TODO Auto-generated method stub
		Uri alert = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		MediaPlayer player = new MediaPlayer();
		player.setDataSource(this, alert);
		final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
			player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			player.setLooping(false);
			player.prepare();
			player.start();
		}
		return player;
	}

	private void startNFC_Listener() {
		nfcAdapter.enableForegroundDispatch(this, pi,
				new IntentFilter[] { tagDetected }, null);
	}

	private void stopNFC_Listener() {
		nfcAdapter.disableForegroundDispatch(this);
	}

	private void init_NFC() {
		pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
	}
}
