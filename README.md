# Biu

# 使用之前必须阅读使用许可或者得到柳志强本人的同意
# Init Infomation Example
#Endpoint:https://compute.uscom-central-1.oraclecloud.com<br />
#Username:cloud.admin<br />
#Password:Jerky@*****<br />
#Domain:gse00012384<br />

# 需手动将oracle驱动注册到maven库中
mvn install:install-file -Dfile=D:/download/ojdbc7.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=12.1.0 -Dpackaging=jar