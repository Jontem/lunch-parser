(ns cache
    (:require
        [net.cgrand.enlive-html :as html]
        [boot.core :refer [json-parse]]))

(def ^:dynamic *base-url* "https://www.jkpglunch.se/")
(def ^:dynamic *geo-url* "https://www.jkpglunch.se/cms/wp-content/themes/lunch/mapgenjson.php?cityid=4")
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

(defn get-cache-file [cache-filename url]
    (let [cache-dir (cache/get-cache-dir)
          cache-file (str cache-dir "/" (get-date) "." cache-filename)]
        (if (.exists (java.io.File. cache-file))
            (do 
                (println "exists" cache-file)
                cache-file)
            (do
                (println "not exists")
                (let [data (slurp url)]
                    (spit cache-file data)
                    cache-file)))))

(defn get-html-data []
    (load-cached-data (get-cache-file "lunch-data" *base-url*)))

(defn get-geo-data []
    (json-parse
        (slurp (get-cache-file "geo-data" *geo-url*))))