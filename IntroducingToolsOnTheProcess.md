# Utilización de herramientas de análisis estático ⌚ 🧱 📐

## Herramienta seleccionada 🧭

La herramienta seleccionada para la aplicación de modelos de Calidad cómo SQALE fue SonarQube en su versión Cloud SonarCloud.

[SonarCloud Site](https://sonarcloud.io/)

SonarCloud fue seleccionada del grupo de aplicaciones por su facilidad de ser integrada a entornos de proyectos GitHub, 
sus mecanismos de análisis recaen en la creación de flujos de trabajo con GitHubActions lo que permite integrar la herramienta al flujo de CI/CD (Continuous Integration / Continuous Delivery).

Estos mecanismos de análisis de código e implementación en el proyecto serán explicados en esta sección. 

### Vínculo al estado del último análisis  🔍

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-orange.svg)](https://sonarcloud.io/summary/new_code?id=sc-martinez_Player)

### Preparación de la solución 🔧
Para integrar la herramienta de análisis sonarcloud, es necesaria la creación de un Flujo de trabajo en GitHub Actions, para ello 
se añade la definición de este nuevo _**Job**_ en la ruta [`.github/workflows/build.yml`](https://github.com/sc-martinez/Player/blob/master/.github/workflows/build.yml).


### *SonarCloud Job YML*
<details><summary>Detalle del Job</summary>
<p>

#### Encontrado en test/Model/Model.YoutubeLauncherTest.java

```yml
name: Build
on:
  push:
    branches:
      - master
  pull_request:
    types: [opened, synchronize, reopened]
jobs:
  build:
    name: Build
    runs-on: ubuntu-latest
    strategy:
      matrix:
        database-name:
          - player
        database-password:
          - password
        database-user:
          - postgres
        database-host:
          - 127.0.0.1
        database-port:
          - 5432
    services:
      postgres:
        image: postgres:latest
        env:
          POSTGRES_DB: ${{ matrix.database-name }}
          POSTGRES_USER: ${{ matrix.database-user }}
          POSTGRES_PASSWORD: ${{ matrix.database-password }}
        ports:
          - 5432:5432
        # Set health checks to wait until postgres has started
        options:
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
    steps:
      - uses: actions/checkout@v2
        with:
          fetch-depth: 0  # Shallow clones should be disabled for a better relevancy of analysis
      - name: Set up JDK 11
        uses: actions/setup-java@v1
        with:
          java-version: 11
      - name: Cache SonarCloud packages
        uses: actions/cache@v1
        with:
          path: ~/.sonar/cache
          key: ${{ runner.os }}-sonar
          restore-keys: ${{ runner.os }}-sonar
      - name: Cache Maven packages
        uses: actions/cache@v1
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-m2
      - name: Setup database
        run:
          psql -f dockerYamls/PostgreSQL/sql/compose_database.sql postgresql://postgres:password@localhost:5432/player

      - name: Build and analyze
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}  # Needed to get PR information, if any
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run:
          sudo apt-get install xvfb &&
          Xvfb :99 &>/dev/null & export DISPLAY=":99" &&
          mvn clean install -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=sc-martinez_Player
        
```
</details></p>

Este **Job** interceptará todas las operaciones de _**commit**_ en el repositorio de código fuente, ejecutará el **_build_** de la solución usando **_Maven_**. 

### Consideraciones del Job  🔄

Las pruebas unitarias de esta solución tienen una dependencia fuerte con una fuente de datos PostGreSQL. Para ajustar este requerimiento de pruebas, se ha decidido instanciar un servicio Docker de postgresQl que soportará la ejecución de la suite de pruebas de unidad. 

```yml
strategy:
  matrix:
    database-name:
      - player
    database-password:
      - password
    database-user:
      - postgres
    database-host:
      - 127.0.0.1
    database-port:
      - 5432
  services:
    postgres:
      image: postgres:latest
      env:
        POSTGRES_DB: ${{ matrix.database-name }}
        POSTGRES_USER: ${{ matrix.database-user }}
        POSTGRES_PASSWORD: ${{ matrix.database-password }}
      ports:
        - 5432:5432
      # Set health checks to wait until postgres has started
      options:
        --health-cmd pg_isready
        --health-interval 10s
        --health-timeout 5s
        --health-retries 5
```
En medio de la instanciación del servicio de base de datos se realiza la ejecución del script [`dockerYamls/PostgreSQL/sql/compose_database.sql`](https://github.com/sc-martinez/Player/blob/master/dockerYamls/PostgreSQL/sql/compose_database.sql). Encargado de inicializar la base de datos estructuralmente. 
#### Porción de la definición que inicializa la base de datos
```yml
  ...
    
  - name: Setup database
  run:
    psql -f dockerYamls/PostgreSQL/sql/compose_database.sql postgresql://postgres:password@localhost:5432/player
```

### Ejecución de Unit Tests ✔

La sección de análisis se realiza con **Maven** con una configuración particular, pues al tratarse de una aplicación que integra elementos de GUI de JavaFX, esta debe ejecutarse en un entorno con soporte de **_Display_** que renderizé el entorno gráfico final. 

Esto no es bueno para nuestro proceso, pues la construcción de la solución y ejecución de las pruebas de unidad se ejecutan en un servidor headless (Sin GUI).

### WorkAround 💢

La ejecución de las pruebas de unidad al realizarese en un entorno Linux con distribución Ubuntu permite el uso de librerias que simulan entornos gráficos virtuales por medio de la consola de comandos. Esta solución añade soporte a la automatización de pruebas para aplicaciones con entornos Gráficos tipo desktop.


Para lograr este objetivo, se hizo uso de la librería [XVFB](https://www.x.org/releases/X11R7.6/doc/man/man1/Xvfb.1.xhtml). Que básicamente simula un framebuffer utilizando memoria virtual, orientado a sistemas que no cuentan con dispositivos de entrada o hardware que permita renderizar componentes gráficos

### Poniendo todo junto 🔀

Se integra la solución de usar XVFB en el workflow que automatiza la construcción de la solución. 

```yml
 - name: Build and analyze
     env:
       GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}  # Needed to get PR information, if any
       SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
     run:
       sudo apt-get install xvfb &&
       Xvfb :99 &>/dev/null & export DISPLAY=":99" &&
       mvn clean install -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=sc-martinez_Player
```
Esto permite que nuestra instancia de **_Ubuntu_** que realiza las operaciones de análisis de la solución sea capaz de ejecutar las pruebas de unidad, consolidar los resultados y enviar los mismos a SonarCloud para revisión posterior. 

### Ejemplo de estado de ejecución del Job ⚙

#### 1. Overview 

![img.png](Resources/JobOverview.png)

#### 2. Unit Tests

![img_1.png](Resources/JobTests.png)

#### 3. Push to SonarCloud 

![img_2.png](Resources/PushToSonarCloud.png)


### Primer análisis en SonarCloud ☁

El primer análisis que se hizo en sonar cloud arrojó un porcentaje de 0% en coverage a pesar de que el proyecto ya contaba con pruebas de unidad, esto se debe a que SonarCloud espera los resultados de pruebas de coverage en formatos conocidos cómo JaCoCo.

![img_3.png](Resources/firstAnalisys.png)

### Introduciendo indicador de coverage

Una de las maneras de incluir el análisis de coverage en un proyecto de **Java** con **Maven** es añadir el plugin de Jacoco en nuestro [`pom.xml`](https://github.com/sc-martinez/Player/blob/master/pom.xml).

Este plugin interceptará el proceso de pruebas de la aplicación en la fase de **Build** y generará el reporte de la solución en la ubicación `/target/coverage-reports` de la instancia del Job que se encuentra ejecutando el _**Build**_.

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.7</version>
    <executions>
        <execution>
            <id>default-prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
            <configuration>
                <destFile>${project.build.directory}/coverage-reports/jacoco.exec</destFile>
                <propertyName>surefireArgLine</propertyName>
            </configuration>
        </execution>
        <execution>
            <id>default-report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
            <configuration>
                <dataFile>${project.build.directory}/coverage-reports/jacoco.exec</dataFile>
                <outputDirectory>${project.reporting.outputDirectory}/jacoco</outputDirectory>
            </configuration>
        </execution>
        <execution>
            <id>default-check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>COMPLEXITY</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Segundo análisis en SonarCloud ☁

Ahora que ya hemos incluido nuestra métrica de coverage en el proyecto, veremos algunas de las oportunidades de calidad en nuestra solución. 

![img_4.png](Resources/SonarCloudSummary.png)


## Composición de dashboard del estado de la calidad

Se añade un dashboard en la sección principal del archivo [Readme.md](https://github.com/sc-martinez/Player/blob/master/README.md) que le permitirá al equipo de desarrollo monitorear la calidad de la última versión analizada de la solución. 

![img_5.png](Resources/QualityMeasures.png)