(ns re-action.router
  (:require [re-action.core :refer [store select patch-state!]]
            [re-action.session :as session]
            [re-streamer.core :refer [subscribe]]
            [clojure.string :as string]))

(defrecord Router [current-route routes redirections not-found-redirection])
(defrecord Route [segments page])
(defrecord Redirection [from to])

(defonce ^:private router (store (->Router nil #{} #{} nil)))

(defonce ^:private current-route (select router :current-route))
(defonce ^:private routes (select router :routes))
(defonce ^:private redirections (select router :redirections))
(defonce ^:private not-found-redirection (select router :not-found-redirection))

(defn outlet []
  (let [current-route-state @(:state current-route)]
    (into [(:page current-route-state)] (vals (:params current-route-state)))))

(defn- current-path [] (.. js/window -location -hash))

(defn- set-current-path [path] (set! (.. js/window -location -hash) path))

(defn- remove-hashes [path]
  (string/replace path "#" ""))

(defn- segments->path [segments]
  (string/join "/" segments))

(defn- path->segments [path]
  (filter (comp not empty?) (string/split (remove-hashes path) #"/")))

(defn- segments-match? [segments-from-route segments]
  (every? true? (map (fn [seg-from-route seg]
                       (if (string/starts-with? seg-from-route ":")
                         true
                         (= seg-from-route seg)))
                     segments-from-route
                     segments)))

(defn- segments->route [segments]
  (->> @(:state routes)
       (filter (fn [route]
                 (and (= (count (:segments route)) (count segments))
                      (segments-match? (:segments route) segments))))
       (first)))

(defn- segments->params [segments-from-route segments]
  (let [params (into {} (filter #(string/starts-with? (name (nth % 0)) ":")
                                (map #(-> [(keyword %1) %2])
                                     segments-from-route
                                     segments)))]
    (when (not (empty? params)) params)))

(defn defroute [path page]
  (patch-state! router {:routes (conj @(:state routes) (->Route (path->segments path) page))}))

(defn redirect [from-path to-path]
  (let [from (path->segments from-path)
        to (path->segments to-path)]
    (if (= from ["**"])
      (patch-state! router {:not-found-redirection (->Redirection from to)})
      (patch-state! router {:redirections (conj @(:state redirections) (->Redirection from to))}))))

(defn navigate [path]
  (let [segments (path->segments path)
        route (or (segments->route segments)
                  (->> @(:state redirections)
                       (filter #(= (:from %) segments))
                       (map :to)
                       (first)
                       (segments->route))
                  (segments->route (:to @(:state not-found-redirection)))
                  (throw (js/Error (str "Route: " path " is not defined"))))
        params (segments->params (:segments route) segments)]
    (patch-state! router {:current-route (assoc route :params params)})))

(defn start []
  (navigate (current-path))
  (subscribe current-route (fn [route]
                             (let [segments (map #(or ((keyword %) (:params route)) %)
                                                 (:segments route))]
                               (set-current-path (segments->path segments))
                               (session/put! :current-route {:segments segments
                                                             :params   (into {} (map (fn [[k v]]
                                                                                       [(keyword (subs (name k) 1)) v])
                                                                                     (:params route)))}))))
  (set! (.-onhashchange js/window) #(navigate (current-path))))
