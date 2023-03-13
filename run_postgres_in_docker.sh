#!/usr/bin/env bash

db_name=rallye
db_port=54320
container_name=ltcrallye
db_password=ltc
postgres_version=15.2

shopt -s expand_aliases
alias container_command=docker

message=$(tput setaf 7)
red=$(tput setaf 1)
reset=$(tput sgr0)
green=$(tput setaf 2)
var=$(tput setaf 6)

if [[ ! $(command -v docker) ]]; then
    echo "${message}using podman${reset}"
    alias container_command=podman
fi

echo "${message}checking if a container named $container_name is started ...${reset}"
if container_command ps -qf "name=$container_name" | grep -q .; then
    echo "${red}stopping container ${var}$container_name${reset}"
    container_command stop "$container_name" >/dev/null
fi
echo "${message}checking if a container named $container_name exists ...${reset}"
if container_command ps -aqf "name=$container_name" | grep -q .; then
    echo "${red}deleting container ${var}$container_name${reset}"
    container_command rm "$container_name" >/dev/null
fi

# sleep for one second, otherwise docker will fail because the port is already in use
sleep 1
echo "${green}creating and starting container ${var}$container_name${message} ...${reset}"
container_id=$(container_command run --name $container_name -e POTGRES_DB=$db_name -e POSTGRES_PASSWORD=$db_password -e POSTGRES_USER=rallye -p $db_port:5432 -d postgres:$postgres_version -c max_connections=500)

echo "${message}ID of new $container_name container: ${var}$container_id${reset}"
