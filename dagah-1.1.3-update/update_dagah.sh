# Update Dagah to version 1.1.3
# Assumes in root directory of zip file
# Copy updates to dagah engine:
/usr/bin/cp -rf ./*.pyc /home/dagah/dagah/
/usr/bin/cp -rf ./dagah-documentation.txt /home/dagah/dagah/
/usr/bin/cp -rf ./releasenotes.txt /home/dagah/dagah/
/usr/bin/cp -rf ./dagah_db_schema.sql /home/dagah/dagah/

# Copy updates to DagahModemBridge.apk
/usr/bin/cp -rf ./DagahModemBridge.apk /var/www/html/

# Copy updates to the Android Agent
/usr/bin/cp -rf ./APKs/* /home/dagah/dagah/APKs/

# Copy updates to the iOS Agent
/usr/bin/cp -rf ./IPAs/* /home/dagah/dagah/IPAs/

# Copy updates to the Agent Controller
/usr/bin/cp -rf ./PHP/* /home/dagah/dagah/PHP/

# Copy updates to the Spooler
/usr/bin/cp -rf ./Spool/* /home/dagah/dagah/Spool/
systemctl enable /home/dagah/dagah/Spool/qserver
systemctl start qserver

# Copy updates to the clones directory
/usr/bin/cp -rf ./clones/* /home/dagah/dagah/clones/

# Copy updates to GUI:
/usr/bin/cp -rf ./html/*.html /var/www/html/
/usr/bin/cp -rf ./html/composer.json /var/www/html/
/usr/bin/cp -rf ./html/php/* /var/www/html/php/
/usr/bin/cp -rf ./html/androidapp/* /var/www/html/androidapp/
/usr/bin/cp -rf ./html/components/* /var/www/html/components/
/usr/bin/cp -rf ./html/css/* /var/www/html/css/
/usr/bin/cp -rf ./html/images/* /var/www/html/images/
/usr/bin/cp -rf ./html/js/* /var/www/html/js/
/usr/bin/cp -rf ./html/less/* /var/www/html/less/
mkdir /var/www/html/update
mkdir /var/www/html/upload
/usr/bin/cp -rf ./html/update/* /var/www/html/update/
/usr/bin/cp -rf ./html/upload/* /var/www/html/upload/
/usr/bin/cp -rf ./html/*.pem /var/www/html/
/usr/bin/cp -rf ./html/*.sh /var/www/html/

# Update the database
mysql -u root shevirah < update_schema_targets.sql 
mysql -u root shevirah < update_schema_version.sql
mysql -u root shevirah < update_schema_table.sql

# Platform Updates
# Install Composer (Required by Software Updater)
cd /tmp
mv /var/www/html/composer.json .
curl -sS https://getcomposer.org/installer | php 
php composer.phar install
# PHP.ini
sed -i "s/upload_max_filesize .*/upload_max_filesize = 100M/" /opt/rh/php55/root/etc/php.ini
service httpd stop
service httpd start

# etc/profile
echo 'set JAVA_HOME='
echo 'export JAVA_HOME='
echo 'export PATH=/usr/local/jdk/bin:$PATH' >> /etc/profile
# Whats App Fix
sed -i "s/ _VERSION =.*/ _VERSION = \"2.16.11\"/" /usr/lib/python2.7/site-packages/yowsup/env/env_s40.py
sed -i "s/_TOKEN_STRING =.*/_TOKEN_STRING = \"PdA2DJyKoUrwLw1Bg6EIhzh502dF9noR9uFCllGk1478194306452{phone}\"/" /usr/lib/python2.7/site-packages/yowsup/env/env_s40.py
# Create and run at boot permissions for spooler queue via rc.local 
sudo echo "#!/bin/sh" > /etc/rc.local
sudo echo "plymouth quit" > /etc/rc.local 
sudo echo "touch /var/lock/subsys/local" >> /etc/rc.local
sudo echo "sudo chmod 775 /home/dagah/dagah/Spool/*" >> /etc/rc.local
chmod 777 /etc/rc.local
chmod 777 /etc/rc.d/rc.local
chmod +x /etc/rc.d/rc.local

# Reset Permissions on Dagah Folders
chown -R apache:apache /var/www/html/
chown -R dagah:dagah /home/dagah/dagah
chmod -R 775 /var/www/html
chmod -R 775 /home/dagah/dagah/

