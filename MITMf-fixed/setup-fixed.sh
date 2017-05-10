  GNU nano 2.2.6                                    File: setup-fixed.sh                                                                    Modified  

#!/usr/bin/env bash
#In case you do not have pip utility and setuptools installed
apt-get install python-pip
#In case your system is old: apt-get update && apt-get dist-upgrade
#If you are still missing some of these python packages, install them by using pip.
echo -e "\e[31mRunning Git Submodule Setup...\e[0m"
git submodule init && git submodule update --recursive
echo -e "\e[31mInstalling Recommended packages...\e[0m"
apt-get install -y python-dev python-setuptools libpcap0.8-dev libnetfilter-queue-dev
echo -e "\e[31mInstalling Capstone, Twisted, Requests, Scapy, Dnspython, Cryptography, Crypto...\e[0m"
apt-get install -y python-crypto python-twisted python-requests python-scapy python-dnspython python-cryptography python-capstone
echo -e "\e[31mInstalling Msgpack, Configobj, Pefile, Ipy, Openssl, Pypcap...\e[0m"
apt-get install -y python-configobj python-pefile python-ipy python-openssl python-pypcap python-msgpack
#git clone https://github.com/kti/python-netfilterqueue.git
#python python-netfilterqueue/setup.py install
echo -e "\e[31mRunning Mitmflib Setup...\e[0m"
(cd mitmflib-0.18.4 && python setup.py install)
