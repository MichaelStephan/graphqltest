(ns catalog.core
  (:require 
    [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
    [com.walmartlabs.lacinia :refer [execute]]
    [clojure.data.json :as json]
    [ring.adapter.jetty :as raj]
    [com.walmartlabs.lacinia.schema :as schema]
    [ring.middleware.reload :as rmr]
    [ring.middleware.params :as rmp]))

(defonce jetty-server (atom nil))

(defn resolver-map []
  {:query/person-by-id (fn [ctx args value]
                         {:id "abc" :name "abc"})})

(defn catalog-schema []
  (->
    "resources/schema.edn"
    slurp
    read-string
    (attach-resolvers (resolver-map))
    schema/compile))

(defn handler [req]
  {:status 200
     :headers {"Content-Type" "application/json"}
     :body (let [q (get-in req [:query-params "q"])
                 ret (execute (catalog-schema)  q nil nil)]
             (json/write-str ret))})

(def app 
  (-> handler
      rmp/wrap-params))

(defn main [& args]
  (println "Started http server at port 3000")
  (println "Fire queries like http://localhost:3000/?q={person_by_id%20(id:%2220%22)%20{id%20name}}")  
  (raj/run-jetty (rmr/wrap-reload #'app) {:port 3000}))

(comment
  (raj/run-jetty (rmr/wrap-reload #'app) {:port 3000 :join false})
  (execute (catalog-schema) "{ person_by_id(id:\"abc\") {id}}" nil nil))
