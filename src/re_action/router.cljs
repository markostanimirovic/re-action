(ns re-action.router
  (:require [re-streamer.core :as re-streamer :refer [subscribe emit]]))

(defmulti page #(-> %))

(defn default []
  [:div])

(defmethod page :default [_]
  default)

(defonce init-router-state {:route (.. js/window -location -hash)
                            :page  (page (.. js/window -location -hash))})

(defonce router (re-streamer/behavior-stream init-router-state))

(defonce router-outlet (re-streamer/map router :page))

(subscribe router #(set! (.. js/window -location -hash) (:route %)))

(defn defroute [route component]
  (defmethod page route [_] component))

(defn navigate [route]
  (emit router {:route route
                :page  (page route)}))

(defn start-routing []
  (navigate (:route @(:state router))))
