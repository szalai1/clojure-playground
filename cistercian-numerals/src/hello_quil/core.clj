(ns hello-quil.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))


(def line-coords {0 []
                  1 ['(1 0 2 0)]
                  2 ['(1 1 2 1)]
                  3 ['(1 0 2 1)]
                  4 ['(2 0 1 1)]
                  5 ['(1 0 2 0) '(2 0 1 1)]
                  6 ['(2 0 2 1)]
                  7 ['(1 0 2 0) '(2 0 2 1)]
                  8 ['(1 1 2 1) '(2 0 2 1)]
                  9 ['(1 0 2 0) '(1 1 2 1) '(2 0 2 1)]})

(def transformations [[identity identity identity identity]
                      [#(- 2 %) identity #(- 2 %) identity]
                      [identity #(- 3 %) identity #(- 3 %)]
                      [#(- 2 %) #(- 3 %) #(- 2 %) #(- 3 %)]])

(defn transform-sections [digit n]
  (let [coords (get line-coords n)
        transs (nth transformations digit)]
    (map #(map eval
               (partition 2 2
                          (interleave transs %))) coords)))

(defn digits [n]
  (if (< n 10) [n]
      (conj (digits (quot n 10)) (mod n 10))))

(defn digits-with-padding [n]
  (let [digits (digits n)
        len (count digits)
        pad (repeat (- 4 len) 0)]
    (concat pad digits)))

(defn sections-of-n [n]
  (let [digits  (digits-with-padding n)]
    (apply concat
           (map-indexed #(transform-sections (- 3 %1) %2) digits))))


(defn draw-number [n]
  (q/line 20 0 20 60)
  (doseq [sec (sections-of-n n)]
    (let [scaled  (map #(* % 20)  sec)]
      (apply q/line scaled))))


(defn setup []
  (q/smooth)
  (q/background 100 0 0)
  (q/frame-rate 4)
  (q/color-mode :rgb)
  {:n 0})

(defn update-state [state]
  ; Update sketch state by changing circle color and position.
  {:n (rand-int 9999)})

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240 180 250)
  ; Set circle color.
  (q/fill 0 255 200)
  ; Calculate x and y coordinates of the circle
  (q/with-translation [100 100]
    (q/scale 2 2)
    (draw-number (:n state))
    (q/text (str (:n state)) 10 80)))


(q/defsketch hello-quil
  :size [1000 1000]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state

  :draw draw-state
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
