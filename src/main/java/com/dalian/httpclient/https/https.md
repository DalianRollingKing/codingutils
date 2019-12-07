# HTTPS JAVA 配置
## CA->颁发机构 模拟CA 首先生成 pubkey、prikey
java命令: keytool -genkeypair -keyalg RSA -keysize 2048 -keystore CA.jks -alias CA
Enter keystore password:  somewordsforpassword
Re-enter new password:    somewordsforpassword
What is your first and last name?
  [Unknown]:  jinwei guo
What is the name of your organizational unit?
  [Unknown]:  https    
What is the name of your organization?
  [Unknown]:  rollingking
What is the name of your City or Locality?
  [Unknown]:  dalian
What is the name of your State or Province?
  [Unknown]:  liaoningsslcontext
What is the two-letter country code for this unit?
  [Unknown]:  CN
Is CN=myca, OU=mallen's ca, O=mallen, L=chengdu, ST=sichuan, C=CN correct?
  [no]:  y
Enter key password for <CA>
    (RETURN if same as keystore password):   此处直接回车使用第一行的密码（.jks访问密码）
## 提取CA的公钥文件 从生成的CA.jks中提取pubkey到.crt文件中
java命令: keytool -exportcert -keystore CA.jks -alias CA -file CA.crt
## 服务端公私钥 
java命令: keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -keystore server.jks
后续的输入与生成CA.jks一致
但是需要注意的是在What is your first and last name?这一项
What is your first and last name?
  [Unknown]:www.dalianrollingking.club
作为服务器应该填写服务器的域名,比如www.dalianrollingking.club或者*.dalianrollingking.club
jdk中,在验证服务器端证书时,首先会取该字段来与当前访问的地址做对比，如果一致才能进入真正的证书验证阶段。
也就是SSLConnectionSocketFactory.getDefaultHostnameVerifier()
## 证书申请请求 通过服务端公私钥文件转换为证书请求文件 .jks->.csr
java 命令 : keytool -certreq -alias server -keystore server.jks -file server.csr
## CA颁发证书
keytool -gencert  -alias CA -keystore CA.jks -infile server.csr -outfile server.crt
## 将CA.crt导入到受信任的证书列表中
keytool -import -alias CA -file CA.crt -keystore $JAVA_HOME/jre/lib/security/cacerts
## 导入Server证书
keytool -import -alias server -file server.crt -keystore server.jks -trustcacerts
导入时需要输入cacerts的密码，默认密码为changeit
## 配置tomcat
打开tomcat的配置文件$CATALINA_HOME/conf/server.xml
配置connector 8443端口
讲server.jks配置到server.xml中
## 客户端验证
keytool -import -alias CA -file CA.crt -keystore $JAVA_HOME/jre/lib/security/cacerts
httpclient get 请求 https 的url

## 删除证书（可选操作）
keytool -delete -alias CA -keystore $JAVA_HOME/jre/lib/security/cacerts



### https总结
CA章节是模仿颁发机构的行为，最终生成的还是jks只是包装了CA的公钥，所以如果不需要证书而直接使用服务端的pubkey
则可以直接让客户端导入服务端server.jks生成的server.crt过程和CA.jks生成CA.crt一样。
结合项目的应用，当项目中通过httpclient访问https的url时，如果url为域名，则可以通过导入.crt文件来解决
如果url为ip地址，由于一般的jks都是以域名为信息，所以校验host时会出错，这样就没有办法来配置访问了，其实相当于服务端的jks文件有问题，
他如果让我通过ip访问那么就应该给我一个ip对应的crt文件，所以这样的话就得通过配置信任所有证书而不去校验host，来达到正常访问的目的。
项目中漏扫获取天池许可的时候是使用httpclient发起请求来获取许可参数，但是访问的url是IP地址所以只能设置允许所有的证书来获取信息，
项目中下发crt文件的接口的作用是由于单点登陆的过滤器需要从单点登录server获取登陆信息，而单点登录server的url是https的话就不好配置允许所有
因为它初始化的时候，用的是jdk默认的，所以需要配置证书导入。


    
    
