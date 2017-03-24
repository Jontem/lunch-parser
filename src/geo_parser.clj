(ns geo-parser)

(defn parse-position [position]
    {:long (get position 0)
     :lat (get position 1)})

(defn unique-by-name [coll]
    (reduce 
        (fn [so-far current]
            (assoc so-far (:name current) current))
            {}
            coll))

(defn generate-geo-map []
    (let [geo-data (cache/get-geo-data)]
        (unique-by-name
            (map (fn [item] {:name (get item "title")
                         :coordinate (parse-position (get item "position"))}) geo-data))))