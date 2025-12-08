<div align="center">

# **Versa**
### *High performance, feature packed configuration system*

**Successor of FursConfig, rewritten from zero**
  
Readable, fast, flexible, modifiable.  
Works great with small configs, scales to massive ones just as easily.

</div>

---

## ⭐ Features at a glance

✔ Very fast parsing (around **3x faster** than Typesafe's Config based on general benchmarks)  
✔ Human readable syntax with **spaces in keys supported**  
✔ Uses `=` or `:` assignment (`name = "v"`, `name: "v"`) (preserved)  
✔ `//` and `#` comments supported (inline and standalone) (preserved)  
✔ **Formatting preserved** when writing back to file  
✔ **Runtime editing** of config nodes  
✔ **NodeBuilder API** for generating configs in code  
✔ **Merge support** for default + user configs  
✔ Can load, modify, save and reformat configs easily  
✔ Adapters let you bind config data to real objects

---

<div align="center">

## **Installation**


### **Gradle**
</div>

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.vansencool:Versa:2.1.0'
}
```

<div align="center">

### **Maven**

</div>

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.vansencool</groupId>
    <artifactId>Versa</artifactId>
    <version>2.1.0</version>
</dependency>
```

---

## 📌 Syntax Overview

### Root values

```hocon
enabled = true
welcome: "Hello world"
answer is = 42         # spaces in keys work fine
````

### Branches

```hocon
server {
    name = "MyServer"
    port = 25565
}
```

### Nested structure

```hocon
app {
    database {
        host = "localhost"
        port = 3306
    }

    logging {
        level = "INFO"
    }
}
```

### Comments

```hocon
// standalone comment
# also supported

value = 10 // inline here
text = "Hi" # works too
```

### Lists

```hocon
names = [ "Alice", "Bob", "Charlie" ]
numbers: [1,2,3,4]
```

### List of branches (complex lists)

```hocon
servers = [
    {
      name = "prod"     
      secure = true
    },
    {
      name = "testing"  
      secure = false
    }
]
```

## NodeBuilder Example

Build configuration programmatically with full control over layout.

```java
Node cfg = new NodeBuilder()
    .name("root")
    .child(NodeBuilder.builder()
            .name("database")
            .startComment(" Connection settings") // branch start comment
            .add(new ValueBuilder().name("host").string("localhost"))
            .add(new ValueBuilder().name("port").intVal(3306))
            .emptyLine()
            .child(
                    new NodeBuilder()
                            .name("pool")
                            .add(new ValueBuilder().name("size").intVal(10))
                            .build()
            ).build()
    )
    .build();

System.out.println(cfg);
```

Output:

```hocon
database { // Connection settings
    host = "localhost"
    port = 3306

    pool {
        size = 10
    }
}
```

---

## Differences from FursConfig

| Feature                   | FursConfig    | Versa        |
|---------------------------|---------------| ------------ |
| Writing configs back      | No            | Yes          |
| Formatting preserved      | No            | Yes          |
| Editing config            | No            | Yes          |
| Merging system            | No            | Yes          |
| Node builder API          | No            | Yes          |
| Handles comments properly | Not at all    | Full support |
| Spaces in keys            | No            | Yes          |
| Performance               | Decently Fast | Much faster  |

Versa is essentially the "completed version" of what FursConfig started.

---

## Notes

There is a lot more to explore inside Versa.

**Documentation is coming soon.**