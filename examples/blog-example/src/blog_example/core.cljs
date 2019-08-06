(ns blog-example.core
  (:require [blog-example.shared :as shared]
            [blog-example.routing :as routing]
            [reagent.core :as r]))

(defn render-app []
  (r/render [shared/shell] (.getElementById js/document "app")))

(defn init! []
  (routing/init)
  (render-app))
