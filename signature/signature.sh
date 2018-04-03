# 设置环境变量
export JAVA_HOME=/usr/java/jdk1.8.0_131
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=$JAVA_HOME/jre/lib/ext:$JAVA_HOME/lib/tools.jar
#!/bin/sh

# 转换平台签名命令
./keytool-importkeypair -k demo.jks -p 123456 -pk8 platform.pk8 -cert platform.x509.pem -alias demo

# demo.jks : 签名文件
# 123456 : 签名文件密码
# platform.pk8、platform.x509.pem : 系统签名文件
# demo : 签名文件别名