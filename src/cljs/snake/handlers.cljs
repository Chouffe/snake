(ns snake.handlers
   (:require [re-frame.core :as re-frame]
             [snake.db :as db]
             [goog.events :as events]))

(re-frame/register-handler :start-game (fn  [_ _] (db/init-state)))

(defn last-direction [{:keys [body] :as snake}]
   (let [[[x-head y-head] [x y] _] body]
      (cond
         (neg? (- y y-head)) :up
         (pos? (- y y-head)) :down
         (neg? (- x x-head)) :left
         (pos? (- x x-head)) :right)))

(re-frame/register-handler
   :change-direction
   (fn [db [_ direction]]
      (assert (get #{:up :down :left :right} direction))
      (if (= (last-direction (:snake db)) direction)
         db
         (assoc-in db [:snake :direction] direction))))

(re-frame/register-handler
   :next-state
   (fn [db _]
      (let [{:keys [snake food board running?]} db]
         (cond
            (db/collision? snake board) (assoc db :running? false)
            (db/food? (db/next-head-position snake) food) (-> db
                                                              (update :points inc)
                                                              (update :snake db/move-snake)
                                                              (dissoc :food)
                                                              (update :snake db/grow-snake snake)
                                                              (db/rand-food))
            :else (update db :snake db/move-snake)))))

(def key-code->move
   "Mapping from the integer key code to the direction keyword"
   {38  :up   ; Up Arrow
    75  :up   ; k
    40  :down ; Down Arrow
    74  :down ; j
    39  :right ; Right Arrow
    76  :right ; l
    37  :left  ; Left Arrow
    72  :left  ; h
    })

(defonce key-handler
   (events/listen
      js/window
      "keydown"
      (fn  [e]
         (let [key-code (.-keyCode e)]
            (when (contains? key-code->move key-code)
               (re-frame/dispatch [:change-direction (key-code->move key-code)]))))))

(defonce snake-moving
   (js/setInterval #(re-frame/dispatch [:next-state]) 100))
