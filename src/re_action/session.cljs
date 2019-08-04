(ns re-action.session
  (:require [re-action.core :refer [store select-distinct patch-state!]])
  (:refer-clojure :exclude [get]))

(defonce ^:private session (store {}))

(defn put! [key value]
  (patch-state! session {key value}))

(defn get [key]
  (select-distinct session key))
