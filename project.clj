(defproject catalog "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.walmartlabs/lacinia "0.25.0"]
                 [ring "1.6.3"]
                 [org.clojure/data.json "0.2.6"]]
  :main catalog.core/main)