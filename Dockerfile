FROM openjdk:8-jdk-alpine
VOLUME /tmp
#EXPOSE 8080

ARG JAR_FILE

RUN mkdir -p /app/
RUN mkdir -p /app/logs
COPY ./target/${JAR_FILE} /app/app.jar
COPY ./entrypoint.sh ./app/entrypoint.sh

RUN chmod +x /app/entrypoint.sh
CMD ["/app/entrypoint.sh"]