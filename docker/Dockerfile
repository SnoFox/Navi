FROM openjdk:9-jre
MAINTAINER Josh "SnoFox" Johnson

WORKDIR /build
RUN wget -O maven.tar.gz http://www-us.apache.org/dist/maven/maven-3/3.5.3/binaries/apache-maven-3.5.3-bin.tar.gz \
    && tar -xf maven.tar.gz \
    && mv apache-maven-* maven
ARG branch=master
RUN wget -O navi.zip https://github.com/SnoFox/Navi/archive/${branch}.zip \
    && unzip navi.zip \
    && cd Navi-${branch} \
    && /build/maven/bin/mvn -Dmaven.repo.local=/build/m2/repository install \
    && mv target/*-jar-with-dependencies.jar /opt/navi.jar \
    && rm -rf /build
WORKDIR /

# config dir
VOLUME /volumes/config

ADD init.sh /init.sh
RUN chmod +x /init.sh

CMD ["/init.sh"]
