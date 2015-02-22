package com.example.SmartNotes;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.SmartNotes.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SmartNotes extends Activity 
{
    private ProgressDialog dialog;
    private HttpPost httppost;
    private HttpResponse response;
    private HttpClient httpclient;
    private HttpEntity entity;
    private List<NameValuePair> nameValuePairs;
    private TextView username,password;
       private String uname=null,pass=null,send=null,role="teacher";
	private String retCcode;
	private String retCname;   
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final Button btn_save = (Button)findViewById(R.id.button1);
	    btn_save.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v)
        	{
        		username =(TextView)findViewById(R.id.editText1);
        		password =(TextView)findViewById(R.id.editText2);
        		 uname = username.getText().toString().trim();
                 pass = password.getText().toString().trim();
                 if(uname.matches(".*\\d.*"))
                 {
               	  role = "student";
                 }
                 if(uname.equals(""))
                 {
                      
                      	username.setError("Please enter username");
                 }
                 if(pass.equals(""))
                 {
                	
                	 password.setError("Please enter password");
                 }
                 
                 if(!uname.equals("") && !pass.equals(""))
                 {
	       		dialog = ProgressDialog.show(SmartNotes.this, "Smart Notes","Validating User...", true);
         	    new MyAsyncTask().execute();
                 }
          	}
        });
    }
    
    
    void login()
    {
        try{            
        	Calendar cal = Calendar.getInstance();
          	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        	int currentDay = cal.get(Calendar.DAY_OF_WEEK);
        	String day = Integer.toString(currentDay);
        	        	           
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);
            httpclient = new DefaultHttpClient(httpParameters);
            httppost= new HttpPost("http://192.168.11.1/Pervasive_Project/login.php/"); 
            nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("username",uname));  
            nameValuePairs.add(new BasicNameValuePair("password",pass)); 
            nameValuePairs.add(new BasicNameValuePair("time", sdf.format(cal.getTime()))); 
            nameValuePairs.add(new BasicNameValuePair("day",day)); 
           
            
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response=httpclient.execute(httppost);
             
            if(response.getStatusLine().getStatusCode()== 200)
            {
                 entity = response.getEntity();
                 if(entity != null)
                 {
                    InputStream instream = entity.getContent();
                    JSONObject jsonResponse = new JSONObject(convertStreamToString(instream));
                                
                    String retUid = jsonResponse.getString("sid");
                    String retUname = jsonResponse.getString("sname");
                    
                   if(jsonResponse.getString("op").equalsIgnoreCase("valid"))
                    {
                    	retCname = jsonResponse.getString("course");
                    	retCcode = jsonResponse.getString("code");
                    }
                    if(uname.equals(retUid))
                    {
                        SharedPreferences sp = getSharedPreferences("logindetails", 0);
                        SharedPreferences.Editor spedit = sp.edit();
                        spedit.putString("user", uname);
                        spedit.putString("pass", pass);
                        spedit.commit();

                        runOnUiThread(new Runnable() 
                        {
                            public void run() 
                            {
                                Toast.makeText(SmartNotes.this,"Login Successful", Toast.LENGTH_SHORT).show();
                            }
                        });
                        
                    
                    
                       if(jsonResponse.getString("op").equalsIgnoreCase("valid"))
                        	send ="direct"+"%"+role+"%"+ retUname+"%"+uname+"%"+retCcode+"%"+retCname;
                       else
                        	 send = "direct"+"%"+role+"%" +retUname+"%"+uname;
                        startActivity(new Intent(SmartNotes.this,LoginSuccess.class).putExtra("data",(CharSequence)send));
                    } 
                  
                 }
            }
        } 
       catch(JSONException e)
       {
    	   dialog.dismiss();
       	showAlert();
       }
        catch(Exception e)
        {
        	dialog.dismiss();
        	// Toast.makeText(SmartNotes.this,e.getStackTrace().toString(), Toast.LENGTH_SHORT).show();
        		showAlertT();   
        }
    }

    public void showAlert()
    {
        SmartNotes.this.runOnUiThread(new Runnable()
        {
            public void run() 
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(SmartNotes.this);
                builder.setTitle("Login Error");
                builder.setMessage("Invalid Username or password")  
                       .setCancelable(false)
                       .setPositiveButton("OK", new DialogInterface.OnClickListener() 
                       {
                           public void onClick(DialogInterface dialog, int id) {
                       }
                       });                     
                AlertDialog alert = builder.create();
                alert.show();               
            }
        });
     }
    
    public void showAlertT()
    {
        SmartNotes.this.runOnUiThread(new Runnable()
        {
            public void run() 
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(SmartNotes.this);
                builder.setTitle("Timeout");
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
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {

        return;
    }
    public static String convertStreamToString(InputStream is) 
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
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Double>
    {
  	  @Override
  	  protected Double doInBackground(String... params) 
  	  {
  		login();
  		return null;
  	  }
  	}
}