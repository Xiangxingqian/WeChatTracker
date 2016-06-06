import os 
os.popen('android create uitest-project -n ehb -t 7 -p .')
os.popen('ant build')
os.popen('adb push bin\ehb.jar /data/local/tmp/')
os.popen('adb shell uiautomator runtest ehb.jar -c LaunchSettings')
