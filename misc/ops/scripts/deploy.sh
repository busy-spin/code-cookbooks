#!/bin/bash

# === Paths ===
SERVER_ID=artio
DEMO_PROJECT=artio-cookbook

BASEDIR="$(cd "$(dirname "$0")" && pwd)"
PARENTDIR="$(cd "$BASEDIR/../../../" && pwd)"
FILESDIR="$(cd "$BASEDIR/../__files" && pwd)"
SERVICE_FILE=aeron-artio-suite@.service

APP_DIR="/opt/apps"
JAR_PATH="$PARENTDIR/${DEMO_PROJECT}/target/${DEMO_PROJECT}.jar"

scp -r "$FILESDIR/env" "artio:$APP_DIR"
scp "$FILESDIR/$SERVICE_FILE" "$SERVER_ID:/tmp"
scp "$BASEDIR/actl.sh" "$SERVER_ID:/tmp/actl"
ssh "$SERVER_ID" "sudo cp /tmp/actl /usr/local/bin/actl; sudo chmod +x /usr/local/bin/actl"
ssh "$SERVER_ID" "sudo cp /tmp/$SERVICE_FILE /etc/systemd/system"
ssh "$SERVER_ID" "sudo systemctl daemon-reload"

# === Step 4: Build JAR ===
echo "🛠 Building project..."
mvn clean install -f $PARENTDIR/${DEMO_PROJECT}

scp "$JAR_PATH" "$SERVER_ID:$APP_DIR"

# === Step 6: Trigger remote deployment ===
echo "🚀 Deployment complete"