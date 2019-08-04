(ns blog-example.posts.post-details
  (:require [blog-example.posts.post-resource :as resource]))

(defn- post-details-facade []
  {:get-post #(resource/get-post (js/parseInt %))})

(defn post-details-container [id]
  (let [facade (post-details-facade)
        post ((:get-post facade) id)]
    [:div
     [:h1 {:class "text-center"} "Post Details"]
     [:p [:b "Post title: "] (:title post)]
     [:p [:b "Post body: "] (:body post)]]))
