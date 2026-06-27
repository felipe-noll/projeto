FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

RUN apk add --no-cache maven

# Copia o parent pom na raiz
COPY pom.xml ./pom.xml

# Copia o módulo auth-service mantendo a estrutura de diretório
COPY auth-service/pom.xml ./auth-service/pom.xml
COPY auth-service/src ./auth-service/src

# Cria diretório vazio do financeiro-service para o parent pom não reclamar
RUN mkdir -p ./financeiro-service && echo '<project><modelVersion>4.0.0</modelVersion><parent><groupId>com.example</groupId><artifactId>projeto-parent</artifactId><version>0.0.1-SNAPSHOT</version></parent><artifactId>financeiro-service</artifactId></project>' > ./financeiro-service/pom.xml

RUN mvn clean package -DskipTests -pl auth-service -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/auth-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
