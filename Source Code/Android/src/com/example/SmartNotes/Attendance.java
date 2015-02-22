package com.example.SmartNotes;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Attendance extends Activity 
{
    	public static final String MIME_TEXT_PLAIN = "text/plain";
	//public static final String MIME_BEAM = "application/com.example.SmartNotes";
	public static final String TAG = "NfcDemo";
	private EditText mEditText;
	private NfcAdapter mNfcAdapter;
	private String sid,sname,degree,image,course_code;
	private ImageButton goButton;
	private TextView tv;
	private HttpPost httppost;
	private HttpResponse response;
    	private HttpClient httpclient;
    	private HttpEntity entity;
    	private List<NameValuePair> nameValuePairs;
    	private  JSONObject jsonResponse;
    	private ImageButton markButton;
    	private  RadioGroup radioGroup;
    	private  String radioButtonSelected="tag";
    	private  int checkedRadioButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attendance);
		Intent in = getIntent();
		course_code = (String) in.getCharSequenceExtra("code");
		tv = (TextView) findViewById(R.id.textView1);
		
		 mEditText = (EditText) findViewById(R.id.idno);
		 mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		 addButtonListener();

		if (mNfcAdapter == null) 
		{
			Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
			
		}
	
		if (!mNfcAdapter.isEnabled()) 
		{
			Toast.makeText(this, "NFC Disabled", Toast.LENGTH_LONG).show();
		} 
		
		
		handleIntent(getIntent());
	}

	public void addButtonListener() {
		 
		        goButton = (ImageButton) findViewById(R.id.button1);
		        markButton = (ImageButton) findViewById(R.id.button3);
		        markButton.setVisibility(View.GONE);
		    	markButton.setBackgroundResource(R.drawable.tick);
		        goButton.setOnClickListener(new OnClickListener() 
			{
		            @Override
		            public void onClick(View view) 
			    {
		
		            	sid =mEditText.getText().toString().trim().toUpperCase();
		            	if(sid.equals(""))
		            		tv.setText("Enter ID no. to proceed");
		            	else
		            	{
		            		 markButton.setBackgroundResource(R.drawable.tick);
		            		 new AttendanceTask().execute();
		            	}
		        		
		            	
		            }
		
		        });
		        
		        markButton.setOnClickListener(new OnClickListener() 
		       {
		            @Override
		            public void onClick(View view) 
                            {
		            	markButton.setBackgroundResource(R.drawable.tick_pressed);
		            	new MarkAttendanceTask().execute();		           
		            }
		
		        });
		        
		        
		      
		    }

	void markAttendance( final int flag )
        {
        	try{            
        	  	java.util.Date utilDate = new java.util.Date();
      	    		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                   	       
            		HttpParams httpParameters = new BasicHttpParams();
            		HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
            		HttpConnectionParams.setSoTimeout(httpParameters, 5000);
            		httpclient = new DefaultHttpClient(httpParameters);
            		if(flag == 1)
            			httppost= new HttpPost("http://192.168.11.1/Pervasive_Project/attendance.php/"); 
            		else if(flag==2)
            	 		httppost= new HttpPost("http://192.168.11.1/Pervasive_Project/markattend.php/"); 
            
	            	nameValuePairs = new ArrayList<NameValuePair>(3);
            		nameValuePairs.add(new BasicNameValuePair("sid",sid));  
            		nameValuePairs.add(new BasicNameValuePair("date",sqlDate.toString())); 
            		nameValuePairs.add(new BasicNameValuePair("code",course_code));  
            		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
           
            		response=httpclient.execute(httppost);
             
            		if(response.getStatusLine().getStatusCode()== 200)
            		{
                 		entity = response.getEntity();
                 		if(entity != null)
                 		{
                    			InputStream instream = entity.getContent();
                    			jsonResponse = new JSONObject(convertStreamToString(instream));
                    			if(flag == 1)
                    			{
	                    			sname = jsonResponse.getString("sname");
			            		degree = jsonResponse.getString("degree");
			            		image = jsonResponse.getString("image");
			             
			            		runOnUiThread(new Runnable() 
			            		{
			                 		public void run() 
			                 		{
			                       			tv.setText(sname+"\n"+degree+"\n");
			                       			markButton.setVisibility(View.VISIBLE);
			                       			markButton.setBackgroundResource(R.drawable.tick);
			                  		}
			             		});
			             		byte[] image_data = Base64.decode(image, Base64.DEFAULT);
			                        ByteArrayInputStream imageStream = new ByteArrayInputStream(image_data);
			             		Bitmap theImage = BitmapFactory.decodeStream(imageStream);
			             		((ImageView)findViewById(R.id.imageView1)).setImageBitmap(theImage);
                    			}
                  
                 		}
            		}
                                    
        	} 
        	catch(ConnectTimeoutException e)
        	{
        		showAlertT(); 
        	}
        	catch(SocketTimeoutException e)
        	{
    			showAlertT(); 
        	}
        	catch (ClientProtocolException e) 
        	{
    			showAlertT(); 
        	}
         	catch (JSONException e) 
        	{ 
        	   Attendance.this.runOnUiThread(new Runnable()
                   {
                    	public void run() 
                    	{
                   		tv.setText("No such record found");
                     		markButton.setVisibility(View.GONE);
                    	}
                   });      	
           	
        	  Attendance.this.runOnUiThread(new Runnable()
                  {
                    	public void run() 
                    	{
                    		((ImageView)findViewById(R.id.imageView1)).setImageDrawable(null);
                    	}
                   });      		
           	  return;
        } 
        catch (IOException e) 
	{
        	e.printStackTrace();
	}
        catch (Exception e)
	{
        	e.printStackTrace();
        }
       
    }
	
	
	 private static String convertStreamToString(InputStream is) 
	 {
	        /*
	         * To convert the InputStream to String we use the BufferedReader.readLine()
	         * method. We iterate until the BufferedReader return null which means
	         * there's no more data to read. Each line will appended to a StringBuilder
	         * and returned as String.
	         */
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();

	        String line = null;
	        try {
	            while ((line = reader.readLine()) != null) 
		    {
	                sb.append(line + "\n");
	            }
	        } 
		catch (IOException e) 
		{
	            e.printStackTrace();
	        } 
		finally 
		{
	            try 
		    {
	                is.close();
	            } 
		    catch (IOException e) 
		    {
	                e.printStackTrace();
	            }
	        }
	        return sb.toString();
	    }
	 
	 public void showAlertT()
	    {
	        Attendance.this.runOnUiThread(new Runnable()
	        {
	            public void run() 
	            {
	                AlertDialog.Builder builder = new AlertDialog.Builder(Attendance.this);
	                builder.setTitle("");
	                builder.setMessage("Network Error")  
	                       .setCancelable(false)
	                       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                           public void onClick(DialogInterface dialog, int id) {
	                           }
	                       });                     
	                AlertDialog alert = builder.create();
	                alert.show();               
	            }
	        });
	        
	    
	    }
	 
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		
		/*
		 * It's important, that the activity is in the foreground (resumed). Otherwise
		 * an IllegalStateException is thrown. 
		 */
		setupForegroundDispatch(this, mNfcAdapter);
	}
	
	@Override
	protected void onPause() 
	{
		/*
		 * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
		 */
		stopForegroundDispatch(this, mNfcAdapter);
		super.onPause();
	}
	
	@Override
	protected void onNewIntent(Intent intent) 
	{
		/*
		 * This method gets called, when a new Intent gets associated with the current activity instance.
		 * Instead of creating a new activity, onNewIntent will be called. For more information have a look
		 * at the documentation.
		 * 
		 * In our case this method gets called, when the user attaches a Tag to the device.
		 */
		handleIntent(intent);
	
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
		mEditText.setText(payload);
		
	}
	
	private void handleIntent(Intent intent) 
	{
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) 
		{
			
			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) 
			{
				 radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
				 checkedRadioButton = radioGroup.getCheckedRadioButtonId();
				 			  
				 switch (checkedRadioButton) 
				 {
				   case R.id.radio0 : radioButtonSelected = "tag";
				                    	              break;
				   case R.id.radio1 : radioButtonSelected = "beam";
				 		                      break;
				   
				 }
				if(radioButtonSelected.startsWith("tag"))
				{
					Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
					new NdefReaderTask().execute(tag);
				}
				else 
				processIntent(intent);
				
				
			} 
		
			else 
			{
				Log.d(TAG, "Wrong mime type: " + type);
			}
		} 
		
		else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) 
		{
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String[] techList = tag.getTechList();
			String searchedTech = Ndef.class.getName();
			for (String tech : techList) 
			{
				if (searchedTech.equals(tech)) 
				{
					new NdefReaderTask().execute(tag);
					break;
				}
			}
		}
	}
	
	
	/**
	 * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
	 * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
	 */
	public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) 
	{
		final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

		IntentFilter[] filters = new IntentFilter[2];
		String[][] techList = new String[][]{};

		filters[0] = new IntentFilter();
		filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
		filters[0].addCategory(Intent.CATEGORY_DEFAULT);
		filters[1] = new IntentFilter();
		filters[1].addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
		filters[1].addCategory(Intent.CATEGORY_DEFAULT);
		try 
		{
			filters[0].addDataType(MIME_TEXT_PLAIN);
			filters[1].addDataType(MIME_TEXT_PLAIN);
		} 
		catch (MalformedMimeTypeException e) 
		{
			throw new RuntimeException("Check your mime type.");
		}
		
		adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
	}

	/**
	 * @param activity The corresponding {@link BaseActivity} requesting to stop the foreground dispatch.
	 * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
	 */
	public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) 
	{
		adapter.disableForegroundDispatch(activity);
	}
	 private class AttendanceTask extends AsyncTask<String, Integer, Double>
	    {
	  	  @Override
	  	  protected Double doInBackground(String... params) 
	  	  {
	  		markAttendance(1);
	  		return null;
	  	  }
	  	}
	 
	 private class MarkAttendanceTask extends AsyncTask<String, Integer, Double>
	    {
	  	  @Override
	  	  protected Double doInBackground(String... params) 
	  	  {
	  		markAttendance(2);
	  		return null;
	  	  }
	  	}
	
	/**
	 * Background task for reading the data. Do not block the UI thread while reading. 
	 *
	 */
	
	private class NdefReaderTask extends AsyncTask<Tag, Void, String> 
	{
		@Override
		protected String doInBackground(Tag... params) 
		{
			Tag tag = params[0];
			Ndef ndef = Ndef.get(tag);
			if (ndef == null) 
			{
				// NDEF is not supported by this Tag. 
				return null;
			}

			NdefMessage ndefMessage = ndef.getCachedNdefMessage();
			NdefRecord[] records = ndefMessage.getRecords();
			for (NdefRecord ndefRecord : records) 
			{
				if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) 
				{
					try 
					{
						return readText(ndefRecord);
					} 
					catch (UnsupportedEncodingException e) 
					{
						Log.e(TAG, "Unsupported Encoding", e);
					}
				}
			}
			return null;
		}
		
		private String readText(NdefRecord record) throws UnsupportedEncodingException 
		{
			byte[] payload = record.getPayload();

			// Get the Text Encoding
			String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

			// Get the Language Code
			int languageCodeLength = payload[0] & 0063;
			
			// Get the Text
			return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
		}
		
		@Override
		protected void onPostExecute(String result) 
		{
			if (result != null) 
			{
				mEditText.setText(result);
			}
		}
		
		
	}
	
}
