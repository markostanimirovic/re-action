(ns blog-example.core
  (:require [reagent.core :as r]
            [blog-example.app :refer [app]]
            [re-action.router :as router]))

(defn mount-root []
  (router/start)
  (r/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
