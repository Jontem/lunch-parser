(ns core
    (:require [parser]))

(defn -main
    [& args]
    (let [html-data (parser/get-html-data)
        restaurants (parser/get-restaurants html-data)
        [search-word] args]
        (doseq [rest (filter (partial parser/has-dish-filter search-word) restaurants)]
            (println "--------------------------")
            (println "")
            (println (:name rest))
            (doseq [dish (:dishes rest)]
                (println (str " - " dish))))))