(ns blog.core
  (:require [blog.shared :as shared]
            [blog.routing :as routing]
            [reagent.core :as r]))

(defn render-app []
  (r/render [shared/shell] (.getElementById js/document "app")))

(defn init! []
  (routing/init)
  (render-app))
