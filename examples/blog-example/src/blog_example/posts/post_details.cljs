(ns blog-example.posts.post-details
  (:require [blog-example.posts.post-resource :as resource]
            [re-action.router :as router]))

(defn- post-details-facade []
  {:get-post #(or (resource/get-post (js/parseInt %))
                  (router/navigate "/not-found"))})

(defn post-details-container [id]
  (let [facade (post-details-facade)
        post ((:get-post facade) id)]
    [:div {:class "card"}
     [:div {:class "card-header"} "Post Details"]
     [:div {:class "card-body"}
      [:label {:class "font-italic font-weight-bold"} "Title"]
      [:div {:class "card-text"} (:title post)]
      [:hr]
      [:label {:class "font-italic font-weight-bold"} "Body"]
      [:div {:class "card-text text-justify"} (:body post)]]]))
