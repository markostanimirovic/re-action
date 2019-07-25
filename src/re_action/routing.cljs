(ns re-action.routing
  (:require [re-streamer.core :as re-streamer :refer [subscribe emit]]))

(defrecord Route [path page])

(defonce router (re-streamer/stream))

(defonce routes (atom []))

(defonce router-outlet (re-streamer/map router :page))

(subscribe router #(set! (.. js/window -location -hash) (:path %)))

(defn defroute [path page]
  (swap! routes conj (->Route path page)))

(defn navigate [path]
  (emit router (->> @routes
                    (filter #(= (:path %) path))
                    (first))))

(defn start-routing []
  (navigate (.. js/window -location -hash)))
