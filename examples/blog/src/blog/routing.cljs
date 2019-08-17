(ns blog.routing
  (:require [blog.shared :as shared]
            [blog.posts.post-index :as post-index]
            [blog.posts.post-details :as post-details]
            [blog.posts.post-edit :as post-edit]
            [re-action.router :as router]))

(defn- init-routes []
  (router/defroute "/home" shared/home)
  (router/defroute "/posts" post-index/container)
  (router/defroute "/posts/:id" post-details/container)
  (router/defroute "/posts/:id/edit" post-edit/container)
  (router/defroute "/not-found" shared/not-found))

(defn- init-redirections []
  (router/redirect "/" "/home")
  (router/redirect "**" "/not-found"))

(defn init []
  (init-routes)
  (init-redirections)
  (router/start))

