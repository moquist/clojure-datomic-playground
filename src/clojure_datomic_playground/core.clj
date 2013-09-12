;;;; This is a short, simple demo program that sets up a datomic:mem
;;;; instance, transacts some schema into it, transacts some data into
;;;; it, and queries it to get results.
;;;; See the (comment) to run it from your REPL.
;;;; Or create an uberjar and run:
;;;;    $ java -jar clojure-datomic-playground.jar sample-1-greta

(ns clojure-datomic-playground.core
  (:require [datomic.api :as d :refer [db q]])
  (:gen-class))

;; See http://docs.datomic.com/clojure-api.html
(def uri "datomic:mem://test")

(defn reset-db
  "Return a connection to a new database."
  []
  ;; This will fail if the DB doesn't exist. But who cares?
  (d/delete-database uri)
  (d/create-database uri)
  (d/connect uri))

(defn p-query
  "Query datomic and print all the attributes of each returned entity."
  [conn query]
  (println "Query results:")
  (let [db (db conn)
        results (d/q query db)]
    (dorun
     (for [entity results 
           attr (d/entity db (first entity))]
       (println attr)))
    results))

(defn demo
  "Run a demo on the given sample data that:
   1. installs the sample schema
   2. asserts the sample data
   3. queries datomic for the person with the specified query-name
   4. prints all the attributes of the person(s) returned by the query
   5. returns the results set"
  [sample query-name]
  (let [conn (reset-db)]
    (d/transact conn (:schema sample))
    (d/transact conn (:data sample))
    (p-query conn `[:find ?person-entity
                    :where [?person-entity :person/name ~query-name]])))

(def sample-1
  {:schema [{:db/id #db/id [:db.part/db]
             :db/ident :person/name
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "A person's name"
             :db.install/_attribute :db.part/db}
            {:db/id #db/id [:db.part/db]
             :db/ident :person/email
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "A person's email address"
             :db.install/_attribute :db.part/db}]
   :data [{:db/id #db/id [:db.part/user]
           :person/name "Greta"
           :person/email "greta@greta.aterg"}
          {:db/id #db/id [:db.part/user]
           :person/name "Fred"
           :person/email "fred@fred.derf"}]})

(defn -main
  "Pass sample-1-greta or sample-1-fred on the command line to run a demo from the CLI."
  [cmd]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  ;; Nothing clever -- keepin' it short and clear.
  (cond
   (= cmd "sample-1-greta") (demo sample-1 "Greta")
   (= cmd "sample-1-fred") (demo sample-1 "Fred")))

(comment
  (demo sample-1 "Greta")
  (demo sample-1 "Fred")
  )
