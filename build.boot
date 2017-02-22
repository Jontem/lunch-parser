(set-env!
  :resource-paths #{"cache"}
  :source-paths #{"src"}
  :dependencies '[[enlive "1.1.6"]
                  [org.clojure/clojure "1.7.0"]])

(task-options!
  pom {}
  jar {})


(require 'core)

(deftask run []
  (let [[search-word] *args*]
    (core/-main search-word)))