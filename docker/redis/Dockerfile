FROM redis:latest

CMD ["mkdirt -p /var/lib/redis"]
COPY redis.conf /usr/local/etc/redis/redis.conf
CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]

EXPOSE 6379