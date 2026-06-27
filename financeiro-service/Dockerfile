FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

RUN apk add --no-cache maven

# Copia o parent pom na raiz
COPY pom.xml ./pom.xml

# Copia o módulo financeiro-service mantendo a estrutura de diretório
COPY financeiro-service/pom.xml ./financeiro-service/pom.xml
COPY financeiro-service/src ./financeiro-service/src

# Cria diretório vazio do auth-service para o parent pom não reclamar
RUN mkdir -p ./auth-service && echo '<project><modelVersion>4.0.0</modelVersion><parent><groupId>com.example</groupId><artifactId>projeto-parent</artifactId><version>0.0.1-SNAPSHOT</version></parent><artifactId>auth-service</artifactId></project>' > ./auth-service/pom.xml

RUN mvn clean package -DskipTests -pl financeiro-service -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/financeiro-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
