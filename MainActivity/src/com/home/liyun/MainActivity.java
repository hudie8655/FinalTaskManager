package com.home.liyun;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortController;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ListActivity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Debug;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends ListActivity {

    private RunAppAdapter adapter;

    private ArrayList<RunApp> mRunApps;

    private String[] mRunAppNames;
    private String[] mRunAppDiscr;
    private String[] mRunAppMem;
    private Drawable[] mRunAppicon;

    private DragSortListView.DropListener onDrop =
        new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
            	RunApp item = adapter.getItem(from);

                adapter.remove(item);
                adapter.insert(item, to);
            }
        };

    private DragSortListView.RemoveListener onRemove = 
        new DragSortListView.RemoveListener() {
            @SuppressLint("NewApi")
			@Override
            public void remove(int which) {
            	showMemInfo();
            	
            	RunApp item = adapter.getItem(which);
            	ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
				activityManager.killBackgroundProcesses(item.name);
                adapter.remove(adapter.getItem(which));
            }
        };
        
        private String getTotalMem()
        {
        	 String str1 = "/proc/meminfo";// 系统内存信息文件  
             String str2;  
             String[] arrayOfString;  
             long initial_memory = 0;  
             try {  
                 FileReader localFileReader = new FileReader(str1);  
                 BufferedReader localBufferedReader = new BufferedReader(  
                         localFileReader, 8192);  
                 str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小  
          
                 arrayOfString = str2.split("\\s+");  
                 for (String num : arrayOfString) {  
                     Log.i(str2, num + "\t");  
                 }  
          
                 initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte  
                 localBufferedReader.close();  
          
             } catch (IOException e) {  
             }  
             return Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化  
         }  
        
        public void showMemInfo(){
            MemoryInfo memInfo = new MemoryInfo();
            ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getMemoryInfo(memInfo);
            
            
   	
            TextView tv = (TextView) findViewById(R.id.memInfo2);
            tv.setText("Total Mem:"+getTotalMem()+"\t Avail Mem:"+Formatter.formatFileSize(getBaseContext(), memInfo.availMem)) ;
       }
        
        private HashMap<String,ApplicationInfo> appMap;
        private PackageManager pm;
        private void initAppInfo(){
        	
            appMap = new HashMap<String,ApplicationInfo>();
            pm = getApplicationContext().getPackageManager();  
            for(ApplicationInfo ap :pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES))
            {
            	 //if((ap.flags&ApplicationInfo.FLAG_SYSTEM)==0&&  
            	//		                     (ap.flags&ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)==0)  
            	
            	 {
            		 appMap.put(ap.processName, ap);
            	 	 Log.i("app",ap.loadLabel(pm).toString());
            	 }
            }
        }
        
        private ApplicationInfo getAppInfo(String packname){
        	   //if(appMap.get(packname) == null)
                	//	return null;
               //Drawable icon = appMap.get(procInfo.processName).loadIcon(pm);
               //String apk_name = appMap.get(packname).loadLabel(pm).toString();
               return appMap.get(packname);
               
               
                //Drawable icon = pm.getinfo
                //int[] memPid = new int[]{ procInfo.pid };
                //此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
                //Debug.MemoryInfo[] memoryInfo = ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getProcessMemoryInfo(memPid);
                //获取进程占内存用信息kb单位
                //int memSize = memoryInfo[0].dalvikPrivateDirty;
                //map.put("proc_id", apk_name+"pid:"+procInfo.pid+"\tuid:"+procInfo.uid+"\tmem:"+Formatter.formatFileSize(getBaseContext(), memSize*1024));
                
                //map.put("icon", icon);
        }
        private void getRuningApps(){
        	ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        	List<RunningAppProcessInfo> procList = activityManager.getRunningAppProcesses();
        	List<String> ls = new ArrayList<String>();
        	List<String> ls2 = new ArrayList<String>();
        	List<String> ls3 = new ArrayList<String>(); //mem
        	List<Drawable> ls4 = new ArrayList<Drawable>(); //icon
        	
        	for ( Iterator<RunningAppProcessInfo> iterator = procList.iterator(); iterator.hasNext();) {
               
              	RunningAppProcessInfo procInfo = iterator.next();
              	ApplicationInfo temp = null;
              	if((temp=getAppInfo(procInfo.processName))!=null)
                {
              		ls.add(procInfo.processName);
              		ls2.add(temp.loadLabel(pm).toString());
              		
              		int[] memPid = new int[]{ procInfo.pid };
                    //此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
                    Debug.MemoryInfo[] memoryInfo = ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getProcessMemoryInfo(memPid);
                    //获取进程占内存用信息kb单位
                    int memSize = memoryInfo[0].getTotalPss();
                    ls3.add(Formatter.formatFileSize(getBaseContext(), memSize*1024));
                    ls4.add(temp.loadIcon(pm));
                }
                //ls.add(procInfo.processName);
              }
        	 //mRunAppNames = getResources().getStringArray(R.array.jazz_artist_names);
             //mRunAppDiscr = getResources().getStringArray(R.array.jazz_artist_albums);
        	mRunAppNames = ls.toArray(new String[ls.size()]);
        	mRunAppDiscr = ls2.toArray(new String[ls2.size()]);
        	mRunAppMem = ls3.toArray(new String[ls3.size()]);
        	mRunAppicon = ls4.toArray(new Drawable[ls4.size()]);
        	//mRunAppDiscr = getAppInfo(mRunAppNames);
             mRunApps = new ArrayList<RunApp>();
             RunApp app;
             for (int i = 0; i < mRunAppNames.length; ++i) {
               app = new RunApp();
               app.name = mRunAppNames[i];
               //if (i < mRunAppDiscr.length) {
                 app.appDis = mRunAppDiscr[i];
                 app.mem = mRunAppMem[i];
                 app.icon = mRunAppicon[i];
               //} else {
               //  app.albums = "No albums listed";
               //}
               mRunApps.add(app);
             }
        	
        }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hetero_main);

        showMemInfo();
        DragSortListView lv = (DragSortListView) getListView(); 

        //lv.setDropListener(onDrop);
        lv.setRemoveListener(onRemove);

        initAppInfo();
       getRuningApps();

        adapter = new RunAppAdapter(mRunApps);
        
        setListAdapter(adapter);

    }

    private class RunApp {
      public String name;
      public String appDis;
      public String mem;
      public Drawable icon;

      @Override
      public String toString() {
        return name;
      }
    }

    private class ViewHolder {
      public TextView appName;
      public TextView appDis;
      public TextView mem;
      public ImageView icon;
    }

    private class RunAppAdapter extends ArrayAdapter<RunApp> {
      
      public RunAppAdapter(List<RunApp> Apps) {
        super(MainActivity.this, R.layout.jazz_artist_list_item,
          R.id.artist_albums_textview, Apps);
      }

      public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        if (v != convertView && v != null) {
          ViewHolder holder = new ViewHolder();

          TextView tv = (TextView) v.findViewById(R.id.artist_name_textview);
          holder.appName = tv;
          tv = (TextView) v.findViewById(R.id.proc_mem);
          holder.mem = tv;
          ImageView dr = (ImageView)v.findViewById(R.id.icon);
          holder.icon = dr;

          v.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) v.getTag();
        String albums = getItem(position).appDis;

        holder.appName.setText(albums);
        holder.mem.setText(getItem(position).mem);
        holder.icon.setImageDrawable(getItem(position).icon);

        return v;
      }
    }

}
