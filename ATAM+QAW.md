## Evaluación de Arquitectura y atributos (QAW + ATAM) 🏬

### Propósito 🔎

_**ATAM**_ es un método altamente estructurado para evaluar un diseño de arquitectura el cuál permite detectar, de manera temprana, riesgos técnicos, conflictos entre atributos, puntos sensitivos del diseño y soluciones.

_**QAW**_ (Quality Attribute Workshops) por su parte proporciona un método para la determinación de un sistema crítico basado   en   el análisis   de   los   atributos   de   calidad,   al   igual   que disponibilidad,  rendimiento,  seguridad,  interoperabilidad  y  modificabilidad.  En  este  orden, QAW  complementa  la  arquitectura  _**ATAM**_,  puesto  que  identifica  los  atributos  relevantes  de calidad   y   permite   establecer   los   requisitos   del   sistema   antes de la existencia de una arquitectura de software.
**(M.  R.  Barbacci,  R.  Ellison,  A.  J.  Lattanze,  J.  Stafford,  C.  Weinstock  y  W.  Wood,  Quality Attribute   Workshops   (QAWs),   Third   Edition   (CMU/SEI-2003-TR-016,   ADA418428)   ed., Pittsburgh, PA: Software Engineering Institute, Carnegie Mellon University, 2003.)**

En este análisis se presentará una primera aproximación a la documentación arquitectónica de esta solución, así cómo una evaluación inicial a sus atributos claves de negocio (Drivers) cómo input inicial a las operaciones y mantenimiento de arquitectura futuros de la misma. 

### Presentación de la arquitectura 🔨

La documentación mediante vistas es una forma de documentar el diseño de arquitectura considerando los intereses de los interesados en el producto. Existen variadas maneras de expresar las vistas y sus interrelaciones, en esta oporunidad y considerando el alcance del producto, se presentarán las vistas de componentes combinado con una vista a las interacciones a alto nivel entre los mismos. 

Algunas de estas vistas se generaron utilizando la herramienta  [Code Iris](https://plugins.jetbrains.com/plugin/7324-code-iris) para IntelliJ. 

### Vista de Interacción 🔄
![interaction.png](Resources/interaction.png)

### Vista de Paquetes y uso 🗂

![ModuleView.png](Resources/ModuleView.png)

### Vista de Dependencias 🔗

![Dependency](Resources/DependencyView.png)

### Evaluación de atributos de calidad ✅



