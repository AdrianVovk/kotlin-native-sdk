# Sample
This is the sample project component

# Compilation workaround
Currently, Kotlin/Native does not support including external pre-built libraries into programs.
This project uses a hack to string all of the needed files together and exports them as "one project", allowing it to compile and work.

This builds as usual, with the two tasks being `build` and `run`. Once Kotlin/Native releases support for KLibs, this will be scrapped into a standard build process