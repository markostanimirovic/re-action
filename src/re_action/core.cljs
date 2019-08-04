(ns re-action.core
  (:require [re-streamer.core :as re-streamer :refer [emit]]))

(defn store [state]
  (re-streamer/behavior-stream state))

(defn select [store key & keys]
  (if (nil? keys)
    (re-streamer/map store key)
    (re-streamer/pluck store (conj keys key))))

(defn select-distinct [store key & keys]
  (re-streamer/distinct (apply select store key keys) =))

(defn update-state! [store state]
  (emit store state))

(defn patch-state! [store partial-state]
  (update-state! store (into @(:state store) partial-state)))
