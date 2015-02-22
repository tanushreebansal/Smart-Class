package com.example.SmartNotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.widget.Toast;

public class NFCBeam extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback
{
	private String uid;
	private TextView tv;
	private NfcAdapter mNfcAdapter;
	private static final int MESSAGE_SENT = 1;
	private static final String MIME_TYPE = "text/plain";
	private static final String PACKAGE_NAME = "com.example.SmartNotes";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
			 super.onCreate(savedInstanceState);
			 setContentView(R.layout.nfc_beam);
			
			 Intent in = getIntent();
		     uid = (String) in.getCharSequenceExtra("uid");
			 tv = (TextView) findViewById(R.id.textView1);
			 tv.setGravity(Gravity.CENTER_HORIZONTAL);
			 tv.setHint("Tap an NFC enabled device to send the following data:\n\n"+uid);
			 
			// Check for available NFC Adapter
			mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
			if (mNfcAdapter == null) 
			{
				Toast.makeText(this, "Sorry, NFC is not available on this device", Toast.LENGTH_SHORT).show();
			} 
			else 
			{
				// Register callback to set NDEF message
				mNfcAdapter.setNdefPushMessageCallback(this, this);
				// Register callback to listen for message-sent success
				mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
			}
		
		 	
	}
	
	
	/**
	 * Implementation for the CreateNdefMessageCallback interface
	 */
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) 
	{
		String text = uid;
		NdefMessage msg = new NdefMessage(new NdefRecord[] { NfcUtils.createRecord(MIME_TYPE, text.getBytes()),
		NdefRecord.createApplicationRecord(PACKAGE_NAME) });
		return msg;
	}

	

	/** This handler receives a message from onNdefPushComplete */
	private final Handler mHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch (msg.what) {
			case MESSAGE_SENT:
				Toast.makeText(getApplicationContext(), "Data sent successfully", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};
	
	
	/**
	 * Implementation for the OnNdefPushCompleteCallback interface
	 */
	@Override
	public void onNdefPushComplete(NfcEvent arg0)
	{
		// A handler is needed to send messages to the activity when this
		// callback occurs, because it happens from a binder thread
		mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	@Override
	public void onNewIntent(Intent intent) 
	{
		// onResume gets called after this to handle the intent
		setIntent(intent);
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		// Check to see that the Activity started due to an Android Beam
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) 
		{
			processIntent(getIntent());
		}
	}

	/**
	 * Parses the NDEF Message from the intent and toast to the user
	 */
	void processIntent(Intent intent) 
	{
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// in this context, only one message was sent over beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		String payload = new String(msg.getRecords()[0].getPayload());
		//Toast.makeText(getApplicationContext(), "Message received over beam: " + payload, Toast.LENGTH_LONG).show();
		 tv.setHint("Message received over beam: " + payload);
		
	}

}
