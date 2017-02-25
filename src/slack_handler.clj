(ns slack-handler
    (:require
        [ring.middleware.json :refer [wrap-json-response]]
        [ring.util.response :refer [response]]))

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
    (println request)
    (let [html-data (parser/get-html-data)
          restaurants (parser/get-restaurants html-data)]
        (response {:response_type "in_channel"
                   :text (format-slack-message restaurants)})))

(def app
    (wrap-json-response handler))