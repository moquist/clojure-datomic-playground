(ns
    ^{:author "Matt Oquist",
      :doc "A record of some clojure I wrote to play around with datomic:mem.

      The code in this file uses an in-memory database. There are a
      couple functions to import and query some simple data."}
    clojure-datomic-playground.demo-mem
  (:require [datomic.api :as d :refer [db q]]))

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

(defn demo-schema!
  "Install the sample schema"
  [conn {schema :schema}]
  (d/transact conn schema))

(defn demo-data!
  "Assert the sample data"
  [conn {data :data}]
  (d/transact conn data))

(defn demo
  "Run a demo on the given sample data that:
   1. installs the sample schema
   2. asserts the sample data
   3. queries datomic for the entities matching the specified query terms
   4. prints all the attributes of the entities returned by the query
   5. returns the results set."
  [sample & query-terms]
  (let [conn (reset-db)]
    (demo-schema! conn sample)
    (demo-data! conn sample)
    (p-query conn
             (apply (:query sample) query-terms))))

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
           :person/email "fred@fred.derf"}]
   ;; I'm using a fn here so the query-term(s) can be provided later.
   :query (fn [query-term]
            `[:find ?person-entity
              :where [?person-entity :person/name ~query-term]])})

(def sample-2
  {:schema [;; classroom entity
            {:db/id #db/id [:db.part/db]
             :db/ident :classroom/display-name
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "classroom name"
             :db.install/_attribute :db.part/db}
            {:db/id #db/id [:db.part/db]
             :db/ident :classroom/idstr
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "classroom ID string"
             :db.install/_attribute :db.part/db}
            {:db/id #db/id [:db.part/db]
             :db/ident :classroom/version
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "classroom version"
             :db.install/_attribute :db.part/db}
            {:db/id #db/id [:db.part/db]
             :db/ident :classroom/status
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "classroom status"
             :db.install/_attribute :db.part/db}
            {:db/id #db/id [:db.part/db]
             :db/ident :classroom/teacher
             ;; a classroom can have 0 or many teachers
             :db/valueType :db.type/ref
             :db/cardinality :db.cardinality/many
             :db/doc "teacher"
             :db.install/_attribute :db.part/db}

            ;; teacher entity
            {:db/id #db/id [:db.part/db]
             :db/ident :teacher/name
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "teacher name"
             :db.install/_attribute :db.part/db}
            {:db/id #db/id [:db.part/db]
             :db/ident :teacher/email
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "teacher email"
             :db.install/_attribute :db.part/db}
            {:db/id #db/id [:db.part/db]
             :db/ident :teacher/url
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "teacher url"
             :db.install/_attribute :db.part/db}]
   :data [;; Note that we're asserting these teacher entities with
          ;; temporary ID numbers that we can reference later.
          {:db/id #db/id [:db.part/user -1]
           :teacher/name "Moiraine Damodred"
           :teacher/url "http://wot.wikia.com/wiki/Moiraine_Damodred"
           :teacher/email "mdamodred@one.source"}
          {:db/id #db/id [:db.part/user -2]
           :teacher/name "Nynaeve al'Meara"
           :teacher/url "http://wot.wikia.com/wiki/El'Nynaeve_ti_al'Meara_Mandragoran"
           :teacher/email "nmandragoran@rivers.two"}
          {:db/id #db/id [:db.part/user -3]
           :teacher/name "Rand al'Thor"
           :teacher/url "http://en.wikipedia.org/wiki/Rand_al'Thor"
           :teacher/email "boss@1"}

          {:db/id #db/id [:db.part/user]
           :classroom/display-name "Aes Sedai Business 101"
           :classroom/idstr "101"
           :classroom/version "v8"
           ;; This class is taught by Moiraine and Nynaeve.
           :classroom/teacher [#db/id [:db.part/user -1] #db/id [:db.part/user -2]]
           :classroom/status "ACTIVE"}
          {:db/id #db/id [:db.part/user]
           :classroom/display-name "Trolloc Fighting 1732"
           :classroom/idstr "1732"
           :classroom/version "v8"
           :classroom/status "ACTIVE"}
          {:db/id #db/id [:db.part/user]
           :classroom/display-name "Gallivanting Off"
           :classroom/idstr "173829"
           :classroom/version "v3"
           ;; This class is taught by Tam, but we didn't assert him
           ;; above. Instead, we're nesting his assertion here.
           :classroom/teacher [{:db/id #db/id [:db.part/user]
                                :teacher/name "Tam al'Thor"
                                :teacher/url "http://wot.wikia.com/wiki/Tamlin_al'Thor"
                                :teacher/email "talthor@rivers.two"}]
           :classroom/status "ARCHIVED"}]
   ;; I'm using a fn here so the query-term(s) can be provided later.
   :query (fn [name]
            `[:find ?e
              :where [?e :classroom/display-name ~name]])})


(comment
  (demo sample-1 "Greta")
  (demo sample-1 "Fred")
  (demo sample-2 "Gallivanting Off")

  ;; test schema and data loading but not query
  (doto (reset-db) (demo-schema! sample-2) (demo-data! sample-2))

  )