#!/bin/bash
# This script exports tani-bilet realm from the container and save it to the local realm.json file
# Use when you have modified the keycloak realm and want your changes to be persistent and available for other devs

if command -v docker &> /dev/null; then
    CONTAINER_TOOL="docker"
elif command -v podman &> /dev/null; then
    CONTAINER_TOOL="podman"
else
    echo "❌ Neither Docker nor Podman is installed!"
    exit 1
fi

echo "✅ Using: $CONTAINER_TOOL"
$CONTAINER_TOOL exec keycloak /bin/bash -c 'cd opt/keycloak/bin && ./kc.sh export --file realm.json --realm io-project -v'
$CONTAINER_TOOL cp keycloak:/opt/keycloak/bin/realm.json ./realm.json
