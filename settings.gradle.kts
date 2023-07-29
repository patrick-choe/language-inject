rootProject.name = "language-inject"

val impl = "${rootProject.name}-impl"

include(":$impl")

file(impl).listFiles { file ->
    file.isDirectory && file.name.startsWith("v")
}?.forEach { file ->
    include(":$impl:${file.name}")
}