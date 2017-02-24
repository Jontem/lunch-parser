(ns core
    (:require
        [net.cgrand.enlive-html :as html]
        [ring.middleware.json :refer [wrap-json-response]]
        [ring.util.response :refer [response]]
        [ring.middleware.reload :refer [wrap-reload]]
        [ring.adapter.jetty :as jetty]))

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
        (some #(.contains (.toLowerCase %) search-word) dishes)))

(defn format-dishes [dishes]
    (reduce (fn [acc dish]
                (str acc dish "\n"))
            ""
            dishes))

(defn format-restaurant [rest]
    (str (:name rest)
         "\n"
         (format-dishes (:dishes rest))
         "\n\n"))

(defn format-slack-message [restaurants]
    (reduce (fn [acc rest]
                (str acc (format-restaurant rest)))
            ""
            restaurants))

(defn handler [request]
    (let [html-data (get-html-data)
          restaurants (get-restaurants html-data)]
        (response {:response_type "in_channel"
                   :text (format-slack-message restaurants )})))

(def app
  (wrap-json-response handler))

(def reloadable-app
    (wrap-reload app))

(defn -main
    [& args]
    (let [html-data (get-html-data)
        restaurants (get-restaurants html-data)
        [search-word] args]
        (doseq [rest (filter (partial has-dish-filter search-word) restaurants)]
            (println "--------------------------")
            (println "")
            (println (:name rest))
            (doseq [dish (:dishes rest)]
                (println (str " - " dish))))))


;; repl debugging
;(defonce server (jetty/run-jetty reloadable-app {:port 3000 :join? false}))

;; use (.start server) or (.stop server)