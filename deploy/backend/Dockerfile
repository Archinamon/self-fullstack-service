FROM centos/systemd

MAINTAINER "Eduard `archinamon` Obolenskiy" <archinamon@gmail.com>

ARG servicePath=deploy/backend/
ARG kexePath=build/bin/server.kexe

COPY ${kexePath} /app/server.kexe
COPY ${servicePath}/archinamon.tcp.service /lib/systemd/user/archinamon.tcp.service
COPY ${servicePath}/archinamon.tcp.socket /lib/systemd/user/archinamon.tcp.socket

EXPOSE 80

CMD systemctl --user daemon-reload
CMD systemctl --user start archinamon.tcp.socket

CMD ["/usr/sbin/init"]
