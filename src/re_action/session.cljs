(ns re-action.session
  (:require [re-action.core :refer [store select patch-state!]])
  (:refer-clojure :exclude [get]))

(defonce ^:private session (store {}))

(defn put! [key value]
  (patch-state! session {key value}))

(defn get [key]
  (select session key))
