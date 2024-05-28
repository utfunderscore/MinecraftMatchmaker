FROM eclipse-temurin:21-jdk-jammy as deps
LABEL authors="connor"

WORKDIR /server

COPY server-start.sh server-start.sh

EXPOSE 25565

CMD ["ls"]

ENTRYPOINT ["sh", "server-start.sh"]