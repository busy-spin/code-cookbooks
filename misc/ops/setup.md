# sudo visudo -f /etc/sudoers.d/reboot_nopasswd_artio

## No password for secured use

artio ALL=(ALL) NOPASSWD: /sbin/reboot
artio ALL=(ALL) NOPASSWD: /sbin/shutdown
artio ALL=(ALL) NOPASSWD: /bin/systemctl
artio ALL=(ALL) NOPASSWD: /bin/journalctl

## No password for all

artio ALL=(ALL) NOPASSWD: ALL