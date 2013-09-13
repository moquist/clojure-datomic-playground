(ns
    ^{:author "Matt Oquist"
      :doc "This is a motley collection of clojure code I wrote while learning to use datomic.

            See the (comment) in demo_mem.clj to run some demos from your REPL.

            Or create an uberjar and run to make it go:
                $ java -jar clojure-datomic-playground.jar sample-1-greta"}
  clojure-datomic-playground.core
  (:require [datomic.api :as d :refer [db q]]
            [clojure-datomic-playground.demo-mem :as d-m])
  (:gen-class))

(defn -main
  [cmd]
  ;; Nothing clever -- keepin' it short and clear.
  (cond
   (= cmd "sample-1-greta") (d-m/demo d-m/sample-1 "Greta")
   (= cmd "sample-1-fred") (d-m/demo d-m/sample-1 "Fred")
   (= cmd "sample-2-galllivanting") (d-m/demo d-m/sample-2 "Gallivanting Off")
   (= cmd "sample-2-business") (d-m/demo d-m/sample-2 "Aes Sedai Business 101")))

(comment

  ;; Walk down through a returned entity.
  (println
   (:teacher/name
    (first
     (:classroom/teacher
      (d/entity (db conn)
                (ffirst
                 (d/q '[:find ?e
                        :where [?e :classroom/display-name "Gallivanting Off"]]
                      (db conn))))))))

  ;; Do the same walk, a *little* cleaner, with threading.
  (->
   (d/entity (db conn)
             (ffirst
              (d/q '[:find ?e
                     :where [?e :classroom/display-name "Gallivanting Off"]]
                   (db conn))))
   :classroom/teacher
   first
   :teacher/name
   println)
  )
