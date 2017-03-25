(ns query-handler
    (:require
        [parser]
        [ring.middleware.json :refer [wrap-json-response]]
        [ring.util.response :refer [response]]))

(defn handler [request]
    (let [restaurants (parser/get-restaurants)]
        (response restaurants)))

(def app
    (wrap-json-response handler))