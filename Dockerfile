FROM openjdk:9-jre as build
WORKDIR /build
RUN wget -O maven.tar.gz http://www-us.apache.org/dist/maven/maven-3/3.5.3/binaries/apache-maven-3.5.3-bin.tar.gz \
    && tar -xf maven.tar.gz \
    && mv apache-maven-* maven
COPY pom.xml .
RUN /build/maven/bin/mvn -Dmaven.repo.local=/build/m2/repository dependency:go-offline
COPY src src
RUN /build/maven/bin/mvn -Dmaven.repo.local=/build/m2/repository install

FROM openjdk:9-jre
MAINTAINER Josh "SnoFox" Johnson

RUN groupadd -g 99 navi && useradd -MNu 99 -g 99 -s /usr/sbin/nologin navi
VOLUME /volumes/config

COPY --from=build /build/target/*-jar-with-dependencies.jar /opt/navi.jar
COPY docker/init.sh /init.sh
RUN chmod +x /init.sh

CMD ["/init.sh"]
