# sudo visudo -f /etc/sudoers.d/reboot_nopasswd_artio
artio ALL=(ALL) NOPASSWD: /sbin/reboot
artio ALL=(ALL) NOPASSWD: /sbin/shutdown
artio ALL=(ALL) NOPASSWD: /bin/systemctl