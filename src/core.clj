(ns core
    (:require [net.cgrand.enlive-html :as html]))

(def ^:dynamic *base-url* "https://www.jkpglunch.se/mandag/")

(def name-selector [:h3.huvudsida :> :a])
(def dishes-selector [:ul.lunchul :> :li])
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
    (let [cache-dir (get-cache-dir)
          cache-file (str cache-dir "/" (get-date) ".html")]
        (if (.exists (java.io.File. cache-file))
            (do 
                (println "exists" cache-file)
                (load-cached-data cache-file))
            (do
                (println "not exists")
                (let [data (slurp *base-url*)]
                    (spit cache-file data)
                    (html/html-resource cache-file))))))

(defn get-base [html-data]
    (html/select html-data [:div.list :> :.all]))

(defn parse-dishes [html-data]
    (map #(html/text %) (html/select html-data dishes-selector)))

(defn parse-restaurant [html-data]
    (let [[name-node] (html/select html-data name-selector)]
        {:name (html/text name-node)
         :dishes (parse-dishes html-data) }))

(defn get-restaurants [html-data]
    (map #(parse-restaurant %) (get-base html-data)))

(defn -main
  [& args]
  (let [html-data (get-html-data)
        restaurants (get-restaurants html-data)]
        (println restaurants)))