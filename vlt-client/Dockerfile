FROM node:22.14.0

LABEL authors="wladischlau"

WORKDIR /usr/src/vlt-client

COPY package.json .

RUN npm install
RUN npm i -g serve

COPY . .

RUN npm run build

EXPOSE 3000

CMD [ "serve", "-s", "dist" ]