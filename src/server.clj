(ns server
    (:require
        [parser]
        [query-handler]
        [slack-handler]
        [ring.middleware.reload :refer [wrap-reload]]
        [ring.adapter.jetty :as jetty]
        [compojure.core :refer :all]
        [compojure.route :as route]))

(def reloadable-query-app
    (wrap-reload query-handler/app))

(def reloadable-slack-app
    (wrap-reload slack-handler/app))

(defroutes app
    (GET "/" [] "<h1>Welcome!</h1>")
    (GET "/api/v1" [] reloadable-query-app)
    (POST "/slack-api/v1" [] reloadable-slack-app)
    (route/not-found "<h1>Page not found</h1>"))

;; repl debugging
;;(defonce server (jetty/run-jetty server/app {:port 3000 :join? false}))

;; use (.start server) or (.stop server)