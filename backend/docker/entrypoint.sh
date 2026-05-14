#!/bin/sh
set -e

echo "[Trio] Starting deployment..."

# Wait for PostgreSQL to be ready
until pg_isready -h db -p 5432 -U trio 2>/dev/null; do
  echo "[Trio] Waiting for PostgreSQL..."
  sleep 2
done

echo "[Trio] PostgreSQL is ready, running migration..."

# Switch to PostgreSQL schema
cp /app/docker/schema.prisma /app/prisma/schema.prisma

# Generate Prisma client with PostgreSQL
npx prisma generate

# Push schema to database
npx prisma db push --accept-data-loss

# Create admin user if not exists
node -e "
const { PrismaClient } = require('@prisma/client');
const bcrypt = require('bcrypt');
(async () => {
  const p = new PrismaClient();
  const existing = await p.user.findUnique({ where: { email: 'admin@trio.app' } });
  if (!existing) {
    const hash = await bcrypt.hash(process.env.ADMIN_PASSWORD || 'admin123', 10);
    await p.user.create({
      data: {
        username: 'admin',
        email: 'admin@trio.app',
        password: hash,
        role: 'ADMIN',
      },
    });
    console.log('[Trio] Admin user created');
  } else {
    await p.user.update({
      where: { email: 'admin@trio.app' },
      data: { role: 'ADMIN' },
    });
    console.log('[Trio] Admin user updated');
  }
  await p.\$disconnect();
})();
"

echo "[Trio] Starting server..."
exec node dist/main.js