# Update Dagah to version 1.1.2
cd /home/dagah/dagah/
/usr/bin/cp -rf ./html/*.html /var/www/html/*
mysql -u root shevirah < update_schema.sql
chown -R apache:apache /var/www/html/
chown -R dagah:dagah /home/dagah/dagah
chmod -R 775 /var/www/html
chmod -R 775 /home/dagah/dagah/

