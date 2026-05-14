FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm ci --omit=dev

COPY dist ./dist
COPY prisma ./prisma
COPY views ./views
COPY public ./public
COPY docker ./docker

EXPOSE 3000

ENTRYPOINT ["sh", "/app/docker/entrypoint.sh"]