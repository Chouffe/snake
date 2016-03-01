(ns snake.subs
   (:require-macros [reagent.ratom :refer [reaction]])
   (:require [.core :refer :all]))

(register-sub :points (fn [db] (reaction (:points @db))))

(register-sub :snake (fn [db] (reaction (:snake @db))))

(register-sub :running? (fn [db] (reaction (:running? @db))))

(register-sub :food (fn [db] (reaction (:food @db))))

(register-sub :board (fn [db] (reaction (:board @db))))
