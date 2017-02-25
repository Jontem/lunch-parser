(ns slack-handler
    (:require
        [ring.middleware.json :refer [wrap-json-response]]
        [ring.util.response :refer [response]]
        [ring.middleware.params :refer [wrap-params]]))

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
    (println )
    (let [search-word (get (:form-params request) "text")
          html-data (parser/get-html-data)
          restaurants (parser/get-restaurants html-data)
          filtered-restaurants (filter (partial parser/has-dish-filter search-word) restaurants)]
        (response {:response_type "in_channel"
                   :text (format-slack-message filtered-restaurants)})))

(def app
    (wrap-params
        (wrap-json-response handler)))