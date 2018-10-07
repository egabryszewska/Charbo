#! /bin/bash
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-ports 8080

#Check if mongodb is currently running
`ps -A | grep -q '[m]ongod'`

if [ "$?" -eq "0" ]; then
#Mongo is running
echo 'MongoDB is already running'
else
#Start mongo
echo 'Starting MongoDB'
sudo mongod --dbpath /var/lib/mongodb&
fi

#Check if nodejs is currently running
`ps -A | grep -q '[n]odejs'`

if [ "$?" -eq "0" ]; then
echo "NodeJS is already running"
echo "Killing and restarting NodeJS"
sudo pkill nodejs
sudo nohup nodejs /bin/www >> nodejs.log&
else
echo 'Starting the NodeJS server'
sudo nohup nodejs /bin/www >> nodejs.log&
fi
