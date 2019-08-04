(ns blog-example.app
  (:require [blog-example.home :refer [home-container]]
            [blog-example.posts.post-index :refer [post-index-container]]
            [blog-example.posts.post-details :refer [post-details-container]]
            [blog-example.shared :refer [header footer not-found]]
            [re-action.router :as router]))

(router/defroute "/home" home-container)
(router/defroute "/posts" post-index-container)
(router/defroute "/posts/:id" post-details-container)
(router/defroute "/not-found" not-found)

(router/redirect "/" "/home")
(router/redirect "**" "/not-found")

(defn app []
  [:div {:class "wrapper"}
   [header]
   [:div {:class "container body"} (router/outlet)]
   [footer]])
