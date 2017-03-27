(ns geo-parser
    (:require [cache]
              [haversine.core]))

(defn parse-position [position]
    {:long (Float/parseFloat (get position 0))
     :lat (Float/parseFloat (get position 1))})

(defn unique-by-name [coll]
    (reduce 
        (fn [so-far current]
            (assoc so-far (:name current) current))
            {}
            coll))

(defn missing-coordinate [item]
    (let [position (get item "position")
          has-value (comp not nil?)]
        (and (has-value (get position 0)) (has-value (get position 1)))))

(defn generate-geo-map []
    (let [geo-data (cache/get-geo-data)
          filtered-geo-data (filter missing-coordinate geo-data)]
        (unique-by-name
            (map (fn [item] {:name (get item "title")
                         :coordinate (parse-position (get item "position"))}) filtered-geo-data))))

(defn calculate-distance [pos1 pos2]
    (if (nil? pos2)
        99999999999
        (haversine.core/haversine
            {:latitude (:lat pos1) :longitude (:long pos1)}
            {:latitude (:lat pos2) :longitude (:long pos2)})))

(def distance-divid-hq (partial calculate-distance { :lat 14.1590071 :long 57.7794258}))