# Biu

# A fully opensource code framework for Oracle Public Cloud and Oracle At Customer
# Biu has been realized fully Rest API for Oracle Public Cloud and Oracle At Customer

# Biu have a console UI for customer can easy use Oracle Public Cloud and Oracle At Customer

# Init Infomation Example
#Endpoint:https://compute.uscom-central-1.oraclecloud.com<br />
#Username:cloud.admin<br />
#Password:Jerky@*****<br />
#Domain:gse00012384<br />

# 需手动将oracle驱动注册到maven库中
mvn install:install-file -Dfile=D:/download/ojdbc7.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=12.1.0 -Dpackaging=jar