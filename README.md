# Biu

#A fully opensource code framework for Oracle Public Cloud and Oracle At Customer
Biu has been realized fully Rest API for Oracle Public Cloud and Oracle At Customer
Biu have a console UI for customer can easy use Oracle Public Cloud and Oracle At Customer

# Init Infomation Example
#Endpoint:https://compute.uscom-central-1.oraclecloud.com<br />
#Username:cloud.admin<br />
#Password:**********<br />
#Domain:gse00012384<br />

# You need add Oracle JDBC Driver into maven repository by your self
mvn install:install-file -Dfile=D:/download/ojdbc7.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=12.1.0 -Dpackaging=jar

# Language
The default language is english, if you want to use Chinese, please change the value _en to _cn in Biu.bat<br />
Example<br />
java -jar target/Biu-jar-with-dependencies.jar _en