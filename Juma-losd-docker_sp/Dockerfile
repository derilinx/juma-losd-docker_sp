FROM ubuntu:xenial

ENV MYSQL_PWD root
RUN echo "mysql-server mysql-server/root_password password $MYSQL_PWD" | debconf-set-selections
RUN echo "mysql-server mysql-server/root_password_again password $MYSQL_PWD" | debconf-set-selections

RUN apt-get update -y \ 
 && apt-get install -y screen openjdk-8-jdk mysql-server-5.7 maven vim apache2 php libapache2-mod-php curl libcurl3 libcurl3-dev php7.0-curl
 
COPY start.sh /juma/start.sh
COPY juma-uplift-master /juma/juma-uplift-master
COPY juma-api /var/www/html/juma-api
COPY ports.conf /etc/apache2/ports.conf
COPY apache2.conf /etc/apache2/apache2.conf

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
ENV PATH $JAVA_HOME/bin:$PATH

RUN chmod -R 777 /var/www/html/juma-api/temp/
RUN chmod -R 777 /juma/juma-uplift-master/r2rml_updated/
RUN chmod -R 777 /juma/juma-uplift-master/userfiles/
RUN mkdir /var/run/mysqld  
RUN chown -R mysql:mysql /var/lib/mysql /var/run/mysqld
RUN a2enmod proxy && a2enmod proxy_http
VOLUME /var/lib/mysql
VOLUME /var/lib/mysql-keyring
VOLUME /var/lib/mysql-files
RUN chown -R mysql:mysql /var/lib/mysql /var/run/mysqld /var/lib/mysql-keyring /var/lib/mysql-files
#RUN service apache2 start
#RUN service mysql start
#RUN chmod +x /juma/start.sh
EXPOSE 8889 

CMD ["/bin/bash", "/juma/start.sh"]
