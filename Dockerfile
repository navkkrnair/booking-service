FROM openjdk:8-jdk-alpine
MAINTAINER "navkkrnair@gmail.com"
ENV APPROOT="/usr/nobody"
WORKDIR $APPROOT    
ADD target/booking-service-1.0.jar $APPROOT
EXPOSE 8080
USER nobody
ENTRYPOINT ["java"]
CMD ["-jar","-Xmx256m","-Xms256m","-Djava.security.egd=file:/dev/./urandom", "/usr/nobody/booking-service-1.0.jar"]
