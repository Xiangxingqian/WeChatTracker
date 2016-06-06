from datetime import *  
import time 
import os


def writeFile(i):
	f = open("D:\wechatlog\\"+str(i)+".txt",'w+')	
	i = i+1
	f.write(os.popen('adb logcat -s -d qian').read())
	f.close()

while(1):
	i = 0;
	writeFile(i)
	time.sleep(300)
	i = i +1 