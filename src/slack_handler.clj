(ns slack-handler
    (:require
        [clojure.string :as str]
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

(defn format-slack-message [restaurants search-word]
    (if (empty? restaurants)
        (str "Sorry dude! No \"" search-word "\" for you...")
        (reduce (fn [acc rest]
                    (str acc (format-restaurant rest)))
                ""
                restaurants)))

(defn search-for-dish [search-word]
    (let [html-data (parser/get-html-data)
          restaurants (parser/get-restaurants html-data)
          filtered-restaurants (filter (partial parser/has-dish-filter search-word) restaurants)]
        (response {:response_type "in_channel"
                   :text (format-slack-message filtered-restaurants search-word)})))

(defn search-for-restaurant [search-word]
    (let [html-data (parser/get-html-data)
          restaurants (parser/get-restaurants html-data)
          filtered-restaurants (filter (partial parser/matches-restaurant-name search-word) restaurants)]
        (response {:response_type "in_channel"
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

(defn handler [request]
    (let [query (get (:form-params request) "text")
          command (parse-command query)
          search-word (parse-search-word query)]
          (println command)
          (println search-word)
        (cond
            (= "dish" command) (search-for-dish search-word)
            (= "restaurant" command) (search-for-restaurant search-word)
            :else (response {:response_type "in_channel"
                             :text "Please provide a command"}))))

(def app
    (wrap-params
        (wrap-json-response handler)))