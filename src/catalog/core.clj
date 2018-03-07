(ns catalog.core
  (:require 
    [com.walmartlabs.lacinia.pedestal :as pe]
    [com.walmartlabs.lacinia.util :as lu]
    [com.walmartlabs.lacinia :refer [execute]]
    [com.walmartlabs.lacinia.schema :as schema]
    [io.pedestal.http :as http]))

(defn resolver-map []
  {:query/person-by-id (fn [ctx args value]
                         (schema/tag-with-type {:id "foo" :name "Bohdan"} :Person))})

(defn streamer-map []
  {:stream/persons (fn [ctx args source-stream]
                     (println "called")
                     (source-stream (schema/tag-with-type {:id "foo" :name "Bohdan"} :Person))
                     #(source-stream nil))})

(defn catalog-schema []
  (->
    "resources/schema.edn"
    slurp
    read-string
    (lu/attach-streamers (streamer-map))
    (lu/attach-resolvers (resolver-map))
    schema/compile))

(def service (pe/service-map (catalog-schema) {:graphiql true :subscriptions true}))

(defonce runnable-service (http/create-server service))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (http/start runnable-service))
