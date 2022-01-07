# Manual de contribución

## Tabla de contenido

- [1. Introducción](#1-introducción)
- [2. El día a día de un desarrollador](#2-el-día-a-día-de-un-desarrollador)
- [3. Estructura del proyecto](#3-estructura-del-proyecto)
- [4. Preparando el entorno de desarrollo](#4-preparando-el-entorno-de-desarrollo)
- [5. Desplegando y probando la aplicación localmente](#5-desplegando-y-probando-la-aplicación-localmente)
- [6. Cambios que no forman parte de tareas](#6-cambios-que-no-forman-parte-de-tareas)
- [7. Convenciones de estilo](#7-convenciones-de-estilo)
- [7.1. Estilo de confirmaciones](#71-estilo-de-confirmaciones)
- [7.2. Estilo del código fuente](#72-estilo-del-código-fuente)
- [8. Publicaciones](#8-publicaciones)
  - [8.1. Proceso para publicar una nueva versión menor o de parche](#81-proceso-para-publicar-una-nueva-versión-menor-o-de-parche)
    - [8.1.1. Proceso para publicar una nueva versión menor o de parche con rama temporal de preparación de publicación](#811-proceso-para-publicar-una-nueva-versión-menor-o-de-parche-con-rama-temporal-de-preparación-de-publicación)
    - [8.1.2. Proceso para publicar una nueva versión menor o de parche sin rama temporal de preparación de publicación](#812-proceso-para-publicar-una-nueva-versión-menor-o-de-parche-sin-rama-temporal-de-preparación-de-publicación)
  - [8.2. Proceso para publicar una nueva versión mayor](#82-proceso-para-publicar-una-nueva-versión-mayor)
  - [8.3. Proceso para incorporar cambios y hacer nuevas publicaciones de versiones mayores en mantenimiento](#83-proceso-para-incorporar-cambios-y-hacer-nuevas-publicaciones-de-versiones-mayores-en-mantenimiento)

## 1. Introducción

Este proyecto se desarrolla en entornos que emplean Docker para conseguir
integración y despliegue continuo. Cada confirmación (_commit_) genera una
imagen de Docker que se despliega en un repositorio Docker Hub de preproducción.
Cuando ocurre una publicación (_release_), el despliegue se hace a un
repositorio Docker Hub de producción, que pone a disposición del cliente la
nueva versión de la aplicación. Estos procesos están totalmente automatizados y
definidos en ficheros de descripción de flujos de trabajo de GitHub Actions y en
el POM del proyecto.

Este documento describe instrucciones concretas para contribuir correctamente al
proyecto, en observancia de los entornos definidos.

## 2. El día a día de un desarrollador

Los procesos de desarrollo se han definido inspirándose en el modelo de
ramificación de [Trunk Based Development](https://trunkbaseddevelopment.com/), y
en los patrones del libro _Software Configuration Management Patterns_ de
Berczuk et al. En particular, el día a día de cada desarrollador debe seguir el
siguiente proceso:

1. Seleccionar una tarea del sprint actual de la que sea responsable en
   **Taiga**. Las tareas se crean para resolver historias de usuario o
   incidencias reportadas por éstos. Se debe de leer su descripción, historia de
   usuario o incidencia asociada, y otro material relacionado con la misma que
   pueda resultar de interés para el desarrollo.
2. Cambiar su repositorio local a la rama `master`, si no está ya en esa rama.
3. Actualizar la copia local de `master` a su última confirmación remota. Para
   esto se incorporan las últimas confirmaciones del repositorio remoto mediante
   un _pull_.
   - Esta operación nunca debe crear confirmaciones de combinación (_merge
     commits_). Para garantizar que esto sea así, los _pulls_ deben de hacerse
     mediante un _rebase_ (`git pull --rebase`), que reescribirá las
     confirmaciones locales sobre las remotas. Esto puede generar conflictos a
     resolver.
   - Si hay cambios locales en `master` que no forman parte de ninguna
     confirmación, se debe de hacer un guardado rápido (_stash_) de los cambios
     antes de hacer el _pull_, para incorporarlos en una posterior confirmación.
4. Crear una nueva rama cuyo nombre siga el formato `task-X`, donde `X` es el
   número de la tarea a resolver, y cambiarse a ella: `git checkout -b task-X`.
5. Trabajar en la tarea. El código que se añada o modifique debe incluir los
   correspondientes cambios a las pruebas de unidad e integración.
6. Ejecutar la fase `verify` de Maven en el proyecto raíz (`./mvnw verify`).
   Esto construirá el proyecto, su imagen de **Docker**, y ejecutará las pruebas
   de unidad e integración. Este comando se debe completar exitosamente para dar
   por terminada una tarea.
   - Si la tarea aún no se ha terminado (`./mvnw verify` no se completa con
     éxito), pero se han desarrollado artefactos meritorios de publicar
     igualmente, se puede seguir con este proceso.
7. Crear una o varias confirmaciones con los cambios, dependiendo de su ámbito.
   Cada confirmación debería de poder construirse con éxito por separado y
   encargarse de un solo asunto o faceta de la tarea.
   - Véase la [sección 7](#7-convenciones-de-estilo) de este documento para
     conocer el formato que debe tener cada confirmación.
8. Enviar los cambios en la nueva rama al repositorio remoto.
9. Crear una nueva _pull request_ (PR) en **GitHub**, con el título `[Task X] Y`, donde `X`
    es el número de la tarea a resolver e `Y` su título. Explicar en el cuerpo
    de la PR detalles que puedan resultar de interés para quien la revise (por
    qué se ha implementado algo de una forma y no otra, dificultades
    encontradas, decisiones tomadas, trabajo pendiente, etc.). La PR debe tener
    como objetivo la rama `master`.
   - Si la tarea está terminada (ha pasado `./mvnw verify` localmente), pero no
     ha pasado las comprobaciones de **GitHub Actions**, se deben corregir las
     causas del fallo modificando las confirmaciones de la PR (`git commit
     --amend`).
   - Si la tarea no está terminada (no ha pasado `./mvnw verify` localmente),
     marcar la PR creada como borrador (_draft_). Esto señalará su carácter no
     terminado a otros desarrolladores y evitará su combinación con la rama
     principal. Se debería de indicar en el cuerpo de la PR qué queda por hacer
     y por qué se ha publicado. Las PR borrador deben de terminarse en algún
     momento, pasando a ser PR no borrador que pasen `./mvnw verify` localmente,
     o ser borradas si no se van a terminar.
10. _(Solo para PRs no borrador)_ Preparar PRs propias o de otros
    desarrolladores para su combinación con `master`. Esto implica:
    - Asegurar que sus confirmaciones se aplican sobre la última confirmación de
      `master`. Esto implica hacer _rebase_ de la rama de la PR sobre `master`
      si hay cambios, lo que puede llevar a tener que resolver conflictos
      modificando sus confirmaciones (`git commit --amend`). En ningún caso esto
      debe debe causar que se deje de pasar `./mvnw verify` o las comprobaciones
      de **GitHub Actions**. Si accidentalmente falla alguna comprobación, las
      confirmaciones deben modificarse para arreglarlo.
    - Solicitar su revisión por otros miembros del equipo, si y solo si dada la
      amplitud de los cambios o la política de la organización se estima
      necesario.
    - Revisar las PRs de otros miembros del equipo, en caso de que éstas hayan
      solicitado revisión.
11. _(Solo para PRs no borrador)_ Combinar las PRs preparadas con `master`
    (_merge_). Esta combinación debe hacerse mediante la opción "Squash and
    merge" de la interfaz web de **GitHub**, que combinará todas las confirmaciones
    de la PR en una sola en `master`, generando automáticamente un mensaje de
    confirmación apropiado.
    - En todo caso, las PRs que se combinen con `master` deben pasar las
      comprobaciones de **GitHub Actions**.
12. _(Solo para PRs no borrador)_ Borrar la rama creada para la PR ya combinada
    con `master` (esta rama es una _short-lived feature branch_).
13. Actualizar el estado de la tarea correspondiente en **Taiga**.
14. Volver a empezar en el paso 1 hasta que se termine la jornada laboral.

El propósito de este proceso es darle a cada desarrollador un espacio
potencialmente público de trabajo en el que ir guardando los artefactos que
genere, mientras se garantiza que `master` contiene en todo momento código
estable que se puede usar como punto de partida para las tareas, y que existe un
lugar en el que los desarrolladores pueden discutir los cambios implementados
para una tarea incluso después de que se dé por terminada (útil si hay que
revertirla o similares).

Para que haya pocos conflictos de cambios y el proceso se mantenga ágil, es
conveniente que las tareas sean de grano bastante fino, tal que un desarrollador
promedio pueda completarlas en menos de una jornada, contabilizando el tiempo
desde que empieza a trabajar en ella hasta que su PR se combina con `master`. La
frecuencia de confirmaciones a `master` debe mantenerse constante a lo largo de
la semana. Mientras se esté trabajando en la rama de una PR, no pasa nada porque
las comprobaciones de la integración continua fallen, siempre y cuando ello se
acabe corrigiendo como se ha indicado.

En caso de que una tarea tarde mucho en completarse y sea una dependencia
crítica para otros desarrolladores que no puede esperar, puede recurrirse al uso
de la técnica de [_branch by
abstraction_](https://trunkbaseddevelopment.com/branch-by-abstraction/).

## 3. Estructura del proyecto

Este proyecto está estructurado en varios módulos de Maven:

* **domain**: módulo que contiene las clases del dominio (entidades).
* **service**: módulo que contiene los EJB del sistema, que serán utilizados
  tanto por la capa web. Aquí se hacen los controles de acceso.
* **web**: módulo que contiene la capa de servicios REST y la capa JSF. Define
  en su POM cómo generar imágenes de Docker y desplegar la aplicación con él.
  También contiene pruebas de integración con **Arquillian**.

**docker-compose.yml** es un fichero que define cómo desplegar localmente la
última imagen Docker del entorno de preproducción generada por una confirmación
a `master`, útil para QA. Para usarlo, basta con ejecutar el comando
`docker-compose up`. Este fichero puede modificarse para desplegar las
imágenes generadas por cambios en otras ramas. Las etiquetas de las imágenes del
repositorio de preproducción siguen el formato `<evento>-<rama>-latest`.

El siguiente diagrama muestra la estructura del entorno en el que vive el
proyecto, relacionándolo con las diversas tecnologías y herramientas usadas.

![Diagrama del entorno](Representaci%C3%B3n%20gr%C3%A1fica%20del%20entorno.svg?raw=true)

## 4. Preparando el entorno de desarrollo

Es necesario que cada desarrollador disponga de las siguientes herramientas
antes de empezar a trabajar:

- **Un IDE**. Se recomienda usar _Eclipse IDE for Enterprise Java and Web
  Developers_, que se puede descargar desde [este
  enlace](https://www.eclipse.org/downloads/packages/release/2021-12/r/eclipse-ide-enterprise-java-and-web-developers).
- **Java 8**. Si se usa una distribución Linux con paquetes de OpenJDK 8, lo más
  recomendable es instalar Java 8 mediante esos paquetes. Si no, o si se usa
  otro sistema operativo, se puede descargar desde
  [Adoptium](https://adoptium.net/).
- **Docker**. Lo más recomendable es seguir las [instrucciones
  oficiales](https://docs.docker.com/engine/install/), que son específicas al
  sistema operativo que se esté usando.
- **Git**. La [página de descargas oficial](https://git-scm.com/downloads)
  contiene instrucciones para obtener Git.

## 5. Desplegando y probando la aplicación localmente

Es posible desplegar la aplicación tal cual está en el repositorio local, sin
necesidad de publicar confirmación alguna, ejecutando `./mvnw integration-test`.
Si todo va bien, la aplicación debería de empezar a aceptar conexiones HTTP en
el puerto 8080. Si no, puede ser útil recordar que se pueden ver los registros
de los contenedores al ejecutar `./mvnw -Ddocker.showLogs=true
integration-test`. Para detener y eliminar los contenedores Docker creados por
estos comandos hay que ejecutar `./mvnw docker:stop -Ddocker.allContainers`.

Sin embargo, antes de desplegar la aplicación es una buena idea ejecutar sus
pruebas, implementadas con **jUnit** y **Arquillian**. Para esto puede
ejecutarse la fase `test` del proyecto de Maven con `./mvnw test`, o
equivalentemente se puede utilizar la integración con Maven proporcionada por el
IDE para tal fin.

## 6. Cambios que no forman parte de tareas

Aparte de `master` y la familia de ramas `task-X`, puede ocurrir que los
desarrolladores deseen hacer pequeños cambios al proyecto que no sean meritorios
de una tarea (_chores_), como corregir errores tipográficos, mejorar la
documentación o introducir alguna mejora menor a algún proceso. En estos casos,
los cambios deben hacerse como si fuesen una tarea más, pero en una rama cuyo
nombre siga el formato `chore-X`, donde `X` es un código en clave corto de los
cambios, con las palabras separadas por guiones (-). Por ejemplo,
`chore-update-docs`. El título de la PR asociada seguirá el formato `[Chore] X`,
en lugar de `[Task X] Y`.

## 7. Convenciones de estilo

## 7.1. Estilo de confirmaciones

Todas las confirmaciones deben seguir el estilo definido en [Conventional
Commits 1.0.0](https://www.conventionalcommits.org/es/v1.0.0/). Concretando esa
especificación:

- Los tipos en los títulos de las confirmaciones deben escribirse en minúsculas.
- Se debe usar el tipo `revert` para reversiones, junto con una nota `Refs` a
  pie de página indicando las confirmaciones revertidas.
- Las confirmaciones deben incluir en su ámbito la tarea a la que hacen
  referencia, si es aplicable, siguiendo el formato `tskX`, donde `X` es el
  número de la tarea. Ejemplo: `fix(tsk5): properly map User entity
  relationship`.
- Se debe de hacer un esfuerzo por limitar los títulos de las confirmaciones a
  50 caracteres. La longitud de la cada línea del cuerpo debe limitarse a 80
  caracteres.
- Las confirmaciones deben escribirse en inglés.

## 7.2. Estilo del código fuente

- El código debe seguir el formato del formateador predeterminado de Eclipse.
  Esto incluye usar caracteres de tabulación de cuatro espacios de ancho para el
  sangrado, un único espacio entre operadores, y el uso de la llave de apertura
  de un bloque en la misma línea donde se define.
- Cada clase debe tener un comentario Javadoc asociado que indique su propósito.
  Opcional aunque recomendablemente también se deben documentar con Javadoc sus
  métodos. El comando `./mvnw javadoc:javadoc` comprueba la validez de los
  comentarios Javadoc y debe completarse sin errores.
- Los saltos de línea siguen el formato Unix (carácter LF, \n). El último
  carácter de un fichero de código fuente debe ser LF.
- Se deben evitar las sentencias `import` comodín (p. ej. `import
  com.enterprise.business.*;`).
- Si se captura una excepción para ignorarla, debe añadirse un comentario breve
  explicando por qué se ignora. Ejemplo: `// Error condition checked below`.
- Los comentarios e identificadores (nombres de variables, clases...) deben
  estar en inglés.
- En caso de duda, debe seguirse el estilo del código ya existente.

## 8. Publicaciones

Cuando se complete una cantidad de tareas que se estime merecedora de la
publicación de una nueva versión de la aplicación, será necesario que alguien
haga ciertos preparatorios para que ésta llegue a los clientes, sea trazable, y
se pueda darle mantenimiento. Esta sección documenta los procesos necesarios,
inspirándose principalmente en los patrones de ramificación _release line_ y
_release-prep codeline_. Una parte de estos procesos está automatizada con [el
complemento de publicaciones de
Maven](https://maven.apache.org/maven-release/maven-release-plugin/index.html).

### 8.1. Proceso para publicar una nueva versión menor o de parche

En ciertos casos puede ser necesario hacer algunos cambios a la aplicación antes
de publicarla (p. ej., si QA detecta errores a última hora, hay que cambiar
alguna constante en el código fuente, el código en `master` contiene
funcionalidades experimentales o que se han aplazado a una versión posterior,
etc.). Sean estos cambios de última hora necesarios o no, existe un proceso
definido para cada ocasión que en ningún caso impedirá que los desarrolladores
sigan trabajando con normalidad en `master`.

#### 8.1.1. Proceso para publicar una nueva versión menor o de parche con rama temporal de preparación de publicación

Este proceso debe seguirse en caso de querer hacer cambios de última hora antes
de publicar la aplicación. También puede seguirse si no hay cambios de última
hora, pero no sería lo más cómodo; en este supuesto es mejor seguir el proceso
definido en la sección 8.1.2.

1. En un repositorio local con la última versión de la rama `master`
   seleccionada se ejecuta el comando `./mvnw release:branch`. Este comando
   asegurará que el repositorio local está limpio, pedirá información de la
   nueva rama temporal y versión, actualizará automáticamente los POM del
   proyecto, y creará la confirmación y rama necesaria. **Es importante** nombrar
   la nueva rama siguiendo el formato `relese-prep-vX.Y.Z`, donde `X`, `Y` y `Z`
   son los componentes del nuevo número de versión.
2. A continuación se hacen los cambios necesarios en la rama `release-prep`
   creada. Esto puede hacerse siguiendo el procedimiento habitual de _pull
   requests_, solo que en este caso se harían contra la rama temporal de
   preparación. Alternativamente, si el tiempo apremia, pueden hacerse los
   cambios directamente sin pasar por una PR, siempre y cuando pasen todas las
   comprobaciones de CI.
   - Por el momento, ningún cambio hecho en esta rama debe de pasar a `master`.
     En todo caso es posible hacer _cherry pick_ de cambios que tengan poco
     riesgo desde `master` hasta la rama de preparación de la publicación.
   - Se debe recordar que la etiqueta de la imagen de Docker que se publicará
     tendrá el sufijo `-latest` en caso de que la versión del proyecto indicada
     en los POM termine en `-SNAPSHOT`.
3. Una vez completados y publicados los preparativos necesarios en esta rama,
   que deberían de llevar poco tiempo, se crea una nueva publicación en
   **GitHub**. Ésta debe titularse `vX.Y.Z`, donde `X`, `Y` y `Z` tienen el
   significado mencionado en el paso 1, y crear una nueva etiqueta (_tag_)
   contra la rama de preparación del mismo nombre.
4. **GitHub Actions** recibirá el evento de publicación y empezará
   automágicamente el proceso de CD para subir la imagen Docker de la aplicación
   al repositorio de producción.
   - Si falla el proceso de CD, la imagen Docker no será subida al repositorio
     de producción.
5. Seguro que todo va bien, así que es hora de combinar (hacer _merge_, no
   _rebase_, para que quede constancia de la existencia de esta preparación) de
   la rama temporal de preparación a `master`. **Es importante** que se mantenga
   la historia de confirmaciones hecha en la rama temporal hasta la confirmación
   que referencia la etiqueta de la publicación, para garantizar la trazabilidad
   de esta publicación. Si se han hecho cambios en esta rama no apropiados para
   `master` (p. ej. revertir una funcionalidad experimental sobre la que se está
   trabajando), se deben revertir antes de hacer el _merge_.
6. La rama temporal de preparación de la publicación ha llegado al final de su
   vida útil, así que debe borrarse tanto de los repositorios locales como el
   remoto.

#### 8.1.2. Proceso para publicar una nueva versión menor o de parche sin rama temporal de preparación de publicación

En caso de que `master` se encuentre en un estado aceptable para publicar
directamente, puede evitarse la burocracia asociada a la creación de una rama
temporal de preparación de publicación.

1. En un repositorio local con la última versión de la rama `master`
   seleccionada se ejecuta el comando `./mvnw release:prepare`. Este comando
   asegurará que el repositorio local está limpio, pedirá información de la
   nueva versión, ejecutará el objetivo `verify`, creará una etiqueta (_tag_)
   para la confirmación actual y actualizará automáticamente los POM del
   proyecto. Los valores predeterminados que sugiere este complemento son
   apropiados para una nueva versión de parche. En el caso de que se vaya a
   publicar una nueva versión menor se debe de seguir el formato de los valores
   predeterminados igualmente.
   - Se debe recordar que la etiqueta de la imagen de Docker que se publicará
     tendrá el sufijo `-latest` en caso de que la versión del proyecto indicada
     en los POM termine en `-SNAPSHOT`.
2. Ahora se crea una nueva publicación en **GitHub**, como en el paso 3 del
   proceso definido en la sección 8.1.1. En este caso, sin embargo, `./mvnw
   release:prepare` ya crea una etiqueta, así que se debe escoger esa y no crear
   una nueva.
3. **GitHub Actions** recibirá el evento de publicación y empezará
   automágicamente el proceso de CD para subir la imagen Docker de la aplicación
   al repositorio de producción.
   - Si falla el proceso de CD, la imagen Docker no será subida al repositorio
     de producción.

### 8.2. Proceso para publicar una nueva versión mayor

En este caso siempre se crea una rama de publicación (_release branch_), para
dar soporte al mantenimiento de versiones anteriores. Esta rama también puede
usarse como rama de preparación de publicación si es necesario. El procedimiento
es muy similar al definido en la sección 8.1.1, con las siguientes diferencias:

- La nueva rama debe nombrarse `release-vX`, no `relese-prep-vX.Y.Z`.
- Solo hace falta seguir el proceso definido en la sección 8.1.1 hasta el paso 4
  (inclusive), ya que la rama de publicación creada en este caso tendrá una vida
  más larga.

Una vez terminada la publicación debe de borrarse la rama que corresponda a la
versión mayor que se acaba de quedar obsoleta (sin mantenimiento). Por ejemplo,
si solo se soportan las últimas 5 versiones mayores y se acaba de hacer la rama
`release-v6`, se borrará la rama `release-v1`.

### 8.3. Proceso para incorporar cambios y hacer nuevas publicaciones de versiones mayores en mantenimiento

`master` es la rama donde ocurre el desarrollo de la última versión mayor. Hay
varias formas de parchear versiones mayores anteriores que todavía estén en
mantenimiento:

- Incorporando confirmaciones inviduales desde `master` a la rama de publicación
  correspondiente con un _cherry pick_. Dado que puede haber notables
  diferencias entre ambas versiones, es posible que ocurran conflictos de
  combinación, que deberán resolverse.
- Haciendo los cambios directamente sobre la rama de publicación. Puede optarse
  por usar PRs para normalizar el proceso.

Como siempre, cualquier cambio que se haga para estas versiones debe pasar las
comprobaciones de CI. En ningún caso se incorporarán cambios de una rama de
publicación a `master`.

Las publicaciones de nuevas versiones menores y de parche de versiones mayores
en mantenimiento se hará de forma similar a la documentada en la sección 8.1.2,
solo que debe hacerse sobre la rama de publicación correspondiente en vez de
sobre `master`. Recuérdese que la rama de publicación también puede usarse como
rama de preparación de publicación.
