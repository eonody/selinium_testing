FROM ubuntu:24.04

# Install dependencies
RUN apt-get update && apt-get install -y curl unzip wget gnupg2 software-properties-common \
    fonts-dejavu fonts-liberation fonts-noto

ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# Install Java
RUN add-apt-repository ppa:openjdk-r/ppa && \
    apt-get update && \
    apt-get install -y openjdk-8-jdk

# Install Gradle
ENV GRADLE_VERSION=7.4
RUN wget -q https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip && \
    unzip gradle-$GRADLE_VERSION-bin.zip && \
    rm gradle-$GRADLE_VERSION-bin.zip && \
    mv gradle-$GRADLE_VERSION /opt/gradle && \
    ln -s /opt/gradle/bin/gradle /usr/bin/gradle

# Set environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
ENV GRADLE_HOME=/opt/gradle

# Add Gradle and Java to PATH
ENV PATH=$PATH:$GRADLE_HOME/bin:$JAVA_HOME/bin

RUN apt-get update && apt-get install -y sudo

ARG uid
ARG UNAME=selenium

RUN usermod -l $UNAME ubuntu && \
    usermod -d /home/$UNAME -m $UNAME && \
    groupmod -n $UNAME ubuntu && \
    usermod -aG sudo $UNAME && \
    usermod -g $UNAME $UNAME && \
    if ! id -u $UID >/dev/null 2>&1; then \
        usermod -u $UID $UNAME; \
    fi && \
    chown -R $UNAME:$UNAME /home/$UNAME

ENV TZ=Europe/Budapest
ENV DEBIAN_FRONTEND=noninteractive
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

USER $UNAME
WORKDIR /home/$UNAME
ENV HOME=/home/$UNAME

CMD ["/bin/bash"]