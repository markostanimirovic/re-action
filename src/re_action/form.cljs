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

(defn- errors [validators value]
  (->> validators
       (map #(-> [(key %) (not ((val %) value))]))
       (into {})))


(defn create [form]
  (let [controls (r/atom {})]
    (r/after-render
      #(doseq [[id validators] form]
         (let [element (.getElementById js/document (name id))
               value (.-value element)]
           (swap! controls update id assoc
                  :value value
                  :errors (errors validators value))
           (set! (.-oninput element) (fn [e]
                                       (let [value (.. e -target -value)]
                                         (swap! controls update id assoc
                                                :value value
                                                :dirty true
                                                :errors (errors validators value)))))
           (set! (.-onblur element) (fn [_]
                                      (swap! controls assoc-in [id :touched] true))))))
    controls))
