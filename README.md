导出 Android 手机内的短信
=========================

想要导出 Android （小米）上的短信， 但是不相信别人的短信导出程序和在线备份工具， 现学现卖做的，只有功能没有界面。

因为是临时用的，做的很简陋，
保存短信，并且会查找相应的通讯录，如果在通信录里有记录，则输出短信的对方收发信人的名字。

路径:

    sdcard/外部存储: Documents/smsbackup/sms.csv

格式:

    TO/FROM, 联系人, 号码,日期, 内容

排序: Sms.ADDRESS DESC, Sms.Date DESC

有小问题没有解决：

1. 每次都写入 getExternalStorageDirectory()/Documents/smsbackup/sms.csv，执行会覆盖上一次的文件。
2. 因为是在onCreate里阻塞方式查询和写入文件，所以进入界面就卡住，直到完全生成文件。
3. 完全没有控制界面和选项
4. 不知道为什么不显示在 Android Transfer 里，在手机自己的文件管理里复制一下，反而就都显示了，奇怪
