(ns re-action.form
  (:require [reagent.core :as r]))

(defn touched? [form id]
  (get-in form [id :touched]))

(defn dirty? [form id]
  (get-in form [id :dirty]))

(defn valid? [form id]
  (get-in form [id :valid]))

(defn value [form]
  (into {} (map (fn [[id props]] [id (:value props)]) form)))

(defn init [form]
  (let [controls (r/atom (into {} (map (fn [[id validators]] [id {:touched false :dirty false :value nil}]) form)))]
    (r/after-render
      #(doseq [[id validators] form]
         (let [element (.getElementById js/document (name id))]
           (swap! controls assoc-in [id :value] (.. element -value))
           (set! (.-oninput element) (fn [e]
                                       (swap! controls update id
                                              assoc :value (.. e -target -value) :dirty true)))
           (set! (.-onfocus element) (fn [e]
                                       (swap! controls assoc-in [id :touched] true))))))
    controls))
