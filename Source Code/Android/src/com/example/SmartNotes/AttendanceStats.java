package com.example.SmartNotes;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class AttendanceStats extends Activity 
{
	private TextView setmsg,stats,datetv;
	private String stuTotal,stuPresent,code,inData,role,uid;
	private int absent;
	private float percentPresent;
	private DefaultHttpClient httpclient;
	private HttpPost httppost;
	private ArrayList<NameValuePair> nameValuePairs;
	private HttpResponse response;
	private HttpEntity entity;
	
	
	
	
	void getAttendanceStats()
    {
        try{            
        	java.util.Date utilDate = new java.util.Date();
        	java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());       	           
            
        	HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);
            httpclient = new DefaultHttpClient(httpParameters);
            httppost= new HttpPost("http://192.168.11.1/Pervasive_Project/stats.php/"); 
            nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("code",code));  
            nameValuePairs.add(new BasicNameValuePair("uid",uid));  
            nameValuePairs.add(new BasicNameValuePair("date",sqlDate.toString())); 
          
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
             
            if(response.getStatusLine().getStatusCode()== 200)
            {
                 entity = response.getEntity();
                 if(entity != null)
                 {
                    InputStream instream = entity.getContent();
                    JSONObject jsonResponse = new JSONObject(SmartNotes.convertStreamToString(instream));
                                
                    stuTotal = jsonResponse.getString("total");
                    stuPresent = jsonResponse.getString("present");
                    
                    absent = Integer.parseInt(stuTotal)-Integer.parseInt(stuPresent);
	  		 	    float total = Float.parseFloat(stuTotal);
  		 	        float present = Float.parseFloat(stuPresent);
  		 	        percentPresent = (present/total)*100;
  		 	     percentPresent=(float)Math.round(percentPresent * 100) / 100;
  		 	  
  		 	     runOnUiThread(new Runnable() 
		            {
		                 public void run() 
		                 {
		                	 if (role.startsWith("teacher"))
		                	 stats.setText("Total students enrolled : " +stuTotal+"\nPresent : "+stuPresent+"\nAbsent : "+absent+"\n\n\n% of students present = "+ percentPresent+"%");
		                	else
		                	stats.setText("Total classes held : " +stuTotal+"\nPresent : "+stuPresent+"\nAbsent : "+absent+"\n\n\n% of classes attended = "+ percentPresent+"%");
		                  }
		             });
                  
                  
                 } 
                  
             }
            
        } 
         catch(Exception e)
        {
        	  runOnUiThread(new Runnable() 
	            {
	                 public void run() 
	                 {
	                	 Toast.makeText(AttendanceStats.this,"Error occurred while fetching data", Toast.LENGTH_SHORT).show();
	                  }
	             });
        	
        		
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats);
		
		setmsg = (TextView)findViewById(R.id.textView2);
		stats = (TextView)findViewById(R.id.textView3);
		datetv = (TextView)findViewById(R.id.textView4);
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		
		datetv.setText("\n"+dateFormat.format(date).toString());
		Intent in = getIntent();
	    inData = (String) in.getCharSequenceExtra("title");
	    StringTokenizer st = new StringTokenizer(inData,":");
	    role = st.nextToken();
	    uid = st.nextToken();
    	code = st.nextToken().trim();
    	String cname = st.nextToken();
    
	         if (inData != null)
	        {
	   	        	if(code.startsWith("Error"))
	  	        		 setmsg.setText("\nNo course is currently running.\n");
	  	        	else
	  	        	{
	  	        		   		
	  		 	        setmsg.setPaintFlags(setmsg.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
	  	        		setmsg.setText("\n"+code+" : "+cname+"\n\n");
	  	        		 new getStatsTask().execute();
	  	    	      
	  	    	     
	  	        		
	  	        	}
	        }
	}
	
	

	 private class getStatsTask extends AsyncTask<String, Integer, Double>
    {
	  	  @Override
	  	  protected Double doInBackground(String... params) 
	  	  {
	  		getAttendanceStats();
	  		return null;
	  	  }
	  	  
	  	
	  }
	 
}
