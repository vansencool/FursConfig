# FursConfig

FursConfig is a high-performance configuration similar to HOCON, but with significantly faster parsing. It is designed to handle both small and large configurations with ease.

> **⚠ Experimental, but stable enough for use!** While FursConfig is still new, it works just fine and is ready for practical applications.

## Installation

**Gradle**

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
    implementation 'com.github.vansencool:FursConfig:1.0.1'
}
```

**Maven**

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

```xml
<dependency>
    <groupId>com.github.vansencool</groupId>
    <artifactId>FursConfig</artifactId>
    <version>1.0.1</version>
</dependency>
```

---

## Features

- **Branches**: Organize configurations with a hierarchical structure, we call them "branches" (or "nodes" in code terms).
- **Multiple Data Types**: Supports `list`, `float`, `double`, `int`, `long`, and `string`.
- **Comment Support**: Define comments using `//` or `#`.
- **Optimized Parsing**: Up to **90% faster** compared to Typesafe Config.

---

## Syntax Overview

### Root Node

```hocon
is_enabled = true
what_is_3_and_3 = 6
```

### Branches

```hocon
some_branch {
    is_enabled = true
    what_is_3_and_3 = 6
}
```

### Nested Branches

```hocon
some_branch_1 {
     cool_to_meet_you = true
     other_branch {
         some_other_branchs_value = "hello"
     }
}
```

### Supported Data Types

- `list`
- `float` (stored as double but converted to float)
- `double`
- `int`
- `long`
- `string`

### Comments

Define comments using:

```hocon
// This is a comment
# This is also a comment
```

#### Comments on Branches

```hocon
branch { // This is cool!
    value = 42
}
```

#### Comments on Values

```hocon
this_is_cool_isnt_it = true // Of course!
```

### Lists

**Simple Lists:**

```hocon
list = ["Hello there", "How are you doing?"]
```

**Complex Lists:** (List of branches)

```hocon
list = [
    {
        value = "Hello there!"
    },
    {
        value = "How are you doing?"
    }
]
```

---

## Differences from Typesafe Config

- **No Spaces in Keys**: FursConfig does not allow spaces in keys (like, `some key = "value"`).
- **Faster Parsing**: "At least" 15% faster in every scenario, with average improvements ranging from 30% to 70%.

---
