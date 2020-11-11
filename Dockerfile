FROM adoptopenjdk/openjdk11:jre-11.0.8_10-alpine
MAINTAINER Timofey Volkov <timofey.volkov@gmail.com>

ENV APP_HOME /opt/app

RUN mkdir -p $APP_HOME

COPY build/libs/* $APP_HOME
COPY entrypoint.sh $APP_HOME


WORKDIR $APP_HOME

EXPOSE 3000

RUN apk add --no-cache wget
RUN apk add bash
RUN mkdir -p /opt/init
RUN wget -O /opt/init/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.2/dumb-init_1.2.2_x86_64
RUN chmod +x /opt/init/dumb-init

ENTRYPOINT ["/opt/init/dumb-init", "--"]
CMD ["bash", "-c", "/opt/app/entrypoint.sh"]
