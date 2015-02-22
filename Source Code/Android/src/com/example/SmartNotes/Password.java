package com.example.SmartNotes;


import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Password extends Activity 
{
	private String oldP,newP,cPass;
	private DefaultHttpClient httpclient;
	private HttpPost httppost;
	private ArrayList<NameValuePair> nameValuePairs;
	private String sid;
	private HttpResponse response;

	public void onCreate(Bundle savedInstanceState) 
	{
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.password);
	        Intent in = getIntent();
	        sid = (String) in.getCharSequenceExtra("uid");
	        final Button btn_save = (Button)findViewById(R.id.button1);
		    btn_save.setOnClickListener(new OnClickListener()
	        {
	        	public void onClick(View v)
	        	{
	        		TextView oldpass = (TextView)findViewById(R.id.editText1);
	        		TextView newpass = (TextView)findViewById(R.id.editText2);
	        		TextView confirmpass = (TextView)findViewById(R.id.editText3);
	        		 oldP = oldpass.getText().toString().trim();
	                 newP = newpass.getText().toString().trim();
	                 cPass = confirmpass.getText().toString().trim();
	               
	                 if(oldP.equals(""))oldpass.setError("Please enter your existing password");
	                 if(newP.equals(""))newpass.setError("Please enter a new password");
	                 if(cPass.equals(""))confirmpass.setError("Please retype your new password");
	                 if(!oldP.equals("") && !newP.equals("")&& !cPass.equals(""))
	                 {
		       		     if(oldP.equals(newP))
		       		     {
		       		    	Toast toast = Toast.makeText(Password.this, "New password same as current. Pick a different password", Toast.LENGTH_SHORT);
		       		    	TextView vt = (TextView)toast.getView().findViewById(android.R.id.message);
		       		    	if( vt != null) vt.setGravity(Gravity.CENTER);
		       		    	toast.show();
		       		    	//Toast.makeText(Password.this,"New password same as current. Pick a different password", Toast.LENGTH_SHORT).show();
		       		     }
		       		     if(!cPass.equals(newP))
		       		    	Toast.makeText(Password.this,"Passwords do not match", Toast.LENGTH_SHORT).show();
	         	 
		       		     if(!oldP.equals(newP) && cPass.equals(newP) )
		       		     {
		       		    	new PasswordTask().execute();
		       		    	//Toast.makeText(Password.this,"Password change successful", Toast.LENGTH_SHORT).show();
		       		     }
		       	     }
	          	}
	        });
	    
	 }
	
	void passwordChange()
    {
        try{            
        	                   	       
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);
            httpclient = new DefaultHttpClient(httpParameters);
            httppost= new HttpPost("http://192.168.11.1/Pervasive_Project/passwordchange.php/"); 
            nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("sid",sid));  
            nameValuePairs.add(new BasicNameValuePair("oldpass",oldP)); 
            nameValuePairs.add(new BasicNameValuePair("newpass",newP));  
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response=httpclient.execute(httppost);             
            if(response.getStatusLine().getStatusCode()== 200)
            {
                    runOnUiThread(new Runnable() 
                    {
                        public void run() 
                        {
                          	 Toast.makeText(Password.this,"Password changed successfully!",Toast.LENGTH_LONG).show();
                        }
                    });
                
            } 
            else
            {
            	showAlertT(); 
            }
        }
           catch (Exception e)
           {
        	showAlertT(); 
           }
       
    }
	 public void showAlertT()
	    {
	        Password.this.runOnUiThread(new Runnable()
	        {
	            public void run() 
	            {
	                AlertDialog.Builder builder = new AlertDialog.Builder(Password.this);
	                builder.setTitle("Network Error");
	                builder.setMessage("Server not responding")  
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
	 
   	 private class PasswordTask extends AsyncTask<String, Integer, Double>
	    {
	  	  @Override
	  	  protected Double doInBackground(String... params) 
	  	  {
	  		passwordChange();
	  		return null;
	  	  }
	  	}
	    
}
