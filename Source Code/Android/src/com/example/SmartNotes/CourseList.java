package com.example.SmartNotes;

import java.util.StringTokenizer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
 
public class CourseList extends ListActivity 
{
 
	@SuppressWarnings("unused")
	private String courseNoStr,courseNameStr,cno,cname,role,uid,uname,intent,listItem;
	private static String[] COURSES = new String[8];
	private static int count = 0;
	private static boolean insert = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		 Intent in = getIntent();
	     String inData = (String) in.getCharSequenceExtra("data");
	        
	   
	        	StringTokenizer st = new StringTokenizer(inData,"%");
	        	intent= st.nextToken();
	        	role= st.nextToken();
	        	uid= st.nextToken();
	        	uname= st.nextToken();
	        	
	        	    courseNoStr= st.nextToken();
	        		courseNameStr= st.nextToken();
	        		StringTokenizer courseNoSt = new StringTokenizer(courseNoStr,"@");
	        		StringTokenizer courseNameSt = new StringTokenizer(courseNameStr,"@");
				     
				     while(courseNoSt.hasMoreTokens() && courseNameSt.hasMoreTokens()) 
				     { 
				    	  cno = courseNoSt.nextToken();
					      cname = courseNameSt.nextToken();
					      listItem=cno+" - "+cname;
					      int i;
					      for(i=0;i<count;i++)
					      {
					    	  if(COURSES[i].contains(listItem))
					    	  {
					    		 insert = false;
					    	  }
					      }
					      if(insert)
					    	  COURSES[count++] = cno+" - "+cname;
					      
				    	
				     } 
				     setListAdapter(new ArrayAdapter<String>(this, R.layout.course_list,COURSES));
	        	
				     ListView listView = getListView();
				     listView.setTextFilterEnabled(true);
				     listView.setOnItemClickListener(new OnItemClickListener()
				     {
				    	 public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
				    	 {
								 String str = (((TextView) view).getText()).toString();
								 StringTokenizer st = new StringTokenizer(str,"-");
								 String code = st.nextToken().trim();
								 String name = st.nextToken().trim();
								 
									 String data = "indirect"+"%"+role+"%"+uname+"%"+uid+"%"+code+"%"+name;
									 startActivity(new Intent(CourseList.this,LoginSuccess.class).putExtra("data",(CharSequence)data));
				    	 }
				     });
 
	}
 
}