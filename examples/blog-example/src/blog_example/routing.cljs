(ns blog-example.routing
  (:require [blog-example.shared :as shared]
            [blog-example.posts.post-index :as post-index]
            [blog-example.posts.post-details :as post-details]
            [re-action.router :as router]))

(defn- init-routes []
  (router/defroute "/home" shared/home)
  (router/defroute "/posts" post-index/container)
  (router/defroute "/posts/:id" post-details/container)
  (router/defroute "/not-found" shared/not-found))

(defn- init-redirections []
  (router/redirect "/" "/home")
  (router/redirect "**" "/not-found"))

(defn init []
  (init-routes)
  (init-redirections)
  (router/start))

