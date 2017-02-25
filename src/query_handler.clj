(ns query-handler
    (:require
        [parser]
        [ring.middleware.json :refer [wrap-json-response]]
        [ring.util.response :refer [response]]))

(defn handler [request]
    (let [html-data (parser/get-html-data)
          restaurants (parser/get-restaurants html-data)]
        (response restaurants)))

(def app
    (wrap-json-response handler))