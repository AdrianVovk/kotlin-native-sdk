# Sample
This is the sample project component

# Compilation workaround
Currently, Kotlin/Native does not support including external pre-built libraries into programs.
This project uses a hack to string all of the needed files together and exports them as "one project", allowing it to compile and work.

If you are having issues, update the sources by running `./update-sources`.