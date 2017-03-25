(set-env!
  :project "lunch-parser"
  :version "0.5.0"
  :resource-paths #{"cache"}
  :source-paths #{"src"}
  :dependencies '[[enlive "1.1.6"]
                  [org.clojure/clojure "1.7.0"]
                  [ring/ring-core "1.5.1"]
                  [ring/ring-jetty-adapter "1.5.1"]
                  [ring/ring-json "0.4.0"]
                  [ring/ring-devel "1.5.1"]
                  [compojure "1.5.2"]
                  [clj-http "2.3.0"]
                  [haversine "0.1.1"]])

(task-options!
  pom {}
  jar {})


(require 'core)
(require
  '[ring.adapter.jetty :as jetty]
  '[core]
  '[server])


(deftask run []
  (let [[search-word] *args*]
    (core/-main search-word)))

(deftask start-server []
  (jetty/run-jetty server/app {:port 3000}))