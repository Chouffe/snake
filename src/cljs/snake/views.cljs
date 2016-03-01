(ns snake.views
  (:require [re-frame.core :as re-frame]))

(defn ^:private cell-class [position food-position snake-body-positions]
  (cond
    (= position food-position)          "food"
    (get snake-body-positions position) "snake"
    :else                               "empty"))

(defn score []
  (let [points (re-frame/subscribe [:points])]
    [:div.score "Score: " @points]))

(defn board []
  (let [board (re-frame/subscribe [:board])
        snake (re-frame/subscribe [:snake])
        food (re-frame/subscribe [:food])]
    (fn []
      (let [[width height] @board
            {:keys [body]} @snake]
        (into [:table.stage]
              (for [y (range height)]
                (into [:tr {:key y}]
                      (mapv (fn [x] [:td {:key (str x "." y)
                                          :class (cell-class [x y] @food (set body))}])
                            (range width)))))))))

(defn game-over []
  (let [running? (re-frame/subscribe [:running?])]
    (fn []
      (if @running?
        [:div]
        [:div.overlay
         [:div.play {:on-click #(re-frame/dispatch [:start-game])}
          [:h1 "↺"]]]))))

(defn game []
  [:div.game
   [score]
   [board]
   [game-over]])
