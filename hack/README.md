# Sample compilation hack
This folder contains a hack for sample compilation. Currently, Kotlin/Native does not support including external pre-built libraries into programs.
This hack strings all of the files together and exports them as "one project", allowing it to compile and work.