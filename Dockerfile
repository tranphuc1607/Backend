# ---------- Stage 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copy pom truoc de Docker cache lop dependency,
# chi tai lai khi pom.xml thay doi
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Chay bang user thuong thay vi root
RUN useradd -r -u 1001 -m appuser

# Thu muc luu anh upload. Tao san va cap quyen cho appuser truoc khi doi user,
# vi appuser khong ghi duoc vao /app do root so huu.
RUN mkdir -p /app/uploads && chown -R appuser:appuser /app

USER appuser

COPY --from=build /build/target/drink-order-api-*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
