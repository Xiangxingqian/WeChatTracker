android create uitest-project -n ehb -t 7 -p .
ant build
adb push bin\ehb.jar /data/local/tmp/
adb shell uiautomator runtest ehb.jar -c LaunchSettings