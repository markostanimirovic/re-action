(ns re-action.core
  (:require [re-streamer.core :as re-streamer :refer [subscribe emit]]))

(defn store [state]
  (re-streamer/behavior-stream state))

(defn select
  ([store key]
   (-> store
       (re-streamer/map key)
       (re-streamer/distinct =)))
  ([store & keys]
   (-> store
       (re-streamer/pluck keys)
       (re-streamer/distinct =))))

(defn update-state! [store state]
  (emit store state))

(defn patch-state! [store partial-state]
  (update-state! store (into @(:state store) partial-state)))
