(ns re-action.router
  (:require [re-streamer.core :as re-streamer :refer [subscribe emit]]
            [clojure.string :as string]))

(defrecord Route [segments page])
(defrecord Redirection [from to])

(defonce ^:private routes (atom #{}))
(defonce ^:private redirections (atom #{}))
(defonce ^:private not-found-redirection (atom nil))

(defonce ^:private store (re-streamer/stream))
(defonce ^:private current-page (re-streamer/map store :page))
(defonce outlet (:state current-page))

(defn- current-path [] (.. js/window -location -hash))
(defn- set-current-path [path] (set! (.. js/window -location -hash) path))

(defn- segments->path [segments]
  (string/join "/" segments))

(defn- path->segments [path]
  (filter (comp not empty?) (string/split path #"/")))

(defn- segments->route [segments]
  (->> @routes
       (filter #(= (:segments %) segments))
       (first)))

(subscribe store #(set-current-path (segments->path (:segments %))))

(defn defroute [path page]
  (let [segments (path->segments path)]
    (swap! routes conj (->Route segments page))))

(defn redirect [from to]
  (let [segments-from (path->segments from)
        segments-to (path->segments to)]
    (if (= segments-from ["**"])
      (reset! not-found-redirection (->Redirection segments-from segments-to))
      (swap! redirections conj (->Redirection segments-from segments-to)))))

(defn navigate [path]
  (let [segments (path->segments path)
        route (or (segments->route segments)
                  (->> @redirections
                       (filter #(= (:from %) segments))
                       (map :to)
                       (first)
                       (segments->route))
                  (segments->route (:to @not-found-redirection))
                  (throw (js/Error (str "Route: " segments " is not defined"))))]
    (emit store route)))

(defn start []
  (navigate (string/replace (current-path) "#" ""))
  (set! (.-onhashchange js/window) (fn []
                                     (let [path (string/replace (current-path) "#" "")]
                                       (if (not (= (:segments @(:state store)) (path->segments path)))
                                         (navigate path))))))

