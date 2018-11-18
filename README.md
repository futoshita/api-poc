# API poc

Preuve de concept d'une API RESTful et d'un SDK développés en Java.

* validation des entités
* internationalisation des erreurs
* OAuth 1 (WIP)

## Pré-requis

Installer le serveur d'application [Tomcat 8](https://tomcat.apache.org/download-80.cgi) localement.

Modifier la propriété `LOG_DIR` dans le fichier `api-server⁩/⁨src⁩/⁨main⁩/⁨resources⁩/app.properties` pour indiquer le répertoire dans lequel stocker les logs de l'API.

## Installation

```
mvn clean install -DskipTests
cp ./api-server/target/api-server.war $/path/to/tomcat/webapps/dir/$
```

## Exécution des tests

Le SDK contient les tests d'intégration.

```
mvn test -f api-sdk/pom.xml -Dtest=IntegrationTestingFactory
```

## Built With

* [Jersey](https://jersey.github.io) - RESTful Web Services in Java
* [Hibernate Validator](http://hibernate.org/validator/) - The Bean Validation reference implementation
* [Maven](https://maven.apache.org/) - Dependency Management

## À venir

* OAuth 1
* monitoring des appels à l'API