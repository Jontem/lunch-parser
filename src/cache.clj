(ns cache
    (:require
        [net.cgrand.enlive-html :as html]))

(def ^:dynamic *base-url* "https://www.jkpglunch.se/")
(def cache-dir-path "cache")
(def date-formatter (java.text.SimpleDateFormat. "yyyy-MM-dd") )

(defn get-date []
    (.format date-formatter (java.util.Date.)))

(defn get-cache-dir []
    (let [cache-dir (java.io.File. cache-dir-path)]
        (if (.exists cache-dir)
            (.getAbsolutePath cache-dir)
            (do
                (.mkdir cache-dir)
                (.getAbsolutePath cache-dir)))))

(defn load-cached-data [file]
    (html/html-resource (java.io.File. file)))

(defn get-html-data []
    (let [cache-dir (cache/get-cache-dir)
          cache-file (str cache-dir "/" (get-date) ".html")]
        (if (.exists (java.io.File. cache-file))
            (do 
                (println "exists" cache-file)
                (load-cached-data cache-file))
            (do
                (println "not exists")
                (let [data (slurp *base-url*)]
                    (spit cache-file data)
                    (load-cached-data cache-file))))))