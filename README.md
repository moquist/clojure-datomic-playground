# clojure-datomic-playground

This is a short, simple demo program that sets up a datomic:mem instance, transacts some schema into it, transacts some data into it, and queries it to get results.

The whole point is to look at the source and see how to do the minimum to get data into and out of datomic.

## Usage

See the (comment) in core.clj to run it from your REPL.

Less helpfully (because the point is to look at the source), you could create an uberjar and execute it from the command line:

    $ java -jar clojure-datomic-playground-0.1.0-SNAPSHOT-standalone.jar { sample-1-greta | sample-1-fred }

## License

Copyright © 2013 Matt Oquist <moquist@majen.net>

Distributed under the Eclipse Public License, the same as Clojure.
