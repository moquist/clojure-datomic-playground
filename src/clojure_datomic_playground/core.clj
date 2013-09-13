(ns
    ^{:author "Matt Oquist"
      :doc "This is a motley collection of clojure code I wrote while learning to use datomic.

            See the (comment) in demo_mem.clj to run some demos from your REPL.

            Or create an uberjar and run to make it go:
                $ java -jar clojure-datomic-playground.jar sample-1-greta"}
  clojure-datomic-playground.core
  (:require [datomic.api :as d :refer [db q]]
            [clojure-datomic-playground.demo-mem :as demo-mem])
  (:gen-class))

(defn -main
  "Pass sample-1-greta or sample-1-fred on the command line to run a demo from the CLI."
  [cmd]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  ;; Nothing clever -- keepin' it short and clear.
  (cond
   (= cmd "sample-1-greta") (demo-mem/demo demo-mem/sample-1 "Greta")
   (= cmd "sample-1-fred") (demo-mem/demo demo-mem/sample-1 "Fred")
   (= cmd "sample-2-gallavanting") (demo-mem/demo demo-mem/sample-2 "Galavanting Off")
   (= cmd "sample-2-business") (demo-mem/demo demo-mem/sample-2 "Aes Sedai Business 101")))

(comment
  (println
   (:teacher/name
    (first
     (:classroom/teacher
      (d/entity (db conn)
                (ffirst
                 (d/q '[:find ?e
                        :where [?e :classroom/display-name "Galavanting Off"]]
                      (db conn))))))))
  

  (->
   (d/entity (db conn)
             (ffirst
              (d/q '[:find ?e
                     :where [?e :classroom/display-name "Galavanting Off"]]
                   (db conn))))
   :classroom/teacher
   first
   :teacher/name
   println)
  )
