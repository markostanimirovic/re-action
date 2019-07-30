(ns re-action.router
  (:require [re-streamer.core :as re-streamer :refer [subscribe emit]]))

(defrecord Route [path page])
(defrecord Redirection [from to])

(defonce ^:private routes (atom #{}))
(defonce ^:private redirections (atom #{}))
(defonce ^:private not-found-redirection (atom nil))

(defonce ^:private store (re-streamer/stream))
(defonce ^:private current-page (re-streamer/map store :page))
(defonce outlet (:state current-page))

(subscribe store #(set! (.. js/window -location -hash) (:path %)))

(defn defroute [path page]
  (swap! routes conj (->Route path page)))

(defn redirect [from to]
  (if (= from "**")
    (reset! not-found-redirection (->Redirection from to))
    (swap! redirections conj (->Redirection from to))))

(defn- path->route [path]
  (->> @routes
       (filter #(= (:path %) path))
       (first)))

(defn navigate [path]
  (let [route (or (path->route path)
                  (->> @redirections
                       (filter #(= (:from %) path))
                       (map :to)
                       (first)
                       (path->route))
                  (path->route (:to @not-found-redirection))
                  (throw (js/Error (str "Route: " path " is not defined"))))]
    (emit store route)))

(defn start []
  (navigate (.. js/window -location -hash)))
