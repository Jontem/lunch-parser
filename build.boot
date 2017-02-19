(set-env!
  :resource-paths #{"cache"}
  :source-paths #{"src"}
  :dependencies '[[enlive "1.1.6"]])

(task-options!
  pom {}
  jar {})


(require 'core)

(deftask run []
  (with-pass-thru _
    (core/-main)))