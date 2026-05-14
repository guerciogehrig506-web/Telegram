FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY tsconfig.json nest-cli.json ./
COPY src ./src
COPY prisma ./prisma

RUN npx prisma generate
RUN npm run build

FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY --from=build /app/dist ./dist
COPY --from=build /app/node_modules/.prisma ./node_modules/.prisma

COPY prisma ./prisma
COPY views ./views
COPY public ./public
COPY docker ./docker

EXPOSE 3000

CMD ["node", "dist/main.js"]