package com.example.SmartNotes;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
import com.example.SmartNotes.R;
import com.example.SmartNotes.RowItem;
import com.example.SmartNotes.CustomListViewAdapter;
import com.example.SmartNotes.LoginSuccess;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginSuccess extends Activity 
{
		private String uname,code=null,course,uid,title,role,courseCodeStr,courseNameStr,intent;
		private DrawerLayout drawerLayout;
	    private ListView drawerListView;
	    private  AlertDialog alert;
	    private ActionBarDrawerToggle actionBarDrawerToggle;
	    public static final String[] titles = new String[] { "Courses","Attendance Stats", "Change Password", "Logout" };
	    public static final Integer[] images = { R.drawable.ic_menu_database, R.drawable.ic_menu_chart, R.drawable.ic_menu_settings, R.drawable.ic_menu_exit };
	    ListView listView;
	    List<RowItem> rowItems;
		private DefaultHttpClient httpclient;
		private HttpPost httppost;
		private ArrayList<NameValuePair> nameValuePairs;
		private HttpResponse response;
		private HttpEntity entity;
		
	   
	    
	    
	    void getCourses()
	    {
	        try{            
	        	 HttpParams httpParameters = new BasicHttpParams();
	            HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
	            HttpConnectionParams.setSoTimeout(httpParameters, 5000);
	            httpclient = new DefaultHttpClient(httpParameters);
	            httppost= new HttpPost("http://192.168.11.1/Pervasive_Project/courselist.php/"); 
	            nameValuePairs = new ArrayList<NameValuePair>(1);
	            nameValuePairs.add(new BasicNameValuePair("uid",uid));  
	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            response=httpclient.execute(httppost);
	             
	            if(response.getStatusLine().getStatusCode()== 200)
	            {
	                 entity = response.getEntity();
	                 if(entity != null)
	                 {
	                    InputStream instream = entity.getContent();
	                    JSONObject jsonResponse = new JSONObject(SmartNotes.convertStreamToString(instream));
	                                
	                    courseCodeStr = jsonResponse.getString("course_no");
	                    courseNameStr = jsonResponse.getString("course_name");
	                    
	                 }                                     
	              }
	            
	        } 
	      
	        catch(Exception e)
	        {
	        	 runOnUiThread(new Runnable() 
                 {
                     public void run() 
                     {
                         Toast.makeText(LoginSuccess.this,"An error occurred while fetching data.", Toast.LENGTH_SHORT).show();
                     }
                 }); 
	        }
	    }
   
    /** Called when the activity is first created. */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
	        super.onCreate(savedInstanceState); 
	        setContentView(R.layout.second);
	      
	        final Button lecture = (Button)findViewById(R.id.button1);
	        final Button attendance = (Button)findViewById(R.id.button2);
	        final Button nfcBeam = (Button)findViewById(R.id.button3);
	        nfcBeam.setVisibility(View.GONE);
		    attendance.setOnClickListener(new OnClickListener()
	        {
	        	public void onClick(View v)
	        	{
	        		
	        		  startActivity(new Intent(LoginSuccess.this,Attendance.class).putExtra("code",(CharSequence)code));
	          	}
	        }
	       );
		    lecture.setOnClickListener(new OnClickListener()
	        {
	        	public void onClick(View v)
	        	{
	        		
	        		 startActivity(new Intent(LoginSuccess.this,LiveLectures.class).putExtra("code",(CharSequence)code+"%"+uid));
	          	}
	        }
	       );
		    nfcBeam.setOnClickListener(new OnClickListener()
	        {
	        	public void onClick(View v)
	        	{
	        		
	        		 startActivity(new Intent(LoginSuccess.this,NFCBeam.class).putExtra("uid",(CharSequence)uid));
	          	}
	        }
	       );
	        
	        
	        Intent in = getIntent();
	        String inData = (String) in.getCharSequenceExtra("data");
	        
	        if (inData != null)
	        {
	        	final TextView setmsg = (TextView)findViewById(R.id.showmsg);
	        	StringTokenizer st = new StringTokenizer(inData,"%");
	        	intent = st.nextToken();
	        	role = st.nextToken();
	        	uname = st.nextToken();
	        	uid = st.nextToken();
	        	
	        	if(st.hasMoreTokens())
	        	{
	        	code = st.nextToken();
	        	course = st.nextToken();
	        	if(intent.startsWith("direct"))
	        	{
	        	 AudioManager audio_mngr = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
  	        	audio_mngr .setRingerMode(AudioManager.RINGER_MODE_SILENT);
  	        	 Toast.makeText(getApplicationContext(),"Silent mode activated", Toast.LENGTH_SHORT).show();
	        	}
	        	title = code+" : "+course;
	        	
	        	 runOnUiThread(new Runnable() 
		            {
		                 public void run() 
		                 {
		                	 if(intent.startsWith("direct"))
		                	 setmsg.setText("\nWelcome "+uname+"!\n\n\n"+code+" : "+course+"\n\n\n");
		                	 else
		                		 setmsg.setText("\n\n\n"+code+" : "+course+"\n\n\n\n");
		                	if(role.startsWith("student"))
		                	{
		                		attendance.setVisibility(View.GONE);
		                		  nfcBeam.setVisibility(View.VISIBLE);
		                	}
		                	
		                		 
		                  }
		             });
	        	
	        	}
	        	else
	        	{
	        		title = "Error:Error";
	        		if(intent.startsWith("direct"))
	        		setmsg.setText("\nWelcome "+uname+"!\n\nNo courses are scheduled currently.Click on Courses from the navigation drawer to view a complete list of courses.\n");
	        		else
	        			setmsg.setText("\n\nNo courses are scheduled currently.Click on Courses from the navigation drawer to view a complete list of courses.\n");	
	        		attendance.setVisibility(View.GONE);
                    lecture.setVisibility(View.GONE);
	        	}
	        }
	        
	        rowItems = new ArrayList<RowItem>();
	        for (int i = 0; i < titles.length; i++) {
	            RowItem item = new RowItem(images[i], titles[i]);
	            rowItems.add(item);
	        }
	 
	        drawerListView = (ListView) findViewById(R.id.left_drawer);
	        CustomListViewAdapter adapter = new CustomListViewAdapter(this,
	                R.layout.list_item, rowItems);
	        drawerListView.setAdapter(adapter);
	        
	   
			
			
			
			// 2. App Icon 
			drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

			// 2.1 create ActionBarDrawerToggle
			actionBarDrawerToggle = new ActionBarDrawerToggle(
	                this,                  /* host Activity */
	                drawerLayout,         /* DrawerLayout object */
	                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
	                R.string.drawer_open,  /* "open drawer" description */
	                R.string.drawer_close  /* "close drawer" description */
	                );

	        // 2.2 Set actionBarDrawerToggle as the DrawerListener
	        drawerLayout.setDrawerListener(actionBarDrawerToggle);
	        
	        // 2.3 enable and show "up" arrow
	        getActionBar().setDisplayHomeAsUpEnabled(true); 
	        
	        // just styling option
			drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
			
			drawerListView.setOnItemClickListener(new DrawerItemClickListener());
			
				new MyAsyncTask().execute();
			
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
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
         actionBarDrawerToggle.syncState();
    }

	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		 // call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
        // then it has handled the app icon touch event

		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	    {
	    	 if(position == 0)
			    {	
			    		drawerLayout.closeDrawer(drawerListView);
			    	    startActivity(new Intent(LoginSuccess.this,CourseList.class).putExtra("data",(CharSequence)intent+"%"+role+"%"+uid+"%"+uname+"%"+courseCodeStr+"%"+courseNameStr));
			    		
			    
			    }
	    	
	    	 else if(position == 1)
		    {	
		    		drawerLayout.closeDrawer(drawerListView);
		    		
		    	
		    			startActivity(new Intent(LoginSuccess.this,AttendanceStats.class).putExtra("title",(CharSequence)role+":"+uid+":"+title));
		    }
	        else if(position == 2)
	    	{	
	    		drawerLayout.closeDrawer(drawerListView);
	    		startActivity(new Intent(LoginSuccess.this,Password.class).putExtra("uid",(CharSequence)uid));
	    	}
	    	else if(position == 3)
	    	{
	    		drawerLayout.closeDrawer(drawerListView);
	    		confirmExit();
	    	}
	    	

	    }
	}
	
	public void confirmExit()
    {
        LoginSuccess.this.runOnUiThread(new Runnable()
        {
            public void run() 
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginSuccess.this);
                
                builder.setTitle("Logout");
                builder.setMessage("Do you really want to logout?")  
                       .setCancelable(true)
                         .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) 
                           {
                        	   startActivity(new Intent(LoginSuccess.this,SmartNotes.class));
                           }
                       
                       })    
                       .setNegativeButton("No", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							alert.dismiss();
							
						}
					});
                                   
                alert = builder.create();
                alert.show();
                
            }
        });
    }
	
	   private class MyAsyncTask extends AsyncTask<String, Integer, Double>
	    {
	  	  @Override
	  	  protected Double doInBackground(String... params) 
	  	  {
	  		getCourses();
	  		return null;
	  	  }
	  	}
	
}

