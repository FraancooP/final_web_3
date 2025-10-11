docker volume create mysqliw3tpfinal_data

docker run -d \
  --name mysqliw3_tpfinal \
  --restart always \
  -e MYSQL_ROOT_PASSWORD=finalweb3 \
  -e MYSQL_DATABASE=iw3_tp_final \
  -e MYSQL_USER=iw3 \
  -e MYSQL_PASSWORD=finalweb3 \
  -p 33406:3306 \
  -v mysqliw3tpfinal_data:/var/lib/mysql \
  -v /etc/localtime:/etc/localtime:ro \
  mysql:8.0.43 \
	--character-set-server=utf8mb4 \
	--collation-server=utf8mb4_0900_ai_ci \
	--default-authentication-plugin=mysql_native_password
