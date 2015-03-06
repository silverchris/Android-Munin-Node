Android Munin Node - A munin node program for Android
=====================================================
Android Munin Node is a implementation of munin node for android. It isn't directly compatible with Munin, but uses a Virtual node in Munin with a plugin that parses
data that another script takes as an upload from the "node" running on the Android Device. Plugins can be easily added to the project but need to be built in at this
time. Requires mod_python to be installed on the web server to collect the data from the Android Devices.

Example Install
=====================================================
Lets pretend that your website data lives at /var/www/localhost/htdocs/

Make a Directory in /var/www/localhost/htdocs/ called android and copy the python scripts there
Make a Directory in /var/www/localhost/htdocs/android/ called data

Edit android_ and change the variable storedir to /var/www/localhost/htdocs/android/data/
Then edit handler.py and change the variable storedir to /var/www/localhost/htdocs/android/data/

Create Directive in your apache vhost or .htaccess file so that handler.py gets ran
Example:
<Directory "/var/www/localhost/htdocs/android/">
                Options +ExecCGI
                AllowOverride all
                Order allow,deny
                allow from all
                SetHandler mod_python
                PythonHandler handler
</Directory>

Install Munin_Node.apk to your android device and get the unique id.
Create a symlink in your munin plugins directory to point to /var/www/localhost/htdocs/android/android_ called android_youruniqueid
eg: ln -sf /var/www/localhost/htdocs/android/android_ /etc/munin/plugins/android_bd52a2a1608bb3d3

Add to your new virtual node into your plugin-conf.d/munin-node in your munin directory.
Example virtual node:

[android_bd52a2a1608bb3d3]
host_name android-debug

Make sure to change the part in the brackets to your unique id, and set the host_name to something you remember.

Edit your Munin Server munin.conf for your Virtual Node
Example Configuration:
[android-debug]
   address 127.0.0.1
   use_node_name no
   
 Make sure you change the part in the brackets to the host name you set in the previous step and the address points to the host that had the android plugin.
 
 Make sure that you reload your apache configuration and your munin-node so that the changes take place
 
