package com.example.SmartNotes;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
import com.example.SmartNotes.DrawingView;
import com.example.SmartNotes.R;


import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class LiveLectures extends Activity implements OnClickListener
{
	private DrawingView drawView;
	private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;
	private float smallBrush, mediumBrush, largeBrush;
	private Switch mySwitch;
	private String uid,courseCode,ip;
	private java.sql.Date sqlDate;
	private static int autoNum =1;
	private DefaultHttpClient httpclient;
	private HttpPost httppost;
	private ArrayList<NameValuePair> nameValuePairs;
	private HttpResponse response;
	private HttpEntity entity;
	private JSONObject jsonResponse;
	private String switchOn;
	private ServerSocket serverSocket=null;
	private Socket client;
	public static int SERVERPORT,port;
	private Bitmap bmp;
	private DataInputStream is;
	private DataOutputStream os;
	private ArrayList<Socket> connectedClients ;
	
	
	void toggleValid()
    {
		
        try{            
        		
        	if(!uid.matches(".*\\d.*") && switchOn == "1")
        	{
        		if(serverSocket !=null)
        		{
        			if(!serverSocket.isClosed())
        			serverSocket.close();
        		}
        		serverSocket = new ServerSocket(0);
        		SERVERPORT = serverSocket.getLocalPort();
        		connectedClients = new ArrayList<Socket>();
        	}
        	
	            HttpParams httpParameters = new BasicHttpParams();
	            HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
	            HttpConnectionParams.setSoTimeout(httpParameters, 5000);
	            httpclient = new DefaultHttpClient(httpParameters);
	            httppost= new HttpPost("http://192.168.11.1/Pervasive_Project/toggle.php/"); 
	            nameValuePairs = new ArrayList<NameValuePair>(4);
	            nameValuePairs.add(new BasicNameValuePair("code",courseCode));  
	            nameValuePairs.add(new BasicNameValuePair("uid",uid)); 
	            nameValuePairs.add(new BasicNameValuePair("setVal",switchOn)); 
	            nameValuePairs.add(new BasicNameValuePair("serverport",String.valueOf(SERVERPORT))); 
	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	      
	            response = httpclient.execute(httppost);
	            if(response.getStatusLine().getStatusCode()== 200)
	            {
	            	 if(uid.matches(".*\\d.*"))
	                {
			            	 entity = response.getEntity();
			                 if(entity != null)
			                 {
			                    InputStream instream = entity.getContent();
			                    jsonResponse = new JSONObject(SmartNotes.convertStreamToString(instream));
			                    ip  = jsonResponse.getString("ipaddr");
			                    port  = Integer.parseInt(jsonResponse.getString("port"));
			                    
			                 }
			                 displayToast();
	                       
	                 }
	            	 
	            	 
	            	 if(uid.matches(".*\\d.*"))//Student is the user
	 	            {
	            		        
			 	            	if(!ip.startsWith("error") && switchOn=="1"& port!=80)//TCP client start
			 	            	{
						 	            		
						 	            		  try 
						 	            		  {
							 	            			  	client = new Socket(InetAddress.getByName(ip), port);
							 	            			  	is = new DataInputStream(client.getInputStream());
							 	           				    os = new DataOutputStream(client.getOutputStream());
							 	            			  
							 	           				    int msgLen = is.readInt();
										 	                byte[] msg = new byte[msgLen];
										 	            	is.readFully(msg);
										 	            	 bmp = BitmapFactory.decodeByteArray(msg, 0, msgLen);
										 	            			    
										 	            			   runOnUiThread(new Runnable() 
																	    {
																	         public void run() 
																	         {
																	        	 Toast.makeText(LiveLectures.this,"bitmap recieved", Toast.LENGTH_SHORT).show();
																	          }
																	     });
										 	            			   runOnUiThread(new Runnable() 
													 	               {
													 	                    public void run() 
													 	                    {
													 	                    
													 	                   	drawView.setBitmap(bmp);
													 	                     }
													 	                });
										 	            			  
										 	            			  	
										 	            			  	os.writeChar('S');
										 	            			  	os.flush();
										 	            			  
										 	            			  	
							 	            			  
							 	            			  	//client.close();
							 	            			  	is.close();
							 	            			  	os.close();//Thread.sleep(1000);
							 	            			  	
						 	            			  	
						 	            		  }  
									 	             catch (final IOException ioe) 
							 	            		{
							 	            			
							 	            			runOnUiThread(new Runnable() 
										 	               {
										 	                    public void run() 
										 	                    {
										 	                   	 Toast.makeText(LiveLectures.this,"Error when reading object\n", Toast.LENGTH_LONG).show();
										 	                     }
										 	                });
							 	                   		
							 	            	    }
						 	            		 
			 	            	}
			 	            	else if(switchOn == "0")
			 	            	{
			 	            		client.close();
			 	            	}
			 	            	
			 	            	
			 	            	
	 	            }
	            	 
	 	            else//teacher
	 	            {
	 	            	 
		 	           						 	           		
			 	            	if(switchOn == "1")//start teacher server and push data 
			 	            	{
							 	                
							 	           		 try 
							 	           		 {
							 	           			//serverSocket = new ServerSocket(SERVERPORT);
							 	           			if(serverSocket.isBound())
							 	           			{
										 	           		runOnUiThread(new Runnable() 
										 	               {
										 	                    public void run() 
										 	                    {
										 	                   	 Toast.makeText(LiveLectures.this,"TCP Server listening on port "+SERVERPORT, Toast.LENGTH_SHORT).show();
										 	                     }
										 	                });
										 	           	
							 	           			}
							 	           		while (true)
						 	           			{
						 	           				
						 	           				 client = serverSocket.accept();
						 	           				 connectedClients.add(client);
						 	           			
						 	           				 if(client.isConnected())
						 	           				 {
						 	           				 	runOnUiThread(new Runnable() 
									 	               {
									 	                    public void run() 
									 	                    {
									 	                   	 Toast.makeText(LiveLectures.this,client.getInetAddress().getHostAddress()+" connected to the Server", Toast.LENGTH_SHORT).show();
									 	                     }
									 	                });
						 	           				 } 	           			
						 	           			     myThread t1 = new myThread(client);
						 	           				 Thread thread = new Thread(t1);
						 	           				 thread.start();
						 	           				 
						 	           				
						 	           										 	           	       
						 	           			}
							 	           	 } 
							 	           		catch (UnknownHostException e) 
							 	           		{
							 	           		runOnUiThread(new Runnable() 
								 	               {
								 	                    public void run() 
								 	                    {
								 	                   	 Toast.makeText(LiveLectures.this,"Unknown Host Exception!!", Toast.LENGTH_SHORT).show();
								 	                     }
								 	                });
							 	           		}
							 	               catch (IOException ioe) 
							 	               {
							 	            	   displayError();
							 	               }
							 	           		 finally
							 	           		 {
							 	           			 
							 	           			 serverSocket.close();
							 	           			 
							 	           		 }
							 	           		
			 	            	}
			 	            	else
			 	            	{
			 	            		for(Socket s:connectedClients)
			 	           			 {
			 	           				 s.close();
			 	           			 }
			 	            		serverSocket.close();
			 	            	}
			 	            	
	 	            }
	             
	            } 
	            
	                     
          } 
         catch(Exception e)
        {
        	  displayError();
        		
        }
    }
	
	public void displayToast()
	{
		 runOnUiThread(new Runnable() 
        	{
             	public void run() 
             	{
             		if(ip.startsWith("error") || port == 80)
             		{
                 		Toast toast = Toast.makeText(LiveLectures.this, "The course Instructor has not turned on sharing yet. Please try again after some time.", Toast.LENGTH_SHORT);
	       		    	TextView vt = (TextView)toast.getView().findViewById(android.R.id.message);
	       		    	if( vt != null) vt.setGravity(Gravity.CENTER);
	       		    	if(switchOn == "1")
	       		    		
	       		    	toast.show();
             		}
             		
             		else
             		{
             			//if(switchOn == "1")
             			//Toast.makeText(LiveLectures.this,"Instructor IP is "+ip+":"+port, Toast.LENGTH_SHORT).show();
             		}
             	}
        	});
	}

	public void displayError(){
	runOnUiThread(new Runnable() 
    {
         public void run() 
         {
        	 Toast.makeText(LiveLectures.this,"An error has occurred", Toast.LENGTH_SHORT).show();
          }
     });
	}
	
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.livelec);
		
		  Intent in = getIntent();
	        String inData = (String) in.getCharSequenceExtra("code");
	        
	        if (inData != null)
	        {
	        	
	        	StringTokenizer st = new StringTokenizer(inData,"%");
	        	
	        	courseCode = st.nextToken();
	        	uid = st.nextToken();
	        }
	        
	        java.util.Date utilDate = new java.util.Date();
        sqlDate = new java.sql.Date(utilDate.getTime());       	
		mySwitch = (Switch) findViewById(R.id.switch1);
	
		// set the switch to ON
		mySwitch.setChecked(false);
		// attach a listener to check for changes in state
		mySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
			{
				 
			
				
					if(isChecked)
					{
	                	
	               	 //Toast.makeText(getApplicationContext(), "Live Lectures active now",Toast.LENGTH_SHORT).show();
	               	 switchOn = "1";
	               	
					}
	               	 else
	               	 {
	               		// Toast.makeText(getApplicationContext(),"Live Lectures turned OFF", Toast.LENGTH_SHORT).show();
	               		 switchOn = "0";
	               	 }
					
					new toggleTask().execute();
		
			}
		
		});
		//get drawing view
		drawView = (DrawingView)findViewById(R.id.drawing);

		//get the palette and first color button
		LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
		currPaint = (ImageButton)paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

		//sizes from dimensions
		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);

		//draw button
		drawBtn = (ImageButton)findViewById(R.id.draw_btn);
		drawBtn.setOnClickListener(this);

		//set initial size
		drawView.setBrushSize(smallBrush);

		//erase button
		eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
		eraseBtn.setOnClickListener(this);

		//new button
		newBtn = (ImageButton)findViewById(R.id.new_btn);
		newBtn.setOnClickListener(this);

		//save button
		saveBtn = (ImageButton)findViewById(R.id.save_btn);
		saveBtn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//user clicked paint
	public void paintClicked(View view){
		//use chosen color

		//set erase false
		drawView.setErase(false);
		drawView.setBrushSize(drawView.getLastBrushSize());

		if(view!=currPaint){
			ImageButton imgView = (ImageButton)view;
			String color = view.getTag().toString();
			drawView.setColor(color);
			//update ui
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
			currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
			currPaint=(ImageButton)view;
		}
	}

	@Override
	public void onClick(View view){

		if(view.getId()==R.id.draw_btn){
			//draw button clicked
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Brush size:");
			brushDialog.setContentView(R.layout.brush_chooser);
			//listen for clicks on size buttons
			ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setErase(false);
					drawView.setBrushSize(smallBrush);
					drawView.setLastBrushSize(smallBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setErase(false);
					drawView.setBrushSize(mediumBrush);
					drawView.setLastBrushSize(mediumBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setErase(false);
					drawView.setBrushSize(largeBrush);
					drawView.setLastBrushSize(largeBrush);
					brushDialog.dismiss();
				}
			});
			//show and wait for user interaction
			brushDialog.show();
		}
		else if(view.getId()==R.id.erase_btn){
			//switch to erase - choose size
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Eraser size:");
			brushDialog.setContentView(R.layout.brush_chooser);
			//size buttons
			ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(smallBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(mediumBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(largeBrush);
					brushDialog.dismiss();
				}
			});
			brushDialog.show();
		}
		else if(view.getId()==R.id.new_btn){
			//new button
			AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
			newDialog.setTitle("New drawing");
			newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
			newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					drawView.startNew();
					dialog.dismiss();
				}
			});
			newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					dialog.cancel();
				}
			});
			newDialog.show();
		}
		else if(view.getId()==R.id.save_btn){
			//save drawing
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Save drawing");
			saveDialog.setMessage("Save drawing to device Gallery?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					//save drawing
					drawView.setDrawingCacheEnabled(true);
					//attempt to save
					String imgSaved = MediaStore.Images.Media.insertImage(
							getContentResolver(), drawView.getDrawingCache(),
							courseCode+"--"+sqlDate.toString()+"--"+autoNum+".png", "drawing");
					autoNum = autoNum +1;
					//feedback
					if(imgSaved!=null){
						Toast savedToast = Toast.makeText(getApplicationContext(), 
								"Drawing saved to Gallery!", Toast.LENGTH_SHORT);
						savedToast.show();
					}
					else{
						Toast unsavedToast = Toast.makeText(getApplicationContext(), 
								"Oops! Image could not be saved.", Toast.LENGTH_SHORT);
						unsavedToast.show();
					}
					drawView.destroyDrawingCache();
				}
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					dialog.cancel();
				}
			});
			saveDialog.show();
		}
	}
	
	 private class toggleTask extends AsyncTask<String, Integer, Double>
	    {
		  	  @Override
		  	  protected Double doInBackground(String... params) 
		  	  {
		  		toggleValid();
		  		return null;
		  	  }
		  	  
		  	
		  }
	 
	 public class myThread implements Runnable
	{
		private Socket csoc;
		private DataOutputStream out;
		private DataInputStream in;
		 
		 public myThread(Socket c) 
		{
			csoc = c;
			
			try {
				in = new DataInputStream(csoc.getInputStream());
				out = new DataOutputStream(csoc.getOutputStream());
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		 
		 public void run()
		{
				try
				{
					
								//drawView.setDrawingCacheEnabled(true);
								//Bitmap b=drawView.getDrawingCache();
								Bitmap b = drawView.getBitmap();
								ByteArrayOutputStream stream = new ByteArrayOutputStream();
				                b.compress(Bitmap.CompressFormat.PNG, 100, stream);
				                byte[] msg = stream.toByteArray();
								
					              out.writeInt(msg.length);
					              out.write(msg);
					              out.flush();
				                
					              runOnUiThread(new Runnable() 
								    {
								         public void run() 
								         {
								        	 Toast.makeText(LiveLectures.this,"bitmap sent", Toast.LENGTH_SHORT).show();
								          }
								     });
					              char ack =in.readChar();
					            
				               // while(b.sameAs(drawView.getDrawingCache()));
							
							 out.close();
			            	  in.close();
			            	//  csoc.close();
				               
				   } 
							catch (IOException e) 
							{
								runOnUiThread(new Runnable() 
							    {
							         public void run() 
							         {
							        	 Toast.makeText(LiveLectures.this,"Error Occurred while sending data to client", Toast.LENGTH_SHORT).show();
							          }
							     });
							}
				finally{
					
					try {
						out.close();
						in.close();
						csoc.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
							
						
			}

	}


}

