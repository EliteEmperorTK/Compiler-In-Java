* * *

🛠️ **Compilador - Lenguaje Propio**
====================================

Este proyecto implementa un **compilador completo** para un lenguaje de programación diseñado específicamente para esta práctica. El compilador cubre **todas las fases del proceso de compilación**, desde el análisis léxico hasta la generación de código ensamblador para **Motorola 68000**.

* * *

📌 **Índice**
-------------

*   [Descripción](#descripci%C3%B3n)
*   [Autores](#autores)
*   [Características del Lenguaje](#caracter%C3%ADsticas-del-lenguaje)
*   [Fases del Compilador](#fases-del-compilador)
    *   [Análisis Léxico](#an%C3%A1lisis-l%C3%A9xico)
    *   [Análisis Sintáctico](#an%C3%A1lisis-sint%C3%A1ctico)
    *   [Tabla de Símbolos](#tabla-de-s%C3%ADmbolos)
    *   [Análisis Semántico](#an%C3%A1lisis-sem%C3%A1ntico)
    *   [Generación de Código Intermedio](#generaci%C3%B3n-de-c%C3%B3digo-intermedio)
    *   [Optimización de Código](#optimizaci%C3%B3n-de-c%C3%B3digo)
    *   [Generación de Código Ensamblador](#generaci%C3%B3n-de-c%C3%B3digo-ensamblador)
*   [Instalación y Ejecución](#instalaci%C3%B3n-y-ejecuci%C3%B3n)
*   [Ejemplo de Código](#ejemplo-de-c%C3%B3digo)
*   [Contacto](#contacto)

* * *

📖 **Descripción**
------------------

Este proyecto consiste en la implementación de un **compilador completo**, capaz de traducir código fuente escrito en un **lenguaje de programación propio** a código ensamblador **Motorola 68000**.

El compilador está dividido en varias etapas:

*   **Análisis léxico:** Tokenización del código fuente.
*   **Análisis sintáctico:** Construcción del AST (Árbol de Sintaxis Abstracta).
*   **Análisis semántico:** Validación de tipos y reglas semánticas.
*   **Generación de código intermedio:** Producción de código de tres direcciones (C3D).
*   **Optimización de código:** Eliminación de redundancias y mejoras en la eficiencia.
*   **Traducción a ensamblador:** Conversión a instrucciones del procesador **Motorola 68000**.

* * *

👥 **Autores**
--------------

*   **Joaquín Esperon Solari**
*   **Marc Nadal Sastre Gondar**

* * *

🚀 **Características del Lenguaje**
-----------------------------------

El lenguaje diseñado en este proyecto soporta:  
✅ **Tipos de datos primitivos:** `int`, `boolean`  
✅ **Estructuras de control:** `if`, `else`, `while`, `for`  
✅ **Funciones con parámetros y retorno**  
✅ **Funciones sin retorno**  
✅ **Declaración de constantes (`const`)**  
✅ **Entrada (`read`) y salida (`write`)**  
✅ **Operadores aritméticos:** `+`, `-`, `*`  
✅ **Operadores relacionales:** `==`, `!=`, `<`, `>`  
✅ **Operadores lógicos:** `&&`, `||`, `!`  
✅ **Manejo de **tuplas** (`tupla.get(index)`)**

* * *

🏗️ **Fases del Compilador**
----------------------------

### 🔹 **Análisis Léxico**

El análisis léxico se encarga de **identificar tokens** en el código fuente. Se implementa en **scanner.flex**, donde se definen **palabras clave**, **operadores** y **delimitadores** mediante **expresiones regulares**.

✔️ **Generación de tokens:** `main`, `if`, `else`, `while`, `for`, `read`, `write`, `int`, `boolean`, etc.  
✔️ **Manejo de errores léxicos:** Se reportan tokens no reconocidos.  
✔️ **Eliminación de comentarios y espacios en blanco.**

* * *

### 🔹 **Análisis Sintáctico**

El análisis sintáctico utiliza **Java CUP** para construir el **Árbol de Sintaxis Abstracta (AST)**.  
✔️ Se define una **gramática LALR** para reconocer estructuras del lenguaje.  
✔️ **Manejo de precedencias** para operadores.  
✔️ **Recuperación de errores sintácticos** mediante producción `error`.

* * *

### 🔹 **Tabla de Símbolos**

✔️ Implementada como una **estructura jerárquica** para manejar ámbitos anidados.  
✔️ **Verifica duplicación de variables, funciones y constantes.**  
✔️ **Permite la búsqueda eficiente** de identificadores en distintos niveles de alcance.

* * *

### 🔹 **Análisis Semántico**

✔️ **Verificación de tipos** en asignaciones y operaciones.  
✔️ **Chequeo de retorno en funciones** (prohibido `return;` vacío).  
✔️ **Restricciones en ámbitos locales** (`for`, `if`, `while` no pueden redeclarar variables del padre).  
✔️ **Validación de acceso a tuplas** (`tupla.get(index)`).

* * *

### 🔹 **Generación de Código Intermedio (C3D)**

✔️ **Traducción del AST a código de tres direcciones.**  
✔️ **Manejo de temporales** (`t1`, `t2`, ...) para almacenar cálculos intermedios.  
✔️ **Generación de etiquetas** (`e1: skip`) para saltos condicionales e incondicionales.  
✔️ **Traducción de estructuras de control** a código eficiente con `goto`.

* * *

### 🔹 **Optimización de Código**

Se implementaron varias técnicas para mejorar la eficiencia del código intermedio:

✅ **Eliminación de código inaccesible** (`goto` seguido de código innecesario).  
✅ **Reducción de ramificaciones** (`if` + `goto` simplificados).  
✅ **Eliminación de etiquetas redundantes** (etiquetas sin referencias).  
✅ **Propagación de copias** (`t1 = 5; x = t1;` → `x = 5;`).  
✅ **Operaciones constantes** (`t1 = 2 + 3;` → `t1 = 5;`).

* * *

### 🔹 **Generación de Código Ensamblador**

✔️ **Traducción a ensamblador Motorola 68000.**  
✔️ **Manejo de pila y registros (`D0`, `D1`, `A0`)** para operaciones.  
✔️ **Generación de subrutinas** (`write_int`, `read_int`).  
✔️ **Estructura organizada** con `.DATA` para variables y `.TEXT` para instrucciones.

* * *

🛠️ **Instalación y Ejecución**
-------------------------------


### 📌 **Instrucciones**

1️⃣ **Abrir el proyecto en NetBeans**

*   Clonar o descargar el repositorio y abrirlo en **NetBeans**.
*   Asegurarse de que el **JDK** esté configurado correctamente.

2️⃣ **Compilar el proyecto**

*   NetBeans compilará automáticamente el código al ejecutarlo.
*   Si es necesario, puedes limpiar y construir el proyecto con:  
    **`Run > Clean and Build Project`**

3️⃣ **Ejecutar el compilador**

*   En **NetBeans**, ubicar la clase **`Yio.java`**.
*   Hacer clic derecho y seleccionar **"Run File"** (`Shift + F6`).
*   Esto iniciará el compilador y permitirá analizar y generar código ensamblador.

3️⃣ **Ejecutar código ensamblador utilizando el simulador Easy68K (o cualquiera que soporte el Motorola 68000)**

* * *

📝 **Ejemplo de Código**
------------------------

```cpp
main() {
    int x = 5;
    boolean y = true;

    if (y == true) {
        write(x);
    } else {
        write(0);
    }

    def int sumar(int a, int b) {
        int ret = a+b;
        return ret;
    }

    int g = 3;
    int h = 5;
    h = sumar(g, h);
    write(h); 

    tupla t = (5, true);
    write(t.get(0)); 
    write(t.get(1));

    const int con = 2;
    write(con);

    for (int i = 0; i < x; i = i + 1) {
        write(i);
    }
}
```

✔️ **Ejecuta operaciones matemáticas y booleanas.**  
✔️ **Incluye funciones, condicionales, bucles y tuplas.**  
✔️ **Genera código ensamblador optimizado para Motorola 68000.**

* * *

📩 **Contacto**
---------------

Si tienes dudas o sugerencias, puedes contactar a los autores:
📧 **Joaquín Esperon Solari** - joacoesperon1@gmail.com
📧 **Marc Nadal Sastre Gondar** - marcsasgon@gmail.com

* * *