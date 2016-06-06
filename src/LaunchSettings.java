
/**
 * WeChat Capture 
 * */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.android.uiautomator.core.UiCollection;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class LaunchSettings extends UiAutomatorTestCase {   
	
	String day;
	String currentTime;
	List<String> messages = new ArrayList<String>();
	List<String> names = new ArrayList<String>();
	List<String> ids = new ArrayList<String>();
 	private Set<LineMessage> lineMessages = new HashSet<LineMessage>();
	
	public void test() {
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH)+1;
		day = calendar.get(Calendar.YEAR)+"-"+month+"-"+calendar.get(Calendar.DAY_OF_MONTH);
		while(true){
			clickLeaderboard();
			clickAccessButton();
			parseAllTextViewMsg();
			clickBack();
			sleep(300*1000);
			messages.clear();
			names.clear();
			ids.clear();
			lineMessages.clear();
		}
	}
	
	private void clickBack(){
		getUiDevice().pressBack();
	}
	
	//点击 名次，查看05月25日排行榜
	private void clickAccessButton() {
		getUiDevice().click(200, 900);
		sleep(2000);
//		UiObject uiObject = new UiObject(new UiSelector().text("0").instance(1));
//		uiObject.click();
	}
	
	//点击排行榜
	private void clickLeaderboard(){
		getUiDevice().click(400, 1200);
		sleep(3000);
	}
	
	private void parseAllTextViewMsg(){
		sleep(5000);
		int i = 7;
		currentTime = getCurrentTime();
		while(i>0){
			UiScrollable ui = new UiScrollable(new UiSelector().className(ListView.class));
			parseTextViewMsg();
			sleep(5000);
			try {
				ui.scrollForward(80);
			} catch (UiObjectNotFoundException e) {
				e.printStackTrace();
			}
			i--;
		}
		handleMessages();
		printLineMessages();
	}
	
	private void parseTextViewMsg(){
		UiCollection listViewCollection = new UiCollection(new UiSelector().className(ListView.class));
		int size = listViewCollection.getChildCount(new UiSelector().className(TextView.class));
		int viewsize = listViewCollection.getChildCount(new UiSelector().className(View.class));
		for(int i = 0;i<viewsize;i++){
			String name  ="";
			if(i<1)
				continue;
			try {
				UiObject viewChild = listViewCollection.getChildByInstance(new UiSelector().className("android.view.View"), i);
				name = viewChild.getText();
				if((name!="")&&(!names.contains(name))){
					names.add(name);
					Log.v("qian", "Name: "+name);
				}
			} catch (Exception e) {
				i--;
				sleep(3000);
			}
		}
		if(size>0){
			for(int i = 0;i<size;i++){
				if(i<4)
					continue;
				try {
					String msg = listViewCollection.getChildByInstance(new UiSelector().className("android.widget.TextView"), i).getText();
					messages.add(msg);
				} catch (Exception e) {
					i = i--;
					sleep(3000);
				}
			}
		}
	}
	
	private String getCurrentTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return df.format(new Date());
	}
	
	private void handleMessages(){
		Map<String, String> map = new HashMap<String, String>();
		removeInvalidData();
		for(int i = 0; i<names.size();i++){
			String name = names.get(i);
			String step = messages.get(i*3+1);
			map.put(name, step);
		}
		for(String name:map.keySet()){
			LineMessage line = new LineMessage(currentTime, name, map.get(name));
			lineMessages.add(line);
		}
	}
	
	private void printLineMessages(){
		StringBuilder sBuilder = new StringBuilder();
		for(LineMessage lineMessage: lineMessages){
			sBuilder.append(lineMessage+"\n");
		}
		writeFile("/mnt/sdcard/"+day+".txt", sBuilder.toString());
	}
	
	public void writeFile(String filePathAndName, String fileContent) {
		try {
			File f = new File(filePathAndName);
			if (!f.exists()) {
				f.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(fileContent);
			writer.close();
		} catch (Exception e) {
			System.out.println("写文件内容操作出错");
			e.printStackTrace();
		}
	}
	
	/**
	 * 消除无效数据
	 */
	private void removeInvalidData(){
		Log.v("qian","Messages: "+messages+" Size: "+messages.size());
		for(int i = 0;i<messages.size();i++){
			if(i%3==0){
				int value = Integer.valueOf(messages.get(i));
				if(value!=(i/3+1)){
					messages.remove(i);
					i--;
				}
				else {
					if(messages.get(i)==messages.get(i+1)){
						messages.remove(i);
						i--;
					}
				}
			}	
		}
		Log.v("qian","Messages: "+messages);
	}
}
