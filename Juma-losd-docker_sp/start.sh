#!/bin/bash

#chmod -R 777 /var/www/html/juma-api/temp/
#chmod -R 777 /juma/juma-uplift-master/r2rml_updated/
#chmod -R 777 /juma/juma-uplift-master/userfiles/
#a2enmod proxy && a2enmod proxy_http
chown -R mysql:mysql /var/lib/mysql
service mysql start
service apache2 start
cd /juma/juma-uplift-master
mvn jetty:run -Djetty.port=8888
#read
