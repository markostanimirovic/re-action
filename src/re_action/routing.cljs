(ns re-action.routing
  (:require [re-streamer.core :as re-streamer :refer [subscribe emit]]))

(defrecord Route [path page])
(defrecord Redirection [from to])

(defonce routes (atom []))
(defonce redirections (atom []))

(defonce router (re-streamer/stream))
(defonce router-outlet (re-streamer/map router :page))

(subscribe router #(set! (.. js/window -location -hash) (:path %)))

(defn defroute [path page]
  (swap! routes conj (->Route path page)))

(defn redirect [from to]
  (swap! redirections conj (->Redirection from to)))

(defn navigate [path]
  (let [path (or (->> @redirections
                      (filter #(= (:from %) path))
                      (map :to)
                      (first))
                 path)
        route (->> @routes
                   (filter #(= (:path %) path))
                   (first))]
    (emit router route)))

(defn start-routing []
  (navigate (.. js/window -location -hash)))
