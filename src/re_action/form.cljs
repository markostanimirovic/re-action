(ns re-action.form
  (:require [reagent.core :as r]))

(defn touched?
  ([form]
   (->> form
        (map #(:touched (val %)))
        (every? true?)))
  ([form id]
   (get-in form [id :touched])))

(defn dirty?
  ([form]
   (->> form
        (map #(:dirty (val %)))
        (every? true?)))
  ([form id]
   (get-in form [id :dirty])))

(defn valid?
  ([form]
   (->> form
        (map #(vals (:errors (val %))))
        (reduce #(into %1 %2) '())
        (every? false?)))
  ([form id]
   (->> (get-in form [id :errors])
        (vals)
        (every? false?)))
  ([form id error]
   (not (get-in form [id :errors error]))))

(defn value [form]
  (->> form
       (map #(-> [(key %) (:value (val %))]))
       (into {})))

(defn init [form]
  (let [controls (r/atom (into {} (map (fn [[id validators]] [id {:touched false
                                                                  :dirty   false
                                                                  :value   nil
                                                                  :errors  (into {} (map #(-> [(key %) false])
                                                                                         validators))}])
                                       form)))]
    (r/after-render
      (fn []
        (doseq [[id validators] form]
          (let [element (.getElementById js/document (name id))]
            (swap! controls assoc-in [id :value] (.. element -value))
            (swap! controls assoc-in [id :errors] (into {}
                                                        (map (fn [[name f]] [name (not (f (.. element -value)))])
                                                             validators)))
            (set! (.-oninput element) (fn [e]
                                        (swap! controls update id
                                               assoc :value (.. e -target -value) :dirty true
                                               :errors (into {} (map (fn [[name f]] [name (not (f (.. e -target -value)))])
                                                                     validators)))))
            (set! (.-onfocus element) (fn [e]
                                        (swap! controls assoc-in [id :touched] true)))))))
    controls))
