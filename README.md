# Biu Eco System

A fully opensource code framework base on Java for Oracle Public Cloud and Oracle At Customer<br />
Biu is a Java SDK who has been realized fully Rest API for Oracle Public Cloud and Oracle At Customer<br />
Biu have a console UI for customer can easy use Oracle Public Cloud and Oracle At Customer

# = License Agreement =
Base On GPL3.0<br/>
**Anybody who used Biu any code in your program, he/she have to let me know. Since you have used Biu any code, it means you have agree this license.**<br/>
Notification Email: zhiqiang.x.liu@oracle.com

# Programmer Black List
List who used Biu code but don't let me know.<br/>
Nobody for now.

# Biu - User List
**Oracle China CPSC Team**<br />
**Oracle China Cloud Team**<br />
**Oracle China OSC Team**<br />
**Oracle China SCC Team**<br />

# Biu - Blue Plan
The current version is **v2.3**, the next coming version is **3.0**<br/>
**v3.0** will be great release, can one click deploy Oracle RAC in OPC, OCM<br/>
**v4.0** will be fresh release, can one click deploy Nginx + 2 Node Tomcat, support SLB<br/>
**v4.5** will be integrate with <a href="https://github.com/guangleibao/jiu">Jiu</a>, support one click deploy Oracle product and opensource solution in OPC, OCM, OCI<br/>
**v5.0** will be great release, Biu will support canvas web UI, can design a solution architecture with drag and drop, like ravello.<br/>
**v6.0** will be a end release, Biu will support a mirror function, can mirror VM from AWS to Oracle Cloud.<br/>

# Compile and Run
mvn package && biu<br/>

# Init Infomation Example
Endpoint:https://compute.uscom-central-1.oraclecloud.com<br />
Username:cloud.admin<br />
Password:###########<br />
Domain:gse00012384<br />

# Installation Biu For Plugin Module
In your program entry point add the below code and make sure the code can only be execute once.<br/>
Biu.BIUPROFILE = "D:/project/Biu-orc/src/main/lib/biuaccount";<br/>
Biu.main(new String[] { "_cn", "plugin" });<br/>     

# You need add Oracle JDBC Driver into maven repository by your self
mvn install:install-file -Dfile=D:/download/ojdbc7.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=12.1.0 -Dpackaging=jar

# Language
The default language is english, if you want to use Chinese, please change the value _en to _cn in Biu.bat<br />
Example<br />
java -jar target/Biu-jar-with-dependencies.jar _en

# Biu Console Menu Screenshot
![image](https://github.com/oraclechina/Biu/blob/master/images/biu_logo.png)

# Biu Plugin-ORC Live Demo Show
![image](https://github.com/oraclechina/Biu/blob/master/images/Biu演示2.gif)

# Recommend A Best Book With Practice For Oracle Cloud
![image](https://github.com/oraclechina/Biu/blob/master/images/book.png) - <a href="https://www.amazon.cn/dp/B075SZFSZL/ref=sr_1_1?ie=UTF8&qid=1515512395&sr=8-1&keywords=oracle+iaas+and+paas" target="_blank">Amazon Link</a>
