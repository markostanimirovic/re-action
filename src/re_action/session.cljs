(ns re-action.session
  (:require [re-streamer.core :as re-streamer :refer [emit]]))

(defonce ^:private store (re-streamer/behavior-stream))

(defn put! [key value]
  (emit store (assoc @(:state store) key value)))

(defn get [key]
  (key @(:state store)))

(defn get-s [key]
  (-> store
      (re-streamer/map key)
      (re-streamer/distinct =)))
