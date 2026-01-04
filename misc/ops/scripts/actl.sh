#!/bin/sh

PROCESS_NAME="$2"
INSTANCE_ID="$3"

SERVICE_UNIT="aero-artio-suite@${PROCESS_NAME}-${INSTANCE_ID}.service"
APP_DIR="/opt/apps/${PROCESS_NAME}-${INSTANCE_ID}"
SERVICE_TEMPLATE="/etc/systemd/system/aero-artio-suite@.service"

# Validate input
if [ -z "$PROCESS_NAME" ] || [ -z "$INSTANCE_ID" ]; then
  echo "❌ PROCESS_NAME and INSTANCE_ID are required. Usage: actl <command> <process> <instance>"
  exit 1
fi

# Validate process name
case "$PROCESS_NAME" in
  md|fe) ;;
  *) echo "❌ Invalid process name: $PROCESS_NAME (allowed: md - media driver, fe - fix engine)"; exit 1 ;;
esac

# Commands
start_app() {
  echo "🚀 Starting $SERVICE_UNIT..."
  sudo systemctl start "$SERVICE_UNIT"
}

stop_app() {
  echo "🛑 Stopping $SERVICE_UNIT..."
  sudo systemctl stop "$SERVICE_UNIT"
}

restart_app() {
  echo "🔁 Restarting $SERVICE_UNIT..."
  sudo systemctl restart "$SERVICE_UNIT"
}

status_app() {
  echo "📋 Status of $SERVICE_UNIT:"
  sudo systemctl status "$SERVICE_UNIT"
  echo "✅ Environment File: $APP_DIR/.env"
  cat "$APP_DIR/.env" 2>/dev/null || echo "⚠️ No .env found"
  echo ""
}

logs_app() {
  echo "📄 Tailing systemd logs for $SERVICE_UNIT:"
  tail -f $APP_DIR/logs/app.log
}

enable_app() {
  echo "🔓 Enabling $SERVICE_UNIT..."
  sudo systemctl enable "$SERVICE_UNIT"
  echo "✅ $SERVICE_UNIT is enabled and will start on reboot."
}

disable_app() {
  echo "🛑 Disabling $SERVICE_UNIT..."
  sudo systemctl disable "$SERVICE_UNIT"
  echo "✅ $SERVICE_UNIT is disabled and will not start on reboot."
}

print_help() {
  echo "Usage: actl <command> <process> <instance>"
  echo "Commands:"
  echo "  start      Start the service for instance"
  echo "  stop       Stop the service for instance"
  echo "  restart    Restart the service for instance"
  echo "  status     Show status and .env for instance"
  echo "  logs       Tail systemd logs for instance"
  echo "  disable    Disable the service for instance (prevent starting on reboot)"
  echo "  enable     Enable the service for instance (start on reboot)"
  echo ""
  echo "Examples:"
  echo "  botctl start md 1"
  echo "  botctl start fe 1"
  echo "  botctl restart md 1"
  echo "  botctl enable fe 2"
}

case "$1" in
  start)
    start_app
    ;;
  stop)
    stop_app
    ;;
  restart)
    restart_app
    ;;
  status)
    status_app
    ;;
  logs)
    logs_app
    ;;
  disable)
    disable_app
    ;;
  enable)
    enable_app
    ;;
  *)
    echo "❌ Unknown command: $1 (allowed: start, stop, restart, status, logs, disable, enable)"
    exit 1
    ;;
esac
