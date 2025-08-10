FROM ubuntu:latest
LABEL authors="tlseh"

ENTRYPOINT ["top", "-b"]