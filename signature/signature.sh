# ���û�������
export JAVA_HOME=/usr/java/jdk1.8.0_131
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=$JAVA_HOME/jre/lib/ext:$JAVA_HOME/lib/tools.jar
#!/bin/sh

# ת��ƽ̨ǩ������
./keytool-importkeypair -k demo.jks -p 123456 -pk8 platform.pk8 -cert platform.x509.pem -alias demo

# demo.jks : ǩ���ļ�
# 123456 : ǩ���ļ�����
# platform.pk8��platform.x509.pem : ϵͳǩ���ļ�
# demo : ǩ���ļ�����