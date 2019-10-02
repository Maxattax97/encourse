FROM node:carbon-jessie-slim
# Needs to be x64 node v8 on glibc (not musl -- as in no Alpine image).

RUN npm install -g serve

ADD ./entrypoint.sh /entrypoint.sh

EXPOSE 5000

ENTRYPOINT ["/entrypoint.sh"]
