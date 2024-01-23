<h1 align="center">Traitement du graphe temporel CityBike avec Spark-GraphX</h1>
<h4 align="center">Mael KERICHARD - Cody ADAM</h4>
<p align="center">
   <img src="https://img.shields.io/badge/-ESIR-orange" alt="ESIR">
   <img src="https://img.shields.io/badge/-DMB-red" alt="DMB">
</p>


## Résultats

Les résultats pré-générés sont disponibles à [./results.md](results.md).

Les instructions pour générer les résultats sont disponibles dans la section [Génération des résultats](#génération-des-résultats).

## Génération des résultats
 
### Prérequis

- Java
- Scala
- SBT

### Lancer le projet

```bash
export JAVA_OPTS='--add-exports java.base/sun.nio.ch=ALL-UNNAMED'
sbt run
```

Le résultat est disponible dans le fichier `results.md`.


#### `cannot access class sun.nio.ch.DirectBuffer`[^1]

> If you are using IntelliJ IDEA, this is based on @Anil Reddaboina's answer, and thanks!
> 
> This adds more info as I don't have that "VM Options" field by default.
> 
> Follow this:
> 
> ![](https://i.stack.imgur.com/1DYAA.png)
> 
> Then you should be able to add `--add-exports java.base/sun.nio.ch=ALL-UNNAMED` to "VM Options" field.
> 
> or add fully necessary VM Options arguments:
> 
> ```
> --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.net=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.util.concurrent=ALL-UNNAMED --add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/sun.nio.cs=ALL-UNNAMED --add-opens=java.base/sun.security.action=ALL-UNNAMED --add-opens=java.base/sun.util.calendar=ALL-UNNAMED --add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED
> ```

[^1]: https://stackoverflow.com/questions/73465937/apache-spark-3-3-0-breaks-on-java-17-with-cannot-access-class-sun-nio-ch-direct
