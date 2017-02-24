(set-env!
  :resource-paths #{"cache"}
  :source-paths #{"src"}
  :dependencies '[[enlive "1.1.6"]
                  [org.clojure/clojure "1.7.0"]
                  [ring/ring-core "1.5.1"]
                  [ring/ring-jetty-adapter "1.5.1"]
                  [ring/ring-json "0.4.0"]
                  [ring/ring-devel "1.5.1"]])

(task-options!
  pom {}
  jar {})


(require 'core)
(deftask run []
  (let [[search-word] *args*]
    (core/-main search-word)))

(require '[ring.adapter.jetty :as jetty])
(deftask start-server []
  (jetty/run-jetty core/app {:port 3000}))