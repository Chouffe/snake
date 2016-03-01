(ns snake.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [snake.handlers]
              [snake.subs]
              [snake.views :as views]
              [snake.config :as config]))

(when config/debug?
  (println "dev mode"))

(defn run []
  (reagent/render [views/game]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:start-game])
  (run))
