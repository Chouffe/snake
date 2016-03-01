(ns snake.db)

(defn positions [[width height :as board]]
  (for [x (range width)
        y (range height)]
    [x y]))

(defn rand-free-position [board unavailable-positions]
  (->> board
       positions
       (remove (set unavailable-positions))
       rand-nth))

(defn rand-food [{:keys [snake board] :as state}]
  (assoc state :food (rand-free-position board (:body snake))))

(defn init-snake []
  {:direction :right
   :body [[3 2] [2 2] [1 2] [0 2]]})

(defn init-board [] [35 25])

(defn init-state []
  (let [snake (init-snake)
        board (init-board)]
    {:running? true
     :board board
     :snake snake
     :food (rand-free-position board (:body snake))
     :points 0}))

(defn valid? [[x y :as position] [width height :as board]]
  (and (< -1 x width) (< -1 y height)))

(def wall? (complement valid?))

(defn food? [[x y :as position] food] (= position food))

(defn next-head-position
  [{:keys [body direction] :as snake}]
  (assert (get #{:up :down :left :right} direction))
  (let [[x y :as pos] (first body)]
    (case direction
      :left  [(dec x) y]
      :right [(inc x) y]
      :up    [x (dec y)]
      :down  [x (inc y)])))

(defn eat-itself? [{:keys [body] :as snake}]
  (boolean (get (into #{} body) (next-head-position snake))))

(defn collision? [snake board]
  (or (wall? (next-head-position snake) board)
      (eat-itself? snake)))

(defn move-snake
  [{:keys [body direction] :as snake}]
  (update snake :body (fn [body] (cons (next-head-position snake)
                                       (drop-last body)))))

(defn grow-snake
  [snake previous-snake]
  (update snake :body concat [(last (:body previous-snake))]))
