FROM postgres:15-alpine

# Tạo thư mục chứa file SQL để tránh Docker tự động chạy bừa
RUN mkdir -p /db-scripts

# Copy các file SQL vào thư mục tạm
COPY hr_backup.sql /db-scripts/
COPY seed-data.sql /db-scripts/

# CHỈ copy duy nhất file init-db.sh vào thư mục tự chạy của Docker
COPY init-db.sh /docker-entrypoint-initdb.d/
RUN chmod +x /docker-entrypoint-initdb.d/init-db.sh