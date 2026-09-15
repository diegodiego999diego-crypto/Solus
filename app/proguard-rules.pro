# Reglas de ProGuard/R8 para Solus
# Por ahora vacío porque compilamos en modo debug.
# Aquí se agregarán reglas de ofuscación cuando se compile en release.

# Mantener anotaciones
-keepattributes *Annotation*

# Mantener clases de Kotlin
-keep class kotlin.** { *; }
