(ns parser
    (:require
        [net.cgrand.enlive-html :as html]
        [geo-parser]
        [cache]))

(def name-selector [:h3.huvudsida :> :a])
(def dishes-selector [:ul :li :span.rattdefault])

(defn get-base [html-data]
    (html/select html-data [:div.list :.all]))

(defn parse-dishes [html-data]
    (map #(html/text %) (html/select html-data dishes-selector)))

(defn parse-restaurant [geo-map html-data]
    (let [[name-node] (html/select html-data name-selector)
           name (html/text name-node)]
        {:name name
         :dishes (parse-dishes html-data)
         :coordinate (get-in geo-map [name :coordinate]) }))

(defn sort-by-distance [restaurants]
    (sort-by
        (fn [restaurant] (geo-parser/distance-divid-hq (:coordinate restaurant)))
        restaurants))

(defn get-restaurants []
    (let [restaurants 
            (map 
                #((partial parse-restaurant (geo-parser/generate-geo-map)) %)
                (get-base (cache/get-html-data)))]
            (sort-by-distance restaurants)))

(defn has-dish-filter [search-word restaurant]
    (let [dishes (:dishes restaurant)]
        (some #(.contains (.toLowerCase %) (.toLowerCase search-word)) dishes)))

(defn matches-restaurant-name [search-word restaurant]
    (let [rest-name (:name restaurant)]
        (.contains (.toLowerCase rest-name) (.toLowerCase search-word))))
