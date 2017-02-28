(ns parser
    (:require
        [net.cgrand.enlive-html :as html]))

(def ^:dynamic *base-url* "https://www.jkpglunch.se/")

(def name-selector [:h3.huvudsida :> :a])
(def dishes-selector [:ul :li :span.rattdefault])
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
                    (load-cached-data cache-file))))))

(defn get-base [html-data]
    (html/select html-data [:div.list :.all]))

(defn parse-dishes [html-data]
    (map #(html/text %) (html/select html-data dishes-selector)))

(defn parse-restaurant [html-data]
    (let [[name-node] (html/select html-data name-selector)]
        {:name (html/text name-node)
         :dishes (parse-dishes html-data) }))

(defn get-restaurants [html-data]
    (map #(parse-restaurant %) (get-base html-data)))

(defn has-dish-filter [search-word restaurant]
    (let [dishes (:dishes restaurant)]
        (some #(.contains (.toLowerCase %) (.toLowerCase search-word)) dishes)))

(defn matches-restaurant-name [search-word restaurant]
    (let [rest-name (:name restaurant)]
        (.contains (.toLowerCase rest-name) (.toLowerCase search-word))))
