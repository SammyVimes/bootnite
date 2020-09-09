export HOST_IP=$(ip -4 addr show docker0 | grep -Po 'inet \K[\d.]+')
docker run -P -e HOST_IP=${HOST_IP} -e DISCO_PORT=$2 -p $2:47500 --name $1 ignition
