(ns slack-handler
    (:require
        [clojure.string :as str]
        [ring.middleware.json :refer [wrap-json-response]]
        [ring.util.response :refer [response]]
        [ring.middleware.params :refer [wrap-params]]
        [clj-http.client :as client]))

(defn post-back [response-url data]
    (client/post response-url {:form-params data :content-type :json}))

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

(defn format-slack-message [restaurants search-word]
    (if (empty? restaurants)
        (str "Sorry dude! No \"" search-word "\" for you...")
        (reduce (fn [acc rest]
                    (str acc (format-restaurant rest)))
                ""
                restaurants)))

(defn search-for-dish [search-word response-url]
    (let [html-data (parser/get-html-data)
          restaurants (parser/get-restaurants html-data)
          filtered-restaurants (filter (partial parser/has-dish-filter search-word) restaurants)]
        (post-back response-url {:response_type "in_channel"
                                 :text (format-slack-message filtered-restaurants search-word)})))


(defn search-for-restaurant [search-word response-url]
    (let [html-data (parser/get-html-data)
          restaurants (parser/get-restaurants html-data)
          filtered-restaurants (filter (partial parser/matches-restaurant-name search-word) restaurants)]
        (post-back response-url {:response_type "in_channel"
                                 :text (format-slack-message filtered-restaurants search-word)})))

(defn parse-command [text]
    (if (nil? text)
        []
        (get (str/split text #" ") 0)))

(defn parse-search-word [text]
    (if (nil? text)
        ""
        (let [[_ & rest] (str/split text #" ")]
            (str/join " " rest))))

(def immediate-response (response {:response_type "in_channel"}))

(defn dish-command? [command]
    (some #(= command %) ["d" "dish"]))

(defn restaurant-command? [command]
    (some #(= command %) ["r" "restaurant"]))

(defn handler [request]
    (let [query (get (:form-params request) "text")
          command (parse-command query)
          search-word (parse-search-word query)
          response-url (get (:form-params request) "response_url")]
          (println command)
          (println search-word)
          (println response-url)
        (cond
            (dish-command? command) (do
                                (future (search-for-dish search-word response-url))
                                immediate-response)
            (restaurant-command? command) (do
                                        (future (search-for-restaurant search-word response-url))
                                        immediate-response)
            :else (response {:response_type "in_channel"
                             :text "Please provide a command"}))))

(def app
    (wrap-params
        (wrap-json-response handler)))