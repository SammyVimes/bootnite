export HOST_IP=$(ipconfig getifaddr en0)
docker run -e HOST_IP=${HOST_IP} -e DISCO_PORT=$2 -p $3:8080 -p $2:47500 --name $1 ignition
