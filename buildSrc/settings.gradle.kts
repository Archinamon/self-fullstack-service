val plugin = arrayOf(
    ":kompiler:graph",
    ":kompiler:plugin"
)

include(*plugin)

plugin.forEach { module ->
    project(module)
        .projectDir = File("..${module.replace(":", "/")}")
}