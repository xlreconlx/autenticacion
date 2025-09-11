# Imagen base de Java 21 (Temurin es la distribución recomendada)
FROM eclipse-temurin:21-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el JAR generado por Maven/Gradle (target/*.jar o build/libs/*.jar)
COPY target/*.jar app.jar

# Exponer el puerto en el que corre Spring Boot
EXPOSE 8081

# Comando para ejecutar el microservicio
ENTRYPOINT ["java", "-jar", "app.jar"]